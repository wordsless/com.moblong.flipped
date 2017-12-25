package com.moblong.flipped.util;

import java.util.ArrayList;
import java.util.List;

import com.moblong.flipped.feature.IDBTirgger;
import com.moblong.flipped.feature.ItemPushable;
import com.moblong.flipped.model.Account;

import android.database.Cursor;

public final class InterestingController extends DatabaseController<Account> {

	public InterestingController() {
		super("t_interesting_base");
	}

	@Override
	public List<Account> reload() {
		List<Account> ds = null;
		Cursor cursor = db.rawQuery("SELECT aid, alias, signature, ppid FROM t_interesting_base", null);
		ds = new ArrayList<Account>(cursor.getColumnCount());
        while(cursor.moveToNext()){
        	Account account = new Account();
        	account.setId(cursor.getString(0));
        	account.setAlias(cursor.getString(1));
        	account.setSignature(cursor.getString(2));
        	account.setAvatar(cursor.getString(3));
        	ds.add(account);
        }
        cursor.close();
        cursor = null;
        return ds;
	}

	@Override
	public void add(Account account) {
		db.execSQL("INSERT INTO t_interesting_base(aid, alias, signature, ppid) VALUES(?, ?, ?, ?)", new String[]{account.getId(), account.getAlias(), account.getSignature(), account.getAvatar()});
		for(IDBTirgger<Boolean> trigger : addTirggers)
			trigger.trigger(true);
	}

	@Override
	public void delete(Account account) {
		boolean result = db.delete("t_interesting_base", "aid=?", new String[]{account.getId()}) > 0;
		for(IDBTirgger<Boolean> trigger : deleteTirggers)
			trigger.trigger(result);
	}

	@Override
	public boolean exsit(Account t) {
		boolean exsit = false;
		Cursor cursor = db.rawQuery("SELECT COUNT(1) FROM t_interesting_base WHERE aid = ?", new String[]{t.getId()});
		if(cursor.moveToNext()) {
			exsit = cursor.getInt(0) > 0;
		}
		cursor.close();
        cursor = null;
		return exsit;
	}

	@Override
	public void update(Account t) {
		
	}

	public List<ItemPushable<Account>> reloadAsItemPushTrigger() {
		List<Account> accounts = reload();
		List<ItemPushable<Account>> ds = new ArrayList<ItemPushable<Account>>(accounts.size());
		for(Account account : accounts) {
			ItemPushable<Account> trigger = new ItemPushable<Account>(new String[]{"com.moblong.action.INVITE"}, account);
			ds.add(trigger);
		}
		return ds;
	}
}
