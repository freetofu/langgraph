package com.nomagic.magicdraw.examples.specificnonsymboldiagram;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.plugins.Plugin;

/**
 * The example of specific diagram in the MagicDraw application.
 *
 * @author Martynas Lelevicius
 */
public class SpecificNonSymbolDiagramPlugin extends Plugin
{
	/**
	 * Initializing the plugin.
	 */
	@Override
	public void init()
	{
		Application.getInstance().addNewDiagramType(new SpecificNonSymbolDiagramDescriptor());
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
	 * @see com.nomagic.magicdraw.plugins.Plugin#isSupported()
	 */
	@Override
	public boolean isSupported()
	{
		return true;
	}
}
