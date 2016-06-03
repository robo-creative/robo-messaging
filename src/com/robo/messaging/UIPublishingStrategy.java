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

import com.robo.threading.ThreadUtils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

final class UIPublishingStrategy<TMessage extends com.robo.messaging.Message> implements PublishingStrategy<TMessage> {

	private Looper mLooper;

	public UIPublishingStrategy(Looper looper) {
		mLooper = looper;
	}

	@Override
	public void deliverMessage(Subscriber<TMessage> subscriber, TMessage message) {
		if (ThreadUtils.isCurrentThreadBackgroundThread()) {
			Handler handler = new DeliveryHandler(subscriber, message);
			handler.sendMessage(handler.obtainMessage());
		} else {
			subscriber.receive(message);
		}
	}

	class DeliveryHandler extends Handler {

		private Subscriber<TMessage> mSubscriber;
		private TMessage mMessage;

		public DeliveryHandler(Subscriber<TMessage> subscriber, TMessage message) {
			super(mLooper);
			mSubscriber = subscriber;
			mMessage = message;
		}

		@Override
		public void handleMessage(Message msg) {
			mSubscriber.receive(mMessage);
		}
	}
}
