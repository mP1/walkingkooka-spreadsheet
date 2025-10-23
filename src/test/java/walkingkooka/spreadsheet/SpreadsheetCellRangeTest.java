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

package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorContext;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorContexts;
import walkingkooka.spreadsheet.compare.SpreadsheetComparators;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetColumnOrRowSpreadsheetComparators;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.text.TextAlign;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellRangeTest implements ClassTesting<SpreadsheetCellRange>,
    HashCodeEqualsDefinedTesting2<SpreadsheetCellRange>,
    SpreadsheetMetadataTesting,
    TreePrintableTesting {

    private final static SpreadsheetCellRangeReference RANGE = SpreadsheetSelection.parseCellRange("A1:b2");

    private final static SpreadsheetCell A1_CELL = SpreadsheetSelection.A1.setFormula(
        SpreadsheetFormula.EMPTY.setText("=1")
    );

    private final static SpreadsheetCellReference B2 = SpreadsheetSelection.parseCell("B2");

    private final static SpreadsheetCell B2_CELL = B2.setFormula(SpreadsheetFormula.EMPTY.setText("=22"))
        .setFormatter(
            Optional.of(
                SpreadsheetPattern.DEFAULT_TEXT.spreadsheetFormatterSelector()
            )
        ).setParser(
            Optional.of(
                SpreadsheetPattern.parseNumberParsePattern("#.##")
                    .spreadsheetParserSelector()
            )
        ).setStyle(
            TextStyle.EMPTY.set(
                TextStylePropertyName.TEXT_ALIGN,
                TextAlign.LEFT
            )
        );

    private final static SpreadsheetCellReference C3 = SpreadsheetSelection.parseCell("C3");

    private final static Set<SpreadsheetCell> VALUE = Sets.of(
        A1_CELL,
        B2_CELL
    );

    @Test
    public void testWithNullRangeFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetCellRange.with(
                null,
                VALUE
            )
        );
    }

    @Test
    public void testWithNullValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetCellRange.with(
                RANGE,
                null
            )
        );
    }

    @Test
    public void testWithValueCellOutOfBoundsFails() {
        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("$C$3");
        final SpreadsheetCellReference d4 = SpreadsheetSelection.parseCell("D4");

        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetCellRange.with(
                RANGE,
                Sets.of(
                    SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY),
                    c3.setFormula(SpreadsheetFormula.EMPTY),
                    d4.setFormula(SpreadsheetFormula.EMPTY)
                )
            )
        );

        this.checkEquals(
            "Found 2 cells out of range A1:B2 got C3, D4",
            thrown.getMessage(),
            "message"
        );
    }

    @Test
    public void testWith() {
        final SpreadsheetCellRange spreadsheetCellRange = SpreadsheetCellRange.with(
            RANGE,
            VALUE
        );
        this.checkRange(spreadsheetCellRange);
        this.checkValue(spreadsheetCellRange);
    }


    // setRange.........................................................................................................

    @Test
    public void testSetRangeNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject().setRange(null)
        );
    }

    @Test
    public void testSetRangeSame() {
        final SpreadsheetCellRange spreadsheetCellRange = this.createObject();

        assertSame(
            spreadsheetCellRange,
            spreadsheetCellRange.setRange(RANGE)
        );
    }

    @Test
    public void testSetRangeOutOfBoundsFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.createObject()
                .setRange(SpreadsheetSelection.parseCellRange("A1"))
        );

        this.checkEquals(
            "Found 1 cells out of range A1 got B2",
            thrown.getMessage()
        );
    }

    @Test
    public void testSetRangeDifferentLarger() {
        final SpreadsheetCellRange spreadsheetCellRange = this.createObject();
        final SpreadsheetCellRangeReference differentRange = SpreadsheetSelection.parseCellRange("A1:C3");

        final SpreadsheetCellRange different = spreadsheetCellRange.setRange(differentRange);

        assertNotSame(
            spreadsheetCellRange,
            different
        );

        this.checkRange(different, differentRange);
        this.checkValue(different);

        this.checkRange(spreadsheetCellRange);
        this.checkValue(spreadsheetCellRange);
    }

    @Test
    public void testSetRangeDifferentSmaller() {
        final SpreadsheetCellRange spreadsheetCellRange = this.createObject();
        final SpreadsheetCellRangeReference differentRange = SpreadsheetSelection.parseCellRange("A1:C3");

        final SpreadsheetCellRange different = spreadsheetCellRange.setRange(differentRange)
            .setRange(RANGE);

        assertNotSame(
            spreadsheetCellRange,
            different
        );

        this.checkRange(different);
        this.checkValue(different);

        this.checkRange(spreadsheetCellRange);
        this.checkValue(spreadsheetCellRange);
    }

    // setValue.........................................................................................................

    @Test
    public void testSetValueNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .setValue(null)
        );
    }

    @Test
    public void testSetValueSame() {
        final SpreadsheetCellRange spreadsheetCellRange = this.createObject();

        assertSame(
            spreadsheetCellRange,
            spreadsheetCellRange.setValue(VALUE)
        );
    }

    @Test
    public void testSetValueOutOfBoundsFails() {
        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("C3");

        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.createObject()
                .setValue(
                    Sets.of(
                        SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY),
                        c3.setFormula(SpreadsheetFormula.EMPTY.setText("=1"))
                    )
                )
        );

        this.checkEquals(
            "Found 1 cells out of range A1:B2 got C3",
            thrown.getMessage()
        );
    }

    @Test
    public void testSetValueDifferent() {
        final SpreadsheetCellRange spreadsheetCellRange = this.createObject();
        final Set<SpreadsheetCell> differentValue = Sets.of(
            SpreadsheetSelection.A1.setFormula(
                SpreadsheetFormula.EMPTY.setText("'different")
            )
        );

        final SpreadsheetCellRange different = spreadsheetCellRange.setValue(differentValue);

        assertNotSame(
            spreadsheetCellRange,
            different
        );

        this.checkRange(different);
        this.checkValue(different, differentValue);

        this.checkRange(spreadsheetCellRange);
        this.checkValue(spreadsheetCellRange);
    }

    // helpers..........................................................................................................

    private void checkRange(final SpreadsheetCellRange spreadsheetCellRange) {
        this.checkRange(
            spreadsheetCellRange,
            RANGE
        );
    }

    private void checkRange(final SpreadsheetCellRange spreadsheetCellRange,
                            final SpreadsheetCellRangeReference range) {
        this.checkEquals(
            range,
            spreadsheetCellRange.range()
        );
    }

    private void checkValue(final SpreadsheetCellRange spreadsheetCellRange) {
        this.checkValue(
            spreadsheetCellRange,
            VALUE
        );
    }

    private void checkValue(final SpreadsheetCellRange spreadsheetCellRange,
                            final Set<SpreadsheetCell> value) {
        this.checkEquals(
            value,
            spreadsheetCellRange.value()
        );
    }

    // move.............................................................................................................

    @Test
    public void testMoveNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject().move(null)
        );
    }

    @Test
    public void testMoveSame() {
        final SpreadsheetCellRange range = this.createObject();

        assertSame(
            range,
            range.move(RANGE)
        );
    }

    @Test
    public void testMoveSame2() {
        final SpreadsheetCellRangeReference moveToRange = SpreadsheetSelection.parseCellRange("$A1:B2");

        this.moveAndCheck(
            this.createObject(),
            moveToRange,
            moveToRange.setValue(VALUE)
        );
    }

    @Test
    public void testMoveSameOriginSmallerDimensions() {
        final SpreadsheetCellRangeReference moveTo = SpreadsheetSelection.parseCellRange("A1:A2");

        this.moveAndCheck(
            this.createObject(),
            moveTo,
            moveTo.setValue(
                Sets.of(
                    A1_CELL
                )
            )
        );
    }

    @Test
    public void testMoveCell() {
        final SpreadsheetCellRangeReference moveTo = SpreadsheetSelection.parseCellRange("B2:C3");

        this.moveAndCheck(
            SpreadsheetCellRange.with(
                RANGE,
                Sets.of(
                    SpreadsheetSelection.A1
                        .setFormula(
                            SpreadsheetMetadataTesting.parseFormula("=100+B2")
                        ),
                    B2.setFormula(
                        SpreadsheetMetadataTesting.parseFormula("=200+B3")
                    )
                )
            ),
            moveTo,
            moveTo.setValue(
                Sets.of(
                    B2.setFormula(
                        SpreadsheetMetadataTesting.parseFormula("=100+C3")
                    ),
                    C3.setFormula(
                        SpreadsheetMetadataTesting.parseFormula("=200+C4")
                    )
                )
            )
        );
    }

    @Test
    public void testMoveCellSmallerRange() {
        final SpreadsheetCellRangeReference moveTo = B2.toCellRange();

        this.moveAndCheck(
            SpreadsheetCellRange.with(
                RANGE,
                Sets.of(
                    SpreadsheetSelection.A1
                        .setFormula(
                            SpreadsheetMetadataTesting.parseFormula("=100+B2")
                        ),
                    B2.setFormula(
                        SpreadsheetMetadataTesting.parseFormula("=200+B3")
                    )
                )
            ),
            moveTo,
            moveTo.setValue(
                Sets.of(
                    B2.setFormula(
                        SpreadsheetMetadataTesting.parseFormula("=100+C3")
                    )
                )
            )
        );
    }

    private void moveAndCheck(final SpreadsheetCellRange from,
                              final SpreadsheetCellRangeReference moveTo,
                              final SpreadsheetCellRange expected) {
        final SpreadsheetCellRange moved = from.move(moveTo);
        assertNotSame(
            expected,
            moved
        );

        this.checkRange(from);

        this.checkEquals(
            expected,
            moved
        );
    }

    // sort.............................................................................................................

    private final static BiConsumer<SpreadsheetCell, SpreadsheetCell> MOVED_CELLS_BICONSUMER = (from, to) -> {
        throw new UnsupportedOperationException();
    };

    @Test
    public void testSortWithNullComparatorsFails() {
        this.sortFails(
            null,
            MOVED_CELLS_BICONSUMER,
            SpreadsheetComparatorContexts.fake()
        );
    }

    @Test
    public void testSortWithColumnComparatorsOutOfBoundsFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.createObject()
                .sort(
                    Lists.of(
                        SpreadsheetColumnOrRowSpreadsheetComparators.with(
                            SpreadsheetSelection.parseColumn("B"),
                            Lists.of(
                                SpreadsheetComparators.text()
                            )
                        ),
                        SpreadsheetColumnOrRowSpreadsheetComparators.with(
                            SpreadsheetSelection.parseColumn("C"),
                            Lists.of(
                                SpreadsheetComparators.text()
                            )
                        ),
                        SpreadsheetColumnOrRowSpreadsheetComparators.with(
                            SpreadsheetSelection.parseColumn("ZZ"),
                            Lists.of(
                                SpreadsheetComparators.text()
                            )
                        )
                    ),
                    MOVED_CELLS_BICONSUMER,
                    SpreadsheetComparatorContexts.fake()
                )
        );

        this.checkEquals(
            "Invalid column(s) C, ZZ are not within A1:B2",
            thrown.getMessage()
        );
    }

    @Test
    public void testSortWithRowComparatorsOutOfBoundsFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.createObject()
                .sort(
                    Lists.of(
                        SpreadsheetColumnOrRowSpreadsheetComparators.with(
                            SpreadsheetSelection.parseRow("2"),
                            Lists.of(
                                SpreadsheetComparators.text()
                            )
                        ),
                        SpreadsheetColumnOrRowSpreadsheetComparators.with(
                            SpreadsheetSelection.parseRow("3"),
                            Lists.of(
                                SpreadsheetComparators.text()
                            )
                        ),
                        SpreadsheetColumnOrRowSpreadsheetComparators.with(
                            SpreadsheetSelection.parseRow("99"),
                            Lists.of(
                                SpreadsheetComparators.text()
                            )
                        )
                    ),
                    MOVED_CELLS_BICONSUMER,
                    SpreadsheetComparatorContexts.fake()
                )
        );

        this.checkEquals(
            "Invalid row(s) 3, 99 are not within A1:B2",
            thrown.getMessage()
        );
    }

    @Test
    public void testSortWithNullMovedCellsBiConsumerFails() {
        this.sortFails(
            SpreadsheetColumnOrRowSpreadsheetComparators.parse(
                "A=text",
                SpreadsheetComparatorProviders.spreadsheetComparators(),
                PROVIDER_CONTEXT
            ),
            null,
            SpreadsheetComparatorContexts.fake()
        );
    }

    @Test
    public void testSortWithNullContextFails() {
        this.sortFails(
            SpreadsheetColumnOrRowSpreadsheetComparators.parse(
                "A=text",
                SpreadsheetComparatorProviders.spreadsheetComparators(),
                PROVIDER_CONTEXT
            ),
            MOVED_CELLS_BICONSUMER,
            null
        );
    }

    private void sortFails(final List<SpreadsheetColumnOrRowSpreadsheetComparators> comparators,
                           final BiConsumer<SpreadsheetCell, SpreadsheetCell> movedCells,
                           final SpreadsheetComparatorContext context) {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .sort(
                    comparators,
                    movedCells,
                    context
                )
        );
    }

    @Test
    public void testSortColumnNoChanges() {
        final Set<SpreadsheetCell> cells = Sets.of(
            cellWithValue("A1", "1a"),
            cellWithValue("B2", "2b")
        );

        final SpreadsheetCellRange cellRange = SpreadsheetSelection.parseCellRange("A1:B2")
            .setValue(cells);

        this.sortAndCheck(
            cellRange,
            "A=text",
            MOVED_CELLS_BICONSUMER,
            cellRange
        );
    }

    @Test
    public void testSortColumnNoChanges2() {
        final Set<SpreadsheetCell> cells = Sets.of(
            cell("A1"),
            cell("B2")
        );

        final SpreadsheetCellRange cellRange = SpreadsheetSelection.parseCellRange("A1:B2")
            .setValue(cells);

        this.sortAndCheck(
            cellRange,
            "A=text",
            MOVED_CELLS_BICONSUMER,
            cellRange
        );
    }

    @Test
    public void testSortRowNoChanges() {
        final Set<SpreadsheetCell> cells = Sets.of(
            cellWithValue("A1", "1a"),
            cellWithValue("B2", "2b")
        );

        final SpreadsheetCellRange cellRange = SpreadsheetSelection.parseCellRange("A1:B2")
            .setValue(cells);

        this.sortAndCheck(
            cellRange,
            "1=text",
            MOVED_CELLS_BICONSUMER,
            cellRange
        );
    }

    @Test
    public void testSortRowNoChanges2() {
        final Set<SpreadsheetCell> cells = Sets.of(
            cell("A1"),
            cell("B2")
        );

        final SpreadsheetCellRange cellRange = SpreadsheetSelection.parseCellRange("A1:B2")
            .setValue(cells);

        this.sortAndCheck(
            cellRange,
            "1=text",
            MOVED_CELLS_BICONSUMER,
            cellRange
        );
    }

    @Test
    public void testSortColumnRowSwapped() {
        final SpreadsheetCell a1 = cellWithValue("A1", "BBB");
        final SpreadsheetCell a2 = cellWithValue("A2", "AAA");

        final Set<SpreadsheetCell> cells = Sets.of(
            a1,
            a2
        );

        final SpreadsheetCellRange cellRange = SpreadsheetSelection.parseCellRange("A1:B2")
            .setValue(cells);

        final SpreadsheetCell newA1 = this.cellWithValue("A1", "AAA");
        final SpreadsheetCell newA2 = this.cellWithValue("A2", "BBB");

        final Map<SpreadsheetCell, SpreadsheetCell> remapped = Maps.sorted(SpreadsheetCell.REFERENCE_COMPARATOR);

        this.sortAndCheck(
            cellRange,
            "A=text",
            remapped::put,
            cellRange.setValue(
                Sets.of(
                    newA1,
                    newA2
                )
            )
        );

        this.checkEquals(
            Maps.of(
                a1, newA2,
                a2, newA1
            ),
            remapped,
            "remapped"
        );
    }

    @Test
    public void testSortRowColumnSwapped() {
        final SpreadsheetCell a1 = cellWithValue("A1", "BBB");
        final SpreadsheetCell b1 = cellWithValue("B1", "AAA");

        final Set<SpreadsheetCell> cells = Sets.of(
            a1,
            b1
        );

        final SpreadsheetCellRange cellRange = SpreadsheetSelection.parseCellRange("A1:B2")
            .setValue(cells);

        final SpreadsheetCell newA1 = this.cellWithValue("A1", "AAA");
        final SpreadsheetCell newB1 = this.cellWithValue("B1", "BBB");

        final Map<SpreadsheetCell, SpreadsheetCell> remapped = Maps.sorted(SpreadsheetCell.REFERENCE_COMPARATOR);

        this.sortAndCheck(
            cellRange,
            "1=text",
            remapped::put,
            cellRange.setValue(
                Sets.of(
                    newA1,
                    newB1
                )
            )
        );

        this.checkEquals(
            Maps.of(
                a1, newB1,
                b1, newA1
            ),
            remapped,
            "remapped"
        );
    }

    @Test
    public void testSortColumnRowSwapped2() {
        final SpreadsheetCell a1 = cellWithValue("A1", "BBB1");
        final SpreadsheetCell b1 = cellWithValue("B1", "BBB2");
        final SpreadsheetCell a2 = cellWithValue("A2", "AAA1");
        final SpreadsheetCell b2 = cellWithValue("B2", "AAA2");

        final Set<SpreadsheetCell> cells = Sets.of(
            a1,
            b1,
            a2,
            b2
        );

        final SpreadsheetCellRange cellRange = SpreadsheetSelection.parseCellRange("A1:B2")
            .setValue(cells);

        final SpreadsheetCell newA1 = this.cellWithValue("A1", "AAA1");
        final SpreadsheetCell newB1 = this.cellWithValue("B1", "AAA2");
        final SpreadsheetCell newA2 = this.cellWithValue("A2", "BBB1");
        final SpreadsheetCell newB2 = this.cellWithValue("B2", "BBB2");

        this.sortAndCheck(
            cellRange,
            "A=text",
            cellRange.setValue(
                Sets.of(
                    newA1,
                    newB1,
                    newA2,
                    newB2
                )
            ),
            Maps.of(
                a1, newA2,
                b1, newB2,
                a2, newA1,
                b2, newB1
            )
        );
    }

    @Test
    public void testSortRowColumnSwapped2() {
        final SpreadsheetCell a1 = cellWithValue("A1", "BBB1");
        final SpreadsheetCell a2 = cellWithValue("A2", "BBB2");
        final SpreadsheetCell b1 = cellWithValue("B1", "AAA1");
        final SpreadsheetCell b2 = cellWithValue("B2", "AAA2");

        final Set<SpreadsheetCell> cells = Sets.of(
            a1,
            a2,
            b1,
            b2
        );

        final SpreadsheetCellRange cellRange = SpreadsheetSelection.parseCellRange("A1:B2")
            .setValue(cells);

        final SpreadsheetCell newA1 = this.cellWithValue("A1", "AAA1");
        final SpreadsheetCell newA2 = this.cellWithValue("A2", "AAA2");
        final SpreadsheetCell newB1 = this.cellWithValue("B1", "BBB1");
        final SpreadsheetCell newB2 = this.cellWithValue("B2", "BBB2");

        this.sortAndCheck(
            cellRange,
            "1=text",
            cellRange.setValue(
                Sets.of(
                    newA1,
                    newA2,
                    newB1,
                    newB2
                )
            ),
            Maps.of(
                a1, newB1,
                a2, newB2,
                b1, newA1,
                b2, newA2
            )
        );
    }

    @Test
    public void testSortColumnRowSwappedEmptyRow() {
        final SpreadsheetCell a1 = cellWithValue("A1", "BBB1");
        final SpreadsheetCell b1 = cellWithValue("B1", "BBB2");
        final SpreadsheetCell a2 = cellWithValue("A2", "AAA1");
        final SpreadsheetCell b2 = cellWithValue("B2", "AAA2");

        final Set<SpreadsheetCell> cells = Sets.of(
            a1,
            b1,
            a2,
            b2
        );

        final SpreadsheetCellRange cellRange = SpreadsheetSelection.parseCellRange("A1:C3")
            .setValue(cells);

        final SpreadsheetCell newA1 = this.cellWithValue("A1", "AAA1");
        final SpreadsheetCell newB1 = this.cellWithValue("B1", "AAA2");
        final SpreadsheetCell newA2 = this.cellWithValue("A2", "BBB1");
        final SpreadsheetCell newB2 = this.cellWithValue("B2", "BBB2");

        this.sortAndCheck(
            cellRange,
            "A=text",
            cellRange.setValue(
                Sets.of(
                    newA1,
                    newB1,
                    newA2,
                    newB2
                )
            ),
            Maps.of(
                a1, newA2,
                b1, newB2,
                a2, newA1,
                b2, newB1
            )
        );
    }

    @Test
    public void testSortColumnRowSwappedUnmovedRow() {
        final SpreadsheetCell a1 = cellWithValue("A1", "BBB1");
        final SpreadsheetCell b1 = cellWithValue("B1", "BBB2");
        final SpreadsheetCell a2 = cellWithValue("A2", "AAA1");
        final SpreadsheetCell b2 = cellWithValue("B2", "AAA2");

        final SpreadsheetCell a3 = cellWithValue("A3", "ZZZ");

        final Set<SpreadsheetCell> cells = Sets.of(
            a1,
            b1,
            a2,
            b2,
            a3
        );

        final SpreadsheetCellRange cellRange = SpreadsheetSelection.parseCellRange("A1:C3")
            .setValue(cells);

        final SpreadsheetCell newA1 = this.cellWithValue("A1", "AAA1");
        final SpreadsheetCell newB1 = this.cellWithValue("B1", "AAA2");
        final SpreadsheetCell newA2 = this.cellWithValue("A2", "BBB1");
        final SpreadsheetCell newB2 = this.cellWithValue("B2", "BBB2");

        this.sortAndCheck(
            cellRange,
            "A=text",
            cellRange.setValue(
                Sets.of(
                    newA1,
                    newB1,
                    newA2,
                    newB2,
                    a3
                )
            ),
            Maps.of(
                a1, newA2,
                b1, newB2,
                a2, newA1,
                b2, newB1
            )
        );
    }

    @Test
    public void testSortColumnRowSwappedUnmovedRow2() {
        final SpreadsheetCell a1 = cellWithValue("A1", "ZZZ1");
        final SpreadsheetCell b1 = cellWithValue("B1", "ZZZ2");

        final SpreadsheetCell a2 = cellWithValue("a2", "MMM");

        final SpreadsheetCell a3 = cellWithValue("A3", "AAA1");
        final SpreadsheetCell b3 = cellWithValue("B3", "AAA2");

        final Set<SpreadsheetCell> cells = Sets.of(
            a1,
            b1,
            a2,
            a3,
            b3
        );

        final SpreadsheetCellRange cellRange = SpreadsheetSelection.parseCellRange("A1:C3")
            .setValue(cells);

        final SpreadsheetCell newA1 = this.cellWithValue("A1", "AAA1");
        final SpreadsheetCell newB1 = this.cellWithValue("B1", "AAA2");
        final SpreadsheetCell newA3 = this.cellWithValue("A3", "ZZZ1");
        final SpreadsheetCell newB3 = this.cellWithValue("B3", "ZZZ2");

        this.sortAndCheck(
            cellRange,
            "A=text",
            cellRange.setValue(
                Sets.of(
                    newA1,
                    newB1,
                    a2,
                    newA3,
                    newB3
                )
            ),
            Maps.of(
                a1, newA3,
                b1, newB3,
                a3, newA1,
                b3, newB1
            )
        );
    }

    @Test
    public void testSortColumnRowSwappedFormulaExpressionReferencesUpdated() {
        final SpreadsheetCell a1 = cellWithFormula("A1", "=B1", "BBB1");
        final SpreadsheetCell b1 = cellWithValue("B1", "BBB2");
        final SpreadsheetCell a2 = cellWithValue("A2", "AAA1");
        final SpreadsheetCell b2 = cellWithValue("B2", "AAA2");

        final Set<SpreadsheetCell> cells = Sets.of(
            a1,
            b1,
            a2,
            b2
        );

        final SpreadsheetCellRange cellRange = SpreadsheetSelection.parseCellRange("A1:C3")
            .setValue(cells);

        final SpreadsheetCell newA1 = this.cellWithValue("A1", "AAA1");
        final SpreadsheetCell newB1 = this.cellWithValue("B1", "AAA2");
        final SpreadsheetCell newA2 = this.cellWithFormula("A2", "=B2"); // value lost because formula rewritten
        final SpreadsheetCell newB2 = this.cellWithValue("B2", "BBB2");

        this.sortAndCheck(
            cellRange,
            "A=text",
            cellRange.setValue(
                Sets.of(
                    newA1,
                    newB1,
                    newA2,
                    newB2
                )
            ),
            Maps.of(
                a1, newA2,
                b1, newB2,
                a2, newA1,
                b2, newB1
            )
        );
    }

    @Test
    public void testSortColumnRowSwappedFormulaExpressionAbsoluteReferences() {
        final SpreadsheetCell a1 = cellWithFormula("A1", "=$B$1", "BBB1");
        final SpreadsheetCell b1 = cellWithValue("B1", "BBB2");
        final SpreadsheetCell a2 = cellWithValue("A2", "AAA1");
        final SpreadsheetCell b2 = cellWithValue("B2", "AAA2");

        final Set<SpreadsheetCell> cells = Sets.of(
            a1,
            b1,
            a2,
            b2
        );

        final SpreadsheetCellRange cellRange = SpreadsheetSelection.parseCellRange("A1:C3")
            .setValue(cells);

        final SpreadsheetCell newA1 = this.cellWithValue("A1", "AAA1");
        final SpreadsheetCell newB1 = this.cellWithValue("B1", "AAA2");
        final SpreadsheetCell newA2 = this.cellWithFormula("A2", "=$B$1", "BBB1"); // value not LOST
        final SpreadsheetCell newB2 = this.cellWithValue("B2", "BBB2");

        this.sortAndCheck(
            cellRange,
            "A=text",
            cellRange.setValue(
                Sets.of(
                    newA1,
                    newB1,
                    newA2,
                    newB2
                )
            ),
            Maps.of(
                a1, newA2,
                b1, newB2,
                a2, newA1,
                b2, newB1
            )
        );
    }

    private SpreadsheetCell cell(final String reference) {
        return this.cellWithValue(
            reference,
            Optional.empty()
        );
    }

    private SpreadsheetCell cellWithValue(final String reference,
                                          final Object value) {
        return this.cellWithValue(
            reference,
            Optional.of(value)
        );
    }

    private SpreadsheetCell cellWithValue(final String reference,
                                          final Optional<Object> value) {
        return SpreadsheetSelection.parseCell(reference)
            .setFormula(SpreadsheetFormula.EMPTY.setValue(
                value
            ));
    }

    private SpreadsheetCell cellWithFormula(final String reference,
                                            final String formula) {
        return this.cellWithFormula(
            reference,
            formula,
            Optional.empty()
        );
    }

    private SpreadsheetCell cellWithFormula(final String reference,
                                            final String formula,
                                            final Object value) {
        return this.cellWithFormula(
            reference,
            formula,
            Optional.of(value)
        );
    }

    private SpreadsheetCell cellWithFormula(final String reference,
                                            final String formula,
                                            final Optional<Object> value) {
        return SpreadsheetSelection.parseCell(reference)
            .setFormula(
                SpreadsheetMetadataTesting.parseFormula(formula)
                    .setValue(
                        value
                    )
            );
    }

    private void sortAndCheck(final SpreadsheetCellRange range,
                              final String comparators,
                              final SpreadsheetCellRange expected,
                              final Map<SpreadsheetCell, SpreadsheetCell> expectedMovedCells) {
        final Map<SpreadsheetCell, SpreadsheetCell> remapped = Maps.sorted(SpreadsheetCell.REFERENCE_COMPARATOR);

        this.sortAndCheck(
            range,
            comparators,
            remapped::put,
            SPREADSHEET_COMPARATOR_CONTEXT,
            expected
        );

        this.checkEquals(
            expectedMovedCells,
            remapped,
            "remapped"
        );
    }

    private void sortAndCheck(final SpreadsheetCellRange range,
                              final String comparators,
                              final BiConsumer<SpreadsheetCell, SpreadsheetCell> movedCells,
                              final SpreadsheetCellRange expected) {
        this.sortAndCheck(
            range,
            comparators,
            movedCells,
            SPREADSHEET_COMPARATOR_CONTEXT,
            expected
        );
    }

    private void sortAndCheck(final SpreadsheetCellRange range,
                              final String comparators,
                              final BiConsumer<SpreadsheetCell, SpreadsheetCell> movedCells,
                              final SpreadsheetComparatorContext context,
                              final SpreadsheetCellRange expected) {
        this.sortAndCheck(
            range,
            SpreadsheetColumnOrRowSpreadsheetComparators.parse(
                comparators,
                SpreadsheetComparatorProviders.spreadsheetComparators(),
                PROVIDER_CONTEXT
            ),
            movedCells,
            context,
            expected
        );
    }

    private void sortAndCheck(final SpreadsheetCellRange range,
                              final List<SpreadsheetColumnOrRowSpreadsheetComparators> comparators,
                              final BiConsumer<SpreadsheetCell, SpreadsheetCell> movedCells,
                              final SpreadsheetComparatorContext context,
                              final SpreadsheetCellRange expected) {
        this.checkEquals(
            expected,
            range.sort(
                comparators,
                movedCells,
                context
            ),
            () -> range.range() + " sort " + comparators
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            this.createObject(),
            "A1:B2\n" +
                "  Cell A1\n" +
                "    Formula\n" +
                "      text:\n" +
                "        \"=1\"\n" +
                "  Cell B2\n" +
                "    Formula\n" +
                "      text:\n" +
                "        \"=22\"\n" +
                "    formatter:\n" +
                "      text\n" +
                "        \"@\"\n" +
                "    parser:\n" +
                "      number\n" +
                "        \"#.##\"\n" +
                "    style:\n" +
                "      TextStyle\n" +
                "        text-align=LEFT (walkingkooka.tree.text.TextAlign)\n"
        );
    }

    // ClassTesting....................................................................................................

    @Override
    public Class<SpreadsheetCellRange> type() {
        return SpreadsheetCellRange.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // HashCodeEqualsDefinedTesting2...................................................................................

    @Override
    public SpreadsheetCellRange createObject() {
        return SpreadsheetCellRange.with(
            RANGE,
            VALUE
        );
    }
}