package com.moblong.flipped.util;

import java.util.ArrayList;
import java.util.List;

import com.moblong.flipped.feature.IDBTirgger;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public abstract class DatabaseController<T> {
	
	public static final int EXIST = 1;
	
	public static final int SUCCESS = 2;
	
	public static final int FAILED = 2;
	
	protected String tableName;
	
	protected SQLiteDatabase db;
	
	protected List<IDBTirgger<Boolean>> addTirggers, deleteTirggers, updateTirggers;
	
	public DatabaseController(String tableName) {
		this.tableName = tableName;
	}
	
	public DatabaseController<T> open(final Context context) {
		if(db != null && db.isOpen())
			return this;
		db = context.openOrCreateDatabase("flipped_1.0", Context.MODE_PRIVATE, null);
		if(!isTableExist()) {
			SecurityReaderAndWirter reader = new SecurityReaderAndWirter();
			String  script = reader.read(context.getAssets(), String.format("%s_create.sql", tableName));
			if(script != null && script.length() > 0)
				db.execSQL(script);
		}
		addTirggers    = new ArrayList<IDBTirgger<Boolean>>(10);
		deleteTirggers = new ArrayList<IDBTirgger<Boolean>>(10);
		updateTirggers = new ArrayList<IDBTirgger<Boolean>>(10);
		return this;
	}
	
	public void close() {
		db.close();
		db = null;
	}
	
	public void register(final String operator, final IDBTirgger<Boolean> trigger) {
		if(operator.equals("add")) {
			if(!addTirggers.contains(trigger))
				addTirggers.add(trigger);
		} else if(operator.equals("delete")) {
			if(!deleteTirggers.contains(trigger))
				deleteTirggers.add(trigger);
		} else if(operator.equals("update")) {
			if(!updateTirggers.contains(trigger))
				updateTirggers.add(trigger);
		}
	}
	
	public abstract List<T> reload();
	
	public abstract void add(final T t);
	
	public abstract void delete(final T t);
	
	public abstract void update(final T t);
	
	public abstract boolean exsit(T t);
	
	private boolean isTableExist() {
		boolean result = false;
		if(!db.isOpen())
			return result;
		
		Cursor cursor = db.rawQuery("SELECT COUNT(1) FROM sqlite_master WHERE type ='table' and name = ?", new String[]{tableName});
        if(cursor.moveToNext()){
        	result = cursor.getInt(0) > 0;
        }
        cursor.close();
        cursor = null;
        return result;
	}

	public void unregister(IDBTirgger<Boolean> tirgger) {
		if(addTirggers.contains(tirgger)) {
			addTirggers.remove(tirgger);
		} else if(deleteTirggers.contains(tirgger)){
			deleteTirggers.remove(tirgger);
		} else if(updateTirggers.contains(tirgger)) {
			updateTirggers.remove(tirgger);
		}
	}
}
