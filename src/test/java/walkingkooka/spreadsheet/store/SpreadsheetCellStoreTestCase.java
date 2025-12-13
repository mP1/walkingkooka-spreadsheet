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
import walkingkooka.collect.set.SortedSets;
import walkingkooka.reflect.TypeNameTesting;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
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
    TypeNameTesting<S>,
    SpreadsheetMetadataTesting {

    final static SpreadsheetCellReference REFERENCE = SpreadsheetReferenceKind.RELATIVE
        .column(1)
        .setRow(SpreadsheetReferenceKind.RELATIVE.row(2));

    final static SpreadsheetMetadata METADATA = SpreadsheetMetadataTesting.METADATA_EN_AU.set(SpreadsheetMetadataPropertyName.DATE_PARSER, SpreadsheetPattern.parseDateParsePattern("d/m/y").spreadsheetParserSelector())
        .set(SpreadsheetMetadataPropertyName.DATE_TIME_PARSER, SpreadsheetPattern.parseDateTimeParsePattern("d/m/y h:mm").spreadsheetParserSelector())
        .set(SpreadsheetMetadataPropertyName.NUMBER_PARSER, SpreadsheetPattern.parseNumberParsePattern("#;#.#").spreadsheetParserSelector())
        .set(SpreadsheetMetadataPropertyName.TIME_PARSER, SpreadsheetPattern.parseTimeParsePattern("hh:mm").spreadsheetParserSelector());

    final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = METADATA.expressionNumberKind();


    SpreadsheetCellStoreTestCase() {
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

        this.loadAndCheck(
            store,
            reference
        );
    }

    @Test
    public final void testSaveDeleteLoadAbsoluteSpreadsheetCellReference() {
        final S store = this.createStore();

        final SpreadsheetCellReference reference = this.cellReference(1, 2);
        final SpreadsheetCell cell = this.cell(reference);
        store.save(cell);
        store.delete(reference.toAbsolute());

        this.loadAndCheck(
            store,
            reference
        );
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
    public final void testColumnCountWithOnlyCellA24() {
        final S store = this.createStore();

        store.save(this.cell(0, 2));
        store.save(this.cell(0, 23));

        this.columnCountAndCheck(
            store,
            1
        );
    }

    @Test
    public final void testColumnCount() {
        final S store = this.createStore();

        store.save(this.cell(1, 1));
        store.save(this.cell(99, 1));
        store.save(this.cell(98, 2));

        this.columnCountAndCheck(
            store,
            100
        );
    }

    @Test
    public final void testRowCountWithA24() {
        final S store = this.createStore();

        store.save(
            this.cell(0, 2)
        );
        store.save(
            this.cell(0, 23)
        );

        this.rowCountAndCheck(
            store,
            24
        );
    }

    @Test
    public final void testRowCount() {
        final S store = this.createStore();

        store.save(this.cell(1, 2));
        store.save(this.cell(1, 99));
        store.save(this.cell(1, 5));

        this.rowCountAndCheck(
            store,
            100
        );
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

        final SpreadsheetCell a = this.cell("a1");
        final SpreadsheetCell b = this.cell("b2");
        final SpreadsheetCell c = this.cell("c3");
        final SpreadsheetCell d = this.cell("d4");

        store.save(a);
        store.save(b);
        store.save(c);
        store.save(d);

        this.valuesAndCheck(store,
            1,
            2,
            b,
            c
        );
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
        final Set<SpreadsheetCell> actual = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);
        actual.addAll(cells);

        final Set<SpreadsheetCell> expectedSets = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);
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
            .setFormatter(this.formatter())
            .setFormattedValue(this.formattedValue());
    }

    final SpreadsheetCellReference cellReference(final int column, final int row) {
        return SpreadsheetSelection.cell(SpreadsheetReferenceKind.RELATIVE.column(column),
            SpreadsheetReferenceKind.RELATIVE.row(row));
    }

    private SpreadsheetFormula formula() {
        final String text = "1+2";

        return SpreadsheetFormula.EMPTY
            .setText(text)
            .setToken(Optional.of(
                SpreadsheetFormulaParserToken.addition(
                    List.of(
                        SpreadsheetFormulaParserToken.number(
                            Lists.of(
                                SpreadsheetFormulaParserToken.digits("1", "1")
                            ),
                            "1"
                        ),
                        SpreadsheetFormulaParserToken.plusSymbol("+", "+"),
                        SpreadsheetFormulaParserToken.number(
                            Lists.of(
                                SpreadsheetFormulaParserToken.digits("2", "2")
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
        return TextStyle.EMPTY.set(
            TextStylePropertyName.FONT_WEIGHT,
            FontWeight.BOLD
        );
    }

    @SuppressWarnings("SameReturnValue")
    private Optional<SpreadsheetFormatterSelector> formatter() {
        return SpreadsheetCell.NO_FORMATTER;
    }

    @SuppressWarnings("SameReturnValue")
    private Optional<TextNode> formattedValue() {
        return SpreadsheetCell.NO_FORMATTED_VALUE_CELL;
    }

    // TypeNameTesting..................................................................

    @Override
    public String typeNameSuffix() {
        return SpreadsheetCellStore.class.getSimpleName();
    }
}
