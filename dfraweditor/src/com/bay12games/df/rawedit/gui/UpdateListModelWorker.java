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

import javax.swing.SwingWorker;
import org.apache.log4j.Logger;

/**
 *
 * @author Matus Goljer
 * @version 1.0
 */
public class UpdateListModelWorker extends SwingWorker<Void, Void> {

    private static final Logger log = Logger.getLogger(UpdateListModelWorker.class);
    private char appendToPrefix;
    private RawDocument doc;

    public UpdateListModelWorker(char appendToPrefix, RawDocument doc) {
        this.appendToPrefix = appendToPrefix;
        this.doc = doc;
    }

    @Override
    protected Void doInBackground() throws Exception {
        log.trace("Building model");
        new BuildModelTimerTask(doc).run();
        log.trace("Done building model");
        return null;
    }

    @Override
    protected void done() {
        CompletionWindow.getInstance().updateListModel(appendToPrefix, true);
    }
}
