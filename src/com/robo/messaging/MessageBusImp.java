/**
 * Copyright (c) 2016 Robo Creative - https://robo-creative.github.io.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.robo.messaging;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.robo.Guard;
import com.robo.reflect.TypeUtils;

/**
 * Default implementation of {@link com.robo.messaging.MessageBus MessageBus}.
 *
 * @author robo-admin
 */
public class MessageBusImp implements MessageBus {

    private Map<Type, Subscriptions> mSubscriptions;
    private SubscriptionBuilder mSubscriptionBuilder;
    private MessageRepository mMessageRepository;
    private Map<Class<?>, List<Class<?>>> mMessageSuperTypeMap;
    private TokenGenerator mTokenGenerator;
    private WeakHashMap<Subscriber<?>, SubscriptionToken> mManagedTokens;

    public MessageBusImp() {
        this(new UUIDTokenGenerator(), Executors.newCachedThreadPool(), new InMemoryMessageRepository());
    }

    public MessageBusImp(TokenGenerator tokenGenerator, ExecutorService executorService, MessageRepository messageRepository) {
        mSubscriptions = new HashMap<>();
        mMessageSuperTypeMap = new HashMap<>();
        mTokenGenerator = tokenGenerator;
        mSubscriptionBuilder = new SubscriptionBuilder(executorService);
        mMessageRepository = messageRepository;
        mManagedTokens = new WeakHashMap<>();
    }

    @Override
    public <TMessage extends Message> void subscribe(Subscriber<TMessage> subscriber) {
        subscribe(subscriber, 0);
    }

    @Override
    public <TMessage extends Message> void subscribe(Subscriber<TMessage> subscriber, int priority) {
        subscribe(subscriber, priority, true);
    }

    @Override
    public <TMessage extends Message> void subscribe(Subscriber<TMessage> subscriber, int priority, boolean acceptsChildMessages) {
        subscribe(subscriber, priority, acceptsChildMessages, false);
    }

    @Override
    public <TMessage extends Message> void subscribe(Subscriber<TMessage> subscriber, int priority, boolean acceptsChildMessages, boolean receiveHistoricMessages) {
        subscribe(subscriber, priority, acceptsChildMessages, receiveHistoricMessages, ThreadOption.PUBLISHER);
    }

    @Override
    public <TMessage extends Message> void subscribe(Subscriber<TMessage> subscriber, int priority, boolean acceptsChildMessages, boolean receiveHistoricMessages, ThreadOption threadOption) {
        subscribe(subscriber, priority, acceptsChildMessages, receiveHistoricMessages, ThreadOption.PUBLISHER, false);
    }

    @Override
    public <TMessage extends Message> void subscribe(Subscriber<TMessage> subscriber, int priority, boolean acceptsChildMessages, boolean receiveHistoricMessages, ThreadOption threadOption, boolean keepSubscriberAlive) {
        Type messageType = TypeUtils.getGenericParameterType(subscriber, 0);
        addSubscription(messageType, mSubscriptionBuilder.build(getSubscriptionToken(subscriber), subscriber, priority, acceptsChildMessages, threadOption, keepSubscriberAlive));
        if (receiveHistoricMessages) {
            publishHistoricMessages(subscriber, (Class<? extends Message>) messageType, acceptsChildMessages);
        }
    }

    @Override
    public <TMessage extends Message> void subscribe(Subscriber<TMessage> subscriber, int priority, boolean acceptsChildMessages, boolean receiveHistoricMessages, PublishingStrategy<TMessage> publishingStrategy, boolean keepSubscriberAlive) {
        Type messageType = TypeUtils.getGenericParameterType(subscriber, 0);
        addSubscription(messageType, mSubscriptionBuilder.build(getSubscriptionToken(subscriber), subscriber, priority, acceptsChildMessages, publishingStrategy, keepSubscriberAlive));
        if (receiveHistoricMessages) {
            publishHistoricMessages(subscriber, (Class<? extends Message>) messageType, acceptsChildMessages);
        }
    }

    @Override
    public <TMessage extends Message> void unsubscribe(Subscriber<TMessage> subscriber) {
        removeSubscription(TypeUtils.getGenericParameterType(subscriber, 0),
                getSubscriptionToken(subscriber));
        removeSubscriptionToken(subscriber);
    }

    @Override
    public <TMessage extends Message> void publish(TMessage message) {
        publish(message, false);
    }

    @Override
    public <TMessage extends Message> void publish(TMessage message, boolean keepInHistory) {
        publish(message, keepInHistory, null);
    }

