package cn.sharkmc.demo;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    public static WebSocketController instance;

    public final SimpMessagingTemplate messagingTemplate;

    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        instance = this;
    }

    @MessageMapping("/send")
    public void sendMessageToClient(String message) {
        // Send the message to the specified topic
        messagingTemplate.convertAndSend("/topic/messages", message);
    }

    @SendTo("/topic/messages")
    public String sendMessage(String message) {
        return message;
    }

}
