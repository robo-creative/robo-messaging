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

import java.util.concurrent.ExecutorService;

import com.robo.threading.ThreadUtils;

final class BackgroundPublishingStrategy<TMessage extends Message> implements PublishingStrategy<TMessage> {

	private ExecutorService mExecutorService;

	public BackgroundPublishingStrategy(ExecutorService executorService) {
		mExecutorService = executorService;
	}

	@Override
	public void deliverMessage(final Subscriber<TMessage> subscriber, final TMessage message) {
		if (ThreadUtils.isCurrentThreadBackgroundThread()) {
			subscriber.receive(message);
		} else {
			mExecutorService.execute(new Runnable() {
				@Override
				public void run() {
					subscriber.receive(message);
				}
			});
		}
	}
}
