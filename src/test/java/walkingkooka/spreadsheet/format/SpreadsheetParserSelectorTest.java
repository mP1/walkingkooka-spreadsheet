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

package walkingkooka.spreadsheet.format;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.InvalidCharacterException;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.CharSequences;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetParserSelectorTest implements ClassTesting2<SpreadsheetParserSelector>,
        HashCodeEqualsDefinedTesting2<SpreadsheetParserSelector>,
        HasTextTesting,
        ToStringTesting<SpreadsheetParserSelector>,
        ParseStringTesting<SpreadsheetParserSelector>,
        JsonNodeMarshallingTesting<SpreadsheetParserSelector>,
        TreePrintableTesting {

    private final static SpreadsheetParserName NAME = SpreadsheetParserName.with("number-parse-pattern");

    private final static String TEXT = "$0.00";

    @Test
    public void testWithNullNameFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetParserSelector.with(
                        null,
                        TEXT
                )
        );
    }

    @Test
    public void testWithNullTextFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetParserSelector.with(
                        NAME,
                        null
                )
        );
    }

    @Test
    public void testWith() {
        final SpreadsheetParserSelector selector = SpreadsheetParserSelector.with(
                NAME,
                TEXT
        );

        this.checkEquals(NAME, selector.name(), "name");
        this.textAndCheck(
                selector,
                TEXT
        );
    }

    // setName..........................................................................................................

    @Test
    public void testSetNameWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetParserSelector.with(
                        NAME,
                        TEXT
                ).setName(null)
        );
    }

    @Test
    public void testSetNameWithSame() {
        final SpreadsheetParserSelector selector = SpreadsheetParserSelector.with(
                NAME,
                TEXT
        );
        assertSame(
                selector,
                selector.setName(NAME)
        );
    }

    @Test
    public void testSetNameWithDifferent() {
        final SpreadsheetParserSelector selector = SpreadsheetParserSelector.with(
                NAME,
                TEXT
        );
        final SpreadsheetParserName differentName = SpreadsheetParserName.with("different");
        final SpreadsheetParserSelector different = selector.setName(differentName);

        assertNotSame(
                different,
                selector
        );
        this.checkEquals(
                differentName,
                different.name(),
                "name"
        );
        this.textAndCheck(
                selector,
                TEXT
        );
    }

    // parse............................................................................................................

    @Test
    public void testParseInvalidSpreadsheetParserNameFails() {
        this.parseStringFails(
                "A!34",
                new InvalidCharacterException("A!34", 1)
        );
    }

    @Test
    public void testParseSpreadsheetParserName() {
        final String text = "text-format";
        this.parseStringAndCheck(
                text,
                SpreadsheetParserSelector.with(
                        SpreadsheetParserName.with(text),
                        ""
                )
        );
    }

    @Test
    public void testParseSpreadsheetParserNameSpace() {
        final String text = "text-format";
        this.parseStringAndCheck(
                text + " ",
                SpreadsheetParserSelector.with(
                        SpreadsheetParserName.with(text),
                        ""
                )
        );
    }

    @Test
    public void testParseSpreadsheetParserNameSpacePatternText() {
        final String name = "text-format";
        final String patternText = "@@";

        this.parseStringAndCheck(
                name + " " + patternText,
                SpreadsheetParserSelector.with(
                        SpreadsheetParserName.with(name),
                        patternText
                )
        );
    }

    // SpreadsheetParserSelector.parse must be able to parse all SpreadsheetParserSelector.toString.

    @Test
    public void testParseToString() {
        final SpreadsheetParserSelector selector = SpreadsheetPattern.parseDateParsePattern("dd/mm/yyyy")
                .spreadsheetParserSelector();

        this.parseStringAndCheck(
                selector.toString(),
                selector
        );
    }

    @Test
    public void testParseToStringWithQuotes() {
        final SpreadsheetParserSelector selector = SpreadsheetPattern.parseNumberParsePattern("\"Hello\" $0.00")
                .spreadsheetParserSelector();

        this.parseStringAndCheck(
                selector.toString(),
                selector
        );
    }

    @Override
    public SpreadsheetParserSelector parseString(final String text) {
        return SpreadsheetParserSelector.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> type) {
        return type;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }

    // spreadsheetParsePattern.........................................................................................

    @Test
    public void testSpreadsheetParsePatternWithDateParsePattern() {
        this.spreadsheetParsePatternAndCheck(
                "date-parse-pattern dd/mm/yyyy",
                SpreadsheetPattern.parseDateParsePattern("dd/mm/yyyy")
        );
    }

    @Test
    public void testSpreadsheetParsePatternWithDateFormatWithoutPattern() {
        this.spreadsheetParsePatternAndCheck(
                "date-format"
        );
    }

    private void spreadsheetParsePatternAndCheck(final String text) {
        this.spreadsheetParsePatternAndCheck(
                text,
                Optional.empty()
        );
    }

    private void spreadsheetParsePatternAndCheck(final String text,
                                                 final SpreadsheetParsePattern expected) {
        this.spreadsheetParsePatternAndCheck(
                text,
                Optional.of(expected)
        );
    }

    private void spreadsheetParsePatternAndCheck(final String text,
                                                 final Optional<SpreadsheetParsePattern> expected) {
        this.checkEquals(
                expected,
                SpreadsheetParserSelector.parse(text)
                        .spreadsheetParsePattern(),
                () -> "spreadsheetParsePattern " + CharSequences.quote(text)
        );
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentName() {
        this.checkNotEquals(
                SpreadsheetParserSelector.with(
                        SpreadsheetParserName.with("different"),
                        TEXT
                )
        );
    }

    @Test
    public void testEqualsDifferentText() {
        this.checkNotEquals(
                SpreadsheetParserSelector.with(
                        NAME,
                        "different"
                )
        );
    }

    @Override
    public SpreadsheetParserSelector createObject() {
        return SpreadsheetParserSelector.with(
                NAME,
                TEXT
        );
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetParserSelector.with(
                        NAME,
                        TEXT
                ),
                "number-parse-pattern $0.00"
        );
    }

    @Test
    public void testToStringWithQuotes() {
        this.toStringAndCheck(
                SpreadsheetParserSelector.with(
                        NAME,
                        "\"Hello\""
                ),
                "number-parse-pattern \"Hello\""
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetParserSelector> type() {
        return SpreadsheetParserSelector.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // Json.............................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
                this.createJsonNodeMarshallingValue(),
                "\"number-parse-pattern $0.00\""
        );
    }

    @Test
    public void testUnmarshall() {
        this.unmarshallAndCheck(
                "\"number-parse-pattern $0.00\"",
                this.createJsonNodeMarshallingValue()
        );
    }

    @Override
    public SpreadsheetParserSelector unmarshall(final JsonNode json,
                                                final JsonNodeUnmarshallContext context) {
        return SpreadsheetParserSelector.unmarshall(
                json,
                context
        );
    }

    @Override
    public SpreadsheetParserSelector createJsonNodeMarshallingValue() {
        return this.createObject();
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintWithoutText() {
        this.treePrintAndCheck(
                SpreadsheetParserSelector.parse("abc123"),
                "abc123\n"
        );
    }

    @Test
    public void testTreePrintWithText() {
        this.treePrintAndCheck(
                SpreadsheetParserSelector.parse("number-parse-pattern $0.00"),
                "number-parse-pattern\n" +
                        "  $0.00\n"
        );
    }
}
