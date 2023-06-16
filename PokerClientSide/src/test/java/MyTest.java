import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MyTest {

	@Test
	public void testUpdatePokerInfoWager() {

		PokerInfo pk = new PokerInfo();

		assertTrue(PokerGameLogic.updatePokerInfoWager("10", "15", pk));
		assertEquals(10, pk.pairPlusWagerNumber);
		assertEquals(15, pk.anteWagerNumber);

		assertFalse(PokerGameLogic.updatePokerInfoWager("", "", pk));
		assertEquals(-1, pk.pairPlusWagerNumber);
		assertEquals(-1, pk.anteWagerNumber);

		assertFalse(PokerGameLogic.updatePokerInfoWager("3", "10", pk));
		assertEquals(3, pk.pairPlusWagerNumber);
		assertEquals(10, pk.anteWagerNumber);

		assertFalse(PokerGameLogic.updatePokerInfoWager("30", "20", pk));
		assertEquals(30, pk.pairPlusWagerNumber);
		assertEquals(20, pk.anteWagerNumber);

	}

}
