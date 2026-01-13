/*
 * Copyright (c) 2002 NoMagic, Inc. All Rights Reserved.
 */

package com.nomagic.magicdraw.examples.simpleconfigurators;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.ActionsID;
import com.nomagic.magicdraw.actions.MDAction;

/**
 * Class for configuring main menu and adding  action in "Help" sub-menu.
 *
 * @author Donatas Simkunas
 */
class MainMenuConfigurator implements AMConfigurator
{
	/**
	 * Action will be added to manager.
	 */
	private final MDAction action;

	/**
	 * Creates configurator.
	 *
	 * @param action action to be added to main menu.
	 */
	MainMenuConfigurator(MDAction action)
	{
		this.action = action;
	}

	/**
	 * @see com.nomagic.actions.AMConfigurator#configure(ActionsManager)
	 * Methods adds action to given manager Help category
	 */
	@Override
	public void configure(ActionsManager manager)
	{
		// searching for Help action category
		final ActionsCategory category = (ActionsCategory) manager.getActionFor(ActionsID.HELP);
		if (category != null)
		{
			// adding action to found category.
			category.addAction(action);
		}
	}

	@Override
	public int getPriority()
	{
		return AMConfigurator.MEDIUM_PRIORITY;
	}

}