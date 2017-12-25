package com.moblong.flipped;

import com.moblong.flipped.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

public class VerificationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.verification);
		
		final Shell flipped = (Shell) this.getApplication();
		final ImageLoader imageLoader = flipped.getImageLoader();
		
		View back = findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				VerificationActivity.this.finish();
			}
			
		});
		
		View save = findViewById(R.id.save);
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText code = (EditText) findViewById(R.id.codeInput);
				Intent intent = VerificationActivity.this.getIntent();
				intent.putExtra("verification", code.getText().toString());
				VerificationActivity.this.setResult(RESULT_OK, intent);
				VerificationActivity.this.finish();
			}
			
		});
		
		imageLoader.displayImage("http://"+flipped.getDomain()+":8080/amuse/CAPTCHA.activity", (ImageView)findViewById(R.id.code));
	}

}
