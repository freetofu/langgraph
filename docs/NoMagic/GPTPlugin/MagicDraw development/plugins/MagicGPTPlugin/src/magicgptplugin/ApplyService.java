package magicgptplugin;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.uml2.ext.jmi.helpers.DeprecatedStereotypesHelper;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelperInternal;
import com.nomagic.uml2.ext.jmi.helpers.TagsHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Comment;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.PackageableElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.TaggedValue;
import com.nomagic.uml2.ext.magicdraw.classes.mddependencies.Dependency;
import com.nomagic.uml2.impl.ElementsFactory;
import com.nomagic.magicdraw.uml.DiagramType;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.shapes.ShapeElement;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ApplyService
{
	public ApplyResult applyAll(ProposalSet proposals)
	{
		if (!proposals.isSuccess())
		{
			return ApplyResult.failure(proposals.getErrorMessage());
		}
		ModelSpec spec = proposals.getModelSpec();
		if (spec == null)
		{
			return ApplyResult.failure("No model spec to apply.");
		}

		Project project = Application.getInstance().getProject();
		if (project == null)
		{
			return ApplyResult.failure("No active project.");
		}

		ApplyReport report = new ApplyReport(spec.getElements().size(), spec.getRelationships().size(),
			spec.getDiagrams().size());
		SessionManager sessionManager = SessionManager.getInstance();

		sessionManager.createSession(project, "MagicGPT Apply");
		try
		{
			ElementsFactory factory = project.getElementsFactory();
			Package targetPackage = findOrCreatePackage(project, spec.getPackageName(), factory, report);
			Map<String, NamedElement> created = applyElements(factory, targetPackage, spec, report);
			applyRelationships(factory, targetPackage, spec, created, report);
			applyDiagrams(factory, targetPackage, spec, created, report);
			sessionManager.closeSession(project);
		}
		catch (Exception error)
		{
			sessionManager.cancelSession(project);
			return ApplyResult.failure("Apply failed: " + error.getMessage());
		}

		String summary = report.buildSummary();
		if (report.isComplete())
		{
			return ApplyResult.full(summary);
		}
		if (report.isEmpty())
		{
			return ApplyResult.failure("No elements were applied.");
		}
		return ApplyResult.partial(summary);
	}

	private Package findOrCreatePackage(Project project, String name, ElementsFactory factory, ApplyReport report)
		throws ReadOnlyElementException
	{
		Package root = project.getModel();
		if (name == null || name.trim().isEmpty())
		{
			return root;
		}
		for (PackageableElement element : root.getPackagedElement())
		{
			if (element instanceof Package)
			{
				Package candidate = (Package) element;
				if (name.equals(candidate.getName()))
				{
					return candidate;
				}
			}
		}
		Package created = factory.createPackageInstance();
		created.setName(name);
		ModelElementsManager.getInstance().addElement(created, root);
		report.createdPackages++;
		return created;
	}

	private Map<String, NamedElement> applyElements(ElementsFactory factory, Package targetPackage, ModelSpec spec,
		ApplyReport report) throws ReadOnlyElementException
	{
		Map<String, NamedElement> created = new HashMap<>();
		Project project = Application.getInstance().getProject();
		Stereotype requirement = findStereotype(project, "Requirement");

		for (ModelElementSpec elementSpec : spec.getElements())
		{
			String type = normalizeType(elementSpec.getType());
			if (!"requirement".equals(type))
			{
				report.addWarning("Unsupported element type: " + elementSpec.getType());
				continue;
			}

			com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class element = factory.createClassInstance();
			String resolvedName = resolveRequirementName(elementSpec.getName(), elementSpec.getText(), targetPackage, created);
			element.setName(resolvedName);
			ModelElementsManager.getInstance().addElement(element, targetPackage);
			created.put(resolvedName, element);
			report.recordElementType(type);
			report.createdElements++;

			if (requirement != null && StereotypesHelper.canApplyStereotype(element, requirement))
			{
				StereotypesHelper.addStereotype(element, requirement);
			}
			boolean textApplied = applyRequirementText(element, requirement, elementSpec.getText(), factory, report);
			if (!textApplied && elementSpec.getText() != null && !elementSpec.getText().isEmpty())
			{
				report.addWarning("Requirement text not applied for " + resolvedName);
			}
		}
		return created;
	}

	private String resolveRequirementName(String name, String text, Package targetPackage,
		Map<String, NamedElement> created)
	{
		String candidate = name == null ? "" : name.trim();
		boolean generic = candidate.isEmpty()
			|| candidate.matches("(?i)^r\\d+$")
			|| candidate.matches("(?i)^req\\d+$")
			|| candidate.matches("(?i)^requirement\\d*$");

		if (generic && text != null && !text.trim().isEmpty())
		{
			candidate = buildNameFromText(text);
		}
		if (candidate.isEmpty())
		{
			candidate = "Requirement";
		}

		String unique = candidate;
		int index = 2;
		while (created.containsKey(unique) || findElement(created, targetPackage, unique) != null)
		{
			unique = candidate + "_" + index;
			index++;
		}
		return unique;
	}

	private String buildNameFromText(String text)
	{
		String cleaned = text.replaceAll("[^\\p{L}\\p{N} ]", " ").toLowerCase();
		String[] parts = cleaned.split("\\s+");
		List<String> tokens = new ArrayList<>();
		for (String part : parts)
		{
			if (part.length() < 3)
			{
				continue;
			}
			if (isStopWord(part))
			{
				continue;
			}
			tokens.add(part);
			if (tokens.size() >= 4)
			{
				break;
			}
		}
		if (tokens.isEmpty())
		{
			return "Requirement_" + Math.abs(text.hashCode());
		}
		return String.join("_", tokens);
	}

	private boolean isStopWord(String token)
	{
		switch (token)
		{
			case "the":
			case "and":
			case "for":
			case "with":
			case "that":
			case "this":
			case "shall":
			case "must":
			case "should":
			case "from":
			case "into":
			case "over":
			case "under":
			case "within":
			case "support":
			case "provide":
			case "enable":
			case "system":
			case "vehicle":
			case "user":
				return true;
			default:
				return false;
		}
	}

	private void applyRelationships(ElementsFactory factory, Package targetPackage, ModelSpec spec,
		Map<String, NamedElement> created, ApplyReport report) throws ReadOnlyElementException
	{
		Project project = Application.getInstance().getProject();
		Stereotype satisfy = findStereotype(project, "Satisfy");
		Stereotype deriveReqt = findStereotype(project, "deriveReqt");
		Stereotype refine = findStereotype(project, "refine");
		Stereotype trace = findStereotype(project, "trace");

		for (ModelRelationshipSpec relationshipSpec : spec.getRelationships())
		{
			String type = normalizeType(relationshipSpec.getType());

			NamedElement source = findElement(created, targetPackage, relationshipSpec.getSource());
			NamedElement target = findElement(created, targetPackage, relationshipSpec.getTarget());
			if (source == null || target == null)
			{
				report.addWarning("Missing relationship endpoints: " + relationshipSpec.getSource() + " -> "
					+ relationshipSpec.getTarget());
				continue;
			}

			if (isContainment(type))
			{
				if (applyContainment(source, target, report))
				{
					report.recordRelationshipType("containment");
					report.createdRelationships++;
				}
				else
				{
					report.addWarning("Failed to apply containment for: " + relationshipSpec.getSource()
						+ " -> " + relationshipSpec.getTarget());
				}
				continue;
			}

			Dependency dependency = factory.createDependencyInstance();
			dependency.getClient().add(source);
			dependency.getSupplier().add(target);
			ModelElementsManager.getInstance().addElement(dependency, targetPackage);
			report.createdRelationships++;

			if ("satisfy".equals(type) && areBothRequirements(source, target))
			{
				type = "derivereqt";
			}

			report.recordRelationshipType(type);

			Stereotype stereotype = null;
			if ("satisfy".equals(type))
			{
				stereotype = satisfy;
			}
			else if ("derivereqt".equals(type))
			{
				stereotype = deriveReqt;
			}
			else if ("refine".equals(type))
			{
				stereotype = refine;
			}
			else if ("trace".equals(type))
			{
				stereotype = trace;
			}
			else
			{
				report.addWarning("Unsupported relationship type: " + relationshipSpec.getType());
			}

			if (stereotype != null && StereotypesHelper.canApplyStereotype(dependency, stereotype))
			{
				StereotypesHelper.addStereotype(dependency, stereotype);
			}
			else if (stereotype == null)
			{
				report.addWarning("Missing stereotype for relationship: " + relationshipSpec.getType());
			}
		}
	}

	private void applyDiagrams(ElementsFactory factory, Package targetPackage, ModelSpec spec,
		Map<String, NamedElement> created, ApplyReport report) throws ReadOnlyElementException
	{
		Project project = Application.getInstance().getProject();
		for (ModelDiagramSpec diagramSpec : spec.getDiagrams())
		{
			String diagramTypeName = resolveDiagramTypeName(diagramSpec);
			if (diagramTypeName == null)
			{
				report.addWarning("Unsupported diagram type: " + diagramSpec.getType());
				continue;
			}

			Diagram diagram = factory.createDiagramInstance();
			String name = diagramSpec.getName();
			if (name == null || name.isEmpty())
			{
				name = diagramTypeName;
			}
			diagram.setName(name);
			diagram.setOwnerOfDiagram(targetPackage);
			diagram.setContext(targetPackage);
			ModelElementsManager.getInstance().addElement(diagram, targetPackage);

			DiagramPresentationElement diagramPresentation = project.getDiagram(diagram);
			if (diagramPresentation == null)
			{
				diagramPresentation = new DiagramPresentationElement(diagram, DiagramType.createDiagramType(diagramTypeName));
				diagramPresentation.setDiagramType(DiagramType.createDiagramType(diagramTypeName));
			}

			diagramPresentation.setShowDiagramFrame(true);
			diagramPresentation.setShowDiagramName(true);
			diagramPresentation.setShowDiagramType(true);
			diagramPresentation.setShowContextName(true);
			diagramPresentation.setShowContextType(true);
			diagramPresentation.setInitialFrameSizeSet(false);
			diagramPresentation.setInitialDiagramFrameBounds();

			List<String> elementNames = diagramSpec.getElementNames();
			if (elementNames.isEmpty())
			{
				elementNames = new ArrayList<>(created.keySet());
			}

			layoutElements(diagramPresentation, elementNames, created);
			report.createdDiagrams++;
			report.addDiagramDetail(diagramTypeName, name, elementNames.size(), diagramSpec);
		}
	}

	private void layoutElements(DiagramPresentationElement diagramPresentation, List<String> elementNames,
		Map<String, NamedElement> created) throws ReadOnlyElementException
	{
		PresentationElementsManager manager = PresentationElementsManager.getInstance();
		int x = 60;
		int y = 60;
		int column = 0;
		int maxColumns = 4;
		int xStep = 220;
		int yStep = 160;

		for (String name : elementNames)
		{
			NamedElement element = created.get(name);
			if (element == null)
			{
				continue;
			}
			Point point = new Point(x + (column * xStep), y);
			ShapeElement shape = manager.createShapeElement((Element) element, diagramPresentation, true, point);
			if (shape != null)
			{
				column++;
				if (column >= maxColumns)
				{
					column = 0;
					y += yStep;
				}
			}
		}
	}

	private NamedElement findElement(Map<String, NamedElement> created, Package targetPackage, String name)
	{
		if (name == null)
		{
			return null;
		}
		NamedElement existing = created.get(name);
		if (existing != null)
		{
			return existing;
		}
		for (PackageableElement element : targetPackage.getPackagedElement())
		{
			if (element instanceof NamedElement)
			{
				NamedElement named = (NamedElement) element;
				if (name.equals(named.getName()))
				{
					return named;
				}
			}
		}
		return null;
	}

	private boolean applyRequirementText(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class element,
		Stereotype requirement, String text, ElementsFactory factory, ApplyReport report) throws ReadOnlyElementException
	{
		if (text == null || text.trim().isEmpty())
		{
			return false;
		}

		boolean applied = false;
		if (requirement != null)
		{
			Property property = StereotypesHelperInternal.getPropertyByName(requirement, "text", true);
			if (property == null)
			{
				property = StereotypesHelperInternal.getPropertyByName(requirement, "Text", true);
			}
			if (property != null)
			{
				TagsHelper.setStereotypePropertyValue(element, requirement, property, text);
				applied = true;
			}
			else
			{
				try
				{
					TagsHelper.setStereotypePropertyValue(element, requirement, "text", text, true);
					applied = true;
				}
				catch (Exception ignored)
				{
					try
					{
						TagsHelper.setStereotypePropertyValue(element, requirement, "Text", text, true);
						applied = true;
					}
					catch (Exception ignoredInner)
					{
						applied = false;
					}
				}
			}
		}

		if (!applied)
		{
			Comment comment = factory.createCommentInstance();
			comment.setBody(text);
			comment.getAnnotatedElement().add(element);
			ModelElementsManager.getInstance().addElement(comment, element.getOwner());
			report.addWarning("Requirement text stored as comment for " + element.getName());
		}
		return applied;
	}

	private Stereotype findStereotype(Project project, String name)
	{
		if (project == null || name == null)
		{
			return null;
		}

		Profile sysmlProfile = StereotypesHelper.getProfile(project, "SysML");
		if (sysmlProfile != null)
		{
			applyProfileIfNeeded(project, sysmlProfile);
			Stereotype stereotype = StereotypesHelper.getStereotype(project, name, sysmlProfile);
			if (stereotype != null)
			{
				return stereotype;
			}
		}

		Profile requirementsProfile = StereotypesHelper.getProfile(project, "SysML::Requirements");
		if (requirementsProfile != null)
		{
			applyProfileIfNeeded(project, requirementsProfile);
			Stereotype stereotype = StereotypesHelper.getStereotype(project, name, requirementsProfile);
			if (stereotype != null)
			{
				return stereotype;
			}
		}

		Profile sysmlProfileNamed = StereotypesHelper.getProfile(project, "SysML Profile");
		if (sysmlProfileNamed != null)
		{
			applyProfileIfNeeded(project, sysmlProfileNamed);
			Stereotype stereotype = StereotypesHelper.getStereotype(project, name, sysmlProfileNamed);
			if (stereotype != null)
			{
				return stereotype;
			}
		}

		for (Profile profile : StereotypesHelper.getAllProfiles(project))
		{
			Stereotype stereotype = StereotypesHelper.getStereotype(project, name, profile);
			if (stereotype != null)
			{
				applyProfileIfNeeded(project, profile);
				return stereotype;
			}
		}

		for (Profile profile : StereotypesHelper.getAllProfiles(project))
		{
			for (Stereotype stereotype : StereotypesHelper.getStereotypesByProfile(profile))
			{
				if (stereotype.getName() != null && stereotype.getName().equalsIgnoreCase(name))
				{
					applyProfileIfNeeded(project, profile);
					return stereotype;
				}
			}
		}

		return DeprecatedStereotypesHelper.getStereotype(project, name);
	}

	private void applyProfileIfNeeded(Project project, Profile profile)
	{
		Package root = project.getModel();
		if (StereotypesHelper.getAppliedProfiles(root).contains(profile))
		{
			return;
		}
		if (StereotypesHelper.canApplyProfile(root, profile))
		{
			StereotypesHelper.applyProfile(root, profile);
		}
	}

	private String normalizeType(String type)
	{
		if (type == null)
		{
			return "";
		}
		return type.trim().toLowerCase().replace(" ", "").replace("-", "").replace("_", "");
	}

	private boolean areBothRequirements(NamedElement source, NamedElement target)
	{
		return DeprecatedStereotypesHelper.hasStereotypeOrDerived(source, "Requirement")
			&& DeprecatedStereotypesHelper.hasStereotypeOrDerived(target, "Requirement");
	}

	private boolean isContainment(String type)
	{
		return "containment".equals(type)
			|| "contain".equals(type)
			|| "parentchild".equals(type)
			|| "include".equals(type);
	}

	private boolean applyContainment(NamedElement source, NamedElement target, ApplyReport report)
		throws ReadOnlyElementException
	{
		if (!(source instanceof Element) || !(target instanceof Element))
		{
			report.addWarning("Containment endpoints are not elements: " + source.getName()
				+ " -> " + target.getName());
			return false;
		}

		Element sourceElement = (Element) source;
		Element targetElement = (Element) target;
		if (targetElement.getOwner() == sourceElement)
		{
			return true;
		}

		ModelElementsManager.getInstance().moveElement(targetElement, sourceElement);
		return true;
	}

	private static class ApplyReport
	{
		private final int requestedElements;
		private final int requestedRelationships;
		private final int requestedDiagrams;
		private final List<String> warnings = new ArrayList<>();
		private final Map<String, Integer> elementTypeCounts = new LinkedHashMap<>();
		private final Map<String, Integer> relationshipTypeCounts = new LinkedHashMap<>();
		private final List<String> diagramDetails = new ArrayList<>();
		private int createdElements;
		private int createdRelationships;
		private int createdPackages;
		private int createdDiagrams;

		private ApplyReport(int requestedElements, int requestedRelationships, int requestedDiagrams)
		{
			this.requestedElements = requestedElements;
			this.requestedRelationships = requestedRelationships;
			this.requestedDiagrams = requestedDiagrams;
		}

		private void recordElementType(String type)
		{
			recordType(elementTypeCounts, type);
		}

		private void recordRelationshipType(String type)
		{
			recordType(relationshipTypeCounts, type);
		}

		private void recordType(Map<String, Integer> bucket, String type)
		{
			if (bucket == null)
			{
				return;
			}
			String key = type == null ? "unknown" : type.trim();
			if (key.isEmpty())
			{
				key = "unknown";
			}
			key = key.toLowerCase();
			bucket.put(key, bucket.getOrDefault(key, 0) + 1);
		}

		private void addDiagramDetail(String actualType, String diagramName, int elementCount, ModelDiagramSpec spec)
		{
			String requestedType = spec == null ? null : spec.getType();
			String typeLabel = actualType != null && !actualType.isEmpty() ? actualType : requestedType;
			if (typeLabel == null || typeLabel.isEmpty())
			{
				typeLabel = "diagram";
			}
			String nameLabel = diagramName != null && !diagramName.isEmpty() ? diagramName : "unnamed";
			String hint = requestedType != null && !requestedType.isEmpty() ? " (requested as " + requestedType + ")" : "";
			diagramDetails.add(typeLabel + " '" + nameLabel + "' with " + elementCount + " element(s)" + hint);
		}

		private void addWarning(String warning)
		{
			warnings.add(warning);
		}

		private boolean isComplete()
		{
			return createdElements == requestedElements
				&& createdRelationships == requestedRelationships
				&& createdDiagrams == requestedDiagrams
				&& warnings.isEmpty();
		}

		private boolean isEmpty()
		{
			return createdElements == 0 && createdRelationships == 0 && createdDiagrams == 0;
		}

		private String buildSummary()
		{
			StringBuilder builder = new StringBuilder();
			builder.append("elements ").append(createdElements).append("/").append(requestedElements);
			if (!elementTypeCounts.isEmpty())
			{
				builder.append(" (types: ").append(formatCounts(elementTypeCounts)).append(")");
			}
			builder.append(", relationships ").append(createdRelationships).append("/").append(requestedRelationships);
			if (!relationshipTypeCounts.isEmpty())
			{
				builder.append(" [").append(formatCounts(relationshipTypeCounts)).append("]");
			}
			builder.append(", diagrams ").append(createdDiagrams).append("/").append(requestedDiagrams);
			if (!diagramDetails.isEmpty())
			{
				builder.append(" [").append(String.join("; ", diagramDetails)).append("]");
			}
			if (createdPackages > 0)
			{
				builder.append(", packages ").append(createdPackages);
			}
			if (!warnings.isEmpty())
			{
				builder.append(", warnings: ");
				for (int i = 0; i < warnings.size(); i++)
				{
					if (i > 0)
					{
						builder.append("; ");
					}
					builder.append(warnings.get(i));
				}
			}
			return builder.toString();
		}

		private String formatCounts(Map<String, Integer> counts)
		{
			StringBuilder builder = new StringBuilder();
			int index = 0;
			for (Map.Entry<String, Integer> entry : counts.entrySet())
			{
				if (index++ > 0)
				{
					builder.append(", ");
				}
				builder.append(entry.getKey()).append("=").append(entry.getValue());
			}
			return builder.toString();
		}
	}

	private String resolveDiagramTypeName(ModelDiagramSpec diagramSpec)
	{
		Project project = Application.getInstance().getProject();
		String preferred = project == null ? "" : getPreferredRequirementsDiagramType(project);
		String preferredNormalized = normalizeType(preferred);

		if (diagramSpec == null)
		{
			if (!preferred.isEmpty())
			{
				String preferredMatch = findDiagramType(preferredNormalized);
				if (preferredMatch != null)
				{
					return preferredMatch;
				}
			}
			String requirementMatch = findDiagramType("requirementdiagram");
			return requirementMatch == null ? findDiagramType("requirementsdiagram") : requirementMatch;
		}

		String requested = diagramSpec.getType();
		String normalized = normalizeType(requested);
		String exactMatch = findExactDiagramType(requested);
		if (exactMatch != null)
		{
			return exactMatch;
		}

		if (isRequirementIntent(diagramSpec))
		{
			String requirementType = selectRequirementDiagramType(diagramSpec, project);
			if (requirementType != null)
			{
				return requirementType;
			}
		}
		if (normalized.isEmpty())
		{
			normalized = detectRequirementHint(diagramSpec);
		}
		if (normalized.isEmpty())
		{
			normalized = "requirementdiagram";
		}

		if ((normalized.equals("requirements") || normalized.equals("requirementsdiagram"))
			&& !preferred.isEmpty()
			&& !isUafPreferred(diagramSpec))
		{
			String preferredMatch = findDiagramType(preferredNormalized);
			if (preferredMatch != null)
			{
				return preferredMatch;
			}
		}

		boolean preferSysml = isRequirementRequest(preferredNormalized) && !containsUafHint(preferred);
		boolean allowUaf = isUafPreferred(diagramSpec) || containsUafHint(preferred);
		String found = findDiagramType(normalized, preferSysml, allowUaf);
		if (found != null)
		{
			return found;
		}

		if (!"requirementsdiagram".equals(normalized))
		{
			found = findDiagramType("requirementdiagram", preferSysml, allowUaf);
			if (found == null)
			{
				found = findDiagramType("requirementsdiagram", preferSysml, allowUaf);
			}
			if (found != null)
			{
				return found;
			}
		}

		return null;
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

	private boolean isUafPreferred(ModelDiagramSpec diagramSpec)
	{
		String type = diagramSpec.getType();
		String name = diagramSpec.getName();
		return containsUafHint(type) || containsUafHint(name);
	}

	private boolean containsUafHint(String value)
	{
		if (value == null || value.trim().isEmpty())
		{
			return false;
		}
		String normalized = normalizeType(value);
		return normalized.contains("uaf")
			|| normalized.contains("nov3")
			|| normalized.contains("operationalinformationrequirements")
			|| normalized.contains("operationalinformation");
	}

	private String detectRequirementHint(ModelDiagramSpec diagramSpec)
	{
		if (diagramSpec == null)
		{
			return "";
		}
		if (containsRequirementWord(diagramSpec.getName()))
		{
			return "requirementdiagram";
		}
		if (containsRequirementWord(diagramSpec.getType()))
		{
			return "requirementdiagram";
		}
		return "";
	}

	private boolean containsRequirementWord(String value)
	{
		if (value == null)
		{
			return false;
		}
		return normalizeType(value).contains("require");
	}

	private boolean isRequirementIntent(ModelDiagramSpec diagramSpec)
	{
		if (diagramSpec == null)
		{
			return false;
		}
		return containsRequirementWord(diagramSpec.getType())
			|| containsRequirementWord(diagramSpec.getName());
	}

	private String selectRequirementDiagramType(ModelDiagramSpec diagramSpec, Project project)
	{
		List<String> candidates = DiagramType.getCreatableDiagramTypes();
		if (candidates == null || candidates.isEmpty())
		{
			return null;
		}

		boolean preferSysml = shouldPreferSysml(project);
		List<String> requirementCandidates = filterRequirementCandidates(candidates, preferSysml);
		if (requirementCandidates.isEmpty())
		{
			requirementCandidates = candidates;
		}

		String requested = normalizeType(diagramSpec.getType());
		String name = normalizeType(diagramSpec.getName());
		String intent = !requested.isEmpty() ? requested : name;

		List<String> preferredNames = resolveRequirementPreferences(intent);
		return pickFirstAvailable(preferredNames, requirementCandidates);
	}

	private boolean shouldPreferSysml(Project project)
	{
		if (project == null)
		{
			return false;
		}
		String preferred = getPreferredRequirementsDiagramType(project);
		return !preferred.isEmpty() && normalizeType(preferred).contains("requirementdiagram");
	}

	private List<String> filterRequirementCandidates(List<String> types, boolean preferSysml)
	{
		List<String> result = new ArrayList<>();
		for (String type : types)
		{
			String normalized = normalizeType(type);
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

	private List<String> resolveRequirementPreferences(String normalized)
	{
		List<String> preferences = new ArrayList<>();
		boolean wantsMatrix = normalized.contains("matrix");
		boolean wantsTable = normalized.contains("table");
		boolean wantsMap = normalized.contains("map");

		if (normalized.contains("derive") && wantsMatrix)
		{
			preferences.add("Derive Requirement Matrix");
		}
		if (normalized.contains("refine") && wantsMatrix)
		{
			preferences.add("Refine Requirement Matrix");
		}
		if (normalized.contains("satisfy") && wantsMatrix)
		{
			preferences.add("Satisfy Requirement Matrix");
		}
		if (normalized.contains("verify") && wantsMatrix)
		{
			preferences.add("Verify Requirement Matrix");
		}
		if (normalized.contains("containment"))
		{
			preferences.add("Requirement Containment Map");
		}
		if (normalized.contains("derivation") || normalized.contains("derive"))
		{
			preferences.add("Requirement Derivation Map");
		}
		if (wantsMap)
		{
			preferences.add("Requirement Derivation Map");
			preferences.add("Requirement Containment Map");
		}
		if (wantsTable)
		{
			preferences.add("Requirement Table");
		}
		if (wantsMatrix)
		{
			preferences.add("Derive Requirement Matrix");
			preferences.add("Refine Requirement Matrix");
			preferences.add("Satisfy Requirement Matrix");
			preferences.add("Verify Requirement Matrix");
		}

		preferences.add("Requirement Diagram");
		preferences.add("Requirements Diagram");
		return preferences;
	}

	private String pickFirstAvailable(List<String> preferredNames, List<String> candidates)
	{
		if (preferredNames == null || candidates == null || candidates.isEmpty())
		{
			return null;
		}
		for (String preferred : preferredNames)
		{
			for (String candidate : candidates)
			{
				if (candidate.equalsIgnoreCase(preferred))
				{
					return candidate;
				}
			}
		}
		return null;
	}

	private String findExactDiagramType(String requested)
	{
		if (requested == null || requested.trim().isEmpty())
		{
			return null;
		}
		for (String type : DiagramType.getCreatableDiagramTypes())
		{
			if (type.equalsIgnoreCase(requested.trim()))
			{
				return type;
			}
		}
		return null;
	}

	private String findDiagramType(String normalized)
	{
		return findDiagramType(normalized, false, true);
	}

	private String findDiagramType(String normalized, boolean preferSysml, boolean allowUaf)
	{
		String bestMatch = null;
		int bestScore = Integer.MAX_VALUE;
		List<String> candidates = DiagramType.getCreatableDiagramTypes();
		if (preferSysml && isRequirementRequest(normalized) && !allowUaf)
		{
			candidates = filterOutUaf(candidates);
		}
		for (String type : candidates)
		{
			String typeNormalized = normalizeType(type);
			int score = scoreDiagramType(normalized, typeNormalized);
			if (score < bestScore)
			{
				bestScore = score;
				bestMatch = type;
			}
		}
		if (bestMatch == null && !allowUaf)
		{
			return findDiagramType(normalized, preferSysml, true);
		}
		return bestScore == Integer.MAX_VALUE ? null : bestMatch;
	}

	private int scoreDiagramType(String normalized, String candidate)
	{
		if (normalized == null || normalized.isEmpty() || candidate == null || candidate.isEmpty())
		{
			return Integer.MAX_VALUE;
		}
		if (candidate.equals(normalized))
		{
			return 0;
		}

		if (isRequirementRequest(normalized))
		{
			int penalty = isUafDiagramType(candidate) ? 20 : 0;
			if (candidate.contains("sysml"))
			{
				penalty = Math.max(0, penalty - 2);
			}

			if ("requirementsdiagram".equals(candidate) || "requirementdiagram".equals(candidate))
			{
				return 1 + penalty;
			}
			if (candidate.contains("requirementsdiagram") || candidate.contains("requirementdiagram"))
			{
				return 2 + penalty;
			}
			if (candidate.startsWith("requirements") || candidate.endsWith("requirements")
				|| candidate.startsWith("requirement") || candidate.endsWith("requirement"))
			{
				return 4 + penalty;
			}
			if (candidate.contains("requirements") || candidate.contains("requirement"))
			{
				return 6 + candidate.length() + penalty;
			}
			return Integer.MAX_VALUE;
		}

		if (candidate.contains(normalized))
		{
			return 10 + (candidate.length() - normalized.length());
		}
		return Integer.MAX_VALUE;
	}

	private boolean isRequirementRequest(String normalized)
	{
		return "requirements".equals(normalized)
			|| "requirementsdiagram".equals(normalized)
			|| "requirement".equals(normalized)
			|| "requirementdiagram".equals(normalized);
	}

	private boolean isUafDiagramType(String normalized)
	{
		return normalized.contains("nov3")
			|| normalized.contains("operationalinformationrequirements")
			|| normalized.contains("operationalinformation")
			|| normalized.contains("uaf");
	}

	private List<String> filterOutUaf(List<String> types)
	{
		List<String> result = new ArrayList<>();
		for (String type : types)
		{
			String normalized = normalizeType(type);
			if (!isUafDiagramType(normalized))
			{
				result.add(type);
			}
		}
		return result;
	}
}
