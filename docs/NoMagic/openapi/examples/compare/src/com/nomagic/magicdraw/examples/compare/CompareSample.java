package com.nomagic.magicdraw.examples.compare;

import com.nomagic.annotation.Used;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectDescriptor;
import com.nomagic.magicdraw.core.project.ProjectDescriptorsFactory;
import com.nomagic.magicdraw.diff.Difference;
import com.nomagic.magicdraw.diff.ElementModification;
import com.nomagic.magicdraw.diff.ModificationInfo;
import com.nomagic.magicdraw.diff.PrimitiveValueModificationInfo;
import com.nomagic.magicdraw.diff.macro.MacroDifference;
import com.nomagic.magicdraw.merge.Change;
import com.nomagic.magicdraw.merge.CompareUtil;
import com.nomagic.magicdraw.merge.Optimization;
import com.nomagic.magicdraw.merge.ProjectDifference;
import com.nomagic.magicdraw.merge.macro.MacroChange;
import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.uml2.impl.PropertyNames;
import com.nomagic.utils.ErrorHandler;

import javax.annotation.CheckForNull;
import java.io.File;

/**
 * Project compare API usage sample.
 *
 * @author Martynas Lelevicius
 */
public class CompareSample
{

	/**
	 * Find elements that changed name.
	 *
	 * @param project         project.
	 * @param baseProjectFile base project file.
	 */
	@Used("sample")
	public static void findElementsWithChangedName(Project project, File baseProjectFile)
	{
		final ProjectDescriptor baseProjectDescriptor = getProjectDescriptor(baseProjectFile);
		if (baseProjectDescriptor == null)
		{
			return;
		}

		// if you are going access the target or source projects after diff - use Optimization.PERFORMANCE,
		// when using Optimization.MEMORY - source project is closed to reduce memory usage.
		final Optimization optimization = Optimization.PERFORMANCE;
		// compare projects
		final ProjectDifference projectDifference = CompareUtil.compareProjects(project, baseProjectDescriptor, new SimpleErrorHandler(), optimization);
		if (projectDifference != null)
		{
			for (Change change : projectDifference.getChanges())
			{
				final MacroDifference macroDifference = ((MacroChange) change).getDifference();
				for (Difference difference : macroDifference.getDifferences())
				{
					// analyze difference
					if (difference instanceof ElementModification elementModification)
					{
						if (PropertyNames.NAME.equals(elementModification.getChangedPropertyName()))
						{
							final BaseElement elementByID = project.getElementByID(elementModification.getElementID());
							if (elementByID != null)
							{
								// name changed for element
								final ModificationInfo modificationInfo = elementModification.getModificationInfo();
								System.out.println("Name for " + elementByID.getHumanName() + " changed to \"" +
												   ((PrimitiveValueModificationInfo) modificationInfo).getValue() + "\"");
							}
						}
					}
				}
			}

			// on diff MagicDraw may load projects, so need to restore previous state after diff
			CompareUtil.restore(projectDifference);
		}
	}

	/**
	 * Compare projects and display project difference GUI.
	 *
	 * @param project         project to compare.
	 * @param baseProjectFile base project file.
	 */
	public static void compareProjects(Project project, File baseProjectFile)
	{
		final ProjectDescriptor baseProjectDescriptor = getProjectDescriptor(baseProjectFile);
		if (baseProjectDescriptor != null)
		{
			final ProjectDifference difference = CompareUtil.compareProjects(project, baseProjectDescriptor, new SimpleErrorHandler(), Optimization.MEMORY);
			if (difference != null)
			{
				if (difference.getChanges().isEmpty())
				{
					// on diff MagicDraw may load projects, so need to restore previous state after diff
					CompareUtil.restore(difference);
				}
				else
				{
					CompareUtil.showDifferenceGUI(difference);
				}
			}
		}
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
