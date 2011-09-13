/*
 * Created on 06.09.2006
 * 
 * Copyright (c) 2006, Manuel Alabor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary 
 * form must reproduce the above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. Neither the name of Manuel Alabor nor the names 
 * of its contributors may be used to endorse or promote products derived from 
 * this software without specific prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package me.alabor.jdbccopier.ui.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;


/**
 * Diese <code>LayoutManager</code>-Implementierung zeigt alle <code>Components</code>
 * in einer Liste an. (Vergleichbar mit der Anordnung von <code>JList</code>).
 * 
 * @author Manuel Alabor
 * @version 1.0
 */
public class ListLayout implements LayoutManager {
    public void removeLayoutComponent(Component comp) {}

    public void addLayoutComponent(String name, Component comp) {}

    public void layoutContainer(Container target) {
        synchronized (target.getTreeLock()) {
            Insets insets = target.getInsets();
            int nmembers = target.getComponentCount();
            int x = insets.left;
            int y = insets.top;

            for (int i = 0; i < nmembers; i++) {
                Component m = target.getComponent(i);
                if (m.isVisible()) {
                    Dimension d = m.getPreferredSize();
                    m.setBounds(x, y, target.getWidth()-(insets.right+insets.left), d.height);
                    y += d.height;
                }
            }
        }
    }

    public Dimension preferredLayoutSize(Container target) {
        synchronized (target.getTreeLock()) {
            Dimension dim = new Dimension(0, 0);
            int nmembers = target.getComponentCount();
            for (int i = 0; i < nmembers; i++) {
                Component m = target.getComponent(i);
                if (m.isVisible()) {
                    Dimension d = m.getPreferredSize();
                    dim.height += d.height;
                    dim.width = Math.max(dim.width, d.width);
                }
            }
            Insets insets = target.getInsets();
            dim.width += insets.left + insets.right;
            dim.height += insets.top + insets.bottom;
            return dim;
        }
    }

    public Dimension minimumLayoutSize(Container target) {
        synchronized (target.getTreeLock()) {
            Dimension dim = new Dimension(0, 0);
            int nmembers = target.getComponentCount();
            for (int i = 0; i < nmembers; i++) {
                Component m = target.getComponent(i);
                if (m.isVisible()) {
                    Dimension d = m.getMinimumSize();
                    dim.height += d.height;
                    dim.width = Math.max(dim.width, d.width);
                }
            }
            Insets insets = target.getInsets();
            dim.width += insets.left + insets.right;
            dim.height += insets.top + insets.bottom;
            return dim;
        }
    }
}