package io.github.jevaengine.rpg.entity.character;

import io.github.jevaengine.rpg.item.IItem;
import io.github.jevaengine.rpg.item.IItemSlot;
import io.github.jevaengine.rpg.item.IItem.IWieldTarget;
import io.github.jevaengine.util.Nullable;

public interface ILoadout extends IImmutableLoadout
{
	void clear();
	IItem unequip(IWieldTarget type);
	
	@Nullable
	IItem equip(IItem item);
	
	@Nullable
	IItemSlot getSlot(IWieldTarget wieldTarget);
	
	IItemSlot[] getSlots();
}
