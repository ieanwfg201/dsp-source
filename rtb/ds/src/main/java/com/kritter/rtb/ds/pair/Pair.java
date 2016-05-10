package com.kritter.rtb.ds.pair;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Implements a single pair.
 */
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Pair<F, S> implements Serializable {
    private F first;
    private S second;

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return first.toString() + ":" + second.toString();
    }
}
