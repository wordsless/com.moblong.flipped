package com.moblong.flipped.feature;

import java.util.ArrayList;
import java.util.List;

import com.moblong.flipped.model.Identifiable;
import com.moblong.flipped.model.Whistle;
import com.moblong.iwe.IRecivedListener;

public class ItemPushable<T extends Identifiable<?>> implements IRecivedListener {
	
	private String[] actions;
	
	private T target;
	
	private IUpdateUI<?> parent;
	
	private IObserver<String> receiver, consumer;

	private List<String> slot = new ArrayList<String>(128);
	
	public final IUpdateUI<?> getParent() {
		return parent;
	}

	public final void bind(IUpdateUI<?> parent) {
		this.parent = parent;
	}

	public final void setReceiver(IObserver<String> receiver) {
		this.receiver = receiver;
	}
	
	public final void setConsumer(IObserver<String> consumer) {
		this.consumer = consumer;
	}
	
	public ItemPushable(final String[] action, final T target) {
		this.actions = new String[action.length];
		System.arraycopy(action, 0, this.actions, 0, action.length);
		this.target = target;
	}
	
	public ItemPushable(String[] action) {
		this(action, null);
	}
	
	public void bind(final T target) {
		this.target = target;
	}
	
	public void bind(final IObserver<String> receiver, final IObserver<String> consumer) {
		this.receiver = receiver;
		this.consumer = consumer;
	}
	
	public T getTarget() {
		return this.target;
	}
	
	public final boolean consume() {
		int index = slot.size() - 1;
		if(consumer != null && index >= 0) {
			consumer.observe(slot.get(index));
			slot.remove(index);
		}
		return index >= 0;
	}
	
	public final int count() {
		return slot.size();
	}
	
	public boolean accept(final String _action) {
		for(String action : actions) {
			if(_action.equals(action))
				return true;
		}
		return false;
	}

	@Override
	public boolean recived(Whistle<String> whistle) {
		if(accept(whistle.getAction()) & whistle.getTarget().equals(target.getId())) {
			slot.add(whistle.getContent());
			if(receiver != null)
				receiver.observe(whistle.getContent());
			return true;
		}
		return false;
	}
}
