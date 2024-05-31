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
import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.ConstantsTesting;
import walkingkooka.spreadsheet.component.SpreadsheetComponentNameTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;

final public class SpreadsheetFormatterNameTest implements SpreadsheetComponentNameTesting<SpreadsheetFormatterName>,
        ConstantsTesting<SpreadsheetFormatterName> {

    // constants........................................................................................................

    @Test
    public void testWithDateFormat() {
        this.verifyConstant("date-format");
    }

    @Test
    public void testWithDateTimeFormat() {
        this.verifyConstant("date-time-format");
    }

    @Test
    public void testWithNumberFormat() {
        this.verifyConstant("number-format");
    }

    @Test
    public void testWithTextFormat() {
        this.verifyConstant("text-format");
    }

    @Test
    public void testWithTimeFormat() {
        this.verifyConstant("time-format");
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
}
