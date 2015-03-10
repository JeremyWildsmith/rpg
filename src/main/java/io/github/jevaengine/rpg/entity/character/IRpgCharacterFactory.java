package io.github.jevaengine.rpg.entity.character;

import io.github.jevaengine.config.IImmutableVariable;

import java.net.URI;

public interface IRpgCharacterFactory
{
	IRpgCharacter create(String instanceName, URI config, IImmutableVariable auxConfig) throws CharacterCreationException;
	IRpgCharacter create(String instanceName, IImmutableVariable config) throws CharacterCreationException;
	
	public static final class CharacterCreationException extends Exception
	{
		private static final long serialVersionUID = 1L;
	
		public CharacterCreationException(String name, Exception e)
		{
			super("Error constructing character " + name, e);
		}
	}
}
