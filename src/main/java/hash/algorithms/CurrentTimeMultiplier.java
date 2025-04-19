package hash.algorithms;

public class CurrentTimeMultiplier<T> extends HashCodeOnly<T> {

    private final long time;

    public CurrentTimeMultiplier() {
        this.time = System.currentTimeMillis();
    }

    @Override
    public long hash(T value, long limit) {
        long hashValue = getHashAsLong(value);
        long mul = 0L;
        while (true) {
            try {
                mul = time + hashValue;
                break;
            } catch (ArithmeticException e) {
                // Handle the case where the multiplication overflows
                hashValue = hashValue % 10;
            }
        }
        return cutSize(mul, limit);
    }

    protected long getHashAsLong(T value) {
        return value.hashCode();
    }
}
