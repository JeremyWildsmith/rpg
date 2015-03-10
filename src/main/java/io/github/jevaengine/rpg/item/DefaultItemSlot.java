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
