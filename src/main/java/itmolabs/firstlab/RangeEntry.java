package itmolabs.firstlab;

/**
 * Created by dart_revan on 08/10/15.
 */
class RangeEntry extends Entry {
    private final int count;

    public RangeEntry(String productName, Number summaryPrice, int count) {
        super(productName, summaryPrice);
        this.count = count;

    }

    public RangeEntry(Entry entry) {
        super(entry.name, entry.price);
        count = 1;
    }

    public int getCount() {
        return count;
    }

    public Number getAveragePrice(){
        return count > 0 ? price.doubleValue() / count : 0;
    }

    @Override
    public Entry merge(Entry mergedEntry) {
        if (mergedEntry == null) return null;

        if (mergedEntry.getClass() == ColorEntry.class){
            return new Entry(
                    name + "_" + mergedEntry.name,
                    getAveragePrice().doubleValue() + mergedEntry.getPrice().doubleValue()
            );
        }
        if (mergedEntry.getClass() == RangeEntry.class){
            return new RangeEntry(
                    name,
                    mergedEntry.price.doubleValue() + price.doubleValue(),
                    ((RangeEntry) mergedEntry).getCount() + count
            );
        }

        return this;
    }
}
