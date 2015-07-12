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

import io.github.jevaengine.rpg.entity.character.IAllegianceResolver.NullAllegianceResolver;
import io.github.jevaengine.rpg.entity.character.ICombatResolver.NullCombatResolver;
import io.github.jevaengine.rpg.entity.character.IDialogueResolver.NullDialogueResolver;
import io.github.jevaengine.rpg.entity.character.IMovementResolver.NullMovementResolver;
import io.github.jevaengine.rpg.entity.character.ISpellCastResolver.NullSpellCastResolver;
import io.github.jevaengine.rpg.entity.character.IStatusResolver.NullStatusResolver;
import io.github.jevaengine.rpg.entity.character.IVisionResolver.NullVisionResolver;
import io.github.jevaengine.rpg.item.IItemStore;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.NullObservers;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.world.World;
import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.entity.IEntityTaskModel;
import io.github.jevaengine.world.entity.NullEntityTaskModel;
import io.github.jevaengine.world.entity.WorldAssociationException;
import io.github.jevaengine.world.physics.IPhysicsBody;
import io.github.jevaengine.world.physics.NullPhysicsBody;
import io.github.jevaengine.world.scene.model.IImmutableSceneModel;
import io.github.jevaengine.world.scene.model.NullSceneModel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public interface IRpgCharacter extends IEntity
{	
	IItemStore getInventory();
	ILoadout getLoadout();
	
	IStatusResolver getStatusResolver();
	ICombatResolver getCombatResolver();
	ISpellCastResolver getSpellCastResolver();
	IDialogueResolver getDialogueResolver();
	IMovementResolver getMovementResolver();
	IVisionResolver getVisionResolver();
	IAllegianceResolver getAllegianceResolver();
	
	public static final class NullRpgCharacter implements IRpgCharacter
	{
		private static final AtomicInteger INSTANCE_COUNT = new AtomicInteger();
		
		private final String m_name = getClass().getName() + INSTANCE_COUNT.getAndIncrement();
		
		private World m_world;
		
		private final EntityBridge m_bridge;

		public NullRpgCharacter()
		{
			m_bridge = new EntityBridge(this);
		}
		
		@Override
		public void dispose()
		{
			if(m_world != null)
				disassociate();
		}
		
		@Override
		@Nullable
		public World getWorld()
		{
			return m_world;
		}

		@Override
		public void associate(World world)
		{
			if(m_world != null)
				throw new WorldAssociationException("Entity already associated to world.");
			
			m_world = world;
			
		}

		@Override
		public void disassociate()
		{
			if(m_world == null)
				throw new WorldAssociationException("Entity has not been associated to a world.");
			
			m_world = null;
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
			return new NullObservers();
		}

		@Override
		public EntityBridge getBridge()
		{
			return m_bridge;
		}

		@Override
		public void update(int delta) { }

		@Override
		public IItemStore getInventory()
		{
			return new DefaultInventory(0);
		}

		@Override
		public ILoadout getLoadout()
		{
			return new DefaultLoadout();
		}

		@Override
		public IStatusResolver getStatusResolver()
		{
			return new NullStatusResolver();
		}
		
		@Override
		public ICombatResolver getCombatResolver()
		{
			return new NullCombatResolver();
		}

		@Override
		public ISpellCastResolver getSpellCastResolver()
		{
			return new NullSpellCastResolver();
		}
		
		@Override
		public IDialogueResolver getDialogueResolver()
		{
			return new NullDialogueResolver();
		}

		@Override
		public IMovementResolver getMovementResolver()
		{
			return new NullMovementResolver();
		}

		@Override
		public IVisionResolver getVisionResolver()
		{
			return new NullVisionResolver();
		}
		
		@Override
		public IAllegianceResolver getAllegianceResolver()
		{
			return new NullAllegianceResolver();
		}
	}
}
