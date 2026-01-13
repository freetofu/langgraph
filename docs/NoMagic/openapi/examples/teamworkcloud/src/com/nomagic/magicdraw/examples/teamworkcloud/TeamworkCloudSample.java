/*
 * Copyright (c) 2015 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.teamworkcloud;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.ProjectUtilities;
import com.nomagic.magicdraw.core.project.ProjectDescriptor;
import com.nomagic.magicdraw.esi.EsiUtils;
import com.nomagic.magicdraw.teamwork2.ITeamworkService;
import com.nomagic.magicdraw.teamwork2.ServerLoginInfo;

import javax.annotation.CheckForNull;
import java.net.URI;

/**
 * CEDW API Usage sample.
 * @see  EsiUtils
 * @author Donatas Simkunas
 */
@SuppressWarnings({"unused", "squid:S106", "ConstantConditions"})
public class TeamworkCloudSample
{
	/**
	 * Login to server running on localhost with user Administrator.
	 */
	public void login()
	{
		EsiUtils.getTeamworkService().login(new ServerLoginInfo("localhost", "Administrator", "Administrator", false), true);
	}

	/**
	 * Prints all projects from server
	 * @throws Exception
	 */
	public void listProjects() throws Exception
	{
		for (ProjectDescriptor projectDescriptor : EsiUtils.get().getRemoteProjectDescriptors())
		{
			System.out.println(projectDescriptor.getRepresentationString());
		}
	}

	/**
	 * Create new project in the server and rename it to "newName".
	 * @return created project.
	 * @throws Exception
	 */
	public Project createProject() throws Exception
	{
		final Project project = EsiUtils.get().createProject("Project1", "category");

		final org.eclipse.emf.common.util.URI locationURI = project.getPrimaryProject().getLocationURI();
		ProjectDescriptor projectDescriptorFound = getExistingProjectDescriptor(locationURI);

		final String newName = "newName";
		EsiUtils.setProjectName(projectDescriptorFound, newName);

		return project;
	}

	/**
	 * Find project descriptor for given URI
	 * @param locationURI project URI
	 * @return descriptor for project.
	 * @throws Exception
	 */
	@CheckForNull
	private static ProjectDescriptor getExistingProjectDescriptor(org.eclipse.emf.common.util.URI locationURI) throws Exception
	{
		final URI projectURI = ProjectUtilities.getURI(locationURI);
		return EsiUtils.get().getRemoteProjectDescriptors().stream()
				.filter(projectDescriptor -> projectDescriptor.getURI().equals(projectURI))
				.findFirst()
				.orElse(null);
	}


	/**
	 * Find project descriptor using project name
	 * @param projectName project name
	 * @return project descriptor or null
	 */
	@CheckForNull
	public static ProjectDescriptor getExistingProjectDescriptor(String projectName) throws Exception
	{
		return EsiUtils.get().getProjectDescriptorByQualifiedName(projectName);
	}


}
