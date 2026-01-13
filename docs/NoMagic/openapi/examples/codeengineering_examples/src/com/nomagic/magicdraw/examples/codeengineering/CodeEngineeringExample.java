/*
 * Copyright (c) 2007 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.codeengineering;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.actions.ActionsID;
import com.nomagic.magicdraw.ce.CodeEngineeringManager;
import com.nomagic.magicdraw.ce.CodeEngineeringSet;
import com.nomagic.magicdraw.ce.java.JavaDescriptor;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.plugins.Plugin;

import javax.annotation.CheckForNull;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Collections;

/**
 * Example for showing {@link CodeEngineeringManager} open api methods.
 * How to create code engineering set and how to generate and reverse it.
 *
 * Example action is added to Tools -> Java Set Example.
 * NOTE: It needs project to be executed and .java class for {@link CodeEngineeringManager} reverse.
 *
 * @author Modestas Mikuckas
 */
public class CodeEngineeringExample extends Plugin
{
    @Override
    public void init()
    {
        ActionsConfiguratorsManager manager = ActionsConfiguratorsManager.getInstance();
        createMenuConfiguration(manager);
    }

    /**
     * Adds action to menu tools.
     *
     * @param manager instance of {@link ActionsConfiguratorsManager}
     */
    private static void createMenuConfiguration(ActionsConfiguratorsManager manager)
    {
        AMConfigurator mainMenuConfigurator = new AMConfigurator()
        {
            @Override
            public void configure(ActionsManager manager)
            {
                ActionsCategory category = (ActionsCategory) manager.getActionFor(ActionsID.TOOLS);
                if (category != null)
                {
                    ActionsCategory innerCategory = new ActionsCategory();
                    innerCategory.addAction(new JavaSetAction());
                    category.addAction(innerCategory);
                }
            }

            @Override
            public int getPriority()
            {
                return AMConfigurator.HIGH_PRIORITY;
            }
        };
        manager.addMainMenuConfigurator(mainMenuConfigurator);
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

    /**
     * Action for demonstrating {@link CodeEngineeringManager} reverse and generate
     * methods and how to create code engineering set.
     */
    private static class JavaSetAction extends NMAction
    {
        /**
         * Action id.
         */
        private static final String JAVA_SET_ACTION_ID = "JAVA_SET_ACTION_ID";

        /**
         * Constructor.
         */
        public JavaSetAction()
        {
            super(JAVA_SET_ACTION_ID, "Java Set Example", null);
        }

        @Override
        public void updateState()
        {
            setEnabled(Application.getInstance().getProject() != null);
        }

        @Override
        public void actionPerformed(@CheckForNull ActionEvent e)
        {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.addChoosableFileFilter(new FileFilter()
            {
                @Override
                public boolean accept(File f)
                {
                    return f.getName().endsWith(".java");
                }

                @Override
                public String getDescription()
                {
                    return "Java files";
                }
            });
            int i = jFileChooser.showOpenDialog(Application.getInstance().getMainFrame());
            if(i == JFileChooser.APPROVE_OPTION)
            {
                File selectedFile = jFileChooser.getSelectedFile();
                Project project = Application.getInstance().getProject();
                if(project != null)
                {
                    CodeEngineeringSet example = CodeEngineeringManager.createCodeEngineeringSet(JavaDescriptor.JAVA, null, "Example", project, null, null);
                    example.addFilesToCodeEngineeringSet(Collections.singletonList(selectedFile));
                    CodeEngineeringManager.reverse(example, false);

                    CodeEngineeringManager.generate(example);
                }
            }
        }
    }
}
