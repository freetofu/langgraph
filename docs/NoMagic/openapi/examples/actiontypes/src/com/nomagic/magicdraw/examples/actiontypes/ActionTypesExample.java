/*
 *
 * Copyright (c) 2002 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.actiontypes;


import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.plugins.Plugin;

/**
 * Example shows how to create more complicated GUI components from actions.
 *
 * @author Donatas Simkunas
 */
public class ActionTypesExample extends Plugin
{

	/**
	 * @see com.nomagic.magicdraw.plugins.Plugin#init()
	 */
	@Override
	public void init()
	{
		ActionsConfiguratorsManager manager = ActionsConfiguratorsManager.getInstance();
		// adding sub-menu
		manager.addMainMenuConfigurator(new MainMenuConfigurator(getSubMenuActions()));
		// adding actions with separator
		manager.addMainMenuConfigurator(new MainMenuConfigurator(getSeparatedActions()));
		// adding check box menu item
		manager.addMainMenuConfigurator(new MainMenuConfigurator(getStateAction()));
		// adding radio button menu items
		manager.addMainMenuConfigurator(new MainMenuConfigurator(getGroupedStateAction()));
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

	/**
	 * Creates group of actions. This group is separated from others using menu separator (when it represented in menu).
	 * Separator is added for group of actions in one actions category.
	 */
	private static NMAction getSeparatedActions()
	{
		ActionsCategory category = new ActionsCategory();
		category.addAction(new SimpleAction(null, "Action1"));
		category.addAction(new SimpleAction(null, "Action2"));
		return category;
	}

	/**
	 * Creates action which is sub-menu (when it represented in menu).
	 * Separator is added for group of actions in actions category.
	 */
	private static NMAction getSubMenuActions()
	{
		ActionsCategory category = new ActionsCategory(null, "SubMenu");
		// this call makes sub-menu.
		category.setNested(true);
		category.addAction(new SimpleAction(null, "SubAction1"));
		category.addAction(new SimpleAction(null, "SubAction2"));
		return category;
	}

	/**
	 * Creates action which is represented by JCheckBoxMenuItem.
	 */
	private static NMAction getStateAction()
	{
		return new SimpleStateAction("StateAction", "State Action");
	}

	/**
	 * @return action which represents state action groups. It is represented in menu by JRadioButtonMenuItem.
	 */
	private static NMAction getGroupedStateAction()
	{
		String[] choice = new String[1];

		SimpleChoiceAction a1 = new SimpleChoiceAction("R1", "R1", choice);
		SimpleChoiceAction a2 = new SimpleChoiceAction("R2", "R2", choice);
		choice[0] = a1.getName();
		a1.updateState();
		a2.updateState();
		// actions must be added to one category.
		ActionsCategory cat = new ActionsCategory();
		cat.addAction(a1);
		cat.addAction(a2);
		return cat;
	}


}

