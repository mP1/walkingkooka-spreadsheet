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
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorInfoSet;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterInfoSet;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.format.SpreadsheetParserInfoSet;
import walkingkooka.spreadsheet.format.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.test.Testing;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionInfoSet;
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
            .set(SpreadsheetMetadataPropertyName.CELL_CHARACTER_WIDTH, 1)
            .set(SpreadsheetMetadataPropertyName.CREATOR, EmailAddress.parse("user@example.com"))
            .set(SpreadsheetMetadataPropertyName.CREATE_DATE_TIME, LocalDateTime.now())
            .set(SpreadsheetMetadataPropertyName.DATE_FORMATTER, SpreadsheetPattern.parseDateFormatPattern("yyyy/mm/dd").spreadsheetFormatterSelector())
            .set(SpreadsheetMetadataPropertyName.DATE_PARSER, SpreadsheetPattern.parseDateParsePattern("yyyy/mm/dd").spreadsheetParserSelector())
            .set(SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER, SpreadsheetPattern.parseDateTimeFormatPattern("yyyy/mm/dd hh:mm").spreadsheetFormatterSelector())
            .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.EXCEL_1900_DATE_SYSTEM_OFFSET)
            .set(SpreadsheetMetadataPropertyName.DATE_TIME_PARSER, SpreadsheetPattern.parseDateTimeParsePattern("yyyy/mm/dd hh:mm").spreadsheetParserSelector())
            .set(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, 2000)
            .set(SpreadsheetMetadataPropertyName.EXPRESSION_FUNCTIONS, ExpressionFunctionInfoSet.parse(""))
            .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, ExpressionNumberKind.BIG_DECIMAL)
            .set(SpreadsheetMetadataPropertyName.GENERAL_NUMBER_FORMAT_DIGIT_COUNT, 8)
            .set(SpreadsheetMetadataPropertyName.MODIFIED_BY, EmailAddress.parse("user@example.com"))
            .set(SpreadsheetMetadataPropertyName.MODIFIED_DATE_TIME, LocalDateTime.now())
            .set(SpreadsheetMetadataPropertyName.NUMBER_FORMATTER, SpreadsheetPattern.parseNumberFormatPattern("0.#").spreadsheetFormatterSelector())
            .set(SpreadsheetMetadataPropertyName.NUMBER_PARSER, SpreadsheetPattern.parseNumberParsePattern(").#").spreadsheetParserSelector())
            .set(SpreadsheetMetadataPropertyName.PRECISION, 7)
            .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.HALF_UP)
            .set(SpreadsheetMetadataPropertyName.SPREADSHEET_COMPARATORS, SpreadsheetComparatorInfoSet.with(SpreadsheetComparatorProviders.spreadsheetComparators().spreadsheetComparatorInfos()))
            .set(SpreadsheetMetadataPropertyName.SPREADSHEET_FORMATTERS, SpreadsheetFormatterInfoSet.with(SpreadsheetFormatterProviders.spreadsheetFormatPattern().spreadsheetFormatterInfos()))
            .set(SpreadsheetMetadataPropertyName.SPREADSHEET_PARSERS, SpreadsheetParserInfoSet.with(SpreadsheetParserProviders.spreadsheetParsePattern().spreadsheetParserInfos()))
            .set(SpreadsheetMetadataPropertyName.STYLE, TextStyle.EMPTY
                    .set(TextStylePropertyName.WIDTH, Length.parsePixels("100px"))
                    .set(TextStylePropertyName.HEIGHT, Length.parsePixels("50px"))
            ).set(SpreadsheetMetadataPropertyName.TEXT_FORMATTER, SpreadsheetPattern.parseTextFormatPattern("@").spreadsheetFormatterSelector())
            .set(SpreadsheetMetadataPropertyName.TIME_FORMATTER, SpreadsheetPattern.parseTimeFormatPattern("hh:mm:ss").spreadsheetFormatterSelector())
            .set(SpreadsheetMetadataPropertyName.TIME_PARSER, SpreadsheetPattern.parseTimeParsePattern("hh:mm:ss").spreadsheetParserSelector())
            .set(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR, 50);

    static SpreadsheetFormula parseFormula(final String text) {
        return SpreadsheetFormula.parse(
                TextCursors.charSequence(text),
                METADATA_EN_AU
                        .parser(SpreadsheetParserProviders.spreadsheetParsePattern()),
                METADATA_EN_AU
                        .parserContext(LocalDateTime::now)
        );
    }
}
