package io.github.jevaengine.rpg.entity.character;

public interface IAllegianceResolver
{
	boolean isConflictingAllegiance(IRpgCharacter subject);
	
	public static final class NullAllegianceResolver implements IAllegianceResolver
	{
		@Override
		public boolean isConflictingAllegiance(IRpgCharacter subject)
		{
			return false;
		}
	}
}
