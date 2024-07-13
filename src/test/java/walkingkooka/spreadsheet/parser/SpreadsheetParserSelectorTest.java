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

package walkingkooka.spreadsheet.parser;

import org.junit.jupiter.api.Test;
import walkingkooka.plugin.PluginSelectorLikeTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Optional;

public final class SpreadsheetParserSelectorTest implements PluginSelectorLikeTesting<SpreadsheetParserSelector, SpreadsheetParserName> {

    @Override
    public SpreadsheetParserSelector createPluginSelectorLike(final SpreadsheetParserName name,
                                                              final String text) {
        return SpreadsheetParserSelector.with(
                name,
                text
        );
    }

    @Override
    public SpreadsheetParserName createName(final String value) {
        return SpreadsheetParserName.with(value);
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

    @Override
    public SpreadsheetParserSelector parseString(final String text) {
        return SpreadsheetParserSelector.parse(text);
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
        return SpreadsheetParserSelector.with(
                SpreadsheetParserName.NUMBER_PARSER,
                "$0.00"
        );
    }

    // type name........................................................................................................

    @Override
    public String typeNamePrefix() {
        return SpreadsheetParser.class.getSimpleName();
    }

}
