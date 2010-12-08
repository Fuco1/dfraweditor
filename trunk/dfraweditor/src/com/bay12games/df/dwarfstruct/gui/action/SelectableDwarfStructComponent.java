package com.bay12games.df.dwarfstruct.gui.action;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.border.Border;

import com.bay12games.df.dwarfstruct.model.DwarfStructElement;

public interface SelectableDwarfStructComponent
{
	public void addElement(DwarfStructElement element);
	
	
	/**
	 * @see javax.swing.JPanel.setBorder(Border border)
	 */
	public void setBorder(Border border);
	
	public List<DwarfStructElement> getPossibleElements();
}
