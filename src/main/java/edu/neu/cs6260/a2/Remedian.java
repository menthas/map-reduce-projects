package edu.neu.cs6260.a2;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by menthas on 2/10/15.
 */
public class Remedian {
    private int b;
    private int i;
    private ArrayList<Double[]> storage;
    private ArrayList<Integer> storage_index;

    public Remedian(int b) {
        if (b % 2 == 0)
            b++; // make sure b is always odd
        this.b = b;
        this.storage = new ArrayList<>();
        this.storage_index = new ArrayList<>();
        i = 0;
        addLevelIfNotExists(i);
    }

    public void add(double item) {
        if (!isLevelFull(i)) {
            this.storage.get(i)[getLevelNextIndex(i)] = item;
            incrementLevelIndex(i);
        } else {
            double median = levelMedian(i);
            resetLevelIndex(i);
            addLevelIfNotExists(++i);
            add(median);
            i = 0;
            add(item);
        }
    }

    public double getRemedian() {
        if (isLevelFull(i)) { // flushing the full list at level i
            double median = levelMedian(i);
            resetLevelIndex(i);
            addLevelIfNotExists(++i);
            add(median);
            i = 0;
        }

        int final_level = this.storage.size() - 1;
        if (isLevelFull(final_level)) {
            return levelMedian(final_level);
        } else {
            int j = this.storage_index.get(final_level);
            Double[] final_level_elems = new Double[j+1];
            for (int k=0; k<=j; k++) {
                final_level_elems[k] = this.storage.get(final_level)[k];
            }
            Arrays.sort(final_level_elems);
            if ((j + 1) % 2 == 1) {
                return final_level_elems[(j + 1) / 2];
            } else {
                return (final_level_elems[j / 2] + final_level_elems[(j + 1) / 2]) / 2;
            }
        }
    }

    private double levelMedian(int level) {
        Arrays.sort(this.storage.get(level));
        return this.storage.get(level)[b / 2];
    }

    private void addLevelIfNotExists(int level) {
        if (this.storage.size() < level + 1) {
            this.storage.add(new Double[b]);
            this.storage_index.add(-1);
        }
    }

    private boolean isLevelFull(int level) {
        return this.storage_index.get(level) + 1 >= b;
    }

    private void resetLevelIndex(int level) {
        this.storage_index.set(level, -1);
    }

    private void incrementLevelIndex(int level) {
        this.storage_index.set(level, this.storage_index.get(level) + 1);
    }

    private int getLevelNextIndex(int level) {
        return this.storage_index.get(level) + 1;
    }
}
