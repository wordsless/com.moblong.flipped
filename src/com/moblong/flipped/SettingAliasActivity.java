package com.moblong.flipped;

import com.moblong.flipped.R;
import com.moblong.flipped.model.Account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class SettingAliasActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_alias);
		
		final Shell     shell = (Shell) this.getApplication();
		final Account account = shell.getSelf();
		final EditText  alias = (EditText) findViewById(R.id.alias);
		
		View back = findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SettingAliasActivity.this.setResult(RESULT_CANCELED);
				SettingAliasActivity.this.finish();
			}
			
		});
		
		View save = findViewById(R.id.save);
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String content = alias.getText().toString();
				if(content == null || content.length() <= 0) {
					Toast toast=Toast.makeText(getApplicationContext(), "暱稱可以代替真實姓名，所以還是設置一個比較好。", Toast.LENGTH_SHORT);
					toast.show();
					return;
				}
				account.setAlias(content);
				Intent result = new Intent();
				result.putExtra("alias", content);
				SettingAliasActivity.this.setResult(RESULT_OK, result);
				SettingAliasActivity.this.finish();
			}
		});
		
		if(account.getAlias() != null)
			alias.setText(account.getAlias());
	}

}
