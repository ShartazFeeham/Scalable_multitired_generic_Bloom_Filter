package hash.algorithms;

public class CurrentTimeAdderReverseHash<T> extends CurrentTimeAdder<T> {

    @Override
    protected long getHashAsLong(T value) {
        int hash = value.hashCode();
        return Long.reverse(hash);
    }

}
