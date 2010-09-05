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
import com.bay12games.df.rawedit.adt.Range;
import com.bay12games.df.rawedit.gui.RawDocument;
import com.bay12games.df.rawedit.model.Model;
import com.bay12games.df.rawedit.model.Node;
import com.bay12games.df.rawedit.xml.entities.Argument;
import com.bay12games.df.rawedit.xml.entities.Container;
import com.bay12games.df.rawedit.xml.entities.Id;
import com.bay12games.df.rawedit.xml.entities.Token;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Pattern;
import javax.swing.text.BadLocationException;
import org.apache.log4j.Logger;

/**
 * Class responsible to load RAW documents and build their models.
 *
 * @author Matus Goljer
 * @version 1.0
 */
public final class DocumentLoader {

    private static final Logger log = Logger.getLogger(DocumentLoader.class);
    /** Precompiled "whitespace" pattern for efficiency */
    private static final Pattern p = Pattern.compile("\\s+");
    /** Root property */
    private static final String ROOT_STRING = "ROOT_STRING";

    private DocumentLoader() {
    }

    /**
     * Load a RAW text document.
     *
     * @param source Path to the file to be loaded
     * @return A new instance of {@code RawDocument} representing the content of this file.
     * @throws IOException
     * @see RawDocument
     */
    public static RawDocument load(String source) throws IOException {
        return load(new File(source));
    }

    /**
     * Load a RAW text document.
     *
     * @param file File to be loaded
     * @return A new instance of {@code RawDocument} representing the content of this file.
     * @throws IOException
     * @see RawDocument
     */
    public static RawDocument load(File file) throws IOException {
        RawDocument d = new RawDocument();
        LineNumberReader r = null;

        try {
            r = new LineNumberReader(new BufferedReader(new FileReader(file)));
            char[] buf = new char[16384];
            int len = 0;
            while ((len = r.read(buf)) != -1) {
                // document should be detached
                // [TODO]: use thread-unsafe batch insert with manual sync! (15-20x performance gain)
                // [TODO]: find another way to get rid of windows' \r
                d.insertString(d.getLength(), String.copyValueOf(buf, 0, len).replace("\r", ""), null);
            }
        } catch (IOException ex) {
            log.error("An exception occured while loading a RAW file", ex);
            throw new IOException("An exception occured while loading a RAW file", ex);
        } catch (BadLocationException ex) {
            log.error("An exception occured while loading a RAW file", ex);
            throw new IOException("An exception occured while loading a RAW file", ex);
        } finally {
            if (r != null) {
                try {
                    r.close();
                } catch (IOException ex) {
                    log.error("An exception occured while closing a RAW file", ex);
                    throw new IOException("An exception occured while closing a RAW file", ex);
                }
            }
        }

        return d;
    }

