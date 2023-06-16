
import static org.junit.jupiter.api.Assertions.*;

import com.sun.scenario.effect.impl.prism.ps.PPSBlend_ADDPeer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;

public class MyTest {

    static ServerInstructions serverInstructions;
    static PokerInfo pk;
    static Server server;

    @BeforeAll
    static void setup(){
        serverInstructions = new ServerInstructions();
        pk = new PokerInfo();
        server = new Server(message -> {},"1234");
    }

    @Test
    public void testServerConstructor() {

        assertNotNull(server.clients);
        assertNotNull(server.server);
        assertEquals(Integer.parseInt("1234"), server.portNumberField);
    }

    @Test
    public void testPokerInfo() {

        assertEquals(pk.playerCards.size(),0);
        assertEquals(pk.dealersCards.size(),0);
        assertEquals(pk.shuffledDeck.size(),0);
        assertFalse(pk.validDealerHand);
        assertFalse(pk.playerFold);
        assertEquals("", pk.winningHand);
        assertEquals(-1, pk.anteWagerNumber);
        assertEquals(-1, pk.pairPlusWagerNumber);
        assertEquals(-1, pk.playWagerNumber);
        assertEquals(-1, pk.playerRank);
        assertEquals(-1, pk.dealerRank);
        assertEquals(-1, pk.foldLosses);
        assertEquals(-1, pk.pairPlusWinnings);
        assertEquals(-1, pk.anteWagerWinnings);

    }


    @Test
    public void testShuffleDeck(){

        ArrayList<String> d1 = ServerInstructions.shuffleDeck();
        ArrayList<String> d2 = ServerInstructions.shuffleDeck();

        assertNotEquals(d1,d2);

    }

    @Test
    public void testGetThreeCards(){
        ArrayList<String> getThreeCards1 = ServerInstructions.getThreeCards(ServerInstructions.shuffleDeck());
        assertEquals(getThreeCards1.size(),3);

        ArrayList<String> getThreeCards2 = ServerInstructions.getThreeCards(ServerInstructions.shuffleDeck());
        assertEquals(getThreeCards2.size(),3);

        assertNotEquals(getThreeCards1,getThreeCards2);
    }

    @Test
    public void testValidateHand(){

        ArrayList<String> deck1 = new ArrayList<>();
        deck1.add("3_of_clubs.png");
        deck1.add("3_of_hearts.png");
        deck1.add("3_of_diamonds.png");

        ArrayList<String> deck2 = new ArrayList<>();
        deck2.add("king_of_clubs.png");
        deck2.add("10_of_spades.png");
        deck2.add("9_of_spades.png");

        assertFalse(ServerInstructions.validateDealerHand(deck1));
        assertTrue(ServerInstructions.validateDealerHand(deck2));

    }

    @Test
    public void testThreeOfAKind() {

        ArrayList<String> cards1 = new ArrayList<>();
        cards1.add("2_of_clubs.png");
        cards1.add("2_of_diamonds.png");
        cards1.add("2_of_hearts.png");

        ArrayList<String> cards2 = new ArrayList<>();

        cards2.add("5_of_spades.png");
        cards2.add("5_of_clubs.png");
        cards2.add("5_of_hearts.png");

        ArrayList<String> cards3 = new ArrayList<>();
        cards3.add("king_of_clubs.png");
        cards3.add("king_of_diamonds.png");
        cards3.add("king_of_hearts.png");

        assertTrue(serverInstructions.checkThreeOfAKind(cards1));
        assertTrue(serverInstructions.checkThreeOfAKind(cards2));
        assertTrue(serverInstructions.checkThreeOfAKind(cards3));
    }

    @Test
    public void testNotThreeOfAKind() {

        ArrayList<String> cards1 = new ArrayList<>();
        cards1.add("2_of_clubs.png");
        cards1.add("3_of_diamonds.png");
        cards1.add("4_of_hearts.png");

        ArrayList<String> cards2 = new ArrayList<>();
        cards2.add("jack_of_hearts.png");
        cards2.add("queen_of_diamonds.png");
        cards2.add("king_of_spades.png");

        ArrayList<String> cards3 = new ArrayList<>();
        cards3.add("9_of_clubs.png");
        cards3.add("9_of_spades.png");
        cards3.add("10_of_hearts.png");

        assertFalse(serverInstructions.checkThreeOfAKind(cards1));
        assertFalse(serverInstructions.checkThreeOfAKind(cards2));
        assertFalse(serverInstructions.checkThreeOfAKind(cards3));

    }

