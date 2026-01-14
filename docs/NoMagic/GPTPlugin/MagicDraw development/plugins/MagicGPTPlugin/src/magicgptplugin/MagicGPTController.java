package magicgptplugin;

public class MagicGPTController
{
	private final ContextService contextService;
	private final GptService gptService;
	private final ProposalService proposalService;
	private final ApplyService applyService;

	public MagicGPTController()
	{
		this.contextService = new ContextService();
		this.gptService = new GptService();
		this.proposalService = new ProposalService();
		this.applyService = new ApplyService();
	}

	public String handleQuery(String query)
	{
		ContextPayload context = contextService.buildContext(query);
		GptResponse response = gptService.ask(context);
		ProposalSet proposals = proposalService.buildProposals(response);
		ApplyResult applyResult = applyService.applyAll(proposals);
		if (!applyResult.isSuccess())
		{
			return "오류: " + applyResult.getErrorMessage();
		}
		return applyResult.getSummary();
	}
}
