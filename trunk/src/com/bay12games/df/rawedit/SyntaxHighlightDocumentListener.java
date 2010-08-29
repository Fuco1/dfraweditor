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

import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;

/**
 *
 * @author Matus Goljer
 * @version 1.0
 */
public class SyntaxHighlightDocumentListener implements DocumentListener {

    private Config config;

    public SyntaxHighlightDocumentListener(Config config) {
        this.config = config;
    }

    private void update(DocumentEvent e) {
        final DefaultStyledDocument d = (DefaultStyledDocument) e.getDocument();
        final int offset = e.getOffset();
        final int length = e.getLength();
        
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                try {
                    Element parStart = d.getParagraphElement(offset);
                    int start = parStart.getStartOffset();
                    int len = parStart.getEndOffset() - parStart.getStartOffset();
                    //System.out.println("Paragraph start: " + d.getText(start, len));

                    Element parEnd = d.getParagraphElement(offset + length);
                    int startE = parEnd.getStartOffset();
                    int lenE = parEnd.getEndOffset() - parEnd.getStartOffset();
                    //System.out.println("Paragraph end: " + d.getText(startE, lenE));
                    
                    int blockLength = startE + lenE - start;

                    String t = d.getText(start, blockLength);
                    //System.out.println("Modifying tokens: " + t);

                    SyntaxHighlighter.reset(d, start, blockLength);
                    SyntaxHighlighter.highlight(d, start, blockLength);
                } catch (BadLocationException ex) {
                }
            }
        });
    }

    public void insertUpdate(DocumentEvent e) {
        update(e);
    }

    public void removeUpdate(DocumentEvent e) {
        update(e);
    }

    public void changedUpdate(DocumentEvent e) {
        //  update(e);
    }
}
