package walkingkooka.spreadsheet.hateos;

import walkingkooka.collect.list.Lists;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosIdResourceCollectionResourceCollectionHandler;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A base {@link HateosIdResourceCollectionResourceCollectionHandler} for several {@link SpreadsheetCellStore} methods.
 */
abstract class SpreadsheetCellStoreHateosIdResourceCollectionResourceCollectionHandler<I extends Comparable<I>,
        R extends HateosResource<I>>
        extends SpreadsheetStoreHateosHandler<I,
        R,
        SpreadsheetCellStore>
        implements HateosIdResourceCollectionResourceCollectionHandler<I, R, SpreadsheetCell> {

    SpreadsheetCellStoreHateosIdResourceCollectionResourceCollectionHandler(final SpreadsheetCellStore store) {
        super(store);
    }

    @Override
    public final List<SpreadsheetCell> handle(final I id,
                                              final List<R> resources,
                                              final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(id, this.operation());
        checkResourcesEmpty(resources);
        checkParameters(parameters);

        final List<SpreadsheetCell> response = Lists.array();
        response.addAll(this.handle0(id));
        return response;
    }

    abstract Set<SpreadsheetCell> handle0(final I id);

    abstract String operation();

    @Override
    public final String toString() {
        return SpreadsheetCellStore.class.getSimpleName() + '.' + this.operation();
    }
}
