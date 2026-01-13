/*
 * Copyright (c) 2007 NoMagic, Inc. All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.dependencymatrix;

import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.ui.MainFrame;

import javax.annotation.CheckForNull;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * Action for samples
 *
 * @author Vytautas Dagilis
 */
class SampleAction extends NMAction
{

    private final String mText;

    public SampleAction(String text)
    {
        super(text, text, KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), null);
        mText = text;
    }

    @Override
    public void actionPerformed(@CheckForNull ActionEvent e)
    {
        MainFrame mainFrame = Application.getInstance().getMainFrame();
        JOptionPane.showMessageDialog(mainFrame, mText);
    }
}
