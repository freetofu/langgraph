package magicgptplugin;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.ui.ProjectWindowsManager;
import com.nomagic.magicdraw.ui.WindowComponentInfo;
import com.nomagic.magicdraw.ui.browser.Browser;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class MagicGPTPlugin extends Plugin
{
	public static final WindowComponentInfo PANEL_INFO = new WindowComponentInfo(
			"MAGICGPT_PANEL",
			"MagicGPT",
			null,
			ProjectWindowsManager.SIDE_WEST,
			ProjectWindowsManager.STATE_DOCKED,
			true
	);

	@Override
	public void init()
	{
		Browser.addBrowserInitializer(new Browser.BrowserInitializer()
		{
			@Override
			public void init(Browser browser, Project project)
			{
				browser.addPanel(new MagicGPTPanel(PANEL_INFO));
			}

			@Override
			public WindowComponentInfoRegistration getInfo()
			{
				return new WindowComponentInfoRegistration(PANEL_INFO, null);
			}
		});

		ActionsConfiguratorsManager.getInstance()
				.addMainMenuConfigurator(new MagicGPTMenuConfigurator(new OpenMagicGPTAction()));
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

	private static class OpenMagicGPTAction extends NMAction
	{
		private static final String MESSAGE = "MagicGPT panel is available in the Documentation tab.";

		public OpenMagicGPTAction()
		{
			super("MagicGPT.OpenPanel", "Open MagicGPT Panel", null, null);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Application.getInstance().getGUILog().showMessage(MESSAGE);
			JOptionPane.showMessageDialog(
					Application.getInstance().getMainFrame(),
					MESSAGE,
					"MagicGPT",
					JOptionPane.INFORMATION_MESSAGE
			);
		}
	}

	private static class MagicGPTMenuConfigurator implements AMConfigurator
	{
		private static final String CATEGORY = "Tools";
		private final NMAction action;

		public MagicGPTMenuConfigurator(NMAction action)
		{
			this.action = action;
		}

		@Override
		public void configure(ActionsManager manager)
		{
			ActionsCategory category = (ActionsCategory) manager.getActionFor(CATEGORY);
			if (category == null)
			{
				category = new MDActionsCategory(CATEGORY, CATEGORY);
				category.setNested(true);
				manager.addCategory(category);
			}
			category.addAction(action);
		}

		@Override
		public int getPriority()
		{
			return AMConfigurator.MEDIUM_PRIORITY;
		}
	}
}
