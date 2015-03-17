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
package io.github.jevaengine.rpg.entity;

import io.github.jevaengine.audio.IAudioClipFactory;
import io.github.jevaengine.math.Rect2F;
import io.github.jevaengine.math.Vector2F;
import io.github.jevaengine.rpg.entity.character.IRpgCharacter;
import io.github.jevaengine.script.IFunctionFactory;
import io.github.jevaengine.script.IScriptBuilder;
import io.github.jevaengine.script.IScriptBuilder.ScriptConstructionException;
import io.github.jevaengine.script.ScriptEvent;
import io.github.jevaengine.script.ScriptExecuteException;
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
import io.github.jevaengine.world.scene.model.IImmutableSceneModel;
import io.github.jevaengine.world.scene.model.NullSceneModel;
import io.github.jevaengine.world.search.RectangleSearchFilter;
import java.net.URI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AreaTrigger implements IEntity
{
	private static final int SCAN_INTERVAL = 400;
	
	private final Logger m_logger = LoggerFactory.getLogger(AreaTrigger.class);

	private final Observers m_observers = new Observers();

	private World m_world;
	
	private final String m_name;
	
	private int m_lastScan;	
	private List<IRpgCharacter> m_includedEntities = new ArrayList<>();

	private final float m_width;
	private final float m_height;
	
	private AreaTriggerBridge m_bridge;

	private IPhysicsBody m_body = new NullPhysicsBody();
	
	public AreaTrigger(IAudioClipFactory audioClipFactory, IScriptBuilder scriptBuilder, String name, float width, float height)
	{
		m_name = name;
		
		m_width = width;
		m_height = height;
		
		m_bridge = new AreaTriggerBridge(audioClipFactory, scriptBuilder.getFunctionFactory(), scriptBuilder.getUri());
		
		try {
			scriptBuilder.create(m_bridge);
		} catch (ScriptConstructionException e) {
			m_logger.error("Unable to instantiate behavior for entity " + name + " defaulting to null behavior.", e);
		}

		m_observers.add(new BridgeNotifier());
	}

	@Override
	public void dispose()
	{
		if(m_world != null)
			m_world.removeEntity(this);
		
		m_includedEntities.clear();
		m_observers.clear();
	}
	
	private Rect2F getContainingBounds()
	{
		Vector2F location = getBody().getLocation().getXy();
		
		//The bounds for an area trigger are meant to be interpreted as containing
		//whole tiles. I.e, a width of 1, and height of 1, located at 0,0 should contain the tile 0,0.
		//The way the engine interprets a rect located at 0,0 with a width 1 and height 1 is to have it start
		//from the origin of 0,0 (the center of the tile at 0,0). In this case, we want it to start
		//from the corner, thus containing the entire tile...

		return new Rect2F(location.x - 0.5F, location.y - 0.5F, m_width, m_height);
	}
	
	@Override
	public String getInstanceName()
	{
		return m_name;
	}
	
	@Override
	public World getWorld()
	{
		return m_world;
	}

	@Override
	public void associate(World world)
	{
		if (m_world != null)
			throw new WorldAssociationException("Already associated with world");

		m_world = world;

		constructPhysicsBody();
		m_observers.raise(IEntityWorldObserver.class).enterWorld();
	}

	@Override
	public void disassociate()
	{
		if (m_world == null)
			throw new WorldAssociationException("Not associated with world");

		m_observers.raise(IEntityWorldObserver.class).leaveWorld();
		destroyPhysicsBody();

		m_world = null;	
	}

	private void constructPhysicsBody()
	{
		m_body = new NonparticipantPhysicsBody(this);
	}
	
	private void destroyPhysicsBody()
	{
		m_body.destory();
		m_body = new NullPhysicsBody();
	}
	
	@Override
	public boolean isStatic()
	{
		return false;
	}
	
	@Override
	public Map<String, Integer> getFlags()
	{
		return new HashMap<>();
	}

	@Override
	public IImmutableSceneModel getModel()
	{
		return new NullSceneModel();
	}

	@Override
	public IPhysicsBody getBody()
	{
		return m_body;
	}

	@Override
	public IObserverRegistry getObservers()
	{
		return m_observers;
	}
	
	@Override
	public void update(int deltaTime)
	{
		m_lastScan -= deltaTime;

		if (m_lastScan <= 0)
		{
			m_lastScan = SCAN_INTERVAL;

			IEntity[] entities = getWorld().getEntities().search(new RectangleSearchFilter<IEntity>(getContainingBounds()));

			List<IRpgCharacter> unfoundCharacters = new ArrayList<>(m_includedEntities);

			for (IEntity entity : entities)
			{
				if (!(entity instanceof IRpgCharacter))
					continue;

				IRpgCharacter character = (IRpgCharacter)entity;

				if (!unfoundCharacters.contains(character))
				{
					m_includedEntities.add(character);
					m_observers.raise(IAreaTriggerAreaObserver.class).enter(character);
					character.getObservers().add(new TriggerCharacterObserver(character));
				} else
				{
					unfoundCharacters.remove(character);
				}
			}

			for (IRpgCharacter character : unfoundCharacters)
			{
				m_includedEntities.remove(character);
				m_observers.raise(IAreaTriggerAreaObserver.class).leave(character);
			}
		}
	}

	@Override
	public EntityBridge getBridge()
	{
		return m_bridge;
	}
	
	private final class BridgeNotifier implements IAreaTriggerAreaObserver
	{
		@Override
		public void enter(IRpgCharacter character)
		{
			try
			{
				m_bridge.onAreaEnter.fire(character.getBridge());
			}catch(ScriptExecuteException e)
			{
				m_logger.error("onAreaEnter delegate failed on entity " + getInstanceName(), e);
			}
		}

		@Override
		public void leave(IRpgCharacter character)
		{
			try
			{
				m_bridge.onAreaLeave.fire(character.getBridge());
			}catch(ScriptExecuteException e)
			{
				m_logger.error("onAreaLeave delegate failed on entity " + getInstanceName(), e);
			}	
		}
	}
	
	public interface IAreaTriggerAreaObserver
	{	
		void enter(IRpgCharacter character);
		void leave(IRpgCharacter character);
	}
	
	@Override
	public IEntityTaskModel getTaskModel()
	{
		return new NullEntityTaskModel();
	}
	
	public class AreaTriggerBridge extends EntityBridge
	{
		public final ScriptEvent onAreaEnter;
		public final ScriptEvent onAreaLeave;
		
		public AreaTriggerBridge(IAudioClipFactory audioClipFactory, IFunctionFactory functionFactory, URI scriptUri)
		{
			super(AreaTrigger.this, audioClipFactory, functionFactory, scriptUri);
			onAreaEnter = new ScriptEvent(functionFactory);
			onAreaLeave = new ScriptEvent(functionFactory);
		}
	}

	private class TriggerCharacterObserver implements IEntityWorldObserver
	{
		private IRpgCharacter m_observee;

		public TriggerCharacterObserver(IRpgCharacter observee)
		{
			m_observee = observee;
		}

		@Override
		public void leaveWorld()
		{
			m_includedEntities.remove(m_observee);
			m_observee.getObservers().remove(this);
		}

		@Override
		public void enterWorld() { }
	}
}
