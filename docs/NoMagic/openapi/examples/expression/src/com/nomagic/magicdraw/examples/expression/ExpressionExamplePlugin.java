/*
 * ExpressionExample
 *
 * $Revision$ $Date$
 * $Author$
 *
 * Copyright (c) 2009 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.expression;

import com.nomagic.magicdraw.evaluation.EvaluationConfigurator;
import com.nomagic.magicdraw.plugins.Plugin;

/**
 * The example shows how to create and register a new expression.
 *
 * @author Rimvydas Vaidelis
 */
public class ExpressionExamplePlugin extends Plugin
{
    @Override
	public void init()
    {
        EvaluationConfigurator.getInstance().registerBinaryImplementers(ExpressionExamplePlugin.class.getClassLoader());
    }

    @Override
	public boolean close()
    {
        return true;
    }

    @Override
	public boolean isSupported()
    {
        return true;
    }
}
