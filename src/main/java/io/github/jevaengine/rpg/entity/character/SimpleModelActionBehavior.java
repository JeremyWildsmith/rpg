package io.github.jevaengine.rpg.entity.character;

import io.github.jevaengine.world.scene.model.IAnimationSceneModel.AnimationSceneModelAnimationState;
import io.github.jevaengine.world.scene.model.IAnimationSceneModel.IAnimationSceneModelAnimation;
import io.github.jevaengine.world.scene.model.IAnimationSceneModel.IAnimationSceneModelAnimationObserver;
import io.github.jevaengine.world.scene.model.action.DefaultActionModel.IDefaultActionModelBehavior;

public class SimpleModelActionBehavior implements IDefaultActionModelBehavior
{
	private final IAnimationSceneModelAnimation m_actionAnimation;
	
	private final String m_name;
	
	private boolean m_isDone = false;
	private boolean m_isPerformed = false;
	private final boolean m_isPassive;
	
	public SimpleModelActionBehavior(String name, IAnimationSceneModelAnimation actionAnimation, boolean isPassive)
	{
		m_name = name;
		m_actionAnimation = actionAnimation;
		m_isPassive = isPassive;
	}
	
	@Override
	public String getName()
	{
		return m_name;
	}

	@Override
	public void enter()
	{
		m_isDone = true;
		m_isPerformed = true;	
		
		m_actionAnimation.getObservers().add(new IAnimationSceneModelAnimationObserver() {
			@Override
			public void event(String name)
			{
				m_isPerformed = true;
			}

			@Override
			public void stateChanged(AnimationSceneModelAnimationState state)
			{				
				if(state == AnimationSceneModelAnimationState.Stop)
				{
					m_actionAnimation.getObservers().remove(this);
					m_isDone = true;
				}else
				{
					m_isPerformed = false;
					m_isDone = false;
				}
			}
		});
		
		m_actionAnimation.setState(AnimationSceneModelAnimationState.PlayToEnd);
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

	@Override
	public boolean interrupt()
	{
		if(!m_isPassive)
			return false;
		
		m_actionAnimation.setState(AnimationSceneModelAnimationState.Stop);
		m_isDone = true;
		return true;
	}
}
