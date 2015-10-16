package itmolabs.firstlab;

/**
 * Created by dart_revan on 08/10/15.
 */
public class ColorEntry extends Entry{

    public ColorEntry(String colorName, Number price) {
        super(colorName, price);
    }

    public ColorEntry(Entry entry) {
        super(entry.name, entry.price);
    }

    @Override
    public Entry merge(Entry mergedEntry) {
        if (mergedEntry == null) return null;

        if (mergedEntry.getClass() == RangeEntry.class){
            return new Entry(
                    mergedEntry.name + "_" + name,
                    price.doubleValue() + mergedEntry.getPrice().doubleValue()
            );
        }

        return this;
    }
}
