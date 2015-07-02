package io.github.jevaengine.rpg.entity.character;

import io.github.jevaengine.rpg.IImmutableAttributeSet;
import io.github.jevaengine.rpg.spell.IImmutableSpellImpactController;
import io.github.jevaengine.rpg.spell.ISpell;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.Observers;
import io.github.jevaengine.world.scene.model.IActionSceneModel;

public interface ISpellCastResolver extends IRpgCharacterMechanicResolver
{
	void cast(IRpgCharacter reciever, ISpell spell);
	void cast(IRpgCharacter reciever, ISpell spell, ISpellCastObserver o);
	
	void castImpacted(IRpgCharacter summoner, IImmutableSpellImpactController impactController);
	void recieveImpact(IRpgCharacter attacker, IImmutableAttributeSet impact);
	
	CastAbilityTestResults testCastAbility(IRpgCharacter reciever, ISpell spell);
	
	IObserverRegistry getObservers();
	
	interface ISpellCastResolverObserver
	{
		void castSummoned(ISpell spell, IImmutableSpellImpactController casterImpact);
		void castImpacted(IRpgCharacter summoner, IImmutableSpellImpactController impactController);
	}
	
	interface ISpellCastObserver
	{
		void begin();
		void performed(IImmutableSpellImpactController casterImpact, IImmutableSpellImpactController targetImpact);
		void end();
	}
	
	public static final class NullSpellCastObserver implements ISpellCastObserver
	{
		@Override
		public void begin() { }

		@Override
		public void performed(IImmutableSpellImpactController casterImpact, IImmutableSpellImpactController targetImpact) { }

		@Override
		public void end() { }	
	}
	
	public static final class NullSpellCastResolver implements ISpellCastResolver
	{

		@Override
		public IObserverRegistry getObservers()
		{
			return new Observers();
		}
		
		@Override
		public void cast(IRpgCharacter reciever, ISpell spell) { }

		@Override
		public void cast(IRpgCharacter reciever, ISpell spell, ISpellCastObserver o)
		{
			o.begin();
			o.end();
		}

		@Override
		public void recieveImpact(IRpgCharacter attacker, IImmutableAttributeSet impact) { }

		@Override
		public CastAbilityTestResults testCastAbility(IRpgCharacter reciever, ISpell spell)
		{
			return new CastAbilityTestResults(false);
		}

		@Override
		public void update(int deltaTime) { }
		
		@Override
		public IActionSceneModel decorate(IActionSceneModel subject)
		{
			return subject;
		}

		@Override
		public void castImpacted(IRpgCharacter summoner, IImmutableSpellImpactController impactController) { }
	}
	
	public static final class CastAbilityTestResults
	{
		private final String m_message;
		private final boolean m_canCast;
		
		public CastAbilityTestResults(boolean canCast)
		{
			this(canCast, "");
		}
		
		public CastAbilityTestResults(boolean canCast, String message)
		{
			m_message = message;
			m_canCast = canCast;
		}
	
		public String getMessage()
		{
			return m_message;
		}
		
		public boolean canCast()
		{
			return m_canCast;
		}
	}
	
}
