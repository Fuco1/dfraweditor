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
import com.bay12games.df.rawedit.adt.PrefixTree;
import com.bay12games.df.rawedit.adt.Range;
import com.bay12games.df.rawedit.model.Model;
import com.bay12games.df.rawedit.model.Node;
import com.bay12games.df.rawedit.xml.entities.Argument;
import com.bay12games.df.rawedit.xml.entities.Container;
import com.bay12games.df.rawedit.xml.entities.Token;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import org.apache.log4j.Logger;

/**
 *
 * @author Matus Goljer
 * @version 1.0
 */
public class Autocomplete {

    private static final Autocomplete instance = new Autocomplete();
    private static final Logger log = Logger.getLogger(Autocomplete.class);
    private static final ArrayList<String> noSuggestionArray;

    static {
        noSuggestionArray = new ArrayList<String>();
        noSuggestionArray.add("No suggestions");
    }

    private Autocomplete() {
    }

    public static Autocomplete getInstance() {
        return instance;
    }

    /**
     * Build the ordered list of suggestions for given model, document and offset
     * in the document
     *
     * @param model "DOM" of the current document
     * @param d Document
     * @param offset Offset in the document
     * @return Ordered list of suggestions
     */
    public List<String> getSuggestionList(final Model model,
                                          final Document d,
                                          final int offset) {

        int nodeIndex = model.getClosestNodeIndexByStartOffset(offset);
        Node node = null;
        if (nodeIndex > 0) {
            node = model.getRanges().get(nodeIndex).getUserData();
        }
        //Node node = model.getClosestNodeByStartOffset(offset);
        if (node == null) {
            return noSuggestionArray;
        }

        log.trace(offset);
        log.trace(node.getStartOffset());
        log.trace(node.getEndOffset());
        log.trace(node.getParent().getUserObject().getClass());

        String prefix = null;

        try {
            if (node.isWhitespace()) {
                prefix = "";
            }
            else {
                prefix = d.getText(node.getStartOffset(), offset - node.getStartOffset());
            }
        } catch (BadLocationException ex) {
            return noSuggestionArray;
        }

        PrefixTree suggestions = new PrefixTree();

        // Comment is either a comment or not-finished token definition.
        // So we treat it as a token if it's inside container, and as an
        // argument if it's inside token.
        if (node.isComment()) {
            Node parent = node.getParent();
            // first check Container since it's subclass of Token
            if (parent.getUserObject() instanceof Container) {
                Container container = (Container) parent.getUserObject();
                suggestions = container.getSuggestions();
            }
            else if (parent.getUserObject() instanceof Token) {
                int indexOfArgument = parent.getIndex(node);
                Token t = (Token) parent.getUserObject();
                Argument a = t.getArgument(indexOfArgument);
                suggestions = a.getSuggestions(t, indexOfArgument,
                  getFullTokenList(t.getArgumentSize(), nodeIndex, indexOfArgument));
            }
        }
        else if (node.getUserObject() instanceof Argument) {
            Argument a = (Argument) node.getUserObject();
            Node parent = node.getParent();
            Token t = (Token) parent.getUserObject();
            int indexOfArgument = parent.getIndex(node);
            suggestions = a.getSuggestions(t, indexOfArgument,
              getFullTokenList(t.getArgumentSize(), nodeIndex, indexOfArgument));
        }
        else if (node.getUserObject() instanceof Token) {
            Node parent = node.getParent();
            Object userObject = parent.getUserObject();
            if (userObject instanceof Container) {
                Container container = (Container) userObject;
                suggestions = container.getSuggestions();
            }
        }

        PrefixTree subtree = suggestions.getSubTree(prefix);
        List<String> re = new ArrayList<String>();
        if (subtree == null) {
            return noSuggestionArray;
        }
        for (String k : subtree.getItems()) {
            re.add(prefix + k);
        }
        if (re.isEmpty()) {
            return noSuggestionArray;
        }
        return re;
    }

    private String[] getFullTokenList(int numberOfArgs, int currentRangeIndex, int currentArgIndex) {
        String[] re = new String[numberOfArgs + 1];
        int index = currentRangeIndex - currentArgIndex - 1;
        Model model = Config.getInstance().getModel();
        ArrayList<Range<Node>> ranges = model.getRanges();
        re[0] = ranges.get(index).getUserData().getText();
        for (int i = 1; i < numberOfArgs + 1; i++) {
            if (ranges.get(index + i).getUserData().isComment()) {
                break;
            }
            re[i] = ranges.get(index + i).getUserData().getText();
        }
        return re;
    }

    public void complete(final DefaultStyledDocument d, final Model model, final int index, String insert) {
        int nodeIndex = model.getClosestNodeIndexByStartOffset(index);
        if (nodeIndex == -1) {
            return;
        }

        Node node = model.getRanges().get(nodeIndex).getUserData();

        Map<String, Container> containers = Main.getConfig().getContainers();
        Map<String, Token> tokens = Main.getConfig().getTokens();
        Token c = null;
        if (containers.containsKey(insert)) {
            c = containers.get(insert);
        }
        else if (tokens.containsKey(insert)) {
            c = tokens.get(insert);
        }

        // if we are starting new token, add the closing bracket or colon if apropriate
        if (c != null) {
            if (node.getParent().getUserObject() instanceof Container) {
                if (c.isFlag()) {
                    insert += ']';
                }
                else {
                    insert += ':';
                }
            }
        }

        // if we are starting new token, and it's not already prefixed with opening
        // bracket, add it
        try {
            if (node.getParent().getUserObject() instanceof Container) {
                if (!"[".equals(d.getText(node.getStartOffset() - 1, 1))) {
                    insert = '[' + insert;
                }
            }
        } catch (BadLocationException ex) {
            log.warn("An error occured while completing the text", ex);
        }

        int start = node.getStartOffset();
        int end = node.getEndOffset();
        try {
            if (!node.isWhitespace()) {
                d.replace(start, end - start, insert, null);
            }
            else {
                d.insertString(index, insert, null);
            }
        } catch (BadLocationException ex) {
            log.warn("An error occured while completing the text", ex);
        }
    }
}
