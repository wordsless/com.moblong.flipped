package com.moblong.flipped.feature;

import java.util.UUID;

public class Login {

	public void exec(final IObserver<String> observer) {
		observer.observe(UUID.randomUUID().toString());
	}
	
}
