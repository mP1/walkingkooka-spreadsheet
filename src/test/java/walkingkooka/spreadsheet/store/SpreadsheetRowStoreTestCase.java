
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
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.value.SpreadsheetRow;

import static org.junit.jupiter.api.Assertions.assertSame;

public abstract class SpreadsheetRowStoreTestCase<S extends SpreadsheetRowStore> implements SpreadsheetRowStoreTesting<S>,
    TypeNameTesting<S> {

    final static SpreadsheetRowReference REFERENCE = SpreadsheetSelection.parseRow("2");

    SpreadsheetRowStoreTestCase() {
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

        final SpreadsheetRow row = REFERENCE.row();
        this.checkEquals(row, store.save(row), "incorrect key returned");

        assertSame(row, store.loadOrFail(REFERENCE));
    }

    @Test
    public final void testSaveAndLoadAbsoluteRowReference() {
        final S store = this.createStore();

        final SpreadsheetRowReference reference = REFERENCE.setReferenceKind(SpreadsheetReferenceKind.ABSOLUTE);
        final SpreadsheetRow row = REFERENCE.row();
        this.checkEquals(
            row,
            store.save(row),
            "incorrect row returned"
        );

        assertSame(
            row,
            store.loadOrFail(
                reference.setReferenceKind(SpreadsheetReferenceKind.ABSOLUTE)
            )
        );
    }

    @Test
    public final void testSaveDeleteLoad() {
        final S store = this.createStore();

        final SpreadsheetRowReference reference = REFERENCE;
        final SpreadsheetRow row = REFERENCE.row();
        store.save(row);
        store.delete(reference);

        this.loadAndCheck(
            store,
            reference
        );
    }

    @Test
    public final void testCount() {
        final S store = this.createStore();

        store.save(row("2"));
        store.save(row("3"));

        this.countAndCheck(store, 2);
    }

    @Test
    public final void testCount2() {
        final S store = this.createStore();

        final SpreadsheetRow row = this.row("2");
        store.save(row);
        store.save(this.row("3"));
        store.delete(row.reference());

        this.countAndCheck(store, 2 - 1);
    }

    @Test
    public final void testIds() {
        final S store = this.createStore();

        final SpreadsheetRow a = this.row("1");
        final SpreadsheetRow b = this.row("2");
        final SpreadsheetRow c = this.row("3");

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

        final SpreadsheetRow a = this.row("1");
        final SpreadsheetRow b = this.row("2");
        final SpreadsheetRow c = this.row("3");
        final SpreadsheetRow d = this.row("4");

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

        final SpreadsheetRow a = this.row("1");
        final SpreadsheetRow b = this.row("2");
        final SpreadsheetRow c = this.row("3");

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

        final SpreadsheetRow a = this.row("1");
        final SpreadsheetRow b = this.row("2");
        final SpreadsheetRow c = this.row("3");
        final SpreadsheetRow d = this.row("4");

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
    public final SpreadsheetRowReference id() {
        return REFERENCE;
    }

    @Override
    public SpreadsheetRow value() {
        return this.row(this.id());
    }

    private SpreadsheetRow row(final String text) {
        return this.row(
            SpreadsheetSelection.parseRow(text)
        );
    }

    private SpreadsheetRow row(final SpreadsheetRowReference reference) {
        return reference.row();
    }

    // TypeNameTesting..................................................................

    @Override
    public String typeNameSuffix() {
        return SpreadsheetRowStore.class.getSimpleName();
    }
}
