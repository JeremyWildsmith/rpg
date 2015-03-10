package io.github.jevaengine.rpg;

import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.NullObservers;

import java.util.Map;
import java.util.Set;

public interface IImmutableAttributeSet
{
	boolean has(IAttributeIdentifier attribute);
	boolean has(String name);
	
	IImmutableAttribute get(IAttributeIdentifier attribute);
	IImmutableAttribute get(String name);
	
	Set<Map.Entry<IAttributeIdentifier, IImmutableAttribute>> getSet();
	
	public interface IAttributeIdentifier
	{
		String getName();
		String getDescription();
	}
	
	public interface IImmutableAttribute
	{
		boolean isZero();
		float get();
		IObserverRegistry getObservers();
	}
	
	public interface IAttributeChangeObserver
	{
		void changed(float delta);
	}
	
	public interface IAttribute extends IImmutableAttribute
	{
		void set(float value);
	}

	public static final class NullAttribute implements IAttribute
	{
		@Override
		public float get()
		{
			return 0;
		}

		@Override
		public void set(float value) { }
		
		@Override
		public IObserverRegistry getObservers()
		{
			return new NullObservers();
		}

		@Override
		public boolean isZero()
		{
			return true;
		}
	}
}
