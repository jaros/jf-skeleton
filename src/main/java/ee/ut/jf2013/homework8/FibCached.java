package ee.ut.jf2013.homework8;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Runtime.getRuntime;
import static java.lang.System.currentTimeMillis;
import static java.math.BigInteger.ONE;

public class FibCached {

    static class MyCache {

        private Map<Long, SoftReference<BigInteger>> cache = new HashMap<>();

        private long hitCount;
        private long putCount;
        private long getCount;
        private long collectedCount;
        private ReferenceQueue<BigInteger> queue = new ReferenceQueue<>();

        public BigInteger get(long n) {
            getCount++;
            SoftReference<BigInteger> reference = cache.get(n);
            if (reference == null) {
                return null;
            }
            hitCount++;
            return reference.get();
        }

        public void put(long n, BigInteger result) {
            cache.put(n, new SoftReference<>(result, queue));
            putCount++;
            checkGC();
        }

        private void checkGC() {
            Reference<? extends BigInteger> poll;
            while ((poll = queue.poll()) != null) {
                if (poll.get() == null)
                    collectedCount++;
            }
        }

        public long hitCount() {
            return hitCount;
        }

        public long putCount() {
            return putCount;
        }

        public long getCount() {
            return getCount;
        }

        public long collectedCount() {
            return collectedCount;
        }
    }

    static MyCache cache = new MyCache();
// this uses too much memory
//  static Map<Long, BigInteger> cache = new HashMap<Long, BigInteger>();

    public static BigInteger fib(long n) {
        if (n == 0 || n == 1)
            return ONE;

        BigInteger cached = cache.get(n);
        if (cached != null)
            return cached;

        BigInteger result = fib(n - 1).add(fib(n - 2));
        cache.put(n, result);
        return result;
    }

    public static void main(String... args) {
        System.out.println("max memory: " + getRuntime().maxMemory() / 1_000_000);
        System.out.println("free memory: " + getRuntime().freeMemory() / 1_000_000);
        BigInteger result = null;
        long start = currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            result = fib(i);
        }

        System.out.println("time: " + (currentTimeMillis() - start));

        System.out.println("done");
        System.out.println("free memory: " + getRuntime().freeMemory() / 1_000_000);
        System.out.println(result);
        // report statistics
        System.out.println("putCount=" + cache.putCount()); // number of calls to cache put method
        System.out.println("getCount=" + cache.getCount()); // number of calls to cache get method
        System.out.println("hitCount=" + cache.hitCount()); // number of calls to cache get method that found a value from cache
        System.out.println("missCount=" + (cache.getCount() - cache.hitCount())); // number of calls to cache get method that did not find a value from cache
        System.out.println("hitRatio=" + ((double) cache.hitCount()) / cache.getCount());
        System.out.println("collectedCount=" + cache.collectedCount()); // number of elements that were put to cache and collected by gc
    }

}
