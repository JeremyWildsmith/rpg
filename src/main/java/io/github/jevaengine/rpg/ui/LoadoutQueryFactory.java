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
package io.github.jevaengine.rpg.ui;

import io.github.jevaengine.IDisposable;
import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.math.Vector2D;
import io.github.jevaengine.rpg.entity.character.ILoadout;
import io.github.jevaengine.rpg.item.IItem.IWieldTarget;
import io.github.jevaengine.rpg.item.IItemSlot;
import io.github.jevaengine.ui.*;
import io.github.jevaengine.ui.IWindowFactory.WindowConstructionException;
import io.github.jevaengine.util.Observers;

import java.net.URI;

public final class LoadoutQueryFactory {

	private final URI m_inventoryLayout;

	private final WindowManager m_windowManager;
	private final IWindowFactory m_windowFactory;

	public LoadoutQueryFactory(WindowManager windowManager, IWindowFactory windowFactory, URI inventoryLayout) {
		m_windowManager = windowManager;
		m_windowFactory = windowFactory;
		m_inventoryLayout = inventoryLayout;
	}

	public LoadoutQuery create(ILoadout store) throws WindowConstructionException {
		Observers observers = new Observers();
		
		Window window = m_windowFactory.create(m_inventoryLayout, new CharacterLoadoutQueryBehaviourInjector(store, observers));
		m_windowManager.addWindow(window);

		window.center();

		return new LoadoutQuery(window, observers);
	}

	public static class LoadoutQuery implements IDisposable {

		private final Window m_window;
		private final Observers m_observers;

		private LoadoutQuery(Window window, Observers observers) {
			m_window = window;
			m_observers = observers;
		}

		@Override
		public void dispose() {
			m_window.dispose();
		}

		public Observers getObservers() {
			return m_observers;
		}
		
		public void setVisible(boolean isVisible) {
			m_window.setVisible(isVisible);
		}

		public boolean isVisible() {
			return m_window.isVisible();
		}

		public void setLocation(Vector2D location) {
			m_window.setLocation(location);
		}

		public void center() {
			m_window.center();
		}

		public void focus() {
			m_window.focus();
		}

		public void setMovable(boolean isMovable) {
			m_window.setMovable(false);
		}

		public void setTopMost(boolean isTopMost) {
			m_window.setTopMost(isTopMost);
		}

		public Rect2D getBounds() {
			return m_window.getBounds();
		}
	}

	private class CharacterLoadoutQueryBehaviourInjector extends WindowBehaviourInjector {

		private final ILoadout m_loadout;

		private final Observers m_observers;
		
		public CharacterLoadoutQueryBehaviourInjector(ILoadout loadout, final Observers observers) {
			m_loadout = loadout;
			m_observers = observers;
		}

		@Override
		protected void doInject() throws NoSuchControlException {
			for(IWieldTarget t : m_loadout.getWieldTargets()) {
				if(!hasControl(ItemContainer.class, t.getName()))
					continue;
				
				final ItemContainer c = getControl(ItemContainer.class, t.getName());
				IItemSlot s = m_loadout.getSlot(t);
				c.setSlot(s);
				
				c.getObservers().add(new ItemContainer.IItemContainerObserver() {
					@Override
					public void selected() {
						m_observers.raise(ILoadoutQueryObserver.class).selected(c.getSlot());
					}
				});
			}
		}
	}

	public interface ILoadoutQueryObserver {
		void selected(IItemSlot slot);
	}
}
