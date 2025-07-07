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
import walkingkooka.InvalidCharacterException;
import walkingkooka.plugin.PluginSelectorLikeTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetFormatterSelectorTest implements PluginSelectorLikeTesting<SpreadsheetFormatterSelector, SpreadsheetFormatterName> {

    @Override
    public SpreadsheetFormatterSelector createPluginSelectorLike(final SpreadsheetFormatterName name,
                                                                 final String text) {
        return SpreadsheetFormatterSelector.with(
            name,
            text
        );
    }

    @Override
    public SpreadsheetFormatterName createName(final String value) {
        return SpreadsheetFormatterName.with(value);
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

    @Override
    public SpreadsheetFormatterSelector parseString(final String text) {
        return SpreadsheetFormatterSelector.parse(text);
    }

    // spreadsheetFormatPattern.........................................................................................

    @Test
    public void testSpreadsheetFormatPatternWithTextFormatPatternInvalidPatternFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetFormatterSelector.with(
                SpreadsheetFormatterName.TEXT_FORMAT_PATTERN,
                ""
            ).spreadsheetFormatPattern()
        );
    }

    @Test
    public void testSpreadsheetFormatPatternInvalidCharacterExceptionMessage() {
        final String selector = "date-format-pattern yyyy/!";

        final InvalidCharacterException thrown = assertThrows(
            InvalidCharacterException.class,
            () -> SpreadsheetFormatterSelector.parse(selector)
                .spreadsheetFormatPattern()
        );

        this.checkEquals(
            "Invalid character '!' at 25 expected {WHITESPACE | COLOR}, GENERAL, {WHITESPACE | COLOR} | {COLOR | {\"D\"} | {\"M\"} | {\"Y\"} | ESCAPE | DATETIME_TEXT_LITERAL | QUOTED}",
            thrown.getMessage(),
            "message"
        );

        this.checkEquals(
            '!',
            selector.charAt(25)
        );
    }

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
        return SpreadsheetFormatterSelector.with(
            SpreadsheetFormatterName.TEXT_FORMAT_PATTERN,
            "@@"
        );
    }

    // type name........................................................................................................

    @Override
    public String typeNamePrefix() {
        return SpreadsheetFormatter.class.getSimpleName();
    }
}
