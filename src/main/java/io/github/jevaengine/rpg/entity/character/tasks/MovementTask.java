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
import io.github.jevaengine.world.entity.tasks.ITask;
import io.github.jevaengine.world.pathfinding.IRouteFactory;
import io.github.jevaengine.world.pathfinding.IRoutingRules;
import io.github.jevaengine.world.pathfinding.IncompleteRouteException;
import io.github.jevaengine.world.pathfinding.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MovementTask implements ITask
{
	private final Logger m_logger = LoggerFactory.getLogger(MovementTask.class);
	
	private final TraverseRouteTask m_traverseRouteTask;
	private final IRouteFactory m_routeFactory;
	private final IRoutingRules m_routingRules;
	
	private final Vector2F m_destination;
	
	private final float m_arrivalTolorance;
	
	private final int m_maxSteps;
	
	public MovementTask(IRouteFactory routeFactory, IRoutingRules routingRules, Vector2F destination, float arrivalTolorance, int maxSteps)
	{
		m_traverseRouteTask = new TraverseRouteTask();
		m_routeFactory = routeFactory;
		m_routingRules = routingRules;
		m_destination = new Vector2F(destination);
		m_arrivalTolorance = arrivalTolorance;
		m_maxSteps = maxSteps;
	}
	
	@Override
	public void begin(IEntity entity)
	{
		Route route = new Route(m_routingRules);
		
		try
		{
			route = m_routeFactory.create(m_routingRules, entity.getWorld(), entity.getBody().getLocation().getXy(), m_destination, m_arrivalTolorance);
			route.truncate(m_maxSteps);
		} catch (IncompleteRouteException e) {
			m_logger.error(String.format("Unable to constuct path to %f, %f for entity %s.", m_destination.x, m_destination.y, entity.getInstanceName()));
		}
		
		m_traverseRouteTask.setRoute(route, m_arrivalTolorance);
		
		m_traverseRouteTask.begin(entity);
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

	@Override
	public boolean doCycle(int deltaTime)
	{
		return m_traverseRouteTask.doCycle(deltaTime);
	}

	@Override
	public boolean isParallel()
	{
		return m_traverseRouteTask.isParallel();
	}
}
