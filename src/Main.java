import java.util.Random;

public class Main {
	public static void main(String args[]) {
		try {
			SkipList<Integer, Integer> check = new SkipList<Integer, Integer>();
			Random rnd = new Random();
			for (int i=0; i<100; i++){
				check.add(rnd.nextInt(100),1000+i);
			}
			System.out.print("\r\n");
			String str = check.toStr();
			System.out.println(str);
		} catch (Exception ex) {
			System.out.println("Error: " + ex.getMessage());
		}
	}
}