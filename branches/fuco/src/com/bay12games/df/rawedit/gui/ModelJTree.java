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
package com.bay12games.df.rawedit.gui;

import com.bay12games.df.rawedit.Config;
import com.bay12games.df.rawedit.model.Model;
import com.bay12games.df.rawedit.model.Node;
import com.bay12games.df.rawedit.xml.entities.Argument;
import com.bay12games.df.rawedit.xml.entities.Id;
import com.bay12games.df.rawedit.xml.entities.Token;
import java.awt.event.MouseEvent;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 *
 * @author Matus Goljer
 * @version 1.0
 */
public class ModelJTree extends JTree {

    public ModelJTree(Model model) {
        super(model);
    }

    @Override
    public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Node node = (Node) value;
        String noderange = " " + node.getStartOffset() + "-" + node.getEndOffset();
        if (node.isComment()) {
            return "Comment: " + (String) node.getUserObject() + noderange;
        }
        else if (node.getUserObject() instanceof String) {
            return (String) node.getUserObject() + noderange;
        }
        else if (node.getUserObject() instanceof Argument) {
            Argument argument = (Argument) node.getUserObject();
            return argument.getType() + noderange;
        }
        Token token = (Token) node.getUserObject();
        return token.getName() + noderange;
    }

    // Currently not used. Legacy code
    @Override
    public String getToolTipText(MouseEvent event) {
        if (getRowForLocation(event.getX(), event.getY()) == -1) {
            return "TEST -1";
        }
        TreePath curPath = getPathForLocation(event.getX(), event.getY());
        Node node = (Node) curPath.getLastPathComponent();
        if (node.getUserObject() instanceof Argument) {
            Argument a = (Argument) node.getUserObject();
            if (a.getRef() != null) {
                Id id = Config.getInstance().getIds().get(a.getRef());
                StringBuilder sb = new StringBuilder();
                sb.append("<html>Possible values:<ul>");
                for (String s : id.getItems()) {
                    sb.append("<li>").append(s).append("</li>");
                }
                sb.append("</ul></html>");
                return sb.toString();
            }
        }
        return "NOTHING";
    }
}
