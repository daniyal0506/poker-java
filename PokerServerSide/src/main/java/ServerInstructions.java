import java.util.*;

public class ServerInstructions {

    // list of comments
    static String[] cards = {"2_of_clubs.png", "2_of_diamonds.png", "2_of_hearts.png", "2_of_spades.png",
            "3_of_clubs.png", "3_of_diamonds.png", "3_of_hearts.png", "3_of_spades.png", "4_of_clubs.png",
            "4_of_diamonds.png", "4_of_hearts.png", "4_of_spades.png", "5_of_clubs.png", "5_of_diamonds.png",
            "5_of_hearts.png", "5_of_spades.png", "6_of_clubs.png", "6_of_diamonds.png", "6_of_hearts.png",
            "6_of_spades.png", "7_of_clubs.png", "7_of_diamonds.png", "7_of_hearts.png", "7_of_spades.png",
            "8_of_clubs.png", "8_of_diamonds.png", "8_of_hearts.png", "8_of_spades.png", "9_of_clubs.png",
            "9_of_diamonds.png", "9_of_hearts.png", "9_of_spades.png", "10_of_clubs.png", "10_of_diamonds.png",
            "10_of_hearts.png", "10_of_spades.png", "ace_of_clubs.png", "ace_of_diamonds.png", "ace_of_hearts.png",
            "ace_of_spades.png", "jack_of_clubs.png", "jack_of_diamonds.png", "jack_of_hearts.png", "jack_of_spades.png",
            "king_of_clubs.png", "king_of_diamonds.png", "king_of_hearts.png", "king_of_spades.png", "queen_of_clubs.png",
            "queen_of_diamonds.png", "queen_of_hearts.png", "queen_of_spades.png" };

    // used to shuffle deck of cards
    public static ArrayList<String> shuffleDeck () {
        ArrayList<String> cardList = new ArrayList<>(Arrays.asList(cards));
        Collections.shuffle(cardList);
        return cardList;
    }

    // used to return 3 cards for dealer & player
    public static ArrayList<String> getThreeCards(ArrayList<String> shuffleDeck){
        ArrayList<String> threeCards = new ArrayList<>(shuffleDeck.subList(0, 3));
        shuffleDeck.subList(0, 3).clear();
        return threeCards;
    }

    // used to validate the dealer hand
    public static boolean validateDealerHand(ArrayList<String> dealerHand) {
        boolean hasQueenOrHigher = false;
        // checks for queen or greater
        for (String card : dealerHand) {
            if (card.contains("queen") || card.contains("king") || card.contains("ace")) {
                return true;
            }
        }
        return false;
    }

    // checks for flush in the list
    public boolean checkFlush(ArrayList<String> cardList){

        Set<String> suits = new HashSet<>();
        for(String e : cardList){
            // split the string to get the suit
            String suit = e.split("_")[2].split("\\.")[0];
            suits.add(suit);
        }

        // make sure the set is only one type of suit
        return suits.size() == 1;
    }

    // returns a list of all card ranks
    public ArrayList<Integer> updateCardListValues(ArrayList<String> cardList){

        ArrayList<Integer> cardValues = new ArrayList<>();

        // adding the ranks to the card list
        for(String e: cardList){
            String val = e.split("_")[0];
            if(Objects.equals(val, "jack")){
                cardValues.add(11);
            } else if (Objects.equals(val, "queen")){
                cardValues.add(12);
            } else if (Objects.equals(val, "king")){
                cardValues.add(13);
            } else if (Objects.equals(val, "ace")){
                cardValues.add(14);
            } else {
                cardValues.add(Integer.valueOf(val));
            }
        }

        // sort the ranks before returning
        Collections.sort(cardValues);

        return cardValues;

    }

    // check for pair in the card list
    public boolean checkPair(ArrayList<String> cardList){

        ArrayList<Integer> cardValues = updateCardListValues(cardList);

        // compare adjacent cards
        for(int i = 0; i < cardValues.size() - 1; i++){
            if(Objects.equals(cardValues.get(i + 1), cardValues.get(i))){
                return true;
            }
        }

        return false;
    }

    // check for a straight
    public boolean checkStraight(ArrayList<String> cardList){

        ArrayList<Integer> cardValues = updateCardListValues(cardList);

        // compare cards in ascending order
        for(int i = 0; i < cardValues.size() - 1; i++){
            if(cardValues.get(i+1) - cardValues.get(i) != 1){
                return false;
            }
        }

        return true;

    }

    // checks for a straight flush
    public boolean checkStraightFlush(ArrayList<String> cardList){

        // calls for straight and flush
        return checkStraight(cardList) && checkFlush(cardList);

    }

    // checks for a three of kind
    public boolean checkThreeOfAKind(ArrayList<String> cardList){

        ArrayList<Integer> cardValues = updateCardListValues(cardList);

        // makes sure all cards are same rank
        for (int i = 0; i < cardValues.size() - 1; i++){
            if(!Objects.equals(cardValues.get(i + 1), cardValues.get(i))){
                return false;
            }
        }

        return true;

    }

    // return rank based on hand
    public int evaluateHandRank(ArrayList<String> cardList){

        if(checkStraightFlush(cardList)){
            return 1;
        } else if (checkThreeOfAKind(cardList)){
            return 2;
        } else if (checkStraight(cardList)){
            return 3;
        } else if (checkFlush(cardList)){
            return 4;
        } else if (checkPair(cardList)){
            return 5;
        } else {
            return 6;
        }

    }

    // compare player and dealer hand, to see who wins
    public static String checkWinningHand(int playerRank, int dealerRank){
        if (playerRank > dealerRank){
            return "Dealer";
        } else if (dealerRank > playerRank){
            return "Player";
        } else {
            return "Draw";
        }
    }

    // calculate the pair plus winnings
    public int calculatePairPlusWinnings(int pairPlusWager, ArrayList<String> playerCards){

        // if pair plus wasn't made, return 0
        if(pairPlusWager == -1){
            return 0;
        }

        // return pair plus winning based on hand rank
        int evaluateHand = evaluateHandRank(playerCards);
        if(evaluateHand == 6){
            return (pairPlusWager * -1);
        } else if (evaluateHand == 5){
            return pairPlusWager;
        } else if (evaluateHand == 4){
            return pairPlusWager * 3;
        } else if (evaluateHand == 3){
            return pairPlusWager * 6;
        } else if (evaluateHand == 2){
            return pairPlusWager * 30;
        } else {
            return pairPlusWager * 40;
        }

    }

    // return the winnings for the ante bet wager
    public int calculateWinnings(PokerInfo pk){
        // player gains 1:1 ratio of their wager
        if(Objects.equals("Player", pk.winningHand)){
            return (pk.anteWagerNumber + pk.playWagerNumber) * 2;
        } else{
            // player loses the money they wagered
            return (pk.anteWagerNumber + pk.playWagerNumber) * -1;
        }
    }

    // return players loss based on if they fold
    public int foldLosses(PokerInfo pk){

        // check for pair plus wager or not
        int amountLost = (pk.anteWagerNumber);
        if(pk.pairPlusWagerNumber == -1){
            amountLost *= -1;
        } else {
            // players lose both pair plus and ante wager
            amountLost = (amountLost + pk.pairPlusWagerNumber) * -1;
        }
        return amountLost;
    }

}
