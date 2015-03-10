package io.github.jevaengine.rpg.item;

import java.net.URI;

public interface IItemFactory
{
	IItem create(URI name) throws ItemContructionException;
	
	public static final class ItemContructionException extends Exception
	{
		private static final long serialVersionUID = 1L;

		public ItemContructionException(URI assetName, Exception cause)
		{
			super("Error constructing item " + assetName.toString(), cause);
		}
		
	}
}
