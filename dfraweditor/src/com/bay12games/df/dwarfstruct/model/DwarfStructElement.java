package com.bay12games.df.dwarfstruct.model;

import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.bay12games.df.rawedit.xml.entities.Token;

public class DwarfStructElement implements Comparable<DwarfStructElement>
{
	private static final Logger log = Logger.getLogger(DwarfStructElement.class);
	private Set<DwarfStructElement> children;
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
		children = new TreeSet<DwarfStructElement>();
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


	@Override
	public int compareTo(DwarfStructElement o)
	{
		return this.getToken().compareTo(o.getToken());
	}
	
	
}
