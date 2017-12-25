package com.moblong.flipped;

import com.moblong.flipped.model.DetailsItem;

public final class IncludeTendentiousFilter<T> implements ITendentiousFilter<T> {
	
	private T[] items;
	
	private String name;
	
	public IncludeTendentiousFilter(final String name, final T[] items) {
		this.name  = name;
		this.items = items;
	}
	
	@Override
	public boolean filte(final DetailsItem<T> data) {
		for(T item : items) {
			if(item.equals(data.getContent()))
				return true;
		}
		return false;
	}

	@Override
	public String getName() {
		return name;
	}
	
}
