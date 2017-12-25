package com.moblong.flipped;

import com.moblong.flipped.model.DetailsItem;

public interface ITendentiousFilter<T> {

	public String getName();
	
	public boolean filte(final DetailsItem<T> item);
	
}
