/*******************************************************************************
 * Copyright (c) 2013 Jeremy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * If you'd like to obtain a another license to this code, you may contact Jeremy to discuss alternative redistribution options.
 * 
 * Contributors:
 *     Jeremy - initial API and implementation
 ******************************************************************************/


package io.github.jevaengine.rpg.entity.character;

import io.github.jevaengine.rpg.item.DefaultItemSlot;
import io.github.jevaengine.rpg.item.IItem;
import io.github.jevaengine.rpg.item.IItemSlot;
import io.github.jevaengine.rpg.item.IItem.IWieldTarget;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.util.Observers;

import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author Jeremy
 */
public final class DefaultLoadout implements ILoadout
{
	private HashMap<IWieldTarget, DefaultItemSlot> m_slots = new HashMap<>();
	
	private Observers m_observers = new Observers();
	
	public DefaultLoadout() { }
	
	public void addWieldTarget(IWieldTarget target)
	{
		if(!m_slots.containsKey(target))
			m_slots.put(target, new DefaultItemSlot());
	}
	
	@Override
	public IObserverRegistry getObservers()
	{
		return m_observers;
	}
	
	@Override
	@Nullable
	public IItemSlot getSlot(IWieldTarget gearType)
	{
		return m_slots.get(gearType);
	}
	
	@Override
	@Nullable
	public IItem equip(IItem item)
	{
		DefaultItemSlot targetSlot = m_slots.get(item.getFunction().getWieldTarget());
		
		if(targetSlot == null)
			return null;
		
		IItem currentItem = targetSlot.getItem();
		
		if(currentItem == item)
			return item;
		
		if(currentItem != null)
			m_observers.raise(ILoadoutObserver.class).unequip(currentItem.getFunction().getWieldTarget());
		
		m_observers.raise(ILoadoutObserver.class).equip(item);
		return targetSlot.setItem(item);
	}
	
	@Override
	public IItem unequip(IWieldTarget target)
	{
		DefaultItemSlot targetSlot = m_slots.get(target);
		
		if(targetSlot == null)
			return null;

		m_observers.raise(ILoadoutObserver.class).unequip(target);
	
		return targetSlot.clear();
	}
	
	@Override
	public void clear()
	{
		for(DefaultItemSlot s : m_slots.values())
		{
			if(!s.isEmpty())
				unequip(s.getItem().getFunction().getWieldTarget());
		}
	}

	@Override
	public IItemSlot[] getSlots()
	{
		Collection<DefaultItemSlot> slots = m_slots.values();
		
		return slots.toArray(new IItemSlot[slots.size()]);
	}
}
