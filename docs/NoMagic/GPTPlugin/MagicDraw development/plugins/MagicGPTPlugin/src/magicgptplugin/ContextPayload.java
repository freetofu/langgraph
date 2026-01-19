package magicgptplugin;

public class ContextPayload
{
	private final String query;
	private final String contextSummary;
	private final java.util.List<java.io.File> attachments;

	public ContextPayload(String query, String contextSummary)
	{
		this(query, contextSummary, java.util.Collections.emptyList());
	}

	public ContextPayload(String query, String contextSummary, java.util.List<java.io.File> attachments)
	{
		this.query = query;
		this.contextSummary = contextSummary;
		if (attachments == null || attachments.isEmpty())
		{
			this.attachments = java.util.Collections.emptyList();
		}
		else
		{
			this.attachments = java.util.Collections.unmodifiableList(new java.util.ArrayList<>(attachments));
		}
	}

	public String getQuery()
	{
		return query;
	}

	public String getContextSummary()
	{
		return contextSummary;
	}

	public java.util.List<java.io.File> getAttachments()
	{
		return attachments;
	}
}
