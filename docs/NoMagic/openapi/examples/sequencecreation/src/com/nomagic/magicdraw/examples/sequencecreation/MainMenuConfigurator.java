package com.nomagic.magicdraw.examples.sequencecreation;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.ActionsID;
import com.nomagic.magicdraw.actions.MDAction;

/**
 * Class for configuring main menu and adding  action in "Tools" sub-menu.
 *
 * @author Martynas Lelevicius
 * @author Mindaugas Genutis
 */
public class MainMenuConfigurator implements AMConfigurator
{
	/**
	 * Action will be added to manager.
	 */
	private final MDAction mAction;

	/**
	 * Creates configurator.
	 *
	 */
	public MainMenuConfigurator()
	{
		mAction = new CreateSequenceAction();
	}

	/**
	 * @see com.nomagic.actions.AMConfigurator#configure(com.nomagic.actions.ActionsManager)
	 *      Methods adds action to given manager Tools category
	 */
	@Override
	public void configure(ActionsManager manager)
	{
		// searching for Tools action category
		ActionsCategory tools = (ActionsCategory) manager.getActionFor(ActionsID.TOOLS);

		if (tools != null)
		{
			// adding action to found category.
			ActionsCategory category = new ActionsCategory();
			tools.addAction(category);
			category.addAction(mAction);
		}
	}

	@Override
	public int getPriority()
	{
		return AMConfigurator.MEDIUM_PRIORITY;
	}
}