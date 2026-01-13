package com.nomagic.magicdraw.examples.elementselection;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsManager;
import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.actions.ActionsGroups;
import com.nomagic.magicdraw.actions.ActionsID;
import com.nomagic.magicdraw.actions.MDAction;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.magicdraw.ui.dialogs.SelectElementInfo;
import com.nomagic.magicdraw.ui.dialogs.SelectElementTypes;
import com.nomagic.magicdraw.ui.dialogs.selection.ElementSelectionDlg;
import com.nomagic.magicdraw.ui.dialogs.selection.ElementSelectionDlgFactory;
import com.nomagic.magicdraw.ui.dialogs.selection.TypeFilter;
import com.nomagic.magicdraw.ui.dialogs.selection.TypeFilterImpl;
import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.magicdraw.uml.ClassTypes;
import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.magicdraw.utils.MDLog;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Example plugin which demonstrates element selection Open API.
 *
 * @author Mindaugas Genutis
 */
public class ElementSelectionExamplePlugin extends Plugin
{
	@Override
	public void init()
	{
		addMainMenuActions();
	}

	/**
	 * Adds main menu actions.
	 */
	private static void addMainMenuActions()
	{
		ActionsConfiguratorsManager.getInstance().addMainMenuConfigurator(new AMConfigurator()
		{
			@Override
			public void configure(ActionsManager manager)
			{
				final NMAction action = manager.getActionFor(ActionsID.EDIT);
				if (action != null)
				{
					action.addAction(new SelectElementsAction());
				}
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
	static class SelectElementsAction extends MDAction
	{
		/**
		 * Constructs this action.
		 */
		public SelectElementsAction()
		{
			super("SELECT_ELEMENTS_FROM_PLUGIN", "Select Elements From Plugin", null,
				  ActionsGroups.UML_PROJECT_OPENED_RELATED);
		}

		@Override
		public void actionPerformed(ActionEvent evt)
		{
			ElementSelectionDlg elementSelectionDlg = createElementSelectionDialog();

			elementSelectionDlg.setVisible(true);

			if (elementSelectionDlg.isOkClicked())
			{
				List<BaseElement> selectedElements = elementSelectionDlg.getSelectedElements();

				for (BaseElement selectedElement : selectedElements)
				{
					MDLog.getGeneralLog().info(selectedElement.getHumanName());
				}
			}
		}

		/**
		 * Constructs element selection dialog.
		 *
		 * @return created element selection dialog.
		 */
		private static ElementSelectionDlg createElementSelectionDialog()
		{
			// Only properties and their subtypes are offered to select.
			List<java.lang.Class> types = ClassTypes.getSubtypes(Property.class);
			SelectElementTypes selectElementTypes = new SelectElementTypes(types, types, null, types);

			// Available properties are filtered so that only the ones which start with 'p' are selected.
			final Collection<Property> candidates = getSelectionCandidates("p");

			TypeFilter selectableFilter = new TypeFilterImpl(selectElementTypes.select)
			{
				@Override
				public boolean accept(BaseElement baseElement, boolean checkType)
				{
					//noinspection SuspiciousMethodCalls
					return super.accept(baseElement, checkType) && candidates.contains(baseElement);
				}
			};

			TypeFilter visibleFilter = new TypeFilterImpl(selectElementTypes.display)
			{
				@Override
				public boolean accept(BaseElement baseElement, boolean checkType)
				{
					//noinspection SuspiciousMethodCalls
					return super.accept(baseElement, checkType) && candidates.contains(baseElement);
				}
			};

			ElementSelectionDlg selectionDlg = ElementSelectionDlgFactory
					.create(MDDialogParentProvider.getProvider().getDialogOwner(), "Select properties which start with 'p'", null);

			SelectElementInfo selectElementInfo = new SelectElementInfo(true, false, null, true);
			// Gets elements which are initially selected in the dialog.
			List<Property> initialSelection = getInitialSelection(candidates);
			ElementSelectionDlgFactory.initMultiple(selectionDlg, selectElementInfo, visibleFilter, selectableFilter, selectElementTypes.usedAsTypes,
													selectElementTypes.create, initialSelection);

			return selectionDlg;
		}

		/**
		 * Gets elements which are initially selected in the dialog.
		 * We select only those properties which have Class set as their types.
		 *
		 * @param candidates candidate elements from which to select.
		 * @return a list of initially selected elements.
		 */
		private static List<Property> getInitialSelection(Collection<Property> candidates)
		{
			return candidates.stream()
					.filter(property -> property.getType() instanceof Class)
					.collect(Collectors.toList());
		}

		/**
		 * Gets candidates which should be offered in the selection dialog.
		 *
		 * @param start start of the name by which to filter elements.
		 * @return candidates which are offered to select in the selection dialog.
		 */
		private static Collection<Property> getSelectionCandidates(String start)
		{
			final Collection<Property> properties = new ArrayList<>();

			final Project project = Application.getInstance().getProject();
			if (project != null)
			{
				final Collection<Property> candidates = Finder.byTypeRecursively().find(project, new java.lang.Class[] {Property.class}, false);
				for (Property property : candidates)
				{
					if (property.getName().startsWith(start))
					{
						properties.add(property);
					}
				}
			}
			return properties;
		}
	}
}