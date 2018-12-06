package RSA;
/**
 * TODO Class Documentation
 */
public class Person
{
	private final int BLOCK_SIZE = 3; //ARBITRARY, SUBJECT TO CHANGE (max is hypothetically 8)
	
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
	 * TODO Create person and generate public/private keys from random primes p and q 
	 */
	public Person()
	{
		
	}
	
	/**
	 * TODO encrypt a message using the recipient's public key (use BLOCK_SIZE)
	 * @param msg plain text to be encrypted
	 * @param recipient person to encrypt message to
	 * @return encrypted long array
	 */
	public long[] encryptTo(String msg, Person recipient)
	{
		long recipE = recipient.getE();
		long recipM = recipient.getM();


		return null;
	}
	
	/**
	 * TODO decrypt a message with this person's private key (use BLOCK_SIZE)
	 * @param message
	 * @return
	 */
	public String decrypt(long[] message)
	{
		return null;
	}
}
