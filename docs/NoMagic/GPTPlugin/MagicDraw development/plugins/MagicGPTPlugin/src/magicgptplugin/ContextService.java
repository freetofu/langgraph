package magicgptplugin;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.uml.DiagramType;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContextService
{
	public ContextPayload buildContext(String query)
	{
		String contextSummary = buildContextSummary(query, Collections.emptyList());
		return new ContextPayload(query, contextSummary);
	}

	public ContextPayload buildContext(String query, List<File> attachments)
	{
		String contextSummary = buildContextSummary(query, attachments);
		return new ContextPayload(query, contextSummary, attachments);
	}

	private String buildContextSummary(String query, List<File> attachments)
	{
		Project project = Application.getInstance().getProject();
		if (project == null)
		{
			return "Project: none.";
		}

		StringBuilder builder = new StringBuilder();
		builder.append("Project: ").append(project.getName());
		String perspective = getActivePerspectiveName();
		if (!perspective.isEmpty())
		{
			builder.append(". Perspective: ").append(perspective);
		}
		builder.append(". Applied profiles: ").append(getAppliedProfileNames(project));
		String preferred = getPreferredRequirementsDiagramType(project);
		if (!preferred.isEmpty())
		{
			builder.append(". Preferred requirements diagram: ").append(preferred);
		}
		String diagramTypes = getDiagramTypeHint(project, query);
		if (!diagramTypes.isEmpty())
		{
			builder.append(". Available diagram types: ").append(diagramTypes);
		}
		String attachmentSummary = buildAttachmentSummary(attachments);
		if (!attachmentSummary.isEmpty())
		{
			builder.append(". Attachments: ").append(attachmentSummary);
		}
		return builder.toString();
	}

	private String getActivePerspectiveName()
	{
		com.nomagic.magicdraw.core.options.EnvironmentOptions options =
			Application.getInstance().getEnvironmentOptions();
		if (options == null)
		{
			return "";
		}
		com.nomagic.magicdraw.core.options.ExperienceOptionsGroup experience =
			options.getExperienceOptionsGroup();
		if (experience == null)
		{
			return "";
		}
		String mode = experience.getActiveUserMode();
		return mode == null ? "" : mode.trim();
	}

	private String getAppliedProfileNames(Project project)
	{
		Package root = project.getModel();
		List<String> names = new ArrayList<>();
		for (Profile profile : StereotypesHelper.getAppliedProfiles(root))
		{
			String name = profile.getName();
			if (name != null && !name.trim().isEmpty())
			{
				names.add(name.trim());
			}
		}
		if (names.isEmpty())
		{
			return "none";
		}
		return String.join(", ", names);
	}

	private String getPreferredRequirementsDiagramType(Project project)
	{
		String perspective = getActivePerspectiveName().toLowerCase();
		if (!perspective.isEmpty())
		{
			if (perspective.contains("system engineer") || perspective.contains("system analyst"))
			{
				return "Requirement Diagram";
			}
			if (perspective.contains("uaf"))
			{
				return "NOV-3 Operational Information Requirements";
			}
		}

		Package root = project.getModel();
		boolean hasUaf = false;
		boolean hasSysml = false;

		for (Profile profile : StereotypesHelper.getAppliedProfiles(root))
		{
			String name = profile.getName();
			if (name == null)
			{
				continue;
			}
			String normalized = name.trim().toLowerCase();
			if (normalized.contains("uaf"))
			{
				hasUaf = true;
			}
			if (normalized.contains("sysml") || normalized.contains("requirements"))
			{
				hasSysml = true;
			}
		}

		if (hasSysml)
		{
			return "Requirement Diagram";
		}
		if (hasUaf)
		{
			return "NOV-3 Operational Information Requirements";
		}
		return "";
	}

	private String getDiagramTypeHint(Project project, String query)
	{
		List<String> types = DiagramType.getCreatableDiagramTypes();
		if (types == null || types.isEmpty())
		{
			return "";
		}

		boolean preferSysml = shouldPreferSysml(project);
		List<String> filtered = filterDiagramTypes(types, query, preferSysml);
		if (filtered.isEmpty())
		{
			filtered = types;
		}
		int limit = Math.min(20, filtered.size());
		return String.join(", ", filtered.subList(0, limit));
	}

	private List<String> filterDiagramTypes(List<String> types, String query, boolean preferSysml)
	{
		if (query == null || query.trim().isEmpty())
		{
			return Collections.emptyList();
		}

		String normalizedQuery = normalize(query);
		if (normalizedQuery.contains("require"))
		{
			List<String> filtered = filterRequirementCandidates(types, preferSysml);
			if (filtered.isEmpty())
			{
				filtered = filterByToken(types, "require");
			}
			if (preferSysml)
			{
				filtered = filterOutUaf(filtered);
			}
			return filtered;
		}

		List<String> tokens = extractTokens(normalizedQuery);
		List<String> result = new ArrayList<>();
		for (String type : types)
		{
			String typeNormalized = normalize(type);
			for (String token : tokens)
			{
				if (token.length() < 3)
				{
					continue;
				}
				if (typeNormalized.contains(token))
				{
					result.add(type);
					break;
				}
			}
		}
		return result;
	}

	private List<String> filterByToken(List<String> types, String token)
	{
		List<String> result = new ArrayList<>();
		for (String type : types)
		{
			if (normalize(type).contains(token))
			{
				result.add(type);
			}
		}
		return result;
	}

	private List<String> extractTokens(String normalizedQuery)
	{
		if (normalizedQuery == null || normalizedQuery.isEmpty())
		{
			return Collections.emptyList();
		}
		String[] parts = normalizedQuery.split("\\s+");
		List<String> tokens = new ArrayList<>();
		for (String part : parts)
		{
			if (!part.isEmpty())
			{
				tokens.add(part);
			}
		}
		return tokens;
	}

	private String normalize(String value)
	{
		if (value == null)
		{
			return "";
		}
		String lower = value.trim().toLowerCase();
		return lower.replaceAll("[^a-z0-9 ]", " ").replaceAll("\\s+", " ").trim();
	}

	private String buildAttachmentSummary(List<File> attachments)
	{
		if (attachments == null || attachments.isEmpty())
		{
			return "none";
		}
		List<String> parts = new ArrayList<>();
		for (File file : attachments)
		{
			if (file == null)
			{
				continue;
			}
			String name = file.getName();
			long size = file.length();
			parts.add(name + " (" + size + " bytes)");
		}
		return parts.isEmpty() ? "none" : String.join(", ", parts);
	}

	private boolean shouldPreferSysml(Project project)
	{
		if (project == null)
		{
			return false;
		}
		String preferred = getPreferredRequirementsDiagramType(project);
		return !preferred.isEmpty() && preferred.toLowerCase().contains("requirement");
	}

	private List<String> filterRequirementCandidates(List<String> types, boolean preferSysml)
	{
		List<String> result = new ArrayList<>();
		for (String type : types)
		{
			String normalized = normalize(type);
			if (!normalized.contains("requirement"))
			{
				continue;
			}
			if (preferSysml && isUafDiagramType(normalized))
			{
				continue;
			}
			result.add(type);
		}
		return result;
	}

	private List<String> filterOutUaf(List<String> types)
	{
		List<String> result = new ArrayList<>();
		for (String type : types)
		{
			String normalized = normalize(type);
			if (!isUafDiagramType(normalized))
			{
				result.add(type);
			}
		}
		return result;
	}

	private boolean isUafDiagramType(String normalized)
	{
		return normalized.contains("nov3")
			|| normalized.contains("operationalinformationrequirements")
			|| normalized.contains("operationalinformation")
			|| normalized.contains("uaf");
	}
}
