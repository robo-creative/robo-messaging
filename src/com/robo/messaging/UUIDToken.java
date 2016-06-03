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

import java.util.UUID;

/**
 * A token based on UUID.
 *
 * @author robo-admin
 */
public class UUIDToken implements SubscriptionToken {
    private final UUID mId;

    public UUIDToken() {
        mId = UUID.randomUUID();
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj || !(obj instanceof UUIDToken)) {
            return false;
        }
        return ((UUIDToken) obj).mId.equals(mId);
    }

    @Override
    public int hashCode() {
        return mId.hashCode();
    }
}
