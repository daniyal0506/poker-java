import java.io.Serializable;
import java.util.ArrayList;

public class PokerInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    int anteWagerNumber;
    int pairPlusWagerNumber;
    int playWagerNumber;
    int playerRank;
    int dealerRank;
    int pairPlusWinnings;
    int anteWagerWinnings;
    int foldLosses;
    ArrayList<String> playerCards;
    ArrayList<String> dealersCards;
    ArrayList<String> shuffledDeck;
    boolean validDealerHand;
    boolean playerFold;
    String winningHand;

    PokerInfo(){
        anteWagerNumber = -1;
        pairPlusWagerNumber = -1;
        playWagerNumber = -1;
        playerRank = -1;
        dealerRank = -1;
        foldLosses = -1;
        playerCards = new ArrayList<>();
        dealersCards = new ArrayList<>();
        shuffledDeck = new ArrayList<>();
        validDealerHand = false;
        playerFold = false;
        winningHand = "";
        pairPlusWinnings = -1;
        anteWagerWinnings = -1;
    }

}
