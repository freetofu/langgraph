package com.nomagic.magicdraw.examples.testframework;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.components.mdbasiccomponents.Component;

/**
 * MagicDraw application sample to test.
 */
public class ComponentCreator
{
	/**
	 * Creates component with given name in target package.
	 *
	 * @param name          created component name.
	 * @param targetPackage parent package for created component.
	 * @return created component.
	 * @throws ReadOnlyElementException if model is read only.
	 */
	public static Component createComponent(String name, Package targetPackage) throws ReadOnlyElementException
	{
		Project project = Project.getProject(targetPackage);
		Application.getInstance().getProjectsManager().setActiveProject(project);
		SessionManager.getInstance().createSession(project, "Component creation");

		Component component = project.getElementsFactory().createComponentInstance();
		component.setName(name);

		ModelElementsManager.getInstance().addElement(component, targetPackage);

		SessionManager.getInstance().closeSession(project);

		return component;
	}
}
