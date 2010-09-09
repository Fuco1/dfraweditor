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

import com.bay12games.df.rawedit.adt.Range;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.tree.DefaultTreeModel;

/**
 * A class representing tree structure of a RAW file. Tokens are represented as
 * children of containers, and arguemnts as children of either tokens or containers.
 * Comments are represented by nodes with String user object and a flag indicating
 * a comment.
 *
 * <p><strong>NOTE</strong>: You have to add the ranges in the order specified by
 * default integer ascending ordering.
 *
 * @author Matus Goljer
 * @version 1.0
 * @see Node
 * @see Range
 */
public class Model extends DefaultTreeModel {

    /**
     * The list of ranges representing tags in the document modelled by this model.
     *
     * <p><strong>Note:</strong> ArrayList is used instead of List due to performance
     * issues (virtual methods)
     */
    private ArrayList<Range<Node>> ranges = new ArrayList<Range<Node>>();
    /**
     * The buffer of IDs parsed in the last DocumentLoader#buildModel invocation.
     * We use this buffer to compare the current invocation with the last and dispose
     * IDs that are no longer valid (ie, deleted or modified)
     */
    private HashSet<String> idBuffer = new HashSet<String>();

    public Model(Node root) {
        super(root);
    }

    /**
     * Add a range representing an integer interval [a,b] in the content that is occupied
     * by the node inside the range.
     * 
     * @param range range
     */
    public void addRange(Range<Node> range) {
        ranges.add(range);

    }

    /**
     * Add a range representing an integer interval [a,b] in the content that is occupied
     * by the node inside the range.
     * 
     * @param bottom Bottom of the interval occupied by this node
     * @param top Top of the interval occupied by this node
     * @param node node
     */
    public void addRange(int bottom, int top, Node node) {
        ranges.add(new Range<Node>(bottom, top, node));
    }

    /**
     * @param offset Offset of the node we want to retrieve
     * 
     * @return The node with smallest start offset that contains the offset 
     * (startOffset &lt; offset &lt; startOffset + length)
     */
    public Node getClosestNodeByStartOffset(int offset) {
        Range<Node> r = Range.binarySearch(ranges, offset);
        if (r != null) {
            return r.getUserData();
        }
        return null;
    }

    /**
     * <p><strong>Note:</strong> This method is implementation-bound. It might get
     * abstracted in the future to relieve user from the underlying collection details
     * 
     * @param offset Offset of the node which index we want to retrieve
     * @return The index of the range in the internal array. You can use this index
     * to access successors or predecessors.
     */
    public int getClosestNodeIndexByStartOffset(int offset) {
        return Range.getIndex(ranges, offset);
    }

    /**
     * @return The collection of ranges
     */
    public ArrayList<Range<Node>> getRanges() {
        return ranges;
    }

    /**
     * Clear the model.
     *
     * <p><strong>Note:</strong> This method is implementation-bound. It might get
     * abstracted in the future to relieve user from the underlying collection details.
     * Should be used with caution!
     */
    public void clear() {
        ranges.clear();
    }

    /**
     * Add id to id buffer
     * @param id Id
     */
    public void addId(String id) {
        idBuffer.add(id);
    }

    public HashSet<String> getIdBuffer() {
        return idBuffer;
    }

    public void setIdBuffer(HashSet<String> idBuffer) {
        this.idBuffer = idBuffer;
    }
}
