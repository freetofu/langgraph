package magicgptplugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleJsonParser
{
	private final String input;
	private int index;

	private SimpleJsonParser(String input)
	{
		this.input = input;
		this.index = 0;
	}

	public static Object parse(String input)
	{
		if (input == null)
		{
			return null;
		}
		SimpleJsonParser parser = new SimpleJsonParser(input);
		Object value = parser.parseValue();
		parser.skipWhitespace();
		return value;
	}

	private Object parseValue()
	{
		skipWhitespace();
		if (index >= input.length())
		{
			return null;
		}
		char current = input.charAt(index);
		if (current == '{')
		{
			return parseObject();
		}
		if (current == '[')
		{
			return parseArray();
		}
		if (current == '"')
		{
			return parseString();
		}
		if (current == '-' || Character.isDigit(current))
		{
			return parseNumber();
		}
		if (input.startsWith("true", index))
		{
			index += 4;
			return Boolean.TRUE;
		}
		if (input.startsWith("false", index))
		{
			index += 5;
			return Boolean.FALSE;
		}
		if (input.startsWith("null", index))
		{
			index += 4;
			return null;
		}
		return null;
	}

	private Map<String, Object> parseObject()
	{
		Map<String, Object> map = new HashMap<>();
		index++; // skip '{'
		while (true)
		{
			skipWhitespace();
			if (index >= input.length())
			{
				return map;
			}
			char current = input.charAt(index);
			if (current == '}')
			{
				index++;
				return map;
			}
			String key = parseString();
			skipWhitespace();
			if (index < input.length() && input.charAt(index) == ':')
			{
				index++;
			}
			Object value = parseValue();
			map.put(key, value);
			skipWhitespace();
			if (index < input.length() && input.charAt(index) == ',')
			{
				index++;
				continue;
			}
		}
	}

	private List<Object> parseArray()
	{
		List<Object> list = new ArrayList<>();
		index++; // skip '['
		while (true)
		{
			skipWhitespace();
			if (index >= input.length())
			{
				return list;
			}
			char current = input.charAt(index);
			if (current == ']')
			{
				index++;
				return list;
			}
			Object value = parseValue();
			list.add(value);
			skipWhitespace();
			if (index < input.length() && input.charAt(index) == ',')
			{
				index++;
			}
		}
	}

	private String parseString()
	{
		StringBuilder builder = new StringBuilder();
		if (input.charAt(index) == '"')
		{
			index++;
		}
		while (index < input.length())
		{
			char current = input.charAt(index++);
			if (current == '"')
			{
				break;
			}
			if (current == '\\' && index < input.length())
			{
				char escaped = input.charAt(index++);
				switch (escaped)
				{
					case '"':
						builder.append('"');
						break;
					case '\\':
						builder.append('\\');
						break;
					case '/':
						builder.append('/');
						break;
					case 'b':
						builder.append('\b');
						break;
					case 'f':
						builder.append('\f');
						break;
					case 'n':
						builder.append('\n');
						break;
					case 'r':
						builder.append('\r');
						break;
					case 't':
						builder.append('\t');
						break;
					case 'u':
						if (index + 4 <= input.length())
						{
							String hex = input.substring(index, index + 4);
							index += 4;
							try
							{
								builder.append((char) Integer.parseInt(hex, 16));
							}
							catch (NumberFormatException ignored)
							{
								builder.append("\\u").append(hex);
							}
						}
						break;
					default:
						builder.append(escaped);
						break;
				}
			}
			else
			{
				builder.append(current);
			}
		}
		return builder.toString();
	}

	private Number parseNumber()
	{
		int start = index;
		if (input.charAt(index) == '-')
		{
			index++;
		}
		while (index < input.length() && Character.isDigit(input.charAt(index)))
		{
			index++;
		}
		if (index < input.length() && input.charAt(index) == '.')
		{
			index++;
			while (index < input.length() && Character.isDigit(input.charAt(index)))
			{
				index++;
			}
		}
		if (index < input.length() && (input.charAt(index) == 'e' || input.charAt(index) == 'E'))
		{
			index++;
			if (index < input.length() && (input.charAt(index) == '+' || input.charAt(index) == '-'))
			{
				index++;
			}
			while (index < input.length() && Character.isDigit(input.charAt(index)))
			{
				index++;
			}
		}
		String value = input.substring(start, index);
		try
		{
			if (value.contains(".") || value.contains("e") || value.contains("E"))
			{
				return Double.parseDouble(value);
			}
			return Long.parseLong(value);
		}
		catch (NumberFormatException error)
		{
			return 0;
		}
	}

	private void skipWhitespace()
	{
		while (index < input.length() && Character.isWhitespace(input.charAt(index)))
		{
			index++;
		}
	}
}
