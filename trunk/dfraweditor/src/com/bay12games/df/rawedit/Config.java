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

import com.bay12games.df.rawedit.xml.KeyWordType;
import com.bay12games.df.rawedit.xml.KeyWordTypeLoader;
import com.bay12games.df.rawedit.xml.RawsLoader;
import com.bay12games.df.rawedit.xml.entities.Container;
import com.bay12games.df.rawedit.xml.entities.ElementContainer;
import com.bay12games.df.rawedit.xml.entities.Id;
import com.bay12games.df.rawedit.xml.entities.Token;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author Matus Goljer
 * @version 1.0
 */
public class Config {

    private Map<String, KeyWordType> keywordTypes;
    private Map<String, Container> containers;
    private Map<String, Token> tokens;
    private Map<String, Id> ids;
    private Properties properties;

    public Config() {
        properties = new Properties();
        Reader is = null;
        try {
            is = new FileReader("config.properties");
            properties.load(is);

            KeyWordTypeLoader loader = new KeyWordTypeLoader();
            keywordTypes = loader.load(properties.getProperty("keyword.TypesSource"));
//            KeyWordLoader kwloader = new KeyWordLoader();
//            keywords = kwloader.load(properties.getProperty("keyword.Source"), keywordTypes);

            RawsLoader rloader = new RawsLoader();
            ElementContainer ec = rloader.parse(properties.getProperty("raws.Source"));
            if (ec != null) {
                containers = ec.getContainers();
                tokens = ec.getTokens();
            }
//            for (int i = 0; i < 100; i++) {
//                keywords.put(Integer.toString(i), null);
//            }
        } catch (IOException ex) {
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    public Map<String, KeyWordType> getKeywordTypes() {
        return keywordTypes;
    }

//    public Map<String, KeyWord> getKeywords() {
//        return keywords;
//    }

    public Map<String, Container> getContainers() {
        return containers;
    }

    public Map<String, Token> getTokens() {
        return tokens;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
