package com.bay12games.df.common.gui;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.Main;
import com.bay12games.df.common.model.Constants;
import com.bay12games.df.common.model.PropertiesLoader;
import com.bay12games.df.dwarfstruct.gui.DwarfStructContentPanel;

public class ModToolGui extends JFrame
{
	private JPanel contentPanel;
	private Main rawEdit;
	private DwarfStructContentPanel dwarfStruct;
	PropertiesLoader prop;
	
	public ModToolGui() throws HeadlessException, FileNotFoundException, IOException
	{
		super(PropertiesLoader.getInstance().getProperty(Constants.GUI_WINDOW_TITLE_PROPERTY));
		prop=PropertiesLoader.getInstance();
		super.setDefaultCloseOperation(EXIT_ON_CLOSE);
		super.setJMenuBar(createMenuBar());
		contentPanel = new JPanel(new BorderLayout());
        rawEdit = new Main();
        dwarfStruct = new DwarfStructContentPanel();
        setContentDwarfStruct();
	}
	
	public void setContentRawEdit()
	{
		contentPanel.add(BorderLayout.CENTER,rawEdit);
	}
	
	public void setContentDwarfStruct()
	{
		contentPanel.add(BorderLayout.CENTER,dwarfStruct);
	}
	
	public void display()
	{
		this.setContentPane(contentPanel);
		this.pack();
		this.setVisible(true);
	}
	
	/**
	 * Creates the menubar.
	 * @return
	 */
	private JMenuBar createMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();
		
		
		menuBar.add(createFileMenu());
		menuBar.add(createWindowMenu());
		return menuBar;
	}

	private JMenu createFileMenu()
	{
		JMenu file = new JMenu(prop.getProperty(Constants.GUI_MENU_FILE));
		file.setMnemonic(KeyEvent.VK_F);
		return file;
	}

	/**
	 * @return
	 */
	private JMenu createWindowMenu()
	{
		JMenu window = new JMenu(prop.getProperty(Constants.GUI_MENU_WINDOW));
		window.setMnemonic(KeyEvent.VK_W); 
		JMenuItem dwarfStruct = new JMenuItem(prop.getProperty(Constants.GUI_MENU_WINDOW_DWARFSTRUCT),KeyEvent.VK_D);
		dwarfStruct.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				setContentDwarfStruct();
				display();
			}
		});
		JMenuItem rawEdit = new JMenuItem(prop.getProperty(Constants.GUI_MENU_WINDOW_RAWEDIT), KeyEvent.VK_R);
		rawEdit.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				setContentRawEdit();
				display();
			}
		});
		window.add(dwarfStruct);
		window.add(rawEdit);
		window.addSeparator();
		window.add(createLookAndFeelMenu());
		return window;
	}

	/**
	 * 
	 */
	private JMenu createLookAndFeelMenu()
	{
		JMenu looknFeelMenu = new JMenu(prop.getProperty(Constants.GUI_MENU_WINDOW_LOOKNFEEL));
		JMenuItem systemLooknFeel = new JMenuItem(prop.getProperty(Constants.GUI_MENU_WINDOW_LOOKNFEEL_SYSTEM));
		JMenuItem javaLooknFeel = new JMenuItem(prop.getProperty(Constants.GUI_MENU_WINDOW_LOOKNFEEL_CROSS));
		systemLooknFeel.addActionListener(new ActionListener()
		{
		
			@Override
			public void actionPerformed(ActionEvent e)
			{
			setSystemLookAndFeel();
			}
		});
		
		javaLooknFeel.addActionListener(new ActionListener()
		{
		
			@Override
			public void actionPerformed(ActionEvent e)
			{
			setCrossPlatformLookAndFeel();
			}
		});
		looknFeelMenu.add(systemLooknFeel);
		looknFeelMenu.add(javaLooknFeel);
		return looknFeelMenu;
	}
	
	public void setSystemLookAndFeel()
	{
			try
			{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedLookAndFeelException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SwingUtilities.updateComponentTreeUI(contentPanel);
	}
	public void setCrossPlatformLookAndFeel()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SwingUtilities.updateComponentTreeUI(contentPanel);
	}
}
