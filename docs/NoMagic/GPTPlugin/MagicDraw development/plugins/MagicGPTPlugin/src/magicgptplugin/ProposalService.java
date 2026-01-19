package magicgptplugin;

public class ProposalService
{
	public ProposalSet buildProposals(GptResponse response)
	{
		if (!response.isSuccess())
		{
			return ProposalSet.failure(response.getErrorMessage());
		}
		ModelSpecParser.ParseResult parsed = ModelSpecParser.parse(response.getMessage());
		if (!parsed.isSuccess())
		{
			return ProposalSet.failure("Failed to parse JSON response: " + parsed.getErrorMessage());
		}
		return ProposalSet.success(parsed.getRawJson(), parsed.getSpec());
	}
}
