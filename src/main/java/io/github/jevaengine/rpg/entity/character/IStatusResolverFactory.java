/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.jevaengine.rpg.entity.character;

import io.github.jevaengine.rpg.AttributeSet;
import io.github.jevaengine.world.scene.model.IActionSceneModel;

/**
 *
 * @author Jeremy
 */
public interface IStatusResolverFactory
{
	IStatusResolver create(IRpgCharacter host, AttributeSet attributes, IActionSceneModel model);
}
