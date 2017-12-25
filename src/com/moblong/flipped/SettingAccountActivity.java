package com.moblong.flipped;

import java.util.regex.Pattern;

import com.moblong.flipped.R;
import com.moblong.flipped.model.Account;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class SettingAccountActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_account);
		
		final Shell         shell = (Shell) this.getApplication();
		final Account     account = shell.getSelf();
		final EditText      phone = (EditText) findViewById(R.id.account);
		final Resources resources = getResources();
		
		View back = findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SettingAccountActivity.this.setResult(RESULT_CANCELED);
				SettingAccountActivity.this.finish();
			}
			
		});
		
		View save = findViewById(R.id.save);
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String content = phone.getText().toString();
				if(content == null || content.length() <= 0) {
					Toast toast=Toast.makeText(getApplicationContext(), "手機號不能為空。", Toast.LENGTH_SHORT);
					toast.show();
					return;
				}
				Pattern number = Pattern.compile("[0-9]*");
				if(number.matcher(content).matches()){
					account.setTelephone(content);
					Intent result = new Intent();
					result.putExtra("account", content);
					SettingAccountActivity.this.setResult(RESULT_OK, result);
					SettingAccountActivity.this.finish();
				} else {
					Toast toast=Toast.makeText(getApplicationContext(), resources.getString(R.string.please_fillout_your_phone), Toast.LENGTH_SHORT);
					toast.show();
				}
			}
			
		});
		
		if(account.getTelephone() != null)
			phone.setText(account.getTelephone());
	}

}
