package hr.tvz.konjetic.goboardgame.utils;

import hr.tvz.konjetic.goboardgame.chat.ChatService;
import javafx.scene.control.TextArea;

import java.rmi.RemoteException;
import java.util.List;

public class ChatUtils {

    public static void refreshChatTextArea(ChatService stub, TextArea chatTextArea){
        List<String> chatHistory;
        try {
            chatHistory = stub.returnChatHistory();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        StringBuilder sb = new StringBuilder();

        for (String s : chatHistory) {
            sb.append(s);
            sb.append("\n");
        }

        chatTextArea.setText(sb.toString());
    }}
