/*
 *
 * Copyright (c) 2003 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.specificdiagram;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.properties.PropertyID;
import com.nomagic.magicdraw.properties.PropertyPool;
import com.nomagic.magicdraw.ui.actions.DrawShapeDiagramAction;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.ui.ScalableImageIcon;
import com.nomagic.ui.SquareIcon;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * Action for drawing entity element.
 *
 * @author Mindaugas Ringys
 */
class MyClassAction extends DrawShapeDiagramAction
{
	public static final String DRAW_MY_CLASS_ACTION = "DRAW_MY_CLASS_ACTION";

	public MyClassAction()
	{
		super(DRAW_MY_CLASS_ACTION, "My Class", KeyStroke.getKeyStroke(KeyEvent.VK_M, 0));
		//noinspection OverridableMethodCallDuringObjectConstruction,SpellCheckingInspection
		setLargeIcon(SquareIcon.fitOrCenter(new ScalableImageIcon(getClass(), "icons/myclass.svg"), 16));
	}

	/**
	 * Creates model element
	 *
	 * @return created model element
	 */
	@Override
	protected Element createElement()
	{
		//noinspection ConstantConditions
		com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class clazz = Application.getInstance().getProject().getElementsFactory().createClassInstance();
		clazz.setActive(true);
		return clazz;
	}

	/**
	 * Creates presentation element.
	 *
	 * @return created presentation element
	 */
	@Override
	protected PresentationElement createPresentationElement()
	{
		PresentationElement element = super.createPresentationElement();
		if (element != null)
		{
			PresentationElementsManager manager = PresentationElementsManager.getInstance();
			manager.addProperty(element, PropertyPool.getBooleanProperty(PropertyID.SUPPRESS_CLASS_ATTRIBUTES, true, "ATTRIBUTES"));
			manager.addProperty(element, PropertyPool.getBooleanProperty(PropertyID.SUPPRESS_CLASS_OPERATIONS, true, "OPERATIONS"));
		}
		return element;
	}
}
