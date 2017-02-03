package Assignment2_DequeAndRandomizedQueue;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by Hua on 1/31/2017.

 ASSESSMENT SUMMARY Compilation: PASSED (0 errors, 3 warnings)
 API: PASSED
 Findbugs: FAILED (4 warnings)
 Checkstyle: FAILED (122 warnings)
 Correctness: 43/43 tests passed
 Memory: 54/53 tests passed
 Timing: 110/110 tests passed
 Aggregate score: 100.19% [Compilation: 5%, API: 5%, Findbugs: 0%, Checkstyle: 0%, Correctness: 60%, Memory: 10%, Timing: 20%]


 Dequeue. A double-ended queue or deque (pronounced "deck") is a generalization of a stack and a queue that supports
 adding and removing items from either the front or the back of the data structure. Create a generic data type Assignment2_DequeAndRandomizedQueue.Deque
 that implements the following API:

 public class Assignment2_DequeAndRandomizedQueue.Deque<Item> implements Iterable<Item> {
     public Assignment2_DequeAndRandomizedQueue.Deque()                           // construct an empty deque
     public boolean isEmpty()                 // is the deque empty?
     public int size()                        // return the number of items on the deque
     public void addFirst(Item item)          // add the item to the front
     public void addLast(Item item)           // add the item to the end
     public Item removeFirst()                // remove and return the item from the front
     public Item removeLast()                 // remove and return the item from the end
     public Iterator<Item> iterator()         // return an iterator over items in order from front to end
     public static void main(String[] args)   // unit testing (optional)
 }



 Corner cases.
 Throw a java.lang.NullPointerException if the client attempts to add a null item;
 throw a java.util.NoSuchElementException if the client attempts to remove an item from an empty deque;
 throw a java.lang.UnsupportedOperationException if the client calls the remove() method in the iterator;
 throw a java.util.NoSuchElementException if the client calls the next() method in the iterator and
 there are no more items to return.

 Performance requirements.
 Your deque implementation must support each deque operation in constant worst-case time.
 A deque containing n items must use at most 48n + 192 bytes of memory. and use space proportional to
 the number of items currently in the deque. Additionally, your iterator implementation must support
 each operation (including construction) in constant worst-case time.

 */

// using double-linked list
public class Deque<Item> implements Iterable<Item> {
    private class Node {
        private Node next, prev;
        private Item item;
        private Node(Item i){
            item = i;
            next = null;
            prev = null;
        }
    }

    private int n;
    private Node first, last;

    // construct an empty deque
    public Deque(){
        n = 0;
        first = last = null;
    }

    // is the deque empty?
    public boolean isEmpty(){
        return n == 0;
    }

    // return the number of items on the deque
    public int size(){
        return n;
    }

    // add the item to the front
    public void addFirst(Item item){
        if(item == null) throw new java.lang.NullPointerException("Error when adding null to first");
        if(n == 0){
            first = new Node(item);
            last = first;
        }else{
            Node oldFirst = first;
            first = new Node(item);
            first.next = oldFirst;
            oldFirst.prev = first;
        }
        n++;
    }

    // add the item to the end
    public void addLast(Item item){
        if(item == null) throw new java.lang.NullPointerException("Error when adding null to last");
        if(n == 0){
            first = new Node(item);
            last = first;
        }else{
            Node oldLast = last;
            last = new Node(item);
            oldLast.next = last;
            last.prev = oldLast;
        }
        n++;
    }

    // remove and return the item from the front
    public Item removeFirst(){
        if(isEmpty()) throw new java.util.NoSuchElementException("Error when removing the first item from an empty deque");
        Item ret = first.item;
        first = first.next;
        if(n == 1) last = first;
        else first.prev = null;
        n--;
        return ret;
    }

    // remove and return the item from the end
    public Item removeLast(){
        if(isEmpty()) throw new java.util.NoSuchElementException("Error when removing the last item from an empty deque");
        Item ret = last.item;
        last = last.prev;
        if(n == 1) first = last;
        else last.next = null;
        n--;
        return ret;
    }


    // return an iterator over items in order from front to end
    public Iterator<Item> iterator(){
        return new MyIterator();
    }

    private class MyIterator implements Iterator<Item>{
        private Node cur = first;
        public boolean hasNext(){return cur != null;}
        public Item next(){
            if(!hasNext()) throw new NoSuchElementException();
            Item ret = cur.item;
            cur = cur.next;
            return ret;
        }
        public void remove() { throw new UnsupportedOperationException();  }
    }



    // first char is either +/-, meaning add or remove
    // second char is either F/L, meaning first or last
    // print from first to last
    private void unitTest(String in){
        System.out.println("\ninput = " + in);
        Deque<String> d = new Deque<>();
        String[] s = in.split(" ");
        for(String token: s){
            try {
                if (token.charAt(0) == '+') {
                    String e = token.substring(2);
                    if (token.charAt(1) == 'F') {
                        if(e.equals("null"))d.addFirst(null);
                        else d.addFirst(e);
                    }
                    else {
                        if(e.equals("null"))d.addLast(null);
                        d.addLast(e);
                    }
                } else {
                    if (token.charAt(1) == 'F') d.removeFirst();
                    else d.removeLast();
                }
                if (d.isEmpty()) System.out.print("deque is empty");
                else for (String e : d) System.out.print(e + " ");
                System.out.println();
            }catch (Exception exp){
                System.out.println(exp.toString());
            }
        }
    }


    private void runUnitTest(){
        unitTest("+F10 +L20 +F30 +L40 -F -F -L -L");
        unitTest("+F10 -L");
        unitTest("+F10 +F20 +F30 +L40 +L50");
        unitTest("+F10 -L +F20 -F +F30 -L +L40 -F +L50 +F60");

        // below should throw exceptions
        unitTest("-L");
        unitTest("-F");
        unitTest("+F10 -L -L");
        unitTest("+Fnull");
        unitTest("+Lnull");

    }

    // unit testing (optional)
    public static void main(String[] args){
        new Deque<>().runUnitTest();
    }
}

