package engine;

import java.io.InputStream;
import java.io.IOException;

public class ErrorEventHandler implements EventHandler {
    @Override
    public String getHandler(){
        return "0001";
    }
    @Override
    public void handleEvent(InputStream inputStream) {
        System.out.println("ErrorEventHandler: Error message received");
        try {
            byte[] bytes = new byte[1024];
            inputStream.read(bytes);
            //..... your biz logic
        } catch (IOException e) { }
    }
}
