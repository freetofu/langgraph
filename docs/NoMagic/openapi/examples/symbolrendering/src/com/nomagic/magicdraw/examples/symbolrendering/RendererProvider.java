/*
 * Copyright (c) 2007 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.symbolrendering;

import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElementRenderer;
import com.nomagic.magicdraw.uml.symbols.PresentationElementRendererProvider;
import com.nomagic.magicdraw.uml.symbols.paths.DependencyView;
import com.nomagic.magicdraw.uml.symbols.shapes.PackageView;
import com.nomagic.magicdraw.uml.symbols.shapes.SlotView;

/**
 * Custom renderer provider.
 *
 * @author Martynas Lelevicius
 */
class RendererProvider implements PresentationElementRendererProvider
{
	private final SlotRenderer mSlotRenderer;
	private final DependencyRenderer mDependencyRenderer;
	private final PackageRenderer mPackageRenderer;

	RendererProvider()
	{
		mSlotRenderer = new SlotRenderer();
		mDependencyRenderer = new DependencyRenderer();
		mPackageRenderer = new PackageRenderer();
	}

	@Override
	public PresentationElementRenderer getRenderer(PresentationElement presentationElement)
	{
		if (presentationElement instanceof SlotView)
		{
			// slot renderer
			return mSlotRenderer;
		}

		if (presentationElement instanceof DependencyView)
		{
			// dependency renderer
			return mDependencyRenderer;
		}

		if (presentationElement instanceof PackageView)
		{
			// package renderer
			return mPackageRenderer;
		}

		return null;
	}

}