    /**
     * Build a Model tree from the specified content. See {@link Model} for details on how
     * the model is stored.
     * 
     * @param content String content to be parsed (the text of the document, not the
     * path to the document!)
     * @return A model of the content.
     * @see Model
     */
    public static Model buildModel(String content) {
        Map<String, Container> containers = Main.getConfig().getContainers();
        Map<String, Token> tokens = Main.getConfig().getTokens();
        Map<String, Id> ids = Main.getConfig().getIds();

        Deque<Node> stack = new LinkedList<Node>();
        Deque<Node> backStack = new LinkedList<Node>();

        Node root = new Node(ROOT_STRING);
        Model model = new Model(root);
        model.setRoot(root);
        stack.add(root);

        TokenIterator ti = new TokenIterator(content);
        Node node = null;
        boolean insideToken = false;
        Argument a = null;
        int argumentIndex = 0;
        int argumentLimit = -1;
        Container topContainer = null;
        Container currentContainer = null;
        Token topToken = null;
        Token currentToken = null;

        for (String t : ti) {
            insideToken = false;
            if (":".equals(ti.getLastStartDelimiter())) {
                insideToken = true;
            }

            log.trace("Token:        " + t);
            log.trace("Start deli:   " + ti.getLastStartDelimiter());
            log.trace("End deli:     " + ti.getLastDelimiter());
            log.trace("Inside token: " + insideToken + "\n");

            // The input token is valid container. We have to find a container that
            // can hold the current container. If there is none, we assume it's a top
            // level container and put it under root
            if (containers.containsKey(t)) {
                // get the container object for the input token and construct the
                // node
                currentContainer = containers.get(t);
                node = new Node(currentContainer);
                boolean added = false;

                while (!added) {
                    if (containerOnTop(stack)) {
                        topContainer = (Container) stack.getLast().getUserObject();
                        if (topContainer.getContainer(t) != null) {
                            // add the container as a child to current container
                            // and start new branch
                            stack.getLast().add(node);
                            stack.add(node);
                            added = true;
                        }
                        else {
                            // get back one level if we can't fit the container there
                            stack.removeLast();
                        }
                    }
                    else if (tokenOnTop(stack)) {
                        // we can't add container to token, go back one level
                        topToken = (Token) stack.getLast().getUserObject();
                        stack.removeLast();
                    }
                    // hacky way to figure out we're at the root
                    else if (stack.getLast().getUserObject() instanceof String) {
                        // However, it might also be a comment.
                        // As comments can't have children, this can only mean we're
                        // at the container/token level, in which case, the case
                        // should've already been catched by some of two previous branches
                        // print some note to investigate.
                        if (stack.getLast().isComment()) {
                            log.warn("We're in the String node which is also a comment."
                              + " Item on top: " + stack.getLast().getUserObject().getClass() + ":"
                              + getShortToString(stack.getLast().getUserObject()));
                            // why? I don't know...
                            stack.removeLast();
                            continue;
                        }
                        // if we're indeed at the root, just add the node as a new child
                        stack.getLast().add(node);
                        stack.add(node);
                        added = true;
                    }
                }
                // ... and set the argument index and limit for the current container
                // Next argumentLimit number of input tokens MIGHT be arguments. For that
                // we need insideToken to be true. If it's false, it means we're
                // outside the [] definition and so the token is new cont/tok/comment
                argumentIndex = 0;
                argumentLimit = currentContainer.getArgumentSize();
            }
            // The input token is a valid token. We search for a container in which
            // we can place it. If there is none, roll back and assume it's a comment
            else if (tokens.containsKey(t)) {
                currentToken = tokens.get(t);
                node = new Node(currentToken);
                boolean added = false;
                backStack.clear();

                while (!added) {
                    if (containerOnTop(stack)) {
                        topContainer = (Container) stack.getLast().getUserObject();
                        if (topContainer.getToken(t) != null) {
                            // if the container can hold the token, add it and
                            // quit the loop
                            stack.getLast().add(node);
                            added = true;
                        }
                        else {
                            // Get back one level if we can't fit the token there
                            // maybe we can add it to the container one level above.
                            // We also store the node to the backStack if we need to
                            // roll back.
                            backStack.add(stack.removeLast());
                        }
                    }
                    else if (tokenOnTop(stack)) {
                        // We can't add token to token, so we go one level up
                        // Again, save the node into rollback stack
                        topToken = (Token) stack.getLast().getUserObject();
                        backStack.add(stack.removeLast());
                    }
                    // hacky way to figure out we're at the root
                    else if (stack.getLast().getUserObject() instanceof String) {
                        // However, it might also be a comment.
                        // As comments can't have children, this can only mean we're
                        // at the container/token level, in which case, the case
                        // should've already been catched by some of two previous branches
                        // print some note to investigate.
                        if (stack.getLast().isComment()) {
                            log.warn("We're in the String node which is also a comment."
                              + " Item on top: " + stack.getLast().getUserObject().getClass() + ":"
                              + getShortToString(stack.getLast().getUserObject()));
                            // why? I don't know...
                            stack.removeLast();
                            continue;
                        }
                        // if we're indeed at the root, we have to roll back to
                        // where we found the token and assume it's a comment
                        while (!backStack.isEmpty()) {
                            stack.add(backStack.removeLast());
                        }

                        node = new Node(t);
                        node.setComment(true);

                        stack.getLast().add(node);
                        added = true;
                    }
                }

                // the node is a valid token, might get some arguments
                if (!node.isComment()) {
                    // Next argumentLimit number of input tokens MIGHT be arguments. For that
                    // we need insideToken to be true. If it's false, it means we're
                    // outside the [] definition and so the token is new cont/tok/comment
                    argumentIndex = 0;
                    argumentLimit = currentToken.getArgumentSize();

                    // if the node can have arguments, we push it on the stack
                    if (argumentLimit > 0) {
                        stack.add(node);
                    }
                }
            }
            // empty input tokens can only be handled from now on, since they are not
            // valid tokens or containers
            else if (containerOnTop(stack)) {
                topContainer = (Container) stack.getLast().getUserObject();
                // if the argument index for this container doesn't exceed the limit
                // and we're inside the body ([]), we treat the next input token as an
                // argument
                if (argumentIndex < argumentLimit && insideToken) {
                    a = topContainer.getArgument(argumentIndex);
                    node = new Node(a);
                    stack.getLast().add(node);
                    argumentIndex++;
                    // if the argument is ID, add the value to the id list
                    addItemToIdList(a, t, topContainer.getName(), ids);
                }
                // This is not a token or container, since it falled past the
                // branches. This is not an argument either. So it's a comment...
                // We're at the container level, so just add it as a child
                else {
                    node = new Node(t);
                    node.setComment(true);
                    stack.getLast().add(node);
                }
            }
            else if (tokenOnTop(stack)) {
                topToken = (Token) stack.getLast().getUserObject();
                // if the argument index for this token doesn't exceed the limit
                // and we're inside the body ([]), we treat the next input token as an
                // argument
                if (argumentIndex < argumentLimit && insideToken) {
                    a = topToken.getArgument(argumentIndex);
                    node = new Node(a);
                    stack.getLast().add(node);
                    argumentIndex++;
                    // if the argument is ID, add the value to the id list
                    addItemToIdList(a, t, topToken.getName(), ids);
                }
                else {
                    // If we're out of the argument limit or the token body has ended
                    // we pop it out the stack. This should get container on the top!
                    stack.removeLast();
                    // we add the comment to the container
                    node = new Node(t);
                    node.setComment(true);
                    stack.getLast().add(node);
                }
            }
            else if (stack.getLast().getUserObject() == ROOT_STRING) {
                node = new Node(t);
                node.setComment(true);
                stack.getLast().add(node);
            }

            if (node != null) {
                node.setText(t);
                node.setStartOffset(ti.getTokenStart());
                node.setEndOffset(ti.getTokenEnd() - 1);
                node.setLength(t.length());
                if (p.matcher(t).matches()) {
                    node.setWhitespace(true);
                }
                model.addRange(new Range<Node>(ti.getTokenStart(), ti.getTokenEnd() - 1, node));
            }
            node = null;

        }

        return model;
    }

