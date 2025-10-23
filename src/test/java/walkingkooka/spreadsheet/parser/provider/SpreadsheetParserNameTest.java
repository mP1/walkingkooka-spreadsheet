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

package walkingkooka.spreadsheet.parser.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.plugin.PluginNameTesting;
import walkingkooka.reflect.ConstantsTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;

final public class SpreadsheetParserNameTest implements PluginNameTesting<SpreadsheetParserName>,
    ConstantsTesting<SpreadsheetParserName> {

    // constants........................................................................................................

    @Test
    public void testWithDateParsePattern() {
        this.verifyConstant("date");
    }

    @Test
    public void testWithDateTimeParsePattern() {
        this.verifyConstant("date-time");
    }

    @Test
    public void testWithNumberParsePattern() {
        this.verifyConstant("number");
    }

    @Test
    public void testWithTimeParsePattern() {
        this.verifyConstant("time");
    }

    private void verifyConstant(final String text) {
        assertSame(
            SpreadsheetParserName.with(text),
            SpreadsheetParserName.with(text)
        );

        final SpreadsheetParserName instance = SpreadsheetParserName.with(text);
        assertSame(
            instance,
            this.unmarshall(
                this.marshallContext()
                    .marshall(instance)
            )
        );
    }

    @Override
    public Set<SpreadsheetParserName> intentionalDuplicateConstants() {
        return Sets.empty();
    }

    // isSpreadsheetParsePattern.......................................................................................

    @Test
    public void testIsSpreadsheetParsePatternWithNonSpreadsheetParsePatternName() {
        this.isSpreadsheetParsePatternAndCheck(
            "abc-123",
            false
        );
    }

    @Test
    public void testIsSpreadsheetParsePatternWithDateParsePattern() {
        this.isSpreadsheetParsePatternAndCheck(
            "date",
            true
        );
    }

    @Test
    public void testIsSpreadsheetParsePatternWithDateTimeParsePattern() {
        this.isSpreadsheetParsePatternAndCheck(
            "date-time",
            true
        );
    }

    @Test
    public void testIsSpreadsheetParsePatternWithNumberParsePattern() {
        this.isSpreadsheetParsePatternAndCheck(
            "number",
            true
        );
    }

    @Test
    public void testIsSpreadsheetParsePatternWithTimeParsePattern() {
        this.isSpreadsheetParsePatternAndCheck(
            "time",
            true
        );
    }

    private void isSpreadsheetParsePatternAndCheck(final String name,
                                                   final boolean expected) {
        this.checkEquals(
            expected,
            SpreadsheetParserName.with(name)
                .isSpreadsheetParsePattern()
        );
    }

    // name.............................................................................................................

    @Override
    public SpreadsheetParserName createName(final String name) {
        return SpreadsheetParserName.with(name);
    }

    @Override
    public Class<SpreadsheetParserName> type() {
        return SpreadsheetParserName.class;
    }

    @Override
    public SpreadsheetParserName unmarshall(final JsonNode from,
                                            final JsonNodeUnmarshallContext context) {
        return SpreadsheetParserName.unmarshall(from, context);
    }

    // setText..........................................................................................................

    @Test
    public void testSetText() {
        final SpreadsheetParserName name = SpreadsheetParserName.TIME_PARSER_PATTERN;
        final String text = "yyyy/mm/dd";

        this.checkEquals(
            SpreadsheetParserSelector.with(name, text),
            name.setValueText(text)
        );
    }
}
