package com.nomagic.magicdraw.examples.copypaste;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.actions.BrowserContextAMConfigurator;
import com.nomagic.magicdraw.actions.DiagramContextAMConfigurator;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.nomagic.magicdraw.copypaste.CopyPasting;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.ui.browser.Tree;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.magicdraw.uml.DiagramTypeConstants;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Example plugin which demonstrates usage of Copy/Paste Open API.
 *
 * @author Mindaugas Genutis
 */
public class CopyPasteExamplePlugin extends Plugin
{
	@Override
	public void init()
	{
		addBrowserContextMenuAction();
		addSymbolContextMenu();
	}

	/**
	 * Adds symbol context menu on the diagram.
	 */
	private static void addSymbolContextMenu()
	{
		ActionsConfiguratorsManager.getInstance().addDiagramContextConfigurator(DiagramTypeConstants.UML_ANY_DIAGRAM,
				new DiagramContextAMConfigurator()
				{
					@Override
					public void configure(ActionsManager manager, DiagramPresentationElement diagram,
										  PresentationElement[] selected, PresentationElement requestor)
					{
						ActionsCategory category = new ActionsCategory();
						manager.addCategory(category);
						category.addAction(new CloneSymbolAction());
					}

					@Override
					public int getPriority()
					{
						return AMConfigurator.MEDIUM_PRIORITY;
					}
				});
	}

	/**
	 * Adds context menu to elements in the browser.
	 */
	private static void addBrowserContextMenuAction()
	{
		ActionsConfiguratorsManager.getInstance().addContainmentBrowserContextConfigurator(new BrowserContextAMConfigurator()
		{
			@Override
			public void configure(ActionsManager manager, Tree browser)
			{
				MDActionsCategory category = new MDActionsCategory();
				category.addAction(new CloneElementAction());
				manager.addCategory(category);
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

	/**
	 * Action for cloning elements.
	 */
	static class CloneElementAction extends DefaultBrowserAction
	{
		/**
		 * Constructs this action.
		 */
		public CloneElementAction()
		{
			super("CLONE", "Clone", null, null);
		}

		@Override
		public void actionPerformed(ActionEvent evt)
		{
			BaseElement baseElement = getFirstElement();

			if (baseElement instanceof Element element)
			{

				Project project = Project.getProject(baseElement);

				SessionManager sessionManager = SessionManager.getInstance();
				sessionManager.createSession(project,"Clone");

				//noinspection ConstantConditions
				CopyPasting.copyPasteElement(element, element.getOwner(), true);

				sessionManager.closeSession(project);
			}
		}
	}

	/**
	 * Action for cloning elements.
	 */
	static class CloneSymbolAction extends DefaultDiagramAction
	{
		/**
		 * Constructs this action.
		 */
		public CloneSymbolAction()
		{
			super("CLONE", "Clone", null, null);
		}

		@Override
		public void actionPerformed(ActionEvent evt)
		{
			List<PresentationElement> views = getSelected();

			if (!views.isEmpty())
			{
				Project project = Project.getProject(views.get(0));

				SessionManager sessionManager = SessionManager.getInstance();
				sessionManager.createSession(project,"Clone");

				//noinspection ConstantConditions
				List<BaseElement> baseElements = CopyPasting.copyPasteElements(views, null, getDiagram(), false, false);

				List<PresentationElement> presentationElements = baseElements.stream()
						.filter(baseElement -> baseElement instanceof PresentationElement)
						.map(baseElement -> (PresentationElement) baseElement)
						.collect(Collectors.toList());

				try
                {
                    PresentationElementsManager.getInstance().movePresentationElements(presentationElements, new Point(100, 50));
                }
                catch (ReadOnlyElementException e)
                {
                    e.printStackTrace();
                }

				sessionManager.closeSession(project);
			}
		}
	}
}