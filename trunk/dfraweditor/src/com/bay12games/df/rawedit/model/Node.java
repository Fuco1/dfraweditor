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
package com.bay12games.df.rawedit.model;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * A node in the model tree. This class should be used <strong>AS IF</strong> it was
 * immutable. It is currently not because of implementation details.
 *
 * @author Matus Goljer
 * @version 1.0
 */
// [TODO]: It is possible to refactor this class to be immutable
public class Node extends DefaultMutableTreeNode {

    private boolean comment = false;
    private boolean whitespace = false;
    private int startOffset;
    private int endOffset;
    private int length;
    private String text;

    public Node() {
    }

    /**
     * Construct new Node with specified user object stored.
     * 
     * @param userObject An instance of an object to be stored. Null is permited.
     */
    public Node(Object userObject) {
        super(userObject);
    }

    /**
     * @return The text associated with this node
     */
    public String getText() {
        return text;
    }

    /**
     * Set the text of this node
     * @param text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return The parent node
     */
    @Override
    public Node getParent() {
        return (Node) super.getParent();
    }

    /**
     * @return True if this node is comment, false otherwise
     */
    public boolean isComment() {
        return comment;
    }

    /**
     * Set if this node is comment or not.
     *
     * @param comment True for comment, false otherwise (default)
     */
    public void setComment(boolean comment) {
        this.comment = comment;
    }

    /**
     * @return The end offset of the text this node is representing in the document
     * corresponding to the model containing this node
     */
    public int getEndOffset() {
        return endOffset;
    }

    /**
     * Set the end offset of the text this node is representing in the document
     * corresponding to the model containing this node
     *
     * @param endOffset offset
     */
    public void setEndOffset(int endOffset) {
        this.endOffset = endOffset;
    }

    /**
     * @return Length of the text this node is representing in the document
     * corresponding to the model containing this node
     */
    public int getLength() {
        return length;
    }

    /**
     * Set the length of the text this node is representing in the document
     * corresponding to the model containing this node
     *
     * @param length length
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * @return The start offset of the text this node is representing in the document
     * corresponding to the model containing this node
     */
    public int getStartOffset() {
        return startOffset;
    }

    /**
     * Set the start offset of the text this node is representing in the document
     * corresponding to the model containing this node
     *
     * @param startOffset offset
     */
    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    /**
     * @return True if this node is a whitespace comment. If this node is not
     * comment, behaviour is unspecified.
     */
    public boolean isWhitespace() {
        return whitespace;
    }

    /**
     * Set the whitespace property of the node. This should only be specified if the node
     * is comment, otherwise the value is unspecified.
     *
     * @param whitespace True if whitespace, false otherwise (default)
     */
    public void setWhitespace(boolean whitespace) {
        this.whitespace = whitespace;
    }
}
