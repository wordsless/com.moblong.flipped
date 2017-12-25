package com.moblong.flipped;

import java.util.List;

import com.moblong.flipped.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public final class PaymentActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.payment_layout);
		Intent intent = getIntent();
		final List<String> items = intent.getStringArrayListExtra("item");
		final LayoutInflater inflater = getLayoutInflater();
		ListView list = (ListView) findViewById(R.id.item);
		list.setAdapter(new BaseAdapter() {

			@Override
			public int getCount() {
				return items.size();
			}

			@Override
			public Object getItem(int position) {
				return items.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if(convertView == null) {
					convertView = inflater.inflate(R.layout.common_item, null);
					ImageView symbol = (ImageView) convertView.findViewById(R.id.symbol);
					symbol.setImageBitmap(null);
				}
				TextView title = (TextView) convertView.findViewById(R.id.title);
				title.setText(items.get(position));
				
				TextView status = (TextView) convertView.findViewById(R.id.status);
				status.setText("x1");
				return convertView;
			}
			
		});
		
		final String[] paymentWays = new String[]{"微信支付", "支付宝"};
		ListView payment = (ListView) findViewById(R.id.payment_way);
		payment.setAdapter(new BaseAdapter() {

			@Override
			public int getCount() {
				return paymentWays.length;
			}

			@Override
			public Object getItem(int position) {
				return paymentWays[position];
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if(convertView == null) {
					convertView = inflater.inflate(R.layout.common_item, null);
					ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
					TextView title = (TextView) convertView.findViewById(R.id.title);
					if(position == 0) {
						icon.setImageResource(R.drawable.weixin);
						title.setText(paymentWays[position]);
					} else if(position == 1) {
						icon.setImageResource(R.drawable.alipay);
						title.setText(paymentWays[position]);
					}
				}
				return convertView;
			}
			
		});
	}

}
