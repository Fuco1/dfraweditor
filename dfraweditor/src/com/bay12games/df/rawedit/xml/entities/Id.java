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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

/**
 * <strong>TODO</strong>: Lists should be replaced with prefix trees.
 *
 * @author Matus Goljer
 * @version 1.0
 */
public class Id {

    private String parentName;
    private String name;
    private String description;
//    private String from;
//    private String to;
    /** Map of from-to pairs from <id> definitions. If this id is flat (no subs),
    this is null */
    private HashMap<String, String> fromToMap;
    /** If there are no categories, this only takes 4 bytes (reference). We
    have separate list for flat categories (no subs) to save time and complexity.
    The key is the "to" value from  the <id> definition. */
    private HashMap<String, HashSet<String>> categories;
    /** list of items for flat category (no subs) */
    private HashSet<String> items;

    public Id(String name) {
        this.name = name;
    }

    public HashMap<String, HashSet<String>> getCategories() {
        return categories;
    }

    /**
     * Get items from this id, if it's flat.
     *
     * @return The set of possible values for this id/ref
     */
    public HashSet<String> getItems() {
        return items;
    }

    public boolean hasNoItems() {
        return items == null || items.isEmpty();
    }

    public HashSet<String> getCategory(String name) {
        if (categories != null) {
            return categories.get(name);
        }
        return null;
    }

    public boolean hasNoCategories() {
        return categories == null || categories.isEmpty();
    }

    public void addItem(String item) {
        if (items == null) {
            items = new HashSet<String>();
        }
        items.add(item);
    }

    // category == to
    public void addItemToCategory(String category, String item) {
        if (categories == null) {
            categories = new HashMap<String, HashSet<String>>();
        }
        HashSet<String> cat = categories.get(category);
        if (cat == null) {
            cat = new HashSet<String>();
            categories.put(category, cat);
        }
        cat.add(item);
    }

    public void addFromTo(String from, String to) {
        if (fromToMap == null) {
            fromToMap = new HashMap<String, String>();
        }
        fromToMap.put(from, to);
    }

    public Map<String, String> getFromToMap() {
        return fromToMap;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFlat() {
        return (categories == null || categories.isEmpty())
          && (fromToMap == null || fromToMap.isEmpty());
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Id:\nName: ").append(name);
        boolean flat = isFlat();
        sb.append("\n  Is flat: ").append(flat);
        if (flat) {
            sb.append("\n  Items: ");
            if (items != null) {
                for (String item : items) {
                    sb.append(item).append(' ');
                }
            }
        }
        else {
            if (fromToMap != null) {
                sb.append("\n  From-to map: ");
                for (Entry<String, String> entry : fromToMap.entrySet()) {
                    sb.append(entry.getKey()).append("->").append(entry.getValue()).
                      append("\n               ");
                }
            }

            if (categories != null) {
                sb.append("\n  Sub-categories: ");
                for (Entry<String, HashSet<String>> entry : categories.entrySet()) {
                    //sb.append(entry.getKey()).append("->").append(entry.getValue());
                    sb.append("\n    Category: ").append(entry.getKey()).append("\n    ");
                    for (String item : entry.getValue()) {
                        sb.append(item).append(' ');
                    }
                }
            }
        }
        return sb.toString();
    }
}