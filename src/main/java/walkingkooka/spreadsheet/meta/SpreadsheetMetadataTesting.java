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
import walkingkooka.color.Color;
import walkingkooka.convert.Converters;
import walkingkooka.convert.provider.ConverterInfoSet;
import walkingkooka.convert.provider.ConverterProvider;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorContext;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorContexts;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorInfoSet;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorProvider;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConvertersConverterProviders;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterInfoSet;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviderSamplesContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserInfoSet;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProvider;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.provider.SpreadsheetProviders;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.test.Testing;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionInfoSet;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviders;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.text.Length;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Provides factory methods for creating a {@link SpreadsheetMetadata} for testing.
 */
@GwtIncompatible
public interface SpreadsheetMetadataTesting extends Testing {

    ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.BIG_DECIMAL;

    Supplier<LocalDateTime> NOW = () -> LocalDateTime.of(
            1999,
            12,
            31,
            12,
            58
    );

    Locale LOCALE = Locale.forLanguageTag("EN-AU");

    ExpressionFunctionProvider EXPRESSION_FUNCTION_PROVIDER = ExpressionFunctionProviders.fake();

    SpreadsheetComparatorProvider SPREADSHEET_COMPARATOR_PROVIDER = SpreadsheetComparatorProviders.spreadsheetComparators();


    SpreadsheetFormatterProvider SPREADSHEET_FORMATTER_PROVIDER = SpreadsheetFormatterProviders.spreadsheetFormatPattern();

    SpreadsheetParserProvider SPREADSHEET_PARSER_PROVIDER = SpreadsheetParserProviders.spreadsheetParsePattern(
            SPREADSHEET_FORMATTER_PROVIDER
    );

    ConverterProvider CONVERTER_PROVIDER = SpreadsheetConvertersConverterProviders.spreadsheetConverters(
            SpreadsheetMetadata.EMPTY
                    .set(SpreadsheetMetadataPropertyName.DATE_FORMATTER, SpreadsheetPattern.parseDateFormatPattern("yyyy/mm/dd").spreadsheetFormatterSelector())
                    .set(SpreadsheetMetadataPropertyName.DATE_PARSER, SpreadsheetPattern.parseDateParsePattern("yyyy/mm/dd").spreadsheetParserSelector())
                    .set(SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER, SpreadsheetPattern.parseDateTimeFormatPattern("yyyy/mm/dd hh:mm").spreadsheetFormatterSelector())
                    .set(SpreadsheetMetadataPropertyName.DATE_TIME_PARSER, SpreadsheetPattern.parseDateTimeParsePattern("yyyy/mm/dd hh:mm").spreadsheetParserSelector())
                    .set(SpreadsheetMetadataPropertyName.NUMBER_FORMATTER, SpreadsheetPattern.parseNumberFormatPattern("0.#").spreadsheetFormatterSelector())
                    .set(SpreadsheetMetadataPropertyName.NUMBER_PARSER, SpreadsheetPattern.parseNumberParsePattern("0.#").spreadsheetParserSelector())
                    .set(SpreadsheetMetadataPropertyName.TEXT_FORMATTER, SpreadsheetPattern.parseTextFormatPattern("@").spreadsheetFormatterSelector())
                    .set(SpreadsheetMetadataPropertyName.TIME_FORMATTER, SpreadsheetPattern.parseTimeFormatPattern("hh:mm:ss").spreadsheetFormatterSelector())
                    .set(SpreadsheetMetadataPropertyName.TIME_PARSER, SpreadsheetPattern.parseTimeParsePattern("hh:mm:ss").spreadsheetParserSelector()),
            SPREADSHEET_FORMATTER_PROVIDER,
            SPREADSHEET_PARSER_PROVIDER
    );

