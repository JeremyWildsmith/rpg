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
import io.github.jevaengine.rpg.item.IItemSlot;
import io.github.jevaengine.rpg.item.IItemStore;
import io.github.jevaengine.ui.Button;
import io.github.jevaengine.ui.Button.IButtonPressObserver;
import io.github.jevaengine.ui.IWindowFactory;
import io.github.jevaengine.ui.IWindowFactory.WindowConstructionException;
import io.github.jevaengine.ui.Label;
import io.github.jevaengine.ui.NoSuchControlException;
import io.github.jevaengine.ui.Window;
import io.github.jevaengine.ui.WindowBehaviourInjector;
import io.github.jevaengine.ui.WindowManager;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public final class ItemStoreQueryFactory
{
	private final URI m_inventoryLayout;
	
	private final WindowManager m_windowManager;
	private final IWindowFactory m_windowFactory;
	
	public ItemStoreQueryFactory(WindowManager windowManager, IWindowFactory windowFactory, URI inventoryLayout)
	{
		m_windowManager = windowManager;
		m_windowFactory = windowFactory;
		m_inventoryLayout = inventoryLayout;
	}
	
	public ItemStoreQuery create(IItemStore store) throws WindowConstructionException
	{	
		Window window = m_windowFactory.create(m_inventoryLayout, new CharacterInventoryQueryBehaviourInjector(store));
		m_windowManager.addWindow(window);

		window.center();
		
		return new ItemStoreQuery(window);
	}
	
	public static class ItemStoreQuery implements IDisposable
	{
		private final Window m_window;
		
		private ItemStoreQuery(Window window)
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
		private final IItemStore m_itemStore;
		
		private int m_itemPage = 0;
		
		public CharacterInventoryQueryBehaviourInjector(IItemStore itemStore)
		{
			m_itemStore = itemStore;
		}
		
		private void nextPage(List<ItemContainer> itemContainers, Label lblCurrentPage)
		{
			IItemSlot storeSlots[] = m_itemStore.getSlots();
		
			int maxPages = storeSlots.length == 0 ? 1 : (int)Math.ceil((double)storeSlots.length / itemContainers.size());
			
			m_itemPage = Math.min(m_itemPage + 1, maxPages - 1);
		
			setupPage(itemContainers, lblCurrentPage);
		}
		
		
		private void previousPage(List<ItemContainer> itemContainers, Label lblCurrentPage)
		{
			m_itemPage = Math.max(0, m_itemPage - 1);
		
			setupPage(itemContainers, lblCurrentPage);
		}
		
		private void setupPage(List<ItemContainer> itemContainers, Label lblCurrentPage)
		{
			IItemSlot storeSlots[] = m_itemStore.getSlots();
			int startItem = m_itemPage * itemContainers.size();
			
			int maxPages = storeSlots.length == 0 ? 1 : (int)Math.ceil((double)storeSlots.length / itemContainers.size());
			
			for(int i = 0; startItem + i < storeSlots.length && i < itemContainers.size(); i++)
				itemContainers.get(i).setSlot(storeSlots[startItem + i]);
			
			lblCurrentPage.setText(m_itemPage + 1 + "/" + maxPages);
		}
		
		@Override
		protected void doInject() throws NoSuchControlException
		{
			final List<ItemContainer> itemContainers = new ArrayList<>();
			final Label lblCurrentPage = getControl(Label.class, "lblCurrentPage");
			
			for(int i = 0; hasControl(ItemContainer.class, "itemContainer" + i); i++)
				itemContainers.add(getControl(ItemContainer.class, "itemContainer" + i));
		
			if(itemContainers.isEmpty())
				throw new NoSuchControlException(ItemContainer.class, "itemContainer0");
			
			getControl(Button.class, "btnNextItems").getObservers().add(new IButtonPressObserver() {
				@Override
				public void onPress() {
					nextPage(itemContainers, lblCurrentPage);
				}
			});
			
			getControl(Button.class, "btnPreviousItems").getObservers().add(new IButtonPressObserver() {
				@Override
				public void onPress() {
					previousPage(itemContainers, lblCurrentPage);
				}
			});
			
			setupPage(itemContainers, lblCurrentPage);
		}
	}
}