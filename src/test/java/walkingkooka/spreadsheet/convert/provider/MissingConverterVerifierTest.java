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

package walkingkooka.spreadsheet.convert.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.Binary;
import walkingkooka.Either;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.color.ColorContext;
import walkingkooka.color.ColorContextDelegator;
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.currency.CurrencyCode;
import walkingkooka.currency.CurrencyExchange;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContextDelegator;
import walkingkooka.environment.CanParseEnvironmentValueName;
import walkingkooka.environment.CanParseEnvironmentValueNameDelegator;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContextDelegator;
import walkingkooka.locale.LocaleLanguageTag;
import walkingkooka.logging.CanLogs;
import walkingkooka.logging.LoggingContext;
import walkingkooka.logging.LoggingContextDelegator;
import walkingkooka.logging.LoggingContexts;
import walkingkooka.logging.LoggingLevel;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.header.MediaType;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.reflect.PackagePrivateClassTesting;
import walkingkooka.spreadsheet.color.SpreadsheetColorContexts;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContexts;
import walkingkooka.spreadsheet.convert.SpreadsheetConverters;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextTesting;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.validation.SpreadsheetValidationReference;
import walkingkooka.storage.StoragePath;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.text.printer.Printer;
import walkingkooka.text.printer.Printers;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContextDelegator;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;
import walkingkooka.validation.ValueType;

