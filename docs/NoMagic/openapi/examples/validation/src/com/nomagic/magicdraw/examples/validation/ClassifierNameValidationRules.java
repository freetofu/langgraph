/*
 * ClassNameValidationRules
 *
 * $Revision$ $Date$
 * $Author$
 *
 * Copyright (c) 2012 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.validation;

import com.nomagic.annotation.Used;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Classifier;

import javax.annotation.CheckForNull;

/**
 * This class is an example of binary validation rule.
 */
public class ClassifierNameValidationRules
{
    public static final int MIN_LENGTH = 3;
    public static final int MAX_LENGTH = 30;

    /**
     * Validates classifier. Constraint specification should be set to:
     * com.nomagic.magicdraw.examples.validation.ClassifierNameValidationRules.isValid
     * Constrained element of the constraint should be Classifier.
     *
     * @param classifier a classifier.
     * @return true if the specified classifier is valid.
     */
    @Used
    public static boolean isValid(Classifier classifier) {
        return classifierNameError(classifier) == null;
    }

    /**
     * Returns error message if the classifier is invalid. The method return null if the specified classifier is valid.
     * errorMessage tag's value should be:
     * {bin:com.nomagic.magicdraw.examples.validation.ClassifierNameValidationRules.classifierNameError}
     *
     * @param classifier a classifier.
     * @return error message or null.
     */
    @CheckForNull
    public static String classifierNameError(Classifier classifier) {
        String name = classifier.getName();
        if (name.length() < MIN_LENGTH) {
            return "Name of the classifier is too short. Min length is " + MIN_LENGTH + ". Current name length: " + name.length();
        }
        else {
            if (name.length() > MAX_LENGTH) {
                return "Name of the classifier is too long. Max length is " + MAX_LENGTH + ". Current name length: " + name.length();
            }
        }
        return null;
    }
}
