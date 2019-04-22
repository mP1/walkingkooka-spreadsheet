package walkingkooka.spreadsheet.store.label;

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.store.Store;
import walkingkooka.spreadsheet.store.Watchers;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A {@link SpreadsheetLabelStore} that uses a {@link Map}.
 */
final class TreeMapSpreadsheetLabelStore implements SpreadsheetLabelStore {

    /**
     * Factory that creates a new {@link TreeMapSpreadsheetLabelStore}
     */
    static TreeMapSpreadsheetLabelStore create() {
        return new TreeMapSpreadsheetLabelStore();
    }

    /**
     * Private ctor.
     */
    private TreeMapSpreadsheetLabelStore() {
        super();
    }

    @Override
    public Optional<SpreadsheetLabelMapping> load(final SpreadsheetLabelName label) {
        Objects.requireNonNull(label, "labels");
        return Optional.ofNullable(this.mappings.get(label));
    }

    @Override
    public SpreadsheetLabelMapping save(final SpreadsheetLabelMapping mapping) {
        Objects.requireNonNull(mapping, "mapping");

        final SpreadsheetLabelName key = mapping.label();
        if (false == mapping.equals(this.mappings.put(key, mapping))) {
            this.saveWatchers.accept(mapping);
        }

        return mapping;
    }

    @Override
    public Runnable addSaveWatcher(final Consumer<SpreadsheetLabelMapping> saved) {
        return this.saveWatchers.addWatcher(saved);
    }

    private final Watchers<SpreadsheetLabelMapping> saveWatchers = Watchers.create();

    @Override
    public void delete(final SpreadsheetLabelName label) {
        Objects.requireNonNull(label, "label");

        if (null != this.mappings.remove(label)) {
            this.deleteWatchers.accept(label);
        }
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<SpreadsheetLabelName> deleted) {
        return this.deleteWatchers.addWatcher(deleted);
    }

    private final Watchers<SpreadsheetLabelName> deleteWatchers = Watchers.create();

    @Override
    public int count() {
        return this.mappings.size();
    }

    @Override
    public Set<SpreadsheetLabelName> ids(final int from,
                                         final int count) {
        Store.checkFromAndTo(from, count);

        return this.mappings.keySet()
                .stream()
                .skip(from)
                .limit(count)
                .collect(Collectors.toCollection(Sets::ordered));
    }

    /**
     * Find the first mapping at or after the from {@link SpreadsheetLabelName} and then gather the required count.
     */
    @Override
    public List<SpreadsheetLabelMapping> values(final SpreadsheetLabelName from,
                                                final int count) {
        Store.checkFromAndToIds(from, count);

        return this.mappings.entrySet()
                .stream()
                .filter(e -> e.getKey().compareTo(from) >= 0)
                .map(e -> e.getValue())
                .limit(count)
                .collect(Collectors.toCollection(Lists::array));
    }

    @Override
    public Set<? super ExpressionReference> loadCellReferencesOrRanges(final SpreadsheetLabelName label) {
        Objects.requireNonNull(label, "label");

        return Sets.readOnly(TreeMapSpreadsheetLabelStoreReferencesSpreadsheetExpressionReferenceVisitor.gather(label, this.mappings));
    }

    @Override
    public Set<SpreadsheetLabelName> labels(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");

        return Sets.readOnly(TreeMapSpreadsheetLabelStoreLabelsSpreadsheetExpressionReferenceVisitor.gather(cell, this.mappings));
    }

    /**
     * All mappings present in this spreadsheet
     */
    private final Map<SpreadsheetLabelName, SpreadsheetLabelMapping> mappings = Maps.sorted();

    @Override
    public String toString() {
        return this.mappings.values().toString();
    }
}
