package io.github.jevaengine.rpg.item;

import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.Nullable;

public interface IImmutableItemSlot
{
	boolean isEmpty();
	
	@Nullable
	IItem getItem();
	
	IObserverRegistry getObservers();
	
	public static interface IItemSlotObserver
	{
		void itemChanged();
	}
}
