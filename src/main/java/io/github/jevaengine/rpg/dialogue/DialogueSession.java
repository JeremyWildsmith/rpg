package io.github.jevaengine.rpg.dialogue;

import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.util.Observers;
import io.github.jevaengine.world.entity.IEntity;

public final class DialogueSession implements IDialogueListenerSession, IDialogueSpeakerSession
{
	private IDialogueSessionController m_dialogueSessionController;

	private Observers m_observers = new Observers();
	private DialogueQuery m_currentQuery;
	
	public DialogueSession(IDialogueSessionController route)
	{
		m_dialogueSessionController = route;
		setQuery(m_dialogueSessionController.getCurrentQuery());
	}
	
	private void setQuery(@Nullable DialogueQuery query)
	{
		if(query == m_currentQuery)
			return;
	
		m_currentQuery = query;

		if(m_currentQuery == null)
		{
			m_observers.raise(IDialogueSpeakerSessionObserver.class).end();
			m_observers.raise(IDialogueListenerSessionObserver.class).end();
		}else
			m_observers.raise(IDialogueListenerSessionObserver.class).speakerInquired(query);
	}
	
	@Override
	public IObserverRegistry getObservers()
	{
		return m_observers;
	}

	@Override
	public void listenerSay(String answer)
	{
		if(m_dialogueSessionController.parseAnswer(answer))
		{
			m_observers.raise(IDialogueSpeakerSessionObserver.class).listenerSaid(answer);
			setQuery(m_dialogueSessionController.getCurrentQuery());	
		}
	}

	@Override
	public boolean isActive()
	{
		return m_currentQuery != null;
	}

	@Override
	public void cancel()
	{
		m_dialogueSessionController.end();
		setQuery(m_dialogueSessionController.getCurrentQuery());
	}

	@Override
	@Nullable
	public DialogueQuery getCurrentQuery()
	{
		return m_currentQuery;
	}
	
	public interface IDialogueSessionController
	{
		@Nullable
		DialogueQuery getCurrentQuery();
		boolean parseAnswer(String answer);
		void end();
	}
	
	public static final class NullDialogueSessionController implements IDialogueSessionController
	{

		@Override
		@Nullable
		public DialogueQuery getCurrentQuery()
		{
			return null;
		}

		@Override
		public boolean parseAnswer(String answer)
		{
			return false;
		}

		@Override
		public void end() { }
	}
	
	public static final class DialogueQuery
	{
		private String m_query;
		private String[] m_answers;
		
		private IEntity m_speaker;
		private IEntity m_listener;
		
		public DialogueQuery(IEntity speaker, IEntity listener, String query, String[] answers)
		{
			m_speaker = speaker;
			m_listener = listener;
			
			m_query = query;
			m_answers = answers;
		}
		
		public String getQuery()
		{
			return m_query;
		}
		
		public String[] getAnswers()
		{
			return m_answers;
		}
		
		public IEntity getListener()
		{
			return m_listener;
		}
		
		public IEntity getSpeaker()
		{
			return m_speaker;
		}
	}
}
