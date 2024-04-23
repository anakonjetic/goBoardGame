package hr.tvz.konjetic.goboardgame.chat;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ChatServer {

    public static final int RMI_PORT = 1099;
    private static final int RANDOM_PORT_HINT = 0;



        public static void main(String[] args) {
            try {
                Registry registry = LocateRegistry.createRegistry(RMI_PORT);
                ChatService chatService = new ChatServiceImplementation();
                ChatService skeleton = (ChatService) UnicastRemoteObject.exportObject(chatService, RANDOM_PORT_HINT);
                registry.rebind(ChatService.REMOTE_OBJECT_NAME, skeleton);
                System.err.println("Chat service ready");
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

    }


