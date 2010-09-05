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

import com.Main;
import com.bay12games.df.rawedit.adt.PrefixTree;
import java.util.Set;

/**
 * This class represents a single argument of container or token. Each argument
 * has a type, currently one of the four following:
 * <ul>
 * <li>int - for numerical values</li>
 * <li>string - text strings</li>
 * <li>enum - a list of allowed string or integer values (treated as strings)</li>
 * <li>range - a min-max integer range</li>
 * </ul>
 *
 * For simplicity, each argument has all of the properties defined and defaulted
 * at null or 0.
 *
 * @author Matus Goljer
 * @version 1.0
 */
public class Argument {

    private String type;
    private String description;
    private int min = 0;
    private int max = 0;
    private Set<String> enumItems = null;
    private String id = null;
    private String ref = null;

    public Argument(String type) {
        this.type = type;
    }

    public Argument(String type, String id, String ref) {
        this.type = type;
        this.id = id;
        this.ref = ref;
    }

    public Argument(String type, int min, int max) {
        this.type = type;
        this.min = min;
        this.max = max;
    }

    public Argument(String type, Set<String> enumItems) {
        this.type = type;
        this.enumItems = enumItems;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getRef() {
        return ref;
    }

    public Set<String> getEnumItems() {
        return enumItems;
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    /**
     * @return PrefixTree representing all valid values that can appear inside this argument.
     * No suggestions are returned for type range and string (which is not ref). 0 is returned for
     * type int.
     */
    public PrefixTree getSuggestions() {
        PrefixTree suggestions = new PrefixTree();
        if (getRef() != null) {
            Id idOb = Main.getConfig().getIds().get(getRef());
            suggestions.add(idOb.getItems());
        }
        else if ("enum".equals(getType())) {
            suggestions.add(getEnumItems());
        }
        else if ("int".equals(getType())) {
            suggestions.add("0");
        }
        return suggestions;
    }

    public PrefixTree getSuggestions(Token owner) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public PrefixTree getSuggestions(Argument previousArgument) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Argument other = (Argument) obj;
        if ((this.type == null) ? (other.type != null) : !this.type.equals(other.type)) {
            return false;
        }
        if (this.min != other.min) {
            return false;
        }
        if (this.max != other.max) {
            return false;
        }
        if (this.enumItems != other.enumItems && (this.enumItems == null || !this.enumItems.equals(other.enumItems))) {
            return false;
        }
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        if ((this.ref == null) ? (other.ref != null) : !this.ref.equals(other.ref)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 79 * hash + this.min;
        hash = 79 * hash + this.max;
        hash = 79 * hash + (this.enumItems != null ? this.enumItems.hashCode() : 0);
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 79 * hash + (this.ref != null ? this.ref.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Argument:\n  Type: ").append(type);
        sb.append(description != null ? "\n  Description: " + description : "");
        if ("string".equals(type)) {
            sb.append(id != null ? "\n  Id: " + id : "");
            sb.append(ref != null ? "\n  Ref: " + ref : "");
        }
        else if ("range".equals(type)) {
            sb.append("\n  From ").append(min).append(" to ").append(max);
        }
        else if ("enum".equals(type)) {
            sb.append("\n  Allowed values: ");
            for (String s : enumItems) {
                sb.append(s).append(' ');
            }
        }

        return sb.toString();
    }
}
