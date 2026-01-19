package magicgptplugin;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.ui.ProjectWindowsManager;
import com.nomagic.magicdraw.ui.WindowComponentInfo;
import com.nomagic.magicdraw.ui.browser.Browser;

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
