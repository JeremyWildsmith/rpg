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
package io.github.jevaengine.rpg.item;

import io.github.jevaengine.graphics.IImmutableGraphic;
import io.github.jevaengine.rpg.AttributeSet;
import io.github.jevaengine.world.scene.model.IAnimationSceneModel;

import java.util.Objects;

public final class DefaultItem implements IItem
{
	private final String m_name;
	private final String m_description;
	private final IItemFunction m_function;
	private final AttributeSet m_attributes;
	private final IImmutableGraphic m_icon;
	private final IAnimationSceneModel m_model;
	
	public DefaultItem(DefaultItem item)
	{
		m_name = item.m_name;
		m_description = item.m_description;
		m_function = item.m_function;
		m_attributes = new AttributeSet(item.m_attributes);
		m_icon = item.m_icon;
		m_model = item.m_model.clone();
	}
	
	public DefaultItem(String name, String description, IItemFunction function, AttributeSet attributes, IImmutableGraphic graphic, IAnimationSceneModel model)
	{
		m_name = name;
		m_description = description;
		m_function = function;
		m_attributes = attributes;
		m_icon = graphic;
		m_model = model;
	}
	
	@Override
	public String getName()
	{
		return m_name;
	}

	@Override
	public String getDescription()
	{
		return m_description;
	}

	@Override
	public IItemFunction getFunction()
	{
		return m_function;
	}

	@Override
	public AttributeSet getAttributes()
	{
		return m_attributes;
	}

	@Override
	public IImmutableGraphic getIcon()
	{
		return m_icon;
	}
	
	@Override
	public IAnimationSceneModel createModel()
	{
		return m_model.clone();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DefaultItem that = (DefaultItem) o;
		return Objects.equals(m_name, that.m_name) &&
				Objects.equals(m_description, that.m_description) &&
				Objects.equals(m_function, that.m_function);
	}

	@Override
	public int hashCode() {
		return Objects.hash(m_name, m_description, m_function);
	}
}
