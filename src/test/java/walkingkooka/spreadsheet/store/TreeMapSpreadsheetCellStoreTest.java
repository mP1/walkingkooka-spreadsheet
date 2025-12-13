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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReferencePath;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetValueType;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.text.Length;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertThrows;

final class TreeMapSpreadsheetCellStoreTest extends SpreadsheetCellStoreTestCase<TreeMapSpreadsheetCellStore>
    implements HashCodeEqualsDefinedTesting2<TreeMapSpreadsheetCellStore> {

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

    // loadCellRange....................................................................................................

    @Test
    public void testLoadCellRange() {
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

        this.loadCellRangeAndCheck(
            store,
            SpreadsheetSelection.parseCellRange("A1:D4"),
            SpreadsheetCellRangeReferencePath.LRTD,
            0, // offset
            4, // count
            a1,
            b2,
            c3,
            d4
        );
    }

    @Test
    public void testLoadCellRangeMixedReferenceKind() {
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
            SpreadsheetSelection.parseCell("$C3")
                .setFormula(SpreadsheetFormula.EMPTY)
        );

        final SpreadsheetCell d4 = store.save(
            SpreadsheetSelection.parseCell("$D$4")
                .setFormula(SpreadsheetFormula.EMPTY)
        );

        this.loadCellRangeAndCheck(
            store,
            SpreadsheetSelection.parseCellRange("A1:$D$4"),
            SpreadsheetCellRangeReferencePath.LRTD,
            0, // offset
            4, // count
            a1,
            b2,
            c3,
            d4
        );
    }


    @Test
    public void testLoadCellRangeOppositeReferenceKind() {
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
            SpreadsheetSelection.parseCell("C3")
                .setFormula(SpreadsheetFormula.EMPTY)
        );

        final SpreadsheetCell d4 = store.save(
            SpreadsheetSelection.parseCell("D4")
                .setFormula(SpreadsheetFormula.EMPTY)
        );

        this.loadCellRangeAndCheck(
            store,
            SpreadsheetSelection.parseCellRange("$A$1:$D$4"),
            SpreadsheetCellRangeReferencePath.LRTD,
            0, // offset
            4, // count
            a1,
            b2,
            c3,
            d4
        );
    }

    @Test
    public void testLoadCellRangeCountZero() {
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

        this.loadCellRangeAndCheck(
            store,
            SpreadsheetSelection.parseCellRange("A1:D4"),
            SpreadsheetCellRangeReferencePath.LRTD,
            0, // offset
            0 // count
        );
    }

    @Test
    public void testLoadCellRangeCountLess() {
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

        this.loadCellRangeAndCheck(
            store,
            SpreadsheetSelection.parseCellRange("A1:D4"),
            SpreadsheetCellRangeReferencePath.LRTD,
            0, // offset
            3, // count
            a1,
            b2,
            c3
        );
    }

    @Test
    public void testLoadCellRangeRLTD() {
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

        this.loadCellRangeAndCheck(
            store,
            SpreadsheetSelection.parseCellRange("A1:D4"),
            SpreadsheetCellRangeReferencePath.RLTD,
            0, // offset
            4, // count
            d1,
            c1,
            b1,
            a1
        );
    }

    @Test
    public void testLoadCellRangeRLTDOffset() {
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

        this.loadCellRangeAndCheck(
            store,
            SpreadsheetSelection.parseCellRange("A1:D4"),
            SpreadsheetCellRangeReferencePath.RLTD,
            1, // offset
            3, // count
            c1,
            b1,
            a1
        );
    }

    @Test
    public void testLoadCellRangeRLTDOffset2() {
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

        this.loadCellRangeAndCheck(
            store,
            SpreadsheetSelection.parseCellRange("A1:D4"),
            SpreadsheetCellRangeReferencePath.RLTD,
            2, // offset
            2, // count
            b1,
            a1
        );
    }

    @Test
    public void testLoadCellRangeRLTDCount() {
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

        this.loadCellRangeAndCheck(
            store,
            SpreadsheetSelection.parseCellRange("A1:D4"),
            SpreadsheetCellRangeReferencePath.RLTD,
            0, // offset
            3, // count
            d1,
            c1,
            b1
        );
    }

    @Test
    public void testLoadCellRangeBULRCount() {
        final TreeMapSpreadsheetCellStore store = this.createStore();

        final SpreadsheetCell a1 = store.save(
            SpreadsheetSelection.A1
                .setFormula(SpreadsheetFormula.EMPTY)
        );

        final SpreadsheetCell a2 = store.save(
            SpreadsheetSelection.parseCell("a2")
                .setFormula(SpreadsheetFormula.EMPTY)
        );

        final SpreadsheetCell a3 = store.save(
            SpreadsheetSelection.parseCell("a3")
                .setFormula(SpreadsheetFormula.EMPTY)
        );

        final SpreadsheetCell b1 = store.save(
            SpreadsheetSelection.parseCell("b1")
                .setFormula(SpreadsheetFormula.EMPTY)
        );

        final SpreadsheetCell b2 = store.save(
            SpreadsheetSelection.parseCell("b2")
                .setFormula(SpreadsheetFormula.EMPTY)
        );

        this.loadCellRangeAndCheck(
            store,
            SpreadsheetSelection.parseCellRange("A1:D4"),
            SpreadsheetCellRangeReferencePath.BULR,
            0, // offset
            5, // count
            a3,
            a2,
            a1,
            b2,
            b1
        );
    }

    @Test
    public void testLoadCellRangeAllLrtd() {
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

        final SpreadsheetCell last = store.save(
            SpreadsheetReferenceKind.RELATIVE.lastColumn()
                .setRow(SpreadsheetReferenceKind.RELATIVE.lastRow()
                ).setFormula(SpreadsheetFormula.EMPTY)
        );

        this.loadCellRangeAndCheck(
            store,
            SpreadsheetSelection.parseCellRange("A1:" + last.reference()),
            SpreadsheetCellRangeReferencePath.LRTD,
            0, // offset
            4, // count
            a1,
            b1,
            c1,
            last
        );
    }

    @Test
    public void testLoadCellRangeAllBurl() {
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

        final SpreadsheetCell last = store.save(
            SpreadsheetReferenceKind.RELATIVE.lastColumn()
                .setRow(SpreadsheetReferenceKind.RELATIVE.lastRow()
                ).setFormula(SpreadsheetFormula.EMPTY)
        );

        this.loadCellRangeAndCheck(
            store,
            SpreadsheetSelection.parseCellRange("A1:" + last.reference()),
            SpreadsheetCellRangeReferencePath.BULR,
            0, // offset
            4, // count
            last,
            c1,
            b1,
            a1
        );
    }

    // save.............................................................................................................

    @Test
    public void testLoadCellRangeWithinSaveCellWithSaveWatcher() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setText("'Hello")
        );

        final Set<SpreadsheetCell> loaded = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);

        final TreeMapSpreadsheetCellStore store = this.createStore();

        store.addSaveWatcher(
            (s) -> loaded.addAll(
                store.loadCellRange(
                    SpreadsheetSelection.A1.toCellRange()
                )
            )
        );
        store.save(a1);

        this.checkEquals(
            Sets.of(a1),
            loaded
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

        this.loadAndCheck(store, b2);
        this.loadAndCheck(store, c3);
    }

    @Test
    public void testLoadCellRangeWithinDeleteCellWithDeleteWatcher() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setText("'Hello")
        );

        final TreeMapSpreadsheetCellStore store = this.createStore();
        store.save(a1);

        final Set<SpreadsheetCell> loaded = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);

        store.addDeleteWatcher(
            (s) -> loaded.addAll(
                store.loadCellRange(
                    SpreadsheetSelection.A1.toCellRange()
                )
            )
        );

        store.delete(a1.reference());

        this.checkEquals(
            Sets.empty(),
            loaded
        );
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
                            Expression.namedFunction(
                                SpreadsheetExpressionFunctions.name("hello")
                            )
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
                            Expression.namedFunction(
                                SpreadsheetExpressionFunctions.name("hello")
                            )
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
                            Expression.namedFunction(
                                SpreadsheetExpressionFunctions.name("hello")
                            )
                        )
                    )
            );

        store.save(
            withoutFormatted.setFormattedValue(
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
                            Expression.namedFunction(
                                SpreadsheetExpressionFunctions.name("hello")
                            )
                        )
                    )
            );

        store.save(
            withoutFormatted.setFormattedValue(
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

    // maxRowHeight.....................................................................................................

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

    // nextEmptyColumn..................................................................................................

    @Test
    public void testNextEmptyColumnWhenEmptyRow() {
        final TreeMapSpreadsheetCellStore store = this.createStore();
        store.save(
            SpreadsheetSelection.A1
                .setFormula(
                    SpreadsheetFormula.EMPTY
                        .setText("1+2")
                )
        );

        this.nextEmptyColumnAndCheck(
            store,
            SpreadsheetSelection.parseRow("2"),
            SpreadsheetReferenceKind.RELATIVE.firstColumn()
        );
    }

    @Test
    public void testNextEmptyColumnWhenSeveralRows() {
        final TreeMapSpreadsheetCellStore store = this.createStore();
        store.save(
            SpreadsheetSelection.A1
                .setFormula(
                    SpreadsheetFormula.EMPTY
                        .setText("1")
                )
        );
        store.save(
            SpreadsheetSelection.parseCell("B2")
                .setFormula(
                    SpreadsheetFormula.EMPTY
                        .setText("22")
                )
        );
        store.save(
            SpreadsheetSelection.parseCell("B3")
                .setFormula(
                    SpreadsheetFormula.EMPTY
                        .setText("222")
                )
        );
        store.save(
            SpreadsheetSelection.parseCell("C3")
                .setFormula(
                    SpreadsheetFormula.EMPTY
                        .setText("3")
                )
        );

        this.nextEmptyColumnAndCheck(
            store,
            SpreadsheetSelection.parseRow("3"),
            SpreadsheetSelection.parseColumn("D")
        );
    }

    @Test
    public void testNextEmptyColumnWhenFull() {
        final TreeMapSpreadsheetCellStore store = this.createStore();
        store.save(
            SpreadsheetSelection.A1
                .setFormula(
                    SpreadsheetFormula.EMPTY
                        .setText("1")
                )
        );
        store.save(
            SpreadsheetSelection.parseRow("2")
                .setColumn(SpreadsheetReferenceKind.RELATIVE.lastColumn())
                .setFormula(
                    SpreadsheetFormula.EMPTY
                        .setText("22")
                )
        );
        store.save(
            SpreadsheetSelection.parseCell("C3")
                .setFormula(
                    SpreadsheetFormula.EMPTY
                        .setText("3")
                )
        );

        this.nextEmptyColumnAndCheck(
            store,
            SpreadsheetSelection.parseRow("2")
        );
    }

    // nextEmptyRow.....................................................................................................

    @Test
    public void testNextEmptyRowWhenEmptyColumn() {
        final TreeMapSpreadsheetCellStore store = this.createStore();
        store.save(
            SpreadsheetSelection.A1
                .setFormula(
                    SpreadsheetFormula.EMPTY
                        .setText("1+2")
                )
        );

        this.nextEmptyRowAndCheck(
            store,
            SpreadsheetSelection.parseColumn("B"),
            SpreadsheetReferenceKind.RELATIVE.firstRow()
        );
    }

    @Test
    public void testNextEmptyRowWhenSeveralColumns() {
        final TreeMapSpreadsheetCellStore store = this.createStore();
        store.save(
            SpreadsheetSelection.A1
                .setFormula(
                    SpreadsheetFormula.EMPTY
                        .setText("1")
                )
        );
        store.save(
            SpreadsheetSelection.parseCell("B1")
                .setFormula(
                    SpreadsheetFormula.EMPTY
                        .setText("22")
                )
        );
        store.save(
            SpreadsheetSelection.parseCell("B3")
                .setFormula(
                    SpreadsheetFormula.EMPTY
                        .setText("222")
                )
        );
        store.save(
            SpreadsheetSelection.parseCell("C3")
                .setFormula(
                    SpreadsheetFormula.EMPTY
                        .setText("3")
                )
        );

        this.nextEmptyRowAndCheck(
            store,
            SpreadsheetSelection.parseColumn("B"),
            SpreadsheetSelection.parseRow("4")
        );
    }

    @Test
    public void testNextEmptyRowWhenFull() {
        final TreeMapSpreadsheetCellStore store = this.createStore();
        store.save(
            SpreadsheetSelection.A1
                .setFormula(
                    SpreadsheetFormula.EMPTY
                        .setText("1")
                )
        );
        store.save(
            SpreadsheetSelection.parseColumn("B")
                .setRow(SpreadsheetReferenceKind.RELATIVE.lastRow())
                .setFormula(
                    SpreadsheetFormula.EMPTY
                        .setText("22")
                )
        );
        store.save(
            SpreadsheetSelection.parseCell("C3")
                .setFormula(
                    SpreadsheetFormula.EMPTY
                        .setText("3")
                )
        );

        this.nextEmptyRowAndCheck(
            store,
            SpreadsheetSelection.parseColumn("B")
        );
    }

    @Override
    public TreeMapSpreadsheetCellStore createStore() {
        return TreeMapSpreadsheetCellStore.create();
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEquals2() {
        final TreeMapSpreadsheetCellStore store1 = this.createStore();
        final TreeMapSpreadsheetCellStore store2 = this.createStore();

        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setText("=1")
        );

        store1.save(cell);
        store2.save(cell);

        this.checkEquals(
            store1,
            store2
        );
    }

    @Test
    public void testEqualsDifferent() {
        final TreeMapSpreadsheetCellStore different = this.createStore();

        different.save(
            SpreadsheetSelection.A1.setFormula(
                SpreadsheetFormula.EMPTY.setText("=1")
            )
        );

        this.checkNotEquals(different);
    }

    @Override
    public TreeMapSpreadsheetCellStore createObject() {
        return this.createStore();
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
            "[A1 \"1+2\"]"
        );
    }

    // class.............................................................................................................

    @Override
    public Class<TreeMapSpreadsheetCellStore> type() {
        return TreeMapSpreadsheetCellStore.class;
    }

    @Override
    public String typeNamePrefix() {
        return TreeMap.class.getSimpleName();
    }
}
