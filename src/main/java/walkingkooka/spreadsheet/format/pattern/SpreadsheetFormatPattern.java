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

import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetPatternSpreadsheetFormatter;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterName;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.text.CaseKind;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.function.Consumer;

/**
 * Holds a {@link ParserToken} tokens and some common functionality.
 */
public abstract class SpreadsheetFormatPattern extends SpreadsheetPattern {

    // ctor.............................................................................................................

    /**
     * Package private ctor use factory
     */
    SpreadsheetFormatPattern(final ParserToken token) {
        super(token);
    }

    // spreadsheetFormatterSelector.....................................................................................

    /**
     * Returns the {@link SpreadsheetFormatterSelector} equivalent to this pattern.
     * <pre>
     * date
     * date-time
     * </pre>
     */
    public final SpreadsheetFormatterSelector spreadsheetFormatterSelector() {
        final String formatterName = CaseKind.CAMEL.change(
            this.getClass().getSimpleName()
                .substring("Spreadsheet".length()),
            CaseKind.KEBAB
        ).replace(
            "date-format-pattern",
            "date"
        ).replace(
            "date-time-format-pattern",
            "date-time"
        );

        return SpreadsheetFormatterSelector.with(
            SpreadsheetFormatterName.with(formatterName),
            this.text()
        );
    }

    // toFormat.........................................................................................................

    /**
     * subclasses of {@link SpreadsheetFormatPattern} always return this.
     */
    @Override
    public final SpreadsheetFormatPattern toFormat() {
        return this;
    }

    // HasFormatter.....................................................................................................

    /**
     * Factory that lazily creates a {@link SpreadsheetFormatter}
     */
    @Override final SpreadsheetPatternSpreadsheetFormatter createFormatter() {
        return SpreadsheetFormatPatternCreateFormatterSpreadsheetFormatParserTokenVisitor.createFormatter(
            this
        );
    }

    /**
     * All subclasses will return the given {@link SpreadsheetFormatter} except for number which will add a condition
     * depending on the index and total.
     */
    abstract void missingCondition(final int index,
                                   final int total,
                                   final SpreadsheetPatternSpreadsheetFormatter formatter,
                                   final Consumer<SpreadsheetPatternSpreadsheetFormatter> formatters);
}