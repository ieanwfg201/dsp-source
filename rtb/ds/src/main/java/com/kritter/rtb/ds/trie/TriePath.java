package com.kritter.rtb.ds.trie;

import lombok.Getter;

/**
 * Class signifying a path in the trie(starting from the root node).
 * @param <T> Type of value
 */
@Getter
public class TriePath<T> {
    private int[] dimensionList;
    private int[] dimensionValue;
    private T value;

    public TriePath(int[] dimensionList, int[] dimensionValue, T value) {
        this.dimensionList = dimensionList;
        this.dimensionValue = dimensionValue;
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < dimensionList.length; ++i) {
            builder.append(dimensionList[i]);
            builder.append("=");
            builder.append(dimensionValue[i]);
            if(i != dimensionList.length - 1)
                builder.append(",");
        }

        builder.append("\u0001");
        builder.append(value);

        return builder.toString();
    }
}