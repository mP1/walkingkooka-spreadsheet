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

import walkingkooka.convert.Converters;
import walkingkooka.convert.provider.ConverterInfoSet;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorInfoSet;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.convert.SpreadsheetConvertersConverterProviders;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterInfoSet;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.parser.SpreadsheetParserInfoSet;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProvider;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProviders;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionInfoSet;
import walkingkooka.tree.text.Length;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Locale;

class SpreadsheetMetadataTestingHelper {

    final static Locale LOCALE = Locale.forLanguageTag("EN-AU");

    final static SpreadsheetFormatterProvider SPREADSHEET_FORMATTER_PROVIDER = SpreadsheetFormatterProviders.spreadsheetFormatPattern(
            LOCALE,
            () -> {
                throw new UnsupportedOperationException();
            }
    );

    final static SpreadsheetParserProvider SPREADSHEET_PARSER_PROVIDER = SpreadsheetParserProviders.spreadsheetParsePattern(
            SPREADSHEET_FORMATTER_PROVIDER
    );

    static SpreadsheetMetadata metadataEnAu() {
        return SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
                .loadFromLocale()
                .set(SpreadsheetMetadataPropertyName.CELL_CHARACTER_WIDTH, 1)
                .set(
                        SpreadsheetMetadataPropertyName.CONVERTERS,
                        ConverterInfoSet.with(
                                SpreadsheetConvertersConverterProviders.spreadsheetConverters(
                                        SpreadsheetMetadata.EMPTY,
                                        SPREADSHEET_FORMATTER_PROVIDER,
                                        SPREADSHEET_PARSER_PROVIDER
                                ).converterInfos()
                        )
                ).set(SpreadsheetMetadataPropertyName.CREATOR, EmailAddress.parse("user@example.com"))
                .set(SpreadsheetMetadataPropertyName.CREATE_DATE_TIME, LocalDateTime.now())
                .set(SpreadsheetMetadataPropertyName.DATE_FORMATTER, SpreadsheetPattern.parseDateFormatPattern("yyyy/mm/dd").spreadsheetFormatterSelector())
                .set(SpreadsheetMetadataPropertyName.DATE_PARSER, SpreadsheetPattern.parseDateParsePattern("yyyy/mm/dd").spreadsheetParserSelector())
                .set(SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER, SpreadsheetPattern.parseDateTimeFormatPattern("yyyy/mm/dd hh:mm").spreadsheetFormatterSelector())
                .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.EXCEL_1900_DATE_SYSTEM_OFFSET)
                .set(SpreadsheetMetadataPropertyName.DATE_TIME_PARSER, SpreadsheetPattern.parseDateTimeParsePattern("yyyy/mm/dd hh:mm").spreadsheetParserSelector())
                .set(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, 2000)
                .set(SpreadsheetMetadataPropertyName.EXPRESSION_CONVERTER, ConverterSelector.parse("general"))
                .set(SpreadsheetMetadataPropertyName.EXPRESSION_FUNCTIONS, ExpressionFunctionInfoSet.parse(""))
                .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, ExpressionNumberKind.BIG_DECIMAL)
                .set(SpreadsheetMetadataPropertyName.GENERAL_NUMBER_FORMAT_DIGIT_COUNT, 8)
                .set(SpreadsheetMetadataPropertyName.MODIFIED_BY, EmailAddress.parse("user@example.com"))
                .set(SpreadsheetMetadataPropertyName.MODIFIED_DATE_TIME, LocalDateTime.now())
                .set(SpreadsheetMetadataPropertyName.NUMBER_FORMATTER, SpreadsheetPattern.parseNumberFormatPattern("0.#").spreadsheetFormatterSelector())
                .set(SpreadsheetMetadataPropertyName.NUMBER_PARSER, SpreadsheetPattern.parseNumberParsePattern("0.#").spreadsheetParserSelector())
                .set(SpreadsheetMetadataPropertyName.PRECISION, 7)
                .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.HALF_UP)
                .set(SpreadsheetMetadataPropertyName.SPREADSHEET_COMPARATORS, SpreadsheetComparatorInfoSet.with(SpreadsheetComparatorProviders.spreadsheetComparators().spreadsheetComparatorInfos()))
                .set(
                        SpreadsheetMetadataPropertyName.SPREADSHEET_FORMATTERS,
                        SpreadsheetFormatterInfoSet.with(
                                SPREADSHEET_FORMATTER_PROVIDER.spreadsheetFormatterInfos()
                        )
                ).set(SpreadsheetMetadataPropertyName.SPREADSHEET_PARSERS,
                        SpreadsheetParserInfoSet.with(
                                SPREADSHEET_PARSER_PROVIDER.spreadsheetParserInfos()
                        )
                ).set(SpreadsheetMetadataPropertyName.STYLE, TextStyle.EMPTY
                        .set(TextStylePropertyName.WIDTH, Length.parsePixels("100px"))
                        .set(TextStylePropertyName.HEIGHT, Length.parsePixels("50px"))
                ).set(SpreadsheetMetadataPropertyName.TEXT_FORMATTER, SpreadsheetPattern.parseTextFormatPattern("@").spreadsheetFormatterSelector())
                .set(SpreadsheetMetadataPropertyName.TIME_FORMATTER, SpreadsheetPattern.parseTimeFormatPattern("hh:mm:ss").spreadsheetFormatterSelector())
                .set(SpreadsheetMetadataPropertyName.TIME_PARSER, SpreadsheetPattern.parseTimeParsePattern("hh:mm:ss").spreadsheetParserSelector())
                .set(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR, 50);
    }

    private SpreadsheetMetadataTestingHelper() {
        throw new UnsupportedOperationException();
    }
}
