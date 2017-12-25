package com.moblong.flipped;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.moblong.flipped.R;
import com.moblong.flipped.feature.IObserver;
import com.moblong.flipped.model.DetailsItem;
import com.moblong.flipped.util.SecurityReaderAndWirter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;

public final class TendentiousSearchActivity extends Activity {

	public static final int REQUEST_CITY = 1000;
	
	private BaseAdapter adapter;
	
	private List<DetailsItem<?>> tendenties;
	
	public static final int CITY = 0, SEX = 1, HEIGHT = 2, JOB = 3, INCOMING = 4, NATION = 5;

	public static final int CAR = 8;

	public static final int HOUSE = 7;

	public static final int EDUCATION = 6;

	public static final int MARRAGE = 9;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		final Resources resource = this.getResources();
		tendenties = new ArrayList<DetailsItem<?>>(10);
		tendenties.add(new DetailsItem<String> (0,  "所在城市", "",    1));
		tendenties.add(new DetailsItem<Boolean>(1,  "性别",    false, 1));
		tendenties.add(new DetailsItem<Integer[]>(2,  "身高",  new Integer[]{170, 170},   1));
		tendenties.add(new DetailsItem<Integer[]>(3,  "职业",  new Integer[]{0},     1));
		tendenties.add(new DetailsItem<Integer[]>(4,  "收入",  new Integer[]{3000, 5000},     1));
		tendenties.add(new DetailsItem<Integer[]>(5,  "民族",  new Integer[]{0},     1));
		tendenties.add(new DetailsItem<Integer[]>(6,  "学历",  new Integer[]{0},     1));
		tendenties.add(new DetailsItem<Integer[]>(7,  "购房",  new Integer[]{0},     1));
		tendenties.add(new DetailsItem<Integer[]>(8,  "购车",  new Integer[]{0},     1));
		tendenties.add(new DetailsItem<Integer[]>(9,  "婚姻",  new Integer[]{0},     1));
		
