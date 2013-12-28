import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import uk.co.stikman.dominion.CardSet;
import uk.co.stikman.dominion.GameConfig;


public class TestGameConfigSerialise {

	@Test
	public void test1() {
		GameConfig config = new GameConfig();
		config.addAll();
		config.setCardScale(15.0f);
		config.setNumtypes(15);
		String s = config.save();
		
		GameConfig two = GameConfig.load(s);
		
		List<String> sl1 = new ArrayList<>();
		for (CardSet x : config)
			sl1.add(x.getName());
		Collections.sort(sl1);
		
		List<String> sl2 = new ArrayList<>();
		for (CardSet x : two)
			sl2.add(x.getName());
		Collections.sort(sl2);
		
		assertEquals(sl1, sl2);
		assertEquals(config.getCardScale(), two.getCardScale(), 0.01f);
		assertEquals(config.getNumtypes(), two.getNumtypes());
		
	}

}
