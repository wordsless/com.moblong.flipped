package com.moblong.flipped.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.moblong.flipped.feature.IDBTirgger;
import com.moblong.flipped.feature.ItemPushable;
import com.moblong.flipped.model.Account;

import android.database.Cursor;

public final class MaterialController extends DatabaseController<List<Map<String, String>>> {

	private int operator;
	
	public MaterialController() {
		super("t_material_base");
	}

	public void bind(final int operator) {
		this.operator = operator;
	}
	
	@Override
	public void add(List<Map<String, String>> list) {
		for(Map<String, String> item : list) {
			db.execSQL("INSERT INTO t_material_base(operator, res, desc) VALUES(?, ?, ?)", new String[]{Integer.toString(operator), item.get("res"), item.get("desc")});
		}
		for(IDBTirgger<Boolean> trigger : updateTirggers)
			trigger.trigger(true);
	}

	@Override
	public void delete(List<Map<String, String>> t) {
		db.execSQL("DELETE FROM t_material_base WHERE operator = ?", new String[]{Integer.toString(operator)});
	}

	@Override
	public boolean exsit(List<Map<String, String>> t) {
		boolean exsit = false;
		Cursor cursor = db.rawQuery("SELECT COUNT(1) FROM t_material_base WHERE operator = ?", new String[]{Integer.toString(operator)});
		if(cursor.moveToNext()) {
			exsit = cursor.getInt(0) > 0;
		}
		cursor.close();
        cursor = null;
		return exsit;
	}

	@Override
	public List<List<Map<String, String>>> reload() {
		List<List<Map<String, String>>> ds = null;
		Cursor cursor = db.rawQuery("SELECT operator, res, desc FROM t_material_base WHERE operator = ?", new String[]{Integer.toString(operator)});
		ds = new ArrayList<List<Map<String, String>>>(cursor.getColumnCount());
		List<Map<String, String>> material = new ArrayList<Map<String, String>>();
        while(cursor.moveToNext()){
        	Map<String, String> item = new HashMap<String, String>();
        	item.put("res", cursor.getString(0));
        	item.put("desc", cursor.getString(1));
        	material.add(item);
        }
        ds.add(material);
        cursor.close();
        cursor = null;
        return ds;
	}

	@Override
	public void update(List<Map<String, String>> list) {
		boolean result = false;
		result = db.delete("t_interesting_base", "operator = ?", new String[]{Integer.toString(operator)}) > 0;
		for(Map<String, String> item : list) {
			db.execSQL("INSERT INTO t_material_base(operator, res, desc) VALUES(?, ?, ?)", new String[]{Integer.toString(operator), item.get("res"), item.get("desc")});
		}
		for(IDBTirgger<Boolean> trigger : updateTirggers)
			trigger.trigger(result);
	}

}
