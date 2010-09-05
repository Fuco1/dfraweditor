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
package com.bay12games.df.rawedit.adt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a prefix-tree, or trie.
 *
 * @link http://en.wikipedia.org/wiki/Trie
 * @author Matus Goljer
 * @version 1.0
 */
public class PrefixTree {

    private Map<Character, PrefixTree> children = new HashMap<Character, PrefixTree>();
    private boolean isWord = false;

    public boolean isWord() {
        return isWord;
    }

    /**
     * Add new entity to this prefix tree.
     *
     * @param entity New entity
     */
    public void add(String entity) throws IllegalArgumentException {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must be not null");
        }

        if (entity.isEmpty()) {
            isWord = true;
            return;
        }

        char index = entity.charAt(0);
        if (children.containsKey(index)) {
            children.get(index).add(entity.substring(1));
        }
        else {
            PrefixTree child = new PrefixTree();
            children.put(index, child);
            child.add(entity.substring(1));
        }
    }

    public void add(Collection<String> entities) {
        for (String s : entities) {
            add(s);
        }
    }

    /**
     * Remove entity from this prefix tree.
     *
     * @param entity Entity to be removed
     */
    public void remove(String entity) throws IllegalArgumentException {
        if (entity == null || entity.length() < 1) {
            throw new IllegalArgumentException("Entity must be not null and not empty (length < 1)");
        }

        char index = entity.charAt(0);
        if (entity.length() == 1) {
            if (children.containsKey(index)) {
                children.remove(index);
            }
        }
        else {
            if (children.containsKey(index)) {
                children.get(index).remove(entity.substring(1));
            }
        }
    }

    /**
     * Get sub-tree with the specified entity as a root. This will not copy the
     * subtree into the new instance, so all changes are reflected to the original
     * tree as well.
     *
     * @param entity The root of the new sub-tree
     * @return The sub-tree with the specified entity as a root
     */
    public PrefixTree getSubTree(String entity) {
        if (entity == null) {
            return null;
        }

        if (entity.length() == 0) {
            return this;
        }

        char index = entity.charAt(0);
        if (children.containsKey(index)) {
            return children.get(index).getSubTree(entity.substring(1));
        }
        else {
            return null;
        }
    }

    public boolean contains(String entity) {
        if (entity == null || entity.length() < 1) {
            throw new IllegalArgumentException("Entity must be not null and not empty (length < 1)");
        }

        return (getSubTree(entity) != null);
    }

    /**
     * Helper method to get the items in the tree.
     *
     * @param tree Instance of the tree to be examined
     * @param list Accumulator of the words in the tree
     * @param sb Accumulator for the strings
     * @return The list of strings in this prefix tree, ordered by default string
     * ordering
     */
    private static List<String> toList(PrefixTree tree, List<String> list, StringBuilder sb) {
        Character[] keys = new Character[0];

        if (tree.isWord()) {
            list.add(sb.toString());
        }

        if (!tree.children.isEmpty()) {
            keys = tree.children.keySet().toArray(keys);
            Arrays.sort(keys);

            for (Character s : keys) {
                toList(tree.children.get(s), list, sb.append(s));
                if (sb.length() > 0) {
                    sb.delete(sb.length() - 1, sb.length());
                }
            }
        }
//        else {
//            if (sb.length() > 0) {
//                sb.delete(sb.length() - 1, sb.length());
//            }
//        }
        return list;
    }

    public List<String> getItems() {
        return toList(this, new ArrayList<String>(), new StringBuilder());

    }

    @Override
    public String toString() {
        return getItems().toString();
    }
}
