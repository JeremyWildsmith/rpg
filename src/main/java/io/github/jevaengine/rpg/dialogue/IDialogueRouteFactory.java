package io.github.jevaengine.rpg.dialogue;

import java.net.URI;

public interface IDialogueRouteFactory
{
	IDialogueRoute create(URI name) throws DialogueRouteConstructionException;
	
	public static final class DialogueRouteConstructionException extends Exception
	{
		private static final long serialVersionUID = 1L;

		public DialogueRouteConstructionException(URI assetName, Exception cause)
		{
			super("Error constructing dialogue route " + assetName.toString(), cause);
		}
		
	}
}
