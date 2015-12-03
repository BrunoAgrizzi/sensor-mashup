package actors.messages;

public class CoapMessageReceived {

    private String message;

    public CoapMessageReceived(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
