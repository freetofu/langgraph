/*
 * Copyright (c) 2004 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.mymodeltransformation;

import com.nomagic.magicdraw.modeltransformations.ModelTransformation;
import com.nomagic.magicdraw.modeltransformations.impl.any_to_any.AnyToAnyModelTransformationInfo;

/**
 * Custom transformation info file
 *
 * @see com.nomagic.magicdraw.modeltransformations.ModelTransformationInfo
 * @author Sarunas Misius
 */
public class MyModelTransformationInfo extends AnyToAnyModelTransformationInfo
{
	public MyModelTransformationInfo()
	{
        super("My Model Transformation", "Description.html", "custom.gif");
	}

    @Override
	public ModelTransformation getTransformation()
    {
        return new MyModelTransformation();
    }
}
