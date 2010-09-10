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

import com.bay12games.df.rawedit.Config;
import com.bay12games.df.rawedit.DocumentLoader;
import com.bay12games.df.rawedit.SyntaxHighlighter;
import com.bay12games.df.rawedit.adt.Range;
import com.bay12games.df.rawedit.model.Model;
import java.util.ArrayList;
import java.util.TimerTask;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.text.Segment;
import org.apache.log4j.Logger;

/**
 *
 * @author Matus Goljer
 * @version 1.0
 */
public class BuildModelTimerTask extends TimerTask {

    private static final Logger log = Logger.getLogger(BuildModelTimerTask.class);
    private static final Config config = Config.getInstance();
    private JTree activeTree;
    private RawDocument activeDoc;
    private boolean fixVisual = false;

    public BuildModelTimerTask() {
    }

    public BuildModelTimerTask(RawDocument activeDoc) {
        this.activeDoc = activeDoc;
    }

    public BuildModelTimerTask(JTree activeTree, RawDocument activeDoc, boolean fixVisual) {
        this.activeTree = activeTree;
        this.activeDoc = activeDoc;
        this.fixVisual = fixVisual;
    }

    public void setActiveDoc(RawDocument activeDoc) {
        this.activeDoc = activeDoc;
    }

    public void setActiveTree(JTree activeTree) {
        this.activeTree = activeTree;
    }

    @Override
    public void run() {
        synchronized (getClass()) {
            if (config.getChangeBuffer().isEmpty()) {
                return;
            }

            try {
                // should be faster then pulling all the text at once.
                StringBuilder sb = new StringBuilder();
                int nleft = activeDoc.getLength();
                Segment seg = new Segment();
                int offs = 0;
                seg.setPartialReturn(true);
                while (nleft > 0) {
                    activeDoc.getText(offs, nleft, seg);
                    sb.append(seg.toString());
                    nleft -= seg.count;
                    offs += seg.count;
                }
                String data = sb.toString();

                Model model = DocumentLoader.buildModel(data);
                // [WARNING] Possibly thread-unsafe
                config.setModel(model);
                // [TODO] Change buffer must be able to handle multiple documents

                if (fixVisual) {
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
                                    SyntaxHighlighter.highlight(activeDoc, range.getBottom(), range.getTop() - range.getBottom());
                                }
                                log.trace("Done fixing highlight " + (System.currentTimeMillis() - time));
                            }
                            // [TODO] TEMP!!!!!!!!!!!!!
//                            activeTree.setModel(config.getModel());
//                            int row = 0;
//                            while (row < activeTree.getRowCount()) {
//                                activeTree.expandRow(row);
//                                row++;
//                            }
                            // [TODO] TEMP!!!!!!!!!!!!!s
                        }
                    });
                }
            } catch (Exception ex) {
                log.error("An error occured in timer thread!", ex);
            }
        }
    }
}
