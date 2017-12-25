package com.moblong.flipped;

import com.moblong.flipped.model.IOperator;

public class IncludeOperator<T extends Comparable<?>> implements IOperator<T> {

	@Override
	public boolean operate(final T[] data, final T t) {
		for(int i = 0; i < data.length; ++i) {
			if(t.equals(t))
				return true;
		}
		return false;
	}
}
