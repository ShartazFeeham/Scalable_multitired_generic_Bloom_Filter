import hash.Hash;
import hash.algorithms.CurrentTimeAdderReverseHash;
import hash.algorithms.CurrentTimeMultiplier;
import hash.algorithms.CurrentTimeMultiplierReverseHash;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBloomFilter <T> implements BloomFilter<T> {

    protected final double falsePositiveRatio;
    private final boolean showLog;
    private final List<Hash<T>> algorithms = new ArrayList<>();

    /**
     * Default constructor for AbstractBloomFilter.
     * Sets the default false positive ratio to 1%, showLogs to true, and initializes the hash algorithms.
     */
    public AbstractBloomFilter() {
        this(0.1, true);
    }

    /**
     * Constructor for AbstractBloomFilter with specified false positive ratio and showLogs flag.
     * @param falsePositiveRatio  the desired false positive ratio
     * @param showLog            flag to enable or disable logging
     */
    public AbstractBloomFilter(double falsePositiveRatio, boolean showLog) {
        this.falsePositiveRatio = falsePositiveRatio;
        this.showLog = showLog;
        setHashes();
    }

    protected void setHashes() {
        this.algorithms.add(new CurrentTimeMultiplier<>());
        this.algorithms.add(new CurrentTimeMultiplierReverseHash<>());
        this.algorithms.add(new CurrentTimeAdderReverseHash<>());
    }

    protected void log(String message) {
        if (showLog) {
            System.out.println(message);
        }
    }

    /**
     * Check if the bloom filter is full or not
     * @return boolean true if the bloom filter is full, false otherwise
     */
    abstract protected boolean isPlaceToAdd();

    /**
     * Add the bits to the bitmap
     * @param setBits      list of bits to be added
     */
    abstract void addToBitMap(List<Long> setBits);

    /**
     * Check if all bits are set
     * @param setBits      list of bits to be checked
     * @return boolean     true if all bits are set, false otherwise
     */
    abstract protected boolean isAllBitSet(List<Long> setBits);

    /**
     * Scale up the bloom filter
     */
    abstract protected void scaleUp();

    /**
     * Get the size of the bitmap
     * @return long       size of the bitmap
     */
    abstract long getTargetBitmapSize();

    @Override
    public boolean contains(T value) {
        log("Checking if value " + value.toString() + " is present in the bloom filter");
        List<Long> setBits = getSetBits(value);
        log("Set bits: " + setBits);
        boolean allSet = isAllBitSet(setBits);
        log("Bits set status: " + allSet);
        return  allSet;
    }

    @Override
    public void add(T value) {
        if (contains(value)) {
            log("Value " + value.toString() + " is already present in the bloom filter");
            return;
        }
        log("Adding value " + value.toString() + " to the bloom filter");
        if (isPlaceToAdd()) {
            List<Long> setBits = getSetBits(value);
            log("Set bits: " + setBits);
            addToBitMap(setBits);
            log("Value " + value.toString() + " added to the bloom filter");
        } else {
            log("Bloom filter is full, scaling up");
            scaleUp();
            log("Bloom filter scaled up");
            List<Long> setBits = getSetBits(value);
            log("Set bits: " + setBits);
            addToBitMap(setBits);
            log("Value " + value.toString() + " added to the bloom filter");
        }
    }

    @Override
    public boolean containsOrAdd(T value) {
        boolean exists = contains(value);
        if (!exists) {
            add(value);
        }
        log("Value " + value.toString() + " is " + (exists ? "already present" : "added") + " in the bloom filter");
        return exists;
    }

    protected List<Long> getSetBits(T value) {
        return algorithms.stream()
                .map(algorithm -> algorithm.hash(value, getTargetBitmapSize()))
                .toList();
    }
}
