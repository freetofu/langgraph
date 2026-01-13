package com.nomagic.magicdraw.examples.job;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.job.Job;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.task.ProgressStatus;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.impl.ElementsFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A job which creates a class in the model and sets current time as its name.
 * 
 * @author Mindaugas Genutis
 */
public class ExampleJob implements Job
{
	private final Project mProject;

	public ExampleJob(Project project)
	{
		mProject = project;
	}

	@Override
	public boolean needsExecute()
	{
		return true;
	}

	@Override
	public void execute(ProgressStatus progressStatus) throws Exception
	{
		Package model = mProject.getPrimaryModel();

		SessionManager sessionManager = SessionManager.getInstance();
		sessionManager.createSession(mProject, getName());

		createClass(model);

		sessionManager.closeSession(mProject);
	}

	private static void createClass(Package model)
	{
		ElementsFactory factory = Project.getProject(model).getElementsFactory();
		
		Class clazz = factory.createClassInstance();
		
		clazz.setOwner(model);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		clazz.setName(dateFormat.format(new Date()));
	}

	@Override
	public void finished()
	{

	}

	@Override
	public String getName()
	{
		return "Example Job";
	}
}
