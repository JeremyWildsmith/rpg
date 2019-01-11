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

import io.github.jevaengine.rpg.item.*;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.util.Observers;

import java.util.ArrayList;

/**
 *
 * @author Jeremy
 */
public final class DefaultInventory implements IItemStore
{	
	private final ArrayList<DefaultItemSlot> m_inventory;

	private final Observers m_observers = new Observers();

	public DefaultInventory(int slotCount)
	{
		m_inventory = new ArrayList<>(slotCount);

		for (int i = 0; i < slotCount; i++) {
			DefaultItemSlot slot = new DefaultItemSlot();
			slot.getObservers().add(new ItemSlotObserver(i));
			m_inventory.add(slot);
		}
	}

	@Override
	public IObserverRegistry getObservers()
	{
		return m_observers;
	}

	@Override
	public DefaultItemSlot[] getSlots()
	{
		return m_inventory.toArray(new DefaultItemSlot[m_inventory.size()]);
	}

	@Override
	public boolean hasItem(IItem item)
	{
		for (DefaultItemSlot slot : m_inventory)
		{
			if (!slot.isEmpty() && slot.getItem().equals(item))
				return true;
		}

		return false;
	}

	@Override
	public boolean addItem(IItem item)
	{
		for (int i = 0; i < m_inventory.size(); i++)
		{
			if (m_inventory.get(i).isEmpty())
			{
				m_inventory.get(i).setItem(item);
				m_observers.raise(IItemStoreObserver.class).addItem(i, item);
				return true;
			}
		}

		return false;
	}

	@Override
	public void clear() {
		for(IItemSlot s : m_inventory)
			s.clear();
	}

	@Override
	public boolean removeItem(IItem item)
	{
		for (int i = 0; i < m_inventory.size(); i++)
		{
			DefaultItemSlot slot = m_inventory.get(i);

			if (!slot.isEmpty() && slot.getItem().equals(item))
			{
				m_observers.raise(IItemStoreObserver.class).removeItem(i, slot.getItem());
				slot.clear();
				return true;
			}
		}

		return false;
	}

	@Override
	@Nullable
	public DefaultItemSlot getEmptySlot()
	{
		for (DefaultItemSlot slot : m_inventory)
		{
			if (slot.isEmpty())
				return slot;
		}

		return null;
	}

	@Override
	public boolean isFull()
	{
		return getEmptySlot() == null;
	}

	private final class ItemSlotObserver implements IImmutableItemSlot.IItemSlotObserver {
		private final int index;

		public ItemSlotObserver(int index) {
			this.index = index;
		}

		@Override
		public void itemChanged(IItem old, IItem newItem) {
			if(old != null) {
				m_observers.raise(IItemStoreObserver.class).removeItem(index, old);
			}

			if(newItem != null) {
				m_observers.raise(IItemStoreObserver.class).addItem(index, newItem);
			}
		}
	}
}
