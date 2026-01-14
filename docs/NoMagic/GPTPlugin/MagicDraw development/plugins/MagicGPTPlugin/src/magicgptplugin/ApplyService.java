package magicgptplugin;

public class ApplyService
{
	public ApplyResult applyAll(ProposalSet proposals)
	{
		if (!proposals.isSuccess())
		{
			return ApplyResult.failure(proposals.getErrorMessage());
		}
		return ApplyResult.success(proposals.getSummary());
	}
}
