package com.moblong.flipped;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.moblong.flipped.R;
import com.moblong.flipped.feature.Feature;
import com.moblong.flipped.feature.IDestructor;
import com.moblong.flipped.feature.IObserver;
import com.moblong.flipped.model.Account;
import com.moblong.flipped.model.Configuration;
import com.moblong.flipped.model.LoverTypePreference;
import com.moblong.flipped.model.DetailsItem;
import com.moblong.flipped.util.DatabaseController;
import com.moblong.flipped.util.InterestingController;
import com.moblong.flipped.util.MaterialController;
import com.moblong.flipped.util.SecurityReaderAndWirter;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;

public final class Shell extends Application {

	public final static String PREPARED = "PREPARED";
	
	public final static String UNPREPARED = "UNPREPARED";
	
	//保存回话
	private Account self;
	
	private List<DetailsItem<?>> user;
	
	private File root, cache, databases;
	
	private ImageLoader imageLoader;
	
	private Feature feature;
	
	private Configuration config;
	
	private List<LoverTypePreference> loverTypePreferences;
	
	private DatabaseController<Account> interestingController;
	
	private DatabaseController<List<Map<String, String>>> materialController;
	
	private IDestructor destructor;
	
	private RabbitMQBinder rabbitmq;
	
	private List<DetailsItem<?>> tendenties;
	
	private String domain;
	
	private boolean initialized = false;
	
	public final boolean hasInitialized() {
		return initialized;
	}

	public final void initialized(boolean complete) {
		this.initialized = complete;
	}

	public final RabbitMQBinder getRabbitmq() {
		return rabbitmq;
	}

	public void log(final String message) {/*
		File log = new File(root, "log.txt");
		if(!log.exists())
			try {
				log.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		PrintWriter output = null;
		try {
			output = new PrintWriter(log);
			output.println(message);
			output.close();
			output = null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if(output != null) {
				output.close();
				output = null;
			}
		}
	*/}
	
	public Shell() {
		self = new Account();
		interestingController = new InterestingController();
		materialController    = new MaterialController();
		loverTypePreferences  = new ArrayList<LoverTypePreference>(5);
	}
	
	public void bind(RabbitMQBinder rabbitmq) {
		this.rabbitmq = rabbitmq;
	}
	
	/**
	 * 登录成功后相关处理
	 */
	public void signedIn(final Account account) {
		Gson gson = new GsonBuilder()
				.setDateFormat("yyyy-MM-dd HH:mm:ss")
				.create();
		SecurityReaderAndWirter readerAndWirter = new SecurityReaderAndWirter();
		config = new Configuration();
		config.setCookie(account.getId());
		readerAndWirter.save(root, "config.ini",  gson.toJson(config));
		readerAndWirter.save(root, "account.ini", gson.toJson(account));
		Shell.this.self = account;
	}
	
