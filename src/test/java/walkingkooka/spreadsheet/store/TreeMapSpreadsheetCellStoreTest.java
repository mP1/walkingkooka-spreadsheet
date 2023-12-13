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
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetValueType;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangePath;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.FunctionExpressionName;
import walkingkooka.tree.text.Length;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertThrows;

final class TreeMapSpreadsheetCellStoreTest extends SpreadsheetCellStoreTestCase<TreeMapSpreadsheetCellStore> {


    // between..........................................................................................................

    @Test
    public void testBetween() {
        final TreeMapSpreadsheetCellStore store = this.createStore();

        final SpreadsheetCell a1 = SpreadsheetSelection.A1
                .setFormula(SpreadsheetFormula.EMPTY);
        store.save(a1);

        final SpreadsheetCell b2 = SpreadsheetSelection.parseCell("B2")
                .setFormula(SpreadsheetFormula.EMPTY);
        store.save(b2);

        final SpreadsheetCell b4 = SpreadsheetSelection.parseCell("B4")
                .setFormula(SpreadsheetFormula.EMPTY);
        store.save(b4);

        final SpreadsheetCell c3 = SpreadsheetSelection.parseCell("C3")
                .setFormula(SpreadsheetFormula.EMPTY);
        store.save(c3);

        final SpreadsheetCell d4 = SpreadsheetSelection.parseCell("D4")
                .setFormula(SpreadsheetFormula.EMPTY);
        store.save(d4);

        this.betweenAndCheck(
                store,
                b2.reference(),
                c3.reference(),
                b2,
                c3
        );
    }

    // loadCells........................................................................................................

    @Test
    public void testLoadCells() {
        final TreeMapSpreadsheetCellStore store = this.createStore();

        final SpreadsheetCell a1 = store.save(
                SpreadsheetSelection.A1
                        .setFormula(SpreadsheetFormula.EMPTY)
        );

        final SpreadsheetCell b2 = store.save(
                SpreadsheetSelection.parseCell("B2")
                        .setFormula(SpreadsheetFormula.EMPTY)
        );

        final SpreadsheetCell c3 = store.save(
                SpreadsheetSelection.parseCell("c3")
                        .setFormula(SpreadsheetFormula.EMPTY)
        );

        final SpreadsheetCell d4 = store.save(
                SpreadsheetSelection.parseCell("d4")
                        .setFormula(SpreadsheetFormula.EMPTY)
        );

        this.loadCellsAndCheck(
                store,
                SpreadsheetSelection.parseCellRange("A1:D4"),
                SpreadsheetCellRangePath.LRTD,
                0, // offset
                4, // max
                a1,
                b2,
                c3,
                d4
        );
    }

    @Test
    public void testLoadCellsMaxZero() {
        final TreeMapSpreadsheetCellStore store = this.createStore();

        final SpreadsheetCell a1 = store.save(
                SpreadsheetSelection.A1
                        .setFormula(SpreadsheetFormula.EMPTY)
        );

        final SpreadsheetCell b2 = store.save(
                SpreadsheetSelection.parseCell("B2")
                        .setFormula(SpreadsheetFormula.EMPTY)
        );

        final SpreadsheetCell c3 = store.save(
                SpreadsheetSelection.parseCell("c3")
                        .setFormula(SpreadsheetFormula.EMPTY)
        );

        final SpreadsheetCell d4 = store.save(
                SpreadsheetSelection.parseCell("d4")
                        .setFormula(SpreadsheetFormula.EMPTY)
        );

        this.loadCellsAndCheck(
                store,
                SpreadsheetSelection.parseCellRange("A1:D4"),
                SpreadsheetCellRangePath.LRTD,
                0, // offset
                0 // max
        );
    }

