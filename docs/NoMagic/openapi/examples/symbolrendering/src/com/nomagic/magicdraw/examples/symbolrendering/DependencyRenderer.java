/*
 * Copyright (c) 2007 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.symbolrendering;

import com.nomagic.magicdraw.properties.Property;
import com.nomagic.magicdraw.properties.PropertyID;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElementColorEnum;
import com.nomagic.magicdraw.uml.symbols.PropertiesHelper;
import com.nomagic.magicdraw.uml.symbols.paths.*;

import javax.annotation.CheckForNull;
import java.awt.*;
import java.util.List;

/**
 * Custom dependency link renderer.
 *
 * @author Martynas Lelevicius
 */
class DependencyRenderer extends PathElementRenderer
{
	private final PathEndRenderer mClientEndRenderer;

	DependencyRenderer()
	{
		// custom client end renderer - use filled circle at the end
		mClientEndRenderer = new PathEndRenderer(PathEndAdornment.CIRCLE, PathEndAdornmentModifier.FILLED);
	}

	@Override
	public Color getColor(PresentationElement presentationElement, PresentationElementColorEnum colorEnum)
	{
		if (PresentationElementColorEnum.LINE.equals(colorEnum))
		{
			// use blue color for line
			return Color.BLUE;
		}
		return super.getColor(presentationElement, colorEnum);
	}

	@Override
	protected PathEndRenderer getClientEndRenderer(PathElement pathElement, @CheckForNull PresentationElement client)
	{
		// use custom end renderer
		return mClientEndRenderer;
	}

	@Override
	public int getLineWidth(PathElement presentationElement)
	{
		// line with is 2
		return 2;
	}

	@Override
	protected void drawPathAdornment(Graphics g, PathElement pathElement, Point supplierPoint, Point clientPoint, List<Point> breakPoints,
									 @CheckForNull PresentationElement supplier, @CheckForNull PresentationElement client)
	{
		super.drawPathAdornment(g, pathElement, supplierPoint, clientPoint, breakPoints, supplier, client);

		// draw circle at the middle of the dependency line
		Color background = Color.WHITE;
		PresentationElement presentationElement = DiagramPresentationElement.get(pathElement);
		Property property = PropertiesHelper.getProperty(presentationElement, PropertyID.DIAGRAM_BACKGROUND_COLOR);
		if (property != null)
		{
			Object value = property.getValue();
			if (value instanceof Color)
			{
				background = (Color) value;
			}
		}
		Point middlePoint = pathElement.getPointOnPath(supplierPoint, clientPoint, breakPoints, 0.5);
		if (middlePoint != null)
		{
			int diameter = 10;
			int radius = diameter / 2;
			int x = middlePoint.x - radius;
			int y = middlePoint.y - radius;

			Color color = g.getColor();
			g.setColor(background);
			g.fillOval(x, y, diameter, diameter);
			g.setColor(color);
			g.drawOval(x, y, diameter, diameter);
		}
	}
}
