package io.github.jevaengine.rpg.spell;

import io.github.jevaengine.world.scene.model.IAnimationSceneModel;

public interface IImmutableSpellImpactController
{
	boolean isPersisting();
	IAnimationSceneModel createImpactModel();
}
