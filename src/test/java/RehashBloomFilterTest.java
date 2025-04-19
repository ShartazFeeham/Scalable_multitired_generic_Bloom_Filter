import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RehashBloomFilterTest {

    private static BloomFilter<Integer> bloomFilter;
    private static Random random;

    @BeforeAll
    public static void setUp() {
        bloomFilter = new RehashBoomFilter<>();
        random = new Random();
    }

    private static void assertResult(double falsePositiveCount, int[] notPushedItems, double setFPR, int endIndex) {
        double falsePositiveRatio = falsePositiveCount / notPushedItems.length;
        System.out.println("False positive ratio: " + falsePositiveRatio);
        System.out.println("False positive ratio set: " + setFPR);
        Assertions.assertTrue(falsePositiveRatio < setFPR || String.valueOf(setFPR).substring(0, endIndex).equals(String.valueOf(falsePositiveRatio).substring(0, endIndex)));
    }

    @Test
    @DisplayName("Adding of 1000 elements")
    public void testAdd1000Items() {
        Assertions.assertDoesNotThrow(() -> {
            int[] ar = new int[1000];
            for (int i = 0; i < 1000; i++) {
                ar[i] = random.nextInt(1000);
            }

            for (int i = 0; i < 1000; i++) {
                bloomFilter.containsOrAdd(ar[i]);
            }
        });
    }

    @Test
    @DisplayName("Checking of 1000 elements")
    public void testAddAndCheck1000ItemsAllMustExist() {


        int[] ar = new int[1000];
        for (int i = 0; i < 1000; i++) {
            ar[i] = random.nextInt(1000);
        }

        for (int i = 0; i < 1000; i++) {
            bloomFilter.containsOrAdd(ar[i]);
        }

        for (int i = 0; i < 1000; i++) {
            System.out.println("\n\nChecking for " + i + "th element" + ": " + ar[i]);
            boolean contains = bloomFilter.containsOrAdd(ar[i]);
            Assertions.assertTrue(contains);
        }
    }

    @Test
    @DisplayName("Adding 1000 items with very low false positive rate")
    public void testAddAndCheck1000ItemsWithLowFalsePositive() {
        RehashBoomFilter<Integer> bloomFilter = new RehashBoomFilter<>(0.00001, true);

        int[] ar = new int[1000];
        for (int i = 0; i < 1000; i++) {
            ar[i] = random.nextInt(1000);
        }

        for (int i = 0; i < 1000; i++) {
            bloomFilter.containsOrAdd(ar[i]);
        }

        for (int i = 0; i < 1000; i++) {
            System.out.println("\n\nChecking for " + i + "th element" + ": " + ar[i]);
            boolean contains = bloomFilter.containsOrAdd(ar[i]);
            Assertions.assertTrue(contains);
        }
    }

    @Test
    @DisplayName("Checking of 1000 elements false positive rate")
    public void testAddAndCheck1000ItemsFalsePositiveRatio_1() {

        double setFPR = 0.01;
        RehashBoomFilter  <Integer> bloomFilter = new RehashBoomFilter  <>(setFPR, false);

        int[] pushedItems = new int[1000];
        int[] notPushedItems = new int[1000];
        populatePushItemArrays(pushedItems, notPushedItems, random);

        for (int item : pushedItems) {
            bloomFilter.containsOrAdd(item);
        }

        int falsePositiveCount = 0;
        for (int item : notPushedItems) {
            falsePositiveCount += bloomFilter.containsOrAdd(item) ? 1 : 0;
        }

        assertResult(falsePositiveCount, notPushedItems, setFPR, 4);
    }

    @Test
    @DisplayName("Checking of 1000 elements false positive rate")
    public void testAddAndCheck1000ItemsFalsePositiveRatio__1() {

        double setFPR = 0.001;
        RehashBoomFilter  <Integer> bloomFilter = new RehashBoomFilter  <>(setFPR, false);

        int[] pushedItems = new int[1000];
        int[] notPushedItems = new int[1000];
        populatePushItemArrays(pushedItems, notPushedItems, random);

        for (int item : pushedItems) {
            bloomFilter.containsOrAdd(item);
        }

        int falsePositiveCount = 0;
        for (int item : notPushedItems) {
            falsePositiveCount += bloomFilter.containsOrAdd(item) ? 1 : 0;
        }

        assertResult(falsePositiveCount, notPushedItems, setFPR, 5);
    }

    @Test
    @DisplayName("Checking of 100000 elements false positive rate")
    public void testAddAndCheck100000ItemsFalsePositiveRatio___1() {

        double setFPR = 0.0001;
        RehashBoomFilter  <Integer> bloomFilter = new RehashBoomFilter  <>(setFPR, false);

        int[] pushedItems = new int[1000];
        int[] notPushedItems = new int[1000];
        populatePushItemArrays(pushedItems, notPushedItems, random);

        for (int item : pushedItems) {
            bloomFilter.containsOrAdd(item);
        }

        int falsePositiveCount = 0;
        for (int item : notPushedItems) {
            falsePositiveCount += bloomFilter.containsOrAdd(item) ? 1 : 0;
        }

        assertResult(falsePositiveCount, notPushedItems, setFPR, 6);
    }

    private void populatePushItemArrays(int[] pushedItems, int[] notPushedItems, Random random) {

        Set<Integer> set = new HashSet<>();
        while (set.size() < pushedItems.length + notPushedItems.length) {
            set.add(random.nextInt());
        }

        int pi = 0, npi = 0;
        List<Integer> list = set.stream().toList();
        for (int i = 0; i < list.size(); i++) {
            if (i < pushedItems.length) {
                pushedItems[pi++] = list.get(i);
            } else {
                notPushedItems[npi++] = list.get(i);
            }
        }
    }

}
