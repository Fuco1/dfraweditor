package com.bay12games.df.dwarfstruct.gui.argument;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.bay12games.df.rawedit.xml.entities.Argument;

class InputArgumentText extends InputArgument
{
	
	protected int HEIGHT = 20;
	private JPanel inPanel;
	private JTextField inputValue;
	
	protected InputArgumentText(Argument argument)
	{
		super(argument);
		
	}


	@Override
	public Argument getArgument()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected void setArgument(Argument argument)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void create(Argument argument)
	{
		
		this.setLayout(new BorderLayout());
		inPanel = createInPanelAndDescription(argument);
		inputValue = new JTextField();
//		inputValue.setPreferredSize(new Dimension(this.getWidth()/3*2,HEIGHT));
		inputValue.setPreferredSize(new Dimension(450,20));
		inputValue.setToolTipText(argument.getType());
		inPanel.add(inputValue,BorderLayout.LINE_END);
		this.add(inPanel,BorderLayout.PAGE_START);

	}


	@Override
	public void setSize(Dimension preferredSize)
	{
		
	}


	@Override
	public String getValue()
	{
		return inputValue.getText();
	}


	@Override
	public void setValue(String value)
	{
		inputValue.setText(value);
	}	
}