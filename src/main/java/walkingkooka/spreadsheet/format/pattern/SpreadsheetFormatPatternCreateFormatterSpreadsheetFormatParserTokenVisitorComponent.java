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

package walkingkooka.spreadsheet.format.pattern;

import walkingkooka.spreadsheet.format.parser.ConditionSpreadsheetFormatParserToken;
import walkingkooka.text.CharSequences;

import java.util.function.Consumer;

final class SpreadsheetFormatPatternCreateFormatterSpreadsheetFormatParserTokenVisitorComponent {

    static SpreadsheetFormatPatternCreateFormatterSpreadsheetFormatParserTokenVisitorComponent create() {
        return new SpreadsheetFormatPatternCreateFormatterSpreadsheetFormatParserTokenVisitorComponent();
    }

    SpreadsheetFormatPatternCreateFormatterSpreadsheetFormatParserTokenVisitorComponent() {
        super();
    }

    ConditionSpreadsheetFormatParserToken condition;

    SpreadsheetPatternSpreadsheetFormatter formatter;

    void prepare(final int index,
                 final int total,
                 final SpreadsheetFormatPattern formatPattern,
                 final Consumer<SpreadsheetPatternSpreadsheetFormatter> formatters) {
        SpreadsheetPatternSpreadsheetFormatter formatter = this.formatter;
        if (null == formatter) {
            throw new IllegalArgumentException(
                "Missing formatter for pattern " +
                    CharSequences.quoteAndEscape(
                        formatPattern.value.text()
                    )
            );
        }

        final ConditionSpreadsheetFormatParserToken condition = this.condition;
        if (null != condition) {
            formatters.accept(
                SpreadsheetPatternSpreadsheetFormatters.conditional(
                    condition,
                    formatter
                )
            );
        } else {
            formatPattern.missingCondition(
                index,
                total,
                formatter,
                formatters
            );
        }
    }
}