    @Override
    public <TMessage extends Message> void publish(TMessage message, boolean keepInHistory, PublisherCallback callback) {
        Guard.isNotNull(message, IllegalArgumentException.class, "message");
        Subscription<TMessage>[] subscriptionsSnapshot = purgeAndGetSubscriptions(message);
        if (null != callback) {
            if (subscriptionsSnapshot.length == 0) {
                callback.noSubscriber();
            } else {
                callback.messageEnqueued();
            }
        }
        Arrays.sort(subscriptionsSnapshot);
        for (Subscription<TMessage> subscription : subscriptionsSnapshot) {
            subscription.publish(message);
        }
        if (keepInHistory) {
            mMessageRepository.store(message);
        }
    }

    @Override
    public <TMessage extends Message> void remove(TMessage message) {
        mMessageRepository.remove(message);
    }

    @Override
    public void clearHistory() {
        mMessageRepository.removeAll();
    }

    @Override
    public int getHistoryCount() {
        return mMessageRepository.size();
    }

    private <TMessage extends Message> void publishHistoricMessages(Subscriber<TMessage> subscriber, Class<? extends Message> messageType, boolean acceptsChildMessages) {
        Collection<Message> historicMessages = mMessageRepository.find(messageType, acceptsChildMessages);
        for (Message message : historicMessages) {
            message.setHistoric(true);
            subscriber.receive((TMessage) message);
        }
    }

    synchronized private <TMessage extends Message> void addSubscription(Type messageType, Subscription<TMessage> subscription) {
        Subscriptions byMessageSubscriptions;
        if (!mSubscriptions.containsKey(messageType)) {
            byMessageSubscriptions = new Subscriptions();
            mSubscriptions.put(messageType, byMessageSubscriptions);
        } else {
            byMessageSubscriptions = mSubscriptions.get(messageType);
        }
        byMessageSubscriptions.add(subscription);
    }

    synchronized private void removeSubscription(Type messageType, SubscriptionToken token) {
        if (mSubscriptions.containsKey(messageType)) {
            Subscriptions byMessageSubscriptions = mSubscriptions.get(messageType);
            byMessageSubscriptions.removeByKey(token);
            if (byMessageSubscriptions.size() == 0) {
                mSubscriptions.remove(messageType);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <TMessage extends Message> Subscription<TMessage>[] purgeAndGetSubscriptions(TMessage message) {
        List<Subscription<? extends Message>> subscriptionsSnapshot = new ArrayList<>();
        Class<? extends TMessage> messageType = (Class<? extends TMessage>) message.getClass();
        subscriptionsSnapshot.addAll(purgeAndGetSubscriptions(messageType, false));
        List<Class<?>> allSuperTypes = getAllSuperTypes(message);
        for (Class<?> messageSuperType : allSuperTypes) {
            subscriptionsSnapshot.addAll(purgeAndGetSubscriptions(messageSuperType, true));
        }
        return subscriptionsSnapshot.toArray(new Subscription[subscriptionsSnapshot.size()]);
    }

    @SuppressWarnings("unchecked")
    synchronized private Collection<Subscription<? extends Message>> purgeAndGetSubscriptions(Class<?> messageType, boolean isFindingBySuperType) {
        List<Subscription<? extends Message>> subscriptionsSnapshot = new ArrayList<>();
        if (mSubscriptions.containsKey(messageType)) {
            Subscriptions subscriptions = mSubscriptions.get(messageType);
            for (int i = 0; i < subscriptions.size(); i++) {
                Subscription<? extends Message> subscription = subscriptions.getAt(i);
                if (!subscription.isSubscriberAlive()) {
                    subscriptions.remove(subscription);
                } else {
                    if (!isFindingBySuperType || subscription.acceptsChildMessages()) {
                        subscriptionsSnapshot.add(subscription);
                    }
                }
            }
            if (subscriptions.size() == 0) {
                mSubscriptions.remove(messageType);
            }
        }
        return subscriptionsSnapshot;
    }

    synchronized private List<Class<?>> getAllSuperTypes(Object message) {
        Class<?> messageType = message.getClass();
        List<Class<?>> allTypesIncludingSuper;
        if (!mMessageSuperTypeMap.containsKey(messageType)) {
            allTypesIncludingSuper = new ArrayList<>();
            TypeUtils.fetchAllSuperTypes(messageType, allTypesIncludingSuper);
            TypeUtils.fetchAllInterfaces(messageType, allTypesIncludingSuper);
            mMessageSuperTypeMap.put(messageType, allTypesIncludingSuper);
        } else {
            allTypesIncludingSuper = mMessageSuperTypeMap.get(messageType);
        }
        return allTypesIncludingSuper;
    }

    private SubscriptionToken getSubscriptionToken(Subscriber<?> subscriber) {
        SubscriptionToken token;
        if (mManagedTokens.containsKey(subscriber)) {
            token = mManagedTokens.get(subscriber);
        } else {
            token = mTokenGenerator.generateToken(subscriber);
            mManagedTokens.put(subscriber, token);
        }
        return token;
    }

    private void removeSubscriptionToken(Subscriber<?> subscriber) {
        mManagedTokens.remove(subscriber);
    }
}
