/*
 * Copyright (c) 2012 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.notifications;

import com.nomagic.annotation.OpenApiAll;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.magicdraw.ui.dialogs.selection.ElementSelectionDlg;
import com.nomagic.magicdraw.ui.dialogs.selection.ElementSelectionDlgFactory;
import com.nomagic.magicdraw.ui.notification.HRefRunnable;
import com.nomagic.magicdraw.ui.notification.Notification;
import com.nomagic.magicdraw.ui.notification.NotificationManager;
import com.nomagic.magicdraw.ui.notification.NotificationSeverity;
import com.nomagic.magicdraw.ui.notification.config.NotificationViewConfig;

import java.awt.*;

/**
 * Notifications API usage sample.
 *
 * @author Paulius Grigaliunas
 */
@SuppressWarnings({"UnusedDeclaration"})
@OpenApiAll
public class NotificationsSample
{
	/**
	 * Shows simple application notification in lower right corner
	 */
	public void showApplicationNotification()
	{
		NotificationManager.getInstance().showNotification(new Notification("notificationID", "Hello World", null));
	}

	/**
	 * Shows container error notification in Element Selection Dialog
	 */
	public void showContainerNotification()
	{
		// please note that SimpleBaseDialog implements NotificationsContainer interface
		ElementSelectionDlg dlg = ElementSelectionDlgFactory.create(MDDialogParentProvider.getProvider().getDialogOwner());

		NotificationManager.getInstance().showNotification(new Notification("notificationID", "Container error notification", null, NotificationSeverity.ERROR), dlg);

		dlg.setVisible(true);
	}

	/**
	 * Shows custom application notification with link that shows simple greeting dialog
	 */
	public void showCustomNotification()
	{
		HRefRunnable showHelp = new HRefRunnable("http://showDlg", "show", true)
		{
			@Override
			public void run()
			{
				Application.getInstance().getGUILog().showMessage("Hello");
			}
		};

		NotificationViewConfig myConfig = new NotificationViewConfig();
		myConfig.setBackgroundColor(Color.WHITE);
		// hide notification after 5 seconds.
		myConfig.setExpirationTime(5);

		NotificationManager.getInstance().showNotification(new Notification("notificationID", "Show dialog", null, new HRefRunnable[] {showHelp}), myConfig);
	}
}
