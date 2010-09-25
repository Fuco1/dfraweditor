package com.bay12games.df.common.gui.dialogpannels;

import java.awt.Container;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.bay12games.df.common.model.Constants;
import com.bay12games.df.common.model.PropertiesLoader;
import com.bay12games.df.dwarfstruct.model.DwarfStructElement;
import com.bay12games.df.rawedit.Config;

/**
 * @author Bruno Zimmermann
 * Creates JPannels and the like for dialog boxes. 
 */
public class DialogPanelFactory
{
	private ImageIcon image;
	private PropertiesLoader prop;
	private Config config;
	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public DialogPanelFactory() throws FileNotFoundException, IOException
	{
		setImage(Constants.DWARF_COLORED_DOUBLE_SIZE_ICON());
		prop=PropertiesLoader.getInstance();
		config = Config.getInstance();
	}
	/**
	 * @return
	 */
	public ImageIcon getImage()
	{
		return image;
	}
	/**
	 * @param image
	 */
	public void setImage(ImageIcon image)
	{
		this.image = image;
	}
	
	/**
	 * 
	 * @return <code>null</code> if the user aborts. <br>If the user accepts a List with 2 Strings will be returned. <br>
	 * The first one is the filename.<br> 
	 * The second one the type. 
	 * @throws FileNotFoundException if there is no Config file
	 * @throws IOException if the config file could not have been read
	 */
	public List<String> newFilePrompt(Container container) throws FileNotFoundException, IOException
	{
		List<String> userInput = null;
		NewFilePanel pane = new NewFilePanel();
		int answer = JOptionPane.showConfirmDialog(container, pane,prop.getProperty(Constants.GUI_MENU_FILE_NEW),JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,getImage());
		if(answer==JOptionPane.OK_OPTION)
		{
			userInput = new ArrayList<String>(2);
			userInput.add(pane.getFileNameValue());
			userInput.add(pane.getComboboxSelection());
		}
		return userInput;
	}
	
}
