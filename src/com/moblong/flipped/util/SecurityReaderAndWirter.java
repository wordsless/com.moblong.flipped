package com.moblong.flipped.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.content.res.AssetManager;

public final class SecurityReaderAndWirter {

	public void save(final File dir, final String fileName, final InputStream content) {
		int c = 0;
		byte[] buf = new byte[1024];
		File file = new File(dir, fileName);
		FileOutputStream output = null;
		try {
			if(file.exists()) {
				file.delete();
			}
			file.createNewFile();
			output = new FileOutputStream(file);
			while((c = content.read(buf)) > 0) {
				output.write(buf, 0, c);
			}
			output.close();
			output = null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				output = null;
			}
		}
	}
	
	public void save(final File dir, final String fileName, final String content) {
		InputStream input = null;
		try {
			input = new ByteArrayInputStream(content.getBytes("UTF-8"));
			save(dir, fileName, input);
			input.close();
			input = null;
		} catch (UnsupportedEncodingException e) {
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
		}
	}

	public byte[] readByteArray(final InputStream input) {
		int c = 0;
		byte[] buf = new byte[1024];
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			while((c = input.read(buf)) > 0) {
				bos.write(buf, 0, c);
			}
			buf = null;
			buf = bos.toByteArray();
			bos.close();
			bos = null;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				bos = null;
			}
		}
		return buf;
	}
	
	public String read(final InputStream input) throws UnsupportedEncodingException, IOException {
		int c = 0;
		byte[] buf = new byte[1024];
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while((c = input.read(buf)) > 0) {
			bos.write(buf, 0, c);
		}
		String result = new String(bos.toByteArray(), "UTF-8");
		bos.close();
		bos = null;
		buf = null;
		return result;
	}
	
	public String read(final File dir, final String fileName) {
		String content = null;
		File file = new File(dir, fileName);
		if(!file.exists())
			return null;
		InputStream input = null;
		try {
			input = new FileInputStream(file);
			content = read(input);
			input.close();
			input = null;
		} catch (FileNotFoundException e) {
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
		}
		return content;
	}

	public String read(AssetManager assets, String fileName) {
		try {
			return read(assets.open(fileName));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
