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

import io.github.jevaengine.math.Vector2F;
import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.entity.IEntity.NullEntity;
import io.github.jevaengine.world.entity.tasks.ITask;
import io.github.jevaengine.world.pathfinding.IRouteFactory;
import io.github.jevaengine.world.pathfinding.IRoutingRules;
import io.github.jevaengine.world.pathfinding.IncompleteRouteException;
import io.github.jevaengine.world.pathfinding.Route;

import java.lang.ref.WeakReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FollowEntityTask implements ITask
{
	private static final float REFRESH_ROUTE_TARGET_MOVEMENT_DELTA = 0.6F;
	private static final float WAYPOINT_TOLORANCE = 0.8F;
	
	private final Logger m_logger = LoggerFactory.getLogger(MovementTask.class);
	
	private final TraverseRouteTask m_traverseRouteTask;
	private final IRouteFactory m_routeFactory;
	private final IRoutingRules m_routingRules;
	
	private final WeakReference<IEntity> m_target;
	
	private Vector2F m_lastTargetLocation = new Vector2F();
	
	private IEntity m_host = new NullEntity();
	
	public FollowEntityTask(IRouteFactory routeFactory, IRoutingRules routingRules, IEntity target)
	{
		m_traverseRouteTask = new TraverseRouteTask();
		m_routeFactory = routeFactory;
		m_routingRules = routingRules;
		m_target = new WeakReference<>(target);
	}
	
	@Override
	public void begin(IEntity entity)
	{	
		m_host = entity;
		m_traverseRouteTask.begin(entity);
		refreshRoute();
	}

	@Override
	public void end()
	{
		m_traverseRouteTask.end();
	}

	@Override
	public void cancel()
	{
		m_traverseRouteTask.cancel();
	}

	public void refreshRoute()
	{
		Route route = new Route();
		
		float arrivalTolorance = Float.MAX_VALUE;
		
		if(m_target.get() != null)
		{
			arrivalTolorance = m_host.getBody().getBoundingCircle().radius + m_target.get().getBody().getBoundingCircle().radius + 0.01F;
			
			try
			{
				m_lastTargetLocation = m_target.get().getBody().getLocation().getXy();
				route = m_routeFactory.create(m_routingRules, m_host.getWorld(), m_host.getBody().getLocation().getXy(), m_lastTargetLocation, arrivalTolorance);
			} catch (IncompleteRouteException e) {
				m_logger.error(String.format("Unable to constuct path to %s for entity %s.", m_target.get().getInstanceName(), m_host.getInstanceName()));
			}
		}
		
		m_traverseRouteTask.setRoute(route, arrivalTolorance, WAYPOINT_TOLORANCE);
	}
	
	@Override
	public boolean doCycle(int deltaTime)
	{
		if(m_target.get() == null)
			return true;
		
		if(m_lastTargetLocation.difference(m_target.get().getBody().getLocation().getXy()).getLength() > REFRESH_ROUTE_TARGET_MOVEMENT_DELTA)
			refreshRoute();
		
		return m_traverseRouteTask.doCycle(deltaTime);
	}

	@Override
	public boolean isParallel()
	{
		return m_traverseRouteTask.isParallel();
	}
}

