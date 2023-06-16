import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Application;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientGUI extends Application {

    // animation delay
    PauseTransition pause = new PauseTransition(Duration.seconds(0.55));

    // used to send information back to the server
    Client clientConnection = null;
    PokerInfo pKInfo;

    // update player & dealer cards
    ArrayList<ImageView> playerCards;
    ArrayList<ImageView> dealerCards;

    BorderPane playScreen;
    TextField playWagerTextField;

    ListView<String> gameInfoList;
    int totalWinningsLabel = 0;

    // used for colors in scene
    String primaryColor = "#0099ff";
    String secondaryColor = "#ffba00";
    String accentColor = "#00008b";

    // load fonts
    InputStream is = getClass().getResourceAsStream("/fonts/Chainwhacks-vm72E.ttf");
    Font titleFont = Font.loadFont(is, 50);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // create new PokerInfo object
        pKInfo = new PokerInfo();
        gameInfoList = new ListView<>();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

        primaryStage.setTitle("3 Card Poker");

        // start at home screen
        primaryStage.setScene(createHomeGui(primaryStage));
        primaryStage.show();

    }

    private void changeStyles(VBox leftVBox, Button confirmWagers, Label gameInfoLabel, Label dealersCardsLabel, Label playersCardsLabel) {

        titleFont = new Font("Avenir Next", 24);

        leftVBox.setStyle("-fx-background-color: purple" + ";-fx-padding: 8px 16px;");
        playScreen.setStyle("-fx-background-color: pink;");
        confirmWagers.setStyle("-fx-background-color: yellow" + "; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8px 16px; -fx-background-radius: 8px;");
        gameInfoLabel.setFont(titleFont);
        dealersCardsLabel.setFont(titleFont);
        playersCardsLabel.setFont(titleFont);

    }

    public Scene createHomeGui(Stage primaryStage) {

        // if there was an error in connecting, an alert is thrown
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Connection Failed");

        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Menu");
        MenuItem exitMenuItem = new MenuItem("Exit");
        menu.getItems().add(exitMenuItem);
        menuBar.getMenus().add(menu);

        Label title = new Label("3 Card Poker");
        title.setFont(titleFont);

        // text field to store user input
        TextField ipField = new TextField();
        ipField.setPromptText("IP Address");
        TextField portField = new TextField();
        portField.setPromptText("Port Number");

        Button connectButton = new Button("Connect");
        connectButton.setStyle("-fx-background-color: " + accentColor + "; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8px 16px; -fx-background-radius: 8px;");

        connectButton.setOnAction(event -> {

            // establish connection to server
            try {

                clientConnection = new Client(data -> {
                    Platform.runLater(() -> {

                        // receive PokerInfo data from server
                        pKInfo = (PokerInfo) data;

                        // first time receiving data from server...
                        if(pKInfo.playerRank == -1 && pKInfo.dealerRank == -1){

                            updatePlayerCards(playerCards, pKInfo.playerCards,primaryStage);
                        }

                    });
                },data2 -> {Platform.runLater(() -> {
                    if(data2 == "valid"){
                        primaryStage.setScene(createPlayScreenGui(primaryStage.getWidth(),primaryStage.getHeight(),primaryStage));
                    }  else {
                        alert.setContentText("Could not make a valid connection. Server may be at maximum capacity");
                        alert.showAndWait();
                    }
                });}, ipField.getText(), portField.getText());

                clientConnection.start();

            } catch (Exception e) {

                alert.showAndWait();

            }

        });

        HBox titleBox = new HBox(title);
        titleBox.setAlignment(Pos.CENTER);
        HBox ipBox = new HBox(ipField);
        ipBox.setAlignment(Pos.CENTER);
        HBox portBox = new HBox(portField);
        portBox.setAlignment(Pos.CENTER);
        HBox connectBox = new HBox(connectButton);
        connectBox.setAlignment(Pos.CENTER);

        VBox vbox = new VBox(20, titleBox, ipBox, portBox, connectBox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(40));
        vbox.setMaxSize(500,500);
        vbox.setStyle("-fx-background-color: " + secondaryColor + ";-fx-padding: 8px 16px; -fx-background-radius: 8px;");

        BorderPane playScreen = new BorderPane();
        playScreen.setStyle("-fx-background-color: " + primaryColor + ";");
        playScreen.setTop(menuBar);
        playScreen.setCenter(vbox);

        return new Scene(playScreen, 800, 600);
    }

    public Scene createFoldLostScene(double width, double height, Stage primaryStage) {

        Label lostLabel = new Label();;
        Label amountLabel;

        // checks to see if the player folded or lost...
        if(pKInfo.playerFold){
            lostLabel.setText("You folded!");
            // prints the amount loss based on if the user folded.
            totalWinningsLabel += pKInfo.foldLosses;
            amountLabel = new Label("$" + pKInfo.foldLosses);
        } else {
            // pair plus could still win even if dealer beats player
            int amount = pKInfo.anteWagerWinnings + pKInfo.pairPlusWinnings;
            totalWinningsLabel += amount;
            amountLabel = new Label("$" + amount);
            lostLabel.setText("You lost!");
        }
        lostLabel.setFont(titleFont);
        lostLabel.setTextFill(Color.RED);

        amountLabel.setFont(titleFont);
        amountLabel.setTextFill(Color.RED);

        // play again allows user to start a new turn
        Button playAgainButton = new Button("Play Again");
        playAgainButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8px 16px; -fx-background-radius: 8px;");

        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8px 16px; -fx-background-radius: 8px;");

        playAgainButton.setOnAction(event -> {
            resetPokerInfo();
            primaryStage.setScene(createPlayScreenGui(primaryStage.getWidth(),primaryStage.getHeight(),primaryStage));
        });
        exitButton.setOnAction(event -> {
            primaryStage.close();
        });


        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(lostLabel, amountLabel, playAgainButton, exitButton);
        vbox.setStyle("-fx-background-color: " + secondaryColor + ";-fx-padding: 8px 16px; -fx-background-radius: 8px;");

        return new Scene(vbox,width,height);
    }

    public Scene createWinScene(double width, double height, Stage primaryStage) {

        Label lostLabel = new Label("You win!");
        lostLabel.setFont(titleFont);
        lostLabel.setTextFill(Color.GREEN);

        // calculates winnings based on pair plus and ante wager winnings
        int totalWinnings = pKInfo.pairPlusWinnings + pKInfo.anteWagerWinnings;
        totalWinningsLabel += totalWinnings;

        Label amountLabel = new Label("$" + totalWinnings);
        amountLabel.setFont(titleFont);
        amountLabel.setTextFill(Color.GREEN);

        Button playAgainButton = new Button("Play Again");
        playAgainButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8px 16px; -fx-background-radius: 8px;");
        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8px 16px; -fx-background-radius: 8px;");

        playAgainButton.setOnAction(event -> {
            resetPokerInfo();
            primaryStage.setScene(createPlayScreenGui(primaryStage.getWidth(),primaryStage.getHeight(),primaryStage));
        });
        exitButton.setOnAction(event -> {
            primaryStage.close();
        });


        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(lostLabel, amountLabel, playAgainButton, exitButton);
        vbox.setStyle("-fx-background-color: " + secondaryColor + ";-fx-padding: 8px 16px; -fx-background-radius: 8px;");

        return new Scene(vbox,width,height);
    }

    public Scene createDrawScreen(double width, double height, Stage primaryStage) {

        Label lostLabel = new Label("You drawed!");
        lostLabel.setFont(titleFont);
        lostLabel.setTextFill(Color.GREEN);

        // calculates winnings for pair plus. Player can win even if they draw the dealer
        Label amountLabel = new Label("Pair Plus Wager Results $: " + pKInfo.pairPlusWinnings);
        totalWinningsLabel += pKInfo.pairPlusWinnings;
        amountLabel.setFont(titleFont);
        amountLabel.setTextFill(Color.BLUE);

        // checks to see if the user won or lost the pair plus wager
        if(pKInfo.pairPlusWinnings > 0){
            gameInfoList.getItems().add("Player has won the pair plus wager");
        } else {
            gameInfoList.getItems().add("Player has lost the pair plus wager");
        }

        // calculates money that was returned to user after draw
        int moneyReturned = pKInfo.anteWagerNumber + pKInfo.playWagerNumber;
        Label moneyReturnedLabel = new Label("Ante & Play Wager Returned $: " + moneyReturned);
        moneyReturnedLabel.setFont(titleFont);
        moneyReturnedLabel.setTextFill(Color.BLUE);

        Button playAgainButton = new Button("Play Again");
        playAgainButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8px 16px; -fx-background-radius: 8px;");
        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8px 16px; -fx-background-radius: 8px;");

        playAgainButton.setOnAction(event -> {
            resetPokerInfo();
            primaryStage.setScene(createPlayScreenGui(primaryStage.getWidth(),primaryStage.getHeight(),primaryStage));
        });
        exitButton.setOnAction(event -> {
            primaryStage.close();
        });


        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(lostLabel,amountLabel,moneyReturnedLabel,playAgainButton, exitButton);
        vbox.setStyle("-fx-background-color: " + secondaryColor + ";-fx-padding: 8px 16px; -fx-background-radius: 8px;");

        return new Scene(vbox,width,height);
    }

    public Scene createPlayScreenGui(double width, double length, Stage primaryStage) {

        // menu bar at the top of the play screen
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Option");
        MenuItem freshStartMenuItem = new MenuItem("Fresh Start");
        MenuItem newLookMenuItem = new MenuItem("New Look");
        MenuItem exitMenuItem = new MenuItem("Exit");

        // restarts the game and resets data to default values
        freshStartMenuItem.setOnAction(e ->{
            resetPokerInfo();
            totalWinningsLabel = 0;
            primaryStage.setScene(createPlayScreenGui(primaryStage.getWidth(),primaryStage.getHeight(),primaryStage));
        });

        exitMenuItem.setOnAction((ActionEvent event) -> primaryStage.close());

        menu.getItems().addAll(freshStartMenuItem, newLookMenuItem, exitMenuItem);
        menuBar.getMenus().add(menu);

        // stores the player and dealer cards so they can be "flipped" later
        playerCards = new ArrayList<>();
        dealerCards = new ArrayList<>();

        Label gameInfoLabel = new Label("Game Info");
        gameInfoLabel.setFont(titleFont);

        // text field for user to input their wagers
        TextField anteWageTextField = new TextField();
        anteWageTextField.setText(pKInfo.anteWagerNumber != -1 ? String.valueOf(pKInfo.anteWagerNumber) : "");
        anteWageTextField.setPromptText("$ Ante Wage");
        TextField pairPlusTextField = new TextField();
        pairPlusTextField.setPromptText("$ Pair Plus");
        playWagerTextField = new TextField();
        playWagerTextField.setPromptText("$ Play Wager");
        playWagerTextField.setDisable(true);

        HBox wagerBox = new HBox(anteWageTextField, pairPlusTextField, playWagerTextField);
        wagerBox.setAlignment(Pos.CENTER);
        wagerBox.setSpacing(10);
        wagerBox.setStyle("-fx-background-color: " + secondaryColor + "; -fx-background-radius: 10;");

        Button confirmWagers = new Button("Confirm Wager");
        confirmWagers.setStyle("-fx-background-color: " + accentColor + "; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8px 16px; -fx-background-radius: 8px;");

        confirmWagers.setOnAction(event -> {

            gameInfoList.getItems().add("Player has confirmed wager");

            // validates user selection before sending to server
            if (PokerGameLogic.updatePokerInfoWager(pairPlusTextField.getText(), anteWageTextField.getText()
                    , pKInfo)) {

                pairPlusTextField.setDisable(true);
                anteWageTextField.setDisable(true);

                // sends poker info to server
                clientConnection.sendPokerInfoData(pKInfo);
                confirmWagers.setDisable(true);

            } else {

                // alert is thrown if user selections are invalid
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Wager Making Failed");
                alert.setContentText("Could not make a valid wager. Ante wager & Pair Plus Wager should be between $5 - $25. You must pick an ante wager.");
                alert.showAndWait();

            }
        });

        // displays the total winnings of the user
        TextField totalWinningsTextField = new TextField();
        totalWinningsTextField.setEditable(false);
        totalWinningsTextField.setStyle("-fx-text-fill: green; -fx-font-weight: bold");
        totalWinningsTextField.setText("Total Winnings $:" + totalWinningsLabel);
        totalWinningsTextField.setAlignment(Pos.CENTER);

        VBox leftVBox = new VBox(gameInfoLabel, gameInfoList, wagerBox, confirmWagers, totalWinningsTextField);
        leftVBox.setSpacing(10);
        leftVBox.setPadding(new Insets(20));
        leftVBox.setAlignment(Pos.CENTER);

        HBox dealerCardsBox = new HBox();
        dealerCardsBox.setSpacing(10);
        dealerCardsBox.setAlignment(Pos.CENTER);
        ImageView dealerCard1 = new ImageView(new Image("card_back.png"));
        dealerCard1.setFitWidth(125);
        dealerCard1.setFitHeight(175);
        ImageView dealerCard2 = new ImageView(new Image("card_back.png"));
        dealerCard2.setFitWidth(125);
        dealerCard2.setFitHeight(175);
        ImageView dealerCard3 = new ImageView(new Image("card_back.png"));
        dealerCard3.setFitWidth(125);
        dealerCard3.setFitHeight(175);
        dealerCardsBox.getChildren().addAll(dealerCard1, dealerCard2, dealerCard3);


        HBox playerCardsBox = new HBox();
        playerCardsBox.setSpacing(10);
        playerCardsBox.setAlignment(Pos.CENTER);
        ImageView playerCard1 = new ImageView(new Image("card_back.png"));
        playerCard1.setFitWidth(125);
        playerCard1.setFitHeight(175);
        ImageView playerCard2 = new ImageView(new Image("card_back.png"));
        playerCard2.setFitWidth(125);
        playerCard2.setFitHeight(175);
        ImageView playerCard3 = new ImageView(new Image("card_back.png"));
        playerCard3.setFitWidth(125);
        playerCard3.setFitHeight(175);
        playerCardsBox.getChildren().addAll(playerCard1, playerCard2, playerCard3);

        // stores the player & dealer cards so they can be flipped later
        playerCards.add(playerCard1);
        playerCards.add(playerCard2);
        playerCards.add(playerCard3);
        dealerCards.add(dealerCard1);
        dealerCards.add(dealerCard2);
        dealerCards.add(dealerCard3);

        Label dealersCardsLabel = new Label("Dealer's Cards:");
        dealersCardsLabel.setAlignment(Pos.CENTER);
        dealersCardsLabel.setFont(titleFont);

        Label playersCardsLabel = new Label("Player's Cards");
        playersCardsLabel.setAlignment(Pos.CENTER);
        playersCardsLabel.setFont(titleFont);

        VBox rightVBox = new VBox();
        rightVBox.setSpacing(20);
        rightVBox.setPadding(new Insets(20));
        rightVBox.setAlignment(Pos.CENTER);
        rightVBox.getChildren().addAll(dealersCardsLabel, dealerCardsBox, playersCardsLabel, playerCardsBox);

        leftVBox.setStyle("-fx-background-color: " + secondaryColor + ";-fx-padding: 8px 16px;");

        playScreen = new BorderPane();
        playScreen.setStyle("-fx-background-color: " + primaryColor + ";");
        playScreen.setTop(menuBar);
        playScreen.setLeft(leftVBox);
        playScreen.setCenter(rightVBox);

        newLookMenuItem.setOnAction(e->{
            changeStyles(leftVBox,confirmWagers,gameInfoLabel,dealersCardsLabel,playersCardsLabel);
        });

        return new Scene(playScreen, width, length);

    }

    public void updateDealersCards(ArrayList<ImageView> dealerCards, ArrayList<String> threeCards, Stage primaryStage) {

        AtomicInteger counter = new AtomicInteger();

        // animation to flip the dealers cards
        pause.setOnFinished(event -> {

            if (counter.get() < 3) {
                Image img = new Image(threeCards.get(counter.get()));
                dealerCards.get(counter.get()).setImage(img);
                counter.getAndIncrement();
                pause.play();
            } else {

                // delay after all cards have been flipped, allows user to see updates
                Timeline delay = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
                    // if player didn't fold
                    if (!pKInfo.playerFold) {
                        if (!pKInfo.validDealerHand) {

                            // alert if the dealer didn't have a valid hand
                            String message = "Dealer does not have at least Queen high; ante wager is pushed";
                            gameInfoList.getItems().add(message);
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Information Dialog");
                            alert.setHeaderText(null);
                            alert.setContentText("The dealer does not qualify.\n$ " + pKInfo.anteWagerNumber + " was returned.");
                            alert.setOnHidden(f -> {
                                // once the alert is closed, the ante wage is returned and turn restarts
                                int temp = pKInfo.anteWagerNumber;
                                resetPokerInfo();
                                pKInfo.anteWagerNumber = temp;
                                // this delay is added so gameInfo can be updated before the scene changes
                                PauseTransition pause = new PauseTransition(Duration.seconds(0.7));
                                pause.setOnFinished(l -> {
                                    primaryStage.setScene(createPlayScreenGui(primaryStage.getWidth(),primaryStage.getHeight(),primaryStage));
                                });
                                pause.play();
                            });
                            alert.show();

                        } else if (Objects.equals(pKInfo.winningHand, "Player")){
                            // condition where the player wins...
                            gameInfoList.getItems().add("Player has won the pair plus wager");
                            gameInfoList.getItems().add("Player beats the dealer");
                            PauseTransition pause = new PauseTransition(Duration.seconds(0.7));
                            pause.setOnFinished(l -> {
                                primaryStage.setScene(createWinScene(primaryStage.getWidth(),primaryStage.getHeight(),primaryStage));
                            });
                            pause.play();
                        } else if(Objects.equals(pKInfo.winningHand, "Dealer")){
                            // condition where the dealer wins...
                            gameInfoList.getItems().add("Player loses to the dealer");
                            PauseTransition pause = new PauseTransition(Duration.seconds(0.7));
                            pause.setOnFinished(l -> {
                                primaryStage.setScene(createFoldLostScene(primaryStage.getWidth(),primaryStage.getHeight(),primaryStage));
                            });
                            pause.play();
                        } else if (Objects.equals(pKInfo.winningHand, "Draw")){
                            // condition where it is a draw..
                            gameInfoList.getItems().add("Player has drawn with the dealer");
                            PauseTransition pause = new PauseTransition(Duration.seconds(0.7));
                            pause.setOnFinished(l -> {
                                primaryStage.setScene(createDrawScreen(primaryStage.getWidth(),primaryStage.getHeight(),primaryStage));
                            });
                            pause.play();
                        }
                    } else {
                        // condition where the player folded...
                        gameInfoList.getItems().add("Player has lost the pair plus wager");
                        clientConnection.sendPokerInfoData(pKInfo);
                        PauseTransition pause = new PauseTransition(Duration.seconds(0.1));
                        pause.setOnFinished(l -> {
                            primaryStage.setScene(createFoldLostScene(primaryStage.getWidth(),primaryStage.getHeight(), primaryStage));
                        });
                        pause.play();
                    }
                }));

                // plays the animation
                delay.play();

            }

        });

        // plays the animation
        pause.play();

    }

    public void updatePlayerCards(ArrayList<ImageView> playerCards, ArrayList<String> threeCards, Stage primaryStage) {

        AtomicInteger counter = new AtomicInteger();

        // animation that plays to flip the player cards
        pause.setOnFinished(event -> {

            if (counter.get() < 3) {
                Image img = new Image(threeCards.get(counter.get()));
                playerCards.get(counter.get()).setImage(img);
                counter.getAndIncrement();
                pause.play();
            } else {

                // once animation is over, player has option to play or fold
                Button playButton = new Button("Play");
                playButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14;");
                Button foldButton = new Button("Fold");
                foldButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14;");

                HBox playFoldBox = new HBox(playButton, foldButton);
                playFoldBox.setSpacing(10);
                playFoldBox.setAlignment(Pos.CENTER);

                VBox vBoxTemp = (VBox) playScreen.getLeft();
                vBoxTemp.getChildren().add(playFoldBox);

                playWagerTextField.setStyle("-fx-background-color: Green");

                playButton.setOnAction(e -> {

                    // if play is picked, the data is sent to the server and calculations are made
                    clientConnection.sendPokerInfoData(pKInfo);
                    gameInfoList.getItems().add("Player has decided to play");
                    playWagerTextField.setText(String.valueOf(pKInfo.anteWagerNumber));
					playButton.setDisable(true);
                    foldButton.setDisable(true);

                    // began flipping dealer's cards
                    updateDealersCards(dealerCards, pKInfo.dealersCards, primaryStage);

                });

                foldButton.setOnAction(e -> {

                    // if the player decides to fold...
                    gameInfoList.getItems().add("Player has folded");
                    pKInfo.playerFold = true;
                    playButton.setDisable(true);
                    foldButton.setDisable(true);

                    // still show dealers cards
                    updateDealersCards(dealerCards, pKInfo.dealersCards, primaryStage);

                });

            }

        });

        pause.play();

    }
    
    void resetPokerInfo(){

        // reset data for PokerInfo object in between turns
        pKInfo.anteWagerNumber = -1;
        pKInfo.pairPlusWagerNumber = -1;
        pKInfo.playWagerNumber = -1;
        pKInfo.playerRank = -1;
        pKInfo.dealerRank = -1;
        pKInfo.playerCards = new ArrayList<>();
        pKInfo.dealersCards = new ArrayList<>();
        pKInfo.shuffledDeck = new ArrayList<>();
        pKInfo.validDealerHand = false;
        pKInfo.playerFold = false;
        pKInfo.winningHand = "";
        pKInfo.pairPlusWinnings = -1;
        pKInfo.anteWagerWinnings = -1;

    }

}
