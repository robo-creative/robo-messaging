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

import java.util.Collection;

/**
 * @author robo-admin
 */
public interface MessageRepository {

    /**
     * Indicates current number of messages being stored in this repository.
     */
    int size();

    /**
     * Stores a message.
     */
    void store(Message message);

    /**
     * Removes a message.
     */
    boolean remove(Message message);

    /**
     * Removes all messages.
     */
    void removeAll();

    /**
     * Finds all messages of a specified contract type.
     *
     * @param contractType    Message contract type.
     * @param includeChildren Flag, indicates if child messages are also be counted.
     * @return The collection of messages if found. Otherwise an empty collection.
     */
    Collection<Message> find(Class<? extends Message> contractType, boolean includeChildren);
}
