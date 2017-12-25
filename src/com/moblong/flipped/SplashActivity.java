package com.moblong.flipped;

import com.moblong.flipped.feature.IDestructor;
import com.moblong.flipped.feature.IObserver;
import com.moblong.flipped.model.Account;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

public final class SplashActivity extends Activity {
	
	private IDestructor destructor;
	
	public static final int REQEST_REGISTER = 1000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Shell shell = (Shell)getApplication();
		
		ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        
        if(ni == null || !ni.isConnectedOrConnecting()) {
        	new AlertDialog.Builder(this) 
         	.setTitle("确认")
         	.setMessage("无网络连接，请选择“是”，到设置界面检查设置！选择“否”将退出程序。")
         	.setPositiveButton("是", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent =  new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);  
			        startActivity(intent);
				}
         		
         	})
         	.setNegativeButton("否", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
         		
         	})
         	.show();
        }
        
		final ServiceConnection con = new ServiceConnection() {

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				RabbitMQBinder rabbitmq = (RabbitMQBinder) service;
				if(!rabbitmq.isAlive()) {
					Account account = shell.getSelf();
					try {
						rabbitmq.init(account.getId(), "push.tlthsc.com", 5476);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				shell.bind(rabbitmq);
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				
			}
		};
		
		final Thread constructor = new Thread(new Runnable() {

			@Override
			public void run() {
				shell.init(SplashActivity.this, new IObserver<Boolean>() {

					@Override
					public boolean observe(final Boolean prepared) {
						Intent intent = new Intent(SplashActivity.this, RabbitMQService.class);
						intent.setAction("com.moblong.action.PUSHING");
						SplashActivity.this.bindService(intent, con, Context.BIND_AUTO_CREATE);
						if(!prepared) {
							//没有准备好，跳到Register页面
							startActivityForResult(new Intent(SplashActivity.this, RegisterActivity.class), REQEST_REGISTER);	
						} else {
							Intent flag = new Intent(SplashActivity.this, HomeActivity.class);
							flag.putExtra("flag", false);
							startActivity(flag);
							finish();
						}
						return false;
					}
				});
			}
			
		});
		
		destructor = new IDestructor() {

			@Override
			public void destruct() {
				SplashActivity.this.unbindService(con);
			}
			
		};
		
		constructor.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		destructor.destruct();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQEST_REGISTER) {
			//无论成功与否，都将跳往HomeActivity页面。如果没有注册，则以匿名的形式运行。
			startActivity(new Intent(SplashActivity.this, HomeActivity.class));
			finish();
		}
	}
}
