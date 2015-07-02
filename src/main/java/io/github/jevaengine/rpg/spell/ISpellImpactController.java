package io.github.jevaengine.rpg.spell;

import io.github.jevaengine.world.scene.model.IAnimationSceneModel;
import io.github.jevaengine.world.scene.model.IAnimationSceneModel.NullAnimationSceneModel;

public interface ISpellImpactController extends IImmutableSpellImpactController
{
	void update(int deltaTime);
	
	public static final class NullSpellImpactController implements ISpellImpactController
	{
		@Override
		public boolean isPersisting()
		{
			return false;
		}

		@Override
		public void update(int deltaTime) { }

		@Override
		public IAnimationSceneModel createImpactModel() {
			return new NullAnimationSceneModel();
		}
	}
}
