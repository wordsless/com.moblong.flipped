package com.moblong.flipped.feature;

public interface ICommand<T> {

	public int exec(final String[] argv, final IResultCallback<T> processor);
	
}
