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

import com.bay12games.df.rawedit.model.Model;
import javax.swing.text.DefaultStyledDocument;

/**
 *
 * @author Matus Goljer
 * @version 1.0
 */
public interface TextInsertionProvider {

    /**
     * Insert the text from autocomplete dialog to the specified document complying the
     * provided model
     *
     * @param d Document where the insertion is made
     * @param model Model of the document
     * @param index Index where we insert new text
     * @param insert The text to be inserted
     */
    public void complete(final DefaultStyledDocument d, final Model model, final int index, String insert);
}
