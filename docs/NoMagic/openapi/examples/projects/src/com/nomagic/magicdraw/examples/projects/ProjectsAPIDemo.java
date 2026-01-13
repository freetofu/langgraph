/*
 *
 * Copyright (c) 2003 No Magic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.projects;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.actions.ActionsGroups;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectDescriptor;
import com.nomagic.magicdraw.core.project.ProjectDescriptorsFactory;
import com.nomagic.magicdraw.core.project.ProjectsManager;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.magicdraw.utils.MDLog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URI;
import java.util.List;

/**
 * This is a plugin that show how to manage project.
 *
 * @author Nerijus Jankevicius
 */
public class ProjectsAPIDemo extends Plugin
{
	@Override
	public void init()
	{
		ActionsConfiguratorsManager manager = ActionsConfiguratorsManager.getInstance();
		manager.addMainMenuConfigurator(new MainMenuConfigurator(getProjectsActions()));
	}

	private static NMAction getProjectsActions()
	{
		ActionsCategory category = new ActionsCategory(null, "Projects");
		// this call makes submenu.
		category.setNested(true);

		category.addAction(new NewProjectAction());
		category.addAction(new CloseProjectAction());
		category.addAction(new ChangeActiveProjectAction());
		category.addAction(new ShowLocationAction());
		category.addAction(new SaveAllAction());
		category.addAction(new SilentSaveAllAction());
		category.addAction(new LoadFromLocationAction());
		category.addAction(new SaveIntoLocationAction());
		category.addAction(new ReloadCurrentProjectAction());

		return category;
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

	private static class ShowLocationAction extends NMAction
	{
		public ShowLocationAction()
		{
			super("Show Location", "Show Location", null, ActionsGroups.ANY_PROJECT_OPENED_RELATED);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			StringBuilder locations = new StringBuilder();
			Project prj = Application.getInstance().getProjectsManager().getActiveProject();
			//noinspection ConstantConditions
			List<ProjectDescriptor> projectDescriptors = ProjectDescriptorsFactory.getAvailableDescriptorsForProject(prj);
			for (int i = projectDescriptors.size() - 1; i >= 0; --i)
			{
				URI uri = projectDescriptors.get(i).getURI();
				if (uri != null)
				{
					String location = uri.toString();
					if (location != null)
					{
						locations.append(location).append("\n");
					}
				}
			}
			MDLog.getGeneralLog().info("locations = " + locations);
			JOptionPane.showMessageDialog(MDDialogParentProvider.getProvider().getDialogOwner(), locations.toString(), "Location", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private static class SaveAllAction extends NMAction
	{
		public SaveAllAction()
		{
			super("Save All", "Save All", null, ActionsGroups.ANY_PROJECT_OPENED_RELATED);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			ProjectsManager projectsManager = Application.getInstance().getProjectsManager();
			for (Project project : projectsManager.getProjects())
			{
				for (ProjectDescriptor projectDescriptor : ProjectDescriptorsFactory.getAvailableDescriptorsForProject(project))
				{
					projectsManager.saveProject(projectDescriptor, false);
				}
			}
		}
	}

	private static class SilentSaveAllAction extends NMAction
	{
		public SilentSaveAllAction()
		{
			super("Silent Save All", "Silent Save All", null, ActionsGroups.ANY_PROJECT_OPENED_RELATED);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			ProjectsManager projectsManager = Application.getInstance().getProjectsManager();
			for (Project project : projectsManager.getProjects())
			{
				for (ProjectDescriptor projectDescriptor : ProjectDescriptorsFactory.getAvailableDescriptorsForProject(project))
				{
					projectsManager.saveProject(projectDescriptor, true);
				}
			}
		}
	}

	private static class LoadFromLocationAction extends NMAction
	{
		public LoadFromLocationAction()
		{
			super("Load from Location", "Load from Location", null, null);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			String location = JOptionPane.showInputDialog("Enter location:");
			if (location != null)
			{
				File file = new File(location);
				if (file.exists())
				{
					ProjectDescriptor des = ProjectDescriptorsFactory.createProjectDescriptor(file.toURI());
					//noinspection ConstantConditions
					Application.getInstance().getProjectsManager().loadProject(des, true);
				}
			}
		}
	}

	private static class SaveIntoLocationAction extends NMAction
	{
		public SaveIntoLocationAction()
		{
			super("Save into Location", "Save into Location", null, ActionsGroups.ANY_PROJECT_OPENED_RELATED);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			String location = JOptionPane.showInputDialog("Enter location:");
			if (location != null)
			{
				ProjectDescriptor des = ProjectDescriptorsFactory
						.createLocalProjectDescriptor(Application.getInstance().getProjectsManager().getActiveProject(), new File(location));
				Application.getInstance().getProjectsManager().saveProject(des, true);
			}
		}
	}

	private static class ReloadCurrentProjectAction extends NMAction
	{
		public ReloadCurrentProjectAction()
		{
			super("Reload current project", "Reload current project", null, ActionsGroups.ANY_PROJECT_EDIT_RELATED);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			ProjectsManager projectsManager = Application.getInstance().getProjectsManager();
			//noinspection ConstantConditions
			ProjectDescriptor projectDescriptor = ProjectDescriptorsFactory.getDescriptorForProject(projectsManager.getActiveProject());
			projectsManager.loadProject(projectDescriptor, true);
		}
	}

	private static class NewProjectAction extends NMAction
	{
		public NewProjectAction()
		{
			super("New Project", "New Project", null, null);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Application.getInstance().getProjectsManager().createProject();
		}
	}

	private static class CloseProjectAction extends NMAction
	{
		public CloseProjectAction()
		{
			super("Close Project", "Close Project", null, ActionsGroups.ANY_PROJECT_OPENED_RELATED);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Application.getInstance().getProjectsManager().closeProject();
		}
	}

	private static class ChangeActiveProjectAction extends NMAction
	{
		public ChangeActiveProjectAction()
		{
			super("Change Active Project", "Change Active Project", null, null);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			ProjectsManager manager = Application.getInstance().getProjectsManager();
			manager.getProjects().stream()
					.filter(project -> !manager.isProjectActive(project))
					.findFirst()
					.ifPresent(manager::setActiveProject);
		}
	}

	public static class MainMenuConfigurator implements AMConfigurator
	{

		String EXAMPLES = "Examples";

		/**
		 * Action will be added to manager.
		 */
		private final NMAction action;

		/**
		 * Creates configurator.
		 *
		 * @param action action to be added to main menu.
		 */
		public MainMenuConfigurator(NMAction action)
		{
			this.action = action;
		}

		/**
		 * @see com.nomagic.actions.AMConfigurator#configure(com.nomagic.actions.ActionsManager)
		 * Methods adds action to given manager Examples category.
		 */
		@Override
		public void configure(ActionsManager mngr)
		{
			// searching for Examples action category
			ActionsCategory category = (ActionsCategory) mngr.getActionFor(EXAMPLES);

			if (category == null)
			{
				// creating new category
				category = new MDActionsCategory(EXAMPLES, EXAMPLES);
				category.setNested(true);
				mngr.addCategory(category);
			}
			category.addAction(action);
		}

		@Override
		public int getPriority()
		{
			return AMConfigurator.MEDIUM_PRIORITY;
		}
	}
}
