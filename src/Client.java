//Logan Jakuboski
//12517110
//Date: 11/15/2019
//ChatRoom Version 1

import java.net.*;
import java.io.*;

//Class for the Client side
public class Client{

    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException{
        //Get the localhost IP address
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        String name = "";

        while(!name.equals("exit")){
            socket = new Socket(host, 17110);
            //write to socket using ObjectOutputStream
            oos = new ObjectOutputStream(socket.getOutputStream());
            //Buffer Reader for reading user input
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            name = reader.readLine();
            oos.writeObject(name);
            //read the server response message
            ois = new ObjectInputStream(socket.getInputStream());
            //Prints out message from server
            String message = (String) ois.readObject();
            System.out.println(message);
            //Closes Input and Output Streams
            ois.close();
            oos.close();
        }
    }


}
