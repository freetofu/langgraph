package com.nomagic.magicdraw.examples.autoid.squarenumber;

import com.nomagic.annotation.OpenApiAll;
import com.nomagic.magicdraw.autoid.IJavaNumberPart;


/**
 * This sample will create Square Numbers as output.
 *
 * Jun 29, 2011
 * @author Roger Holenweger
 *
 */
@OpenApiAll
public class SquareNumberPart implements IJavaNumberPart {

	@Override
	public String generateNextId(String lastNumberPart) {

		int nextSquare = 1;
		try {
			if ("".equals(lastNumberPart)) {
				lastNumberPart = "0";
			}
			int square = Integer.parseInt(lastNumberPart);
			int number = (int) Math.sqrt(square) + 1;
			nextSquare = (int) Math.pow(number, 2);

		} catch (NumberFormatException ex) {
			// already set nextSquare as initialValue
		}
		return Integer.toString(nextSquare);
	}
}
