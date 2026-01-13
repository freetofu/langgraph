package com.nomagic.magicdraw.examples.autoid.fixedlength;

import com.nomagic.magicdraw.autoid.IJavaNumberPart;

/**
 * Example class shows the code to provide the fixed length numbering function.
 * 
 * For example, if number length is 6 symbols, in this case the numbers will be like this:
 * 	000001
 * 	000002
 * 	000003
 * 	....
 * 	000010
 * 	000011
 * 
 * @since August 6, 2012
 * 
 * @author teerawat chaiyakijpichet
 *
 */
public class FixedLengthNumbering implements IJavaNumberPart {

	private int fixedLength = -1;

	@Override
	public String generateNextId(String lastNumberPart) {
		try {
			if ("".equals(lastNumberPart)) { //$NON-NLS-1$
				lastNumberPart = "0"; //$NON-NLS-1$
			}else
			{
				if (fixedLength == -1) {
					fixedLength = lastNumberPart.length();
				}
			}

			long currentValue = Long.parseLong(lastNumberPart);
			++currentValue;
			long nextValue = currentValue;

			StringBuilder nextValueString = new StringBuilder();
			nextValueString.append(Long.toString(nextValue));
			while (nextValueString.length() < fixedLength) {
				nextValueString.insert(0, '0');
			}

			return nextValueString.toString();

		} catch (NumberFormatException ex) {
			// already set nextSquare as initialValue
		}

		return Integer.toString(1);

	}

}
