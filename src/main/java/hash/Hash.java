package hash;

public interface Hash <T> {

    /**
     * Hashes the value and returns a long value
     * @param value         value to be hashed
     * @param limit         limit of the hash value
     * @return long         hashed value
     */
    long hash(T value, long limit);

}