    @Test
    public void testCheckStraight() {

        ArrayList<String> cards1 = new ArrayList<>();
        cards1.add("2_of_clubs.png");
        cards1.add("3_of_diamonds.png");
        cards1.add("4_of_hearts.png");

        ArrayList<String> cards2 = new ArrayList<>();
        cards2.add("7_of_clubs.png");
        cards2.add("5_of_spades.png");
        cards2.add("6_of_hearts.png");

        ArrayList<String> cards3 = new ArrayList<>();
        cards3.add("king_of_clubs.png");
        cards3.add("ace_of_diamonds.png");
        cards3.add("queen_of_hearts.png");

        ArrayList<String> cards4 = new ArrayList<>();
        cards4.add("ace_of_clubs.png");
        cards4.add("king_of_diamonds.png");
        cards4.add("queen_of_hearts.png");

        ArrayList<String> cards5 = new ArrayList<>();
        cards5.add("ace_of_spades.png");
        cards5.add("2_of_clubs.png");
        cards5.add("3_of_hearts.png");

        assertTrue(serverInstructions.checkStraight(cards1));
        assertTrue(serverInstructions.checkStraight(cards2));
        assertTrue(serverInstructions.checkStraight(cards3));
        assertTrue(serverInstructions.checkStraight(cards4));
        assertFalse(serverInstructions.checkStraight(cards5));
    }

    @Test
    public void testCheckStraightFlush() {

        ArrayList<String> cards1 = new ArrayList<>();
        cards1.add("2_of_clubs.png");
        cards1.add("3_of_clubs.png");
        cards1.add("4_of_clubs.png");

        ArrayList<String> cards2 = new ArrayList<>();
        cards2.add("7_of_clubs.png");
        cards2.add("5_of_clubs.png");
        cards2.add("6_of_clubs.png");

        ArrayList<String> cards3 = new ArrayList<>();
        cards3.add("king_of_clubs.png");
        cards3.add("ace_of_clubs.png");
        cards3.add("queen_of_clubs.png");

        ArrayList<String> cards4 = new ArrayList<>();
        cards4.add("ace_of_clubs.png");
        cards4.add("king_of_clubs.png");
        cards4.add("queen_of_clubs.png");

        ArrayList<String> cards5 = new ArrayList<>();
        cards5.add("ace_of_spades.png");
        cards5.add("2_of_spades.png");
        cards5.add("3_of_spades.png");

        ArrayList<String> cards6 = new ArrayList<>();
        cards6.add("ace_of_spades.png");
        cards6.add("king_of_spades.png");
        cards6.add("queen_of_spades.png");

        assertTrue(serverInstructions.checkStraightFlush(cards1));
        assertTrue(serverInstructions.checkStraightFlush(cards2));
        assertTrue(serverInstructions.checkStraightFlush(cards3));
        assertTrue(serverInstructions.checkStraightFlush(cards4));
        assertFalse(serverInstructions.checkStraightFlush(cards5));
        assertTrue(serverInstructions.checkStraightFlush(cards6));
    }

    @Test
    public void testCheckFlush() {

        ArrayList<String> cards1 = new ArrayList<>();
        cards1.add("2_of_clubs.png");
        cards1.add("5_of_clubs.png");
        cards1.add("4_of_clubs.png");

        ArrayList<String> cards2 = new ArrayList<>();
        cards2.add("7_of_diamonds.png");
        cards2.add("5_of_diamonds.png");
        cards2.add("6_of_diamonds.png");

        ArrayList<String> cards3 = new ArrayList<>();
        cards3.add("king_of_hearts.png");
        cards3.add("ace_of_spades.png");
        cards3.add("queen_of_hearts.png");

        ArrayList<String> cards4 = new ArrayList<>();
        cards4.add("ace_of_hearts.png");
        cards4.add("king_of_hearts.png");
        cards4.add("queen_of_hearts.png");

        ArrayList<String> cards5 = new ArrayList<>();
        cards5.add("ace_of_spades.png");
        cards5.add("2_of_spades.png");
        cards5.add("3_of_spades.png");

        assertTrue(serverInstructions.checkFlush(cards1));
        assertTrue(serverInstructions.checkFlush(cards2));
        assertFalse(serverInstructions.checkFlush(cards3));
        assertTrue(serverInstructions.checkFlush(cards4));
        assertTrue(serverInstructions.checkFlush(cards5));
    }

