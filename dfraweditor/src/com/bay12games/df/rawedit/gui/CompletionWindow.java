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

import com.bay12games.df.rawedit.Autocomplete;
import com.bay12games.df.rawedit.Config;
import com.bay12games.df.rawedit.TextInsertionProvider;
import com.bay12games.df.rawedit.TokenDescriptionProvider;
import com.bay12games.df.rawedit.model.Model;
import com.bay12games.df.rawedit.model.Node;
import java.awt.Color;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.JTextComponent;
import org.apache.log4j.Logger;

/**
 * @version 0.0.3
 * Code completion window
 * @author Yves Zoundi <yveszoundi at users dot sf dot net>, modified and enhanced by Matus Goljer
 */
public class CompletionWindow extends JWindow implements KeyListener,
                                                         ListSelectionListener,
                                                         FocusListener,
                                                         MouseListener {

    private static final long serialVersionUID = 6201420460065287340L;
    private static final Logger log = Logger.getLogger(CompletionWindow.class);
    private static final ArrayList<String> pleaseWait = new ArrayList<String>();
    private static CompletionWindow instance;
    private JList completionList;
    private CompletionListModel completionListModel;
    private JScrollPane scrollPane;
    private JTextArea descriptionArea;
    private JScrollPane descriptionAreaScrollPane;
    private JTextComponent textEditor;
    private int startPos;
    private int endPos;
    private int pos;
    private int nodeIndex;
    private Node node;
    private UpdateListModelWorker worker;
    private TextInsertionProvider insertionProvider = Autocomplete.getInstance();
    private TokenDescriptionProvider descriptionProvider = Config.getInstance();

    static {
        pleaseWait.add("Please wait...");
    }

    public CompletionWindow() {
        super(Config.getInstance().getMainFrame());
        initComponents();
    }

    /**
     * Method getCompletionListModel returns the completionListModel of this CompletionWindow object.
     *
     * @return the completionListModel (type CompletionListModel) of this CompletionWindow object.
     */
    public CompletionListModel getCompletionListModel() {
        return completionListModel;
    }

    /**
     * Single instance of this class
     *
     * @return the single instance of this class
     */
    public static synchronized CompletionWindow getInstance() {
        if (instance == null) {
            instance = new CompletionWindow();
        }

        return instance;
    }

    private void closeWindow() {
        log.debug("Close AC Window");
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                setVisible(false);

                textEditor.requestFocusInWindow();
                textEditor.requestFocus();
                textEditor.grabFocus();
            }
        });
    }

    private void initComponents() {
        completionListModel = new CompletionListModel();
        completionList = new JList(completionListModel);
        completionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        completionList.setFocusable(false);
        completionList.addFocusListener(this);
        completionList.addListSelectionListener(this);
        completionList.addMouseListener(this);
        completionList.addKeyListener(this);
        //completionList.getInputMap().clear();
        //completionList.setPreferredSize(new Dimension(350, 160));
        scrollPane = new JScrollPane(completionList);

        descriptionArea = new JTextArea();
        descriptionArea.setBackground(new Color(240, 240, 240));
        descriptionArea.setEditable(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Arial", 0, 12));
        descriptionArea.setText("No description available");
        //descriptionArea.setPreferredSize(new Dimension(350, 100));
        descriptionAreaScrollPane = new JScrollPane(descriptionArea);

        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 0;

        add(scrollPane, c);

        c.gridx = 0;
        c.gridy = 1;
        add(descriptionAreaScrollPane, c);

        setSize(350, 350);

        scrollPane.getInputMap().clear();

        this.setFocusable(true);
    }

    /**
     * Show the completion window
     *
     * @param textEditor The text editor component
     */
    public void showWindow(JTextComponent textEditor) {
        this.textEditor = textEditor;
        completionList.setVisibleRowCount(7);

        try {
            Rectangle caretBounds = textEditor.modelToView(textEditor.getCaretPosition());
            Point p = new Point(textEditor.getLocationOnScreen());
            p.translate(caretBounds.x, caretBounds.y + caretBounds.height);

            if ((p.getY() + this.getHeight()) > Toolkit.getDefaultToolkit().getScreenSize().getHeight()) {
                p.translate(0,
                  (int) (-this.getHeight() - caretBounds.getHeight()));
            }

            int outOfSight = (int) ((p.getX() + this.getWidth())
              - Toolkit.getDefaultToolkit().getScreenSize().getWidth());

            if (outOfSight > 0) {
                p.translate(-outOfSight, 0);
            }

            pos = textEditor.getCaretPosition();

            try {
                Model model = Config.getInstance().getModel();
                nodeIndex = model.getClosestNodeIndexByStartOffset(pos);
                node = model.getRanges().get(nodeIndex).getUserData();
                startPos = node.getStartOffset();
                endPos = node.getEndOffset();
            } catch (NullPointerException ex) {
                log.warn("Setting default interval for suggestion.");
                startPos = pos;
                endPos = startPos;
                node = null;
                nodeIndex = -1;
            }

            updateListModel();

            setLocation(p);
        } catch (BadLocationException ble) {
            log.debug(ble.getMessage(), ble);
        }

        setVisible(true);
        valueChanged(null);
        // [TODO] platform dependant
        textEditor.requestFocus();
    }

    private void insertSelection() {
        String selection = ((String) completionList.getSelectedValue());
        insertionProvider.complete((DefaultStyledDocument) textEditor.getDocument(),
          Config.getInstance().getModel(), pos, selection);
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        JTextComponent source = (JTextComponent) e.getSource();
        if (e.getKeyCode() == KeyEvent.VK_SPACE && (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0) {
            log.debug("Show AC window");
            if (isVisible()) {
                return;
            }
            log.debug("Show AC window - yes, really show it");
            showWindow(source);
            e.consume();
            return;
        }

        if (isVisible()) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_ENTER:
                    insertSelection();
                case KeyEvent.VK_ESCAPE:
                    closeWindow();
                    e.consume();
                    break;
                case KeyEvent.VK_PAGE_DOWN:
                case KeyEvent.VK_PAGE_UP:
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_UP:
                    KeyStroke key = KeyStroke.getKeyStrokeForEvent(e);
                    Object actionId = completionList.getInputMap().get(key);
                    Action action = completionList.getActionMap().get(actionId);
                    SwingUtilities.notifyAction(action, key, e, completionList, e.getModifiers());
                    e.consume();
                    break;
                default:
                    if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        pos = textEditor.getCaretPosition() + (e.getKeyCode() == KeyEvent.VK_LEFT ? -1 : 1);
                        log.trace("Update on arrow, position: " + pos);
                        updateListModel();
                    }
                    else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                        pos = textEditor.getCaretPosition() - 1;
                        endPos--;
                        log.trace("Update suggestion model on backspace: " + pos);
                        updateListModel();
                    }
                    else if (e.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
                        pos = textEditor.getCaretPosition();
                        endPos++;
                        try {
                            //String s = source.getDocument().getText(pos - 1, 1);
                            log.trace("Last letter?: \"" + e.getKeyChar() + "\"");
                        } catch (Exception ex) {
                        }
                        log.trace("Update suggestion model: " + pos);
                        updateListModel(e.getKeyChar());
                    }
            }
        }
    }

    public void updateListModel() {
        updateListModel('\0', false);
    }

    public void updateListModel(char appendToPrefix) {
        updateListModel(appendToPrefix, false);
    }

    public void updateListModel(char appendToPrefix, boolean fromWorker) {
        // update ranges of the current language token
        Model model = Config.getInstance().getModel();
        if (!Config.getInstance().getChangeBuffer().isEmpty() && !fromWorker) {
            if (worker != null) {
                return;
            }
            updateData(pleaseWait);
            worker = new UpdateListModelWorker(appendToPrefix, (RawDocument) textEditor.getDocument());
            worker.execute();
            return;
        }

        if (fromWorker) {
            worker = null;
            log.trace("List update from worker");
            int oldIndex = nodeIndex;
            Node oldNode = node;
            model = Config.getInstance().getModel();
            try {
                nodeIndex = model.getClosestNodeIndexByStartOffset(pos);
                node = model.getRanges().get(nodeIndex).getUserData();
            } catch (NullPointerException ex) {
                nodeIndex = oldIndex;
                node = oldNode;
            }
        }

        log.trace(pos);
        log.trace(startPos);
        log.trace(endPos);
        if (pos > endPos || pos < startPos) {
            log.debug("Close window, we're out of interval");
            closeWindow();
            return;
        }

        String prefix = "";

        try {
            prefix = textEditor.getDocument().getText(startPos, pos - startPos);
        } catch (BadLocationException ex) {
            log.warn("An error occured while retrieving text from document model.", ex);
        }

        if (appendToPrefix != '\0') {
            prefix += appendToPrefix;
        }

        final String prefix2 = prefix;

//        SwingUtilities.invokeLater(new Runnable() {
//
//            @Override
//            public void run() {
        List<String> sug = Autocomplete.getInstance().getSuggestionList(
          model, nodeIndex, node, prefix2, pos);
        updateData(sug);
        valueChanged(null);
//            }
//        });
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e != null) {
            completionList.ensureIndexIsVisible(completionList.getSelectedIndex());
        }
        if (isVisible()) {
            String element = (String) completionList.getSelectedValue();
            descriptionArea.setText(
              descriptionProvider.getDescriptionForElement(pos, element));
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
    }

    @Override
    public void focusLost(FocusEvent e) {
        log.debug("Focus lost");
        if (isVisible()) {
            closeWindow();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (isVisible()) {
            log.trace("Mouse clicked ...");
            if (e.getSource() == completionList) {
                log.trace("... in the AC dialog's list");
                insertSelection();
            }
            closeWindow();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isVisible()) {
            closeWindow();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * @param completionItems The sorted collection to be shown in this window's list
     */
    private void updateData(List<String> completionItems) {
        completionListModel.updateData(completionItems);
    }

    public class CompletionListModel extends AbstractListModel {

        private static final long serialVersionUID = 3061505335047555230L;
        private ArrayList<Object> data;

        public CompletionListModel() {
            data = new ArrayList<Object>();
        }

        @Override
        public Object getElementAt(int index) {
            return data.get(index);
        }

        @Override
        public int getSize() {
            return data.size();
        }

        /**
         * Update the completion list items
         *
         * @param completionItems a collection of completion items
         */
        public void updateData(List<String> completionItems) {
            data.clear();
            data.addAll(completionItems);

            // notify the list the content has been updated
            fireContentsChanged(completionList, 0, data.size());

            completionList.revalidate();
            completionList.setSelectedIndex(0);
            completionList.ensureIndexIsVisible(0);
        }
    }
}
