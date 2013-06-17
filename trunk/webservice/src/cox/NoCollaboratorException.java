package cox;

public class NoCollaboratorException extends Exception{
	public NoCollaboratorException()  {}
	public NoCollaboratorException(String message) {
		super(message);
	}
}