package magicgptplugin;

public class ProposalSet
{
	private final String summary;
	private final boolean success;
	private final String errorMessage;

	private ProposalSet(String summary, boolean success, String errorMessage)
	{
		this.summary = summary;
		this.success = success;
		this.errorMessage = errorMessage;
	}

	public static ProposalSet success(String summary)
	{
		return new ProposalSet(summary, true, null);
	}

	public static ProposalSet failure(String errorMessage)
	{
		return new ProposalSet(null, false, errorMessage);
	}

	public String getSummary()
	{
		return summary;
	}

	public boolean isSuccess()
	{
		return success;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}
}
