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

	private JSlider slider;
	
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
		slider = new JSlider(argument.getMin(),argument.getMax());
		slider.setMinorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		inPanel.add(slider,BorderLayout.LINE_END);
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


	@Override
	public String getValue()
	{
		return Integer.toString(slider.getValue());
	}


	@Override
	public void setValue(String value)
	{
		slider.setValue(new Integer(value).intValue());
	}

	
}