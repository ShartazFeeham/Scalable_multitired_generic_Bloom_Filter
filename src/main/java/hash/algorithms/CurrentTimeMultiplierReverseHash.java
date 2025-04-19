package hash.algorithms;

public class CurrentTimeMultiplierReverseHash<T> extends CurrentTimeMultiplier<T> {

    @Override
    protected long getHashAsLong(T value) {
        int hash = value.hashCode();
        return Long.reverse(hash);
    }

}
