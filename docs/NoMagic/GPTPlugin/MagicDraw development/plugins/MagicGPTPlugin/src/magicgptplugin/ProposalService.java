package magicgptplugin;

public class ProposalService
{
	public ProposalSet buildProposals(GptResponse response)
	{
		if (!response.isSuccess())
		{
			return ProposalSet.failure(response.getErrorMessage());
		}
		return ProposalSet.success(response.getMessage());
	}
}
