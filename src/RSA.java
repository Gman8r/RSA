import java.io.*;
import java.math.*;
import java.util.*;	// Random number generator

/**
 * TODO Class Documentation
 */
public class RSA
{  

	private static final int BLOCK_SIZE = 3; //ARBITRARY, SUBJECT TO CHANGE (max is hypothetically 8)

	//Bergmann's driver
	public static void main (String args[])
	{ 	/**
		Person Alice = new Person();
		Person Bob = new Person();

		String msg = new String ("Bob, let's have lunch."); 	// message to be sent to Bob
		long []  cipher;
		cipher =  Alice.encryptTo(msg, Bob);			// encrypted, with Bob's public key

		System.out.println ("Message is: " + msg);
		System.out.println ("Alice sends:");
		show (cipher);

		System.out.println ("Bob decodes and reads: " + Bob.decrypt (cipher));	// decrypted,
									// with Bob's private key.
		System.out.println ();

		msg = new String ("No thanks, I'm busy");
		cipher = Bob.encryptTo (msg, Alice);

		System.out.println ("Message is: " + msg);
		System.out.println ("Bob sends:");
		show (cipher);

		System.out.println ("Alice decodes and reads: " + Alice.decrypt (cipher));
		**/
	}



	/**
	 * TODO (https://dotnetfiddle.net/3nmJrF) use euclidian algorithm to find inv(x) mod m
	 * @param x
	 * @param m
	 * @return
	 */
	public static long modInverse(long x, long m) 
	{
		return 0;
	}

	/**
	 * TODO (https://dotnetfiddle.net/3nmJrF) raise b to the p power mod m
	 * @param b
	 * @param p
	 * @param m
	 * @return
	 */
	public static long modPower(long b, long p, long m)
	{
		return 0;
	}

	/**
	 * TODO (https://dotnetfiddle.net/3nmJrF) helper function for modPower which computes b^n for every power of 2 up to exp2Max
	 * @param b
	 * @param exp2Max
	 * @param m
	 * @return
	 */
	private static long[] binaryPowers(long b, long exp2Max, long m)
	{
		return null;
	}

	/**
	 * TODO helper method for multiplying several numbers together, modding them after each multiple
	 * @param factors 
	 * @param m
	 * @return
	 */
	private static long modMult(long[] factors, long m)
	{
		return 0;
	}

	/**
	 * TODO helper method to return the proper mod of a number (no negatives
	 * @param x
	 * @param m
	 * @return
	 */
	public static long trueMod(long x, long m)
	{
		return 0;
	}

	/**
	 * Generates a random prime number in the range minValue to maxValue.
	 * 
	 * @author Justin Davis
	 * @param minValue the lower end of the range
	 * @param maxValue the upper end of the range
	 * @return a random prime between minValue and MaxValue
	 */
	public static long randomPrime(long minValue, long maxValue, Random rand)
	{
		//first calculates a random double; value in the range [0,1.0)
		//then multiplies it by the range of values for our prime
		long randRelPrime = 0;
		do {
			randRelPrime = (long) (minValue + (rand.nextDouble()*(maxValue-minValue)));
		}
		while(!isPrime(randRelPrime)); //will repeat while the random prime generated is not prime
		return randRelPrime;
	}

	/**
	 * Generates a random long relatively prime to n; it is also less than n
	 * 
	 * @author Justin Davis
	 * @param a long value
	 * @return a random relatively prime number less than n
	 */
	public static long randomRelativePrime(long n, Random rand)
	{
		//first calculates a random double; value in the range [0,1.0)
		//then multiplies it by the value of n
		long randRelPrime = 0;
		do {
			randRelPrime = (long) (rand.nextDouble()*n);
		}
		while(!isRelativelyPrime(n, randRelPrime)); //will repeat while the random relative prime generated is not relatively prime to n
		return randRelPrime;
	}

	/**
	 * Determines whether a given long is prime or not
	 * 
	 * @author Justin Davis
	 * @param a long value
	 * @return whether or not a long is prime
	 */
	private static boolean isPrime(long x)
	{
		boolean prime = true;
		//i initially starts at 2 since any number divided by 1 will have a remainder of zero
		//only checking up to x/2 because anything larger would result in a value less than 2 (some fractional number)
		for(int i = 2; prime && i <= x/2; i++) {
			if(x%i == 0) { //if the long is divisible by the current value of i, it is not prime
				prime = false;
			}
		}
		return prime;
	}

