package com.bay12games.df.dwarfstruct.gui.argument;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.bay12games.df.rawedit.xml.entities.Argument;

public abstract class InputArgument extends JPanel
{
	private Argument	argument;
	private JPanel		lablePanel, textPanel;
	
	/**
	 * @param argument
	 */
	protected InputArgument(Argument argument)
	{
		setArgument(argument);
		lablePanel = new JPanel();
		textPanel = new JPanel();
		create(argument);
	}

	/**
	 * @return
	 */
	public abstract Argument getArgument();

	/**
	 * @param argument
	 */
	protected abstract void setArgument(Argument argument);

	/**
	 * @param argument
	 * @return
	 */
	protected abstract void create(Argument argument);

	/**
	 * @return the lablePanel
	 */
	protected JPanel getLablePanel()
	{
		return lablePanel;
	}

	/**
	 * @param lablePanel
	 *            the lablePanel to set
	 */
	protected void setLablePanel(JPanel lablePanel)
	{
		this.lablePanel = lablePanel;
	}

	/**
	 * @return the textPanel
	 */
	protected JPanel getTextPanel()
	{
		return textPanel;
	}

	/**
	 * @param textPanel
	 *            the textPanel to set
	 */
	protected void setTextPanel(JPanel textPanel)
	{
		this.textPanel = textPanel;
	}

	/**
	 * Creates the inPanel with a label @ LINE_START and the description using
	 * the Attributes Description. Applies the description to the center of the
	 * ArgumentPanel <br>
	 * <br>
	 * Note: Don't forget to add the inPanel!
	 * 
	 * @return inPanel
	 */
	protected JPanel createInPanelAndDescription(Argument argument)
	{
		this.setLayout(new BorderLayout());
		JPanel inPanel = new JPanel(new BorderLayout());
		JLabel label = new JLabel(argument.getId() + ": ");
		label.setToolTipText(argument.getType());
		inPanel.add(label, BorderLayout.LINE_START);
		applyDescription(argument);
		// this.add(inPanel,BorderLayout.PAGE_START);
		return inPanel;
	}

	/**
	 * Creates the description using
	 * the Attributes Description. Applies the description to the center of the
	 * ArgumentPanel
	 */
	protected void applyDescription(Argument argument)
	{
		JTextArea description = createDescription(argument);
		this.add(description, BorderLayout.CENTER);
	}

	/**
	 * Creates the description using
	 * the Attributes Description.
	 * @return Description as a not edible grey colored wrapping JTextArea
	 */
	protected JTextArea createDescription(Argument argument)
	{
		JTextArea description = new JTextArea(argument.getDescription());
		description.setEditable(false);
		description.setBackground(new Color(244, 244, 244, 255));
		description.setLineWrap(true);
		description.setWrapStyleWord(true);
		return description;
	}

}
