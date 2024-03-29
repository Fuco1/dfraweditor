package com.bay12games.df.common.gui;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.Main;
import com.bay12games.df.common.gui.dialogpannels.DialogPanelFactory;
import com.bay12games.df.common.model.Constants;
import com.bay12games.df.common.model.PropertiesLoader;
import com.bay12games.df.dwarfstruct.gui.DwarfStructTabPanel;

public class ModToolGui extends JFrame
{
	private JPanel					contentPanel;
	private Main					rawEdit;
	private DwarfStructTabPanel	dwarfStruct;
	private PropertiesLoader		prop;
	private DialogPanelFactory		dialogFactory;
	

	public ModToolGui() throws HeadlessException, FileNotFoundException, IOException
	{
		super(PropertiesLoader.getInstance().getProperty(Constants.GUI_WINDOW_TITLE_PROPERTY));
		prop = PropertiesLoader.getInstance();
		super.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setIconImage(Constants.DWARF_COLORED_DOUBLE_SIZE_ICON().getImage());
		super.setJMenuBar(createMenuBar());
		contentPanel = new JPanel(new BorderLayout());
		rawEdit = new Main();
		dwarfStruct = new DwarfStructTabPanel();
		dialogFactory = new DialogPanelFactory();
		setContentDwarfStruct();
	}

	public void setContentRawEdit()
	{
		contentPanel.add(BorderLayout.CENTER, rawEdit);
	}

	public void setContentDwarfStruct()
	{
		contentPanel.add(BorderLayout.CENTER, dwarfStruct);
	}

	public void display()
	{
		this.setContentPane(contentPanel);
		this.pack();
		this.setVisible(true);
	}

	/**
	 * Creates the menubar.
	 * 
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
		JMenuItem newFile = new JMenuItem(prop.getProperty(Constants.GUI_MENU_FILE_NEW), KeyEvent.VK_N);
		newFile.addActionListener(new ActionListener()
		{
		
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{  List<String> userInputs = dialogFactory.newFilePrompt(getContentPane());
					if(userInputs!=null)
					{
						Iterator<String> userInputIterator = userInputs.iterator();
						dwarfStruct.createNewTab(createFile(userInputIterator.next()), userInputIterator.next());
					}
				} catch (FileNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			private File createFile(String filename) throws IOException
			{
				File dir = goOutside(new File(""));
				File file = constructFile(dir,filename);
				if(file.exists())
				{
					file = createFile(dir,filename,2);
				}
				// TODO Uncomment when done
//				file.createNewFile();
				return file;
			}
			private File createFile(File dir,String filename, int number)
			{
				File file = constructFile(dir,filename);
				if(file.exists())
				{
					file = createFile(dir,filename, number++);
				}
				return file;
			}
			private File goOutside(File file)
			{
				return file.getAbsoluteFile().getParentFile();
			}
			private File constructFile(File dir, String filename)
			{
				return new File(dir.getAbsolutePath()+File.separator+filename);
			}
		});
		JMenuItem loadFile = new JMenuItem(prop.getProperty(Constants.GUI_MENU_FILE_LOAD), KeyEvent.VK_L);
		loadFile.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					dwarfStruct.load();
				} catch (FileNotFoundException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		JMenuItem saveFile = new JMenuItem(prop.getProperty(Constants.GUI_MENU_FILE_SAVE), KeyEvent.VK_S);
		saveFile.addActionListener(new ActionListener()
		{
		
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					dwarfStruct.saveSelected();
				} catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		JMenuItem saveAll = new JMenuItem(prop.getProperty(Constants.GUI_MENU_FILE_SAVEALL));
		saveAll.addActionListener(new ActionListener()
		{
		
			@Override
			public void actionPerformed(ActionEvent e)
			{
			try
			{
				dwarfStruct.saveAll();
			} catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			}
		});
		file.add(newFile);
		file.add(saveFile);
		file.add(saveAll);
		file.add(loadFile);
		return file;
	}

	/**
	 * @return
	 */
	private JMenu createWindowMenu()
	{
		JMenu window = new JMenu(prop.getProperty(Constants.GUI_MENU_WINDOW));
		window.setMnemonic(KeyEvent.VK_W);
		JMenuItem dwarfStruct = new JMenuItem(prop.getProperty(Constants.GUI_MENU_WINDOW_DWARFSTRUCT), KeyEvent.VK_D);
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

	/**
	 * 
	 */
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

	/**
	 * 
	 */
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
