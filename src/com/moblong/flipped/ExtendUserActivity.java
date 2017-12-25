package com.moblong.flipped;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moblong.flipped.R;
import com.moblong.flipped.feature.Feature;
import com.moblong.flipped.feature.IObserver;
import com.moblong.flipped.model.DetailsItem;
import com.moblong.flipped.util.SecurityReaderAndWirter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;

public final class ExtendUserActivity extends Activity {
	
	public static final int REQUEST_CITY	 = 1000;
	
	public static final int REQUEST_FULLNAME = 2000;
	
	public static final int CITY = 0, FULLNAME = 1, SEX = 2, HEIGHT = 3, JOB = 4, INCOMING = 5, NATION = 6, EDUCATION = 7, HOURSE = 8, CAR = 9, MARITAL = 10;
	
	private BaseAdapter adapter;
	
	private List<DetailsItem<?>> user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.extend_user);
		
		final LayoutInflater inflater = getLayoutInflater();
		final Resources			   rs = getResources();
		final Shell				shell = (Shell) this.getApplication();
		final boolean	         none = shell.getUser() == null;
		
		user = shell.getUser();
		if(user == null) {
			user = new ArrayList<DetailsItem<?>>(16);
			user.add(new DetailsItem<String>(0,   "所在城市", "",    0));
			user.add(new DetailsItem<String>(1,   "全名",     "",    0));
			user.add(new DetailsItem<Boolean>(2,  "性别",     false, 0));
			user.add(new DetailsItem<Integer>(3,  "身高",     170,   0));
			user.add(new DetailsItem<Integer>(4,  "职业",     2,     0));
			user.add(new DetailsItem<Integer>(5,  "收入",     2,     0));
			user.add(new DetailsItem<Integer>(6,  "民族",     2,     0));
			user.add(new DetailsItem<Integer>(7,  "学历",     2,     0));
			user.add(new DetailsItem<Integer>(8,  "购房",     2,     0));
			user.add(new DetailsItem<Integer>(9,  "购车",     2,     0));
			user.add(new DetailsItem<Integer>(10, "婚姻",     2,     0));
			shell.setUser(user);
		}
		
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
		
		final IDetegater<?>[] detegaters = new IDetegater<?>[]{
			//城市
			new IDetegater<String>() {
				
				public void popup(final String[] data, final IObserver<String> getValue) {
					ExtendUserActivity.this.startActivityForResult(new Intent(ExtendUserActivity.this, LocalCityActivity.class), REQUEST_CITY);
				}
			},
			
			//全名
			new IDetegater<String>() {
				
				public void popup(final String[] data, final IObserver<String> getValue) {
					ExtendUserActivity.this.startActivityForResult(new Intent(ExtendUserActivity.this, SettingFullnameActivity.class), REQUEST_FULLNAME);
				}
			},
			
			//性别
			new IDetegater<String>() {

				@Override
				public void popup(String[] data, final IObserver<String> getValue) {
					final String[] sex = new String[]{"男", "女"};
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(ExtendUserActivity.this, android.R.layout.simple_list_item_single_choice, sex);
					ListView view = new ListView(ExtendUserActivity.this);
					view.setAdapter(adapter);
					view.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
					view.setItemChecked(0, true);
					view.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							@SuppressWarnings("unchecked")
							DetailsItem<Boolean> item = (DetailsItem<Boolean>) user.get(DetailsItem.SEX);
							item.setContent(sex[position].equals("男"));
							getValue.observe(position == 0 ? "男" : "女");
						}});
					new AlertDialog.Builder(ExtendUserActivity.this)
					.setTitle(R.string.sex)
					.setView(view)
