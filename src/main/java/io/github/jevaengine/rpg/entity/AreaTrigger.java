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

import io.github.jevaengine.audio.IAudioClipFactory;
import io.github.jevaengine.math.Rect3F;
import io.github.jevaengine.rpg.entity.character.IRpgCharacter;
import io.github.jevaengine.script.IFunctionFactory;
import io.github.jevaengine.script.IScriptBuilder;
import io.github.jevaengine.script.IScriptBuilder.ScriptConstructionException;
import io.github.jevaengine.script.ScriptEvent;
import io.github.jevaengine.script.ScriptExecuteException;
import io.github.jevaengine.util.IObserverRegistry;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AreaTrigger implements IEntity
{
	private static final int SCAN_INTERVAL = 400;
	
	private final Logger m_logger = LoggerFactory.getLogger(AreaTrigger.class);

	private final Observers m_observers = new Observers();

	private World m_world;
	
	private final String m_name;
	private final String m_searchZone;
	
	private int m_lastScan;	
	private List<IRpgCharacter> m_includedEntities = new ArrayList<>();

	private AreaTriggerBridge m_bridge;

	private IPhysicsBody m_body = new NullPhysicsBody();
	
	public AreaTrigger(IScriptBuilder scriptBuilder, String name, String searchZone)
	{
		m_name = name;
		m_searchZone = searchZone;
		
		m_bridge = new AreaTriggerBridge(scriptBuilder.getFunctionFactory(), scriptBuilder.getUri());
		
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
	
	private void refreshEntities(Rect3F zone)
	{
		IRpgCharacter[] entities = getWorld().getEntities().search(IRpgCharacter.class, new RectangleSearchFilter<IRpgCharacter>(zone.getXy()));

		List<IRpgCharacter> unfoundCharacters = new ArrayList<>(m_includedEntities);

		for (IRpgCharacter character : entities)
		{
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

			Rect3F zone = getWorld().getZones().get(m_searchZone);
			
			if(zone != null)
				refreshEntities(zone);
			else
				m_logger.error(String.format("Respective zone %s for area trigger %s does not exist.", m_searchZone, m_name));
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
		
		public AreaTriggerBridge(IFunctionFactory functionFactory, URI scriptUri)
		{
			super(AreaTrigger.this, functionFactory, scriptUri);
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
