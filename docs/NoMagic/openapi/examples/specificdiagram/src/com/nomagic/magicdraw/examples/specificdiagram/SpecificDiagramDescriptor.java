/*
 *
 * Copyright (c) 2003 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.specificdiagram;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.magicdraw.actions.DiagramContextAMConfigurator;
import com.nomagic.magicdraw.actions.MDActionsManager;
import com.nomagic.magicdraw.icons.IconsFactory;
import com.nomagic.magicdraw.ui.actions.BaseDiagramContextAMConfigurator;
import com.nomagic.magicdraw.ui.actions.ClassDiagramShortcutsConfigurator;
import com.nomagic.magicdraw.uml.DiagramDescriptor;
import com.nomagic.magicdraw.uml.DiagramType;
import com.nomagic.ui.ResizableIcon;

import java.net.URL;

/**
 * Descriptor of specific diagram.
 *
 * @author Mindaugas Ringys
 */
public class SpecificDiagramDescriptor extends DiagramDescriptor
{
	public static final String SPECIFIC_DIAGRAM = "Specific Diagram";

	/**
	 * Let this diagram type be a sub type of class diagram type.
	 */
	@Override
	public String getSuperType()
	{
		return DiagramType.UML_CLASS_DIAGRAM;
	}

	/**
	 * This is creatable diagram.
	 */
	@Override
	public boolean isCreatable()
	{
		return true;
	}

	/**
	 * Actions used in this diagram.
	 */
	@Override
	public MDActionsManager getDiagramActions()
	{
		return SpecificDiagramActions.getActions();
	}

	/**
	 * Configurator for diagram toolbar.
	 */
	@Override
	public AMConfigurator getDiagramToolbarConfigurator()
	{
		return new SpecificDiagramToolbarConfigurator(getSuperType());
	}

	/**
	 * Configurator for diagram shortcuts.
	 */
	@Override
	public AMConfigurator getDiagramShortcutsConfigurator()
	{
		return new ClassDiagramShortcutsConfigurator();
	}

	/**
	 * Configurator for diagram context menu.
	 */
	@Override
	public DiagramContextAMConfigurator getDiagramContextConfigurator()
	{
		return new BaseDiagramContextAMConfigurator();
	}

	/**
	 * Id of the diagram type.
	 */
	@Override
	public String getDiagramTypeId()
	{
		return SPECIFIC_DIAGRAM;
	}

	/**
	 * Diagram type human name.
	 */
	@Override
	public String getSingularDiagramTypeHumanName()
	{
		return "Specific Diagram";
	}

	/**
	 * Diagram type human name in plural form.
	 */
	@Override
	public String getPluralDiagramTypeHumanName()
	{
		return "Specific Diagrams";
	}

	/**
	 * Resizable icon for diagram. svg and wmf format. Used in Content diagram.
	 *
	 * @return resizable icon in svg or wmf formats.
	 */
	@Override
	public ResizableIcon getSVGIcon()
	{
		return IconsFactory.getNotScaledIcon(getSVGIconURL());
	}

	private URL getSVGIconURL()
	{
		return getClass().getResource("icons/specificdiagram.svg");
	}

	/**
	 * URL to small icon for diagram.
	 */
	@Override
	public URL getSmallIconURL()
	{
		return getSVGIconURL();
	}
}