/*
 * Copyright (c) 2013 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.customhyperlink;

import com.nomagic.magicdraw.hyperlinks.HyperlinksHandlersRegistry;
import com.nomagic.magicdraw.plugins.Plugin;

/**
 * @author Martynas Lelevicius
 */
public class CustomHyperlinkPlugin extends Plugin
{
	@Override
	public void init()
	{
		HyperlinksHandlersRegistry.addHandler(new CustomHyperlinkHandler());
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
