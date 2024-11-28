package github.grif.responsibility;

public interface Handler {
	void setNext(Handler next);

	boolean handle(String request);
}
