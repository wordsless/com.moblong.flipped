package com.moblong.flipped;

import com.moblong.flipped.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.moblong.flipped.model.Account;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public final class HomeActivity extends Activity {

	public static final int CANDIDATE = 1, INTERESTING = 2, AUTHENTICATION = 3, DATING = 4;
	
	private Fragment candidate;
	
	private Fragment interesting;
	
	private Fragment authentication;
	
	private Fragment dating;
	
	private FragmentManager fragmentManager;
	
	private ImageView[] iconViews;
	
	private int[] gray;
	
	private String[] items;
	
	private Class[]	classes;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		final Shell	shell = (Shell) getApplication();
		final ImageLoader load = shell.getImageLoader();
		final Resources resource = getResources();
		final LayoutInflater flater = getLayoutInflater();
		items = resource.getStringArray(R.array.settings_item);
		final int[] icons = new int[]{R.drawable.details, R.drawable.lovetype, R.drawable.update, R.drawable.update_password};
		classes = new Class[]{ExtendUserActivity.class, TendentiousSearchActivity.class, UpdateActivity.class, SettingPasswordActivity.class};
		final Account account = shell.getSelf();
		
		if(account.isAnonymous()) {
			items[2] = "注册";
			classes[2] = RegisterActivity.class;
		}
		
		candidate		= new CandidatesFragment();
		interesting		= new InterestingFragment();
		authentication	= new CertificationFragment();
		dating			= new DatingFragment();
		
		fragmentManager = this.getFragmentManager();
		fragmentManager.beginTransaction()
					   .add(R.id.fragment_container, candidate)
					   .commit();
		
		ImageView icon = (ImageView) findViewById(R.id.refer_icon);
		gray = new int[]{R.drawable.refer_a_friend_icon_gray, R.drawable.favorite_gray, R.drawable.certificate_gray, R.drawable.dates_gray};
		icon.setImageDrawable(this.getResources().getDrawable(R.drawable.refer_a_friend_icon));
		iconViews = new ImageView[]{(ImageView) findViewById(R.id.refer_icon), (ImageView) findViewById(R.id.favorite_icon), (ImageView) findViewById(R.id.authentication_icon), (ImageView) findViewById(R.id.dating_icon)};
		
		final SlidingMenu menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.slidingmenu_shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// menu.setBehindOffset(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.settings_layout);
		
		ImageView setting = (ImageView) findViewById(R.id.setting);
		setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				menu.showMenu();
			}
			
		});
		
		ViewGroup cadidate = (ViewGroup) findViewById(R.id.candidate);
		cadidate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				choiceContentFragment(CANDIDATE);
			}
			
		});
		
		ViewGroup favorite = (ViewGroup) findViewById(R.id.favorite);
		favorite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				choiceContentFragment(INTERESTING);
			}
			
		});
		
		ViewGroup cert = (ViewGroup) findViewById(R.id.certificate);
		cert.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				choiceContentFragment(AUTHENTICATION);
			}
		});
		
		ImageView portrait = (ImageView) findViewById(R.id.menubar_portrait);
		portrait.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, UpdateActivity.class);
				HomeActivity.this.startActivity(intent);
				menu.toggle(true);
			}
		});
		
		ListView list = (ListView) findViewById(R.id.menu);
		list.setAdapter(new BaseAdapter() {

			@Override
			public int getCount() {
				return items.length;
			}

			@Override
			public Object getItem(int position) {
				return items[position];
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				if(convertView == null) {
					convertView = flater.inflate(R.layout.common_item, null);
					convertView.setBackgroundColor(resource.getColor(R.color.smoky));
					TextView title = (TextView) convertView.findViewById(R.id.title);
					title.setTextColor(resource.getColor(R.color.dimgray));
					title.setText(items[position]);
					ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
					icon.setImageResource(icons[position]);
					ImageView status = (ImageView) convertView.findViewById(R.id.symbol);
					status.setImageDrawable(null);
					convertView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(HomeActivity.this, classes[position]);
							HomeActivity.this.startActivity(intent);
							menu.toggle(true);
						}
						
					});
				}
				return convertView;
			}
			
		});
		
		shell.initialized(true);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Shell	shell = (Shell) getApplication();
		Account account = shell.getSelf();
		ImageLoader load = shell.getImageLoader();
		ImageView portrait = (ImageView) findViewById(R.id.menubar_portrait);
		if(account.getAvatar() != null) {
			if(account.getType().equals(Account.FLIPPED)) {
				String uri = String.format("http://%s:%s/%s", new Object[]{shell.getDomain(), "8079", account.getAvatar()});
				load.displayImage(uri, portrait);
			} else {
				load.displayImage(account.getAvatar(), portrait);
			}
		} else {
			portrait.setImageResource(R.drawable.avatar);
		}
		
		if(account.getAlias() != null) {
			TextView alias = (TextView) findViewById(R.id.alias);
			alias.setText(account.getAlias());
		}
		
		if(!account.isAnonymous()) {
			items[2] = "修改账户信息";
			classes[2] = UpdateActivity.class;
			ListView list = (ListView) findViewById(R.id.menu);
			BaseAdapter adapter = (BaseAdapter) list.getAdapter();
			adapter.notifyDataSetChanged();
		}
	}

	public void choiceContentFragment(final int item) {
		
		for(int c = 0; c < iconViews.length; ++c) {
			ImageView view = iconViews[c];
			view.setImageDrawable(this.getResources().getDrawable(gray[c]));
		}
		
		switch(item) {
		case CANDIDATE:
		{
			fragmentManager.beginTransaction().replace(R.id.fragment_container, candidate).commit();
			ImageView icon = (ImageView) findViewById(R.id.refer_icon);
			icon.setImageDrawable(this.getResources().getDrawable(R.drawable.refer_a_friend_icon));
			break;
		}
		case INTERESTING:
		{
			fragmentManager.beginTransaction().replace(R.id.fragment_container, interesting).commit();
			ImageView icon = (ImageView) findViewById(R.id.favorite_icon);
			icon.setImageDrawable(this.getResources().getDrawable(R.drawable.favorite));
			break;
		}
		case AUTHENTICATION:
		{
			fragmentManager.beginTransaction().replace(R.id.fragment_container, authentication).commit();
			ImageView icon = (ImageView) findViewById(R.id.authentication_icon);
			icon.setImageDrawable(this.getResources().getDrawable(R.drawable.certificate));
			break;
		}
		case DATING:
		{
			fragmentManager.beginTransaction().replace(R.id.fragment_container, dating).commit();
			ImageView icon = (ImageView) findViewById(R.id.dating_icon);
			icon.setImageDrawable(this.getResources().getDrawable(R.drawable.dates));
			break;
		}
		}
	}
}
