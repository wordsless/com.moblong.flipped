package com.moblong.flipped.feature;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.moblong.flipped.model.Account;
import com.moblong.flipped.model.Device;
import com.moblong.flipped.model.Indicator;
import com.moblong.flipped.model.Multipart;
import com.moblong.flipped.model.DetailsItem;
import com.moblong.flipped.util.SecurityReaderAndWirter;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.telephony.TelephonyManager;

public final class Feature {
	
	private final RemoteCommand command;
	
	private final Gson gson = new GsonBuilder()
							  .setDateFormat("yyyy-MM-dd HH:mm:ss")
							  .create();
	
	public Feature(String domain, String cookie) {
		command  = new RemoteCommand(domain, cookie);
	}
	
	private void anonymousRegister(final Device device, final Account account, final IObserver<Account> observer) throws UnsupportedEncodingException {
		command.exec("RegisterAnonymous", String.format("account=%s&device=%s", new Object[]{gson.toJson(account), gson.toJson(device)}).getBytes("UTF-8"), null, new IObserver<byte[]>() {
			
			@Override
			public boolean observe(byte[] data) {
				if(data != null) {
					try {
						BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"));
						String line = reader.readLine();
						observer.observe(gson.fromJson(line, Account.class));
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					return true;
				} else
					return false;
			}
		});
	}
	
	/**
	 * 匿名注册
	 * @param context
	 * @param observer
	 */
	public void anonymousRegister(final Context context, final IObserver<Account> observer) {
		
		TelephonyManager 		tm = (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
		LocationManager locManager = (LocationManager)  context.getSystemService(Context.LOCATION_SERVICE);
		Location 		  location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		Device device = new Device();
		device.setDeviceID(tm.getDeviceId());
		device.setPhone(tm.getLine1Number());
		device.setModel(Build.MODEL);
		device.setManufacture(Build.MANUFACTURER);
		device.setPlatform("ANDROID");
		device.setRelease(Build.VERSION.RELEASE);
		
		Account account = new Account();
		account.setTelephone(tm.getLine1Number());
		
		if(location != null) {
			account.setLatitude(location.getLatitude());
			account.setLongitude(location.getLongitude());	
		}
		
		try {
			anonymousRegister(device, account, observer);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 注册账户，用户注册流程。
	 * @param context
	 * @param account
	 * @param observer
	 */
	public void registerNewAccount(final Account account, final String password, final IObserver<String> observer) {
		try {
			command.exec("RegisterNewAccount", String.format("account=%s&password=%s", new Object[]{URLEncoder.encode(gson.toJson(account), "UTF-8"), URLEncoder.encode(password, "UTF-8")}).getBytes("UTF-8"), null, new IObserver<byte[]>() {
				
				@Override
				public boolean observe(final byte[] data) {
					try {
						BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"));
						observer.observe(reader.readLine());
					} catch (IOException e) {
						e.printStackTrace();
					}
					return true;
				}
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 注册账户，用户注册流程。
	 * @param context
	 * @param account
	 * @param observer
	 */
	public void registerThridPartyAccount(final Account account, final IObserver<String> observer) {
		try {
			command.exec("RegisterThridPartyAccount", String.format("account=%s", new Object[]{URLEncoder.encode(gson.toJson(account), "UTF-8")}).getBytes("UTF-8"), null, new IObserver<byte[]>() {
				
				@Override
				public boolean observe(final byte[] data) {
					observer.observe(null);
					return true;
				}
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 申请候选人列表
	 * @param context
	 * @param content
	 * @param observer
	 */
	public void requestCandidates(final String aid, final int page, final IObserver<List<Account>> observer) {
		try {
			command.exec("RequestCandidates", String.format("aid=%s&page=%s", new Object[]{aid, Integer.toString(page)}).getBytes("UTF-8"), null, new IObserver<byte[]>() {
				
				@Override
				public boolean observe(byte[] data) {
					if(data == null)
						return false;
					List<Account> accounts;
					try {
						accounts = gson.fromJson(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"), new TypeToken<List<Account>>(){}.getType());
						observer.observe(accounts);
					} catch (JsonIOException e) {
						e.printStackTrace();
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					return true;
				}
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 上传图片
	 * @param id
	 * @param input
	 * @param observer
	 */
	public void upload(final InputStream input, final IObserver<String> observer) {
		SecurityReaderAndWirter reader = new SecurityReaderAndWirter();
		byte[] data = reader.readByteArray(input);
		command.exec("SavePicture", data, null, new IObserver<byte[]>() {

			@Override
			public boolean observe(final byte[] data) {
				if(observer != null && data != null) {
					try {
						BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"));
						observer.observe(reader.readLine());
					} catch (IOException e) {
						e.printStackTrace();
					}
					return true;
				}
				return false;
			}

			
		});
	}

	public void submitUserDetails(final String aid, final List<DetailsItem<?>> user, final IObserver<String> observer) {
		try {
			command.exec("SubmitUserDetails", String.format("aid=%s&data=%s", new Object[]{aid, gson.toJson(user)}).getBytes("UTF-8"), null, new IObserver<byte[]>() {
				
				@Override
				public boolean observe(final byte[] data) {
					if(data == null)
						return false;
					try {
						BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"));
						observer.observe(reader.readLine());
					} catch (IOException e) {
						e.printStackTrace();
					}
					return true;
				}
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void submitUserMaterials(final Context context, final String aid, final String type, final List<Map<String, String>> data, final IObserver<String> observer) {
		try {
			final String boundary = "----------------------------" + UUID.randomUUID().toString().replace("-", "");
			command.modify("Content-Type", String.format("multipart/form-data; boundary=%s", boundary));
			command.exec("SaveMaterials", null, new IMultipartyIntegrator() {

				@Override
				public void integrate(final OutputStream output) {
					Multipart multipart;
					try {
						multipart = new Multipart(output, boundary);
						for(Map<String, String> item : data) {
							multipart.addFilePart(String.format("type=%s&desc=%s", type, item.get("desc")), new File(item.get("res")), "image/jpeg");
						}
						multipart.commit();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}, new IObserver<byte[]>() {

				@Override
				public boolean observe(byte[] result) {
					return false;
				}
				
			});
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			
		}
	}

	public void requestIndicator(final Account account, final IObserver<List<DetailsItem<?>>> observer) {
		try {
			command.exec("RequestIndicator", String.format("aid=%s", new Object[]{account.getId()}).getBytes("UTF-8"), null, new IObserver<byte[]>() {
				
				@SuppressWarnings("unchecked")
				@Override
				public boolean observe(byte[] data) {
					if(data == null)
						return false;
					if(observer != null)
						try {
							BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"));
							observer.observe((List<DetailsItem<?>>) gson.fromJson(reader.readLine(), new TypeToken<List<Indicator<?>>>(){}.getType()));
						} catch (IOException e) {
							e.printStackTrace();
						}
					return true;
				}
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/*
	public void requestInvitation(final String source, final String target, final String msg, final IObserver<Boolean> observer) {
		try {
			Message message = new Message();
			message.setId(UUID.randomUUID().toString().replaceAll("-", ""));
			message.setSource(source);
			message.setTarget(target);
			message.setSubscribe(false);
			message.setAction("com.moblong.flipped.action.INVITE");
			message.setContent(msg);
			command.exec("RequestInvitation", String.format("msg=%s", new Object[]{gson.toJson(message)}).getBytes("UTF-8"), null, new IObserver<InputStream>() {
				
				@Override
				public boolean observe(final InputStream input) {
					if(observer != null)
						try {
							BufferedReader reader = new BufferedReader(new InputStreamReader(input));
							observer.observe(Boolean.parseBoolean(reader.readLine()));
						} catch (IOException e) {
							e.printStackTrace();
						}
					return true;
				}
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	*/
	
	public void signIn(final String aid, final String password, final IObserver<Account> observer) {
		try {
			command.exec("SignIn", String.format("phone=%s&pwd=%s", aid, password).getBytes("UTF-8"), null, new IObserver<byte[]>() {
				
				@Override
				public boolean observe(byte[] data) {
					if(data == null)
						return false;
					if(observer != null) {
						try {
							BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"));
							String line = reader.readLine();
							observer.observe(gson.fromJson(line, Account.class));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					return true;
				}
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public void requestAccount(final String aid, final IObserver<Account> observer) {
		try {
			command.exec("RequestAccount", String.format("aid=%s", aid).getBytes("UTF-8"), null, new IObserver<byte[]>() {
				
				@Override
				public boolean observe(byte[] data) {
					if(data == null)
						return false;
					if(observer != null) {
						try {
							BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"));
							String line = reader.readLine();
							observer.observe(gson.fromJson(line, Account.class));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					return true;
				}
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public void requestDeleteAnonymous(final String aid, final IObserver<Boolean> observer) {

		try {
			command.exec("DeleteAnonymous", String.format("aid=%s", aid).getBytes("UTF-8"), null, new IObserver<byte[]>() {
				
				@Override
				public boolean observe(byte[] data) {
					if(data == null)
						return false;
					if(observer != null) {
						try {
							BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"));
							String line = reader.readLine();
							observer.observe(Boolean.parseBoolean(line));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					return true;
				}
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	
	}

	public void updateAccount(Account account, final IObserver<Account> observer) {
		try {
			command.exec("UpdateAccount", String.format("account=%s", gson.toJson(account)).getBytes("UTF-8"), null, new IObserver<byte[]>() {
				
				@Override
				public boolean observe(byte[] data) {
					if(data == null)
						return false;
					if(observer != null) {
						try {
							BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"));
							String line = reader.readLine();
							observer.observe(gson.fromJson(line, Account.class));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					return true;
				}
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