import java.math.MathContext;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class MissingConverterVerifierTest implements TreePrintableTesting,
    PackagePrivateClassTesting<MissingConverterVerifier>,
    SpreadsheetEnvironmentContextTesting {

    private final static SpreadsheetMetadata SPREADSHEET_METADATA_TESTING = SPREADSHEET_METADATA.set(
        SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
        SPREADSHEET_ID
    );

    private final static SpreadsheetMetadata SPREADSHEET_METADATA_DEFAULT = SpreadsheetMetadata.NON_LOCALE_DEFAULTS.setOrRemove(
            SpreadsheetMetadataPropertyName.LOCALE,
            LOCALE
        ).loadFromLocale(CURRENCY_LOCALE_CONTEXT)
        .set(
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
            SPREADSHEET_ID
        );

    @Test
    public void testVerifyAndCheckWithNullConverterFails() {
        assertThrows(
            NullPointerException.class,
            () -> MissingConverterVerifier.verify(
                null,
                SpreadsheetMetadataPropertyName.QUERY_CONVERTER,
                SpreadsheetConverterContexts.fake()
            )
        );
    }

    @Test
    public void testVerifyAndCheckWithNullSpreadsheetMetadataPropertyNameFails() {
        assertThrows(
            NullPointerException.class,
            () -> MissingConverterVerifier.verify(
                Converters.fake(),
                null,
                SpreadsheetConverterContexts.fake()
            )
        );
    }

    @Test
    public void testVerifyAndCheckWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> MissingConverterVerifier.verify(
                Converters.fake(),
                SpreadsheetMetadataPropertyName.QUERY_CONVERTER,
                null
            )
        );
    }

    @Test
    public void testVerifyAndCheckWithWithSpreadsheetMetadataTestingFormulaConverter() {
        this.spreadsheetMetadataTestingVerifyAndCheck(
            SpreadsheetMetadataPropertyName.FORMULA_CONVERTER
        );
    }

    @Test
    public void testVerifyAndCheckWithWithSpreadsheetMetadataTestingFormulaConverterDifferentDateOffset() {
        this.verifyAndCheck(
            SPREADSHEET_METADATA_TESTING.set(
                SpreadsheetMetadataPropertyName.DATE_OFFSET,
                DIFFERENT_DATE_OFFSET
            ),
            SpreadsheetMetadataPropertyName.FORMULA_CONVERTER
        );
    }

    @Test
    public void testVerifyAndCheckWithWithMetadataDefaultFormulaConverter() {
        this.spreadsheetMetadataDefaultVerifyAndCheck(
            SpreadsheetMetadataPropertyName.FORMULA_CONVERTER
        );
    }

    @Test
    public void testVerifyAndCheckWithWithSpreadsheetMetadataTestingFormattingConverter() {
        this.spreadsheetMetadataTestingVerifyAndCheck(
            SpreadsheetMetadataPropertyName.FORMATTING_CONVERTER
        );
    }

    @Test
    public void testVerifyAndCheckWithWithMetadataDefaultFormattingConverter() {
        this.spreadsheetMetadataDefaultVerifyAndCheck(
            SpreadsheetMetadataPropertyName.FORMATTING_CONVERTER
        );
    }

    @Test
    public void testVerifyAndCheckWithWithSpreadsheetMetadataTestingQueryConverter() {
        this.spreadsheetMetadataTestingVerifyAndCheck(
            SpreadsheetMetadataPropertyName.QUERY_CONVERTER
        );
    }

    @Test
    public void testVerifyAndCheckWithWithSpreadsheetMetadataDefaultQueryConverter() {
        this.spreadsheetMetadataDefaultVerifyAndCheck(
            SpreadsheetMetadataPropertyName.QUERY_CONVERTER
        );
    }

    @Test
    public void testVerifyAndCheckWithWithSpreadsheetMetadataTestingScriptingConverter() {
        this.spreadsheetMetadataTestingVerifyAndCheck(
            SpreadsheetMetadataPropertyName.SCRIPTING_CONVERTER
        );
    }

    @Test
    public void testVerifyAndCheckWithWithSpreadsheetMetadataDefaultScriptingConverter() {
        this.spreadsheetMetadataDefaultVerifyAndCheck(
            SpreadsheetMetadataPropertyName.SCRIPTING_CONVERTER
        );
    }

    @Test
    public void testVerifyAndCheckWithWithSpreadsheetMetadataTestingSortConverter() {
        this.spreadsheetMetadataTestingVerifyAndCheck(
            SpreadsheetMetadataPropertyName.SORT_CONVERTER
        );
    }

    @Test
    public void testVerifyAndCheckWithWithSpreadsheetMetadataDefaultsSortConverter() {
        this.spreadsheetMetadataDefaultVerifyAndCheck(
            SpreadsheetMetadataPropertyName.SORT_CONVERTER
        );
    }

    @Test
    public void testVerifyAndCheckWithWithSpreadsheetMetadataTestingValidationConverter() {
        this.spreadsheetMetadataTestingVerifyAndCheck(
            SpreadsheetMetadataPropertyName.VALIDATION_CONVERTER
        );
    }

    @Test
    public void testVerifyAndCheckWithWithSpreadsheetMetadataDefaultValidationConverter() {
        this.spreadsheetMetadataDefaultVerifyAndCheck(
            SpreadsheetMetadataPropertyName.VALIDATION_CONVERTER
        );
    }

    private void spreadsheetMetadataTestingVerifyAndCheck(final SpreadsheetMetadataPropertyName<ConverterSelector> propertyName,
                                                          final MissingConverter... expected) {
        this.verifyAndCheck(
            SPREADSHEET_METADATA_TESTING,
            propertyName,
            expected
        );
    }

    private void spreadsheetMetadataDefaultVerifyAndCheck(final SpreadsheetMetadataPropertyName<ConverterSelector> propertyName,
                                                          final MissingConverter... expected) {
        this.verifyAndCheck(
            SPREADSHEET_METADATA_DEFAULT,
            propertyName,
            expected
        );
    }

    @Test
    public void testValueTypeAllValues() {
        final SpreadsheetMetadataPropertyName<ConverterSelector> propertyName = SpreadsheetMetadataPropertyName.FORMULA_CONVERTER;
        final Converter<SpreadsheetConverterContext> converter = Converters.never();

        final Set<MissingConverter> missingConverters = MissingConverterVerifier.verify(
            converter,
            propertyName,
            new TestSpreadsheetConverterContext(
                SPREADSHEET_METADATA_TESTING.set(
                    propertyName,
                    ConverterSelector.parse("never")
                ),
                converter
            )
        );

        final Set<String> types = SortedSets.tree();

        for (final MissingConverter missingConverter : missingConverters) {
            for (final MissingConverterValue missingConverterValue : missingConverter.values()) {
                final String type = missingConverterValue.type();
                if (ValueType.parse(type).isEmpty()) {
                    types.add(type);
                }
            }
        }

        this.checkEquals(
            Sets.empty(),
            types
        );
    }

    private void verifyAndCheck(final SpreadsheetMetadata spreadsheetMetadata,
                                final SpreadsheetMetadataPropertyName<ConverterSelector> propertyName,
                                final MissingConverter... expected) {
        final ConverterSelector converterSelector = spreadsheetMetadata.getOrFail(propertyName);

        // KEEP useful helps to update SpreadsheetMetadataDefaultTextResource.json
        System.out.println(propertyName + "=" + converterSelector);

        final Converter<SpreadsheetConverterContext> converter = CONVERTER_PROVIDER.converter(
            converterSelector,
            PROVIDER_CONTEXT
        );

        this.checkEquals(
            Sets.of(expected),
            MissingConverterVerifier.verify(
                converter,
                propertyName,
                new TestSpreadsheetConverterContext(
                    spreadsheetMetadata,
                    converter
                )
            ),
            () -> propertyName + "=" + converter
        );
    }

    @Test
    public void testLogWithNone() {
        this.verifyAndLogCheck(
            LoggingLevel.NONE,
            ""
        );
    }

    // add "," top split expected otherwise String length > 64K
    @Test
    public void testLogWithInfo() {
        this.verifyAndLogCheck(
            LoggingLevel.INFO,
            "converter.verify INFO formulaConverter: never\n" +
                "converter.verify INFO formulaConverter: never 56=[binary 48 65 6c 6c 6f 20 57 6f 72 6c 64 20 42 69 6e 61 72 79       Hello World Binary  \n" +
                " walkingkooka.Binary, \"BinaryTextContent123\" walkingkooka.Binary, 42 69 6e 61 72 79 54 65 78 74 43 6f 6e 74 65 6e 74 31 32 33 BinaryTextContent123\n" +
                " java.lang.String, boolean 0 java.lang.Boolean, 0 java.lang.Boolean, \"FALSE\" java.lang.Boolean, 1 java.lang.Boolean, 1 java.lang.Boolean, \"TRUE\" java.lang.Boolean, \"TRUE\" java.lang.String, \"FALSE\" java.lang.String, collection-to [false] java.lang.Boolean, [true] java.lang.Boolean, [123] walkingkooka.tree.expression.ExpressionNumber, [123] java.lang.Byte, [123] java.lang.Short, [123] java.lang.Integer, [123] java.lang.Long, [123] java.lang.Float, [123] java.lang.Double, [123] java.math.BigInteger, [123] java.math.BigDecimal, [1999-12-31] java.time.LocalDate, [1999-12-31T12:58] java.time.LocalDateTime, [\"Hello\"] java.lang.String, [12:58] java.time.LocalTime, collection-to-list [true, false, true] walkingkooka.collect.list.BooleanList, [\"Apple 1\", \"Banana 2\"] walkingkooka.collect.list.CsvStringList, [1999-12-31, null] walkingkooka.datetime.LocalDateList, [1999-12-31T12:58, null] walkingkooka.datetime.LocalDateTimeList, [12:58, null] walkingkooka.datetime.LocalTimeList, [1, 22, 333.5, null] walkingkooka.math.NumberList, [\"Apple 1\", \"Banana 2\"] walkingkooka.collect.list.StringList, color black walkingkooka.color.HslColor, black walkingkooka.color.HsvColor, black walkingkooka.color.RgbColor, black walkingkooka.tree.expression.ExpressionNumber, black java.lang.Integer, black java.lang.Long, black java.lang.Float, black java.lang.Double, black java.math.BigInteger, black java.math.BigDecimal, 78 walkingkooka.tree.expression.ExpressionNumber, 78 java.lang.Byte, 78 java.lang.Short, 78 java.lang.Integer, 78 java.lang.Long, 78 java.lang.Float, 78 java.lang.Double, 78 java.math.BigInteger, 78 java.math.BigDecimal, 12 walkingkooka.tree.expression.ExpressionNumber, 12 java.lang.Byte, 12 java.lang.Short, 12 java.lang.Integer, 12 java.lang.Long, 12 java.lang.Float, 12 java.lang.Double, 12 java.math.BigInteger, 12 java.math.BigDecimal, 34 walkingkooka.tree.expression.ExpressionNumber, 34 java.lang.Byte, 34 java.lang.Short, 34 java.lang.Integer, 34 java.lang.Long, 34 java.lang.Float,, currency \"AUD\" walkingkooka.currency.CurrencyCode, \"AUD\" java.util.Currency, 1 AUD walkingkooka.tree.expression.ExpressionNumber, 1 AUD java.lang.Byte, 1 AUD java.lang.Short, 1 AUD java.lang.Integer, 1 AUD java.lang.Long, 1 AUD java.lang.Float, 1 AUD java.lang.Double, 1 AUD java.math.BigInteger, 1 AUD java.math.BigDecimal, 2 AUD walkingkooka.tree.expression.ExpressionNumber, 2 AUD java.lang.Byte, 2 AUD java.lang.Short, 2 AUD java.lang.Integer, 2 AUD java.lang.Long, 2 AUD java.lang.Float, 2 AUD java.lang.Double, 2 AUD java.math.BigInteger, 2 AUD java.math.BigDecimal, 3.0 AUD walkingkooka.tree.expression.ExpressionNumber, 3.0 AUD java.lang.Byte, 3.0 AUD java.lang.Short, 3.0 AUD java.lang.Integer, 3.0 AUD java.lang.Long, 3.0 AUD java.lang.Float, 3.0 AUD java.lang.Double, 3.0 AUD java.math.BigInteger, 3.0 AUD java.math.BigDecimal, 4.0 AUD walkingkooka.tree.expression.ExpressionNumber, 4.0 AUD java.l, date-time 1999-12-31 java.lang.Boolean, 1999-12-31 java.time.LocalDateTime, 1999-12-31 java.math.BigDecimal, 1999-12-31 java.math.BigInteger, 1999-12-31 java.lang.Double, 1999-12-31 java.lang.Float, 1999-12-31 java.lang.Long, 1999-12-31 walkingkooka.tree.expression.ExpressionNumber, 1999-12-31 java.lang.String, 1999-12-31T12:58 java.lang.Boolean, 1999-12-31T00:00 java.time.LocalDate, 1999-12-31T12:58 java.math.BigDecimal, 1999-12-31T00:00 java.math.BigInteger, 1999-12-31T12:58 java.lang.Double, 1999-12-31T12:58 java.lang.Float, 1999-12-31T00:00 java.lang.Long, 1999-12-31T00:00 walkingkooka.tree.expression.ExpressionNumber, 1999-12-31T12:58 java.lang.String, \"123\" walkingkooka.tree.expression.ExpressionNumber, \"123\" java.lang.Byte, \"123\" java.lang.Short, \"123\" java.lang.Integer, \"123\" java.lang.Long, \"123\" java.lang.Float, \"123\" java.lang.Double, \"123\" java.math.BigInteger, \"123\" java.math.BigDeci, date-time-symbols en_AU walkingkooka.datetime.DateTimeSymbols, A1 \"=1+2+3\" valueType=text/Text currencyExchangeRater=\"test-currency-exchange-rater\" dateTimeSymbols=\"ampms=\"am\", \"pm\" monthNames=\"January\", \"February\", \"March\", \"April\", \"May\", \"June\", \"July\", \"August\", \"September\", \"October\", \"November\", \"December\" monthNameAbbreviations=\"Jan.\", \"Feb.\", \"Mar.\", \"Apr.\", \"May\", \"Jun.\", \"Jul.\", \"Aug.\", \"Sep.\", \"Oct.\", \"Nov.\", \"Dec.\" weekDayNames=\"Sunday\", \"Monday\", \"Tuesday\", \"Wednesday\", \"Thursday\", \"Friday\", \"Saturday\" weekDayNameAbbreviations=\"Sun.\", \"Mon.\", \"Tue.\", \"Wed.\", \"Thu.\", \"Fri.\", \"Sat.\"\" decimalNumberSymbols=\"negativeSign='-' positiveSign='+' zeroDigit='0' currencySymbol=\"$\" decimalSeparator='.' exponentSymbol=\"e\" groupSeparator=',' infinitySymbol=\"∞\" monetaryDecimalSeparator='.' nanSymbol=\"NaN\" percentSymbol='%' permillSymbol='‰'\" formatter=\"test-formatter\" locale=\"en_AU\" parser=\"test-parser\" sty, decimal-number-symbols A1 \"=1+2+3\" valueType=text/Text currencyExchangeRater=\"test-currency-exchange-rater\" dateTimeSymbols=\"ampms=\"am\", \"pm\" monthNames=\"January\", \"February\", \"March\", \"April\", \"May\", \"June\", \"July\", \"August\", \"September\", \"October\", \"November\", \"December\" monthNameAbbreviations=\"Jan.\", \"Feb.\", \"Mar.\", \"Apr.\", \"May\", \"Jun.\", \"Jul.\", \"Aug.\", \"Sep.\", \"Oct.\", \"Nov.\", \"Dec.\" weekDayNames=\"Sunday\", \"Monday\", \"Tuesday\", \"Wednesday\", \"Thursday\", \"Friday\", \"Saturday\" weekDayNameAbbreviations=\"Sun.\", \"Mon.\", \"Tue.\", \"Wed.\", \"Thu.\", \"Fri.\", \"Sat.\"\" decimalNumberSymbols=\"negativeSign='-' positiveSign='+' zeroDigit='0' currencySymbol=\"$\" decimalSeparator='.' exponentSymbol=\"e\" groupSeparator=',' infinitySymbol=\"∞\" monetaryDecimalSeparator='.' nanSymbol=\"NaN\" percentSymbol='%' permillSymbol='‰'\" formatter=\"test-formatter\" locale=\"en_AU\" parser=\"test-parser\" style={color=black} validator=\"test-validator\" w, environment {} walkingkooka.environment.Environment, {charset=UTF-8, currency=AUD} java.lang.String, {charset=UTF-8, currency=AUD} walkingkooka.Binary, expression \"1+sum(2)\" walkingkooka.tree.expression.Expression, json \"Hello\" walkingkooka.tree.json.JsonNode, \"Hello\" walkingkooka.tree.json.JsonArray, \"Hello\" walkingkooka.tree.json.JsonBoolean, \"Hello\" walkingkooka.tree.json.JsonNumber, \"Hello\" walkingkooka.tree.json.JsonString, \"Hello\" walkingkooka.tree.json.JsonObject, \"{\\n  \\\"A1\\\": {\\n    \\\"formula\\\": {\\n      \\\"text\\\": \\\"=1+2+3\\\",\\n      \\\"valueType\\\": \\\"Text\\\"\\n    },\\n    \\\"currencyExchangeRater\\\": \\\"test-currency-exchange-rater\\\",\\n    \\\"dateTimeSymbols\\\": {\\n      \\\"ampms\\\": [\\n        \\\"am\\\",\\n        \\\"pm\\\"\\n      ],\\n      \\\"monthNames\\\": [\\n        \\\"January\\\",\\n        \\\"February\\\",\\n        \\\"March\\\",\\n        \\\"April\\\",\\n        \\\"May\\\",\\n        \\\"June\\\",\\n        \\\"July\\\",\\n        \\\"August\\\",\\n        \\\"September\\\",\\n        \\\"October\\\",\\n        \\\"November\\\",\\n        \\\"December\\\"\\n      ],\\n      \\\"monthNameAbbreviations\\\": [\\n        \\\"Jan.\\\",\\n        \\\"Feb.\\\",\\n        \\\"Mar.\\\",\\, locale \"en-AU\" walkingkooka.datetime.DateTimeSymbols, en_AU walkingkooka.datetime.DateTimeSymbols, \"en-AU\" walkingkooka.math.DecimalNumberSymbols, en_AU walkingkooka.math.DecimalNumberSymbols, \"en-AU\" java.util.Locale, en_AU java.util.Locale, A1 \"=1+2+3\" valueType=text/Text currencyExchangeRater=\"test-currency-exchange-rater\" dateTimeSymbols=\"ampms=\"am\", \"pm\" monthNames=\"January\", \"February\", \"March\", \"April\", \"May\", \"June\", \"July\", \"August\", \"September\", \"October\", \"November\", \"December\" monthNameAbbreviations=\"Jan.\", \"Feb.\", \"Mar.\", \"Apr.\", \"May\", \"Jun.\", \"Jul.\", \"Aug.\", \"Sep.\", \"Oct.\", \"Nov.\", \"Dec.\" weekDayNames=\"Sunday\", \"Monday\", \"Tuesday\", \"Wednesday\", \"Thursday\", \"Friday\", \"Saturday\" weekDayNameAbbreviations=\"Sun.\", \"Mon.\", \"Tue.\", \"Wed.\", \"Thu.\", \"Fri.\", \"Sat.\"\" decimalNumberSymbols=\"negativeSign='-' positiveSign='+' zeroDigit='0' currencySymbol=\"$\" decimalSeparator='.' exponentSymbol, net \"https://example.com/123\" walkingkooka.net.Url, \"mailto:user@example.com\" walkingkooka.net.Url, \"/path1/path2?k1=v1#fragment111\" walkingkooka.net.Url, \"user@example.com\" walkingkooka.net.email.EmailAddress, \"text/plain; charset=UTF-8\" walkingkooka.net.header.MediaType, \"https://example.com/123\" walkingkooka.net.HasHostAddress, \"user@example.com\" walkingkooka.net.HasHostAddress, \"mailto:user@example.com\" walkingkooka.net.HasHostAddress, number null walkingkooka.tree.expression.ExpressionNumber, null java.lang.Byte, null java.lang.Short, null java.lang.Integer, null java.lang.Long, null java.lang.Float, null java.lang.Double, null java.math.BigInteger, null java.math.BigDecimal, 1 walkingkooka.tree.expression.ExpressionNumber, 1 java.lang.Byte, 1 java.lang.Short, 1 java.lang.Integer, 1 java.lang.Long, 1 java.lang.Float, 1 java.lang.Double, 1 java.math.BigInteger, 1 java.math.BigDecimal, optional-to null java.lang.Boolean, false java.lang.Boolean, true java.lang.Boolean, 123 walkingkooka.tree.expression.ExpressionNumber, 123 java.lang.Byte, 123 java.lang.Short, 123 java.lang.Integer, 123 java.lang.Long, 123 java.lang.Float, 123 java.lang.Double, 123 java.math.BigInteger, 123 java.math.BigDecimal, 1999-12-31 java.time.LocalDate, 1999-12-31T12:58 java.time.LocalDateTime, 1999-12-31T12:58 walkingkooka.datetime.LocalDateTimeList, \"Hello\" java.lang.String, 12:58 java.time.LocalTime, plugins A1 \"=1+2+3\" valueType=text/Text currencyExchangeRater=\"test-currency-exchange-rater\" dateTimeSymbols=\"ampms=\"am\", \"pm\" monthNames=\"January\", \"February\", \"March\", \"April\", \"May\", \"June\", \"July\", \"August\", \"September\", \"October\", \"November\", \"December\" monthNameAbbreviations=\"Jan.\", \"Feb.\", \"Mar.\", \"Apr.\", \"May\", \"Jun.\", \"Jul.\", \"Aug.\", \"Sep.\", \"Oct.\", \"Nov.\", \"Dec.\" weekDayNames=\"Sunday\", \"Monday\", \"Tuesday\", \"Wednesday\", \"Thursday\", \"Friday\", \"Saturday\" weekDayNameAbbreviations=\"Sun.\", \"Mon.\", \"Tue.\", \"Wed.\", \"Thu.\", \"Fri.\", \"Sat.\"\" decimalNumberSymbols=\"negativeSign='-' positiveSign='+' zeroDigit='0' currencySymbol=\"$\" decimalSeparator='.' exponentSymbol=\"e\" groupSeparator=',' infinitySymbol=\"∞\" monetaryDecimalSeparator='.' nanSymbol=\"NaN\" percentSymbol='%' permillSymbol='‰'\" formatter=\"test-formatter\" locale=\"en_AU\" parser=\"test-parser\" style={color=black} validator=\"test-validator\" w, properties {\n" +
                "  \"spreadsheetId\": \"1\",\n" +
                "  \"spreadsheetName\": \"Spreadsheet111\"\n" +
                "} walkingkooka.props.Properties, spreadsheet-metadata \"123\" walkingkooka.spreadsheet.meta.SpreadsheetMetadata, 123 walkingkooka.spreadsheet.meta.SpreadsheetMetadata, {\n" +
                "  \"spreadsheetId\": \"123\",\n" +
                "  \"auditInfo\": {\n" +
                "    \"createdBy\": \"user123@example.com\",\n" +
                "    \"createdTimestamp\": \"1999-12-31T12:58:59\",\n" +
                "    \"modifiedBy\": \"user123@example.com\",\n" +
                "    \"modifiedTimestamp\": \"1999-12-31T12:58:59\"\n" +
                "  },\n" +
                "  \"autoHideScrollbars\": false,\n" +
                "  \"cellCharacterWidth\": 1,\n" +
                "  \"color1\": \"black\",\n" +
                "  \"color2\": \"white\",\n" +
                "  \"colorBlack\": 1,\n" +
                "  \"colorWhite\": 2,\n" +
                "  \"comparators\": \"background-color, border-bottom-color, border-color, border-left-color, border-right-color, border-top-color, color, currency, custom-list, custom-list-case-insensitive, date, date-time, day-of-month, day-of-week, error, formatter, hour-of-am-pm, hour-of-day, locale, minute-of-hour, month-of-year, nano-of-second, number, outline-color, parser, seconds-of-minute, text, text-case-insensitive, text-decorat, storage-binary-to-storage-value-binary /file.bin 00 01 02                                                    ???                 \n" +
                " walkingkooka.storage.StorageValue, /file.bin=00 01 02                                                    ???                 \n" +
                " walkingkooka.storage.StorageBinary, storage-binary-to-storage-value-csv /file.csv 41 70 70 6c 65 20 31 2c 42 61 6e 61 6e 61 20 32             Apple 1,Banana 2    \n" +
                " walkingkooka.storage.StorageValue, storage-binary-to-storage-value-environment /EnvironmentContext.env 63 68 61 72 73 65 74 3d 55 54 46 2d 38 0a 63 75 72 72 65 6e charset=UTF-8?curren\n" +
                "63 79 3d 41 55 44 0a 69 6e 64 65 6e 74 61 74 69 6f 6e 3d 22 cy=AUD?indentation=\"\n" +
                "20 20 22 0a 6c 69 6e 65 45 6e 64 69 6e 67 3d 4e 4c 0a 6c 6f   \"?lineEnding=NL?lo\n" +
                "63 61 6c 65 3d 65 6e 2d 41 55                               cale=en-AU          \n" +
                " walkingkooka.storage.StorageValue, storage-binary-to-storage-value-expression /formula.expression.txt 31 2b 32                                                    1+2                 \n" +
                " walkingkooka.storage.StorageValue, storage-binary-to-storage-value-json /file1/file2.json application/json7b 0a 20 20 22 68 65 6c 6c 6f 22 3a 20 22 77 6f 72 6c 64 22 {?  \"hello\": \"world\"\n" +
                "0a 7d                                                       ?}                  \n" +
                " walkingkooka.storage.StorageValue, storage-binary-to-storage-value-properties /file1/file2.properties 68 65 6c 6c 6f 3d 77 6f 72 6c 64 0d 0a                      hello=world??       \n" +
                " walkingkooka.storage.StorageValue, storage-binary-to-storage-value-tsv /file.tsv 41 70 70 6c 65 20 31 09 42 61 6e 61 6e 61 20 32             Apple 1?Banana 2    \n" +
                " walkingkooka.storage.StorageValue, storage-binary-to-storage-value-txt /file1.file2.txt 48 65 6c 6c 6f 20 57 6f 72 6c 64 20 54 65 78 74 20 31 32 33 Hello World Text 123\n" +
                " walkingkooka.storage.StorageValue, storage-value-info-list-to-text [/path1/file2.txt user@example.com 1999-12-31T12:58 user@example.com 1999-12-31T12:58] java.lang.String, storage-value-to-storage-binary-binary /file.bin=48 65 6c 6c 6f 20 57 6f 72 6c 64 20 42 69 6e 61 72 79       Hello World Binary  \n" +
                " walkingkooka.storage.StorageBinary, storage-value-to-storage-binary-csv /file.csv=\"Apple 1\", \"Banana 2\" walkingkooka.storage.StorageBinary, storage-value-to-storage-binary-environment /EnvironmentContext.env=\"charset=UTF-8\n" +
                "currency=AUD\n" +
                "indentation=\"  \"\n" +
                "lineEnding=NL\n" +
                "locale=en-AU\" walkingkooka.storage.StorageBinary, storage-value-to-storage-binary-expression /formula.expression.txt=\"1+2\" walkingkooka.storage.StorageBinary, storage-value-to-storage-binary-json /file1/file2.json={\n" +
                "  \"hello\": \"world\"\n" +
                "} application/json walkingkooka.storage.StorageBinary, storage-value-to-storage-binary-properties /file1/file2.properties=hello=world\n" +
                " walkingkooka.storage.StorageBinary, storage-value-to-storage-binary-tsv /file.tsv=\"Apple 1\", \"Banana 2\" walkingkooka.storage.StorageBinary, storage-value-to-storage-binary-txt /file1.file2.txt=\"Hello World Text 123\" walkingkooka.storage.StorageBinary, style {color=black} walkingkooka.tree.text.TextStyle, A1 style={color=black} walkingkooka.tree.text.TextStyle, color=black\n" +
                " walkingkooka.tree.text.TextStyle, \"color=black\\r\\n\" walkingkooka.tree.text.TextStyle, \"black SOLID 1px\" walkingkooka.tree.text.Border, \"1px\" walkingkooka.tree.text.Margin, \"1px\" walkingkooka.tree.text.Padding, \"Text123\" walkingkooka.spreadsheet.format.SpreadsheetText, \"color: black;\" walkingkooka.tree.text.TextStyle, \"background-color\" walkingkooka.tree.text.TextStylePropertyName, text 'A' java.lang.String, \"Text\" java.lang.String, https://example.com/123 java.lang.String, \"=1+2+3\" valueType=text/Text java.lang.String, ' ' java.lang.Character, ' ' java.lang.String, \"A\" java.lang.Character, \"A\" java.lang.String, \"UTF-8\" java.nio.charset.Charset, \"  \" walkingkooka.text.Indentation, \"    \" walkingkooka.text.Indentation, \"  \" walkingkooka.text.Indentation, \"\\r\\n\" walkingkooka.text.LineEnding, \"crnl\" walkingkooka.text.LineEnding, text-node \"https://example.com/123\" walkingkooka.tree.text.Hyperlink, \"https://example.com/123\" walkingkooka.tree.text.Image, {color: black}[\"Text123\"] walkingkooka.tree.text.Styleable, {color=black} walkingkooka.tree.text.Styleable, 'A' walkingkooka.tree.text.TextNode, \"Text123\" walkingkooka.tree.text.TextNode, \"Text123\" walkingkooka.tree.text.TextNode, text-to-boolean-list \"TRUE, FALSE, true\" walkingkooka.collect.list.BooleanList, text-to-color \"#123456\" walkingkooka.color.Color, text-to-csv-string-list \"Apple 1,Banana 2\" walkingkooka.collect.list.CsvStringList, text-to-date-list \"1999/12/31\" walkingkooka.datetime.LocalDateList, text-to-date-time-list \"1999/12/31 12:58\" walkingkooka.datetime.LocalDateTimeList, text-to-environment \"currency=AUD\" walkingkooka.environment.Environment, text-to-environment-value-name \"currency\" walkingkooka.environment.EnvironmentValueName, text-to-flag \"AU\" walkingkooka.tree.text.Flag, text-to-number-list \"1, 22, 333.5\" walkingkooka.math.NumberList, text-to-path \"/tmp/File1.txt\" java.nio.file.Path, text-to-storage-path \"/path1/file2.txt\" walkingkooka.storage.StoragePath, text-to-string-list \"Apple 1, Banana 2\" walkingkooka.collect.list.StringList, text-to-time-list \"12:58:59\" walkingkooka.datetime.LocalTimeList, text-to-tsv-string-list \"Apple 1\\tBanana 2\" walkingkooka.collect.list.TsvStringList, text-to-tsv-string-set \"Apple 1\\tBanana 2\" walkingkooka.collect.set.TsvStringSet, to-color-properties \"hello=BLACK\\r\\n\" walkingkooka.color.ColorProperties, value #NAME? \"Cell not found: \"A1\"\" A1 walkingkooka.spreadsheet.value.SpreadsheetError, #NAME? \"Cell not found: \"A1\"\" A1 walkingkooka.tree.expression.ExpressionNumber, null walkingkooka.tree.expression.ExpressionNumber, null java.lang.Byte, null java.lang.Short, null java.lang.Integer, null java.lang.Long, null java.lang.Float, null java.lang.Double, null java.math.BigInteger, null java.math.BigDecimal, 1 walkingkooka.tree.expression.ExpressionNumber, 1 java.lang.Byte, 1 java.lang.Short, 1 java.lang.Integer, 1 java.lang.Long, 1 java.lang.Float, 1 java.lang.Double, 1 java.math.BigInteger, 1 java.math.BigDecimal, 1.0 walkingkooka.tree.expression.ExpressionNumber, 1.0 java.lang.Byte, 1.0 java.lang.Short, 1.0 java.lang.Integer, 1.0 java.lang.Long, 1.0 java.lang.Float, 1.0 java.lang.Double, 1.0 java.math.BigInteger, 1.0 java.math.BigDecimal, {\n" +
                "  \"hello\": \"world\"\n" +
                "} walkingkooka.text.MultiLineText, ]\n"
        );
    }

    @Test
    public void testLogWithDebug() {
        this.verifyAndLogCheck(
            LoggingLevel.DEBUG,
            "converter.verify INFO formulaConverter: never\n" +
                "converter.verify DEBUG from java.lang.Integer: 0 to java.lang.Boolean\n" +
                "converter.verify DEBUG from walkingkooka.tree.expression.ExpressionNumberBigDecimal: 0 to java.lang.Boolean\n" +
                "converter.verify DEBUG from java.lang.String: \"FALSE\" to java.lang.Boolean\n" +
                "converter.verify DEBUG from java.lang.Integer: 1 to java.lang.Boolean\n" +
                "converter.verify DEBUG from walkingkooka.tree.expression.ExpressionNumberBigDecimal: 1 to java.lang.Boolean\n" +
                "converter.verify DEBUG from java.lang.String: \"TRUE\" to java.lang.Boolean\n" +
                "converter.verify DEBUG from java.lang.String: \"TRUE\" to java.lang.String\n" +
                "converter.verify DEBUG from java.lang.String: \"FALSE\" to java.lang.String\n" +
                "converter.verify DEBUG from java.util.ArrayList: [true, false, true] to walkingkooka.collect.list.BooleanList\n" +
                "converter.verify DEBUG from java.util.ArrayList: [\"Apple 1\", \"Banana 2\"] to walkingkooka.collect.list.CsvStringList\n" +
                "converter.verify DEBUG from java.util.ArrayList: [1999-12-31, null] to walkingkooka.datetime.LocalDateList\n" +
                "converter.verify DEBUG from java.util.ArrayList: [1999-12-31T12:58, null] to walkingkooka.datetime.LocalDateTimeList\n" +
                "converter.verify DEBUG from java.util.ArrayList: [12:58, null] to walkingkooka.datetime.LocalTimeList\n" +
                "converter.verify DEBUG from walkingkooka.collect.list.ImmutableListImplNonSingleton: [1, 22, 333.5, null] to walkingkooka.math.NumberList\n" +
                "converter.verify DEBUG from java.util.ArrayList: [\"Apple 1\", \"Banana 2\"] to walkingkooka.collect.list.StringList\n" +
                "converter.verify DEBUG from walkingkooka.collect.list.ImmutableListImplSingleton: [false] to java.lang.Boolean\n" +
                "converter.verify DEBUG from walkingkooka.collect.list.ImmutableListImplSingleton: [true] to java.lang.Boolean\n" +
                "converter.verify DEBUG from walkingkooka.collect.list.ImmutableListImplSingleton: [123] to walkingkooka.tree.expression.ExpressionNumber\n" +
                "converter.verify DEBUG from walkingkooka.collect.list.ImmutableListImplSingleton: [123] to java.lang.Byte\n" +
                "converter.verify DEBUG from walkingkooka.collect.list.ImmutableListImplSingleton: [123] to java.lang.Short\n" +
                "converter.verify DEBUG from walkingkooka.collect.list.ImmutableListImplSingleton: [123] to java.lang.Integer\n" +
                "converter.verify DEBUG from walkingkooka.collect.list.ImmutableListImplSingleton: [123] to java.lang.Long\n" +
                "converter.verify DEBUG from walkingkooka.collect.list.ImmutableListImplSingleton: [123] to java.lang.Float\n" +
                "converter.verify DEBUG from walkingkooka.collect.list.ImmutableListImplSingleton: [123] to java.lang.Double\n" +
                "converter.verify DEBUG from walkingkooka.collect.list.ImmutableListImplSingleton: [123] to java.math.BigInteger\n" +
                "converter.verify DEBUG from walkingkooka.collect.list.ImmutableListImplSingleton: [123] to java.math.BigDecimal\n" +
                "converter.verify DEBUG from walkingkooka.collect.list.ImmutableListImplSingleton: [1999-12-31] to java.time.LocalDate\n" +
                "converter.verify DEBUG from walkingkooka.collect.list.ImmutableListImplSingleton: [1999-12-31T12:58] to java.time.LocalDateTime\n" +
                "converter.verify DEBUG from walkingkooka.collect.list.ImmutableListImplSingleton: [\"Hello\"] to java.lang.String\n" +
                "converter.verify DEBUG from walkingkooka.collect.list.ImmutableListImplSingleton: [12:58] to java.time.LocalTime\n" +
                "converter.verify DEBUG from walkingkooka.color.OpaqueRgbColor: black to walkingkooka.color.HslColor\n" +
                "converter.verify DEBUG from walkingkooka.color.OpaqueRgbColor: black to walkingkooka.color.HsvColor\n" +
                "converter.verify DEBUG from walkingkooka.color.OpaqueRgbColor: black to walkingkooka.color.RgbColor\n" +
                "converter.verify DEBUG from walkingkooka.color.OpaqueRgbColor: black to walkingkooka.tree.expression.ExpressionNumber\n" +
                "converter.verify DEBUG from walkingkooka.color.OpaqueRgbColor: black to java.lang.Integer\n" +
                "converter.verify DEBUG from walkingkooka.color.OpaqueRgbColor: black to java.lang.Long\n" +
                "converter.verify DEBUG from walkingkooka.color.OpaqueRgbColor: black to java.lang.Float\n" +
                "converter.verify DEBUG from walkingkooka.color.OpaqueRgbColor: black to java.lang.Double\n" +
                "converter.verify DEBUG from walkingkooka.color.OpaqueRgbColor: black to java.math.BigInteger\n" +
                "converter.verify DEBUG from walkingkooka.color.OpaqueRgbColor: black to java.math.BigDecimal\n" +
                "converter.verify DEBUG from walkingkooka.color.AlphaRgbColorComponent: 78 to walkingkooka.tree.expression.ExpressionNumber\n" +
                "converter.verify DEBUG from walkingkooka.color.AlphaRgbColorComponent: 78 to java.lang.Byte\n" +
                "converter.verify DEBUG from walkingkooka.color.AlphaRgbColorComponent: 78 to java.lang.Short\n" +
                "converter.verify DEBUG from walkingkooka.color.AlphaRgbColorComponent: 78 to java.lang.Integer\n" +
                "converter.verify DEBUG from walkingkooka.color.AlphaRgbColorComponent: 78 to java.lang.Long\n" +
                "converter.verify DEBUG from walkingkooka.color.AlphaRgbColorComponent: 78 to java.lang.Float\n" +
                "converter.verify DEBUG from walkingkooka.color.AlphaRgbColorComponent: 78 to java.lang.Double\n" +
                "converter.verify DEBUG from walkingkooka.color.AlphaRgbColorComponent: 78 to java.math.BigInteger\n" +
                "converter.verify DEBUG from walkingkooka.color.AlphaRgbColorComponent: 78 to java.math.BigDecimal\n" +
                "converter.verify DEBUG from walkingkooka.color.RedRgbColorComponent: 12 to walkingkooka.tree.expression.ExpressionNumber\n" +
                "converter.verify DEBUG from walkingkooka.color.RedRgbColorComponent: 12 to java.lang.Byte\n" +
                "converter.verify DEBUG from walkingkooka.color.RedRgbColorComponent: 12 to java.lang.Short\n" +
                "converter.verify DEBUG from walkingkooka.color.RedRgbColorComponent: 12 to java.lang.Integer\n" +
                "converter.verify DEBUG from walkingkooka.color.RedRgbColorComponent: 12 to java.lang.Long\n" +
                "converter.verify DEBUG from walkingkooka.color.RedRgbColorComponent: 12 to java.lang.Float\n" +
                "converter.verify DEBUG from walkingkooka.color.RedRgbColorComponent: 12 to java.lang.Double\n" +
                "converter.verify DEBUG from walkingkooka.color.RedRgbColorComponent: 12 to java.math.BigInteger\n" +
                "converter.verify DEBUG from walkingkooka.color.RedRgbColorComponent: 12 to java.math.BigDecimal\n" +
                "converter.verify DEBUG from walkingkooka.color.GreenRgbColorComponent: 34 to walkingkooka.tree.expression.ExpressionNumber\n" +
                "converter.verify DEBUG from walkingkooka.color.GreenRgbColorComponent: 34 to java.lang.Byte\n" +
                "converter.verify DEBUG from walkingkooka.color.GreenRgbColorComponent: 34 to java.lang.Short\n" +
                "converter.verify DEBUG from walkingkooka.color.GreenRgbColorComponent: 34 to java.lang.Integer\n" +
                "converter.verify DEBUG from walkingkooka.color.GreenRgbColorComponent: 34 to java.lang.Long\n" +
                "converter.verify DEBUG from walkingkooka.color.GreenRgbColorComponent: 34 to java.lang.Float\n" +
                "converter.verify DEBUG from walkingkooka.color.GreenRgbColorComponent: 34 to java.lang.Double\n" +
                "converter.verify DEBUG from walkingkooka.color.GreenRgbColorComponent: 34 to java.math.BigInteger\n" +
                "converter.verify DEBUG from walkingkooka.color.GreenRgbColorComponent: 34 to java.math.BigDecimal\n" +
                "converter.verify DEBUG from walkingkooka.color.BlueRgbColorComponent: 56 to walkingkooka.tree.expression.ExpressionNumber\n" +
                "converter.verify DEBUG from walkingkooka.color.BlueRgbColorComponent: 56 to java.lang.Byte\n" +
                "converter.verify DEBUG from walkingkooka.color.BlueRgbColorComponent: 56 to java.lang.Short\n" +
                "converter.verify DEBUG from walkingkooka.color.BlueRgbColorComponent: 56 to java.lang.Integer\n" +
                "converter.verify DEBUG from walkingkooka.color.BlueRgbColorComponent: 56 to java.lang.Long\n" +
                "converter.verify DEBUG from walkingkooka.color.BlueRgbColorComponent: 56 to java.lang.Float\n" +
                "converter.verify DEBUG from walkingkooka.color.BlueRgbColorComponent: 56 to java.lang.Double\n" +
                "converter.verify DEBUG from walkingkooka.color.BlueRgbColorComponent: 56 to java.math.BigInteger\n" +
                "converter.verify DEBUG from walkingkooka.color.BlueRgbColorComponent: 56 to java.math.BigDecimal\n" +
                "converter.verify DEBUG from java.lang.String: \"#123456\" to walkingkooka.color.Color\n" +
                "converter.verify DEBUG from java.lang.String: \"hello=BLACK\\r\\n\" to walkingkooka.color.ColorProperties\n" +
                "converter.verify DEBUG from java.lang.String: \"AUD\" to walkingkooka.currency.CurrencyCode\n" +
                "converter.verify DEBUG from java.lang.String: \"AUD\" to java.util.Currency\n" +
                "converter.verify DEBUG from java.lang.String: \"AUD\" to walkingkooka.currency.CurrencyCode\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 1 AUD to walkingkooka.tree.expression.ExpressionNumber\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 1 AUD to java.lang.Byte\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 1 AUD to java.lang.Short\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 1 AUD to java.lang.Integer\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 1 AUD to java.lang.Long\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 1 AUD to java.lang.Float\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 1 AUD to java.lang.Double\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 1 AUD to java.math.BigInteger\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 1 AUD to java.math.BigDecimal\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 2 AUD to walkingkooka.tree.expression.ExpressionNumber\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 2 AUD to java.lang.Byte\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 2 AUD to java.lang.Short\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 2 AUD to java.lang.Integer\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 2 AUD to java.lang.Long\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 2 AUD to java.lang.Float\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 2 AUD to java.lang.Double\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 2 AUD to java.math.BigInteger\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 2 AUD to java.math.BigDecimal\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 3.0 AUD to walkingkooka.tree.expression.ExpressionNumber\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 3.0 AUD to java.lang.Byte\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 3.0 AUD to java.lang.Short\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 3.0 AUD to java.lang.Integer\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 3.0 AUD to java.lang.Long\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 3.0 AUD to java.lang.Float\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 3.0 AUD to java.lang.Double\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 3.0 AUD to java.math.BigInteger\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 3.0 AUD to java.math.BigDecimal\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 4.0 AUD to walkingkooka.tree.expression.ExpressionNumber\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 4.0 AUD to java.lang.Byte\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 4.0 AUD to java.lang.Short\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 4.0 AUD to java.lang.Integer\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 4.0 AUD to java.lang.Long\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 4.0 AUD to java.lang.Float\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 4.0 AUD to java.lang.Double\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 4.0 AUD to java.math.BigInteger\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyValue: 4.0 AUD to java.math.BigDecimal\n" +
                "converter.verify DEBUG from java.lang.String: \"AUD 1.5\" to walkingkooka.currency.CurrencyValue\n" +
                "converter.verify DEBUG from java.lang.String: \"1.5 AUD\" to walkingkooka.currency.CurrencyValue\n" +
                "converter.verify DEBUG from java.lang.String: \"1.5\" to walkingkooka.currency.CurrencyValue\n" +
                "converter.verify DEBUG from walkingkooka.currency.CurrencyCode: AUD to java.util.Currency\n" +
                "converter.verify DEBUG from java.lang.Integer: 1 to walkingkooka.currency.CurrencyValue\n" +
                "converter.verify DEBUG from java.lang.Double: 1.5 to walkingkooka.currency.CurrencyValue\n" +
                "converter.verify DEBUG from walkingkooka.tree.expression.ExpressionNumberBigDecimal: 2.75 to walkingkooka.currency.CurrencyValue\n" +
                "converter.verify DEBUG from java.time.LocalDate: 1999-12-31 to java.lang.Boolean\n" +
                "converter.verify DEBUG from java.time.LocalDate: 1999-12-31 to java.time.LocalDateTime\n" +
                "converter.verify DEBUG from java.time.LocalDate: 1999-12-31 to java.math.BigDecimal\n" +
                "converter.verify DEBUG from java.time.LocalDate: 1999-12-31 to java.math.BigInteger\n" +
                "converter.verify DEBUG from java.time.LocalDate: 1999-12-31 to java.lang.Double\n" +
                "converter.verify DEBUG from java.time.LocalDate: 1999-12-31 to java.lang.Float\n" +
                "converter.verify DEBUG from java.time.LocalDate: 1999-12-31 to java.lang.Long\n" +
                "converter.verify DEBUG from java.time.LocalDate: 1999-12-31 to walkingkooka.tree.expression.ExpressionNumber\n" +
                "converter.verify DEBUG from java.time.LocalDate: 1999-12-31 to java.lang.String\n" +
                "converter.verify DEBUG from java.time.LocalDateTime: 1999-12-31T12:58 to java.lang.Boolean\n" +
                "converter.verify DEBUG from java.time.LocalDateTime: 1999-12-31T00:00 to java.time.LocalDate\n" +
                "converter.verify DEBUG from java.time.LocalDateTime: 1999-12-31T12:58 to java.math.BigDecimal\n" +
                "converter.verify DEBUG from java.time.LocalDateTime: 1999-12-31T00:00 to java.math.BigInteger\n" +
                "converter.verify DEBUG from java.time.LocalDateTime: 1999-12-31T12:58 to java.lang.Double\n" +
                "converter.verify DEBUG from java.time.LocalDateTime: 1999-12-31T12:58 to java.lang.Float\n" +
                "converter.verify DEBUG from java.time.LocalDateTime: 1999-12-31T00:00 to java.lang.Long\n" +
                "converter.verify DEBUG from java.time.LocalDateTime: 1999-12-31T00:00 to walkingkooka.tree.expression.ExpressionNumber\n" +
                "converter.verify DEBUG from java.time.LocalDateTime: 1999-12-31T12:58 to java.lang.String\n" +
                "converter.verify DEBUG from java.lang.String: \"123\" to walkingkooka.tree.expression.ExpressionNumber\n" +
                "converter.verify DEBUG from java.lang.String: \"123\" to java.lang.Byte\n" +
                "converter.verify DEBUG from java.lang.String: \"123\" to java.lang.Short\n" +
                "converter.verify DEBUG from java.lang.String: \"123\" to java.lang.Integer\n" +
                "converter.verify DEBUG from java.lang.String: \"123\" to java.lang.Long\n" +
                "converter.verify DEBUG from java.lang.String: \"123\" to java.lang.Float\n" +
                "converter.verify DEBUG from java.lang.String: \"123\" to java.lang.Double\n" +
                "converter.verify DEBUG from java.lang.String: \"123\" to java.math.BigInteger\n" +
                "converter.verify DEBUG from java.lang.String: \"123\" to java.math.BigDecimal\n" +
                "converter.verify DEBUG from java.lang.String: \"1999/12/31\" to java.time.LocalDate\n" +
                "converter.verify DEBUG from java.lang.String: \"1999/12/31 12:58\" to java.time.LocalDateTime\n" +
                "converter.verify DEBUG from java.lang.String: \"12:58:59\" to java.time.LocalTime\n" +
                "converter.verify DEBUG from java.time.LocalTime: 12:58 to java.lang.Boolean\n" +
                "converter.verify DEBUG from java.time.LocalTime: 12:58 to java.math.BigDecimal\n" +
                "converter.verify DEBUG from java.time.LocalTime: 00:00 to java.math.BigInteger\n" +
                "converter.verify DEBUG from java.time.LocalTime: 12:58 to java.lang.Double\n" +
                "converter.verify DEBUG from java.time.LocalTime: 12:58 to java.lang.Float\n" +
                "converter.verify DEBUG from java.time.LocalTime: 00:00 to java.lang.Long\n" +
                "converter.verify DEBUG from java.time.LocalTime: 00:00 to java.lang.Short\n" +
                "converter.verify DEBUG from java.time.LocalTime: 12:58 to walkingkooka.tree.expression.ExpressionNumber\n" +
                "converter.verify DEBUG from java.time.LocalDateTime: 1999-12-31T12:58 to java.lang.String\n" +
                "converter.verify DEBUG from java.util.Locale: en_AU to walkingkooka.datetime.DateTimeSymbols\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.value.SpreadsheetCell: A1 \"=1+2+3\" valueType=text/Text currencyExchangeRater=\"test-currency-exchange-rater\" dateTimeSymbols=\"ampms=\"am\", \"pm\" monthNames=\"January\", \"February\", \"March\", \"April\", \"May\", \"June\", \"July\", \"August\", \"September\", \"October\", \"November\", \"December\" monthNameAbbreviations=\"Jan.\", \"Feb.\", \"Mar.\", \"Apr.\", \"May\", \"Jun.\", \"Jul.\", \"Aug.\", \"Sep.\", \"Oct.\", \"Nov.\", \"Dec.\" weekDayNames=\"Sunday\", \"Monday\", \"Tuesday\", \"Wednesday\", \"Thursday\", \"Friday\", \"Saturday\" weekDayNameAbbreviations=\"Sun.\", \"Mon.\", \"Tue.\", \"Wed.\", \"Thu.\", \"Fri.\", \"Sat.\"\" decimalNumberSymbols=\"negativeSign='-' positiveSign='+' zeroDigit='0' currencySymbol=\"$\" decimalSeparator='.' exponentSymbol=\"e\" groupSeparator=',' infinitySymbol=\"∞\" monetaryDecimalSeparator='.' nanSymbol=\"NaN\" percentSymbol='%' permillSymbol='‰'\" formatter=\"test-formatter\" locale=\"en_AU\" parser=\"test-parser\" style={color=black} validator=\"test-validator\" to walkingkooka.datetime.DateTimeSymbols\n" +
                "converter.verify DEBUG from walkingkooka.props.Properties: ampms=am,pm\n" +
                "monthNameAbbreviations=Jan.,Feb.,Mar.,Apr.,May,Jun.,Jul.,Aug.,Sep.,Oct.,Nov.,Dec.\n" +
                "monthNames=January,February,March,April,May,June,July,August,September,October,November,December\n" +
                "weekDayNameAbbreviations=Sun.,Mon.,Tue.,Wed.,Thu.,Fri.,Sat.\n" +
                "weekDayNames=Sunday,Monday,Tuesday,Wednesday,Thursday,Friday,Saturday\n" +
                " to walkingkooka.datetime.DateTimeSymbols\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.value.SpreadsheetCell: A1 \"=1+2+3\" valueType=text/Text currencyExchangeRater=\"test-currency-exchange-rater\" dateTimeSymbols=\"ampms=\"am\", \"pm\" monthNames=\"January\", \"February\", \"March\", \"April\", \"May\", \"June\", \"July\", \"August\", \"September\", \"October\", \"November\", \"December\" monthNameAbbreviations=\"Jan.\", \"Feb.\", \"Mar.\", \"Apr.\", \"May\", \"Jun.\", \"Jul.\", \"Aug.\", \"Sep.\", \"Oct.\", \"Nov.\", \"Dec.\" weekDayNames=\"Sunday\", \"Monday\", \"Tuesday\", \"Wednesday\", \"Thursday\", \"Friday\", \"Saturday\" weekDayNameAbbreviations=\"Sun.\", \"Mon.\", \"Tue.\", \"Wed.\", \"Thu.\", \"Fri.\", \"Sat.\"\" decimalNumberSymbols=\"negativeSign='-' positiveSign='+' zeroDigit='0' currencySymbol=\"$\" decimalSeparator='.' exponentSymbol=\"e\" groupSeparator=',' infinitySymbol=\"∞\" monetaryDecimalSeparator='.' nanSymbol=\"NaN\" percentSymbol='%' permillSymbol='‰'\" formatter=\"test-formatter\" locale=\"en_AU\" parser=\"test-parser\" style={color=black} validator=\"test-validator\" to walkingkooka.math.DecimalNumberSymbols\n" +
                "converter.verify DEBUG from java.util.Locale: en_AU to walkingkooka.math.DecimalNumberSymbols\n" +
                "converter.verify DEBUG from walkingkooka.props.Properties: currencySymbol=$\n" +
                "decimalSeparator=.\n" +
                "exponentSymbol=e\n" +
                "groupSeparator=,\n" +
                "infinitySymbol=\\u221e\n" +
                "monetaryDecimalSeparator=.\n" +
                "nanSymbol=NaN\n" +
                "negativeSign=-\n" +
                "percentSymbol=%\n" +
                "permillSymbol=\\u2030\n" +
                "positiveSign=+\n" +
                "zeroDigit=0\n" +
                " to walkingkooka.math.DecimalNumberSymbols\n" +
                "converter.verify DEBUG from walkingkooka.environment.Environment: {} to walkingkooka.environment.Environment\n" +
                "converter.verify DEBUG from java.lang.String: \"currency\" to walkingkooka.environment.EnvironmentValueName\n" +
                "converter.verify DEBUG from walkingkooka.environment.Environment: {charset=UTF-8, currency=AUD} to java.lang.String\n" +
                "converter.verify DEBUG from walkingkooka.environment.Environment: {charset=UTF-8, currency=AUD} to walkingkooka.Binary\n" +
                "converter.verify DEBUG from java.lang.String: \"currency=AUD\" to walkingkooka.environment.Environment\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.value.SpreadsheetError: #NAME? \"Cell not found: \"A1\"\" A1 to walkingkooka.spreadsheet.value.SpreadsheetError\n" +
                "converter.verify DEBUG from java.lang.String: \"1+sum(2)\" to walkingkooka.tree.expression.Expression\n" +
                "converter.verify DEBUG from java.lang.String: \"Hello\" to walkingkooka.tree.json.JsonNode\n" +
                "converter.verify DEBUG from java.lang.String: \"Hello\" to walkingkooka.tree.json.JsonArray\n" +
                "converter.verify DEBUG from java.lang.String: \"Hello\" to walkingkooka.tree.json.JsonBoolean\n" +
                "converter.verify DEBUG from java.lang.String: \"Hello\" to walkingkooka.tree.json.JsonNumber\n" +
                "converter.verify DEBUG from java.lang.String: \"Hello\" to walkingkooka.tree.json.JsonString\n" +
                "converter.verify DEBUG from java.lang.String: \"Hello\" to walkingkooka.tree.json.JsonObject\n" +
                "converter.verify DEBUG from java.lang.String: \"{\\n  \\\"A1\\\": {\\n    \\\"formula\\\": {\\n      \\\"text\\\": \\\"=1+2+3\\\",\\n      \\\"valueType\\\": \\\"Text\\\"\\n    },\\n    \\\"currencyExchangeRater\\\": \\\"test-currency-exchange-rater\\\",\\n    \\\"dateTimeSymbols\\\": {\\n      \\\"ampms\\\": [\\n        \\\"am\\\",\\n        \\\"pm\\\"\\n      ],\\n      \\\"monthNames\\\": [\\n        \\\"January\\\",\\n        \\\"February\\\",\\n        \\\"March\\\",\\n        \\\"April\\\",\\n        \\\"May\\\",\\n        \\\"June\\\",\\n        \\\"July\\\",\\n        \\\"August\\\",\\n        \\\"September\\\",\\n        \\\"October\\\",\\n        \\\"November\\\",\\n        \\\"December\\\"\\n      ],\\n      \\\"monthNameAbbreviations\\\": [\\n        \\\"Jan.\\\",\\n        \\\"Feb.\\\",\\n        \\\"Mar.\\\",\\n        \\\"Apr.\\\",\\n        \\\"May\\\",\\n        \\\"Jun.\\\",\\n        \\\"Jul.\\\",\\n        \\\"Aug.\\\",\\n        \\\"Sep.\\\",\\n        \\\"Oct.\\\",\\n        \\\"Nov.\\\",\\n        \\\"Dec.\\\"\\n      ],\\n      \\\"weekDayNames\\\": [\\n        \\\"Sunday\\\",\\n        \\\"Monday\\\",\\n        \\\"Tuesday\\\",\\n        \\\"Wednesday\\\",\\n        \\\"Thursday\\\",\\n        \\\"Friday\\\",\\n        \\\"Saturday\\\"\\n      ],\\n      \\\"weekDayNameAbbreviations\\\": [\\n        \\\"Sun.\\\",\\n        \\\"Mon.\\\",\\n        \\\"Tue.\\\",\\n        \\\"Wed.\\\",\\n        \\\"Thu.\\\",\\n        \\\"Fri.\\\",\\n        \\\"Sat.\\\"\\n      ]\\n    },\\n    \\\"decimalNumberSymbols\\\": {\\n      \\\"negativeSign\\\": \\\"-\\\",\\n      \\\"positiveSign\\\": \\\"+\\\",\\n      \\\"zeroDigit\\\": \\\"0\\\",\\n      \\\"currencySymbol\\\": \\\"$\\\",\\n      \\\"decimalSeparator\\\": \\\".\\\",\\n      \\\"exponentSymbol\\\": \\\"e\\\",\\n      \\\"groupSeparator\\\": \\\",\\\",\\n      \\\"infinitySymbol\\\": \\\"∞\\\",\\n      \\\"monetaryDecimalSeparator\\\": \\\".\\\",\\n      \\\"nanSymbol\\\": \\\"NaN\\\",\\n      \\\"percentSymbol\\\": \\\"%\\\",\\n      \\\"permillSymbol\\\": \\\"‰\\\"\\n    },\\n    \\\"formatter\\\": \\\"test-formatter\\\",\\n    \\\"locale\\\": \\\"en-AU\\\",\\n    \\\"parser\\\": \\\"test-parser\\\",\\n    \\\"style\\\": {\\n      \\\"color\\\": \\\"black\\\"\\n    },\\n    \\\"validator\\\": \\\"test-validator\\\"\\n  }\\n}\" to walkingkooka.spreadsheet.value.SpreadsheetCell\n" +
                "converter.verify DEBUG from java.lang.String: \"/json1/pointer2\" to walkingkooka.tree.json.pointer.JsonPointer\n" +
                "converter.verify DEBUG from java.lang.String: \"//*/json1/pointer2/.\" to walkingkooka.tree.json.select.JsonSelector\n" +
                "converter.verify DEBUG from java.lang.String: \"en-AU\" to walkingkooka.datetime.DateTimeSymbols\n" +
                "converter.verify DEBUG from java.util.Locale: en_AU to walkingkooka.datetime.DateTimeSymbols\n" +
                "converter.verify DEBUG from java.lang.String: \"en-AU\" to walkingkooka.math.DecimalNumberSymbols\n" +
                "converter.verify DEBUG from java.util.Locale: en_AU to walkingkooka.math.DecimalNumberSymbols\n" +
                "converter.verify DEBUG from java.lang.String: \"en-AU\" to java.util.Locale\n" +
                "converter.verify DEBUG from java.util.Locale: en_AU to java.util.Locale\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.value.SpreadsheetCell: A1 \"=1+2+3\" valueType=text/Text currencyExchangeRater=\"test-currency-exchange-rater\" dateTimeSymbols=\"ampms=\"am\", \"pm\" monthNames=\"January\", \"February\", \"March\", \"April\", \"May\", \"June\", \"July\", \"August\", \"September\", \"October\", \"November\", \"December\" monthNameAbbreviations=\"Jan.\", \"Feb.\", \"Mar.\", \"Apr.\", \"May\", \"Jun.\", \"Jul.\", \"Aug.\", \"Sep.\", \"Oct.\", \"Nov.\", \"Dec.\" weekDayNames=\"Sunday\", \"Monday\", \"Tuesday\", \"Wednesday\", \"Thursday\", \"Friday\", \"Saturday\" weekDayNameAbbreviations=\"Sun.\", \"Mon.\", \"Tue.\", \"Wed.\", \"Thu.\", \"Fri.\", \"Sat.\"\" decimalNumberSymbols=\"negativeSign='-' positiveSign='+' zeroDigit='0' currencySymbol=\"$\" decimalSeparator='.' exponentSymbol=\"e\" groupSeparator=',' infinitySymbol=\"∞\" monetaryDecimalSeparator='.' nanSymbol=\"NaN\" percentSymbol='%' permillSymbol='‰'\" formatter=\"test-formatter\" locale=\"en_AU\" parser=\"test-parser\" style={color=black} validator=\"test-validator\" to java.util.Locale\n" +
                "converter.verify DEBUG from java.lang.String: \"en-AU\" to walkingkooka.locale.LocaleLanguageTag\n" +
                "converter.verify DEBUG from java.util.Locale: en_AU to walkingkooka.locale.LocaleLanguageTag\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.value.SpreadsheetCell: A1 \"=1+2+3\" valueType=text/Text currencyExchangeRater=\"test-currency-exchange-rater\" dateTimeSymbols=\"ampms=\"am\", \"pm\" monthNames=\"January\", \"February\", \"March\", \"April\", \"May\", \"June\", \"July\", \"August\", \"September\", \"October\", \"November\", \"December\" monthNameAbbreviations=\"Jan.\", \"Feb.\", \"Mar.\", \"Apr.\", \"May\", \"Jun.\", \"Jul.\", \"Aug.\", \"Sep.\", \"Oct.\", \"Nov.\", \"Dec.\" weekDayNames=\"Sunday\", \"Monday\", \"Tuesday\", \"Wednesday\", \"Thursday\", \"Friday\", \"Saturday\" weekDayNameAbbreviations=\"Sun.\", \"Mon.\", \"Tue.\", \"Wed.\", \"Thu.\", \"Fri.\", \"Sat.\"\" decimalNumberSymbols=\"negativeSign='-' positiveSign='+' zeroDigit='0' currencySymbol=\"$\" decimalSeparator='.' exponentSymbol=\"e\" groupSeparator=',' infinitySymbol=\"∞\" monetaryDecimalSeparator='.' nanSymbol=\"NaN\" percentSymbol='%' permillSymbol='‰'\" formatter=\"test-formatter\" locale=\"en_AU\" parser=\"test-parser\" style={color=black} validator=\"test-validator\" to walkingkooka.locale.LocaleLanguageTag\n" +
                "converter.verify DEBUG from java.lang.String: \"https://example.com/123\" to walkingkooka.net.Url\n" +
                "converter.verify DEBUG from java.lang.String: \"mailto:user@example.com\" to walkingkooka.net.Url\n" +
                "converter.verify DEBUG from java.lang.String: \"/path1/path2?k1=v1#fragment111\" to walkingkooka.net.Url\n" +
                "converter.verify DEBUG from java.lang.String: \"user@example.com\" to walkingkooka.net.email.EmailAddress\n" +
                "converter.verify DEBUG from java.lang.String: \"text/plain; charset=UTF-8\" to walkingkooka.net.header.MediaType\n" +
                "converter.verify DEBUG from java.lang.String: \"https://example.com/123\" to walkingkooka.net.HasHostAddress\n" +
                "converter.verify DEBUG from java.lang.String: \"user@example.com\" to walkingkooka.net.HasHostAddress\n" +
                "converter.verify DEBUG from java.lang.String: \"mailto:user@example.com\" to walkingkooka.net.HasHostAddress\n" +
                "converter.verify DEBUG from null to walkingkooka.tree.expression.ExpressionNumber\n" +
                "converter.verify DEBUG from null to java.lang.Byte\n" +
                "converter.verify DEBUG from null to java.lang.Short\n" +
                "converter.verify DEBUG from null to java.lang.Integer\n" +
                "converter.verify DEBUG from null to java.lang.Long\n" +
                "converter.verify DEBUG from null to java.lang.Float\n" +
                "converter.verify DEBUG from null to java.lang.Double\n" +
                "converter.verify DEBUG from null to java.math.BigInteger\n" +
                "converter.verify DEBUG from null to java.math.BigDecimal\n" +
                "converter.verify DEBUG from java.lang.Integer: 1 to walkingkooka.tree.expression.ExpressionNumber\n" +
                "converter.verify DEBUG from java.lang.Integer: 1 to java.lang.Byte\n" +
                "converter.verify DEBUG from java.lang.Integer: 1 to java.lang.Short\n" +
                "converter.verify DEBUG from java.lang.Integer: 1 to java.lang.Integer\n" +
                "converter.verify DEBUG from java.lang.Integer: 1 to java.lang.Long\n" +
                "converter.verify DEBUG from java.lang.Integer: 1 to java.lang.Float\n" +
                "converter.verify DEBUG from java.lang.Integer: 1 to java.lang.Double\n" +
                "converter.verify DEBUG from java.lang.Integer: 1 to java.math.BigInteger\n" +
                "converter.verify DEBUG from java.lang.Integer: 1 to java.math.BigDecimal\n" +
                "converter.verify DEBUG from java.util.Optional: null to java.lang.Boolean\n" +
                "converter.verify DEBUG from java.util.Optional: false to java.lang.Boolean\n" +
                "converter.verify DEBUG from java.util.Optional: true to java.lang.Boolean\n" +
                "converter.verify DEBUG from java.util.Optional: 123 to walkingkooka.tree.expression.ExpressionNumber\n" +
                "converter.verify DEBUG from java.util.Optional: 123 to java.lang.Byte\n" +
                "converter.verify DEBUG from java.util.Optional: 123 to java.lang.Short\n" +
                "converter.verify DEBUG from java.util.Optional: 123 to java.lang.Integer\n" +
                "converter.verify DEBUG from java.util.Optional: 123 to java.lang.Long\n" +
                "converter.verify DEBUG from java.util.Optional: 123 to java.lang.Float\n" +
                "converter.verify DEBUG from java.util.Optional: 123 to java.lang.Double\n" +
                "converter.verify DEBUG from java.util.Optional: 123 to java.math.BigInteger\n" +
                "converter.verify DEBUG from java.util.Optional: 123 to java.math.BigDecimal\n" +
                "converter.verify DEBUG from java.util.Optional: 1999-12-31 to java.time.LocalDate\n" +
                "converter.verify DEBUG from java.util.Optional: 1999-12-31T12:58 to java.time.LocalDateTime\n" +
                "converter.verify DEBUG from java.util.Optional: 1999-12-31T12:58 to walkingkooka.datetime.LocalDateTimeList\n" +
                "converter.verify DEBUG from java.util.Optional: \"Hello\" to java.lang.String\n" +
                "converter.verify DEBUG from java.util.Optional: 12:58 to java.time.LocalTime\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.value.SpreadsheetCell: A1 \"=1+2+3\" valueType=text/Text currencyExchangeRater=\"test-currency-exchange-rater\" dateTimeSymbols=\"ampms=\"am\", \"pm\" monthNames=\"January\", \"February\", \"March\", \"April\", \"May\", \"June\", \"July\", \"August\", \"September\", \"October\", \"November\", \"December\" monthNameAbbreviations=\"Jan.\", \"Feb.\", \"Mar.\", \"Apr.\", \"May\", \"Jun.\", \"Jul.\", \"Aug.\", \"Sep.\", \"Oct.\", \"Nov.\", \"Dec.\" weekDayNames=\"Sunday\", \"Monday\", \"Tuesday\", \"Wednesday\", \"Thursday\", \"Friday\", \"Saturday\" weekDayNameAbbreviations=\"Sun.\", \"Mon.\", \"Tue.\", \"Wed.\", \"Thu.\", \"Fri.\", \"Sat.\"\" decimalNumberSymbols=\"negativeSign='-' positiveSign='+' zeroDigit='0' currencySymbol=\"$\" decimalSeparator='.' exponentSymbol=\"e\" groupSeparator=',' infinitySymbol=\"∞\" monetaryDecimalSeparator='.' nanSymbol=\"NaN\" percentSymbol='%' permillSymbol='‰'\" formatter=\"test-formatter\" locale=\"en_AU\" parser=\"test-parser\" style={color=black} validator=\"test-validator\" to walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.value.SpreadsheetCell: A1 \"=1+2+3\" valueType=text/Text currencyExchangeRater=\"test-currency-exchange-rater\" dateTimeSymbols=\"ampms=\"am\", \"pm\" monthNames=\"January\", \"February\", \"March\", \"April\", \"May\", \"June\", \"July\", \"August\", \"September\", \"October\", \"November\", \"December\" monthNameAbbreviations=\"Jan.\", \"Feb.\", \"Mar.\", \"Apr.\", \"May\", \"Jun.\", \"Jul.\", \"Aug.\", \"Sep.\", \"Oct.\", \"Nov.\", \"Dec.\" weekDayNames=\"Sunday\", \"Monday\", \"Tuesday\", \"Wednesday\", \"Thursday\", \"Friday\", \"Saturday\" weekDayNameAbbreviations=\"Sun.\", \"Mon.\", \"Tue.\", \"Wed.\", \"Thu.\", \"Fri.\", \"Sat.\"\" decimalNumberSymbols=\"negativeSign='-' positiveSign='+' zeroDigit='0' currencySymbol=\"$\" decimalSeparator='.' exponentSymbol=\"e\" groupSeparator=',' infinitySymbol=\"∞\" monetaryDecimalSeparator='.' nanSymbol=\"NaN\" percentSymbol='%' permillSymbol='‰'\" formatter=\"test-formatter\" locale=\"en_AU\" parser=\"test-parser\" style={color=black} validator=\"test-validator\" to walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelector\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.value.SpreadsheetCell: A1 \"=1+2+3\" valueType=text/Text currencyExchangeRater=\"test-currency-exchange-rater\" dateTimeSymbols=\"ampms=\"am\", \"pm\" monthNames=\"January\", \"February\", \"March\", \"April\", \"May\", \"June\", \"July\", \"August\", \"September\", \"October\", \"November\", \"December\" monthNameAbbreviations=\"Jan.\", \"Feb.\", \"Mar.\", \"Apr.\", \"May\", \"Jun.\", \"Jul.\", \"Aug.\", \"Sep.\", \"Oct.\", \"Nov.\", \"Dec.\" weekDayNames=\"Sunday\", \"Monday\", \"Tuesday\", \"Wednesday\", \"Thursday\", \"Friday\", \"Saturday\" weekDayNameAbbreviations=\"Sun.\", \"Mon.\", \"Tue.\", \"Wed.\", \"Thu.\", \"Fri.\", \"Sat.\"\" decimalNumberSymbols=\"negativeSign='-' positiveSign='+' zeroDigit='0' currencySymbol=\"$\" decimalSeparator='.' exponentSymbol=\"e\" groupSeparator=',' infinitySymbol=\"∞\" monetaryDecimalSeparator='.' nanSymbol=\"NaN\" percentSymbol='%' permillSymbol='‰'\" formatter=\"test-formatter\" locale=\"en_AU\" parser=\"test-parser\" style={color=black} validator=\"test-validator\" to walkingkooka.validation.provider.ValidatorSelector\n" +
                "converter.verify DEBUG from java.lang.String: \"test-converter\" to walkingkooka.convert.provider.ConverterSelector\n" +
                "converter.verify DEBUG from java.lang.String: \"test-currency-exchange-rater\" to walkingkooka.currency.provider.CurrencyExchangeRaterSelector\n" +
                "converter.verify DEBUG from java.lang.String: \"test-function\" to walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector\n" +
                "converter.verify DEBUG from java.lang.String: \"test-comparator\" to walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorSelector\n" +
                "converter.verify DEBUG from java.lang.String: \"test-formatter\" to walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector\n" +
                "converter.verify DEBUG from java.lang.String: \"test-parser\" to walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelector\n" +
                "converter.verify DEBUG from java.lang.String: \"test-validator\" to walkingkooka.validation.provider.ValidatorSelector\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.meta.SpreadsheetMetadataNonEmpty: {\n" +
                "  \"spreadsheetId\": \"1\",\n" +
                "  \"spreadsheetName\": \"Spreadsheet111\"\n" +
                "} to walkingkooka.props.Properties\n" +
                "converter.verify DEBUG from java.lang.String: \"123\" to walkingkooka.spreadsheet.meta.SpreadsheetMetadata\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.meta.SpreadsheetId: 123 to walkingkooka.spreadsheet.meta.SpreadsheetMetadata\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.meta.SpreadsheetMetadataNonEmpty: {\n" +
                "  \"spreadsheetId\": \"123\",\n" +
                "  \"auditInfo\": {\n" +
                "    \"createdBy\": \"user123@example.com\",\n" +
                "    \"createdTimestamp\": \"1999-12-31T12:58:59\",\n" +
                "    \"modifiedBy\": \"user123@example.com\",\n" +
                "    \"modifiedTimestamp\": \"1999-12-31T12:58:59\"\n" +
                "  },\n" +
                "  \"autoHideScrollbars\": false,\n" +
                "  \"cellCharacterWidth\": 1,\n" +
                "  \"color1\": \"black\",\n" +
                "  \"color2\": \"white\",\n" +
                "  \"colorBlack\": 1,\n" +
                "  \"colorWhite\": 2,\n" +
                "  \"comparators\": \"background-color, border-bottom-color, border-color, border-left-color, border-right-color, border-top-color, color, currency, custom-list, custom-list-case-insensitive, date, date-time, day-of-month, day-of-week, error, formatter, hour-of-am-pm, hour-of-day, locale, minute-of-hour, month-of-year, nano-of-second, number, outline-color, parser, seconds-of-minute, text, text-case-insensitive, text-decoration-color, text-with-numbers, text-with-numbers-case-insensitive, time, validator, value-type, year\",\n" +
                "  \"converters\": \"binary, binary-to-text, boolean, boolean-to-text, collection, collection-to, collection-to-list, color, color-to-number, csv, currency, currency-code-to-currency, currency-value-to, currency-value-to-number, date-time, date-time-symbols, decimal-number-symbols, environment, environment-to-binary, environment-to-text, error-throwing, error-to-error, error-to-number, expression, form-and-validation, format-pattern-to-string, json, json-to, locale, locale-to-text, logging, net, never, null-to-number, number, number-to-color, number-to-currency-value, number-to-number, number-to-text, optional-to, plugins, properties, properties-to-date-time-symbols, properties-to-decimal-number-symbols, properties-to-spreadsheet-metadata, properties-to-text-style, simple, spreadsheet-cell-set, spreadsheet-id-to-spreadsheet-metadata, spreadsheet-metadata, spreadsheet-metadata-to-text, spreadsheet-selection, spreadsheet-selection-to-spreadsheet-selection, spreadsheet-selection-to-text, storage, storage-binary-to-storage-value-binary, storage-binary-to-storage-value-csv, storage-binary-to-storage-value-environment, storage-binary-to-storage-value-expression, storage-binary-to-storage-value-json, storage-binary-to-storage-value-properties, storage-binary-to-storage-value-tsv, storage-binary-to-storage-value-txt, storage-value-info-list-to-text, storage-value-to-storage-binary-binary, storage-value-to-storage-binary-csv, storage-value-to-storage-binary-environment, storage-value-to-storage-binary-expression, storage-value-to-storage-binary-json, storage-value-to-storage-binary-properties, storage-value-to-storage-binary-tsv, storage-value-to-storage-binary-txt, style, system, template, text, text-node, text-to-binary, text-to-boolean-list, text-to-border, text-to-charset, text-to-color, text-to-converter-selector, text-to-csv-string-list, text-to-csv-string-set, text-to-currency, text-to-currency-code, text-to-currency-exchange-rater-selector, text-to-currency-value, text-to-date-list, text-to-date-time-list, text-to-email-address, text-to-environment, text-to-environment-value-name, text-to-error, text-to-expression, text-to-expression-function-selector, text-to-flag, text-to-form-name, text-to-has-host-address, text-to-host-address, text-to-indentation, text-to-json, text-to-json-pointer, text-to-json-selector, text-to-line-ending, text-to-locale, text-to-locale-language-tag, text-to-logging-level, text-to-margin, text-to-media-type, text-to-number-list, text-to-object, text-to-padding, text-to-path, text-to-properties, text-to-spreadsheet-comparator-selector, text-to-spreadsheet-formatter-selector, text-to-spreadsheet-id, text-to-spreadsheet-metadata, text-to-spreadsheet-metadata-color, text-to-spreadsheet-metadata-property-name, text-to-spreadsheet-name, text-to-spreadsheet-parser-selector, text-to-spreadsheet-selection, text-to-spreadsheet-text, text-to-storage-path, text-to-string-list, text-to-template-value-name, text-to-text, text-to-text-node, text-to-text-style, text-to-text-style-property-name, text-to-time-list, text-to-tsv-string-list, text-to-tsv-string-set, text-to-url, text-to-url-fragment, text-to-url-query-string, text-to-validation-error, text-to-validator-selector, text-to-value-type, text-to-web-color-name, text-to-zone-offset, to-binary, to-boolean, to-color, to-color-properties, to-csv-string-list, to-currency, to-currency-code, to-date-time-symbols, to-decimal-number-symbols, to-environment, to-formatter-selector, to-has-host-address, to-host-address, to-json-node, to-locale, to-locale-language-tag, to-multi-line-text, to-number, to-parser-selector, to-properties, to-spreadsheet-selection, to-style, to-styleable, to-text, to-text-node, to-tsv-string-list, to-validation-checkbox, to-validation-choice, to-validation-choice-list, to-validation-error-list, to-validator-selector, to-value, to-value-type, tsv, url, url-to-hyperlink, url-to-image, value\",\n" +
                "  \"currency\": \"AUD\",\n" +
                "  \"dateFormatter\": \"date yyyy/mm/dd\",\n" +
                "  \"dateOffset\": \"-25569\",\n" +
                "  \"dateParser\": \"date yyyy/mm/dd\",\n" +
                "  \"dateTimeFormatter\": \"date-time yyyy/mm/dd hh:mm\",\n" +
                "  \"dateTimeParser\": \"date-time yyyy/mm/dd hh:mm\",\n" +
                "  \"dateTimeSymbols\": {\n" +
                "    \"ampms\": [\n" +
                "      \"am\",\n" +
                "      \"pm\"\n" +
                "    ],\n" +
                "    \"monthNames\": [\n" +
                "      \"January\",\n" +
                "      \"February\",\n" +
                "      \"March\",\n" +
                "      \"April\",\n" +
                "      \"May\",\n" +
                "      \"June\",\n" +
                "      \"July\",\n" +
                "      \"August\",\n" +
                "      \"September\",\n" +
                "      \"October\",\n" +
                "      \"November\",\n" +
                "      \"December\"\n" +
                "    ],\n" +
                "    \"monthNameAbbreviations\": [\n" +
                "      \"Jan.\",\n" +
                "      \"Feb.\",\n" +
                "      \"Mar.\",\n" +
                "      \"Apr.\",\n" +
                "      \"May\",\n" +
                "      \"Jun.\",\n" +
                "      \"Jul.\",\n" +
                "      \"Aug.\",\n" +
                "      \"Sep.\",\n" +
                "      \"Oct.\",\n" +
                "      \"Nov.\",\n" +
                "      \"Dec.\"\n" +
                "    ],\n" +
                "    \"weekDayNames\": [\n" +
                "      \"Sunday\",\n" +
                "      \"Monday\",\n" +
                "      \"Tuesday\",\n" +
                "      \"Wednesday\",\n" +
                "      \"Thursday\",\n" +
                "      \"Friday\",\n" +
                "      \"Saturday\"\n" +
                "    ],\n" +
                "    \"weekDayNameAbbreviations\": [\n" +
                "      \"Sun.\",\n" +
                "      \"Mon.\",\n" +
                "      \"Tue.\",\n" +
                "      \"Wed.\",\n" +
                "      \"Thu.\",\n" +
                "      \"Fri.\",\n" +
                "      \"Sat.\"\n" +
                "    ]\n" +
                "  },\n" +
                "  \"decimalNumberDigitCount\": 8,\n" +
                "  \"decimalNumberSymbols\": {\n" +
                "    \"negativeSign\": \"-\",\n" +
                "    \"positiveSign\": \"+\",\n" +
                "    \"zeroDigit\": \"0\",\n" +
                "    \"currencySymbol\": \"$\",\n" +
                "    \"decimalSeparator\": \".\",\n" +
                "    \"exponentSymbol\": \"e\",\n" +
                "    \"groupSeparator\": \",\",\n" +
                "    \"infinitySymbol\": \"∞\",\n" +
                "    \"monetaryDecimalSeparator\": \".\",\n" +
                "    \"nanSymbol\": \"NaN\",\n" +
                "    \"percentSymbol\": \"%\",\n" +
                "    \"permillSymbol\": \"‰\"\n" +
                "  },\n" +
                "  \"defaultFormHandler\": \"basic\",\n" +
                "  \"defaultYear\": 2000,\n" +
                "  \"errorFormatter\": \"badge-error text @\",\n" +
                "  \"exporters\": \"collection, empty, json\",\n" +
                "  \"expressionNumberKind\": \"BIG_DECIMAL\",\n" +
                "  \"formatters\": \"accounting, automatic, badge-error, collection, currency, date, date-time, default-text, expression, full-date, full-date-time, full-time, general, hyperlinking, long-date, long-date-time, long-time, medium-date, medium-date-time, medium-time, number, percent, scientific, short-date, short-date-time, short-time, text, time\",\n" +
                "  \"formattingConverter\": \"collection(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, json, currency, logging, plugins, properties, spreadsheet-metadata, storage, style, text-node, template, net, binary, optional-to, collection-to)\",\n" +
                "  \"formattingCurrencyExchangeRater\": \"storage-properties(\\\"/samples/CurrencyExchangeRates.properties\\\")\",\n" +
                "  \"formattingFunctions\": \"@\",\n" +
                "  \"formHandlers\": \"\",\n" +
                "  \"formulaConverter\": \"collection(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, json, currency, logging, plugins, properties, spreadsheet-metadata, storage, style, text-node, template, net, binary, optional-to, collection-to)\",\n" +
                "  \"formulaCurrencyExchangeRater\": \"storage-properties(\\\"/samples/CurrencyExchangeRates.properties\\\")\",\n" +
                "  \"formulaFunctions\": \"@\",\n" +
                "  \"functions\": \"@\",\n" +
                "  \"importers\": \"collection, empty, json\",\n" +
                "  \"locale\": \"en-AU\",\n" +
                "  \"numberFormatter\": \"number 0.#;0.#;0\",\n" +
                "  \"numberParser\": \"number 0.#;0.#;0\",\n" +
                "  \"parsers\": \"date, date-time, full-date, full-date-time, full-time, general, long-date, long-date-time, long-time, medium-date, medium-date-time, medium-time, number, short-date, short-date-time, short-time, time, whole-number\",\n" +
                "  \"plugins\": \"\",\n" +
                "  \"precision\": 7,\n" +
                "  \"queryConverter\": \"collection(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, properties, spreadsheet-metadata, style, text-node, template, net, binary, optional-to, collection-to)\",\n" +
                "  \"queryFunctions\": \"@\",\n" +
                "  \"roundingMode\": \"HALF_UP\",\n" +
                "  \"scriptingConverter\": \"collection(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, json, currency, logging, plugins, properties, spreadsheet-metadata, storage, style, text-node, template, net, binary, optional-to, collection-to)\",\n" +
                "  \"scriptingCurrencyExchangeRater\": \"storage-properties(\\\"/samples/CurrencyExchangeRates.properties\\\")\",\n" +
                "  \"scriptingFunctions\": \"@\",\n" +
                "  \"showFormulaEditor\": true,\n" +
                "  \"showFormulas\": false,\n" +
                "  \"showGridLines\": true,\n" +
                "  \"showHeadings\": true,\n" +
                "  \"sortComparators\": \"background-color, border-bottom-color, border-color, border-left-color, border-right-color, border-top-color, color, currency, custom-list, custom-list-case-insensitive, date, datetime, day-of-month, day-of-year, formatter, hour-of-ampm, hour-of-day, locale, minute-of-hour, month-of-year, nano-of-second, number, outline-color, parser, seconds-of-minute, text, text-case-insensitive, time, validator, value-type, year\",\n" +
                "  \"sortConverter\": \"collection(null-to-number, simple, text, boolean, number, date-time, locale, value, optional-to, collection-to)\",\n" +
                "  \"style\": {\n" +
                "    \"height\": \"50px\",\n" +
                "    \"width\": \"100px\"\n" +
                "  },\n" +
                "  \"textFormatter\": \"text @\",\n" +
                "  \"timeFormatter\": \"time hh:mm:ss\",\n" +
                "  \"timeParser\": \"time hh:mm:ss\",\n" +
                "  \"twoDigitYear\": 50,\n" +
                "  \"validationConverter\": \"collection(null-to-number, simple, text, boolean, number, date-time, environment, value, error-throwing, expression, form-and-validation, locale, logging, plugins, properties, template, json, optional-to, collection-to)\",\n" +
                "  \"validationCurrencyExchangeRater\": \"storage-properties(\\\"/samples/CurrencyExchangeRates.properties\\\")\",\n" +
                "  \"validationFunctions\": \"@\",\n" +
                "  \"validationValidators\": \"absolute-url, checkbox, choice-list, collection, email-address, expression, non-null, text-length, text-mask\",\n" +
                "  \"validators\": \"absolute-url, checkbox, choice-list, collection, email-address, expression, non-null, text-length, text-mask\",\n" +
                "  \"valueSeparator\": \",\"\n" +
                "} to java.lang.String\n" +
                "converter.verify DEBUG from java.lang.String: \"123\" to walkingkooka.spreadsheet.meta.SpreadsheetId\n" +
                "converter.verify DEBUG from java.lang.String: \"{\\n  \\\"spreadsheetId\\\": \\\"123\\\"\\n}\" to walkingkooka.spreadsheet.meta.SpreadsheetMetadata\n" +
                "converter.verify DEBUG from java.lang.String: \"SpreadsheetName123\" to walkingkooka.spreadsheet.meta.SpreadsheetName\n" +
                "converter.verify DEBUG from java.lang.String: \"spreadsheetName\" to walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName\n" +
                "converter.verify DEBUG from walkingkooka.props.Properties: spreadsheetId=123\n" +
                " to walkingkooka.spreadsheet.meta.SpreadsheetMetadata\n" +
                "converter.verify DEBUG from java.lang.String: \"/tmp/File1.txt\" to java.nio.file.Path\n" +
                "converter.verify DEBUG from walkingkooka.Binary: 48 65 6c 6c 6f 20 57 6f 72 6c 64 20 42 69 6e 61 72 79       Hello World Binary  \n" +
                " to walkingkooka.Binary\n" +
                "converter.verify DEBUG from java.lang.String: \"BinaryTextContent123\" to walkingkooka.Binary\n" +
                "converter.verify DEBUG from walkingkooka.Binary: 42 69 6e 61 72 79 54 65 78 74 43 6f 6e 74 65 6e 74 31 32 33 BinaryTextContent123\n" +
                " to java.lang.String\n" +
                "converter.verify DEBUG from walkingkooka.storage.StorageBinary: /file.bin 00 01 02                                                    ???                 \n" +
                " to walkingkooka.storage.StorageValue\n" +
                "converter.verify DEBUG from walkingkooka.storage.StorageValue: /file.bin=00 01 02                                                    ???                 \n" +
                " to walkingkooka.storage.StorageBinary\n" +
                "converter.verify DEBUG from walkingkooka.storage.StorageBinary: /file.csv 41 70 70 6c 65 20 31 2c 42 61 6e 61 6e 61 20 32             Apple 1,Banana 2    \n" +
                " to walkingkooka.storage.StorageValue\n" +
                "converter.verify DEBUG from walkingkooka.storage.StorageValue: /file.csv=\"Apple 1\", \"Banana 2\" to walkingkooka.storage.StorageBinary\n" +
                "converter.verify DEBUG from walkingkooka.storage.StorageValue: /EnvironmentContext.env=\"charset=UTF-8\n" +
                "currency=AUD\n" +
                "indentation=\"  \"\n" +
                "lineEnding=NL\n" +
                "locale=en-AU\" to walkingkooka.storage.StorageBinary\n" +
                "converter.verify DEBUG from walkingkooka.storage.StorageBinary: /EnvironmentContext.env 63 68 61 72 73 65 74 3d 55 54 46 2d 38 0a 63 75 72 72 65 6e charset=UTF-8?curren\n" +
                "63 79 3d 41 55 44 0a 69 6e 64 65 6e 74 61 74 69 6f 6e 3d 22 cy=AUD?indentation=\"\n" +
                "20 20 22 0a 6c 69 6e 65 45 6e 64 69 6e 67 3d 4e 4c 0a 6c 6f   \"?lineEnding=NL?lo\n" +
                "63 61 6c 65 3d 65 6e 2d 41 55                               cale=en-AU          \n" +
                " to walkingkooka.storage.StorageValue\n" +
                "converter.verify DEBUG from walkingkooka.storage.StorageValue: /formula.expression.txt=\"1+2\" to walkingkooka.storage.StorageBinary\n" +
                "converter.verify DEBUG from walkingkooka.storage.StorageBinary: /formula.expression.txt 31 2b 32                                                    1+2                 \n" +
                " to walkingkooka.storage.StorageValue\n" +
                "converter.verify DEBUG from walkingkooka.storage.StorageBinary: /file1/file2.json application/json7b 0a 20 20 22 68 65 6c 6c 6f 22 3a 20 22 77 6f 72 6c 64 22 {?  \"hello\": \"world\"\n" +
                "0a 7d                                                       ?}                  \n" +
                " to walkingkooka.storage.StorageValue\n" +
                "converter.verify DEBUG from walkingkooka.storage.StorageValue: /file1/file2.json={\n" +
                "  \"hello\": \"world\"\n" +
                "} application/json to walkingkooka.storage.StorageBinary\n" +
                "converter.verify DEBUG from walkingkooka.storage.StorageBinary: /file1/file2.properties 68 65 6c 6c 6f 3d 77 6f 72 6c 64 0d 0a                      hello=world??       \n" +
                " to walkingkooka.storage.StorageValue\n" +
                "converter.verify DEBUG from walkingkooka.storage.StorageValue: /file1/file2.properties=hello=world\n" +
                " to walkingkooka.storage.StorageBinary\n" +
                "converter.verify DEBUG from walkingkooka.storage.StorageBinary: /file.tsv 41 70 70 6c 65 20 31 09 42 61 6e 61 6e 61 20 32             Apple 1?Banana 2    \n" +
                " to walkingkooka.storage.StorageValue\n" +
                "converter.verify DEBUG from walkingkooka.storage.StorageValue: /file.tsv=\"Apple 1\", \"Banana 2\" to walkingkooka.storage.StorageBinary\n" +
                "converter.verify DEBUG from walkingkooka.storage.StorageBinary: /file1.file2.txt 48 65 6c 6c 6f 20 57 6f 72 6c 64 20 54 65 78 74 20 31 32 33 Hello World Text 123\n" +
                " to walkingkooka.storage.StorageValue\n" +
                "converter.verify DEBUG from walkingkooka.storage.StorageValue: /file1.file2.txt=\"Hello World Text 123\" to walkingkooka.storage.StorageBinary\n" +
                "converter.verify DEBUG from walkingkooka.storage.StorageValue: /file.bin=48 65 6c 6c 6f 20 57 6f 72 6c 64 20 42 69 6e 61 72 79       Hello World Binary  \n" +
                " to walkingkooka.storage.StorageBinary\n" +
                "converter.verify DEBUG from walkingkooka.storage.StorageValueInfoList: [/path1/file2.txt user@example.com 1999-12-31T12:58 user@example.com 1999-12-31T12:58] to java.lang.String\n" +
                "converter.verify DEBUG from java.lang.String: \"/path1/file2.txt\" to walkingkooka.storage.StoragePath\n" +
                "converter.verify DEBUG from walkingkooka.tree.text.TextStyleNonEmpty: {color=black} to walkingkooka.tree.text.TextStyle\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.value.SpreadsheetCell: A1 style={color=black} to walkingkooka.tree.text.TextStyle\n" +
                "converter.verify DEBUG from walkingkooka.props.Properties: color=black\n" +
                " to walkingkooka.tree.text.TextStyle\n" +
                "converter.verify DEBUG from java.lang.String: \"color=black\\r\\n\" to walkingkooka.tree.text.TextStyle\n" +
                "converter.verify DEBUG from java.lang.String: \"black SOLID 1px\" to walkingkooka.tree.text.Border\n" +
                "converter.verify DEBUG from java.lang.String: \"1px\" to walkingkooka.tree.text.Margin\n" +
                "converter.verify DEBUG from java.lang.String: \"1px\" to walkingkooka.tree.text.Padding\n" +
                "converter.verify DEBUG from java.lang.String: \"Text123\" to walkingkooka.spreadsheet.format.SpreadsheetText\n" +
                "converter.verify DEBUG from java.lang.String: \"color: black;\" to walkingkooka.tree.text.TextStyle\n" +
                "converter.verify DEBUG from java.lang.String: \"background-color\" to walkingkooka.tree.text.TextStylePropertyName\n" +
                "converter.verify DEBUG from java.lang.Character: 'A' to java.lang.String\n" +
                "converter.verify DEBUG from java.lang.String: \"Text\" to java.lang.String\n" +
                "converter.verify DEBUG from walkingkooka.net.AbsoluteUrl: https://example.com/123 to java.lang.String\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.formula.SpreadsheetFormula: \"=1+2+3\" valueType=text/Text to java.lang.String\n" +
                "converter.verify DEBUG from java.lang.String: \"TRUE, FALSE, true\" to walkingkooka.collect.list.BooleanList\n" +
                "converter.verify DEBUG from java.lang.String: \"Apple 1,Banana 2\" to walkingkooka.collect.list.CsvStringList\n" +
                "converter.verify DEBUG from java.lang.String: \"1999/12/31\" to walkingkooka.datetime.LocalDateList\n" +
                "converter.verify DEBUG from java.lang.String: \"1999/12/31 12:58\" to walkingkooka.datetime.LocalDateTimeList\n" +
                "converter.verify DEBUG from java.lang.String: \"1, 22, 333.5\" to walkingkooka.math.NumberList\n" +
                "converter.verify DEBUG from java.lang.String: \"Apple 1, Banana 2\" to walkingkooka.collect.list.StringList\n" +
                "converter.verify DEBUG from java.lang.String: \"12:58:59\" to walkingkooka.datetime.LocalTimeList\n" +
                "converter.verify DEBUG from java.lang.String: \"Apple 1\\tBanana 2\" to walkingkooka.collect.list.TsvStringList\n" +
                "converter.verify DEBUG from java.lang.String: \"Apple 1\\tBanana 2\" to walkingkooka.collect.set.TsvStringSet\n" +
                "converter.verify DEBUG from java.lang.String: \"AU\" to walkingkooka.tree.text.Flag\n" +
                "converter.verify DEBUG from java.lang.String: \"https://example.com/123\" to walkingkooka.tree.text.Hyperlink\n" +
                "converter.verify DEBUG from java.lang.String: \"https://example.com/123\" to walkingkooka.tree.text.Image\n" +
                "converter.verify DEBUG from java.lang.String: \"https://example.com/123\" to walkingkooka.tree.text.Hyperlink\n" +
                "converter.verify DEBUG from java.lang.String: \"https://example.com/123\" to walkingkooka.tree.text.Image\n" +
                "converter.verify DEBUG from walkingkooka.tree.text.TextStyleNode: {color: black}[\"Text123\"] to walkingkooka.tree.text.Styleable\n" +
                "converter.verify DEBUG from walkingkooka.tree.text.TextStyleNonEmpty: {color=black} to walkingkooka.tree.text.Styleable\n" +
                "converter.verify DEBUG from java.lang.Character: 'A' to walkingkooka.tree.text.TextNode\n" +
                "converter.verify DEBUG from java.lang.String: \"Text123\" to walkingkooka.tree.text.TextNode\n" +
                "converter.verify DEBUG from walkingkooka.tree.text.Text: \"Text123\" to walkingkooka.tree.text.TextNode\n" +
                "converter.verify DEBUG from java.lang.String: \"https://example.com/123\" to walkingkooka.tree.text.Hyperlink\n" +
                "converter.verify DEBUG from java.lang.String: \"https://example.com/123\" to walkingkooka.tree.text.Image\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.value.SpreadsheetError: #NAME? \"Cell not found: \"A1\"\" A1 to walkingkooka.spreadsheet.value.SpreadsheetError\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.value.SpreadsheetError: #NAME? \"Cell not found: \"A1\"\" A1 to walkingkooka.tree.expression.ExpressionNumber\n" +
                "converter.verify DEBUG from null to walkingkooka.tree.expression.ExpressionNumber\n" +
                "converter.verify DEBUG from null to java.lang.Byte\n" +
                "converter.verify DEBUG from null to java.lang.Short\n" +
                "converter.verify DEBUG from null to java.lang.Integer\n" +
                "converter.verify DEBUG from null to java.lang.Long\n" +
                "converter.verify DEBUG from null to java.lang.Float\n" +
                "converter.verify DEBUG from null to java.lang.Double\n" +
                "converter.verify DEBUG from null to java.math.BigInteger\n" +
                "converter.verify DEBUG from null to java.math.BigDecimal\n" +
                "converter.verify DEBUG from java.lang.Integer: 1 to walkingkooka.tree.expression.ExpressionNumber\n" +
                "converter.verify DEBUG from java.lang.Integer: 1 to java.lang.Byte\n" +
                "converter.verify DEBUG from java.lang.Integer: 1 to java.lang.Short\n" +
                "converter.verify DEBUG from java.lang.Integer: 1 to java.lang.Integer\n" +
                "converter.verify DEBUG from java.lang.Integer: 1 to java.lang.Long\n" +
                "converter.verify DEBUG from java.lang.Integer: 1 to java.lang.Float\n" +
                "converter.verify DEBUG from java.lang.Integer: 1 to java.lang.Double\n" +
                "converter.verify DEBUG from java.lang.Integer: 1 to java.math.BigInteger\n" +
                "converter.verify DEBUG from java.lang.Integer: 1 to java.math.BigDecimal\n" +
                "converter.verify DEBUG from java.lang.Double: 1.0 to walkingkooka.tree.expression.ExpressionNumber\n" +
                "converter.verify DEBUG from java.lang.Double: 1.0 to java.lang.Byte\n" +
                "converter.verify DEBUG from java.lang.Double: 1.0 to java.lang.Short\n" +
                "converter.verify DEBUG from java.lang.Double: 1.0 to java.lang.Integer\n" +
                "converter.verify DEBUG from java.lang.Double: 1.0 to java.lang.Long\n" +
                "converter.verify DEBUG from java.lang.Double: 1.0 to java.lang.Float\n" +
                "converter.verify DEBUG from java.lang.Double: 1.0 to java.lang.Double\n" +
                "converter.verify DEBUG from java.lang.Double: 1.0 to java.math.BigInteger\n" +
                "converter.verify DEBUG from java.lang.Double: 1.0 to java.math.BigDecimal\n" +
                "converter.verify DEBUG from walkingkooka.tree.json.JsonObject: {\n" +
                "  \"hello\": \"world\"\n" +
                "} to walkingkooka.text.MultiLineText\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.reference.SpreadsheetCellReference: A1 to walkingkooka.spreadsheet.reference.SpreadsheetCellReference\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference: B2:C3 to walkingkooka.spreadsheet.reference.SpreadsheetCellReference\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.reference.SpreadsheetCellReference: A1 to walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference: B2:C3 to walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.reference.SpreadsheetColumnReference: A to walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference: B:C to walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.reference.SpreadsheetRowReference: 1 to walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference: 2:3 to walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.reference.SpreadsheetColumnReference: A to walkingkooka.spreadsheet.reference.SpreadsheetColumnReference\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.reference.SpreadsheetColumnReference: A to walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference: B:C to walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.reference.SpreadsheetRowReference: 1 to walkingkooka.spreadsheet.reference.SpreadsheetRowReference\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference: 2:3 to walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.reference.SpreadsheetCellReference: A1 to java.lang.String\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference: B2:C3 to java.lang.String\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.reference.SpreadsheetColumnReference: A to java.lang.String\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference: B:C to java.lang.String\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.reference.SpreadsheetRowReference: 1 to java.lang.String\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference: 2:3 to java.lang.String\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.reference.SpreadsheetLabelName: Label123 to java.lang.String\n" +
                "converter.verify DEBUG from java.lang.Character: ' ' to java.lang.Character\n" +
                "converter.verify DEBUG from java.lang.Character: ' ' to java.lang.String\n" +
                "converter.verify DEBUG from java.lang.String: \"A\" to java.lang.Character\n" +
                "converter.verify DEBUG from java.lang.String: \"A\" to java.lang.String\n" +
                "converter.verify DEBUG from java.lang.String: \"UTF-8\" to java.nio.charset.Charset\n" +
                "converter.verify DEBUG from java.lang.String: \"  \" to walkingkooka.text.Indentation\n" +
                "converter.verify DEBUG from java.lang.String: \"    \" to walkingkooka.text.Indentation\n" +
                "converter.verify DEBUG from walkingkooka.text.Indentation: \"  \" to walkingkooka.text.Indentation\n" +
                "converter.verify DEBUG from java.lang.String: \"\\r\\n\" to walkingkooka.text.LineEnding\n" +
                "converter.verify DEBUG from java.lang.String: \"crnl\" to walkingkooka.text.LineEnding\n" +
                "converter.verify DEBUG from java.lang.String: \"\\n\" to walkingkooka.text.LineEnding\n" +
                "converter.verify DEBUG from java.lang.String: \"A1\" to walkingkooka.spreadsheet.reference.SpreadsheetCellReference\n" +
                "converter.verify DEBUG from java.lang.String: \"B2:C3\" to walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference\n" +
                "converter.verify DEBUG from java.lang.String: \"A\" to walkingkooka.spreadsheet.reference.SpreadsheetColumnReference\n" +
                "converter.verify DEBUG from java.lang.String: \"B:C\" to walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference\n" +
                "converter.verify DEBUG from java.lang.String: \"Label123\" to walkingkooka.spreadsheet.reference.SpreadsheetLabelName\n" +
                "converter.verify DEBUG from java.lang.String: \"A1\" to walkingkooka.spreadsheet.reference.SpreadsheetCellReference\n" +
                "converter.verify DEBUG from java.lang.String: \"A1\" to walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference\n" +
                "converter.verify DEBUG from java.lang.String: \"B2:C3\" to walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference\n" +
                "converter.verify DEBUG from java.lang.String: \"Label123\" to walkingkooka.spreadsheet.reference.SpreadsheetLabelName\n" +
                "converter.verify DEBUG from java.lang.String: \"A\" to walkingkooka.spreadsheet.reference.SpreadsheetColumnReference\n" +
                "converter.verify DEBUG from java.lang.String: \"A\" to walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference\n" +
                "converter.verify DEBUG from java.lang.String: \"A\" to walkingkooka.spreadsheet.reference.SpreadsheetColumnReference\n" +
                "converter.verify DEBUG from java.lang.String: \"B:C\" to walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference\n" +
                "converter.verify DEBUG from java.lang.String: \"1\" to walkingkooka.spreadsheet.reference.SpreadsheetRowReference\n" +
                "converter.verify DEBUG from java.lang.String: \"1\" to walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference\n" +
                "converter.verify DEBUG from java.lang.String: \"1\" to walkingkooka.spreadsheet.reference.SpreadsheetRowReference\n" +
                "converter.verify DEBUG from java.lang.String: \"2:3\" to walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference\n" +
                "converter.verify DEBUG from java.lang.String: \"+12:59\" to java.time.ZoneOffset\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.value.SpreadsheetCell: A1 \"=1+2+3\" valueType=text/Text currencyExchangeRater=\"test-currency-exchange-rater\" dateTimeSymbols=\"ampms=\"am\", \"pm\" monthNames=\"January\", \"February\", \"March\", \"April\", \"May\", \"June\", \"July\", \"August\", \"September\", \"October\", \"November\", \"December\" monthNameAbbreviations=\"Jan.\", \"Feb.\", \"Mar.\", \"Apr.\", \"May\", \"Jun.\", \"Jul.\", \"Aug.\", \"Sep.\", \"Oct.\", \"Nov.\", \"Dec.\" weekDayNames=\"Sunday\", \"Monday\", \"Tuesday\", \"Wednesday\", \"Thursday\", \"Friday\", \"Saturday\" weekDayNameAbbreviations=\"Sun.\", \"Mon.\", \"Tue.\", \"Wed.\", \"Thu.\", \"Fri.\", \"Sat.\"\" decimalNumberSymbols=\"negativeSign='-' positiveSign='+' zeroDigit='0' currencySymbol=\"$\" decimalSeparator='.' exponentSymbol=\"e\" groupSeparator=',' infinitySymbol=\"∞\" monetaryDecimalSeparator='.' nanSymbol=\"NaN\" percentSymbol='%' permillSymbol='‰'\" formatter=\"test-formatter\" locale=\"en_AU\" parser=\"test-parser\" style={color=black} validator=\"test-validator\" to walkingkooka.validation.ValueType\n" +
                "converter.verify DEBUG from walkingkooka.environment.AuditInfo: user@example.com 1999-12-31T12:58 user@example.com 1999-12-31T12:58 to java.lang.String\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.value.SpreadsheetCell: A1 \"=1+2+3\" valueType=text/Text currencyExchangeRater=\"test-currency-exchange-rater\" dateTimeSymbols=\"ampms=\"am\", \"pm\" monthNames=\"January\", \"February\", \"March\", \"April\", \"May\", \"June\", \"July\", \"August\", \"September\", \"October\", \"November\", \"December\" monthNameAbbreviations=\"Jan.\", \"Feb.\", \"Mar.\", \"Apr.\", \"May\", \"Jun.\", \"Jul.\", \"Aug.\", \"Sep.\", \"Oct.\", \"Nov.\", \"Dec.\" weekDayNames=\"Sunday\", \"Monday\", \"Tuesday\", \"Wednesday\", \"Thursday\", \"Friday\", \"Saturday\" weekDayNameAbbreviations=\"Sun.\", \"Mon.\", \"Tue.\", \"Wed.\", \"Thu.\", \"Fri.\", \"Sat.\"\" decimalNumberSymbols=\"negativeSign='-' positiveSign='+' zeroDigit='0' currencySymbol=\"$\" decimalSeparator='.' exponentSymbol=\"e\" groupSeparator=',' infinitySymbol=\"∞\" monetaryDecimalSeparator='.' nanSymbol=\"NaN\" percentSymbol='%' permillSymbol='‰'\" formatter=\"test-formatter\" locale=\"en_AU\" parser=\"test-parser\" style={color=black} validator=\"test-validator\" to java.lang.String\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.value.SpreadsheetError: #NAME? \"Cell not found: \"A1\"\" A1 to java.lang.String\n" +
                "converter.verify DEBUG from walkingkooka.spreadsheet.meta.SpreadsheetId: 1 to java.lang.String\n" +
                "converter.verify DEBUG from walkingkooka.storage.StoragePath: /path1/file2.txt to java.lang.String\n" +
                "converter.verify DEBUG from walkingkooka.storage.StorageValue: /path1/file2.txt=\"StorageValue123\" to java.lang.String\n" +
                "converter.verify DEBUG from walkingkooka.storage.StorageValueInfo: /path1/file2.txt user@example.com 1999-12-31T12:58 user@example.com 1999-12-31T12:58 to java.lang.String\n" +
                "converter.verify DEBUG from walkingkooka.tree.text.TextStyleNonEmpty: {color=black} to java.lang.String\n" +
                "converter.verify DEBUG from java.time.ZoneOffset: Z to java.lang.String\n",
                "converter.verify INFO formulaConverter: never 56=[binary 48 65 6c 6c 6f 20 57 6f 72 6c 64 20 42 69 6e 61 72 79       Hello World Binary  \n" +
                " walkingkooka.Binary, \"BinaryTextContent123\" walkingkooka.Binary, 42 69 6e 61 72 79 54 65 78 74 43 6f 6e 74 65 6e 74 31 32 33 BinaryTextContent123\n" +
                " java.lang.String, boolean 0 java.lang.Boolean, 0 java.lang.Boolean, \"FALSE\" java.lang.Boolean, 1 java.lang.Boolean, 1 java.lang.Boolean, \"TRUE\" java.lang.Boolean, \"TRUE\" java.lang.String, \"FALSE\" java.lang.String, collection-to [false] java.lang.Boolean, [true] java.lang.Boolean, [123] walkingkooka.tree.expression.ExpressionNumber, [123] java.lang.Byte, [123] java.lang.Short, [123] java.lang.Integer, [123] java.lang.Long, [123] java.lang.Float, [123] java.lang.Double, [123] java.math.BigInteger, [123] java.math.BigDecimal, [1999-12-31] java.time.LocalDate, [1999-12-31T12:58] java.time.LocalDateTime, [\"Hello\"] java.lang.String, [12:58] java.time.LocalTime, collection-to-list [true, false, true] walkingkooka.collect.list.BooleanList, [\"Apple 1\", \"Banana 2\"] walkingkooka.collect.list.CsvStringList, [1999-12-31, null] walkingkooka.datetime.LocalDateList, [1999-12-31T12:58, null] walkingkooka.datetime.LocalDateTimeList, [12:58, null] walkingkooka.datetime.LocalTimeList, [1, 22, 333.5, null] walkingkooka.math.NumberList, [\"Apple 1\", \"Banana 2\"] walkingkooka.collect.list.StringList, color black walkingkooka.color.HslColor, black walkingkooka.color.HsvColor, black walkingkooka.color.RgbColor, black walkingkooka.tree.expression.ExpressionNumber, black java.lang.Integer, black java.lang.Long, black java.lang.Float, black java.lang.Double, black java.math.BigInteger, black java.math.BigDecimal, 78 walkingkooka.tree.expression.ExpressionNumber, 78 java.lang.Byte, 78 java.lang.Short, 78 java.lang.Integer, 78 java.lang.Long, 78 java.lang.Float, 78 java.lang.Double, 78 java.math.BigInteger, 78 java.math.BigDecimal, 12 walkingkooka.tree.expression.ExpressionNumber, 12 java.lang.Byte, 12 java.lang.Short, 12 java.lang.Integer, 12 java.lang.Long, 12 java.lang.Float, 12 java.lang.Double, 12 java.math.BigInteger, 12 java.math.BigDecimal, 34 walkingkooka.tree.expression.ExpressionNumber, 34 java.lang.Byte, 34 java.lang.Short, 34 java.lang.Integer, 34 java.lang.Long, 34 java.lang.Float,, currency \"AUD\" walkingkooka.currency.CurrencyCode, \"AUD\" java.util.Currency, 1 AUD walkingkooka.tree.expression.ExpressionNumber, 1 AUD java.lang.Byte, 1 AUD java.lang.Short, 1 AUD java.lang.Integer, 1 AUD java.lang.Long, 1 AUD java.lang.Float, 1 AUD java.lang.Double, 1 AUD java.math.BigInteger, 1 AUD java.math.BigDecimal, 2 AUD walkingkooka.tree.expression.ExpressionNumber, 2 AUD java.lang.Byte, 2 AUD java.lang.Short, 2 AUD java.lang.Integer, 2 AUD java.lang.Long, 2 AUD java.lang.Float, 2 AUD java.lang.Double, 2 AUD java.math.BigInteger, 2 AUD java.math.BigDecimal, 3.0 AUD walkingkooka.tree.expression.ExpressionNumber, 3.0 AUD java.lang.Byte, 3.0 AUD java.lang.Short, 3.0 AUD java.lang.Integer, 3.0 AUD java.lang.Long, 3.0 AUD java.lang.Float, 3.0 AUD java.lang.Double, 3.0 AUD java.math.BigInteger, 3.0 AUD java.math.BigDecimal, 4.0 AUD walkingkooka.tree.expression.ExpressionNumber, 4.0 AUD java.l, date-time 1999-12-31 java.lang.Boolean, 1999-12-31 java.time.LocalDateTime, 1999-12-31 java.math.BigDecimal, 1999-12-31 java.math.BigInteger, 1999-12-31 java.lang.Double, 1999-12-31 java.lang.Float, 1999-12-31 java.lang.Long, 1999-12-31 walkingkooka.tree.expression.ExpressionNumber, 1999-12-31 java.lang.String, 1999-12-31T12:58 java.lang.Boolean, 1999-12-31T00:00 java.time.LocalDate, 1999-12-31T12:58 java.math.BigDecimal, 1999-12-31T00:00 java.math.BigInteger, 1999-12-31T12:58 java.lang.Double, 1999-12-31T12:58 java.lang.Float, 1999-12-31T00:00 java.lang.Long, 1999-12-31T00:00 walkingkooka.tree.expression.ExpressionNumber, 1999-12-31T12:58 java.lang.String, \"123\" walkingkooka.tree.expression.ExpressionNumber, \"123\" java.lang.Byte, \"123\" java.lang.Short, \"123\" java.lang.Integer, \"123\" java.lang.Long, \"123\" java.lang.Float, \"123\" java.lang.Double, \"123\" java.math.BigInteger, \"123\" java.math.BigDeci, date-time-symbols en_AU walkingkooka.datetime.DateTimeSymbols, A1 \"=1+2+3\" valueType=text/Text currencyExchangeRater=\"test-currency-exchange-rater\" dateTimeSymbols=\"ampms=\"am\", \"pm\" monthNames=\"January\", \"February\", \"March\", \"April\", \"May\", \"June\", \"July\", \"August\", \"September\", \"October\", \"November\", \"December\" monthNameAbbreviations=\"Jan.\", \"Feb.\", \"Mar.\", \"Apr.\", \"May\", \"Jun.\", \"Jul.\", \"Aug.\", \"Sep.\", \"Oct.\", \"Nov.\", \"Dec.\" weekDayNames=\"Sunday\", \"Monday\", \"Tuesday\", \"Wednesday\", \"Thursday\", \"Friday\", \"Saturday\" weekDayNameAbbreviations=\"Sun.\", \"Mon.\", \"Tue.\", \"Wed.\", \"Thu.\", \"Fri.\", \"Sat.\"\" decimalNumberSymbols=\"negativeSign='-' positiveSign='+' zeroDigit='0' currencySymbol=\"$\" decimalSeparator='.' exponentSymbol=\"e\" groupSeparator=',' infinitySymbol=\"∞\" monetaryDecimalSeparator='.' nanSymbol=\"NaN\" percentSymbol='%' permillSymbol='‰'\" formatter=\"test-formatter\" locale=\"en_AU\" parser=\"test-parser\" sty, decimal-number-symbols A1 \"=1+2+3\" valueType=text/Text currencyExchangeRater=\"test-currency-exchange-rater\" dateTimeSymbols=\"ampms=\"am\", \"pm\" monthNames=\"January\", \"February\", \"March\", \"April\", \"May\", \"June\", \"July\", \"August\", \"September\", \"October\", \"November\", \"December\" monthNameAbbreviations=\"Jan.\", \"Feb.\", \"Mar.\", \"Apr.\", \"May\", \"Jun.\", \"Jul.\", \"Aug.\", \"Sep.\", \"Oct.\", \"Nov.\", \"Dec.\" weekDayNames=\"Sunday\", \"Monday\", \"Tuesday\", \"Wednesday\", \"Thursday\", \"Friday\", \"Saturday\" weekDayNameAbbreviations=\"Sun.\", \"Mon.\", \"Tue.\", \"Wed.\", \"Thu.\", \"Fri.\", \"Sat.\"\" decimalNumberSymbols=\"negativeSign='-' positiveSign='+' zeroDigit='0' currencySymbol=\"$\" decimalSeparator='.' exponentSymbol=\"e\" groupSeparator=',' infinitySymbol=\"∞\" monetaryDecimalSeparator='.' nanSymbol=\"NaN\" percentSymbol='%' permillSymbol='‰'\" formatter=\"test-formatter\" locale=\"en_AU\" parser=\"test-parser\" style={color=black} validator=\"test-validator\" w, environment {} walkingkooka.environment.Environment, {charset=UTF-8, currency=AUD} java.lang.String, {charset=UTF-8, currency=AUD} walkingkooka.Binary, expression \"1+sum(2)\" walkingkooka.tree.expression.Expression, json \"Hello\" walkingkooka.tree.json.JsonNode, \"Hello\" walkingkooka.tree.json.JsonArray, \"Hello\" walkingkooka.tree.json.JsonBoolean, \"Hello\" walkingkooka.tree.json.JsonNumber, \"Hello\" walkingkooka.tree.json.JsonString, \"Hello\" walkingkooka.tree.json.JsonObject, \"{\\n  \\\"A1\\\": {\\n    \\\"formula\\\": {\\n      \\\"text\\\": \\\"=1+2+3\\\",\\n      \\\"valueType\\\": \\\"Text\\\"\\n    },\\n    \\\"currencyExchangeRater\\\": \\\"test-currency-exchange-rater\\\",\\n    \\\"dateTimeSymbols\\\": {\\n      \\\"ampms\\\": [\\n        \\\"am\\\",\\n        \\\"pm\\\"\\n      ],\\n      \\\"monthNames\\\": [\\n        \\\"January\\\",\\n        \\\"February\\\",\\n        \\\"March\\\",\\n        \\\"April\\\",\\n        \\\"May\\\",\\n        \\\"June\\\",\\n        \\\"July\\\",\\n        \\\"August\\\",\\n        \\\"September\\\",\\n        \\\"October\\\",\\n        \\\"November\\\",\\n        \\\"December\\\"\\n      ],\\n      \\\"monthNameAbbreviations\\\": [\\n        \\\"Jan.\\\",\\n        \\\"Feb.\\\",\\n        \\\"Mar.\\\",\\, locale \"en-AU\" walkingkooka.datetime.DateTimeSymbols, en_AU walkingkooka.datetime.DateTimeSymbols, \"en-AU\" walkingkooka.math.DecimalNumberSymbols, en_AU walkingkooka.math.DecimalNumberSymbols, \"en-AU\" java.util.Locale, en_AU java.util.Locale, A1 \"=1+2+3\" valueType=text/Text currencyExchangeRater=\"test-currency-exchange-rater\" dateTimeSymbols=\"ampms=\"am\", \"pm\" monthNames=\"January\", \"February\", \"March\", \"April\", \"May\", \"June\", \"July\", \"August\", \"September\", \"October\", \"November\", \"December\" monthNameAbbreviations=\"Jan.\", \"Feb.\", \"Mar.\", \"Apr.\", \"May\", \"Jun.\", \"Jul.\", \"Aug.\", \"Sep.\", \"Oct.\", \"Nov.\", \"Dec.\" weekDayNames=\"Sunday\", \"Monday\", \"Tuesday\", \"Wednesday\", \"Thursday\", \"Friday\", \"Saturday\" weekDayNameAbbreviations=\"Sun.\", \"Mon.\", \"Tue.\", \"Wed.\", \"Thu.\", \"Fri.\", \"Sat.\"\" decimalNumberSymbols=\"negativeSign='-' positiveSign='+' zeroDigit='0' currencySymbol=\"$\" decimalSeparator='.' exponentSymbol, net \"https://example.com/123\" walkingkooka.net.Url, \"mailto:user@example.com\" walkingkooka.net.Url, \"/path1/path2?k1=v1#fragment111\" walkingkooka.net.Url, \"user@example.com\" walkingkooka.net.email.EmailAddress, \"text/plain; charset=UTF-8\" walkingkooka.net.header.MediaType, \"https://example.com/123\" walkingkooka.net.HasHostAddress, \"user@example.com\" walkingkooka.net.HasHostAddress, \"mailto:user@example.com\" walkingkooka.net.HasHostAddress, number null walkingkooka.tree.expression.ExpressionNumber, null java.lang.Byte, null java.lang.Short, null java.lang.Integer, null java.lang.Long, null java.lang.Float, null java.lang.Double, null java.math.BigInteger, null java.math.BigDecimal, 1 walkingkooka.tree.expression.ExpressionNumber, 1 java.lang.Byte, 1 java.lang.Short, 1 java.lang.Integer, 1 java.lang.Long, 1 java.lang.Float, 1 java.lang.Double, 1 java.math.BigInteger, 1 java.math.BigDecimal, optional-to null java.lang.Boolean, false java.lang.Boolean, true java.lang.Boolean, 123 walkingkooka.tree.expression.ExpressionNumber, 123 java.lang.Byte, 123 java.lang.Short, 123 java.lang.Integer, 123 java.lang.Long, 123 java.lang.Float, 123 java.lang.Double, 123 java.math.BigInteger, 123 java.math.BigDecimal, 1999-12-31 java.time.LocalDate, 1999-12-31T12:58 java.time.LocalDateTime, 1999-12-31T12:58 walkingkooka.datetime.LocalDateTimeList, \"Hello\" java.lang.String, 12:58 java.time.LocalTime, plugins A1 \"=1+2+3\" valueType=text/Text currencyExchangeRater=\"test-currency-exchange-rater\" dateTimeSymbols=\"ampms=\"am\", \"pm\" monthNames=\"January\", \"February\", \"March\", \"April\", \"May\", \"June\", \"July\", \"August\", \"September\", \"October\", \"November\", \"December\" monthNameAbbreviations=\"Jan.\", \"Feb.\", \"Mar.\", \"Apr.\", \"May\", \"Jun.\", \"Jul.\", \"Aug.\", \"Sep.\", \"Oct.\", \"Nov.\", \"Dec.\" weekDayNames=\"Sunday\", \"Monday\", \"Tuesday\", \"Wednesday\", \"Thursday\", \"Friday\", \"Saturday\" weekDayNameAbbreviations=\"Sun.\", \"Mon.\", \"Tue.\", \"Wed.\", \"Thu.\", \"Fri.\", \"Sat.\"\" decimalNumberSymbols=\"negativeSign='-' positiveSign='+' zeroDigit='0' currencySymbol=\"$\" decimalSeparator='.' exponentSymbol=\"e\" groupSeparator=',' infinitySymbol=\"∞\" monetaryDecimalSeparator='.' nanSymbol=\"NaN\" percentSymbol='%' permillSymbol='‰'\" formatter=\"test-formatter\" locale=\"en_AU\" parser=\"test-parser\" style={color=black} validator=\"test-validator\" w, properties {\n" +
                "  \"spreadsheetId\": \"1\",\n" +
                "  \"spreadsheetName\": \"Spreadsheet111\"\n" +
                "} walkingkooka.props.Properties, spreadsheet-metadata \"123\" walkingkooka.spreadsheet.meta.SpreadsheetMetadata, 123 walkingkooka.spreadsheet.meta.SpreadsheetMetadata, {\n" +
                "  \"spreadsheetId\": \"123\",\n" +
                "  \"auditInfo\": {\n" +
                "    \"createdBy\": \"user123@example.com\",\n" +
                "    \"createdTimestamp\": \"1999-12-31T12:58:59\",\n" +
                "    \"modifiedBy\": \"user123@example.com\",\n" +
                "    \"modifiedTimestamp\": \"1999-12-31T12:58:59\"\n" +
                "  },\n" +
                "  \"autoHideScrollbars\": false,\n" +
                "  \"cellCharacterWidth\": 1,\n" +
                "  \"color1\": \"black\",\n" +
                "  \"color2\": \"white\",\n" +
                "  \"colorBlack\": 1,\n" +
                "  \"colorWhite\": 2,\n" +
                "  \"comparators\": \"background-color, border-bottom-color, border-color, border-left-color, border-right-color, border-top-color, color, currency, custom-list, custom-list-case-insensitive, date, date-time, day-of-month, day-of-week, error, formatter, hour-of-am-pm, hour-of-day, locale, minute-of-hour, month-of-year, nano-of-second, number, outline-color, parser, seconds-of-minute, text, text-case-insensitive, text-decorat, storage-binary-to-storage-value-binary /file.bin 00 01 02                                                    ???                 \n" +
                " walkingkooka.storage.StorageValue, /file.bin=00 01 02                                                    ???                 \n" +
                " walkingkooka.storage.StorageBinary, storage-binary-to-storage-value-csv /file.csv 41 70 70 6c 65 20 31 2c 42 61 6e 61 6e 61 20 32             Apple 1,Banana 2    \n" +
                " walkingkooka.storage.StorageValue, storage-binary-to-storage-value-environment /EnvironmentContext.env 63 68 61 72 73 65 74 3d 55 54 46 2d 38 0a 63 75 72 72 65 6e charset=UTF-8?curren\n" +
                "63 79 3d 41 55 44 0a 69 6e 64 65 6e 74 61 74 69 6f 6e 3d 22 cy=AUD?indentation=\"\n" +
                "20 20 22 0a 6c 69 6e 65 45 6e 64 69 6e 67 3d 4e 4c 0a 6c 6f   \"?lineEnding=NL?lo\n" +
                "63 61 6c 65 3d 65 6e 2d 41 55                               cale=en-AU          \n" +
                " walkingkooka.storage.StorageValue, storage-binary-to-storage-value-expression /formula.expression.txt 31 2b 32                                                    1+2                 \n" +
                " walkingkooka.storage.StorageValue, storage-binary-to-storage-value-json /file1/file2.json application/json7b 0a 20 20 22 68 65 6c 6c 6f 22 3a 20 22 77 6f 72 6c 64 22 {?  \"hello\": \"world\"\n" +
                "0a 7d                                                       ?}                  \n" +
                " walkingkooka.storage.StorageValue, storage-binary-to-storage-value-properties /file1/file2.properties 68 65 6c 6c 6f 3d 77 6f 72 6c 64 0d 0a                      hello=world??       \n" +
                " walkingkooka.storage.StorageValue, storage-binary-to-storage-value-tsv /file.tsv 41 70 70 6c 65 20 31 09 42 61 6e 61 6e 61 20 32             Apple 1?Banana 2    \n" +
                " walkingkooka.storage.StorageValue, storage-binary-to-storage-value-txt /file1.file2.txt 48 65 6c 6c 6f 20 57 6f 72 6c 64 20 54 65 78 74 20 31 32 33 Hello World Text 123\n" +
                " walkingkooka.storage.StorageValue, storage-value-info-list-to-text [/path1/file2.txt user@example.com 1999-12-31T12:58 user@example.com 1999-12-31T12:58] java.lang.String, storage-value-to-storage-binary-binary /file.bin=48 65 6c 6c 6f 20 57 6f 72 6c 64 20 42 69 6e 61 72 79       Hello World Binary  \n" +
                " walkingkooka.storage.StorageBinary, storage-value-to-storage-binary-csv /file.csv=\"Apple 1\", \"Banana 2\" walkingkooka.storage.StorageBinary, storage-value-to-storage-binary-environment /EnvironmentContext.env=\"charset=UTF-8\n" +
                "currency=AUD\n" +
                "indentation=\"  \"\n" +
                "lineEnding=NL\n" +
                "locale=en-AU\" walkingkooka.storage.StorageBinary, storage-value-to-storage-binary-expression /formula.expression.txt=\"1+2\" walkingkooka.storage.StorageBinary, storage-value-to-storage-binary-json /file1/file2.json={\n" +
                "  \"hello\": \"world\"\n" +
                "} application/json walkingkooka.storage.StorageBinary, storage-value-to-storage-binary-properties /file1/file2.properties=hello=world\n" +
                " walkingkooka.storage.StorageBinary, storage-value-to-storage-binary-tsv /file.tsv=\"Apple 1\", \"Banana 2\" walkingkooka.storage.StorageBinary, storage-value-to-storage-binary-txt /file1.file2.txt=\"Hello World Text 123\" walkingkooka.storage.StorageBinary, style {color=black} walkingkooka.tree.text.TextStyle, A1 style={color=black} walkingkooka.tree.text.TextStyle, color=black\n" +
                " walkingkooka.tree.text.TextStyle, \"color=black\\r\\n\" walkingkooka.tree.text.TextStyle, \"black SOLID 1px\" walkingkooka.tree.text.Border, \"1px\" walkingkooka.tree.text.Margin, \"1px\" walkingkooka.tree.text.Padding, \"Text123\" walkingkooka.spreadsheet.format.SpreadsheetText, \"color: black;\" walkingkooka.tree.text.TextStyle, \"background-color\" walkingkooka.tree.text.TextStylePropertyName, text 'A' java.lang.String, \"Text\" java.lang.String, https://example.com/123 java.lang.String, \"=1+2+3\" valueType=text/Text java.lang.String, ' ' java.lang.Character, ' ' java.lang.String, \"A\" java.lang.Character, \"A\" java.lang.String, \"UTF-8\" java.nio.charset.Charset, \"  \" walkingkooka.text.Indentation, \"    \" walkingkooka.text.Indentation, \"  \" walkingkooka.text.Indentation, \"\\r\\n\" walkingkooka.text.LineEnding, \"crnl\" walkingkooka.text.LineEnding, text-node \"https://example.com/123\" walkingkooka.tree.text.Hyperlink, \"https://example.com/123\" walkingkooka.tree.text.Image, {color: black}[\"Text123\"] walkingkooka.tree.text.Styleable, {color=black} walkingkooka.tree.text.Styleable, 'A' walkingkooka.tree.text.TextNode, \"Text123\" walkingkooka.tree.text.TextNode, \"Text123\" walkingkooka.tree.text.TextNode, text-to-boolean-list \"TRUE, FALSE, true\" walkingkooka.collect.list.BooleanList, text-to-color \"#123456\" walkingkooka.color.Color, text-to-csv-string-list \"Apple 1,Banana 2\" walkingkooka.collect.list.CsvStringList, text-to-date-list \"1999/12/31\" walkingkooka.datetime.LocalDateList, text-to-date-time-list \"1999/12/31 12:58\" walkingkooka.datetime.LocalDateTimeList, text-to-environment \"currency=AUD\" walkingkooka.environment.Environment, text-to-environment-value-name \"currency\" walkingkooka.environment.EnvironmentValueName, text-to-flag \"AU\" walkingkooka.tree.text.Flag, text-to-number-list \"1, 22, 333.5\" walkingkooka.math.NumberList, text-to-path \"/tmp/File1.txt\" java.nio.file.Path, text-to-storage-path \"/path1/file2.txt\" walkingkooka.storage.StoragePath, text-to-string-list \"Apple 1, Banana 2\" walkingkooka.collect.list.StringList, text-to-time-list \"12:58:59\" walkingkooka.datetime.LocalTimeList, text-to-tsv-string-list \"Apple 1\\tBanana 2\" walkingkooka.collect.list.TsvStringList, text-to-tsv-string-set \"Apple 1\\tBanana 2\" walkingkooka.collect.set.TsvStringSet, to-color-properties \"hello=BLACK\\r\\n\" walkingkooka.color.ColorProperties, value #NAME? \"Cell not found: \"A1\"\" A1 walkingkooka.spreadsheet.value.SpreadsheetError, #NAME? \"Cell not found: \"A1\"\" A1 walkingkooka.tree.expression.ExpressionNumber, null walkingkooka.tree.expression.ExpressionNumber, null java.lang.Byte, null java.lang.Short, null java.lang.Integer, null java.lang.Long, null java.lang.Float, null java.lang.Double, null java.math.BigInteger, null java.math.BigDecimal, 1 walkingkooka.tree.expression.ExpressionNumber, 1 java.lang.Byte, 1 java.lang.Short, 1 java.lang.Integer, 1 java.lang.Long, 1 java.lang.Float, 1 java.lang.Double, 1 java.math.BigInteger, 1 java.math.BigDecimal, 1.0 walkingkooka.tree.expression.ExpressionNumber, 1.0 java.lang.Byte, 1.0 java.lang.Short, 1.0 java.lang.Integer, 1.0 java.lang.Long, 1.0 java.lang.Float, 1.0 java.lang.Double, 1.0 java.math.BigInteger, 1.0 java.math.BigDecimal, {\n" +
                "  \"hello\": \"world\"\n" +
                "} walkingkooka.text.MultiLineText, ]\n"
        );
    }

    private void verifyAndLogCheck(final LoggingLevel loggingLevel,
                                   final String...expected) {
        Arrays.stream(expected)
            .collect(Collectors.joining());
    }

    private void verifyAndLogCheck(final LoggingLevel loggingLevel,
                                   final String expected) {
        final Converter<SpreadsheetConverterContext> converter = SpreadsheetConverters.never();

        final StringBuilder log = new StringBuilder();
        final Printer printer = Printers.stringBuilder(
            log,
            LINE_ENDING
        );

        MissingConverterVerifier.verify(
            converter,
            SpreadsheetMetadataPropertyName.FORMULA_CONVERTER,
            new TestSpreadsheetConverterContext(
                SPREADSHEET_METADATA_TESTING,
                converter,
                loggingLevel,
                printer
            )
        );

        printer.flush();

        // best to normalize all EOL to NL before compare.
        this.checkEquals(
            expected,
            log.toString()
                .replace("\r\n", "\n")
                .replace("\r", "\n"),
            () -> "loggingLevel " + loggingLevel
        );
    }

    final static class TestSpreadsheetConverterContext implements SpreadsheetConverterContext,
        ColorContextDelegator,
        CanParseEnvironmentValueNameDelegator,
        DateTimeContextDelegator,
        DecimalNumberContextDelegator,
        JsonNodeMarshallUnmarshallContextDelegator,
        LoggingContextDelegator,
        LocaleContextDelegator {

        TestSpreadsheetConverterContext(final SpreadsheetMetadata spreadsheetMetadata,
                                        final Converter<SpreadsheetConverterContext> converter) {
            this(
                spreadsheetMetadata,
                converter,
                LoggingLevel.NONE,
                Printers.sink(LINE_ENDING)
            );
        }

        TestSpreadsheetConverterContext(final SpreadsheetMetadata spreadsheetMetadata,
                                        final Converter<SpreadsheetConverterContext> converter,
                                        final LoggingLevel loggingLevel,
                                        final Printer log) {
            super();

            this.spreadsheetMetadata = spreadsheetMetadata;
            this.converter = converter;
            this.loggingContext = LoggingContexts.canLog(
                loggingLevel,
                CanLogs.printer(log)
            );
        }

        @Override
        public MediaType detect(final String filename,
                                final Binary content) {
            return MEDIA_TYPE_DETECTOR.detect(
                    filename,
                    content
                );
        }

        @Override
        public boolean canConvert(final Object value,
                                  final Class<?> type) {
            return this.converter.canConvert(
                value,
                type,
                SpreadsheetConverterContexts.cycle(
                    value,
                    type,
                    this
                )
            );
        }

        @Override
        public <T> Either<T, String> convert(final Object value,
                                             final Class<T> type) {
            return this.converter.convert(
                value,
                type,
                SpreadsheetConverterContexts.cycle(
                    value,
                    type,
                    this
                )
            );
        }

        private final Converter<SpreadsheetConverterContext> converter;

        @Override
        public ExpressionNumberKind expressionNumberKind() {
            return EXPRESSION_NUMBER_KIND;
        }

        @Override
        public JsonNode marshall(final Object object) {
            return JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT.marshall(object);
        }

        @Override
        public DateTimeContext dateTimeContext() {
            return DATE_TIME_CONTEXT;
        }

        @Override
        public DecimalNumberContext decimalNumberContext() {
            return DECIMAL_NUMBER_CONTEXT;
        }

        @Override
        public MathContext mathContext() {
            return MATH_CONTEXT;
        }

        @Override
        public JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext() {
            return JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT;
        }

        // CanCurrencyExchangeRate......................................................................................

        @Override
        public Optional<Number> currencyExchangeRate(final CurrencyExchange currencyExchange,
                                                     final Optional<LocalDateTime> dateTime) {
            return CURRENCY_CONTEXT.currencyExchangeRate(
                currencyExchange,
                dateTime
            );
        }

        // CanCurrencyExchanges.........................................................................................

        @Override
        public Set<CurrencyExchange> currencyExchanges() {
            return CURRENCY_CONTEXT.currencyExchanges();
        }

        @Override
        public Optional<Currency> currencyForLocale(final Locale locale) {
            return CURRENCY_CONTEXT.currencyForLocale(locale);
        }

        @Override
        public boolean canNumbersHaveGroupSeparator() {
            return false;
        }

        @Override
        public Charset charset() {
            return CHARSET;
        }

        @Override
        public CurrencyCode currencyCode() {
            return CURRENCY_CONTEXT.currencyCode();
        }

        @Override
        public long dateOffset() {
            return this.spreadsheetMetadata.getOrFail(SpreadsheetMetadataPropertyName.DATE_OFFSET);
        }

        @Override
        public Indentation indentation() {
            return INDENTATION;
        }

        @Override
        public LineEnding lineEnding() {
            return LINE_ENDING;
        }

        @Override
        public <N extends Number> N multiply(final Number left,
                                             final Number right,
                                             final Class<N> type) {
            return MULTIPLIER.apply(
                left,
                right,
                type,
                this
            );
        }

        @Override
        public char valueSeparator() {
            return ',';
        }

        @Override
        public Converter<SpreadsheetConverterContext> converter() {
            return this.converter;
        }

        @Override
        public LocaleContext localeContext() {
            return LOCALE_CONTEXT;
        }

        @Override
        public Locale locale() {
            return LOCALE;
        }

        @Override
        public void setLocale(final Locale locale) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<StoragePath> currentWorkingDirectory() {
            return NO_CURRENT_WORKING_DIRECTORY;
        }

        @Override
        public Optional<StoragePath> homeDirectory() {
            return OPTIONAL_HOME_DIRECTORY;
        }

        @Override
        public Optional<EmailAddress> user() {
            return OPTIONAL_USER;
        }

        @Override
        public SpreadsheetValidationReference validationReference() {
            return SpreadsheetSelection.A1;
        }

        @Override
        public Optional<SpreadsheetMetadata> spreadsheetMetadata() {
            return Optional.of(spreadsheetMetadata);
        }

        private final SpreadsheetMetadata spreadsheetMetadata;

        @Override
        public Optional<SpreadsheetMetadata> loadMetadata(final SpreadsheetId id) {
            return Optional.ofNullable(
                SPREADSHEET_ID.equals(id) ?
                    this.spreadsheetMetadata :
                    null
            );
        }

        @Override
        public Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName labelName) {
            return Optional.empty();
        }

        @Override
        public SpreadsheetConverterContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor processor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SpreadsheetConverterContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Locale> localeForLanguageTag(final LocaleLanguageTag languageTag) {
            return LOCALE_CONTEXT.localeForLanguageTag(languageTag);
        }

        // ColorContextDelegator........................................................................................

        @Override
        public ColorContext colorContext() {
            return SpreadsheetColorContexts.spreadsheetMetadataDefaults();
        }

        // CanParseEnvironmentValueNameDelegator........................................................................

        @Override
        public CanParseEnvironmentValueName canParseEnvironmentValueName() {
            return SPREADSHEET_CAN_PARSE_ENVIRONMENT_VALUE_NAME;
        }

        // LoggingContextDelegator......................................................................................

        @Override
        public LoggingContext loggingContext() {
            return this.loggingContext;
        }

        private final LoggingContext loggingContext;
    }

    @Test
    public void testVerifyAndMarshall() {
        final Converter<SpreadsheetConverterContext> converter = SpreadsheetConvertersConverterProviders.spreadsheetConverters(
            (ProviderContext p) -> SpreadsheetMetadata.EMPTY.dateTimeConverter(
                SPREADSHEET_FORMATTER_PROVIDER,
                SPREADSHEET_PARSER_PROVIDER,
                p
            )
        ).converter(
            ConverterSelector.parse("simple"),
            PROVIDER_CONTEXT
        );

        final Set<MissingConverter> missing = MissingConverterVerifier.verify(
            converter,
            SpreadsheetMetadataPropertyName.VALIDATION_CONVERTER,
            new TestSpreadsheetConverterContext(
                SPREADSHEET_METADATA_TESTING,
                converter
            )
        );

        JSON_NODE_MARSHALL_CONTEXT.marshall(missing);
    }

    // class............................................................................................................

    @Override
    public Class<MissingConverterVerifier> type() {
        return MissingConverterVerifier.class;
    }
}
