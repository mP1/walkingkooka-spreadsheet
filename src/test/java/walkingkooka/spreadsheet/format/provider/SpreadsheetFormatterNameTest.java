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
import walkingkooka.collect.set.SortedSets;
import walkingkooka.plugin.PluginNameTesting;
import walkingkooka.reflect.ConstantsTesting;
import walkingkooka.reflect.FieldAttributes;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.lang.reflect.Field;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;

final public class SpreadsheetFormatterNameTest implements PluginNameTesting<SpreadsheetFormatterName>,
    ConstantsTesting<SpreadsheetFormatterName> {

    // constants........................................................................................................

    @Test
    public void testWithBadgeError() {
        this.verifyConstant("badge-error");
    }

    @Test
    public void testWithCollection() {
        this.verifyConstant("collection");
    }

    @Test
    public void testWithCurrency() {
        this.verifyConstant("currency");
    }
    
    @Test
    public void testWithDate() {
        this.verifyConstant("date");
    }

    @Test
    public void testWithDateTime() {
        this.verifyConstant("date-time");
    }

    @Test
    public void testWithFullDate() {
        this.verifyConstant("full-date");
    }

    @Test
    public void testWithFullDateTime() {
        this.verifyConstant("full-date-time");
    }

    @Test
    public void testWithFullTime() {
        this.verifyConstant("full-time");
    }

    @Test
    public void testWithGeneral() {
        this.verifyConstant("general");
    }

    @Test
    public void testWithLongDate() {
        this.verifyConstant("long-date");
    }

    @Test
    public void testWithLongDateTime() {
        this.verifyConstant("long-date-time");
    }

    @Test
    public void testWithLongTime() {
        this.verifyConstant("long-time");
    }

    @Test
    public void testWithMediumDate() {
        this.verifyConstant("medium-date");
    }

    @Test
    public void testWithMediumDateTime() {
        this.verifyConstant("medium-date-time");
    }

    @Test
    public void testWithMediumTime() {
        this.verifyConstant("medium-time");
    }
    
    @Test
    public void testWithNumber() {
        this.verifyConstant("number");
    }

    @Test
    public void testWithPercent() {
        this.verifyConstant("percent");
    }

    @Test
    public void testWithScientific() {
        this.verifyConstant("scientific");
    }

    @Test
    public void testWithShortDate() {
        this.verifyConstant("short-date");
    }

    @Test
    public void testWithShortDateTime() {
        this.verifyConstant("short-date-time");
    }

    @Test
    public void testWithShortTime() {
        this.verifyConstant("short-time");
    }

    @Test
    public void testWithText() {
        this.verifyConstant("text");
    }

    @Test
    public void testWithTime() {
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

    // calling SpreadsheetFormatterName.with constant#value should return constant
    @Test
    public void testWithUsingConstants() throws Exception {
        final Set<SpreadsheetFormatterName> names = SortedSets.tree();

        int i = 0;

        for (final Field field : SpreadsheetFormatterName.class.getFields()) {
            if (false == FieldAttributes.STATIC.is(field)) {
                continue;
            }
            if (SpreadsheetFormatterName.class != field.getType()) {
                continue;
            }
            if (false == JavaVisibility.PUBLIC.equals(JavaVisibility.of(field))) {
                continue;
            }

            final SpreadsheetFormatterName constant = (SpreadsheetFormatterName) field.get(null);
            final SpreadsheetFormatterName got = SpreadsheetFormatterName.with(constant.value());
            if (constant != got) {
                names.add(got);
            }

            i++;
        }

        this.checkNotEquals(
            0,
            i
        );

        this.checkEquals(
            Sets.empty(),
            names
        );
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
    public void testIsSpreadsheetFormatPatternWithDate() {
        this.isSpreadsheetFormatPatternAndCheck(
            "date",
            true
        );
    }

    @Test
    public void testIsSpreadsheetFormatPatternWithDateTime() {
        this.isSpreadsheetFormatPatternAndCheck(
            "date-time",
            true
        );
    }

    @Test
    public void testIsSpreadsheetFormatPatternWithNumber() {
        this.isSpreadsheetFormatPatternAndCheck(
            "number",
            true
        );
    }

    @Test
    public void testIsSpreadsheetFormatPatternWithText() {
        this.isSpreadsheetFormatPatternAndCheck(
            "text",
            true
        );
    }

    @Test
    public void testIsSpreadsheetFormatPatternWithTime() {
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
