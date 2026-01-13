package com.nomagic.magicdraw.examples.expression;

import com.nomagic.magicdraw.expressions.ExpressionSource;
import com.nomagic.magicdraw.expressions.ExpressionSourceTraceContainer;
import com.nomagic.magicdraw.expressions.ParameterizedExpression;
import com.nomagic.magicdraw.expressions.ValueContext;
import com.nomagic.uml2.ext.jmi.smartlistener.SmartListenerConfig;
import com.nomagic.uml2.ext.jmi.smartlistener.SmartListenerConfigProvider;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Type;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.TypedElement;
import com.nomagic.uml2.impl.PropertyNames;

import javax.annotation.CheckForNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Expression collects and returns types of class attributes.
 *
 * @author Rimvydas Vaidelis
 */
public class AttributeTypesExpression implements ParameterizedExpression, SmartListenerConfigProvider, ExpressionSourceTraceContainer
{
	private List<ExpressionSource> expressionSourceTrace;

	/**
	 * Returns empty collection if the specified object is not UML class.
     * If specified object is a class then returns a collection of the class attribute types.
     *
     *
	 * @param object an object.
	 * @param valueContext value context.
	 * @return collection of class attribute types.
     */
	@SuppressWarnings("UnusedDeclaration")
	public Object getValue(@CheckForNull Element object, ValueContext valueContext)
    {
		if (object instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class aClass)
        {
			return aClass.getOwnedAttribute().stream()
					.map(TypedElement::getType)
					.filter(Objects::nonNull)
					.collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }


	@Override
	public Map<java.lang.Class<? extends Element>, Collection<SmartListenerConfig>> getListenerConfigurations()
	{
		SmartListenerConfig classConfig = new SmartListenerConfig();
		classConfig.listenToNested(PropertyNames.OWNED_ATTRIBUTE).listenTo(PropertyNames.TYPE);
		return Collections.singletonMap(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class.class, Collections.singletonList(classConfig));
	}

	/**
	 * Returns type of the result.
	 *
	 * @return result type.
	 */
	@Override
	public Class<?> getResultType()
	{
		return Type.class;
	}

	@CheckForNull
	@Override
	public List<ExpressionSource> getExpressionSourceTrace()
	{
		return expressionSourceTrace;
	}

	@Override
	public void setExpressionSourceTrace(List<ExpressionSource> expressionSourceTrace)
	{
		this.expressionSourceTrace = expressionSourceTrace;
	}
}
