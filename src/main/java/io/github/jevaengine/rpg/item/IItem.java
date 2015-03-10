package io.github.jevaengine.rpg.item;

import io.github.jevaengine.graphics.IRenderable;
import io.github.jevaengine.rpg.AttributeSet;
import io.github.jevaengine.rpg.IImmutableAttributeSet;
import io.github.jevaengine.rpg.entity.character.IRpgCharacter;
import io.github.jevaengine.world.scene.model.IAnimationSceneModel;


public interface IItem
{
	String getName();
	String getDescription();
	IItemFunction getFunction();

	AttributeSet getAttributes();

	IRenderable getIcon();
	IAnimationSceneModel createModel();
	
	public interface IItemFunction
	{
		IWieldTarget getWieldTarget();
		String getName();
		IImmutableAttributeSet use(IRpgCharacter user, IRpgCharacter target, AttributeSet item);
		ItemUseAbilityTestResults testUseAbility(IRpgCharacter user, IRpgCharacter target, IImmutableAttributeSet item);
	}
	
	public static final class ItemUseAbilityTestResults
	{
		private final boolean m_isUseable;
		private final String m_message;
		
		public ItemUseAbilityTestResults(boolean isUseable)
		{
			this(isUseable, "");
		}
		
		public ItemUseAbilityTestResults(boolean isUseable, String message)
		{
			m_isUseable = isUseable;
			m_message = message;
		}
		
		public boolean isUseable()
		{
			return m_isUseable;
		}
		
		public String getMessage()
		{
			return m_message;
		}
	}
	
	public interface IWieldTarget
	{
		String getName();
	}

	public static final class NullWieldTarget implements IWieldTarget
	{
		@Override
		public String getName()
		{
			return "null";
		}	
	}
	
	public static final class NullItemFunction implements IItemFunction
	{
		@Override
		public IWieldTarget getWieldTarget()
		{
			return new NullWieldTarget();
		}

		@Override
		public String getName()
		{
			return "null";
		}

		@Override
		public ItemUseAbilityTestResults testUseAbility(IRpgCharacter user, IRpgCharacter target, IImmutableAttributeSet item)
		{
			return new ItemUseAbilityTestResults(false, "Item will null function cannot be used.");
		}
		
		@Override
		public IImmutableAttributeSet use(IRpgCharacter user, IRpgCharacter target, AttributeSet item)
		{
			return new AttributeSet();
		}
	}
}
