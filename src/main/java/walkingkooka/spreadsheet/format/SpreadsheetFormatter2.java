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

import walkingkooka.tree.text.TextNode;

import java.util.Objects;
import java.util.Optional;

/**
 * Base class for all {@link SpreadsheetFormatter} implementations in this package.
 */
abstract class SpreadsheetFormatter2 implements SpreadsheetFormatter {

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetFormatter2() {
        super();
    }

    /**
     * Accepts a value and uses the {@link SpreadsheetPatternSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor} to produce the formatted text.
     */
    @Override
    public final Optional<TextNode> format(final Object value, final SpreadsheetFormatterContext context) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(context, "context");

        return this.format0(
                value,
                context
        );
    }

    abstract Optional<TextNode> format0(final Object value, final SpreadsheetFormatterContext context);

    @Override
    public abstract String toString();
}
