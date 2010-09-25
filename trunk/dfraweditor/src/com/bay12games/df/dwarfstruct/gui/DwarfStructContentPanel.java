package com.bay12games.df.dwarfstruct.gui;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.bay12games.df.common.model.Constants;
import com.bay12games.df.dwarfstruct.model.DwarfStructElement;
import com.bay12games.df.rawedit.Config;

/**
 * @author Bruno Zimmermann
 *
 */
public class DwarfStructContentPanel extends JTabbedPane
{
	private Map<String, DwarfStructElement> dwarfStructTrees;
	private List<DwarfStructContentPanel> realizedTokens;
	/**
	 * 
	 */
	public DwarfStructContentPanel()
	{
		dwarfStructTrees=Config.getInstance().getDwarfStructTrees();
		realizedTokens = new LinkedList<DwarfStructContentPanel>();
	}
	

	/**
	 * @param file
	 * @param dwarfStructTreeRoot
	 */
	public void createNewTab(File file,String dwarfStructTreeRoot)
	{
		createNewTab(file,dwarfStructTrees.get(dwarfStructTreeRoot));
	}


	/**
	 * @param file
	 * @param dwarfStructElement
	 */
	public void createNewTab(File file,DwarfStructElement dwarfStructElement)
	{
		this.addTab(file.getName(),Constants.DWARF_ICON(), new DwarfStructWorkPanel(dwarfStructElement),file.getPath()+File.separator+file.getName());
	}


	public Map<String, DwarfStructElement> getDwarfStructTrees()
	{
		return dwarfStructTrees;
	}
}
