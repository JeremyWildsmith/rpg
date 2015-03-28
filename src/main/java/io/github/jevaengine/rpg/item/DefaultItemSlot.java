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
package io.github.jevaengine.rpg.item;

import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.util.Observers;

import java.util.NoSuchElementException;

public final class DefaultItemSlot implements IItemSlot
{
	private final Observers m_observers = new Observers();
	
	@Nullable
	private IItem m_item;

	public DefaultItemSlot() { }
	
	public DefaultItemSlot(IItem item)
	{
		m_item = item;
	}

	@Override
	public boolean isEmpty()
	{
		return m_item == null;
	}

	@Override
	@Nullable
	public IItem getItem()
	{
		if (m_item == null)
			throw new NoSuchElementException();

		return m_item;
	}

	@Override
	public IItem setItem(IItem item)
	{
		IItem prev = m_item;
		m_item = item;
		
		if(prev != m_item)
			m_observers.raise(IItemSlotObserver.class).itemChanged();
		
		return prev;	
	}

	@Override
	@Nullable
	public IItem clear()
	{
		IItem prev = m_item;
		m_item = null;
		
		if(prev != m_item)
			m_observers.raise(IItemSlotObserver.class).itemChanged();
		
		return prev;
	}

	@Override
	public IObserverRegistry getObservers()
	{
		return m_observers;
	}
}
