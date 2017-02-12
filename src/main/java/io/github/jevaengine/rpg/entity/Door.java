/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.jevaengine.rpg.entity;

import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.util.Observers;
import io.github.jevaengine.world.World;
import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.entity.IEntityTaskModel;
import io.github.jevaengine.world.entity.NullEntityTaskModel;
import io.github.jevaengine.world.entity.WorldAssociationException;
import io.github.jevaengine.world.physics.IPhysicsBody;
import io.github.jevaengine.world.physics.NonparticipantPhysicsBody;
import io.github.jevaengine.world.physics.NullPhysicsBody;
import io.github.jevaengine.world.physics.PhysicsBodyDescription;
import io.github.jevaengine.world.scene.model.IAnimationSceneModel;
import io.github.jevaengine.world.scene.model.IAnimationSceneModel.AnimationSceneModelAnimationState;
import io.github.jevaengine.world.scene.model.IAnimationSceneModel.IAnimationSceneModelAnimation;
import io.github.jevaengine.world.scene.model.IAnimationSceneModel.IAnimationSceneModelAnimationObserver;
import io.github.jevaengine.world.scene.model.IAnimationSceneModel.NullAnimationSceneModelAnimation;
import io.github.jevaengine.world.scene.model.IImmutableSceneModel;
import io.github.jevaengine.world.scene.model.ISceneModel;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jeremy Wildsmith
 */
public class Door implements IEntity
{
	private static final String CLOSE_ANIMATION_NAME = "close";
	private static final String OPEN_ANIMATION_NAME = "open";
	
	private final String m_name;
	
	private final ISceneModel m_model;

	private final PhysicsBodyDescription m_physicsBodyDescription;
	
	private IPhysicsBody m_body = new NullPhysicsBody();

	@Nullable
	private World m_world;
	
	private final Observers m_observers = new Observers();
	
	private final IEntity.EntityBridge m_bridge;

	private final IAnimationSceneModelAnimation m_openAnimation;
	private final IAnimationSceneModelAnimation m_closeAnimation;
	private boolean m_isOpen = false;
	
	public Door(IAnimationSceneModel model, String name, boolean isOpen)
	{
		m_name = name;
		
		m_model = model;
		
		m_openAnimation = model.hasAnimation(OPEN_ANIMATION_NAME) ? model.getAnimation(OPEN_ANIMATION_NAME) : new NullAnimationSceneModelAnimation();
		m_closeAnimation = model.hasAnimation(CLOSE_ANIMATION_NAME) ? model.getAnimation(CLOSE_ANIMATION_NAME) : new NullAnimationSceneModelAnimation();
		
		m_openAnimation.getObservers().add(new DoorAnimationStateObserver());
		m_closeAnimation.getObservers().add(new DoorAnimationStateObserver());
		
		m_isOpen = false;
		m_closeAnimation.setState(AnimationSceneModelAnimationState.PlayToEnd);
		
		m_physicsBodyDescription = new PhysicsBodyDescription(PhysicsBodyDescription.PhysicsBodyType.Static, model.getBodyShape(), 1.0F, true, false, 1.0F);
		
		m_bridge = new IEntity.EntityBridge(this);
		
		if(isOpen)
			open();
	}
	
	public void close() {
		if(!m_isOpen || !(m_openAnimation.getState() == AnimationSceneModelAnimationState.Stop && m_closeAnimation.getState() == AnimationSceneModelAnimationState.Stop))
			return;
		
		m_isOpen = false;
		m_closeAnimation.setState(AnimationSceneModelAnimationState.PlayToEnd);
	}
	
	public void open() {
		if(m_isOpen || !(m_openAnimation.getState() == AnimationSceneModelAnimationState.Stop && m_closeAnimation.getState() == AnimationSceneModelAnimationState.Stop))
			return;
		
		m_isOpen = true;
		m_openAnimation.setState(AnimationSceneModelAnimationState.PlayToEnd);
	}
	
	public boolean isOpen() {
		return m_isOpen;
	}
	
	@Override
	public void dispose()
	{
		if(m_world != null)
			m_world.removeEntity(this);
		
		m_observers.clear();
	}

	@Override
	public String getInstanceName()
	{
		return m_name;
	}
	
	@Override
	public final World getWorld()
	{
		return m_world;
	}

	@Override
	public final void associate(World world)
	{
		if (m_world != null)
			throw new WorldAssociationException("Already associated with world");

		m_world = world;

		if(!m_isOpen)
			constructPhysicsBody();
		
		m_observers.raise(IEntity.IEntityWorldObserver.class).enterWorld();
	}

	@Override
	public final void disassociate()
	{
		if (m_world == null)
			throw new WorldAssociationException("Not associated with world");

		m_observers.raise(IEntity.IEntityWorldObserver.class).leaveWorld();

		destroyPhysicsBody();
		
		m_world = null;
	}

	private void constructPhysicsBody()
	{
		if(m_world == null)
			return;
		
		destroyPhysicsBody();
		
		if(m_physicsBodyDescription == null)
			m_body = new NonparticipantPhysicsBody(this, m_model.getAABB());
		else
		{
			m_body = m_world.getPhysicsWorld().createBody(this, m_physicsBodyDescription);
			m_observers.raise(IEntity.IEntityBodyObserver.class).bodyChanged(new NullPhysicsBody(), m_body);
		}
	}
	
	private void destroyPhysicsBody()
	{
		m_body.destory();
		m_body = new NullPhysicsBody();
		m_observers.raise(IEntity.IEntityBodyObserver.class).bodyChanged(new NullPhysicsBody(), m_body);
	}

	@Override
	public boolean isStatic()
	{
		return false;
	}
	
	@Override
	public final IPhysicsBody getBody()
	{
		return m_body;
	}
	
	@Override
	public void update(int deltaTime)
	{
		m_model.setDirection(m_body.getDirection());
		m_model.update(deltaTime);
	}
	
	@Override
	@Nullable
	public IImmutableSceneModel getModel()
	{
		return m_model;
	}

	@Override
	public Map<String, Integer> getFlags()
	{
		return new HashMap<>();
	}

	@Override
	public IObserverRegistry getObservers()
	{
		return m_observers;
	}
	
	@Override
	public IEntity.EntityBridge getBridge()
	{
		return m_bridge;
	}

	@Override
	public IEntityTaskModel getTaskModel()
	{
		return new NullEntityTaskModel();
	}
	
	private class DoorAnimationStateObserver implements IAnimationSceneModelAnimationObserver {

		@Override
		public void event(String name) { }

		@Override
		public void stateChanged(AnimationSceneModelAnimationState state) {
			if(state == AnimationSceneModelAnimationState.Stop)
			{
				m_body.setCollidable(!m_isOpen);
			}
		}
	}
}
