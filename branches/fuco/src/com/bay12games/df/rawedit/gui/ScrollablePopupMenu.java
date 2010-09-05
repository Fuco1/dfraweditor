/*
 *  Copyright (C) 2010 Matus Goljer
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.bay12games.df.rawedit.gui;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.ComboPopup;

/**
 *
 * @author viravan, modified by Matus Goljer
 * @version 1.0
 */
public class ScrollablePopupMenu extends JComboBox {

    private boolean keySelection = false;
    private boolean fireSelectionChange = false;
    private MyBasicComboBoxUI myCBUI;
    private int lastX;
    private int lastY;

    public ScrollablePopupMenu() {
        super();
        setup();
    }

    public ScrollablePopupMenu(ComboBoxModel aModel) {
        super(aModel);
        setup();
    }

    public ScrollablePopupMenu(final Object items[]) {
        super(items);
        setup();
    }

    public ScrollablePopupMenu(Vector items) {
        super(items);
        setup();
    }

    private void setup() {
        myCBUI = new MyBasicComboBoxUI();
        setUI(myCBUI);
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() == 10) {
                    keySelection = false;
                    fireActionEvent();
                    return;
                }
                else if (!myCBUI.getPopup().isVisible()) {
                    ke.consume();
                }
                keySelection = true;
            }
        });
        setPreferredSize(new Dimension(0, 0));
        setVisible(true);
    }

    public void addListSelectionListener(ListSelectionListener listener) {
        getPopup().getList().addListSelectionListener(listener);
    }

    /**
     * If the popup is hidden by event, this'll re-show the popup on the last
     * visible location;
     */
    public void updatePopup() {
        showPopup(lastX, lastY);
    }

    public void showPopup(int x, int y) {
        lastX = x;
        lastY = y;

        fireSelectionChange = false;
        setSelectedIndex(0);
        fireSelectionChange = true;

        setLocation(x, y);
        myCBUI.getPopup().show();
    }

    @Override
    public boolean isVisible() {
        return myCBUI != null && myCBUI.getPopup() != null && myCBUI.getPopup().isVisible();
    }

    public ComboPopup getPopup() {
        return myCBUI.getPopup();
    }

    /**
     * Replace all items in this collection with the content of the collection
     * provided. All old items are first removed.
     *
     * @param items New items to be inserted in this component
     */
    public void replaceItems(Collection<String> items) {
        removeAllItems();
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (String s : items) {
            model.addElement(s);
        }
        setModel(model);
    }

    @Override
    public void setSelectedIndex(int index) {
        if (index > dataModel.getSize()) {
            return;
        }
        setSelectedItem(dataModel.getElementAt(index));
    }

    @Override
    public void setSelectedItem(Object anObject) {
        Object oldSelection = selectedItemReminder;
        if (oldSelection == null || !oldSelection.equals(anObject)) {
            if (anObject != null && !isEditable()) {
                boolean found = false;
                for (int i = 0; i < dataModel.getSize(); i++) {
                    if (anObject.equals(dataModel.getElementAt(i))) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return;
                }
            }
            dataModel.setSelectedItem(anObject);
            if (selectedItemReminder != dataModel.getSelectedItem() && fireSelectionChange) {
                selectedItemChanged();

            }
        }
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        Object oldSelection = selectedItemReminder;
        Object newSelection = dataModel.getSelectedItem();
        if ((oldSelection == null || !oldSelection.equals(newSelection)) && fireSelectionChange) {
            selectedItemChanged();
        }
        if (!keySelection && fireSelectionChange) {
            fireActionEvent();
        }
    }

    // just to be able to get to the popup
    class MyBasicComboBoxUI extends BasicComboBoxUI {

        public ComboPopup getPopup() {
            return popup;
        }
    }
}
