package io.github.jevaengine.rpg.item;

import io.github.jevaengine.rpg.entity.character.IRpgCharacter;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.Nullable;

public interface IImmutableItemStore
{
	IObserverRegistry getObservers();
	
	boolean isFull();
	
	IImmutableItemSlot[] getSlots();
	
	@Nullable
	IImmutableItemSlot getEmptySlot();
	
	public interface IItemStoreObserver
	{
		void addItem(int slotIndex, IItem item);
		void removeItem(int slotIndex, IItem item);
		void itemAction(int slotIndex, IRpgCharacter accessor, String action);
	}
}
