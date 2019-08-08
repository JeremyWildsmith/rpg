package io.github.jevaengine.rpg.dialogue;

import io.github.jevaengine.rpg.AttributeSet;
import io.github.jevaengine.rpg.entity.character.IDialogueResolver;
import io.github.jevaengine.rpg.entity.character.IDialogueResolverFactory;
import io.github.jevaengine.rpg.entity.character.IRpgCharacter;
import io.github.jevaengine.world.scene.model.IActionSceneModel;

public class NullDialogueResolverFactory implements IDialogueResolverFactory {
    @Override
    public IDialogueResolver create(IRpgCharacter host, AttributeSet attributes, IActionSceneModel model) {
        return new IDialogueResolver.NullDialogueResolver();
    }
}
