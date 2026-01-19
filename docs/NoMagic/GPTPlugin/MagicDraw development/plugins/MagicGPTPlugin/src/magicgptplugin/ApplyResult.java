package magicgptplugin;

public class ApplyResult
{
	public enum Status
	{
		FULL,
		PARTIAL,
		FAILED
	}

	private final String summary;
	private final Status status;
	private final String errorMessage;

	private ApplyResult(String summary, Status status, String errorMessage)
	{
		this.summary = summary;
		this.status = status;
		this.errorMessage = errorMessage;
	}

	public static ApplyResult full(String summary)
	{
		return new ApplyResult(summary, Status.FULL, null);
	}

	public static ApplyResult partial(String summary)
	{
		return new ApplyResult(summary, Status.PARTIAL, null);
	}

	public static ApplyResult failure(String errorMessage)
	{
		return new ApplyResult(null, Status.FAILED, errorMessage);
	}

	public String getSummary()
	{
		return summary;
	}

	public boolean isSuccess()
	{
		return status == Status.FULL;
	}

	public Status getStatus()
	{
		return status;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}
}
