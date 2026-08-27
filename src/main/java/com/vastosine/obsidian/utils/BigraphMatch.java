package com.vastosine.obsidian.utils;

import java.util.ArrayList;
import java.util.List;

public class BigraphMatch {
    private final List<List<Integer>> l, r;
    private int[][] w;
    private final int[] lCount, rCount;
    private int[] matches;
    private final int ln, rn;

    @FunctionalInterface
    public interface IntBiPredicate {
        boolean test(int leftNode, int rightNode);
    }

    public BigraphMatch(int[] lCount, int[] rCount) {
        this.lCount = lCount;
        this.rCount = rCount;
        this.ln = lCount.length;
        this.rn = rCount.length;
        l = new ArrayList<>();
        r = new ArrayList<>();
        for (int i = 0; i < ln; i++) {
            l.add(new ArrayList<>());
        }
        for (int i = 0; i < rn; i++) {
            r.add(new ArrayList<>());
        }
    }

    public BigraphMatch(int[] lCount, int[] rCount, IntBiPredicate predicate) {
        this(lCount, rCount);
        loadChecker(predicate);
    }

    void loadChecker(IntBiPredicate predicate) {
        for (int i = 0; i < ln; i++) {
            for (int j = 0; j < rn; j++) {
                if (predicate.test(i, j)) {
                    addEdge(i, j);
                }
            }
        }
    }

    public void addEdge(int u, int v) {
        l.get(u).add(v);
        r.get(v).add(u);
    }

    public void setCount(boolean isRight, int index, int count) {
        (isRight ? rCount : lCount)[index] = count;
    }

    private int dfs(boolean side, int u, int count, boolean[] lv, boolean[] rv) {
        boolean[] vis = side ? rv : lv;
        if (count <= 0 || vis[u]) return 0;
        vis[u] = true;
        int ret = 0;
        if (side) {
            ret = Math.min(count, rCount[u] - matches[u]);
            matches[u] += ret;
            count -= ret;
        }
        for (int v : (side ? r : l).get(u)) {
            if (count <= 0) break;
            int x = dfs(!side, v, side ? Math.min(count, w[v][u]) : count, lv, rv);
            ret += x;
            count -= x;
            if (side) w[v][u] -= x;
            else w[u][v] += x;
        }
        return ret;
    }

    public int[] run() {
        w = new int[ln][rn];
        matches = new int[rn];
        for (int i = 0; i < ln; i++) {
            int count = lCount[i], x;
            while ((x = dfs(false, i, count, new boolean[ln], new boolean[rn])) > 0) {
                count -= x;
            }
            if (count > 0) return null;
        }
        return matches;
    }
}