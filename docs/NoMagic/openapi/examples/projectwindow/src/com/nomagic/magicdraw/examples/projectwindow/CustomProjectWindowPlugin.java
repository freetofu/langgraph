/*
 * Copyright (c) 2002 No Magic, Inc. All Rights Reserved.
 */

package com.nomagic.magicdraw.examples.projectwindow;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.ui.*;
import com.nomagic.magicdraw.ui.browser.WindowComponentContent;
import com.nomagic.ui.ScalableImageIcon;

import javax.swing.*;
import java.awt.*;

/**
 * Example-Plugin for showing how to add a custom project window.
 *
 * @author Mindaugas Ringys
 */
public class CustomProjectWindowPlugin extends Plugin
{
	@Override
	public void init()
	{
		ProjectWindowsManager.ConfiguratorRegistry.addConfigurator(new CustomProjectWindowsConfigurator());
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

	private static class CustomProjectWindowsConfigurator implements ProjectWindowsConfigurator
	{
		public static final String ID = "CUSTOM_PROJECT_WINDOW_PANEL_ID";

		public static final Icon WINDOW_ICON = new ScalableImageIcon(CustomProjectWindowsConfigurator.class, "custom_window.png");

		@Override
		public void configure(Project project, ProjectWindowsManager projectWindowsManager)
		{
			addProjectWindow(project, projectWindowsManager);
		}

		@SuppressWarnings("UnusedReturnValue")
		public static ProjectWindow addProjectWindow(Project project, ProjectWindowsManager projectWindowsManager)
		{
			ProjectWindow projectWindow = new ProjectWindow(createWindowComponentInfo(), new CustomProjectWindowComponentContent());
			projectWindowsManager.addWindow(project, projectWindow);

			return projectWindow;
		}

		private static WindowComponentInfo createWindowComponentInfo()
		{
			return new WindowComponentInfo(ID,
										   "Custom Window",
										   WINDOW_ICON,
										   WindowsManager.SIDE_EAST,
										   WindowsManager.STATE_DOCKED, false);
		}
	}

	private static class CustomProjectWindowComponentContent implements WindowComponentContent
	{
		private final JPanel mPanel;

		public CustomProjectWindowComponentContent()
		{
			mPanel = new JPanel();
			mPanel.add(new JLabel("This is a custom window"));
		}

		@Override
		public Component getWindowComponent()
		{
			return mPanel;
		}

		@Override
		public Component getDefaultFocusComponent()
		{
			return null;
		}
	}
}
