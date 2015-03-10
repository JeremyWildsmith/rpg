package io.github.jevaengine.rpg;

import io.github.jevaengine.script.IFunctionFactory;
import io.github.jevaengine.script.NullFunctionFactory;
import io.github.jevaengine.script.ScriptEvent;
import io.github.jevaengine.script.ScriptExecuteException;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.Observers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AttributeSet implements IImmutableAttributeSet
{
	private final Logger m_logger = LoggerFactory.getLogger(AttributeSet.class);
	
	private final Map<IAttributeIdentifier, Attribute> m_attributes = new HashMap<>();
	private final IFunctionFactory m_functionFactory;
	
	public AttributeSet()
	{
		this(new NullFunctionFactory());
	}
	
	public AttributeSet(IImmutableAttributeSet src)
	{
		this(new NullFunctionFactory(), src);
	}
	
	public AttributeSet(Map<IAttributeIdentifier, Float> initialAttributes)
	{
		this(new NullFunctionFactory(), initialAttributes);
	}
	
	public AttributeSet(IFunctionFactory functionFactory)
	{
		m_functionFactory = functionFactory;
	}
	
	public AttributeSet(IFunctionFactory functionFactory, IImmutableAttributeSet src)
	{
		m_functionFactory = functionFactory;
		
		for(Map.Entry<IAttributeIdentifier, IImmutableAttribute> a : src.getSet())
			m_attributes.put(a.getKey(), new Attribute(a.getValue().get()));
	}
	
	public AttributeSet(IFunctionFactory functionFactory, Map<IAttributeIdentifier, Float> initialAttributes)
	{
		m_functionFactory = functionFactory;
		for(Map.Entry<IAttributeIdentifier, Float> a : initialAttributes.entrySet())
			m_attributes.put(a.getKey(), new Attribute(a.getValue()));
	}
	
	@Override
	public Set<Map.Entry<IAttributeIdentifier, IImmutableAttribute>> getSet()
	{
		Map<IAttributeIdentifier, IImmutableAttribute> buffer = new HashMap<>();
		
		for(Map.Entry<IAttributeIdentifier, Attribute> a : m_attributes.entrySet())
			buffer.put(a.getKey(), new Attribute(a.getValue().get()));
	
		return Collections.unmodifiableMap(buffer).entrySet();
	}
	
	public void merge(IImmutableAttributeSet ... sets)
	{
		for(IImmutableAttributeSet s : sets)
		{
			for(Map.Entry<IAttributeIdentifier, IImmutableAttribute> a : s.getSet())
			{
				Attribute current = m_attributes.get(a.getKey());
				
				if(current == null)
					m_attributes.put(a.getKey(), new Attribute(a.getValue().get()));
				else
					m_attributes.put(a.getKey(), new Attribute(current.get() + a.getValue().get()));
			}
		}
	}
	
	public AttributeSet overlay(AttributeSet ... statistics)
	{
		AttributeSet buffer = new AttributeSet(m_functionFactory, this);
		
		for(AttributeSet s : statistics)
			buffer.merge(s);
		
		return buffer;
	}
	
	@Override
	public boolean has(IAttributeIdentifier attribute)
	{
		return m_attributes.containsKey(attribute);
	}
	
	@Override
	public boolean has(String name)
	{
		for(Map.Entry<IAttributeIdentifier, Attribute> a : m_attributes.entrySet())
			if(a.getKey().getName().equals(name))
				return true;
		
		return false;
	}
	
	@Override
	public IAttribute get(IAttributeIdentifier attribute)
	{
		if(!m_attributes.containsKey(attribute))
			m_attributes.put(attribute, new Attribute());
			
		return m_attributes.get(attribute);
	}
	
	@Override
	public IAttribute get(String name)
	{
		for(Map.Entry<IAttributeIdentifier, Attribute> a : m_attributes.entrySet())
			if(a.getKey().getName().equals(name))
				return a.getValue();
		
		m_attributes.put(new DefaultAttributeIdentifier(name), new Attribute());
		
		return get(name);
	}
	
	public static final class DefaultAttributeIdentifier implements IAttributeIdentifier
	{
		private final String m_name;
	
		public DefaultAttributeIdentifier(String name)
		{
			m_name = name;
		}
		
		@Override
		public String getName()
		{
			return m_name;
		}

		@Override
		public String getDescription()
		{
			return "";
		}
	}
	
	public final class Attribute implements IAttribute
	{
		private final Observers m_observers = new Observers();
		private float m_value;
		
		public ScriptEvent onChanged = new ScriptEvent(m_functionFactory);
		
		public Attribute(float value)
		{
			m_value = value;
		}
		
		public Attribute()
		{
			this(0);
		}
		
		@Override
		public float get()
		{
			return m_value;
		}
		
		@Override
		public boolean isZero()
		{
			return Math.abs(m_value) < 0.00001F;
		}
		
		@Override
		public void set(float newValue)
		{
			if(m_value == newValue)
				return;
			
			float oldValue = m_value;
			m_value = newValue;
			
			m_observers.raise(IAttributeChangeObserver.class).changed(newValue - oldValue);
			
			try
			{
				onChanged.fire(newValue - oldValue);
			} catch (ScriptExecuteException e) {
				m_logger.error("Error occured informing script about attribute change", e);
			}
		}
		
		@Override
		public IObserverRegistry getObservers()
		{
			return m_observers;
		}
	}
}
