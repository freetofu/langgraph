/*
 * Copyright (c) 2007 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.symbolrendering;

import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElementColorEnum;
import com.nomagic.magicdraw.uml.symbols.shapes.ShapeRenderer;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

import java.awt.*;

/**
 * Custom package renderer - the empty package (package without inner elements) is filled with green color.
 *
 * @author Martynas Lelevicius
 */
class PackageRenderer extends ShapeRenderer
{
	@Override
	public Color getColor(PresentationElement presentationElement, PresentationElementColorEnum colorEnum)
	{
		if (PresentationElementColorEnum.FILL.equals(colorEnum))
		{
			// color to fill
			Element element = presentationElement.getElement();
			if (element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package &&
				!element.hasOwnedElement())
			{
				// package has no elements - use green color to fill
				return Color.GREEN;
			}
		}
		return super.getColor(presentationElement, colorEnum);
	}
}
