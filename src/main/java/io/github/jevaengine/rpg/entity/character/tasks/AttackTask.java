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
