/**
 * Copyright (c) 2016 Robo Creative - https://robo-creative.github.io.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.robo.messaging;

/**
 * Provides the base class for implementing subscription token for identifying subscription of a subscriber.
 *
 * @author robo-admin
 */
public abstract class SubscriptionToken {

    private Class<? extends Message> mMessageType;

    public final Class<? extends Message> getMessageType() {
        return mMessageType;
    }

    protected SubscriptionToken(Class<? extends Message> messageType) {
        mMessageType = messageType;
    }
}
