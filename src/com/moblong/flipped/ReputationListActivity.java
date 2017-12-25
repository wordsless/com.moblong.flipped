package com.moblong.flipped;

import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.moblong.flipped.R;
import com.moblong.flipped.model.CommonItem;
import com.moblong.flipped.util.SecurityReaderAndWirter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public final class ReputationListActivity extends Activity {

	private CommonItem[] ds;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reputation_list_layout);
		final Resources resources = getResources();
		Intent params = getIntent();
		String tag = params.getStringExtra("tag");
		
		SecurityReaderAndWirter sio = new SecurityReaderAndWirter();
		Gson gson = new GsonBuilder()
		   		    .setDateFormat("yyyy-MM-dd HH:mm:ss")
		   		    .create();
		
		List<List<Map<String, String>>> reputation = gson.fromJson(sio.read(getAssets(), "merchandise.json"), new TypeToken<List<List<Map<String, String>>>>(){}.getType());
		List<Map<String, String>>    certification = reputation.get(0);
		
		if(tag.equals("CERTIFICATION")){
			ds = new CommonItem[certification.size()];
			for(int i = 0; i < ds.length; ++i) {
				ds[i] = new CommonItem(resources.getDrawable(R.drawable.certificate_gray), certification.get(i).get("name"), "获取中...", null);
			}
		} else if(tag.equals("GENARATEE")) {
			ds = new CommonItem[]{new CommonItem(resources.getDrawable(R.drawable.certificate_gray), "体征", "获取中...", null)};
		} else {
			ds = new CommonItem[]{new CommonItem(resources.getDrawable(R.drawable.certificate_gray), "体征", "获取中...", null)};
		}
		
		final LayoutInflater inflater = getLayoutInflater();
		ListView repu = (ListView) findViewById(R.id.certification);
		repu.setAdapter(new BaseAdapter() {

			@Override
			public int getCount() {
				return ds.length;
			}

			@Override
			public Object getItem(int position) {
				return ds[position];
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if(convertView == null) {
					convertView = inflater.inflate(R.layout.common_item, parent);
				}
				
				ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
				icon.setImageDrawable(ds[position].getIcon());
				
				TextView title = (TextView) convertView.findViewById(R.id.title);
				title.setText(ds[position].getTitle());
				
				TextView status = (TextView) convertView.findViewById(R.id.status);
				status.setText(ds[position].getStatus());
				
				ImageView symbol = (ImageView) convertView.findViewById(R.id.symbol);
				symbol.setImageDrawable(ds[position].getSymbol());
				return convertView;
			}
			
		});
	}
	
	
}
