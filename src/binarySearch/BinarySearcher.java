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
    int lowerBound = findLowerBound(element);
    return lowerBound < array.length && array[lowerBound] == element;
  }

  public int findLowerBound(int element) {
    return findLowerBound(0,array.length,element);
  }

  protected int findLowerBound(int l, int r, int element) {
    if (l==r)
      return l;
    int midpoint = findMidpoint(l,r);
    if (element > array[midpoint])
      return findLowerBound(midpoint+1, r, element);
    else
      return findLowerBound(l, midpoint, element);
  }

  public static class InvalidArray extends RuntimeException {
  }

  public static class OutOfOrderArray extends RuntimeException{
  }
}
