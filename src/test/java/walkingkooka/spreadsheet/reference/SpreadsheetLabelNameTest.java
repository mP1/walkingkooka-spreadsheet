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
import walkingkooka.InvalidCharacterException;
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.naming.NameTesting2;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportAnchor;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportWindows;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.visit.Visiting;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

final public class SpreadsheetLabelNameTest extends SpreadsheetExpressionReferenceTestCase<SpreadsheetLabelName>
    implements ComparableTesting2<SpreadsheetLabelName>,
    NameTesting2<SpreadsheetLabelName, SpreadsheetLabelName> {

    @Test
    public void testWithTrueFails() {
        withFails(
            "true",
            IllegalArgumentException.class,
            "Invalid label with \"true\""
        );
    }

    @Test
    public void testWithTrueFails2() {
        withFails(
            "TRue",
            IllegalArgumentException.class,
            "Invalid label with \"TRue\""
        );
    }

    @Test
    public void testWithFalseFails() {
        withFails(
            "false",
            IllegalArgumentException.class,
            "Invalid label with \"false\""
        );
    }

    @Test
    public void testWithInvalidInitialFails() {
        this.withFails(
            "1abc",
            InvalidCharacterException.class,
            "Invalid character '1' at 0"
        );
    }

    @Test
    public void testWithInvalidPartFails() {
        this.withFails(
            "abc$def",
            InvalidCharacterException.class,
            "Invalid character '$' at 3"
        );
    }

    @Test
    public void testWithContainsBackslashFails() {
        this.withFails(
            "Label\\",
            InvalidCharacterException.class,
            "Invalid character '\\\\' at 5"
        );
    }

    @Test
    public void testWithCellReferenceFails() {
        this.withFails(
            "A1",
            IllegalArgumentException.class,
            "Label cannot be a valid cell reference=\"A1\""
        );
    }

    @Test
    public void testWithCellReferenceFails2() {
        this.withFails(
            "AB12",
            IllegalArgumentException.class,
            "Label cannot be a valid cell reference=\"AB12\""
        );
    }

    @Test
    public void testWithCellRangeFails() {
        this.withFails(
            "A1:B2",
            IllegalArgumentException.class
        );
    }

    private <T extends IllegalArgumentException> void withFails(final String text,
                                                                final Class<T> throwsClass) {

        this.withFails(
            text,
            throwsClass,
            null
        );
    }

    private <T extends IllegalArgumentException> void withFails(final String text,
                                                                final Class<T> throwsClass,
                                                                final String message) {
        final T thrown = assertThrows(
            throwsClass,
            () -> SpreadsheetLabelName.with(text)
        );

        if (null != message) {
            this.checkEquals(
                message,
                thrown.getMessage(),
                "message"
            );
        }

        this.checkEquals(
            false,
            SpreadsheetLabelName.isLabelText0(text),
            () -> "isLabelText0(" + CharSequences.quoteAndEscape(text) + ")"
        );
    }

    @Test//(expected = IllegalArgumentException.class)
    public void testCellReferenceFails3() {
        SpreadsheetLabelName.with(SpreadsheetColumnReference.MAX_VALUE_STRING);
    }

    @Test
    public void testWith2() {
        this.createNameAndCheck2("ZZZ1");
    }

    @Test
    public void testWith3() {
        this.createNameAndCheck2("A123Hello");
    }

    @Test
    public void testWith4() {
        this.createNameAndCheck2("A1B2C2");
    }

    @Test
    public void testWithBeginsWithNonAsciiLetter() {
        this.createNameAndCheck2("\u0100ZZZ1");
    }

    @Test
    public void testWithBeginsWithBackslashAlpha() {
        this.createNameAndCheck2("\\Label");
    }

    @Test
    public void testWithBeginsWithUnderscoreAlpha() {
        this.createNameAndCheck2("_Label");
    }

    @Test
    public void testWithLetterDigits() {
        this.createNameAndCheck2(
            "A1234567"
        );
    }

    @Test
    public void testWithLetterDigitsLetters() {
        this.createNameAndCheck2(
            "A1B"
        );
    }

    @Test
    public void testWithLetterUnderscore() {
        this.createNameAndCheck2(
            "A_"
        );
    }

    @Test
    public void testWithLetterDot() {
        this.createNameAndCheck2(
            "A."
        );
    }

    @Test
    public void testWithLetterNonAsciiLetter() {
        this.createNameAndCheck2(
            "A\u0100"
        );
    }

    @Test
    public void testWithMissingRow() {
        this.createNameAndCheck2("A");
    }

    @Test
    public void testWithMissingRow2() {
        this.createNameAndCheck2("ABC");
    }

    @Test
    public void testWithEnormousColumn() {
        this.createNameAndCheck2("ABCDEF1");
    }

    @Test
    public void testWithEnormousColumn2() {
        this.createNameAndCheck2("ABCDEF");
    }

    @Test
    public void testWithInvalidRow() {
        this.createNameAndCheck2(
            "A" + (SpreadsheetRowReference.MAX_VALUE + 1)
        );
    }

    private void createNameAndCheck2(final String value) {
        this.createNameAndCheck(value);

        this.checkEquals(
            true,
            SpreadsheetLabelName.isLabelText0(value),
            () -> "with " + CharSequences.quoteAndEscape(value) + " was successful "
        );
    }

    @Test
    public void testSetLabelMappingReferenceWithCell() {
        this.setLabelMappingReferenceAndCheck(SpreadsheetSelection.A1);
    }

    @Test
    public void testSetLabelMappingTargetWithLabel() {
        this.setLabelMappingReferenceAndCheck(SpreadsheetSelection.labelName("LABEL456"));
    }

    @Test
    public void testSetLabelMappingReferenceWithCellRange() {
        this.setLabelMappingReferenceAndCheck(SpreadsheetSelection.parseCellRange("A1:b2"));
    }

    private void setLabelMappingReferenceAndCheck(final SpreadsheetExpressionReference reference) {
        final SpreadsheetLabelName label = SpreadsheetLabelName.with("LABEL123");

        final SpreadsheetLabelMapping mapping = label.setLabelMappingReference(reference);
        assertSame(label, mapping.label(), "label");
        assertSame(reference, mapping.reference(), "reference");
    }

    @Override
    public void testNameValidChars() {
        // test ignored because short generated names will clash with valid cell references and fail the test.
    }

    // notFound.........................................................................................................

    @Test
    public void testNotFound() {
        this.notFoundTextAndCheck(
            SpreadsheetSelection.labelName("Hello"),
            "Label not found: \"Hello\""
        );
    }

    // text............................................................................................................

    @Test
    public void testText() {
        this.textAndCheck("Label123");
    }

    // toCellOrFail.....................................................................................................

    @Test
    public void testToCellFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createSelection()
                .toCell()
        );
    }

    // toCellOrCellRange................................................................................................

    @Test
    public void testToCellOrCellRangeFails() {
        this.toCellOrCellRangeFails();
    }

    // toColumn.........................................................................................................

    @Test
    public void testToColumnFails() {
        this.toColumnFails();
    }

    // toColumnRange....................................................................................................

    @Test
    public void testToColumnRangeFails() {
        this.toColumnRangeFails();
    }

    // toColumnOrColumnRange............................................................................................

    @Test
    public void testToColumnOrColumnRangeFails() {
        this.toColumnOrColumnRangeFails();
    }

    // toRow.........................................................................................................

    @Test
    public void testToRowFails() {
        this.toRowFails();
    }

    // toRowRange....................................................................................................

    @Test
    public void testToRowRangeFails() {
        this.toRowRangeFails();
    }

    // toRowOrRowRange..................................................................................................

    @Test
    public void testToRowOrRowRangeFails() {
        this.toRowOrRowRangeFails();
    }

    // containsAll......................................................................................................

    @Test
    public void testContainsAllFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createSelection()
                .containsAll(SpreadsheetViewportWindows.EMPTY)
        );
    }

    // toScalar.........................................................................................................

    @Test
    public void testToScalar() {
        this.toScalarAndCheck(
            this.createSelection()
        );
    }

    // toRange..........................................................................................................

    @Test
    public void testToRange() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createSelection()
                .toRange()
        );
    }

    // Comparator ......................................................................................................

    @Test
    public void testSort() {
        final SpreadsheetLabelName a1 = SpreadsheetLabelName.with("LABELa1");
        final SpreadsheetLabelName b2 = SpreadsheetLabelName.with("LABELB2");
        final SpreadsheetLabelName c3 = SpreadsheetLabelName.with("LABELC3");
        final SpreadsheetLabelName d4 = SpreadsheetLabelName.with("LABELd4");

        this.compareToArraySortAndCheck(d4, c3, a1, b2,
            a1, b2, c3, d4);
    }

    // count...........................................................................................................

    @Test
    public void testCount() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createSelection().count()
        );
    }

    // addIfRelative....................................................................................................

    @Test
    public void testAddIfRelativeIgnored() {
        final SpreadsheetLabelName label = this.createSelection();

        assertSame(
            label,
            label.addIfRelative(1, 2)
        );
    }

    // testCell.........................................................................................................

    @Test
    public void testTestCellFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createSelection().testCell(SpreadsheetSelection.A1)
        );
    }

    // testCellRange.....................................................................................................

    @Test
    public void testRangeFails() {
        assertThrows(UnsupportedOperationException.class, () -> this.createSelection().testCellRange(SpreadsheetSelection.parseCellRange("A1")));
    }

    // testColumnRange..................................................................................................

    @Test
    public void testColumnFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createSelection()
                .testColumn(SpreadsheetReferenceKind.RELATIVE.firstColumn())
        );
    }

    // testRowRange..................................................................................................

    @Test
    public void testRowFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createSelection()
                .testRow(SpreadsheetReferenceKind.RELATIVE.firstRow())
        );
    }

    @Test
    public void testToCellRangeFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> SpreadsheetLabelName.with("Label123").toCellRange()
        );
    }

    // extendRange......................................................................................................

    @Override
    SpreadsheetLabelName parseRange(final String range) {
        return SpreadsheetSelection.labelName(range);
    }

    // SpreadsheetSelectionVisitor......................................................................................

    @Test
    public void testSpreadsheetSelectionVisitorAccept() {
        final StringBuilder b = new StringBuilder();
        final SpreadsheetLabelName selection = this.createSelection();

        new FakeSpreadsheetSelectionVisitor() {
            @Override
            protected Visiting startVisit(final SpreadsheetSelection s) {
                assertSame(selection, s);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final SpreadsheetSelection s) {
                assertSame(selection, s);
                b.append("2");
            }

            @Override
            protected void visit(final SpreadsheetLabelName s) {
                assertSame(selection, s);
                b.append("3");
            }
        }.accept(selection);
        this.checkEquals("132", b.toString());
    }

    // extendRange......................................................................................................

    @Test
    public void testExtendRange() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createSelection()
                .extendRange(
                    Optional.empty(),
                    SpreadsheetViewportAnchor.NONE
                )
        );
    }

    // focused..........................................................................................................

    @Test
    public void testFocusedFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createSelection().focused(SpreadsheetViewportAnchor.NONE)
        );
    }

    // toParserToken....................................................................................................

    @Test
    public void testToParserToken() {
        final String text = "Label123";

        this.toParserTokenAndCheck(
            SpreadsheetSelection.labelName(text),
            SpreadsheetFormulaParserToken.label(
                SpreadsheetSelection.labelName(text),
                text
            ),
            SpreadsheetFormulaParsers.labelName()
        );
    }

    // equalsIgnoreReferenceKind.........................................................................................

    @Test
    public void testEqualsIgnoreReferenceDifferentName() {
        this.equalsIgnoreReferenceKindAndCheck(this.createSelection(),
            SpreadsheetLabelName.with("different"),
            false);
    }

    // toRelative.......................................................................................................

    @Test
    public void testToRelative() {
        final SpreadsheetLabelName labelName = this.createSelection();
        final SpreadsheetLabelName relative = labelName.toRelative();
        assertSame(labelName, relative);
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            SpreadsheetSelection.labelName("Label123"),
            "label Label123" + EOL
        );
    }

    // toString.........................................................................................................

    @Test
    @Override
    public void testToString() {
        final SpreadsheetLabelName labelName = this.createSelection();
        this.toStringAndCheck(
            labelName,
            labelName.text()
        );
    }

    // toStringMaybeStar.........................................................................................................

    @Test
    public void testToStringMaybeStar() {
        final SpreadsheetLabelName labelName = this.createSelection();
        this.toStringMaybeStarAndCheck(
            labelName
        );
    }

    // JsonNodeMarshallingTesting.......................................................................................

    @Test
    public void testUnmarshallString() {
        final String value = "LABEL123";
        this.unmarshallAndCheck(JsonNode.string(value),
            SpreadsheetLabelName.with(value));
    }

    // equalsIgnoreReferenceKind.................................................................................................

    @Test
    public void testEqualsIgnoreReferenceKindDifferent() {
        this.equalsIgnoreReferenceKindAndCheck(
            "Label1",
            "Label2",
            false
        );
    }

    @Test
    public void testEqualsDifferentCase() {
        this.checkEqualsAndHashCode(
            SpreadsheetLabelName.with("Label123"),
            SpreadsheetLabelName.with("LABEL123")
        );
    }

    @Override
    SpreadsheetLabelName createSelection() {
        return this.createComparable();
    }

    @Override
    public SpreadsheetLabelName createName(final String name) {
        return SpreadsheetLabelName.with(name);
    }

    @Override
    public CaseSensitivity caseSensitivity() {
        return CaseSensitivity.INSENSITIVE;
    }

    @Override
    public String nameText() {
        return "state";
    }

    @Override
    public String differentNameText() {
        return "different";
    }

    @Override
    public String nameTextLess() {
        return "postcode";
    }

    @Override
    public int minLength() {
        return 1;
    }

    @Override
    public int maxLength() {
        return SpreadsheetLabelName.MAX_LENGTH;
    }

    @Override
    public String possibleValidChars(final int position) {
        return 0 == position ?
            ASCII_LETTERS + "\\_" :
            ASCII_LETTERS_DIGITS + "_.";
    }

    @Override
    public String possibleInvalidChars(final int position) {
        return 0 == position ?
            ASCII_DIGITS + CONTROL + "!@#$%^&*()" :
            CONTROL + "!@#$%^&*()";
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetLabelName> type() {
        return SpreadsheetLabelName.class;
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetLabelName unmarshall(final JsonNode from,
                                           final JsonNodeUnmarshallContext context) {
        return SpreadsheetLabelName.unmarshallLabelName(from, context);
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetLabelName parseString(final String text) {
        return SpreadsheetSelection.labelName(text);
    }
}
