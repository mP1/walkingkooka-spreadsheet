/*
 * Copyright 2023 Miroslav Pokorny (github.com/mP1)
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
import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.text.TextAlign;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

import java.util.Optional;
import java.util.Set;

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
            .setFormatPattern(
                    Optional.of(
                            SpreadsheetPattern.DEFAULT_TEXT_FORMAT_PATTERN
                    )
            ).setParsePattern(
                    Optional.of(
                            SpreadsheetPattern.parseNumberParsePattern("#.##")
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

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
                this.createObject(),
                "A1:B2\n" +
                        "  Cell A1\n" +
                        "    Formula\n" +
                        "      text: \"=1\"\n" +
                        "  Cell B2\n" +
                        "    Formula\n" +
                        "      text: \"=22\"\n" +
                        "    formatPattern:\n" +
                        "      text-format-pattern\n" +
                        "        \"@\"\n" +
                        "    parsePattern:\n" +
                        "      number-parse-pattern\n" +
                        "        \"#.##\"\n" +
                        "    TextStyle\n" +
                        "      text-align=LEFT (walkingkooka.tree.text.TextAlign)\n"
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