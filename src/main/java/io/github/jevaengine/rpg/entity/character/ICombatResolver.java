package io.github.jevaengine.rpg.entity.character;

import io.github.jevaengine.rpg.IImmutableAttributeSet;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.NullObservers;

public interface ICombatResolver
{
	void attack(IRpgCharacter reciever, IAttackObserver observer);
	void attack(IRpgCharacter reciever);
	void recieve(IRpgCharacter attacker, IImmutableAttributeSet impact);
	AttackAbilityTestResults testAttackAbility(IRpgCharacter reciever);
	
	IObserverRegistry getObservers();
	
	interface ICombatResolverObserver
	{
		void attacked(IRpgCharacter target);
		void attackedBy(IRpgCharacter attacker);
	}
	
	interface IAttackObserver
	{
		void begin();
		void performed(IImmutableAttributeSet impact);
		void end();
	}
	
	public static final class NullAttackObserver implements IAttackObserver
	{

		@Override
		public void begin() { }

		@Override
		public void performed(IImmutableAttributeSet impact) { }

		@Override
		public void end() { }	
	}
	
	public static final class NullCombatResolver implements ICombatResolver
	{
		@Override
		public void attack(IRpgCharacter reciever) { }

		@Override
		public void attack(IRpgCharacter reciever, IAttackObserver o) { }

		@Override
		public void recieve(IRpgCharacter attacker, IImmutableAttributeSet impact) { }
	
		@Override
		public AttackAbilityTestResults testAttackAbility(IRpgCharacter reciever)
		{
			return new AttackAbilityTestResults(false, "Cannot attack with a null combat resolver");
		}
		
		@Override
		public IObserverRegistry getObservers()
		{
			return new NullObservers();
		}
	}
	
	public static final class AttackAbilityTestResults
	{
		private final String m_message;
		private final boolean m_canAttack;
		
		public AttackAbilityTestResults(boolean canAttack)
		{
			this(canAttack, "");
		}
		
		public AttackAbilityTestResults(boolean canAttack, String message)
		{
			m_message = message;
			m_canAttack = canAttack;
		}
	
		public String getMessage()
		{
			return m_message;
		}
		
		public boolean canAttack()
		{
			return m_canAttack;
		}
	}
}
