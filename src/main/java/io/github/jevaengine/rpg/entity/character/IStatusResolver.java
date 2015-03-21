/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.jevaengine.rpg.entity.character;

import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.NullObservers;
import io.github.jevaengine.util.Observers;

/**
 *
 * @author Jeremy
 */
public interface IStatusResolver
{
	boolean isDead();
	
	IObserverRegistry getObservers();
	
	public interface IStatusResolverObserver
	{
		void died();
		void revived();
	}
	
	public static final class NullStatusResolver implements IStatusResolver
	{
		@Override
		public boolean isDead()
		{
			return false;
		}
		
		@Override
		public IObserverRegistry getObservers()
		{
			return new NullObservers();
		}
	}
}
