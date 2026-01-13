package com.nomagic.magicdraw.examples.browserpanel;
/*
 * Copyright (c) 2013 NoMagic, Inc. All Rights Reserved.
 */

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.ui.ProjectWindowsManager;
import com.nomagic.magicdraw.ui.WindowComponentInfo;
import com.nomagic.magicdraw.ui.browser.Browser;
import com.nomagic.magicdraw.ui.browser.WindowComponent;
import com.nomagic.magicdraw.ui.browser.WindowComponentContent;
import com.nomagic.ui.ExtendedPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Simple browser panel. Which will be visible only if project
 * is opened. Panel is added to "Documentation" tab.
 *
 * @author Modestas
 */
public class BrowserPanelPlugin extends Plugin
{
	/**
	 * Info of component used 2 times
	 * 1 - on plugin load, to register component.
	 * 2 - on project load.
	 */
	@SuppressWarnings("ConstantConditions")
	private static final WindowComponentInfo info = new WindowComponentInfo("COMPONENT_ID",
																			"COMPONENT_NAME",
																			null,  //icon
																			ProjectWindowsManager.SIDE_WEST,
																			ProjectWindowsManager.STATE_DOCKED,
																			true);

	@Override
	public void init()
	{
		Browser.addBrowserInitializer(new Browser.BrowserInitializer()
		{
			@Override
			public void init(Browser browser, Project project)
			{
				browser.addPanel(new BrowserPanel());
			}

			@Override
			public WindowComponentInfoRegistration getInfo()
			{
				return new WindowComponentInfoRegistration(info, null);
			}
		});
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
	 * Simple panel with label in it.
	 */
	private static class BrowserPanel extends ExtendedPanel implements WindowComponent
	{
		/**
		 * Constructor.
		 */
		public BrowserPanel()
		{
			JLabel label = new JLabel("BrowserPanel");
			label.setHorizontalAlignment(SwingConstants.CENTER);
			add(label, BorderLayout.CENTER);
		}

		@Override
		public WindowComponentInfo getInfo()
		{
			return info;
		}

		@Override
		public WindowComponentContent getContent()
		{
			return new BrowserWindowComponentContext(this);
		}
	}

	/**
	 * Real component which is added to window.
	 */
	private static class BrowserWindowComponentContext implements WindowComponentContent
	{
		private final JPanel panel;

		/**
		 * Constructor.
		 *
		 * @param panel which will be added to window.
		 */
		public BrowserWindowComponentContext(JPanel panel)
		{
			this.panel = panel;
		}

		@Override
		public Component getWindowComponent()
		{
			return panel;
		}

		@Override
		public Component getDefaultFocusComponent()
		{
			return panel;
		}
	}
}
