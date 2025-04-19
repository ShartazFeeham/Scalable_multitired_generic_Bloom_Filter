public interface BloomFilter <T> {

    /**
     * Check if an item exists or not
     * @param value         value that you are searching for
     * @return boolean      true if exists, false otherwise
     */
    boolean contains(T value);

    /**
     * Add an item to the bloom filter
     * @param value         value to be added
     */
    void add(T value);

    /**
     * Check if an item exists or add it to the bloom filter
     * @param value         value that you are searching for
     * @return boolean      true if exists, false otherwise
     */
    boolean containsOrAdd(T value);
}
