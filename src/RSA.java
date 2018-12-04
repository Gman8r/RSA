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
		// Archive arrays that always hold the last 2 values of r, u, and v
		long[] rArchive = {x, m};
		long[] uArchive = {1, 0};
		long[] vArchive = {0, 1};
		
		// Create an r variable and iterate down the algorithm until r is 1
		long r;
		do
		{
			r = rArchive[1] % rArchive[0];					// r = remainder of dividend of previous two r's
			long q = rArchive[1] / rArchive[0];				// d = dividend  of previous two r's (using integer division)
            long u = uArchive[1] - (uArchive[0] * q);		// u[n] = u[n-2] - (u[n-1] * q[n])
            u = trueMod(u, m);
            long v = vArchive[1] - (vArchive[0] * q);		// v[n] = v[n-2] - (v[n-1] * q[n])
            v = trueMod(v, m);

            // Update our archive arrays with new values
            updateArchive(uArchive, u);
            updateArchive(vArchive, v);
            updateArchive(rArchive, r);
		}
		while (r != 1);
		
		//Our last u value is the inverse
		return uArchive[0];
	}
	
	/**
	 * Helper function for modInverse to update our size 2 archive arrays
	 * @author Brian Intile
	 * @param archive
	 * @param newValue
	 */
	private static void updateArchive(long[] archive, long newValue)
	{
		// Shift the first value to the second position and add the new value in the first position
		archive[1] = archive[0];
		archive[0] = newValue;
	}
	
	/**
	 * TODO Raise b to the p power mod m
	 * @autho Brian Intile
	 * @param b
	 * @param p
	 * @param m
	 * @return The number b raised to the p power
	 */
	public static long modPower(long b, long p, long m)
	{
		// Calculate a max binary degree 2^n for us to calculate to
		int maxBinaryDegree = 0;
        while (Math.pow(2, maxBinaryDegree) <= p)
        {
            maxBinaryDegree++;
        }
        maxBinaryDegree--;
        // Store the resulting binary powers in an array
        long[] binaryPowers = binaryPowers(b, maxBinaryDegree, m);
        
        long result = 1;
        long currentPower = p;
        double iValue = Math.pow(2, maxBinaryDegree);	// iValue = 2^i (to save on re-calculating every time)
        
        // Iterate DOWN from maxBinaryDegree down to 0 to determine binary components for exponent 
        for (int i = maxBinaryDegree; i >= 0; i--)
        {
        	// Starting with most significant digit, check if our current number is greater than 2^i 
            if (currentPower >= iValue)
            {
            	// if so, subtract i by 1 and multiply the appropriate binary power into our result
                currentPower -= iValue;
                
                result *= binaryPowers[i];
                var moddedResult = result % m;
                result = moddedResult;
            }
            iValue /= 2;
        }
        return result;
	}
	
	/**
	 * Helper function for modPower which computes b^n for every power of 2 up to exp2Max
	 * @author Brian Intile
	 * @param b
	 * @param exp2Max
	 * @param m
	 * @return array of b^(2^n) for each n in the array
	 */
	private static long[] binaryPowers(long b, int exp2Max, long m)
	{
		// Initiate our return array, this will hold the value of b^(2^n) for each n in the array 
		long[] returnList = new long[exp2Max + 1];
		long currentValue = b;
		returnList[0] = b;	//b^(2^0) = b;
		
		
		// Square the current value and store it in the list 
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
	 * @return the true mod
	 */
	public static long trueMod(long x, long m)
	{
		// Use normal mod and add m if result is negative
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