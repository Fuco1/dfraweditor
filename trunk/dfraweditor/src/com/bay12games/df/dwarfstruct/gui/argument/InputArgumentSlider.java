package com.bay12games.df.dwarfstruct.gui.argument;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.bay12games.df.rawedit.xml.entities.Argument;

class InputArgumentSlider extends InputArgument
{


	protected InputArgumentSlider(Argument argument)
	{
		super(argument);
		// TODO Auto-generated constructor stub
	}


	@Override
	public Argument getArgument()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected void create(Argument argument)
	{
		this.setLayout(new BorderLayout());
		JPanel inPanel = createInPanelAndDescription(argument);
		JSlider silder = new JSlider(argument.getMin(),argument.getMax());
		inPanel.add(silder,BorderLayout.LINE_END);
		this.add(inPanel,BorderLayout.PAGE_START);
		
	}

	@Override
	protected void setArgument(Argument argument)
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setSize(Dimension paramDimension)
	{
		// TODO Auto-generated method stub
		
	}

	
}