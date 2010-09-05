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

import java.awt.Color;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class responsbile for loading the color and font definitions of various keyword
 * types.
 *
 * @author Matus Goljer
 * @version 1.0
 */
//[TODO]: should be refactored to use dom4j parser
public final class KeyWordTypeLoader {

    private static final Logger log = Logger.getLogger(KeyWordTypeLoader.class);

    private KeyWordTypeLoader() {
    }

    /**
     * Load the color and font definitions for keyword types (token, container, flag,
     * number...).
     *
     * @param source Path to the source
     * @return Map of type names to the corresponding {@code KeyWordType} instances
     */
    public static Map<String, KeyWordType> load(String source) {
        if (source == null) {
            log.error("An error occured while loading \"KeyWordType\" xml configuration: The source String is null");
            return null;
        }
        return load(new File(source));
    }

    /**
     * Load the color and font definitions for keyword types (token, container, flag,
     * number...).
     *
     * @param source Instance of the file object specifying the source
     * @return Map of type names to the corresponding {@code KeyWordType} instances
     */
    public static Map<String, KeyWordType> load(File source) {
        Document document;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(source);
        } catch (Exception ex) {
            log.error("An error occured while loading \"KeyWordType\" xml configuration", ex);
            return null;
        }

        Map<String, KeyWordType> re = new HashMap<String, KeyWordType>();

        /*
        <id>OBJECT</id>
        <color>#ff0000</color>
        <bcolor>#ff0000</bcolor>
        <bold/>
        <italic/>
         */

        NodeList types = document.getElementsByTagName("type");
        for (int i = 0; i < types.getLength(); i++) {
            Node typeNode = types.item(i);

            String id = null;
            MutableAttributeSet as = new SimpleAttributeSet();

            NodeList typeNodes = typeNode.getChildNodes();
            for (int j = 0; j < typeNodes.getLength(); j++) {
                Node typeNodeParameter = typeNodes.item(j);
                String nodeName = typeNodeParameter.getNodeName();
                if ("id".equals(nodeName)) {
                    id = typeNodeParameter.getTextContent();
                }
                if ("color".equals(nodeName)) {
                    String text = typeNodeParameter.getTextContent();
                    Integer color = Integer.parseInt(text.substring(1), 16);
                    Color c = new Color(color);
                    StyleConstants.setForeground(as, c);
                }
                if ("bcolor".equals(nodeName)) {
                    String text = typeNodeParameter.getTextContent();
                    Integer color = Integer.parseInt(text.substring(1), 16);
                    Color c = new Color(color);
                    StyleConstants.setBackground(as, c);
                }
                if ("bold".equals(nodeName)) {
                    StyleConstants.setBold(as, true);
                }
                if ("italic".equals(nodeName)) {
                    StyleConstants.setItalic(as, true);
                }
            }

            if (id != null) {
                re.put(id, new KeyWordType(id, as));
            }
            else {
                log.warn("Error loading type: No id found");
            }
        }

        return re;
    }
}
