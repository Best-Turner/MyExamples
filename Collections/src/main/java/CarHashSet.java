import java.util.Iterator;

public class CarHashSet implements CarSet {


    private final static int INITIAL_CAPACITY = 16;
    private int size;
    private Entry[] array = new Entry[INITIAL_CAPACITY];

    private static final double LOAD_FACTOR = 0.75;

    @Override
    public boolean add(Car car) {
        if (size >= (array.length * LOAD_FACTOR)) {
            increaseArray();
        }
        boolean added = add(car, array);
        if (added) {
            size++;
        }
        return added;
    }

    private boolean add(Car car, Entry[] dst) {
        int position = getElementPosition(car, dst.length);
        if (dst[position] == null) {
            dst[position] = new Entry(car, null);

            return true;
        } else {
            Entry existedElement = dst[position];
            while (true) {
                if (existedElement.value.equals(car)) {
                    return false;
                } else if (existedElement.next == null) {
                    existedElement.next = new Entry(car, null);

                    return true;
                } else {
                    existedElement = existedElement.next;
                }
            }
        }
    }

    @Override
    public Iterator<Car> iterator() {
        return new Iterator<Car>() {
            private int index = 0;
            private int arrayIndex = 0;
            private Entry entry;

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public Car next() {
                while (array[arrayIndex] == null) {
                    arrayIndex++;
                }
                if (entry == null) {
                    entry = array[arrayIndex];
                }
                Car car = entry.value;
                entry = entry.next;
                if (entry == null) {
                    arrayIndex++;
                }
                return car;
            }
        };
    }

    @Override
    public boolean contains(Car car) {
        int position = getElementPosition(car, array.length);
        if (array[position] == null) {
            return false;
        }
        Entry existElement = array[position];
        Entry lastElement = existElement.next;
        if (existElement.value.equals(car)) {
            return true;
        }
        while (lastElement != null) {
            if (lastElement.value.equals(car)) {
                return true;
            } else {
                lastElement = lastElement.next;
            }
        }
        return false;
    }

    @Override
    public boolean remove(Car car) {
        int position = getElementPosition(car, array.length);
        if (array[position] == null) {
            return false;
        }

        Entry secondLast = array[position];
        Entry last = secondLast.next;

        if (secondLast.value.equals(car)) {
            array[position] = last;
            size--;
            return true;
        }
        while (last != null) {
            if (last.value.equals(car)) {
                secondLast.next = last.next;
                size--;
                return true;
            } else {
                secondLast = last;
                last = last.next;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        array = new Entry[INITIAL_CAPACITY];
        size = 0;
    }


    private void increaseArray() {
        Entry[] newArray = new Entry[array.length * 2];
        for (Entry entry : array) {
            Entry existedElement = entry;
            while (existedElement != null) {
                add(existedElement.value, newArray);
                existedElement = existedElement.next;
            }
        }
        array = newArray;
    }

    private static class Entry {
        private Car value;
        private Entry next;


        public Entry(Car value, Entry next) {
            this.value = value;
            this.next = next;
        }
    }


    private int getElementPosition(Car car, int arrayLength) {
        return Math.abs(car.hashCode() % arrayLength);
    }
}
