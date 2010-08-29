package com.bay12games.df.rawedit;
///*
// *  Copyright (C) 2010 Matus Goljer
// *
// *  This program is free software: you can redistribute it and/or modify
// *  it under the terms of the GNU General Public License as published by
// *  the Free Software Foundation, either version 3 of the License, or
// *  (at your option) any later version.
// *
// *  This program is distributed in the hope that it will be useful,
// *  but WITHOUT ANY WARRANTY; without even the implied warranty of
// *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// *  GNU General Public License for more details.
// *
// *  You should have received a copy of the GNU General Public License
// *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//package com.bay12games.df.rawedit;
//
//import com.bay12games.df.rawedit.xml.KeyWord;
//import com.bay12games.df.rawedit.xml.KeyWordType;
//import java.util.Map;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import javax.swing.text.BadLocationException;
//import javax.swing.text.DefaultStyledDocument;
//import javax.swing.text.Document;
//
///**
// *
// * @author Matus Goljer
// * @version 1.0
// */
//public class AutoFormatter {
//
//    private static final char indentPrefix = '\t';
//    private static Config config;
//
//    public static String getIndent(int level) {
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < level; i++) {
//            sb.append(indentPrefix);
//        }
//        return sb.toString();
//    }
//
//    public static DefaultStyledDocument format(Document d) {
//        return format(d, 0, d.getLength());
//    }
//
//    public static DefaultStyledDocument format(Document d, int offset, int len) {
////        Map<String, KeyWordType> types = Main.getConfig().getKeywordTypes();
////        Map<String, KeyWord> keywords = Main.getConfig().getKeywords();
//        Map<String, KeyWordType> types = config.getKeywordTypes();
//        Map<String, KeyWord> keywords = config.getKeywords();
//        DefaultStyledDocument doc = null;
//
//        try {
//            int indent = 0;
//            int newOffset = 0;
//            doc = new DefaultStyledDocument();
//            String text = d.getText(offset, d.getLength());
//
//            Matcher m = Pattern.compile("^.*$", Pattern.MULTILINE).matcher(text);
//            Matcher lm = null;
//            Pattern tokenPattern = Pattern.compile("\\[(.*?)((\\:.*?\\])|(\\]))");
//
//            String line = null;
//            String token = null;
//            String tokenId = null;
//            KeyWord kw = null;
//            String insert = null;
//            boolean start = false;
//            boolean lineEnd = false;
//            boolean lastTokenContainer = false;
//
//            while (m.find()) {
//                //TokenIterator ti = new TokenIterator(m.group().trim());
//                System.out.println("-");
//                System.out.println(m.group().trim());
//                System.out.println("-");
//
//                line = m.group().trim();
//                lm = tokenPattern.matcher(line);
//                start = true;
//
//                while (lm.find()) {
//                    System.out.println(lm.group(1));
//                    token = lm.group(0);
//                    tokenId = lm.group(1);
//                    kw = null;
//                    insert = "";
//
//                    lineEnd = lm.end() == line.length();
//
//                    if ((keywords.containsKey(tokenId)) && ((kw = keywords.get(tokenId)) != null)) {
//                        if (kw.isContainer()) {
//                            if (!lastTokenContainer && indent > 0) {
//                                indent--;
//                            }
//                            else {
//                                indent++;
//                            }
//                            start = true;
//                            lastTokenContainer = true;
//                        }
//                        else {
//                            start = false;
//                            lastTokenContainer = false;
//                        }
//
//                        insert =
//                          //(kw.isIndent() ? indentString : "")
//                          (start ? getIndent(indent) : "")
//                          + token
//                          + ((kw.isContainer() || lineEnd) ? '\n' : "");
//
//                        if (kw.isContainer()) {
//                            if (lastTokenContainer) {
//                                indent--;
//                            }
//                            else {
//                                indent++;
//                            }
//                        }
//                    }
//                    else {
//                        insert = (start ? getIndent(indent) : "") + token
//                          + (lineEnd ? '\n' : "");
//                        start = false;
//                    }
//
//                    doc.insertString(newOffset, insert, null);
//                    newOffset += insert.length();
//
//
////                    if ((keywords.containsKey(tokenId)) && ((kw = keywords.get(tokenId)) != null)) {
////                        if (kw.isContainer() && kw.isIndent()) {
////                            insert = (start ? getIndent(indent) : "") + token + '\n';
////                            doc.insertString(newOffset, insert, null);
////                            indent++;
////                            newOffset += insert.length();
////                        }
////                        else {
////                            insert = (start ? getIndent(indent) : "") + token
////                              + ((lm.end() == line.length()) ? '\n' : "");
////                            doc.insertString(newOffset, insert, null);
////                            newOffset += insert.length();
////                            start = false;
////                        }
////                    }
////                    else {
////                        insert = (start ? getIndent(indent) : "") + token
////                          + ((lm.end() == line.length()) ? '\n' : "");
////                        doc.insertString(newOffset, insert, null);
////                        newOffset += insert.length();
////                        start = false;
////                    }
//                }
//            }
//        } catch (BadLocationException ex) {
//        }
//
//        return doc;
//    }
//
//    public static void main(String[] args) throws BadLocationException {
//        config = new Config();
//        DefaultStyledDocument d = new DefaultStyledDocument();
//        d.insertString(0, "[OBJECT:ITEM][ITEM_ARMOR:ITEM_BP] [NAME:bp:bps][LOL:asd]\n     [ARMORLEVEL:4][TAG]", null);
//
//        System.out.println(d.getText(0, d.getLength()));
//        System.out.println("-----");
//        d = format(d);
//        System.out.println("-----");
//        System.out.println(d.getText(0, d.getLength()));
//    }
//}
