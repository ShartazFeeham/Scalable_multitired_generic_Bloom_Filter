import java.util.ArrayList;
import java.util.List;

public class BloomFilter <T> {

    private int bitMapSize;
    private int sizeMultiplier;
    private double falsePositiveRatio;
    private boolean showLog;
    private boolean multiTier;
    private int maxBitMapSize;

    private final List<boolean[]> bitMaps = new ArrayList<>();

    /**
     * <p>
     * A default constructor that comes with -
     *     <li>Initial size: 10</li>
     *     <li>Size multiplier: 2</li>
     *     <li>False positive ratio: 0.1 or 10%</li>
     *     <li>Multi tier: disabled & Rehash: enabled</li>
     *     <li>Show log: disabled</li>
     * </p>
     */
    public BloomFilter() {
        initialize(10, 2, 0.1, 1_000_000, ScaleStrategy.REHASH, false);
    }

    /**
     * @param initialBitMapSize         size of the bloom filter bit map
     * @param sizeMultiplier            size multiplier in case of over-crowding
     * @param falsePositiveRatio        preferred false positive ratio
     * @param maxBitMapSize             maximum size of the bloom filter bitmap
     * @param scaleStrategy             enable or disable multi tier bloom filter, *rehash is inversely related to this*
     */
    public BloomFilter(int initialBitMapSize, int sizeMultiplier,
                       double falsePositiveRatio, int maxBitMapSize,
                       ScaleStrategy scaleStrategy, boolean showLog) {
        initialize(initialBitMapSize, sizeMultiplier, falsePositiveRatio / 100.0, maxBitMapSize, scaleStrategy, showLog);
    }

    /**
     * @param initialBitMapSize         size of the bloom filter bit map
     * @param sizeMultiplier            size multiplier in case of over-crowding
     * @param falsePositivePercentage   preferred false positive ratio
     * @param maxBitMapSize             maximum size of the bloom filter bitmap
     * @param scaleStrategy             enable or disable multi tier bloom filter, *rehash is inversely related to this*
     */
    public BloomFilter(int initialBitMapSize, int sizeMultiplier,
                       int falsePositivePercentage, int maxBitMapSize,
                       ScaleStrategy scaleStrategy, boolean showLog) {
        initialize(initialBitMapSize, sizeMultiplier, falsePositivePercentage / 100.0, maxBitMapSize, scaleStrategy, showLog);
    }

    private void initialize(int initialBitMapSize, int sizeMultiplier,
                            double falsePositiveRatio, int maxBitMapSize,
                            ScaleStrategy scaleStrategy, boolean showLog) {
        this.bitMapSize = initialBitMapSize;
        this.sizeMultiplier = sizeMultiplier;
        this.falsePositiveRatio = falsePositiveRatio;
        this.showLog = showLog;
        if (initialBitMapSize > maxBitMapSize) {
            throw new IllegalArgumentException("Initial bitmap size cannot be greater than " + maxBitMapSize);
        }
        this.maxBitMapSize = maxBitMapSize;
        multiTier = scaleStrategy == ScaleStrategy.MULTI_TIER;
        bitMaps.add(new boolean[initialBitMapSize]);
    }

    /**
     * Check if an item exists or not
     * @param value         value that you are searching for
     * @return boolean      true if exists, false otherwise
     */
    public boolean ifExists(T value) {
        if (multiTier) {
            return proceedWithMultiTier(value);
        } else return proceedWithSingleTier(value);
    }

    private boolean proceedWithSingleTier(T value) {
        return false;
    }

    private boolean proceedWithMultiTier(T value) {
        return false;
    }

    private void log(String message) {
        if (showLog) {
            System.out.println(message);
        }
    }

    public enum ScaleStrategy {
        REHASH, MULTI_TIER
    }
}
