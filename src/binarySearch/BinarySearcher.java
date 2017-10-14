package binarySearch;

public class BinarySearcher {
  private long[] array;

  public BinarySearcher(long[] array) {
    this.array = array;
    if (array == null)
      throw new InvalidArray();
  }

  public void validate() {
    for (int i=0; i<array.length-1; i++)
      if (array[i] > array[i+1])
        throw new OutOfOrderArray();
  }

  public static int findMidpoint(int l, int r) {
    return l + (r - l)/2;
  }

  public boolean find(int element) {
    return find(0,array.length,element);
  }

  protected boolean find(int l, int r, int element) {
    if (l>=r)
      return false;
    int midpoint = findMidpoint(l,r);
    if (array[midpoint] == element)
      return true;
    else if (array[midpoint] < element)
      return find(midpoint+1, r, element);
    else
      return find(l, midpoint-1, element);
  }

  public static class InvalidArray extends RuntimeException {
  }

  public static class OutOfOrderArray extends RuntimeException{
  }
}
