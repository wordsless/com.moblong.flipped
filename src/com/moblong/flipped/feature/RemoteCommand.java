package com.moblong.flipped.feature;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import com.moblong.flipped.util.SecurityReaderAndWirter;

import android.util.Log;

public class RemoteCommand {
	
	protected String cookie;
	
	protected String domain;
	
	protected SecurityReaderAndWirter reader;
	
	protected SynchrousCommand sync;
	
	protected Map<String, String> headers;
	
	public RemoteCommand(final String domain, final String cookie) {
		this.domain = domain;
		this.cookie = cookie;
		reader  = new SecurityReaderAndWirter();
		sync    = new SynchrousCommand();
		headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		headers.put("Connection", "Close");
		headers.put("User-Agent", "com.moblong.flipped");
		headers.put("Cookie", String.format("JSESSIONID=%s", new Object[]{cookie}));
		
	}
	
	public RemoteCommand(final String domain) {
		this(domain, UUID.randomUUID().toString().replace("-", ""));
	}
	
	public final String getCookie() {
		return cookie;
	}

	public final void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public final String getDomain() {
		return domain;
	}

	public final void setDomain(String domain) {
		this.domain = domain;
	}
	
	public final void modify(String field, String value) {
		headers.put("Content-Type", value);
	}
	
	public void exec(final String path, final byte[] data, final IMultipartyIntegrator integrator, final IObserver<byte[]> observer) {
		sync.exec(new IExecutor() {

			@Override
			public void execute() {
				HttpURLConnection con = null;
				InputStream  input    = null;
				OutputStream output   = null;
				try {
					URL url = new URL("http://"+domain+":8080/amuse/"+path);
					con = (HttpURLConnection) url.openConnection();
					//设置调用该方法会抛出异常，与Connection: Keep-Alive属性冲突
					con.setConnectTimeout(60000);
					con.setUseCaches(false);
					con.setInstanceFollowRedirects(true);
					con.setReadTimeout(600);
					con.setChunkedStreamingMode(10240);
					Set<Entry<String, String>> entites = headers.entrySet();
					for(Map.Entry<String, String> entry : entites) {
						con.addRequestProperty(entry.getKey(), entry.getValue());
					}
					if(data != null)
						con.addRequestProperty("Length", Integer.toString(data.length));
					con.addRequestProperty("Host", url.getHost());
					con.setDoOutput(true);
					con.setDoInput(true);
					con.setRequestMethod("POST");
					
					int index = 0, size = 1024;
					if(data != null) {
						output = con.getOutputStream();
						while(index < data.length) {
							int remain = data.length - index, c = 0;
							output.write(data, index, c = (remain < 0 ? data.length : remain > size ? size : remain));
							output.flush();
							index += c;
						}
					} else if(integrator != null) {
						output = con.getOutputStream();
						integrator.integrate(output);
					}
					
					int code = con.getResponseCode();
					if(code == 200) {
						input = con.getInputStream();
						if(input != null && observer != null) {
							byte[] data = reader.readByteArray(input);
							input.close();
							input = null;
							observer.observe(data);
							data = null;
						}
					} else {
						Log.w("Response Code", Integer.toString(code));
						observer.observe(null);
					}
					
					if(output != null) {
						output.close();
						output = null;	
					}
					
					if(input != null) {
						input.close();
						input = null;	
					}
					
					con.disconnect();
					con = null;
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if(input != null) {
						try {
							input.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						input = null;
					}
					
					if(output != null) {
						try {
							output.close();
						} catch (IOException e) {
							e.printStackTrace();
						} 				
						output = null;
					}
					
					if(con != null) {
						con.disconnect();
						con = null;
					}
				}
			}
		});
	}
}
