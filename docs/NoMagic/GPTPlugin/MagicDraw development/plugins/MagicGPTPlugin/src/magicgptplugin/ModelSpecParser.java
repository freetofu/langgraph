package magicgptplugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModelSpecParser
{
	public static ParseResult parse(String responseText)
	{
		String json = extractJson(responseText);
		if (json == null)
		{
			return ParseResult.failure("No JSON object found in response.");
		}

		Object parsed = SimpleJsonParser.parse(json);
		if (!(parsed instanceof Map))
		{
			return ParseResult.failure("JSON root must be an object.");
		}

		Map<?, ?> root = (Map<?, ?>) parsed;
		String packageName = readString(root.get("package"));
		List<ModelElementSpec> elements = readElements(root.get("elements"));
		List<ModelRelationshipSpec> relationships = readRelationships(root.get("relationships"));
		List<ModelDiagramSpec> diagrams = readDiagrams(root.get("diagrams"));
		ModelSpec spec = new ModelSpec(packageName, elements, relationships, diagrams);
		return ParseResult.success(spec, json);
	}

	private static String extractJson(String text)
	{
		if (text == null)
		{
			return null;
		}
		int start = text.indexOf('{');
		int end = text.lastIndexOf('}');
		if (start < 0 || end <= start)
		{
			return null;
		}
		return text.substring(start, end + 1);
	}

	private static List<ModelElementSpec> readElements(Object value)
	{
		List<ModelElementSpec> elements = new ArrayList<>();
		if (!(value instanceof List))
		{
			return elements;
		}
		for (Object item : (List<?>) value)
		{
			if (!(item instanceof Map))
			{
				continue;
			}
			Map<?, ?> map = (Map<?, ?>) item;
			String type = readString(map.get("type"));
			String name = readString(map.get("name"));
			String text = readString(map.get("text"));
			if (name == null || name.trim().isEmpty())
			{
				continue;
			}
			elements.add(new ModelElementSpec(type, name, text));
		}
		return elements;
	}

	private static List<ModelRelationshipSpec> readRelationships(Object value)
	{
		List<ModelRelationshipSpec> relationships = new ArrayList<>();
		if (!(value instanceof List))
		{
			return relationships;
		}
		for (Object item : (List<?>) value)
		{
			if (!(item instanceof Map))
			{
				continue;
			}
			Map<?, ?> map = (Map<?, ?>) item;
			String type = readString(map.get("type"));
			String source = readString(map.get("source"));
			String target = readString(map.get("target"));
			if (source == null || target == null)
			{
				continue;
			}
			relationships.add(new ModelRelationshipSpec(type, source, target));
		}
		return relationships;
	}

	private static String readString(Object value)
	{
		if (value == null)
		{
			return null;
		}
		if (value instanceof String)
		{
			return ((String) value).trim();
		}
		return String.valueOf(value).trim();
	}

	private static List<ModelDiagramSpec> readDiagrams(Object value)
	{
		List<ModelDiagramSpec> diagrams = new ArrayList<>();
		if (!(value instanceof List))
		{
			return diagrams;
		}
		for (Object item : (List<?>) value)
		{
			if (!(item instanceof Map))
			{
				continue;
			}
			Map<?, ?> map = (Map<?, ?>) item;
			String type = readString(map.get("type"));
			String name = readString(map.get("name"));
			List<String> elementNames = readStringList(map.get("elements"));
			diagrams.add(new ModelDiagramSpec(type, name, elementNames));
		}
		return diagrams;
	}

	private static List<String> readStringList(Object value)
	{
		List<String> result = new ArrayList<>();
		if (value instanceof List)
		{
			for (Object item : (List<?>) value)
			{
				String name = readString(item);
				if (name != null && !name.isEmpty())
				{
					result.add(name);
				}
			}
			return result;
		}
		String single = readString(value);
		if (single != null && !single.isEmpty())
		{
			result.add(single);
		}
		return result;
	}

	public static class ParseResult
	{
		private final boolean success;
		private final String errorMessage;
		private final ModelSpec spec;
		private final String rawJson;

		private ParseResult(boolean success, String errorMessage, ModelSpec spec, String rawJson)
		{
			this.success = success;
			this.errorMessage = errorMessage;
			this.spec = spec;
			this.rawJson = rawJson;
		}

		public static ParseResult success(ModelSpec spec, String rawJson)
		{
			return new ParseResult(true, null, spec, rawJson);
		}

		public static ParseResult failure(String errorMessage)
		{
			return new ParseResult(false, errorMessage, null, null);
		}

		public boolean isSuccess()
		{
			return success;
		}

		public String getErrorMessage()
		{
			return errorMessage;
		}

		public ModelSpec getSpec()
		{
			return spec;
		}

		public String getRawJson()
		{
			return rawJson;
		}
	}
}
