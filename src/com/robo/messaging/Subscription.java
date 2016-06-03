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

final class Subscription<TMessage extends Message> implements Comparable<Subscription> {
    private SubscriptionToken mToken;
    private PublishingStrategy<TMessage> mPublishingStrategy;
    private SubscriberReference<TMessage> mSubscriberReference;
    private int mPriority;
    private boolean mAcceptsChildMessages;

    public Subscription(SubscriptionToken token, SubscriberReference<TMessage> subscriberReference, int priority,
                        boolean acceptsChildrenMessages, PublishingStrategy<TMessage> publishingStrategy) {
        mToken = token;
        mSubscriberReference = subscriberReference;
        mPriority = priority;
        mAcceptsChildMessages = acceptsChildrenMessages;
        mPublishingStrategy = publishingStrategy;
    }

    public boolean acceptsChildMessages() {
        return mAcceptsChildMessages;
    }

    public SubscriptionToken getToken() {
        return mToken;
    }

    public void publish(TMessage message) {
        Subscriber<TMessage> subscriber = mSubscriberReference.getSubscriber();
        if (null != subscriber) {
            mPublishingStrategy.deliverMessage(subscriber, message);
        }
    }

    public boolean isSubscriberAlive() {
        return null != mSubscriberReference.getSubscriber();
    }

    @Override
    public int compareTo(Subscription o) {
        return Integer.compare(mPriority, o.mPriority);
    }
}
