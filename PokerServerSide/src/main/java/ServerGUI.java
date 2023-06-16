import javafx.application.Application;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.HashMap;

public class ServerGUI extends Application {

	// store different scenes
	HashMap<String, Scene> sceneMap;

	// used to set up Server
	Server serverConnection;
	PokerInfo pokerInfo;
	ListView<String> listItems;

	// store client count
	IntegerProperty clientCount = new SimpleIntegerProperty(0);

	// colors for scenes
	String primaryColor = "#0099ff";
	String secondaryColor = "#ffba00";
	String accentColor = "#00008b";

	// load font
	InputStream is = getClass().getResourceAsStream("/fonts/Chainwhacks-vm72E.ttf");
	Font titleFont = Font.loadFont(is, 50);

	Alert alert;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		// add scenes to SceneMap
		sceneMap = new HashMap<String, Scene>();
		sceneMap.put("serverHome",  createHomeGui(primaryStage));
		sceneMap.put("serverInfo", createServerInfoGui(primaryStage));
		alert = new Alert(Alert.AlertType.ERROR);

		primaryStage.setTitle("3 Card Poker Server");

		primaryStage.setScene(sceneMap.get("serverHome"));

		primaryStage.show();

	}

	public Scene createHomeGui(Stage primaryStage){

		Label title = new Label("3 Card Poker Server");
		title.setFont(titleFont);

		TextField portField = new TextField();
		portField.setPromptText("Enter port number");

		Button connectButton = new Button("On");

		connectButton.setOnAction(event -> {

			try {
				// set up server connection
				serverConnection = new Server(data -> {
					Platform.runLater(() -> {
						// add to list each time data is sent from client
						listItems.getItems().add(data.toString());
						clientCount.set(serverConnection.clients.size());
					});
				}, portField.getText());

				primaryStage.setScene(sceneMap.get("serverInfo"));
				connectButton.setDisable(true);
			} catch (Exception e){

				// if there was an error in connecting, an alert is thrown
				alert.setTitle("Error");
				alert.setHeaderText("Connection Failed");
				alert.setContentText("Could not launch the server");
				alert.showAndWait();
			}

		});

		HBox titleBox = new HBox(title);
		titleBox.setAlignment(Pos.CENTER);
		HBox portBox = new HBox(portField);
		portBox.setAlignment(Pos.CENTER);
		HBox connectBox = new HBox(connectButton);
		connectBox.setAlignment(Pos.CENTER);

		VBox vbox = new VBox(10, titleBox, portBox, connectBox);
		vbox.setAlignment(Pos.CENTER);
		vbox.setPadding(new Insets(20));
		vbox.setMaxSize(650,650);
		vbox.setStyle("-fx-background-color: " + secondaryColor + ";-fx-padding: 8px 16px; -fx-background-radius: 8px;");

		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(vbox);
		borderPane.setStyle("-fx-background-color: " + primaryColor);

		return new Scene(borderPane, 700, 700);

	}

	public Scene createServerInfoGui(Stage primaryStage){

		listItems = new ListView<String>();

		BorderPane root = new BorderPane();
		root.setPadding(new Insets(70));
		root.setStyle("-fx-background-color: " + primaryColor);

		Button serverOff = new Button("Off");
		serverOff.setStyle("-fx-background-color: " + accentColor + "; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8px 16px; -fx-background-radius: 8px;");

		Button serverOn = new Button("New Server");
		serverOn.setStyle("-fx-background-color: " + accentColor + "; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8px 16px; -fx-background-radius: 8px;");

		serverOff.setOnAction(event -> {
			serverConnection.closeServer();
		});

		serverOn.setOnAction(event -> {
			primaryStage.setScene(createHomeGui(primaryStage));
		});

		// update label, so it changes each time a client is added
		Label clientCountLabel = new Label();
		clientCountLabel.textProperty().bind(Bindings.concat("Client Count: ").concat(clientCount.asString()));

		HBox buttonsBox = new HBox(20,clientCountLabel,serverOff,serverOn);
		buttonsBox.setAlignment(Pos.CENTER);

		root.setTop(buttonsBox);

		root.setCenter(listItems);

		return new Scene(root, 700, 700);

	}

}
