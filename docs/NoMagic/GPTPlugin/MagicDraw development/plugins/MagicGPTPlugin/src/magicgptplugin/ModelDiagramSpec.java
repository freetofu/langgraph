package magicgptplugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModelDiagramSpec
{
	private final String type;
	private final String name;
	private final List<String> elementNames;

	public ModelDiagramSpec(String type, String name, List<String> elementNames)
	{
		this.type = type;
		this.name = name;
		this.elementNames = elementNames == null ? new ArrayList<>() : new ArrayList<>(elementNames);
	}

	public String getType()
	{
		return type;
	}

	public String getName()
	{
		return name;
	}

	public List<String> getElementNames()
	{
		return Collections.unmodifiableList(elementNames);
	}
}
