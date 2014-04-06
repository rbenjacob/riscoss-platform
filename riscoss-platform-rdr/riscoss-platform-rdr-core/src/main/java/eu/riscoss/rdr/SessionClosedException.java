package eu.riscoss.rdr;

public class SessionClosedException extends Exception
{
    private static final long serialVersionUID = -3986410784579641147L;

    public SessionClosedException(String message)
    {
        super(message);
    }
}
