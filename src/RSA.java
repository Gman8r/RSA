import java.util.*;	// Random number generator

/**
 * TODO Class Documentation
 */
public class RSA
{  

	//Bergmann's driver
	public static void main (String args[]) throws Exception
	{
		Person Alice = new Person();
		Person Bob = new Person();
		Person Alan = new Person();

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

		System.out.println ("Alice decodes and reads: " + Alice.decrypt (cipher) + "\n");
		
		System.out.println("Bob now wants to message Alan.\n");
		
		msg = new String("Alice asked me to lunch. I said no. Can you confirm I'm busy?");
		cipher = Bob.encryptTo(msg, Alan);
		
		System.out.println("Message is: " + msg);
		System.out.println("Bob sends:");
		show(cipher);
		
		System.out.println("Alice intercepts the message to Alan and tries to decrypt using her key.");
		try {
			//Alice is attempting to decrypt message to Alan using her private key; should cause an exception
			System.out.println ("Alice decodes and reads: " + Alice.decrypt (cipher));
		}
		catch (Exception e) {
			//Alice was unable to decrypt the message using her private key
			System.out.println("Alice was unable to decrypt the message!");
		}
		System.out.println ("Alan decodes and reads: " + Alan.decrypt (cipher) + "\n");
		
		msg = new String("Sure thing, Bob. If she asks I'll say we have plans.");
		cipher = Alan.encryptTo (msg, Bob);

		System.out.println ("Message is: " + msg);
		System.out.println ("Alan sends:");
		show (cipher);

		System.out.println ("Bob decodes and reads: " + Bob.decrypt (cipher) + "\n");
	}



	/**
	 * Calculates modular inverse using the Extended Euclidean Algorithm
	 * <br>x and m must be relatively prime to each other!
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
        long iValue = (long)Math.pow(2, maxBinaryDegree);	// iValue = 2^i (to save on re-calculating every time)
        
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
	 * <br>The max value for a, b, and m is now (long.MAX_VALUE / 2) instead of (sqrt(long.MAX_VALUE)) if we were to directly multiply
	 * <br>TODO stress-testing to figure out if this is worth it
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
			
			// Integer divide b by 2 
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
	 * Generates a random prime number in the range minValue to maxValue.
	 * 
	 * @author Justin Davis
	 * @param minValue the lower end of the range
	 * @param maxValue the upper end of the range
	 * @param rand a pseudorandom number generator
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
	 * @param n a long value
	 * @param rand a pseudorandom number generator
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
		//only need to check up to and including the square root of x
		double sqrt = Math.sqrt(x);
		for(int i = 2; prime && i <= sqrt; i++) {
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
			r = a%b; //calculates remainder
			a = b; //makes b the new a value
			b = r; //makes r the new b value
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
		//adds 0's to the bit String until it is a multiple of 8
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
		if(n > msg.length()) {
			//trying to convert more characters than the length of the message
			throw new IllegalArgumentException("n CANNOT be larger than the length of the message");
		}
		if(p+n > msg.length()) {
			//trying to characters past the end of the message
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
			converted += (1l << (i))*Long.parseLong(bits.substring(size-i-1, size-i));
		}
		return converted;
	}


}