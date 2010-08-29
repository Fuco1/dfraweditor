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
package com.bay12games.df.rawedit.xml;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;

/**
 *
 * @author Matus Goljer
 * @version 1.0
 */
public class KeyWordType {

    private String id;
    private MutableAttributeSet set;

    public KeyWordType(String id, MutableAttributeSet set) {
        this.id = id;
        this.set = set;
    }

    public String getId() {
        return id;
    }

    public MutableAttributeSet getSet() {
        return set;
    }

    @Override
    public String toString() {
        return getId() + '\n'
          + StyleConstants.isBold(getSet()) + '\n'
          + StyleConstants.isItalic(getSet()) + '\n'
          + StyleConstants.getForeground(getSet());
    }
}
