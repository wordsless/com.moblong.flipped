package com.moblong.flipped;

import org.json.JSONException;
import org.json.JSONObject;

import com.moblong.flipped.feature.Feature;
import com.moblong.flipped.feature.IObserver;
import com.moblong.flipped.model.Account;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public final class ThirdPartySignInActivity extends Activity {

	private Tencent tencent = null;
	
	private IUiListener tencentLoginListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.third_party_signin);
		final Shell shell = (Shell) getApplication();
		final LayoutInflater inflater = getLayoutInflater();
		View back = findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ThirdPartySignInActivity.this.setResult(RESULT_CANCELED);
				ThirdPartySignInActivity.this.finish();
			}
			
		});
		
		tencentLoginListener = new IUiListener() {

				@Override
				public void onCancel() {
					Log.i("tecnet login", "cancel");
				}

				@Override
				public void onComplete(Object value) {
					if(value == null) {
						Log.w("tecnet login", "value is null");
						return;
					}
					Log.w("tecnet login", value.toString());
					JSONObject result = (JSONObject) value;
					try {
						int ret = result.getInt("ret");
						if (ret == 0) {
							Toast.makeText(ThirdPartySignInActivity.this, "登录成功", Toast.LENGTH_LONG).show();
							final String openID		 = result.getString("openid");
							final String accessToken = result.getString("access_token");
							final String expires 	 = result.getString("expires_in");
							final String openid		 = result.getString("openid");
							tencent.setOpenId(openID);
							tencent.setAccessToken(accessToken, expires);
							UserInfo userInfo = new UserInfo(ThirdPartySignInActivity.this, tencent.getQQToken());
					        userInfo.getUserInfo(new IUiListener() {

								@Override
								public void onCancel() {
									
								}

								@Override
								public void onComplete(Object value) {
									JSONObject result = (JSONObject) value;
									final Account account = new Account();
									try {
										account.setType(Account.TENCNET);
										account.setId(openid);
										account.setAlias(result.getString("nickname"));
										account.setAvatar(result.getString("figureurl_qq_1"));
										shell.setAccount(account);
										Feature feature = shell.getFeature();
										feature.registerThridPartyAccount(account, new IObserver<String>() {

											@Override
											public boolean observe(String result) {
												shell.signedIn(account);
												ThirdPartySignInActivity.this.setResult(RESULT_OK);
												ThirdPartySignInActivity.this.finish();
												return true;
											}
											
										});
										
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}

								@Override
								public void onError(UiError arg0) {
									
								}
					        	
					        });
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onError(UiError error) {
					Log.w("tecnet login", error.errorMessage);
				}
				
			};
		
		
		BaseAdapter adapter = new BaseAdapter() {

			@Override
			public int getCount() {
				return 3;
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if(convertView == null) {
					convertView = createItemView(ThirdPartySignInActivity.this, position, inflater);
				}
				return convertView;
			}
			
		};
		ListView listView = (ListView) findViewById(R.id.thirdparty);
		listView.setAdapter(adapter);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(tencent != null) {
			tencent.logout(ThirdPartySignInActivity.this);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == Constants.REQUEST_LOGIN) {
			if(resultCode == Constants.ACTIVITY_OK) {
				Tencent.onActivityResultData(requestCode, resultCode, data, tencentLoginListener);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private View createItemView(final Context context, final int index, final LayoutInflater inflater) {
		View convert = inflater.inflate(R.layout.common_item, null);
		if(index == 0) {
			ImageView icon = (ImageView) convert.findViewById(R.id.icon);
			icon.setImageResource(R.drawable.qq);
			TextView title = (TextView) convert.findViewById(R.id.title);
			title.setText("QQ");
			convert.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					tencent_login();
				}
				
			});
		}
		return convert;
	}
	
	public void tencent_login() {
		if(tencent == null)
			tencent = Tencent.createInstance("1105092774", ThirdPartySignInActivity.this.getApplicationContext());
		if (!tencent.isSessionValid())
			tencent.login(this, "all", tencentLoginListener);
	}
}
