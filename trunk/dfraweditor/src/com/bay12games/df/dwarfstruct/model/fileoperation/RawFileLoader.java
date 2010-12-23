package com.bay12games.df.dwarfstruct.model.fileoperation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.bay12games.df.dwarfstruct.gui.DwarfStructBasePanel;
import com.bay12games.df.dwarfstruct.gui.DwarfStructSelectionTable;
import com.bay12games.df.dwarfstruct.gui.action.SelectableDwarfStructComponent;
import com.bay12games.df.dwarfstruct.gui.element.DwarfStructElementPanel;
import com.bay12games.df.dwarfstruct.model.DwarfStructElement;
import com.bay12games.df.rawedit.Config;

public class RawFileLoader
{
	private static final Logger log = Logger.getLogger(RawFileLoader.class);
	DwarfStructBasePanel basePanel;
	File file;
	public RawFileLoader() throws IOException
	{
		JFileChooser fc= new JFileChooser(new File(""));
		int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
            String completeRaws = read(file);
            praseRaws(completeRaws,file);
        } else {
        	log.info("Load command cancelled by user.");
        }
	}

	private void praseRaws(String completeRaws,File file) throws FileNotFoundException, IOException
	{
		int currentLevel			= 0;
		String TOKEN_START 			= "[";
		String TOKEN_END 			= "]";
		String ATTRIBUTE_SEPERATOR 	= ":";
		String[] rawTokens 	= StringUtils.split(completeRaws, TOKEN_START);
		basePanel=null;
		List<SelectableDwarfStructComponent> currentComponent = new ArrayList<SelectableDwarfStructComponent>();
		//gives things like "NAME:Dwarf:Dwarves:Dwarven] Hey i am a silly comment :)"
		
		for(int i = 0;i<rawTokens.length;i++)
		{
			String[] choppedToken = StringUtils.split(StringUtils.substringBefore(rawTokens[i],TOKEN_END),ATTRIBUTE_SEPERATOR);
			//First chopps away everything after the ']'. Using the same example as above we get NAME:Dwarf:Dwarves:Dwarven
			//As next step it also cuts it to the Token and its attributes. NAME | Dwarf | Dwarves | Dwarven
			if(basePanel==null)
			{
				basePanel = getBasePanel(file,choppedToken);
				currentComponent.add(currentLevel,basePanel);
			}
			else
			{
				currentLevel = processElementPanelLoad(currentLevel, currentComponent, choppedToken);
			}
		}
	}

	/**
	 * @param currentLevel
	 * @param currentComponent
	 * @param choppedToken
	 * @return
	 */
	private int processElementPanelLoad(int currentLevel, List<SelectableDwarfStructComponent> currentComponent, String[] choppedToken)
	{
		SelectableDwarfStructComponent tempComp;
		tempComp = buildElementPanel(currentComponent.get(currentLevel), choppedToken);
		if(tempComp==null)
		{
			//TODO get a working not existent token handler...
			if(currentLevel>0)
			{
				currentLevel--;
				currentLevel = processElementPanelLoad(currentLevel, currentComponent, choppedToken);
			}else
			{
				log.info("Not possible token found; " + choppedToken[0]);
			}
		}
		else if(!tempComp.equals(currentComponent.get(currentLevel)))
		{
			currentLevel++;
			currentComponent.add(currentLevel,tempComp);
		}
		return currentLevel;
	}

	/**
	 * @param currentComponent
	 * @param choppedToken
	 * @return
	 */
	private SelectableDwarfStructComponent buildElementPanel(SelectableDwarfStructComponent currentComponent, String[] choppedToken)
	{
		DwarfStructElement usedElement = null;
		DwarfStructElementPanel elementPanel = null;
		for(DwarfStructElement element : currentComponent.getPossibleElements())
		{
			if(element.getToken().getName().equals(choppedToken[0]))
			{
				elementPanel = currentComponent.addElement(element);
//						elementPanel = getElementPanel(element,basePanel.getSelectTable(),currentComponent);
				List<String> values = new LinkedList<String>();
				for(int j = 1 ; j<choppedToken.length;j++)
				{
					values.add(choppedToken[j]);
				}
				elementPanel.setValues(values);
				usedElement=element;
				break;
			}
		}
		if(usedElement!=null && usedElement.hasChildren())
		{
			currentComponent=elementPanel;
			
		}
		else if(usedElement!=null&&!usedElement.hasChildren())
		{
			currentComponent.getPossibleElements().remove(usedElement);
		}
		else
		{
			currentComponent=null;
		}
		return currentComponent;
	}

	private DwarfStructBasePanel getBasePanel(File file, String[] choppedToken) throws FileNotFoundException, IOException
	{
		 Map<String, DwarfStructElement> treemap= Config.getInstance().getDwarfStructTrees();
		 DwarfStructElement foundBaseElement = treemap.get(choppedToken[0]+":"+choppedToken[1]);
		return new DwarfStructBasePanel(foundBaseElement,file);
	}
	
	private DwarfStructElementPanel getElementPanel(DwarfStructElement element, DwarfStructSelectionTable selectionTable, SelectableDwarfStructComponent currentComponent)
	{
		DwarfStructElementPanel panel = new DwarfStructElementPanel(element,selectionTable,currentComponent);
		return panel;
	}

	private String read(File file) throws IOException
	{
		String completeFile = new String();
		String line;
		BufferedReader reader = new BufferedReader(new FileReader(file));
		while((line=reader.readLine())!=null)
		{
			completeFile+=line;
		}
		return completeFile;
	}

	/**
	 * @return the basePanel loaded out of the file...
	 */
	public DwarfStructBasePanel getBasePanel()
	{
		return basePanel;
	}

	/**
	 * @return the file
	 */
	public File getFile()
	{
		return file;
	}
	
	
}
