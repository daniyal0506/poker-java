import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

public class Client extends Thread{

    // handle connections between server and client
    Socket socketClient;
    ObjectOutputStream out;
    ObjectInputStream in;

    // store ip address and port number of server
    String ipAddressField;
    int portNumberField;

    private Consumer<Serializable> callback;
    private Consumer<Serializable> callback2;

    // constructor that starts connection
    Client(Consumer<Serializable> call,Consumer<Serializable> call2,  String ipAddress, String portNumber ){
        callback = call;
        callback2 = call2;
        ipAddressField = ipAddress;
        portNumberField = Integer.parseInt(portNumber);
    }

    public void run() {

        try {
            socketClient= new Socket(ipAddressField,portNumberField);
            out = new ObjectOutputStream(socketClient.getOutputStream());
            in = new ObjectInputStream(socketClient.getInputStream());
            socketClient.setTcpNoDelay(true);

            if(socketClient.isConnected()){
                callback2.accept("valid");
            }
        }
        catch(Exception e) {
            callback2.accept("invalid");
        }


        while(true) {

            try {
                // receiving data from the server
                PokerInfo data = (PokerInfo) in.readObject();
                callback.accept(data);
            }
            catch(Exception e) {}
        }

    }

    // function to send PokerInfo data back to the server
    public void sendPokerInfoData(PokerInfo data) {
        try {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
