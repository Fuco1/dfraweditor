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

import com.bay12games.df.rawedit.xml.entities.Argument;
import com.bay12games.df.rawedit.xml.entities.Container;
import com.bay12games.df.rawedit.xml.entities.ElementContainer;
import com.bay12games.df.rawedit.xml.entities.Token;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 *
 * @author Matus Goljer
 * @version 1.0
 */
public class RawsLoader {

    private Map<String, Container> containers;
    private Map<String, Token> tokens;

    public RawsLoader() {
    }

    public static void main(String[] args) {
        RawsLoader loader = new RawsLoader();
        loader.parse("raws.xml");
    }
    
    public ElementContainer parse(String file) {
        Document d;
        try {
            SAXReader reader = new SAXReader();
            d = reader.read(file);
        } catch (DocumentException ex) {
            System.err.println("File not found");
            return null;
        }

        if (containers != null) {
            containers.clear();
        }

        if (tokens != null) {
            tokens.clear();
        }

        containers = new HashMap<String, Container>();
        tokens = new HashMap<String, Token>();

        Element root = d.getRootElement();
        for (Element e : root.elements()) {
            if ("c".equals(e.getName())) {
                parseContainer(e);
            }
            else if ("t".equals(e.getName())) {
                parseToken(e);
            }
        }

        for (Container c : containers.values()) {
            System.out.println(c + "\n");
        }

        for (Token t : tokens.values()) {
            System.out.println(t + "\n");
        }

        return new ElementContainer(containers, tokens);
    }

    private Token parseToken(Element e) {
        String name = e.attributeValue("name");

        if (name == null) {
            return null;
        }

        Token token;

        if (tokens.containsKey(name)) {
            token = tokens.get(name);
        }
        else {
            token = new Token(name);
            tokens.put(name, token);
        }

        boolean clean = false;

        for (Element ce : e.elements()) {
            // add the attribute
            if ("a".equals(ce.getName())) {
                Argument a = parseArgument(ce);
                if (a != null) {
                    if (!clean) {
                        token.getArguments().clear();
                        clean = true;
                    }
                    token.addArgument(a);
                }
            }
            else if ("d".equals(ce.getName())) {
                token.setDescription(ce.getText());
            }
        }

        return token;
    }

    private Argument parseArgument(Element e) {
        String type = e.attributeValue("type");
        if (type == null) {
            return null;
        }

        boolean required = Boolean.parseBoolean(e.attributeValue("required"));
        Argument argument = null;

        if ("enum".equals(type)) {
            Set<String> items = new HashSet<String>();
            for (Element ee : e.elements()) {
                if ("e".equals(ee.getName())) {
                    items.add(ee.attributeValue("name"));
                }
            }
            argument = new Argument(type, items);
        }
        else if ("range".equals(type)) {
            int min = 0;
            int max = 0;
            try {
                min = Integer.parseInt(e.attributeValue("min"));
                max = Integer.parseInt(e.attributeValue("max"));
                argument = new Argument(type, min, max);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        else if ("int".equals(type)) {
            argument = new Argument(type);
        }
        else if ("string".equals(type)) {
            String id = e.attributeValue("id");
            String ref = e.attributeValue("ref");
            argument = new Argument(type, id, ref);
        }

        String desc = e.elementText("d");
        if (desc != null && argument != null) {
            argument.setDescription(desc);
        }

        return argument;
    }

    private Container parseContainer(Element e) {
        String name = e.attributeValue("name");

        if (name == null) {
            return null;
        }

        Container container;

        if (containers.containsKey(name)) {
            container = containers.get(name);
        }
        else {
            container = new Container(name);
            containers.put(name, container);
        }

        boolean clean = false;

        for (Element ce : e.elements()) {
            // add the attribute
            if ("a".equals(ce.getName())) {
                Argument a = parseArgument(ce);
                if (a != null) {
                    if (!clean) {
                        container.getArguments().clear();
                        clean = true;
                    }
                    container.addArgument(a);
                }
            }
            else if ("c".equals(ce.getName())) {
                Container cc = parseContainer(ce);
                if (cc != null) {
                    if (!container.getContainers().containsKey(cc.getName())) {
                        container.addContainer(cc);
                    }
                }
            }
            else if ("t".equals(ce.getName())) {
                Token ct = parseToken(ce);
                if (ct != null) {
                    if (!container.getTokens().containsKey(ct.getName())) {
                        container.addToken(ct);
                    }
                }
            }
            else if ("d".equals(ce.getName())) {
                container.setDescription(ce.getText());
            }
        }
        return container;
    }
}
