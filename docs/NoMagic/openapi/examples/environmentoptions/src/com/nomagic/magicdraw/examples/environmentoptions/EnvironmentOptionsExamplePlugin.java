package com.nomagic.magicdraw.examples.environmentoptions;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.options.EnvironmentOptions;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.utils.MDLog;

/**
 * Example plugin which demonstrates usage of Environment Options Open API.
 *
 * @author Mindaugas Genutis
 */
public class EnvironmentOptionsExamplePlugin extends Plugin
{
	/**
	 * Make sure environment listener is a field, because it is registered as a weak reference in MagicDraw.
	 */
	@SuppressWarnings({"FieldCanBeLocal"})
	private EnvironmentOptions.EnvironmentChangeListener mEnvironmentOptionsListener;

	@Override
	public void init()
	{
		configureEnvironmentOptions();
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

	/**
	 * Configures example environment options.
	 */
	private void configureEnvironmentOptions()
	{
		Application application = Application.getInstance();
		EnvironmentOptions options = application.getEnvironmentOptions();
		options.addGroup(new ExampleOptionsGroup());

		mEnvironmentOptionsListener = props -> {
			MDLog.getGeneralLog().info("Environment options changed:");

			for (Object o : props)
			{
				MDLog.getGeneralLog().info(o);
			}
		};

		options.addEnvironmentChangeListener(mEnvironmentOptionsListener);
	}
}