package com.nomagic.magicdraw.examples.dependency;

import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.plugins.Plugin;

/**
 * Example plugin that shows how to use dependency checker.
 *
 * @author Rimvydas Vaidelis
 * @version 1.0
 */
public class DependencyCheckerExample extends Plugin
{
    @Override
	public void init()
    {
        ActionsConfiguratorsManager manager = ActionsConfiguratorsManager.getInstance();
		manager.addMainMenuConfigurator(new MainMenuConfigurator(new DependencyCheckAction()));
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