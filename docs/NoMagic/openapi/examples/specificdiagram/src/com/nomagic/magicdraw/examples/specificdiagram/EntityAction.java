/*
 *
 * Copyright (c) 2003 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.specificdiagram;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.properties.PropertyID;
import com.nomagic.magicdraw.properties.PropertyPool;
import com.nomagic.magicdraw.ui.actions.DrawShapeDiagramAction;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.ui.ScalableImageIcon;
import com.nomagic.uml2.StandardProfile;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * Action for drawing entity element.
 *
 * @author Mindaugas Ringys
 */
class EntityAction extends DrawShapeDiagramAction
{
	public static final String DRAW_ENTITY_ACTION = "DRAW_ENTITY_ACTION";

	public EntityAction()
	{
		super(DRAW_ENTITY_ACTION, "Entity", KeyStroke.getKeyStroke(KeyEvent.VK_E, 0));
		final Class clazz = getClass();
		//noinspection OverridableMethodCallDuringObjectConstruction
		setLargeIcon(new ScalableImageIcon(clazz, "" + "icons/entity.gif"));
	}

	/**
	 * Creates model element
	 *
	 * @return created model element
	 */
	@Override
	protected Element createElement()
	{
		Project project = Application.getInstance().getProject();
		//noinspection DataFlowIssue
		Element element = project.getElementsFactory().createClassInstance();
		StandardProfile.getInstance(project).entity().apply(element);
		return element;
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
			PresentationElementsManager.getInstance().addProperty(element, PropertyPool.getBooleanProperty(PropertyID.SUPPRESS_CLASS_OPERATIONS, true, "OPERATIONS"));
		}
		return element;
	}
}
