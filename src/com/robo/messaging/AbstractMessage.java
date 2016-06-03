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
 * The base class for implementing messages.
 *
 * @author robo-admin
 */
public abstract class AbstractMessage<TContent> implements Message<TContent> {

    private UUID mUuid;
    private TContent mContent;
    private boolean mIsHistoric;

    protected AbstractMessage(TContent content) {
        mUuid = UUID.randomUUID();
        mContent = content;
        mIsHistoric = false;
    }

    @Override
    public TContent getContent() {
        return mContent;
    }

    @Override
    public boolean isHistoric() {
        return mIsHistoric;
    }

    @Override
    public void setHistoric(boolean value) {
        mIsHistoric = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj || !obj.getClass().equals(getClass())) {
            return false;
        }
        return ((AbstractMessage) obj).mUuid.equals(mUuid);
    }

    @Override
    public int hashCode() {
        return mUuid.hashCode() ^ 0x17;
    }
}
