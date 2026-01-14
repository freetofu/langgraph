package magicgptplugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GptService
{
	private static final String DEFAULT_API_URL = "https://api.openai.com/v1/chat/completions";
	private static final String DEFAULT_MODEL = "gpt-4o-mini";
	private static final int DEFAULT_TIMEOUT_SECONDS = 45;

	public GptResponse ask(ContextPayload context)
	{
		String apiKey = readEnv("MAGICGPT_API_KEY");
		if (apiKey == null)
		{
			return GptResponse.failure("MAGICGPT_API_KEY 환경 변수가 설정되지 않았습니다.");
		}

		String apiUrl = readEnvOrDefault("MAGICGPT_API_URL", DEFAULT_API_URL);
		String model = readEnvOrDefault("MAGICGPT_MODEL", DEFAULT_MODEL);
		int timeoutSeconds = readTimeoutSeconds();

		String payload = buildPayload(context, model);

		try
		{
			HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
			connection.setRequestMethod("POST");
			connection.setConnectTimeout(timeoutSeconds * 1000);
			connection.setReadTimeout(timeoutSeconds * 1000);
			connection.setDoOutput(true);
			connection.setRequestProperty("Authorization", "Bearer " + apiKey);
			connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

			try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8))
			{
				writer.write(payload);
			}

			int status = connection.getResponseCode();
			String body = readResponseBody(status < 400 ? connection.getInputStream() : connection.getErrorStream());
			if (status < 200 || status >= 300)
			{
				return GptResponse.failure("API 호출 실패: HTTP " + status + " - " + truncate(body));
			}

			String content = extractFirstContent(body);
			if (content == null || content.isEmpty())
			{
				return GptResponse.failure("응답에서 content를 찾지 못했습니다.");
			}
			return GptResponse.success(content);
		}
		catch (IOException error)
		{
			return GptResponse.failure("API 연결 중 오류가 발생했습니다: " + error.getMessage());
		}
	}

	private static String buildPayload(ContextPayload context, String model)
	{
		String systemPrompt = "당신은 CATIA Magic에서 동작하는 MagicGPT 플러그인입니다. "
			+ "사용자 질의를 시스템 엔지니어링 관점으로 해석해 명확하고 간결한 답변을 제공합니다.";
		String userPrompt = "사용자 질의: " + context.getQuery() + "\n"
			+ "컨텍스트 요약: " + context.getContextSummary();

		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append("\"model\":\"").append(escapeJson(model)).append("\",");
		builder.append("\"messages\":[");
		builder.append("{\"role\":\"system\",\"content\":\"").append(escapeJson(systemPrompt)).append("\"},");
		builder.append("{\"role\":\"user\",\"content\":\"").append(escapeJson(userPrompt)).append("\"}");
		builder.append("]");
		builder.append("}");
		return builder.toString();
	}

	private static String readEnv(String key)
	{
		String value = System.getenv(key);
		if (value == null || value.trim().isEmpty())
		{
			return null;
		}
		return value.trim();
	}

	private static String readEnvOrDefault(String key, String defaultValue)
	{
		String value = readEnv(key);
		return value == null ? defaultValue : value;
	}

	private static int readTimeoutSeconds()
	{
		String value = readEnv("MAGICGPT_TIMEOUT_SECONDS");
		if (value == null)
		{
			return DEFAULT_TIMEOUT_SECONDS;
		}
		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException error)
		{
			return DEFAULT_TIMEOUT_SECONDS;
		}
	}

	private static String readResponseBody(InputStream stream) throws IOException
	{
		if (stream == null)
		{
			return "";
		}
		StringBuilder builder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)))
		{
			String line;
			while ((line = reader.readLine()) != null)
			{
				builder.append(line);
			}
		}
		return builder.toString();
	}

	private static String extractFirstContent(String responseBody)
	{
		int contentIndex = responseBody.indexOf("\"content\"");
		if (contentIndex < 0)
		{
			return null;
		}
		int colonIndex = responseBody.indexOf(":", contentIndex);
		if (colonIndex < 0)
		{
			return null;
		}
		int quoteIndex = responseBody.indexOf("\"", colonIndex + 1);
		if (quoteIndex < 0)
		{
			return null;
		}
		return parseJsonString(responseBody, quoteIndex + 1);
	}

	private static String parseJsonString(String source, int startIndex)
	{
		StringBuilder builder = new StringBuilder();
		boolean escaping = false;
		for (int i = startIndex; i < source.length(); i++)
		{
			char current = source.charAt(i);
			if (escaping)
			{
				switch (current)
				{
					case 'n':
						builder.append('\n');
						break;
					case 't':
						builder.append('\t');
						break;
					case 'r':
						builder.append('\r');
						break;
					case '\\':
					case '"':
						builder.append(current);
						break;
					default:
						builder.append(current);
						break;
				}
				escaping = false;
			}
			else if (current == '\\')
			{
				escaping = true;
			}
			else if (current == '"')
			{
				return builder.toString();
			}
			else
			{
				builder.append(current);
			}
		}
		return builder.toString();
	}

	private static String escapeJson(String input)
	{
		if (input == null)
		{
			return "";
		}
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < input.length(); i++)
		{
			char current = input.charAt(i);
			switch (current)
			{
				case '\\':
					builder.append("\\\\");
					break;
				case '"':
					builder.append("\\\"");
					break;
				case '\n':
					builder.append("\\n");
					break;
				case '\r':
					builder.append("\\r");
					break;
				case '\t':
					builder.append("\\t");
					break;
				default:
					builder.append(current);
					break;
			}
		}
		return builder.toString();
	}

	private static String truncate(String body)
	{
		if (body == null)
		{
			return "";
		}
		int max = 200;
		if (body.length() <= max)
		{
			return body;
		}
		return body.substring(0, max) + "...";
	}
}
