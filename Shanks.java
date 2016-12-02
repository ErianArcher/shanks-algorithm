/* Implementation of Shanks' algorithm 
 * Reference: Cryptography Theory and Practice Third Edition Algorithm 6.1
 * @author Junpeng Liang
 * Date: 2016/12/1
 * Version: 0.1
 */

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

public class Shanks {

    public static void main(String[] args) {
	System.out.println(findDisLog(808, 3, 525));
	System.out.println(findDisLog(24690, 106, 12375));
	System.out.println(findDisLog(458008, 6, 248388));	
    }
    private static class Pair {
	private int key;
	private int value;
	private static Comparator<Pair> cmp = new Comparator<Pair>() {
		public int compare(Pair p1, Pair p2) {
		    return p1.getValue() - p2.getValue();
		}
	    };
	
	public Pair() {
	    key = 0;
	    value = 0;
	}
	public Pair(int key, int value) {
	    this.key = key;
	    this.value = value;
	}

	public int getKey() {
	    return key;
	}
	public int getValue() {
	    return value;
	}
	@Override
	public boolean equals(Object p) {
	    Pair other = (Pair) p;
	    return (this.key == other.key && this.value == other.value);
	}
	@Override
	public String toString() {
	    return new String("(" + key + ", " + value + ")");
	}
	public static Comparator<Pair> getComparator() {
	    return cmp;
	}
    }

    /**
     * binSearch is using binary search to search target in the list
     * @return the index in pairList of identical value pair which does not has the same key as the original one;<br>
     * if it cannot find an identical value then return -1.
     * @param pairList is the sorted arraylist for searching
     * @param tarPair is the target pair whose value is for searching in target list
     */
    public static int binSearch(ArrayList<Pair> pairList, Pair tarPair) {
	int head = 0, tail = pairList.size()-1;
	int tarValue = tarPair.getValue();

	while (head < tail) {
	    int middle = (head + tail) / 2;
	    Pair tmp = pairList.get(middle);
	    int tmpValue = tmp.getValue();
	    
	    if (tmpValue == tarValue) {
		return middle;
	    } else if (tmpValue > tarValue) {
		// Target is between head and middle
		tail = middle;
	    } else {
		// Target is between middle and tail
		head = middle + 1; // In case that head == tail - 1
	    }
	}
	
	return -1;
    }

    /**
     * Modular Exponentiation
     * @return an integer
     * @param base
     * @param pow
     * @param m is the modul
     */
    public static int modExponent(int base, int pow, int m) {
	char[] revPow = (new String((new StringBuffer(Integer.toBinaryString(pow))).reverse())).toCharArray();
	/*debug
	for (char c : revPow) {
	    System.out.print(c);
	}
	System.out.println();
	*/

	int x = 1;
	int power = base % m;

	for (int i = 0; i < revPow.length; i++) {
	    if (revPow[i] == '1') {
		x = (x*power) % m;
	    }
	    power = (power * power) % m;
	}
	
	return x;
    }

    /**
     * Multiplicative Inverse
     * @return an integer which is the inverse of b mod a
     * @param a is the modulo
     * @param b is the interger mod a
     * b^-1 mod a
     */
    public static int mulInv(int a, int b) {
	int a0 = a;
	int b0 = b;
	int t0 = 0, t = 1;
	int q = a0 / b0;
	int r = a0 - q*b0;

	while (r > 0) {
	    // temp <- (t0 - qt) mod a
	    int temp = t0 - q*t;
	    while (temp < 0) {
		temp += a;
	    }
	    temp %= a;
	    
	    t0 = t;
	    t = temp;

	    a0 = b0;
	    b0 = r;
	    q = a0 / b0;
	    r = a0 - q*b0;
	}

	if (b0 != 1) {
	    //System.out.println("The inverse of b mod a does not exist");	    
	    return 0;
	}
	return t;
    }
    
    /**
     * To find the discrete logarithm of beta
     * @return a integer which is the discrete logarithm of beta
     * @param n
     * @param alpha
     * @param beta
     */
    public static int findDisLog(int n, int alpha, int beta) {
	int m = (int)Math.ceil((double)Math.sqrt((double)n));
	ArrayList<Pair> jList = new ArrayList<>();
	ArrayList<Pair> iList = new ArrayList<>();

	int mid = modExponent(alpha, m, n+1);
	for (int j = 0; j < m-1; j++) {
	    int value = modExponent(mid, j, n+1);
	    jList.add(new Pair(j, value));
	}
	Collections.sort(jList, Pair.getComparator());
	
	for (int i = 0; i < m-1; i++) {
	    int value = (beta * mulInv(n+1, modExponent(alpha, i, n+1))) % (n+1);
	    iList.add(new Pair(i, value));
	}
	Collections.sort(iList, Pair.getComparator());

	for (Pair p : jList.toArray(new Pair[jList.size()])) {
		int index = binSearch(iList, p);
		if (index != -1) {
		    Pair jtar = p;
		    Pair itar = iList.get(index);
		    return (m*jtar.getKey() + itar.getKey()) % n;
		}
	}
	/*debug
	System.out.print("j");
	printList(jList);

	System.out.print("i");
	printList(iList);
	*/
	return -1;//cannot find identical value.
    }

    private static void printList(ArrayList<Pair> list) {
	System.out.print("List: ");
	
	for (Pair p : list.toArray(new Pair[list.size()])) {
	    System.out.print("+ " + p.toString());
	    
	}
	System.out.println();
	
    }
}
