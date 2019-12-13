//Logan Jakuboski
//12517110
//Date: 11/15/2019
//ChatRoom Version 1

import java.net.*;
import java.io.*;
import java.lang.ClassNotFoundException;
import java.lang.String;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

//Class for the Server Side
public class Server {

    public static void main(String args[]) throws IOException, ClassNotFoundException {

        //Variables for the connection
        int port = 17110;
        ServerSocket server = new ServerSocket(port);

        //Variables for loggedIn state and for username
        boolean loggedIn = false;
        String user = "";

        //Infinite loop for server side
        while(true)
        {

            //Creates socket and waits for client connection
            Socket socket = server.accept();
            //Reads from socket to ObjectInputStream object
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            //Converts ObjectInputStream object to String
            String message = (String) ois.readObject();
            //Splits message into array by spaces
            String[] messageArray = message.split(" ");
            //ObjectOutputStream for sending back to client
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

            //Switch statement for all user input cases
            switch(messageArray[0])
            {
                //Login case
                case "login":
                    //Checks parameters for username and password
                    if(messageArray.length != 3)
                    {
                        oos.writeObject("Server: Invalid Arguments! Please include UserID and Password");
                        break;

                    }
                    else if(loggedIn) //Must logout before logging in
                    {
                        oos.writeObject("Server: Please Logout first.");

                    }
                    else if(checkUserAndPass(messageArray[1], messageArray[2])) //Successful case
                    {
                        user = messageArray[1];
                        oos.writeObject("Server:" + user + " joins");
                        loggedIn = true;
                    }
                    else //Invalid username or password
                    {
                        oos.writeObject("Server: Invalid UserID or Password");

                    }
                    break;

                //Case for New User
                case "newuser":
                    //Checks for username and password
                    if(messageArray.length != 3)
                    {
                        oos.writeObject("Server: Invalid Arguments! Please include UserID and Password");
                        break;
                    }
                    else if(!validateUsername(messageArray[1])) //Username credentials
                    {
                        oos.writeObject("Server: Username must be less than 32 characters.");
                        break;
                    }
                    else if(!validatePassword(messageArray[2])) //Password credentials
                    {
                        oos.writeObject("Server: Password must be between 4 and 8 characters.");
                        break;
                    }
                    else if(loggedIn)
                    {
                        oos.writeObject("Please Logout First.");
                    }
                    else   //Passes all tests
                    {
                        if(addUser(messageArray[1], messageArray[2])) //Attempts to add user
                        {
                            oos.writeObject("Server: User was created.");
                        }
                        else //User already existed
                        {
                            oos.writeObject("Server: Denied. User already exists.");
                        }
                    }
                    break;

                //Case for send
                case "send":
                    if(loggedIn) //Checks loggedIn state
                    {
                        //Creates new string with user element
                        String[] newMessage = Arrays.copyOfRange(messageArray, 1, messageArray.length);
                        String userMessage = user + ":" + String.join(" ", newMessage);
                        oos.writeObject(userMessage);
                    }
                    else {
                        oos.writeObject("Server: Denied. Please login first");
                    }
                    break;

                //Case for logout
                case "logout":
                    if(loggedIn) //Checks loggedIn state
                    {
                        loggedIn = false;
                        oos.writeObject("Server:" + user + " left.");
                        break;
                    }
                    else {
                        oos.writeObject("Server: Not logged in.");
                    }
                    break;

                //Catches any invalid commands
                default:
                    oos.writeObject("Server: Invalid command! Please enter a valid command.");
            }

            //close resources
            ois.close();
            oos.close();
            socket.close();
            //terminate the server if client sends exit request
            if(message.equalsIgnoreCase("exit")) break;
        }

            server.close();
    }

    //Method for Checking Username and Password
    private static boolean checkUserAndPass(String userID, String password) throws IOException {

        //Creates a file and buffer for text file
        File file = new File("src/users.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String string;

        //While loop to check each line of text file
        while ((string = br.readLine()) != null) {
            String[] check = string.split(",");
            if(userID.equals(check[0]) && password.equals(check[1])){
                return true;    //returns true on success
            }
        }

        return false; //returns false for failure
    }

    //Method for adding a new user
    private static boolean addUser(String userID, String password) throws IOException{

        //Creates a file and buffer
        File file = new File("src/users.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String string;

        //While loop to check if user already exists
        while ((string = br.readLine()) != null) {
            String[] check = string.split(",");
            if(userID.equals(check[0])){
                return false;
            }
        }

        //Writes new username and password to text file
        String lineToWrite = "\n" + userID + "," + password;
        Files.write(Paths.get(String.valueOf(file)), lineToWrite.getBytes(), StandardOpenOption.APPEND);
        return true;

    }

    //Validates Username
    private static boolean validateUsername(String username) throws IOException {
        return username.length() < 32;
    }

    //Validates Password
    private static boolean validatePassword(String password) throws IOException {
        return (password.length() >= 4 && password.length() <= 8);
    }


}

