package com.bay12games.df.common.model;

import java.io.File;
import java.net.MalformedURLException;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * Has all constants (hardcoded strings), mostly
 * 
 * @author Bruno Zimmermann
 * 
 */
public class Constants
{
	/*-----------Steering constants-------------*/
	public static final String		NODE_ATTRIBUTE_NAME					= "name";
	public static final String		XML_SUFFIX							= "xml";
	
	/*-----------PROPERTIES-------------*/
	protected static final String	PROPERTIES							= "config.properties";
	
		/*################ CONFIG FILES ################*/
		public static final String		KEYWORD_TYPES_SOURCE				= "keyword.TypesSource";
		
		/*################ GUI TEXT ################*/
			public static final String		GUI_WINDOW_TITLE_PROPERTY			= "gui.window.title";
			/*######## GLOBAL TEXT ########*/
			public static final String	GUI_GLOBAL_BUTTON_OK		= "gui.global.button.ok";
			public static final String	GUI_GLOBAL_BUTTON_CANCEL	= "gui.global.button.cancel";
			public static final String	GUI_GLOBAL_BUTTON_YES		= "gui.global.button.yes";
			public static final String	GUI_GLOBAL_BUTTON_NO		= "gui.global.button.no";
			/*######## MENU TEXT ########*/
				/*#### FILE ####*/
				public static final String		GUI_MENU_FILE								= "gui.menu.file";
				public static final String		GUI_MENU_FILE_NEW							= "gui.menu.file.new";
				public static final String		GUI_MENU_FILE_NEW_DIALOG					= "gui.menu.file.new.dialog";
				public static final String		GUI_MENU_FILE_NEW_DIALOG_LABEL_COMBOBOX		= "gui.menu.file.new.dialog.label.combobox";
				public static final String		GUI_MENU_FILE_NEW_DIALOG_LABEL_FILENAME		= "gui.menu.file.new.dialog.label.filename";
				public static final String		GUI_MENU_FILE_NEW_DIALOG_DEFAULT_FILENAME	= "gui.menu.file.new.dialog.defaultfilename";
				public static final String		GUI_MENU_FILE_LOAD							= "gui.menu.file.load";
				public static final String		GUI_MENU_FILE_SAVE							= "gui.menu.file.save";
				public static final String		GUI_MENU_FILE_SAVEALL						= "gui.menu.file.saveall";
				/*#### WINDOW ####*/
				public static final String		GUI_MENU_WINDOW						= "gui.menu.window";
				public static final String		GUI_MENU_WINDOW_RAWEDIT				= "gui.menu.window.rawedit";
				public static final String		GUI_MENU_WINDOW_DWARFSTRUCT			= "gui.menu.window.dwarfstruct";
				public static final String		GUI_MENU_WINDOW_LOOKNFEEL			= "gui.menu.window.looknfeel";
				public static final String		GUI_MENU_WINDOW_LOOKNFEEL_SYSTEM	= "gui.menu.window.looknfeel.system";
				public static final String		GUI_MENU_WINDOW_LOOKNFEEL_CROSS		= "gui.menu.window.looknfeel.crosssystem";
			
			/*######## Selection Table ########*/
			public static final String	GUI_SELECTION_TABLE_HEADER_ITEM		= "gui.selection.table.item";
			public static final String	GUI_SELECTION_TABLE_HEADER_DESCRIPTION = "gui.selection.table.description";

/*-----------Other-------------*/
	/**
	 * @return a ImageIcon of a dwarf. And you can't stop him from dying!<br>
	 *         (cause of death: drowning in dimple dye...)
	 */
	public static final ImageIcon DWARF_ICON()
	{
		return new ImageIcon(DWARF_ICON_LOCATION);
	}
	public static final ImageIcon DWARF_COLORED_ICON()
	{
		return new ImageIcon(DWARF_COLORED_ICON_LOCATION);
	}
	public static final ImageIcon DWARF_COLORED_DOUBLE_SIZE_ICON()
	{
		return new ImageIcon(DWARF_COLORED_DOUBLE_SIZE_ICON_LOCATION);
	}

	public static final String	DWARF_ICON_LOCATION	= "images/dorf.png";
	public static final String	DWARF_COLORED_ICON_LOCATION	= "images/dorf_colored.png";
	public static final String	DWARF_COLORED_DOUBLE_SIZE_ICON_LOCATION	= "images/dorf_colored_2x.png";


	
	///////////////////////////////////////////////////////////////////////////////
	
	public static final String XML_ARGUMENT_LABLE = "label";
}
