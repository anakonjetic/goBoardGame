package hr.tvz.konjetic.goboardgame.chat;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class ChatServiceImplementation implements ChatService{

    private List<String> chatMessagesHistory = new ArrayList<>();

    @Override
    public void sendChatMessage(String message) throws RemoteException {
        chatMessagesHistory.add(message);
    }

    @Override
    public List<String> returnChatHistory() throws RemoteException {
        return chatMessagesHistory;
    }
}
