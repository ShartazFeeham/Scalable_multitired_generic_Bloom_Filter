public class BloomFilter <T> {

    private final int size;
    private final int sizeMultiplier;
    private final double falsePositiveRatio;
    private final boolean multiTier;
    private final boolean rehash;
    private final boolean showLog;

    /**
     * <p>
     * A default constructor that comes with -
     *     <li>Initial size: 10</li>
     *     <li>Size multiplier: 2</li>
     *     <li>False positive ratio: 0.1 or 10%</li>
     *     <li>Multi tier: disabled & Rehash: enabled</li>
     *     <li>Show log: disabled</li>
     * </p>     */
    public BloomFilter() {
        this.size = 10;
        this.sizeMultiplier = 2;
        this.falsePositiveRatio = 0.1;
        this.multiTier = false;
        this.rehash = true;
        this.showLog = false;
    }

    /**
     * @param initialSize         size of the bloom filter
     * @param sizeMultiplier      size multiplier in case of over-crowding
     * @param falsePositiveRatio  preferred false positive ratio
     * @param multiTier           enable or multi tier bloom filter, *rehash is inversely related to this*
     */
    public BloomFilter(int initialSize, int sizeMultiplier, double falsePositiveRatio, boolean multiTier, boolean showLog) {
        this.size = initialSize;
        this.sizeMultiplier = sizeMultiplier;
        this.falsePositiveRatio = falsePositiveRatio;
        this.multiTier = multiTier;
        rehash = !multiTier;
        this.showLog = showLog;
    }

    /**
     * @param initialSize               size of the bloom filter
     * @param sizeMultiplier            size multiplier in case of over-crowding
     * @param falsePositivePercentage   preferred false positive ratio
     * @param multiTier                 enable or multi tier bloom filter, *rehash is inversely related to this*
     */
    public BloomFilter(int initialSize, int sizeMultiplier, int falsePositivePercentage, boolean multiTier, boolean showLog) {
        this.size = initialSize;
        this.sizeMultiplier = sizeMultiplier;
        this.falsePositiveRatio = falsePositivePercentage / 100.0;
        this.multiTier = multiTier;
        rehash = !multiTier;
        this.showLog = showLog;
    }

    /**
     * Check if an item exists or not
     * @param value         value that you are searching for
     * @return boolean      true if exists, false otherwise
     */
    public boolean ifExists(T value) {
        return false;
    }

    private void log(String message) {
        if (showLog) {
            System.out.println(message);
        }
    }
}
