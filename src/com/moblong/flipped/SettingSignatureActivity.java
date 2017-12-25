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

public class SettingSignatureActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signature);
		
		final Shell global = (Shell) this.getApplication();
		final Account account = global.getSelf();
		final EditText signature = (EditText) findViewById(R.id.signature);
		
		View back = findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SettingSignatureActivity.this.setResult(RESULT_CANCELED);
				SettingSignatureActivity.this.finish();
			}
			
		});
		
		View save = findViewById(R.id.save);
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String content = signature.getText().toString();
				if(content == null || content.length() < 2) {
					Toast toast=Toast.makeText(getApplicationContext(), "請留下您的內心獨白。", Toast.LENGTH_SHORT);
					toast.show();
					return;
				}
				account.setSignature(signature.getText().toString());
				Intent result = new Intent();
				result.putExtra("signature", signature.getText().toString());
				SettingSignatureActivity.this.setResult(RESULT_OK, result);
				SettingSignatureActivity.this.finish();
			}
		});
		
		if(account.getSignature() != null)
			signature.setText(account.getSignature());
	}

}
