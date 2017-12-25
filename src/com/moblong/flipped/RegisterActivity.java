package com.moblong.flipped;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moblong.flipped.feature.Feature;
import com.moblong.flipped.feature.IObserver;
import com.moblong.flipped.model.Account;
import com.moblong.flipped.util.SecurityReaderAndWirter;
import com.moblong.flipped.wedget.CircleImageView;
import com.moblong.flipped.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public final class RegisterActivity extends Activity {

	public final static int SETTING_ACCOUNT				= 1000;
	
	public final static int SETTING_PASSWORD			= 2000;
	
	public final static int SETTING_VERIFICATION_CODE 	= 3000;

	public final static int SETTING_ALIASE				= 4000;
	
	public final static int SETTING_SIGNATURE			= 5000;
	
	public final static int CHOOSE_PICTURE				= 6000;
	
	public final static int REDUCE_PORTRAIT				= 7000;
	
	public final static int FORWARD_TO_SIGNIN			= 8000;
	
	private final Map<String, String> ds = new HashMap<String, String>(5);
	
	private final boolean[] isReady = new boolean[]{false, false, false, false, false, false};
	
	private String password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		final Shell  shell = (Shell) getApplication();
		final Account account = shell.getSelf();
		
		ds.put("手机号码", account.getTelephone());
		ds.put("密码", "");
		ds.put("验证码", "");
		ds.put("昵称", account.getAlias());
		ds.put("签名", account.getSignature());
		
		//回退
		ImageView back = (ImageView) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RegisterActivity.this.setResult(RESULT_CANCELED);
				RegisterActivity.this.finish();
			}
			
		});
		
		//账户名、密码和验证码
		final ListView list = (ListView) findViewById(R.id.account);
		final LayoutInflater inflater = getLayoutInflater();
		list.setAdapter(new BaseAdapter() {

			@Override
			public int getCount() {
				return 3;
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public long getItemId(int position) {
				return 0;
			}

			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				if(convertView == null) {
					convertView = inflater.inflate(R.layout.common_item, null);
					convertView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if(position == 0) {
								Intent intent = new Intent(RegisterActivity.this, SettingAccountActivity.class);
								RegisterActivity.this.startActivityForResult(intent, SETTING_ACCOUNT);
							} else if(position == 1) {
								Intent intent = new Intent(RegisterActivity.this, SettingPasswordActivity.class);
								intent.putExtra("password", ds.get("密码"));
								RegisterActivity.this.startActivityForResult(intent, SETTING_PASSWORD);
							} else if(position == 2) {
								Intent intent = new Intent(RegisterActivity.this, VerificationActivity.class);
								RegisterActivity.this.startActivityForResult(intent, SETTING_VERIFICATION_CODE);
							}
						}
						
					});
				}
				TextView title = (TextView)convertView.findViewById(R.id.title);
				TextView status = (TextView)convertView.findViewById(R.id.status);
				if(position == 0) {
					title.setText("手机号码");
					status.setText(ds.get("手机号码"));
				} else if(position == 1) {
					title.setText("密码");
					status.setText(ds.get("密码"));
				} else if(position == 2) {
					title.setText("验证码");
					status.setText(ds.get("验证码"));
				}
				return convertView;
			}
			
		});
		
		//昵称和签名
		final ListView simpleUser = (ListView) findViewById(R.id.simple_user_info);
		simpleUser.setAdapter(new BaseAdapter() {

			@Override
			public int getCount() {
				return 2;
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public long getItemId(int position) {
				return 0;
			}

			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					convertView = inflater.inflate(R.layout.common_item, null);
					convertView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (position == 0) {
								Intent intent = new Intent(RegisterActivity.this, SettingAliasActivity.class);
								RegisterActivity.this.startActivityForResult(intent, SETTING_ALIASE);
							} else if (position == 1) {
								Intent intent = new Intent(RegisterActivity.this, SettingSignatureActivity.class);
								RegisterActivity.this.startActivityForResult(intent, SETTING_SIGNATURE);
							}
						}
					});
				}
				TextView title  = (TextView) convertView.findViewById(R.id.title);
				TextView status = (TextView)convertView.findViewById(R.id.status);
				if(position == 0) {
					title.setText("昵称");
					status.setText(ds.get("昵称"));
				} else if(position == 1) {
					title.setText("签名");
					status.setText(ds.get("签名"));
				}
				
				return convertView;
			}
		});
		
		//更多信息
		final View next = findViewById(R.id.more);
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final Gson gson = new GsonBuilder()
								  .setDateFormat("yyyy-MM-dd HH:mm:ss")
								  .create();
				
				final Account account = shell.getSelf();
				if(account.getAlias() == null || account.getAlias().length() <= 0) {
					Toast toast=Toast.makeText(getApplicationContext(), "昵称尚未设置。", Toast.LENGTH_SHORT);
					toast.show();
					return;
				} else if(account.getTelephone() == null || account.getTelephone().length() <= 0) {
					Toast toast=Toast.makeText(getApplicationContext(), "手机号尚未设置。", Toast.LENGTH_SHORT);
					toast.show();
					return;
				} else if(!isReady[2]) {
					Toast toast=Toast.makeText(getApplicationContext(), "密码尚未设置", Toast.LENGTH_SHORT);
					toast.show();
					return;
				}
				
				final Feature feature = shell.getFeature();
				feature.registerNewAccount(account, password, new IObserver<String>() {

					@Override
					public boolean observe(String result) {
						if(!result.equals("OK"))
							return false;
						SecurityReaderAndWirter sio = new SecurityReaderAndWirter();
						sio.save(shell.root(), "account.ini", gson.toJson(shell.getSelf()));
						Intent intent = new Intent(RegisterActivity.this, ExtendUserActivity.class);
						RegisterActivity.this.startActivity(intent);
						RegisterActivity.this.finish();
						return true;
					}
				});
			}
		});
		
		//登录
		final View signIn = findViewById(R.id.signIn);
		signIn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RegisterActivity.this, SignInActivity.class);
				RegisterActivity.this.startActivityForResult(intent, FORWARD_TO_SIGNIN);
			}
			
		});
		
		//提交
		final View submit = findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				final Gson gson = new GsonBuilder()
								  .setDateFormat("yyyy-MM-dd HH:mm:ss")  
								  .create();
				
				final Account account = shell.getSelf();
				if(account.getAlias() == null || account.getAlias().length() <= 0) {
					Toast toast=Toast.makeText(getApplicationContext(), "昵称尚未设置。", Toast.LENGTH_SHORT);
					toast.show();
					return;
				} else if((account.isAnonymous()) && (account.getTelephone() == null || account.getTelephone().length() <= 0)) {
					Toast toast=Toast.makeText(getApplicationContext(), "手机号尚未设置。", Toast.LENGTH_SHORT);
					toast.show();
					return;
				} else if((account.isAnonymous()) & (!isReady[2])) {
					Toast toast=Toast.makeText(getApplicationContext(), "密码尚未设置", Toast.LENGTH_SHORT);
					toast.show();
					return;
				}
				final Handler handler = new Handler() {

					@Override
					public void handleMessage(Message msg) {
						if(msg.what < 0) {
							Toast toast=Toast.makeText(getApplicationContext(), (String) msg.obj, Toast.LENGTH_SHORT);
							toast.show();
						}
					}
					
				};
				final Feature feature = shell.getFeature();
				feature.registerNewAccount(account, password, new IObserver<String>(){

					@Override
					public boolean observe(String result) {
						if(!result.equals("OK")) {
							Message msg = new Message();
							msg.what = -1;
							msg.obj = "注册失败，请检查相关内容。";
							return handler.sendMessage(msg);
						}
						account.setAnonymous(false);
						SecurityReaderAndWirter sio = new SecurityReaderAndWirter();
						sio.save(shell.root(), "account.ini", gson.toJson(account));
						RegisterActivity.this.setResult(RESULT_OK);
						RegisterActivity.this.finish();
						return true;
					}
					
				});
			}
			
		});
		
		//头像
		CircleImageView portrait = (CircleImageView) findViewById(R.id.portrait);
		portrait.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				RegisterActivity.this.startActivityForResult(intent, CHOOSE_PICTURE);
			}
		});
		if(account.getType().equals(Account.FLIPPED) && account.getAvatar() != null) {
			String uri = String.format("http://%s:%s/%s_small", new Object[]{shell.getDomain(), "8079", account.getAvatar()});
			shell.getImageLoader().displayImage(uri, portrait);
		} else {
			portrait.setImageResource(R.drawable.avatar);
		}
		
	}

	@SuppressWarnings("resource")
	private void copy(File source, File dest) {
		FileChannel inputChannel = null;
		FileChannel outputChannel = null;
		try {
			inputChannel  = new FileInputStream(source).getChannel();
			outputChannel = new FileOutputStream(dest).getChannel();
			outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
			inputChannel.close();
			inputChannel = null;
			outputChannel.close();
			outputChannel = null;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(inputChannel != null) {
				try {
					inputChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				inputChannel = null;
			}
			if(outputChannel != null) {
				try {
					outputChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				outputChannel = null;
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		final Shell     shell = (Shell) getApplication();
		final Feature feature = shell.getFeature();
		final Account account = shell.getSelf();
		
		if(requestCode == SETTING_ACCOUNT) {
			if(resultCode == RESULT_OK) {
				final ListView list = (ListView) findViewById(R.id.account);
				final BaseAdapter adapter = (BaseAdapter) list.getAdapter();
				ds.put("手机号码", data.getStringExtra("account"));
				adapter.notifyDataSetChanged();
				isReady[1] = true;
			}
		} else if(requestCode == SETTING_PASSWORD) {
			if(resultCode == RESULT_OK) {
				final ListView list = (ListView) findViewById(R.id.account);
				final BaseAdapter adapter = (BaseAdapter) list.getAdapter();
				password = data.getStringExtra("password");
				ds.put("密码", "完成");
				adapter.notifyDataSetChanged();
				isReady[2] = true;
			}
		} else if(requestCode == SETTING_VERIFICATION_CODE) {
			if(resultCode == RESULT_OK) {
				final ListView list = (ListView) findViewById(R.id.account);
				final BaseAdapter adapter = (BaseAdapter) list.getAdapter();
				ds.put("验证码", "通过验证");
				adapter.notifyDataSetChanged();
				isReady[3] = true;
			}
		} else if(requestCode == SETTING_ALIASE) {
			if(resultCode == RESULT_OK) {
				final ListView list = (ListView) findViewById(R.id.simple_user_info);
				final BaseAdapter adapter = (BaseAdapter) list.getAdapter();
				ds.put("昵称", data.getStringExtra("alias"));
				adapter.notifyDataSetChanged();
				isReady[4] = true;
			}
		} else if(requestCode == SETTING_SIGNATURE) {
			if(resultCode == RESULT_OK) {
				final ListView list = (ListView) findViewById(R.id.simple_user_info);
				final BaseAdapter adapter = (BaseAdapter) list.getAdapter();
				ds.put("签名", data.getStringExtra("signature"));
				adapter.notifyDataSetChanged();
				isReady[5] = true;
			}
		} else if(requestCode == CHOOSE_PICTURE) {
			if(resultCode == RESULT_OK) {
				Uri selectedImage = data.getData();
				File picture = null;
				/*
				if(android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.KITKAT) {
					String[] filePathColumn = { MediaStore.Images.Media.DATA };
		            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
		            cursor.moveToFirst();
		            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		            final String path = cursor.getString(columnIndex);
		            cursor.close();
		            picture = new File(path);
				} else {
					picture = new File(selectedImage.getPath());
				}
	            */
				
				picture = new File(selectedImage.getPath());
				if(!picture.exists()) {
					String[] filePathColumn = { MediaStore.Images.Media.DATA };
		            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
		            cursor.moveToFirst();
		            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		            final String path = cursor.getString(columnIndex);
		            cursor.close();
		            picture = new File(path);
				}
				
				shell.log(picture.getAbsolutePath());
				
	            final File     source = picture;
	            final Handler handler = new Handler();
	            InputStream       pin = null;
				try {
					pin = new FileInputStream(picture);
					feature.upload(pin, new IObserver<String>() {

						@Override
						public boolean observe(final String pid) {
							final File cache = new File(shell.cache(), pid);
							if(!cache.exists())
								try {
									cache.createNewFile();
								} catch (IOException e) {
									e.printStackTrace();
								}
							copy(source, cache);
							handler.post(new Runnable() {

								@Override
								public void run() {
									ImageView portrait = (ImageView) findViewById(R.id.portrait);
						            portrait.setImageBitmap(BitmapFactory.decodeFile(cache.getAbsolutePath()));
						            isReady[0] = true;
						            account.setAvatar(pid);
								}
								
							});
							account.setAvatar(pid);
							SecurityReaderAndWirter wirter = new SecurityReaderAndWirter();
							Gson gson = new GsonBuilder()
									    .setDateFormat("yyyy-MM-dd HH:mm:ss")
									    .create();
							wirter.save(shell.root(), "account.ini", gson.toJson(account));
							return true;
						}
		            	
		            });
		            pin.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if(pin != null) {
						try {
							pin.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						pin = null;
					}
				}
			}
		} else if(requestCode == FORWARD_TO_SIGNIN) {
			//登录成功
			if(resultCode == RESULT_OK) {
				RegisterActivity.this.setResult(RESULT_OK);
				RegisterActivity.this.finish();
			}
		}
	}

}
