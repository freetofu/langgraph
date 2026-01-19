package magicgptplugin;

public class ModelElementSpec
{
	private final String type;
	private final String name;
	private final String text;

	public ModelElementSpec(String type, String name, String text)
	{
		this.type = type;
		this.name = name;
		this.text = text;
	}

	public String getType()
	{
		return type;
	}

	public String getName()
	{
		return name;
	}

	public String getText()
	{
		return text;
	}
}
