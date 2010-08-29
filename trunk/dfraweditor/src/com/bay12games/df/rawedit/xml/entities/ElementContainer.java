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

import java.util.Map;

/**
 *
 * @author Matus Goljer
 * @version 1.0
 */
public class ElementContainer {

    private Map<String, Container> containers;
    private Map<String, Token> tokens;

    public ElementContainer(Map<String, Container> containers, Map<String, Token> tokens) {
        this.containers = containers;
        this.tokens = tokens;
    }

    public Map<String, Container> getContainers() {
        return containers;
    }

    public Map<String, Token> getTokens() {
        return tokens;
    }
}
