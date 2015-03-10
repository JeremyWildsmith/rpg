package io.github.jevaengine.rpg.dialogue;

import io.github.jevaengine.world.entity.IEntity;

public interface IDialogueRoute
{
	DialogueSession begin(IEntity speaker, IEntity listener);
}