    @Test
    public void testLoadCellsMaxLess() {
        final TreeMapSpreadsheetCellStore store = this.createStore();

        final SpreadsheetCell a1 = store.save(
                SpreadsheetSelection.A1
                        .setFormula(SpreadsheetFormula.EMPTY)
        );

        final SpreadsheetCell b2 = store.save(
                SpreadsheetSelection.parseCell("B2")
                        .setFormula(SpreadsheetFormula.EMPTY)
        );

        final SpreadsheetCell c3 = store.save(
                SpreadsheetSelection.parseCell("c3")
                        .setFormula(SpreadsheetFormula.EMPTY)
        );

        final SpreadsheetCell d4 = store.save(
                SpreadsheetSelection.parseCell("d4")
                        .setFormula(SpreadsheetFormula.EMPTY)
        );

        this.loadCellsAndCheck(
                store,
                SpreadsheetSelection.parseCellRange("A1:D4"),
                SpreadsheetCellRangePath.LRTD,
                0, // offset
                3, // max
                a1,
                b2,
                c3
        );
    }

    @Test
    public void testLoadCellsRLTD() {
        final TreeMapSpreadsheetCellStore store = this.createStore();

        final SpreadsheetCell a1 = store.save(
                SpreadsheetSelection.A1
                        .setFormula(SpreadsheetFormula.EMPTY)
        );

        final SpreadsheetCell b1 = store.save(
                SpreadsheetSelection.parseCell("B1")
                        .setFormula(SpreadsheetFormula.EMPTY)
        );

        final SpreadsheetCell c1 = store.save(
                SpreadsheetSelection.parseCell("c1")
                        .setFormula(SpreadsheetFormula.EMPTY)
        );

        final SpreadsheetCell d1 = store.save(
                SpreadsheetSelection.parseCell("d1")
                        .setFormula(SpreadsheetFormula.EMPTY)
        );

        this.loadCellsAndCheck(
                store,
                SpreadsheetSelection.parseCellRange("A1:D4"),
                SpreadsheetCellRangePath.RLTD,
                0, // offset
                4, // max
                d1,
                c1,
                b1,
                a1
        );
    }

    @Test
    public void testLoadCellsRLTDOffset() {
        final TreeMapSpreadsheetCellStore store = this.createStore();

        final SpreadsheetCell a1 = store.save(
                SpreadsheetSelection.A1
                        .setFormula(SpreadsheetFormula.EMPTY)
        );

        final SpreadsheetCell b1 = store.save(
                SpreadsheetSelection.parseCell("B1")
                        .setFormula(SpreadsheetFormula.EMPTY)
        );

        final SpreadsheetCell c1 = store.save(
                SpreadsheetSelection.parseCell("c1")
                        .setFormula(SpreadsheetFormula.EMPTY)
        );

        final SpreadsheetCell d1 = store.save(
                SpreadsheetSelection.parseCell("d1")
                        .setFormula(SpreadsheetFormula.EMPTY)
        );

        this.loadCellsAndCheck(
                store,
                SpreadsheetSelection.parseCellRange("A1:D4"),
                SpreadsheetCellRangePath.RLTD,
                1, // offset
                4, // max
                c1,
                b1,
                a1
        );
    }

    @Test
    public void testLoadCellsRLTDOffset2() {
        final TreeMapSpreadsheetCellStore store = this.createStore();

        final SpreadsheetCell a1 = store.save(
                SpreadsheetSelection.A1
                        .setFormula(SpreadsheetFormula.EMPTY)
        );

        final SpreadsheetCell b1 = store.save(
                SpreadsheetSelection.parseCell("B1")
                        .setFormula(SpreadsheetFormula.EMPTY)
        );

        final SpreadsheetCell c1 = store.save(
                SpreadsheetSelection.parseCell("c1")
                        .setFormula(SpreadsheetFormula.EMPTY)
        );

        final SpreadsheetCell d1 = store.save(
                SpreadsheetSelection.parseCell("d1")
                        .setFormula(SpreadsheetFormula.EMPTY)
        );

        this.loadCellsAndCheck(
                store,
                SpreadsheetSelection.parseCellRange("A1:D4"),
                SpreadsheetCellRangePath.RLTD,
                2, // offset
                4, // max
                b1,
                a1
        );
    }

