package com.bay12games.df.dwarfstruct.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.bay12games.df.rawedit.xml.entities.Token;

/**
 * @author Bruno Zimmermann
 *
 */
public class DwarfStructElement implements Comparable<DwarfStructElement>
{
	private static final Logger log = Logger.getLogger(DwarfStructElement.class);
	private List<DwarfStructElement> children;
	private DwarfStructElement parent;
	private Token token;
	/**
	 * @param children
	 * @param parent
	 * @param token
	 */
	public DwarfStructElement(Token token)
	{
		super();
		this.token = token;
		children = new LinkedList<DwarfStructElement>();
		log.debug("New DwarfStructElement with token: " + token.getName());
	}
	
	public void addChild(DwarfStructElement child)
	{
		children.add(child);
	}

	public DwarfStructElement getParent()
	{
		return parent;
	}

	public void setParent(DwarfStructElement parent)
	{
		this.parent = parent;
	}
	
	public Token getToken()
	{
		return token;
	}

	/**
	 * Return a copy of the children
	 * @return
	 */
	public List<DwarfStructElement> getChildren()
	{
		return new LinkedList<DwarfStructElement>(children);
	}

	@Override
	public int compareTo(DwarfStructElement o)
	{
		return this.getToken().compareTo(o.getToken());
	}
	
	public boolean hasChildren()
	{
		return children.isEmpty();
	}
	
}
