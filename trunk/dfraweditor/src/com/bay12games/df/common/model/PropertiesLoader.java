package com.bay12games.df.common.model;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.bay12games.df.rawedit.xml.KeyWordTypeLoader;

/**
 * Removes the need to load the properties multiple times.
 * 
 * @author Bruno Zimmermann
 * 
 */
public class PropertiesLoader
{
	private static PropertiesLoader	loader;
	private Properties				properties;
	private Reader					reader;
	private Logger					log	= Logger.getLogger(PropertiesLoader.class);

	private PropertiesLoader() throws IllegalArgumentException, FileNotFoundException, IOException
	{
		properties = new Properties();
		reader = new FileReader("config.properties");
		try
		{
			properties.load(reader);
		} catch (IllegalArgumentException e)
		{
			throw e;
		} catch (IOException e)
		{
			try
			{
				if (reader != null)
				{
					reader.close();
				}
			} catch (IOException ex)
			{
				log.error("Unable to close Config file. Fatal error. Shutting down immediately!", e);
				System.exit(1);
			}
		}
	}

	public static PropertiesLoader getInstance() throws FileNotFoundException, IOException
	{
		if (loader == null)
		{
			loader = new PropertiesLoader();
		}
		return loader;
	}

	public String getProperty(String propertyKey)
	{
		return properties.getProperty(propertyKey);
	}
}
