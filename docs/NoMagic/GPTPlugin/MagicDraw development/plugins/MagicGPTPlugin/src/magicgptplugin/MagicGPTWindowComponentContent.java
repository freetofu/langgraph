package magicgptplugin;

import com.nomagic.magicdraw.ui.browser.WindowComponentContent;

import java.awt.*;

public class MagicGPTWindowComponentContent implements WindowComponentContent
{
	private final Component component;

	public MagicGPTWindowComponentContent(Component component)
	{
		this.component = component;
	}

	@Override
	public Component getWindowComponent()
	{
		return component;
	}

	@Override
	public Component getDefaultFocusComponent()
	{
		return component;
	}
}
