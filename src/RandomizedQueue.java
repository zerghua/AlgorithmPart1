import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.princeton.cs.algs4.StdRandom;

/**
 * Created by Hua on 1/31/2017.

 Randomized queue.
 A randomized queue is similar to a stack or queue, except that the item removed is chosen uniformly at random
 from items in the data structure. Create a generic data type RandomizedQueue that implements the following API:

 public class RandomizedQueue<Item> implements Iterable<Item> {
     public RandomizedQueue()                 // construct an empty randomized queue
     public boolean isEmpty()                 // is the queue empty?
     public int size()                        // return the number of items on the queue
     public void enqueue(Item item)           // add the item
     public Item dequeue()                    // remove and return a random item
     public Item sample()                     // return (but do not remove) a random item
     public Iterator<Item> iterator()         // return an independent iterator over items in random order
     public static void main(String[] args)   // unit testing (optional)
 }

 Corner cases.
 The order of two or more iterators to the same randomized queue must be mutually independent;
 each iterator must maintain its own random order.
 Throw a java.lang.NullPointerException if the client attempts to add a null item;
 throw a java.util.NoSuchElementException if the client attempts to sample or dequeue an item from an empty randomized queue;
 throw a java.lang.UnsupportedOperationException if the client calls the remove() method in the iterator;
 throw a java.util.NoSuchElementException if the client calls the next() method in the iterator and there are no more items to return.

 Performance requirements.
 Your randomized queue implementation must support each randomized queue operation (besides creating an iterator)
 in constant amortized time. That is, any sequence of m randomized queue operations (starting from an empty queue)
 should take at most cm steps in the worst case, for some constant c.
 A randomized queue containing n items must use at most 48n + 192 bytes of memory.
 Additionally, your iterator implementation must support operations next() and hasNext() in constant worst-case time;
 and construction in linear time; you may (and will need to) use a linear amount of extra memory per iterator.


 uniform(int a, int b)
 Returns a random integer uniformly in [a, b).

 */

// 2 techniques when using array
// move last item to sampled out item between [first, last]
// resize array when is not enough
public class RandomizedQueue<Item> implements Iterable<Item> {
    private int n;
    private Item[] a;

    private void resize(int capacity){
        assert capacity >= n;  // capacity should larger than n, or data will be lost
        //System.out.printf("resized array from %s to %s, current size=%s\n", a.length, capacity, n);
        Item[] tmp = (Item[]) new Object[capacity];
        for(int i=0;i<n;i++) tmp[i] = a[i];
        a = tmp;

        // simple code
        // a = java.util.Arrays.copyOf(a, capacity);

    }

    // construct an empty randomized queue
    public RandomizedQueue(){
        int initSize = 2;
        a = (Item[]) new Object[initSize];
        n = 0;

    }

    // is the queue empty?
    public boolean isEmpty(){
        return n == 0;
    }

    // return the number of items on the queue
    public int size(){
        return n;
    }

    // add the item
    public void enqueue(Item item){
        if(item == null) throw new java.lang.NullPointerException("Error when enqueue null");
        if(n == a.length) resize(2*n);
        a[n++] = item;
    }

    // remove and return a random item
    // copy last one item to returned item
    public Item dequeue(){
        if(n <= 0) throw new java.util.NoSuchElementException("Error when dequeue from empty deque");
        int index = StdRandom.uniform(n);
        Item ret = a[index];
        a[index] = a[--n];
        a[n] = null;     // to avoid loitering
        if(n>0 && n == a.length/4) resize(a.length/2);
        return ret;
    }

    // return (but do not remove) a random item
    public Item sample(){
        if(n <= 0) throw new java.util.NoSuchElementException("Error when sample from empty deque");
        return a[StdRandom.uniform(n)];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator(){
        return new MyIterator();
    }

    private class MyIterator implements Iterator<Item>{
        private Item[] b;
        private int index;
        MyIterator(){
            b = (Item[]) new Object[n];
            for(int i=0;i<n;i++) b[i] = a[i];
            StdRandom.shuffle(b);
            index = 0;
        }

        public boolean hasNext(){return index<n;}
        public Item next(){
            if(!hasNext()) throw new NoSuchElementException();
            return b[index++];
        }
        public void remove(){throw new UnsupportedOperationException();}
    }

    // if first char is - means dequeue
    // if first char is = means sample
    // else enqueue
    private void unitTest(String in){
        System.out.println("\ninput = " + in);
        RandomizedQueue<String> q = new RandomizedQueue<>();
        String[] s = in.split(" ");
        for(String token : s){
            try {
                char firstChar = token.charAt(0);
                if (firstChar == '-') System.out.println("dequeue item is: " + q.dequeue());
                else if (firstChar == '=') System.out.println("sample item is: " + q.sample());
                else {
                    if(token.equals("null")) q.enqueue(null);
                    else q.enqueue(token);
                }

                if (q.isEmpty()) {
                    System.out.println("queue is empty");
                } else {
                    System.out.print("iterator in random order: ");
                    for (String e : q) System.out.print(e + " ");
                    System.out.println();
                }
            }catch(Exception e){
                System.out.println(e.toString());
            }
        }
    }

    private void unitTestIterator(){
        System.out.println("\ntest Iterator exception");
        RandomizedQueue<String> q = new RandomizedQueue<>();
        q.enqueue("20");
        Iterator<String> iter = q.iterator();
        try{
            System.out.println(iter.next());
            System.out.println(iter.next());
        }catch(Exception e){
            System.out.println(e.toString());
        }

    }

    private void runUnitTest(){
        unitTest("10 20 30 40 50 = = = = = - - - - -");
        unitTest("10 20 = = = = - - ");
        unitTest("10 - -");
        unitTest("10 - =");
        unitTest("null");
        unitTestIterator();
        unitTest("325 - 464 - 117");

    }

    // unit testing (optional)
    public static void main(String[] args){
        new RandomizedQueue<>().runUnitTest();
    }
}
