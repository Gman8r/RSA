
/**
 * Generate a public key for this person, consisting of exponent,e, and modulus, m.
 * Generate a private key, consisting of an exponent, d.
 * Provide access to the public key only.
 *
 * @author brian intile, justin davis, aaron alnutt
 * @version December 2018
 */

import java.util.Random;

public class Person
{
	// Edit these two values to switch modes
	public static final Mode MODE = Mode.SIZE_MODE;	// Change this to change encryption mode. SIZE_MODE allows larger block sizes and saves space, while SPEED_MODE runs faster
	public static final int BLOCK_SIZE = 7; // IMPORTANT: Max possible block size is 7 in SIZE_MODE, 3 in SPEED_MODE
	
	public enum Mode
	{
		SPEED_MODE,
		SIZE_MODE
	}
	
	// Min/max values for p and q: These are calculated appropriately and don't need to be changed when changing modes.
	// Both values are the square root of the min/max sizes for m 
	private final long MIN_PRIME_VAL = (long)Math.ceil(Math.sqrt(Math.pow(256, BLOCK_SIZE))); // m has to be greater than any possible block value (256 ^ BLOCK_SIZE)
	
	private final long MAX_PRIME_VAL = (MODE == Mode.SIZE_MODE) // Ternary operation: Max p and q depends on our current mode
			? (long)Math.floor(Math.sqrt(Long.MAX_VALUE / 2)) // SIZE_MODE: m can go up to Long.MAX/2
			: (long)Math.floor(Math.sqrt(Math.sqrt(Long.MAX_VALUE))); // SIZE_MODE: m can go up to sqrt(Long.MAX) 
	
	private long publicKey;
	private long privateKey;
	private long publicMod;

	/**
	 * Access the public encryption exponent
	 * @author aaron alnutt
	 * @return public encryption exponent
	 */
	public long getE()
	{
		return publicKey;
	}

	/**
	 * Access the public modulus
	 * @author aaron alnutt
	 * @return public modulus
	 */
	public long getM()
	{
		return publicMod;
	}

	/**
	 * Used to test public-key crypto-system, RSA
	 * @author aaron alnutt
	 */
	public Person()
	{
            boolean degenerate = true;  //initially assume keys are degenerate
            long p = 0;
            long q = 0;
            while(degenerate)   //should only have to run once
            {
                Random rand = new Random();
                p = RSA.randomPrime(MIN_PRIME_VAL, MAX_PRIME_VAL, rand);
                q = RSA.randomPrime(MIN_PRIME_VAL, MAX_PRIME_VAL, rand);
                while(p == q) //just in case
                {
                        q = RSA.randomPrime(MIN_PRIME_VAL, MAX_PRIME_VAL, rand);
                }

                publicMod = p * q;   
                publicKey = RSA.randomRelativePrime((p - 1) * (q - 1), rand);
               
                if(!isKeyDegenerate(p, q))
                {
                    degenerate = false; //they aren't degenerate, break out
                }
            }
            long privateHelper = (p-1) * (q-1);
                
            privateKey = RSA.modInverse(publicKey, privateHelper);    // d = e-1 (mod (p-1)*(q-1))
        }
	
	/**
	 * Determines if a key is degenerate based on its p and q
	 * @param p first prime
	 * @param q second prime
	 * @return whether the key is degenerate
	 * @author aaron alnutt
	 */
	private boolean isKeyDegenerate(long p, long q)
	{
		long p1 = p - 1;
		long q1 = q - 1;
		long lcm = (p1 * q1) / (RSA.gcd(p1, q1)); // least common multiple is the product divided by the gcd
		return (lcm % (publicKey - 1)) == 0;	// return if their lcm divides into m-1
	}

	/**
	 * Encrypt a plain text message to recipient.
	 * @param msg plain text to be encrypted
	 * @param recipient Person to encrypt message to
	 * @return encrypted long integer array
	 * @author aaron alnutt
	 */
	public long[] encryptTo(String msg, Person recipient)
	{
		long recipE = recipient.getE();     //recips exp
		long recipM = recipient.getM();		//recips mod
                msg += "1";
		int blockArrLength = msg.length() / BLOCK_SIZE;	//arr size
                if(msg.length() % BLOCK_SIZE > 0)
                {
                    blockArrLength++;
                }

		long block = 0;		//used for storing the current block
		long[] blockArr = new long[blockArrLength];		//return arr

		int ndx = 0;		//keeping track of actual msg index
		int blockNDX = 0;	//keeping track of blockArr index
                
                //adding spaces to serve as padding for now
                
                while (msg.length() % BLOCK_SIZE > 0)
                {
                    msg += "0";
                }
                
		while(ndx < msg.length()) {
                        if(ndx + BLOCK_SIZE > msg.length())
                        {
                            block = RSA.toLong(msg, ndx, ndx + BLOCK_SIZE - msg.length());
                        }
                        else{
                            block = RSA.toLong(msg, ndx, BLOCK_SIZE);
                        }
					//use RSA class's toLong conversion
                        block = RSA.modPower(block, recipE, recipM);
                        //System.out.print(block + ", ");
			blockArr[blockNDX++] = block;
			ndx += BLOCK_SIZE;		//increment by our block size
		}

		return blockArr;
	}

	/**
	 * Decrypt the cipher text
	 * @param message array of long ints to be decrypted
	 * @return plain text of the encrypted message
	 * @author aaron alnutt
	 */
	public String decrypt(long[] message) throws InvalidDecryptionException
	{
		long currentBlock = 0;
		long plainBlock = 0;
		StringBuffer buff = new StringBuffer();
		for(int i = 0; i < message.length; i++)
		{
			//if(message[i] != null) {
				currentBlock = message[i];
				plainBlock = RSA.modPower(currentBlock, privateKey, publicMod);
                                //System.out.print(plainBlock + ", ");
				buff.append(RSA.longToNChars(plainBlock));
			//}
		}
                String paddedString = buff.toString();
                if(paddedString.charAt(paddedString.length() - 1) == '0')
                {
                    while(paddedString.charAt(paddedString.length() - 1) == '0')
                    {
                         paddedString = paddedString.substring(0, paddedString.length() - 1);
                    }
                    paddedString = paddedString.substring(0, paddedString.length() - 1);
                }
                else if(paddedString.charAt(paddedString.length() - 1) == '1')
                {
                    paddedString = paddedString.substring(0, paddedString.length() - 1);
                }
                else{ //there was no 0 or 1 so don't try to remove padding
                    throw new InvalidDecryptionException("Attempted decryption is not in the correct format: " + buff.toString());
                }
                
		return paddedString;
	}

}
