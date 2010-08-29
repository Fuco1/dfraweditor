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

import com.bay12games.df.rawedit.adt.PrefixTree;
import com.bay12games.df.rawedit.gui.ScrollablePopupMenu;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;

/**
 *
 * @author Matus Goljer
 * @version 1.0
 */
public class Autocomplete {

    private PrefixTree trie = new PrefixTree();

    public void build(Set<String> keywords) {
        for (String k : keywords) {
            trie.add(k);
        }
    }

    public List<String> hint(String prefix) {
        PrefixTree subtree = trie.getSubTree(prefix);
        List<String> re = new ArrayList<String>();
        if (subtree == null) {
            re.add("No suggestions");
            return re;
        }
        for (String k : subtree.getItems()) {
            re.add(prefix + k);
        }
        return re;
    }

    public String getPrefix(final DefaultStyledDocument d, int index) {
        Element par = d.getParagraphElement(index);
        int start = par.getStartOffset();
        int len = par.getEndOffset() - par.getStartOffset();
        String text = null;
        try {
            text = d.getText(start, len);
        } catch (BadLocationException ex) {
            return null;
        }
        //System.out.println("Paragraph: " + text);
        String prefix = null;

        TokenIterator ti = new TokenIterator(text);
        for (String token : ti) {
            //System.out.println(token);
            if (start + ti.getTokenStart() <= index && start + ti.getTokenEnd() > index) {
                // we have the right token
                //System.out.println("Right token: " + token);
                prefix = token.substring(0, index - start - ti.getTokenStart());
                //System.out.println("Prefix: " + prefix);
                break;
            }
        }
        return prefix;
    }

    public void showHint(final DefaultStyledDocument d, final JTextPane tp, final ScrollablePopupMenu popup) {
//        SwingUtilities.invokeLater(new Runnable() {
//
//            public void run() {
        Caret c = tp.getCaret();
        Point caretPos = c.getMagicCaretPosition();
        if (caretPos == null) {
            caretPos = new Point(0, 0);
        }

        int index = c.getDot();
        String prefix = getPrefix(d, index);

        popup.replaceItems(hint(prefix));
        popup.showPopup(caretPos.x, caretPos.y + 15);
//            }
//        });
    }

    public void complete(final DefaultStyledDocument d, final int index, final String insert) {
//        SwingUtilities.invokeLater(new Runnable() {
//
//            public void run() {
        String prefix = getPrefix(d, index);
        String insertAtCaret = insert.substring(prefix.length(), insert.length());

        try {
            d.insertString(index, insertAtCaret, null);
        } catch (BadLocationException ex) {
        }

        SyntaxHighlighter.highlightParagraph(d, index);
    }
//        });
//    }
}
