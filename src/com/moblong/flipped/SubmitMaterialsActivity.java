package com.moblong.flipped;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.moblong.flipped.R;
import com.moblong.flipped.feature.Feature;
import com.moblong.flipped.feature.IObserver;
import com.moblong.flipped.model.Account;
import com.moblong.flipped.util.MaterialController;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public final class SubmitMaterialsActivity extends Activity {

	public final static int CHOOSE_PICTURE = 6000;
	
	public final static int CHOOSED_DESC = 7000;
	
	public final static int PAYMENT = 8000;
	
	private List<Map<String, String>> choosed;
	
	private int choosedIndex = 0;
	
	private BaseAdapter adapter;
	
	public List<Map<String, String>> getChoosed() {
		return choosed;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.submit_materials_layout);
		
		final Intent data = getIntent();
		final Shell     shell = (Shell) getApplication();
		final Feature feature = shell.getFeature();
		final Account account = shell.getSelf();
		final MaterialController controller = (MaterialController) shell.getMaterialController();
		final Resources resources = getResources();
		final int param = data.getExtras().getInt("param");
		final LayoutInflater inflater = getLayoutInflater();
		final float scale = resources.getDisplayMetrics().density;
		
		controller.bind(param);
		TextView descript = (TextView) findViewById(R.id.descript);
		descript.setText(data.getExtras().getString("desc"));
		
		choosed = new ArrayList<Map<String, String>>();
		Map<String, String> col = new HashMap<String, String>(2);
		col.put("res",  null);
		col.put("desc", null);
		choosed.add(col);
		
		View back = findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SubmitMaterialsActivity.this.setResult(RESULT_CANCELED);
				SubmitMaterialsActivity.this.finish();
			}
			
		});
		
		adapter = new BaseAdapter() {

			@Override
			public int getCount() {
				return choosed.size();
			}

			@Override
			public Object getItem(int position) {
				return choosed.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}
			
			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				final Map<String, String> item = choosed.get(position);
				if(convertView == null) {
					convertView = inflater.inflate(R.layout.common_item, null);
					final ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
					LayoutParams param = icon.getLayoutParams();
					param.width  = (int) (40 * scale + 0.5f);
					param.height = (int) (40 * scale + 0.5f);
					icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					icon.setLayoutParams(param);
					
					final ImageView symbol = (ImageView) convertView.findViewById(R.id.symbol);
					symbol.setImageResource(R.drawable.choose);
					symbol.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							choosedIndex = position;
							if(item.get("res") == null) {
								Map<String, String> col = new HashMap<String, String>(2);
								col.put("res",  null);
								col.put("desc", null);
								choosed.add(col);
							}
							Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							SubmitMaterialsActivity.this.startActivityForResult(intent, CHOOSE_PICTURE);
						}
						
					});
				}
				
				if(item.get("res") != null) {
					ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
					Bitmap pic = BitmapFactory.decodeFile(item.get("res"));
					icon.setImageBitmap(pic);
				}
				
				if(item.get("desc") != null) {
					TextView desc = (TextView) convertView.findViewById(R.id.title);
					desc.setText(item.get("desc"));
				}
				return convertView;
			}
		};
		ListView choose = (ListView) findViewById(R.id.choose);
		choose.setAdapter(adapter);
		
		View submit = findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if((choosed.size() - 1) <= 0) {
					Toast toast=Toast.makeText(getApplicationContext(), "请提交相关资料", Toast.LENGTH_SHORT);
					toast.show();
					return;
				}
				choosed.remove(choosed.size() - 1);//最后一个是空，要删掉
				feature.submitUserMaterials(SubmitMaterialsActivity.this, account.getId(), data.getStringExtra("param"), choosed, new IObserver<String>() {

					@Override
					public boolean observe(String result) {
						MaterialController controller = (MaterialController) shell.getMaterialController();
						controller.add(choosed);
						
						ArrayList<String> array = new ArrayList<String>();
						for(Map<String, String> item : choosed) {
							array.add(item.get("desc"));
						}
						Intent data = new Intent(SubmitMaterialsActivity.this, PaymentActivity.class);
						data.putStringArrayListExtra("item", array);
						SubmitMaterialsActivity.this.startActivityForResult(data, PAYMENT);
						return true;
					}
				});
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == CHOOSE_PICTURE && resultCode == RESULT_OK) {
			String path = null;
			Uri selectedImage = data.getData();
			if(android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.KITKAT) {
	            String[] filePathColumn = { MediaStore.Images.Media.DATA };
	            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
	            cursor.moveToFirst();
	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	            path = cursor.getString(columnIndex);
	            cursor.close();
			} else {
				path = selectedImage.getPath();
			}
            Intent result = new Intent(this, MaterialEditorActivity.class);
            result.putExtra("result", path);
            startActivityForResult(result, CHOOSED_DESC);
		} else if(requestCode == CHOOSED_DESC && resultCode == RESULT_OK) {
			String desc = data.getStringExtra("desc");
			String fullname = data.getStringExtra("fullname");
			Map<String, String> item = choosed.get(choosedIndex);
			item.put("res",  fullname);
			item.put("desc", desc);
			adapter.notifyDataSetChanged();
		} else if(requestCode == PAYMENT && resultCode == RESULT_OK) {
			SubmitMaterialsActivity.this.setResult(RESULT_OK);
			SubmitMaterialsActivity.this.finish();
		}
	}
}
