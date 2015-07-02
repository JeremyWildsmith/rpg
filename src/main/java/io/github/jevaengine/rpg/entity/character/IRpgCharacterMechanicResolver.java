package io.github.jevaengine.rpg.entity.character;

import io.github.jevaengine.world.scene.model.IActionSceneModel;

public interface IRpgCharacterMechanicResolver
{
	void update(int deltaTime);
	IActionSceneModel decorate(IActionSceneModel subject);
}
