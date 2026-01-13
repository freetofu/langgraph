/*
 *
 * Copyright (c) 2002 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.selectionactions;

import com.nomagic.actions.SelectItemAction;
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Example shows how to use actions for selecting items.
 *
 * @author Donatas Simkunas
 */
public class Example extends Plugin implements PropertyChangeListener
{

	private SelectItemAction selectAction;

	/**
	 * @see com.nomagic.magicdraw.plugins.Plugin#init()
	 */
	@Override
	public void init()
	{
		ActionsConfiguratorsManager manager = ActionsConfiguratorsManager.getInstance();
		createAction();
		// adding action too main toolbar
		manager.addMainToolbarConfigurator(new Configurator(selectAction));
	}

	/**
	 * @see com.nomagic.magicdraw.plugins.Plugin#close()
	 */
	@Override
	public boolean close()
	{
		return true;
	}

	/**
	 * @see com.nomagic.magicdraw.plugins.Plugin#isSupported()
	 */
	@Override
	public boolean isSupported()
	{
		return true;
	}

	private void createAction()
	{
		// creating action to select one of these items
		List<String> selection = new ArrayList<>();
		selection.add("one");
		selection.add("two");
		selection.add("three");
		selectAction = new SelectItemAction("SELECT_EXAMPLE_ACTION", "Select action", null, null, selection, "Selection");
		selectAction.addPropertyChangeListener(this);
	}

	/**
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 * when selection changed this method is called.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		if (evt.getSource() == selectAction && evt.getPropertyName().equals(selectAction.getValueName()))
		{
			JOptionPane.showMessageDialog(MDDialogParentProvider.getProvider().getDialogOwner(), "Selection changed : " + selectAction.getValue());
		}
	}

}
