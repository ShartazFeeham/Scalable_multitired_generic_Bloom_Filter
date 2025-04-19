package hash.algorithms;

import hash.Hash;

public class HashCodeOnly<T> implements Hash<T> {

    @Override
    public long hash(T value, long limit) {
        int hash = value.hashCode();
        return cutSize(hash, limit);
    }

    protected long cutSize(long hashValue, long limit) {
        return Math.abs(hashValue % limit);
    }

}
