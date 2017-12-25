package com.moblong.flipped;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.moblong.flipped.model.Whistle;
import com.moblong.iwe.IRecivedListener;
import com.moblong.iwe.ImmediatelyWhistleEngine;

import android.os.Binder;

public final class RabbitMQBinder extends Binder {
	
	private ImmediatelyWhistleEngine engine = new ImmediatelyWhistleEngine();

	public void send(Whistle<?> whistle) {
		engine.send(whistle);
	}

	public void init(String id, String host, int port) throws IOException, TimeoutException {
		engine.init(id, host, port);
	}

	public void startup(IRecivedListener listener) {
		engine.startup(listener);
	}

	public void shutdown() {
		engine.shutdown();
	}

	public boolean isAlive() {
		return engine.isAlive();
	}
}
