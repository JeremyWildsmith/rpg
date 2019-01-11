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
import io.github.jevaengine.math.Vector2F;
import io.github.jevaengine.math.Vector3F;
import io.github.jevaengine.rpg.AttributeSet;
import io.github.jevaengine.rpg.IImmutableAttributeSet;
import io.github.jevaengine.rpg.entity.character.IRpgCharacter;
import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.scene.model.IAnimationSceneModel;


public interface IItem
{
	String getName();
	String getDescription();
	IItemFunction getFunction();

	AttributeSet getAttributes();

	IImmutableGraphic getIcon();
	IAnimationSceneModel createModel();

	public final class ItemTarget {
		private final Object target;
		private final Vector3F targetLocation;

		public ItemTarget(Object target, Vector3F targetLocation) {
			this.target = target;
			this.targetLocation = targetLocation;
		}

		public ItemTarget(Object o) {
			if (o instanceof Vector3F) {
				this.target = null;
				this.targetLocation = new Vector3F((Vector3F) o);
			} else if (o instanceof IEntity) {
				this.target = (IEntity)o;
				this.targetLocation = ((IEntity)target).getBody().getLocation();
			} else if (o instanceof Vector2F) {
				this.target = null;
				this.targetLocation = new Vector3F((Vector2F) o, 0);
			} else{
				this.target = o;
				this.targetLocation = new Vector3F();
			}
		}

		public ItemTarget(IEntity target) {
			this.target = target;
			this.targetLocation = target.getBody().getLocation();
		}

		public ItemTarget(Vector3F location) {
			this.target = null;
			this.targetLocation = location;
		}

		public ItemTarget(Vector2F location) {
			this.target = null;
			this.targetLocation = new Vector3F(location, 0);
		}

		public Vector3F getTargetLocation() {
			return targetLocation;
		}

		public <T> T getTarget(Class<T> cls) {
			if(target == null)
				return null;

			if(cls.isAssignableFrom(target.getClass()))
				return (T) target;

			return null;
		}
	}

	public interface IItemFunction
	{
		IWieldTarget[] getWieldTargets();
		String getName();
		IImmutableAttributeSet use(IRpgCharacter user, ItemTarget target, AttributeSet itemAttributes, IItem item);
		ItemUseAbilityTestResults testUseAbility(IRpgCharacter user, ItemTarget target, IImmutableAttributeSet item);
	}
	
	public static final class ItemUseAbilityTestResults
	{
		private final boolean m_isUseable;
		private final String m_message;
		
		public ItemUseAbilityTestResults(boolean isUseable)
		{
			this(isUseable, "");
		}
		
		public ItemUseAbilityTestResults(boolean isUseable, String message)
		{
			m_isUseable = isUseable;
			m_message = message;
		}
		
		public boolean isUseable()
		{
			return m_isUseable;
		}
		
		public String getMessage()
		{
			return m_message;
		}
	}
	
	public interface IWieldTarget
	{
		String getName();
	}

	public static final class NullWieldTarget implements IWieldTarget
	{
		@Override
		public String getName()
		{
			return "null";
		}	
	}
	
	public static final class NullItemFunction implements IItemFunction
	{
		@Override
		public IWieldTarget[] getWieldTargets()
		{
			return new IWieldTarget[] {};
		}

		@Override
		public String getName()
		{
			return "null";
		}

		@Override
		public ItemUseAbilityTestResults testUseAbility(IRpgCharacter user, ItemTarget target, IImmutableAttributeSet item)
		{
			return new ItemUseAbilityTestResults(false, "Item will null function cannot be used.");
		}
		
		@Override
		public IImmutableAttributeSet use(IRpgCharacter user, ItemTarget target, AttributeSet itemAttributes, IItem item)
		{
			return new AttributeSet();
		}
	}
}
