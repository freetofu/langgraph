package com.nomagic.magicdraw.examples.job;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectEventListenerAdapter;
import com.nomagic.magicdraw.job.IdleJobService;
import com.nomagic.magicdraw.job.Job;
import com.nomagic.magicdraw.plugins.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Demonstrates job service usage. 
 * The job is created when project is opened and removed when project is closed.
 * The job is temporarily removed/added during project deactivate/activate.
 * 
 * @author Mindaugas Genutis
 */
public class JobExample extends Plugin
{
	// We track jobs by project.
	private final Map<Project, Job> jobByProject = new HashMap<>();

	/**
	 * @see com.nomagic.magicdraw.plugins.Plugin#init()
	 */
	@Override
	public void init()
	{
		Application.getInstance().addProjectEventListener(new ProjectEventListenerAdapter()
		{
			@Override
			public void projectOpened(Project project)
			{
				// Create a job and assign it to the project.
				Job job = new ExampleJob(project);
				IdleJobService.getInstance().addJob(job);
				jobByProject.put(project, job);
			}

			@Override
			public void projectActivated(Project project)
			{
				// Add the job back when project is activated again.
				Job job = jobByProject.get(project);
				IdleJobService.getInstance().addJob(job);
			}

			@Override
			public void projectDeActivated(Project project)
			{
				// Remove the job temporarily when project is deactivated.
				Job job = jobByProject.get(project);
				IdleJobService.getInstance().removeJob(job);
			}

			@Override
			public void projectPreClosed(Project project)
			{
				// Remove the job when project is closed.
				Job job = jobByProject.get(project);
				jobByProject.remove(project);
				IdleJobService.getInstance().removeJob(job);
			}
		});
	}

	/**
	 * @see com.nomagic.magicdraw.plugins.Plugin#close()
	 */
	@Override
	public boolean close()
	{
		return true;
	}

	/**
	 * @see com.nomagic.magicdraw.plugins.Plugin#isSupported()
	 */
	@Override
	public boolean isSupported()
	{
		return true;
	}
}
