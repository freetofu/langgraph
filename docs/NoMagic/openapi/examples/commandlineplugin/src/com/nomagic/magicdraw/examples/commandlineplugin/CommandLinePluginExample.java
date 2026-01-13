/*
 * Copyright (c) 2015 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.commandlineplugin;

import com.nomagic.magicdraw.commandline.CommandLineActionManager;
import com.nomagic.magicdraw.plugins.Plugin;

/**
 * @author Martynas Lelevicius
 */
public class CommandLinePluginExample extends Plugin
{
	@Override
	public void init()
	{
		CommandLineActionManager.getInstance().addAction(new CommandLineActionExample());
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
