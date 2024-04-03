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
import walkingkooka.collect.map.Maps;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.text.TextAlign;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

import java.util.Map;
import java.util.Optional;

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

    private final static Map<SpreadsheetCellReference, Object> VALUE = Maps.of(
            SpreadsheetSelection.A1,
            A1_CELL,
            B2,
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
                        Maps.of(
                                SpreadsheetSelection.A1,
                                SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY),
                                c3,
                                c3.setFormula(SpreadsheetFormula.EMPTY),
                                d4,
                                d4.setFormula(SpreadsheetFormula.EMPTY)
                        )
                )
        );

        this.checkEquals(
                "Found 2 cells out of range A1:B2 got $C$3, D4",
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
                                Maps.of(
                                        SpreadsheetSelection.A1,
                                        SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY),
                                        c3,
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
        final Map<SpreadsheetCellReference, Object> differentValue = Maps.of(
                SpreadsheetSelection.A1,
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
                            final Map<SpreadsheetCellReference, Object> value) {
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
                        Maps.of(
                                SpreadsheetSelection.A1,
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
                        Maps.of(
                                SpreadsheetSelection.A1,
                                SpreadsheetSelection.A1
                                        .setFormula(
                                                SpreadsheetMetadataTesting.parseFormula("=100+B2")
                                        ),
                                B2,
                                B2.setFormula(
                                        SpreadsheetMetadataTesting.parseFormula("=200+B3")
                                )
                        )
                ),
                moveTo,
                moveTo.setValue(
                        Maps.of(
                                B2,
                                B2.setFormula(
                                        SpreadsheetMetadataTesting.parseFormula("=100+C3")
                                ),
                                C3,
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
                        Maps.of(
                                SpreadsheetSelection.A1,
                                SpreadsheetSelection.A1
                                        .setFormula(
                                                SpreadsheetMetadataTesting.parseFormula("=100+B2")
                                        ),
                                B2,
                                B2.setFormula(
                                        SpreadsheetMetadataTesting.parseFormula("=200+B3")
                                )
                        )
                ),
                moveTo,
                moveTo.setValue(
                        Maps.of(
                                B2,
                                B2.setFormula(
                                        SpreadsheetMetadataTesting.parseFormula("=100+C3")
                                )
                        )
                )
        );
    }

    @Test
    public void testMoveFormula() {
        final SpreadsheetCellRangeReference moveTo = SpreadsheetSelection.parseCellRange("B2:C3");

        this.moveAndCheck(
                SpreadsheetCellRange.with(
                        RANGE,
                        Maps.of(
                                SpreadsheetSelection.A1,
                                SpreadsheetMetadataTesting.parseFormula("=100+B2"),
                                B2,
                                SpreadsheetMetadataTesting.parseFormula("=200+B3")
                        )
                ),
                moveTo,
                moveTo.setValue(
                        Maps.of(
                                B2,
                                SpreadsheetMetadataTesting.parseFormula("=100+C3"),
                                C3,
                                SpreadsheetMetadataTesting.parseFormula("=200+C4")
                        )
                )
        );
    }

    @Test
    public void testMoveFormatPattern() {
        final SpreadsheetCellRangeReference moveTo = SpreadsheetSelection.parseCellRange("B2:C3");

        final Optional<SpreadsheetFormatPattern> formatPattern1 = Optional.of(
                SpreadsheetPattern.parseDateFormatPattern("yyyy/mm/dd")
        );

        final Optional<SpreadsheetFormatPattern> formatPattern2 = Optional.empty();

        this.moveAndCheck(
                SpreadsheetCellRange.with(
                        RANGE,
                        Maps.of(
                                SpreadsheetSelection.A1,
                                formatPattern1,
                                B2,
                                formatPattern2
                        )
                ),
                moveTo,
                moveTo.setValue(
                        Maps.of(
                                B2,
                                formatPattern1,
                                C3,
                                formatPattern2
                        )
                )
        );
    }

    @Test
    public void testMoveParsePattern() {
        final SpreadsheetCellRangeReference moveTo = SpreadsheetSelection.parseCellRange("B2:C3");

        final Optional<SpreadsheetParsePattern> parsePattern1 = Optional.of(
                SpreadsheetPattern.parseDateParsePattern("yyyy/mm/dd")
        );

        final Optional<SpreadsheetParsePattern> parsePattern2 = Optional.empty();

        this.moveAndCheck(
                SpreadsheetCellRange.with(
                        RANGE,
                        Maps.of(
                                SpreadsheetSelection.A1,
                                parsePattern1,
                                B2,
                                parsePattern2
                        )
                ),
                moveTo,
                moveTo.setValue(
                        Maps.of(
                                B2,
                                parsePattern1,
                                C3,
                                parsePattern2
                        )
                )
        );
    }

    @Test
    public void testMoveStyle() {
        final SpreadsheetCellRangeReference moveTo = SpreadsheetSelection.parseCellRange("B2:C3");

        final TextStyle style1 = TextStyle.EMPTY.set(TextStylePropertyName.TEXT_ALIGN, TextAlign.LEFT);
        final TextStyle style2 = TextStyle.EMPTY;

        this.moveAndCheck(
                SpreadsheetCellRange.with(
                        RANGE,
                        Maps.of(
                                SpreadsheetSelection.A1,
                                style1,
                                B2,
                                style2
                        )
                ),
                moveTo,
                moveTo.setValue(
                        Maps.of(
                                B2,
                                style1,
                                C3,
                                style2
                        )
                )
        );
    }

    @Test
    public void testMoveFormattedValue() {
        final SpreadsheetCellRangeReference moveTo = SpreadsheetSelection.parseCellRange("B2:C3");

        final Optional<TextNode> formattedValue1 = Optional.of(
                TextNode.text("'Hello")
        );

        final Optional<TextNode> formattedValue2 = Optional.empty();

        this.moveAndCheck(
                SpreadsheetCellRange.with(
                        RANGE,
                        Maps.of(
                                SpreadsheetSelection.A1,
                                formattedValue1,
                                B2,
                                formattedValue2
                        )
                ),
                moveTo,
                moveTo.setValue(
                        Maps.of(
                                B2,
                                formattedValue1,
                                C3,
                                formattedValue2
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
                        "  A1\n" +
                        "    Cell A1\n" +
                        "      Formula\n" +
                        "        text: \"=1\"\n" +
                        "  B2\n" +
                        "    Cell B2\n" +
                        "      Formula\n" +
                        "        text: \"=22\"\n" +
                        "      formatPattern:\n" +
                        "        text-format-pattern\n" +
                        "          \"@\"\n" +
                        "      parsePattern:\n" +
                        "        number-parse-pattern\n" +
                        "          \"#.##\"\n" +
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