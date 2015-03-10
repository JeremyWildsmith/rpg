package io.github.jevaengine.rpg.entity.character.tasks;

import io.github.jevaengine.math.Vector2F;
import io.github.jevaengine.rpg.entity.character.IMovementResolver;
import io.github.jevaengine.rpg.entity.character.IMovementResolver.IMovementDirector;
import io.github.jevaengine.rpg.entity.character.IMovementResolver.NullMovementResolver;
import io.github.jevaengine.rpg.entity.character.IRpgCharacter;
import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.entity.IEntity.NullEntity;
import io.github.jevaengine.world.entity.tasks.ITask;
import io.github.jevaengine.world.entity.tasks.InvalidTaskHostException;
import io.github.jevaengine.world.pathfinding.Route;
import io.github.jevaengine.world.steering.AvoidanceBehavior;
import io.github.jevaengine.world.steering.ISteeringBehavior;
import io.github.jevaengine.world.steering.ISteeringBehavior.NullSteeringBehavior;
import io.github.jevaengine.world.steering.SteeringBehaviorList;
import io.github.jevaengine.world.steering.TraverseRouteBehavior;

public final class TraverseRouteTask implements ITask
{
	private IMovementResolver m_movementResolver = new NullMovementResolver();
	private ISteeringBehavior m_steeringBehavior = new NullSteeringBehavior();
	private final IMovementDirector m_movementDirector = new MovementDirector();
	private IEntity m_subject = new NullEntity();
	
	private boolean m_isCancel = false;
	
	public void setRoute(Route route, float arrivalTolorance, float waypointTolorance)
	{
		m_steeringBehavior = new SteeringBehaviorList(new ISteeringBehavior[] {
				new TraverseRouteBehavior(1.0F, route.reduce(), arrivalTolorance, waypointTolorance),
				new AvoidanceBehavior(.1F)	
			});
	}
	
	@Override
	public void begin(IEntity entity)
	{
		if(!(entity instanceof IRpgCharacter))
			throw new InvalidTaskHostException("Task can only be applied to IRpgCharacter instances.");
	
		m_isCancel = false;
		m_subject = entity;
		m_movementResolver = ((IRpgCharacter)entity).getMovementResolver();
		m_movementResolver.queue(m_movementDirector);
	}

	@Override
	public void end() { }

	@Override
	public void cancel()
	{
		m_isCancel = true;
	}

	@Override
	public boolean doCycle(int deltaTime)
	{
		return m_movementDirector.isDone();
	}

	@Override
	public boolean isParallel()
	{
		return false;
	}

	private class MovementDirector implements IMovementDirector
	{

		@Override
		public ISteeringBehavior getBehavior()
		{
			return m_steeringBehavior;
		}

		@Override
		public boolean isDone()
		{
			return m_isCancel || m_steeringBehavior.direct(m_subject.getBody(), new Vector2F()).isZero();
		}
		
	}
}
