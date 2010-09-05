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
package com.bay12games.df.rawedit;

import com.bay12games.df.rawedit.adt.Range;
import com.bay12games.df.rawedit.gui.RawDocument;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;
import org.apache.log4j.Logger;

/**
 *
 * @author Matus Goljer
 * @version 1.0
 */
public class SyntaxHighlightDocumentListener implements DocumentListener {

    private static final Logger log = Logger.getLogger(SyntaxHighlightDocumentListener.class);
    private Config config;

    public SyntaxHighlightDocumentListener(Config config) {
        this.config = config;
    }

    private void update(final DocumentEvent e) {
        final RawDocument d = (RawDocument) e.getDocument();
        final int offset = e.getOffset();
        final int length = e.getLength();

        Element parStart = d.getParagraphElement(offset);
        int start = parStart.getStartOffset();
        int len = parStart.getEndOffset() - parStart.getStartOffset();
        //log.trace("Paragraph start: " + d.getText(start, len));

        Element parEnd = d.getParagraphElement(offset + length);
        int startE = parEnd.getStartOffset();
        int lenE = parEnd.getEndOffset() - parEnd.getStartOffset();
        //log.trace("Paragraph end: " + d.getText(startE, lenE));

        int blockLength = startE + lenE - start;

        //String t = d.getText(start, blockLength);
        //log.trace("Modifying tokens: " + t);
        config.getChangeBuffer().add(new Range(start, start + blockLength));
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        update(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        update(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }
}
