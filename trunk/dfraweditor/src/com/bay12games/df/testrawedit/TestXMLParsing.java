package com.bay12games.df.testrawedit;

import com.bay12games.df.rawedit.xml.RawsLoader;

import junit.framework.TestCase;

public class TestXMLParsing extends TestCase
{
	public void testParse()
	{
        RawsLoader loader = new RawsLoader();
        loader.parse("raws.xml");
	}
}
