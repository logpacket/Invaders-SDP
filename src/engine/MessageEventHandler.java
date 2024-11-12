package engine;

import java.io.InputStream;
import java.io.IOException;

public class MessageEventHandler implements EventHandler {
    @Override
    public String getHandler() {
        return "002";
    };
    @Override
    public void handleEvent(InputStream inputStream) {
        System.out.println("MessageEventHandler: Received message ");
        try {
            byte[] bytes = new byte[1024];
            inputStream.read(bytes);
        } catch (IOException e) { }
    }
}
