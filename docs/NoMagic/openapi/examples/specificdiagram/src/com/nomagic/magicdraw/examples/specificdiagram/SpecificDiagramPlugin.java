package com.nomagic.magicdraw.examples.specificdiagram;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.plugins.Plugin;

/**
 * The example of specific diagram in the MagicDraw application.
 * @author Mindaugas Ringys
 */
public class SpecificDiagramPlugin extends Plugin
{
	/**
	 * Initializing the plugin.
	 */
	@Override
	public void init()
	{
		Application.getInstance().addNewDiagramType(new SpecificDiagramDescriptor());
	}

    /**
	 * Return true always, because this plugin does not have any close specific actions.
	 */
	@Override
	public boolean close()
	{
		return true;
	}

	/**
	 * @see Plugin#isSupported()
	 */
	@Override
	public boolean isSupported()
	{
		return true;
	}
}
