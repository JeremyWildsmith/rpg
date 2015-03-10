package io.github.jevaengine.rpg.entity.character;

import io.github.jevaengine.rpg.item.IItem;
import io.github.jevaengine.rpg.item.IImmutableItemSlot;
import io.github.jevaengine.rpg.item.IItem.IWieldTarget;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.Nullable;

public interface IImmutableLoadout
{
	IObserverRegistry getObservers();

	@Nullable
	IImmutableItemSlot getSlot(IWieldTarget wieldTarget);
	
	IImmutableItemSlot[] getSlots();
	
	public interface ILoadoutObserver
	{
		void unequip(IWieldTarget wieldTarget);
		void equip(IItem item);
	}
}