    /**
     * Creates a {@link SpreadsheetMetadata} with Locale=EN-AU and standard patterns and other sensible defaults.
     */
    SpreadsheetMetadata METADATA_EN_AU = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
            .loadFromLocale()
            .set(SpreadsheetMetadataPropertyName.CELL_CHARACTER_WIDTH, 1)
            .set(
                    SpreadsheetMetadataPropertyName.CONVERTERS,
                    ConverterInfoSet.with(
                            CONVERTER_PROVIDER.converterInfos()
                    )
            ).set(SpreadsheetMetadataPropertyName.CREATOR, EmailAddress.parse("user@example.com"))
            .set(SpreadsheetMetadataPropertyName.CREATE_DATE_TIME, NOW.get())
            .set(SpreadsheetMetadataPropertyName.DATE_FORMATTER, SpreadsheetPattern.parseDateFormatPattern("yyyy/mm/dd").spreadsheetFormatterSelector())
            .set(SpreadsheetMetadataPropertyName.DATE_PARSER, SpreadsheetPattern.parseDateParsePattern("yyyy/mm/dd").spreadsheetParserSelector())
            .set(SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER, SpreadsheetPattern.parseDateTimeFormatPattern("yyyy/mm/dd hh:mm").spreadsheetFormatterSelector())
            .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.EXCEL_1900_DATE_SYSTEM_OFFSET)
            .set(SpreadsheetMetadataPropertyName.DATE_TIME_PARSER, SpreadsheetPattern.parseDateTimeParsePattern("yyyy/mm/dd hh:mm").spreadsheetParserSelector())
            .set(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, 2000)
            .set(SpreadsheetMetadataPropertyName.EXPRESSION_CONVERTER, ConverterSelector.parse("general"))
            .set(SpreadsheetMetadataPropertyName.EXPRESSION_FUNCTIONS, ExpressionFunctionInfoSet.parse(""))
            .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, EXPRESSION_NUMBER_KIND)
            .set(SpreadsheetMetadataPropertyName.GENERAL_NUMBER_FORMAT_DIGIT_COUNT, 8)
            .set(SpreadsheetMetadataPropertyName.MODIFIED_BY, EmailAddress.parse("user@example.com"))
            .set(SpreadsheetMetadataPropertyName.MODIFIED_DATE_TIME, NOW.get())
            .set(SpreadsheetMetadataPropertyName.NUMBER_FORMATTER, SpreadsheetPattern.parseNumberFormatPattern("0.#").spreadsheetFormatterSelector())
            .set(SpreadsheetMetadataPropertyName.NUMBER_PARSER, SpreadsheetPattern.parseNumberParsePattern("0.#").spreadsheetParserSelector())
            .set(SpreadsheetMetadataPropertyName.PRECISION, 7)
            .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.HALF_UP)
            .set(SpreadsheetMetadataPropertyName.SPREADSHEET_COMPARATORS,
                    SpreadsheetComparatorInfoSet.with(
                            SPREADSHEET_COMPARATOR_PROVIDER.spreadsheetComparatorInfos()
                    )
            ).set(
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
            .set(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR, 50)
            .set(SpreadsheetMetadataPropertyName.numberedColor(1), Color.BLACK)
            .set(SpreadsheetMetadataPropertyName.numberedColor(2), Color.WHITE)
            .set(SpreadsheetMetadataPropertyName.namedColor(SpreadsheetColorName.BLACK), 1)
            .set(SpreadsheetMetadataPropertyName.namedColor(SpreadsheetColorName.WHITE), 2);

    ProviderContext PROVIDER_CONTEXT = new ProviderContext() {
        @Override
        public <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
            return METADATA_EN_AU.environmentContext()
                    .environmentValue(name);
        }
    };

    SpreadsheetLabelNameResolver SPREADSHEET_LABEL_NAME_RESOLVER = SpreadsheetLabelNameResolvers.fake();

    SpreadsheetConverterContext SPREADSHEET_CONVERTER_CONTEXT = METADATA_EN_AU.converterContext(
            CONVERTER_PROVIDER,
            NOW,
            SPREADSHEET_LABEL_NAME_RESOLVER,
            PROVIDER_CONTEXT
    );

    SpreadsheetComparatorContext SPREADSHEET_COMPARATOR_CONTEXT = SpreadsheetComparatorContexts.basic(
            SPREADSHEET_CONVERTER_CONTEXT
    );

    SpreadsheetFormatterContext SPREADSHEET_FORMATTER_CONTEXT = METADATA_EN_AU.formatterContext(
            CONVERTER_PROVIDER,
            SPREADSHEET_FORMATTER_PROVIDER,
            NOW,
            SPREADSHEET_LABEL_NAME_RESOLVER,
            PROVIDER_CONTEXT
    );

    SpreadsheetFormatterProviderSamplesContext SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT = METADATA_EN_AU.spreadsheetFormatterProviderSamplesContext(
            CONVERTER_PROVIDER,
            SPREADSHEET_FORMATTER_PROVIDER,
            NOW,
            SPREADSHEET_LABEL_NAME_RESOLVER,
            PROVIDER_CONTEXT
    );

    JsonNodeMarshallContext JSON_NODE_MARSHALL_CONTEXT = METADATA_EN_AU.jsonNodeMarshallContext();

    JsonNodeUnmarshallContext JSON_NODE_UNMARSHALL_CONTEXT = METADATA_EN_AU.jsonNodeUnmarshallContext();

    SpreadsheetParserContext SPREADSHEET_PARSER_CONTEXT = METADATA_EN_AU.parserContext(NOW);

    SpreadsheetProvider SPREADSHEET_PROVIDER = SpreadsheetProviders.basic(
            CONVERTER_PROVIDER,
            EXPRESSION_FUNCTION_PROVIDER,
            SPREADSHEET_COMPARATOR_PROVIDER,
            SPREADSHEET_FORMATTER_PROVIDER,
            SPREADSHEET_PARSER_PROVIDER
    );

    static SpreadsheetFormula parseFormula(final String text) {
        return SpreadsheetFormula.parse(
                TextCursors.charSequence(text),
                METADATA_EN_AU.parser(
                        SPREADSHEET_PARSER_PROVIDER,
                        PROVIDER_CONTEXT
                ),
                SPREADSHEET_PARSER_CONTEXT
        );
    }
}
