package io.github.jevaengine.rpg.entity.character;

import io.github.jevaengine.rpg.AttributeSet;
import io.github.jevaengine.world.scene.model.IActionSceneModel;

public interface IVisionResolverFactory
{
	IVisionResolver create(IRpgCharacter host, AttributeSet attributes, IActionSceneModel model);
}
