/* 
 * Copyright (C) 2015 Jeremy Wildsmith.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package io.github.jevaengine.rpg.entity.character;

import io.github.jevaengine.rpg.item.DefaultItemSlot;
import io.github.jevaengine.rpg.item.IItem;
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
	private HashMap<IWieldTarget, DefaultLoadoutItemSlot> m_slots = new HashMap<>();
	
	private Observers m_observers = new Observers();
	
	public DefaultLoadout() { }
	
	public void addWieldTarget(IWieldTarget target)
	{
		if(!m_slots.containsKey(target))
			m_slots.put(target, new DefaultLoadoutItemSlot(target));
	}
	
	@Override
	public IObserverRegistry getObservers()
	{
		return m_observers;
	}
	
	@Override
	@Nullable
	public ILoadoutSlot getSlot(IWieldTarget gearType)
	{
		return m_slots.get(gearType);
	}
	
	@Override
	@Nullable
	public IItem equip(IItem item)
	{
		for(IWieldTarget target : item.getFunction().getWieldTargets())
		{
			DefaultLoadoutItemSlot targetSlot = m_slots.get(target);

			if(targetSlot == null)
				continue;

			IItem currentItem = targetSlot.getItem();

			if(currentItem == item)
				return item;

			if(currentItem != null)
				m_observers.raise(ILoadoutObserver.class).unequip(target);

			m_observers.raise(ILoadoutObserver.class).equip(item, target);
			return targetSlot.setItem(item);
		}
		
		return null;
	}
	
	@Override
	public IItem unequip(IWieldTarget target)
	{
		DefaultLoadoutItemSlot targetSlot = m_slots.get(target);
		
		if(targetSlot == null)
			return null;

		m_observers.raise(ILoadoutObserver.class).unequip(target);
	
		return targetSlot.clear();
	}
	
	@Override
	public void clear()
	{
		for(DefaultLoadoutItemSlot s : m_slots.values())
		{
			if(!s.isEmpty())
				unequip(s.getWieldTarget());
		}
	}

	@Override
	public ILoadoutSlot[] getSlots()
	{
		Collection<DefaultLoadoutItemSlot> slots = m_slots.values();
		
		return slots.toArray(new ILoadoutSlot[slots.size()]);
	}

	@Override
	public IWieldTarget[] getWieldTargets() {
		return m_slots.keySet().toArray(new IWieldTarget[m_slots.size()]);
	}
	
	public final class DefaultLoadoutItemSlot implements ILoadoutSlot {
		private final DefaultItemSlot m_slot = new DefaultItemSlot();
		private final IWieldTarget m_wieldTarget;
		
		public DefaultLoadoutItemSlot(IWieldTarget wieldTarget) {
			m_wieldTarget = wieldTarget;
		}
		
		@Override
		public IItem setItem(IItem item) {
			IItem old = clear();
			
			m_slot.setItem(item);
			
			m_observers.raise(ILoadoutObserver.class).equip(item, m_wieldTarget);
			
			return old;
		}

		@Override
		public IItem clear() {
			IItem old;
			
			if((old = m_slot.clear()) != null)
				m_observers.raise(ILoadoutObserver.class).unequip(m_wieldTarget);
		
			return old;
		}

		@Override
		public boolean isEmpty() {
			return m_slot.isEmpty();
		}

		@Override
		public IItem getItem() {
			return m_slot.getItem();
		}

		@Override
		public IObserverRegistry getObservers() {
			return m_slot.getObservers();
		}

		@Override
		public IWieldTarget getWieldTarget() {
			return m_wieldTarget;
		}
	}
}
