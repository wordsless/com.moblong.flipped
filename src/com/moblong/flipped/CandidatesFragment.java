package com.moblong.flipped;

import java.util.LinkedList;
import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.moblong.flipped.R;
import com.moblong.flipped.feature.Feature;
import com.moblong.flipped.feature.IDBTirgger;
import com.moblong.flipped.feature.IObserver;
import com.moblong.flipped.model.Account;
import com.moblong.flipped.util.DatabaseController;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public final class CandidatesFragment extends Fragment implements IDBTirgger<Boolean> {

	private int page = 0;

	private List<Account> candidates = new LinkedList<Account>();

	private BaseAdapter mAdapter;

	private Handler handler;
	
	private DatabaseController<Account> controller;
	
	private IObserver<List<Account>> resp;
	
	private int reloadCount;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.candidates, container, false);
		final Shell shell = (Shell) this.getActivity().getApplication();
		controller = shell.getInterestingController();
		
		final Feature feature = shell.getFeature();
		build(getActivity(), layout, shell.getSelf());
		resp = null;
		if(page == 0) {
			resp = new IObserver<List<Account>>() {
				
				@Override
				public boolean observe(List<Account> candidate) {
					if(candidate != null && candidate.size() > 0) {
						page++;
						Message msg = new Message();
						msg.what = 0;
						msg.obj = candidate;
						handler.sendMessage(msg);
					} else {
						if(reloadCount < 3)
							feature.requestCandidates(shell.getSelf().getId(), page, resp);
					}
					return true;
				}
			};
			feature.requestCandidates(shell.getSelf().getId(), page, resp);
		}
		return layout;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		controller.unregister(this);
	}

	public void build(final Activity activity, final ViewGroup container, final Account account) {

		final Shell             shell = (Shell) activity.getApplication();
		final ImageLoader      loader = shell.getImageLoader();
		final LayoutInflater inflater = LayoutInflater.from(activity);

		mAdapter = new BaseAdapter() {

			@Override
			public int getCount() {
				return candidates.size();
			}

			@Override
			public Object getItem(int position) {
				return candidates.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(final int position, View convertView, final ViewGroup parent) {
				final Account item = candidates.get(position);
				if (convertView == null) {
					convertView = inflater.inflate(R.layout.candidate_item, null);
					convertView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent data = new Intent(activity, DetailsActivity.class);
							Bundle bundle = new Bundle();
							bundle.putSerializable("account", item);
							data.putExtras(bundle);
							activity.startActivity(data);
						}
						
					});
				}
				
				if(item.getType().equals(Account.FLIPPED) && item.getAvatar() != null) {
					ImageView header = (ImageView) convertView.findViewById(R.id.protrait);
					loader.displayImage("http://"+shell.getDomain().trim()+":8079/"+item.getAvatar().trim()+"_small", header);
				} else {
					ImageView header = (ImageView) convertView.findViewById(R.id.protrait);
					loader.displayImage(item.getAvatar(), header);
				}
				
				TextView alias = (TextView) convertView.findViewById(R.id.alias);
				alias.setText(item.getAlias());
				TextView signature = (TextView) convertView.findViewById(R.id.signature);
				signature.setText(item.getSignature());
				
				return convertView;
			}
		};

		final PullToRefreshListView candidateListview = (PullToRefreshListView) container.findViewById(R.id.candidates);
		candidateListview.setAdapter(mAdapter);
		candidateListview.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refresh) {
				String label = DateUtils.formatDateTime(activity, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refresh.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				Feature feature = shell.getFeature();
				feature.requestCandidates(shell.getSelf().getId(), page, new IObserver<List<Account>>() {
					
					@Override
					public boolean observe(List<Account> candidate) {
						if(candidate != null && candidate.size() > 0) {
							++page;
							Message msg = new Message();
							msg.what = 0;
							msg.obj = candidate;
							handler.sendMessage(msg);
						}
						return true;
					}
				});
			}
		});
		
		handler = new Handler() {

			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				candidates.addAll((List<Account>) msg.obj);
				candidateListview.onRefreshComplete();
			}
		};
	}

	@Override
	public void trigger(Boolean result) {
		
	}
}