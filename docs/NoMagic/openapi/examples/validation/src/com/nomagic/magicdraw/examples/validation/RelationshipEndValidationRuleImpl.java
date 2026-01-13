package com.nomagic.magicdraw.examples.validation;

import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.annotation.Annotation;
import com.nomagic.magicdraw.annotation.AnnotationAction;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.uml2.Elements;
import com.nomagic.magicdraw.validation.ElementValidationRuleImpl;
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.jmi.smartlistener.SmartListenerConfig;
import com.nomagic.uml2.ext.jmi.smartlistener.SmartListenerConfigProvider;
import com.nomagic.uml2.ext.magicdraw.classes.mddependencies.Dependency;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Association;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Constraint;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Relationship;
import com.nomagic.uml2.impl.PropertyNames;

import java.awt.event.ActionEvent;
import java.util.*;

/**
 * This is an example of binary validation rule. The rule checks whether Relationship owner is the same as
 * client or client's owner. We are trying to solve the situation when Relationship is left in some Package after moving
 * its client to other owner.
 *
 * @author Mindaugas Ringys
 */
public class RelationshipEndValidationRuleImpl implements ElementValidationRuleImpl, SmartListenerConfigProvider
{

	@Override
	public void init(Project project, Constraint constraint)
    {
    }


    /**
     * Returns a map of classes and smart listener configurations.
     *
     * @return smart listener configurations.
     */
    @Override
	public Map<Class<? extends Element>, Collection<SmartListenerConfig>> getListenerConfigurations()
    {
        Map<Class<? extends Element>, Collection<SmartListenerConfig>> configMap = new IdentityHashMap<>(2);

        Collection<SmartListenerConfig> configs = new ArrayList<>();
        SmartListenerConfig config = new SmartListenerConfig();
        config.listenToNested(PropertyNames.RELATED_ELEMENT).listenTo(PropertyNames.OWNER);
        config.listenTo(PropertyNames.OWNER);
        configs.add(config);
        configMap.put(Association.class, configs);
        configMap.put(Dependency.class, configs);
        return configMap;
    }

    /**
     * Executes the rule.
     *
     * @param project    a project of the constraint.
     * @param constraint constraint which defines validation rules.
     * @param elements   collection of elements that have to be validated.
     * @return a set of <code>Annotation</code> objects which specifies invalid objects.
     */
    @Override
	public Set<Annotation> run(Project project, Constraint constraint, Collection<? extends Element> elements)
    {
        Set<Annotation> result = new HashSet<>();
        for (Element element : elements)
        {
            if (element instanceof Relationship relationship)
            {
				Element client = Elements.getClientElement(relationship);
				Element supplier = Elements.getSupplierElement(relationship);
                //noinspection ConstantConditions
                Element newOwner = ModelHelper.findParent(relationship, client, supplier, null);
                if (newOwner != relationship.getOwner())
                {
                    ArrayList<NMAction> actions = new ArrayList<>();
                    Annotation annotation = new Annotation(element, constraint, actions);
                    actions.add(new MoveAction(annotation));
                    result.add(annotation);
                }
            }
        }
        return result;
    }

    @Override
	public void dispose()
    {
    }


    static class MoveAction extends NMAction implements AnnotationAction
    {
        Annotation annotation;

        MoveAction(Annotation annotation)
        {
            super("MOVE_INT_CLIENT_OWNER", "Move into default owner", null, null);
            this.annotation = annotation;
        }

        @Override
		public void actionPerformed(ActionEvent e)
        {
            Project project = Project.getProject(annotation.getSeverity());
            SessionManager.getInstance().createSession(project, "Move relationship");
            execute(annotation);
            SessionManager.getInstance().closeSession(project);
        }

        public void execute(Annotation annotation)
        {
            Relationship relationship = (Relationship) annotation.getTarget();
            //noinspection ConstantConditions
			Element client = Elements.getClientElement(relationship);
			Element supplier = Elements.getSupplierElement(relationship);
            //noinspection ConstantConditions
            Element newOwner = ModelHelper.findParent(relationship, client, supplier, null);
            relationship.setOwner(newOwner);
        }

        public boolean canExecute(Annotation annotation)
        {
            Relationship relationship = (Relationship) annotation.getTarget();
            //noinspection ConstantConditions
			Element client = Elements.getClientElement(relationship);
			Element supplier = Elements.getSupplierElement(relationship);
            //noinspection ConstantConditions
            Element newOwner = ModelHelper.findParent(relationship, client, supplier, null);
            return relationship.isEditable() && newOwner.canAdd(relationship);
        }

        @Override
		public void execute(Collection<Annotation> annotations)
        {
            Annotation first = annotations.iterator().next();
            Project project = Project.getProject(first.getSeverity());
            SessionManager.getInstance().createSession(project, "Move relationships");
            annotations.forEach(this::execute);
            SessionManager.getInstance().closeSession(project);
        }

        @Override
		public boolean canExecute(Collection<Annotation> annotations)
        {
            return annotations.stream()
                    .allMatch(this::canExecute);
        }
    }
}