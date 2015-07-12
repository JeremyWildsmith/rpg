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
package io.github.jevaengine.rpg.entity.character;

import io.github.jevaengine.IDisposable;
import io.github.jevaengine.math.Matrix3X3;
import io.github.jevaengine.math.Rect3F;
import io.github.jevaengine.rpg.entity.character.ISpellCastResolver.ISpellCastResolverObserver;
import io.github.jevaengine.rpg.spell.IImmutableSpellImpactController;
import io.github.jevaengine.rpg.spell.ISpell;
import io.github.jevaengine.world.Direction;
import io.github.jevaengine.world.physics.PhysicsBodyShape;
import io.github.jevaengine.world.scene.model.IActionSceneModel;
import io.github.jevaengine.world.scene.model.IAnimationSceneModel;
import io.github.jevaengine.world.scene.model.MergeActionSceneModel;
import io.github.jevaengine.world.scene.model.TranslatedAnimationSceneModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class SpellEffectCompositedActionSceneModel implements IActionSceneModel, IDisposable
{
	private final MergeActionSceneModel m_modelMerge;
	private final HashMap<IImmutableSpellImpactController, IAnimationSceneModel> m_mergedImpacts = new HashMap<>();
	private final IActionSceneModel m_base;
	
	public SpellEffectCompositedActionSceneModel(IActionSceneModel base, ISpellCastResolver spellCastResolver)
	{
		m_base = base;
		m_modelMerge = new MergeActionSceneModel(base);
		m_modelMerge.add(base);
		spellCastResolver.getObservers().add(new SpellResolverObserver());
	}
	
	@Override
	public void dispose()
	{
		for(IAnimationSceneModel m : m_mergedImpacts.values())
		{
			m_modelMerge.remove(m);
			m.dispose();
		}
		
		m_mergedImpacts.clear();
		m_modelMerge.dispose();
	}
	
	private void mergeImpact(IImmutableSpellImpactController c)
	{
		removeImpact(c);
		
		IAnimationSceneModel model = new TranslatedAnimationSceneModel(c.createImpactModel(), m_base.getAABB().getPoint(1.0F, 1.0F, 0.25F));
		m_mergedImpacts.put(c, model);
		m_modelMerge.add(model);
	}
	
	private void removeImpact(IImmutableSpellImpactController c)
	{
		if(!m_mergedImpacts.containsKey(c))
			return;
		
		IAnimationSceneModel model = m_mergedImpacts.get(c);
		m_modelMerge.remove(model);
		m_mergedImpacts.remove(c);
		model.dispose();
	}

	@Override
	public void update(int deltaTime)
	{
		ArrayList<IImmutableSpellImpactController> spellImpacts = new ArrayList<>(m_mergedImpacts.keySet());
		
		List<IImmutableSpellImpactController> remove = new ArrayList<>();
		
		for(IImmutableSpellImpactController c : m_mergedImpacts.keySet())
			if(!c.isPersisting())
				remove.add(c);
		
		for(IImmutableSpellImpactController c : remove)
			removeImpact(c);
		
		m_modelMerge.update(deltaTime);
	}

	@Override
	public Direction getDirection()
	{
		return m_modelMerge.getDirection();
	}

	@Override
	public void setDirection(Direction direction)
	{
		m_modelMerge.setDirection(direction);
	}

	@Override
	public Collection<ISceneModelComponent> getComponents(Matrix3X3 projection)
	{
		return m_modelMerge.getComponents(projection);
	}

	@Override
	public Rect3F getAABB()
	{
		return m_modelMerge.getAABB();
	}
	
	@Override
	public PhysicsBodyShape getBodyShape()
	{
		return m_base.getBodyShape();
	}

	@Override
	public IAnimationSceneModel clone()
	{
		throw new SceneModelNotCloneableException();
	}

	@Override
	public IActionSceneModelAction getAction(String name)
	{
		return m_base.getAction(name);
	}

	@Override
	public boolean hasAction(String name)
	{
		return m_base.hasAction(name);
	}
	
	private final class SpellResolverObserver implements ISpellCastResolverObserver
	{
		@Override
		public void castImpacted(IRpgCharacter target, IImmutableSpellImpactController controller)
		{
			mergeImpact(controller);
		}

		@Override
		public void castSummoned(ISpell spell, IImmutableSpellImpactController casterImpact)
		{
			mergeImpact(casterImpact);
		}
	}
}
