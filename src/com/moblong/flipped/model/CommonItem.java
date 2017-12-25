package com.moblong.flipped.model;

import android.graphics.drawable.Drawable;

public final class CommonItem {

	private Drawable icon, symbol;
	
	private String title, status;

	public CommonItem(Drawable icon, String title, String status, Drawable symbol) {
		this.icon   = icon;
		this.title  = title;
		this.status = status;
		this.symbol = symbol;
	}
	
	public final Drawable getIcon() {
		return icon;
	}

	public final void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public final Drawable getSymbol() {
		return symbol;
	}

	public final void setSymbol(Drawable symbol) {
		this.symbol = symbol;
	}

	public final String getTitle() {
		return title;
	}

	public final void setTitle(String title) {
		this.title = title;
	}

	public final String getStatus() {
		return status;
	}

	public final void setStatus(String status) {
		this.status = status;
	}
	
}
