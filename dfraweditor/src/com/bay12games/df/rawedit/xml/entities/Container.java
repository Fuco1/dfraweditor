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
package com.bay12games.df.rawedit.xml.entities;

import com.bay12games.df.rawedit.adt.PrefixTree;
import java.util.HashMap;
import java.util.Map;

/**
 * Container is a special kind of token that can hold other tokens and containers,
 * not just arguments.
 *
 * Each container holds the list of allowed sub-containers and tokens.
 *
 * TODO: The required arguments/containers/tokens are not handled in any way. The
 * XML format however can specify the required attribute.
 *
 * @author Matus Goljer
 * @version 1.0
 */
public class Container extends Token {

    private Map<String, Container> containers;
    private Map<String, Token> tokens;

    public Container(String name) {
        super(name);
    }

    public Container(String name, boolean required) {
        super(name, required);
    }

    public void addContainer(Container c) {
        if (containers == null) {
            containers = new HashMap<String, Container>();
        }
        containers.put(c.getName(), c);
    }

    public Container getContainer(String key) {
        if (containers != null) {
            return containers.get(key);
        }
        else {
            return null;
        }
    }

    public Map<String, Container> getContainers() {
        if (containers == null) {
            containers = new HashMap<String, Container>();
        }
        return containers;
    }

    public void addToken(Token t) {
        if (tokens == null) {
            tokens = new HashMap<String, Token>();
        }
        tokens.put(t.getName(), t);
    }

    public Token getToken(String key) {
        if (tokens != null) {
            return tokens.get(key);
        }
        else {
            return null;
        }
    }

    public Map<String, Token> getTokens() {
        if (tokens == null) {
            tokens = new HashMap<String, Token>();
        }
        return tokens;
    }

    /**
     * @return PrefixTree representing all valid tags that can appear inside this container
     */
    public PrefixTree getSuggestions() {
        PrefixTree tree = new PrefixTree();
        tree.add(getContainers().keySet());
        tree.add(getTokens().keySet());
        return tree;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Container:\nName: ").append(getName());
        sb.append(getDescription() != null ? "\nDescription: " + getDescription() : "");
        for (Argument a : getArguments()) {
            sb.append('\n').append(a.toString());
        }

        if (containers != null && containers.size() > 0) {
            sb.append("\nContainers: ");
            for (Token n : containers.values()) {
                sb.append(n.getName()).append(' ');
            }
        }

        if (tokens != null && tokens.size() > 0) {
            sb.append("\nTokens: ");
            for (Token n : tokens.values()) {
                sb.append(n.getName()).append(' ');
            }
        }
        return sb.toString();
    }
}
