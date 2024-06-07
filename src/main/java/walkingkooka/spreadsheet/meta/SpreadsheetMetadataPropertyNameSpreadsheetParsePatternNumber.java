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

import walkingkooka.spreadsheet.format.pattern.SpreadsheetNumberParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Optional;

/**
 * A property that holds the default {@link SpreadsheetNumberParsePattern}.
 */
final class SpreadsheetMetadataPropertyNameSpreadsheetParsePatternNumber extends SpreadsheetMetadataPropertyNameSpreadsheetParsePattern<SpreadsheetNumberParsePattern> {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNameSpreadsheetParsePatternNumber instance() {
        return new SpreadsheetMetadataPropertyNameSpreadsheetParsePatternNumber();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameSpreadsheetParsePatternNumber() {
        super();
    }

    @Override
    SpreadsheetNumberParsePattern checkValue0(final Object value) {
        return this.checkValueType(value,
                v -> v instanceof SpreadsheetNumberParsePattern);
    }

    @Override
    String expected() {
        return "Number parse pattern";
    }

    @Override
    void accept(final SpreadsheetNumberParsePattern value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitNumberParsePattern(value);
    }

    @Override
    Optional<SpreadsheetNumberParsePattern> extractLocaleValue(final Locale locale) {
        final SpreadsheetNumberParsePattern number = SpreadsheetPattern.decimalFormat(
                (DecimalFormat) DecimalFormat.getInstance(locale)
        );
        final SpreadsheetNumberParsePattern integer = SpreadsheetPattern.decimalFormat(
                (DecimalFormat) DecimalFormat.getIntegerInstance(locale)
        );

        return Optional.of(
                number.equals(integer) ?
                        number :
                        SpreadsheetPattern.parseNumberParsePattern(
                                number.text() +
                                        SpreadsheetPattern.SEPARATOR.string() +
                                        integer.text()
                        )
        );
    }

    @Override
    Class<SpreadsheetNumberParsePattern> type() {
        return SpreadsheetNumberParsePattern.class;
    }

    @Override
    public SpreadsheetNumberParsePattern parseValue0(final String value) {
        return SpreadsheetPattern.parseNumberParsePattern(value);
    }
}
