package com.bay12games.df.dwarfstruct.gui.argument;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.bay12games.df.rawedit.xml.entities.Argument;

public class InputArgumentFactory
{
	public InputArgumentFactory()
	{
		
	}
	
	public InputArgument createInputPanel(Argument argument)
	{
		InputArgument pan;
		if(Argument.TYPE_RANGE.equalsIgnoreCase(argument.getType()))
		{
			pan = new InputArgumentSlider(argument);
		}else if(Argument.TYPE_ENUM.equalsIgnoreCase(argument.getType()))
		{
			pan = new InputArgumentEnum(argument);
		}
		else{
			pan = new InputArgumentText(argument);
		}
		return pan;
	}
	
	public static void main(String[] args)
	{
		InputArgumentFactory factory = new InputArgumentFactory();
		Argument arg1 = new Argument("String","Testoi, rlyy rlyyy long","???");
		Argument arg2 = new Argument("RangeTest","Range",1,255);
		arg1.setDescription("THIS IS TEEEEST AAAAAAAAAAAAAAAAAAA AAAAAAA A A A AAAAAAA A AAAAAA");
		arg2.setDescription("THIS IS TEEEEST AAAAAAAAAAAAAAAAAAA AAAAAAA A A A AAAAAAA A AAAAAA");
		JFrame frame = new JFrame();
		frame.setPreferredSize(new Dimension(800,100));
		frame.add(factory.createInputPanel(arg2));
		frame.pack();
		frame.setVisible(true);
	}




}
