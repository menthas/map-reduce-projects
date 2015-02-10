package edu.neu.cs6260.a2;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Remedian class to estimate the median of a stream of numbers
 *
 * @see http://web.ipac.caltech.edu/staff/fmasci/home/statistics_refs/Remedian.pdf
 * @author behrooz, sahil
 */
public class Remedian {
    /**
     * numbers of slots per level
     */
    private int b;

    /**
     * Current level of input
     */
    private int i;

    /**
     * list of all levels currently in use
     */
    private ArrayList<Double[]> storage;

    /**
     * storage capacity indicator for each level
     */
    private ArrayList<Integer> storage_index;

    /**
     * Initializes a remedian estimator
     * @param b indicate number of slots in each level. must be an odd number.
     */
    public Remedian(int b) {
        if (b % 2 == 0)
            b++; // make sure b is always odd
        this.b = b;
        this.storage = new ArrayList<>();
        this.storage_index = new ArrayList<>();
        i = 0;
        addLevelIfNotExists(i);
    }

    /**
     * Add the next number from the stream to the estimator
     * @param item
     */
    public void add(double item) {
        if (!isLevelFull(i)) { // if level has empty slots use them
            this.storage.get(i)[getLevelNextIndex(i)] = item;
            incrementLevelIndex(i);
        } else { // free up this level and add another
            flush();
            add(item);
        }
    }

    /**
     * Calculate the final remedian by looking at the highest level
     * @return estimated median
     */
    public double getRemedian() {
        if (isLevelFull(i)) { // flushing the full list at level `i`
            flush();
        }

        int final_level = this.storage.size() - 1;
        if (isLevelFull(final_level)) { // then median = level median
            return levelMedian(final_level);
        } else { // get used slots and calculate median based on them
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

    /**
     * Flush current level into the next
     */
    private void flush() {
        double median = levelMedian(i);
        resetLevelIndex(i);
        addLevelIfNotExists(++i);
        add(median);
        i = 0;
    }

    /**
     * Calculate and return the level's median. level is re-ordered in the process
     * @param level
     * @return median
     */
    private double levelMedian(int level) {
        Arrays.sort(this.storage.get(level));
        return this.storage.get(level)[b / 2];
    }

    /**
     * Add a new level if needed
     * @param level
     */
    private void addLevelIfNotExists(int level) {
        if (this.storage.size() < level + 1) {
            this.storage.add(new Double[b]);
            this.storage_index.add(-1);
        }
    }

    /**
     * Is this level full
     * @param level
     * @return bool
     */
    private boolean isLevelFull(int level) {
        return this.storage_index.get(level) + 1 >= b;
    }

    /**
     * reset level index
     * @param level
     */
    private void resetLevelIndex(int level) {
        this.storage_index.set(level, -1);
    }

    /**
     * increment the level capacity pointer by one
     * @param level
     */
    private void incrementLevelIndex(int level) {
        this.storage_index.set(level, this.storage_index.get(level) + 1);
    }

    /**
     * get the next empty slot in this level. need to check the
     * level before using this method. otherwise it will return an out of bound index.
     * @param level
     * @return index of the next empty slot in this level
     */
    private int getLevelNextIndex(int level) {
        return this.storage_index.get(level) + 1;
    }
}