	/**
	 * Determine whether or not two longs are relative prime.
	 * This means their gcd is 1.
	 * This method uses the Euclidean Algorithm to determine the gcd.
	 * 
	 * @author Justin Davis
	 * @param x one long
	 * @param n another long
	 * @return whether or not the two longs are relatively prime
	 */
	private static boolean isRelativelyPrime(long x, long n)
	{
		long a = x, b = n, r = 2; //r initially set to 2 so it can enter the loop
		if(x < n) { //n is larger; swap the values to perform gcd
			a = n;
			b = x;
		}
		while(r != 0) { //the reaminder still is not equal to zero
			r = a%b;
			a = b;
			b = r;
		}
		return (a == 1) ? true : false; //true if gcd is 1, or false if it is not
	}

	/**
	 * Use standard out to display the data as a sequence of numbers
	 * 
	 * @author Justin Davis
	 * @param data an array of long values
	 */
	public static void show(long[] data)
	{
		int size = data.length;
		String allData = "";
		for(int i = 0; i < size; i++) {
			//adds each long value in the array to the String and separates them by a comma
			allData += data[i] + ", ";
		}
		System.out.println(allData.substring(0, allData.length()-2));
	}	

	/**
	 * Convert a long to n chars, where n is the block size of the message
	 * 
	 * @author Justin Davis
	 * @param x the long that will be converted to a String of chars
	 * @return The string made up of n numeric digits representing x
	 */
	public static String longToNChars(long x) {
		String bits = addPadding(Long.toBinaryString(x)); //pads zeros to the binary String until it is a multiple of 8
		int numChars = bits.length()/8; //how many chars (bytes) we will have in the String
		String chars = "";
		//for each byte, we will find the integer value it represents
		for(int i = 0; i < numChars; i++) {
			int currByte = 0;
			//for each byte, we will determine the int value of its twos complement representation
			//starts at the least significant bit
			//uses i to determine which byte we are looking at
			for(int j = 0; j < 8; j++) {
				currByte +=  (1 << (j))*Integer.parseInt(bits.substring((i*8) + 8-(j+1),(i*8) + 8-j));
			}
			//adds the current char to the String
			chars += Character.toString((char) currByte); 
		}
		return chars;
	}

	/**
	 * Adds zeros to the front of a binary String until it has 8 bits (a byte)
	 * 
	 * @author Justin Davis
	 * @param bitString String that needs to be padded
	 * @return a binary String padded with zeros
	 */
	private static String addPadding(String bitString) {
		while(bitString.length()%8 != 0) {
			bitString = '0' + bitString;
		}
		return bitString;
	}
	
	/**
	 * Converts a specified number of numeric chars, n, to a long
	 * 
	 * @author Justin Davis
	 * @param msg String containing chars that need to be converted
	 * @param p position where we will start converting chars
	 * @param n number of characters we will convert
	 * @return the two digit number beginning at position p of msg as a long
	 */
	public static long toLong(String msg, int p, int n) {
		if(n > BLOCK_SIZE) {
			throw new IllegalArgumentException("n CANNOT be larger than max block size of " + BLOCK_SIZE);
		}
		if(n > msg.length()) {
			throw new IllegalArgumentException("n CANNOT be larger than the length of the message");
		}
		if(p+n > msg.length()) {
			throw new IllegalArgumentException("this n will extend past the length of the String");
		}
		String bits = "";
		//goes through the String and adds the byte of each char - this creates a String of bits
		//each successive byte gets added to the end
		//adds padding to the front of the binary number if it does not contain 8 bits
		for(int i = 0; i < n; i++) {
			bits += addPadding(Integer.toBinaryString(msg.charAt(p+i)));
		}
		long converted = 0; //variable storing value of converted long
		int size = bits.length();
		//goes through list of bits starting at least significant
		//multiplies each bit by the 2's complement value according to its position
		for(int i = 0; i < size; i++) {
			converted += (1 << (i))*Long.parseLong(bits.substring(size-i-1, size-i));
		}
		return converted;
	}


}