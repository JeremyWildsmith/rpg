package io.github.jevaengine.rpg.entity.character;

import io.github.jevaengine.world.steering.ISteeringBehavior;

public interface IMovementResolver
{
	void queue(IMovementDirector director);
	void queueTop(IMovementDirector director);
	void dequeue(IMovementDirector director);
	
	void update(int deltaTime);
	
	public interface IMovementDirector
	{
		ISteeringBehavior getBehavior();
		boolean isDone();
	}
	
	public static final class NullMovementResolver implements IMovementResolver
	{
		@Override
		public void queue(IMovementDirector director) { }

		@Override
		public void queueTop(IMovementDirector director) { }

		@Override
		public void dequeue(IMovementDirector director) {}
		
		@Override
		public void update(int deltaTime) { }
	}
}
