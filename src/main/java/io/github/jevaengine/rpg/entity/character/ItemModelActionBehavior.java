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

import io.github.jevaengine.rpg.item.IImmutableItemSlot;
import io.github.jevaengine.rpg.item.IItem.IItemFunction;
import io.github.jevaengine.rpg.item.IImmutableItemSlot.IItemSlotObserver;
import io.github.jevaengine.world.scene.model.IAnimationSceneModel.AnimationSceneModelAnimationState;
import io.github.jevaengine.world.scene.model.IAnimationSceneModel.IAnimationSceneModelAnimation;
import io.github.jevaengine.world.scene.model.IAnimationSceneModel.IAnimationSceneModelAnimationObserver;
import io.github.jevaengine.world.scene.model.IAnimationSceneModel.NullAnimationSceneModelAnimation;
import io.github.jevaengine.world.scene.model.action.DefaultActionModel.IDefaultActionModelBehavior;

import java.util.HashMap;
import java.util.Map;

public final class ItemModelActionBehavior implements IDefaultActionModelBehavior
{
	private final Map<IItemFunction, IAnimationSceneModelAnimation> m_itemAnimations = new HashMap<>();
	private final IAnimationSceneModelAnimation m_noItemActionAnimation;
	
	private final String m_name;
	private final IImmutableItemSlot m_weaponSlot;
	
	private boolean m_isDone = false;
	private boolean m_isPerformed = false;
	private final boolean m_isPassive;
	private final AnimationController m_controller;
	
	public ItemModelActionBehavior(String name, IImmutableItemSlot subjectItemSlot, Map<IItemFunction, IAnimationSceneModelAnimation> itemAnimations, IAnimationSceneModelAnimation noItemActionAnimation, boolean isPassive)
	{
		m_name = name;
		m_isPassive = isPassive;
		m_weaponSlot = subjectItemSlot;
		m_itemAnimations.putAll(itemAnimations);
		m_noItemActionAnimation = noItemActionAnimation;
		m_controller = new AnimationController();
	}
	
	@Override
	public String getName()
	{
		return m_name;
	}
	
	@Override
	public void enter()
	{
		m_controller.begin();
	}

	@Override
	public boolean interrupt()
	{			
		if(!m_isPassive)
			return false;
		
		m_controller.end();
		return true;
	}
	
	@Override
	public boolean isDone()
	{
		return m_isDone;
	}

	@Override
	public boolean update(int deltaTime)
	{
		boolean isPerformed = m_isPerformed;
		m_isPerformed = false;
		
		return isPerformed;
	}

	@Override
	public boolean isPassive()
	{
		return m_isPassive;
	}
	
	private class AnimationController implements IAnimationSceneModelAnimationObserver, IItemSlotObserver
	{
		private IAnimationSceneModelAnimation m_currentAnimation = new NullAnimationSceneModelAnimation();
		
		@Override
		public void itemChanged()
		{
			//Cycle the animation (ie, to select the proper animation for the item.
			end();
			begin();
		}

		@Override
		public void event(String name)
		{
			m_isPerformed = true;
		}

		@Override
		public void stateChanged(AnimationSceneModelAnimationState state)
		{
			if(state == AnimationSceneModelAnimationState.Stop)
				end();
			else
			{
				m_isPerformed = false;
				m_isDone = false;
			}
		}
		
		public void begin()
		{
			end();
			m_currentAnimation = m_weaponSlot.isEmpty() || !m_itemAnimations.containsKey(m_weaponSlot.getItem().getFunction()) ? m_noItemActionAnimation : m_itemAnimations.get(m_weaponSlot.getItem().getFunction());
			m_weaponSlot.getObservers().add(this);
			m_currentAnimation.getObservers().add(this);
			m_currentAnimation.setState(AnimationSceneModelAnimationState.PlayToEnd);
		}
		
		public void end()
		{	
			m_isDone = true;
			m_currentAnimation.getObservers().remove(this);
			m_weaponSlot.getObservers().remove(this);
			m_currentAnimation.setState(AnimationSceneModelAnimationState.Stop);
		}
	}
}
