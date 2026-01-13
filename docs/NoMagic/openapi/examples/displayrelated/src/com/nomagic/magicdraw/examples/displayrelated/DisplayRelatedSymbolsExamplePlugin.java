package com.nomagic.magicdraw.examples.displayrelated;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.actions.DiagramContextAMConfigurator;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.uml.DiagramTypeConstants;
import com.nomagic.magicdraw.uml.symbols.*;
import com.nomagic.uml2.ext.magicdraw.classes.mdinterfaces.InterfaceRealization;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Generalization;

import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Example plugin which demonstrates usage of Display Related Symbols Open API.
 *
 * @author Mindaugas Genutis
 */
public class DisplayRelatedSymbolsExamplePlugin extends Plugin
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
		ActionsConfiguratorsManager.getInstance().addDiagramContextConfigurator(DiagramTypeConstants.UML_ANY_DIAGRAM,
				new DiagramContextAMConfigurator()
				{
					@Override
					public void configure(ActionsManager manager, DiagramPresentationElement diagram,
										  PresentationElement[] selected, PresentationElement requestor)
					{
						ActionsCategory category = new ActionsCategory();
						manager.addCategory(category);

						category.addAction(new DisplayGeneralizationsAction());
						category.addAction(new DisplayContainmentAction());
						category.addAction(new DisplayAnnotatedElementsAction());
						category.addAction(new DisplayCommentsAction());
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
	 * A general action for displaying related symbols.
	 */
	private abstract static class DisplayRelatedSymbolsAction extends DefaultDiagramAction
	{
		/**
		 * Constructs this action.
		 *
		 * @param id   id of the action.
		 * @param name human-readable name of the action.
		 */
		public DisplayRelatedSymbolsAction(String id, String name)
		{
			super(id, name, null, null);
		}

		@Override
		public void actionPerformed(ActionEvent evt)
		{
			List<PresentationElement> views = getSelected();

			if (!views.isEmpty())
			{
				Project project = Project.getProject(views.get(0));

				SessionManager sessionManager = SessionManager.getInstance();
				sessionManager.createSession(project, getName());

				displayRelated(views);

				sessionManager.closeSession(project);
			}
		}

		/**
		 * Display related symbols for given symbols.
		 *
		 * @param views symbols for which to display related symbols.
		 */
		protected abstract void displayRelated(List<PresentationElement> views);
	}

	/**
	 * Action for displaying related generalizations and interface realizations.
	 */
	private static class DisplayGeneralizationsAction extends DisplayRelatedSymbolsAction
	{
		private static final String ACTION_NAME = "Display Generalizations and Interface Realizations";

		/**
		 * Constructs this action.
		 */
		public DisplayGeneralizationsAction()
		{
			super("DISPLAY_GENERALIZATIONS_AND_INTERFACE_REALIZATIONS", ACTION_NAME);
		}

		@Override
		protected void displayRelated(List<PresentationElement> views)
		{
			Set<LinkType> linkTypes = new HashSet<>();
			linkTypes.add(new LinkType(Generalization.class));
			linkTypes.add(new LinkType(InterfaceRealization.class));

			DisplayRelatedSymbolsInfo info = new DisplayRelatedSymbolsInfo(linkTypes);
			info.setCreateAnnotatedElements(false);
			info.setCreateContainment(false);
			info.setCreateComments(false);
			info.setDepthLimited(true);
			info.setDepthLimit(3);

			for (PresentationElement view : views)
			{
                try
                {
                    DisplayRelatedSymbols.displayRelatedSymbols(view, info);
                }
                catch (ReadOnlyElementException e)
                {
                    e.printStackTrace();
                }
            }
		}
	}

	/**
	 * Action for displaying related containment.
	 */
	private static class DisplayContainmentAction extends DisplayRelatedSymbolsAction
	{
		private static final String DISPLAY_CONTAINMENT_ACTION_NAME = "Display Containment";

		/**
		 * Constructs this action.
		 */
		public DisplayContainmentAction()
		{
			super("DISPLAY_CONTAINMENT", DISPLAY_CONTAINMENT_ACTION_NAME);
		}

		@Override
		protected void displayRelated(List<PresentationElement> views)
		{
			DisplayRelatedSymbolsInfo info = new DisplayRelatedSymbolsInfo(Collections.emptySet());

			info.setCreateNewSymbols(true);
			info.setCreateAnnotatedElements(false);
			info.setCreateContainment(true);
			info.setCreateComments(false);

			for (PresentationElement view : views)
			{
                try
                {
                    DisplayRelatedSymbols.displayRelatedSymbols(view, info);
                }
                catch (ReadOnlyElementException e)
                {
                    e.printStackTrace();
                }
            }
		}
	}

	/**
	 * Action for displaying related annotated elements.
	 */
	private static class DisplayAnnotatedElementsAction extends DisplayRelatedSymbolsAction
	{
		private static final String DISPLAY_ANNOTATED_ELEMENTS_ACTION_NAME = "Display Annotated Elements";

		/**
		 * Constructs this action.
		 */
		public DisplayAnnotatedElementsAction()
		{
			super("DISPLAY_ANNOTATED_ELEMENTS", DISPLAY_ANNOTATED_ELEMENTS_ACTION_NAME);
		}

		@Override
		protected void displayRelated(List<PresentationElement> views)
		{
			DisplayRelatedSymbolsInfo info = new DisplayRelatedSymbolsInfo(Collections.emptySet());

			info.setCreateNewSymbols(true);
			info.setCreateAnnotatedElements(true);
			info.setCreateContainment(false);
			info.setCreateComments(false);

			for (PresentationElement view : views)
			{
                try
                {
                    DisplayRelatedSymbols.displayRelatedSymbols(view, info);
                }
                catch (ReadOnlyElementException e)
                {
                    e.printStackTrace();
                }
            }
		}
	}

	/**
	 * Action for displaying comments.
	 */
	private static class DisplayCommentsAction extends DisplayRelatedSymbolsAction
	{
		private static final String DISPLAY_COMMENTS = "Display Comments";

		/**
		 * Constructs this action.
		 */
		public DisplayCommentsAction()
		{
			super("DISPLAY_COMMENTS", DISPLAY_COMMENTS);
		}

		@Override
		protected void displayRelated(List<PresentationElement> views)
		{
			DisplayRelatedSymbolsInfo info = new DisplayRelatedSymbolsInfo(Collections.emptySet());

			info.setCreateNewSymbols(true);
			info.setCreateAnnotatedElements(false);
			info.setCreateContainment(false);
			info.setCreateComments(true);

			for (PresentationElement view : views)
			{
                try
                {
                    DisplayRelatedSymbols.displayRelatedSymbols(view, info);
                }
                catch (ReadOnlyElementException e)
                {
                    e.printStackTrace();
                }
            }
		}
	}
}