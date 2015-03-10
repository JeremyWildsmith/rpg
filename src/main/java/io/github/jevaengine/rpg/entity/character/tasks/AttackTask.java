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

import io.github.jevaengine.rpg.IImmutableAttributeSet;
import io.github.jevaengine.rpg.entity.character.ICombatResolver.IAttackObserver;
import io.github.jevaengine.rpg.entity.character.IRpgCharacter;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.entity.tasks.ITask;
import io.github.jevaengine.world.entity.tasks.InvalidTaskHostException;

public final class AttackTask implements ITask
{
	private IRpgCharacter m_attackee;
	private boolean m_isOver = false;
	
	public AttackTask(@Nullable IRpgCharacter attackee)
	{
		m_attackee = attackee;
	}

	@Override
	public void begin(IEntity entity)
	{
		if(!(entity instanceof IRpgCharacter))
			throw new InvalidTaskHostException("Task host must be an instanceof RpgCharacter.");
	
		m_isOver = false;
		
		IRpgCharacter attacker = (IRpgCharacter)entity;

		if(attacker.getCombatResolver().testAttackAbility(m_attackee).canAttack())
		{
			attacker.getCombatResolver().attack(m_attackee, new IAttackObserver() {

				@Override
				public void performed(IImmutableAttributeSet impact) { }

				@Override
				public void end() {
					m_isOver = true;
				}

				@Override
				public void begin() { }
			});
		} else
			m_isOver = true;
	}

	@Override
	public void end() { }

	@Override
	public void cancel() { }

	@Override
	public boolean doCycle(int deltaTime)
	{
		return m_isOver;
	}

	@Override
	public boolean isParallel()
	{
		return false;
	}
}
