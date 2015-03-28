/* 
 * Copyright (C) 2015 Jeremy Wildsmith.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package io.github.jevaengine.rpg.dialogue;

import io.github.jevaengine.rpg.dialogue.DialogueSession.DialogueQuery;
import io.github.jevaengine.rpg.dialogue.DialogueSession.IDialogueSessionController;
import io.github.jevaengine.world.entity.IEntity;

public final class NullDialogueRoute implements IDialogueRoute
{

	@Override
	public DialogueSession begin(IEntity speaker, IEntity listener) {
		return new DialogueSession(new IDialogueSessionController() {
			
			@Override
			public boolean parseAnswer(String answer)
			{
				return false;
			}
			
			@Override
			public DialogueQuery getCurrentQuery()
			{
				return null;
			}
			
			@Override
			public void end() { }
		});
	}

}
