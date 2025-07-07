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

import java.util.Optional;

/**
 * A {@link SpreadsheetFormatter} for a {@link walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern} that produces
 * {@link SpreadsheetText} which is more limiting than a {@link TextNode}.
 */
public interface SpreadsheetPatternSpreadsheetFormatter extends SpreadsheetFormatter {

    @Override
    default Optional<TextNode> format(final Optional<Object> value,
                                      final SpreadsheetFormatterContext context) {
        return this.formatSpreadsheetText(
            value,
            context
        ).map(SpreadsheetText::toTextNode);
    }

    /**
     * Implementors should implement this method and only produce a {@link SpreadsheetText} rather than the richer
     * {@link TextNode}.
     */
    Optional<SpreadsheetText> formatSpreadsheetText(final Optional<Object> value,
                                                    final SpreadsheetFormatterContext context);
}

