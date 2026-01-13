package com.nomagic.magicdraw.examples.sequencecreation;

import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.plugins.Plugin;

/**
 * Plugin registers the main menu configurator to add the sequence creation action.
 *
 * @author Martynas Lelevicius
 * @author Mindaugas Genutis
 */
public class SequenceCreationPlugin extends Plugin
{
	@Override
	public void init()
	{
		ActionsConfiguratorsManager.getInstance().addMainMenuConfigurator(new MainMenuConfigurator());
	}

	@Override
	public boolean close()
	{
		return true;
	}

	@Override
	public boolean isSupported()
	{
		return true;
	}
}
