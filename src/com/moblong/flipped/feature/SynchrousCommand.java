package com.moblong.flipped.feature;

public class SynchrousCommand {

	private static final int MAX_SYNCHRONOUS_COMMUNICATION_NUMBER = 5;
	
	private static int count = 0;
	
	public void exec(final IExecutor executor) {
		Thread executer = new Thread(new Runnable() {

			@Override
			public void run() {
				while(count >= MAX_SYNCHRONOUS_COMMUNICATION_NUMBER) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				count++;
				executor.execute();
				--count;
			}
			
		});
		executer.start();
	}
	
}