    @Test
    public void testCheckPair() {

        ArrayList<String> cards1 = new ArrayList<>();
        cards1.add("2_of_clubs.png");
        cards1.add("2_of_diamonds.png");
        cards1.add("4_of_hearts.png");

        ArrayList<String> cards2 = new ArrayList<>();
        cards2.add("7_of_clubs.png");
        cards2.add("5_of_spades.png");
        cards2.add("7_of_hearts.png");

        ArrayList<String> cards3 = new ArrayList<>();
        cards3.add("king_of_hearts.png");
        cards3.add("ace_of_spades.png");
        cards3.add("queen_of_hearts.png");

        ArrayList<String> cards4 = new ArrayList<>();
        cards4.add("ace_of_hearts.png");
        cards4.add("king_of_hearts.png");
        cards4.add("queen_of_spades.png");

        ArrayList<String> cards5 = new ArrayList<>();
        cards5.add("ace_of_spades.png");
        cards5.add("2_of_spades.png");
        cards5.add("3_of_hearts.png");

        assertTrue(serverInstructions.checkPair(cards1));
        assertTrue(serverInstructions.checkPair(cards2));
        assertFalse(serverInstructions.checkPair(cards3));
        assertFalse(serverInstructions.checkPair(cards4));
        assertFalse(serverInstructions.checkPair(cards5));

    }

    @Test
    public void testUpdateCardListValue(){

        ArrayList<String> x = new ArrayList<>();
        ArrayList<Integer> y = new ArrayList<>();

        x.add("2_of_clubs.png");
        x.add("2_of_spades.png");
        x.add("8_of_hearts.png");
        serverInstructions.updateCardListValues(x);
        y.add(2);
        y.add(2);
        y.add(8);

        assertEquals(y.size(),x.size());

    }

    @Test
    public void testEvaluateHandRank() {

        ArrayList<String> cards1 = new ArrayList<>();
        cards1.add("2_of_clubs.png");
        cards1.add("2_of_diamonds.png");
        cards1.add("4_of_hearts.png");
        assertEquals(5, serverInstructions.evaluateHandRank(cards1));

        ArrayList<String> cards2 = new ArrayList<>();
        cards2.add("7_of_clubs.png");
        cards2.add("5_of_spades.png");
        cards2.add("7_of_hearts.png");
        assertEquals(5, serverInstructions.evaluateHandRank(cards2));

        ArrayList<String> cards3 = new ArrayList<>();
        cards3.add("king_of_hearts.png");
        cards3.add("ace_of_spades.png");
        cards3.add("queen_of_hearts.png");
        assertEquals(3, serverInstructions.evaluateHandRank(cards3));

        ArrayList<String> cards4 = new ArrayList<>();
        cards4.add("ace_of_spades.png");
        cards4.add("2_of_spades.png");
        cards4.add("3_of_hearts.png");
        assertEquals(6, serverInstructions.evaluateHandRank(cards4));

        ArrayList<String> cards5 = new ArrayList<>();
        cards5.add("10_of_diamonds.png");
        cards5.add("jack_of_spades.png");
        cards5.add("queen_of_clubs.png");
        assertEquals(3, serverInstructions.evaluateHandRank(cards5));

        ArrayList<String> cards6 = new ArrayList<>();
        cards6.add("ace_of_hearts.png");
        cards6.add("10_of_spades.png");
        cards6.add("4_of_hearts.png");
        assertEquals(6, serverInstructions.evaluateHandRank(cards6));

    }

    @Test
    public void testCheckWinningHand() {
        assertEquals("Player", ServerInstructions.checkWinningHand(1, 2));
        assertEquals("Dealer", ServerInstructions.checkWinningHand(3, 1));
        assertEquals("Draw", ServerInstructions.checkWinningHand(4, 4));
    }

    @Test
    public void testCalculateWinnings() {

        PokerInfo pk1 = new PokerInfo();
        pk1.winningHand = "Player";
        pk1.anteWagerNumber = 10;
        pk1.playWagerNumber = 5;
        assertEquals(30, serverInstructions.calculateWinnings(pk1));

        PokerInfo pk2 = new PokerInfo();
        pk2.winningHand = "Dealer";
        pk2.anteWagerNumber = 10;
        pk2.playWagerNumber = 5;
        assertEquals(-15, serverInstructions.calculateWinnings(pk2));

        PokerInfo pk3 = new PokerInfo();
        pk3.winningHand = "Draw";
        pk3.anteWagerNumber = 10;
        pk3.playWagerNumber = 5;
        assertEquals(-15, serverInstructions.calculateWinnings(pk3));

    }

}

