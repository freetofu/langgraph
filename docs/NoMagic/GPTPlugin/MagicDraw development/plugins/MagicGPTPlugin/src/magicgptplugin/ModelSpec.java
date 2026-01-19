package magicgptplugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModelSpec
{
	private final String packageName;
	private final List<ModelElementSpec> elements;
	private final List<ModelRelationshipSpec> relationships;
	private final List<ModelDiagramSpec> diagrams;

	public ModelSpec(String packageName, List<ModelElementSpec> elements, List<ModelRelationshipSpec> relationships,
		List<ModelDiagramSpec> diagrams)
	{
		this.packageName = packageName;
		this.elements = elements == null ? new ArrayList<>() : new ArrayList<>(elements);
		this.relationships = relationships == null ? new ArrayList<>() : new ArrayList<>(relationships);
		this.diagrams = diagrams == null ? new ArrayList<>() : new ArrayList<>(diagrams);
	}

	public String getPackageName()
	{
		return packageName;
	}

	public List<ModelElementSpec> getElements()
	{
		return Collections.unmodifiableList(elements);
	}

	public List<ModelRelationshipSpec> getRelationships()
	{
		return Collections.unmodifiableList(relationships);
	}

	public List<ModelDiagramSpec> getDiagrams()
	{
		return Collections.unmodifiableList(diagrams);
	}
}
