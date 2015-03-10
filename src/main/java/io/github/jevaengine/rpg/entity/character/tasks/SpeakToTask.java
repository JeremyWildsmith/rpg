/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.jevaengine.rpg.entity.character.tasks;

import io.github.jevaengine.rpg.dialogue.IDialogueSpeakerSession;
import io.github.jevaengine.rpg.dialogue.IDialogueSpeakerSession.NullDialogueSpeakerSession;
import io.github.jevaengine.rpg.entity.character.IRpgCharacter;
import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.entity.tasks.ITask;
import java.lang.ref.WeakReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jeremy
 */
public final class SpeakToTask implements ITask
{
	private final Logger m_logger = LoggerFactory.getLogger(SpeakToTask.class);
	
	private final WeakReference<IRpgCharacter> m_target;

	private IDialogueSpeakerSession m_session = new NullDialogueSpeakerSession();
	
	public SpeakToTask(IRpgCharacter target)
	{
		m_target = new WeakReference<>(target);
	}
	
	@Override
	public void begin(IEntity entity)
	{
		m_session = new NullDialogueSpeakerSession();
		
		if(entity instanceof IRpgCharacter)
		{
			if(m_target.get() != null)
				m_session = ((IRpgCharacter)entity).getDialogueResolver().speak(m_target.get());
		} else
		{
			m_logger.error("Cannot converse as a non character. Ignoring converse task.");
		}
		
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
