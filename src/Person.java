
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
        
	private final int BLOCK_SIZE = 4; //ARBITRARY, SUBJECT TO CHANGE (max is hypothetically 8)
	private final long MAX_PRIME_VAL =  (long)Math.sqrt(Long.MAX_VALUE / 2);
	private final long MIN_PRIME_VAL = (long)Math.ceil((long)Math.sqrt(MAX_PRIME_VAL));
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
               
                if(lcm((p - 1), (q - 1)) % (publicKey - 1) != 0)
                {
                    degenerate = false; //they arent degenerate, break out
                }
            }
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
        
        private long lcm(long x, long y)
        {
            return (x * y) / (gcd(x, y));
        }
        
        private long gcd(long x, long y)
        {
            if(y == 0)
            {
                return x; 
            }
            return gcd(y, x  % y);
        }

	/**
	 * Decrypt the cipher text
	 * @param message array of long ints to be decrypted
	 * @return plain text of the encrypted message
	 * @author aaron alnutt
	 */
	public String decrypt(long[] message) throws Exception
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
                    throw new Exception("given cipher is not in the correct format");
                }
                
		return paddedString;
	}

}
