package org.apache.log4j.chainsaw.favourites;

/**
 * A Fauvourite is just a named container of on object that can be used
 * as a basis (prototype) for the creation of exact copies.
 * 
 * Clients should use the FavouritesRegistry to create instances of this class
 * so that explicit checks can be performed about the suitability of the
 * prototype.
 * 
 * @author Paul Smith <psmith@apache.org>
 *
 */
public final class Favourite {

    private String name;
    private Object prototype;

    /**
     * @param name
     * @param object
     */
    Favourite(String name, Object prtotype) {
        this.name = name;
        this.prototype = prtotype;
    }


    /**
     * @return Returns the name.
     */
    public final String getName() {

        return name;
    }

    /**
     * Returns the object that would be used as a basis to create new
     * instances of that same object.
     * @return Returns the prototype.
     */
    public final Object getPrototype() {
      return prototype;
    }
}
