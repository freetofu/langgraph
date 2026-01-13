/*
 * Copyright (c) 2007 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.symbolrendering;

import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElementTextEnum;
import com.nomagic.magicdraw.uml.symbols.shapes.ShapeRenderer;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;

import java.util.List;

/**
 * Custom slot renderer (used in instance specification symbol) - show rounded slot values.
 *
 * @author Martynas Lelevicius
 */
class SlotRenderer extends ShapeRenderer
{
	@Override
	public String getText(PresentationElement presentationElement, PresentationElementTextEnum textEnum)
	{
		if (PresentationElementTextEnum.NAME.equals(textEnum))
		{
			// the slot text is shown as name 
			Element element = presentationElement.getElement();
			if (element instanceof Slot slot)
			{
				List<ValueSpecification> slotValue = slot.getValue();
				if (!slotValue.isEmpty())
				{
					StringBuilder value = new StringBuilder();
					for (ValueSpecification valueSpecification : slotValue)
					{
						if (valueSpecification instanceof LiteralString)
						{
							if (value.length() > 0)
							{
								value.append("; ");
							}
							String literalValue = ((LiteralString) valueSpecification).getValue();
							if (literalValue != null)
							{
								try
								{
									// round value
									double doubleValue = Double.parseDouble(literalValue);
									double rounded = Math.round(doubleValue * 100) / 100;
									literalValue = Double.toString(rounded);
								}
								catch (NumberFormatException e)
								{
									// do nothing
								}
							}
							value.append(literalValue);
						}
					}

					final StructuralFeature definingFeature = slot.getDefiningFeature();
					return (definingFeature != null ? definingFeature.getName() : "") + "=" + value;
				}
			}
		}
		return super.getText(presentationElement, textEnum);
	}
}
