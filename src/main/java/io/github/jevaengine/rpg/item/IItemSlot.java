package io.github.jevaengine.rpg.item;

import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.NullObservers;
import io.github.jevaengine.util.Nullable;

public interface IItemSlot extends IImmutableItemSlot
{
	@Nullable
	IItem setItem(IItem item);
	
	@Nullable
	IItem clear();
	
	public static final class NullItemSlot implements IItemSlot
	{

		@Override
		public boolean isEmpty()
		{
			return true;
		}

		@Override
		public IItem getItem()
		{
			return null;
		}

		@Override
		public IItem setItem(IItem item)
		{
			return null;
		}

		@Override
		public IItem clear()
		{
			return null;
		}

		@Override
		public IObserverRegistry getObservers()
		{
			return new NullObservers();
		}
	}
}
