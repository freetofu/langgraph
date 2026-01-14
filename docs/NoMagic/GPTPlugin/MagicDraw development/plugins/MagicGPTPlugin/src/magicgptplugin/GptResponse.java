package magicgptplugin;

public class GptResponse
{
	private final String message;
	private final boolean success;
	private final String errorMessage;

	private GptResponse(String message, boolean success, String errorMessage)
	{
		this.message = message;
		this.success = success;
		this.errorMessage = errorMessage;
	}

	public static GptResponse success(String message)
	{
		return new GptResponse(message, true, null);
	}

	public static GptResponse failure(String errorMessage)
	{
		return new GptResponse(null, false, errorMessage);
	}

	public String getMessage()
	{
		return message;
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
