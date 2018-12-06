
/**
 * Generate a public key for this person, consisting of exponent,e, and modulus, m.
 * Generate a private key, consisting of an exponent, d.
 * Provide access to the public key only.
 *
 * @author brian intile, justin davis, aaron alnutt
 * December, 2018
 */

import RSA.*;
import java.lang.*;

public class Person
{
	private final int BLOCK_SIZE = 2; //ARBITRARY, SUBJECT TO CHANGE (max is hypothetically 8)
	private final int MAX_PRIME_VAL = 1000000; //needs alot more testing
	private final int MIN_PRIME_VAL = 10000;
	private long publicKey;
	private long privateKey;
	private long publicMod;

	/**
	 * Access the public encryption exponent
	 * @return public encryption exponent
	 */
	public long getE()
	{
		return publicKey;
	}

	/**
	 * Access the public modulus
	 * @return public modulus
	 */
	public long getM()
	{
		return publicMod;
	}

	/**
	 *  Used to test public-key crypto-system, RSA
	 * @author aaron alnutt
	 */
	public Person()
	{
		//p and q will need to have their max values adjusted, this is temporary to keep it fast in testing
		long p = RSA.randomPrime(MIN_PRIME_VAL, MAX_PRIME_VAL);
		long q = RSA.randomPrime(MIN_PRIME_VAL, MAX_PRIME_VAL);

		while(p == q) //just in case
		{
			q = RSA.randomPrime(MIN_PRIME_VAL, MAX_PRIME_VAL);
		}

		publicMod = p * q;   //TODO: need to add overflow check when dealing with larger primes
		publicKey = RSA.randomRelativePrime(publicMod);

		long privateHelper = (p-1) * (q-1);

		privateKey = RSA.modInverse(publicKey, privateHelper);    // d = e-1 (mod (p-1)*(q-1))
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

		int blockArrLength = msg.length() / BLOCK_SIZE;	//arr size

		long block = 0;		//used for storing the current block
		long[] blockArr = new long[blockArrLength];		//return arr

		int ndx = 0;		//keeping track of actual msg index
		int blockNDX = 0;	//keeping track of blockArr index

		while(ndx < msg.length()) {
			block = RSA.toLong(msg, ndx, BLOCK_SIZE);		//use RSA class's toLong conversion
			blockArr[blockNdx++] = block;
			ndx += BLOCK_SIZE;		//increment by our block size
		}

		return blockArr;

		//OLD
		//char first, second;

		//for(int i = 0; i < msg.length; i+=2)
		//{
		//	first = msg.charAt(i);
		//	second = msg.charAt(i + 1);

		//}
	}

	/**
	 * Decrypt the cipher text
	 * @param message array of long ints to be decrypted
	 * @return plain text of the encrypted message
	 * @author aaron alnutt
	 */
	public String decrypt(long[] message)
	{
		long currentBlock = 0;
		long plainBlock = 0;
		StringBuffer buff = new StringBuffer();
		for(int i = 0; i < message.length(); i++)
		{
			currentBlock = message[i];
			plainBlock = RSA.modPower(currentBlock, privateKey, publicMod);
			buff.append(RSA.longToNChars(plainBlock));
		}

		return buff.toString();
	}

}
