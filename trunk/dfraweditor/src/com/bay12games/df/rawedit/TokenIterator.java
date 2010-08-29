/*
 *  Copyright (C) 2010 Matus Goljer
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
package com.bay12games.df.rawedit;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Matus Goljer
 * @version 1.0
 */
public class TokenIterator implements Iterable<String> {

    private String source;
    // [WARNING] the newline match might break something
    private Pattern p = Pattern.compile("\\[|\\]|\\:|OBJECT\\:.*?\\]|\\n");
    private String lastDelimiter;
    private int currentPosition = 0;
    private int tokenStart = 0;
    private int tokenEnd = 0;

    public TokenIterator(String source) {
        this.source = source;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public int getTokenStart() {
        return tokenStart;
    }

    public int getTokenEnd() {
        return tokenEnd;
    }

    public String getLastDelimiter() {
        return lastDelimiter;
    }

    public Iterator<String> iterator() {
        return new TokenIterator0();
    }

    private class TokenIterator0 implements Iterator<String> {

        private Matcher m = p.matcher(source);

        public boolean hasNext() {
//            int cp = currentPosition;
//            int cpb;
//            boolean re = false;
//            do {
//                cpb = cp;
//                re = m.find();
//                if (re) {
//                    cp = m.end();
//                }
//            } while (re && (cpb - m.end() + 1) == 0);
//            return re;
            return m.find();
        }

        public String next() {
            String re = source.substring(currentPosition, m.end() - 1);
            tokenStart = currentPosition;
            tokenEnd = m.end();
            currentPosition = m.end();
            lastDelimiter = m.group();
            return re;
        }

        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public static void main(String[] args) {
        TokenIterator t = new TokenIterator("lol\n\n[OBJECT:ITEM][FAIL]\n[NAME:plate]");
        for (String s : t) {
            System.out.println("\"" + s + "\"");
        }

        t = new TokenIterator("[OBJECT:ITEM]");
        for (String s : t) {
            System.out.println("\"" + s + "\"" + t.getTokenStart());
        }
    }
}
