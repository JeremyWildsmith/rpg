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

import io.github.jevaengine.graphics.IFont;
import io.github.jevaengine.graphics.NullFont;
import io.github.jevaengine.joystick.InputKeyEvent;
import io.github.jevaengine.joystick.InputMouseEvent;
import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.rpg.item.IItemSlot;
import io.github.jevaengine.rpg.item.IItemSlot.NullItemSlot;
import io.github.jevaengine.ui.ComponentState;
import io.github.jevaengine.ui.Control;
import io.github.jevaengine.ui.style.ComponentStateStyle;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.Observers;
import java.awt.Graphics2D;

public final class ItemContainer extends Control
{
	public static final String COMPONENT_NAME = "itemContainer";

	private IItemSlot m_slot = new NullItemSlot();

	private ComponentState m_state = ComponentState.Default;

	private IFont m_font = new NullFont();
	
	private final Rect2D m_bounds;
	
	private Observers m_observers = new Observers();
	
	public ItemContainer(String instanceName, int width, int height)
	{
		super(COMPONENT_NAME, instanceName);
		m_bounds = new Rect2D(width, height);
	}
	
	public void setSlot(IItemSlot slot)
	{
		m_slot = slot;
	}
	
	public IItemSlot getSlot() {
		return m_slot;
	}
	
	public IObserverRegistry getObservers()
	{
		return m_observers;
	}
	
	private void enterState(ComponentState state)
	{
		m_state = state;
		
		ComponentStateStyle stateStyle = getComponentStyle().getStateStyle(m_state);
		stateStyle.playEnter();
		
		m_font = stateStyle.getFont();
	}
	
	@Override
	protected void onEnter()
	{
		enterState(ComponentState.Enter);
	}

	@Override
	protected void onLeave()
	{
		enterState(ComponentState.Default);
	}
	
	@Override
	public boolean onMouseEvent(InputMouseEvent mouseEvent)
	{
		if (mouseEvent.mouseButton == InputMouseEvent.MouseButton.Left)
		{
			if(mouseEvent.type == InputMouseEvent.MouseEventType.MousePressed)
			{
				m_observers.raise(IItemContainerObserver.class).selected();
				enterState(ComponentState.Activated);
			} else if(mouseEvent.type == InputMouseEvent.MouseEventType.MouseReleased)
			{
				enterState(ComponentState.Enter);
			}
		}
		
		return true;
	}

	@Override
	public boolean onKeyEvent(InputKeyEvent keyEvent)
	{
		return false;
	}

	@Override
	public void update(int deltaTime) { }
	
	@Override
	public void render(Graphics2D g, int x, int y, float scale)
	{
		if(m_slot.isEmpty())
			return;
		
		m_slot.getItem().getIcon().render(g, x + m_bounds.width / 2, y + m_bounds.height / 2, scale);
	}

	@Override
	protected void onStyleChanged()
	{
		enterState(m_state);
	}

	@Override
	public Rect2D getBounds()
	{
		return new Rect2D(m_bounds);
	}
	
	public interface IItemContainerObserver {
		void selected();
	}
}
