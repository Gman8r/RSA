public class InvalidDecryptionException extends Exception
{
	private static final long serialVersionUID = 1L;
	
    public InvalidDecryptionException(String errorMessage)
    {
        super(errorMessage);
    } 
}