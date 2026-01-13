package com.nomagic.magicdraw.examples.testframework;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.tests.MagicDrawApplication;
import com.nomagic.magicdraw.uml.ConvertElementInfo;
import com.nomagic.magicdraw.uml.Refactoring;
import com.nomagic.uml2.ext.magicdraw.classes.mdinterfaces.Interface;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.components.mdbasiccomponents.Component;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test example using JUnit5 framework
 *
 * @author Edgaras Dulskis
 * @goal test if element can by converted to other type of element using OpenAPI
 */
@ExtendWith(MagicDrawApplication.class)
public class ConvertElementTest
{
	private static Project project;

	@BeforeAll
	static void createProject()
	{
		project = Application.getInstance().getProjectsManager().createProject();
	}

	@Test
	void convertClassToComponentWithSameID() throws ReadOnlyElementException
	{
		Element classInstance = createClass("A");
		ConvertElementInfo info = new ConvertElementInfo(Component.class);
		info.setPreserveElementID(true);
		String oldId = classInstance.getID();

		SessionManager.getInstance().createSession(project, "convert to component");
		Element converted = Refactoring.Converting.convert(classInstance, info);
		SessionManager.getInstance().closeSession(project);

		assertTrue(converted instanceof Component);
		assertEquals(oldId, converted.getID());
	}

	@Test
	void convertClassToInterfaceWithNewID() throws ReadOnlyElementException
	{
		Element classInstance = createClass("A");
		ConvertElementInfo info = new ConvertElementInfo(Interface.class);
		info.setPreserveElementID(false);
		String oldId = classInstance.getID();

		SessionManager.getInstance().createSession(project, "convert to interface");
		Element converted = Refactoring.Converting.convert(classInstance, info);
		SessionManager.getInstance().closeSession(project);

		assertTrue(converted instanceof Interface);
		assertNotEquals(oldId, converted.getID());
	}

	private static Element createClass(String name)
	{
		SessionManager.getInstance().createSession(project, "create class " + name);
		Class classInstance = project.getElementsFactory().createClassInstance();
		classInstance.setOwner(project.getPrimaryModel());
		classInstance.setName(name);
		SessionManager.getInstance().closeSession(project);
		return classInstance;
	}

	@AfterAll
	static void closeProject()
	{
		Application.getInstance().getProjectsManager().closeProject();
		//noinspection ConstantConditions
		project = null;
	}
}
