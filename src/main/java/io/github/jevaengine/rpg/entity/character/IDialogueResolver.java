/* 
 * Copyright (C) 2015 Jeremy Wildsmith.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package io.github.jevaengine.rpg.entity.character;

import io.github.jevaengine.rpg.dialogue.DialogueSession;
import io.github.jevaengine.rpg.dialogue.DialogueSession.NullDialogueSessionController;
import io.github.jevaengine.rpg.dialogue.IDialogueListenerSession;
import io.github.jevaengine.rpg.dialogue.IDialogueRoute;
import io.github.jevaengine.rpg.dialogue.IDialogueSpeakerSession;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.NullObservers;
import io.github.jevaengine.world.scene.model.IActionSceneModel;

public interface IDialogueResolver extends IRpgCharacterMechanicResolver
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
		
		@Override
		public IActionSceneModel decorate(IActionSceneModel subject)
		{
			return subject;
		}
		
		@Override
		public void update(int deltaTime) { }
	}
}
