
/**
 * TODO Class Documentation
 */
public class Person
{
	private final int BLOCK_SIZE = 3; //ARBITRARY, SUBJECT TO CHANGE (max is hypothetically 8)
	
	private long publicKey;		// e
	private long privateKey;	// d
	private long publicMod;		// m
	private long privateMod;	// n
	private int p;
	private int q;
	
	public long getPublicKey()
	{
		return publicKey;
	}
	
	public long getpublicMod()
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
	 * @param msg
	 * @param recipient
	 * @return
	 */
	public long[] encryptTo(String msg, Person recipient)
	{
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
