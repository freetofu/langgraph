package com.nomagic.magicdraw.examples.sequencecreation;

import com.nomagic.magicdraw.actions.ActionsGroups;
import com.nomagic.magicdraw.actions.MDAction;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.uml.DiagramTypeConstants;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.paths.PathElement;
import com.nomagic.magicdraw.uml.symbols.shapes.ShapeElement;
import com.nomagic.uml2.ext.jmi.helpers.InteractionHelper;
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Constraint;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.commonbehaviors.mdsimpletime.DurationConstraint;
import com.nomagic.uml2.ext.magicdraw.commonbehaviors.mdsimpletime.TimeConstraint;
import com.nomagic.uml2.ext.magicdraw.interactions.mdbasicinteractions.Interaction;
import com.nomagic.uml2.ext.magicdraw.interactions.mdbasicinteractions.Lifeline;
import com.nomagic.uml2.ext.magicdraw.interactions.mdbasicinteractions.Message;
import com.nomagic.uml2.ext.magicdraw.interactions.mdbasicinteractions.MessageSortEnum;
import com.nomagic.uml2.ext.magicdraw.interactions.mdfragments.CombinedFragment;
import com.nomagic.uml2.ext.magicdraw.interactions.mdfragments.InteractionOperand;
import com.nomagic.uml2.ext.magicdraw.interactions.mdfragments.InteractionOperatorKindEnum;
import com.nomagic.uml2.impl.ElementsFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * Action creates sequence diagram with various Sequence related symbols in it.
 *
 * @author Martynas Lelevicius
 * @author Mindaugas Genutis
 */
class CreateSequenceAction extends MDAction
{
	public CreateSequenceAction()
	{
		super("CREATE_SEQUENCE", "Create Sequence", null, ActionsGroups.UML_PROJECT_EDIT_RELATED);
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		boolean enableExecutionSpecificationModeling = Application.getInstance().getGUILog().showQuestion("Enable Execution Specifications modeling?");

		Project project = Application.getInstance().getProject();
		PresentationElementsManager viewManager = PresentationElementsManager.getInstance();
		//noinspection ConstantConditions
		ElementsFactory factory = getElementsFactory(project);

		SessionManager sessionManager = SessionManager.getInstance();
		sessionManager.createSession(project, "Generate sequence diagram");

		try
		{
			Diagram diagram = ModelElementsManager.getInstance().createDiagram(DiagramTypeConstants.UML_SEQUENCE_DIAGRAM, project.getPrimaryModel());
			Interaction interaction = (Interaction) Objects.requireNonNull(diagram.getOwner());

			DiagramPresentationElement diagramView = openDiagram(diagram);

			if (enableExecutionSpecificationModeling)
			{
				InteractionHelper.enableExecutionSpecificationModeling(diagramView);
			}

			// create lifelines
			Lifeline lifeline1 = factory.createLifelineInstance();
			lifeline1.setName("lifeline1");
			lifeline1.setOwner(interaction);

			ShapeElement lifelineShape1 = viewManager.createShapeElement(lifeline1, diagramView);

			// move lifeline1 to right
			Rectangle bounds1 = lifelineShape1.getBounds();
			viewManager.reshapeShapeElement(lifelineShape1, new Rectangle(bounds1.x + 100, bounds1.y, bounds1.width, bounds1.height));
			Lifeline lifeline2 = factory.createLifelineInstance();
			lifeline2.setName("lifeline2");
			lifeline2.setOwner(interaction);

			java.util.List<Lifeline> lifelines = Arrays.asList(lifeline1, lifeline2);

			ShapeElement lifelineShape2 = viewManager.createShapeElement(lifeline2, diagramView);

			// move lifeline2 to right after the lifeline1
			bounds1 = lifelineShape1.getBounds();
			Rectangle bounds2 = lifelineShape2.getBounds();
			viewManager.reshapeShapeElement(lifelineShape2, new Rectangle(bounds1.x + bounds1.width + 100, bounds2.y, bounds2.width, bounds2.height));

			int verticalGap = 20;

			// create message
			Message message1 = factory.createMessageInstance();
			message1.setName("message1");

			PathElement messageView1 = viewManager.createSequenceMessage(message1, MessageSortEnum.CREATEMESSAGE, lifelineShape1, lifelineShape2, false, 0, null, verticalGap);

			// message
			Message message2 = factory.createMessageInstance();
			message2.setName("message2");

			addTimeConstraint(message2, interaction, "time = 2011");
			addTimeConstraint(message2, interaction, "time = 2012");

			addConstraint(message2, interaction, "a = b");
			addConstraint(message2, interaction, "b = c");
			addConstraint(message2, interaction, "c = a");

			PathElement messageView2 = viewManager.createSequenceMessage(message2, MessageSortEnum.SYNCHCALL, lifelineShape1, lifelineShape2, true, 0, message1, verticalGap);

			addDurationConstraint(interaction, messageView1, messageView2);

			// message to self
			Message message3 = factory.createMessageInstance();
			message3.setName("message3");

			viewManager.createSequenceMessage(message3, MessageSortEnum.SYNCHCALL, lifelineShape2, lifelineShape2, false, 0, message2, verticalGap);

			// recursive message
			Message message4 = factory.createMessageInstance();
			message4.setName("message4");

			viewManager.createSequenceMessage(message4, MessageSortEnum.ASYNCHCALL, lifelineShape2, lifelineShape2, true, 0, message3,
											  verticalGap);

			// diagonal message
			Message message5 = factory.createMessageInstance();
			message5.setName("message5");

			viewManager.createSequenceMessage(message5, MessageSortEnum.ASYNCHCALL, lifelineShape2, lifelineShape1, false, 20, message4, verticalGap);

			// reply message
			Message message6 = factory.createMessageInstance();
			message6.setName("message6");

			viewManager.createSequenceMessage(message6, MessageSortEnum.REPLY, lifelineShape1, lifelineShape2, false, 0, message5, verticalGap);

			// delete message
			Message message9 = factory.createMessageInstance();
			message9.setName("message9");

			viewManager.createSequenceMessage(message9, MessageSortEnum.DELETEMESSAGE, lifelineShape1, lifelineShape2, false, 0, message6, verticalGap);

			sessionManager.closeSession(project);

			// to create message not the last message previous messages creation session should be closed 
			sessionManager.createSession(project, "Insert Message");

			// create and insert message after 6th message
			Message message7 = factory.createMessageInstance();
			message7.setName("message7");

			viewManager.createSequenceMessage(message7, MessageSortEnum.SYNCHCALL, lifelineShape1, lifelineShape2, false, 0, message6, verticalGap);

			// create and insert message after 6th message
			Message message8 = factory.createMessageInstance();
			message8.setName("message8");

			viewManager.createSequenceMessage(message8, MessageSortEnum.SYNCHCALL, lifelineShape1, lifelineShape2, false, 0, message7, 0);

			CombinedFragment fragment = createCombinedFragment(interaction, lifelines);
			viewManager.createShapeElement(fragment, diagramView, false, new Point(105, 665));

			sessionManager.closeSession(project);
		}
		catch (ReadOnlyElementException | IllegalArgumentException | IllegalStateException e)
		{
			e.printStackTrace();
			sessionManager.cancelSession(project);
		}
	}

