package com.bay12games.df.dwarfstruct.gui;

import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.JTable;

import com.bay12games.df.dwarfstruct.model.DwarfStructElement;
import com.bay12games.df.rawedit.Config;

public class DwarfStructWorkPanel extends JPanel
{
	/**
	 * The selectionTable that shows which tokens still can be added
	 */
	private DwarfStructSelectionTable selectionTable;
	/**
	 * All subElements that were selected by the user. They are the actual raws that will then be generated
	 */
	private LinkedList<DwarfStructWorkPanel> subElements;
	/**
	 * The element that is displayed by this workPanel
	 */
	private DwarfStructElement element;
	
	public DwarfStructWorkPanel(DwarfStructElement element)
	{
		this.element = element;
		subElements = new LinkedList<DwarfStructWorkPanel>();
		selectionTable = new DwarfStructSelectionTable(element,this);
	}
	
	
}
