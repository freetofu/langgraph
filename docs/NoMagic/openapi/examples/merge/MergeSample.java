/*
 * Copyright (c) 2008 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.merge;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectDescriptor;
import com.nomagic.magicdraw.core.project.ProjectDescriptorsFactory;
import com.nomagic.magicdraw.core.project.ProjectsManager;
import com.nomagic.magicdraw.diff.Difference;
import com.nomagic.magicdraw.diff.ElementAddition;
import com.nomagic.magicdraw.diff.ElementDeletion;
import com.nomagic.magicdraw.esi.EsiUtils;
import com.nomagic.magicdraw.merge.*;
import com.nomagic.magicdraw.merge.macro.MacroChange;
import com.nomagic.magicdraw.teamwork2.ITeamworkService;
import com.nomagic.magicdraw.teamwork2.TeamworkService;
import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Operation;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.utils.ErrorHandler;

import javax.annotation.CheckForNull;
import java.io.File;
import java.util.Iterator;
import java.util.Set;

/**
 * Project merge API usage sample.
 * This sample requires Project Merge plugin.
 *
 * @author Martynas Lelevicius
 */
@SuppressWarnings({"squid:S106", "squid:S1148", "UnusedDeclaration"})
public class MergeSample
{
	/**
	 * 2-way merge - merge source changes to target project.
	 *
	 * @param targetProject target project.
	 * @param sourceFile    source project file.
	 * @return merged project.
	 */
	@CheckForNull
	public static Project standard2wayMerge(Project targetProject, File sourceFile)
	{
		final ProjectDescriptor source = getProjectDescriptor(sourceFile);
		if (source == null)
		{
			return null;
		}
		if (MergeUtil.merge(targetProject, source, null, null, new SimpleErrorHandler(), Optimization.PERFORMANCE))
		{
			// merged
			return Application.getInstance().getProjectsManager().getActiveProject();
		}
		// project not merged
		return null;
	}

	/**
	 * 3-way merge - merge branch changes to trunk.
	 *
	 * @param projectName remote project name.
	 * @param branchName  branch name.
	 * @return merged project or null.
	 * @throws Exception in case of some error.
	 */
	@CheckForNull
	public static Project standard3wayMerge(String projectName, String branchName) throws Exception
	{
		ProjectDescriptor targetDescriptor = EsiUtils.get().getProjectDescriptorByQualifiedName(projectName);
		if (targetDescriptor == null)
		{
			return null;
		}

		// load target project
		final ProjectsManager projectsManager = Application.getInstance().getProjectsManager();
		projectsManager.loadProject(targetDescriptor, true);
		final Project targetProject = projectsManager.getActiveProject();
		if (targetProject == null)
		{
			return null;
		}

		// source is latest version from branch
		final ProjectDescriptor sourceDescriptor = EsiUtils.get().getDescriptorForBranch(targetDescriptor, branchName);
		final long sourceVersion = EsiUtils.get().getLastVersion(sourceDescriptor);

		// prepare ancestor
		final ProjectDescriptor ancestorDescriptor = MergeUtil.createRemoteAncestorDescriptorForMerge(targetProject, sourceVersion, true);
		if (ancestorDescriptor == null)
		{
			return null;
		}

		// merge project
		if (MergeUtil.merge(targetProject, sourceDescriptor, ancestorDescriptor, ConflictResolution.TARGET_PREFERRED, new SimpleErrorHandler(),
							Optimization.PERFORMANCE))
		{
			// merged
			return projectsManager.getActiveProject();
		}
		// not merged
		return null;
	}

