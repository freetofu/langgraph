package com.nomagic.magicdraw.examples.testframework;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.tests.MagicDrawTestCase;
import com.nomagic.magicdraw.tests.common.TestEnvironment;
import com.nomagic.magicdraw.tests.common.comparators.ProjectsComparator;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.components.mdbasiccomponents.Component;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * MagicDraw test case sample using JUnit3. Represents MagicDraw test case development basics.
 */
public class ComponentCreatorTest extends MagicDrawTestCase
{
	private Project testProject;
	private Project correctProject;
	
	private final File testProjectFile;
	private final File correctProjectFile;
	
	public static final String TEST_CASE_DATA_DIRECTORY = "component_creator_test";
	public static final String TEST_PROJECTS_DIRECTORY = "test_projects";
	private static final String CORRECT_PROJECTS_DIRECTORY = "correct_projects";
	private final String componentName;
	
	public ComponentCreatorTest(File testProjectFile, File correctProjectFile, String componentName)
	{
		super("testCreateComponent", "testCreateComponent_on_"+correctProjectFile.getName()+" with "+componentName);
		
		this.testProjectFile = testProjectFile;
		this.correctProjectFile = correctProjectFile;
		this.componentName = componentName;
	}
	
	@Override
	protected void setUpTest() throws Exception
	{
		super.setUpTest();
		
		testProject = openProject(testProjectFile.getAbsolutePath());
		correctProject = openProject(correctProjectFile.getAbsolutePath());
	}
	
	/**
	 * Test case for checking if component with specific name and parent element created in the project.
	 */
	public void testCreateComponent()
	{
		getLogger().info("testCreateComponent has been started with "+testProject.getName()+" and "+correctProject.getName()+" projects.");
		
		assertNotNull("Test project is not loaded.", testProject);
		assertNotNull("Correct project is not loaded.", correctProject);
		
		try
		{
			Package parentElement = testProject.getPrimaryModel();
			
			Component createdComponent = ComponentCreator.createComponent(componentName, parentElement);
			
			assertNotNull("Component is not created.", createdComponent);
			assertEquals("Incorrect created component name.", componentName, createdComponent.getName());
			assertEquals("Incorrect created component parent element.", parentElement, createdComponent.getOwner());
			
			ProjectsComparator projectComparator = createProjectComparator("");
			assertTrue("Test project does not match correct project!", projectComparator.compare(testProject, correctProject));
		}
		catch (ReadOnlyElementException e)
		{
			fail("Component was not created because model is read only");
		}
		
		getLogger().info("testCreateComponent has been finished successfully");
	}
	
	@Override
	protected void tearDownTest() throws Exception
	{
		super.tearDownTest();
		closeAllProjects();
	}
	
	private static Properties loadPropertiesFromFile(File propertiesFile) throws IOException
	{
		Properties properties = new Properties();
	
		properties.load(new FileInputStream(propertiesFile));
		
		return properties;
	}
	
	/**
	 * Prepares test suite with this test case instances for each
	 * MagicDraw test project found in resources/test_projects directory.
	 * 
	 * @return Test suite with ComponentCreatorTest instances created for each test project.
	 * @throws Exception if properties file for the found project cannot be accessed.
	 */
	public static Test suite() throws Exception
	{
		TestSuite suite = new TestSuite();
		List<File> testProjectFiles = TestEnvironment.getProjects(TEST_CASE_DATA_DIRECTORY+"/"+TEST_PROJECTS_DIRECTORY);
		File correctProjectsDirectory = new File(TestEnvironment.getResourceDir(), TEST_CASE_DATA_DIRECTORY+"/"+CORRECT_PROJECTS_DIRECTORY);
		
		for (File testProjectFile : testProjectFiles)
		{
			Properties projectTestProperties = loadPropertiesFromFile(getPropertiesFile(testProjectFile));
			File correctProjectFile = new File(correctProjectsDirectory, projectTestProperties.getProperty("correct_project_name"));
			
			String componentName = projectTestProperties.getProperty("component_name");
			Test componentCreatorTest = new ComponentCreatorTest(testProjectFile, correctProjectFile, componentName);
			suite.addTest(componentCreatorTest);
		}
		
		return suite;
	}
}
