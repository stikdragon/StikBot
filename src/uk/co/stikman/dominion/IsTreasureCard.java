package uk.co.stikman.dominion;

/**
 * <code>IsTreasureCard</code> is a special version of {@link ProducesTreasure}
 * that is treated as always producing its value, even if it's not in play. This
 * is for actual Treasure cards, which contribute their value even if they're
 * just idle in the player's hand
 * 
 * @author frenchd
 * 
 */
public interface IsTreasureCard extends ProducesTreasure {

}
