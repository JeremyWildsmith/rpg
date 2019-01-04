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
import io.github.jevaengine.math.Vector2F;
import io.github.jevaengine.rpg.IImmutableAttributeSet.IImmutableAttribute;
import io.github.jevaengine.ui.*;
import io.github.jevaengine.ui.IWindowFactory.WindowConstructionException;
import io.github.jevaengine.ui.Timer.ITimerObserver;

import java.net.URI;

public final class AttributeRatioGuageQueryFactory
{
	private final URI m_inventoryLayout;
	
	private final WindowManager m_windowManager;
	private final IWindowFactory m_windowFactory;
	
	public AttributeRatioGuageQueryFactory(WindowManager windowManager, IWindowFactory windowFactory, URI inventoryLayout)
	{
		m_windowManager = windowManager;
		m_windowFactory = windowFactory;
		m_inventoryLayout = inventoryLayout;
	}
	
	public CharacterStatusQuery create(IImmutableAttribute numerator, IImmutableAttribute denumerator) throws WindowConstructionException
	{	
		Window window = m_windowFactory.create(m_inventoryLayout, new CharacterInventoryQueryBehaviourInjector(numerator, denumerator));
		m_windowManager.addWindow(window);

		window.center();
		
		return new CharacterStatusQuery(window);
	}
	
	public static class CharacterStatusQuery implements IDisposable
	{
		private final Window m_window;
		
		private CharacterStatusQuery(Window window)
		{
			m_window = window;
		}
		
		@Override
		public void dispose()
		{
			m_window.dispose();
		}
		
		public void setVisible(boolean isVisible)
		{
			m_window.setVisible(isVisible);
		}
	
		public boolean isVisible()
		{
			return m_window.isVisible();
		}
		
		public void setLocation(Vector2D location)
		{
			m_window.setLocation(location);
		}
		
		public void center()
		{
			m_window.center();
		}
		
		public void focus()
		{
			m_window.focus();
		}
		
		public void setMovable(boolean isMovable)
		{
			m_window.setMovable(false);
		}
		
		public void setTopMost(boolean isTopMost)
		{
			m_window.setTopMost(isTopMost);
		}
		
		public Rect2D getBounds()
		{
			return m_window.getBounds();
		}
	}
	
	private class CharacterInventoryQueryBehaviourInjector extends WindowBehaviourInjector
	{
		private final IImmutableAttribute m_numerator;
		private final IImmutableAttribute m_denumerator;
		
		public CharacterInventoryQueryBehaviourInjector(IImmutableAttribute numerator, IImmutableAttribute denumerator)
		{
			m_numerator = numerator;
			m_denumerator = denumerator;
		}
		
		@Override
		protected void doInject() throws NoSuchControlException
		{
			final ValueGuage ratioGuage = getControl(ValueGuage.class, "ratioGuage");
			
			Timer t = new Timer();
			t.getObservers().add(new ITimerObserver() {
				@Override
				public void update(int deltaTime) {
					float numerator = m_numerator.get();
					float denumerator = m_denumerator.get();
					
					float value = Math.abs(denumerator) < Vector2F.TOLERANCE ? 0 : numerator / denumerator;
					
					ratioGuage.setValue(value);
				}
			});
			
			addControl(t);
		}
	}
}