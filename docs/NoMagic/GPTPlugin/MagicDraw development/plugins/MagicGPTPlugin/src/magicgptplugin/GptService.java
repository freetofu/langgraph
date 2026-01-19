package magicgptplugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class GptService
{
	private static final String DEFAULT_API_URL = "https://api.openai.com/v1/responses";
	private static final String DEFAULT_MODEL = "gpt-4o-mini";
	private static final int DEFAULT_TIMEOUT_SECONDS = 180;
	private static final int MAX_ATTACHMENT_CHARS = 200000;
	private static final int ATTACHMENT_CHUNK_SIZE = 15000;

	public GptResponse ask(ContextPayload context)
	{
		String apiKey = readEnv("MAGICGPT_API_KEY");
		if (apiKey == null)
		{
			apiKey = readEnv("OPENAI_API_KEY");
		}
		if (apiKey == null)
		{
			return GptResponse.failure("API key env var missing. Set MAGICGPT_API_KEY or OPENAI_API_KEY.");
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
				return GptResponse.failure("API call failed: HTTP " + status + " - " + truncate(body));
			}

			String content = extractFirstContent(body);
			if (content == null || content.isEmpty())
			{
				return GptResponse.failure("Response did not include any content.");
			}
			return GptResponse.success(content);
		}
		catch (IOException error)
		{
			return GptResponse.failure("API request failed: " + error.getMessage());
		}
	}

	private static String buildPayload(ContextPayload context, String model)
	{
		String systemPrompt = "You are MagicGPT running inside CATIA Magic. "
			+ "Respond ONLY with JSON matching this schema: "
			+ "{\"package\":\"<name>\",\"elements\":[{\"type\":\"Requirement\",\"name\":\"R1\",\"text\":\"...\"}],"
			+ "\"relationships\":[{\"type\":\"containment|deriveReqt|refine|trace|satisfy\",\"source\":\"R1\",\"target\":\"R2\"}],"
			+ "\"diagrams\":[{\"type\":\"requirements\",\"name\":\"<diagram name>\",\"elements\":[\"R1\",\"R2\"]}]}. "
			+ "Use containment for parent/child requirement relationships. "
			+ "Use descriptive requirement names based on the text content (avoid generic names like R1/R2). "
			+ "If attachment content or images are included in the user message, use them. "
			+ "When the user asks for a diagram, include a diagram type that matches one of the available diagram types "
			+ "listed in the context summary. "
			+ "If the context summary includes a preferred requirements diagram type, use that diagram type value. "
			+ "Do not include any extra text.";
		String userPrompt = "User query: " + context.getQuery() + "\n"
			+ "Context summary: " + context.getContextSummary();

		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append("\"model\":\"").append(escapeJson(model)).append("\",");
		builder.append("\"input\":[");
		builder.append("{\"role\":\"system\",\"content\":[{\"type\":\"input_text\",\"text\":\"")
			.append(escapeJson(systemPrompt)).append("\"}]},");
		builder.append("{\"role\":\"user\",\"content\":");
		builder.append(buildUserContentJson(userPrompt, context.getAttachments()));
		builder.append("}");
		builder.append("]");
		builder.append("}");
		return builder.toString();
	}

	private static String buildUserContentJson(String userPrompt, List<java.io.File> attachments)
	{
		List<java.io.File> files = attachments == null ? new ArrayList<>() : attachments;
		if (files.isEmpty())
		{
			return "[{\"type\":\"input_text\",\"text\":\"" + escapeJson(userPrompt) + "\"}]";
		}

		StringBuilder builder = new StringBuilder();
		builder.append("[");
		builder.append("{\"type\":\"input_text\",\"text\":\"").append(escapeJson(userPrompt)).append("\"}");
		for (java.io.File file : files)
		{
			appendAttachment(builder, file);
		}
		builder.append("]");
		return builder.toString();
	}

	private static void appendAttachment(StringBuilder builder, java.io.File file)
	{
		if (file == null || !file.exists())
		{
			return;
		}
		String name = file.getName();
		String lower = name.toLowerCase();

		if (isImageFile(lower))
		{
			String dataUrl = encodeImageDataUrl(file, lower);
			if (dataUrl != null)
			{
				builder.append(",{\"type\":\"input_image\",\"image_url\":\"")
					.append(escapeJson(dataUrl)).append("\"}");
			}
			else
			{
				builder.append(",{\"type\":\"input_text\",\"text\":\"")
					.append(escapeJson("Attachment " + name + " could not be read.")).append("\"}");
			}
			return;
		}

		if (lower.endsWith(".pdf"))
		{
			String text = extractPdfText(file, MAX_ATTACHMENT_CHARS);
			appendTextAttachment(builder, name, text);
			return;
		}

		if (isTextFile(lower))
		{
			String text = readTextFile(file, MAX_ATTACHMENT_CHARS);
			appendTextAttachment(builder, name, text);
			return;
		}

		if (isOfficeXmlFile(lower))
		{
			String text = extractOfficeText(file, MAX_ATTACHMENT_CHARS);
			appendTextAttachment(builder, name, text);
			return;
		}

		if (isBinaryDocumentFile(lower))
		{
			String snippet = readBinaryBase64(file, 20000);
			appendTextAttachment(builder, name + " (base64 snippet)", snippet);
			return;
		}

		builder.append(",{\"type\":\"input_text\",\"text\":\"")
			.append(escapeJson("Attachment " + name + " omitted (unsupported type).")).append("\"}");
	}

	private static boolean isImageFile(String name)
	{
		return name.endsWith(".png")
			|| name.endsWith(".jpg")
			|| name.endsWith(".jpeg")
			|| name.endsWith(".gif")
			|| name.endsWith(".bmp")
			|| name.endsWith(".webp");
	}

	private static boolean isTextFile(String name)
	{
		return name.endsWith(".txt")
			|| name.endsWith(".md")
			|| name.endsWith(".json")
			|| name.endsWith(".csv")
			|| name.endsWith(".xml")
			|| name.endsWith(".yaml")
			|| name.endsWith(".yml");
	}

	private static boolean isOfficeXmlFile(String name)
	{
		return name.endsWith(".docx")
			|| name.endsWith(".xlsx")
			|| name.endsWith(".pptx");
	}

	private static boolean isBinaryDocumentFile(String name)
	{
		return name.endsWith(".pdf")
			|| name.endsWith(".doc")
			|| name.endsWith(".xls")
			|| name.endsWith(".ppt");
	}

	private static String encodeImageDataUrl(java.io.File file, String name)
	{
		try
		{
			byte[] bytes = Files.readAllBytes(file.toPath());
			String mime = getImageMimeType(name);
			String base64 = Base64.getEncoder().encodeToString(bytes);
			return "data:" + mime + ";base64," + base64;
		}
		catch (IOException error)
		{
			return null;
		}
	}

	private static String getImageMimeType(String name)
	{
		if (name.endsWith(".png"))
		{
			return "image/png";
		}
		if (name.endsWith(".gif"))
		{
			return "image/gif";
		}
		if (name.endsWith(".bmp"))
		{
			return "image/bmp";
		}
		if (name.endsWith(".webp"))
		{
			return "image/webp";
		}
		return "image/jpeg";
	}

	private static String readTextFile(java.io.File file, int maxChars)
	{
		try
		{
			byte[] bytes = Files.readAllBytes(file.toPath());
			String content = new String(bytes, StandardCharsets.UTF_8);
			if (content.length() <= maxChars)
			{
				return content;
			}
			return content.substring(0, maxChars) + "...";
		}
		catch (IOException error)
		{
			return "(failed to read " + file.getName() + ")";
		}
	}

	private static String extractOfficeText(java.io.File file, int maxChars)
	{
		try (ZipFile zip = new ZipFile(file))
		{
			String name = file.getName().toLowerCase();
			StringBuilder builder = new StringBuilder();
			if (name.endsWith(".docx"))
			{
				appendZipEntryText(zip, "word/document.xml", builder, maxChars);
			}
			else if (name.endsWith(".pptx"))
			{
				appendZipEntriesByPrefix(zip, "ppt/slides/slide", builder, maxChars);
				appendZipEntriesByPrefix(zip, "ppt/notesSlides/notesSlide", builder, maxChars);
			}
			else if (name.endsWith(".xlsx"))
			{
				appendZipEntryText(zip, "xl/sharedStrings.xml", builder, maxChars);
				appendZipEntriesByPrefix(zip, "xl/worksheets/sheet", builder, maxChars);
			}

			String text = cleanupXmlText(builder.toString());
			if (text.isEmpty())
			{
				return "(no readable text found)";
			}
			if (text.length() > maxChars)
			{
				return text.substring(0, maxChars) + "...";
			}
			return text;
		}
		catch (IOException error)
		{
			return "(failed to read " + file.getName() + ")";
		}
	}

	private static String extractPdfText(java.io.File file, int maxChars)
	{
		try (PDDocument document = PDDocument.load(file))
		{
			PDFTextStripper stripper = new PDFTextStripper();
			String text = stripper.getText(document);
			if (text == null || text.trim().isEmpty())
			{
				return "(no readable text found)";
			}
			String trimmed = text.trim();
			if (trimmed.length() > maxChars)
			{
				return trimmed.substring(0, maxChars) + "...";
			}
			return trimmed;
		}
		catch (IOException error)
		{
			return "(failed to read " + file.getName() + ")";
		}
	}

	private static void appendZipEntryText(ZipFile zip, String entryName, StringBuilder builder, int maxChars)
		throws IOException
	{
		ZipEntry entry = zip.getEntry(entryName);
		if (entry == null)
		{
			return;
		}
		try (InputStream stream = zip.getInputStream(entry))
		{
			byte[] bytes = stream.readAllBytes();
			builder.append(new String(bytes, StandardCharsets.UTF_8)).append(" ");
			if (builder.length() > maxChars)
			{
				builder.setLength(maxChars);
			}
		}
	}

	private static void appendZipEntriesByPrefix(ZipFile zip, String prefix, StringBuilder builder, int maxChars)
		throws IOException
	{
		for (ZipEntry entry : java.util.Collections.list(zip.entries()))
		{
			String name = entry.getName();
			if (!name.startsWith(prefix) || !name.endsWith(".xml"))
			{
				continue;
			}
			try (InputStream stream = zip.getInputStream(entry))
			{
				byte[] bytes = stream.readAllBytes();
				builder.append(new String(bytes, StandardCharsets.UTF_8)).append(" ");
				if (builder.length() > maxChars)
				{
					builder.setLength(maxChars);
					break;
				}
			}
		}
	}

	private static String cleanupXmlText(String xml)
	{
		if (xml == null || xml.isEmpty())
		{
			return "";
		}
		String text = xml.replaceAll("<[^>]+>", " ");
		text = text.replace("&amp;", "&")
			.replace("&lt;", "<")
			.replace("&gt;", ">")
			.replace("&quot;", "\"")
			.replace("&apos;", "'");
		return text.replaceAll("\\s+", " ").trim();
	}

	private static String readBinaryBase64(java.io.File file, int maxBytes)
	{
		try
		{
			byte[] bytes = Files.readAllBytes(file.toPath());
			if (bytes.length > maxBytes)
			{
				byte[] slice = new byte[maxBytes];
				System.arraycopy(bytes, 0, slice, 0, maxBytes);
				return Base64.getEncoder().encodeToString(slice) + "...";
			}
			return Base64.getEncoder().encodeToString(bytes);
		}
		catch (IOException error)
		{
			return "(failed to read " + file.getName() + ")";
		}
	}

	private static void appendTextAttachment(StringBuilder builder, String name, String text)
	{
		if (text == null)
		{
			text = "";
		}
		List<String> chunks = splitText(text, ATTACHMENT_CHUNK_SIZE, MAX_ATTACHMENT_CHARS);
		if (chunks.isEmpty())
		{
			builder.append(",{\"type\":\"input_text\",\"text\":\"")
				.append(escapeJson("Attachment " + name + " is empty.")).append("\"}");
			return;
		}
		for (int i = 0; i < chunks.size(); i++)
		{
			String label = chunks.size() == 1 ? "Attachment " + name
				: "Attachment " + name + " (part " + (i + 1) + "/" + chunks.size() + ")";
			builder.append(",{\"type\":\"input_text\",\"text\":\"")
				.append(escapeJson(label + ":\n" + chunks.get(i))).append("\"}");
		}
	}

	private static List<String> splitText(String text, int chunkSize, int maxChars)
	{
		List<String> chunks = new ArrayList<>();
		if (text == null)
		{
			return chunks;
		}
		String content = text;
		if (content.length() > maxChars)
		{
			content = content.substring(0, maxChars) + "...";
		}
		int index = 0;
		while (index < content.length())
		{
			int end = Math.min(content.length(), index + chunkSize);
			chunks.add(content.substring(index, end));
			index = end;
		}
		return chunks;
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
			int parsed = Integer.parseInt(value);
			return Math.max(parsed, 30);
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
		if (responseBody == null || responseBody.isEmpty())
		{
			return null;
		}

		Object parsed = SimpleJsonParser.parse(responseBody);
		if (parsed instanceof java.util.Map)
		{
			java.util.Map<?, ?> root = (java.util.Map<?, ?>) parsed;
			String outputText = readString(root.get("output_text"));
			if (outputText != null && !outputText.isEmpty())
			{
				return outputText;
			}

			Object output = root.get("output");
			if (output instanceof java.util.List)
			{
				String contentText = extractOutputTextFromList((java.util.List<?>) output);
				if (contentText != null && !contentText.isEmpty())
				{
					return contentText;
				}
			}
		}

		return extractFallbackContent(responseBody);
	}

	private static String extractOutputTextFromList(java.util.List<?> output)
	{
		for (Object item : output)
		{
			if (!(item instanceof java.util.Map))
			{
				continue;
			}
			java.util.Map<?, ?> map = (java.util.Map<?, ?>) item;
			Object content = map.get("content");
			if (!(content instanceof java.util.List))
			{
				continue;
			}
			for (Object part : (java.util.List<?>) content)
			{
				if (!(part instanceof java.util.Map))
				{
					continue;
				}
				java.util.Map<?, ?> partMap = (java.util.Map<?, ?>) part;
				String type = readString(partMap.get("type"));
				String text = readString(partMap.get("text"));
				if ("output_text".equals(type) && text != null && !text.isEmpty())
				{
					return text;
				}
			}
		}
		return null;
	}

	private static String extractFallbackContent(String responseBody)
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

	private static String readString(Object value)
	{
		if (value == null)
		{
			return null;
		}
		if (value instanceof String)
		{
			return (String) value;
		}
		return String.valueOf(value);
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
