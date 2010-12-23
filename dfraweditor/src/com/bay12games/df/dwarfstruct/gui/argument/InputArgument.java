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
	private JPanel		labelPanel, textPanel;
	
	/**
	 * @param argument
	 */
	protected InputArgument(Argument argument)
	{
		setArgument(argument);
		labelPanel = new JPanel();
		textPanel = new JPanel();
		create(argument);
	}

	/**
	 * @return
	 */
	public abstract Argument getArgument();

	public abstract String getValue();
	
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
		return labelPanel;
	}

	/**
	 * @param labelPanel
	 *            the lablePanel to set
	 */
	protected void setLablePanel(JPanel labelPanel)
	{
		this.labelPanel = labelPanel;
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
		String labeltext = new String();
		if(argument.getLabel()!=null)
		{
			labeltext = argument.getLabel() + ": ";
		}
		JLabel label = new JLabel(labeltext);
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
		this.add(description, BorderLayout.PAGE_END);
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

	public abstract void setValue(String string);

}
