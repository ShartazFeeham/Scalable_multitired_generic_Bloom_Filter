
/**
 * Bloom filter implementation that uses rehashing to scale up the bitmap size
 * CAUTION: This implementation only supports a bitmap with maximum length of 2 ^ 30 (Half of Integer.MAX_VALUE)
 * For very small false positive ratio or very large number of elements, this implementation may not work
 * Use MultiTierBloomFilter instead
 * @param <T> the type of elements to be stored in the bloom filter
 */
public class RehashBoomFilter <T> extends MultiTierBloomFilter <T> {

    public RehashBoomFilter(double falsePositiveRatio, boolean showLog) {
        super(falsePositiveRatio, showLog);
    }

    public RehashBoomFilter() {
    }

    @Override
    protected void scaleUp() {
        int determineNewBitMapSize = determineNewBitMapSize();
        boolean[] oldBitMap = bitMaps.getLast();
        int newSize = oldBitMap.length;

        while(newSize < determineNewBitMapSize) {
            newSize = newSize * 2;
        }

        boolean[] newBitMap = new boolean[newSize];
        rehash(oldBitMap, newBitMap);
        bitMaps.clear();
        bitMaps.add(newBitMap);
        log("Initialized with new size. Size is " + newBitMap.length);
    }

    private void rehash(boolean[] oldBitMap, boolean[] newBitMap) {
        for (int i = 0; i < oldBitMap.length; i++) {
            if (oldBitMap[i]) {
                int newIndex = (i * 2) % newBitMap.length;
                newBitMap[newIndex] = true;
            }
        }
    }

}
