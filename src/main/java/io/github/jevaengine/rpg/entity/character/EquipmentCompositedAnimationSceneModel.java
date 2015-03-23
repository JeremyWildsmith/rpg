package io.github.jevaengine.rpg.entity.character;

import io.github.jevaengine.IDisposable;
import io.github.jevaengine.math.Matrix3X3;
import io.github.jevaengine.math.Rect3F;
import io.github.jevaengine.rpg.entity.character.IImmutableLoadout.ILoadoutObserver;
import io.github.jevaengine.rpg.item.IItem;
import io.github.jevaengine.rpg.item.IItem.IWieldTarget;
import io.github.jevaengine.rpg.item.IItemSlot;
import io.github.jevaengine.world.Direction;
import io.github.jevaengine.world.scene.model.IAnimationSceneModel;
import io.github.jevaengine.world.scene.model.MergeAnimationSceneModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class EquipmentCompositedAnimationSceneModel implements IAnimationSceneModel, IDisposable
{
	private final MergeAnimationSceneModel m_modelMerge = new MergeAnimationSceneModel();
	private final Map<IWieldTarget, IAnimationSceneModel> m_mergedEquipment = new HashMap<>();
	
	public EquipmentCompositedAnimationSceneModel(IAnimationSceneModel base, ILoadout loadout)
	{
		m_modelMerge.add(base);
		loadout.getObservers().add(new LoadoutObserver());
		
		for(IItemSlot i : loadout.getSlots())
		{
			if(!i.isEmpty())
				mergeItemModel(i.getItem());
		}
	}
	
	@Override
	public void dispose()
	{
		for(IAnimationSceneModel m : m_mergedEquipment.values())
		{
			m_modelMerge.remove(m);
			m.dispose();
		}
		
		m_mergedEquipment.clear();
		m_modelMerge.dispose();
	}
	
	private void mergeItemModel(IItem item)
	{
		removeItemModel(item.getFunction().getWieldTarget());
		
		IAnimationSceneModel itemModel = item.createModel();
		m_mergedEquipment.put(item.getFunction().getWieldTarget(), itemModel);
		m_modelMerge.add(itemModel);
	}
	
	private void removeItemModel(IWieldTarget wieldTarget)
	{
		if(!m_mergedEquipment.containsKey(wieldTarget))
			return;
		
		m_modelMerge.remove(m_mergedEquipment.get(wieldTarget));
		IAnimationSceneModel model = m_mergedEquipment.remove(wieldTarget);
		
		model.dispose();
	}

	@Override
	public void update(int deltaTime)
	{
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
	public IAnimationSceneModel clone()
	{
		return m_modelMerge.clone();
	}

	@Override
	public IAnimationSceneModelAnimation getAnimation(String name)
	{
		return m_modelMerge.getAnimation(name);
	}

	@Override
	public boolean hasAnimation(String name)
	{
		return m_modelMerge.hasAnimation(name);
	}
	
	private final class LoadoutObserver implements ILoadoutObserver
	{
		@Override
		public void unequip(IWieldTarget wieldTarget)
		{
			removeItemModel(wieldTarget);
		}

		@Override
		public void equip(IItem item)
		{
			mergeItemModel(item);
		}	
	}
}
