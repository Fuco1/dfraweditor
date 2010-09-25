package com.bay12games.df.common.gui.dialogpannels;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.bay12games.df.common.model.Constants;
import com.bay12games.df.common.model.PropertiesLoader;
import com.bay12games.df.dwarfstruct.model.DwarfStructElement;
import com.bay12games.df.rawedit.Config;

/**
 * @author Bruno Zimmermann
 *
 */
class NewFilePanel extends JPanel
{
//	private JButton ok, cancel;
	private JLabel filenameLabel;
	private JLabel comboboxLabel;
	private JTextField filename;
	private JComboBox combobox;
	private Map<String, DwarfStructElement> topElements;
	private PropertiesLoader prop;
	
	public NewFilePanel() throws FileNotFoundException, IOException
	{
		topElements = Config.getInstance().getDwarfStructTrees();
		prop = PropertiesLoader.getInstance();
		init();
		build();
	}

	private void init()
	{

		filenameLabel = new JLabel(prop.getProperty(Constants.GUI_MENU_FILE_NEW_DIALOG_LABEL_FILENAME));
		filenameLabel.setBounds(new Rectangle(10, 10, 80, 30));
		filename = new JTextField(prop.getProperty(Constants.GUI_MENU_FILE_NEW_DIALOG_DEFAULT_FILENAME));
		filename.setBounds(new Rectangle(100, 10, 150, 30));
		
		comboboxLabel = new JLabel(prop.getProperty(Constants.GUI_MENU_FILE_NEW_DIALOG_LABEL_COMBOBOX));
		comboboxLabel.setBounds(new Rectangle(10, 45, 80, 30));
		combobox = new JComboBox(topElements.keySet().toArray());
		combobox.setBounds(new Rectangle(100, 45, 150, 30));
		
//		ok = new JButton(prop.getProperty(Constants.GUI_GLOBAL_BUTTON_OK));
//		ok.setBounds(new Rectangle(120,90,80,30));
//		cancel = new JButton(prop.getProperty(Constants.GUI_GLOBAL_BUTTON_CANCEL));
//		cancel.setBounds(new Rectangle(210,90,80,30 ));
	} 
	
	private void build()
	{
        this.setLayout(null);
        this.setBounds(new Rectangle(0, 0, 200, 100));
        this.setPreferredSize(new Dimension(300,130));
        this.add(filenameLabel, null);
        this.add(filename,null);
        this.add(comboboxLabel,null);
        this.add(combobox,null);
	}
	public String getComboboxSelection()
	{
		return combobox.getSelectedItem().toString();
	}
	public String getFileNameValue()
	{
		return filename.getText();
	}
}
