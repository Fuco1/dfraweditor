package com.bay12games.df.dwarfstruct.gui.element;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
	private JPanel content,subElementPanel;
	
	private JLabel headLable;
	
	public DwarfStructElementPanel(DwarfStructElement element, DwarfStructSelectionTable selectTable, int width)
	{
		super();
		this.setAlignmentX(Component.LEFT_ALIGNMENT);
		setLayout(new BorderLayout());
		init(element, selectTable, width);
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
	private void init(DwarfStructElement element, DwarfStructSelectionTable selectTable, int width)
	{
		subElements = new LinkedList<DwarfStructElementPanel>();
		possibleElements = element.getChildren();
		arguments=new LinkedList<InputArgument>();
		this.element=element;
		this.selectTable=selectTable;
		headLable = new JLabel(element.getToken().getName());
		headLable.setPreferredSize(new Dimension(width,20));
		headLable.setMaximumSize(new Dimension(width,20));
		headLable.setAlignmentX(Component.LEFT_ALIGNMENT);
		content = new JPanel();
		content.setLayout(new BoxLayout(content,BoxLayout.Y_AXIS));
		subElementPanel=new JPanel();
		subElementPanel.setLayout(new BoxLayout(subElementPanel,BoxLayout.Y_AXIS));
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.addMouseListener(new ComponentSelectionListener(this.selectTable,this));
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
			subPanel.build();
			subElementPanel.add(subPanel);
		}
		this.add(headLable,BorderLayout.PAGE_START);
		this.add(content,BorderLayout.CENTER);
		this.add(subElementPanel,BorderLayout.PAGE_END);
		this.revalidate();
	}


	@Override
	public void addElement(DwarfStructElement element)
	{
		DwarfStructElementPanel elementPanel =  new DwarfStructElementPanel(element,selectTable,this.getWidth()-30);
		InputArgumentFactory factory = new InputArgumentFactory();
		List<Argument> arguments = element.getToken().getArguments();
		for(int i = 0;i<arguments.size();i++){
		elementPanel.addInput(factory.createInputPanel(arguments.get(i)));
		}
		elementPanel.build();
		subElements.add(elementPanel);
		this.build();
	}


	@Override
	public List<DwarfStructElement> getPossibleElements()
	{
		return possibleElements;
	}
	
}
