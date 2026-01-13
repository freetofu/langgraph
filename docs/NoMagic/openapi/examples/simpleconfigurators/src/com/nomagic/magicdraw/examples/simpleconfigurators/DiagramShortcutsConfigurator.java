/*
 * Copyright (c) 2002 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.simpleconfigurators;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;

/**
 * Class for configuring diagram shortcuts.
 *
 * @author Donatas Simkunas
 */
class DiagramShortcutsConfigurator implements AMConfigurator
{
	/**
	 * Action to register shortcut.
	 */
	private final DefaultDiagramAction action;

	DiagramShortcutsConfigurator(DefaultDiagramAction action)
	{
		this.action = action;
	}

	/**
	 * Configuring shortcuts.
	 */
	@Override
	public void configure(ActionsManager manager)
	{
		if (manager.getActionFor(action.getID()) == null)
		{
			ActionsCategory category = new ActionsCategory();
			manager.addCategory(category);
			category.addAction(action);
		}
	}
}
