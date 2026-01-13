package com.nomagic.magicdraw.examples.autoid.activitynode;

import com.nomagic.magicdraw.autoid.INumberingAction;
import com.nomagic.magicdraw.autoid.NumberingInfo;
import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.uml2.ext.magicdraw.activities.mdfundamentalactivities.Activity;
import com.nomagic.uml2.ext.magicdraw.activities.mdstructuredactivities.StructuredActivityNode;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

import java.util.*;


/**
 * Sample class to show how one could number uml:Activities
 * depending on their getStructuredNode() value
 * 
 * @since Mar 1, 2012
 * @author Roger Holenweger (roger_h@nomagicasia.com)
 */
public class ActivityNodeNumbering implements INumberingAction {

	@SuppressWarnings("unchecked")
	@Override
	public Map<Element, String> generateIds(List<Element> elements, NumberingInfo nInfo) {

		Map<Element, String> idMap = new HashMap<>();
		if (! elements.isEmpty()) {
			String baseId = nInfo.getPrefix() + nInfo.getSeparator();
			
			// when creating an Activity, this will be called with a single element
			if (elements.size() == 1) {
				Element e = elements.iterator().next();
				Class<?>[] types = new Class<?>[]{com.nomagic.uml2.ext.magicdraw.activities.mdfundamentalactivities.Activity.class};
				//noinspection ConstantConditions
				Collection<Activity> acts = Finder.byTypeRecursively().find(e.getOwner(), types, false);
				String id = baseId + acts.size();
				idMap.put(e, id);
				
		    // when renumbering in the dialog this will be called with multiple elements
			} else {
				int counter = 1;
				// we sort so that renumber will get the same order
				// normally this would ask for a custom comparator class
				// but it is left out for brevity's sake
				Collections.sort(elements);
				for (Element e : elements) {
					
					if (e instanceof Activity act) {
						String id = baseId + counter;
						counter ++;
						
						// we simply attach the suffix '$node_' 
						Collection<StructuredActivityNode> nodes = act.getStructuredNode();					
						if (nodes != null && !nodes.isEmpty()) {
							id += "$node_" + nodes.size();
						}					
						idMap.put(e, id);
					}
				}
			}
		}
		return idMap;
	}
}
