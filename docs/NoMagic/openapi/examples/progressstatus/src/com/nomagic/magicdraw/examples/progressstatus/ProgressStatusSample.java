/*
 * Copyright (c) 2012 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.progressstatus;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.task.BackgroundTaskRunner;
import com.nomagic.magicdraw.ui.MagicDrawProgressStatusRunner;
import com.nomagic.task.ProgressStatus;
import com.nomagic.task.RunnableWithProgress;
import com.nomagic.ui.ProgressStatusRunner;

/**
 * Task with progress status execution sample.
 *
 * @author Martynas Lelevicius
 */
@SuppressWarnings({"squid:S106", "squid:S1148", "UnusedDeclaration"})
public class ProgressStatusSample
{
	/**
	 * MagicDraw UI is not locked during execution - single change affects UI.
	 *
	 * @param project project.
	 */
	public static void nonLockedExecution(final Project project)
	{
		ProgressStatusRunner.runWithProgressStatus(createRunnable(project), "Progress Test", true, 0);
	}

	/**
	 * MagicDraw UI is locked during execution - UI is updated only when task finishes (single changes does not affect UI).
	 *
	 * @param project project.
	 */
	public static void lockedExecution(final Project project)
	{
		MagicDrawProgressStatusRunner.runWithProgressStatus(createRunnable(project), "Locked Progress Test", true, 0);
	}

	/**
	 * Execute task in background.
	 *
	 * @param allowCancel true if cancel should be enabled.
	 */
	public static void backgroundExecution(boolean allowCancel)
	{
		BackgroundTaskRunner.runWithProgressStatus(progressStatus -> {
			final int max = 200;
			progressStatus.init("My Progress...", 0, max);
			for (int i = 0; i < max && !progressStatus.isCancel(); ++i)
			{
				progressStatus.increase();
				sleep(100);
			}
		}, "My Task", allowCancel);
	}

	private static void sleep(int millis)
	{
		try
		{
			Thread.sleep(millis);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Create task runnable.
	 *
	 * @param project project.
	 * @return runnable.
	 */
	private static SampleRunnableWithProgress createRunnable(final Project project)
	{
		return new SampleRunnableWithProgress(project, 3000);
	}

	/**
	 * Simple task:
	 * <ol>
	 * <li>Create class in project model.</li>
	 * <li>Rename class.</li>
	 * </ol>
	 */
	private static class SampleRunnableWithProgress implements RunnableWithProgress
	{
		private final Project mProject;
		private final int mStepPause;

		SampleRunnableWithProgress(final Project project, final int stepPause)
		{
			mProject = project;
			mStepPause = stepPause;
		}

		@Override
		public void run(final ProgressStatus progressStatus)
		{
			progressStatus.setCurrent(0);
			progressStatus.setMin(0);
			progressStatus.setMax(8);

			step("Creating session1...", progressStatus);
			SessionManager.getInstance().createSession(mProject, "Create class");

			step("Creating class...", progressStatus);
			final com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class aClass = mProject.getElementsFactory()
					.createClassInstance();

			step("Setting class owner...", progressStatus);
			aClass.setOwner(mProject.getPrimaryModel());

			step("Closing session1...", progressStatus);
			SessionManager.getInstance().closeSession(mProject);

			step("Creating session2...", progressStatus);
			SessionManager.getInstance().createSession(mProject, "Rename class");

			step("Renaming class...", progressStatus);
			aClass.setName("myClass");

			step("Closing session2...", progressStatus);
			SessionManager.getInstance().closeSession(mProject);

			step("Finishing...", progressStatus);
		}

		private void step(final String description, final ProgressStatus progressStatus)
		{
			progressStatus.increase();
			progressStatus.setDescription(description);
			// pause to see changes
			sleep(mStepPause);
		}
	}
}
