package com.bay12games.df.dwarfstruct.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.bay12games.df.rawedit.Config;
import com.bay12games.df.rawedit.xml.entities.Container;
import com.bay12games.df.rawedit.xml.entities.ElementContainer;
import com.bay12games.df.rawedit.xml.entities.Token;

/**
 * <p>
 * Does parse all elements in the element container to a tree made out of
 * DwarfStructElements. Also has some additional function like checking if all
 * ids that are defined are used.<br>
 * </p>
 * 
 * @author Bruno Zimmermann
 */
public class DwarfStructElementParser
{
	private static final Logger log = Logger.getLogger(DwarfStructElementParser.class);
	private ElementContainer				elmtContainer;
	private Map<String, DwarfStructElement>	topElements;

	/**
	 * @param elmtContainer
	 */
	public DwarfStructElementParser(ElementContainer elmtContainer)
	{
		super();
		this.elmtContainer = elmtContainer;
	}

	/**
	 * Parses the element container given in the constructor what will give you
	 * one or more trees or DwarfStructElements
	 */
	public void parseElements()
	{
		parseContainers();
	}

	/**
	 * @return
	 */
	public Map<String, DwarfStructElement> getTopElements()
	{
		if (topElements == null)
		{
			parseContainers();
		}
		return topElements;
	}

	/**
	 * 
	 */
	private void parseContainers()
	{
		topElements = new HashMap<String, DwarfStructElement>();
		Set<String> referencedObjects = new TreeSet<String>();
		for (String containerKey : elmtContainer.getContainers().keySet())
		{
			referencedObjects.addAll(elmtContainer.getContainers().get(containerKey).getContainers().keySet());
		}
		for (String containerKey : elmtContainer.getContainers().keySet())
		{
			if (!referencedObjects.contains(containerKey))
			{
				topElements.put(containerKey, createDwarfStructContainerElement(elmtContainer.getContainers().get(containerKey)));
			}
		}
	}

	/**
	 * @param container
	 * @return
	 */
	private DwarfStructElement createDwarfStructContainerElement(Container container)
	{
		DwarfStructElement dsElement = new DwarfStructElement(container);
		Map<String, Container> subcontainers = container.getContainers();
		Map<String, Token> tokens = container.getTokens();
		if (subcontainers != null)
		{
			for (Container subcontainer : subcontainers.values())
			{
				DwarfStructElement childElement = createDwarfStructContainerElement(subcontainer);
				setParentChildRelation(dsElement, childElement);
			}
		}
		if (tokens != null)
		{
			for (Token token : tokens.values())
			{
				DwarfStructElement tokenElement = createDwarfStructElement(token);
				setParentChildRelation(dsElement, tokenElement);
			}
		}
		return dsElement;
	}

	/**
	 * @param token
	 * @return
	 */
	private DwarfStructElement createDwarfStructElement(Token token)
	{
		DwarfStructElement dsElement = new DwarfStructElement(token);
		return dsElement;
	}

	/**
	 * @param parentElement
	 * @param childElement
	 */
	private void setParentChildRelation(DwarfStructElement parentElement, DwarfStructElement childElement)
	{
		childElement.setParent(parentElement);
		parentElement.addChild(childElement);
	}

}
