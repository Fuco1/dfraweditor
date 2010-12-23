package com.bay12games.df.dwarfstruct.gui;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JTabbedPane;

import com.bay12games.df.common.model.Constants;
import com.bay12games.df.dwarfstruct.model.DwarfStructElement;
import com.bay12games.df.dwarfstruct.model.fileoperation.RawFileLoader;
import com.bay12games.df.rawedit.Config;

/**
 * @author Bruno Zimmermann
 *
 */
public class DwarfStructTabPanel extends JTabbedPane
{
	private Map<String, DwarfStructElement> dwarfStructTrees;
	private List<DwarfStructTabPanel> realizedTokens;
	/**
	 * 
	 */
	public DwarfStructTabPanel()
	{
		dwarfStructTrees=Config.getInstance().getDwarfStructTrees();
		realizedTokens = new LinkedList<DwarfStructTabPanel>();
		this.setPreferredSize(new Dimension(800,600));
	}
	

	/**
	 * @param file
	 * @param dwarfStructTreeRoot
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public void createNewTab(File file,String dwarfStructTreeRoot) throws FileNotFoundException, IOException
	{		
		createNewTab(file,dwarfStructTrees.get(dwarfStructTreeRoot));
	}


	/**
	 * @param file
	 * @param dwarfStructElement
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public void createNewTab(File file,DwarfStructElement dwarfStructElement) throws FileNotFoundException, IOException
	{
		this.addTab(file.getName(),Constants.DWARF_COLORED_ICON(), new DwarfStructBasePanel(dwarfStructElement, file),file.getName());
	}


	public Map<String, DwarfStructElement> getDwarfStructTrees()
	{
		return dwarfStructTrees;
	}
	
	/**
	 * Saves the current selected Panel in it's file
	 * @throws IOException 
	 */
	public void saveSelected() throws IOException
	{
		((DwarfStructBasePanel)this.getSelectedComponent()).save();
	}
	/**
	 * Saves all open panels in their files
	 * @throws IOException 
	 */
	public void saveAll() throws IOException
	{
		for(int i = 0;i>this.getComponents().length;i++) 
		{
			((DwarfStructBasePanel)this.getComponent(i)).save();
			
		}
	}


	public void load() throws IOException
	{
		RawFileLoader loader = new RawFileLoader();
		if(loader.getFile()!=null)
		{
		this.addTab(loader.getFile().getName(),Constants.DWARF_COLORED_ICON(),loader.getBasePanel());
		}
	}
}
