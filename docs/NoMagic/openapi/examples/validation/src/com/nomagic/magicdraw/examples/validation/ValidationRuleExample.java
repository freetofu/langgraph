package com.nomagic.magicdraw.examples.validation;

import com.nomagic.magicdraw.evaluation.EvaluationConfigurator;
import com.nomagic.magicdraw.plugins.Plugin;

/**
 * The example shows how to create and register a new validation rules.
 *
 * @author Rimvydas Vaidelis
 */
public class ValidationRuleExample extends Plugin
{
    @Override
	public void init()
    {
        EvaluationConfigurator.getInstance().registerBinaryImplementers(ValidationRuleExample.class.getClassLoader());
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
