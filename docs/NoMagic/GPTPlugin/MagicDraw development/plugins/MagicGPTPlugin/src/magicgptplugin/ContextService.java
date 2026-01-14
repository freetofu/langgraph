package magicgptplugin;

public class ContextService
{
	public ContextPayload buildContext(String query)
	{
		String contextSummary = "모델 범위: 전체 모델. 현재는 사용자 질의만 전달합니다.";
		return new ContextPayload(query, contextSummary);
	}
}
