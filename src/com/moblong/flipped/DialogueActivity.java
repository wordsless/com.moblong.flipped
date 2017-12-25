package com.moblong.flipped;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.moblong.flipped.model.Account;
import com.moblong.flipped.model.Whistle;
import com.moblong.iwe.DialoguesManagementEngine;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public final class DialogueActivity extends Activity {

	private BaseAdapter adapter;
	
	private List<DialoguesManagementEngine> conversation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogue_layout);
		Intent data = getIntent();
		Account account = (Account) data.getSerializableExtra("account");
		final Shell shell = (Shell) getApplication();
		Account myself = shell.getSelf();
		final ImageLoader loader = shell.getImageLoader();
		final LayoutInflater inflater = getLayoutInflater();
		
		conversation = new ArrayList<DialoguesManagementEngine>();
		
		adapter = new BaseAdapter() {

			@Override
			public int getCount() {
				return conversation.size();
			}

			@Override
			public Object getItem(int position) {
				return conversation.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if(convertView == null) {
					convertView = (ViewGroup) inflater.inflate(R.layout.common_item, null);
				}
				DialoguesManagementEngine dialogue = conversation.get(position);
				ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
				loader.displayImage("http://"+shell.getDomain().trim()+":8079/" + dialogue.getAccount().getAvatar().trim() + "_small", icon);
				TextView title = (TextView) convertView.findViewById(R.id.title);
				Whistle<?> whistle = dialogue.getHistory().get(position);
				if(whistle.getAction().equals("com.moblong.INVITE"))
					title.setText((String) whistle.getContent());
				return convertView;
			}
			
		};
		
		ListView dialogue = (ListView) findViewById(R.id.dialogue);
		dialogue.setAdapter(adapter);
	}

}
