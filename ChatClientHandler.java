import java.net.*;
import java.io.*;
import java.util.*;


public class ChatClientHandler extends Thread{
    private Socket socket; 
    private BufferedReader in;
    private BufferedWriter out;
    List clients;
    String name;
    
    public ChatClientHandler(Socket socket, List clients){
        this.socket = socket;
        this.clients = clients;
        this.name = "undefined" + (clients.size() + 1);
    }
    
    public String getClientName(){
        return name;
    }
    
    public void run(){
        try{
            open();
            while(true){
                String message = receive();
                String[] commands = message.split(" ");
                if(commands[0].equalsIgnoreCase("post")){
                    post(commands[1]);
                }
                else if(commands[0].equalsIgnoreCase("users")){
                    users();
                }
                else if(commands[0].equalsIgnoreCase("help")){
                    help();
                }
                else if(commands[0].equalsIgnoreCase("name")){
                    name(commands[1]);
                }
                else if(commands[0].equalsIgnoreCase("whoami")){
                    whoami();
                }
                else if(commands[0].equalsIgnoreCase("bye")){
                    bye();
                }
                if(message.equals("")) break;
                send(message);
            }
        } catch(IOException e){
            e.printStackTrace();
        } finally{
            close();
        }
    }
    //postコマンド
    public void post(String message) throws IOException{
        List names = new ArrayList();
        for(int i = 0; i < clients.size(); i++){
            ChatClientHandler handler = (ChatClientHandler)clients.get(i);
            if(handler != this){
                names.add(handler.getClientName());
                handler.send("[" + this.getClientName() + "]" + message);
            }
        }
        Collections.sort(names);
        String returnMessage = "";
        for(int i = 0; i < names.size(); i++){
            returnMessage = returnMessage + names.get(i)+ ",";
        }
        this.send(returnMessage);
    }
    
    //userコマンド
    public void users() throws IOException{
        List names = new ArrayList();
        for(int i = 0; i < clients.size(); i++){
            ChatClientHandler handler = (ChatClientHandler)clients.get(i);
            names.add(handler.getClientName());
        }
        Collections.sort(names);
        String returnMessage = "";
        for(int i = 0; i < names.size(); i++){
            returnMessage = returnMessage + names.get(i)+ ",";
        }
        send("現在サーバに接続しているユーザは以下の通りです");
        this.send(returnMessage);
    }
    
    //helpコマンド
    public void help() throws IOException{
        send("命令一覧");
        send("help, name, whoami, bye, post, users");
    }
    
    //nameコマンド
    public void name(String message) throws IOException{
         List names = new ArrayList();
        for(int i = 0; i < clients.size(); i++){
            ChatClientHandler handler = (ChatClientHandler)clients.get(i);
            if(handler.getClientName().equals(message)){
                send("名前が同一");
            }
        }
        this.name=message;
    }

    //whoamiコマンド
    public void whoami() throws IOException{
        send(getClientName());
    }

    //byeコマンド
    public void bye() throws IOException{
        send("サーバへの接続を終了");
        
        if(clients != null){
            for(int i=0; i < clients.size(); i++){
                ChatClientHandler handler = (ChatClientHandler)clients.get(i);
                if(handler.equals(this)) clients.remove(i);
            }
            close();
        }
    }
    
    //クライアントとやり取りを行なうストリームを開く
    public void open() throws IOException{
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        
    }
    
    //受け取る
    public String receive() throws IOException{
        String line = in.readLine();
        System.out.println(line);
        return line;
    }
    
    //データを送る
    public void send(String message) throws IOException{
        out.write(message);
        out.write("\r\n");
        out.flush();
    }
    
    //ソケットを閉じる
    public void close(){
        if(in != null){
            try{
                in.close();
            } catch(IOException e){ }
        }
        if(out != null){
            try{
                out.close();
            } catch(IOException e){ }
        }
        if(socket != null){
            try{
                socket.close();
            } catch(IOException e){ }
        }
    }
}
