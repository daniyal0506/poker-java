import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PokerGameLogic{

    // function to validate wager selection made by player
    public static boolean updatePokerInfoWager(String text1, String text2, PokerInfo pk) {
        try {
            int value1 = text1.isEmpty() ? -1 : Integer.parseInt(text1);
            int value2 = text2.isEmpty() ? -1 : Integer.parseInt(text2);

            pk.pairPlusWagerNumber = value1;
            pk.anteWagerNumber = value2;

            return (pk.pairPlusWagerNumber >= 5 && pk.pairPlusWagerNumber <= 25 && pk.anteWagerNumber >= 5 && pk.anteWagerNumber <= 25) ||((pk.anteWagerNumber >= 5 && pk.anteWagerNumber <= 25) && value1 == -1);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
