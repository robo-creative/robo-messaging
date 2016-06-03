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

import java.lang.ref.WeakReference;

final class WeakSubscriberReference<TMessage extends Message> implements SubscriberReference<TMessage> {

	private WeakReference<Subscriber<TMessage>> mSubscriberRef;

	public WeakSubscriberReference(Subscriber<TMessage> subscriber) {
		mSubscriberRef = new WeakReference<>(subscriber);
	}

	@Override
	public Subscriber<TMessage> getSubscriber() {
		return mSubscriberRef.get();
	}

}
