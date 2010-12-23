package com.bay12games.df.dwarfstruct.gui.action;

import java.io.IOException;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.border.Border;

import com.bay12games.df.dwarfstruct.gui.element.DwarfStructElementPanel;
import com.bay12games.df.dwarfstruct.model.DwarfStructElement;

public interface SelectableDwarfStructComponent
{
	public DwarfStructElementPanel addElement(DwarfStructElement element);
	
	
	/**
	 * @see javax.swing.JPanel.setBorder(Border border)
	 */
	public void setBorder(Border border);
	
	/**
	 * @return all elements that still can be added to the element
	 */
	public List<DwarfStructElement> getPossibleElements();
	
	/**
	 * @return the sub-elements actually held by the component
	 */
	public List<DwarfStructElementPanel> getSubElements();
	
	
}
