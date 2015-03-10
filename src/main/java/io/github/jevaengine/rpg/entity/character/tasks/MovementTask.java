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
	private final float m_waypointTolorance;
	
	private final int m_maxSteps;
	
	public MovementTask(IRouteFactory routeFactory, IRoutingRules routingRules, Vector2F destination, float arrivalTolorance, float waypointTolorance, int maxSteps)
	{
		m_traverseRouteTask = new TraverseRouteTask();
		m_routeFactory = routeFactory;
		m_routingRules = routingRules;
		m_destination = new Vector2F(destination);
		m_arrivalTolorance = arrivalTolorance;
		m_waypointTolorance = waypointTolorance;
		m_maxSteps = maxSteps;
	}
	
	@Override
	public void begin(IEntity entity)
	{
		Route route = new Route();
		
		try
		{
			route = m_routeFactory.create(m_routingRules, entity.getWorld(), entity.getBody().getLocation().getXy(), m_destination, m_arrivalTolorance);
			route.truncate(m_maxSteps);
		} catch (IncompleteRouteException e) {
			m_logger.error(String.format("Unable to constuct path to %f, %f for entity %s.", m_destination.x, m_destination.y, entity.getInstanceName()));
		}
		
		m_traverseRouteTask.setRoute(route, m_arrivalTolorance, m_waypointTolorance);
		
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