//					.setNegativeButton(R.string.ok, new android.content.DialogInterface.OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							if(which < 0)
//								which = 0;
//							DetailsItem<Boolean> item = (DetailsItem<Boolean>) user.get(DetailsItem.SEX);
//							item.setContent(sex[which].equals("男"));
//							getValue.observe(which == 0 ? "男" : "女");
//						}
//					})
					.show();
				}
			},
			
			//身高
			new IDetegater<Integer>() {

				@Override
				public void popup(String[] data, final IObserver<String> getValue) {
					final NumberPicker height = new NumberPicker(ExtendUserActivity.this);
					height.setMaxValue(300);
					height.setMinValue(120);
					height.setValue(170);
					new AlertDialog.Builder(ExtendUserActivity.this)
					.setTitle(R.string.height)
					.setView(height)
					.setNegativeButton(R.string.cancel, null)
					.setPositiveButton(R.string.ok, new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(which < 0)
								which = 0;
							DetailsItem<Integer> item = (DetailsItem<Integer>) user.get(DetailsItem.HEIGHT);
							item.setContent(height.getValue());
							getValue.observe(Integer.toString(height.getValue())+" m");
						}
					}).show();
				}
			},
			
			//职业
			new IDetegater<String>() {

				@Override
				public void popup(String[] data, final IObserver<String> getValue) {
					final ListView view = new ListView(ExtendUserActivity.this);
					ArrayAdapter<String> t_adapter = new ArrayAdapter<String>(ExtendUserActivity.this, android.R.layout.simple_list_item_multiple_choice);
					t_adapter.addAll(datas[DetailsItem.JOB]);
					view.setAdapter(t_adapter);
					view.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
					new AlertDialog.Builder(ExtendUserActivity.this)
					.setTitle(R.string.job)
					.setView(view)
					.setNegativeButton(R.string.cancel, null)
					.setPositiveButton(R.string.ok, new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(which < 0)
								which = 0;
							DetailsItem<Integer> item = (DetailsItem<Integer>) user.get(DetailsItem.JOB);
							getValue.observe(datas[DetailsItem.JOB][view.getCheckedItemPosition()]);
						}
						
					}).show();
				}
			},
			
			//收入
			new IDetegater<String>() {

				@Override
				public void popup(String[] data, final IObserver<String> getValue) {
					final NumberPicker incomingPicker = new NumberPicker(ExtendUserActivity.this);
					incomingPicker.setDisplayedValues(datas[DetailsItem.INCOMING]);
					incomingPicker.setMaxValue(datas[DetailsItem.INCOMING].length - 1);
					incomingPicker.setMinValue(0);
					incomingPicker.setValue(5);				
					new AlertDialog.Builder(ExtendUserActivity.this)
					.setTitle(R.string.income)
					.setView(incomingPicker)
					.setNegativeButton(R.string.cancel, null)
					.setPositiveButton(R.string.ok, new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(which < 0)
								which = 0;
							DetailsItem<Integer> item = (DetailsItem<Integer>) user.get(DetailsItem.INCOMING);
							item.setContent(incomingPicker.getValue());
							getValue.observe(datas[DetailsItem.INCOMING][incomingPicker.getValue()]);
						}
						
					}).show();
				}
			},
			
			//民族
			new IDetegater<String>() {

				@Override
				public void popup(String[] data, final IObserver<String> getValue) {
					final ArrayAdapter<String> adapter = new ArrayAdapter<String>(ExtendUserActivity.this, android.R.layout.simple_list_item_single_choice, datas[DetailsItem.NATION]); 
					final ListView listView = new ListView(ExtendUserActivity.this);
					listView.setAdapter(adapter);
					listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
					new AlertDialog.Builder(ExtendUserActivity.this)
					.setTitle(R.string.nation)
					.setView(listView)
					.setNegativeButton(R.string.cancel, null)
					.setPositiveButton(R.string.ok, new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(which < 0)
								which = 0;
							DetailsItem<Integer> item = (DetailsItem<Integer>) user.get(DetailsItem.NATION);
							item.setContent(listView.getCheckedItemPosition());
							getValue.observe(datas[DetailsItem.NATION][listView.getCheckedItemPosition()]);
						}
						
					}).show();
				}
				
			},
			
			//学历
			new IDetegater<String>() {

				@Override
				public void popup(String[] data, final IObserver<String> getValue) {
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(ExtendUserActivity.this, android.R.layout.simple_list_item_single_choice, datas[DetailsItem.EDUCATION]); 
					final ListView listView = new ListView(ExtendUserActivity.this);
					listView.setAdapter(adapter);
					listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
					
					new AlertDialog.Builder(ExtendUserActivity.this)
					.setTitle(R.string.nation)
					.setView(listView)
					.setNegativeButton(R.string.cancel, null)
					.setPositiveButton(R.string.ok, new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(which < 0)
								which = 0;
							DetailsItem<Integer> item = (DetailsItem<Integer>) user.get(DetailsItem.EDUCATION);
							item.setContent(listView.getCheckedItemPosition());
							getValue.observe(datas[DetailsItem.EDUCATION][listView.getCheckedItemPosition()]);
						}
						
					}).show();
				}
			},
			
			//购房
			new IDetegater<String>() {

				@Override
				public void popup(String[] data, final IObserver<String> getValue) {

					ArrayAdapter<String> adapter = new ArrayAdapter<String>(ExtendUserActivity.this, android.R.layout.simple_list_item_single_choice, datas[DetailsItem.HOUSE]); 
					final ListView listView = new ListView(ExtendUserActivity.this);
					listView.setAdapter(adapter);
					listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
					
					new AlertDialog.Builder(ExtendUserActivity.this)
					.setTitle(R.string.nation)
					.setView(listView)
					.setNegativeButton(R.string.cancel, null)
					.setPositiveButton(R.string.ok, new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(which < 0)
								which = 0;
							DetailsItem<Integer> item = (DetailsItem<Integer>) user.get(DetailsItem.HOUSE);
							item.setContent(listView.getCheckedItemPosition());
							getValue.observe(datas[DetailsItem.HOUSE][listView.getCheckedItemPosition()]);
						}
					}).show();
				}
				
			},
			
			//私家车
			new IDetegater<String>() {

				@Override
				public void popup(String[] data, final IObserver<String> getValue) {

					ArrayAdapter<String> adapter = new ArrayAdapter<String>(ExtendUserActivity.this, android.R.layout.simple_list_item_single_choice, datas[DetailsItem.CAR]); 
					final ListView listView = new ListView(ExtendUserActivity.this);
					listView.setAdapter(adapter);
					listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
					
					new AlertDialog.Builder(ExtendUserActivity.this)
					.setTitle(R.string.nation)
					.setView(listView)
					.setNegativeButton(R.string.cancel, null)
					.setPositiveButton(R.string.ok, new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(which < 0)
								which = 0;
							DetailsItem<Integer> item = (DetailsItem<Integer>) user.get(DetailsItem.CAR);
							item.setContent(listView.getCheckedItemPosition());
							getValue.observe(datas[DetailsItem.CAR][listView.getCheckedItemPosition()]);
						}
						
					}).show();
				}
				
			},
			
			//婚姻状况
			new IDetegater<String>() {

				@Override
				public void popup(String[] data, final IObserver<String> getValue) {
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(ExtendUserActivity.this, android.R.layout.simple_list_item_single_choice, datas[DetailsItem.MARRAGE]); 
					final ListView listView = new ListView(ExtendUserActivity.this);
					listView.setAdapter(adapter);
					listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
					
					new AlertDialog.Builder(ExtendUserActivity.this)
					.setTitle(R.string.nation)
					.setView(listView)
					.setNegativeButton(R.string.cancel, null)
					.setPositiveButton(R.string.ok, new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(which < 0)
								which = 0;
							DetailsItem<Integer> item = (DetailsItem<Integer>) user.get(DetailsItem.MARRAGE);
							item.setContent(listView.getCheckedItemPosition());
							getValue.observe(datas[DetailsItem.MARRAGE][listView.getCheckedItemPosition()]);
						}
					}).show();
				}
				
			}
		};

		adapter = new BaseAdapter() {

			@Override
			public int getCount() {
				return user.size();
			}

			@Override
			public Object getItem(int position) {
				return user.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				if(convertView == null) {
					convertView = inflater.inflate(R.layout.common_item, null);
					convertView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(final View view) {
							detegaters[position].popup(null, new IObserver<String>() {

								@Override
								public boolean observe(String result) {
									TextView status = (TextView) view.findViewById(R.id.status);
									status.setText(result);
									return true;
								}
							});
						}
						
					});
				}
				
				DetailsItem<?> item = user.get(position);
				if(item != null) {
					TextView title  = (TextView) convertView.findViewById(R.id.title);
					title.setText(item.getTitle());
					TextView status = (TextView) convertView.findViewById(R.id.status);
					if(position >= DetailsItem.JOB) {
						status.setText(datas[position][(Integer) item.getContent()]);
					} else if(position == DetailsItem.SEX) {
						status.setText(((Boolean) item.getContent()) ? "男" : "女");
					} else {
						status.setText(item.getContent().toString());
					}
				}
				return convertView;
			}
		};
		 
		View back = findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				shell.setUser(null);
				ExtendUserActivity.this.finish();
			}
		});
		
		ListView detailsListView = (ListView) this.findViewById(R.id.details);
		detailsListView.setAdapter(adapter);
		
		TextView register = (TextView)findViewById(R.id.register);
		if(!none) {
			register.setText("修改");
		}
		register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Feature feature = shell.getFeature();
				feature.submitUserDetails(shell.getSelf().getId(), user, new IObserver<String>() {

					@Override
					public boolean observe(String result) {
						if(result.equals("OK")) {
							Gson gson = new GsonBuilder()
									  	.setDateFormat("yyyy-MM-dd HH:mm:ss")
									  	.create();
							
							SecurityReaderAndWirter wirter = new SecurityReaderAndWirter();
							wirter.save(shell.root(), "user.ini", gson.toJson(user));
							ExtendUserActivity.this.finish();
							return true;
						}
						return false;
					}
				});
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_CITY) {
			String city = data.getStringExtra("city");
			if(city != null) {
				DetailsItem<String> item = (DetailsItem<String>) user.get(DetailsItem.CITY);
				item.setContent(city);
				adapter.notifyDataSetChanged();
			}
		} else if(requestCode == REQUEST_FULLNAME) {
			String fullname = data.getStringExtra("fullname");
			if(fullname != null) {
				DetailsItem<String> item = (DetailsItem<String>) user.get(DetailsItem.FULLNAME);
				item.setContent(fullname);
				adapter.notifyDataSetChanged();
			}
		}
	}
	
	private String value(final String field) {
		if(field == null || field.length() <= 0)
			return "";
		return field;
	}
}
