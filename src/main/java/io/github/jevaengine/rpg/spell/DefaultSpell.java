package io.github.jevaengine.rpg.spell;

import io.github.jevaengine.graphics.IRenderable;
import io.github.jevaengine.rpg.AttributeSet;
import io.github.jevaengine.rpg.IImmutableAttributeSet;
import io.github.jevaengine.rpg.entity.character.IRpgCharacter;
import io.github.jevaengine.world.scene.model.IAnimationSceneModel;
import io.github.jevaengine.world.scene.model.IAnimationSceneModel.NullAnimationSceneModel;

public final class DefaultSpell implements ISpell
{
	private final IAnimationSceneModel m_castModel;
	private final IAnimationSceneModel m_impactModel;
	private final IRenderable m_icon;
	private final String m_name;
	private final String m_description;
	private final IImmutableAttributeSet m_targetImpact;
	private final IImmutableAttributeSet m_casterImpact;
	
	private final float m_range;
	private final float m_impactProbability;
	private final int m_impactPeriod;
	private final int m_duration;
	
	public DefaultSpell(IAnimationSceneModel castModel, IAnimationSceneModel impactModel, IRenderable icon, String name, String description, float range, float impactProbability, int impactPeriod, int duration, IImmutableAttributeSet casterImpact, IImmutableAttributeSet targetImpact)
	{
		m_castModel = castModel;
		m_impactModel = impactModel;
		m_icon = icon;
		m_name = name;
		m_description = description;
		
		m_range = range;
		m_impactPeriod = impactPeriod;
		m_impactProbability = impactProbability;
		m_duration = duration;
		m_targetImpact = targetImpact;
		m_casterImpact = casterImpact;
	}
	
	@Override
	public ISpellImpactController createTargetImpact(final IRpgCharacter caster, final IRpgCharacter target)
	{
		return new ISpellImpactController() {
			int timeSinceImpact = 0;
			int life = m_duration;
			
			@Override
			public void update(int deltaTime)
			{
				life -= deltaTime;
				
				if(life <= 0)
					return;
				
				timeSinceImpact += deltaTime;
				int impacts = timeSinceImpact / m_impactPeriod;
				
				if(impacts > 0)
				{
					for(int i = 0; i < impacts; i++)
					{
						if(Math.random() < m_impactProbability)
							target.getSpellCastResolver().recieveImpact(caster, m_targetImpact);
					}
					
					timeSinceImpact -= m_impactPeriod * impacts;
				}
			}

			@Override
			public boolean isPersisting()
			{
				return life > 0;
			}

			@Override
			public IAnimationSceneModel createImpactModel()
			{
				return m_impactModel.clone();
			}
		};
	}
	
	@Override
	public ISpellImpactController createCasterImpact(final IRpgCharacter caster, final IRpgCharacter target, final AttributeSet casterAttributes)
	{
		return new ISpellImpactController() {
			boolean performed = false;
			@Override
			public void update(int deltaTime)
			{
				if(!performed)
				{
					casterAttributes.merge(m_casterImpact);
					performed = true;
				}
			}

			@Override
			public boolean isPersisting() {
				return !performed;
			}

			@Override
			public IAnimationSceneModel createImpactModel() {
				return new NullAnimationSceneModel();
			}
		};
	}
	
	@Override
	public String getName()
	{
		return m_name;
	}

	@Override
	public String getDescription()
	{
		return m_description;
	}

	@Override
	public IRenderable getIcon()
	{
		return m_icon;
	}

	@Override
	public IAnimationSceneModel createCastModel()
	{
		return m_castModel.clone();
	}

	@Override
	public SpellUseAbilityTestResults testUseAbility(IImmutableAttributeSet user, IRpgCharacter caster, IRpgCharacter target)
	{
		float distance = caster.getBody().getLocation().difference(target.getBody().getLocation()).getLength();
		
		return new SpellUseAbilityTestResults(distance < m_range);
	}
}
