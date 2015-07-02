package io.github.jevaengine.rpg.entity.character;

import io.github.jevaengine.rpg.AttributeSet;
import io.github.jevaengine.world.scene.model.IActionSceneModel;

public interface ISpellCastResolverFactory
{
	ISpellCastResolver create(IRpgCharacter host, AttributeSet attributes, IActionSceneModel model);
}
