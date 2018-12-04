import java.io.*;
import java.math.*;
import java.util.*;	// Random number generator

/**
 * TODO Class Documentation
 */
public class RSA
{  
	
	//Bergmann's driver
	public static void main (String args[])
	{ 	
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
	 * Helper function for modPower which computes b^n for every power of 2 up to exp2Max
	 * @author Brian Intile
	 * @param b
	 * @param exp2Max
	 * @param m
	 * @return
	 */
	private static long[] binaryPowers(long b, int exp2Max, long m)
	{
		//Initiate our return array, this will hold the value of b^(2^n) for each n in the array 
		long[] returnList = new long[exp2Max + 1];
		long currentValue = b;
		returnList[0] = b;	//b^(2^0) = b;
		
		//Square the current value and store it in the list 
		for(int i = 1; i <= exp2Max; i ++)
		{
			currentValue *= currentValue; //TODO Overflow handling???
			currentValue %= m;
			returnList[i] = currentValue;
		}
		return returnList;
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
	 * Helper method to return the proper mod of a number (no negatives
	 * @author Brian Intile
	 * @param x
	 * @param m
	 * @return
	 */
	public static long trueMod(long x, long m)
	{
		//Use normal mod and add m if result is negative
		x = x % m; 
		return x < 0 ? x + m : x;
	}
	
	/**
	 * TODO return a random prime number between min and max
	 * @param minValue
	 * @param maxValue
	 * @return
	 */
	public static long randomPrime(long minValue, long maxValue)
	{
		return 0;
	}
	
	/**
	 * TODO return a random number relatively prime to n, less than n
	 * @param n
	 * @return
	 */
	public static long randomRelativePrime(long n)
	{
		return 0;
	}
	
	/**
	 * TODO return whether x is prime
	 * @param x
	 * @return
	 */
	private static boolean isPrime(long x)
	{
		return false;
	}
	
	/**
	 * TPDP return whether x is relatively prime to n
	 * @param x
	 * @param n
	 * @return
	 */
	private static boolean isRelativelyPrime(long x, long n)
	{
		return false;
	}
	
	/**
	 * TODO use standard out to display the data as a sequence of numbers
	 * @param data
	 */
	public static void show(long[] data)
	{
		
	}	
}