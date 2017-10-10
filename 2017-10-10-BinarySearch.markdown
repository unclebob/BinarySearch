---
layout: post
title: Let's write a Binary Search
tags: ["Software"]
---
Let's use TDD to write a Binary Search.  Since we know the algorithm already, we don't need to use TDD, or the [TPP](http://blog.cleancoder.com/uncle-bob/2013/05/27/TheTransformationPriorityPremise.html) to derive it.  So we'll just write our tests to verify that the algorithm is correct.

We begin, as usual, with an empty test, just to get an execution environment working.

    package binarySearch;
    import org.junit.Test;

    public class BinarySearchTest {
      @Test
      public void nothing() throws Exception {
      }
    }

Next we'll write the test that forces us to create the class for the binary search.

    public class BinarySearchTest {
      @Test
      public void createSearcher() throws Exception {
        long array[] = new long[2];
        BinarySearcher searcher = new BinarySearcher(array);

      }
    }
    
----

    package binarySearch;

    public class BinarySearcher {
      public BinarySearcher(long[] array) {
      }
    }

As you can see, we've made the decision to include the array to be searched in the constructor of the `BinarySearcher` class.  

The next few tests make sure we properly handle an invalid input array.

    @Test(expected = BinarySearcher.InvalidArray.class)
    public void nullInputThrowsException() throws Exception {
      BinarySearcher searcher = new BinarySearcher(null);
    }

    @Test(expected = BinarySearcher.InvalidArray.class)
    public void zeroSizedArrayThrowsException() throws Exception {
      BinarySearcher searcher = new BinarySearcher(new long[0]);
    }

----

    public class BinarySearcher {
      public BinarySearcher(long[] array) {
        if (array == null || array.length == 0)
          throw new InvalidArray();
      }

      public class InvalidArray extends RuntimeException {
      }
    }

As we continue, you will note that we are following the principle of "Avoiding the gold".  We will write tests around the _outside_ of the problem first; making sure that all the validation and organization is working before we head into the actual problem of doing a binary search.

The next tests check that the input array can be verified to be ordered.  We used this approach because the check is expensive.  Programmers who use this class may already know that their input is properly ordered.  Other programmers may need the check.  

    @Test
    public void checkInOrder() throws Exception {
      long[][] inOrderArrays = { {0}, {0, 1}, {0, 1, 2, 3} };
      for (long[] inOrder : inOrderArrays) {
        searcher = new BinarySearcher(inOrder);
        searcher.validate();
      }
    }

    @Test
    public void outOfOrderThrowsException() throws Exception {
      long[][] outOfOrderArrays = { {1, 0}, {1, 0, 2}, {0, 2, 1}, {0, 1, 2, 4, 3} };
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

----

    public class BinarySearcher {
      private long[] array;

      //...

      public void validate() {
        for (int i=0; i<array.length-1; i++)
          if (array[i] > array[i+1])
            throw new OutOfOrderArray();
      }

      //...

      public class OutOfOrderArray extends RuntimeException{
      }
    }

That concludes the perhiphery of the system.  Now we can start to go for the gold.  

One of the internal calculations that must be made for a binary search, is the calculation of the midpoint.  Given two positions in the array (`l` and `r`), this function must find the position that is the appropriate midpoint between them.  This is typically done with `floor((l+r)/2)`.

    @Test
    public void findsProperMidpoint() throws Exception {
      assertEquals(0, findMidpoint(0, 0));
      assertEquals(0, findMidpoint(0, 1));
      assertEquals(1, findMidpoint(0, 2));
      assertEquals(1, findMidpoint(0, 3));
      assertEquals(Integer.MAX_VALUE/2, findMidpoint(0,Integer.MAX_VALUE));
    }

----

    public static int findMidpoint(int l, int r) {
      return (l+r)>>1;
    }
    
This works so long as the sum `l+r` is `<` `Integer.MAX_VALUE`.  But for very large arrays that may not be the case.  So we should try those cases.

`Integer.MAX_VALUE` is the largest positive integer.  If you increment it you get a negative number.  So you have to be careful how you do the math.  The cases below explore that math.  Make sure you understand them.

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

The solution is to divide the two indexes by two _first_ and then add them together.  However, you also have to remember any possible carry from the low order bit, and add that in too.  Thus:

    public static int findMidpoint(int l, int r) {
      int carry = ((l&1)+(r&1))>>1;
      return (l>>1)+(r>>1)+carry;
    }

Now we can write some simply tests to check the search results.  

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

We can test these tests by writing a simple linear search.

  public boolean find(int element) {
    for (int i=0; i<array.length; i++) {
      if (array[i] == element)
        return true;
    }
    return false;
  }
  
And now, finally, we can write the test that forces us to implement the binary search.  The test counts the number of comparisons and ensures that number is always O(Log2 N) or less.  We use a simple spy to count the comparisons.

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
    
And, of course, the algorithm that passes these tests is:

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

The end result can be found [here](https://github.com/unclebob/BinarySearch).

