package io.github.jevaengine.rpg.entity.character;

import io.github.jevaengine.world.entity.IEntity;

public interface IVisionResolver
{
	IEntity[] getVisibleEntities();
	
	public static final class NullVisionResolver implements IVisionResolver
	{
		@Override
		public IEntity[] getVisibleEntities()
		{
			return new IEntity[0];
		}
	}
}
