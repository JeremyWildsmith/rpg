package io.github.jevaengine.rpg.dialogue;

import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.NullObservers;


public interface IDialogueSpeakerSession
{
	void cancel();
	
	boolean isActive();
	
	IObserverRegistry getObservers();
	
	public interface IDialogueSpeakerSessionObserver
	{
		void listenerSaid(String message);
		void end();
		void eventRaised(int event);
	}
	
	public static final class NullDialogueSpeakerSession implements IDialogueSpeakerSession
	{
		@Override
		public void cancel() { }

		@Override
		public boolean isActive()
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
