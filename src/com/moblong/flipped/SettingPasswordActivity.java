package com.moblong.flipped;

import com.moblong.flipped.R;
import com.moblong.flipped.R.id;
import com.moblong.flipped.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class SettingPasswordActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_password);
		
		final EditText pwd = (EditText) findViewById(R.id.password);
		final EditText confirmInput = (EditText) findViewById(R.id.confirm_password);
		
		Intent intent = getIntent();
		String password = intent.getStringExtra("password");
		if(password != null && password.length() > 0) {
			pwd.setText(password);
			confirmInput.setText(password);
		}
		
		View back = findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SettingPasswordActivity.this.setResult(RESULT_CANCELED);
				SettingPasswordActivity.this.finish();
			}
			
		});
		
		View save = findViewById(R.id.save);
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String password = pwd.getText().toString();
				String confirm  = confirmInput.getText().toString();
				if(password == null || password.length() < 6 || password.length() > 16) {
					Toast toast=Toast.makeText(getApplicationContext(), "请按照提示输入密码。", Toast.LENGTH_SHORT);
					toast.show();
					return;
				}
				
				if(!confirm.equals(password)) {
					Toast toast=Toast.makeText(getApplicationContext(), "密码输入不一致，请检查后重新输入。", Toast.LENGTH_SHORT);
					toast.show();
					return;
				}
				Intent intent = getIntent();
				intent.putExtra("password", password);
				SettingPasswordActivity.this.setResult(RESULT_OK, intent);
				SettingPasswordActivity.this.finish();
			}
			
		});
	}

}
