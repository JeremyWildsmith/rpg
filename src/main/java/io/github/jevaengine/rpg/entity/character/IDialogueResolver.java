package io.github.jevaengine.rpg.entity.character;

import io.github.jevaengine.rpg.dialogue.DialogueSession;
import io.github.jevaengine.rpg.dialogue.DialogueSession.NullDialogueSessionController;
import io.github.jevaengine.rpg.dialogue.IDialogueListenerSession;
import io.github.jevaengine.rpg.dialogue.IDialogueRoute;
import io.github.jevaengine.rpg.dialogue.IDialogueSpeakerSession;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.NullObservers;

public interface IDialogueResolver
{
	DialogueSession speak(IRpgCharacter listener);
	IDialogueListenerSession listen(IRpgCharacter speaker);
	
	IDialogueListenerSession narrate(IDialogueRoute dialogue);
	IDialogueListenerSession join(DialogueSession session);
	
	IObserverRegistry getObservers();
	
	interface IDialogueResolverObserver
	{
		void speaking(IDialogueSpeakerSession session);
		void listening(IDialogueListenerSession session);
	}
	
	public static final class NullDialogueResolver implements IDialogueResolver
	{
		@Override
		public DialogueSession speak(IRpgCharacter listener)
		{
			return new DialogueSession(new NullDialogueSessionController());
		}

		@Override
		public IDialogueListenerSession listen(IRpgCharacter speaker)
		{
			return new DialogueSession(new NullDialogueSessionController());
		}
		
		@Override
		public IDialogueListenerSession narrate(IDialogueRoute route)
		{
			return new DialogueSession(new NullDialogueSessionController());
		}
		
		@Override
		public IDialogueListenerSession join(DialogueSession session)
		{
			return session;
		}
		
		@Override
		public IObserverRegistry getObservers()
		{
			return new NullObservers();
		}
	}
}
