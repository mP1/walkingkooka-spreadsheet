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

package walkingkooka.spreadsheet.meta;

import walkingkooka.spreadsheet.format.SpreadsheetTextFormatterPattern;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatGeneralParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.text.CharSequences;
import walkingkooka.visit.Visiting;

/**
 * A {@link SpreadsheetFormatParserTokenVisitor} that visits a pattern and fails if any invalid token is encountered.
 */
final class SpreadsheetMetadataPropertyValueHandlerSpreadsheetTextFormatterPatternFormatSpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatParserTokenVisitor {

    static void check(final SpreadsheetMetadataPropertyName<?> name,
                      final SpreadsheetTextFormatterPattern pattern) {
        new SpreadsheetMetadataPropertyValueHandlerSpreadsheetTextFormatterPatternFormatSpreadsheetFormatParserTokenVisitor(name, pattern)
                .accept(pattern.value());
    }

    SpreadsheetMetadataPropertyValueHandlerSpreadsheetTextFormatterPatternFormatSpreadsheetFormatParserTokenVisitor(final SpreadsheetMetadataPropertyName<?> name,
                                                                                                                    final SpreadsheetTextFormatterPattern pattern) {
        super();
        this.name = name;
        this.pattern = pattern;
    }

    /**
     * GENERAL is never allowed in default patterns.
     */
    @Override
    protected final Visiting startVisit(final SpreadsheetFormatGeneralParserToken token) {
        return fail(token);
    }

    final <T> T fail(final SpreadsheetFormatParserToken token) {
        throw new SpreadsheetMetadataPropertyValueException("Invalid pattern " + CharSequences.quoteAndEscape(token.text()),
                this.name,
                this.pattern);
    }

    private final SpreadsheetMetadataPropertyName<?> name;
    private final SpreadsheetTextFormatterPattern pattern;

    @Override
    public final String toString() {
        return this.name + "=" + this.pattern;
    }
}
