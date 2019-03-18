package walkingkooka.spreadsheet.store.label;

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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
        this.mappings.put(key, mapping);
        return mapping;
    }

    @Override
    public void delete(final SpreadsheetLabelName label) {
        Objects.requireNonNull(label, "label");
        this.mappings.remove(label);
    }

    @Override
    public int count() {
        return this.mappings.size();
    }

    @Override
    public Collection<SpreadsheetLabelMapping> all() {
        final List<SpreadsheetLabelMapping> copy = Lists.array();
        copy.addAll(this.mappings.values());
        return Collections.unmodifiableCollection(copy);
    }

    @Override
    public Set<? super ExpressionReference> loadCellReferencesOrRanges(final SpreadsheetLabelName label) {
        Objects.requireNonNull(label, "label");

        return Sets.readOnly(TreeMapSpreadsheetLabelStoreSpreadsheetExpressionReferenceVisitor.gather(label, this.mappings));
    }

    /**
     * All mappings present in this spreadsheet
     */
    private final Map<SpreadsheetLabelName, SpreadsheetLabelMapping> mappings = Maps.sorted();

    @Override
    public String toString() {
        return this.mappings.toString();
    }
}
