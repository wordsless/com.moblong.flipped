package com.moblong.flipped;

import com.moblong.flipped.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public final class MaterialEditorActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_material_layout);
		final ImageView preview = (ImageView) findViewById(R.id.preview);
		Intent data = getIntent();
		final String fullname = data.getStringExtra("result");
		Bitmap org = BitmapFactory.decodeFile(fullname);
		preview.setImageBitmap(org);
		
		final EditText info = (EditText) findViewById(R.id.desc);
		
		TextView save = (TextView) findViewById(R.id.save);
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String content = info.getText().toString();
				Intent data = new Intent();
				data.putExtra("desc", content);
				data.putExtra("fullname", fullname);
				MaterialEditorActivity.this.setResult(RESULT_OK, data);
				MaterialEditorActivity.this.finish();
			}
		});
		
		View back = findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MaterialEditorActivity.this.finish();
			}
			
		});
	}

}
