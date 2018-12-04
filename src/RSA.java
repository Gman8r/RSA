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
	 * Calculates modular inverse using the Extended Euclidian Algorithm
	 * @author Brian Intile
	 * @param x The number to find the inverse of 
	 * @param m	The mod to work in
	 * @return The inverse of x mod m
	 */
	public static long modInverse(long x, long m) 
	{
		// Create archive arrays that will hold the previous 2 values of r, u, and v
		long[] rArchive = {x, m};
		long[] uArchive = {1, 0};
		long[] vArchive = {0, 1};
		
		// Create an r variable and iterate down the algorithm until r is 1
		long r;
		do
		{
			r = rArchive[1] % rArchive[0];					// r = remainder of dividend of previous two r's

			long q = rArchive[1] / rArchive[0];				// d = dividend  of previous two r's (using integer division)
			
			
            long u = uArchive[1] -							// u[n] = u[n-2] - (u[n-1] * q[n])
            		modMult(uArchive[0], q, m);		
            u = trueMod(u, m);	

            long v = vArchive[1] -				            // v[n] = v[n-2] - (v[n-1] * q[n])
            		modMult(vArchive[0], q, m);
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
	 * @param archive The archive array
	 * @param newValue The new value to be inserted
	 */
	private static void updateArchive(long[] archive, long newValue)
	{
		// Shift the first value to the second position and add the new value in the first position
		archive[1] = archive[0];
		archive[0] = newValue;
	}
	
	/**
	 * Raise b to the p power mod m
	 * @author Brian Intile
	 * @param b The base
	 * @param p The exponent to raise the base to
	 * @param m	The mod to work in
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
                
                result = modMult(result, binaryPowers[i], m); // Use modMult to calculate without overflow
            }
            iValue /= 2;
        }
        return result;
	}
	
	/**
	 * Helper function for modPower which computes b^n for every power of 2 up to exp2Max
	 * @author Brian Intile
	 * @param b The base
	 * @param exp2Max The max n to calculate x = 2^n with
	 * @param m	The mod to work in
	 * @return Array of b^(2^n) for each n in the array
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
			currentValue = modMult(currentValue, currentValue, m); // Use modMult to square without overflow
			returnList[i] = currentValue;
		}
		return returnList;
	}
	
	/**
	 * Algorithm that performs modular multiplication and mitigates overflow
	 * The max value for a, b, and m is now (long.MAX_VALUE / 2) instead of (sqrt(long.MAX_VALUE)) if we were to directly multiply  
	 * @author Brian Intile
	 * @param a The first number to multiply (mod m)
	 * @param b The second number to multiply (mod m)
	 * @param m The mod to work in
	 * @return The product mod m
	 */
	public static long modMult(long a, long b, long m)
	{
		long result = 0;
		while(b > 0)
		{
			// If b is odd, add a to result
			if (b % 2 == 1)
				result = (result + a) % m;
			
			// Multiply a by 2
			a = (a * 2) % m;
			
			// Divide b by 2 
			b /= 2;
		}
		return result;
	}
	
	/**
	 * Helper method to return the proper mod of a number (no negatives)
	 * @author Brian Intile
	 * @param x The number to apply mod to
	 * @param m	The mod to work in
	 * @return The true mod of x
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