	/**
	 * Advanced 3-way merge - merge all changes except deletion of property "myProperty" and addition of operation "myOperation".
	 *
	 * @param targetProject target project.
	 * @param sourceFile,   source project file.
	 * @param ancestorFile  ancestor project file.
	 * @return merged project.
	 */
	@CheckForNull
	public static Project advanced3wayMerge(Project targetProject, File sourceFile, File ancestorFile)
	{
		final ProjectDescriptor source = getProjectDescriptor(sourceFile);
		if (source == null)
		{
			return null;
		}
		final ProjectDescriptor ancestor = getProjectDescriptor(ancestorFile);
		if (ancestor == null)
		{
			return null;
		}

		// NOTE! MagicDraw loads ancestor (in a 3-way merge) and source projects (if they are not loaded yet),
		// also changes active projects during merge. Your code must not load any additional projects on merge.

		// if you are going access the target or source projects after diff - use Optimization.PERFORMANCE,
		// when using Optimization.MEMORY - source and target projects are closed to reduce memory usage.
		final Optimization optimization = Optimization.PERFORMANCE;

		// get differences
		final ProjectDifference projectDifference = CompareUtil.getDifference(targetProject, source, ancestor, new SimpleErrorHandler(), optimization);

		if (projectDifference != null)
		{
			final ProjectsManager projectsManager = Application.getInstance().getProjectsManager();

			final Set<Change> sourceChanges = projectDifference.getSourceChanges();
			final Set<Change> targetChanges = projectDifference.getTargetChanges();

			if (!targetChanges.isEmpty() || !sourceChanges.isEmpty())
			{
				// standard change acceptance - accept all target changes and non conflicting source changes
				// swap passed parameters to accept all source changes and non conflicting target changes
				MergeUtil.acceptChanges(targetChanges, sourceChanges);

				Change deletion = null;
				Change addition = null;
				for (Iterator<Change> changeIterator = targetChanges.iterator(); changeIterator.hasNext() && (deletion == null || addition == null); )
				{
					final Change change = changeIterator.next();
					if (change.getState().equals(ChangeState.ACCEPTED))
					{
						for (Iterator<Difference> diffIterator = ((MacroChange) change).getDifference().getDifferences().iterator();
							 diffIterator.hasNext() && (deletion == null || addition == null); )
						{
							final Difference difference = diffIterator.next();
							if (deletion == null && difference instanceof ElementDeletion elementDeletion)
							{
								// get ancestor project so we can search for "deleted" element
								final Project ancestorProject = projectsManager.findProject(ancestor);
								if (ancestorProject != null)
								{
									final String elementID = elementDeletion.getElementID();
									final BaseElement elementByID = ancestorProject.getElementByID(elementID);
									if (elementByID instanceof Property && "myProperty".equals(((Property) elementByID).getName()))
									{
										// deleted Property "myProperty"
										deletion = change;
									}
								}
							}
							else if (addition == null && difference instanceof ElementAddition elementAddition)
							{
								// use Optimization.PERFORMANCE option when getting differences to make sure the target project is not closed
								final String elementID = elementAddition.getElementID();
								final BaseElement elementByID = targetProject.getElementByID(elementID);
								if (elementByID instanceof Operation && "myOperation".equals(((Operation) elementByID).getName()))
								{
									// added(created) Operation "myOperation"
									addition = change;
								}
							}
						}
					}
				}
				if (deletion != null)
				{
					// reject element deletion
					MergeUtil.setChangeState(deletion, ChangeState.REJECTED);
				}
				if (addition != null)
				{
					// reject element addition
					MergeUtil.setChangeState(addition, ChangeState.REJECTED);
				}

				if (MergeUtil.getConflictingChanges(projectDifference.getTargetChanges()).isEmpty() || MergeUtil.showMergeGUI(projectDifference))
				{
					// if there are no conflicts or we resolved them via GUI
					// apply changes
					MergeUtil.applyChanges(projectDifference, new SimpleErrorHandler());

					// merged
					// NOTE that merged project is not the same as targetProject after 3 way merge
					return projectsManager.getActiveProject();
				}
			}
			else
			{
				// on diff MagicDraw may load projects, lock teamwork elements so need to restore previous state after diff
				CompareUtil.restore(projectDifference);
			}
		}
		return null;
	}

	@CheckForNull
	private static ProjectDescriptor getProjectDescriptor(File sourceFile)
	{
		return ProjectDescriptorsFactory.createProjectDescriptor(sourceFile.toURI());
	}

	private static class SimpleErrorHandler implements ErrorHandler<Exception>
	{
		@Override
		public void error(Exception ex) throws Exception
		{
			// just print stack trace
			ex.printStackTrace();
		}
	}
}
