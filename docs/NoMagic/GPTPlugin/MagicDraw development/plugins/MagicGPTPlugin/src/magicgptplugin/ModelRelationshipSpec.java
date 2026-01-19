package magicgptplugin;

public class ModelRelationshipSpec
{
	private final String type;
	private final String source;
	private final String target;

	public ModelRelationshipSpec(String type, String source, String target)
	{
		this.type = type;
		this.source = source;
		this.target = target;
	}

	public String getType()
	{
		return type;
	}

	public String getSource()
	{
		return source;
	}

	public String getTarget()
	{
		return target;
	}
}
