package com.nomagic.magicdraw.examples.compartmentedit;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.actions.DiagramContextAMConfigurator;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.uml.DiagramTypeConstants;
import com.nomagic.magicdraw.uml.symbols.CompartmentID;
import com.nomagic.magicdraw.uml.symbols.CompartmentManager;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.shapes.ClassView;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Operation;

import java.awt.event.ActionEvent;
import java.util.List;

/**
 * <p>
 * Demonstrates usage of Open API for Compartment Edit functionality.
 * </p>
 *
 * @author Mindaugas Genutis
 * @see com.nomagic.magicdraw.uml.symbols.CompartmentManager
 * @see com.nomagic.magicdraw.uml.symbols.CompartmentID
 * @see com.nomagic.magicdraw.openapi.uml.SessionManager
 * @see com.nomagic.magicdraw.uml.symbols.PresentationElement
 * @see com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
 */
public class CompartmentEditExamplePlugin extends Plugin
{
	@Override
	public void init()
	{
		addSymbolContextMenu();
	}

	/**
	 * Adds symbol context menu on the diagram.
	 */
	private static void addSymbolContextMenu()
	{
		DiagramContextAMConfigurator configurator = new DiagramContextAMConfigurator()
		{
			@Override
			public void configure(ActionsManager manager, DiagramPresentationElement diagramView, PresentationElement[] selected,
								  PresentationElement requestor)
			{
				if (selected.length == 1)
				{
					ActionsCategory category = new ActionsCategory();
					manager.addCategory(category);

					category.addAction(new HideFirstOperationAction());
				}
			}

			@Override
			public int getPriority()
			{
				return AMConfigurator.MEDIUM_PRIORITY;
			}
		};

		ActionsConfiguratorsManager.getInstance().addDiagramContextConfigurator(DiagramTypeConstants.UML_ANY_DIAGRAM, configurator);
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

	/**
	 * Hides first operation in a class.
	 *
	 * @author Mindaugas Genutis
	 */
	private static class HideFirstOperationAction extends DefaultDiagramAction
	{
		/**
		 * Name of the action.
		 */
		private static final String ACTION_NAME = "Hide First Class Operation";

		/**
		 * Constructs this action.
		 */
		public HideFirstOperationAction()
		{
			super(ACTION_NAME, ACTION_NAME, null, null);
		}

		@Override
		public void actionPerformed(ActionEvent evt)
		{
			List<PresentationElement> views = getSelected();

			if (!views.isEmpty())
			{
				PresentationElement view = views.get(0);

				if (view instanceof ClassView)
				{
					//noinspection ConstantConditions
					List<Operation> operations = ((Class) view.getElement()).getOwnedOperation();

					if (!operations.isEmpty())
					{
						Project project = Project.getProject(view);

						SessionManager sessionManager = SessionManager.getInstance();
						sessionManager.createSession(project, getName());

						CompartmentManager.hideCompartmentElement(view, CompartmentID.OPERATIONS, operations.get(0));

						sessionManager.closeSession(project);
					}
				}
			}
		}
	}
}