public interface Map<K, D> {

    public void add (K key, D data);

    public void delete (K key);

    public D find (K key);
}