    @Test
    public void testLoadCellsRLTDMax() {
        final TreeMapSpreadsheetCellStore store = this.createStore();

        final SpreadsheetCell a1 = store.save(
                SpreadsheetSelection.A1
                        .setFormula(SpreadsheetFormula.EMPTY)
        );

        final SpreadsheetCell b1 = store.save(
                SpreadsheetSelection.parseCell("B1")
                        .setFormula(SpreadsheetFormula.EMPTY)
        );

        final SpreadsheetCell c1 = store.save(
                SpreadsheetSelection.parseCell("c1")
                        .setFormula(SpreadsheetFormula.EMPTY)
        );

        final SpreadsheetCell d1 = store.save(
                SpreadsheetSelection.parseCell("d1")
                        .setFormula(SpreadsheetFormula.EMPTY)
        );

        this.loadCellsAndCheck(
                store,
                SpreadsheetSelection.parseCellRange("A1:D4"),
                SpreadsheetCellRangePath.RLTD,
                0, // offset
                3, // max
                d1,
                c1,
                b1
        );
    }

    // deleteCells.....................................................................................................

    @Test
    public void testDeleteCells() {
        final TreeMapSpreadsheetCellStore store = this.createStore();

        store.save(
                SpreadsheetSelection.A1
                        .setFormula(SpreadsheetFormula.EMPTY)
        );

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("B2");
        store.save(
                b2.setFormula(SpreadsheetFormula.EMPTY)
        );

        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("c3");
        store.save(
                c3.setFormula(SpreadsheetFormula.EMPTY)
        );

        store.save(
                SpreadsheetSelection.parseCell("D4")
                        .setFormula(SpreadsheetFormula.EMPTY)
        );

        store.deleteCells(
                SpreadsheetSelection.parseCellRange("B2:C3")
        );

        this.checkEquals(
                2,
                store.count()
        );

        this.loadFailCheck(store, b2);
        this.loadFailCheck(store, c3);
    }

    // clearParsedFormulaExpressions..............................................................................................

    @Test
    public void testClearParsedFormulaExpressions() {
        final TreeMapSpreadsheetCellStore store = this.createStore();

        final SpreadsheetCell withoutParsedFormula = SpreadsheetSelection.A1
                .setFormula(
                        SpreadsheetFormula.EMPTY.setText("hello()")
                );

        store.save(
                withoutParsedFormula.setFormula(
                        withoutParsedFormula.formula()
                                .setExpression(
                                        Optional.of(
                                                Expression.namedFunction(FunctionExpressionName.with("hello"))
                                        )
                                )
                )
        );

        store.clearParsedFormulaExpressions();

        this.loadAndCheck(
                store,
                withoutParsedFormula.reference(),
                withoutParsedFormula
        );
    }

    @Test
    public void testClearParsedFormulaExpressions2() {
        final TreeMapSpreadsheetCellStore store = this.createStore();

        final SpreadsheetCell withoutParsedFormula = SpreadsheetSelection.A1
                .setFormula(
                        SpreadsheetFormula.EMPTY.setText("hello()")
                );

        store.save(
                withoutParsedFormula.setFormula(
                        withoutParsedFormula.formula()
                                .setExpression(
                                        Optional.of(
                                                Expression.namedFunction(FunctionExpressionName.with("hello"))
                                        )
                                )
                )
        );

        final SpreadsheetCell withoutParsedFormula2 = SpreadsheetSelection.parseCell("B2")
                .setFormula(
                        SpreadsheetFormula.EMPTY.setText("hello2()")
                );

        store.save(
                withoutParsedFormula2
        );

        final Set<SpreadsheetCellReference> cleared = Sets.ordered();
        store.addSaveWatcher(s -> cleared.add(s.reference()));

        store.clearParsedFormulaExpressions();

        this.loadAndCheck(
                store,
                withoutParsedFormula.reference(),
                withoutParsedFormula
        );

        this.checkEquals(
                Sets.of(withoutParsedFormula.reference()),
                cleared
        );
    }

    // clearFormatted...................................................................................................

    @Test
    public void testClearFormatted() {
        final TreeMapSpreadsheetCellStore store = this.createStore();

        final SpreadsheetCell withoutFormatted = SpreadsheetSelection.A1
                .setFormula(
                        SpreadsheetFormula.EMPTY.setText("hello()")
                                .setExpression(
                                        Optional.of(
                                                Expression.namedFunction(FunctionExpressionName.with("hello"))
                                        )
                                )
                );

        store.save(
                withoutFormatted.setFormatted(
                        Optional.of(
                                TextNode.text("123")
                        )
                )
        );

        store.clearFormatted();

        this.loadAndCheck(
                store,
                withoutFormatted.reference(),
                withoutFormatted
        );
    }

