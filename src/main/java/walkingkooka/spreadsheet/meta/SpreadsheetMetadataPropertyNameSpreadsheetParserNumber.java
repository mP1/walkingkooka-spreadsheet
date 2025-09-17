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
import walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternKind;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelector;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Optional;

/**
 * This {@link SpreadsheetMetadataPropertyName} holds the default parser for {@link walkingkooka.tree.expression.ExpressionNumber} values.
 */
final class SpreadsheetMetadataPropertyNameSpreadsheetParserNumber extends SpreadsheetMetadataPropertyNameSpreadsheetParser {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNameSpreadsheetParserNumber instance() {
        return new SpreadsheetMetadataPropertyNameSpreadsheetParserNumber();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameSpreadsheetParserNumber() {
        super(
            "numberParser",
            SpreadsheetPatternKind.NUMBER_PARSE_PATTERN
        );
    }

    @Override
    void accept(final SpreadsheetParserSelector value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitNumberParser(value);
    }

    @Override
    Optional<SpreadsheetParsePattern> extractLocaleAwareValueSpreadsheetParsePattern(final Locale locale) {
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
}
