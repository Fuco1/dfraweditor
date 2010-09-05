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
import com.bay12games.df.rawedit.SyntaxHighlighter;
import com.bay12games.df.rawedit.adt.Range;
import com.bay12games.df.rawedit.gui.ModelJTree;
import com.bay12games.df.rawedit.gui.RawDocument;
import com.bay12games.df.rawedit.gui.RedoAction;
import com.bay12games.df.rawedit.gui.ScrollablePopupMenu;
import com.bay12games.df.rawedit.gui.UndoAction;
import com.bay12games.df.rawedit.model.Model;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.AbstractDocument.DefaultDocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Segment;
import javax.swing.undo.UndoManager;
import org.apache.log4j.Logger;

/**
 *
 * @author Matus Goljer
 * @version 1.0
 */
public class Main extends JPanel {

    private static final Logger log = Logger.getLogger(Main.class);
    private static Config config;

    public static Config getConfig() {
        return config;
    }

    // [TODO] Move from constructor to createAndShowGUI()!
    public Main() {
        super(new BorderLayout());
        final Autocomplete ac = Autocomplete.getInstance();

        // window for descriptions
        final JWindow window = new JWindow();
        final JTextArea descriptionArea = new JTextArea();
        descriptionArea.setBackground(new Color(240, 240, 240));
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        final JScrollPane descriptionAreaPane = new JScrollPane(descriptionArea);
        window.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        window.add(descriptionAreaPane, c);
        window.setPreferredSize(new Dimension(300, 150));
        window.pack();
        // window end

        // prepare document and keyword stuff
        RawDocument d1 = null;
        try {
            d1 = DocumentLoader.load("testraw.txt");
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            System.exit(2);
        }

        final RawDocument d = d1;

        SyntaxHighlightDocumentListener dl = new SyntaxHighlightDocumentListener(config);
        d.addDocumentListener(dl);

        // build model
        log.debug("Building initial model: " + System.currentTimeMillis());
        Model model = null;
        try {
            model = DocumentLoader.buildModel(d.getText(0, d.getLength()));
        } catch (BadLocationException ex) {
        }
        log.debug(System.currentTimeMillis());
        Config.getInstance().setModel(model);

        final JTree tree = new ModelJTree(model);
        // expand all treenodes
        int row = 0;
        while (row < tree.getRowCount()) {
            tree.expandRow(row);
            row++;
        }
        tree.setRootVisible(false);

        final JTextPane text = new JTextPane();

        final ScrollablePopupMenu popup = new ScrollablePopupMenu();
        popup.addActionListener(
          new ActionListener() {

              @Override
              public void actionPerformed(ActionEvent e) {
                  if (popup.getSelectedItem() != null) {
                      String selectedItemText = popup.getSelectedItem().toString();
                      if (!selectedItemText.equals("No suggestions")) {
                          System.out.println("Selected item: " + selectedItemText);
                          //ac.complete(d, text.getCaretPosition(), selectedItemText);
                          ac.complete(d, config.getModel(), text.getCaretPosition(), selectedItemText);
                      }
                  }
              }
          });

        popup.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                JComboBox box = (JComboBox) e.getSource();
                Object comp = box.getUI().getAccessibleChild(box, 0);
                if (!(comp instanceof JPopupMenu)) {
                    return;
                }
                JComponent scrollPane = (JComponent) ((JPopupMenu) comp).getComponent(0);
                Dimension size = new Dimension();
                size.width = 200;
                size.height = scrollPane.getPreferredSize().height;
                scrollPane.setPreferredSize(size);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                window.setVisible(false);
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                window.setVisible(false);
            }
        });

        popup.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (window.isVisible()) {
                    String element = (String) ((JList) e.getSource()).getSelectedValue();
                    descriptionArea.setText(config.getDescriptionForElement(text.getCaretPosition(), element));
                }
            }
        });

        // [TODO] find a better SAFE solution (we can forget to add the
        // pane to the document and break deattaching on edits
        // [WARNING] not curently used
        d.add(text);
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

        // [TODO] MOVE TO CONFIG!
        text.setFont(new Font("Lucida Console", 0, 12));

        text.registerKeyboardAction(undoAction, KeyStroke.getKeyStroke(
          KeyEvent.VK_Z, InputEvent.CTRL_MASK), JComponent.WHEN_FOCUSED);
        text.registerKeyboardAction(redoAction, KeyStroke.getKeyStroke(
          KeyEvent.VK_Y, InputEvent.CTRL_MASK), JComponent.WHEN_FOCUSED);

        text.addKeyListener(new KeyAdapter() {

            private void updatePopup(KeyEvent e) {
                if (popup.isVisible()) {
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
                                log.trace("Key event in JTextPane - invoke AC suggestion dialog");
                                Caret c = text.getCaret();
                                Point caretPos = c.getMagicCaretPosition();
                                if (caretPos == null) {
                                    caretPos = new Point(0, 0);
                                }

                                int index = c.getDot();

                                List<String> sug = ac.getSuggestionList(config.getModel(), d, index);
                                popup.replaceItems(sug);
                                popup.showPopup(caretPos.x, caretPos.y + 15);
                                Point p = popup.getLocationOnScreen();
                                descriptionArea.setText(config.getDescriptionForElement(index, sug.get(0)));
                                //[TODO] better way to figure out height? Maybe we should place it over the list anyway...
                                window.setLocation(p.x, p.y + (int) popup.getPopup().getList().getVisibleRect().getHeight());
                                window.setVisible(true);
                                processed = true;
                            }
                            break;
                        // THIS IS NOT SAFE UNDER EVERY LAF!
                        // [TODO]: Find a proper way to determine the cursor has moved.
                        // [TODO]: Find a way to detect typing and deleting of characters
                        case KeyEvent.VK_LEFT:
                        case KeyEvent.VK_RIGHT:
                            if (!popup.isVisible()) {
                                break;
                            }
                            int index = text.getCaretPosition() + (e.getKeyCode() == KeyEvent.VK_LEFT ? -1 : 1);
                            List<String> sug = ac.getSuggestionList(config.getModel(), d, index);
                            popup.replaceItems(sug);
                            popup.updatePopup();
                            descriptionArea.setText(config.getDescriptionForElement(index, sug.get(0)));
                            Point p = popup.getLocationOnScreen();
                            window.setLocation(p.x, p.y + (int) popup.getPopup().getList().getVisibleRect().getHeight());
                            window.setVisible(true);
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

        add(popup, BorderLayout.PAGE_START);
        add(scrollpane, BorderLayout.CENTER);
        add(treeScrollpane, BorderLayout.LINE_END);
        setPreferredSize(new Dimension(700, 600));

        long time = System.currentTimeMillis();
        log.trace("Initial highlight");
        SyntaxHighlighter.highlight(d, 0, d.getLength());
        log.trace("Initial highlight done " + (System.currentTimeMillis() - time));

        Timer modelBuildingTimer = new Timer("modelBuildingTimer", true);
        TimerTask buildModelAndFixHighlight = new TimerTask() {

            @Override
            public void run() {
                try {
                    // should be faster then pulling all the text at once.
                    StringBuilder sb = new StringBuilder();
                    int nleft = d.getLength();
                    Segment seg = new Segment();
                    int offs = 0;
                    seg.setPartialReturn(true);
                    while (nleft > 0) {
                        d.getText(offs, nleft, seg);
                        sb.append(seg.toString());
                        nleft -= seg.count;
                        offs += seg.count;
                    }
                    String data = sb.toString();

                    Model model = DocumentLoader.buildModel(data);
                    // [WARNING] Possibly thread-unsafe
                    config.setModel(model);
                    // [TODO] Change buffer must be able to handle multiple documents
                    final ArrayList<Range> changes = config.getChangeBuffer().mergeAndClear();

                    // Fix the GUI on EDT
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            // [TODO] Fix visual tree as well
                            long time = System.currentTimeMillis();
                            if (!changes.isEmpty()) {
                                log.trace("Fixing highlight");
                                for (Range range : changes) {
                                    SyntaxHighlighter.highlight(d, range.getBottom(), range.getTop() - range.getBottom());
                                }
                                log.trace("Done fixing highlight " + (System.currentTimeMillis() - time));
                            }
                        }
                    });


                } catch (Exception ex) {
                    log.error("An error occured in timer thread!", ex);
                }
            }
        };

        // schedule the timer
        modelBuildingTimer.schedule(buildModelAndFixHighlight, 1000, 500);
    }

    public static void main(String[] args) {
        log.info("Start");
        config = Config.getInstance();


        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame frame = new JFrame("Dwarf Fortress RAW editor");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setContentPane(new Main());
                frame.pack();
                frame.setVisible(true);
            }
        });

        log.info("Session closed");
    }
}
