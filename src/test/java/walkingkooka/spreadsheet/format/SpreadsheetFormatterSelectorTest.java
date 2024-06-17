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
import walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern;
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

public final class SpreadsheetFormatterSelectorTest implements ClassTesting2<SpreadsheetFormatterSelector>,
        HashCodeEqualsDefinedTesting2<SpreadsheetFormatterSelector>,
        HasTextTesting,
        ToStringTesting<SpreadsheetFormatterSelector>,
        ParseStringTesting<SpreadsheetFormatterSelector>,
        JsonNodeMarshallingTesting<SpreadsheetFormatterSelector>,
        TreePrintableTesting {

    private final static SpreadsheetFormatterName NAME = SpreadsheetFormatterName.with("text-format-pattern");

    private final static String TEXT = "@@";

    @Test
    public void testWithNullNameFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetFormatterSelector.with(
                        null,
                        TEXT
                )
        );
    }

    @Test
    public void testWithNullTextFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetFormatterSelector.with(
                        NAME,
                        null
                )
        );
    }

    @Test
    public void testWith() {
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterSelector.with(
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
                () -> SpreadsheetFormatterSelector.with(
                        NAME,
                        TEXT
                ).setName(null)
        );
    }

    @Test
    public void testSetNameWithSame() {
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterSelector.with(
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
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterSelector.with(
                NAME,
                TEXT
        );
        final SpreadsheetFormatterName differentName = SpreadsheetFormatterName.with("different");
        final SpreadsheetFormatterSelector different = selector.setName(differentName);

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
    public void testParseInvalidSpreadsheetFormatterNameFails() {
        this.parseStringFails(
                "A!34",
                new InvalidCharacterException("A!34", 1)
        );
    }

    @Test
    public void testParseSpreadsheetFormatterName() {
        final String text = "text-format";
        this.parseStringAndCheck(
                text,
                SpreadsheetFormatterSelector.with(
                        SpreadsheetFormatterName.with(text),
                        ""
                )
        );
    }

    @Test
    public void testParseSpreadsheetFormatterNameSpace() {
        final String text = "text-format";
        this.parseStringAndCheck(
                text + " ",
                SpreadsheetFormatterSelector.with(
                        SpreadsheetFormatterName.with(text),
                        ""
                )
        );
    }

    @Test
    public void testParseSpreadsheetFormatterNameSpacePatternText() {
        final String name = "text-format";
        final String patternText = "@@";

        this.parseStringAndCheck(
                name + " " + patternText,
                SpreadsheetFormatterSelector.with(
                        SpreadsheetFormatterName.with(name),
                        patternText
                )
        );
    }

    // SpreadsheetFormatterSelector.parse must be able to parse all SpreadsheetFormatterSelector.toString.

    @Test
    public void testParseToString() {
        final SpreadsheetFormatterSelector selector = SpreadsheetPattern.parseDateFormatPattern("dd/mm/yyyy")
                .spreadsheetFormatterSelector();

        this.parseStringAndCheck(
                selector.toString(),
                selector
        );
    }

    @Test
    public void testParseToStringWithQuotes() {
        final SpreadsheetFormatterSelector selector = SpreadsheetPattern.parseTextFormatPattern("\"Hello\" @")
                .spreadsheetFormatterSelector();

        this.parseStringAndCheck(
                selector.toString(),
                selector
        );
    }

    @Override
    public SpreadsheetFormatterSelector parseString(final String text) {
        return SpreadsheetFormatterSelector.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> type) {
        return type;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }

    // spreadsheetFormatPattern.........................................................................................

    @Test
    public void testSpreadsheetFormatPatternWithDateFormatPattern() {
        this.spreadsheetFormatPatternAndCheck(
                "date-format-pattern dd/mm/yyyy",
                SpreadsheetPattern.parseDateFormatPattern("dd/mm/yyyy")
        );
    }

    @Test
    public void testSpreadsheetFormatPatternWithDateFormatWithoutPattern() {
        this.spreadsheetFormatPatternAndCheck(
                "date-format"
        );
    }

    private void spreadsheetFormatPatternAndCheck(final String text) {
        this.spreadsheetFormatPatternAndCheck(
                text,
                Optional.empty()
        );
    }

    private void spreadsheetFormatPatternAndCheck(final String text,
                                                  final SpreadsheetFormatPattern expected) {
        this.spreadsheetFormatPatternAndCheck(
                text,
                Optional.of(expected)
        );
    }

    private void spreadsheetFormatPatternAndCheck(final String text,
                                                  final Optional<SpreadsheetFormatPattern> expected) {
        this.checkEquals(
                expected,
                SpreadsheetFormatterSelector.parse(text)
                        .spreadsheetFormatPattern(),
                () -> "spreadsheetFormatPattern " + CharSequences.quote(text)
        );
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentName() {
        this.checkNotEquals(
                SpreadsheetFormatterSelector.with(
                        SpreadsheetFormatterName.with("different"),
                        TEXT
                )
        );
    }

    @Test
    public void testEqualsDifferentText() {
        this.checkNotEquals(
                SpreadsheetFormatterSelector.with(
                        NAME,
                        "different"
                )
        );
    }

    @Override
    public SpreadsheetFormatterSelector createObject() {
        return SpreadsheetFormatterSelector.with(
                NAME,
                TEXT
        );
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetFormatterSelector.with(
                        NAME,
                        TEXT
                ),
                "text-format-pattern @@"
        );
    }

    @Test
    public void testToStringWithQuotes() {
        this.toStringAndCheck(
                SpreadsheetFormatterSelector.with(
                        NAME,
                        "\"Hello\""
                ),
                "text-format-pattern \"Hello\""
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetFormatterSelector> type() {
        return SpreadsheetFormatterSelector.class;
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
                "\"text-format-pattern @@\""
        );
    }

    @Test
    public void testUnmarshall() {
        this.unmarshallAndCheck(
                "\"text-format-pattern @@\"",
                this.createJsonNodeMarshallingValue()
        );
    }

    @Override
    public SpreadsheetFormatterSelector unmarshall(final JsonNode json,
                                                   final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormatterSelector.unmarshall(
                json,
                context
        );
    }

    @Override
    public SpreadsheetFormatterSelector createJsonNodeMarshallingValue() {
        return this.createObject();
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintWithoutText() {
        this.treePrintAndCheck(
                SpreadsheetFormatterSelector.parse("abc123"),
                "abc123\n"
        );
    }

    @Test
    public void testTreePrintWithText() {
        this.treePrintAndCheck(
                SpreadsheetFormatterSelector.parse("text-format-pattern @@"),
                "text-format-pattern\n" +
                        "  \"@@\"\n"
        );
    }
}
