package magicgptplugin;

public class ContextPayload
{
	private final String query;
	private final String contextSummary;

	public ContextPayload(String query, String contextSummary)
	{
		this.query = query;
		this.contextSummary = contextSummary;
	}

	public String getQuery()
	{
		return query;
	}

	public String getContextSummary()
	{
		return contextSummary;
	}
}
