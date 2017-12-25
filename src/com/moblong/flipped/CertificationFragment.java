package com.moblong.flipped;

import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.moblong.flipped.R;
import com.moblong.flipped.util.SecurityReaderAndWirter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public final class CertificationFragment extends Fragment {

	private final static int BASIC_PROPERTY = 1000;
	
	private List<Map<String, String>> cert;
	
	private int selectedIndex = 0;
	
	private BaseAdapter adapter;
	
	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final Gson gson = new GsonBuilder()
				  			  .setDateFormat("yyyy-MM-dd HH:mm:ss")
				  			  .create();
		ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.certification_layout, container, false);
		SecurityReaderAndWirter reader = new SecurityReaderAndWirter();
		final List<List<Map<String, String>>> pool = gson.fromJson(reader.read(getActivity().getAssets(), "merchandise.json"), new TypeToken<List<List<Map<String, String>>>>(){}.getType());
		cert = pool.get(0);
		final List<Map<String, String>> gena = pool.get(1);
		final Activity activity = getActivity();
		
		adapter = new BaseAdapter() {

			@Override
			public int getCount() {
				return cert.size();
			}

			@Override
			public Object getItem(int position) {
				return cert.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(final int position, View convert, ViewGroup parent) {
				final Map<String, String> item = cert.get(position);
				if(convert == null) {
					convert = inflater.inflate(R.layout.common_item, null);
					convert.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							selectedIndex = position;
							Intent intent = new Intent(activity, SubmitMaterialsActivity.class);
							intent.putExtra("param", item.get("name"));
							intent.putExtra("desc",  item.get("desc"));
							startActivityForResult(intent, BASIC_PROPERTY);
						}
						
					});
				}
				
				TextView title = (TextView) convert.findViewById(R.id.title);
				title.setText(item.get("name"));
				TextView status = (TextView) convert.findViewById(R.id.status);
				status.setText(item.get("status"));
				
				return convert;
			}
			
		};
		
		ListView list = (ListView) layout.findViewById(R.id.certification);
		list.setAdapter(adapter);
		return layout;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == BASIC_PROPERTY && resultCode == Activity.RESULT_OK) {
			Map<String, String> item = cert.get(selectedIndex);
			item.put("status", "审核中，请等待");
			adapter.notifyDataSetChanged();
		}
	}

}
