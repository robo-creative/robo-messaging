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

import java.util.concurrent.ExecutorService;

import android.os.Looper;

final class SubscriptionBuilder {

    private ExecutorService mExecutorService;
    private final Looper mMainLooper = Looper.getMainLooper();

    public SubscriptionBuilder(ExecutorService executorService) {
        mExecutorService = executorService;
    }

    public <TMessage extends Message> Subscription<TMessage> build(SubscriptionToken token, Subscriber<TMessage> subscriber, int priority,
                                                   boolean acceptsChildMessages, ThreadOption threadOption,
                                                   boolean keepSubscriberAlive) {
        switch (threadOption) {
            case PUBLISHER:
                return new Subscription<>(token,
                        createSubscriberReference(subscriber, keepSubscriberAlive),
                        priority, acceptsChildMessages,
                        new PublisherThreadPublishingStrategy<TMessage>());
            case BACKGROUND:
                return new Subscription<>(token,
                        createSubscriberReference(subscriber, keepSubscriberAlive),
                        priority, acceptsChildMessages,
                        new BackgroundPublishingStrategy<TMessage>(mExecutorService));
            default: // case UI:
                return new Subscription<>(token,
                        createSubscriberReference(subscriber, keepSubscriberAlive),
                        priority, acceptsChildMessages,
                        new UIPublishingStrategy<TMessage>(mMainLooper));
        }
    }

    private <TMessage extends Message> SubscriberReference<TMessage> createSubscriberReference(Subscriber<TMessage> subscriber,
                                                                               boolean keepSubscriberAlive) {
        return keepSubscriberAlive ? new StrongSubscriberReference<>(subscriber)
                : new WeakSubscriberReference<>(subscriber);
    }
}
