/**
 * *****************************************************************************
 * Copyright (c) 2013 Jeremy. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the GNU Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * If you'd like to obtain a another license to this code, you may contact
 * Jeremy to discuss alternative redistribution options.
 *
 * Contributors: Jeremy - initial API and implementation
 *****************************************************************************
 */
package io.github.jevaengine.rpg.entity.character;

import io.github.jevaengine.rpg.item.DefaultItemSlot;
import io.github.jevaengine.rpg.item.IItem;
import io.github.jevaengine.rpg.item.IItemStore;
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
	private ArrayList<DefaultItemSlot> m_inventory;

	private Observers m_observers = new Observers();

	public DefaultInventory(int slotCount)
	{
		m_inventory = new ArrayList<DefaultItemSlot>(slotCount);

		for (int i = 0; i < slotCount; i++)
			m_inventory.add(new DefaultItemSlot());
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
}
