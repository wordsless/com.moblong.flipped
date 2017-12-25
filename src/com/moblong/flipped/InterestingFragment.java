package com.moblong.flipped;

import java.util.ArrayList;
import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.moblong.flipped.R;
import com.moblong.flipped.feature.IDBTirgger;
import com.moblong.flipped.feature.IObserver;
import com.moblong.flipped.feature.IUpdateUI;
import com.moblong.flipped.feature.ItemPushable;
import com.moblong.flipped.model.Account;
import com.moblong.flipped.util.InterestingController;
import com.nostra13.universalimageloader.core.ImageLoader;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public final class InterestingFragment extends Fragment implements IDBTirgger<Boolean> {

	private List<ItemPushable<Account>> ds;
	
	private InterestingController controller;
	
	private Handler handler;
	
	private BaseAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Shell shell = (Shell) getActivity().getApplication();
		controller  = (InterestingController) shell.getInterestingController();
		ds = new ArrayList<ItemPushable<Account>>(0);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.interesting_layout, container, false);
		final PullToRefreshListView listView = (PullToRefreshListView) root.findViewById(R.id.interesting);
		final Activity activity = getActivity();
		final Shell shell = (Shell) activity.getApplication();
		final RabbitMQBinder rabbitmq = shell.getRabbitmq();
		final ImageLoader loader = shell.getImageLoader();
		final TextView countView = (TextView) root.findViewById(R.id.interesting_count);
		final IUpdateUI<Integer> updater = new IUpdateUI<Integer>() {

			private int count = 0;
			
			@Override
			public void update(final Integer param) {
				count += param;
				if(count > 0) {
					countView.setText(Integer.toString(count));
					countView.setVisibility(View.VISIBLE);
				} else {
					countView.setVisibility(View.GONE);
				}
			}
			
		};
		
		adapter = new BaseAdapter() {

			@Override
			public int getCount() {
				return ds.size();
			}

			@Override
			public Object getItem(int position) {
				return ds.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@SuppressLint("InflateParams")
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				final ItemPushable<Account> item = ds.get(position);
				if (convertView == null) {
					convertView = inflater.inflate(R.layout.candidate_item, null);
					final View itemview = convertView;
					item.bind(new IObserver<String>() {

						@Override
						public boolean observe(String message) {
							int count = item.count();
							if(count > 0) {
								TextView countView = (TextView) itemview.findViewById(R.id.count);
								countView.setVisibility(View.VISIBLE);
								countView.setText(Integer.toString(count));
							} else {
								TextView countView = (TextView) itemview.findViewById(R.id.count);
								countView.setVisibility(View.GONE);
							}
							return true;
						}
						
					}, new IObserver<String>() {

						@Override
						public boolean observe(String result) {
							if (result != null) {
								final AlertDialog dialog = new AlertDialog.Builder(activity)
										.setTitle("通知")
										.setMessage(result).setIcon(R.drawable.icon)
										.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {

											@Override
											public void onClick(DialogInterface dialog, int which) {
												
											}
											
										}).create();
								dialog.show();
							}
							return true;
						}
						
					});
					item.bind(updater);
					
					convertView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							//每次点击都会消耗消息，当消息消费完之后则跳转到详情
							if(!item.consume()) {
								Intent data = new Intent(InterestingFragment.this.getActivity(), DetailsActivity.class);
								Bundle bundle = new Bundle();
								bundle.putSerializable("account", item.getTarget());
								data.putExtras(bundle);
								InterestingFragment.this.getActivity().startActivity(data);	
							}
						}
					});
				}
				ImageView header = (ImageView) convertView.findViewById(R.id.protrait);
				if(item.getTarget().getType().equals(Account.FLIPPED))
					loader.displayImage("http://"+shell.getDomain().trim()+":8079/"+item.getTarget().getAvatar().trim()+"_small", header);
				else
					loader.displayImage(item.getTarget().getAvatar(), header);
				TextView alias = (TextView) convertView.findViewById(R.id.alias);
				alias.setText(item.getTarget().getAlias());
				TextView signature = (TextView) convertView.findViewById(R.id.signature);
				signature.setText(item.getTarget().getSignature());
				return convertView;
			}

		};
		listView.setAdapter(adapter);
		
		handler = new Handler() {
			 
		    @Override
		    public void handleMessage(Message msg) {
		    	adapter.notifyDataSetChanged();
		    }
		};
		return root;
	}

	@Override
	public void onResume() {
		super.onResume();
		ds = controller.reloadAsItemPushTrigger();
		adapter.notifyDataSetChanged();
	}

	@Override
	public void trigger(Boolean result) {
		ds.clear();
		ds = controller.reloadAsItemPushTrigger();
		adapter.notifyDataSetChanged();
	}
}
