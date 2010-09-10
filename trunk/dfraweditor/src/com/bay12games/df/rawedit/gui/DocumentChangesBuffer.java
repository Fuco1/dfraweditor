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
package com.bay12games.df.rawedit.gui;

import com.bay12games.df.rawedit.adt.Range;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author Matus Goljer
 * @version 1.0
 */
public class DocumentChangesBuffer {

    public static final Object EVENT_BUFFER = new Object();
    private ArrayList<Range> ranges = new ArrayList<Range>();
    private static final ArrayList<Range> emptyList = new ArrayList<Range>();

    public synchronized boolean isEmpty() {
        return ranges.isEmpty();
    }

    public synchronized void clear() {
        ranges.clear();
    }

    public synchronized boolean add(Range e) {
        return ranges.add(e);
    }

    public synchronized ArrayList<Range> getRanges() {
        ArrayList<Range> re = new ArrayList<Range>();
        re.addAll(ranges);
        return re;
    }

    public synchronized ArrayList<Range> mergeAndReturn() {
        if (ranges.isEmpty()) {
            return emptyList;
        }

        Collections.sort(ranges, new Comparator<Range>() {

            @Override
            public int compare(Range o1, Range o2) {
                if (o1.getBottom() < o2.getBottom()) {
                    return -1;
                }
                if (o1.getBottom() > o2.getBottom()) {
                    return 1;
                }
                return 0;
            }
        });
        ArrayList<Range> result = new ArrayList<Range>();
        Range cur = ranges.get(0);
        int i = 1;
        int newTop;
        while (i < ranges.size()) {
            if (ranges.get(i).getBottom() < cur.getTop()) {
                newTop = Math.max(ranges.get(i).getTop(), cur.getTop());
                cur = new Range(cur.getBottom(), newTop);
                //result.add(cur);
            }
            else {
                result.add(cur);
                cur = ranges.get(i);
            }
            i++;
        }
        result.add(cur);
        ranges.clear();
        ranges = result;
        ArrayList<Range> re = new ArrayList<Range>();
        re.addAll(ranges);
        return re;
    }

    public synchronized ArrayList<Range> mergeAndClear() {
        ArrayList<Range> re = mergeAndReturn();
        ranges.clear();
        return re;
    }

    public static void main(String[] args) {
        DocumentChangesBuffer dcb = new DocumentChangesBuffer();
        dcb.add(new Range(1, 3));
        dcb.add(new Range(2, 5));
        dcb.add(new Range(3, 7));
        dcb.add(new Range(8, 9));
        dcb.add(new Range(12, 16));
        dcb.add(new Range(14, 17));
//        dcb.add(new Range(5,10));
//        dcb.add(new Range(5,10));
        dcb.mergeAndReturn();
        System.out.println(dcb.getRanges());
    }
    /*
    def merge(intervals):
    if not intervals:
    return []
    intervals = sorted(intervals, key = lambda x: x[0])
    result = []
    (a, b) = intervals[0]
    for (x, y) in intervals[1:]:
    if x <= b:
    b = max(b, y)
    else:
    result.append((a, b))
    (a, b) = (x, y)
    result.append((a, b))
    return result
     */
}
