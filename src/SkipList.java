import java.util.NoSuchElementException;
import java.util.Random;

public class SkipList<K extends Comparable<K>, D> implements Map<K, D> {

	protected int quantlev = 0;
	protected Cell<K, D> first = new Cell<K, D>();                // голова списка

	private Cell<K, D> findcell(K key) {                        // функция поиска ячейки по ключу
		Cell<K, D> current = first;
		int curLevel = quantlev - 1;
		int comparison;
		do {
			comparison = current.key.compareTo(key);
			if (comparison == 0) {
				return current;                                 // если найдена, вернуть нужную ячеку
			}
			if (comparison < 0) {
				current = current.next.get(curLevel);           // если ключ текущей ячеки меньше, то перейти к следующей
			} else {
				if ((curLevel > 0) && (current != first)) {     // иначе, если есть уровень ниже и предыдущая ячека,
					current = current.prev.get(curLevel);       // то перейти в нее и понизить уровень
					curLevel--;
				} else return null;
			}
		} while (current != null);
		return null;
	}

	public D find(K key) {                                      // функция поиска данных, метод из интерфейса
		if (first == null) {
			throw new NoSuchElementException("Is empty");
		} else {
			Cell<K, D> temp = findcell(key);
			if (temp == null) {
				throw new NoSuchElementException("Not found");
			} else return temp.data;
		}
	}

	private void delFromHead() {                                // функция удаления из головы списка
		Cell<K, D> second = first.next.get(0);
		for (int i = second.next.size(); i < quantlev; i++) {   // протолкнуть вторую ячейку на максимальный уровень
			second.next.add(first.next.get(i));
			second.next.get(i).prev.set(i, second);
		}
		second.prev.clear();                                    // очистить связи с головой списка
		for (int i = 0; i < quantlev; i++) {
			second.prev.add(null);
		}
		first = second;                                         // переназначить голову списка
	}

	private void delFromMiddle(Cell<K, D> deleted) {            // функция удаления из середины списка
		for (int i = deleted.next.size() - 1; i >= 0; i--) {
			deleted.prev.get(i).next.set(i, deleted.next.get(i));
			deleted.next.get(i).prev.set(i, deleted.prev.get(i));
		}
	}

	private void delfromEnd(Cell<K, D> deleted) {               // функция удаления с конца списка
		for (int i = 0; i < quantlev; i++) {
			deleted.prev.get(i).next.set(i, null);
		}
	}

	private void delTheLast() {                                 // функция удаления последнего элемента
		first.next.clear();
		first.prev.clear();
		first.key = null;
		first.data = null;
		quantlev = 1;
	}

	public void delete(K key) {                                 // функция удаления из списка, метод интерфейса
		if (first.key == null) {                                // проверка на пустоту
			throw new NoSuchElementException("Is empty");
		}
		if (first.key == key) {                                 // если удаляемая ячейка - голова списка, удалить голову
			if (first.next.get(0) == null) {
				delTheLast();
				return;
			} else {
				delFromHead();
				return;
			}
		}
		Cell<K, D> deleted = findcell(key);
		if (deleted == null) {                                  // проверка существования удаляемой ячейки
			throw new NoSuchElementException("Not found");
		}
		if (deleted.next.get(0) != null) {                      // удалить из середины
			delFromMiddle(deleted);
		} else {                                                // удалить с конца
			delfromEnd(deleted);
		}
	}

	private void addToEmpty(K key, D data) {                    // функция добавления в пустой список
		first.key = key;
		first.data = data;
		first.next.add(null);
		first.prev.add(null);
		quantlev = 1;
	}

	private void addToFront(K key, D data) {                    // функция добавления в начало списка
		Cell<K, D> added = new Cell<K, D>(key, data);
		for (int i = 0; i < quantlev; i++) {                    // помещение новой ячейки перед головой списка
			added.prev.add(null);
			added.next.add(first);
			first.prev.set(i, added);
		}
		delTopLevels(first);                                    // удалить верхние уровни новой второй ячейки
		first = added;
	}

