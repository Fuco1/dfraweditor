package com.bay12games.df.dwarfstruct.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import com.bay12games.df.dwarfstruct.gui.action.ComponentSelectionListener;
import com.bay12games.df.dwarfstruct.gui.action.SelectableDwarfStructComponent;
import com.bay12games.df.dwarfstruct.gui.argument.InputArgumentFactory;
import com.bay12games.df.dwarfstruct.gui.element.DwarfStructElementPanel;
import com.bay12games.df.dwarfstruct.model.DwarfStructElement;
import com.bay12games.df.dwarfstruct.model.fileoperation.RawFileSaver;
import com.bay12games.df.rawedit.xml.entities.Argument;

public class DwarfStructBasePanel extends JPanel implements SelectableDwarfStructComponent
{
	private static final Logger log = Logger.getLogger(DwarfStructBasePanel.class);
	/**
	 * The selectionTable that shows which tokens still can be added
	 */
	private DwarfStructSelectionTable selectTable;
	/**
	 * The element that is displayed by this workPanel
	 */
	private DwarfStructElement element;
	/**
	 * All subElements that were selected by the user. They are the actual raws that will then be generated
	 */
	private List<DwarfStructElementPanel> subElements;
	private List<DwarfStructElement> possibleElements;
	
	private File file;
	
	private JPanel contentPanel;
	private JScrollPane contentScrollPanel;
	
	private JLabel elementTitleLabel;
	
	public DwarfStructBasePanel(DwarfStructElement element, File file) throws FileNotFoundException, IOException
	{
		this.element = element;
		this.file = file;
		possibleElements = element.getChildren();
		subElements = new LinkedList<DwarfStructElementPanel>();
		init();
		build();
	}


	private void init() throws FileNotFoundException, IOException
	{
		this.setPreferredSize(new Dimension(800,600));
		this.setMinimumSize(new Dimension(800,600));
		this.setLayout(new BorderLayout());
		elementTitleLabel = new JLabel(element.getToken().getName());
		elementTitleLabel.setFont(new Font("titleFont",Font.BOLD,18));
		elementTitleLabel.setPreferredSize(new Dimension(this.getWidth(),24));
		selectTable = new DwarfStructSelectionTable(this);
		contentPanel=new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel,BoxLayout.Y_AXIS));
		
	}
	
	private void build()
	{
		
		add(elementTitleLabel,BorderLayout.PAGE_START);
		add(contentScrollPanel=new JScrollPane(contentPanel),BorderLayout.CENTER);
		JScrollPane selectionTablePane = new JScrollPane(selectTable);
		selectionTablePane.setPreferredSize(new Dimension(300,500));
		//contentPanel.setPreferredSize(new Dimension(500,500));
		add(selectionTablePane,BorderLayout.LINE_END);
		this.addMouseListener(new ComponentSelectionListener(this.selectTable,this));
		contentPanel.addMouseListener(new ComponentSelectionListener(this.selectTable,this));
		
	}

	
	

	@Override
	public DwarfStructElementPanel addElement(DwarfStructElement element)
	{
		DwarfStructElementPanel elementPanel =  new DwarfStructElementPanel(element,selectTable,this);
		InputArgumentFactory factory = new InputArgumentFactory();
		List<Argument> arguments = element.getToken().getArguments();
		for(int i = 0;i<arguments.size();i++){
		elementPanel.addInput(factory.createInputPanel(arguments.get(i)));
		}
		elementPanel.build();
		addElementToPanel(elementPanel);
		return elementPanel;
	}




	/**
	 * @param elementPanel
	 */
	private void addElementToPanel(DwarfStructElementPanel elementPanel)
	{
		contentPanel.add(elementPanel);
		subElements.add(elementPanel);
		contentScrollPanel.revalidate();
	}
	
	public void removeElement(DwarfStructElement element)
	{
		subElements.remove(element);
		contentScrollPanel.revalidate();
	}



	@Override
	public List<DwarfStructElement> getPossibleElements()
	{
		return possibleElements;
	}


	/**
	 * Well.. Saves it. What else to say?
	 * @throws IOException 
	 */
	public void save() throws IOException
	{
		RawFileSaver saver = new RawFileSaver(file, this);
		
	}


	/**
	 * @return the subElements
	 */
	public List<DwarfStructElementPanel> getSubElements()
	{
		return subElements;
	}


	/**
	 * @return the element
	 */
	public DwarfStructElement getElement()
	{
		return element;
	}


	/**
	 * @return the selectTable
	 */
	public DwarfStructSelectionTable getSelectTable()
	{
		return selectTable;
	}



	
	
}
