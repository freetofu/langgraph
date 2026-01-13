package com.nomagic.magicdraw.examples.refactor;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.actions.BrowserContextAMConfigurator;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.ui.browser.Tree;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.magicdraw.ui.dialogs.SelectElementInfo;
import com.nomagic.magicdraw.ui.dialogs.SelectElementTypes;
import com.nomagic.magicdraw.ui.dialogs.selection.ElementSelectionDlg;
import com.nomagic.magicdraw.ui.dialogs.selection.ElementSelectionDlgFactory;
import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.magicdraw.uml.ConvertElementInfo;
import com.nomagic.magicdraw.uml.Refactoring;
import com.nomagic.uml2.ext.magicdraw.classes.mdinterfaces.Interface;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Example plugin which demonstrates usage of Refactor Open API.
 *
 * @author Mindaugas Genutis
 */
public class RefactorExamplePlugin extends Plugin
{
	@Override
	public void init()
	{
		addBrowserContextMenuAction();
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

				category.addAction(new ReplaceElementAction());
				category.addAction(new ConvertToInterfaceElementAction());
				category.addAction(new ReverseRelationAction());

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
	 * Action for replacing an element.
	 */
	static class ReplaceElementAction extends DefaultBrowserAction
	{
		/**
		 * Constructs this action.
		 */
		public ReplaceElementAction()
		{
			super("REPLACE_FROM_PLUGIN", "Replace From Plugin", null, null);
		}

		@Override
		public void actionPerformed(ActionEvent evt)
		{
			BaseElement baseElement = getFirstElement();

			if (baseElement instanceof Element element)
			{
				ElementSelectionDlg elementSelectionDlg = createElementSelectionDialog();

				elementSelectionDlg.setVisible(true);

				if (elementSelectionDlg.isOkClicked())
				{
					BaseElement selected = elementSelectionDlg.getSelectedElement();

					if (selected instanceof Element selectedElement)
					{
						Project project = Project.getProject(element);

						SessionManager sessionManager = SessionManager.getInstance();
						sessionManager.createSession(project, "Replace");

						ConvertElementInfo info = new ConvertElementInfo(selectedElement.getClassType());
						info.setConvertOnlyIncomingReferences(true);

						try
						{
							Refactoring.Replacing.replace(element, selectedElement, info);
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

		/**
		 * Constructs element selection dialog for element replacing.
		 *
		 * @return created element selection dialog.
		 */
		private static ElementSelectionDlg createElementSelectionDialog()
		{
			ElementSelectionDlg elementSelectionDlg = ElementSelectionDlgFactory.create(MDDialogParentProvider.getProvider().getDialogOwner());

			List<Class> selectable = new ArrayList<>();
			selectable.add(Interface.class);
			SelectElementTypes types = new SelectElementTypes(null, selectable);

			ElementSelectionDlgFactory.initSingle(elementSelectionDlg, types, new SelectElementInfo(false, false), null);

			return elementSelectionDlg;
		}
	}

	/**
	 * Action for converting an element to an interface.
	 */
	static class ConvertToInterfaceElementAction extends DefaultBrowserAction
	{
		/**
		 * Constructs this action.
		 */
		public ConvertToInterfaceElementAction()
		{
			super("CONVERT_TO_INTERFACE", "Convert To Interface", null, null);
		}

		@Override
		public void actionPerformed(ActionEvent evt)
		{
			BaseElement baseElement = getFirstElement();

			if (baseElement instanceof Element element)
			{
				Project project = Project.getProject(element);
				SessionManager sessionManager = SessionManager.getInstance();
				sessionManager.createSession(project, "Convert");

				ConvertElementInfo info = new ConvertElementInfo(Interface.class);
				try
				{
					Refactoring.Converting.convert(element, info);
				}
				catch (ReadOnlyElementException e)
				{
					e.printStackTrace();
				}

				sessionManager.closeSession(project);
			}
		}
	}

	/**
	 * Action for reversing a relationship.
	 */
	static class ReverseRelationAction extends DefaultBrowserAction
	{
		/**
		 * Constructs this action.
		 */
		public ReverseRelationAction()
		{
			super("REVERSE_RELATION", "Reverse Relation", null, null);
		}

		@Override
		public void actionPerformed(ActionEvent evt)
		{
			BaseElement baseElement = getFirstElement();

			if (baseElement instanceof Element)
			{
				Project project = Project.getProject(baseElement);
				SessionManager sessionManager = SessionManager.getInstance();
				sessionManager.createSession(project, "Reverse relation");

				Refactoring.RelationReversing.reverseRelationDirection((Element) baseElement);

				sessionManager.closeSession(project);
			}
		}
	}
}