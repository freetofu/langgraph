/*
 * Copyright (c) 2007 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.symbolrendering;

import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.uml.symbols.PresentationElementRendererManager;

/**
 * Plugin registers the custom presentation element renderer.
 *
 * @author Martynas Lelevicius
 */
public class SymbolRenderingPlugin extends Plugin
{
	@Override
	public void init()
	{
		// register renderer provider
		PresentationElementRendererManager.getInstance().addProvider(new RendererProvider());
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
