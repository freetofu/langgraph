package com.nomagic.magicdraw.examples.legenditem;

import com.nomagic.magicdraw.plugins.Plugin;

/**
 * This is empty plugin implementation which is only required for other example classes
 * (i.e. {@link AdornComponentsOfInterestInDiagramValidator}) to be added to the class path of MagicDraw automatically
 * @author Edgaras Dulskis
 */
public class LegendItemImplementationExamplePlugin extends Plugin
{
	@Override
	public void init()
	{
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
