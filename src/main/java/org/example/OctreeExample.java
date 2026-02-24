package org.example;


import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

final class OctreeExample {

    public static final Random RANDOM = new Random(System.currentTimeMillis());

    public static void main(final String[] args) {
        final Octree<String> first = Octree.createOctree(256, OctCoordinate::toString);
        // measure `first`
        System.out.println("size of 'first' = " + MemoryMeasurer.measure(first));

        first.hashCode();
        // measure `first` again
        System.out.println("size of 'first' after .hashCode() call = " + MemoryMeasurer.measure(first));

        final Octree<String> second = Octree.createOctree(256, OctCoordinate::toString);
        second.hashCode();
        // measure `second`
        System.out.println("size of 'second' after .hashCode() call = " + MemoryMeasurer.measure(second));

        final Octree<long[][][]> third = Octree.createOctree(256, octCoordinate -> {
            final long[][][] longs = new long[][][]{
                    {
                            {octCoordinate.x, octCoordinate.y, octCoordinate.z},
                            {octCoordinate.x, octCoordinate.z, octCoordinate.y},
                    },
                    {
                            {octCoordinate.y, octCoordinate.x, octCoordinate.z},
                            {octCoordinate.y, octCoordinate.z, octCoordinate.x},
                    },
                    {
                            {octCoordinate.z, octCoordinate.x, octCoordinate.y},
                            {octCoordinate.z, octCoordinate.y, octCoordinate.x},
                    },
            };
            return longs;
        });
        // measure `third`
        System.out.println("size of 'third' = " + MemoryMeasurer.measure(third));

        final Octree<String> penultimate = Octree.createOctree(1024, octCoordinate -> null);
        // measure `penultimate`
        System.out.println("size of 'penultimate' = " + MemoryMeasurer.measure(penultimate));

        final Octree<String> ultimate = Octree.createOctree(1024 * 1024, octCoordinate -> null);
        // measure `ultimate`
        System.out.println("size of 'ultimate' = " + MemoryMeasurer.measure(ultimate));
    }

    public enum OctPosition {
        TOP_LEFT_FRONT("top left front", 0),
        TOP_RIGHT_FRONT("top right front", 1),
        TOP_LEFT_BACK("top left back", 2),
        TOP_RIGHT_BACK("top right back", 3),
        BOTTOM_LEFT_FRONT("bottom left front", 4),
        BOTTOM_RIGHT_FRONT("bottom right front", 5),
        BOTTOM_LEFT_BACK("bottom left back", 6),
        BOTTOM_RIGHT_BACK("bottom right back", 7),
        ;
        private final CharSequence name;
        private final int index;

        OctPosition(final CharSequence name, final int index) {
            this.name = name;
            this.index = index;
        }
    }

    public static final class Octree<T> {
        private static final OctCoordinate NULL_COORDINATE = new OctCoordinate(-1, -1, -1);

        private static final Octree<?>[] NULL_OCTREE_MAP = new Octree[OctPosition.values().length];

        static {
            for (final OctPosition octPosition : OctPosition.values()) {
                NULL_OCTREE_MAP[octPosition.index] = new Octree<>(octPosition, NULL_COORDINATE);
            }
        }

        private final OctPosition position;
        private final OctCoordinate coordinate;

        private final Octree<T>[] children = new Octree[OctPosition.values().length];

        private T object;
        private int[] hash;

        private Octree(final OctPosition position, final OctCoordinate coordinate) {
            this.position = position;
            this.coordinate = coordinate;
        }

        public static <T> Octree<T> createOctree(final long maxDepth, final Function<OctCoordinate, T> constructor) {
            final Octree<T> octree = new Octree<>(null, NULL_COORDINATE);
            if (Long.compareUnsigned(maxDepth, 0) > 0) {
                for (final OctPosition position : OctPosition.values()) {
                    octree.children[position.index] = Octree.createOctree(position, null, constructor);
                    long depthRemaining = maxDepth - 1;
                    Octree<T> tail = null;
                    while (Long.compareUnsigned(depthRemaining, 0) > 0) {
                        final OctPosition randomPosition = OctPosition.values()[RANDOM.nextInt(OctPosition.values().length)];
                        tail = createOctree(randomPosition, tail, constructor);
                        depthRemaining--;
                    }
                    if (tail != null) {
                        octree.children[position.index].children[tail.position.index] = tail;
                    }
                }
            }
            return octree;
        }

        private static <T> Octree<T> createOctree(final OctPosition position, final Octree<T> child, final Function<OctCoordinate, T> constructor) {
            final OctCoordinate coordinate1 = new OctCoordinate(RANDOM.nextLong() & Long.MAX_VALUE, RANDOM.nextLong() & Long.MAX_VALUE, RANDOM.nextLong() & Long.MAX_VALUE);
            final Octree<T> octree1 = new Octree<>(position, coordinate1);
            for (final OctPosition octPos : OctPosition.values()) {
                octree1.children[octPos.index] = getNullOctree(octPos);
            }
            octree1.object = constructor.apply(coordinate1);
            final Octree<T> octree = octree1;
            if (child != null) {
                octree.children[child.position.index] = child;
            }
            return octree;
        }

        private static <T> Octree<T> getNullOctree(final OctPosition octPos) {
            return (Octree<T>) NULL_OCTREE_MAP[octPos.index];
        }

        public T getObject() {
            return object;
        }

        public void setObject(final T object) {
            this.object = object;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Octree)) {
                return false;
            }
            final Octree<?> octree = (Octree<?>) obj;
            if (position != octree.position || !Objects.equals(coordinate, octree.coordinate)) {
                return false;
            }

            return Objects.equals(object, octree.object) && Arrays.equals(children, octree.children);
        }

        @Override
        public int hashCode() {
            if (hash == null) {
                hash = new int[children.length];
                for (int i = 0, length = children.length; i < length; i++) {
                    hash[i] = Objects.hashCode(children[i]);
                }
            }
            return Objects.hash(position, coordinate, object, hash);
        }

        @Override
        public String toString() {
            return "Octree{" +
                    "position=" + position +
                    ", coordinate=" + coordinate +
                    ", object=" + object +
                    '}';
        }
    }

    public static final class OctCoordinate extends BaseOctCoordinate {
        public OctCoordinate(long x, long y, long z) {
            super(x, y, z);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof OctCoordinate)) {
                return false;
            }
            final OctCoordinate that = (OctCoordinate) obj;
            return x == that.x && y == that.y && z == that.z;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
        }

        @Override
        public String toString() {
            return String.format("{x:%d, y:%d, z:%d}", x, y, z);
        }

    }

    public static class BaseOctCoordinate {
        protected final long x, y, z;

        public BaseOctCoordinate(final long x, final long y, final long z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

    }
}
