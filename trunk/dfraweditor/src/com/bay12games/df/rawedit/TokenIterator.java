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
 * Token iterator. Use this class to retrieve the stream of tokens from the RAW (format)
 * input stream.
 *
 * @author Matus Goljer
 * @version 1.0
 */
public class TokenIterator implements Iterable<String> {

    private String source;
    // [NOTE] OBJECT treated as special pattern.
    private static Pattern p = Pattern.compile("\\[|\\]|\\:|OBJECT\\:.*?\\]|\\n");
    private String lastDelimiter;
    private String lastStartDelimiter;
    private int currentPosition = 0;
    private int tokenStart = 0;
    private int tokenEnd = 0;

    /**
     * Return an instance of the iterator that will tokenize the source.
     * 
     * @param source Source of the data
     */
    public TokenIterator(String source) {
        this.source = source;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    /**
     * @return The start index of the last matched token. The value is obtained
     * from Matcher's m.start();
     */
    public int getTokenStart() {
        return tokenStart;
    }

    /**
     * @return The end index of the last matched token. The value is obtained from
     * Matcher's m.end();
     */
    public int getTokenEnd() {
        return tokenEnd;
    }

    /**
     * @return The last matching delimiter. This is the delimiter that ended the current token,
     * that is, the delimiter right after the last character of the token.
     */
    public String getLastDelimiter() {
        return lastDelimiter;
    }

    /**
     * @return The last matching start delimiter. This is the delimiter that started the current token
     * (or ended the last!), that is, the delimiter right before the first character of the token.
     */
    public String getLastStartDelimiter() {
        return lastStartDelimiter;
    }

    /**
     * @return Iterator
     */
    @Override
    public Iterator<String> iterator() {
        return new TokenIterator0();
    }

    /**
     * The private class that realize the parsing. Use simple regular expressions.
     */
    private class TokenIterator0 implements Iterator<String> {

        private Matcher m = p.matcher(source);

        @Override
        public boolean hasNext() {
            return m.find();
        }

        @Override
        public String next() {
            String re = source.substring(currentPosition, m.end() - 1);
            tokenStart = currentPosition;
            tokenEnd = m.end();
            currentPosition = m.end();
            lastStartDelimiter = lastDelimiter;
            lastDelimiter = m.group();
            if (lastDelimiter.startsWith("OBJECT")) {
                lastDelimiter = "]";
            }
            return re;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    // test
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
