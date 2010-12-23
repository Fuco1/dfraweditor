/*
 *  Copyright (C) 2010 Matus Goljer & Bruno Zimmermann
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

import com.bay12games.df.common.model.Constants;
import com.bay12games.df.common.model.PropertiesLoader;
import com.bay12games.df.rawedit.xml.entities.Argument;
import com.bay12games.df.rawedit.xml.entities.Container;
import com.bay12games.df.rawedit.xml.entities.ElementContainer;
import com.bay12games.df.rawedit.xml.entities.Id;
import com.bay12games.df.rawedit.xml.entities.Token;
import java.io.File;
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
 * Parser for loading files with RAW definitions.<br>
 *<br>
 * Parser will create each container/token/id instance
 * exactly once. This instance is then stored in the global lookup table, one for
 * containers and one for tokens. The definitions can be split in multiple files.<br>
 *<br>
 * The placeholder instance is created on the first encounter in the XML. It is then
 * stored in the global lookup table without additional attributes, that might be
 * loaded from different file in the future.<br>
 *
 * @author Matus Goljer
 * @version 1.0
 */
public class RawsDescriptionLoader {

    private static final Logger log = Logger.getLogger(RawsDescriptionLoader.class);
    private Map<String, Container> containers;
    private Map<String, Token> tokens;
    private Map<String, Id> ids;

    public RawsDescriptionLoader() {
    }

    // [TEST]
    public static void main(String[] args) {
        RawsDescriptionLoader loader = new RawsDescriptionLoader();
        loader.parse("raws.xml");
    }

    /**
     * Load and parse specified raws definition file.<br><br>
     *
     * New instance of ElementContainer is returned. New instances of underlying
     * maps are created as well.<br>
     *
     * @param source Path to the file to load and parse
     * @return New instance of ElementContainer with maps of tokens and containers
     */
    public ElementContainer parse(String source) {
        return parse(new File(source), null);
    }

    /**
     * Load and parse specified raws definition file. If elements is null, only
     * the content of the current file will be returned. If elements is not null
     * and contains not-null token and container maps, the content of current
     * file will be appended as if the definitions were in one file.<br>
     *
     * The instances of the underlying containers are preserved.<br>
     *
     * <p><strong>Note:</strong> There should be only one global access to the token/container
     * definitions in the application.<br>
     *
     * @param source Path to the file to load and parse
     * @param elements The tuple-container for token, container and id elements
     * @return New instance of ElementContainer with maps of tokens and containers if the
     * supplied ElementContainer was null, or the same instance with updated content.
     * The supplied instance (possibly null) is returned on error
     */
    public ElementContainer parse(String source, ElementContainer elements) {
        return parse(new File(source), elements);
    }

    /**
     * Load and parse specified raws definition file.<br>
     *
     * New instance of ElementContainer is returned. New instances of underlying
     * maps are created as well.<br>
     *
     * @param file The file to load and parse
     * @return New instance of ElementContainer with maps of tokens and containers
     */
    public ElementContainer parse(File file) {
        return parse(file, null);
    }

    /**
     * Load and parse specified raws definition file. If elements is null, only
     * the content of the current file will be returned. If elements is not null
     * and contains not-null token and container maps, the content of current
     * file will be appended as if the definitions were in one file.<br><br>
     *
     * The instances of the underlying containers are preserved.<br><br>
     *
     * <p><strong>Note:</strong> There should be only one global access to the token/container
     * definitions in the application.<br>
     *
     * @param file The file to load and parse
     * @param elements The tuple-container for token, container and id elements
     * @return New instance of ElementContainer with maps of tokens and containers if the
     * supplied ElementContainer was null, or the same instance with updated content.
     * The supplied instance (possibly null) is returned on error
     */
    public ElementContainer parse(File file, ElementContainer elements) {
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
            elements = new ElementContainer(containers, tokens, ids);
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

        return elements;//new ElementContainer(containers, tokens, ids);
    }

    /**
     * Parse the XML element containing token definition.
     * 
     * @param e XML DOM element
     * @return Parsed token. If the token existed before, returns the same (possibly
     * altered) instance.
     */
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

    /**
     * Parse the XML element containing token definition.
     *
     * @param e XML DOM element
     * @param parentName Name of the parent element (currently unused - 20100905)
     * @return Parsed token. If the token existed before, returns the same (possibly
     * altered) instance.
     */
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
        argument.setLabel(e.attributeValue(Constants.XML_ARGUMENT_LABLE));
        
        
        return argument;
    }

    /**
     * Parse the XML element containing container definition.
     *
     * @param e XML DOM element
     * @return Parsed container. If the container existed before, returns the same (possibly
     * altered) instance.
     */
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
    /**
     * Parse the XML element containing id definition.
     *
     * @param e XML DOM element
     * @return Parsed id. If the id existed before, returns the same (possibly
     * altered) instance.
     */
    private Id parseId(Element e) {
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
            id.setParentName(superidName);
        }

        return id;
    }
}