	private void delTopLevels(Cell<K, D> changed) {             // функция изменения уровня ячейки
		if (quantlev == 1) return;
		Random rnd = new Random();
		int newLevel = rnd.nextInt(quantlev - 1);               // newLevel - новый уровень ячейки
		for (int i = quantlev - 1; i > newLevel; i--) {         // удаление верхних уровней ячейки
			changed.next.get(i).prev.set(i, changed.prev.get(i));
			changed.prev.get(i).next.set(i, changed.next.get(i));
			changed.next.remove(i);
			changed.prev.remove(i);
		}
	}

	private Cell<K, D> findCellPrevAdd(K key, int level, Cell<K, D> prevCell) {// функция поиска ячейки после которой добавится новая
		while (prevCell.next.get(level) != null) {
			if (prevCell.next.get(level).key.compareTo(key) < 0) {
				prevCell = prevCell.next.get(level);
			} else break;
		}
		return prevCell;
	}

	private void addToEnd(Cell<K, D> lastCell, K key, D data) {      // функция добавления в конец списка
		Cell<K, D> added = new Cell<K, D>(key, data);
		for (int i = 0; i < quantlev; i++) {                         // помещение новой ячейки в конец списка
			added.next.add(null);
			added.prev.add(lastCell);
			lastCell.next.set(i, added);
		}
		delTopLevels(lastCell);                                      // уменьшить уровень новой предпоследней ячейки
	}

	private void levelUp(Cell<K, D> uppedCell) {
		first.prev.add(null);
		first.next.add(uppedCell);
		uppedCell.prev.add(first);
		int level = quantlev - 1;
		Cell<K, D> lastCell = uppedCell;
		while (lastCell.next.get(level) != null) {
			lastCell = lastCell.next.get(level);
		}
		uppedCell.next.add(lastCell);
		lastCell.prev.add(uppedCell);
		lastCell.next.add(null);
		quantlev++;
	}

	private void addToMiddle(Cell<K, D> prevCell, K key, D data) {
		Cell<K, D> newCell = new Cell<K, D>(key, data);
		for (int i = 0; i < quantlev; i++) {
			newCell.next.add(null);
			newCell.prev.add(null);
		}
		for (int i = quantlev - 1; i >= 0; i--) {
			prevCell = findCellPrevAdd(key, i, prevCell);
			newCell.next.set(i, prevCell.next.get(i));
			newCell.prev.set(i, prevCell);
			newCell.next.get(i).prev.set(i, newCell);
			prevCell.next.set(i, newCell);
		}
		Random rnd = new Random();
		int level = rnd.nextInt(quantlev+1);
		if (level == quantlev) {
			levelUp(newCell);
		} else {
			delTopLevels(newCell);
		}
	}

	public void add(K key, D data) {                             // функция добавления элемента, метод интерфейса
		if (first.key == null) {                                 // добавить в пустой список
			addToEmpty(key, data);
			return;
		}
		{
			Cell<K, D> existing = findcell(key);
			if (existing != null) {                              // если ячейка с нужным ключем существует, перезаписать данные
				existing.data = data;
				return;
			}
		}
		int comparison = first.key.compareTo(key);
		if (comparison > 0) {                                    // добавить перед головой списка
			addToFront(key, data);
		} else {
			Cell<K, D> prevCell = findCellPrevAdd(key, quantlev - 1, first); // найти ячейку, после которой нужно добавить новую
			if (prevCell.next.get(0) == null) {                  // если ячейка в конце списка,
				addToEnd(prevCell, key, data);                   // добавить в конец
			} else {
				addToMiddle(prevCell, key, data);
			}
		}
	}

	public String toStr() {
		Cell<K, D> current;
		String strOut = "";
		for (int i = 0; i < quantlev; i++) {
			current = first;
			while (current != null) {
				strOut = strOut + "(" + current.key + ", " + current.data + ") ";
				current = current.next.get(i);
			}
			strOut = strOut + "\r\n";
		}
		return strOut;
	}
}