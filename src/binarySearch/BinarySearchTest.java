package binarySearch;

import binarySearch.BinarySearcher.*;
import org.junit.Test;

import static binarySearch.BinarySearcher.findMidpoint;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BinarySearchTest {
  private BinarySearcher searcher;

  @Test
  public void createSearcher() throws Exception {
    searcher = new BinarySearcher(new long[1]);
  }

  @Test(expected = InvalidArray.class)
  public void nullInputThrowsException() throws Exception {
    searcher = new BinarySearcher(null);
  }

  @Test(expected = InvalidArray.class)
  public void zeroSizedArrayThrowsException() throws Exception {
    searcher = new BinarySearcher(new long[0]);
  }

  @Test
  public void checkInOrder() throws Exception {
    long[][] inOrderArrays = {{0}, {0, 1}, {0, 1, 2, 3}};
    for (long[] inOrder : inOrderArrays) {
      searcher = new BinarySearcher(inOrder);
      searcher.validate();
    }
  }

  @Test
  public void outOfOrderThrowsException() throws Exception {
    long[][] outOfOrderArrays = {{1, 0}, {1, 0, 2}, {0, 2, 1}, {0, 1, 2, 4, 3}};
    int exceptions = 0;
    for (long[] outOfOrder : outOfOrderArrays) {
      searcher = new BinarySearcher(outOfOrder);
      try {
        searcher.validate();
      } catch (OutOfOrderArray e) {
        exceptions++;
      }
    }
    assertEquals(outOfOrderArrays.length, exceptions);
  }

  @Test
  public void findsProperMidpoint() throws Exception {
    assertEquals(0, findMidpoint(0,0));
    assertEquals(0, findMidpoint(0, 1));
    assertEquals(1, findMidpoint(0, 2));
    assertEquals(1, findMidpoint(0, 3));
    assertEquals(Integer.MAX_VALUE/2, findMidpoint(0,Integer.MAX_VALUE));
    assertEquals(Integer.MAX_VALUE/2+1, findMidpoint(1, Integer.MAX_VALUE));
    assertEquals(Integer.MAX_VALUE/2+1, findMidpoint(2, Integer.MAX_VALUE));
    assertEquals(Integer.MAX_VALUE/2+2, findMidpoint(3, Integer.MAX_VALUE));
    assertEquals(Integer.MAX_VALUE/2, findMidpoint(1, Integer.MAX_VALUE-2));

  }

  private void assertFound(int target, long[] domain) {
    BinarySearcher searcher = new BinarySearcher(domain);
    assertTrue(searcher.find(target));
  }

  private void assertNotFound(int target, long[] domain) {
    BinarySearcher searcher = new BinarySearcher(domain);
    assertFalse(searcher.find(target));
  }

  @Test
  public void simpleFinds() throws Exception {
    assertFound(0, new long[]{0});
    assertFound(5, new long[]{0,1,5,7});
    assertFound(7, new long[]{0,1,5,7});

    assertNotFound(1, new long[]{0});
    assertNotFound(6, new long[]{1,2,5,7,9});
  }

  long[] makeArray(int n) {
    long[] array = new long[n];
    for (int i=0; i<n; i++)
      array[i] = i;
    return array;
  }

  private void assertCompares(int compares, int n) {
    long[] array = makeArray(n);
    BinarySearcherSpy spy = new BinarySearcherSpy(array);
    spy.find(0);
    assertTrue(spy.compares > 0);
    assertTrue(spy.compares <= compares);
  }

  @Test
  public void logNCheck() throws Exception {
    assertCompares(1,1);
    assertCompares(5,32);
    assertCompares(16,65536);
  }
}

class BinarySearcherSpy extends BinarySearcher {
  public int compares = 0;

  public BinarySearcherSpy(long[] array) {
    super(array);
  }

  protected boolean find(int l, int r, int element) {
    compares++;
    return super.find(l, r, element);
  }
}
