package magicgptplugin;

public class ApplyResult
{
	private final String summary;
	private final boolean success;
	private final String errorMessage;

	private ApplyResult(String summary, boolean success, String errorMessage)
	{
		this.summary = summary;
		this.success = success;
		this.errorMessage = errorMessage;
	}

	public static ApplyResult success(String summary)
	{
		return new ApplyResult(summary, true, null);
	}

	public static ApplyResult failure(String errorMessage)
	{
		return new ApplyResult(null, false, errorMessage);
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
