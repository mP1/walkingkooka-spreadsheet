package walkingkooka.spreadsheet.store;

import org.checkerframework.checker.units.qual.K;
import org.junit.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.test.PackagePrivateClassTestCase;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public abstract class StoreTestCase<S extends Store<K, V>, K, V> extends PackagePrivateClassTestCase<S> {
    
    @Test(expected = NullPointerException.class)
    public final void testLoadNullIdFails() {
        this.createStore().load(null);
    }

    @Test(expected = NullPointerException.class)
    public final void testSaveNullFails() {
        this.createStore().save(null);
    }

    @Test(expected = NullPointerException.class)
    public final void testDeleteNullFails() {
        this.createStore().delete(null);
    }

    abstract protected S createStore();

    protected final V loadOrFail(final S store, final K id) {
        final Optional<V> value = store.load(id);
        if(!value.isPresent()) {
            fail("Loading " + id + " should have succeeded");
        }
        return value.get();
    }

    protected final void loadFailCheck(final K id) {
        this.loadFailCheck(this.createStore(), id);
    }

    protected final void loadFailCheck(final S store, final K id) {
        final Optional<V> value = store.load(id);
        assertEquals("Expected id " + id + " to fail", Optional.empty(), value);
    }
}
