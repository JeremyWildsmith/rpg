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
package io.github.jevaengine.rpg.dialogue;

import io.github.jevaengine.rpg.dialogue.DialogueSession.DialogueQuery;
import io.github.jevaengine.rpg.dialogue.DialogueSession.IDialogueSessionController;
import io.github.jevaengine.script.IScript;
import io.github.jevaengine.script.IScriptBuilder;
import io.github.jevaengine.script.IScriptBuilder.ScriptConstructionException;
import io.github.jevaengine.script.NullScript;
import io.github.jevaengine.script.ScriptExecuteException;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.entity.IEntity.EntityBridge;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptedDialogueRoute implements IDialogueRoute
{
	private final List<ScriptedDialogueQuery> m_rootQueries;
	
	private final IScriptBuilder m_scriptFactory;
	
	private Logger m_logger = LoggerFactory.getLogger(ScriptedDialogueRoute.class);
	
	public ScriptedDialogueRoute(IScriptBuilder scriptFactory, ScriptedDialogueQuery[] rootQueries)
	{
		m_scriptFactory = scriptFactory;
		m_rootQueries = Arrays.asList(rootQueries);
	}
	
	@Override
	public DialogueSession begin(IEntity speaker, IEntity listener)
	{
		return new DialogueSession(new ScriptedDialogueSessionController(speaker, listener));
	}
	
	public final class ScriptedDialogueSessionController implements IDialogueSessionController
	{
		private IEntity m_speaker;
		private IEntity m_listener;
		
		@Nullable
		private ScriptedDialogueQuery m_currentQuery;

		private IScript m_script = new NullScript();
		
		public ScriptedDialogueSessionController(IEntity speaker, IEntity listener)
		{
			m_speaker = speaker;
			m_listener = listener;
			
			try
			{
				m_script = m_scriptFactory.create(new DialogueBridge());
			} catch (ScriptConstructionException e)
			{
				m_logger.error("Error instantiating dialogue script, using null script instead.");
			}
	
			m_currentQuery = findInitialQuery(speaker, listener);
		}
		
		@Nullable
		private ScriptedDialogueQuery findInitialQuery(IEntity speaker, IEntity listener)
		{
			try
			{	
				for(ScriptedDialogueQuery q : m_rootQueries)
				{
					if(q.evaluate(m_script))
						return q;
				}
			} catch (ScriptExecuteException e)
			{
				m_logger.error("Error testing for initial query. Assuming none were found.", e);
			}
			
			return null;
		}
		
		@Override
		@Nullable
		public DialogueQuery getCurrentQuery()
		{
			if(m_currentQuery == null)
				return null;
			
			List<String> answers = new ArrayList<String>();
			
			try
			{
				for(ScriptedDialogueAnswer a : m_currentQuery.getAnswers(m_script))
					answers.add(a.getAnswer());
			} catch (ScriptExecuteException e) {
				m_logger.error("Error getting dialogue answers. Assuming no answers.", e);
			}
			
			return new DialogueQuery(m_speaker, m_listener, m_currentQuery.getQuery(), answers.toArray(new String[answers.size()]));
		}
		
		@Override
		public boolean parseAnswer(String message)
		{
			if(m_currentQuery == null)
				return false;
			
			ScriptedDialogueAnswer answer = null;
				
			try
			{
				for(ScriptedDialogueAnswer a : m_currentQuery.getAnswers(m_script))
				{
					if(a.getAnswer().equals(message))
						answer = a;
				}
			} catch (ScriptExecuteException e) {
				m_logger.error("Error getting dialogue answers. Assuming no answers.", e);
			}
			
			if(answer == null)
				return false;
			
			try {
				answer.execute(m_script);
			} catch (ScriptExecuteException e) {
				m_logger.error("Error occured invoking answers exec.", e);
			}
			
			if(answer.isEndOfDialogue())
				end();
			else
			{
				try
				{
					m_currentQuery = answer.getNextQuery(m_script);
				} catch (ScriptExecuteException e)
				{
					m_logger.error("Error evaluating for next query, assuming end of dialogue.", e);
					end();
				}
			}
			
			return true;
		}
		
		@Override
		public void end()
		{
			m_speaker = null;
			m_listener = null;
			m_currentQuery = null;
		}
	
		public class DialogueBridge
		{	
			public EntityBridge getSpeaker()
			{
				return m_speaker.getBridge();
			}
			
			public EntityBridge getListener()
			{
				return m_listener.getBridge();
			}
		}
	}
	
	public static final class ScriptedDialogueAnswer
	{
		private String m_answer;
		private List<ScriptedDialogueQuery> m_queries = new ArrayList<>();
		
		@Nullable
		private String m_eval;
		
		@Nullable String m_exec;
		
		public ScriptedDialogueAnswer(String answer, @Nullable String eval, @Nullable String exec)
		{
			m_answer = answer;
			m_eval = eval;
			m_exec = exec;
		}
		
		public void execute(IScript environment) throws ScriptExecuteException
		{
			if(m_exec != null)
				environment.evaluate(m_exec);
		}
		
		public boolean evaluate(IScript testEnvironment) throws ScriptExecuteException
		{
			if(m_eval == null)
				return true;
			else
			{
				Object oResult = testEnvironment.evaluate(m_eval);
				
				if(oResult instanceof Boolean)
					return (Boolean)oResult;
				else
					return false;
			}
		}
		
		public String getAnswer()
		{
			return m_answer;
		}
		
		public boolean isEndOfDialogue()
		{
			return m_queries.size() == 0;
		}
		
		@Nullable
		public ScriptedDialogueQuery getNextQuery(IScript testEnvironment) throws ScriptExecuteException
		{
			for(ScriptedDialogueQuery q : m_queries)
				if(q.evaluate(testEnvironment))
					return q;
			
			return null;
		}
		
		public void addQuery(ScriptedDialogueQuery query)
		{
			m_queries.add(query);
		}
	}
	
	public static final class ScriptedDialogueQuery
	{
		private String m_eval;
		private String m_query;
		private ScriptedDialogueAnswer[] m_answers;
		
		public ScriptedDialogueQuery(String query, @Nullable String eval, ScriptedDialogueAnswer[] answers)
		{
			m_eval = eval;
			m_query = query;
			m_answers = answers;
		}
		
		public ScriptedDialogueQuery(String query, ScriptedDialogueAnswer[] answers)
		{
			m_query = query;
			m_answers = answers;
		}
		
		public String getQuery()
		{
			return m_query;
		}
		
		public ScriptedDialogueAnswer[] getAnswers(IScript testEnvironment) throws ScriptExecuteException
		{
			ArrayList<ScriptedDialogueAnswer> answers = new ArrayList<>();
			
			for(ScriptedDialogueAnswer a : m_answers)
			{
				if(a.evaluate(testEnvironment))
					answers.add(a);
			}
					
			return answers.toArray(new ScriptedDialogueAnswer[answers.size()]);
		}
		
		public boolean evaluate(IScript testEnvironment) throws ScriptExecuteException
		{
			if(m_eval == null)
				return true;
			else
			{
				Object oResult = testEnvironment.evaluate(m_eval);
				
				if(oResult instanceof Boolean)
					return (Boolean)oResult;
				else
					return false;
			}
		}
	}
}