	/**
	 * Creates Duration Constraint with max duration of 100 ms between the given messages
	 *
	 *
	 * @param interaction owner of the created Duration constraint
	 * @param message1 view of the first constrained message of the constraint
	 * @param message2 view of the second constrained message of the constraint
	 */
	private static void addDurationConstraint(Interaction interaction, PathElement message1, PathElement message2) throws ReadOnlyElementException
	{
		ElementsFactory factory = getElementsFactory(Project.getProject(interaction));
		DurationConstraint durationConstraint = factory.createDurationConstraintInstance();
		durationConstraint.setOwner(interaction);
		InteractionHelper.setDurationInterval(durationConstraint, null, "100 ms");

		PresentationElementsManager.getInstance().createDurationConstraint(durationConstraint, message1, message2);
	}

	private static CombinedFragment createCombinedFragment(Interaction interaction, Collection<Lifeline> lifelines)
	{
		ElementsFactory factory = getElementsFactory(Project.getProject(interaction));

		CombinedFragment fragment = factory.createCombinedFragmentInstance();
		fragment.getCovered().addAll(lifelines);
		fragment.setInteractionOperator(InteractionOperatorKindEnum.ALT);
		fragment.setOwner(interaction);

		InteractionOperand operand1 = factory.createInteractionOperandInstance();
		operand1.setOwner(fragment);

		InteractionOperand operand2 = factory.createInteractionOperandInstance();
		operand2.setOwner(fragment);

		return fragment;
	}

	private static DiagramPresentationElement openDiagram(Diagram diagram)
	{
		DiagramPresentationElement diagramView = Project.getProject(diagram).getDiagram(diagram);

		// diagram should be opened to make messages layout
		//noinspection ConstantConditions
		diagramView.open();

		return diagramView;
	}

	private static void addTimeConstraint(Element element, Element owner, String text)
	{
		ElementsFactory factory = getElementsFactory(Project.getProject(element));
		TimeConstraint timeConstraint = factory.createTimeConstraintInstance();
		timeConstraint.setOwner(owner);

		//noinspection ConstantConditions
		InteractionHelper.setTimeInterval(timeConstraint, text, null);
		//noinspection ConstantConditions
		InteractionHelper.setEventForTimeExpression(timeConstraint.getSpecification().getMin(), (NamedElement) element);

		timeConstraint.getConstrainedElement().add(element);
	}

	private static void addConstraint(Element element, Element owner, String text)
	{
		ElementsFactory factory = getElementsFactory(Project.getProject(element));
		Constraint constraint = factory.createConstraintInstance();
		constraint.setOwner(owner);

		ModelHelper.setConstraintText(constraint, text, false, true);

		constraint.getConstrainedElement().add(element);
	}

	private static ElementsFactory getElementsFactory(Project project)
	{
		return project.getElementsFactory();
	}
}