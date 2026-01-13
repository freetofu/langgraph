package com.nomagic.magicdraw.examples.autoid.callbehavior;

import com.nomagic.magicdraw.autoid.INumberingAction;
import com.nomagic.magicdraw.autoid.NumberingInfo;
import com.nomagic.magicdraw.autoid.NumberingInfoHelper;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.CallBehaviorAction;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.commonbehaviors.mdbasicbehaviors.Behavior;

import javax.annotation.CheckForNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Example class shows the source code for Call Behavior Numbering ( Nested numbers, where the call-behavior action might be "4" 
 * and the call-behavior actions inside the called behavior (these actions are related via behavior property) are "4.1" and "4.2". 
 * That is the way which the reader can tell that 4.1 and 4.2 are details of 4. ).
 * 
 * @since August 6, 2012
 * 
 * @author teerawat chaiyakijpichet
 * 
 */
public class CallBehaviorActionNumbering implements INumberingAction {

//	@SuppressWarnings("unchecked")
	@Override
	public Map<Element, String> generateIds(List<Element> elements, NumberingInfo nInfo)
    {
		
		Map<Element, String> idMap = new HashMap<>();
		if (! elements.isEmpty())
        {
			
			String baseId = nInfo.getPrefix();
			
			// when creating an Call Behavior Action, this will be called with a single element
			if (elements.size() == 1 && elements.get(0) instanceof CallBehaviorAction rootCallBehaviorAction)
            {
				rootCallBehaviorAction = getRootCallBehaviorAction(rootCallBehaviorAction);
				assignNumberToCurrentLevel(idMap, nInfo , baseId, rootCallBehaviorAction);
				
		    // when renumbering in the dialog this will be called with multiple elements
			}
            else
            {
				CallBehaviorAction rootCallBehaviorAction = elements.stream()
						.filter(e -> e instanceof CallBehaviorAction)
						.findFirst()
						.map(e -> getRootCallBehaviorAction((CallBehaviorAction) e))
						.orElse(null);
				assignNumberToCurrentLevel(idMap, nInfo , baseId, rootCallBehaviorAction);
				
			}
		}
		return idMap;
	}
	
	/**
	 * Assign number to all elements in current level and also the children of each element in the current level.
	 * @param idMap the id map.
	 * @param nInfo the number info.
	 * @param baseId the base id.
	 * @param root the element which is in the current level.
	 */
	private static void assignNumberToCurrentLevel(Map<Element, String> idMap, NumberingInfo nInfo, String baseId, @CheckForNull CallBehaviorAction root)
	{
		if(root != null && root.getOwner() != null)
		{
			int counter = 1;
			for (Element checkElement : root.getOwner().getOwnedElement()) {
				
				if (checkElement instanceof CallBehaviorAction) {
					if(!idMap.containsKey(checkElement))
					{
						String id = baseId + counter;
						counter++;

						Behavior behavior =  ((CallBehaviorAction)checkElement).getBehavior();
						
						idMap.put(checkElement, id);

						//noinspection ConstantConditions
						assignNumberForCallBehaviorAction(idMap, nInfo, id, behavior);
					}
				}
					
			}
		}
	}
	
	/**
	 * Assign the number for call behavior actions in behavior element and also the children of each call behavior action.
	 * @param idMap the id map.
	 * @param nInfo the number info.
	 * @param parentID the parent id.
	 * @param element the behavior element.
	 */
	private static void assignNumberForCallBehaviorAction(Map<Element, String> idMap, NumberingInfo nInfo, String parentID, Behavior element)
	{
			if(element != null)
			{
				Collection<Element> collection = element.getOwnedElement();
				List<CallBehaviorAction> actList = collection.stream()
						.filter(ele -> ele instanceof CallBehaviorAction)
						.map(ele -> (CallBehaviorAction) ele)
						.collect(Collectors.toList());
				if(actList.size() > 0)
				{
					int counter = 1;
					for (CallBehaviorAction activity : actList) {
						String id = parentID + getSeparator(nInfo) + counter;
						counter++;
						if(!idMap.containsKey(activity))
						{
							idMap.put(activity, id);
							//noinspection ConstantConditions
							assignNumberForCallBehaviorAction(idMap, nInfo, id, activity.getBehavior());
						}
					}
				}
			}
	}
	
	/**
	 * Return the override separator status. 
	 * @param nInfo the number info.
	 * @return the boolean.
	 */
	private static boolean overrideSeparator(NumberingInfo nInfo)
	{
		return nInfo.getSeparator() != null && nInfo.getSeparator().length() > 0;
	}
	
	/**
	 * Get the separator value. If the override separator is true, it will return the separator value from the number info, 
	 * the otherwise returns from the schema of the number info.
	 *  
	 * @param nInfo the number info.
	 * @return the separator value.
	 */
	private static String getSeparator(NumberingInfo nInfo)
	{
		return overrideSeparator(nInfo) ? nInfo.getSeparator() : NumberingInfoHelper.extractSeparator(nInfo);
	}
	
	/**
	 * Get the root of current call behavior action in order to assign the number from the root. 
	 * @param child the current call behavior action.
	 * @return the root of current call behavior action.
	 */
	private static CallBehaviorAction getRootCallBehaviorAction(CallBehaviorAction child)
	{
		Class<?>[] types = new Class<?>[]{CallBehaviorAction.class};
        Object[] callBehaviorAction = Finder.byTypeRecursively().find(Project.getProject(child), types, false).toArray();
        Collection<CallBehaviorAction> visitedCallBehaviorAction = new ArrayList<>();
        visitedCallBehaviorAction.add(child);
        CallBehaviorAction parent = child;
        while(true)
        {
        	boolean foundParent = false;
	        for (Object obj : callBehaviorAction) {
	        	CallBehaviorAction findCallBehaviorAction = (CallBehaviorAction) obj;
				if(findCallBehaviorAction.getBehavior() == parent.getOwner())
				{
					parent = findCallBehaviorAction;
					if (visitedCallBehaviorAction.contains(parent))
					{
						break;
					}
					else
					{
						visitedCallBehaviorAction.add(parent);
					}
					foundParent = true;
					break;
				}
			}
	        
	        if(!foundParent)
	        {
	        	break;
	        }
        }
		return parent;
	}
}