	public void init(final Context context, final IObserver<Boolean> terminal) {
		final Gson gson = new GsonBuilder()  
	    		   		  .setDateFormat("yyyy-MM-dd HH:mm:ss")
	    		   		  .create();
		
		interestingController.open(this);
		materialController.open(this);
		
		databases = new File("/data/data/com.moblong.flipped/databases");
		if(!databases.exists())
			databases.mkdir();
			
		root = new File(Environment.getExternalStorageDirectory(), "com.moblong.flipped");
		if(!root.exists())
			root.mkdir();
		
		cache = new File(root, "cache");
		if(!cache.exists())
			cache.mkdir();
		
		imageLoader = createImageLoader();
		
		final File dat = new File(root, "config.ini");
		if(dat.exists()) {
			SecurityReaderAndWirter readerAndWirter = new SecurityReaderAndWirter();
			config  = gson.fromJson(readerAndWirter.read(root, "config.ini"), 	Configuration.class);
			self	= gson.fromJson(readerAndWirter.read(root, "account.ini"), 	Account.class);
			user 	= gson.fromJson(readerAndWirter.read(root, "user.ini"), 	new TypeToken<List<DetailsItem<?>>>(){}.getType());
			tendenties = gson.fromJson(readerAndWirter.read(root, "tendenty.ini"), 	new TypeToken<List<DetailsItem<?>>>(){}.getType());
			
			AssetManager asset = getAssets();
			InputStream  input = null;
			try {
				input = asset.open("host.ini");
				domain = readerAndWirter.read(input);
				input.close();
				input = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
			feature = new Feature(domain, getCookie());
			
			if(terminal != null)
				terminal.observe(true);
		} else {
			AssetManager asset = getAssets();
			InputStream  input = null;
			SecurityReaderAndWirter readerAndWirter = new SecurityReaderAndWirter();
			try {
				input = asset.open("host.ini");
				domain = readerAndWirter.read(input);
				input.close();
				input = null;
				
				feature = new Feature(domain, UUID.randomUUID().toString().replace("-", ""));
				feature.anonymousRegister(this, new IObserver<Account>() {

					@Override
					public boolean observe(final Account account) {
						if(account == null)
							return false;
						account.setAnonymous(true);
						account.setType(Account.FLIPPED);
						signedIn(account);
						if(terminal != null)
							terminal.observe(false);
						return true;
					}
				});
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
		
		destructor = new IDestructor() {

			@Override
			public void destruct() {
				
			}
			
		};
	}

	@Override
	public void onTerminate() {
		destructor.destruct();
	}

	private ImageLoader createImageLoader() {
		
		final DisplayImageOptions options = new DisplayImageOptions.Builder()
	            .showImageOnLoading(R.drawable.loading)
	            .showImageForEmptyUri(R.drawable.empty)
	            .showImageOnFail(R.drawable.empty)
				.resetViewBeforeLoading(false)
	            .delayBeforeLoading(1000)
	            .cacheInMemory(true)
	            .cacheOnDisk(true)
	            .resetViewBeforeLoading(true)
	            .considerExifParams(false)
	            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
	            .bitmapConfig(Bitmap.Config.RGB_565)
	            .displayer(new SimpleBitmapDisplayer())
	            .build();

	    final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(Shell.this)
	            .memoryCacheExtraOptions(480, 800)
	            .diskCacheExtraOptions(480, 800, null)
	            .threadPoolSize(3)
	            .threadPriority(Thread.NORM_PRIORITY - 2)
	            .tasksProcessingOrder(QueueProcessingType.FIFO)
	            .denyCacheImageMultipleSizesInMemory()
	            .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
	            .memoryCacheSize(2 * 1024 * 1024)
	            .memoryCacheSizePercentage(13)
	            .diskCache(new UnlimitedDiskCache(cache))
	            .diskCacheSize(50 * 1024 * 1024)
	            .diskCacheFileCount(100)
	            .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
	            .imageDownloader(new BaseImageDownloader(Shell.this) {

					@Override
					protected HttpURLConnection createConnection(String url, Object extra) throws IOException {
						HttpURLConnection conn = super.createConnection(url, extra);
						conn.addRequestProperty("Cookie", "JSESSIONID=" + self.getId());
						return conn;
					}
	            })
	            .imageDecoder(new BaseImageDecoder(true))
	            .defaultDisplayImageOptions(options)
	            .writeDebugLogs()
	            .build();
		
	    ImageLoader loader = ImageLoader.getInstance();
	    loader.init(config);
	    return loader;
	}
	
	public final String getDomain() {
		return domain;
	}
	
	public final String getCookie() {
		return config.getCookie();
	}
	
	public final Account getSelf() {
		return self;
	}

	public final List<DetailsItem<?>> getUser() {
		return user;
	}
	
	public final File root() {
		return root;
	}

	public final File databases() {
		return databases;
	}
	
	public final File cache() {
		return this.cache;
	}
	
	public final ImageLoader getImageLoader() {
		return imageLoader;
	}

	public final List<LoverTypePreference> getLoverTypePreferences() {
		return loverTypePreferences;
	}

	public final Feature getFeature() {
		return feature;
	}
	
	public final Configuration getConfig() {
		return this.config;
	}

	public void setUser(final List<DetailsItem<?>> user) {
		this.user = user;
	}

	public void setAccount(Account account) {
		this.self = account;
	}
	
	public final DatabaseController<Account> getInterestingController() {
		return interestingController;
	}
	
	public final DatabaseController<List<Map<String, String>>> getMaterialController() {
		return materialController;
	}

	public final List<DetailsItem<?>> getTendenties() {
		return tendenties;
	}

	public final void setTendenties(List<DetailsItem<?>> tendenties) {
		this.tendenties = tendenties;
	}
}
