package io.github.jevaengine.rpg.dialogue;

import java.net.URI;

public class NullDialogueRouteFactory implements IDialogueRouteFactory {
    @Override
    public IDialogueRoute create(URI name) throws DialogueRouteConstructionException {
        return new NullDialogueRoute();
    }
}
