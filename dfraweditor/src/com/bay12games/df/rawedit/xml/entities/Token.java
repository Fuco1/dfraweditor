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

import java.util.ArrayList;
import java.util.List;

/**
 * Token represents the directive in the RAW language that can't have any child
 * directives. Each token can have an ordered list of arguments. It can also
 * contain no arguments, in which case it is referred to as "flag".
 *
 * @author Matus Goljer
 * @author Bruno Zimmermann
 * @version 1.0
 */
public class Token implements Comparable<Token>{

    private ArrayList<Argument> arguments;
    private String name;
    private String description;
    private boolean required = false;

    public Token(String name) {
        this.name = name;
    }

    public Token(String name, boolean required) {
        this.name = name;
        this.required = required;
    }

    public boolean isFlag() {
        return arguments == null || arguments.isEmpty();
    }

    public void addArgument(Argument a) {
        if (arguments == null) {
            arguments = new ArrayList<Argument>();
        }
        arguments.add(a);
    }

    public Argument getArgument(int i) {
        if (arguments != null) {
            return arguments.get(i);
        }
        else {
            return null;
        }
    }

    public ArrayList<Argument> getArguments() {
        if (arguments == null) {
            arguments = new ArrayList<Argument>();
        }
        return arguments;
    }

    public int getArgumentSize() {
        return !isFlag() ? arguments.size() : 0;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Token:\nName: ").append(getName());
        sb.append(getDescription() != null ? "\nDescription: " + getDescription() : "");
        if (!isFlag()) {
            for (Argument a : arguments) {
                sb.append('\n').append(a.toString());
            }
        }
        else {
            sb.append("\nFlag");
        }
        return sb.toString();
    }

	@Override
	public int compareTo(Token o)
	{
		return this.getName().compareTo(o.getName());
	}
}
