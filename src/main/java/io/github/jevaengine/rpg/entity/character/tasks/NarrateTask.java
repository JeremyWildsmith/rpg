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

import io.github.jevaengine.rpg.dialogue.IDialogueListenerSession;
import io.github.jevaengine.rpg.dialogue.IDialogueListenerSession.NullDialogueListenerSession;
import io.github.jevaengine.rpg.dialogue.IDialogueRoute;
import io.github.jevaengine.rpg.entity.character.IRpgCharacter;
import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.entity.tasks.ITask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jeremy
 */
public final class NarrateTask implements ITask
{
	private final Logger m_logger = LoggerFactory.getLogger(NarrateTask.class);
	
	private IDialogueListenerSession m_session = new NullDialogueListenerSession();
	private final IDialogueRoute m_dialogueRoute;
	
	public NarrateTask(IDialogueRoute route)
	{
		m_dialogueRoute = route;
	}
	
	@Override
	public void begin(IEntity entity)
	{
		m_session = new NullDialogueListenerSession();
		
		if(entity instanceof IRpgCharacter)
			m_session = ((IRpgCharacter)entity).getDialogueResolver().narrate(m_dialogueRoute);
		else
			m_logger.error("Cannot converse as a non character. Ignoring converse task.");
	}

	@Override
	public void end() { }

	@Override
	public void cancel()
	{
		m_session.cancel();
	}

	@Override
	public boolean doCycle(int deltaTime)
	{
		return !m_session.isActive();
	}

	@Override
	public boolean isParallel()
	{
		return false;
	}
}
