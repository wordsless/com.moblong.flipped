package com.moblong.flipped;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.moblong.flipped.model.Whistle;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public final class RabbitMQService extends Service {

	private RabbitMQBinder binder = null;
	
	private LocationClient locator = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		final Shell shell = (Shell) getApplication();
		binder = new RabbitMQBinder();

		locator = new LocationClient(this.getApplicationContext());
		locator.registerLocationListener(new BDLocationListener() {

			@Override
			public void onReceiveLocation(BDLocation location) {
				Whistle<String> whistle = new Whistle<String>();
				whistle.setAction("com.moblong.flipped.LOCATION");
				whistle.setContent(String.format("{'aid':'%s', 'latitude':'%s', 'longitude':'%s'}", shell.getSelf().getId(), location.getLatitude(), location.getLongitude()));
				whistle.setTarget("000000000000000000");
				binder.send(whistle);
			}
		});
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");
		option.setScanSpan(10000);
		option.setAddrType("all");
		option.disableCache(true);
		option.setPriority(LocationClientOption.GpsFirst);
		locator.setLocOption(option);
		locator.start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(locator != null)
			locator.stop();
		if(binder != null)
			binder.shutdown();
	}

}
