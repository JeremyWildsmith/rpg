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
package io.github.jevaengine.rpg;

import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.NullObservers;
import java.util.Map;
import java.util.Set;

public interface IImmutableAttributeSet
{
	boolean has(IAttributeIdentifier attribute);
	boolean has(String name);
	
	IImmutableAttribute get(IAttributeIdentifier attribute);
	IImmutableAttribute get(String name);
	
	Set<Map.Entry<IAttributeIdentifier, IImmutableAttribute>> getSet();
	
	public interface IAttributeIdentifier
	{
		String getName();
		String getDescription();
	}
	
	public interface IImmutableAttribute
	{
		boolean isZero();
		float get();
		IObserverRegistry getObservers();
	}
	
	public interface IAttributeChangeObserver
	{
		void changed(float delta);
	}
	
	public interface IAttribute extends IImmutableAttribute
	{
		void set(float value);
	}

	public static final class NullAttribute implements IAttribute
	{
		@Override
		public float get()
		{
			return 0;
		}

		@Override
		public void set(float value) { }
		
		@Override
		public IObserverRegistry getObservers()
		{
			return new NullObservers();
		}

		@Override
		public boolean isZero()
		{
			return true;
		}
	}
}
