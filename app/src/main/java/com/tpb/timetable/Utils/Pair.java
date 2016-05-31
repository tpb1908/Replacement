package com.tpb.timetable.Utils;

/**
 * Created by theo on 17/05/16.
 */
public class Pair<L, R> {
    private final L left;
    private final R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) return false;
        Pair p = (Pair) o;
        return this.left.equals(p.left) &&
                this.right.equals(p.right);
    }
}
