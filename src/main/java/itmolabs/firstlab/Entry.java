package itmolabs.firstlab;

/**
 * Created by dart_revan on 13/10/15.
 */
public class Entry {
    protected String name;
    protected Number price;

    public Entry(String name, Number price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public Number getPrice() {
        return price;
    }

    public Entry merge(Entry mergedEntry) {
        return this;
    }

    @Override
    public String toString() {
        return name + ' ' + price;
    }
}
