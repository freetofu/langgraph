/*
 * Copyright (c) 2021 No Magic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.customdiagrampainter;

import com.dassault_systemes.modeler.foundation.diagram.AbstractDiagramSurfacePainter;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectEventListenerAdapter;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.ui.DiagramWindow;
import com.nomagic.magicdraw.uml.symbols.AbstractDiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.DiagramSurface;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.shapes.ShapeElement;

import java.awt.*;
import java.beans.PropertyChangeEvent;

/**
 * Plugin for doing some custom diagram painting. This sample shows how to do shapes highlighting the diagram.
 *
 * @author Mindaugas Ringys
 */
public class CustomDiagramPainter extends Plugin
{
	/**
	 * Initializing the plugin.
	 */
	@Override
	public void init()
	{
		//add listener for getting events about an opened project
		Application.getInstance().addProjectEventListener(new CustomDiagramPainterRegistrar(new SampleDiagramSurfacePainter()));
	}

	/**
	 * Return true always, because this plugin does not have any close specific actions.
	 */
	@Override
	public boolean close()
	{
		return true;
	}

	/**
	 * @see com.nomagic.magicdraw.plugins.Plugin#isSupported()
	 */
	@Override
	public boolean isSupported()
	{
		return true;
	}

	/**
	 * Sample painter
	 */
	private static class SampleDiagramSurfacePainter implements AbstractDiagramSurfacePainter
	{
		@Override
		public void paint(Graphics g, AbstractDiagramPresentationElement diagram)
		{
			g.setColor(Color.BLUE);
			//traverse all symbols in the diagram
			for (PresentationElement presentationElement : diagram.getPresentationElements())
			{
				//do painting just around Shapes
				if (presentationElement instanceof ShapeElement)
				{
					Rectangle bounds = presentationElement.getBounds();
					bounds.grow(5, 5);
					((Graphics2D) g).draw(bounds);
				}
			}
		}
	}

	/**
	 * Registers diagram painter.
	 */
	private static class CustomDiagramPainterRegistrar extends ProjectEventListenerAdapter
	{
		private final AbstractDiagramSurfacePainter painter;

		CustomDiagramPainterRegistrar(AbstractDiagramSurfacePainter painter)
		{
			this.painter = painter;
		}

		@Override
		public void projectOpened(Project project)
		{
			addPainterForOpenedDiagrams(project);
			project.addPropertyChangeListener(this::addPainterForOpenedDiagram);
		}

		private void addPainterForOpenedDiagrams(Project project)
		{
			for (DiagramPresentationElement diagram : project.getDiagrams())
			{
				addPainter(diagram);
			}
		}

		private void addPainterForOpenedDiagram(PropertyChangeEvent evt)
		{
			if (Project.DIAGRAM_OPENED.equals(evt.getPropertyName()))
			{
				Object newValue = evt.getNewValue();
				if (newValue instanceof DiagramWindow window)
				{
					AbstractDiagramPresentationElement diagram = window.getAbstractDiagramPresentationElement();
					if (diagram != null)
					{
						addPainter(diagram);
					}
				}
			}
		}

		private void addPainter(AbstractDiagramPresentationElement diagram)
		{
			DiagramSurface diagramSurface = DiagramSurface.getDiagramSurface(diagram);
			if (diagramSurface != null)
			{
				diagramSurface.addPainter(painter);
			}
		}
	}
}