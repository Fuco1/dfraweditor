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
import com.bay12games.df.rawedit.model.Model;
import com.bay12games.df.rawedit.model.Node;
import com.bay12games.df.rawedit.xml.entities.Argument;
import com.bay12games.df.rawedit.xml.entities.Container;
import com.bay12games.df.rawedit.xml.entities.Token;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;

/**
 *
 * @author Matus Goljer
 * @version 1.0
 */
public class DocumentLoader {

    public static DefaultStyledDocument load(String file) {
        DefaultStyledDocument d = new DefaultStyledDocument();
        LineNumberReader r = null;

        try {
            r = new LineNumberReader(new BufferedReader(new FileReader(new File(file))));
            char[] buf = new char[16384];
            int len = 0;
            while ((len = r.read(buf)) != -1) {
                d.insertString(d.getLength(), String.copyValueOf(buf, 0, len), null);
            }
        } catch (IOException ex) {
        } catch (BadLocationException ex) {
        } finally {
            if (r != null) {
                try {
                    r.close();
                } catch (IOException ex) {
                }
            }
        }

        SyntaxHighlighter.highlight(d, 0, d.getLength());
        return d;
    }

    public static Model buildModel(Document d) {
        Map<String, Container> containers = Main.getConfig().getContainers();
        Map<String, Token> tokens = Main.getConfig().getTokens();

        Deque<Node> queue = new LinkedList<Node>();
        Node root = new Node("roooooooooooooooooooooooooooooooooooooooooooot");
        Model model = new Model(root);
        model.setRoot(root);
        queue.add(root);

        String text = "";
        try {
            text = d.getText(0, d.getLength());
        } catch (BadLocationException ex) {
        }

        TokenIterator ti = new TokenIterator(text);
        Node node;
        int argumentIndex = 0;
        int argumentLimit = -1;
        Container topContainer;
        Container currentContainer;
        Token topToken;
        Token currentToken;
        Argument a;

        tokenstream:
        for (String token : ti) {
            if (token.isEmpty() || token.matches("\\s+")) {
                continue;
            }

            if (containers.containsKey(token)) {
                currentContainer = containers.get(token);
                node = new Node(currentContainer);
                boolean added = false;

                while (!added) {
                    if (containerOnTop(queue)) {
                        topContainer = (Container) queue.getLast().getUserObject();
                        if (topContainer.getContainer(token) != null) {
                            queue.getLast().add(node);
                            queue.add(node);
                            added = true;
                        }
                        else {
                            queue.removeLast();
                        }
                    }
                    else if (tokenOnTop(queue)) {
                        topToken = (Token) queue.getLast().getUserObject();
                        queue.removeLast();
                    }
                    else if (queue.getLast().getUserObject() instanceof String) {
                        queue.getLast().add(node);
                        queue.add(node);
                        added = true;
                    }
                }
                argumentIndex = 0;
                argumentLimit = currentContainer.getArgumentSize();
            }
            else if (tokens.containsKey(token)) {
                currentToken = tokens.get(token);
                node = new Node(currentToken);
                boolean added = false;

                while (!added) {
                    if (containerOnTop(queue)) {
                        topContainer = (Container) queue.getLast().getUserObject();
                        if (topContainer.getToken(token) != null) {
                            queue.getLast().add(node);
                            added = true;
                        }
                        else {
                            queue.removeLast();
                        }
                    }
                    else if (tokenOnTop(queue)) {
                        queue.removeLast();
                    }
                    else if (queue.getLast().getUserObject() instanceof String) {
                        break tokenstream;
                    }
                }
                argumentIndex = 0;
                argumentLimit = currentToken.getArgumentSize();
                if (argumentLimit > 0) {
                    queue.add(node);
                }
            }
            else if (containerOnTop(queue)) {
                topContainer = (Container) queue.getLast().getUserObject();
                if (argumentIndex < argumentLimit) {
                    a = topContainer.getArgument(argumentIndex);
                    node = new Node(a);
                    queue.getLast().add(node);
                }
            }
            else if (tokenOnTop(queue)) {
                topToken = (Token) queue.getLast().getUserObject();
                if (argumentIndex < argumentLimit) {
                    a = topToken.getArgument(argumentIndex);
                    node = new Node(a);
                    queue.getLast().add(node);
                }
            }
            System.out.println(indent(queue.size()) + token);
        }
        return model;
    }

    private static String indent(int level) {
        String re = "";
        for (int i = 0; i < level; i++) {
            re = re + "    ";
        }
        return re;
    }

    private static boolean tokenOnTop(Deque<Node> queue) {
        return queue.getLast().getUserObject() instanceof Token;
    }

    private static boolean containerOnTop(Deque<Node> queue) {
        return queue.getLast().getUserObject() instanceof Container;
    }

    private static String nodeToString(Node node) {
        if (node.getUserObject() instanceof String) {
            return (String) node.getUserObject();
        }
        else if (node.getUserObject() instanceof Argument) {
            Argument argument = (Argument) node.getUserObject();
            return argument.getType();
        }
        Token token = (Token) node.getUserObject();
        return token.getName();
    }
}
