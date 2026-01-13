/*
 *
 * Copyright (c) 2003 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.symbolcreation;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.actions.BrowserContextAMConfigurator;
import com.nomagic.magicdraw.actions.DiagramContextAMConfigurator;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.ui.browser.Tree;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.uml.DiagramTypeConstants;
import com.nomagic.magicdraw.uml.symbols.AbstractDiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.paths.PathElement;
import com.nomagic.magicdraw.uml.symbols.shapes.NoteView;
import com.nomagic.magicdraw.uml.symbols.shapes.ShapeElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Classifier;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.LiteralString;
import com.nomagic.uml2.impl.ElementsFactory;

import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * This plugin is use for create a presentation element of the selected element.
 * @author Mindaugas Ringys
 */
public class SymbolCreationPlugin extends Plugin
{
	private DefaultDiagramAction mAddNote;
	private DefaultDiagramAction mAddTextBox;
	private DefaultDiagramAction mConnectNote;
	private DefaultDiagramAction mSetConstraint;

	private DefaultBrowserAction mCreateDiagram;
	private DefaultBrowserAction mCreateSymbol;

	/**
	 * Initializing a plugin.
	 */
	@Override
	public void init()
	{
		final PresentationElementsManager manager = PresentationElementsManager.getInstance();

		/*
		 * Add action to add Note to diagram.
		 */
		mAddNote = new DefaultDiagramAction("add note", "Add Note", null, null)
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				DiagramPresentationElement diagram = getDiagram();
				if (diagram != null)
				{
					Project project = Project.getProject(diagram);

					SessionManager.getInstance().createSession(project, "Add Note");

					try
					{
						ShapeElement note = manager.createNote(diagram);
						manager.setText(note, "Note text");
					}
					catch (ReadOnlyElementException e1)
					{
						e1.printStackTrace();
					}

					SessionManager.getInstance().closeSession(project);
				}
			}
		};

		/*
		 * Add action to add text box to diagram.
		 */
		mAddTextBox = new DefaultDiagramAction("add text box", "Add TextBox", null, null)
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				DiagramPresentationElement diagram = getDiagram();
				if (diagram != null)
				{
					Project project = Project.getProject(diagram);

					SessionManager.getInstance().createSession(project, "Add TextBox");

					try
					{
						manager.setText(manager.createTextBox(diagram), "text box text");
					}
					catch (ReadOnlyElementException e1)
					{
						e1.printStackTrace();
					}

					SessionManager.getInstance().closeSession(project);
				}
			}
		};

		/*
		 * Add action to add connect note to diagram.
		 */
		mConnectNote = new DefaultDiagramAction("connect note", "Connect Note", null, null)
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				DiagramPresentationElement diagram = getDiagram();
				if (diagram != null)
				{
					Project project = Project.getProject(diagram);

					SessionManager.getInstance().createSession(project, "Connect note");

					try
					{
						PresentationElement element = getFirstSelected();
						if (element != null)
						{
							ShapeElement note = manager.createNote(diagram);
							manager.setText(note, "some note");
							Rectangle bounds = element.getBounds();
							bounds.x = bounds.x + bounds.width + 100;
							manager.reshapeShapeElement(note, bounds);

							manager.connectNote(note, element);
						}

					}
					catch (ReadOnlyElementException e1)
					{
						e1.printStackTrace();
					}

					SessionManager.getInstance().closeSession(project);
				}
			}

			@Override
			public void updateState()
			{
				setEnabled(getFirstSelected() != null && getSelected().size() == 1);
			}
		};

		/*
		 * Add action to add constraint.
		 */
		mSetConstraint = new DefaultDiagramAction("set constraint", "Set constraint", null, null)
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Project project = Application.getInstance().getProject();
				if (project != null)
				{
					SessionManager.getInstance().createSession(project, "Set Constraint");

					try
					{
						// Element factory that working with element.
						ElementsFactory elementsFactory = project.getElementsFactory();

						// Create constraint instance.
						com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Constraint constraint = elementsFactory.createConstraintInstance();
						constraint.setName("name");

						// Create string literal.
						LiteralString value = elementsFactory.createLiteralStringInstance();
						value.setValue("expression");
						constraint.setSpecification(value);
						ModelElementsManager.getInstance().addElement(constraint, project.getPrimaryModel());

						ShapeElement note = (ShapeElement) getFirstSelected();
						if (note != null)
						{
							PathElement noteAnchor = note.getConnectedPathElements().get(0);
							@SuppressWarnings("ObjectEquality")
							PresentationElement otherEnd = noteAnchor.getClient() == note ?
														   noteAnchor.getSupplier() :
														   noteAnchor.getClient();
							Element otherModelElement = otherEnd.getElement();
							if (otherModelElement != null)
							{
								SessionManager.getInstance().checkReadOnly(otherModelElement);
								constraint.getConstrainedElement().add(otherModelElement);

								manager.setConstraintForNote(note, constraint);
							}
						}

					}
					catch (ReadOnlyElementException e1)
					{
						e1.printStackTrace();
					}

					SessionManager.getInstance().closeSession(project);
				}
			}

			@Override
			public void updateState()
			{
				PresentationElement firstSelected = getFirstSelected();
				setEnabled( firstSelected != null &&
				            getSelected().size() == 1 &&
				            firstSelected.getClassType().equals(NoteView.class) &&
				            ((ShapeElement)firstSelected).getConnectedPathElementCount() == 1);
			}
		};

		/*
		 * Create Diagram.
		 */
		mCreateDiagram = new DefaultBrowserAction("create diagram", "Create Class Diagram", null, null)
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Project project = Application.getInstance().getProject();
				if (project != null)
				{
					SessionManager.getInstance().createSession(project, "Create Class Diagram");

					try
					{
						Diagram diagram = ModelElementsManager.getInstance().createDiagram(DiagramTypeConstants.UML_CLASS_DIAGRAM, project.getPrimaryModel());

						DiagramPresentationElement dpe = project.getDiagram(diagram);
						if (dpe != null)
						{
							dpe.open();
						}
					}
					catch (ReadOnlyElementException e1)
					{
						e1.printStackTrace();
					}

					SessionManager.getInstance().closeSession(project);
				}
			}
		};

		/*
		 * Create symbol
		 */
		mCreateSymbol = new DefaultBrowserAction("create symbol", "Create Symbol For Selected", null, null)
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Element el = (Element) getSelectedObject();
				if (el != null)
				{
					Project project = Project.getProject(el);

					SessionManager.getInstance().createSession(project, "Create Symbol");

					try
					{
						AbstractDiagramPresentationElement diagram = project.getActiveAbstractDiagram();
						if (diagram != null)
						{
							manager.createShapeElement(el, diagram);
						}
					}
					catch (ReadOnlyElementException e1)
					{
						e1.printStackTrace();
					}

					SessionManager.getInstance().closeSession(project);
				}
			}

			@Override
			public void updateState()
			{
				boolean enable = false;
				Project project = Application.getInstance().getProject();
				if (project != null)
				{
					enable = getSelectedObject() instanceof Classifier && project.getActiveAbstractDiagram() != null;
				}
				setEnabled(enable);
			}
		};


		ActionsConfiguratorsManager.getInstance().addDiagramContextConfigurator(DiagramTypeConstants.UML_CLASS_DIAGRAM, new DiagramContextAMConfigurator()
		{
			// Add the created action to class diagram.
			@Override
			public void configure(ActionsManager actionsManager, DiagramPresentationElement diagram, PresentationElement[] selected, PresentationElement requestor)
			{
				ActionsCategory temp = new ActionsCategory();
				actionsManager.addCategory(temp);
				temp.addAction(mAddNote);
				temp.addAction(mAddTextBox);
				temp.addAction(mConnectNote);
				temp.addAction(mSetConstraint);
			}
			@Override
			public int getPriority()
			{
				return AMConfigurator.MEDIUM_PRIORITY;
			}
		});

		ActionsConfiguratorsManager.getInstance().addContainmentBrowserContextConfigurator(new BrowserContextAMConfigurator()
		{
			// Add created action to browser.
			@Override
			public void configure(ActionsManager actionsManager, Tree tree)
			{
				ActionsCategory temp = new ActionsCategory();
				actionsManager.addCategory(temp);
				temp.addAction(mCreateDiagram);
				temp.addAction(mCreateSymbol);
			}
			@Override
			public int getPriority()
			{
				return AMConfigurator.MEDIUM_PRIORITY;
			}

		});
	}

	@Override
	public boolean close()
	{
		return true;
	}

	@Override
	public boolean isSupported()
	{
		return true;
	}
}

