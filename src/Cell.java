import java.util.Vector;

class Cell<K extends Comparable<K>, D> {
	K key = null;
	D data = null;
	Vector<Cell<K, D>> next = new Vector<Cell<K, D>>();
	Vector<Cell<K, D>> prev = new Vector<Cell<K, D>>();

	public Cell (){}

	public Cell (K key, D data){
		this.key = key;
		this.data = data;
	}
}
