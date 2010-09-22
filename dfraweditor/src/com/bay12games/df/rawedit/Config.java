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
package com.bay12games.df.rawedit;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import com.bay12games.df.common.model.Constants;
import com.bay12games.df.common.model.PropertiesLoader;
import com.bay12games.df.dwarfstruct.model.DwarfStructElement;
import com.bay12games.df.dwarfstruct.model.DwarfStructElementParser;
import com.bay12games.df.rawedit.gui.DocumentChangesBuffer;
import com.bay12games.df.rawedit.model.Model;
import com.bay12games.df.rawedit.model.Node;
import com.bay12games.df.rawedit.xml.KeyWordType;
import com.bay12games.df.rawedit.xml.KeyWordTypeLoader;
import com.bay12games.df.rawedit.xml.RawsDescriptionLoader;
import com.bay12games.df.rawedit.xml.XMLFileSearcher;
import com.bay12games.df.rawedit.xml.entities.Argument;
import com.bay12games.df.rawedit.xml.entities.Container;
import com.bay12games.df.rawedit.xml.entities.ElementContainer;
import com.bay12games.df.rawedit.xml.entities.Id;
import com.bay12games.df.rawedit.xml.entities.Token;

/**
 * Singleton access point to program settings and data.
 *
 * @author Matus Goljer
 * @author Bruno Zimmermann
 * @version 1.0
 */
public class Config implements TokenDescriptionProvider {

    private static final Logger log = Logger.getLogger(Config.class);
    private static final Config instance = new Config();
    private Map<String, KeyWordType> keywordTypes;
    private ElementContainer elementContainer;
    private PropertiesLoader properties;
    private Model model;
    private JFrame mainFrame;
    private DocumentChangesBuffer changeBuffer = new DocumentChangesBuffer();
    

    private Config() {
        init();
    }

    private void init() {
        model = new Model(null);
 
        try {
        	properties = PropertiesLoader.getInstance();

            keywordTypes = KeyWordTypeLoader.load(properties.getProperty(Constants.KEYWORD_TYPES_SOURCE));

            this.elementContainer=readRawsSource();
            parseDwarfStructElements();
            
        } catch (IOException ex) {
            log.error("Unable to init Config. Application is shutting down.", ex);
            System.exit(1);
        }
    }



	private void parseDwarfStructElements()
	{
		DwarfStructElementParser parser = new DwarfStructElementParser(elementContainer);
		for(String test : parser.getTopElements().keySet())
		{
			log.debug("Whoop" + test);
		}
		
	}

	/**
	 * Reads all XML files defined under <i>raws.Source</i> in the properties.<br> 
	 * Use with caution. It takes a while to load the XML-definitions.<br>
	 * @return an new <code>ElementContainer</code> containing all tokens, containers and ids read from the xmls.
	 * @throws IOException if the raws.Source in the properties does not exist.
	 */
	private ElementContainer readRawsSource() throws IOException //TODO maybe change it to public when you want to have a way to reread the XMLs
	{
		ElementContainer elementContainer = null;
		RawsDescriptionLoader rloader = new RawsDescriptionLoader();
		// list of colon-delimited sources for raws
		String sourceProp = properties.getProperty("raws.Source");
		XMLFileSearcher searcher = new XMLFileSearcher(sourceProp);
		List<File> xmlFiles = searcher.getXML();
		for(File file:xmlFiles)
		{
			elementContainer = rloader.parse(file, elementContainer);
		}
		
//		String[] sources = {""};
//		if (sourceProp != null) {
//		    sources = sourceProp.split("\\:"); //TODO replace with a folder where all xmls are.
//		}
//
//		// load all files incrementally
//		for (String s : sources) {
//			elementContainer = rloader.parse(s, elementContainer);
//		}
		
		return elementContainer;
	}

    public static Config getInstance() {
        return instance;
    }

    /**
     * @return The unmodifiable view of the map containing all keyword types
     * and their visual attributes
     */
    public Map<String, KeyWordType> getKeywordTypes() {
        return Collections.unmodifiableMap(keywordTypes);
    }

    /**
     * @return The unmodifiable view of the map containing all available Containers
     */
    public Map<String, Container> getContainers() {
        return Collections.unmodifiableMap(elementContainer.getContainers());
    }

    /**
     * @return The unmodifiable view of the map containing all available Tokens
     */
    public Map<String, Token> getTokens() {
        return Collections.unmodifiableMap(elementContainer.getTokens());
    }

    /**
     * @return The unmodifiable view of the map containing all available Ids
     */
    public Map<String, Id> getIds() {
        return Collections.unmodifiableMap(elementContainer.getIds());
    }

