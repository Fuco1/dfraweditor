package com.bay12games.df.dwarfstruct.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.bay12games.df.dwarfstruct.model.DwarfStructElement;
import com.bay12games.df.rawedit.xml.entities.Token;

/**
 * @author Bruno Zimmermann
 *
 */
public class DwarfStructSelectionTable extends JTable
{
	private static final String[][] columnNames = {{"Token"},{"Description"}};
	private static final int COLUMN_NAMES_LENGTH = 2;
	private List<DwarfStructElement> possibleElements;
	private DwarfStructWorkPanel owner;
	/**
	 * @param element
	 */
	public DwarfStructSelectionTable(DwarfStructElement element, DwarfStructWorkPanel owner)
	{
		super();
		possibleElements = element.getChildren();
		DefaultTableModel tableModel = new DefaultTableModel(createTableData(possibleElements),columnNames);
		this.owner=owner;
		setModel(tableModel);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setColumnSelectionAllowed(false);
		setRowSelectionAllowed(true);
		addDoubleClickListener(this);
	}

	/**
	 * @param elements
	 * @return
	 */
	private static String[][] createTableData(List<DwarfStructElement> elements)
	{
		int rows = elements.size();
		String[][] data = new String[rows][COLUMN_NAMES_LENGTH];
		for(int i = 0; i<rows;i++)
		{
			Token rowToken = elements.get(i).getToken();
			data[i][0] = rowToken.getName();
			data[i][1] = rowToken.getDescription();
		}
		return data;
	}
	
	private void rebuildTable()
	{
		((DefaultTableModel)getModel()).setDataVector(createTableData(getPossibleElements()), columnNames);
		updateUI();
		update(getGraphics());
		validate();
	}
	
	/**
	 * @param dwarfStructSelectionTable
	 */
	private void addDoubleClickListener(DwarfStructSelectionTable dwarfStructSelectionTable)
	{
		dwarfStructSelectionTable.addMouseListener(new MouseAdapter()
		{

			/* (non-Javadoc)
			 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
			 */
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(e.getClickCount()==2)
				{
					DwarfStructElement selectedElement = getPossibleElements().get(getSelectedRow());
					getPossibleElements().remove(getSelectedRow());
					rebuildTable();
				}
			}
			
		});
	}

	/**
	 * @return
	 */
	public List<DwarfStructElement> getPossibleElements()
	{
		return possibleElements;
	}

	
	
}
