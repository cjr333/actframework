package websocket;

import act.Act;
import act.ws.WebSocketContext;
import org.osgl.mvc.annotation.GetAction;
import org.osgl.mvc.annotation.WsAction;
import org.osgl.util.S;

public class ChatApp {
    @GetAction
    public void home() {
    }

    @WsAction("msg")
    public void onMessage(String message, WebSocketContext context) {
        // suppress blank lines
        if (S.notBlank(message)) {
            context.sendToPeers(message);
        }
    }

    public static void main(String[] args) throws Exception {
        Act.start("chat room");
    }
}
