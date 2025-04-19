import java.util.ArrayList;
import java.util.List;

/**
 * Bloom filter implementation that uses horizontal scaling to scale up the bitmap size. It can play with very large number of loads.
 * Element limit: Until your RAM melt down
 * False negative ratio limit: 0.00000000139699 (1 false negative in 715 million hits)
 *
 * @param <T> the type of elements to be stored in the bloom filter
 */
public class MultiTierBloomFilter<T> extends AbstractBloomFilter<T> {

    protected final List<boolean[]> bitMaps = new ArrayList<>();
    protected final List<Integer> setBitsCount = new ArrayList<>();

    public MultiTierBloomFilter() {
        super();
        // Setting first bitmap size to 1000
        this.bitMaps.add(new boolean[1000]);
        this.setBitsCount.add(0);
    }

    public MultiTierBloomFilter(double falsePositiveRatio, boolean showLog) {
        super(falsePositiveRatio, showLog);
        // Setting first bitmap size to 1000
        this.bitMaps.add(new boolean[1000]);
        this.setBitsCount.add(0);
    }

    @Override
    protected boolean isPlaceToAdd() {
        int totalSet = setBitsCount.getLast();
        // Subtracted 3 to get state after next addition worst case
        int availableSize = bitMaps.getLast().length - totalSet - algorithms.size();
        double falsePositiveRatio = calculateFalsePositiveRatio(availableSize, bitMaps.getLast().length);
        log("Available size: " + availableSize + ", False positive ratio: " + falsePositiveRatio);
        if (falsePositiveRatio >= this.falsePositiveRatio) {
            log("Can not add more bits to preserve false positive ratio");
            return false;
        }
        log("Can add more bits.");
        return true;
    }

    @Override
    void addToBitMap(List<Long> setBits) {
        setBits.stream()
                .map(Long::intValue)
                .forEach(bit -> {
                    boolean[] bitMap = bitMaps.getLast();
                    if (!bitMap[bit]) {
                        bitMap[bit] = true;
                        setBitsCount.add(setBitsCount.removeLast() + 1);
                    }
                });
    }

    @Override
    protected void scaleUp() {
        int newBitMapSize = determineNewBitMapSize();
        boolean[] newBitMap = new boolean[newBitMapSize];
        bitMaps.add(newBitMap);
        setBitsCount.add(0);
        log("New bitmap added in list. Size is " + newBitMap.length);
    }

    protected int determineNewBitMapSize() {
        long newBitMapSize = bitMaps.getLast().length;

        double falsePositiveRatio = Integer.MAX_VALUE;
        while(falsePositiveRatio > this.falsePositiveRatio) {
            newBitMapSize = newBitMapSize * 2L;
            if (newBitMapSize >= Integer.MAX_VALUE) {
                log("The new bitmap size is too large, setting it to " + (Integer.MAX_VALUE - 1));
                newBitMapSize = Integer.MAX_VALUE - 1;
            }
            falsePositiveRatio = calculateFalsePositiveRatio(newBitMapSize - algorithms.size(), newBitMapSize);
            if (newBitMapSize == Integer.MAX_VALUE - 1 && falsePositiveRatio >= this.falsePositiveRatio) {
                throw new IllegalStateException("The false positive ratio is too low! Cannot scale up.");
            }
        }
        log("New bitmap size is " + newBitMapSize);
        return (int) newBitMapSize;
    }

    protected double calculateFalsePositiveRatio(double availableSize, double totalSize) {
        return 1.0 - (availableSize / totalSize);
    }

    @Override
    long getTargetBitmapSize() {
        return bitMaps.getLast().length;
    }

    @Override
    public boolean contains(T value) {
        log("Checking if value " + value.toString() + " is present in the bloom filter");
        for (int i=0; i<bitMaps.size(); i++) {
            boolean allSet = checkForTier(i, value);
            if (allSet) {
                return true;
            }
        }
        log("Value " + value.toString() + " is not present in the bloom filter");
        return false;
    }

    protected boolean checkForTier(int i, T value) {
        log("Checking for tier " + i);
        boolean[] bitMap = bitMaps.get(i);
        List<Long> setBits = getSetBits(value, bitMap.length);
        log("Set bits: " + setBits);
        for(Long bit : setBits) {
            if (!bitMap[bit.intValue()]) {
                log("Bit " + bit + " is not set");
                return false;
            }
        }
        log("All bits are set for tier " + i);
        return true;
    }

    protected boolean checkInBitMap(int[] bitMap, List<Long> setBits) {
        for(int i = 0; i < setBitsCount.size(); i++) {
            if (bitMap[i] == 0) {
                log("Bit " + i + " is not set");
                return false;
            }
        }
        return true;
    }

    protected List<Long> getSetBits(T value, int limit) {
        return algorithms.stream()
                .map(algorithm -> algorithm.hash(value, limit))
                .toList();
    }

    @Override
    protected List<Long> getSetBits(T value) {
        return algorithms.stream()
                .map(algorithm -> algorithm.hash(value, getTargetBitmapSize()))
                .toList();
    }
}
