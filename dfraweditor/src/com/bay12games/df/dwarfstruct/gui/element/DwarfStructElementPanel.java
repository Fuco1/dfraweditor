package com.bay12games.df.dwarfstruct.gui.element;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.bay12games.df.dwarfstruct.gui.DwarfStructSelectionTable;
import com.bay12games.df.dwarfstruct.gui.action.ComponentSelectionListener;
import com.bay12games.df.dwarfstruct.gui.action.SelectableDwarfStructComponent;
import com.bay12games.df.dwarfstruct.gui.argument.InputArgument;
import com.bay12games.df.dwarfstruct.gui.argument.InputArgumentFactory;
import com.bay12games.df.dwarfstruct.model.DwarfStructElement;
import com.bay12games.df.rawedit.xml.entities.Argument;

public class DwarfStructElementPanel extends JPanel implements SelectableDwarfStructComponent
{

	private DwarfStructElement element;
	private DwarfStructSelectionTable selectTable;
	private List<InputArgument> arguments;
	private List<DwarfStructElementPanel> subElements;
	private List<DwarfStructElement> possibleElements;
	private JPanel content,subElementPanel,headPanel;
	private JTextArea description;
	
	private JLabel headLable;
	
	public DwarfStructElementPanel(DwarfStructElement element, DwarfStructSelectionTable selectTable,SelectableDwarfStructComponent parent)
	{
		super();
		this.setAlignmentX(Component.LEFT_ALIGNMENT);
		setLayout(new BorderLayout());
		init(element, selectTable);
	}


//
//	public DwarfStructElementPanel(LayoutManager layoutManager,DwarfStructElement element,DwarfStructSelectionTable selectTable, int width)
//	{
//		super();
//		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
//		init(element, selectTable, width);
//	}

	/**
	 * @param element
	 * @param selectTable
	 * @param width
	 */
	private void init(DwarfStructElement element, DwarfStructSelectionTable selectTable)
	{
		subElements = new LinkedList<DwarfStructElementPanel>();
		possibleElements = element.getChildren();
		arguments=new LinkedList<InputArgument>();
		this.element=element;
		this.selectTable=selectTable;
		headPanel = new JPanel(new BorderLayout());
		headLable = new JLabel(element.getToken().getName());
		headLable.setAlignmentX(Component.LEFT_ALIGNMENT);
		description = new JTextArea(element.getToken().getDescription());
		description.setEditable(false);
		description.setBackground(new Color(244, 244, 244, 255));
		description.setLineWrap(true);
		description.setWrapStyleWord(true);
		content = new JPanel();
		content.setLayout(new BoxLayout(content,BoxLayout.Y_AXIS));
		subElementPanel=new JPanel();
		subElementPanel.setLayout(new BoxLayout(subElementPanel,BoxLayout.Y_AXIS));
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.addMouseListener(new ComponentSelectionListener(this.selectTable,this));
		headPanel.addMouseListener(new ComponentSelectionListener(this.selectTable,this));
		content.addMouseListener(new ComponentSelectionListener(this.selectTable,this));
		subElementPanel.addMouseListener(new ComponentSelectionListener(this.selectTable,this));
	}
	
	public void addInput(InputArgument inputPanel)
	{
		arguments.add(inputPanel);
	}
	
	public void build()
	{

		for(InputArgument arg : arguments)
		{
			content.add(arg);
		}
		for(DwarfStructElementPanel subPanel:subElements)
		{
			subElementPanel.add(subPanel);
		}
		headPanel.add(headLable,BorderLayout.PAGE_START);
		if(description.getText()!=null&&description.getText().length()>0)
		{
		headPanel.add(description,BorderLayout.PAGE_END);
		}
		this.add(headPanel,BorderLayout.PAGE_START);
		this.add(content,BorderLayout.CENTER);
		this.add(subElementPanel,BorderLayout.PAGE_END);
		
		this.revalidate();
	}





	@Override
	public List<DwarfStructElement> getPossibleElements()
	{
		return possibleElements;
	}


	/**
	 * @return the subElements
	 */
	public List<DwarfStructElementPanel> getSubElements()
	{
		return subElements;
	}


	/**
	 * @return the arguments
	 */
	public List<InputArgument> getArguments()
	{
		return arguments;
	}


	/**
	 * @return the element
	 */
	public DwarfStructElement getElement()
	{
		return element;
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
		subElements.add(elementPanel);
		this.build();
	}


	/**
	 * @param values
	 */
	public void setValues(List<String> values)
	{
		for(int i = 0;i<values.size();i++)
		{
			arguments.get(i).setValue(values.get(i));
		}
		this.build();
	}
	
}
