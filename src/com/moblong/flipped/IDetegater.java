package com.moblong.flipped;

import com.moblong.flipped.feature.IObserver;

public interface IDetegater<T> {

	public void popup(final String[] data, final IObserver<String> getValue);
}