    @Test
    public void testClearFormatted2() {
        final TreeMapSpreadsheetCellStore store = this.createStore();

        final SpreadsheetCell withoutFormatted = SpreadsheetSelection.A1
                .setFormula(
                        SpreadsheetFormula.EMPTY.setText("hello()")
                                .setExpression(
                                        Optional.of(
                                                Expression.namedFunction(FunctionExpressionName.with("hello"))
                                        )
                                )
                );

        store.save(
                withoutFormatted.setFormatted(
                        Optional.of(
                                TextNode.text("123")
                        )
                )
        );

        final SpreadsheetCell withoutFormatted2 = SpreadsheetSelection.parseCell("B2")
                .setFormula(
                        SpreadsheetFormula.EMPTY.setText("hello2()")
                );

        store.save(
                withoutFormatted2
        );

        final Set<SpreadsheetCellReference> cleared = Sets.ordered();
        store.addSaveWatcher(s -> cleared.add(s.reference()));

        store.clearFormatted();

        this.loadAndCheck(
                store,
                withoutFormatted.reference(),
                withoutFormatted
        );

        this.loadAndCheck(
                store,
                withoutFormatted2.reference(),
                withoutFormatted2
        );

        this.checkEquals(
                Sets.of(withoutFormatted.reference()),
                cleared
        );
    }

    // maxColumnWidth...................................................................................................

    @Test
    public void testMaxColumnWidthWithNullFails() {
        assertThrows(NullPointerException.class, () -> this.createStore().maxColumnWidth(null));
    }

    @Test
    public void testMaxColumnWidthColumnWithoutCells() {
        final TreeMapSpreadsheetCellStore store = this.createStore();
        maxColumnWidthAndCheck(store, SpreadsheetSelection.parseColumn("A"), 0);
    }

    @Test
    public void testMaxColumnWidthWithCells() {
        final TreeMapSpreadsheetCellStore store = this.createStore();
        store.save(cellWithWidth("C3", 50.0));
        store.save(cellWithWidth("D4", 150.0));

        this.maxColumnWidthAndCheck(store, SpreadsheetSelection.parseColumn("C"), 50.0);
    }

    @Test
    public void testMaxColumnWidthWithCellsMissingWidth() {
        final TreeMapSpreadsheetCellStore store = this.createStore();
        store.save(cellWithWidth("C3", 50.0));
        store.save(cellWithWidth("C4", 0));

        this.maxColumnWidthAndCheck(store, SpreadsheetSelection.parseColumn("C"), 50.0);
    }

    @Test
    public void testMaxColumnWidthWithSeveralCells() {
        final TreeMapSpreadsheetCellStore store = this.createStore();
        store.save(cellWithWidth("C3", 50.0));
        store.save(cellWithWidth("C4", 40.0));
        store.save(cellWithWidth("C5", 99.0));
        store.save(cellWithWidth("D4", 150.0));

        this.maxColumnWidthAndCheck(store, SpreadsheetSelection.parseColumn("C"), 99.0);
    }

    private SpreadsheetCell cellWithWidth(final String cellReference,
                                          final double pixels) {
        SpreadsheetCell cell = SpreadsheetSelection.parseCell(cellReference)
                .setFormula(SpreadsheetFormula.EMPTY
                        .setText("1+2")
                );
        if (pixels > 0) {
            cell = cell.setStyle(TextStyle.EMPTY
                    .set(TextStylePropertyName.WIDTH, Length.pixel(pixels)));
        }
        return cell;
    }

    private void maxColumnWidthAndCheck(final TreeMapSpreadsheetCellStore store,
                                        final SpreadsheetColumnReference column,
                                        final double expected) {
        this.checkEquals(expected,
                store.maxColumnWidth(column),
                () -> "maxColumnWidth of " + column + " store=" + store);
    }

    // maxRowHeight...................................................................................................

