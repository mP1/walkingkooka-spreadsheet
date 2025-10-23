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

package walkingkooka.spreadsheet.format.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.plugin.PluginNameTesting;
import walkingkooka.reflect.ConstantsTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;

final public class SpreadsheetFormatterNameTest implements PluginNameTesting<SpreadsheetFormatterName>,
    ConstantsTesting<SpreadsheetFormatterName> {

    // constants........................................................................................................

    @Test
    public void testWithDateFormatPattern() {
        this.verifyConstant("date");
    }

    @Test
    public void testWithDateTimeFormatPattern() {
        this.verifyConstant("date-time");
    }

    @Test
    public void testWithNumberFormatPattern() {
        this.verifyConstant("number");
    }

    @Test
    public void testWithTextFormatPattern() {
        this.verifyConstant("text");
    }

    @Test
    public void testWithTimeFormatPattern() {
        this.verifyConstant("time");
    }

    private void verifyConstant(final String text) {
        assertSame(
            SpreadsheetFormatterName.with(text),
            SpreadsheetFormatterName.with(text)
        );

        final SpreadsheetFormatterName instance = SpreadsheetFormatterName.with(text);
        assertSame(
            instance,
            this.unmarshall(
                this.marshallContext()
                    .marshall(instance)
            )
        );
    }

    @Override
    public Set<SpreadsheetFormatterName> intentionalDuplicateConstants() {
        return Sets.empty();
    }

    // isSpreadsheetFormatPattern.......................................................................................

    @Test
    public void testIsSpreadsheetFormatPatternWithNonSpreadsheetFormatPatternName() {
        this.isSpreadsheetFormatPatternAndCheck(
            "abc-123",
            false
        );
    }

    @Test
    public void testIsSpreadsheetFormatPatternWithDateFormatPattern() {
        this.isSpreadsheetFormatPatternAndCheck(
            "date",
            true
        );
    }

    @Test
    public void testIsSpreadsheetFormatPatternWithDateTimeFormatPattern() {
        this.isSpreadsheetFormatPatternAndCheck(
            "date-time",
            true
        );
    }

    @Test
    public void testIsSpreadsheetFormatPatternWithNumberFormatPattern() {
        this.isSpreadsheetFormatPatternAndCheck(
            "number",
            true
        );
    }

    @Test
    public void testIsSpreadsheetFormatPatternWithDateTextFormatPattern() {
        this.isSpreadsheetFormatPatternAndCheck(
            "text",
            true
        );
    }

    @Test
    public void testIsSpreadsheetFormatPatternWithTimeFormatPattern() {
        this.isSpreadsheetFormatPatternAndCheck(
            "time",
            true
        );
    }

    private void isSpreadsheetFormatPatternAndCheck(final String name,
                                                    final boolean expected) {
        this.checkEquals(
            expected,
            SpreadsheetFormatterName.with(name)
                .isSpreadsheetFormatPattern()
        );
    }

    // name.............................................................................................................

    @Override
    public SpreadsheetFormatterName createName(final String name) {
        return SpreadsheetFormatterName.with(name);
    }

    @Override
    public Class<SpreadsheetFormatterName> type() {
        return SpreadsheetFormatterName.class;
    }

    @Override
    public SpreadsheetFormatterName unmarshall(final JsonNode from,
                                               final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormatterName.unmarshall(from, context);
    }

    // setText..........................................................................................................

    @Test
    public void testSetText() {
        final SpreadsheetFormatterName name = SpreadsheetFormatterName.TEXT;
        final String text = "@@";

        this.checkEquals(
            SpreadsheetFormatterSelector.with(name, text),
            name.setValueText(text)
        );
    }
}
