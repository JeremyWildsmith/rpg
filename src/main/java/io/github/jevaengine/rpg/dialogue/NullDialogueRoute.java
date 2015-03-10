package io.github.jevaengine.rpg.dialogue;

import io.github.jevaengine.rpg.dialogue.DialogueSession.DialogueQuery;
import io.github.jevaengine.rpg.dialogue.DialogueSession.IDialogueSessionController;
import io.github.jevaengine.world.entity.IEntity;

public final class NullDialogueRoute implements IDialogueRoute
{

	@Override
	public DialogueSession begin(IEntity speaker, IEntity listener) {
		return new DialogueSession(new IDialogueSessionController() {
			
			@Override
			public boolean parseAnswer(String answer)
			{
				return false;
			}
			
			@Override
			public DialogueQuery getCurrentQuery()
			{
				return null;
			}
			
			@Override
			public void end() { }
		});
	}

}
