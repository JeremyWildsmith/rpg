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

import io.github.jevaengine.rpg.AttributeSet;
import io.github.jevaengine.rpg.dialogue.DialogueSession;
import io.github.jevaengine.rpg.dialogue.DialogueSession.DialogueQuery;
import io.github.jevaengine.rpg.dialogue.DialogueSession.NullDialogueSessionController;
import io.github.jevaengine.rpg.dialogue.IDialogueListenerSession;
import io.github.jevaengine.rpg.dialogue.IDialogueRoute;
import io.github.jevaengine.rpg.entity.character.IDialogueResolver;
import io.github.jevaengine.rpg.entity.character.IDialogueResolverFactory;
import io.github.jevaengine.rpg.entity.character.IRpgCharacter;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.Observers;
import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.scene.model.IActionSceneModel;

public final class DefaultDialogueResolverFactory implements IDialogueResolverFactory
{
	private final IDialogueRoute m_dialogueRoute;
	
	public DefaultDialogueResolverFactory(IDialogueRoute route)
	{
		m_dialogueRoute = route;
	}
	
	@Override
	public IDialogueResolver create(IRpgCharacter host, AttributeSet attributes, IActionSceneModel model)
	{
		return new UsrDialogueResolver(host, m_dialogueRoute);
	}

	private static final class UsrDialogueResolver implements IDialogueResolver
	{
		private final Observers m_observers = new Observers();

		private final IRpgCharacter m_subject;
		private final IDialogueRoute m_route;
		
		public UsrDialogueResolver(IRpgCharacter subject, IDialogueRoute route)
		{
			m_subject = subject;
			m_route = route;
		}
		
		private static boolean isWithinConversationDistance(IEntity speaker, IEntity listener)
		{
			if(speaker.getWorld() != listener.getWorld() || speaker.getWorld() == null)
				return false;

			float netRadius = speaker.getBody().getBoundingCircle().radius + listener.getBody().getBoundingCircle().radius;
			float distance = speaker.getBody().getLocation().difference(listener.getBody().getLocation()).getLength() - netRadius;
			
			return (distance < speaker.getBody().getBoundingCircle().radius * 2 &&
								distance < listener.getBody().getBoundingCircle().radius * 2);
		}
		
		@Override
		public DialogueSession speak(IRpgCharacter listener)
		{
			DialogueSession session;
			
			if(!isWithinConversationDistance(m_subject, listener))
				session = new SystemDialogue("I am too far to speak to this person.").begin(m_subject, m_subject);
			else if(m_subject.getStatusResolver().isDead())
				session = new DialogueSession(new NullDialogueSessionController());
			else if(listener.getStatusResolver().isDead())
				session = new SystemDialogue("I cannot speak to a dead corpse.").begin(m_subject, m_subject);
			else
				session = m_route.begin(m_subject, listener);
			
			if(session.isActive())
			{
				m_observers.raise(IDialogueResolverObserver.class).speaking(session);
				listener.getDialogueResolver().join(session);
			}
			return session;
		}
		
		@Override
		public IDialogueListenerSession listen(IRpgCharacter speaker)
		{
			DialogueSession session;
	
			if(!isWithinConversationDistance(m_subject, speaker))
				session = new SystemDialogue("I am too far to engage in conversation with this person.").begin(m_subject, m_subject);
			else if(m_subject.getStatusResolver().isDead())
				session = new DialogueSession(new NullDialogueSessionController());
			else if(speaker.getStatusResolver().isDead())
				session = new SystemDialogue("I cannot speak with a dead corpse.").begin(m_subject, m_subject);
			else
				session = speaker.getDialogueResolver().speak(m_subject);

			if(session.isActive())
				m_observers.raise(IDialogueResolverObserver.class).listening(session);
			
			return session;
		}

		@Override
		public IDialogueListenerSession narrate(IDialogueRoute route)
		{
			return join(route.begin(m_subject, m_subject));
		}
		
		@Override
		public IDialogueListenerSession join(DialogueSession session)
		{
			if(session.isActive())
				m_observers.raise(IDialogueResolverObserver.class).listening(session);
			
			return session;
		}

		@Override
		public IObserverRegistry getObservers()
		{
			return m_observers;
		}

		@Override
		public void update(int deltaTime) { }

		@Override
		public IActionSceneModel decorate(IActionSceneModel subject)
		{
			return subject;
		}
	}
	
	private static final class SystemDialogue implements IDialogueRoute
	{
		private final String m_message;
		
		public SystemDialogue(String message)
		{
			m_message = message;
		}
		
		@Override
		public DialogueSession begin(IEntity speaker, final IEntity listener)
		{
			return new DialogueSession(new DialogueSession.IDialogueSessionController() {
				private boolean isOver = false;
				@Override
				public DialogueQuery getCurrentQuery() {
					if(isOver)
						return null;
					else
						return new DialogueQuery(listener, listener, m_message, new String[] {"Okay"});
				}

				@Override
				public boolean parseAnswer(String answer)
				{
					if(!answer.equals("Okay"))
						return false;
					
					isOver = true;
					
					return true;
				}

				@Override
				public void end() { }
			});
		};
		
	}
}
