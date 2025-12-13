
/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.spreadsheet.store;

import org.junit.jupiter.api.Test;
import walkingkooka.reflect.TypeNameTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.value.SpreadsheetColumn;

import static org.junit.jupiter.api.Assertions.assertSame;

public abstract class SpreadsheetColumnStoreTestCase<S extends SpreadsheetColumnStore> implements SpreadsheetColumnStoreTesting<S>,
    TypeNameTesting<S> {

    final static SpreadsheetColumnReference REFERENCE = SpreadsheetSelection.parseColumn("B");

    SpreadsheetColumnStoreTestCase() {
        super();
    }

    @Test
    public final void testLoadUnknown() {
        this.loadAndCheck(
            this.createStore(),
            REFERENCE
        );
    }

    @Test
    public final void testSaveAndLoad() {
        final S store = this.createStore();

        final SpreadsheetColumn column = REFERENCE.column();
        this.checkEquals(column, store.save(column), "incorrect key returned");

        assertSame(column, store.loadOrFail(REFERENCE));
    }

    @Test
    public final void testSaveAndLoadAbsoluteColumnReference() {
        final S store = this.createStore();

        final SpreadsheetColumnReference reference = SpreadsheetSelection.parseColumn("$B");
        final SpreadsheetColumn column = reference.column();
        this.checkEquals(column, store.save(column), "incorrect key returned");

        assertSame(
            column,
            store.loadOrFail(
                reference.setReferenceKind(SpreadsheetReferenceKind.ABSOLUTE)
            )
        );
    }

    @Test
    public final void testSaveDeleteLoad() {
        final S store = this.createStore();

        final SpreadsheetColumnReference reference = REFERENCE;
        final SpreadsheetColumn column = REFERENCE.column();
        store.save(column);
        store.delete(reference);

        this.loadAndCheck(
            store,
            reference
        );
    }

    @Test
    public final void testCount() {
        final S store = this.createStore();

        store.save(column("B"));
        store.save(column("C"));

        this.countAndCheck(store, 2);
    }

    @Test
    public final void testCount2() {
        final S store = this.createStore();

        final SpreadsheetColumn column = this.column("B");
        store.save(column);
        store.save(this.column("C"));
        store.delete(column.reference());

        this.countAndCheck(store, 2 - 1);
    }

    @Test
    public final void testIds() {
        final S store = this.createStore();

        final SpreadsheetColumn a = this.column("a");
        final SpreadsheetColumn b = this.column("b");
        final SpreadsheetColumn c = this.column("c");

        store.save(a);
        store.save(b);
        store.save(c);

        this.idsAndCheck(store,
            0,
            3,
            a.reference(), b.reference(), c.reference());
    }

    @Test
    public final void testIdsWindow() {
        final S store = this.createStore();

        final SpreadsheetColumn a = this.column("a");
        final SpreadsheetColumn b = this.column("b");
        final SpreadsheetColumn c = this.column("c");
        final SpreadsheetColumn d = this.column("d");

        store.save(a);
        store.save(b);
        store.save(c);
        store.save(d);

        this.idsAndCheck(store,
            1,
            2,
            b.reference(), c.reference());
    }

    @Test
    public final void testValues() {
        final S store = this.createStore();

        final SpreadsheetColumn a = this.column("a");
        final SpreadsheetColumn b = this.column("b");
        final SpreadsheetColumn c = this.column("c");

        store.save(a);
        store.save(b);
        store.save(c);

        this.valuesAndCheck(
            store,
            0,
            3,
            a,
            b,
            c
        );
    }

    @Test
    public final void testValuesWindow() {
        final S store = this.createStore();

        final SpreadsheetColumn a = this.column("a");
        final SpreadsheetColumn b = this.column("b");
        final SpreadsheetColumn c = this.column("c");
        final SpreadsheetColumn d = this.column("d");

        store.save(a);
        store.save(b);
        store.save(c);
        store.save(d);

        this.valuesAndCheck(
            store,
            1,
            2,
            b,
            c
        );
    }

    @Override
    public final SpreadsheetColumnReference id() {
        return REFERENCE;
    }

    @Override
    public SpreadsheetColumn value() {
        return this.column(this.id());
    }

    private SpreadsheetColumn column(final String text) {
        return this.column(
            SpreadsheetSelection.parseColumn(text)
        );
    }

    private SpreadsheetColumn column(final SpreadsheetColumnReference reference) {
        return reference.column();
    }

    // TypeNameTesting..................................................................

    @Override
    public String typeNameSuffix() {
        return SpreadsheetColumnStore.class.getSimpleName();
    }
}
