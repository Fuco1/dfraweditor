package com.bay12games.df.dwarfstruct.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.bay12games.df.common.model.Constants;
import com.bay12games.df.common.model.PropertiesLoader;
import com.bay12games.df.dwarfstruct.gui.action.SelectableDwarfStructComponent;
import com.bay12games.df.dwarfstruct.gui.element.DwarfStructElementPanel;
import com.bay12games.df.dwarfstruct.model.DwarfStructElement;
import com.bay12games.df.rawedit.xml.entities.Token;

/**
 * @author Bruno Zimmermann
 *
 */
public class DwarfStructSelectionTable extends JTable
{
	private PropertiesLoader		prop;
	private List<String> columnNames;
	private static final int COLUMN_NAMES_LENGTH = 2;
	private SelectableDwarfStructComponent currentSelected;
	
	
	/**
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public DwarfStructSelectionTable(SelectableDwarfStructComponent currentSelected
			) throws FileNotFoundException, IOException
	{
		super();
		prop = PropertiesLoader.getInstance();
		List<DwarfStructElement> possibleElements = currentSelected.getPossibleElements();
		columnNames=new ArrayList<String>();
		columnNames.add(prop.getProperty(Constants.GUI_SELECTION_TABLE_HEADER_ITEM));
		columnNames.add(prop.getProperty(Constants.GUI_SELECTION_TABLE_HEADER_DESCRIPTION));
		DefaultTableModel tableModel = new DefaultTableModel(createTableData(possibleElements),columnNames.toArray());
		this.currentSelected=currentSelected;
		setModel(tableModel);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setColumnSelectionAllowed(false);
		setRowSelectionAllowed(true);
		addDoubleClickListener(this);
		setColumnSelectionAllowed(false);
		setEnabled(true);
		cellSelectionEnabled=false;
	}

	/**
	 * Builds the Table using DwarfStructElements
	 * @param elements A List of DwarfstructElemetns
	 * @return An Array of String Arrays, as used for the DefaultTableModel
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
	
	/**
	 * Rebuilds the table using the available Tokens. (aka; Refreshing the GUI)
	 */
	private void rebuildTable()
	{
		((DefaultTableModel)getModel()).setDataVector(createTableData(getPossibleElements()), columnNames.toArray());
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
					System.out.println(selectedElement.getToken().getName());
					getCurrentSelected().addElement(selectedElement);
					//If it's a container then it can be used n-times (eg. castes)
					if(!selectedElement.hasChildren())
					{
						getPossibleElements().remove(getSelectedRow());
					}
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
		return getCurrentSelected().getPossibleElements();
	}

	/**
	 * @see javax.swing.JTable#isCellEditable(int, int)
	 * @return This always returns false!
	 */
	@Override
	public boolean isCellEditable(int row, int column)
	{
		return false;
	}

	/**
	 * @return the currentSelected
	 */
	public SelectableDwarfStructComponent getCurrentSelected()
	{
		return currentSelected;
	}

	/**
	 * @param currentSelected the currentSelected to set
	 */
	public void setCurrentSelected(SelectableDwarfStructComponent currentSelected)
	{
		this.currentSelected = currentSelected;
		rebuildTable();
	}
	
	
}
