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
package com;

import com.bay12games.df.rawedit.Autocomplete;
import com.bay12games.df.rawedit.Config;
import com.bay12games.df.rawedit.DocumentLoader;
import com.bay12games.df.rawedit.SyntaxHighlightDocumentListener;
import com.bay12games.df.rawedit.gui.RedoAction;
import com.bay12games.df.rawedit.gui.ScrollablePopupMenu;
import com.bay12games.df.rawedit.gui.UndoAction;
import com.bay12games.df.rawedit.model.Model;
import com.bay12games.df.rawedit.model.Node;
import com.bay12games.df.rawedit.xml.entities.Argument;
import com.bay12games.df.rawedit.xml.entities.Token;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.plaf.TreeUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.text.AbstractDocument.DefaultDocumentEvent;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.undo.UndoManager;

/**
 *
 * @author Matus Goljer
 * @version 1.0
 */
public class Main extends JPanel {

    private static final String pattern = "(\\[.*?\\])|(.*?\\s+)|(\\d+)"; //"(\\[.*?\\])|(.*?)"; //
    private static Config config;
    private static Autocomplete ac;

    public static Config getConfig() {
        return config;
    }

    public Main() {
        super(new BorderLayout());

        // prepare document and keyword stuff
        final DefaultStyledDocument d = DocumentLoader.load("testraw.txt");
        d.addDocumentListener(new SyntaxHighlightDocumentListener(config));
        ac = new Autocomplete();
        //ac.build(config.getKeywords().keySet());

        Model model = DocumentLoader.buildModel(d);

        final JTree tree = new JTree(model) {

            @Override
            public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Node node = (Node) value;
                if (node.getUserObject() instanceof String) {
                    return (String) node.getUserObject();
                }
                else if (node.getUserObject() instanceof Argument) {
                    Argument argument = (Argument) node.getUserObject();
                    return argument.getType();
                }
                Token token = (Token) node.getUserObject();
                return token.getName();
            }
        };

        tree.setRootVisible(false);
        //tree.setPreferredSize(new Dimension(200, 600));

        final JTextPane text = new JTextPane();

        final ScrollablePopupMenu popup = new ScrollablePopupMenu(); //ac.hint("").toArray()
        popup.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (popup.getSelectedItem() != null) {
                    String selectedItemText = popup.getSelectedItem().toString();
                    if (!selectedItemText.equals("No suggestions")) {
                        System.out.println("Selected item: " + selectedItemText);
                        ac.complete(d, text.getCaretPosition(), selectedItemText);
                    }
                }
            }
        });

        popup.addPopupMenuListener(new PopupMenuListener() {

            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                JComboBox box = (JComboBox) e.getSource();
                Object comp = box.getUI().getAccessibleChild(box, 0);
                if (!(comp instanceof JPopupMenu)) {
                    return;
                }
                JComponent scrollPane = (JComponent) ((JPopupMenu) comp).getComponent(0);
                Dimension size = new Dimension();
                size.width = 200;//box.getPreferredSize().width;
                size.height = scrollPane.getPreferredSize().height;
                scrollPane.setPreferredSize(size);
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });


        text.setDocument(d);

        // hacky undo manager
        // discards all change events (color/decoration) (so we won't have
        // undo/redo spam when we fix the highlighting
        UndoManager manager = new UndoManager() {

            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                if (((DefaultDocumentEvent) e.getEdit()).getType()
                  != DocumentEvent.EventType.CHANGE) {
                    super.undoableEditHappened(e);
                }
            }
        };
        manager.setLimit(1000);
        text.getDocument().addUndoableEditListener(manager);

        Action undoAction = new UndoAction(manager);
        Action redoAction = new RedoAction(manager);

        text.setFont(new Font("Lucida Console", 0, 12));

        text.registerKeyboardAction(undoAction, KeyStroke.getKeyStroke(
          KeyEvent.VK_Z, InputEvent.CTRL_MASK), JComponent.WHEN_FOCUSED);
        text.registerKeyboardAction(redoAction, KeyStroke.getKeyStroke(
          KeyEvent.VK_Y, InputEvent.CTRL_MASK), JComponent.WHEN_FOCUSED);

        text.addKeyListener(new KeyAdapter() {

            private void updatePopup(KeyEvent e) {
                if (popup.isVisible()) {
//                    System.out.println(KeyEvent.getKeyText(e.getKeyCode()));
//                    popup.dispatchEvent(e);
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_DOWN:
                        case KeyEvent.VK_UP:
                        case KeyEvent.VK_ENTER:
                        case KeyEvent.VK_HOME:
                        case KeyEvent.VK_END:
                        case KeyEvent.VK_PAGE_UP:
                        case KeyEvent.VK_PAGE_DOWN:
                        case KeyEvent.VK_ESCAPE:
                            popup.dispatchEvent(e);
                            break;
                    }

                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                boolean processed = false;
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_SPACE:
                            if ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) > 0) {
                                System.out.println("EVENT");
                                ac.showHint(d, text, popup);
                                processed = true;
                            }
                            break;
                        case KeyEvent.VK_LEFT:
                        case KeyEvent.VK_RIGHT:
                            if (!popup.isVisible()) {
                                break;
                            }
                            int index = text.getCaretPosition() + (e.getKeyCode() == KeyEvent.VK_LEFT ? -1 : 1);
                            String prefix = ac.getPrefix(d, index);
                            popup.replaceItems(ac.hint(prefix));
                            popup.updatePopup();
                            break;
                    }
                }
                if (!processed) {
                    updatePopup(e);
                }
            }
        });

        final JScrollPane scrollpane = new JScrollPane(text);
        final JScrollPane treeScrollpane = new JScrollPane(tree);
        treeScrollpane.setPreferredSize(new Dimension(300, 600));

//        add("West", scrollpane);
//        add("East", treeScrollpane);
//        add("North", popup);
        add(popup, BorderLayout.PAGE_START);
        add(scrollpane, BorderLayout.CENTER);
        add(treeScrollpane, BorderLayout.LINE_END);
        setPreferredSize(new Dimension(600, 600));

//        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
//
//            public boolean dispatchKeyEvent(KeyEvent e) {
//                boolean discardEvent = false;
//
//                if (e.getID() == KeyEvent.KEY_PRESSED) {
//                    switch (e.getKeyCode()) {
//                        case KeyEvent.VK_SPACE:
//                            if ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) > 0) {
//                                discardEvent = true;
//                                ac.showHint((DefaultStyledDocument) d, text, popup);
//                            }
//                            break;
//                        case KeyEvent.VK_F:
//                            break;
//                    }
//                }
//                return discardEvent;
//            }
//        });
    }

    public static void main(String[] args) {
        config = new Config();

        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                JFrame frame = new JFrame("Dwarf Fortress RAW editor");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setContentPane(new Main());
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
