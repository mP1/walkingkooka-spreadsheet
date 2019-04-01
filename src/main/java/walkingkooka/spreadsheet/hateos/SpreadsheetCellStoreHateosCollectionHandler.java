package walkingkooka.spreadsheet.hateos;

import walkingkooka.collect.list.Lists;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosCollectionHandler;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A base {@link HateosCollectionHandler} for several {@link SpreadsheetCellStore} methods.
 */
abstract class SpreadsheetCellStoreHateosCollectionHandler<I extends Comparable<I>, R extends HateosResource<I>> extends SpreadsheetStoreHateosHandler<I, R, SpreadsheetCellStore>
        implements HateosCollectionHandler<I, R, SpreadsheetCell> {

    SpreadsheetCellStoreHateosCollectionHandler(final SpreadsheetCellStore store) {
        super(store);
    }

    @Override
    public final List<SpreadsheetCell> handle(final I id,
                                              final List<R> cells,
                                              final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(id, this.operation());
        checkResourcesEmpty(cells);
        checkParameters(parameters);

        final List<SpreadsheetCell> cellsResponse = Lists.array();
        cellsResponse.addAll(this.handle0(id));
        return cellsResponse;
    }

    abstract Set<SpreadsheetCell> handle0(final I id);

    abstract String operation();

    @Override
    public final String toString() {
        return SpreadsheetCellStore.class.getSimpleName() + "." + this.operation();
    }
}
