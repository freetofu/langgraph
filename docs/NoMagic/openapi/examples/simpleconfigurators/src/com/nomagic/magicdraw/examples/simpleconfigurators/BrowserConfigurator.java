/*
 * Copyright (c) 2002 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.simpleconfigurators;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.BrowserContextAMConfigurator;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.nomagic.magicdraw.ui.browser.Tree;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;

/**
 * Configurator for configuring actions manager used for browser tree pop-up and browser tree shortcuts.
 *
 * @author Donatas Simkunas
 */
class BrowserConfigurator implements BrowserContextAMConfigurator, AMConfigurator
{

	/**
	 * Action which should be added to the tree.
	 */
	private final DefaultBrowserAction action;

	/**
	 * Creates configurator for adding given action.
	 *
	 * @param action action to be added to manager.
	 */
	public BrowserConfigurator(DefaultBrowserAction action)
	{
		this.action = action;
	}

	/**
	 * @see com.nomagic.magicdraw.actions.BrowserContextAMConfigurator#configure(com.nomagic.actions.ActionsManager, com.nomagic.magicdraw.ui.browser.Tree)
	 */
	@Override
	public void configure(ActionsManager manager, Tree tree)
	{
		configure(manager);
	}

	/**
	 * @see com.nomagic.actions.AMConfigurator#configure(com.nomagic.actions.ActionsManager)
	 */
	@Override
	public void configure(ActionsManager manager)
	{
		// adding action separator
		final MDActionsCategory category = new MDActionsCategory("BrowserConfiguratorExample", "BrowserConfiguratorExample");
		category.addAction(action);
		manager.addCategory(category);
	}

	@Override
	public int getPriority()
	{
		return AMConfigurator.MEDIUM_PRIORITY;
	}
}
