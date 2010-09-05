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
package com.bay12games.df.rawedit.adt;

import java.util.ArrayList;

/**
 *
 * @author Matus Goljer
 * @version 1.0
 */
public class Range<T> implements Comparable<Range<T>> {

    private int bottom;
    private int top;
    private T userData;

    public Range(int bottom, int top) {
        this.bottom = bottom;
        this.top = top;
    }

    public Range(int bottom, int top, T userData) {
        this.bottom = bottom;
        this.top = top;
        this.userData = userData;
    }

    public int getBottom() {
        return bottom;
    }

    public int getTop() {
        return top;
    }

    public T getUserData() {
        return userData;
    }

    @Override
    public int compareTo(Range<T> other) {
        if (bottom < other.bottom && top < other.top) {
            return -1;
        }
        if (bottom > other.bottom && top > other.top) {
            return 1;
        }
        if (bottom == other.bottom && top == other.top) {
            return 0;
        }
        throw new IllegalArgumentException("Incomparable values (overlapping)");
    }

    public int compareTo(int value) {
        if (value < bottom) {
            return 1;
        }
        if (value > top) {
            return -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Range: " + bottom + "-" + top;
    }

    public static <T extends Range<?>> T binarySearch(ArrayList<T> ranges, int value) {
        int min = 0;
        int max = ranges.size() - 1;

        while (min <= max) {
            int mid = (min + max) / 2;
            int comparison = ranges.get(mid).compareTo(value);
            if (comparison == 0) {
                return ranges.get(mid);
            }
            if (comparison < 0) {
                min = mid + 1;
            }
            else if (comparison > 0) {
                max = mid - 1;
            }
        }
        return null;
    }

    /**
     * Return the index of the Range containing the value in the specified List.
     * 
     * @param ranges List of ranges (SORTED!)
     * @param value Value we're looking for
     * @return Index of the Range, or -1 if not found
     */
    public static <T extends Range<?>> int getIndex(ArrayList<T> ranges, int value) {
        int min = 0;
        int max = ranges.size() - 1;

        while (min <= max) {
            int mid = (min + max) / 2;
            int comparison = ranges.get(mid).compareTo(value);
            if (comparison == 0) {
                return mid;
            }
            if (comparison < 0) {
                min = mid + 1;
            }
            else if (comparison > 0) {
                max = mid - 1;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        Range r1 = new Range(0, 5, "");
        Range r2 = new Range(6, 11, "");
        Range r3 = new Range(12, 18, "");
        ArrayList<Range> ranges = new ArrayList<Range>();
        ranges.add(r1);
        ranges.add(r2);
        ranges.add(r3);

        System.out.println(binarySearch(ranges, 4));
        System.out.println(getIndex(ranges, 0));
    }
}
