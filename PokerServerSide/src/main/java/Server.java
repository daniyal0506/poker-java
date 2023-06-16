import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

public class Server {

    int count = 1;
    ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
    TheServer server;
    private Consumer<Serializable> callback;

    // port number to "listen" to
    int portNumberField;

    Server(Consumer<Serializable> call, String portNumber) {
        callback = call;
        server = new TheServer();
        server.start();
        portNumberField = Integer.parseInt(portNumber);
    }

    public class TheServer extends Thread {

        public void run() {

            try (ServerSocket mysocket = new ServerSocket(portNumberField);) {

                while (true) {
                    // check if the size of the server is at capacity
                    if (clients.size() < 4) {
                        ClientThread c = new ClientThread(mysocket.accept(), count);
                        callback.accept("Client #" + count + " has connected to the server");
                        clients.add(c);
                        c.start();
                        count++;
                    } else {
                        // if at capacity, don't let client connect
                        Socket socket = mysocket.accept();
                        socket.close();
                    }
                }

            } catch (Exception e) {
                callback.accept("Server socket did not launch");
            }

        }

    }

    public void closeServer(){
        for(ClientThread c : clients){
            c.interrupt();
        }
        server.interrupt();
    }


    class ClientThread extends Thread {

        // used to send data to server
        Socket connection;
        int count;
        ObjectInputStream in;
        ObjectOutputStream out;
        PokerInfo pkServer;

        // helper class for calculations
        ServerInstructions serverInstructions = new ServerInstructions();

        ClientThread(Socket s, int count) {
            this.connection = s;
            this.count = count;
        }

        public void run() {

            try {
                in = new ObjectInputStream(connection.getInputStream());
                out = new ObjectOutputStream(connection.getOutputStream());
                connection.setTcpNoDelay(true);
            } catch (Exception e) {
                System.out.println("Streams not open");
            }

            while (true) {

                try {
                    // read in PokerInfo data from client
                    pkServer = (PokerInfo) in.readObject();

                    // if this is first time sending data (deck hasn't been sent)
                    if (pkServer.shuffledDeck.isEmpty() && pkServer.playerCards.isEmpty() && pkServer.dealersCards.isEmpty()) {

                        callback.accept("Client " + count + " has just made a wager of $" + pkServer.anteWagerNumber + " and $" + pkServer.pairPlusWagerNumber);

                        // update PokerInfo with shuffled deck
                        pkServer.shuffledDeck = ServerInstructions.shuffleDeck();

                        // set 3 cards each for player and dealer
                        pkServer.playerCards = ServerInstructions.getThreeCards(pkServer.shuffledDeck);
                        pkServer.dealersCards = ServerInstructions.getThreeCards(pkServer.shuffledDeck);

                    } else if (pkServer.playerRank == -1 && pkServer.dealerRank == -1) {

                        // do calculations to see each hand of player
                        pkServer.validDealerHand = ServerInstructions.validateDealerHand(pkServer.dealersCards);

                        pkServer.playerRank = serverInstructions.evaluateHandRank(pkServer.playerCards);
                        pkServer.dealerRank = serverInstructions.evaluateHandRank(pkServer.dealersCards);
                        pkServer.foldLosses = serverInstructions.foldLosses(pkServer);

                        // if the dealer hand is valid and playable...
                        if (pkServer.validDealerHand) {

                            // if the plauer didn't fold, the play wager is equal to the ante wager
                            if (!pkServer.playerFold) {
                                pkServer.playWagerNumber = pkServer.anteWagerNumber;
                            } else {
                                // if the player folded, no play wager was made
                                callback.accept("Client " + count + " has folded");
                                pkServer.playWagerNumber = 0;
                            }

                            // calculate winning hand, and the winnings for pair plus
                            pkServer.winningHand = ServerInstructions.checkWinningHand(pkServer.playerRank, pkServer.dealerRank);
                            pkServer.pairPlusWinnings = serverInstructions.calculatePairPlusWinnings(pkServer.pairPlusWagerNumber, pkServer.playerCards);

                            // update server based on the results of the game
                            if (!pkServer.winningHand.equals("Draw")) {
                                pkServer.anteWagerWinnings = serverInstructions.calculateWinnings(pkServer);
                                if (Objects.equals(pkServer.winningHand, "Player")) {
                                    callback.accept("Client " + count + " beat the dealer");
                                } else {
                                    callback.accept("Client " + count + " lost to the dealer");
                                }
                            } else {
                                if(!pkServer.playerFold){
                                    callback.accept("Client " + count + " had a draw");
                                }
                                pkServer.anteWagerWinnings = 0;
                            }

                            // winnings are printed differently based on if the user folded or not
                            if(!pkServer.playerFold){
                                callback.accept("Client " + count + " total winnings were: $" + pkServer.anteWagerWinnings + " and $" + pkServer.pairPlusWinnings);
                            } else {
                                callback.accept("Client " + count + " total winnings were: $" + pkServer.foldLosses);
                            }

                        } else {
                            // condition where dealer hand wasn't valid but user still folded
                            if(pkServer.playerFold){
                                callback.accept("Client " + count + " has folded");
                                callback.accept("Client " + count + " total winnings were: $" + pkServer.foldLosses);
                            } else {
                                callback.accept("Client " + count + " didn't have a valid dealer hand. The ante bet was returned");
                            }
                        }

                    }

                    out.reset();
                    out.writeObject(pkServer);
                } catch (Exception e) {
                    // remove client after disconnecting
                    callback.accept("Client #" + count + " has disconnected");
                    clients.remove(this);
                    break;
                }

            }
        }
    }

}






