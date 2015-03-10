package io.github.jevaengine.rpg.dialogue;

import io.github.jevaengine.rpg.dialogue.DialogueSession.DialogueQuery;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.NullObservers;
import io.github.jevaengine.util.Nullable;

public interface IDialogueListenerSession
{
	void listenerSay(String answer);
	void cancel();
	
	boolean isActive();
	
	@Nullable
	DialogueQuery getCurrentQuery();
	
	IObserverRegistry getObservers();
	
	public interface IDialogueListenerSessionObserver
	{
		void speakerInquired(DialogueQuery query);
		void end();
	}
	
	public static final class NullDialogueListenerSession implements IDialogueListenerSession
	{
		@Override
		public void listenerSay(String answer) { }

		@Override
		public void cancel() { }

		@Override
		public boolean isActive()
		{
			return false;
		}

		@Override
		public DialogueQuery getCurrentQuery()
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
