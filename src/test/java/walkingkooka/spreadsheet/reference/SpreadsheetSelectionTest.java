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

package walkingkooka.spreadsheet.reference;

import org.junit.jupiter.api.Test;
import walkingkooka.EmptyTextException;
import walkingkooka.HasNotFoundTextTesting;
import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.CharSequences;
import walkingkooka.text.HasTextTesting;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetSelectionTest implements ClassTesting2<SpreadsheetSelection>,
    HasTextTesting,
    HasNotFoundTextTesting,
    ParseStringTesting<SpreadsheetExpressionReference> {

    // isSelectionClass.................................................................................................

    @Test
    public void testIsSelectionClassWithNull() {
        this.isSelectionClassAndCheck(
            null,
            false
        );
    }

    @Test
    public void testIsSelectionClassWithObject() {
        this.isSelectionClassAndCheck(
            Object.class,
            false
        );
    }

    @Test
    public void testIsSelectionClassWithSpreadsheetSelection() {
        this.isSelectionClassAndCheck(
            SpreadsheetSelection.class,
            true
        );
    }

    @Test
    public void testIsSelectionClassWithSpreadsheetColumnOrRowReferenceOrRange() {
        this.isSelectionClassAndCheck(
            SpreadsheetColumnOrRowReferenceOrRange.class,
            true
        );
    }

    @Test
    public void testIsSelectionClassWithSpreadsheetColumnReferenceOrRange() {
        this.isSelectionClassAndCheck(
            SpreadsheetColumnReferenceOrRange.class,
            true
        );
    }

    @Test
    public void testIsSelectionClassWithSpreadsheetColumnRangeReference() {
        this.isSelectionClassAndCheck(
            SpreadsheetColumnRangeReference.class,
            true
        );
    }

    @Test
    public void testIsSelectionClassWithSpreadsheetColumnReference() {
        this.isSelectionClassAndCheck(
            SpreadsheetColumnReference.class,
            true
        );
    }

    @Test
    public void testIsSelectionClassWithSpreadsheetRowRangeReference() {
        this.isSelectionClassAndCheck(
            SpreadsheetRowRangeReference.class,
            true
        );
    }

    @Test
    public void testIsSelectionClassWithSpreadsheetRowReference() {
        this.isSelectionClassAndCheck(
            SpreadsheetRowReference.class,
            true
        );
    }

    @Test
    public void testIsSelectionClassWithSpreadsheetExpressionReference() {
        this.isSelectionClassAndCheck(
            SpreadsheetExpressionReference.class,
            true
        );
    }

    @Test
    public void testIsSelectionClassWithSpreadsheetCellReferenceOrRange() {
        this.isSelectionClassAndCheck(
            SpreadsheetCellReferenceOrRange.class,
            true
        );
    }

    @Test
    public void testIsSelectionClassWithSpreadsheetCellRangeReference() {
        this.isSelectionClassAndCheck(
            SpreadsheetCellRangeReference.class,
            true
        );
    }

    @Test
    public void testIsSelectionClassWithSpreadsheetCellReference() {
        this.isSelectionClassAndCheck(
            SpreadsheetCellReference.class,
            true
        );
    }

    @Test
    public void testIsSelectionClassWithSpreadsheetLabelName() {
        this.isSelectionClassAndCheck(
            SpreadsheetLabelName.class,
            true
        );
    }

    @Test
    public void testIsSelectionClassWithThisTest() {
        this.isSelectionClassAndCheck(
            this.getClass(),
            false
        );
    }

    private void isSelectionClassAndCheck(final Class<?> type,
                                          final boolean expected) {
        this.checkEquals(
            expected,
            SpreadsheetSelection.isSelectionClass(type),
            () -> null != type ? type.getName() : null
        );

        this.checkEquals(
            expected,
            null != type && SpreadsheetSelection.class.isAssignableFrom(type),
            () -> null != type ? type.getName() : null
        );
    }

    // constants........................................................................................................

    @Test
    public void testA1Constant() {
        final SpreadsheetCellReference a1 = SpreadsheetCellReference.A1;
        this.checkEquals(
            SpreadsheetSelection.parseColumn("A"),
            SpreadsheetReferenceKind.RELATIVE.firstColumn()
        );
        this.checkEquals(
            SpreadsheetSelection.parseRow("1"),
            SpreadsheetReferenceKind.RELATIVE.firstRow()
        );
    }

    @Test
    public void testAllCells() {
        this.checkEquals(
            "A1:XFD1048576",
            SpreadsheetSelection.ALL_CELLS.toString()
        );
    }

    @Test
    public void testAllColumns() {
        this.checkEquals(
            "A:XFD",
            SpreadsheetSelection.ALL_COLUMNS.toString()
        );
    }

    @Test
    public void testAllRows() {
        this.checkEquals(
            "1:1048576",
            SpreadsheetSelection.ALL_ROWS.toString()
        );
    }

    @Test
    public void testMaxColumn() {
        this.checkNotEquals(
            0,
            SpreadsheetSelection.MAX_COLUMN
        );
    }

    @Test
    public void testMaxRow() {
        this.checkNotEquals(
            0,
            SpreadsheetSelection.MAX_ROW
        );
    }

    // isCellText........................................................................................................

    @Test
    public void testIsCellTextNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExpressionReference.isCellText(null)
        );
    }

    @Test
    public void testIsCellTextEmptyFalse() {
        this.isCellTextAndCheck("", false);
    }

    @Test
    public void testIsCellTextInvalidCharacterFalse() {
        this.isCellTextAndCheck("!", false);
    }

    @Test
    public void testIsCellTextAbsolutePrefixOnlyFalse() {
        this.isCellTextAndCheck("$", false);
    }

    @Test
    public void testIsCellTextAbsoluteColumnOnlyFalse() {
        this.isCellTextAndCheck("$A", false);
    }

    @Test
    public void testIsCellTextColumnOnlyFalse() {
        this.isCellTextAndCheck("A", false);
    }

    @Test
    public void testIsCellTextAbsoluteRowOnlyFalse() {
        this.isCellTextAndCheck("$9", false);
    }

    @Test
    public void testIsCellTextRowOnlyFalse() {
        this.isCellTextAndCheck("9", false);
    }

    @Test
    public void testIsCellTextRowOnlyFalse2() {
        this.isCellTextAndCheck("98", false);
    }

    @Test
    public void testIsCellTextWithCellReferenceUppercase() {
        this.isCellTextAndCheck("A1", true);
    }

    @Test
    public void testIsCellTextWithCellReferenceUppercaseAbsolute() {
        this.isCellTextAndCheck("$A1", true);
    }

    @Test
    public void testIsCellTextWithCellReferenceLowercase() {
        this.isCellTextAndCheck("a1", true);
    }

    @Test
    public void testIsCellTextWithCellReferenceLowercaseExtra() {
        this.isCellTextAndCheck("a1!", false);
    }

    @Test
    public void testIsCellTextWithCellReferenceLowercaseAbsolute() {
        this.isCellTextAndCheck("$a1", true);
    }

    @Test
    public void testIsCellTextAbsoluteColumnAbsoluteRow() {
        this.isCellTextAndCheck("$a$1", true);
    }

    @Test
    public void testIsCellTextAbsoluteColumnAbsoluteRowExtra() {
        this.isCellTextAndCheck("$a$1!", false);
    }

    @Test
    public void testIsCellTextLastColumn() {
        this.isCellTextAndCheck("XFD1", true);
    }

    @Test
    public void testIsCellTextLastColumnPlus1() {
        this.isCellTextAndCheck("XFE1", false);
    }

    @Test
    public void testIsCellTextLastRow() {
        this.isCellTextAndCheck("A1048576", true);
    }

    @Test
    public void testIsCellTextLastRowPlus1() {
        this.isCellTextAndCheck("A1048577", false);
    }

    @Test
    public void testIsCellTextWithLabel() {
        this.isCellTextAndCheck("LABEL123", false);
    }

    private void isCellTextAndCheck(final String text,
                                    final boolean expected) {
        this.checkEquals(
            expected,
            SpreadsheetExpressionReference.isCellText(text),
            () -> "isCellText " + CharSequences.quoteAndEscape(text)
        );
        if (expected) {
            SpreadsheetSelection.parseCell(text);
        }
    }

    // isLabelText......................................................................................................

    @Test
    public void testIsLabelTextNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetSelection.isLabelText(null)
        );
    }

    @Test
    public void testIsLabelTextEmpty() {
        this.isLabelTextAndCheck(
            "",
            false
        );
    }

    @Test
    public void testIsLabelTextWithTrue() {
        this.isLabelTextAndCheck(
            "true",
            false
        );
    }

    @Test
    public void testIsLabelTextWithTrueCaseInsensitive() {
        this.isLabelTextAndCheck(
            "TRue",
            false
        );
    }

    @Test
    public void testIsLabelTextWithFalse() {
        this.isLabelTextAndCheck(
            "false",
            false
        );
    }

    @Test
    public void testIsLabelTextWithFalseCaseInsensitive() {
        this.isLabelTextAndCheck(
            "FalsE",
            false
        );
    }

    @Test
    public void testIsLabelTextWithLabel() {
        this.isLabelTextAndCheck(
            "Label123",
            true
        );
    }

    @Test
    public void testIsLabelTextWithCell() {
        this.isLabelTextAndCheck(
            "A1",
            false
        );
    }

    @Test
    public void testIsLabelTextWithCellRange() {
        this.isLabelTextAndCheck(
            "A1:B2",
            false
        );
    }

    private void isLabelTextAndCheck(final String text,
                                     final boolean expected) {
        this.checkEquals(
            expected,
            SpreadsheetSelection.isLabelText(text),
            () -> "isLabelText(" + CharSequences.quoteAndEscape(text) + ")"
        );
    }

    // boundingRange....................................................................................................

    @Test
    public void testBoundingRangeWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetSelection.boundingRange(null)
        );
    }

    @Test
    public void testBoundRangeWithEmpty() {
        this.boundingRangeAndCheck(
            Lists.empty(),
            Optional.empty()
        );
    }

    @Test
    public void testBoundRangeWithOne() {
        this.boundingRangeAndCheck(
            "A1",
            "A1"
        );
    }

    @Test
    public void testBoundRangeWithSeveral() {
        this.boundingRangeAndCheck(
            "A1,D4",
            "A1:D4"
        );
    }

    @Test
    public void testBoundRangeWithSeveral2() {
        this.boundingRangeAndCheck(
            "A1,B2,C3,D4",
            "A1:D4"
        );
    }

    @Test
    public void testBoundRangeWithSeveral3() {
        this.boundingRangeAndCheck(
            "D4,C3,B2,A1",
            "A1:D4"
        );
    }

    @Test
    public void testBoundRangeWithSeveral4() {
        this.boundingRangeAndCheck(
            "C3,D4,M2,K9",
            "C2:M9"
        );
    }

    @Test
    public void testBoundRangeWithSeveralSomeAbsolute() {
        this.boundingRangeAndCheck(
            "C3,D4,$M$2,$K$9",
            "C$2:$M$9"
        );
    }

    @Test
    public void testBoundRangeWithSeveralSomeAbsolute2() {
        this.boundingRangeAndCheck(
            "$C$3,D4,M2,K9",
            "$C2:M9"
        );
    }

    private void boundingRangeAndCheck(final String cells,
                                       final String expected) {
        this.boundingRangeAndCheck(
            Arrays.stream(cells.split(","))
                .map(SpreadsheetSelection::parseCell)
                .collect(Collectors.toList()),
            SpreadsheetSelection.parseCellRange(expected)
        );
    }

    private void boundingRangeAndCheck(final Collection<SpreadsheetCellReference> cells,
                                       final SpreadsheetCellRangeReference expected) {
        this.boundingRangeAndCheck(
            cells,
            Optional.of(expected)
        );
    }

    private void boundingRangeAndCheck(final Collection<SpreadsheetCellReference> cells,
                                       final Optional<SpreadsheetCellRangeReference> expected) {
        this.checkEquals(
            expected,
            SpreadsheetSelection.boundingRange(cells),
            cells::toString
        );
    }

    // parseCell........................................................................................................

    @Test
    public void testParseCell() {
        this.checkEquals(
            SpreadsheetSelection.A1,
            SpreadsheetSelection.cell(
                SpreadsheetSelection.parseColumn("A"),
                SpreadsheetSelection.parseRow("1")
            )
        );
    }

    @Test
    public void testParseCellEmptyFails() {
        final String text = "";

        this.parseStringFails(
            text,
            new EmptyTextException("text")
        );
    }

    @Test
    public void testParseCellFails() {
        final String text = "ABC!123";

        this.parseStringFails(
            text,
            new InvalidCharacterException(
                text,
                text.indexOf('!')
            )
        );
    }

    @Test
    public void testParseCellFails2() {
        final String text = "ABC123!456";

        this.parseStringFails(
            text,
            new InvalidCharacterException(
                text,
                text.indexOf('!')
            )
        );
    }

    // parseCellRange...................................................................................................

    @Test
    public void testParseCellRangeFails() {
        final String text = "A1:!B";

        this.parseStringFails(
            text,
            new InvalidCharacterException(
                text,
                text.indexOf('!')
            )
        );
    }

    @Test
    public void testParseCellRangeFails2() {
        final String text = "A1:B!Hello";

        this.parseStringFails(
            text,
            new InvalidCharacterException(
                text,
                text.indexOf('!')
            )
        );
    }

    // parseCellRangeOrLabel............................................................................................

    @Test
    public void testParseCellRangeOrLabelWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetSelection.parseCellRangeOrLabel(null)
        );
    }

    @Test
    public void testParseCellRangeOrLabelWithEmptyFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetSelection.parseCellRangeOrLabel("")
        );
    }

    @Test
    public void testParseCellRangeOrLabelWithInvalidFails() {
        final String text = "A1:B!Hello";

        final InvalidCharacterException thrown = assertThrows(
            InvalidCharacterException.class,
            () -> SpreadsheetSelection.parseCellRange(text)
        );

        this.checkEquals(
            new InvalidCharacterException(
                text,
                text.indexOf("!")
            ).getMessage(),
            thrown.getMessage()
        );
    }

    @Test
    public void testParseCellRangeOrLabelWithCell() {
        this.parseCellRangeOrLabelCheck(
            "a1",
            SpreadsheetSelection::parseCellRange
        );
    }

    @Test
    public void testParseCellRangeOrLabelWithCellRange() {
        this.parseCellRangeOrLabelCheck(
            "B2:C3",
            SpreadsheetSelection::parseCellRange
        );
    }

    @Test
    public void testParseCellRangeOrLabelWithLabel() {
        this.parseCellRangeOrLabelCheck(
            "Label123",
            SpreadsheetSelection::labelName
        );
    }

    private void parseCellRangeOrLabelCheck(final String text,
                                            final Function<String, SpreadsheetExpressionReference> expected) {
        this.checkEquals(
            expected.apply(text),
            SpreadsheetSelection.parseCellRangeOrLabel(text),
            () -> "parseCellRangeOrLabel " + CharSequences.quoteAndEscape(text)
        );
    }

    // parseColumn......................................................................................................

    @Test
    public void testParseColumnWithCellFails() {
        final String text = "ZY99";

        final InvalidCharacterException thrown = assertThrows(
            InvalidCharacterException.class,
            () -> SpreadsheetSelection.parseColumn(text)
        );

        this.checkEquals(
            new InvalidCharacterException(text, 2).getMessage(),
            thrown.getMessage()
        );
    }

    @Test
    public void testParseColumnWithRangeFails() {
        final String text = "B:C";

        final InvalidCharacterException thrown = assertThrows(
            InvalidCharacterException.class,
            () -> SpreadsheetSelection.parseColumn(text)
        );

        this.checkEquals(
            new InvalidCharacterException(text, 1).getMessage(),
            thrown.getMessage()
        );
    }

    @Test
    public void testParseColumnWithRowFails() {
        assertThrows(
            InvalidCharacterException.class,
            () -> SpreadsheetSelection.parseColumn("1")
        );
    }

    @Test
    public void testParseColumn() {
        this.checkEquals(
            SpreadsheetReferenceKind.RELATIVE.column(2),
            SpreadsheetSelection.parseColumn("B")
        );
    }

    // parseColumnOrRow.................................................................................................

    @Test
    public void testParseColumnOrRowWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetSelection.parseColumnOrRow(null)
        );
    }

    @Test
    public void testParseColumnOrRowWithCellFails() {
        this.parseColumnOrRowFails("F6");
    }

    @Test
    public void testParseColumnOrRowWithCellRangeFails() {
        this.parseColumnOrRowFails("D4:E5");
    }

    @Test
    public void testParseColumnOrRowWithColumnRangeFails() {
        this.parseColumnOrRowFails("A:B");
    }

    @Test
    public void testParseColumnOrRowWithLabelFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetSelection.parseColumnOrRow("Label123")
        );
    }

    @Test
    public void testParseColumnOrRowWithRowRangeFails() {
        this.parseColumnOrRowFails("2:3");
    }

    private void parseColumnOrRowFails(final String text) {
        assertThrows(
            InvalidCharacterException.class,
            () -> SpreadsheetSelection.parseColumnOrRow(text)
        );
    }

    @Test
    public void testParseColumnOrRowWithAbsoluteColumn() {
        this.parseColumnOrRowAndCheck(
            "$C",
            SpreadsheetReferenceKind.ABSOLUTE.column(3)
        );
    }

    @Test
    public void testParseColumnOrRowWithRelativeColumn() {
        this.parseColumnOrRowAndCheck(
            "D",
            SpreadsheetReferenceKind.RELATIVE.column(4)
        );
    }

    @Test
    public void testParseColumnOrRowWithAbsoluteRow() {
        this.parseColumnOrRowAndCheck(
            "$3",
            SpreadsheetReferenceKind.ABSOLUTE.row(3)
        );
    }

    @Test
    public void testParseColumnOrRowWithRelativeRow() {
        this.parseColumnOrRowAndCheck(
            "4",
            SpreadsheetReferenceKind.RELATIVE.row(4)
        );
    }

    private void parseColumnOrRowAndCheck(final String text,
                                          final SpreadsheetSelection expected) {
        this.checkEquals(
            expected,
            SpreadsheetSelection.parseColumnOrRow(text),
            () -> "parseColumnOrRow " + CharSequences.quoteAndEscape(text)
        );
    }

    // parseColumnRange.................................................................................................

    @Test
    public void testParseColumnRangeWithCellFails() {
        final String text = "XY12";

        final InvalidCharacterException thrown = assertThrows(
            InvalidCharacterException.class,
            () -> SpreadsheetSelection.parseColumn(text)
        );

        this.checkEquals(
            new InvalidCharacterException(
                text,
                2
            ).getMessage(),
            thrown.getMessage()
        );
    }

    @Test
    public void testParseColumnRangeWithInvalidFails() {
        final String text = "BC!:9";

        final InvalidCharacterException thrown = assertThrows(
            InvalidCharacterException.class,
            () -> SpreadsheetSelection.parseColumn(text)
        );

        this.checkEquals(
            new InvalidCharacterException(
                text,
                text.indexOf('!')
            ).getMessage(),
            thrown.getMessage()
        );
    }

    @Test
    public void testParseColumnRangeWithInvalidFails2() {
        final String text = "B:9";

        final InvalidCharacterException thrown = assertThrows(
            InvalidCharacterException.class,
            () -> SpreadsheetSelection.parseColumn(text)
        );

        this.checkEquals(
            new InvalidCharacterException(text, 1)
                .getMessage(),
            thrown.getMessage()
        );
    }

    @Test
    public void testParseColumnRangeWithExtraComponentFails() {
        final String text = "A:BC:DE";

        final InvalidCharacterException thrown = assertThrows(
            InvalidCharacterException.class,
            () -> SpreadsheetSelection.parseColumnRange(text)
        );

        this.checkEquals(
            new InvalidCharacterException(
                text,
                text.lastIndexOf(':')
            ).getMessage(),
            thrown.getMessage()
        );
    }

    @Test
    public void testParseColumnRangeWithColumn() {
        this.checkEquals(
            SpreadsheetReferenceKind.RELATIVE.column(2)
                .toColumnRange(),
            SpreadsheetSelection.parseColumnRange("B")
        );
    }

    @Test
    public void testParseColumnRangeWithRange() {
        this.checkEquals(
            SpreadsheetReferenceKind.RELATIVE.column(2)
                .columnRange(
                    SpreadsheetReferenceKind.RELATIVE.column(4)
                ),
            SpreadsheetSelection.parseColumnRange("B:D")
        );
    }

    @Test
    public void testParseColumnRangeWithSingleton() {
        this.checkEquals(
            SpreadsheetReferenceKind.RELATIVE.column(2)
                .toColumnRange(),
            SpreadsheetSelection.parseColumnRange("B:B")
        );
    }

    // parseColumnOrColumnRange.........................................................................................

    @Test
    public void testParseColumnOrColumnRangeWithRowFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetSelection.parseColumnOrColumnRange("1")
        );
    }

    @Test
    public void testParseColumnOrColumnRangeWithColumn() {
        this.checkEquals(
            SpreadsheetSelection.parseColumn("B"),
            SpreadsheetSelection.parseColumnOrColumnRange("B")
        );
    }

    @Test
    public void testParseColumnOrColumnRangeWithColumnRange() {
        this.checkEquals(
            SpreadsheetSelection.parseColumnRange("C:D"),
            SpreadsheetSelection.parseColumnOrColumnRange("C:D")
        );
    }

    @Test
    public void testParseColumnOrColumnRangeWithColumnRangeSingleton() {
        this.checkEquals(
            SpreadsheetSelection.parseColumn("E"),
            SpreadsheetSelection.parseColumnOrColumnRange("E:E")
        );
    }

    // ParseExpressionReference.........................................................................................

    @Test
    public void testParseExpressionReferenceStar() {
        this.parseStringAndCheck(
            "*",
            SpreadsheetSelection.ALL_CELLS
        );
    }

    @Test
    public void testParseExpressionReferenceCellReferenceUpperCaseRelativeRelative() {
        final String reference = "A2";
        this.parseStringAndCheck(
            reference,
            SpreadsheetReferenceKind.RELATIVE.column(1)
                .setRow(
                    SpreadsheetReferenceKind.RELATIVE.row(2)
                )
        );
    }

    @Test
    public void testParseExpressionReferenceCellReferenceUpperCaseRelativeAbsolute() {
        final String reference = "C$4";

        this.parseStringAndCheck(
            reference,
            SpreadsheetReferenceKind.RELATIVE.column(3)
                .setRow(
                    SpreadsheetReferenceKind.ABSOLUTE.row(4)
                )
        );
    }

    @Test
    public void testParseExpressionReferenceCellReferenceUpperCaseAbsoluteRelative() {
        final String reference = "$E6";

        this.parseStringAndCheck(
            reference,
            SpreadsheetReferenceKind.ABSOLUTE.column(5)
                .setRow(
                    SpreadsheetReferenceKind.RELATIVE.row(6)
                )
        );
    }

    @Test
    public void testParseExpressionReferenceCellReferenceUpperCaseAbsoluteAbsolute() {
        final String reference = "$G$8";
        this.parseStringAndCheck(
            reference,
            SpreadsheetReferenceKind.ABSOLUTE.column(7)
                .setRow(
                    SpreadsheetReferenceKind.ABSOLUTE.row(8)
                )
        );
    }

    @Test
    public void testParseExpressionReferenceCellReferenceLowercaseRelativeRelative() {
        final String reference = "i10";

        this.parseStringAndCheck(
            reference,
            SpreadsheetReferenceKind.RELATIVE.column(9)
                .setRow(SpreadsheetReferenceKind.RELATIVE.row(10))
        );
    }

    @Test
    public void testParseExpressionReferenceCellReferenceLowercaseAbsolute() {
        final String reference = "$k12";

        this.parseStringAndCheck(
            reference,
            SpreadsheetReferenceKind.ABSOLUTE.column(11)
                .setRow(
                    SpreadsheetReferenceKind.RELATIVE.row(12)
                )
        );
    }

    @Test
    public void testParseExpressionReferenceLabelFails() {
        final String text = "Label1 2";

        final InvalidCharacterException thrown = assertThrows(
            InvalidCharacterException.class,
            () -> SpreadsheetSelection.labelName(text)
        );

        this.checkEquals(
            new InvalidCharacterException(
                text,
                text.indexOf(' ')
            ).getMessage(),
            thrown.getMessage()
        );
    }

    @Test
    public void testParseExpressionReferenceLabel() {
        final String label = "label123";
        this.parseStringAndCheck(label, SpreadsheetSelection.labelName(label));
    }

    @Test
    public void testParseExpressionReferenceExtraComponentFails() {
        this.parseStringFails(
            "A1:B2:C3",
            new IllegalArgumentException("Expected cell, label or range got \"A1:B2:C3\"")
        );
    }

    @Test
    public void testParseExpressionReferenceCellRange() {
        final String range = "A2:B2";
        this.parseStringAndCheck(
            range,
            SpreadsheetSelection.parseCellRange(range)
        );
    }

    // parseCellOrLabel.................................................................................................

    @Test
    public void testParseCellOrLabelNullFails() {
        parseCellOrLabelFails(
            null,
            NullPointerException.class
        );
    }

    @Test
    public void testParseCellOrLabelEmptyFails() {
        parseCellOrLabelFails(
            "",
            IllegalArgumentException.class
        );
    }

    @Test
    public void testParseCellOrLabelStarFails() {
        parseCellOrLabelFails(
            "*",
            InvalidCharacterException.class
        );
    }

    @Test
    public void testParseCellOrLabelRangeFails() {
        parseCellOrLabelFails(
            SpreadsheetSelection.parseCellRange("A1:B2").toString(),
            InvalidCharacterException.class
        );
    }

    private void parseCellOrLabelFails(final String text,
                                       final Class<? extends RuntimeException> thrown) {
        assertThrows(thrown, () -> SpreadsheetSelection.parseCellOrLabel(text));
    }

    @Test
    public void testParseCellOrLabelCell() {
        final String text = "A1";
        this.parseCellOrLabelAndCheck(text, SpreadsheetSelection.parseCell(text));
    }

    @Test
    public void testParseCellOrLabelLabel() {
        final String text = "Label123";
        this.parseCellOrLabelAndCheck(text, SpreadsheetCellReference.labelName(text));
    }

    private void parseCellOrLabelAndCheck(final String text,
                                          final SpreadsheetExpressionReference expected) {
        final SpreadsheetExpressionReference parsed = SpreadsheetSelection.parseCellOrLabel(text);

        this.checkEquals(
            expected,
            parsed,
            () -> "Parsing of " + CharSequences.quoteAndEscape(text) + " failed"
        );
    }

    // parseCellOrCellRange.............................................................................................

    @Test
    public void testParseCellOrCellRangeNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetSelection.parseCellOrCellRange(null)
        );
    }

    @Test
    public void testParseCellOrCellRangeEmptyFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetSelection.parseCellOrCellRange("")
        );
    }

    @Test
    public void testParseCellOrCellRangeInvalidCellFails() {
        final String text = "A@@@";

        final InvalidCharacterException thrown = assertThrows(
            InvalidCharacterException.class,
            () -> SpreadsheetSelection.parseCellOrCellRange(text)
        );
        this.checkEquals(
            new InvalidCharacterException(
                text,
                text.indexOf('@')
            ).getMessage(),
            thrown.getMessage(),
            "message"
        );
    }

    @Test
    public void testParseCellOrCellRangeInvalidCellRangeFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetSelection.parseCellOrCellRange("A1:")
        );
        this.checkEquals(
            "Empty upper range in \"A1:\"",
            thrown.getMessage(),
            "message"
        );
    }

    @Test
    public void testParseCellOrCellRangeWithCell() {
        this.parseCellOrCellRangeAndCheck(
            "A1",
            SpreadsheetSelection.A1
        );
    }

    @Test
    public void testParseCellOrCellRangeWithCellRange() {
        this.parseCellOrCellRangeAndCheck(
            "B2:C3",
            SpreadsheetSelection.parseCellOrCellRange("B2:C3")
        );
    }

    @Test
    public void testParseCellOrCellRangeWithCellRangeSame() {
        this.parseCellOrCellRangeAndCheck(
            "D4:D4",
            SpreadsheetSelection.parseCellOrCellRange("D4:D4")
        );
    }

    @Test
    public void testParseCellOrCellRangeWithStar() {
        this.parseCellOrCellRangeAndCheck(
            "*",
            SpreadsheetSelection.ALL_CELLS
        );
    }

    private void parseCellOrCellRangeAndCheck(final String text,
                                              final SpreadsheetCellReferenceOrRange expected) {
        this.checkEquals(
            expected,
            SpreadsheetSelection.parseCellOrCellRange(text),
            () -> "parseCellOrCellRange(" + CharSequences.quoteAndEscape(text) + ")"
        );
    }

    // parseRow.........................................................................................................

    @Test
    public void testParseRowWithCellFails() {
        final String text = "A23";

        final InvalidCharacterException thrown = assertThrows(
            InvalidCharacterException.class,
            () -> SpreadsheetSelection.parseRow(text)
        );

        this.checkEquals(
            new InvalidCharacterException(text, 0)
                .getMessage(),
            thrown.getMessage()
        );
    }

    @Test
    public void testParseRowWithColumnFails() {
        final String text = "A";

        final InvalidCharacterException thrown = assertThrows(
            InvalidCharacterException.class,
            () -> SpreadsheetSelection.parseRow(text)
        );

        this.checkEquals(
            new InvalidCharacterException(text, 0)
                .getMessage(),
            thrown.getMessage()
        );
    }

    @Test
    public void testParseRowWithRangeFails() {
        final String text = "12:34";

        final InvalidCharacterException thrown = assertThrows(
            InvalidCharacterException.class,
            () -> SpreadsheetSelection.parseRow(text)
        );

        this.checkEquals(
            new InvalidCharacterException(text, 2)
                .getMessage(),
            thrown.getMessage()
        );
    }

    @Test
    public void testParseRow() {
        this.checkEquals(
            SpreadsheetReferenceKind.RELATIVE.row(2),
            SpreadsheetSelection.parseRow("2")
        );
    }

    @Test
    public void testParseRow2() {
        this.checkEquals(
            SpreadsheetReferenceKind.RELATIVE.row(23),
            SpreadsheetSelection.parseRow("23")
        );
    }

    // parseRowRange....................................................................................................

    @Test
    public void testParseRowRangeWithColumnFails() {
        final String text = "C";

        final InvalidCharacterException thrown = assertThrows(
            InvalidCharacterException.class,
            () -> SpreadsheetSelection.parseRow(text)
        );

        this.checkEquals(
            new InvalidCharacterException(text, 0)
                .getMessage(),
            thrown.getMessage()
        );
    }

    @Test
    public void testParseRowRangeWithInvalidFails() {
        final String text = "23X:Y";

        final InvalidCharacterException thrown = assertThrows(
            InvalidCharacterException.class,
            () -> SpreadsheetSelection.parseRow(text)
        );

        this.checkEquals(
            new InvalidCharacterException(text, 2)
                .getMessage(),
            thrown.getMessage()
        );
    }

    @Test
    public void testParseRowRangeWithInvalidFails2() {
        final String text = "23:X";

        final InvalidCharacterException thrown = assertThrows(
            InvalidCharacterException.class,
            () -> SpreadsheetSelection.parseRow(text)
        );

        this.checkEquals(
            new InvalidCharacterException(text, 2)
                .getMessage(),
            thrown.getMessage()
        );
    }

    @Test
    public void testParseRowRangeWithExtraComponentFails() {
        final String text = "1:2:3";

        final InvalidCharacterException thrown = assertThrows(
            InvalidCharacterException.class,
            () -> SpreadsheetSelection.parseRowRange(text)
        );

        this.checkEquals(
            new InvalidCharacterException(
                text,
                text.lastIndexOf(':')
            ).getMessage(),
            thrown.getMessage()
        );
    }

    @Test
    public void testParseRowRangeWithRow() {
        this.checkEquals(
            SpreadsheetReferenceKind.RELATIVE.row(2)
                .toRowRange(),
            SpreadsheetSelection.parseRowRange("2")
        );
    }

    @Test
    public void testParseRowRangeWithRange() {
        this.checkEquals(
            SpreadsheetReferenceKind.RELATIVE.row(2)
                .rowRange(
                    SpreadsheetReferenceKind.RELATIVE.row(4)
                ),
            SpreadsheetSelection.parseRowRange("2:4")
        );
    }

    @Test
    public void testParseRowRangeWithSingleton() {
        this.checkEquals(
            SpreadsheetReferenceKind.RELATIVE.row(2)
                .toRowRange(),
            SpreadsheetSelection.parseRowRange("2:2")
        );
    }

    // parseRowOrRowRange...............................................................................................

    @Test
    public void testParseRowOrRowRangeWithColumnFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetSelection.parseRowOrRowRange("A")
        );
    }

    @Test
    public void testParseRowOrRowRangeWithRow() {
        this.checkEquals(
            SpreadsheetSelection.parseRow("2"),
            SpreadsheetSelection.parseRowOrRowRange("2")
        );
    }

    @Test
    public void testParseRowOrRowRangeWithRowRange() {
        this.checkEquals(
            SpreadsheetSelection.parseRowRange("3:4"),
            SpreadsheetSelection.parseRowOrRowRange("3:4")
        );
    }

    @Test
    public void testParseRowOrRowRangeWithRowRangeSingleton() {
        this.checkEquals(
            SpreadsheetSelection.parseRow("5"),
            SpreadsheetSelection.parseRowOrRowRange("5:5")
        );
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetExpressionReference parseString(final String text) {
        return SpreadsheetSelection.parseExpressionReference(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(Class<? extends RuntimeException> throwing) {
        return throwing;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException expected) {
        return expected;
    }

    // textLabel........................................................................................................

    @Test
    public void testTextLabelCell() {
        this.textLabelAndCheck(
            SpreadsheetSelection.parseCell("B2"),
            "Cell"
        );
    }

    @Test
    public void testTextLabelCellRange() {
        this.textLabelAndCheck(
            SpreadsheetSelection.parseCellRange("C3:D4"),
            "Cell Range"
        );
    }

    @Test
    public void testTextLabelColumn() {
        this.textLabelAndCheck(
            SpreadsheetSelection.parseColumn("C"),
            "Column"
        );
    }

    @Test
    public void testTextLabelColumnRange() {
        this.textLabelAndCheck(
            SpreadsheetSelection.parseColumnRange("C:D"),
            "Column Range"
        );
    }

    @Test
    public void testTextLabelSpreadsheetLabelName() {
        this.textLabelAndCheck(
            SpreadsheetSelection.labelName("Label123"),
            "Label"
        );
    }

    @Test
    public void testTextLabelRow() {
        this.textLabelAndCheck(
            SpreadsheetSelection.parseRow("2"),
            "Row"
        );
    }

    @Test
    public void testTextLabelRowRange() {
        this.textLabelAndCheck(
            SpreadsheetSelection.parseRowRange("3:4"),
            "Row Range"
        );
    }

    private void textLabelAndCheck(final SpreadsheetSelection selection,
                                   final String textLabel) {
        this.checkEquals(
            textLabel,
            selection.textLabel(),
            () -> "textLabel of " + selection
        );
    }

    // parse............................................................................................................

    @Test
    public void testParseWithNullSelectionFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetSelection.parse(null, "cell")
        );
    }

    @Test
    public void testParseWithNullSelectionTypeFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetSelection.parse("A1", null)
        );
    }

    @Test
    public void testParseWithInvalidSelectionType() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetSelection.parse("A1", "bad123")
        );
        this.checkEquals(
            "Invalid selectionType \"bad123\" value \"A1\"",
            thrown.getMessage(),
            "message"
        );
    }

    @Test
    public void testParseWithInvalidCell() {
        final String cell = "hello";

        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetSelection.parse(cell, "cell")
        );

        this.checkEquals(
            "Invalid column \"hello\" not between \"A\" and \"XFD\"",
            thrown.getMessage(),
            "message"
        );
    }

    @Test
    public void testParseWithCell() {
        this.parseAndCheck(
            "A1",
            "cell",
            SpreadsheetSelection::parseCell
        );
    }

    @Test
    public void testParseWithCellRange() {
        this.parseAndCheck(
            "B2:C3",
            "cell-range",
            SpreadsheetSelection::parseCellRange
        );
    }

    @Test
    public void testParseWithColumn() {
        this.parseAndCheck(
            "D",
            "column",
            SpreadsheetSelection::parseColumn
        );
    }

    @Test
    public void testParseWithColumnRange() {
        this.parseAndCheck(
            "E:F",
            "column-range",
            SpreadsheetSelection::parseColumnRange
        );
    }

    @Test
    public void testParseWithRow() {
        this.parseAndCheck(
            "11",
            "row",
            SpreadsheetSelection::parseRow
        );
    }

    @Test
    public void testParseWithRowRange() {
        this.parseAndCheck(
            "22:33",
            "row-range",
            SpreadsheetSelection::parseRowRange
        );
    }

    @Test
    public void testParseWithLabel() {
        this.parseAndCheck(
            "Label123",
            "label",
            SpreadsheetSelection::labelName
        );
    }

    private void parseAndCheck(final String selection,
                               final String selectionType,
                               final Function<String, SpreadsheetSelection> expected) {
        this.parseAndCheck(
            selection,
            selectionType,
            expected.apply(selection)
        );
    }

    private void parseAndCheck(final String selection,
                               final String selectionType,
                               final SpreadsheetSelection expected) {
        final SpreadsheetSelection spreadsheetSelection = SpreadsheetSelection.parse(
            selection,
            selectionType
        );
        this.checkEquals(
            expected,
            spreadsheetSelection,
            () -> "parse " + CharSequences.quoteAndEscape(selection) + " " + CharSequences.quoteAndEscape(selectionType)
        );

        this.textAndCheck(
            spreadsheetSelection,
            selection
        );
    }

    // isScalar.........................................................................................................

    @Test
    public void testIsScalarWithCell() {
        this.isScalarAndCheck(
            SpreadsheetSelection.A1,
            true
        );
    }

    @Test
    public void testIsScalarWithCellRange() {
        this.isScalarAndCheck(
            SpreadsheetSelection.parseCellRange("A1"),
            false
        );
    }

    @Test
    public void testIsScalarWithColumn() {
        this.isScalarAndCheck(
            SpreadsheetSelection.A1.column(),
            true
        );
    }

    @Test
    public void testIsScalarWithColumnRange() {
        this.isScalarAndCheck(
            SpreadsheetSelection.A1.toColumnRange(),
            false
        );
    }

    @Test
    public void testIsScalarWithLabel() {
        this.isScalarAndCheck(
            SpreadsheetSelection.labelName("Label123"),
            false
        );
    }

    @Test
    public void testIsScalarWithRow() {
        this.isScalarAndCheck(
            SpreadsheetSelection.A1.row(),
            true
        );
    }

    @Test
    public void testIsScalarWithRowRange() {
        this.isScalarAndCheck(
            SpreadsheetSelection.A1.toRowRange(),
            false
        );
    }

    private void isScalarAndCheck(final SpreadsheetSelection selection,
                                  final boolean expected) {
        this.checkEquals(
            expected,
            selection.isScalar(),
            selection::toString
        );
    }

    // toCellRange......................................................................................................

    @Test
    public void testToCellRangeWhenLabelFails() {
        final UnsupportedOperationException thrown = assertThrows(
            UnsupportedOperationException.class,
            () -> SpreadsheetSelection.labelName("Label123").toCellRange()
        );

        this.checkEquals(
            "Selection is a label \"Label123\"",
            thrown.getMessage()
        );
    }

    @Test
    public void testToCellRangeCell() {
        this.toCellRangeAndCheck(
            SpreadsheetSelection.A1,
            "A1:A1"
        );
    }

    @Test
    public void testToCellRangeCellB2() {
        this.toCellRangeAndCheck(
            SpreadsheetSelection.parseCell("B2"),
            "B2:B2"
        );
    }

    @Test
    public void testToCellRangeColumn() {
        this.toCellRangeAndCheck(
            SpreadsheetSelection.parseColumn("A"),
            "A1:A1048576"
        );
    }

    @Test
    public void testToCellRangeColumn2() {
        this.toCellRangeAndCheck(
            SpreadsheetSelection.parseColumn("B"),
            "B1:B1048576"
        );
    }

    @Test
    public void testToCellRangeColumnRange() {
        this.toCellRangeAndCheck(
            SpreadsheetSelection.parseColumnRange("A:B"),
            "A1:B1048576"
        );
    }

    @Test
    public void testToCellRangeColumnRange2() {
        this.toCellRangeAndCheck(
            SpreadsheetSelection.parseColumnRange("B:C"),
            "B1:C1048576"
        );
    }

    @Test
    public void testToCellRangeRow() {
        this.toCellRangeAndCheck(
            SpreadsheetSelection.parseRow("1"),
            "A1:XFD1"
        );
    }

    @Test
    public void testToCellRangeRow2() {
        this.toCellRangeAndCheck(
            SpreadsheetSelection.parseRow("2"),
            "A2:XFD2"
        );
    }

    @Test
    public void testToCellRangeRowRange() {
        this.toCellRangeAndCheck(
            SpreadsheetSelection.parseRowRange("1:2"),
            "A1:XFD2"
        );
    }

    @Test
    public void testToCellRangeRowRange2() {
        this.toCellRangeAndCheck(
            SpreadsheetSelection.parseRowRange("2:3"),
            "A2:XFD3"
        );
    }

    private void toCellRangeAndCheck(final SpreadsheetSelection selection,
                                     final String expected) {
        this.toCellRangeAndCheck(
            selection,
            SpreadsheetSelection.parseCellRange(expected)
        );
    }

    private void toCellRangeAndCheck(final SpreadsheetSelection selection,
                                     final SpreadsheetCellRangeReference expected) {
        this.checkEquals(
            expected,
            selection.toCellRange(),
            selection::toString
        );
    }

    // deletedText.......................................................................................................

    @Test
    public void testDeletedTextCell() {
        this.checkEquals(
            "Cell deleted: Z99",
            SpreadsheetSelection.parseCell("Z99").deleteText()
        );
    }

    @Test
    public void testDeletedTextColumn() {
        this.checkEquals(
            "Column deleted: Z",
            SpreadsheetSelection.parseColumn("Z").deleteText()
        );
    }

    // notFoundText.....................................................................................................

    @Test
    public void testNotFoundText() {
        this.notFoundTextAndCheck(
            SpreadsheetSelection.parseCell("Z99"),
            "Cell not found: \"Z99\""
        );
    }

    // sortedSetIgnoresReferenceKindCollector...........................................................................

    @Test
    public void testSortedSetIgnoresReferenceKindCollectorWithCell() {
        Sets.of(
                SpreadsheetSelection.A1
            ).stream()
            .collect(SpreadsheetSelection.sortedSetIgnoresReferenceKindCollector());
    }

    @Test
    public void testSortedSetIgnoresReferenceKindCollectorWithColumn() {
        Sets.of(
                SpreadsheetSelection.parseColumn("A")
            ).stream()
            .collect(SpreadsheetSelection.sortedSetIgnoresReferenceKindCollector());
    }

    @Test
    public void testSortedSetIgnoresReferenceKindCollectorWithExpressionReference() {
        Sets.of(
                SpreadsheetSelection.A1,
                SpreadsheetSelection.parseCellRange("B2:C3"),
                SpreadsheetSelection.labelName("Label123")
            ).stream()
            .collect(SpreadsheetSelection.sortedSetIgnoresReferenceKindCollector());
    }

    // parser constants.................................................................................................

    @Test
    public void testCellParserToString() {
        this.checkEquals(
            "CELL",
            SpreadsheetSelection.CELL_PARSER.toString()
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetSelection> type() {
        return SpreadsheetSelection.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
