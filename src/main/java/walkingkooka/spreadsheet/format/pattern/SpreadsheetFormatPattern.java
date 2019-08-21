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

import walkingkooka.spreadsheet.format.HasSpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;

/**
 * Holds a single {@link SpreadsheetFormatDateTimeParserToken date/time} or {@link SpreadsheetFormatNumberParserToken} number tokens and some common functionality.
 */
public abstract class SpreadsheetFormatPattern<T extends SpreadsheetFormatParserToken> extends SpreadsheetPattern<T>
        implements HasSpreadsheetFormatter {

    // ctor.............................................................................................................

    /**
     * Package private ctor use factory
     */
    SpreadsheetFormatPattern(final T token) {
        super(token);
    }

    // HasSpreadsheetFormatter..........................................................................................

    /**
     * Returns a {@link SpreadsheetFormatter} built from this pattern.
     */
    public final SpreadsheetFormatter formatter() {
        if (null == this.formatter) {
            this.formatter = this.createFormatter();
        }
        return this.formatter;
    }

    private SpreadsheetFormatter formatter;

    /**
     * Factory that lazily creates a {@link SpreadsheetFormatter}
     */
    abstract SpreadsheetFormatter createFormatter();

    // Object...........................................................................................................

    @Override
    public final String toString() {
        return this.value.toString();
    }
}
