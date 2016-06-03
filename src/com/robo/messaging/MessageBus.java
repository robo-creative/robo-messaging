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

/**
 * Intended for exchanging messages.
 *
 * @author robo-admin
 */
public interface MessageBus {

    /**
     * Subscribes a subscriber to receive messages of a specified type.
     *
     * @param subscriber The subscriber.
     */
    <TMessage extends Message> void subscribe(Subscriber<TMessage> subscriber);

    /**
     * Subscribes a subscriber to receive messages of a specified type.
     *
     * @param subscriber The subscriber.
     * @param priority   Indicates priority of this subscriber. The lower number, the sooner this
     *                   subscriber receives messages than the other.
     */
    <TMessage extends Message> void subscribe(Subscriber<TMessage> subscriber, int priority);

    /**
     * Subscribes a subscriber to receive messages of a specified type.
     *
     * @param subscriber             The subscriber.
     * @param priority               Indicates priority of this subscriber. The lower number, the sooner this
     *                               subscriber receives messages than the other.
     * @param acceptsChildMessages Flag, determines if this subscriber also accepts messages of children type.
     */
    <TMessage extends Message> void subscribe(Subscriber<TMessage> subscriber, int priority, boolean acceptsChildMessages);

    /**
     * Subscribes a subscriber to receive messages of a specified type.
     *
     * @param subscriber              The subscriber.
     * @param priority                Indicates priority of this subscriber. The lower number, the sooner this
     *                                subscriber receives messages than the other.
     * @param acceptsChildMessages  Flag, determines if this subscriber also accepts messages of children type.
     * @param receiveHistoricMessages Flag, indicates if the subscriber also wants to receive historic messages.
     */
    <TMessage extends Message> void subscribe(Subscriber<TMessage> subscriber, int priority, boolean acceptsChildMessages,
                              boolean receiveHistoricMessages);

    /**
     * Subscribes a subscriber to receive messages of a specified type.
     *
     * @param subscriber              The subscriber.
     * @param priority                Indicates priority of this subscriber. The lower number, the sooner this
     *                                subscriber receives messages than the other.
     * @param acceptsChildMessages  Flag, determines if this subscriber also accepts messages of children type.
     * @param receiveHistoricMessages Flag, indicates if the subscriber also wants to receive historic messages.
     * @param threadOption            Specifies delivery thread.
     */
    <TMessage extends Message> void subscribe(Subscriber<TMessage> subscriber, int priority, boolean acceptsChildMessages,
                              boolean receiveHistoricMessages, ThreadOption threadOption);

    /**
     * Subscribes a subscriber to receive messages of a specified type.
     *
     * @param subscriber             The receiver.
     * @param priority               Indicates priority of this subscriber. The lower number, the sooner this
     *                               subscriber receives messages than the other.
     * @param acceptsChildMessages Flag, determines if this subscriber also accepts messages of children type.
     * @param threadOption           Specifies delivery thread.
     * @param keepSubscriberAlive    If true, the message bus will keep a strong reference to the
     *                               subscriber until the subscriber is unsubscribed.
     */
    <TMessage extends Message> void subscribe(Subscriber<TMessage> subscriber, int priority, boolean acceptsChildMessages,
                              boolean receiveHistoricMessages, ThreadOption threadOption, boolean keepSubscriberAlive);

    /**
     * Cancels subscription for a specified subscriber.
     *
     * @param subscriber The subscriber.
     */
    <TMessage extends Message> void unsubscribe(Subscriber<TMessage> subscriber);

    /**
     * Publishes a message.
     *
     * @param message The message.
     */
    <TMessage extends Message> void publish(TMessage message);

    /**
     * Publishes a message.
     *
     * @param message       The message.
     * @param keepInHistory Flag, indicates if the message will be kept in history for sending to late-bound subscribers.
     */
    <TMessage extends Message> void publish(TMessage message, boolean keepInHistory);

    /**
     * Publishes a message.
     *
     * @param message       The message.
     * @param keepInHistory Flag, indicates if the message will be kept in history for sending to late-bound subscribers.
     * @param callback      A callback object that allows message bus to interact with the publisher.
     */
    <TMessage extends Message> void publish(TMessage message, boolean keepInHistory, PublishingCallback callback);

    /**
     * Removes a historic message from history.
     *
     * @param message    The message.
     * @param <TMessage> Type of message.
     */
    <TMessage extends Message> void remove(TMessage message);

    /**
     * Clears all historic messages.
     */
    void clearHistory();

    /**
     * Indicates current number of historic messages being stored.
     */
    int getHistoryCount();
}
