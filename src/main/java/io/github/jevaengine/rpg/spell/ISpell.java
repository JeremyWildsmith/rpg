package io.github.jevaengine.rpg.spell;

import io.github.jevaengine.graphics.IRenderable;
import io.github.jevaengine.graphics.NullRenderable;
import io.github.jevaengine.rpg.AttributeSet;
import io.github.jevaengine.rpg.IImmutableAttributeSet;
import io.github.jevaengine.rpg.entity.character.IRpgCharacter;
import io.github.jevaengine.rpg.spell.ISpellImpactController.NullSpellImpactController;
import io.github.jevaengine.world.scene.model.IAnimationSceneModel;
import io.github.jevaengine.world.scene.model.IAnimationSceneModel.NullAnimationSceneModel;

public interface ISpell
{
	ISpellImpactController createTargetImpact(IRpgCharacter caster, IRpgCharacter target);
	ISpellImpactController createCasterImpact(IRpgCharacter caster, IRpgCharacter target, AttributeSet casterAttributes);

	String getName();
	String getDescription();
	IRenderable getIcon();
	
	IAnimationSceneModel createCastModel();
	
	SpellUseAbilityTestResults testUseAbility(IImmutableAttributeSet user, IRpgCharacter caster, IRpgCharacter target);
	
	public static final class SpellUseAbilityTestResults
	{
		private final boolean m_isUseable;
		private final String m_message;
		
		public SpellUseAbilityTestResults(boolean isUseable)
		{
			this(isUseable, "");
		}
		
		public SpellUseAbilityTestResults(boolean isUseable, String message)
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
	
	public static final class NullSpell implements ISpell
	{
		@Override
		public String getName()
		{
			return "";
		}

		@Override
		public String getDescription()
		{
			return "";
		}
		
		@Override
		public SpellUseAbilityTestResults testUseAbility(IImmutableAttributeSet user, IRpgCharacter caster, IRpgCharacter target)
		{
			return new SpellUseAbilityTestResults(false);
		}

		@Override
		public IRenderable getIcon() {
			return new NullRenderable();
		}

		@Override
		public IAnimationSceneModel createCastModel() {
			return new NullAnimationSceneModel();
		}

		@Override
		public ISpellImpactController createTargetImpact(IRpgCharacter caster, IRpgCharacter target) {
			return new NullSpellImpactController();
		}

		@Override
		public ISpellImpactController createCasterImpact(IRpgCharacter caster, IRpgCharacter target, AttributeSet casterAttributes) {
			return new NullSpellImpactController();
		}
	}
}