		final String[][] datas = new String[][]{
			null, null, null,
			resource.getStringArray(R.array.job),
			resource.getStringArray(R.array.income),
			resource.getStringArray(R.array.nation),
			resource.getStringArray(R.array.education),
			resource.getStringArray(R.array.housing),
			resource.getStringArray(R.array.car),
			resource.getStringArray(R.array.marital_status)
		};
		final LayoutInflater inflater = this.getLayoutInflater();
		final IDetegater<?>[] detegaters = new IDetegater<?>[]{
			//城市
			new IDetegater<String>() {
				
				public void popup(final String[] data, final IObserver<String> getValue) {
					TendentiousSearchActivity.this.startActivityForResult(new Intent(TendentiousSearchActivity.this, LocalCityActivity.class), REQUEST_CITY);
				}
			},
			
			//性别
			new IDetegater<String>() {

				@Override
				public void popup(String[] data, final IObserver<String> getValue) {
					final String[] sex = new String[]{"男", "女"};
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(TendentiousSearchActivity.this, android.R.layout.simple_list_item_single_choice, sex);
					ListView view = new ListView(TendentiousSearchActivity.this);
					view.setAdapter(adapter);
					view.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
					view.setItemChecked(0, true);
					
					final AlertDialog dialog = new AlertDialog.Builder(TendentiousSearchActivity.this)
					.setTitle(R.string.sex)
					.setView(view).create();
					dialog.show();
					view.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							@SuppressWarnings("unchecked")
							DetailsItem<Boolean> item = (DetailsItem<Boolean>) tendenties.get(SEX);
							item.setContent(sex[position].equals("男"));
							item.setOperator(new IncludeOperator<Boolean>());
							getValue.observe(position == 0 ? "男" : "女");
							dialog.dismiss();
						}
					});
					/*.setNegativeButton(R.string.ok, new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(which < 0)
								which = 0;
							DetailsItem<Boolean> item = (DetailsItem<Boolean>) tendenties.get(DetailsItem.SEX);
							item.setContent(sex[which].equals("男"));
							getValue.observe(which == 0 ? "男" : "女");
						}
					})*/
				}
			},
			
			//身高
			new IDetegater<Integer>() {

				@Override
				public void popup(String[] data, final IObserver<String> getValue) {
//					final NumberPicker height = new NumberPicker(TendentiousSearchActivity.this);
//					height.setMaxValue(300);
//					height.setMinValue(120);
//					height.setValue(170);
					ViewGroup root = (ViewGroup) inflater.inflate(R.layout.tow_number_picker, null);
					
					final NumberPicker begin = (NumberPicker) root.findViewById(R.id.begin);
					begin.setMaxValue(300);
					begin.setMinValue(120);
					begin.setValue(170);
					
					final NumberPicker end = (NumberPicker) root.findViewById(R.id.end);
					end.setMaxValue(300);
					end.setMinValue(120);
					end.setValue(170);
					
					TextView u1 = (TextView) root.findViewById(R.id.u1);
					u1.setText("cm");
					
					TextView u2 = (TextView) root.findViewById(R.id.u2);
					u2.setText("cm");
					
					new AlertDialog.Builder(TendentiousSearchActivity.this)
					.setTitle(R.string.height)
					.setView(root)
					.setNegativeButton(R.string.cancel, null)
					.setPositiveButton(R.string.ok, new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Integer[] d = new Integer[2];
							int b1 = begin.getValue(), b2 = end.getValue();
							if(b1 > b2)
								d = new Integer[]{b2, b1};
							else
								d = new Integer[]{b1, b2};
							DetailsItem<Integer[]> item = (DetailsItem<Integer[]>) tendenties.get(HEIGHT);
							item.setContent(d);
							item.setOperator(new IntervalOperator<Integer>());
							StringBuilder sb = new StringBuilder();
							sb.append(d[0]);
							sb.append("cm");
							sb.append("到");
							sb.append(d[1]);
							sb.append("cm");
							getValue.observe(sb.toString());
						}
					}).show();
				}
			},
			
			//职业
			new IDetegater<String>() {

				@Override
				public void popup(String[] data, final IObserver<String> getValue) {
					final List<Integer> result = new ArrayList<Integer>(10);
					final ListView list = new ListView(TendentiousSearchActivity.this);
					final ArrayAdapter<String> adapter = new ArrayAdapter<String>(TendentiousSearchActivity.this, android.R.layout.simple_list_item_multiple_choice);
					adapter.addAll(datas[JOB]);
					list.setAdapter(adapter);
					list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
					list.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							result.add(position);
						}
						
					});
					new AlertDialog.Builder(TendentiousSearchActivity.this)
					.setTitle(R.string.job)
					.setView(list)
					.setNegativeButton(R.string.cancel, null)
					.setPositiveButton(R.string.ok, new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							DetailsItem<Integer[]> item = (DetailsItem<Integer[]>) tendenties.get(JOB);
							Integer[] array = new Integer[result.size()];
							result.toArray(array);
							item.setContent(array);
							item.setOperator(new IncludeOperator<Integer>());
							StringBuilder sb = new StringBuilder();
							for(Integer index : result) {
								sb.append(datas[JOB][index]);
								sb.append(',');
							}
							getValue.observe(sb.toString().substring(0, sb.length() - 1));
						}
						
					}).show();
				}
			},
			
			//收入
			new IDetegater<String>() {

				@Override
				public void popup(String[] data, final IObserver<String> getValue) {
					
					ViewGroup root = (ViewGroup) inflater.inflate(R.layout.tow_number_picker, null);
					
					final NumberPicker begin = (NumberPicker) root.findViewById(R.id.begin);
					begin.setMaxValue(100000);
					begin.setMinValue(1000);
					begin.setValue(3000);
					
					final NumberPicker end = (NumberPicker) root.findViewById(R.id.end);
					end.setMaxValue(100000);
					end.setMinValue(1000);
					end.setValue(3000);
					begin.setOnValueChangedListener(new OnValueChangeListener() {

						@Override
						public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
							newVal = oldVal + 1000;
						}
						
					});
					
					TextView u1 = (TextView) root.findViewById(R.id.u1);
					u1.setText("元");
					
					TextView u2 = (TextView) root.findViewById(R.id.u2);
					u2.setText("元");
					
					new AlertDialog.Builder(TendentiousSearchActivity.this)
					.setTitle(R.string.income)
					.setView(root)
					.setNegativeButton(R.string.cancel, null)
					.setPositiveButton(R.string.ok, new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Integer[] d = new Integer[2];
							int b1 = begin.getValue(), b2 = end.getValue();
							d[0] = Math.min(b1, b2);
							d[1] = Math.max(b1, b2);
							DetailsItem<Integer[]> item = (DetailsItem<Integer[]>) tendenties.get(INCOMING);
							item.setContent(d);
							item.setOperator(new IncludeOperator<Integer>());
							StringBuilder sb = new StringBuilder();
							sb.append(d[0]);
							sb.append("元");
							sb.append("到");
							sb.append(d[1]);
							sb.append("元");
							getValue.observe(sb.toString());
						}
						
					}).show();
				}
			},
			
			//民族
			new IDetegater<String>() {

				@Override
				public void popup(String[] data, final IObserver<String> getValue) {
					final List<Integer> result = new ArrayList<Integer>(10);
					final ArrayAdapter<String> adapter = new ArrayAdapter<String>(TendentiousSearchActivity.this, android.R.layout.simple_list_item_multiple_choice, datas[NATION]); 
					final ListView list = new ListView(TendentiousSearchActivity.this);
					list.setAdapter(adapter);
					list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
					list.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							result.add(position);
						}
						
					});
					
					new AlertDialog.Builder(TendentiousSearchActivity.this)
					.setTitle(R.string.nation)
					.setView(list)
					.setNegativeButton(R.string.cancel, null)
					.setPositiveButton(R.string.ok, new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							DetailsItem<Integer[]> item = (DetailsItem<Integer[]>) tendenties.get(NATION);
							Integer[] array = new Integer[result.size()];
							result.toArray(array);
							item.setContent(array);
							item.setOperator(new IncludeOperator<Integer>());
							StringBuilder sb = new StringBuilder();
							for(Integer index : result) {
								sb.append(datas[NATION][index]);
								sb.append(',');
							}
							getValue.observe(sb.toString().substring(0, sb.length() - 1));
						}
						
					}).show();
				}
				
			},
			
			//学历
			new IDetegater<String>() {

				@Override
				public void popup(String[] data, final IObserver<String> getValue) {
					final List<Integer> result = new ArrayList<Integer>(10);
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(TendentiousSearchActivity.this, android.R.layout.simple_list_item_multiple_choice, datas[EDUCATION]); 
					final ListView list = new ListView(TendentiousSearchActivity.this);
					list.setAdapter(adapter);
					list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
					list.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							result.add(position);
						}
						
					});
					
					new AlertDialog.Builder(TendentiousSearchActivity.this)
					.setTitle(R.string.education)
					.setView(list)
					.setNegativeButton(R.string.cancel, null)
					.setPositiveButton(R.string.ok, new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Integer[] array = new Integer[result.size()];
							result.toArray(array);
							DetailsItem<Integer[]> item = (DetailsItem<Integer[]>) tendenties.get(EDUCATION);
							item.setContent(array);
							item.setOperator(new IncludeOperator<Integer>());
							StringBuilder sb = new StringBuilder();
							for(Integer index : result) {
								sb.append(datas[EDUCATION][index]);
								sb.append(',');
							}
							getValue.observe(sb.toString().substring(0, sb.length() - 1));
						}
						
					}).show();
				}
			},
			
			//购房
			new IDetegater<String>() {

				@Override
				public void popup(String[] data, final IObserver<String> getValue) {
					final List<Integer> result = new ArrayList<Integer>(10);
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(TendentiousSearchActivity.this, android.R.layout.simple_list_item_multiple_choice, datas[HOUSE]); 
					final ListView listView = new ListView(TendentiousSearchActivity.this);
					listView.setAdapter(adapter);
					listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
					listView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							result.add(position);
						}
						
					});
					
					new AlertDialog.Builder(TendentiousSearchActivity.this)
					.setTitle(R.string.housing)
					.setView(listView)
					.setNegativeButton(R.string.cancel, null)
					.setPositiveButton(R.string.ok, new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							@SuppressWarnings("unchecked")
							DetailsItem<Integer[]> item = (DetailsItem<Integer[]>) tendenties.get(HOUSE);
							Integer[] array = new Integer[result.size()];
							result.toArray(array);
							item.setContent(array);
							item.setOperator(new IncludeOperator<Integer>());
							StringBuilder sb = new StringBuilder();
							for(Integer index : result) {
								sb.append(datas[HOUSE][index]);
								sb.append(',');
							}
							getValue.observe(sb.toString().substring(0, sb.length() - 1));
						}
					}).show();
				}
				
			},
			
			//私家车
			new IDetegater<String>() {

				@Override
				public void popup(String[] data, final IObserver<String> getValue) {
					final List<Integer> result = new ArrayList<Integer>(5);
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(TendentiousSearchActivity.this, android.R.layout.simple_list_item_multiple_choice, datas[CAR]); 
					final ListView list = new ListView(TendentiousSearchActivity.this);
					list.setAdapter(adapter);
					list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
					list.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							result.add(position);
						}
						
					});
					
					new AlertDialog.Builder(TendentiousSearchActivity.this)
					.setTitle(R.string.car)
					.setView(list)
					.setNegativeButton(R.string.cancel, null)
					.setPositiveButton(R.string.ok, new android.content.DialogInterface.OnClickListener() {

						@SuppressWarnings("unchecked")
						@Override
						public void onClick(DialogInterface dialog, int which) {
							DetailsItem<Integer[]> item = (DetailsItem<Integer[]>) tendenties.get(CAR);
							Integer[] array = new Integer[result.size()];
							result.toArray(array);
							item.setContent(array);
							item.setOperator(new IncludeOperator<Integer>());
							StringBuilder sb = new StringBuilder();
							for(Integer index : result) {
								sb.append(datas[CAR][index]);
								sb.append(',');
							}
							getValue.observe(sb.toString().substring(0, sb.length() - 1));
						}
						
					}).show();
				}
				
			},
			
			//婚姻状况
			new IDetegater<String>() {

				@Override
				public void popup(String[] data, final IObserver<String> getValue) {
					final List<Integer> result = new ArrayList<Integer>(5);
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(TendentiousSearchActivity.this, android.R.layout.simple_list_item_multiple_choice, datas[MARRAGE]); 
					final ListView list = new ListView(TendentiousSearchActivity.this);
					list.setAdapter(adapter);
					list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
					list.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							result.add(position);
						}
						
					});
					
					new AlertDialog.Builder(TendentiousSearchActivity.this)
					.setTitle(R.string.marrage)
					.setView(list)
					.setNegativeButton(R.string.cancel, null)
					.setPositiveButton(R.string.ok, new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							DetailsItem<Integer[]> item = (DetailsItem<Integer[]>) tendenties.get(MARRAGE);
							Integer[] array = new Integer[result.size()];
							result.toArray(array);
							item.setContent(array);
							item.setOperator(new IncludeOperator<Integer>());
							StringBuilder sb = new StringBuilder();
							for(Integer index : result) {
								sb.append(datas[CAR][index]);
								sb.append(',');
							}
							getValue.observe(sb.toString().substring(0, sb.length() - 1));
						}
					}).show();
				}
				
			}
		};
		adapter = new BaseAdapter() {

			@Override
			public int getCount() {
				return tendenties.size();
			}

			@Override
			public Object getItem(int position) {
				return tendenties.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				final DetailsItem<?> item = tendenties.get(position);
				if(convertView == null) {
					convertView = inflater.inflate(R.layout.tendentious_item, null);
					final TextView validate = (TextView) convertView.findViewById(R.id.validate);
					//初始化颜色
					if(item.getCondition() > 0) {
						validate.setBackgroundColor(resource.getColor(R.color.deepskyblue));
					} else {
						validate.setBackgroundColor(resource.getColor(R.color.red));
					}
					//乒乓按钮
					validate.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if(item.getCondition() > 0) {
								item.setCondition(0);
								validate.setBackgroundColor(resource.getColor(R.color.red));
								validate.setText(R.string.unvalidate);
							} else {
								item.setCondition(1);
								validate.setBackgroundColor(resource.getColor(R.color.deepskyblue));
								validate.setText(R.string.validate);
							}
						}
						
					});
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
				
				TextView title = (TextView) convertView.findViewById(R.id.title);
				title.setText(item.getTitle());
				TextView status = (TextView) convertView.findViewById(R.id.status);
				if(position == HEIGHT) {
					Integer[] items = (Integer[]) item.getContent();
					StringBuilder sb = new StringBuilder();
					sb.append(items[0]);
					sb.append("cm");
					sb.append("到");
					sb.append(items[1]);
					sb.append("cm");
					status.setText(sb.toString());
				} else if(position == INCOMING) {
					Integer[] items = (Integer[]) item.getContent();
					StringBuilder sb = new StringBuilder();
					sb.append(items[0]);
					sb.append("元");
					sb.append("到");
					sb.append(items[1]);
					sb.append("元");
					status.setText(sb.toString());
				} else if(position >= JOB) {
					Integer[] items = (Integer[]) item.getContent();
					StringBuilder sb = new StringBuilder();
					for(Integer index : items) {
						sb.append(datas[position][index]);
						sb.append(',');
					}
					status.setText(sb.toString().subSequence(0, sb.length() - 1));
				} else if(position == SEX) {
					status.setText(((Boolean) item.getContent()) ? "男" : "女");
				} else {
					status.setText(item.getContent().toString());
				}
				return convertView;
			}
		};
		
		ListView listView = (ListView) findViewById(R.id.options);
		listView.setAdapter(adapter);
		
		Button sbtn = (Button) findViewById(R.id.sbtn);
		sbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Shell shell = (Shell) TendentiousSearchActivity.this.getApplication();
				shell.setTendenties(tendenties);
				Gson gson = new Gson();
				String dat = gson.toJson(tendenties);
				SecurityReaderAndWirter wirter = new SecurityReaderAndWirter();
				wirter.save(shell.root(), "tendenty.ini", dat);
			}
			
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_CITY) {
			String city = data.getStringExtra("city");
			if(city != null) {
				DetailsItem<String[]> item = (DetailsItem<String[]>) tendenties.get(CITY);
				item.setContent(new String[]{city});
				item.setOperator(new IncludeOperator<String>());
				adapter.notifyDataSetChanged();
			}
		}
	}
}
