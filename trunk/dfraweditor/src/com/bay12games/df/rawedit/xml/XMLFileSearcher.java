package com.bay12games.df.rawedit.xml;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.bay12games.df.rawedit.util.Constants;

/**
 * @author Bruno Zimmermann
 * 
 */
public class XMLFileSearcher
{

	private String		sourceFolder;
	private List<File>	xmlFiles;

	/**
	 * @param sourceFolder
	 *            the folder (or the path) to where the xml files are located.
	 *            Giving null will result in an nullpointer when calling a
	 *            getXML method.
	 */
	public XMLFileSearcher(String sourceFolder)
	{
		this.sourceFolder = sourceFolder;
	}

	/**
	 * Tries first to get external XMLs. If that fails internal XMLs will be
	 * searched.
	 * 
	 * @return a list of all XML files in the sourceFolder
	 * @throws IOException
	 *             if the folder sourceFolder is referencing does not exist.
	 * @throws NullPointerException
	 *             if sourceFolder is null
	 */
	public List<File> getXML() throws IOException
	{
		try
		{
			return getExternalXML();
		} catch (IOException e)
		{
			return getInternalXML();
		}
	}

	/**
	 * Browses the sourceFolder and all it's sub-directories for XML files.<br>
	 * The sourceFolder will be searched within the jar.
	 * 
	 * @return a list of all XML files in the sourceFolder
	 * @throws IOException
	 *             if the folder sourceFolder is referencing does not exist.
	 * @throws NullPointerException
	 *             if sourceFolder is null
	 */
	public List<File> getInternalXML() throws IOException
	{
		xmlFiles = new LinkedList<File>();
		File sourceFolderFile = new File(sourceFolder);
		ensureExistence(sourceFolderFile);
		processDir(sourceFolderFile);
		return xmlFiles;
	}

	/**
	 * Browses the sourceFolder and all it's sub-directories for XML files.<br>
	 * The sourceFolder will be searched in the same folder as the jar.
	 * 
	 * @return a list of all XML files in the sourceFolder
	 * @throws IOException
	 *             if the folder sourceFolder is referencing does not exist.
	 * @throws NullPointerException
	 *             if sourceFolder is null
	 */
	public List<File> getExternalXML() throws IOException
	{
		xmlFiles = new LinkedList<File>();
		File homeFolder = new File(new File("").getAbsolutePath()).getParentFile();
		File sourceFolderFile = new File(homeFolder.getAbsolutePath().concat(File.separator).concat(sourceFolder));
		ensureExistence(sourceFolderFile);
		processDir(sourceFolderFile);
		return xmlFiles;
	}

	/**
	 * @param sourceFolderFile
	 *            the file to be checked for existence
	 * @throws IOException
	 *             if the file does not exist!
	 */
	private void ensureExistence(File sourceFolderFile) throws IOException
	{
		if (sourceFolderFile == null || !sourceFolderFile.exists())
		{
			throw new IOException("sourceFolder does not exist!");
		}
	}

	/**
	 * @param file
	 */
	private void processFile(File file)
	{
		if (isXML(file))
		{
			xmlFiles.add(file);
		}
	}

	/**
	 * opens the directory and checks all items inside. <br>
	 * If the item is an xml it will be added.<br>
	 * If the item is an directory it will be again opened and processed.<br>
	 * If the item is neither a directory nor an xml it will be ignored.
	 * 
	 * @param dir
	 */
	private void processDir(File dir)
	{
		File[] items = dir.listFiles();
		if (items != null)
		{
			for (File item : items)
			{
				if (item.isDirectory())
				{
					processDir(item);
				}
				else
				{
					processFile(item);
				}
			}
		}
	}

	/**
	 * Checks if the file is an xml file (ends with .xml).<br>
	 * 
	 * @param file
	 *            the file to be checked
	 * @return items.xml -> <code>true</code><br>
	 *         items.armor.xml -> <code>true</code><br>
	 *         items.xls -> <code>false</code><br>
	 *         items.xml.backup -> <code>false</code>
	 */
	private boolean isXML(File file)
	{
		return StringUtils.endsWithIgnoreCase(file.getName(), Constants.XML_SUFFIX);
	}

	/**
	 * @return the sourceFolder for the searcher
	 */
	public String getSourceFolder()
	{
		return sourceFolder;
	}

	/**
	 * Changes the sourceFolder for the searcher.
	 * @param sourceFolder
	 */
	public void setSourceFolder(String sourceFolder)
	{
		this.sourceFolder = sourceFolder;
	}

}
