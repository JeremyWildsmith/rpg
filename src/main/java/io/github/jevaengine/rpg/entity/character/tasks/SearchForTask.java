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
