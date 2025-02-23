package hr.tvz.konjetic.goboardgame.chat;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ChatService extends Remote {

    String REMOTE_OBJECT_NAME = "hr.tvz.rmi.service";

    void sendChatMessage(String message) throws RemoteException;

    List<String> returnChatHistory() throws RemoteException;

}
