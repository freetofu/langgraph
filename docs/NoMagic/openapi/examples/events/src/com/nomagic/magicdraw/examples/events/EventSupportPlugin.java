/*
 * Copyright (c) 2010 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.events;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectEventListenerAdapter;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.uml2.ext.jmi.smartlistener.Registration;
import com.nomagic.uml2.ext.jmi.smartlistener.SmartListenerConfig;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Classifier;
import com.nomagic.uml2.impl.PropertyNames;
import com.nomagic.uml2.transaction.TransactionManager;

/**
 * Plugin dedicated to provide MagicDraw event listening samples. This events sample include: <br> <ul><li>transaction
 * commit listener, see {@link MyTransactionListener} </li><li> Simple property change listener registered to
 * element</li> <li>Listener registered to SmartEventSupport, to receive events provided by configurator from any
 * Classifier Element.</li> </ul> <br> Using this sample, every project will have transaction commit listener which
 * listens to new attribute create event and updates classifiers (attribute owner) name. For each new created attribute
 * new listener will be registered which will listen for "is derived" property change. <br> Additionally project will
 * have smart listener which listens to owned attributes name or isStatic property change of any Classifiers instance.
 *
 * @author Justinas Bisikirskas
 */
public class EventSupportPlugin extends Plugin
{
	// transaction listener.
	private MyTransactionListener mTransactionListener;

	@Override
	public void init()
	{
		mTransactionListener = new MyTransactionListener();

		Application.getInstance().getProjectsManager().addProjectListener(new ProjectEventListenerAdapter()
		{
			@Override
			public void projectOpened(Project project)
			{
				TransactionManager transactionManager = project.getRepository().getTransactionManager();
				transactionManager.addTransactionCommitListener(mTransactionListener);
				registerListenerToSmartEventSupport(project);
			}

			@Override
			public void projectClosed(Project project)
			{
				project.getRepository().getTransactionManager().removeTransactionCommitListener(mTransactionListener);
			}

		});
	}

	/**
	 * Registers listener to {@link com.nomagic.uml2.ext.jmi.smartlistener.SmartEventSupport}, for Classifiers to receive
	 * events when it's owned attribute Static or Name property value are changed.
	 *
	 * @param project project where listener is registered.
	 */
	private static void registerListenerToSmartEventSupport(Project project)
	{
		//this config will listen for Owned attribute static and Name property value changes, for example: if Classifier has attribute, and this listener is registered to it, it will receive events if any of attribute name or static property is changed.
		SmartListenerConfig cfg = new SmartListenerConfig();
		SmartListenerConfig config = cfg.listenToNested(PropertyNames.OWNED_ATTRIBUTE);
		config.listenTo(PropertyNames.IS_STATIC);
		config.listenTo(PropertyNames.NAME);
		// register at event support.
		Registration registration = new Registration(Classifier.class, cfg, new AnyPropertyChangeListener(), "examples EventSupportPlugin");
		project.getSmartEventSupport().register(registration);
	}

	@Override
	public boolean close()
	{
		return true;
	}

	@Override
	public boolean isSupported()
	{
		return true;
	}

}