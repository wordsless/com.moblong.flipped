package com.moblong.flipped.feature;

import android.view.View;

public interface IViewChangedCallback<T> {

	public void setView(final View view);
	
	public void callback(final String id, final T value);
	
}
