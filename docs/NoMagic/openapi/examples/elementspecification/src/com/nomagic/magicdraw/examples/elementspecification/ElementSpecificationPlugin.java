/*
 * Copyright (c) 2014 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.elementspecification;

import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.ui.dialogs.specifications.SpecificationDialogManager;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

/**
 * Example plugin for Element specification dialog configuration.
 *
 * @author Martynas Lelevicius
 */
public class ElementSpecificationPlugin extends Plugin
{
	@Override
	public void init()
	{
		SpecificationDialogManager.getManager().addConfigurator(Element.class, new SpecificationNodeConfigurator());
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
