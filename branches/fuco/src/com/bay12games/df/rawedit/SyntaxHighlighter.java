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
import com.bay12games.df.rawedit.xml.KeyWordType;
import com.bay12games.df.rawedit.xml.entities.Container;
import com.bay12games.df.rawedit.xml.entities.Token;
import java.util.Map;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;

/**
 *
 * @author Matus Goljer
 * @version 1.0
 */
public class SyntaxHighlighter {

    public static void highlightParagraph(DefaultStyledDocument d, int index) {
        Element par = d.getParagraphElement(index);
        int start = par.getStartOffset();
        int len = par.getEndOffset() - par.getStartOffset();
        highlight(d, start, len);
    }

    public static void highlight(DefaultStyledDocument d, int offset, int len) {
        Map<String, KeyWordType> types = Main.getConfig().getKeywordTypes();
        Map<String, Container> containers = Main.getConfig().getContainers();
        Map<String, Token> tokens = Main.getConfig().getTokens();

        MutableAttributeSet as = null;
        try {
            TokenIterator in = new TokenIterator(d.getText(offset, len));
            for (String token : in) {
                as = d.getStyle("default");
                if (containers.containsKey(token)) {
                    as = types.get("CONTAINER").getSet();
                }
                else if (tokens.containsKey(token)) {
                    Token t = tokens.get(token);
                    if (t == null) {
                        continue;
                    }
                    if (!t.isFlag()) {
                        as = types.get("TOKEN").getSet();
                    }
                    else {
                        as = types.get("FLAG").getSet();
                    }
                }
                else if (token.matches("\\d+")) {
                    as = types.get("INT").getSet();
                }

                d.setCharacterAttributes(offset + in.getTokenStart(), token.length(), as, true);
            }
        } catch (BadLocationException ex) {
        }
    }

    public static void reset(DefaultStyledDocument d, int offset, int len) {
        MutableAttributeSet as = d.getStyle("default");
        d.setCharacterAttributes(offset, len, as, true);
    }
}
