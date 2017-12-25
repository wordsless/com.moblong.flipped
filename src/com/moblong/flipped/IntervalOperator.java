package com.moblong.flipped;

import com.moblong.flipped.model.IOperator;

public class IntervalOperator<T extends Comparable> implements IOperator<T> {

	@SuppressWarnings("unchecked")
	@Override
	public boolean operate(T[] data, T t) {
		T min = data[0];
		T max = data[1];
		return (t.compareTo(min) <= 0 & t.compareTo(max) >= 0);
	}

}
