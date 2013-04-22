import junit.framework.TestCase;
import org.junit.Assert;

import java.util.Random;

public class SkipListTest extends TestCase{

	public void testFind() throws Exception {
		SkipList<Integer, Double> test = new SkipList<Integer, Double>();
		Double data[] = new Double[10];
		Random rnd = new Random();
		data[0] = rnd.nextDouble();
		test.first = new Cell<Integer, Double>(0, data[0]);
		Cell<Integer, Double> current = test.first;
		test.quantlev = 1;
		for (int i = 1; i < 10; i++) {
			data[i] = rnd.nextDouble();
			Cell<Integer, Double> temp = new Cell<Integer, Double>(i, data[i]);
			if ((i == 1) || (i == 9)) current.next.add(temp);
			current.next.add(temp);
			current = current.next.get(0);
		}
		int num;
		for (int i = 0; i < 100; i++) {
			num = rnd.nextInt(10);
			Assert.assertEquals(test.find(num), data[num]);
		}
	}

	public void testDelete() throws Exception {
		SkipList<Integer, Double> test = new SkipList<Integer, Double>();
		Integer keys[] = new Integer[10];
		Double data[] = new Double[10];
		Random rnd = new Random();
		for (int i = 0; i < 10; i++) {
			keys[i] = rnd.nextInt(1000);
			data[i] = rnd.nextDouble();
			test.add(keys[i], data[i]);
		}
		for (int i = 0; i < 10; i += 2) {
			test.delete(keys[i]);
		}
		try {
			for (int i = 0; i < 10; i++) {
				test.find(keys[i]);
			}
		} catch (Exception ex) {
			Assert.assertEquals(ex.getMessage(), "Not found");
		}
	}

	public void testAdd() throws Exception {
		SkipList<Integer, Double> test = new SkipList<Integer, Double>();
		Integer keys[] = new Integer[10];
		Double data[] = new Double[10];
		Random rnd = new Random();
		for (int i = 0; i < 10; i++) {
			keys[i] = rnd.nextInt(1000);
			data[i] = rnd.nextDouble();
			test.add(keys[i], data[i]);
		}
		Cell<Integer, Double> current = test.first;
		for (int i = 0; i < 10; i++) {
			while (!(current.key.equals(keys[i]))){
				current = current.next.get(0);
			}
			Assert.assertEquals(current.key, keys[i]);
			Assert.assertEquals(current.data, data[i]);
			current = test.first;
		}
	}
}
