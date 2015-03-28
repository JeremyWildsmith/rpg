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
import io.github.jevaengine.util.NullObservers;
import io.github.jevaengine.util.Nullable;

public interface IItemSlot extends IImmutableItemSlot
{
	@Nullable
	IItem setItem(IItem item);
	
	@Nullable
	IItem clear();
	
	public static final class NullItemSlot implements IItemSlot
	{

		@Override
		public boolean isEmpty()
		{
			return true;
		}

		@Override
		public IItem getItem()
		{
			return null;
		}

		@Override
		public IItem setItem(IItem item)
		{
			return null;
		}

		@Override
		public IItem clear()
		{
			return null;
		}

		@Override
		public IObserverRegistry getObservers()
		{
			return new NullObservers();
		}
	}
}
