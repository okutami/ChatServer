import java.net.*;
import java.io.*;
import java.util.*;


public class ChatServer{
    private ServerSocket server;
    private List clients = new ArrayList();
    
    public void listen(){
        try{
            server = new ServerSocket(18080);
            System.out.println("Chatサーバをポート18080で起動しました。");
            
            while(true){
                Socket socket = server.accept();
                ChatClientHandler handler = new ChatClientHandler(socket, clients);
                
                clients.add(handler);
                System.out.println("クライアントが接続してきました。");
                handler.start();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args){
        ChatServer chat = new ChatServer();
        chat.listen();
    }
}
    