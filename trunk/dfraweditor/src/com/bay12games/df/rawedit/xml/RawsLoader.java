/*
 *  Copyright (C) 2010 Matus Goljer
 *  Copyleft	  2010 Bruno Zimmermann
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.bay12games.df.rawedit.util.Constants;
import com.bay12games.df.rawedit.xml.entities.Argument;
import com.bay12games.df.rawedit.xml.entities.Container;
import com.bay12games.df.rawedit.xml.entities.ElementContainer;
import com.bay12games.df.rawedit.xml.entities.Token;

/**
 * Reads the xml definition files for raw. Also parses them. 
 * 
 * @author Matus Goljer
 * @author Bruno Zimmermann
 * @version 1.1
 */
public class RawsLoader {

    private Map<String, Container> containers;
    private Map<String, Token> tokens;

    public RawsLoader() {
        containers = new HashMap<String, Container>();
        tokens = new HashMap<String, Token>();
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
        Document doc;
        try {
            SAXReader reader = new SAXReader();
            doc = reader.read(file);
        } catch (DocumentException ex) {
            return null;
        }

        cleanMaps();

        Element root = doc.getRootElement();
        for (Element element : root.elements()) {
        	
        	if(StringUtils.containsAny(element.getName(), "ct"))
        	{
        		parseTokenNode(element);        		
        	}
            if ("c".equals(element.getName())) {
                parseContainer(element);
            }
            else if ("t".equals(element.getName())) {
                parseToken(element);
            }
        }

//        for (Container c : containers.values()) {
//            System.out.println(c + "\n");
//        }
//
//        for (Token t : tokens.values()) {
//            System.out.println(t + "\n");
//        }

        return new ElementContainer(containers, tokens);
    }

	/**
	 * Prepares containers and tokens
	 */
	private void cleanMaps()
	{
            containers.clear();
            tokens.clear();
	}

    private Token parseTokenNode(Element element)
    {
    	Token token = null;
    	String name = element.attributeValue(Constants.NODE_ATTRIBUTE_NAME);
    	return token;
    }
    
    /**
     * @param e
     * @return
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
    
    /**
     * @param e
     * @return
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
    
    

    /**
     * @param e
     * @return
     */
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


}
