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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.TypeNameTesting;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.text.FontWeight;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;

public abstract class SpreadsheetCellStoreTestCase<S extends SpreadsheetCellStore> implements SpreadsheetCellStoreTesting<S>,
        TypeNameTesting<S> {

    final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DOUBLE;

    final static SpreadsheetCellReference REFERENCE = SpreadsheetReferenceKind.RELATIVE
            .column(1)
            .setRow(SpreadsheetReferenceKind.RELATIVE.row(2));

    SpreadsheetCellStoreTestCase() {
        super();
    }

    @Test
    public final void testLoadUnknown() {
        this.loadFailCheck(REFERENCE);
    }

    @Test
    public final void testSaveAndLoad() {
        final S store = this.createStore();

        final SpreadsheetCellReference reference = this.cellReference(1, 2);
        final SpreadsheetCell cell = this.cell(reference);
        this.checkEquals(cell, store.save(cell), "incorrect key returned");

        assertSame(cell, store.loadOrFail(reference));
    }

    @Test
    public final void testSaveAndLoadAbsoluteCellReference() {
        final S store = this.createStore();

        final SpreadsheetCellReference reference = this.cellReference(1, 2);
        final SpreadsheetCell cell = this.cell(reference);
        this.checkEquals(cell, store.save(cell), "incorrect key returned");

        assertSame(cell, store.loadOrFail(reference.toAbsolute()));
    }

    @Test
    public final void testSaveDeleteLoad() {
        final S store = this.createStore();

        final SpreadsheetCellReference reference = this.cellReference(1, 2);
        final SpreadsheetCell cell = this.cell(reference);
        store.save(cell);
        store.delete(reference);

        this.loadFailCheck(store, reference);
    }

    @Test
    public final void testSaveDeleteLoadAbsoluteSpreadsheetCellReference() {
        final S store = this.createStore();

        final SpreadsheetCellReference reference = this.cellReference(1, 2);
        final SpreadsheetCell cell = this.cell(reference);
        store.save(cell);
        store.delete(reference.toAbsolute());

        this.loadFailCheck(store, reference);
    }

    @Test
    public final void testCount() {
        final S store = this.createStore();

        store.save(this.cell(1, 2));
        store.save(this.cell(3, 4));

        this.countAndCheck(store, 2);
    }

    @Test
    public final void testCount2() {
        final S store = this.createStore();

        final SpreadsheetCellReference cell = this.cellReference(1, 2);
        store.save(this.cell(cell));
        store.save(this.cell(3, 4));
        store.delete(cell);

        this.countAndCheck(store, 2 - 1);
    }

    @Test
    public final void testColumnsWhenEmpty() {
        final S store = this.createStore();

        this.columnsAndCheck(
                store,
                0
        );
    }

    @Test
    public final void testColumnsWithOnlyCellA2() {
        final S store = this.createStore();

        store.save(this.cell(1, 2));

        this.columnsAndCheck(
                store,
                1
        );
    }

    @Test
    public final void testColumns() {
        final S store = this.createStore();

        store.save(this.cell(1, 1));
        store.save(this.cell(99, 1));
        store.save(this.cell(98, 2));

        this.columnsAndCheck(
                store,
                99
        );
    }

    @Test
    public final void testRowsWhenEmpty() {
        final S store = this.createStore();

        this.rowsAndCheck(
                store,
                0
        );
    }

    @Test
    public final void testRowsWithA2() {
        final S store = this.createStore();

        store.save(
                this.cell(1, 2)
        );

        this.rowsAndCheck(
                store,
                2
        );
    }

    @Test
    public final void testRows() {
        final S store = this.createStore();

        store.save(this.cell(1, 2));
        store.save(this.cell(1, 99));
        store.save(this.cell(1, 5));

        this.rowsAndCheck(store, 99);
    }

    @Test
    public final void testRow() {
        final S store = this.createStore();

        final SpreadsheetCell a = this.cell(11, 1);
        final SpreadsheetCell b = this.cell(22, 1);
        final SpreadsheetCell c = this.cell(11, 2);
        final SpreadsheetCell d = this.cell(22, 2);

        store.save(a);
        store.save(b);
        store.save(c);
        store.save(d);

        checkCells("row 1", store.row(a.reference().row()), a, b);
        checkCells("row 2", store.row(c.reference().row()), c, d);
        checkCells("row 99", store.row(SpreadsheetSelection.parseRow("99")));
    }

    @Test
    public final void testRowAbsolute() {
        final S store = this.createStore();

        final SpreadsheetCell a = this.cell(11, 1);
        store.save(a);

        checkCells(
                "row 1",
                store.row(a.reference()
                        .row()
                        .setReferenceKind(SpreadsheetReferenceKind.ABSOLUTE)),
                a
        );
    }

    @Test
    public final void testColumn() {
        final S store = this.createStore();

        final SpreadsheetCell a = this.cell(1, 11);
        final SpreadsheetCell b = this.cell(1, 22);
        final SpreadsheetCell c = this.cell(2, 11);
        final SpreadsheetCell d = this.cell(2, 22);

        store.save(a);
        store.save(b);
        store.save(c);
        store.save(d);

        checkCells("column 1", store.column(a.reference().column()), a, b);
        checkCells("column 2", store.column(c.reference().column()), c, d);
        checkCells("column 99", store.column(SpreadsheetSelection.parseColumn("ZZ")));
    }

    @Test
    public final void testColumnAbsolute() {
        final S store = this.createStore();

        final SpreadsheetCell a = this.cell(1, 11);
        store.save(a);

        checkCells(
                "column 1",
                store.column(a.reference()
                        .column()
                        .setReferenceKind(SpreadsheetReferenceKind.ABSOLUTE)),
                a
        );
    }

    @Test
    public final void testIds() {
        final S store = this.createStore();

        final SpreadsheetCell a = this.cell("a1");
        final SpreadsheetCell b = this.cell("b2");
        final SpreadsheetCell c = this.cell("c3");

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

        final SpreadsheetCell a = this.cell("a1");
        final SpreadsheetCell b = this.cell("b2");
        final SpreadsheetCell c = this.cell("c3");
        final SpreadsheetCell d = this.cell("d4");

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

        final SpreadsheetCell a = this.cell("a1");
        final SpreadsheetCell b = this.cell("b2");
        final SpreadsheetCell c = this.cell("c3");

        store.save(a);
        store.save(b);
        store.save(c);

        this.valuesAndCheck(store,
                a.reference(),
                3,
                a, b, c);
    }

    @Test
    public final void testValuesWindow() {
        final S store = this.createStore();

        final SpreadsheetCell a = this.cell("a1");
        final SpreadsheetCell b = this.cell("b2");
        final SpreadsheetCell c = this.cell("c3");
        final SpreadsheetCell d = this.cell("d4");

        store.save(a);
        store.save(b);
        store.save(c);
        store.save(d);

        this.valuesAndCheck(store,
                b.reference(),
                2,
                b, c);
    }

    @Override
    public final SpreadsheetCellReference id() {
        return SpreadsheetSelection.A1;
    }

    @Override
    public SpreadsheetCell value() {
        return this.cell(this.id());
    }

    private void checkCells(final String message,
                            final Collection<SpreadsheetCell> cells,
                            final SpreadsheetCell... expected) {
        final Set<SpreadsheetCell> actual = Sets.sorted();
        actual.addAll(cells);

        final Set<SpreadsheetCell> expectedSets = Sets.sorted();
        expectedSets.addAll(Lists.of(expected));

        this.checkEquals(expectedSets, actual, message);
    }

    private SpreadsheetCell cell(final int column, final int row) {
        return this.cell(this.cellReference(column, row));
    }

    private SpreadsheetCell cell(final String reference) {
        return this.cell(SpreadsheetSelection.parseCell(reference));
    }

    private SpreadsheetCell cell(final SpreadsheetCellReference cellReference) {
        return cellReference.setFormula(
                        this.formula()
                )
                .setStyle(this.style())
                .setFormatPattern(this.formatPattern())
                .setFormatted(this.formatted());
    }

    final SpreadsheetCellReference cellReference(final int column, final int row) {
        return SpreadsheetSelection.cell(SpreadsheetReferenceKind.RELATIVE.column(column),
                SpreadsheetReferenceKind.RELATIVE.row(row));
    }

    final void rowsAndCheck(final SpreadsheetCellStore store, final int row) {
        this.checkEquals(row, store.rows(), () -> "rows for store=" + store);
    }

    final void columnsAndCheck(final SpreadsheetCellStore store, final int column) {
        this.checkEquals(column, store.columns(), () -> "columns for store=" + store);
    }

    private SpreadsheetFormula formula() {
        final String text = "1+2";

        return SpreadsheetFormula.EMPTY
                .setText(text)
                .setToken(Optional.of(
                        SpreadsheetParserToken.addition(
                                List.of(
                                        SpreadsheetParserToken.number(
                                                Lists.of(
                                                        SpreadsheetParserToken.digits("1", "1")
                                                ),
                                                "1"
                                        ),
                                        SpreadsheetParserToken.plusSymbol("+", "+"),
                                        SpreadsheetParserToken.number(
                                                Lists.of(
                                                        SpreadsheetParserToken.digits("2", "2")
                                                ),
                                                "2"
                                        )
                                ),
                                text
                        )
                ))
                .setExpression(Optional.of(
                        Expression.add(
                                Expression.value(
                                        EXPRESSION_NUMBER_KIND.one()
                                ),
                                Expression.value(
                                        EXPRESSION_NUMBER_KIND.create(2)
                                )
                        )
                ));
    }

    private TextStyle style() {
        return TextStyle.with(Maps.of(TextStylePropertyName.FONT_WEIGHT, FontWeight.BOLD));
    }

    @SuppressWarnings("SameReturnValue")
    private Optional<SpreadsheetFormatPattern> formatPattern() {
        return SpreadsheetCell.NO_FORMAT_PATTERN;
    }

    @SuppressWarnings("SameReturnValue")
    private Optional<TextNode> formatted() {
        return SpreadsheetCell.NO_FORMATTED_CELL;
    }

    // TypeNameTesting..................................................................

    @Override
    public String typeNameSuffix() {
        return SpreadsheetCellStore.class.getSimpleName();
    }
}
