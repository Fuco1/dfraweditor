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
import com.bay12games.df.rawedit.xml.entities.Id;
import com.bay12games.df.rawedit.xml.entities.Token;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * Parser for loading files with RAW definitions.
 *
 * Parser will create each container/token instance
 * exactly once. This instance is then stored in the global lookup table, one for
 * containers and one for tokens. The definitions can be split in multiple files.
 *
 * The placeholder instance is created on the first encounter in the XML. It is then
 * stored in the global lookup table without additional attributes, that might be
 * loaded from different file in the future.
 *
 * @author Matus Goljer
 * @version 1.0
 */
public class RawsLoader {

    private static Logger log = Logger.getLogger(RawsLoader.class);
    private Map<String, Container> containers;
    private Map<String, Token> tokens;
    private Map<String, Id> ids;

    public RawsLoader() {
    }

    public static void main(String[] args) {
        RawsLoader loader = new RawsLoader();
        loader.parse("raws.xml");
    }

    /**
     * Load and parse specified raws definition file.
     *
     * New instance of ElementContainer is returned. New instances of underlying
     * maps are created as well.
     *
     * @param file The file to load and parse
     * @return New instance of ElementContainer with maps of tokens and containers
     */
    public ElementContainer parse(String file) {
        return parse(file, null);
    }

    /**
     * Load and parse specified raws definition file. If elements is null, only
     * the content of the current file will be returned. If elements is not null
     * and contains not-null token and container maps, the content of current
     * file will be appended as if the definitions were in one file.
     *
     * New instance of ElementContainer is returned, but the underlying content
     * containers are the same instance as supplied one, if supplied. If not, 
     * new map instances are returned.
     *
     * @param file The file to load and parse
     * @param elements The tuple-container for token and container elements
     * @return New instance of ElementContainer with maps of tokens and containers
     */
    public ElementContainer parse(String file, ElementContainer elements) {
        Document d;
        try {
            SAXReader reader = new SAXReader();
            d = reader.read(file);
        } catch (DocumentException ex) {
            log.error("Unable to load file:" + file + '.', ex);
            return elements;
        }

        if (elements == null) {
            containers = new HashMap<String, Container>();
            tokens = new HashMap<String, Token>();
            ids = new HashMap<String, Id>();
        }
        else {
            if (elements.getContainers() != null) {
                containers = elements.getContainers();
            }
            else {
                containers = new HashMap<String, Container>();
            }

            if (elements.getTokens() != null) {
                tokens = elements.getTokens();
            }
            else {
                tokens = new HashMap<String, Token>();
            }

            if (elements.getIds() != null) {
                ids = elements.getIds();
            }
            else {
                ids = new HashMap<String, Id>();
            }
        }

        Element root = d.getRootElement();
        for (Element e : root.elements()) {
            if ("c".equals(e.getName())) {
                parseContainer(e);
            }
            else if ("t".equals(e.getName())) {
                parseToken(e);
            }
            else if ("id".equals(e.getName())) {
                parseId(e);
            }
        }

//        for (Container c : containers.values()) {
//            System.out.println(c + "\n");
//        }
//
//        for (Token t : tokens.values()) {
//            System.out.println(t + "\n");
//        }

        return new ElementContainer(containers, tokens, ids);
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
                Argument a = parseArgument(ce, name);
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

    private Argument parseArgument(Element e, String parentName) {
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

            if (id != null) {
                // [NOTE] inline "default" id can only be a flat list.
                if (!ids.containsKey(id)) {
                    Id idObject = new Id(id);
                    ids.put(id, idObject);
                    // we might load a description later...
                }
            }

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
                Argument a = parseArgument(ce, name);
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

    // <id name="ITEM_SUB_ID" from="ITEM_ARMOR" to="ARMOR" id="ITEM_ID"/>
    public Id parseId(Element e) {
        String name = e.attributeValue("name");

        if (name == null) {
            return null;
        }

        Id id;

        if (ids.containsKey(name)) {
            id = ids.get(name);
        }
        else {
            id = new Id(name);
            ids.put(name, id);
        }

        String description = e.elementText("d");
        String from = e.attributeValue("from");
        String to = e.attributeValue("to");
        String superidName = e.attributeValue("id");

        id.setDescription(description);


        if (from != null && to != null) {
            if (id.getFromToMap() != null && id.getFromToMap().containsKey(from)) {
                log.warn("From-to map on id " + id.getName() + " already contains key " + from);
            }
            id.addFromTo(from, to);
        }
        // else...
        // If from or to are null, the list is flat, so we don't have to set anything

        if (superidName != null) {
            Id superid;

            if (ids.containsKey(superidName)) {
                superid = ids.get(superidName);
            }
            else {
                superid = new Id(superidName);
                ids.put(superidName, superid);
            }

            // [NOTE] supercategory is always flat!!
            superid.addItem(to);
        }

        return id;
    }
}
