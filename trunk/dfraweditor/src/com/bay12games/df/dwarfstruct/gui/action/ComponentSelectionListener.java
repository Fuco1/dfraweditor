package com.bay12games.df.dwarfstruct.gui.action;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;

import com.bay12games.df.dwarfstruct.gui.DwarfStructSelectionTable;

public class ComponentSelectionListener implements MouseListener
{

	private SelectableDwarfStructComponent selectable;
	private DwarfStructSelectionTable selectionTable;
	
	/**
	 * @param selectionTable
	 */
	public ComponentSelectionListener(DwarfStructSelectionTable selectionTable,SelectableDwarfStructComponent selectablePanel)
	{
		this.selectionTable=selectionTable;
		this.selectable=selectablePanel;
		selectable.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}
	
	@Override
	public void mouseReleased(MouseEvent event)
	{}

	@Override
	public void mousePressed(MouseEvent event)
	{}

	@Override
	public void mouseExited(MouseEvent event)
	{}

	@Override
	public void mouseEntered(MouseEvent event)
	{}

	@Override
	public void mouseClicked(MouseEvent event)
	{
		selectionTable.getCurrentSelected().setBorder(BorderFactory.createLineBorder(Color.BLACK));
		selectionTable.setCurrentSelected(selectable);
		selectionTable.getCurrentSelected().setBorder(BorderFactory.createLineBorder(Color.RED));

	}
}
