import java.util.ArrayList;
import java.util.List;

public class MultiTierBloomFilter<T> extends AbstractBloomFilter<T> {

    private final List<int[]> bitMaps = new ArrayList<>();
    private final List<Integer> setBitsCount = new ArrayList<>();

    public MultiTierBloomFilter() {
        super();
        // Setting first bitmap size to 1000
        this.bitMaps.add(new int[1000]);
        this.setBitsCount.add(0);
    }

    public MultiTierBloomFilter(double falsePositiveRatio, boolean showLog) {
        super(falsePositiveRatio, showLog);
        // Setting first bitmap size to 1000
        this.bitMaps.add(new int[1000]);
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
                    int[] bitMap = bitMaps.getLast();
                    if (bitMap[bit] == 0) {
                        bitMap[bit] = 1;
                        setBitsCount.add(setBitsCount.removeLast() + 1);
                    }
                });
    }

    @Override
    protected void scaleUp() {
        int newBitMapSize = determineNewBitMapSize();
        int[] newBitMap = new int[newBitMapSize];
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
        int[] bitMap = bitMaps.get(i);
        List<Long> setBits = getSetBits(value, bitMap.length);
        log("Set bits: " + setBits);
        for(Long bit : setBits) {
            if (bitMap[bit.intValue()] == 0) {
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