    @Test
    public void testMaxRowHeightWithNullFails() {
        assertThrows(NullPointerException.class, () -> this.createStore().maxRowHeight(null));
    }

    @Test
    public void testMaxRowHeightRowWithoutCells() {
        final TreeMapSpreadsheetCellStore store = this.createStore();
        maxRowHeightAndCheck(store, SpreadsheetSelection.parseRow("9"), 0);
    }

    @Test
    public void testMaxRowHeightWithCells() {
        final TreeMapSpreadsheetCellStore store = this.createStore();
        store.save(cellWithHeight("C3", 50.0));
        store.save(cellWithHeight("D4", 150.0));

        this.maxRowHeightAndCheck(store, SpreadsheetSelection.parseRow("3"), 50.0);
    }

    @Test
    public void testMaxRowHeightWithCellsMissingWidth() {
        final TreeMapSpreadsheetCellStore store = this.createStore();
        store.save(cellWithHeight("C3", 50.0));
        store.save(cellWithHeight("C4", 0));

        this.maxRowHeightAndCheck(store, SpreadsheetSelection.parseRow("3"), 50.0);
    }

    @Test
    public void testMaxRowHeightWithSeveralCells() {
        final TreeMapSpreadsheetCellStore store = this.createStore();
        store.save(cellWithHeight("C3", 50.0));
        store.save(cellWithHeight("D3", 40.0));
        store.save(cellWithHeight("E3", 99.0));
        store.save(cellWithHeight("Z99", 150.0));

        this.maxRowHeightAndCheck(store, SpreadsheetSelection.parseRow("3"), 99.0);
    }

    private SpreadsheetCell cellWithHeight(final String cellReference,
                                           final double pixels) {
        SpreadsheetCell cell = SpreadsheetSelection.parseCell(cellReference)
                .setFormula(
                        SpreadsheetFormula.EMPTY
                                .setText("1+2")
                );
        if (pixels > 0) {
            cell = cell.setStyle(TextStyle.EMPTY
                    .set(TextStylePropertyName.HEIGHT, Length.pixel(pixels)));
        }
        return cell;
    }

    private void maxRowHeightAndCheck(final TreeMapSpreadsheetCellStore store,
                                      final SpreadsheetRowReference row,
                                      final double expected) {
        this.checkEquals(expected,
                store.maxRowHeight(row),
                () -> "maxRowHeight of " + row + " store=" + store);
    }

    // findCellsWithValueType...........................................................................................

    @Test
    public void testFindCellsWithValueType() {
        final TreeMapSpreadsheetCellStore store = this.createStore();

        final SpreadsheetCell a1 = store.save(
                SpreadsheetSelection.A1.setFormula(
                        SpreadsheetFormula.EMPTY.setText("=1")
                                .setValue(
                                        Optional.of(1)
                                )
                )
        );

        final SpreadsheetCell a2 = store.save(
                SpreadsheetSelection.parseCell("A2")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("='ABC")
                                        .setValue(
                                                Optional.of("ABC")
                                        )
                        )
        );

