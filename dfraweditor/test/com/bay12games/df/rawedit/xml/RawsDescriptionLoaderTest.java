/*
 *  Copyright (C) 2010 Matus Goljer, Bruno Zimmermann
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * !!!NOTES!!!
 *
 * Keep test classes in test directory. That's what it's for. You can even place the stuff
 * in the same package to keep yourself from adding imports. Also allows you to
 * abuse package visibility.
 *
 * Simply load sources from 2 directories, src for sources and test for tests. Eclipse
 * should be able to do this.
 *
 * Also append the license crap to each source file. The names should go on one line,
 * sorted alphabetically.
 *
 * Authors of the class should then be ordered by "contribution" (or alphabet, but we should
 * keep it the same)
 *
 * Test classes should be called "NameOfTheClass"Test, to make automatization simple
 *
 * Well, since you've started with tests, I'll pull the code there as well. It'd probably be
 * better anyway.
 */
// are you using JUnit 3? mmm
//import junit.framework.TestCase;
package com.bay12games.df.rawedit.xml;

import org.junit.Test;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * Test for parser.
 *
 * @author Bruno Zimmermann
 * @author Matus Goljer
 */
public class RawsDescriptionLoaderTest {

    public RawsDescriptionLoaderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testParse() {
        RawsDescriptionLoader loader = new RawsDescriptionLoader();
        loader.parse("raws.xml");
    }
}
