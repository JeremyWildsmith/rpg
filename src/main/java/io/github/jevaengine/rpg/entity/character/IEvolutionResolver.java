package io.github.jevaengine.rpg.entity.character;

import io.github.jevaengine.rpg.IImmutableAttributeSet;
import io.github.jevaengine.util.IObserverRegistry;


public interface IEvolutionResolver
{
	IObserverRegistry getObservers();
	
	public interface IEvolutionObserver
	{
		void evolved(IImmutableAttributeSet delta);
	}
}