        final SpreadsheetCell a3 = store.save(
                SpreadsheetSelection.parseCell("A3")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("=2+3")
                                        .setValue(
                                                Optional.of(5)
                                        )
                        )
        );

        // ignored because value wrong type
        final SpreadsheetCell a4 = store.save(
                SpreadsheetSelection.parseCell("A4")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("=true()")
                                        .setValue(
                                                Optional.of(true)
                                        )
                        )
        );

        // ignored because value missing
        final SpreadsheetCell a5 = store.save(
                SpreadsheetSelection.parseCell("A5")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("=555")
                        )
        );

        // ignore because out of range
        final SpreadsheetCell a7 = store.save(
                SpreadsheetSelection.parseCell("A7")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("=678")
                                        .setValue(
                                                Optional.of(678)
                                        )
                        )
        );

        this.findCellsWithValueTypeAndCheck(
                store,
                SpreadsheetSelection.parseCellRange("A1:A6"),
                SpreadsheetValueType.NUMBER,
                100,
                a1,
                a3
        );
    }

    @Test
    public void testFindCellsWithValueTypeExpressionNumber() {
        final TreeMapSpreadsheetCellStore store = this.createStore();

        final SpreadsheetCell a1 = store.save(
                SpreadsheetSelection.A1.setFormula(
                        SpreadsheetFormula.EMPTY.setText("=1")
                                .setValue(
                                        Optional.of(
                                                ExpressionNumberKind.BIG_DECIMAL.create(5)
                                        )
                                )
                )
        );

        final SpreadsheetCell a2 = store.save(
                SpreadsheetSelection.parseCell("A2")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("='ABC")
                                        .setValue(
                                                Optional.of("ABC")
                                        )
                        )
        );

        final SpreadsheetCell a3 = store.save(
                SpreadsheetSelection.parseCell("A3")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("=2+3")
                                        .setValue(
                                                Optional.of(
                                                        ExpressionNumberKind.DOUBLE.create(5)
                                                )
                                        )
                        )
        );

        // ignored because value wrong type
        final SpreadsheetCell a4 = store.save(
                SpreadsheetSelection.parseCell("A4")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("=true()")
                                        .setValue(
                                                Optional.of(true)
                                        )
                        )
        );

        // ignored because value missing
        final SpreadsheetCell a5 = store.save(
                SpreadsheetSelection.parseCell("A5")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("=555")
                        )
        );

        // ignore because out of range
        final SpreadsheetCell a7 = store.save(
                SpreadsheetSelection.parseCell("A7")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("=678")
                                        .setValue(
                                                Optional.of(678)
                                        )
                        )
        );

        this.findCellsWithValueTypeAndCheck(
                store,
                SpreadsheetSelection.parseCellRange("A1:A6"),
                SpreadsheetValueType.NUMBER,
                100,
                a1,
                a3
        );
    }

    @Test
    public void testFindCellsWithValueTypeWildcard() {
        final TreeMapSpreadsheetCellStore store = this.createStore();

        final SpreadsheetCell a1 = store.save(
                SpreadsheetSelection.A1.setFormula(
                        SpreadsheetFormula.EMPTY.setText("=1")
                                .setValue(
                                        Optional.of(1)
                                )
                )
        );

        final SpreadsheetCell a2 = store.save(
                SpreadsheetSelection.parseCell("A2")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("='ABC")
                                        .setValue(
                                                Optional.of("ABC")
                                        )
                        )
        );

        final SpreadsheetCell a3 = store.save(
                SpreadsheetSelection.parseCell("A3")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("=2+3")
                                        .setValue(
                                                Optional.of(5)
                                        )
                        )
        );

        final SpreadsheetCell a4 = store.save(
                SpreadsheetSelection.parseCell("A4")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("=true()")
                                        .setValue(
                                                Optional.of(true)
                                        )
                        )
        );

        // ignored because value missing
        final SpreadsheetCell a5 = store.save(
                SpreadsheetSelection.parseCell("A5")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("=555")
                        )
        );

        // ignore because out of range
        final SpreadsheetCell a7 = store.save(
                SpreadsheetSelection.parseCell("A7")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("=678")
                                        .setValue(
                                                Optional.of(678)
                                        )
                        )
        );

        this.findCellsWithValueTypeAndCheck(
                store,
                SpreadsheetSelection.parseCellRange("A1:A6"),
                SpreadsheetValueType.ANY,
                100,
                a1,
                a2,
                a3,
                a4
        );
    }

    @Test
    public void testFindCellsWithValueTypeWildcardAndMax() {
        final TreeMapSpreadsheetCellStore store = this.createStore();

        final SpreadsheetCell a1 = store.save(
                SpreadsheetSelection.A1.setFormula(
                        SpreadsheetFormula.EMPTY.setText("=1")
                                .setValue(
                                        Optional.of(1)
                                )
                )
        );

        final SpreadsheetCell a2 = store.save(
                SpreadsheetSelection.parseCell("A2")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("='ABC")
                                        .setValue(
                                                Optional.of("ABC")
                                        )
                        )
        );

        final SpreadsheetCell a3 = store.save(
                SpreadsheetSelection.parseCell("A3")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("=2+3")
                                        .setValue(
                                                Optional.of(5)
                                        )
                        )
        );

        final SpreadsheetCell a4 = store.save(
                SpreadsheetSelection.parseCell("A4")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("=true()")
                                        .setValue(
                                                Optional.of(true)
                                        )
                        )
        );

        // ignored because value missing
        final SpreadsheetCell a5 = store.save(
                SpreadsheetSelection.parseCell("A5")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("=555")
                        )
        );

        // ignore because out of range
        final SpreadsheetCell a7 = store.save(
                SpreadsheetSelection.parseCell("A7")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("=678")
                                        .setValue(
                                                Optional.of(678)
                                        )
                        )
        );

        this.findCellsWithValueTypeAndCheck(
                store,
                SpreadsheetSelection.parseCellRange("A1:A6"),
                SpreadsheetValueType.ANY,
                3,
                a1,
                a2,
                a3 // a4 excluded because of max
        );
    }

    // countCellsWithValueType.........................................................................................

    @Test
    public void testCountCellsWithValueType() {
        final TreeMapSpreadsheetCellStore store = this.createStore();

        final SpreadsheetCell a1 = store.save(
                SpreadsheetSelection.A1.setFormula(
                        SpreadsheetFormula.EMPTY.setText("=1")
                                .setValue(
                                        Optional.of(1)
                                )
                )
        );

        final SpreadsheetCell a2 = store.save(
                SpreadsheetSelection.parseCell("A2")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("='ABC")
                                        .setValue(
                                                Optional.of("ABC")
                                        )
                        )
        );

        final SpreadsheetCell a3 = store.save(
                SpreadsheetSelection.parseCell("A3")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("=2+3")
                                        .setValue(
                                                Optional.of(5)
                                        )
                        )
        );

        // ignored because value wrong type
        final SpreadsheetCell a4 = store.save(
                SpreadsheetSelection.parseCell("A4")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("=true()")
                                        .setValue(
                                                Optional.of(true)
                                        )
                        )
        );

        // ignored because value missing
        final SpreadsheetCell a5 = store.save(
                SpreadsheetSelection.parseCell("A5")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("=555")
                        )
        );

        // ignore because out of range
        final SpreadsheetCell a7 = store.save(
                SpreadsheetSelection.parseCell("A7")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("=678")
                                        .setValue(
                                                Optional.of(678)
                                        )
                        )
        );

        this.countCellsWithValueTypeAndCheck(
                store,
                SpreadsheetSelection.parseCellRange("A1:A6"),
                SpreadsheetValueType.NUMBER,
                2
        );
    }

    @Test
    public void testCountCellsWithValueTypeWildcard() {
        final TreeMapSpreadsheetCellStore store = this.createStore();

        final SpreadsheetCell a1 = store.save(
                SpreadsheetSelection.A1.setFormula(
                        SpreadsheetFormula.EMPTY.setText("=1")
                                .setValue(
                                        Optional.of(1)
                                )
                )
        );

        final SpreadsheetCell a2 = store.save(
                SpreadsheetSelection.parseCell("A2")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("='ABC")
                                        .setValue(
                                                Optional.of("ABC")
                                        )
                        )
        );

        // ignored because has no value
        final SpreadsheetCell a3 = store.save(
                SpreadsheetSelection.parseCell("A3")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("='ABC")
                        )
        );

        this.countCellsWithValueTypeAndCheck(
                store,
                SpreadsheetSelection.parseCellRange("A1:A6"),
                SpreadsheetValueType.ANY,
                2
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final TreeMapSpreadsheetCellStore store = this.createStore();
        store.save(
                SpreadsheetSelection.A1
                        .setFormula(
                                SpreadsheetFormula.EMPTY
                                        .setText("1+2")
                        )
        );

        this.toStringAndCheck(
                store,
                "[A1 1+2]"
        );
    }

    @Override
    public TreeMapSpreadsheetCellStore createStore() {
        return TreeMapSpreadsheetCellStore.create();
    }

    @Override
    public Class<TreeMapSpreadsheetCellStore> type() {
        return TreeMapSpreadsheetCellStore.class;
    }

    // TypeNameTesting..................................................................

    @Override
    public String typeNamePrefix() {
        return TreeMap.class.getSimpleName();
    }
}