    /**
     * Return a Container specified by its name.
     *
     * <p><strong>Note:</strong> If you plan on calling this method multiple times, consider
     * using {@link Config#getContainers()} to retrieve the whole map, as this method
     * performs a null-check each time.
     *
     * <p><strong>WARNING:</strong>Treat the returned object as if it was IMMUTABLE! It is not due
     * to implementation details. This will change in the future.
     * 
     * @param name Name of the Container
     * @return The immutable instance of the Container
     */
    public Container getContainer(String name) {
        if (elementContainer.getContainers() != null) {
            return elementContainer.getContainers().get(name);
        }
        else {
            return null;
        }
    }

    /**
     * Return a Token specified by its name.
     *
     * <p><strong>Note:</strong> If you plan on calling this method multiple times, consider
     * using {@link Config#getTokens()} to retrieve the whole map, as this method
     * performs a null-check each time.
     *
     * <p><strong>WARNING:</strong>Treat the returned object as if it was IMMUTABLE! It is not due
     * to implementation details. This will change in the future.
     *
     * @param name Name of the Token
     * @return The immutable instance of the Token
     */
    public Token getToken(String name) {
        if (elementContainer.getTokens() != null) {
        	return elementContainer.getTokens().get(name);
            
        }
        else {
            return null;
        }
    }

    /**
     * Return an Id specified by its name.
     *
     * <p><strong>Note:</strong> If you plan on calling this method multiple times, consider
     * using {@link Config#getIds()} to retrieve the whole map, as this method
     * performs a null-check each time.
     *
     * <p><strong>WARNING:</strong>Treat the returned object as if it was IMMUTABLE! It is not due
     * to implementation details. This will change in the future.
     *
     * @param name Name of the Id
     * @return The immutable instance of the Id
     */
    public Id getId(String name) {
        if (elementContainer.getIds() != null) {
            return elementContainer.getIds().get(name);
        }
        else {
            return null;
        }
    }

    /**
     * Get property from config
     *
     * @param key Property key
     * @return Property value
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * @return The most recently calculated model for active document
     */
    // [TODO] Multidoc support
    public Model getModel() {
        return model;
    }

    /**
     * Set the model of the active document's content
     * @param model New model instance
     */
    // [TODO] Multidoc support
    // Possibly not thread-safe
    public void setModel(Model model) {
        this.model = model;
    }

    /**
     * @return The most recently calculated change buffer for active document
     */
    // [TODO] Multidoc support
    public DocumentChangesBuffer getChangeBuffer() {
        return changeBuffer;
    }

    /**
     * Get the description for suggestion at index <i>index</i> and name <i>element</i>
     *
     * @param index Index in the document
     * @param element Element name to be checked
     * @return The description for the element
     */
    // [TODO] i18n
    @Override
    public String getDescriptionForElement(int index, String element) {
        Node node = model.getClosestNodeByStartOffset(index);
        if (node.isComment()) {
            Node parent = node.getParent();
            // first check Container since it's subclass of Token
            if (parent.getUserObject() instanceof Container) {
                Container container = (Container) parent.getUserObject();
                if (container.getContainers().containsKey(element)) {
                    return getNullDescription(container.getContainers().get(element).getDescription());
                }
                else if (container.getTokens().containsKey(element)) {
                    return getNullDescription(container.getTokens().get(element).getDescription());
                }
            }
            else if (parent.getUserObject() instanceof Token) {
                int indexOfArgument = parent.getIndex(node);
                Token t = (Token) parent.getUserObject();
                Argument a = t.getArgument(indexOfArgument);
                return getNullDescription(a.getDescription());
            }
        }
        else if (node.getUserObject() instanceof Argument) {
            Argument a = (Argument) node.getUserObject();
            return getNullDescription(a.getDescription());
        }
        else if (node.getUserObject() instanceof Container) {
            Node parent = node.getParent();
            Object userObject = parent.getUserObject();
            if (userObject instanceof Container) {
                Container parentContainer = (Container) userObject;
                if (parentContainer.getContainers().containsKey(element)) {
                    return getNullDescription(parentContainer.getContainers().get(element).getDescription());
                }
                else if (parentContainer.getTokens().containsKey(element)) {
                    return getNullDescription(parentContainer.getTokens().get(element).getDescription());
                }
            }
        }
        else if (node.getUserObject() instanceof Token) {
            Node parent = node.getParent();
            Object userObject = parent.getUserObject();
            if (userObject instanceof Container) {
                Container container = (Container) userObject;
                if (container.getTokens().containsKey(element)) {
                    return getNullDescription(container.getTokens().get(element).getDescription());
                }
            }
        }

        return "No description available";
    }

    /**
     * Check if the desc is null, if not, return desc, otherwise "No description available"
     * is returned
     *
     * @param desc
     * @return
     */
    // [TODO] i18n
    private String getNullDescription(String desc) {
        if (desc != null) {
            return desc;
        }
        else {
            return "No description available";
        }
    }

    public JFrame getMainFrame() {
        return mainFrame;
    }

    public void setMainFrame(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }
}
