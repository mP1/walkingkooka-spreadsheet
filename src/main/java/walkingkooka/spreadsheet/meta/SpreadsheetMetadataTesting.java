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

import javaemul.internal.annotations.GwtIncompatible;
import walkingkooka.convert.Converters;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePattern;
import walkingkooka.test.Testing;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.text.Length;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Provides factory methods for creating a {@link SpreadsheetMetadata} for testing.
 */
@GwtIncompatible
public interface SpreadsheetMetadataTesting extends Testing {

    /**
     * Creates a {@link SpreadsheetMetadata} with Locale=EN-AU and standard patterns and other sensible defaults.
     */
    SpreadsheetMetadata METADATA_EN_AU = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.forLanguageTag("EN-AU"))
            .loadFromLocale()
            .set(SpreadsheetMetadataPropertyName.CREATOR, EmailAddress.parse("user@example.com"))
            .set(SpreadsheetMetadataPropertyName.CREATE_DATE_TIME, LocalDateTime.now())
            .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.EXCEL_1900_DATE_SYSTEM_OFFSET)
            .set(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, 2000)
            .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, ExpressionNumberKind.BIG_DECIMAL)
            .set(SpreadsheetMetadataPropertyName.MODIFIED_BY, EmailAddress.parse("user@example.com"))
            .set(SpreadsheetMetadataPropertyName.MODIFIED_DATE_TIME, LocalDateTime.now())
            .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.HALF_UP)
            .set(SpreadsheetMetadataPropertyName.PRECISION, 7)
            .set(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR, 50)
            .set(SpreadsheetMetadataPropertyName.DATE_FORMATTER, SpreadsheetParsePattern.parseDateFormatPattern("yyyy/mm/dd").spreadsheetFormatterSelector())
            .set(SpreadsheetMetadataPropertyName.DATE_PARSE_PATTERN, SpreadsheetParsePattern.parseDateParsePattern("yyyy/mm/dd"))
            .set(SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER, SpreadsheetParsePattern.parseDateTimeFormatPattern("yyyy/mm/dd hh:mm").spreadsheetFormatterSelector())
            .set(SpreadsheetMetadataPropertyName.DATETIME_PARSE_PATTERN, SpreadsheetParsePattern.parseDateTimeParsePattern("yyyy/mm/dd hh:mm"))
            .set(SpreadsheetMetadataPropertyName.NUMBER_FORMATTER, SpreadsheetParsePattern.parseNumberFormatPattern("0.#").spreadsheetFormatterSelector())
            .set(SpreadsheetMetadataPropertyName.GENERAL_NUMBER_FORMAT_DIGIT_COUNT, 8)
            .set(SpreadsheetMetadataPropertyName.NUMBER_PARSE_PATTERN, SpreadsheetParsePattern.parseNumberParsePattern(").#"))
            .set(SpreadsheetMetadataPropertyName.TEXT_FORMATTER, SpreadsheetParsePattern.parseTextFormatPattern("@").spreadsheetFormatterSelector())
            .set(SpreadsheetMetadataPropertyName.TIME_FORMATTER, SpreadsheetParsePattern.parseTimeFormatPattern("hh:mm:ss").spreadsheetFormatterSelector())
            .set(SpreadsheetMetadataPropertyName.TIME_PARSE_PATTERN, SpreadsheetParsePattern.parseTimeParsePattern("hh:mm:ss"))
            .set(SpreadsheetMetadataPropertyName.STYLE, TextStyle.EMPTY
                    .set(TextStylePropertyName.WIDTH, Length.parsePixels("100px"))
                    .set(TextStylePropertyName.HEIGHT, Length.parsePixels("50px"))
            ).set(SpreadsheetMetadataPropertyName.CELL_CHARACTER_WIDTH, 1);

    static SpreadsheetFormula parseFormula(final String text) {
        return SpreadsheetFormula.parse(
                TextCursors.charSequence(text),
                METADATA_EN_AU
                        .parser(),
                METADATA_EN_AU
                        .parserContext(LocalDateTime::now)
        );
    }
}
