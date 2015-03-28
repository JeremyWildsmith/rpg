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
package io.github.jevaengine.rpg.entity.character.tasks;

import io.github.jevaengine.rpg.entity.character.IRpgCharacter;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.entity.tasks.ITask;
import io.github.jevaengine.world.entity.tasks.InvalidTaskHostException;

public final class SearchForTask<T extends IEntity> implements ITask
{
	@Nullable
	private IRpgCharacter m_searcher;

	private Class<T> m_seekingClass;
	private ISearchListener<T> m_listener;

	private boolean m_isQueryCancel;

	public SearchForTask(Class<T> seekingClass, ISearchListener<T> listener)
	{
		m_seekingClass = seekingClass;
		m_isQueryCancel = false;
		m_listener = listener;
	}
	
	@Override
	public final void cancel()
	{
		m_isQueryCancel = true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final boolean doCycle(int deltaTime)
	{
		if (m_isQueryCancel)
			return true;

		for (IEntity e : m_searcher.getVisionResolver().getVisibleEntities())
		{
			if (m_seekingClass.isAssignableFrom(e.getClass()))
				m_listener.found((T)e);
		}

		return false;
	}

	@Override
	public final boolean isParallel()
	{
		return true;
	}

	@Override
	public final void begin(IEntity entity)
	{
		if(!(entity instanceof IRpgCharacter))
			throw new InvalidTaskHostException("This task must be applied only to instances of IRpgCharacter.");
		
		m_isQueryCancel = false;
		m_searcher = (IRpgCharacter)entity;
	}

	@Override
	public final void end() { }

	public interface ISearchListener<Y extends IEntity>
	{
		void found(Y entity);
	}
}
