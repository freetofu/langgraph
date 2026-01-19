package magicgptplugin;

import java.io.File;
import java.util.Collections;
import java.util.List;

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
		return handleQuery(query, Collections.emptyList());
	}

	public String handleQuery(String query, List<File> attachments)
	{
		ContextPayload context = contextService.buildContext(query, attachments);
		GptResponse response = gptService.ask(context);
		ProposalSet proposals = proposalService.buildProposals(response);
		ApplyResult applyResult = applyService.applyAll(proposals);
		if (applyResult.getStatus() == ApplyResult.Status.FAILED)
		{
			return "Error: " + applyResult.getErrorMessage();
		}
		if (applyResult.getStatus() == ApplyResult.Status.PARTIAL)
		{
			return "Partial: " + applyResult.getSummary();
		}
		return "Done: " + applyResult.getSummary();
	}
}
