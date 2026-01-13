/*
 *
 * Copyright (c) 2004 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.mymodeltransformation;

import com.nomagic.magicdraw.modeltransformations.ModelTransformationsManager;
import com.nomagic.rcpf.product.license.utils.LicenseUtils;

/**
 * Helper plugin for initiation of custom transformation.
 *
 * @author Sarunas Misius
 */
public class MyModelTransformationPlugin extends com.nomagic.magicdraw.plugins.Plugin
{
	@Override
	public void init()
	{
		ModelTransformationsManager.getInstance().addTransformation(new MyModelTransformationInfo());
	}

	@Override
	public boolean close()
	{
		return true;
	}

	@Override
	public boolean isSupported()
	{
		return LicenseUtils.isStandardEditionOrHigher();
	}
}
