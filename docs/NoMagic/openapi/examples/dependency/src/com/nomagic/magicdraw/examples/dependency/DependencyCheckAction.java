package com.nomagic.magicdraw.examples.dependency;

import com.nomagic.magicdraw.actions.ActionsGroups;
import com.nomagic.magicdraw.actions.MDAction;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.dependency.DependencyCheckResult;
import com.nomagic.magicdraw.dependency.DependencyCheckerHelper;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Example action that invokes dependency checker via open API and displays validation result.
 *
 * @author Rimvydas Vaidelis
 * @version 1.0
 */
public class DependencyCheckAction extends MDAction
{
	/**
	 * Creates and initializes a new <code>DependencyCheckAction</code> object.
	 */
	public DependencyCheckAction()
	{
		super("DEPENDENCY_CHECK_EXAMPLE", "Dependency Checker", null, ActionsGroups.UML_PROJECT_OPENED_RELATED);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Project project = Application.getInstance().getProject();
		if (project != null)
		{
			// check dependencies between modules and project
			DependencyCheckResult result = DependencyCheckerHelper.checkDependencies(project);
			JOptionPane
					.showMessageDialog(MDDialogParentProvider.getProvider().getDialogOwner(), result.toString(), "Dependency Checker", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
