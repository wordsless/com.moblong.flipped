package com.moblong.flipped;

import com.moblong.flipped.R;
import com.moblong.flipped.model.Configuration;
import com.zcw.togglebutton.ToggleButton;
import com.zcw.togglebutton.ToggleButton.OnToggleChanged;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class SettingFullnameActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_fullname);
		
		final Shell          shell = (Shell) getApplication();
		final Configuration config = shell.getConfig();
		final ToggleButton visible = (ToggleButton) findViewById(R.id.visible);
		final Resources  resources = getResources();
		
		visible.toggleOn();
		config.setFullnameVisible(true);
		visible.setOnToggleChanged(new OnToggleChanged(){
			
            @Override
            public void onToggle(boolean on) {
            	config.setFullnameVisible(on);
            }
		});
		
		View back = findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SettingFullnameActivity.this.setResult(RESULT_CANCELED);
				SettingFullnameActivity.this.finish();
			}
			
		});
		
		View save = findViewById(R.id.save);
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText fullnameField = (EditText) findViewById(R.id.fullname);
				String fullname = fullnameField.getText().toString().trim();
				if(fullname == null || fullname.length() == 0) {
					Toast toast=Toast.makeText(getApplicationContext(), resources.getString(R.string.please_fillout_your_fullname), Toast.LENGTH_SHORT);
					toast.show();
					return;
				}
				Intent data = new Intent();
				data.putExtra("fullname", fullname);
				SettingFullnameActivity.this.setResult(RESULT_OK,data);
				SettingFullnameActivity.this.finish();
			}
			
		});
	}

}
