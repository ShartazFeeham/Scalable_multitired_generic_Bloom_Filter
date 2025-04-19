package hash.algorithms;

public class CurrentTimeAdder<T> extends HashCodeOnly<T> {

    private final long time;

    public CurrentTimeAdder() {
        this.time = System.currentTimeMillis();
    }

    @Override
    public long hash(T value, long limit) {
        long sum = time + getHashAsLong(value);
        return cutSize(sum, limit);
    }

    protected long getHashAsLong(T value) {
        return value.hashCode();
    }
}
