package io.github.jevaengine.rpg.spell;

import java.net.URI;

public interface ISpellFactory
{
	ISpell create(URI name) throws SpellConstructionException;
	
	public static final class SpellConstructionException extends Exception
	{
		public SpellConstructionException(URI assetName, Exception cause)
		{
			super("Error constructing item " + assetName.toString(), cause);
		}
	}
}
