import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CarCollectionTest {

    private CarCollection carCollection;

    @Before
    public void setUp() throws Exception {
        carCollection = new CarHashSet();
        for (int i = 0; i < 100; i++) {
            carCollection.add(new Car("Brand - " + i, i));
        }
    }


    @Test
    public void contains() {
        assertTrue(carCollection.contains(new Car("Brand - " + 0, 0)));
        assertFalse(carCollection.contains(new Car("Brand - " + 868, 89)));
    }

    @Test
    public void testForeach() {
        int index = 0;
        for (int i = 0; i < carCollection.size(); i++) {
            index++;
        }
        assertEquals(100, index);
    }
}