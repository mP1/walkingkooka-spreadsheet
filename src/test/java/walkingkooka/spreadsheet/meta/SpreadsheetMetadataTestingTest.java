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

import org.junit.jupiter.api.Test;
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.convert.Converter;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.environment.ReadOnlyEnvironmentValueException;
import walkingkooka.locale.LocaleContextTesting;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.provider.SpreadsheetConvertersConverterProviders;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextTesting;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataTestingTest implements SpreadsheetMetadataTesting,
    LocaleContextTesting,
    SpreadsheetEnvironmentContextTesting,
    TreePrintableTesting {

    @Test
    public void testSpreadsheetNameConstantsDifferent() {
        this.checkNotEquals(
            SPREADSHEET_NAME,
            DIFFERENT_SPREADSHEET_NAME
        );
    }

    @Test
    public void testCurrencyContextReadOnly() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> SpreadsheetMetadataTesting.CURRENCY_CONTEXT.setCurrency(DIFFERENT_CURRENCY)
        );
    }

    @Test
    public void testEffectiveStyle() {
        METADATA_EN_AU.effectiveStyle();
    }

    @Test
    public void testEnvironmentContextSetEnvironmentValueFails() {
        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> SPREADSHEET_ENVIRONMENT_CONTEXT.setEnvironmentValue(
                EnvironmentValueName.with(
                    "Hello",
                    String.class
                ),
                "World"
            )
        );
    }

    @Test
    public void testEnvironmentContextRemoveEnvironmentValueFails() {
        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> SPREADSHEET_ENVIRONMENT_CONTEXT.removeEnvironmentValue(
                SpreadsheetEnvironmentContext.LINE_ENDING
            )
        );
    }

    @Test
    public void testEnvironmentContextConvertAllValuesToString() {
        final Converter<SpreadsheetConverterContext> converter = METADATA_EN_AU.converter(
            SpreadsheetMetadataPropertyName.SCRIPTING_CONVERTER,
            CONVERTER_PROVIDER,
            PROVIDER_CONTEXT
        );

        final SpreadsheetConverterContext converterContext = METADATA_EN_AU.spreadsheetConverterContext(
            SpreadsheetMetadata.NO_CELL,
            SpreadsheetMetadata.NO_VALIDATION_REFERENCE,
            SpreadsheetMetadataPropertyName.SCRIPTING_CONVERTER,
            SPREADSHEET_LABEL_NAME_RESOLVER,
            MEDIA_TYPE_DETECTOR,
            MULTIPLIER,
            SPREADSHEET_METADATA_LOADER,
            CONVERTER_PROVIDER,
            CURRENCY_LOCALE_CONTEXT,
            SPREADSHEET_ENVIRONMENT_CONTEXT,
            PROVIDER_CONTEXT
        );

        final Set<EnvironmentValueName<?>> names = SortedSets.tree();

        for (final EnvironmentValueName<?> name : SPREADSHEET_ENVIRONMENT_CONTEXT.environmentValueNames()) {
            final Either<?, String> result = converter.convert(
                SPREADSHEET_ENVIRONMENT_CONTEXT.environmentValueOrFail(name),
                String.class,
                converterContext
            );
            if (result.isRight()) {
                names.add(name);
            }
        }

        this.checkEquals(
            Sets.empty(),
            names,
            SPREADSHEET_ENVIRONMENT_CONTEXT::toString
        );
    }

    @Test
    public void testFormatter() {
        METADATA_EN_AU.spreadsheetFormatter(
            SPREADSHEET_FORMATTER_PROVIDER,
            PROVIDER_CONTEXT
        );
    }

    @Test
    public void testJsonNodeMarshallContext() {
        METADATA_EN_AU.jsonNodeMarshallContext();
    }

    @Test
    public void testJsonNodeUnmarshallContext() {
        METADATA_EN_AU.jsonNodeUnmarshallContext(
            SPREADSHEET_CAN_PARSE_ENVIRONMENT_VALUE_NAME,
            CURRENCY_LOCALE_CONTEXT
        );
    }

    @Test
    public void testParseFormula() {
        this.checkEquals(
            SpreadsheetFormula.EMPTY.setToken(
                Optional.of(
                    SpreadsheetFormulaParserToken.expression(
                        Lists.of(
                            SpreadsheetFormulaParserToken.equalsSymbol(
                                "=",
                                "="
                            ),
                            SpreadsheetFormulaParserToken.number(
                                Lists.of(
                                    SpreadsheetFormulaParserToken.digits(
                                        "1",
                                        "1"
                                    )
                                ),
                                "1"
                            )
                        ),
                        "=1"
                    )
                )
            ),
            SpreadsheetMetadataTesting.parseFormula(
                "=1"
            )
        );
    }

    @Test
    public void testSetLocaleFails() {
        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> SPREADSHEET_ENVIRONMENT_CONTEXT.setLocale(DIFFERENT_LOCALE)
        );
        
        this.localeAndCheck(
            SPREADSHEET_ENVIRONMENT_CONTEXT,
            LOCALE
        );
    }

    @Test
    public void testProviderContextSetEnvironmentValueFails() {
        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> PROVIDER_CONTEXT.setEnvironmentValue(
                EnvironmentValueName.with(
                    "Hello",
                    String.class
                ),
                "World"
            )
        );
    }

    @Test
    public void testProviderContextRemoveEnvironmentValueFails() {
        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> PROVIDER_CONTEXT.removeEnvironmentValue(
                EnvironmentValueName.with(
                    "Hello",
                    String.class
                )
            )
        );
    }

    @Test
    public void testProviderContextCloneEnvironmentSetLineEnding() {
        this.setLineEndingAndCheck(
            PROVIDER_CONTEXT.cloneEnvironment(),
            DIFFERENT_LINE_ENDING
        );
    }

    @Test
    public void testProviderContextCloneEnvironmentSetLocale() {
        this.setLocaleAndCheck(
            PROVIDER_CONTEXT.cloneEnvironment(),
            DIFFERENT_LOCALE
        );
    }

    @Test
    public void testProviderContextCloneEnvironmentSetUser() {
        this.setUserAndCheck(
            PROVIDER_CONTEXT.cloneEnvironment(),
            DIFFERENT_USER
        );
    }

    @Test
    public void testProviderContextCloneEnvironmentSetEnvironmentValue() {
        final EnvironmentValueName<String> name = EnvironmentValueName.with(
            "Hello",
            String.class
        );
        final String value = "World123";

        final ProviderContext context = PROVIDER_CONTEXT.cloneEnvironment();
        this.setEnvironmentValueAndCheck(
            context,
            name,
            value
        );
    }

    @Test
    public void testSpreadsheetFormatterContext() {
        METADATA_EN_AU.spreadsheetFormatterContext(
            SpreadsheetMetadata.NO_CELL,
            (final Optional<Object> value) -> {
                throw new UnsupportedOperationException();
            },
            SPREADSHEET_LABEL_NAME_RESOLVER,
            MEDIA_TYPE_DETECTOR,
            MULTIPLIER,
            SPREADSHEET_METADATA_LOADER,
            CURRENCY_LOCALE_CONTEXT,
            SPREADSHEET_ENVIRONMENT_CONTEXT,
            SPREADSHEET_PROVIDER,
            PROVIDER_CONTEXT
        );
    }

    @Test
    public void testSpreadsheetParser() {
        METADATA_EN_AU.spreadsheetParser(
            SPREADSHEET_PARSER_PROVIDER,
            PROVIDER_CONTEXT
        );
    }

    @Test
    public void testSpreadsheetParserContext() {
        METADATA_EN_AU
            .spreadsheetParserContext(
                SpreadsheetMetadata.NO_CELL,
                SpreadsheetMetadataTesting.LOCALE_CONTEXT,
                HAS_NOW
            );
    }

    @Test
    public void testTerminalContext() {
        this.checkEquals(
            "1, input: FakeTextReader, output: sink, error: sink {charset=UTF-8, currency=AUD, currentWorkingDirectory=/current1/working2/directory3, homeDirectory=/users/user123@example.com, indentation=\"  \", lineEnding=\"\\n\", locale=en_AU, loggingLevel=NONE, serverUrl=https://example.com, terminalId=1, timeOffset=Z, user=user123@example.com}",
            SpreadsheetMetadataTesting.TERMINAL_CONTEXT.toString()
        );
    }

    @Test
    public void testSetUserFails() {
        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> SPREADSHEET_ENVIRONMENT_CONTEXT.setUser(OPTIONAL_DIFFERENT_USER)
        );

        this.userAndCheck(
            SPREADSHEET_ENVIRONMENT_CONTEXT,
            USER
        );
    }

    @Test
    public void testFormattingConverterPrintTree() {
        this.treePrintAndCheck(
            SpreadsheetMetadataPropertyName.FORMATTING_CONVERTER,
            "ConverterCustomToString\n" +
                "  \"collection (null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, json, currency, logging, plugins, properties, spreadsheet-metadata, storage, style, text-node, template, net, optional-to, collection-to)\"\n" +
                "    ConverterCollection\n" +
                "      ConverterCustomToString\n" +
                "        \"null-to-number\"\n" +
                "          null to Number (walkingkooka.spreadsheet.convert.SpreadsheetConverterNullToNumber)\n" +
                "      simple (walkingkooka.convert.ConverterSimple)\n" +
                "      ConverterCustomToString\n" +
                "        \"text\"\n" +
                "          ConverterCollection\n" +
                "            Character or CharSequence or HasText or String to Character or CharSequence or String (walkingkooka.convert.ConverterCharacterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString)\n" +
                "            TEXT to Charset (walkingkooka.convert.ConverterTextToCharset)\n" +
                "            TEXT to Indentation (walkingkooka.convert.ConverterTextToIndentation)\n" +
                "            TEXT to LineEnding (walkingkooka.convert.ConverterTextToLineEnding)\n" +
                "      ConverterCustomToString\n" +
                "        \"boolean\"\n" +
                "          ConverterCollection\n" +
                "            to Boolean (walkingkooka.spreadsheet.convert.SpreadsheetConverterToBoolean)\n" +
                "            Boolean to TEXT (walkingkooka.spreadsheet.convert.SpreadsheetConverterBooleanToText)\n" +
                "      ConverterCustomToString\n" +
                "        \"number\"\n" +
                "          ConverterCollection\n" +
                "            Number to Number (walkingkooka.tree.expression.convert.ExpressionNumberConverterNumberToNumber)\n" +
                "            to Number (walkingkooka.spreadsheet.convert.SpreadsheetConverterToNumber)\n" +
                "            Number to TEXT (walkingkooka.spreadsheet.convert.SpreadsheetConverterNumberToText)\n" +
                "      ConverterCustomToString\n" +
                "        \"date-time\"\n" +
                "          dateTime (walkingkooka.spreadsheet.convert.SpreadsheetConverterDateTime)\n" +
                "      ConverterCustomToString\n" +
                "        \"environment\"\n" +
                "          ConverterCollection\n" +
                "            to Environment (walkingkooka.environment.convert.EnvironmentConverterToEnvironment)\n" +
                "            Environment to Binary (walkingkooka.environment.convert.EnvironmentConverterEnvironmentToBinary)\n" +
                "            Environment to TEXT (walkingkooka.environment.convert.EnvironmentConverterEnvironmentToString)\n" +
                "            TEXT to Environment (walkingkooka.environment.convert.EnvironmentConverterTextToEnvironment)\n" +
                "            TEXT to EnvironmentValueName (walkingkooka.environment.convert.EnvironmentConverterTextToEnvironmentValueName)\n" +
                "      ConverterCustomToString\n" +
                "        \"locale\"\n" +
                "          ConverterCollection\n" +
                "            Locale to String (walkingkooka.convert.ConverterLocaleToString)\n" +
                "            LocaleLike to Locale (walkingkooka.convert.ConverterLocaleToLocale)\n" +
                "            LocaleLike to LocaleLanguageTag (walkingkooka.convert.ConverterLocaleToLocaleLanguageTag)\n" +
                "            ConverterCollection\n" +
                "              to DateTimeSymbols (walkingkooka.convert.ConverterToDateTimeSymbols)\n" +
                "              LocaleLike to DateTimeSymbols (walkingkooka.convert.ConverterLocaleToDateTimeSymbols)\n" +
                "              Properties to DateTimeSymbols (walkingkooka.convert.ConverterPropertiesToDateTimeSymbols)\n" +
                "            ConverterCollection\n" +
                "              to DecimalNumberSymbols (walkingkooka.convert.ConverterToDecimalNumberSymbols)\n" +
                "              LocaleLike to DecimalNumberSymbols (walkingkooka.convert.ConverterLocaleToDecimalNumberSymbols)\n" +
                "              Properties to DecimalNumberSymbols (walkingkooka.convert.ConverterPropertiesToDecimalNumberSymbols)\n" +
                "            TEXT to LocaleLanguageTag (walkingkooka.convert.ConverterTextToLocaleLanguageTag)\n" +
                "      ConverterCustomToString\n" +
                "        \"value\"\n" +
                "          ConverterCollection\n" +
                "            SpreadsheetError to Number (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetErrorToNumber)\n" +
                "            null to Number (walkingkooka.spreadsheet.convert.SpreadsheetConverterNullToNumber)\n" +
                "            ConverterCustomToString\n" +
                "              \"SPREADSHEET SELECTION\"\n" +
                "                ConverterCollection\n" +
                "                  HasSpreadsheetReference (walkingkooka.spreadsheet.convert.SpreadsheetConverterToSpreadsheetSelection)\n" +
                "                  SELECTION to SELECTION (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetSelectionToSpreadsheetSelection)\n" +
                "                  SELECTION to TEXT (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetSelectionToText)\n" +
                "                  TEXT to SELECTION (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetSelection)\n" +
                "            SpreadsheetError to SpreadsheetError (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetErrorToSpreadsheetError)\n" +
                "            TEXT to SpreadsheetError (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetError)\n" +
                "            to ValueType (walkingkooka.validation.convert.ValidationConverterToValueType)\n" +
                "            TEXT to ValueType (walkingkooka.validation.convert.ValidationConverterTextToValueType)\n" +
                "            TEXT to ZoneOffset (walkingkooka.convert.ConverterTextToZoneOffset)\n" +
                "            SpreadsheetCellSet (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetCellSet)\n" +
                "            Collection to List (walkingkooka.convert.ConverterCollectionToList)\n" +
                "            TEXT to BooleanList (walkingkooka.convert.ConverterTextToCollectionListBooleanList)\n" +
                "            TEXT to LocalDateList (walkingkooka.convert.ConverterTextToCollectionListLocalDateList)\n" +
                "            TEXT to LocalDateTimeList (walkingkooka.convert.ConverterTextToCollectionListLocalDateTimeList)\n" +
                "            TEXT to LineEnding (walkingkooka.convert.ConverterTextToLineEnding)\n" +
                "            TEXT to NumberList (walkingkooka.convert.ConverterTextToCollectionListNumberList)\n" +
                "            TEXT to LocalTimeList (walkingkooka.convert.ConverterTextToCollectionListLocalTimeList)\n" +
                "            TEXT to StringList (walkingkooka.convert.ConverterTextToCollectionListStringList)\n" +
                "            ConverterCustomToString\n" +
                "              \"CSV\"\n" +
                "                ConverterCollection\n" +
                "                  to CsvStringList (walkingkooka.convert.ConverterToCsvStringList)\n" +
                "                  TEXT to CsvStringList (walkingkooka.convert.ConverterTextToCollectionListCsvStringList)\n" +
                "                  TEXT to CsvStringSet (walkingkooka.convert.ConverterTextToCollectionSetCsvStringSet)\n" +
                "            ConverterCustomToString\n" +
                "              \"TSV\"\n" +
                "                ConverterCollection\n" +
                "                  to TsvStringList (walkingkooka.convert.ConverterToTsvStringList)\n" +
                "                  TEXT to TsvStringList (walkingkooka.convert.ConverterTextToCollectionListTsvStringList)\n" +
                "                  TEXT to TsvStringSet (walkingkooka.convert.ConverterTextToCollectionSetTsvStringSet)\n" +
                "            Binary to TEXT (walkingkooka.convert.ConverterBinaryToString)\n" +
                "            to MultiLineText (walkingkooka.convert.ConverterToMultiLineText)\n" +
                "            * to String (walkingkooka.convert.ConverterObjectToString)\n" +
                "      ConverterCustomToString\n" +
                "        \"error-throwing\"\n" +
                "          throws SpreadsheetError (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetErrorThrowing)\n" +
                "      ConverterCustomToString\n" +
                "        \"color\"\n" +
                "          ConverterCollection\n" +
                "            ConverterCustomToString\n" +
                "              \"TEXT\"\n" +
                "                ConverterCollection\n" +
                "                  Character or CharSequence or HasText or String to Character or CharSequence or String (walkingkooka.convert.ConverterCharacterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString)\n" +
                "                  TEXT to Charset (walkingkooka.convert.ConverterTextToCharset)\n" +
                "                  TEXT to Indentation (walkingkooka.convert.ConverterTextToIndentation)\n" +
                "                  TEXT to LineEnding (walkingkooka.convert.ConverterTextToLineEnding)\n" +
                "            Color to Color (walkingkooka.color.convert.ConverterColorToColor)\n" +
                "            TEXT to Color (walkingkooka.color.convert.ConverterTextToColor)\n" +
                "            Color to Number (walkingkooka.color.convert.ConverterColorToNumber)\n" +
                "            Number to Color (walkingkooka.color.convert.ConverterNumberToColor)\n" +
                "            to ColorProperties (walkingkooka.color.convert.ConverterToColorProperties)\n" +
                "            TEXT to SpreadsheetColorName (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetColorName)\n" +
                "            TEXT to SpreadsheetMetadata Color (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetMetadataColor)\n" +
                "            ConverterCustomToString\n" +
                "              \"PROPERTIES\"\n" +
                "                ConverterCollection\n" +
                "                  to Properties (walkingkooka.convert.ConverterToProperties)\n" +
                "                  TEXT to Properties (walkingkooka.convert.ConverterTextToProperties)\n" +
                "      ConverterCustomToString\n" +
                "        \"expression\"\n" +
                "          TEXT to Expression (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToExpression)\n" +
                "      ConverterCustomToString\n" +
                "        \"json\"\n" +
                "          ConverterCollection\n" +
                "            JsonNode to type (walkingkooka.tree.json.convert.JsonNodeConverterJsonNodeTo)\n" +
                "            Json TEXT to Object (walkingkooka.tree.json.convert.JsonNodeConverterTextToObject)\n" +
                "            * to JsonNode (walkingkooka.tree.json.convert.JsonNodeConverterToJsonNode)\n" +
                "            TEXT to JsonNode (walkingkooka.tree.json.convert.JsonNodeConverterTextToJsonNode)\n" +
                "            TEXT to JsonPointer (walkingkooka.tree.json.convert.JsonNodeConverterTextToJsonPointer)\n" +
                "            TEXT to JsonSelector (walkingkooka.tree.json.convert.JsonNodeConverterTextToJsonSelector)\n" +
                "      ConverterCustomToString\n" +
                "        \"currency\"\n" +
                "          ConverterCollection\n" +
                "            to Currency (walkingkooka.convert.ConverterToCurrency)\n" +
                "            to CurrencyCode (walkingkooka.convert.ConverterToCurrencyCode)\n" +
                "            CurrencyCode to Currency (walkingkooka.convert.ConverterCurrencyCodeToCurrency)\n" +
                "            CurrencyValue to Number (walkingkooka.convert.ConverterCurrencyValueToNumber)\n" +
                "            CurrencyValue to (walkingkooka.convert.ConverterCurrencyValueTo)\n" +
                "            Number to CurrencyValue (walkingkooka.convert.ConverterNumberToCurrencyValue)\n" +
                "            TEXT to Currency (walkingkooka.convert.ConverterTextToCurrency)\n" +
                "            TEXT to CurrencyCode (walkingkooka.convert.ConverterTextToCurrencyCode)\n" +
                "            TEXT to CurrencyValue (walkingkooka.convert.ConverterTextToCurrencyValue)\n" +
                "      ConverterCustomToString\n" +
                "        \"logging\"\n" +
                "          TEXT to LoggingLevel (walkingkooka.convert.ConverterTextToLoggingLevel)\n" +
                "      ConverterCustomToString\n" +
                "        \"plugins\"\n" +
                "          ConverterCollection\n" +
                "            to SpreadsheetFormatterSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterToSpreadsheetFormatterSelector)\n" +
                "            to SpreadsheetParserSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterToSpreadsheetParserSelector)\n" +
                "            to ValidatorSelector (walkingkooka.validation.convert.ValidationConverterToValidatorSelector)\n" +
                "            TEXT to ConverterSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToConverterSelector)\n" +
                "            TEXT to CurrencyExchangeRaterSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToCurrencyExchangeRaterSelector)\n" +
                "            TEXT to ExpressionFunctionSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToExpressionFunctionSelector)\n" +
                "            TEXT to SpreadsheetComparatorSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetComparatorSelector)\n" +
                "            TEXT to SpreadsheetFormatterSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetFormatterSelector)\n" +
                "            TEXT to SpreadsheetParserSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetParserSelector)\n" +
                "            TEXT to ValidatorSelector (walkingkooka.validation.convert.ValidationConverterTextToValidatorSelector)\n" +
                "      ConverterCustomToString\n" +
                "        \"properties\"\n" +
                "          ConverterCollection\n" +
                "            to Properties (walkingkooka.convert.ConverterToProperties)\n" +
                "            TEXT to Properties (walkingkooka.convert.ConverterTextToProperties)\n" +
                "      ConverterCustomToString\n" +
                "        \"spreadsheet-metadata\"\n" +
                "          ConverterCollection\n" +
                "            Properties to SpreadsheetMetadata (walkingkooka.spreadsheet.convert.SpreadsheetConverterPropertiesToSpreadsheetMetadata)\n" +
                "            TEXT to SpreadsheetId (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetId)\n" +
                "            SpreadsheetId to SpreadsheetMetadata (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetIdToSpreadsheetMetadata)\n" +
                "            TEXT to SpreadsheetMetadata (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetMetadata)\n" +
                "            TEXT to SpreadsheetMetadataPropertyName (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetMetadataPropertyName)\n" +
                "            TEXT to SpreadsheetName (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetName)\n" +
                "      ConverterCustomToString\n" +
                "        \"storage\"\n" +
                "          ConverterCollection\n" +
                "            TEXT to StoragePath (walkingkooka.storage.convert.StorageConverterTextToStoragePath)\n" +
                "            TEXT to Path (walkingkooka.convert.ConverterTextToPath)\n" +
                "            StorageBinary *.csv | text/csv to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueSharedCsv)\n" +
                "            StorageBinary *.env | text/x-env to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueSharedEnvironment)\n" +
                "            StorageBinary *.expression.txt | text/expression to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueSharedExpression)\n" +
                "            StorageBinary *.json | application/json to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueSharedJson)\n" +
                "            StorageBinary *.properties | text/x-java-properties to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueSharedProperties)\n" +
                "            StorageBinary *.tsv | text/tab-separated-values to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueSharedTsv)\n" +
                "            StorageBinary *.txt | text/plain to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueSharedTxt)\n" +
                "            * to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueBinary)\n" +
                "            *.csv to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinarySharedCsv)\n" +
                "            *.env to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinarySharedEnvironment)\n" +
                "            *.expression.txt to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinarySharedExpression)\n" +
                "            *.json to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinarySharedJson)\n" +
                "            *.properties to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinarySharedProperties)\n" +
                "            *.tsv to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinarySharedTsv)\n" +
                "            *.txt to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinarySharedTxt)\n" +
                "            StorageValue(Binary) to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinaryBinary)\n" +
                "            ConverterCustomToString\n" +
                "              \"BINARY\"\n" +
                "                ConverterCollection\n" +
                "                  to Binary (walkingkooka.convert.ConverterToBinary)\n" +
                "                  Binary to TEXT (walkingkooka.convert.ConverterBinaryToString)\n" +
                "                  TEXT to Binary (walkingkooka.convert.ConverterTextToBinary)\n" +
                "      ConverterCustomToString\n" +
                "        \"style\"\n" +
                "          ConverterCollection\n" +
                "            to TextStyle (walkingkooka.tree.text.convert.TreeTextConverterToTextStyle)\n" +
                "            to Styleable (walkingkooka.tree.text.convert.TreeTextConverterToStyleable)\n" +
                "            Properties to TextStyle (walkingkooka.tree.text.convert.TreeTextConverterPropertiesToTextStyle)\n" +
                "            TEXT to Border (walkingkooka.tree.text.convert.TreeTextConverterTextToBorder)\n" +
                "            TEXT to Margin (walkingkooka.tree.text.convert.TreeTextConverterTextToMargin)\n" +
                "            TEXT to Padding (walkingkooka.tree.text.convert.TreeTextConverterTextToPadding)\n" +
                "            TEXT to TextStyle (walkingkooka.tree.text.convert.TreeTextConverterTextToTextStyle)\n" +
                "            TEXT to TextStylePropertyName (walkingkooka.tree.text.convert.TreeTextConverterTextToTextStylePropertyName)\n" +
                "      ConverterCustomToString\n" +
                "        \"text-node\"\n" +
                "          ConverterCollection\n" +
                "            to TextNode (walkingkooka.tree.text.convert.TreeTextConverterToTextNode)\n" +
                "            Url to Hyperlink (walkingkooka.tree.text.convert.TreeTextConverterUrlToHyperlink)\n" +
                "            Url to Image (walkingkooka.tree.text.convert.TreeTextConverterUrlToImage)\n" +
                "            TEXT to Flag (walkingkooka.tree.text.convert.TreeTextConverterTextToFlag)\n" +
                "            TEXT to SpreadsheetText (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetText)\n" +
                "            TEXT to TextNode (walkingkooka.tree.text.convert.TreeTextConverterTextToTextNode)\n" +
                "      ConverterCustomToString\n" +
                "        \"template\"\n" +
                "          TEXT to TemplateValueName (walkingkooka.template.convert.TextToTemplateValueNameConverter)\n" +
                "      ConverterCustomToString\n" +
                "        \"net\"\n" +
                "          ConverterCollection\n" +
                "            Character or CharSequence or HasText or String to Character or CharSequence or String (walkingkooka.convert.ConverterCharacterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString)\n" +
                "            to HasHostAddress (walkingkooka.net.convert.NetConverterToHasHostAddress)\n" +
                "            to HostAddress (walkingkooka.net.convert.NetConverterToHostAddress)\n" +
                "            TEXT to HasHostAddress (walkingkooka.net.convert.NetConverterTextToHasHostAddress)\n" +
                "            TEXT to EmailAddress (walkingkooka.net.convert.NetConverterTextToEmailAddress)\n" +
                "            TEXT to HostAddress (walkingkooka.net.convert.NetConverterTextToHostAddress)\n" +
                "            TEXT to MediaType (walkingkooka.net.convert.NetConverterTextToMediaType)\n" +
                "            TEXT to Url (walkingkooka.net.convert.NetConverterTextToUrl)\n" +
                "            TEXT to UrlFragment (walkingkooka.net.convert.NetConverterTextToUrlFragment)\n" +
                "            TEXT to UrlQueryString (walkingkooka.net.convert.NetConverterTextToUrlQueryString)\n" +
                "      ConverterCustomToString\n" +
                "        \"optional-to\"\n" +
                "          Optional to (walkingkooka.convert.ConverterOptionalTo)\n" +
                "      ConverterCustomToString\n" +
                "        \"collection-to\"\n" +
                "          Collection to (walkingkooka.convert.ConverterCollectionTo)\n"
        );
    }

    @Test
    public void testFormulaConverterPrintTree() {
        this.treePrintAndCheck(
            SpreadsheetMetadataPropertyName.FORMULA_CONVERTER,
            "ConverterCustomToString\n" +
                "  \"collection (null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, json, currency, logging, plugins, properties, spreadsheet-metadata, storage, style, text-node, template, net, optional-to, collection-to)\"\n" +
                "    ConverterCollection\n" +
                "      ConverterCustomToString\n" +
                "        \"null-to-number\"\n" +
                "          null to Number (walkingkooka.spreadsheet.convert.SpreadsheetConverterNullToNumber)\n" +
                "      simple (walkingkooka.convert.ConverterSimple)\n" +
                "      ConverterCustomToString\n" +
                "        \"text\"\n" +
                "          ConverterCollection\n" +
                "            Character or CharSequence or HasText or String to Character or CharSequence or String (walkingkooka.convert.ConverterCharacterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString)\n" +
                "            TEXT to Charset (walkingkooka.convert.ConverterTextToCharset)\n" +
                "            TEXT to Indentation (walkingkooka.convert.ConverterTextToIndentation)\n" +
                "            TEXT to LineEnding (walkingkooka.convert.ConverterTextToLineEnding)\n" +
                "      ConverterCustomToString\n" +
                "        \"boolean\"\n" +
                "          ConverterCollection\n" +
                "            to Boolean (walkingkooka.spreadsheet.convert.SpreadsheetConverterToBoolean)\n" +
                "            Boolean to TEXT (walkingkooka.spreadsheet.convert.SpreadsheetConverterBooleanToText)\n" +
                "      ConverterCustomToString\n" +
                "        \"number\"\n" +
                "          ConverterCollection\n" +
                "            Number to Number (walkingkooka.tree.expression.convert.ExpressionNumberConverterNumberToNumber)\n" +
                "            to Number (walkingkooka.spreadsheet.convert.SpreadsheetConverterToNumber)\n" +
                "            Number to TEXT (walkingkooka.spreadsheet.convert.SpreadsheetConverterNumberToText)\n" +
                "      ConverterCustomToString\n" +
                "        \"date-time\"\n" +
                "          dateTime (walkingkooka.spreadsheet.convert.SpreadsheetConverterDateTime)\n" +
                "      ConverterCustomToString\n" +
                "        \"environment\"\n" +
                "          ConverterCollection\n" +
                "            to Environment (walkingkooka.environment.convert.EnvironmentConverterToEnvironment)\n" +
                "            Environment to Binary (walkingkooka.environment.convert.EnvironmentConverterEnvironmentToBinary)\n" +
                "            Environment to TEXT (walkingkooka.environment.convert.EnvironmentConverterEnvironmentToString)\n" +
                "            TEXT to Environment (walkingkooka.environment.convert.EnvironmentConverterTextToEnvironment)\n" +
                "            TEXT to EnvironmentValueName (walkingkooka.environment.convert.EnvironmentConverterTextToEnvironmentValueName)\n" +
                "      ConverterCustomToString\n" +
                "        \"locale\"\n" +
                "          ConverterCollection\n" +
                "            Locale to String (walkingkooka.convert.ConverterLocaleToString)\n" +
                "            LocaleLike to Locale (walkingkooka.convert.ConverterLocaleToLocale)\n" +
                "            LocaleLike to LocaleLanguageTag (walkingkooka.convert.ConverterLocaleToLocaleLanguageTag)\n" +
                "            ConverterCollection\n" +
                "              to DateTimeSymbols (walkingkooka.convert.ConverterToDateTimeSymbols)\n" +
                "              LocaleLike to DateTimeSymbols (walkingkooka.convert.ConverterLocaleToDateTimeSymbols)\n" +
                "              Properties to DateTimeSymbols (walkingkooka.convert.ConverterPropertiesToDateTimeSymbols)\n" +
                "            ConverterCollection\n" +
                "              to DecimalNumberSymbols (walkingkooka.convert.ConverterToDecimalNumberSymbols)\n" +
                "              LocaleLike to DecimalNumberSymbols (walkingkooka.convert.ConverterLocaleToDecimalNumberSymbols)\n" +
                "              Properties to DecimalNumberSymbols (walkingkooka.convert.ConverterPropertiesToDecimalNumberSymbols)\n" +
                "            TEXT to LocaleLanguageTag (walkingkooka.convert.ConverterTextToLocaleLanguageTag)\n" +
                "      ConverterCustomToString\n" +
                "        \"value\"\n" +
                "          ConverterCollection\n" +
                "            SpreadsheetError to Number (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetErrorToNumber)\n" +
                "            null to Number (walkingkooka.spreadsheet.convert.SpreadsheetConverterNullToNumber)\n" +
                "            ConverterCustomToString\n" +
                "              \"SPREADSHEET SELECTION\"\n" +
                "                ConverterCollection\n" +
                "                  HasSpreadsheetReference (walkingkooka.spreadsheet.convert.SpreadsheetConverterToSpreadsheetSelection)\n" +
                "                  SELECTION to SELECTION (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetSelectionToSpreadsheetSelection)\n" +
                "                  SELECTION to TEXT (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetSelectionToText)\n" +
                "                  TEXT to SELECTION (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetSelection)\n" +
                "            SpreadsheetError to SpreadsheetError (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetErrorToSpreadsheetError)\n" +
                "            TEXT to SpreadsheetError (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetError)\n" +
                "            to ValueType (walkingkooka.validation.convert.ValidationConverterToValueType)\n" +
                "            TEXT to ValueType (walkingkooka.validation.convert.ValidationConverterTextToValueType)\n" +
                "            TEXT to ZoneOffset (walkingkooka.convert.ConverterTextToZoneOffset)\n" +
                "            SpreadsheetCellSet (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetCellSet)\n" +
                "            Collection to List (walkingkooka.convert.ConverterCollectionToList)\n" +
                "            TEXT to BooleanList (walkingkooka.convert.ConverterTextToCollectionListBooleanList)\n" +
                "            TEXT to LocalDateList (walkingkooka.convert.ConverterTextToCollectionListLocalDateList)\n" +
                "            TEXT to LocalDateTimeList (walkingkooka.convert.ConverterTextToCollectionListLocalDateTimeList)\n" +
                "            TEXT to LineEnding (walkingkooka.convert.ConverterTextToLineEnding)\n" +
                "            TEXT to NumberList (walkingkooka.convert.ConverterTextToCollectionListNumberList)\n" +
                "            TEXT to LocalTimeList (walkingkooka.convert.ConverterTextToCollectionListLocalTimeList)\n" +
                "            TEXT to StringList (walkingkooka.convert.ConverterTextToCollectionListStringList)\n" +
                "            ConverterCustomToString\n" +
                "              \"CSV\"\n" +
                "                ConverterCollection\n" +
                "                  to CsvStringList (walkingkooka.convert.ConverterToCsvStringList)\n" +
                "                  TEXT to CsvStringList (walkingkooka.convert.ConverterTextToCollectionListCsvStringList)\n" +
                "                  TEXT to CsvStringSet (walkingkooka.convert.ConverterTextToCollectionSetCsvStringSet)\n" +
                "            ConverterCustomToString\n" +
                "              \"TSV\"\n" +
                "                ConverterCollection\n" +
                "                  to TsvStringList (walkingkooka.convert.ConverterToTsvStringList)\n" +
                "                  TEXT to TsvStringList (walkingkooka.convert.ConverterTextToCollectionListTsvStringList)\n" +
                "                  TEXT to TsvStringSet (walkingkooka.convert.ConverterTextToCollectionSetTsvStringSet)\n" +
                "            Binary to TEXT (walkingkooka.convert.ConverterBinaryToString)\n" +
                "            to MultiLineText (walkingkooka.convert.ConverterToMultiLineText)\n" +
                "            * to String (walkingkooka.convert.ConverterObjectToString)\n" +
                "      ConverterCustomToString\n" +
                "        \"error-throwing\"\n" +
                "          throws SpreadsheetError (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetErrorThrowing)\n" +
                "      ConverterCustomToString\n" +
                "        \"color\"\n" +
                "          ConverterCollection\n" +
                "            ConverterCustomToString\n" +
                "              \"TEXT\"\n" +
                "                ConverterCollection\n" +
                "                  Character or CharSequence or HasText or String to Character or CharSequence or String (walkingkooka.convert.ConverterCharacterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString)\n" +
                "                  TEXT to Charset (walkingkooka.convert.ConverterTextToCharset)\n" +
                "                  TEXT to Indentation (walkingkooka.convert.ConverterTextToIndentation)\n" +
                "                  TEXT to LineEnding (walkingkooka.convert.ConverterTextToLineEnding)\n" +
                "            Color to Color (walkingkooka.color.convert.ConverterColorToColor)\n" +
                "            TEXT to Color (walkingkooka.color.convert.ConverterTextToColor)\n" +
                "            Color to Number (walkingkooka.color.convert.ConverterColorToNumber)\n" +
                "            Number to Color (walkingkooka.color.convert.ConverterNumberToColor)\n" +
                "            to ColorProperties (walkingkooka.color.convert.ConverterToColorProperties)\n" +
                "            TEXT to SpreadsheetColorName (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetColorName)\n" +
                "            TEXT to SpreadsheetMetadata Color (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetMetadataColor)\n" +
                "            ConverterCustomToString\n" +
                "              \"PROPERTIES\"\n" +
                "                ConverterCollection\n" +
                "                  to Properties (walkingkooka.convert.ConverterToProperties)\n" +
                "                  TEXT to Properties (walkingkooka.convert.ConverterTextToProperties)\n" +
                "      ConverterCustomToString\n" +
                "        \"expression\"\n" +
                "          TEXT to Expression (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToExpression)\n" +
                "      ConverterCustomToString\n" +
                "        \"json\"\n" +
                "          ConverterCollection\n" +
                "            JsonNode to type (walkingkooka.tree.json.convert.JsonNodeConverterJsonNodeTo)\n" +
                "            Json TEXT to Object (walkingkooka.tree.json.convert.JsonNodeConverterTextToObject)\n" +
                "            * to JsonNode (walkingkooka.tree.json.convert.JsonNodeConverterToJsonNode)\n" +
                "            TEXT to JsonNode (walkingkooka.tree.json.convert.JsonNodeConverterTextToJsonNode)\n" +
                "            TEXT to JsonPointer (walkingkooka.tree.json.convert.JsonNodeConverterTextToJsonPointer)\n" +
                "            TEXT to JsonSelector (walkingkooka.tree.json.convert.JsonNodeConverterTextToJsonSelector)\n" +
                "      ConverterCustomToString\n" +
                "        \"currency\"\n" +
                "          ConverterCollection\n" +
                "            to Currency (walkingkooka.convert.ConverterToCurrency)\n" +
                "            to CurrencyCode (walkingkooka.convert.ConverterToCurrencyCode)\n" +
                "            CurrencyCode to Currency (walkingkooka.convert.ConverterCurrencyCodeToCurrency)\n" +
                "            CurrencyValue to Number (walkingkooka.convert.ConverterCurrencyValueToNumber)\n" +
                "            CurrencyValue to (walkingkooka.convert.ConverterCurrencyValueTo)\n" +
                "            Number to CurrencyValue (walkingkooka.convert.ConverterNumberToCurrencyValue)\n" +
                "            TEXT to Currency (walkingkooka.convert.ConverterTextToCurrency)\n" +
                "            TEXT to CurrencyCode (walkingkooka.convert.ConverterTextToCurrencyCode)\n" +
                "            TEXT to CurrencyValue (walkingkooka.convert.ConverterTextToCurrencyValue)\n" +
                "      ConverterCustomToString\n" +
                "        \"logging\"\n" +
                "          TEXT to LoggingLevel (walkingkooka.convert.ConverterTextToLoggingLevel)\n" +
                "      ConverterCustomToString\n" +
                "        \"plugins\"\n" +
                "          ConverterCollection\n" +
                "            to SpreadsheetFormatterSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterToSpreadsheetFormatterSelector)\n" +
                "            to SpreadsheetParserSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterToSpreadsheetParserSelector)\n" +
                "            to ValidatorSelector (walkingkooka.validation.convert.ValidationConverterToValidatorSelector)\n" +
                "            TEXT to ConverterSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToConverterSelector)\n" +
                "            TEXT to CurrencyExchangeRaterSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToCurrencyExchangeRaterSelector)\n" +
                "            TEXT to ExpressionFunctionSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToExpressionFunctionSelector)\n" +
                "            TEXT to SpreadsheetComparatorSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetComparatorSelector)\n" +
                "            TEXT to SpreadsheetFormatterSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetFormatterSelector)\n" +
                "            TEXT to SpreadsheetParserSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetParserSelector)\n" +
                "            TEXT to ValidatorSelector (walkingkooka.validation.convert.ValidationConverterTextToValidatorSelector)\n" +
                "      ConverterCustomToString\n" +
                "        \"properties\"\n" +
                "          ConverterCollection\n" +
                "            to Properties (walkingkooka.convert.ConverterToProperties)\n" +
                "            TEXT to Properties (walkingkooka.convert.ConverterTextToProperties)\n" +
                "      ConverterCustomToString\n" +
                "        \"spreadsheet-metadata\"\n" +
                "          ConverterCollection\n" +
                "            Properties to SpreadsheetMetadata (walkingkooka.spreadsheet.convert.SpreadsheetConverterPropertiesToSpreadsheetMetadata)\n" +
                "            TEXT to SpreadsheetId (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetId)\n" +
                "            SpreadsheetId to SpreadsheetMetadata (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetIdToSpreadsheetMetadata)\n" +
                "            TEXT to SpreadsheetMetadata (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetMetadata)\n" +
                "            TEXT to SpreadsheetMetadataPropertyName (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetMetadataPropertyName)\n" +
                "            TEXT to SpreadsheetName (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetName)\n" +
                "      ConverterCustomToString\n" +
                "        \"storage\"\n" +
                "          ConverterCollection\n" +
                "            TEXT to StoragePath (walkingkooka.storage.convert.StorageConverterTextToStoragePath)\n" +
                "            TEXT to Path (walkingkooka.convert.ConverterTextToPath)\n" +
                "            StorageBinary *.csv | text/csv to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueSharedCsv)\n" +
                "            StorageBinary *.env | text/x-env to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueSharedEnvironment)\n" +
                "            StorageBinary *.expression.txt | text/expression to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueSharedExpression)\n" +
                "            StorageBinary *.json | application/json to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueSharedJson)\n" +
                "            StorageBinary *.properties | text/x-java-properties to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueSharedProperties)\n" +
                "            StorageBinary *.tsv | text/tab-separated-values to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueSharedTsv)\n" +
                "            StorageBinary *.txt | text/plain to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueSharedTxt)\n" +
                "            * to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueBinary)\n" +
                "            *.csv to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinarySharedCsv)\n" +
                "            *.env to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinarySharedEnvironment)\n" +
                "            *.expression.txt to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinarySharedExpression)\n" +
                "            *.json to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinarySharedJson)\n" +
                "            *.properties to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinarySharedProperties)\n" +
                "            *.tsv to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinarySharedTsv)\n" +
                "            *.txt to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinarySharedTxt)\n" +
                "            StorageValue(Binary) to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinaryBinary)\n" +
                "            ConverterCustomToString\n" +
                "              \"BINARY\"\n" +
                "                ConverterCollection\n" +
                "                  to Binary (walkingkooka.convert.ConverterToBinary)\n" +
                "                  Binary to TEXT (walkingkooka.convert.ConverterBinaryToString)\n" +
                "                  TEXT to Binary (walkingkooka.convert.ConverterTextToBinary)\n" +
                "      ConverterCustomToString\n" +
                "        \"style\"\n" +
                "          ConverterCollection\n" +
                "            to TextStyle (walkingkooka.tree.text.convert.TreeTextConverterToTextStyle)\n" +
                "            to Styleable (walkingkooka.tree.text.convert.TreeTextConverterToStyleable)\n" +
                "            Properties to TextStyle (walkingkooka.tree.text.convert.TreeTextConverterPropertiesToTextStyle)\n" +
                "            TEXT to Border (walkingkooka.tree.text.convert.TreeTextConverterTextToBorder)\n" +
                "            TEXT to Margin (walkingkooka.tree.text.convert.TreeTextConverterTextToMargin)\n" +
                "            TEXT to Padding (walkingkooka.tree.text.convert.TreeTextConverterTextToPadding)\n" +
                "            TEXT to TextStyle (walkingkooka.tree.text.convert.TreeTextConverterTextToTextStyle)\n" +
                "            TEXT to TextStylePropertyName (walkingkooka.tree.text.convert.TreeTextConverterTextToTextStylePropertyName)\n" +
                "      ConverterCustomToString\n" +
                "        \"text-node\"\n" +
                "          ConverterCollection\n" +
                "            to TextNode (walkingkooka.tree.text.convert.TreeTextConverterToTextNode)\n" +
                "            Url to Hyperlink (walkingkooka.tree.text.convert.TreeTextConverterUrlToHyperlink)\n" +
                "            Url to Image (walkingkooka.tree.text.convert.TreeTextConverterUrlToImage)\n" +
                "            TEXT to Flag (walkingkooka.tree.text.convert.TreeTextConverterTextToFlag)\n" +
                "            TEXT to SpreadsheetText (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetText)\n" +
                "            TEXT to TextNode (walkingkooka.tree.text.convert.TreeTextConverterTextToTextNode)\n" +
                "      ConverterCustomToString\n" +
                "        \"template\"\n" +
                "          TEXT to TemplateValueName (walkingkooka.template.convert.TextToTemplateValueNameConverter)\n" +
                "      ConverterCustomToString\n" +
                "        \"net\"\n" +
                "          ConverterCollection\n" +
                "            Character or CharSequence or HasText or String to Character or CharSequence or String (walkingkooka.convert.ConverterCharacterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString)\n" +
                "            to HasHostAddress (walkingkooka.net.convert.NetConverterToHasHostAddress)\n" +
                "            to HostAddress (walkingkooka.net.convert.NetConverterToHostAddress)\n" +
                "            TEXT to HasHostAddress (walkingkooka.net.convert.NetConverterTextToHasHostAddress)\n" +
                "            TEXT to EmailAddress (walkingkooka.net.convert.NetConverterTextToEmailAddress)\n" +
                "            TEXT to HostAddress (walkingkooka.net.convert.NetConverterTextToHostAddress)\n" +
                "            TEXT to MediaType (walkingkooka.net.convert.NetConverterTextToMediaType)\n" +
                "            TEXT to Url (walkingkooka.net.convert.NetConverterTextToUrl)\n" +
                "            TEXT to UrlFragment (walkingkooka.net.convert.NetConverterTextToUrlFragment)\n" +
                "            TEXT to UrlQueryString (walkingkooka.net.convert.NetConverterTextToUrlQueryString)\n" +
                "      ConverterCustomToString\n" +
                "        \"optional-to\"\n" +
                "          Optional to (walkingkooka.convert.ConverterOptionalTo)\n" +
                "      ConverterCustomToString\n" +
                "        \"collection-to\"\n" +
                "          Collection to (walkingkooka.convert.ConverterCollectionTo)\n"
        );
    }

    @Test
    public void testQueryConverterPrintTree() {
        this.treePrintAndCheck(
            SpreadsheetMetadataPropertyName.QUERY_CONVERTER,
            "ConverterCustomToString\n" +
                "  \"collection (null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, properties, spreadsheet-metadata, style, text-node, template, net, optional-to, collection-to)\"\n" +
                "    ConverterCollection\n" +
                "      ConverterCustomToString\n" +
                "        \"null-to-number\"\n" +
                "          null to Number (walkingkooka.spreadsheet.convert.SpreadsheetConverterNullToNumber)\n" +
                "      simple (walkingkooka.convert.ConverterSimple)\n" +
                "      ConverterCustomToString\n" +
                "        \"text\"\n" +
                "          ConverterCollection\n" +
                "            Character or CharSequence or HasText or String to Character or CharSequence or String (walkingkooka.convert.ConverterCharacterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString)\n" +
                "            TEXT to Charset (walkingkooka.convert.ConverterTextToCharset)\n" +
                "            TEXT to Indentation (walkingkooka.convert.ConverterTextToIndentation)\n" +
                "            TEXT to LineEnding (walkingkooka.convert.ConverterTextToLineEnding)\n" +
                "      ConverterCustomToString\n" +
                "        \"boolean\"\n" +
                "          ConverterCollection\n" +
                "            to Boolean (walkingkooka.spreadsheet.convert.SpreadsheetConverterToBoolean)\n" +
                "            Boolean to TEXT (walkingkooka.spreadsheet.convert.SpreadsheetConverterBooleanToText)\n" +
                "      ConverterCustomToString\n" +
                "        \"number\"\n" +
                "          ConverterCollection\n" +
                "            Number to Number (walkingkooka.tree.expression.convert.ExpressionNumberConverterNumberToNumber)\n" +
                "            to Number (walkingkooka.spreadsheet.convert.SpreadsheetConverterToNumber)\n" +
                "            Number to TEXT (walkingkooka.spreadsheet.convert.SpreadsheetConverterNumberToText)\n" +
                "      ConverterCustomToString\n" +
                "        \"date-time\"\n" +
                "          dateTime (walkingkooka.spreadsheet.convert.SpreadsheetConverterDateTime)\n" +
                "      ConverterCustomToString\n" +
                "        \"environment\"\n" +
                "          ConverterCollection\n" +
                "            to Environment (walkingkooka.environment.convert.EnvironmentConverterToEnvironment)\n" +
                "            Environment to Binary (walkingkooka.environment.convert.EnvironmentConverterEnvironmentToBinary)\n" +
                "            Environment to TEXT (walkingkooka.environment.convert.EnvironmentConverterEnvironmentToString)\n" +
                "            TEXT to Environment (walkingkooka.environment.convert.EnvironmentConverterTextToEnvironment)\n" +
                "            TEXT to EnvironmentValueName (walkingkooka.environment.convert.EnvironmentConverterTextToEnvironmentValueName)\n" +
                "      ConverterCustomToString\n" +
                "        \"locale\"\n" +
                "          ConverterCollection\n" +
                "            Locale to String (walkingkooka.convert.ConverterLocaleToString)\n" +
                "            LocaleLike to Locale (walkingkooka.convert.ConverterLocaleToLocale)\n" +
                "            LocaleLike to LocaleLanguageTag (walkingkooka.convert.ConverterLocaleToLocaleLanguageTag)\n" +
                "            ConverterCollection\n" +
                "              to DateTimeSymbols (walkingkooka.convert.ConverterToDateTimeSymbols)\n" +
                "              LocaleLike to DateTimeSymbols (walkingkooka.convert.ConverterLocaleToDateTimeSymbols)\n" +
                "              Properties to DateTimeSymbols (walkingkooka.convert.ConverterPropertiesToDateTimeSymbols)\n" +
                "            ConverterCollection\n" +
                "              to DecimalNumberSymbols (walkingkooka.convert.ConverterToDecimalNumberSymbols)\n" +
                "              LocaleLike to DecimalNumberSymbols (walkingkooka.convert.ConverterLocaleToDecimalNumberSymbols)\n" +
                "              Properties to DecimalNumberSymbols (walkingkooka.convert.ConverterPropertiesToDecimalNumberSymbols)\n" +
                "            TEXT to LocaleLanguageTag (walkingkooka.convert.ConverterTextToLocaleLanguageTag)\n" +
                "      ConverterCustomToString\n" +
                "        \"value\"\n" +
                "          ConverterCollection\n" +
                "            SpreadsheetError to Number (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetErrorToNumber)\n" +
                "            null to Number (walkingkooka.spreadsheet.convert.SpreadsheetConverterNullToNumber)\n" +
                "            ConverterCustomToString\n" +
                "              \"SPREADSHEET SELECTION\"\n" +
                "                ConverterCollection\n" +
                "                  HasSpreadsheetReference (walkingkooka.spreadsheet.convert.SpreadsheetConverterToSpreadsheetSelection)\n" +
                "                  SELECTION to SELECTION (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetSelectionToSpreadsheetSelection)\n" +
                "                  SELECTION to TEXT (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetSelectionToText)\n" +
                "                  TEXT to SELECTION (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetSelection)\n" +
                "            SpreadsheetError to SpreadsheetError (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetErrorToSpreadsheetError)\n" +
                "            TEXT to SpreadsheetError (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetError)\n" +
                "            to ValueType (walkingkooka.validation.convert.ValidationConverterToValueType)\n" +
                "            TEXT to ValueType (walkingkooka.validation.convert.ValidationConverterTextToValueType)\n" +
                "            TEXT to ZoneOffset (walkingkooka.convert.ConverterTextToZoneOffset)\n" +
                "            SpreadsheetCellSet (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetCellSet)\n" +
                "            Collection to List (walkingkooka.convert.ConverterCollectionToList)\n" +
                "            TEXT to BooleanList (walkingkooka.convert.ConverterTextToCollectionListBooleanList)\n" +
                "            TEXT to LocalDateList (walkingkooka.convert.ConverterTextToCollectionListLocalDateList)\n" +
                "            TEXT to LocalDateTimeList (walkingkooka.convert.ConverterTextToCollectionListLocalDateTimeList)\n" +
                "            TEXT to LineEnding (walkingkooka.convert.ConverterTextToLineEnding)\n" +
                "            TEXT to NumberList (walkingkooka.convert.ConverterTextToCollectionListNumberList)\n" +
                "            TEXT to LocalTimeList (walkingkooka.convert.ConverterTextToCollectionListLocalTimeList)\n" +
                "            TEXT to StringList (walkingkooka.convert.ConverterTextToCollectionListStringList)\n" +
                "            ConverterCustomToString\n" +
                "              \"CSV\"\n" +
                "                ConverterCollection\n" +
                "                  to CsvStringList (walkingkooka.convert.ConverterToCsvStringList)\n" +
                "                  TEXT to CsvStringList (walkingkooka.convert.ConverterTextToCollectionListCsvStringList)\n" +
                "                  TEXT to CsvStringSet (walkingkooka.convert.ConverterTextToCollectionSetCsvStringSet)\n" +
                "            ConverterCustomToString\n" +
                "              \"TSV\"\n" +
                "                ConverterCollection\n" +
                "                  to TsvStringList (walkingkooka.convert.ConverterToTsvStringList)\n" +
                "                  TEXT to TsvStringList (walkingkooka.convert.ConverterTextToCollectionListTsvStringList)\n" +
                "                  TEXT to TsvStringSet (walkingkooka.convert.ConverterTextToCollectionSetTsvStringSet)\n" +
                "            Binary to TEXT (walkingkooka.convert.ConverterBinaryToString)\n" +
                "            to MultiLineText (walkingkooka.convert.ConverterToMultiLineText)\n" +
                "            * to String (walkingkooka.convert.ConverterObjectToString)\n" +
                "      ConverterCustomToString\n" +
                "        \"error-throwing\"\n" +
                "          throws SpreadsheetError (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetErrorThrowing)\n" +
                "      ConverterCustomToString\n" +
                "        \"color\"\n" +
                "          ConverterCollection\n" +
                "            ConverterCustomToString\n" +
                "              \"TEXT\"\n" +
                "                ConverterCollection\n" +
                "                  Character or CharSequence or HasText or String to Character or CharSequence or String (walkingkooka.convert.ConverterCharacterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString)\n" +
                "                  TEXT to Charset (walkingkooka.convert.ConverterTextToCharset)\n" +
                "                  TEXT to Indentation (walkingkooka.convert.ConverterTextToIndentation)\n" +
                "                  TEXT to LineEnding (walkingkooka.convert.ConverterTextToLineEnding)\n" +
                "            Color to Color (walkingkooka.color.convert.ConverterColorToColor)\n" +
                "            TEXT to Color (walkingkooka.color.convert.ConverterTextToColor)\n" +
                "            Color to Number (walkingkooka.color.convert.ConverterColorToNumber)\n" +
                "            Number to Color (walkingkooka.color.convert.ConverterNumberToColor)\n" +
                "            to ColorProperties (walkingkooka.color.convert.ConverterToColorProperties)\n" +
                "            TEXT to SpreadsheetColorName (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetColorName)\n" +
                "            TEXT to SpreadsheetMetadata Color (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetMetadataColor)\n" +
                "            ConverterCustomToString\n" +
                "              \"PROPERTIES\"\n" +
                "                ConverterCollection\n" +
                "                  to Properties (walkingkooka.convert.ConverterToProperties)\n" +
                "                  TEXT to Properties (walkingkooka.convert.ConverterTextToProperties)\n" +
                "      ConverterCustomToString\n" +
                "        \"expression\"\n" +
                "          TEXT to Expression (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToExpression)\n" +
                "      ConverterCustomToString\n" +
                "        \"properties\"\n" +
                "          ConverterCollection\n" +
                "            to Properties (walkingkooka.convert.ConverterToProperties)\n" +
                "            TEXT to Properties (walkingkooka.convert.ConverterTextToProperties)\n" +
                "      ConverterCustomToString\n" +
                "        \"spreadsheet-metadata\"\n" +
                "          ConverterCollection\n" +
                "            Properties to SpreadsheetMetadata (walkingkooka.spreadsheet.convert.SpreadsheetConverterPropertiesToSpreadsheetMetadata)\n" +
                "            TEXT to SpreadsheetId (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetId)\n" +
                "            SpreadsheetId to SpreadsheetMetadata (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetIdToSpreadsheetMetadata)\n" +
                "            TEXT to SpreadsheetMetadata (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetMetadata)\n" +
                "            TEXT to SpreadsheetMetadataPropertyName (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetMetadataPropertyName)\n" +
                "            TEXT to SpreadsheetName (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetName)\n" +
                "      ConverterCustomToString\n" +
                "        \"style\"\n" +
                "          ConverterCollection\n" +
                "            to TextStyle (walkingkooka.tree.text.convert.TreeTextConverterToTextStyle)\n" +
                "            to Styleable (walkingkooka.tree.text.convert.TreeTextConverterToStyleable)\n" +
                "            Properties to TextStyle (walkingkooka.tree.text.convert.TreeTextConverterPropertiesToTextStyle)\n" +
                "            TEXT to Border (walkingkooka.tree.text.convert.TreeTextConverterTextToBorder)\n" +
                "            TEXT to Margin (walkingkooka.tree.text.convert.TreeTextConverterTextToMargin)\n" +
                "            TEXT to Padding (walkingkooka.tree.text.convert.TreeTextConverterTextToPadding)\n" +
                "            TEXT to TextStyle (walkingkooka.tree.text.convert.TreeTextConverterTextToTextStyle)\n" +
                "            TEXT to TextStylePropertyName (walkingkooka.tree.text.convert.TreeTextConverterTextToTextStylePropertyName)\n" +
                "      ConverterCustomToString\n" +
                "        \"text-node\"\n" +
                "          ConverterCollection\n" +
                "            to TextNode (walkingkooka.tree.text.convert.TreeTextConverterToTextNode)\n" +
                "            Url to Hyperlink (walkingkooka.tree.text.convert.TreeTextConverterUrlToHyperlink)\n" +
                "            Url to Image (walkingkooka.tree.text.convert.TreeTextConverterUrlToImage)\n" +
                "            TEXT to Flag (walkingkooka.tree.text.convert.TreeTextConverterTextToFlag)\n" +
                "            TEXT to SpreadsheetText (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetText)\n" +
                "            TEXT to TextNode (walkingkooka.tree.text.convert.TreeTextConverterTextToTextNode)\n" +
                "      ConverterCustomToString\n" +
                "        \"template\"\n" +
                "          TEXT to TemplateValueName (walkingkooka.template.convert.TextToTemplateValueNameConverter)\n" +
                "      ConverterCustomToString\n" +
                "        \"net\"\n" +
                "          ConverterCollection\n" +
                "            Character or CharSequence or HasText or String to Character or CharSequence or String (walkingkooka.convert.ConverterCharacterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString)\n" +
                "            to HasHostAddress (walkingkooka.net.convert.NetConverterToHasHostAddress)\n" +
                "            to HostAddress (walkingkooka.net.convert.NetConverterToHostAddress)\n" +
                "            TEXT to HasHostAddress (walkingkooka.net.convert.NetConverterTextToHasHostAddress)\n" +
                "            TEXT to EmailAddress (walkingkooka.net.convert.NetConverterTextToEmailAddress)\n" +
                "            TEXT to HostAddress (walkingkooka.net.convert.NetConverterTextToHostAddress)\n" +
                "            TEXT to MediaType (walkingkooka.net.convert.NetConverterTextToMediaType)\n" +
                "            TEXT to Url (walkingkooka.net.convert.NetConverterTextToUrl)\n" +
                "            TEXT to UrlFragment (walkingkooka.net.convert.NetConverterTextToUrlFragment)\n" +
                "            TEXT to UrlQueryString (walkingkooka.net.convert.NetConverterTextToUrlQueryString)\n" +
                "      ConverterCustomToString\n" +
                "        \"optional-to\"\n" +
                "          Optional to (walkingkooka.convert.ConverterOptionalTo)\n" +
                "      ConverterCustomToString\n" +
                "        \"collection-to\"\n" +
                "          Collection to (walkingkooka.convert.ConverterCollectionTo)\n"
        );
    }

    @Test
    public void testScriptingConverterPrintTree() {
        this.treePrintAndCheck(
            SpreadsheetMetadataPropertyName.SCRIPTING_CONVERTER,
            "ConverterCustomToString\n" +
                "  \"collection (null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, json, currency, logging, plugins, properties, spreadsheet-metadata, storage, style, text-node, template, net, optional-to, collection-to)\"\n" +
                "    ConverterCollection\n" +
                "      ConverterCustomToString\n" +
                "        \"null-to-number\"\n" +
                "          null to Number (walkingkooka.spreadsheet.convert.SpreadsheetConverterNullToNumber)\n" +
                "      simple (walkingkooka.convert.ConverterSimple)\n" +
                "      ConverterCustomToString\n" +
                "        \"text\"\n" +
                "          ConverterCollection\n" +
                "            Character or CharSequence or HasText or String to Character or CharSequence or String (walkingkooka.convert.ConverterCharacterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString)\n" +
                "            TEXT to Charset (walkingkooka.convert.ConverterTextToCharset)\n" +
                "            TEXT to Indentation (walkingkooka.convert.ConverterTextToIndentation)\n" +
                "            TEXT to LineEnding (walkingkooka.convert.ConverterTextToLineEnding)\n" +
                "      ConverterCustomToString\n" +
                "        \"boolean\"\n" +
                "          ConverterCollection\n" +
                "            to Boolean (walkingkooka.spreadsheet.convert.SpreadsheetConverterToBoolean)\n" +
                "            Boolean to TEXT (walkingkooka.spreadsheet.convert.SpreadsheetConverterBooleanToText)\n" +
                "      ConverterCustomToString\n" +
                "        \"number\"\n" +
                "          ConverterCollection\n" +
                "            Number to Number (walkingkooka.tree.expression.convert.ExpressionNumberConverterNumberToNumber)\n" +
                "            to Number (walkingkooka.spreadsheet.convert.SpreadsheetConverterToNumber)\n" +
                "            Number to TEXT (walkingkooka.spreadsheet.convert.SpreadsheetConverterNumberToText)\n" +
                "      ConverterCustomToString\n" +
                "        \"date-time\"\n" +
                "          dateTime (walkingkooka.spreadsheet.convert.SpreadsheetConverterDateTime)\n" +
                "      ConverterCustomToString\n" +
                "        \"environment\"\n" +
                "          ConverterCollection\n" +
                "            to Environment (walkingkooka.environment.convert.EnvironmentConverterToEnvironment)\n" +
                "            Environment to Binary (walkingkooka.environment.convert.EnvironmentConverterEnvironmentToBinary)\n" +
                "            Environment to TEXT (walkingkooka.environment.convert.EnvironmentConverterEnvironmentToString)\n" +
                "            TEXT to Environment (walkingkooka.environment.convert.EnvironmentConverterTextToEnvironment)\n" +
                "            TEXT to EnvironmentValueName (walkingkooka.environment.convert.EnvironmentConverterTextToEnvironmentValueName)\n" +
                "      ConverterCustomToString\n" +
                "        \"locale\"\n" +
                "          ConverterCollection\n" +
                "            Locale to String (walkingkooka.convert.ConverterLocaleToString)\n" +
                "            LocaleLike to Locale (walkingkooka.convert.ConverterLocaleToLocale)\n" +
                "            LocaleLike to LocaleLanguageTag (walkingkooka.convert.ConverterLocaleToLocaleLanguageTag)\n" +
                "            ConverterCollection\n" +
                "              to DateTimeSymbols (walkingkooka.convert.ConverterToDateTimeSymbols)\n" +
                "              LocaleLike to DateTimeSymbols (walkingkooka.convert.ConverterLocaleToDateTimeSymbols)\n" +
                "              Properties to DateTimeSymbols (walkingkooka.convert.ConverterPropertiesToDateTimeSymbols)\n" +
                "            ConverterCollection\n" +
                "              to DecimalNumberSymbols (walkingkooka.convert.ConverterToDecimalNumberSymbols)\n" +
                "              LocaleLike to DecimalNumberSymbols (walkingkooka.convert.ConverterLocaleToDecimalNumberSymbols)\n" +
                "              Properties to DecimalNumberSymbols (walkingkooka.convert.ConverterPropertiesToDecimalNumberSymbols)\n" +
                "            TEXT to LocaleLanguageTag (walkingkooka.convert.ConverterTextToLocaleLanguageTag)\n" +
                "      ConverterCustomToString\n" +
                "        \"value\"\n" +
                "          ConverterCollection\n" +
                "            SpreadsheetError to Number (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetErrorToNumber)\n" +
                "            null to Number (walkingkooka.spreadsheet.convert.SpreadsheetConverterNullToNumber)\n" +
                "            ConverterCustomToString\n" +
                "              \"SPREADSHEET SELECTION\"\n" +
                "                ConverterCollection\n" +
                "                  HasSpreadsheetReference (walkingkooka.spreadsheet.convert.SpreadsheetConverterToSpreadsheetSelection)\n" +
                "                  SELECTION to SELECTION (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetSelectionToSpreadsheetSelection)\n" +
                "                  SELECTION to TEXT (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetSelectionToText)\n" +
                "                  TEXT to SELECTION (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetSelection)\n" +
                "            SpreadsheetError to SpreadsheetError (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetErrorToSpreadsheetError)\n" +
                "            TEXT to SpreadsheetError (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetError)\n" +
                "            to ValueType (walkingkooka.validation.convert.ValidationConverterToValueType)\n" +
                "            TEXT to ValueType (walkingkooka.validation.convert.ValidationConverterTextToValueType)\n" +
                "            TEXT to ZoneOffset (walkingkooka.convert.ConverterTextToZoneOffset)\n" +
                "            SpreadsheetCellSet (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetCellSet)\n" +
                "            Collection to List (walkingkooka.convert.ConverterCollectionToList)\n" +
                "            TEXT to BooleanList (walkingkooka.convert.ConverterTextToCollectionListBooleanList)\n" +
                "            TEXT to LocalDateList (walkingkooka.convert.ConverterTextToCollectionListLocalDateList)\n" +
                "            TEXT to LocalDateTimeList (walkingkooka.convert.ConverterTextToCollectionListLocalDateTimeList)\n" +
                "            TEXT to LineEnding (walkingkooka.convert.ConverterTextToLineEnding)\n" +
                "            TEXT to NumberList (walkingkooka.convert.ConverterTextToCollectionListNumberList)\n" +
                "            TEXT to LocalTimeList (walkingkooka.convert.ConverterTextToCollectionListLocalTimeList)\n" +
                "            TEXT to StringList (walkingkooka.convert.ConverterTextToCollectionListStringList)\n" +
                "            ConverterCustomToString\n" +
                "              \"CSV\"\n" +
                "                ConverterCollection\n" +
                "                  to CsvStringList (walkingkooka.convert.ConverterToCsvStringList)\n" +
                "                  TEXT to CsvStringList (walkingkooka.convert.ConverterTextToCollectionListCsvStringList)\n" +
                "                  TEXT to CsvStringSet (walkingkooka.convert.ConverterTextToCollectionSetCsvStringSet)\n" +
                "            ConverterCustomToString\n" +
                "              \"TSV\"\n" +
                "                ConverterCollection\n" +
                "                  to TsvStringList (walkingkooka.convert.ConverterToTsvStringList)\n" +
                "                  TEXT to TsvStringList (walkingkooka.convert.ConverterTextToCollectionListTsvStringList)\n" +
                "                  TEXT to TsvStringSet (walkingkooka.convert.ConverterTextToCollectionSetTsvStringSet)\n" +
                "            Binary to TEXT (walkingkooka.convert.ConverterBinaryToString)\n" +
                "            to MultiLineText (walkingkooka.convert.ConverterToMultiLineText)\n" +
                "            * to String (walkingkooka.convert.ConverterObjectToString)\n" +
                "      ConverterCustomToString\n" +
                "        \"error-throwing\"\n" +
                "          throws SpreadsheetError (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetErrorThrowing)\n" +
                "      ConverterCustomToString\n" +
                "        \"color\"\n" +
                "          ConverterCollection\n" +
                "            ConverterCustomToString\n" +
                "              \"TEXT\"\n" +
                "                ConverterCollection\n" +
                "                  Character or CharSequence or HasText or String to Character or CharSequence or String (walkingkooka.convert.ConverterCharacterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString)\n" +
                "                  TEXT to Charset (walkingkooka.convert.ConverterTextToCharset)\n" +
                "                  TEXT to Indentation (walkingkooka.convert.ConverterTextToIndentation)\n" +
                "                  TEXT to LineEnding (walkingkooka.convert.ConverterTextToLineEnding)\n" +
                "            Color to Color (walkingkooka.color.convert.ConverterColorToColor)\n" +
                "            TEXT to Color (walkingkooka.color.convert.ConverterTextToColor)\n" +
                "            Color to Number (walkingkooka.color.convert.ConverterColorToNumber)\n" +
                "            Number to Color (walkingkooka.color.convert.ConverterNumberToColor)\n" +
                "            to ColorProperties (walkingkooka.color.convert.ConverterToColorProperties)\n" +
                "            TEXT to SpreadsheetColorName (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetColorName)\n" +
                "            TEXT to SpreadsheetMetadata Color (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetMetadataColor)\n" +
                "            ConverterCustomToString\n" +
                "              \"PROPERTIES\"\n" +
                "                ConverterCollection\n" +
                "                  to Properties (walkingkooka.convert.ConverterToProperties)\n" +
                "                  TEXT to Properties (walkingkooka.convert.ConverterTextToProperties)\n" +
                "      ConverterCustomToString\n" +
                "        \"expression\"\n" +
                "          TEXT to Expression (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToExpression)\n" +
                "      ConverterCustomToString\n" +
                "        \"json\"\n" +
                "          ConverterCollection\n" +
                "            JsonNode to type (walkingkooka.tree.json.convert.JsonNodeConverterJsonNodeTo)\n" +
                "            Json TEXT to Object (walkingkooka.tree.json.convert.JsonNodeConverterTextToObject)\n" +
                "            * to JsonNode (walkingkooka.tree.json.convert.JsonNodeConverterToJsonNode)\n" +
                "            TEXT to JsonNode (walkingkooka.tree.json.convert.JsonNodeConverterTextToJsonNode)\n" +
                "            TEXT to JsonPointer (walkingkooka.tree.json.convert.JsonNodeConverterTextToJsonPointer)\n" +
                "            TEXT to JsonSelector (walkingkooka.tree.json.convert.JsonNodeConverterTextToJsonSelector)\n" +
                "      ConverterCustomToString\n" +
                "        \"currency\"\n" +
                "          ConverterCollection\n" +
                "            to Currency (walkingkooka.convert.ConverterToCurrency)\n" +
                "            to CurrencyCode (walkingkooka.convert.ConverterToCurrencyCode)\n" +
                "            CurrencyCode to Currency (walkingkooka.convert.ConverterCurrencyCodeToCurrency)\n" +
                "            CurrencyValue to Number (walkingkooka.convert.ConverterCurrencyValueToNumber)\n" +
                "            CurrencyValue to (walkingkooka.convert.ConverterCurrencyValueTo)\n" +
                "            Number to CurrencyValue (walkingkooka.convert.ConverterNumberToCurrencyValue)\n" +
                "            TEXT to Currency (walkingkooka.convert.ConverterTextToCurrency)\n" +
                "            TEXT to CurrencyCode (walkingkooka.convert.ConverterTextToCurrencyCode)\n" +
                "            TEXT to CurrencyValue (walkingkooka.convert.ConverterTextToCurrencyValue)\n" +
                "      ConverterCustomToString\n" +
                "        \"logging\"\n" +
                "          TEXT to LoggingLevel (walkingkooka.convert.ConverterTextToLoggingLevel)\n" +
                "      ConverterCustomToString\n" +
                "        \"plugins\"\n" +
                "          ConverterCollection\n" +
                "            to SpreadsheetFormatterSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterToSpreadsheetFormatterSelector)\n" +
                "            to SpreadsheetParserSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterToSpreadsheetParserSelector)\n" +
                "            to ValidatorSelector (walkingkooka.validation.convert.ValidationConverterToValidatorSelector)\n" +
                "            TEXT to ConverterSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToConverterSelector)\n" +
                "            TEXT to CurrencyExchangeRaterSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToCurrencyExchangeRaterSelector)\n" +
                "            TEXT to ExpressionFunctionSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToExpressionFunctionSelector)\n" +
                "            TEXT to SpreadsheetComparatorSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetComparatorSelector)\n" +
                "            TEXT to SpreadsheetFormatterSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetFormatterSelector)\n" +
                "            TEXT to SpreadsheetParserSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetParserSelector)\n" +
                "            TEXT to ValidatorSelector (walkingkooka.validation.convert.ValidationConverterTextToValidatorSelector)\n" +
                "      ConverterCustomToString\n" +
                "        \"properties\"\n" +
                "          ConverterCollection\n" +
                "            to Properties (walkingkooka.convert.ConverterToProperties)\n" +
                "            TEXT to Properties (walkingkooka.convert.ConverterTextToProperties)\n" +
                "      ConverterCustomToString\n" +
                "        \"spreadsheet-metadata\"\n" +
                "          ConverterCollection\n" +
                "            Properties to SpreadsheetMetadata (walkingkooka.spreadsheet.convert.SpreadsheetConverterPropertiesToSpreadsheetMetadata)\n" +
                "            TEXT to SpreadsheetId (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetId)\n" +
                "            SpreadsheetId to SpreadsheetMetadata (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetIdToSpreadsheetMetadata)\n" +
                "            TEXT to SpreadsheetMetadata (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetMetadata)\n" +
                "            TEXT to SpreadsheetMetadataPropertyName (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetMetadataPropertyName)\n" +
                "            TEXT to SpreadsheetName (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetName)\n" +
                "      ConverterCustomToString\n" +
                "        \"storage\"\n" +
                "          ConverterCollection\n" +
                "            TEXT to StoragePath (walkingkooka.storage.convert.StorageConverterTextToStoragePath)\n" +
                "            TEXT to Path (walkingkooka.convert.ConverterTextToPath)\n" +
                "            StorageBinary *.csv | text/csv to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueSharedCsv)\n" +
                "            StorageBinary *.env | text/x-env to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueSharedEnvironment)\n" +
                "            StorageBinary *.expression.txt | text/expression to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueSharedExpression)\n" +
                "            StorageBinary *.json | application/json to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueSharedJson)\n" +
                "            StorageBinary *.properties | text/x-java-properties to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueSharedProperties)\n" +
                "            StorageBinary *.tsv | text/tab-separated-values to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueSharedTsv)\n" +
                "            StorageBinary *.txt | text/plain to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueSharedTxt)\n" +
                "            * to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueBinary)\n" +
                "            *.csv to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinarySharedCsv)\n" +
                "            *.env to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinarySharedEnvironment)\n" +
                "            *.expression.txt to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinarySharedExpression)\n" +
                "            *.json to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinarySharedJson)\n" +
                "            *.properties to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinarySharedProperties)\n" +
                "            *.tsv to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinarySharedTsv)\n" +
                "            *.txt to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinarySharedTxt)\n" +
                "            StorageValue(Binary) to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinaryBinary)\n" +
                "            ConverterCustomToString\n" +
                "              \"BINARY\"\n" +
                "                ConverterCollection\n" +
                "                  to Binary (walkingkooka.convert.ConverterToBinary)\n" +
                "                  Binary to TEXT (walkingkooka.convert.ConverterBinaryToString)\n" +
                "                  TEXT to Binary (walkingkooka.convert.ConverterTextToBinary)\n" +
                "      ConverterCustomToString\n" +
                "        \"style\"\n" +
                "          ConverterCollection\n" +
                "            to TextStyle (walkingkooka.tree.text.convert.TreeTextConverterToTextStyle)\n" +
                "            to Styleable (walkingkooka.tree.text.convert.TreeTextConverterToStyleable)\n" +
                "            Properties to TextStyle (walkingkooka.tree.text.convert.TreeTextConverterPropertiesToTextStyle)\n" +
                "            TEXT to Border (walkingkooka.tree.text.convert.TreeTextConverterTextToBorder)\n" +
                "            TEXT to Margin (walkingkooka.tree.text.convert.TreeTextConverterTextToMargin)\n" +
                "            TEXT to Padding (walkingkooka.tree.text.convert.TreeTextConverterTextToPadding)\n" +
                "            TEXT to TextStyle (walkingkooka.tree.text.convert.TreeTextConverterTextToTextStyle)\n" +
                "            TEXT to TextStylePropertyName (walkingkooka.tree.text.convert.TreeTextConverterTextToTextStylePropertyName)\n" +
                "      ConverterCustomToString\n" +
                "        \"text-node\"\n" +
                "          ConverterCollection\n" +
                "            to TextNode (walkingkooka.tree.text.convert.TreeTextConverterToTextNode)\n" +
                "            Url to Hyperlink (walkingkooka.tree.text.convert.TreeTextConverterUrlToHyperlink)\n" +
                "            Url to Image (walkingkooka.tree.text.convert.TreeTextConverterUrlToImage)\n" +
                "            TEXT to Flag (walkingkooka.tree.text.convert.TreeTextConverterTextToFlag)\n" +
                "            TEXT to SpreadsheetText (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetText)\n" +
                "            TEXT to TextNode (walkingkooka.tree.text.convert.TreeTextConverterTextToTextNode)\n" +
                "      ConverterCustomToString\n" +
                "        \"template\"\n" +
                "          TEXT to TemplateValueName (walkingkooka.template.convert.TextToTemplateValueNameConverter)\n" +
                "      ConverterCustomToString\n" +
                "        \"net\"\n" +
                "          ConverterCollection\n" +
                "            Character or CharSequence or HasText or String to Character or CharSequence or String (walkingkooka.convert.ConverterCharacterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString)\n" +
                "            to HasHostAddress (walkingkooka.net.convert.NetConverterToHasHostAddress)\n" +
                "            to HostAddress (walkingkooka.net.convert.NetConverterToHostAddress)\n" +
                "            TEXT to HasHostAddress (walkingkooka.net.convert.NetConverterTextToHasHostAddress)\n" +
                "            TEXT to EmailAddress (walkingkooka.net.convert.NetConverterTextToEmailAddress)\n" +
                "            TEXT to HostAddress (walkingkooka.net.convert.NetConverterTextToHostAddress)\n" +
                "            TEXT to MediaType (walkingkooka.net.convert.NetConverterTextToMediaType)\n" +
                "            TEXT to Url (walkingkooka.net.convert.NetConverterTextToUrl)\n" +
                "            TEXT to UrlFragment (walkingkooka.net.convert.NetConverterTextToUrlFragment)\n" +
                "            TEXT to UrlQueryString (walkingkooka.net.convert.NetConverterTextToUrlQueryString)\n" +
                "      ConverterCustomToString\n" +
                "        \"optional-to\"\n" +
                "          Optional to (walkingkooka.convert.ConverterOptionalTo)\n" +
                "      ConverterCustomToString\n" +
                "        \"collection-to\"\n" +
                "          Collection to (walkingkooka.convert.ConverterCollectionTo)\n"
        );
    }

    @Test
    public void testSortConverterPrintTree() {
        this.treePrintAndCheck(
            SpreadsheetMetadataPropertyName.SORT_CONVERTER,
            "ConverterCustomToString\n" +
                "  \"collection (null-to-number, simple, text, boolean, number, date-time, locale, value, optional-to, collection-to)\"\n" +
                "    ConverterCollection\n" +
                "      ConverterCustomToString\n" +
                "        \"null-to-number\"\n" +
                "          null to Number (walkingkooka.spreadsheet.convert.SpreadsheetConverterNullToNumber)\n" +
                "      simple (walkingkooka.convert.ConverterSimple)\n" +
                "      ConverterCustomToString\n" +
                "        \"text\"\n" +
                "          ConverterCollection\n" +
                "            Character or CharSequence or HasText or String to Character or CharSequence or String (walkingkooka.convert.ConverterCharacterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString)\n" +
                "            TEXT to Charset (walkingkooka.convert.ConverterTextToCharset)\n" +
                "            TEXT to Indentation (walkingkooka.convert.ConverterTextToIndentation)\n" +
                "            TEXT to LineEnding (walkingkooka.convert.ConverterTextToLineEnding)\n" +
                "      ConverterCustomToString\n" +
                "        \"boolean\"\n" +
                "          ConverterCollection\n" +
                "            to Boolean (walkingkooka.spreadsheet.convert.SpreadsheetConverterToBoolean)\n" +
                "            Boolean to TEXT (walkingkooka.spreadsheet.convert.SpreadsheetConverterBooleanToText)\n" +
                "      ConverterCustomToString\n" +
                "        \"number\"\n" +
                "          ConverterCollection\n" +
                "            Number to Number (walkingkooka.tree.expression.convert.ExpressionNumberConverterNumberToNumber)\n" +
                "            to Number (walkingkooka.spreadsheet.convert.SpreadsheetConverterToNumber)\n" +
                "            Number to TEXT (walkingkooka.spreadsheet.convert.SpreadsheetConverterNumberToText)\n" +
                "      ConverterCustomToString\n" +
                "        \"date-time\"\n" +
                "          dateTime (walkingkooka.spreadsheet.convert.SpreadsheetConverterDateTime)\n" +
                "      ConverterCustomToString\n" +
                "        \"locale\"\n" +
                "          ConverterCollection\n" +
                "            Locale to String (walkingkooka.convert.ConverterLocaleToString)\n" +
                "            LocaleLike to Locale (walkingkooka.convert.ConverterLocaleToLocale)\n" +
                "            LocaleLike to LocaleLanguageTag (walkingkooka.convert.ConverterLocaleToLocaleLanguageTag)\n" +
                "            ConverterCollection\n" +
                "              to DateTimeSymbols (walkingkooka.convert.ConverterToDateTimeSymbols)\n" +
                "              LocaleLike to DateTimeSymbols (walkingkooka.convert.ConverterLocaleToDateTimeSymbols)\n" +
                "              Properties to DateTimeSymbols (walkingkooka.convert.ConverterPropertiesToDateTimeSymbols)\n" +
                "            ConverterCollection\n" +
                "              to DecimalNumberSymbols (walkingkooka.convert.ConverterToDecimalNumberSymbols)\n" +
                "              LocaleLike to DecimalNumberSymbols (walkingkooka.convert.ConverterLocaleToDecimalNumberSymbols)\n" +
                "              Properties to DecimalNumberSymbols (walkingkooka.convert.ConverterPropertiesToDecimalNumberSymbols)\n" +
                "            TEXT to LocaleLanguageTag (walkingkooka.convert.ConverterTextToLocaleLanguageTag)\n" +
                "      ConverterCustomToString\n" +
                "        \"value\"\n" +
                "          ConverterCollection\n" +
                "            SpreadsheetError to Number (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetErrorToNumber)\n" +
                "            null to Number (walkingkooka.spreadsheet.convert.SpreadsheetConverterNullToNumber)\n" +
                "            ConverterCustomToString\n" +
                "              \"SPREADSHEET SELECTION\"\n" +
                "                ConverterCollection\n" +
                "                  HasSpreadsheetReference (walkingkooka.spreadsheet.convert.SpreadsheetConverterToSpreadsheetSelection)\n" +
                "                  SELECTION to SELECTION (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetSelectionToSpreadsheetSelection)\n" +
                "                  SELECTION to TEXT (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetSelectionToText)\n" +
                "                  TEXT to SELECTION (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetSelection)\n" +
                "            SpreadsheetError to SpreadsheetError (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetErrorToSpreadsheetError)\n" +
                "            TEXT to SpreadsheetError (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetError)\n" +
                "            to ValueType (walkingkooka.validation.convert.ValidationConverterToValueType)\n" +
                "            TEXT to ValueType (walkingkooka.validation.convert.ValidationConverterTextToValueType)\n" +
                "            TEXT to ZoneOffset (walkingkooka.convert.ConverterTextToZoneOffset)\n" +
                "            SpreadsheetCellSet (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetCellSet)\n" +
                "            Collection to List (walkingkooka.convert.ConverterCollectionToList)\n" +
                "            TEXT to BooleanList (walkingkooka.convert.ConverterTextToCollectionListBooleanList)\n" +
                "            TEXT to LocalDateList (walkingkooka.convert.ConverterTextToCollectionListLocalDateList)\n" +
                "            TEXT to LocalDateTimeList (walkingkooka.convert.ConverterTextToCollectionListLocalDateTimeList)\n" +
                "            TEXT to LineEnding (walkingkooka.convert.ConverterTextToLineEnding)\n" +
                "            TEXT to NumberList (walkingkooka.convert.ConverterTextToCollectionListNumberList)\n" +
                "            TEXT to LocalTimeList (walkingkooka.convert.ConverterTextToCollectionListLocalTimeList)\n" +
                "            TEXT to StringList (walkingkooka.convert.ConverterTextToCollectionListStringList)\n" +
                "            ConverterCustomToString\n" +
                "              \"CSV\"\n" +
                "                ConverterCollection\n" +
                "                  to CsvStringList (walkingkooka.convert.ConverterToCsvStringList)\n" +
                "                  TEXT to CsvStringList (walkingkooka.convert.ConverterTextToCollectionListCsvStringList)\n" +
                "                  TEXT to CsvStringSet (walkingkooka.convert.ConverterTextToCollectionSetCsvStringSet)\n" +
                "            ConverterCustomToString\n" +
                "              \"TSV\"\n" +
                "                ConverterCollection\n" +
                "                  to TsvStringList (walkingkooka.convert.ConverterToTsvStringList)\n" +
                "                  TEXT to TsvStringList (walkingkooka.convert.ConverterTextToCollectionListTsvStringList)\n" +
                "                  TEXT to TsvStringSet (walkingkooka.convert.ConverterTextToCollectionSetTsvStringSet)\n" +
                "            Binary to TEXT (walkingkooka.convert.ConverterBinaryToString)\n" +
                "            to MultiLineText (walkingkooka.convert.ConverterToMultiLineText)\n" +
                "            * to String (walkingkooka.convert.ConverterObjectToString)\n" +
                "      ConverterCustomToString\n" +
                "        \"optional-to\"\n" +
                "          Optional to (walkingkooka.convert.ConverterOptionalTo)\n" +
                "      ConverterCustomToString\n" +
                "        \"collection-to\"\n" +
                "          Collection to (walkingkooka.convert.ConverterCollectionTo)\n"
        );
    }

    @Test
    public void testValidationConverterPrintTree() {
        this.treePrintAndCheck(
            SpreadsheetMetadataPropertyName.VALIDATION_CONVERTER,
            "ConverterCustomToString\n" +
                "  \"collection (null-to-number, simple, text, boolean, number, date-time, environment, value, error-throwing, expression, form-and-validation, locale, logging, plugins, properties, template, json, optional-to, collection-to)\"\n" +
                "    ConverterCollection\n" +
                "      ConverterCustomToString\n" +
                "        \"null-to-number\"\n" +
                "          null to Number (walkingkooka.spreadsheet.convert.SpreadsheetConverterNullToNumber)\n" +
                "      simple (walkingkooka.convert.ConverterSimple)\n" +
                "      ConverterCustomToString\n" +
                "        \"text\"\n" +
                "          ConverterCollection\n" +
                "            Character or CharSequence or HasText or String to Character or CharSequence or String (walkingkooka.convert.ConverterCharacterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString)\n" +
                "            TEXT to Charset (walkingkooka.convert.ConverterTextToCharset)\n" +
                "            TEXT to Indentation (walkingkooka.convert.ConverterTextToIndentation)\n" +
                "            TEXT to LineEnding (walkingkooka.convert.ConverterTextToLineEnding)\n" +
                "      ConverterCustomToString\n" +
                "        \"boolean\"\n" +
                "          ConverterCollection\n" +
                "            to Boolean (walkingkooka.spreadsheet.convert.SpreadsheetConverterToBoolean)\n" +
                "            Boolean to TEXT (walkingkooka.spreadsheet.convert.SpreadsheetConverterBooleanToText)\n" +
                "      ConverterCustomToString\n" +
                "        \"number\"\n" +
                "          ConverterCollection\n" +
                "            Number to Number (walkingkooka.tree.expression.convert.ExpressionNumberConverterNumberToNumber)\n" +
                "            to Number (walkingkooka.spreadsheet.convert.SpreadsheetConverterToNumber)\n" +
                "            Number to TEXT (walkingkooka.spreadsheet.convert.SpreadsheetConverterNumberToText)\n" +
                "      ConverterCustomToString\n" +
                "        \"date-time\"\n" +
                "          dateTime (walkingkooka.spreadsheet.convert.SpreadsheetConverterDateTime)\n" +
                "      ConverterCustomToString\n" +
                "        \"environment\"\n" +
                "          ConverterCollection\n" +
                "            to Environment (walkingkooka.environment.convert.EnvironmentConverterToEnvironment)\n" +
                "            Environment to Binary (walkingkooka.environment.convert.EnvironmentConverterEnvironmentToBinary)\n" +
                "            Environment to TEXT (walkingkooka.environment.convert.EnvironmentConverterEnvironmentToString)\n" +
                "            TEXT to Environment (walkingkooka.environment.convert.EnvironmentConverterTextToEnvironment)\n" +
                "            TEXT to EnvironmentValueName (walkingkooka.environment.convert.EnvironmentConverterTextToEnvironmentValueName)\n" +
                "      ConverterCustomToString\n" +
                "        \"value\"\n" +
                "          ConverterCollection\n" +
                "            SpreadsheetError to Number (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetErrorToNumber)\n" +
                "            null to Number (walkingkooka.spreadsheet.convert.SpreadsheetConverterNullToNumber)\n" +
                "            ConverterCustomToString\n" +
                "              \"SPREADSHEET SELECTION\"\n" +
                "                ConverterCollection\n" +
                "                  HasSpreadsheetReference (walkingkooka.spreadsheet.convert.SpreadsheetConverterToSpreadsheetSelection)\n" +
                "                  SELECTION to SELECTION (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetSelectionToSpreadsheetSelection)\n" +
                "                  SELECTION to TEXT (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetSelectionToText)\n" +
                "                  TEXT to SELECTION (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetSelection)\n" +
                "            SpreadsheetError to SpreadsheetError (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetErrorToSpreadsheetError)\n" +
                "            TEXT to SpreadsheetError (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetError)\n" +
                "            to ValueType (walkingkooka.validation.convert.ValidationConverterToValueType)\n" +
                "            TEXT to ValueType (walkingkooka.validation.convert.ValidationConverterTextToValueType)\n" +
                "            TEXT to ZoneOffset (walkingkooka.convert.ConverterTextToZoneOffset)\n" +
                "            SpreadsheetCellSet (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetCellSet)\n" +
                "            Collection to List (walkingkooka.convert.ConverterCollectionToList)\n" +
                "            TEXT to BooleanList (walkingkooka.convert.ConverterTextToCollectionListBooleanList)\n" +
                "            TEXT to LocalDateList (walkingkooka.convert.ConverterTextToCollectionListLocalDateList)\n" +
                "            TEXT to LocalDateTimeList (walkingkooka.convert.ConverterTextToCollectionListLocalDateTimeList)\n" +
                "            TEXT to LineEnding (walkingkooka.convert.ConverterTextToLineEnding)\n" +
                "            TEXT to NumberList (walkingkooka.convert.ConverterTextToCollectionListNumberList)\n" +
                "            TEXT to LocalTimeList (walkingkooka.convert.ConverterTextToCollectionListLocalTimeList)\n" +
                "            TEXT to StringList (walkingkooka.convert.ConverterTextToCollectionListStringList)\n" +
                "            ConverterCustomToString\n" +
                "              \"CSV\"\n" +
                "                ConverterCollection\n" +
                "                  to CsvStringList (walkingkooka.convert.ConverterToCsvStringList)\n" +
                "                  TEXT to CsvStringList (walkingkooka.convert.ConverterTextToCollectionListCsvStringList)\n" +
                "                  TEXT to CsvStringSet (walkingkooka.convert.ConverterTextToCollectionSetCsvStringSet)\n" +
                "            ConverterCustomToString\n" +
                "              \"TSV\"\n" +
                "                ConverterCollection\n" +
                "                  to TsvStringList (walkingkooka.convert.ConverterToTsvStringList)\n" +
                "                  TEXT to TsvStringList (walkingkooka.convert.ConverterTextToCollectionListTsvStringList)\n" +
                "                  TEXT to TsvStringSet (walkingkooka.convert.ConverterTextToCollectionSetTsvStringSet)\n" +
                "            Binary to TEXT (walkingkooka.convert.ConverterBinaryToString)\n" +
                "            to MultiLineText (walkingkooka.convert.ConverterToMultiLineText)\n" +
                "            * to String (walkingkooka.convert.ConverterObjectToString)\n" +
                "      ConverterCustomToString\n" +
                "        \"error-throwing\"\n" +
                "          throws SpreadsheetError (walkingkooka.spreadsheet.convert.SpreadsheetConverterSpreadsheetErrorThrowing)\n" +
                "      ConverterCustomToString\n" +
                "        \"expression\"\n" +
                "          TEXT to Expression (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToExpression)\n" +
                "      ConverterCustomToString\n" +
                "        \"form-and-validation\"\n" +
                "          ConverterCollection\n" +
                "            to ValidationCheckbox (walkingkooka.validation.convert.ValidationConverterValidationCheckbox)\n" +
                "            to ValidationChoice (walkingkooka.validation.convert.ValidationConverterToValidationChoice)\n" +
                "            to ValidationChoiceList (walkingkooka.validation.convert.ValidationConverterValidationChoiceList)\n" +
                "            to ValidationErrorList (walkingkooka.validation.convert.ValidationConverterValidationErrorList)\n" +
                "            TEXT to FormName (walkingkooka.validation.convert.ValidationConverterTextToFormName)\n" +
                "            TEXT to ValidationError (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToValidationError)\n" +
                "      ConverterCustomToString\n" +
                "        \"locale\"\n" +
                "          ConverterCollection\n" +
                "            Locale to String (walkingkooka.convert.ConverterLocaleToString)\n" +
                "            LocaleLike to Locale (walkingkooka.convert.ConverterLocaleToLocale)\n" +
                "            LocaleLike to LocaleLanguageTag (walkingkooka.convert.ConverterLocaleToLocaleLanguageTag)\n" +
                "            ConverterCollection\n" +
                "              to DateTimeSymbols (walkingkooka.convert.ConverterToDateTimeSymbols)\n" +
                "              LocaleLike to DateTimeSymbols (walkingkooka.convert.ConverterLocaleToDateTimeSymbols)\n" +
                "              Properties to DateTimeSymbols (walkingkooka.convert.ConverterPropertiesToDateTimeSymbols)\n" +
                "            ConverterCollection\n" +
                "              to DecimalNumberSymbols (walkingkooka.convert.ConverterToDecimalNumberSymbols)\n" +
                "              LocaleLike to DecimalNumberSymbols (walkingkooka.convert.ConverterLocaleToDecimalNumberSymbols)\n" +
                "              Properties to DecimalNumberSymbols (walkingkooka.convert.ConverterPropertiesToDecimalNumberSymbols)\n" +
                "            TEXT to LocaleLanguageTag (walkingkooka.convert.ConverterTextToLocaleLanguageTag)\n" +
                "      ConverterCustomToString\n" +
                "        \"logging\"\n" +
                "          TEXT to LoggingLevel (walkingkooka.convert.ConverterTextToLoggingLevel)\n" +
                "      ConverterCustomToString\n" +
                "        \"plugins\"\n" +
                "          ConverterCollection\n" +
                "            to SpreadsheetFormatterSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterToSpreadsheetFormatterSelector)\n" +
                "            to SpreadsheetParserSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterToSpreadsheetParserSelector)\n" +
                "            to ValidatorSelector (walkingkooka.validation.convert.ValidationConverterToValidatorSelector)\n" +
                "            TEXT to ConverterSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToConverterSelector)\n" +
                "            TEXT to CurrencyExchangeRaterSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToCurrencyExchangeRaterSelector)\n" +
                "            TEXT to ExpressionFunctionSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToExpressionFunctionSelector)\n" +
                "            TEXT to SpreadsheetComparatorSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetComparatorSelector)\n" +
                "            TEXT to SpreadsheetFormatterSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetFormatterSelector)\n" +
                "            TEXT to SpreadsheetParserSelector (walkingkooka.spreadsheet.convert.SpreadsheetConverterTextToSpreadsheetParserSelector)\n" +
                "            TEXT to ValidatorSelector (walkingkooka.validation.convert.ValidationConverterTextToValidatorSelector)\n" +
                "      ConverterCustomToString\n" +
                "        \"properties\"\n" +
                "          ConverterCollection\n" +
                "            to Properties (walkingkooka.convert.ConverterToProperties)\n" +
                "            TEXT to Properties (walkingkooka.convert.ConverterTextToProperties)\n" +
                "      ConverterCustomToString\n" +
                "        \"template\"\n" +
                "          TEXT to TemplateValueName (walkingkooka.template.convert.TextToTemplateValueNameConverter)\n" +
                "      ConverterCustomToString\n" +
                "        \"json\"\n" +
                "          ConverterCollection\n" +
                "            JsonNode to type (walkingkooka.tree.json.convert.JsonNodeConverterJsonNodeTo)\n" +
                "            Json TEXT to Object (walkingkooka.tree.json.convert.JsonNodeConverterTextToObject)\n" +
                "            * to JsonNode (walkingkooka.tree.json.convert.JsonNodeConverterToJsonNode)\n" +
                "            TEXT to JsonNode (walkingkooka.tree.json.convert.JsonNodeConverterTextToJsonNode)\n" +
                "            TEXT to JsonPointer (walkingkooka.tree.json.convert.JsonNodeConverterTextToJsonPointer)\n" +
                "            TEXT to JsonSelector (walkingkooka.tree.json.convert.JsonNodeConverterTextToJsonSelector)\n" +
                "      ConverterCustomToString\n" +
                "        \"optional-to\"\n" +
                "          Optional to (walkingkooka.convert.ConverterOptionalTo)\n" +
                "      ConverterCustomToString\n" +
                "        \"collection-to\"\n" +
                "          Collection to (walkingkooka.convert.ConverterCollectionTo)\n"
        );
    }

    private void treePrintAndCheck(final SpreadsheetMetadataPropertyName<ConverterSelector> property,
                                   final String expected) {
        final Converter<SpreadsheetConverterContext> converter = SpreadsheetConvertersConverterProviders.spreadsheetConverters(
            (ProviderContext p) -> METADATA_EN_AU.dateTimeConverter(
                SPREADSHEET_FORMATTER_PROVIDER,
                SPREADSHEET_PARSER_PROVIDER,
                PROVIDER_CONTEXT
            )
        ).converter(
            METADATA_EN_AU.getOrFail(property),
            PROVIDER_CONTEXT
        );

        this.treePrintAndCheck(
            (TreePrintable) converter,
            expected
        );
    }
}
