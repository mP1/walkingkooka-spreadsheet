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
import walkingkooka.tree.text.TextNode;

import java.util.Optional;

public final class SpreadsheetFormatterTest implements SpreadsheetFormatterTesting {

    @Test
    public void testFormatOrEmptyText() {
        final String text = "Abc123";
        final Color red = Color.parse("#123");

        this.checkEquals(
            SpreadsheetText.with(
                text + text + text
            ).setColor(
                Optional.of(red)
            ).toTextNode(),
            new FakeSpreadsheetFormatter() {
                @Override
                public Optional<TextNode> format(final Optional<Object> value,
                                                 final SpreadsheetFormatterContext context) {
                    return Optional.of(
                        SpreadsheetText.EMPTY
                            .setText(text + text + text)
                            .setColor(
                                Optional.of(red)
                            ).toTextNode()
                    );
                }
            }.formatOrEmptyText(
                Optional.of(text),
                SpreadsheetFormatterContexts.fake()
            )
        );
    }
}
