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

import io.github.jevaengine.config.IConfigurationFactory;
import io.github.jevaengine.config.IConfigurationFactory.ConfigurationConstructionException;
import io.github.jevaengine.config.IImmutableVariable;
import io.github.jevaengine.config.ImmutableVariableOverlay;
import io.github.jevaengine.config.NoSuchChildVariableException;
import io.github.jevaengine.config.NullVariable;
import io.github.jevaengine.config.ValueSerializationException;
import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.ui.Control;
import io.github.jevaengine.ui.IControlFactory;
import io.github.jevaengine.ui.UnsupportedControlException;
import io.github.jevaengine.util.Nullable;
import java.net.URI;
import javax.inject.Inject;

public class RpgControlFactory implements IControlFactory
{
	private final IControlFactory m_controlFactory;
	private final IConfigurationFactory m_configurationFactory;
	
	@Inject
	public RpgControlFactory(IControlFactory controlFactory, IConfigurationFactory configurationFactory)
	{
		m_controlFactory = controlFactory;
		m_configurationFactory = configurationFactory;
	}
	
	@Override
	@Nullable
	public Class<? extends Control> lookup(String className)
	{
		if(className.equals(ItemContainer.COMPONENT_NAME))
			return ItemContainer.class;
		else
			return m_controlFactory.lookup(className);
	}

	@Override
	public <T extends Control> String lookup(Class<T> controlClass)
	{
		if(controlClass.equals(ItemContainer.class))
			return ItemContainer.COMPONENT_NAME;
		else
			return m_controlFactory.lookup(controlClass);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Control> T create(Class<T> controlClass, String instanceName, URI config, IImmutableVariable auxConfig) throws ControlConstructionException
	{
		try
		{
			String configPath = config.getPath();
			IImmutableVariable configVar = new ImmutableVariableOverlay(auxConfig, configPath.isEmpty() || configPath.endsWith("/") ? new NullVariable() : m_configurationFactory.create(config));
		
			if(controlClass.equals(ItemContainer.class))
			{
				Rect2D bounds = configVar.getChild("bounds").getValue(Rect2D.class);
				return (T)new ItemContainer(instanceName, bounds.width, bounds.height);
			}else
				return m_controlFactory.create(controlClass, instanceName, config, auxConfig);
		} catch(ConfigurationConstructionException | ValueSerializationException | NoSuchChildVariableException e)
		{
			throw new ControlConstructionException(controlClass.getName(), e);
		}
	}

	@Override
	public Control create(String controlName, String instanceName, URI config, IImmutableVariable auxConfig) throws ControlConstructionException
	{
		Class<? extends Control> ctrlClass = lookup(controlName);
		
		if(ctrlClass == null)
			throw new ControlConstructionException(controlName, new UnsupportedControlException());
		
		return create(ctrlClass, instanceName, config, auxConfig);
	}
}
