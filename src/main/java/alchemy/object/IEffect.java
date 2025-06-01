package alchemy.object;
public interface IEffect {
    /**
     * Gets the unique identifier of the effect.
     * @return the effect ID
     */
    int getId();

    /**
     * Gets the title of the effect.
     * @return the effect title
     */
    String getTitle();

    /**
     * Gets the description of the effect.
     * @return the effect description
     */
    String getDescription();
}
