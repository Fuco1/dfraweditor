/*
 *  Copyright (C) 2010 Matus Goljer & Bruno Zimmermann
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.AbstractDocument.DefaultDocumentEvent;
import javax.swing.undo.UndoManager;

import org.apache.log4j.Logger;

import com.bay12games.df.common.gui.ModToolGui;
import com.bay12games.df.dwarfstruct.gui.DwarfStructSelectionTable;
import com.bay12games.df.dwarfstruct.model.DwarfStructElement;
import com.bay12games.df.rawedit.Autocomplete;
import com.bay12games.df.rawedit.Config;
import com.bay12games.df.rawedit.DocumentLoader;
import com.bay12games.df.rawedit.SyntaxHighlightDocumentListener;
import com.bay12games.df.rawedit.SyntaxHighlighter;
import com.bay12games.df.rawedit.gui.BuildModelTimerTask;
import com.bay12games.df.rawedit.gui.CompletionWindow;
import com.bay12games.df.rawedit.gui.DocumentTabButton;
import com.bay12games.df.rawedit.gui.ModelJTree;
import com.bay12games.df.rawedit.gui.RawDocument;
import com.bay12games.df.rawedit.gui.RedoAction;
import com.bay12games.df.rawedit.gui.UndoAction;
import com.bay12games.df.rawedit.model.Model;

/**
 *
 * @author Matus Goljer
 * @author Bruno Zimmermann
 * @version 1.0
 */
public class Main extends JPanel {

    private static final Logger log = Logger.getLogger(Main.class);
    private static Config config;
    
    private static JPanel contentPanel;

    public static Config getConfig() {
        return config;
    }

//    private void hookActions(JTextPane text) {
//        final InputMap inputMap = text.getInputMap();
//        final ActionMap actionMap = text.getActionMap();
//
//        final Action
//
//        final Action nextWordAction = text.getActionMap().get("caret-next-word");
//        Action nextWordWrapper = new AbstractAction() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                log.info("From action wrapper " + e.paramString());
//                nextWordAction.actionPerformed(e);
//            }
//        };
//        text.getActionMap().put("caret-next-word", nextWordWrapper);
//    }
    // [TODO] Move from constructor to createAndShowGUI()!
    public Main() {
        super(new BorderLayout());
        final Autocomplete ac = Autocomplete.getInstance();
        
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

        text.addKeyListener(CompletionWindow.getInstance());
        text.addMouseListener(CompletionWindow.getInstance());

        final JScrollPane scrollpane = new JScrollPane(text);
        final JScrollPane treeScrollpane = new JScrollPane(tree);
        treeScrollpane.setPreferredSize(new Dimension(300, 600));

        final JTabbedPane tabPane = new JTabbedPane();
        tabPane.add(scrollpane, "Document.txt", 0);
        tabPane.setTabComponentAt(0, new DocumentTabButton(tabPane));

        add(tabPane, BorderLayout.CENTER);
        add(treeScrollpane, BorderLayout.LINE_END);
        setPreferredSize(new Dimension(700, 600));

        long time = System.currentTimeMillis();
        log.trace("Initial highlight");
        SyntaxHighlighter.highlight(d, 0, d.getLength());
        log.trace("Initial highlight done " + (System.currentTimeMillis() - time));

        Timer modelBuildingTimer = new Timer("modelBuildingTimer", true);
        TimerTask buildModelAndFixHighlight = new BuildModelTimerTask(tree, d, true);

        // schedule the timer
        modelBuildingTimer.schedule(buildModelAndFixHighlight, 1000, 500);
    }

    public static void main(String[] args) {
    	printInfo("Armoks grace, what is it?");
        log.info("Start");
        Date t1 = new Date();
        config = Config.getInstance();
        Date t2 = new Date();
        log.debug("Time needed for initialize config:" + Long.toString(t2.getTime()-t1.getTime()));


        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
            	printInfo("Runnin");
                ModToolGui frame = null;
				try
				{
					frame = new ModToolGui();
				} catch (HeadlessException e)
				{
					printInfo("An unexpected error occured!\n"+e.toString());
					e.printStackTrace();
				} catch (FileNotFoundException e)
				{
					// TODO Auto-generated catch block
					printInfo("A file was not found!\n"+e.toString());
					e.printStackTrace();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					printInfo("An unexpected error occured!\n"+e.toString());
					e.printStackTrace();
				} 
                config.setMainFrame(frame);
                //TODO Make it remember!
                frame.setCrossPlatformLookAndFeel();
                frame.display();
            }


        });

        log.info("Session closed");
    }
    
    
	public static void printInfo(String info)
	{
		JOptionPane.showMessageDialog(null, info);
	}
}
