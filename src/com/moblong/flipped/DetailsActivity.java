package com.moblong.flipped;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.moblong.flipped.R;
import com.moblong.flipped.feature.Feature;
import com.moblong.flipped.feature.IDBTirgger;
import com.moblong.flipped.feature.IObserver;
import com.moblong.flipped.model.Account;
import com.moblong.flipped.model.DetailsItem;
import com.moblong.flipped.model.Indicator;
import com.moblong.flipped.model.Whistle;
import com.moblong.flipped.util.DatabaseController;
import com.moblong.flipped.util.InterestingController;
import com.moblong.flipped.wedget.CircleImageView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public final class DetailsActivity extends Activity {

	private IDBTirgger<Boolean> subscribe, cancel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Account target = (Account) getIntent().getSerializableExtra("account");
		final Shell shell = (Shell) getApplication();
		final LayoutInflater inflater = getLayoutInflater();
		final InterestingController controller = (InterestingController) shell.getInterestingController();
		final Feature feature = shell.getFeature();
		final RelativeLayout contentView = (RelativeLayout) inflater.inflate(R.layout.detail_layout, null);
		final Resources rs = getResources();
		final boolean exsit = controller.exsit(target);
		final Handler handler = new Handler();
		
		setContentView(contentView);
		
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(target.getAlias());
		
		View back = findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DetailsActivity.this.finish();
			}
			
		});
		
		final ProgressDialog progress = new ProgressDialog(this);
		// 设置进度条风格，风格为圆形，旋转的
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // 设置ProgressDialog 提示信息
		progress.setMessage("请稍等 ... ...");
        // 设置ProgressDialog 的进度条是否不明确
		progress.setIndeterminate(false);
        // 设置ProgressDialog 是否可以按退回按键取消
		progress.setCancelable(false);
		subscribe = new IDBTirgger<Boolean>() {

			@Override
			public void trigger(Boolean result) {
				final TextView focus = (TextView) contentView.findViewById(R.id.focus);
				focus.setText("取消关注");
				focus.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						progress.show();
						controller.delete(target);
						progress.dismiss();
					}
					
				});
			}
			
		};
		
		cancel = new IDBTirgger<Boolean>() {

			@Override
			public void trigger(Boolean result) {
				final TextView focus = (TextView) contentView.findViewById(R.id.focus);
				focus.setText("关注");
				focus.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						progress.show();
						controller.add(target);
						progress.dismiss();
					}
					
				});
			}
			
		};
		
		controller.register("add", subscribe);
		controller.register("delete", cancel);
		
		final String[][] datas = new String[][]{
			null, null, null, null,
			rs.getStringArray(R.array.job),
			rs.getStringArray(R.array.income),
			rs.getStringArray(R.array.nation),
			rs.getStringArray(R.array.education),
			rs.getStringArray(R.array.housing),
			rs.getStringArray(R.array.car),
			rs.getStringArray(R.array.marital_status)
		};
		
		final List<DetailsItem<?>> ds = new ArrayList<DetailsItem<?>>(11);
		final ListView listView = (ListView) this.findViewById(R.id.details);
		final BaseAdapter adapter = new BaseAdapter() {

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

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if(convertView == null) {
					convertView = inflater.inflate(R.layout.user_details_item_layout, null);
				}
				
				DetailsItem<?> item = ds.get(position);
				
				TextView title = (TextView) convertView.findViewById(R.id.title);
				title.setText(item.getTitle());
				
				TextView level = (TextView) convertView.findViewById(R.id.level);
				level.setText(item.getCondition() > 0 ? "已验证" : "未验证");
				
				TextView content = (TextView) convertView.findViewById(R.id.content);
				if(position >= DetailsItem.JOB) {
					content.setText(datas[position][(Integer) item.getContent()]);
				} else if(position == DetailsItem.SEX) {
					content.setText(((Boolean) item.getContent()) ? "男" : "女");
				} else {
					content.setText(item.getContent().toString());
				}
				return convertView;
			}
		};
		listView.setAdapter(adapter);
	
		feature.requestIndicator(target, new IObserver<List<DetailsItem<?>>>() {

			@Override
			public boolean observe(final List<DetailsItem<?>> indicatores) {
				if(indicatores == null)
					return false;
				handler.post(new Runnable() {

					@Override
					public void run() {
						ds.clear();
						ds.addAll(indicatores);
						adapter.notifyDataSetChanged();
					}
					
				});
				return true;
			}
			
		});
		
		TextView inviter = (TextView) findViewById(R.id.hi);
		inviter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Account session = shell.getSelf();
				RabbitMQBinder rabbitmq = shell.getRabbitmq();
				Whistle message = new Whistle();
				message.setId(UUID.randomUUID().toString().replace("-", ""));
				message.setSource(session.getId());
				message.setTarget(target.getId());
				message.setContent("hello");
				rabbitmq.send(message);
			}
			
		});

		TextView foucs = (TextView) findViewById(R.id.focus);
		if(exsit) {
			
			foucs.setText(rs.getString(R.string.cancel_focus));
			foucs.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					controller.delete(target);
				}
				
			});
		} else {
			foucs.setText(rs.getString(R.string.focus));
			foucs.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					controller.add(target);
				}
				
			});
		}
		
		CircleImageView protrait = (CircleImageView) contentView.findViewById(R.id.avater);
		if(target.getType().equals(Account.FLIPPED) && target.getAvatar() != null && protrait != null) {
			shell.getImageLoader().displayImage(String.format("http://%s:8079/%s_small", new Object[]{shell.getDomain(), target.getAvatar()}), protrait);
		} else {
			shell.getImageLoader().displayImage(target.getAvatar(), protrait);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		final Shell	shell = (Shell) getApplication();
		final DatabaseController<Account> controller = shell.getInterestingController();
		controller.unregister(subscribe);
		controller.unregister(cancel);
	}
}
