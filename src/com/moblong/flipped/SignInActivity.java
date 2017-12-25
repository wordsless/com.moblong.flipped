package com.moblong.flipped;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.moblong.flipped.R;
import com.moblong.flipped.feature.Feature;
import com.moblong.flipped.feature.IObserver;
import com.moblong.flipped.model.Account;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public final class SignInActivity extends Activity {

	public final static int THIRD_PARTY_SIGNIN = 1000;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signin_layout);
		
		final Shell shell = (Shell) getApplication();
		final Account user = shell.getSelf();
		final Resources resources = getResources();
		
		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				Toast toast = Toast.makeText(getApplicationContext(), (String) msg.obj, Toast.LENGTH_SHORT);
				toast.show();
			}
			
		};
		
		//验证账户输入是否符合要求
		final EditText account = (EditText) findViewById(R.id.account);
		account.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String emailAddressOrPhoneNumber = account.getText().toString();
				if(isTelphoneNumber(emailAddressOrPhoneNumber)) {
					user.setTelephone(emailAddressOrPhoneNumber);
				} else {
					Message msg = new Message();
					msg.what = 0;
					msg.obj = resources.getString(R.string.note_account);
					handler.sendMessage(msg);
				}
			}
			
		});
		
		//验证密码输入是否符合要求
		final EditText password = (EditText) findViewById(R.id.password);
		password.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String content = password.getText().toString();
				if(didCompliancePassword(content)) {
					//TODO 密码不能作为对象的一部分，但是必须是登录协议的一部分。
				} else {
					Message msg = new Message();
					msg.what = 0;
					msg.obj = resources.getString(R.string.note_of_password);
					handler.sendMessage(msg);
				}
			}
			
		});
		
		//忘记密码
		final TextView forgetPassword = (TextView) findViewById(R.id.forget_password);
		forgetPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//TODO 跳转到找回密码的页面
			}
			
		});
		
		//登录
		final TextView signIn = (TextView) findViewById(R.id.signIn);
		signIn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//TODO 发起登录
				final String phone  = account.getText().toString();
				final String passwd = password.getText().toString();
				final Feature feature = shell.getFeature();
				feature.signIn(phone, passwd, new IObserver<Account>() {

					@Override
					public boolean observe(Account account) {
						if(account == null) {
							Toast toast=Toast.makeText(getApplicationContext(), "登录失败，请检查用户名和密码！", Toast.LENGTH_SHORT);
							toast.show();
							return false;
						} else {
							//登录成功
							account.setAnonymous(false);
							if(shell.getSelf().isAnonymous())
								feature.requestDeleteAnonymous(shell.getSelf().getId(), null);
							shell.signedIn(account);
							SignInActivity.this.setResult(RESULT_OK);
							SignInActivity.this.finish();
							return true;
						}
					}
				});
			}
		});
		
		//回退，退回去就是注册界面
		final View back = findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SignInActivity.this.setResult(RESULT_CANCELED);
				SignInActivity.this.finish();
			}
		});
		
		final View thirdparty = findViewById(R.id.thirdparty);
		thirdparty.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SignInActivity.this, ThirdPartySignInActivity.class);
				SignInActivity.this.startActivityForResult(intent, THIRD_PARTY_SIGNIN);
			}
			
		});
	}
	
	private boolean isTelphoneNumber(final String content) {
		Pattern pattern = Pattern.compile("^-?\\d+$");
		Matcher matcher = pattern.matcher(content);
		return matcher.matches();
	}
	
	private boolean didCompliancePassword(final String content) {
		char[] chs = content.toCharArray();
		if(chs.length < 6 || chs.length > 16)
			return false;
		boolean rule = true;
		for(char c : chs) {
			rule &= (c >= 33 && c <= 126);
		}
		return rule;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == THIRD_PARTY_SIGNIN) {
			if(resultCode == RESULT_OK) {
				SignInActivity.this.setResult(RESULT_OK);
				SignInActivity.this.finish();
			}
		}
	}
	
}
