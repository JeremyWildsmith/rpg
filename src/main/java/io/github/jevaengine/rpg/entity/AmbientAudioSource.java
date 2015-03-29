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
package io.github.jevaengine.rpg.entity;

import io.github.jevaengine.audio.IAudioClip;
import io.github.jevaengine.script.IFunctionFactory;
import io.github.jevaengine.script.IScriptBuilder;
import io.github.jevaengine.script.IScriptBuilder.ScriptConstructionException;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.Observers;
import io.github.jevaengine.world.World;
import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.entity.IEntityTaskModel;
import io.github.jevaengine.world.entity.NullEntityTaskModel;
import io.github.jevaengine.world.entity.WorldAssociationException;
import io.github.jevaengine.world.physics.IPhysicsBody;
import io.github.jevaengine.world.physics.NullPhysicsBody;
import io.github.jevaengine.world.scene.model.IImmutableSceneModel;
import io.github.jevaengine.world.scene.model.NullSceneModel;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmbientAudioSource implements IEntity
{
	private final Logger m_logger = LoggerFactory.getLogger(AmbientAudioSource.class);
	
	private final String m_name;
	private final AmbientAudioSourceBridge m_bridge;
	private final IAudioClip m_clip;

	private final Observers m_observers = new Observers();
	
	private World m_world;
	
	public AmbientAudioSource(IAudioClip clip, IScriptBuilder behavior, String name)
	{
		m_clip = clip;
		m_name = name;
		
		m_bridge = new AmbientAudioSourceBridge(this, behavior.getFunctionFactory(), behavior.getUri());
		
		try
		{
			behavior.create(m_bridge);
		} catch (ScriptConstructionException e)
		{
			m_logger.error("Error constructing behavior for AmbientAudioSource '" + name + "'. Assuming null behavior", e);
		}
	}
	
	@Override
	public World getWorld()
	{
		return m_world;
	}

	@Override
	public void associate(World world)
	{
		if(m_world != null)
			throw new WorldAssociationException("Entity already associated with world.");
		
		m_world = world;
		
	}

	@Override
	public void disassociate()
	{
		if(m_world == null)
			throw new WorldAssociationException("Entity not associated with world.");
		
		m_world = null;
		
		m_observers.raise(IEntityWorldObserver.class).leaveWorld();
	}

	@Override
	public String getInstanceName()
	{
		return m_name;
	}

	@Override
	public Map<String, Integer> getFlags()
	{
		return new HashMap<>();
	}

	@Override
	public boolean isStatic()
	{
		return true;
	}

	@Override
	public IImmutableSceneModel getModel()
	{
		return new NullSceneModel();
	}

	@Override
	public IPhysicsBody getBody()
	{
		return new NullPhysicsBody();
	}

	@Override
	public IEntityTaskModel getTaskModel()
	{
		return new NullEntityTaskModel();
	}

	@Override
	public IObserverRegistry getObservers()
	{
		return m_observers;
	}

	@Override
	public EntityBridge getBridge()
	{
		return m_bridge;
	}

	@Override
	public void update(int delta) { }

	@Override
	public void dispose()
	{
		m_clip.dispose();
	}
	
	public final class AmbientAudioSourceBridge extends EntityBridge
	{
		public AmbientAudioSourceBridge(IEntity host, IFunctionFactory functionFactory, URI context)
		{
			super(host, functionFactory, context);
		}
		
		public void play()
		{
			m_clip.play();
		}
		
		public void stop()
		{
			m_clip.stop();
		}
		
		public void repeat()
		{
			m_clip.repeat();
		}
		
		public void setVolume(float volume)
		{
			m_clip.setVolume(volume);
		}
	}
}