    /**
     * Adds an item to the list or category of the ID of specified argument.
     * 
     * @param a Argument with ID attribute
     * @param item The item to add
     * @param tokenName Name of the parent token/container of this argument
     */
    private static void addItemToIdList(Argument a, String item, String tokenName,
                                        Map<String, Id> ids) {
        if (a.getId() != null) {
            Id id = ids.get(a.getId());
            if (id.isFlat()) {
                id.addItem(item);
            }
            else {
                Map<String, String> fromToMap = id.getFromToMap();
                if (fromToMap != null) {
                    String catgory = fromToMap.get(tokenName);
                    id.addItemToCategory(catgory, item);
                }
            }
        }
    }

    /**
     * @param stack Stack
     * @return Return true if the user object on the top of the stack is instance of Token
     */
    private static boolean tokenOnTop(Deque<Node> stack) {
        return stack.getLast().getUserObject() instanceof Token;
    }

    /**
     * @param stack Stack
     * @return Return true if the user object on the top of the stack is instance of Container
     * (Container is also instance of Token, beware)
     */
    private static boolean containerOnTop(Deque<Node> stack) {
        return stack.getLast().getUserObject() instanceof Container;
    }

    /**
     * @param o Object
     * @return Return short description of object or null if the object class is
     * unknown
     */
    private static String getShortToString(Object o) {
        if (o instanceof Container) {
            return ((Container) o).getName();
        }
        else if (o instanceof Token) {
            return ((Token) o).getName();
        }
        else if (o instanceof Argument) {
            return ((Argument) o).getType();
        }
        else if (o instanceof String) {
            return (String) o;
        }
        return null;
    }
}
