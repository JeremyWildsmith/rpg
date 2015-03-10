package io.github.jevaengine.rpg.entity.character.tasks;

import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.entity.tasks.ITask;
import io.github.jevaengine.world.pathfinding.IRouteFactory;
import io.github.jevaengine.world.pathfinding.IRoutingRules;
import io.github.jevaengine.world.pathfinding.Route;

public final class WonderTask implements ITask
{
	private static final float TRAVERSE_TOLORANCE = 0.8F;
	private final TraverseRouteTask m_traverseRouteTask;
	private final IRouteFactory m_routeFactory;
	private final IRoutingRules m_routingRules;
	
	private final int m_wonderRadius;
	
	public WonderTask(IRouteFactory routeFactory, IRoutingRules routingRules, int wonderRadius)
	{
		m_traverseRouteTask = new TraverseRouteTask();
		m_routeFactory = routeFactory;
		m_routingRules = routingRules;
		m_wonderRadius = wonderRadius;
	}
	
	@Override
	public void begin(IEntity entity)
	{
		Route route = m_routeFactory.create(m_routingRules, entity.getWorld(), entity.getBody().getLocation().getXy(), m_wonderRadius);

		m_traverseRouteTask.setRoute(route, TRAVERSE_TOLORANCE, TRAVERSE_TOLORANCE);
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
