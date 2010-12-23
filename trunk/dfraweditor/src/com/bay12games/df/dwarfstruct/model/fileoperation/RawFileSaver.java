package com.bay12games.df.dwarfstruct.model.fileoperation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFileChooser;

import org.apache.log4j.Logger;

import com.Main;
import com.bay12games.df.dwarfstruct.gui.DwarfStructBasePanel;
import com.bay12games.df.dwarfstruct.gui.argument.InputArgument;
import com.bay12games.df.dwarfstruct.gui.element.DwarfStructElementPanel;
import com.bay12games.df.dwarfstruct.model.DwarfStructElement;

public class RawFileSaver
{
	private static final Logger log = Logger.getLogger(RawFileSaver.class);
	public static final String INDENT_STRING	=	"\t";
	public static final String NEWLINE_STRING	=	"\n";
	private JFileChooser fc;
	private File file; 
	private DwarfStructBasePanel dwarfStructBasePanel;
	private List<String> raws;
	private String rawsString = new String();
	public RawFileSaver(File file, DwarfStructBasePanel dwarfStructBasePanel) throws IOException
	{
		raws=new LinkedList<String>();
		fc= new JFileChooser(new File(""));
		int returnVal = fc.showSaveDialog(dwarfStructBasePanel);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
            processRaws(dwarfStructBasePanel);
            saveFile(file,raws);
        } else {
        	System.out.println("Save command cancelled by user.");
        }
	}
	private void saveFile(File file,List<String> raws) throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
//		FileWriter writer = new FileWriter(file);
		for(String rawLine : raws)
		{
		writer.write(rawLine);
		writer.newLine();
		}
		writer.close();
		log.info("Saved the file " + file.getAbsolutePath());
	}
	private String processRaws(DwarfStructBasePanel dwarfStructBasePanel)
	{
		List<DwarfStructElementPanel> subElements = dwarfStructBasePanel.getSubElements();
		rawsString=buildRawCodeLine(new String(), dwarfStructBasePanel.getElement(), null);
		rawsString+=processSubelements(subElements,INDENT_STRING);
		return rawsString;
	}
	private String processSubelements(List<DwarfStructElementPanel> subElements, String indent)
	{
		String subElementRawCode = new String();
		List<DwarfStructElementPanel> containerElementPanels = new LinkedList<DwarfStructElementPanel>();
		for(DwarfStructElementPanel elementPanel:subElements)
		{
			//First add all the tokens. Then add the containers
			if(elementPanel.getElement().hasChildren())
			{
				containerElementPanels.add(elementPanel);
			}else
			{
				subElementRawCode+=buildRawCodeLine(indent, elementPanel.getElement(), elementPanel.getArguments());
			}
		}
		for(DwarfStructElementPanel containerElement:containerElementPanels)
		{
			subElementRawCode+=buildRawCodeLine(indent, containerElement.getElement(), containerElement.getArguments());
			subElementRawCode+=processSubelements(containerElement.getSubElements(), indent+INDENT_STRING);
		}
		return subElementRawCode;
	}
	
	private String buildRawCodeLine(String indent,DwarfStructElement element,List<InputArgument>arguments)
	{
		String codeLine = new String(indent+"[");
		codeLine+=element.getToken().getName();
		if(arguments!=null)
		{
			for(InputArgument arg:arguments)
			{
				codeLine+=":"+arg.getValue();
			}
		}
		codeLine+="]";
		raws.add(codeLine);
		return codeLine+NEWLINE_STRING;
	}
}
