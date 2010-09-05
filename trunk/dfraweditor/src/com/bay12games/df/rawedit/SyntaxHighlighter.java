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

import com.Main;
import com.bay12games.df.rawedit.gui.RawDocument;
import com.bay12games.df.rawedit.model.Model;
import com.bay12games.df.rawedit.model.Node;
import com.bay12games.df.rawedit.xml.KeyWordType;
import com.bay12games.df.rawedit.xml.entities.Argument;
import com.bay12games.df.rawedit.xml.entities.Container;
import com.bay12games.df.rawedit.xml.entities.Token;
import java.util.Map;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import org.apache.log4j.Logger;

/**
 * Class responsible to highlight keywords in document.
 *
 * @author Matus Goljer
 * @version 1.0
 */
public class SyntaxHighlighter {

    private static final Logger log = Logger.getLogger(SyntaxHighlighter.class);
    public static final Document nullDocument = new DefaultStyledDocument();

    /**
     * Highlight the portion of the text in document.
     *
     * <p><strong>Should run on EDT!</strong>
     *
     * <p><strong>This should generally be used ONLY to fix small portions of
     * document, as to not block EDT for too long.</strong>
     *
     * @param d Document
     * @param start Start index from which we start fixing highlighting
     * @param len Length of the block
     */
    public static void highlight(RawDocument d, int start, int len) {
        Map<String, KeyWordType> types = Main.getConfig().getKeywordTypes();
        Model model = Main.getConfig().getModel();

        MutableAttributeSet as = null;

        int currentNodeIndex = model.getClosestNodeIndexByStartOffset(start);
        if (currentNodeIndex < 0) {
            return;
        }

        reset(d, start, len);

        Node current;
        try {
            while ((current = model.getRanges().get(currentNodeIndex++).getUserData()).getEndOffset() < start + len) {
                as = d.getStyle("default");

                if (current.getUserObject() instanceof Container) {
                    as = types.get("CONTAINER").getSet();
                }
                else if (current.getUserObject() instanceof Token) {
                    Token t = (Token) current.getUserObject();
                    if (!t.isFlag()) {
                        as = types.get("TOKEN").getSet();
                    }
                    else {
                        as = types.get("FLAG").getSet();
                    }
                }
                else if (current.getUserObject() instanceof Argument) {
                    Argument a = (Argument) current.getUserObject();
                    if ("int".equals(a.getType())) {
                        as = types.get("INT").getSet();
                    }
                }

                d.setCharacterAttributes(current.getStartOffset(), current.getLength(), as, true);

                if (currentNodeIndex >= model.getRanges().size()) {
                    break;
                }
            }
        } catch (Exception ex) {
            log.warn("An error occured during highlight", ex);
        }
    }

    /**
     * Reset the attributes on the portion of the text in the document.
     * The DefaultStyledDocument will merge all elements with unified style into one.
     * 
     * @param d Document
     * @param offset Start offset
     * @param len Length of block
     */
    public static void reset(DefaultStyledDocument d, int offset, int len) {
        MutableAttributeSet as = d.getStyle("default");
        d.setCharacterAttributes(offset, len, as, true);
    }
}
