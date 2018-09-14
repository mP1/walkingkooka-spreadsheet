package walkingkooka.spreadsheet.store.label;

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetLabelStore} that uses a {@link Map}.
 */
final class BasicSpreadsheetLabelStore implements SpreadsheetLabelStore {

    /**
     * Factory that creates a new {@link BasicSpreadsheetLabelStore}
     */
    static BasicSpreadsheetLabelStore create() {
        return new BasicSpreadsheetLabelStore();
    }

    /**
     * Private ctor.
     */
    private BasicSpreadsheetLabelStore() {
        super();
    }

    @Override
    public Optional<SpreadsheetLabelMapping> load(final SpreadsheetLabelName label) {
        Objects.requireNonNull(label, "labels");
        return Optional.ofNullable(this.mappings.get(label));
    }

    @Override
    public void save(final SpreadsheetLabelMapping mapping) {
        Objects.requireNonNull(mapping, "mapping");

        this.mappings.put(mapping.label(), mapping);
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

    /**
     * All mappings present in this spreadsheet
     */
    private final Map<SpreadsheetLabelName, SpreadsheetLabelMapping> mappings = Maps.sorted();

    @Override
    public String toString() {
        return this.mappings.toString();
    }
}
