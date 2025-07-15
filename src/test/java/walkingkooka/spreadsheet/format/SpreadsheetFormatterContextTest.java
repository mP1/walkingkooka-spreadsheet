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
import walkingkooka.color.Color;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.tree.text.TextNode;

import java.util.Optional;

public final class SpreadsheetFormatterContextTest implements ClassTesting<SpreadsheetFormatterContext> {

    @Test
    public void testFormatValueOrEmptyText() {
        final String value = "Abc123";
        final TextNode expected = SpreadsheetText.EMPTY.setText(value + value + value)
            .setColor(
                Optional.of(
                    Color.parse("#234")
                )
            ).toTextNode();

        this.checkEquals(
            expected,
            new FakeSpreadsheetFormatterContext() {
                @Override
                public Optional<TextNode> formatValue(final Optional<Object> v) {
                    checkEquals(
                        Optional.of(value),
                        v
                    );
                    return Optional.of(expected);
                }
            }.formatValueOrEmptyText(
                Optional.of(value)
            )
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetFormatterContext> type() {
        return SpreadsheetFormatterContext.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
