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

import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.NullObservers;
import io.github.jevaengine.world.scene.model.IActionSceneModel;

/**
 *
 * @author Jeremy
 */
public interface IStatusResolver extends IRpgCharacterMechanicResolver
{
	boolean isDead();
	
	IObserverRegistry getObservers();
	
	public interface IStatusResolverObserver
	{
		void died();
		void revived();
	}
	
	public static final class NullStatusResolver implements IStatusResolver
	{
		@Override
		public boolean isDead()
		{
			return false;
		}
		
		@Override
		public IObserverRegistry getObservers()
		{
			return new NullObservers();
		}
		
		@Override
		public IActionSceneModel decorate(IActionSceneModel subject)
		{
			return subject;
		}
		
		@Override
		public void update(int deltaTime) { }
	}
}
