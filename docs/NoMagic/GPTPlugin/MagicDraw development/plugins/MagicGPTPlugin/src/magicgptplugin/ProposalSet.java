package magicgptplugin;

public class ProposalSet
{
	private final String summary;
	private final boolean success;
	private final String errorMessage;
	private final ModelSpec modelSpec;

	private ProposalSet(String summary, boolean success, String errorMessage, ModelSpec modelSpec)
	{
		this.summary = summary;
		this.success = success;
		this.errorMessage = errorMessage;
		this.modelSpec = modelSpec;
	}

	public static ProposalSet success(String summary, ModelSpec modelSpec)
	{
		return new ProposalSet(summary, true, null, modelSpec);
	}

	public static ProposalSet failure(String errorMessage)
	{
		return new ProposalSet(null, false, errorMessage, null);
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

	public ModelSpec getModelSpec()
	{
		return modelSpec;
	}
}
