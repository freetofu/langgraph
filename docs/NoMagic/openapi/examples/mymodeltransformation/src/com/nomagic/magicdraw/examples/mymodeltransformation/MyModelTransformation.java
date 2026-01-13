/*
 * Copyright (c) 2004 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.mymodeltransformation;

import com.nomagic.magicdraw.modeltransformations.impl.any_to_any.AnyToAnyModelTransformation;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Custom transformation class
 *
 * @see com.nomagic.magicdraw.modeltransformations.ModelTransformation
 * @author Sarunas Misius
 */
public class MyModelTransformation extends AnyToAnyModelTransformation
{
	@Override
	protected void customTransformationForElement(Element original, Collection<Element> mapped) throws ReadOnlyElementException
	{
		if (original instanceof Class)
		{
			for (Element target : new ArrayList<>(mapped))
			{
				//transform class
				if (target instanceof Class mc)
				{
					SessionManager.getInstance().checkReadOnly(target);
					for (Stereotype anAl : new ArrayList<>(mc.getAppliedStereotype()))
					{
						StereotypesHelper.removeStereotype(mc, anAl);
					}
				}
			}
		}
    }
}
