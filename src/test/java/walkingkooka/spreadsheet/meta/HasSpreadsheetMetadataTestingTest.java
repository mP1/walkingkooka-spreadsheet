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
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelector;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HasSpreadsheetMetadataTestingTest implements HasSpreadsheetMetadataTesting,
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
            () -> HasSpreadsheetMetadataTesting.CURRENCY_CONTEXT.setCurrency(DIFFERENT_CURRENCY)
        );
    }

    @Test
    public void testEffectiveStyle() {
        SPREADSHEET_METADATA.effectiveStyle();
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
        final Converter<SpreadsheetConverterContext> converter = SPREADSHEET_METADATA.converter(
            SpreadsheetMetadataPropertyName.SCRIPTING_CONVERTER,
            CONVERTER_PROVIDER,
            PROVIDER_CONTEXT
        );

        final SpreadsheetConverterContext converterContext = SPREADSHEET_METADATA.spreadsheetConverterContext(
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
        SPREADSHEET_METADATA.spreadsheetFormatter(
            SPREADSHEET_FORMATTER_PROVIDER,
            PROVIDER_CONTEXT
        );
    }

    // formatter........................................................................................................

    @Test
    public void testFormatterDatePrintTree() {
        this.formatterPrintTreeAndCheck(
            SpreadsheetMetadataPropertyName.DATE_FORMATTER,
            "SpreadsheetPatternSpreadsheetFormatterDateTime\n" +
                "  LocalDate\n" +
                "    DateTimeSpreadsheetFormat \"yyyy/mm/dd\"\n" +
                "      YearSpreadsheetFormat \"yyyy\" \"yyyy\"\n" +
                "      TextLiteralSpreadsheetFormat \"/\" \"/\"\n" +
                "      MonthSpreadsheetFormat \"mm\" \"mm\"\n" +
                "      TextLiteralSpreadsheetFormat \"/\" \"/\"\n" +
                "      DaySpreadsheetFormat \"dd\" \"dd\"\n"
        );
    }

    @Test
    public void testFormatterDateTimePrintTree() {
        this.formatterPrintTreeAndCheck(
            SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER,
            "SpreadsheetPatternSpreadsheetFormatterDateTime\n" +
                "  LocalDateTime\n" +
                "    DateTimeSpreadsheetFormat \"yyyy/mm/dd hh:mm\"\n" +
                "      YearSpreadsheetFormat \"yyyy\" \"yyyy\"\n" +
                "      TextLiteralSpreadsheetFormat \"/\" \"/\"\n" +
                "      MonthSpreadsheetFormat \"mm\" \"mm\"\n" +
                "      TextLiteralSpreadsheetFormat \"/\" \"/\"\n" +
                "      DaySpreadsheetFormat \"dd\" \"dd\"\n" +
                "      TextLiteralSpreadsheetFormat \" \" \" \"\n" +
                "      HourSpreadsheetFormat \"hh\" \"hh\"\n" +
                "      TextLiteralSpreadsheetFormat \":\" \":\"\n" +
                "      MinuteSpreadsheetFormat \"mm\" \"mm\"\n"
        );
    }

    @Test
    public void testFormatterErrorPrintTree() {
        this.formatterPrintTreeAndCheck(
            SpreadsheetMetadataPropertyName.ERROR_FORMATTER,
            "SpreadsheetFormatterSharedBadgeError\n" +
                "  SpreadsheetPatternSpreadsheetFormatterText\n" +
                "    TextSpreadsheetFormat \"@\"\n" +
                "      TextPlaceholderSpreadsheetFormat \"@\" \"@\"\n"
        );
    }

    @Test
    public void testFormatterNumberPrintTree() {
        this.formatterPrintTreeAndCheck(
            SpreadsheetMetadataPropertyName.NUMBER_FORMATTER,
            "SpreadsheetPatternSpreadsheetFormatterCollection\n" +
                "  0\n" +
                "    SpreadsheetPatternSpreadsheetFormatterCondition\n" +
                "      token\n" +
                "        GreaterThanSpreadsheetFormat \">0\"\n" +
                "          GreaterThanSymbolSpreadsheetFormat \">\" \">\"\n" +
                "          ConditionNumberSpreadsheetFormat \"0\" 0 (java.math.BigDecimal)\n" +
                "      formatter\n" +
                "        SpreadsheetPatternSpreadsheetFormatterNumber\n" +
                "          NumberSpreadsheetFormat \"0.#\"\n" +
                "            DigitZeroSpreadsheetFormat \"0\" \"0\"\n" +
                "            DecimalPointSpreadsheetFormat \".\" \".\"\n" +
                "            DigitSpreadsheetFormat \"#\" \"#\"\n" +
                "  1\n" +
                "    SpreadsheetPatternSpreadsheetFormatterCondition\n" +
                "      token\n" +
                "        LessThanSpreadsheetFormat \"<0\"\n" +
                "          LessThanSymbolSpreadsheetFormat \"<\" \"<\"\n" +
                "          ConditionNumberSpreadsheetFormat \"0\" 0 (java.math.BigDecimal)\n" +
                "      formatter\n" +
                "        SpreadsheetPatternSpreadsheetFormatterNumber\n" +
                "          NumberSpreadsheetFormat \"0.#\"\n" +
                "            DigitZeroSpreadsheetFormat \"0\" \"0\"\n" +
                "            DecimalPointSpreadsheetFormat \".\" \".\"\n" +
                "            DigitSpreadsheetFormat \"#\" \"#\"\n" +
                "  2\n" +
                "    SpreadsheetPatternSpreadsheetFormatterCondition\n" +
                "      token\n" +
                "        EqualsSpreadsheetFormat \"=0\"\n" +
                "          EqualsSymbolSpreadsheetFormat \"=\" \"=\"\n" +
                "          ConditionNumberSpreadsheetFormat \"0\" 0 (java.math.BigDecimal)\n" +
                "      formatter\n" +
                "        SpreadsheetPatternSpreadsheetFormatterNumber\n" +
                "          NumberSpreadsheetFormat \"0\"\n" +
                "            DigitZeroSpreadsheetFormat \"0\" \"0\"\n" +
                "  3\n" +
                "    SpreadsheetPatternSpreadsheetFormatterText\n" +
                "      TextSpreadsheetFormat \"@\"\n" +
                "        TextPlaceholderSpreadsheetFormat \"@\" \"@\"\n"
        );
    }

    @Test
    public void testFormatterTextPrintTree() {
        this.formatterPrintTreeAndCheck(
            SpreadsheetMetadataPropertyName.TEXT_FORMATTER,
            "SpreadsheetPatternSpreadsheetFormatterText\n" +
                "  TextSpreadsheetFormat \"@\"\n" +
                "    TextPlaceholderSpreadsheetFormat \"@\" \"@\"\n"
        );
    }

    @Test
    public void testFormatterTimePrintTree() {
        this.formatterPrintTreeAndCheck(
            SpreadsheetMetadataPropertyName.TIME_FORMATTER,
            "SpreadsheetPatternSpreadsheetFormatterDateTime\n" +
                "  LocalTime\n" +
                "    DateTimeSpreadsheetFormat \"hh:mm:ss\"\n" +
                "      HourSpreadsheetFormat \"hh\" \"hh\"\n" +
                "      TextLiteralSpreadsheetFormat \":\" \":\"\n" +
                "      MinuteSpreadsheetFormat \"mm\" \"mm\"\n" +
                "      TextLiteralSpreadsheetFormat \":\" \":\"\n" +
                "      SecondSpreadsheetFormat \"ss\" \"ss\"\n"
        );
    }

    private void formatterPrintTreeAndCheck(final SpreadsheetMetadataPropertyName<SpreadsheetFormatterSelector> propertyName,
                                            final String expected) {
        final SpreadsheetFormatterSelector selector = SPREADSHEET_METADATA.getOrFail(propertyName);

        final SpreadsheetFormatter formatter = SpreadsheetFormatterProviders.spreadsheetFormatters()
            .spreadsheetFormatter(
                selector,
                PROVIDER_CONTEXT
            );

        this.treePrintAndCheck(
            (TreePrintable) formatter,
            expected
        );
    }

    @Test
    public void testJsonNodeMarshallContext() {
        SPREADSHEET_METADATA.jsonNodeMarshallContext();
    }

    @Test
    public void testJsonNodeUnmarshallContext() {
        SPREADSHEET_METADATA.jsonNodeUnmarshallContext(
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
            HasSpreadsheetMetadataTesting.parseFormula(
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

    // parser...........................................................................................................

    @Test
    public void testParserDatePrintTree() {
        this.parserPrintTreeAndCheck(
            SpreadsheetMetadataPropertyName.DATE_PARSER,
            "SpreadsheetNonNumberParsePatternSpreadsheetParser\n" +
                "  parser\n" +
                "    AndEmptyTextCursorParser\n" +
                "      TransformingParser\n" +
                "        parser\n" +
                "          SequenceParser\n" +
                "            digit{4,4}, \"/\", digit{1,2}, \"/\", digit{1,2}\n" +
                "              0\n" +
                "                TransformingParser\n" +
                "                  parser\n" +
                "                    digit{4,4} (walkingkooka.text.cursor.parser.CharPredicateStringParser)\n" +
                "                  transformer\n" +
                "                    Lambda\n" +
                "              1\n" +
                "                TransformingParser\n" +
                "                  parser\n" +
                "                    StringParser\n" +
                "                      \"/\"\n" +
                "                  transformer\n" +
                "                    Lambda\n" +
                "              2\n" +
                "                TransformingParser\n" +
                "                  parser\n" +
                "                    digit{1,2} (walkingkooka.text.cursor.parser.CharPredicateStringParser)\n" +
                "                  transformer\n" +
                "                    Lambda\n" +
                "              3\n" +
                "                TransformingParser\n" +
                "                  parser\n" +
                "                    StringParser\n" +
                "                      \"/\"\n" +
                "                  transformer\n" +
                "                    Lambda\n" +
                "              4\n" +
                "                TransformingParser\n" +
                "                  parser\n" +
                "                    digit{1,2} (walkingkooka.text.cursor.parser.CharPredicateStringParser)\n" +
                "                  transformer\n" +
                "                    Lambda\n" +
                "        transformer\n" +
                "          Lambda\n" +
                "  valueType\n" +
                "    date-time/Date\n" +
                "  tokens\n" +
                "    Sequence \"yyyy/mm/dd\"\n" +
                "      DateSpreadsheetFormat \"yyyy/mm/dd\"\n" +
                "        YearSpreadsheetFormat \"yyyy\" \"yyyy\"\n" +
                "        TextLiteralSpreadsheetFormat \"/\" \"/\"\n" +
                "        MonthSpreadsheetFormat \"mm\" \"mm\"\n" +
                "        TextLiteralSpreadsheetFormat \"/\" \"/\"\n" +
                "        DaySpreadsheetFormat \"dd\" \"dd\"\n"
        );
    }

    @Test
    public void testParserDateTimePrintTree() {
        this.parserPrintTreeAndCheck(
            SpreadsheetMetadataPropertyName.DATE_TIME_PARSER,
            "SpreadsheetNonNumberParsePatternSpreadsheetParser\n" +
                "  parser\n" +
                "    AndEmptyTextCursorParser\n" +
                "      TransformingParser\n" +
                "        parser\n" +
                "          SequenceParser\n" +
                "            digit{4,4}, \"/\", digit{1,2}, \"/\", digit{1,2}, \" \", digit{1,2}, \":\", digit{1,2}\n" +
                "              0\n" +
                "                TransformingParser\n" +
                "                  parser\n" +
                "                    digit{4,4} (walkingkooka.text.cursor.parser.CharPredicateStringParser)\n" +
                "                  transformer\n" +
                "                    Lambda\n" +
                "              1\n" +
                "                TransformingParser\n" +
                "                  parser\n" +
                "                    StringParser\n" +
                "                      \"/\"\n" +
                "                  transformer\n" +
                "                    Lambda\n" +
                "              2\n" +
                "                TransformingParser\n" +
                "                  parser\n" +
                "                    digit{1,2} (walkingkooka.text.cursor.parser.CharPredicateStringParser)\n" +
                "                  transformer\n" +
                "                    Lambda\n" +
                "              3\n" +
                "                TransformingParser\n" +
                "                  parser\n" +
                "                    StringParser\n" +
                "                      \"/\"\n" +
                "                  transformer\n" +
                "                    Lambda\n" +
                "              4\n" +
                "                TransformingParser\n" +
                "                  parser\n" +
                "                    digit{1,2} (walkingkooka.text.cursor.parser.CharPredicateStringParser)\n" +
                "                  transformer\n" +
                "                    Lambda\n" +
                "              5\n" +
                "                TransformingParser\n" +
                "                  parser\n" +
                "                    StringParser\n" +
                "                      \" \"\n" +
                "                  transformer\n" +
                "                    Lambda\n" +
                "              6\n" +
                "                TransformingParser\n" +
                "                  parser\n" +
                "                    digit{1,2} (walkingkooka.text.cursor.parser.CharPredicateStringParser)\n" +
                "                  transformer\n" +
                "                    Lambda\n" +
                "              7\n" +
                "                TransformingParser\n" +
                "                  parser\n" +
                "                    StringParser\n" +
                "                      \":\"\n" +
                "                  transformer\n" +
                "                    Lambda\n" +
                "              8\n" +
                "                TransformingParser\n" +
                "                  parser\n" +
                "                    digit{1,2} (walkingkooka.text.cursor.parser.CharPredicateStringParser)\n" +
                "                  transformer\n" +
                "                    Lambda\n" +
                "        transformer\n" +
                "          Lambda\n" +
                "  valueType\n" +
                "    date-time/DateTime\n" +
                "  tokens\n" +
                "    Sequence \"yyyy/mm/dd hh:mm\"\n" +
                "      DateTimeSpreadsheetFormat \"yyyy/mm/dd hh:mm\"\n" +
                "        YearSpreadsheetFormat \"yyyy\" \"yyyy\"\n" +
                "        TextLiteralSpreadsheetFormat \"/\" \"/\"\n" +
                "        MonthSpreadsheetFormat \"mm\" \"mm\"\n" +
                "        TextLiteralSpreadsheetFormat \"/\" \"/\"\n" +
                "        DaySpreadsheetFormat \"dd\" \"dd\"\n" +
                "        TextLiteralSpreadsheetFormat \" \" \" \"\n" +
                "        HourSpreadsheetFormat \"hh\" \"hh\"\n" +
                "        TextLiteralSpreadsheetFormat \":\" \":\"\n" +
                "        MinuteSpreadsheetFormat \"mm\" \"mm\"\n"
        );
    }

    @Test
    public void testParserNumberPrintTree() {
        this.parserPrintTreeAndCheck(
            SpreadsheetMetadataPropertyName.NUMBER_PARSER,
            "SpreadsheetNumberParsePatternSpreadsheetParser\n" +
                "  pattern\n" +
                "    number-parse-pattern\n" +
                "      \"0.#\" ;\n" +
                "      \"0.#\" ;\n" +
                "      \"0\"\n"
        );
    }

    @Test
    public void testParserTimePrintTree() {
        this.parserPrintTreeAndCheck(
            SpreadsheetMetadataPropertyName.TIME_PARSER,
            "SpreadsheetNonNumberParsePatternSpreadsheetParser\n" +
                "  parser\n" +
                "    AndEmptyTextCursorParser\n" +
                "      TransformingParser\n" +
                "        parser\n" +
                "          SequenceParser\n" +
                "            digit{1,2}, \":\", digit{1,2}, \":\", digit{1,2}\n" +
                "              0\n" +
                "                TransformingParser\n" +
                "                  parser\n" +
                "                    digit{1,2} (walkingkooka.text.cursor.parser.CharPredicateStringParser)\n" +
                "                  transformer\n" +
                "                    Lambda\n" +
                "              1\n" +
                "                TransformingParser\n" +
                "                  parser\n" +
                "                    StringParser\n" +
                "                      \":\"\n" +
                "                  transformer\n" +
                "                    Lambda\n" +
                "              2\n" +
                "                TransformingParser\n" +
                "                  parser\n" +
                "                    digit{1,2} (walkingkooka.text.cursor.parser.CharPredicateStringParser)\n" +
                "                  transformer\n" +
                "                    Lambda\n" +
                "              3\n" +
                "                TransformingParser\n" +
                "                  parser\n" +
                "                    StringParser\n" +
                "                      \":\"\n" +
                "                  transformer\n" +
                "                    Lambda\n" +
                "              4\n" +
                "                TransformingParser\n" +
                "                  parser\n" +
                "                    digit{1,2} (walkingkooka.text.cursor.parser.CharPredicateStringParser)\n" +
                "                  transformer\n" +
                "                    Lambda\n" +
                "        transformer\n" +
                "          Lambda\n" +
                "  valueType\n" +
                "    date-time/Time\n" +
                "  tokens\n" +
                "    Sequence \"hh:mm:ss\"\n" +
                "      TimeSpreadsheetFormat \"hh:mm:ss\"\n" +
                "        HourSpreadsheetFormat \"hh\" \"hh\"\n" +
                "        TextLiteralSpreadsheetFormat \":\" \":\"\n" +
                "        MinuteSpreadsheetFormat \"mm\" \"mm\"\n" +
                "        TextLiteralSpreadsheetFormat \":\" \":\"\n" +
                "        SecondSpreadsheetFormat \"ss\" \"ss\"\n"
        );
    }

    private void parserPrintTreeAndCheck(final SpreadsheetMetadataPropertyName<SpreadsheetParserSelector> propertyName,
                                         final String expected) {
        final SpreadsheetParserSelector selector = SPREADSHEET_METADATA.getOrFail(propertyName);

        final SpreadsheetParser parser = SpreadsheetParserProviders.spreadsheetParsePattern(
            SpreadsheetFormatterProviders.spreadsheetFormatters()
        ).spreadsheetParser(
            selector,
            PROVIDER_CONTEXT
        );

        this.treePrintAndCheck(
            (TreePrintable) parser,
            expected
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
        SPREADSHEET_METADATA.spreadsheetFormatterContext(
            HAS_SPREADSHEET_CELL,
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
        SPREADSHEET_METADATA.spreadsheetParser(
            SPREADSHEET_PARSER_PROVIDER,
            PROVIDER_CONTEXT
        );
    }

    @Test
    public void testSpreadsheetParserContext() {
        SPREADSHEET_METADATA
            .spreadsheetParserContext(
                SpreadsheetMetadata.NO_CELL,
                HasSpreadsheetMetadataTesting.LOCALE_CONTEXT,
                HAS_NOW
            );
    }

    @Test
    public void testTerminalContext() {
        this.checkEquals(
            "1, input: FakeTextReader, output: sink, error: sink {charset=UTF-8, currency=AUD, currentWorkingDirectory=/current1/working2/directory3, homeDirectory=/users/user123@example.com, indentation=\"  \", lineEnding=\"\\n\", locale=en_AU, loggingLevel=NONE, serverUrl=https://example.com, terminalId=1, timeOffset=Z, user=user123@example.com}",
            HasSpreadsheetMetadataTesting.TERMINAL_CONTEXT.toString()
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
                "            to Color (walkingkooka.color.convert.ColorConverterToColor)\n" +
                "            TEXT to Color (walkingkooka.color.convert.ColorConverterTextToColor)\n" +
                "            Color to Number (walkingkooka.color.convert.ColorConverterColorToNumber)\n" +
                "            Number to Color (walkingkooka.color.convert.ColorConverterNumberToColor)\n" +
                "            to ColorProperties (walkingkooka.color.convert.ColorConverterToColorProperties)\n" +
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
                "            to Color (walkingkooka.color.convert.ColorConverterToColor)\n" +
                "            TEXT to Color (walkingkooka.color.convert.ColorConverterTextToColor)\n" +
                "            Color to Number (walkingkooka.color.convert.ColorConverterColorToNumber)\n" +
                "            Number to Color (walkingkooka.color.convert.ColorConverterNumberToColor)\n" +
                "            to ColorProperties (walkingkooka.color.convert.ColorConverterToColorProperties)\n" +
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
                "            to Color (walkingkooka.color.convert.ColorConverterToColor)\n" +
                "            TEXT to Color (walkingkooka.color.convert.ColorConverterTextToColor)\n" +
                "            Color to Number (walkingkooka.color.convert.ColorConverterColorToNumber)\n" +
                "            Number to Color (walkingkooka.color.convert.ColorConverterNumberToColor)\n" +
                "            to ColorProperties (walkingkooka.color.convert.ColorConverterToColorProperties)\n" +
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
                "            to Color (walkingkooka.color.convert.ColorConverterToColor)\n" +
                "            TEXT to Color (walkingkooka.color.convert.ColorConverterTextToColor)\n" +
                "            Color to Number (walkingkooka.color.convert.ColorConverterColorToNumber)\n" +
                "            Number to Color (walkingkooka.color.convert.ColorConverterNumberToColor)\n" +
                "            to ColorProperties (walkingkooka.color.convert.ColorConverterToColorProperties)\n" +
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
            (ProviderContext p) -> SPREADSHEET_METADATA.dateTimeConverter(
                SPREADSHEET_FORMATTER_PROVIDER,
                SPREADSHEET_PARSER_PROVIDER,
                PROVIDER_CONTEXT
            )
        ).converter(
            SPREADSHEET_METADATA.getOrFail(property),
            PROVIDER_CONTEXT
        );

        this.treePrintAndCheck(
            (TreePrintable) converter,
            expected
        );
    }

    // SpreadsheetMetadata..............................................................................................

    @Test
    public void testSpreadsheetMetadataOnlyDefaultsPrintTree() {
        this.treePrintAndCheck(
            SPREADSHEET_METADATA,
            "SpreadsheetMetadataNonEmpty\n" +
                "  auditInfo: \n" +
                "    AuditInfo\n" +
                "      created\n" +
                "        user123@example.com 1999-12-31T12:58:59\n" +
                "      modified\n" +
                "        user123@example.com 1999-12-31T12:58:59\n" +
                "  autoHideScrollbars: false\n" +
                "  cellCharacterWidth: 1\n" +
                "  color1: black\n" +
                "  color2: white\n" +
                "  colorBlack: 1\n" +
                "  colorWhite: 2\n" +
                "  comparators: \n" +
                "    background-color\n" +
                "    border-bottom-color\n" +
                "    border-color\n" +
                "    border-left-color\n" +
                "    border-right-color\n" +
                "    border-top-color\n" +
                "    color\n" +
                "    currency\n" +
                "    custom-list\n" +
                "    custom-list-case-insensitive\n" +
                "    date\n" +
                "    date-time\n" +
                "    day-of-month\n" +
                "    day-of-week\n" +
                "    error\n" +
                "    formatter\n" +
                "    hour-of-am-pm\n" +
                "    hour-of-day\n" +
                "    locale\n" +
                "    minute-of-hour\n" +
                "    month-of-year\n" +
                "    nano-of-second\n" +
                "    number\n" +
                "    outline-color\n" +
                "    parser\n" +
                "    seconds-of-minute\n" +
                "    text\n" +
                "    text-case-insensitive\n" +
                "    text-decoration-color\n" +
                "    text-with-numbers\n" +
                "    text-with-numbers-case-insensitive\n" +
                "    time\n" +
                "    validator\n" +
                "    value-type\n" +
                "    year\n" +
                "  converters: \n" +
                "    binary\n" +
                "    binary-to-text\n" +
                "    boolean\n" +
                "    boolean-to-text\n" +
                "    collection\n" +
                "    collection-to\n" +
                "    collection-to-list\n" +
                "    color\n" +
                "    color-to-number\n" +
                "    csv\n" +
                "    currency\n" +
                "    currency-code-to-currency\n" +
                "    currency-value-to\n" +
                "    currency-value-to-number\n" +
                "    date-time\n" +
                "    date-time-symbols\n" +
                "    decimal-number-symbols\n" +
                "    environment\n" +
                "    environment-to-binary\n" +
                "    environment-to-text\n" +
                "    error-throwing\n" +
                "    error-to-error\n" +
                "    error-to-number\n" +
                "    expression\n" +
                "    form-and-validation\n" +
                "    format-pattern-to-string\n" +
                "    json\n" +
                "    json-to\n" +
                "    locale\n" +
                "    locale-to-text\n" +
                "    logging\n" +
                "    net\n" +
                "    never\n" +
                "    null-to-number\n" +
                "    number\n" +
                "    number-to-color\n" +
                "    number-to-currency-value\n" +
                "    number-to-number\n" +
                "    number-to-text\n" +
                "    optional-to\n" +
                "    plugins\n" +
                "    properties\n" +
                "    properties-to-date-time-symbols\n" +
                "    properties-to-decimal-number-symbols\n" +
                "    properties-to-spreadsheet-metadata\n" +
                "    properties-to-text-style\n" +
                "    simple\n" +
                "    spreadsheet-cell-set\n" +
                "    spreadsheet-id-to-spreadsheet-metadata\n" +
                "    spreadsheet-metadata\n" +
                "    spreadsheet-selection\n" +
                "    spreadsheet-selection-to-spreadsheet-selection\n" +
                "    spreadsheet-selection-to-text\n" +
                "    storage\n" +
                "    storage-binary-to-storage-value-binary\n" +
                "    storage-binary-to-storage-value-csv\n" +
                "    storage-binary-to-storage-value-environment\n" +
                "    storage-binary-to-storage-value-expression\n" +
                "    storage-binary-to-storage-value-json\n" +
                "    storage-binary-to-storage-value-properties\n" +
                "    storage-binary-to-storage-value-tsv\n" +
                "    storage-binary-to-storage-value-txt\n" +
                "    storage-value-info-list-to-text\n" +
                "    storage-value-to-storage-binary-binary\n" +
                "    storage-value-to-storage-binary-csv\n" +
                "    storage-value-to-storage-binary-environment\n" +
                "    storage-value-to-storage-binary-expression\n" +
                "    storage-value-to-storage-binary-json\n" +
                "    storage-value-to-storage-binary-properties\n" +
                "    storage-value-to-storage-binary-tsv\n" +
                "    storage-value-to-storage-binary-txt\n" +
                "    style\n" +
                "    system\n" +
                "    template\n" +
                "    text\n" +
                "    text-node\n" +
                "    text-to-binary\n" +
                "    text-to-boolean-list\n" +
                "    text-to-border\n" +
                "    text-to-charset\n" +
                "    text-to-color\n" +
                "    text-to-converter-selector\n" +
                "    text-to-csv-string-list\n" +
                "    text-to-csv-string-set\n" +
                "    text-to-currency\n" +
                "    text-to-currency-code\n" +
                "    text-to-currency-exchange-rater-selector\n" +
                "    text-to-currency-value\n" +
                "    text-to-date-list\n" +
                "    text-to-date-time-list\n" +
                "    text-to-email-address\n" +
                "    text-to-environment\n" +
                "    text-to-environment-value-name\n" +
                "    text-to-error\n" +
                "    text-to-expression\n" +
                "    text-to-expression-function-selector\n" +
                "    text-to-flag\n" +
                "    text-to-form-name\n" +
                "    text-to-has-host-address\n" +
                "    text-to-host-address\n" +
                "    text-to-indentation\n" +
                "    text-to-json\n" +
                "    text-to-json-pointer\n" +
                "    text-to-json-selector\n" +
                "    text-to-line-ending\n" +
                "    text-to-locale\n" +
                "    text-to-locale-language-tag\n" +
                "    text-to-logging-level\n" +
                "    text-to-margin\n" +
                "    text-to-media-type\n" +
                "    text-to-number-list\n" +
                "    text-to-object\n" +
                "    text-to-padding\n" +
                "    text-to-path\n" +
                "    text-to-properties\n" +
                "    text-to-spreadsheet-comparator-selector\n" +
                "    text-to-spreadsheet-formatter-selector\n" +
                "    text-to-spreadsheet-id\n" +
                "    text-to-spreadsheet-metadata\n" +
                "    text-to-spreadsheet-metadata-color\n" +
                "    text-to-spreadsheet-metadata-property-name\n" +
                "    text-to-spreadsheet-name\n" +
                "    text-to-spreadsheet-parser-selector\n" +
                "    text-to-spreadsheet-selection\n" +
                "    text-to-spreadsheet-text\n" +
                "    text-to-storage-path\n" +
                "    text-to-string-list\n" +
                "    text-to-template-value-name\n" +
                "    text-to-text\n" +
                "    text-to-text-node\n" +
                "    text-to-text-style\n" +
                "    text-to-text-style-property-name\n" +
                "    text-to-time-list\n" +
                "    text-to-tsv-string-list\n" +
                "    text-to-tsv-string-set\n" +
                "    text-to-url\n" +
                "    text-to-url-fragment\n" +
                "    text-to-url-query-string\n" +
                "    text-to-validation-error\n" +
                "    text-to-validator-selector\n" +
                "    text-to-value-type\n" +
                "    text-to-web-color-name\n" +
                "    text-to-zone-offset\n" +
                "    to-binary\n" +
                "    to-boolean\n" +
                "    to-color\n" +
                "    to-color-properties\n" +
                "    to-csv-string-list\n" +
                "    to-currency\n" +
                "    to-currency-code\n" +
                "    to-date-time-symbols\n" +
                "    to-decimal-number-symbols\n" +
                "    to-environment\n" +
                "    to-formatter-selector\n" +
                "    to-has-host-address\n" +
                "    to-host-address\n" +
                "    to-json-node\n" +
                "    to-locale\n" +
                "    to-locale-language-tag\n" +
                "    to-multi-line-text\n" +
                "    to-number\n" +
                "    to-parser-selector\n" +
                "    to-properties\n" +
                "    to-spreadsheet-selection\n" +
                "    to-string\n" +
                "    to-style\n" +
                "    to-styleable\n" +
                "    to-text-node\n" +
                "    to-tsv-string-list\n" +
                "    to-validation-checkbox\n" +
                "    to-validation-choice\n" +
                "    to-validation-choice-list\n" +
                "    to-validation-error-list\n" +
                "    to-validator-selector\n" +
                "    to-value\n" +
                "    to-value-type\n" +
                "    tsv\n" +
                "    url\n" +
                "    url-to-hyperlink\n" +
                "    url-to-image\n" +
                "    value\n" +
                "  currency: AUD (java.util.Currency)\n" +
                "  dateFormatter: \n" +
                "    date\n" +
                "      \"yyyy/mm/dd\"\n" +
                "  dateParser: \n" +
                "    date\n" +
                "      \"yyyy/mm/dd\"\n" +
                "  dateTimeFormatter: \n" +
                "    date-time\n" +
                "      \"yyyy/mm/dd hh:mm\"\n" +
                "  dateTimeOffset: -25569L\n" +
                "  dateTimeParser: \n" +
                "    date-time\n" +
                "      \"yyyy/mm/dd hh:mm\"\n" +
                "  dateTimeSymbols: \n" +
                "    DateTimeSymbols\n" +
                "      ampms\n" +
                "        am\n" +
                "        pm\n" +
                "      monthNames\n" +
                "        January\n" +
                "        February\n" +
                "        March\n" +
                "        April\n" +
                "        May\n" +
                "        June\n" +
                "        July\n" +
                "        August\n" +
                "        September\n" +
                "        October\n" +
                "        November\n" +
                "        December\n" +
                "      monthNameAbbreviations\n" +
                "        Jan.\n" +
                "        Feb.\n" +
                "        Mar.\n" +
                "        Apr.\n" +
                "        May\n" +
                "        Jun.\n" +
                "        Jul.\n" +
                "        Aug.\n" +
                "        Sep.\n" +
                "        Oct.\n" +
                "        Nov.\n" +
                "        Dec.\n" +
                "      weekDayNames\n" +
                "        Sunday\n" +
                "        Monday\n" +
                "        Tuesday\n" +
                "        Wednesday\n" +
                "        Thursday\n" +
                "        Friday\n" +
                "        Saturday\n" +
                "      weekDayNameAbbreviations\n" +
                "        Sun.\n" +
                "        Mon.\n" +
                "        Tue.\n" +
                "        Wed.\n" +
                "        Thu.\n" +
                "        Fri.\n" +
                "        Sat.\n" +
                "  decimalNumberDigitCount: 8\n" +
                "  decimalNumberSymbols: \n" +
                "    DecimalNumberSymbols\n" +
                "      negativeSign\n" +
                "        '-'\n" +
                "      positiveSign\n" +
                "        '+'\n" +
                "      zeroDigit\n" +
                "        '0'\n" +
                "      currencySymbol\n" +
                "        \"$\"\n" +
                "      decimalSeparator\n" +
                "        '.'\n" +
                "      exponentSymbol\n" +
                "        \"e\"\n" +
                "      groupSeparator\n" +
                "        ','\n" +
                "      infinitySymbol\n" +
                "        \"∞\"\n" +
                "      monetaryDecimalSeparator\n" +
                "        '.'\n" +
                "      nanSymbol\n" +
                "        \"NaN\"\n" +
                "      percentSymbol\n" +
                "        '%'\n" +
                "      permillSymbol\n" +
                "        '‰'\n" +
                "  defaultFormHandler: \n" +
                "    basic\n" +
                "  defaultYear: 2000\n" +
                "  errorFormatter: \n" +
                "    badge-error\n" +
                "      \"text @\"\n" +
                "  exporters: \n" +
                "    collection\n" +
                "    empty\n" +
                "    json\n" +
                "  expressionNumberKind: BIG_DECIMAL\n" +
                "  formatters: \n" +
                "    accounting\n" +
                "    automatic\n" +
                "    badge-error\n" +
                "    collection\n" +
                "    currency\n" +
                "    date\n" +
                "    date-time\n" +
                "    default-text\n" +
                "    expression\n" +
                "    full-date\n" +
                "    full-date-time\n" +
                "    full-time\n" +
                "    general\n" +
                "    hyperlinking\n" +
                "    long-date\n" +
                "    long-date-time\n" +
                "    long-time\n" +
                "    medium-date\n" +
                "    medium-date-time\n" +
                "    medium-time\n" +
                "    number\n" +
                "    percent\n" +
                "    scientific\n" +
                "    short-date\n" +
                "    short-date-time\n" +
                "    short-time\n" +
                "    text\n" +
                "    time\n" +
                "  formattingConverter: \n" +
                "    collection\n" +
                "      \"(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, json, currency, logging, plugins, properties, spreadsheet-metadata, storage, style, text-node, template, net, optional-to, collection-to)\"\n" +
                "  formattingCurrencyExchangeRater: \n" +
                "    storage-properties\n" +
                "      \"(\\\"/samples/CurrencyExchangeRates.properties\\\")\"\n" +
                "  formattingFunctions: \n" +
                "  formHandlers: \n" +
                "  formulaConverter: \n" +
                "    collection\n" +
                "      \"(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, json, currency, logging, plugins, properties, spreadsheet-metadata, storage, style, text-node, template, net, optional-to, collection-to)\"\n" +
                "  formulaCurrencyExchangeRater: \n" +
                "    storage-properties\n" +
                "      \"(\\\"/samples/CurrencyExchangeRates.properties\\\")\"\n" +
                "  formulaFunctions: \n" +
                "  functions: \n" +
                "  importers: \n" +
                "    collection\n" +
                "    empty\n" +
                "    json\n" +
                "  locale: en_AU (java.util.Locale)\n" +
                "  numberFormatter: \n" +
                "    number\n" +
                "      \"0.#;0.#;0\"\n" +
                "  numberParser: \n" +
                "    number\n" +
                "      \"0.#;0.#;0\"\n" +
                "  parsers: \n" +
                "    date\n" +
                "    date-time\n" +
                "    full-date\n" +
                "    full-date-time\n" +
                "    full-time\n" +
                "    general\n" +
                "    long-date\n" +
                "    long-date-time\n" +
                "    long-time\n" +
                "    medium-date\n" +
                "    medium-date-time\n" +
                "    medium-time\n" +
                "    number\n" +
                "    short-date\n" +
                "    short-date-time\n" +
                "    short-time\n" +
                "    time\n" +
                "    whole-number\n" +
                "  plugins: \n" +
                "  precision: 7\n" +
                "  queryConverter: \n" +
                "    collection\n" +
                "      \"(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, properties, spreadsheet-metadata, style, text-node, template, net, optional-to, collection-to)\"\n" +
                "  queryFunctions: \n" +
                "  roundingMode: HALF_UP\n" +
                "  scriptingConverter: \n" +
                "    collection\n" +
                "      \"(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, json, currency, logging, plugins, properties, spreadsheet-metadata, storage, style, text-node, template, net, optional-to, collection-to)\"\n" +
                "  scriptingCurrencyExchangeRater: \n" +
                "    storage-properties\n" +
                "      \"(\\\"/samples/CurrencyExchangeRates.properties\\\")\"\n" +
                "  scriptingFunctions: \n" +
                "  showFormulaEditor: true\n" +
                "  showFormulas: false\n" +
                "  showGridLines: true\n" +
                "  showHeadings: true\n" +
                "  sortComparators: \n" +
                "    [background-color, border-bottom-color, border-color, border-left-color, border-right-color, border-top-color, color, currency, custom-list, custom-list-case-insensitive, date, datetime, day-of-month, day-of-year, formatter, hour-of-ampm, hour-of-day, locale, minute-of-hour, month-of-year, nano-of-second, number, outline-color, parser, seconds-of-minute, text, text-case-insensitive, time, validator, value-type, year]\n" +
                "  sortConverter: \n" +
                "    collection\n" +
                "      \"(null-to-number, simple, text, boolean, number, date-time, locale, value, optional-to, collection-to)\"\n" +
                "  style: \n" +
                "    TextStyle\n" +
                "      height=50px\n" +
                "      width=100px\n" +
                "  textFormatter: \n" +
                "    text\n" +
                "      \"@\"\n" +
                "  timeFormatter: \n" +
                "    time\n" +
                "      \"hh:mm:ss\"\n" +
                "  timeParser: \n" +
                "    time\n" +
                "      \"hh:mm:ss\"\n" +
                "  twoDigitYear: 50\n" +
                "  validationConverter: \n" +
                "    collection\n" +
                "      \"(null-to-number, simple, text, boolean, number, date-time, environment, value, error-throwing, expression, form-and-validation, locale, logging, plugins, properties, template, json, optional-to, collection-to)\"\n" +
                "  validationCurrencyExchangeRater: \n" +
                "    storage-properties\n" +
                "      \"(\\\"/samples/CurrencyExchangeRates.properties\\\")\"\n" +
                "  validationFunctions: \n" +
                "  validationValidators: \n" +
                "    absolute-url\n" +
                "    checkbox\n" +
                "    choice-list\n" +
                "    collection\n" +
                "    email-address\n" +
                "    expression\n" +
                "    non-null\n" +
                "    text-length\n" +
                "    text-mask\n" +
                "  validators: \n" +
                "    absolute-url\n" +
                "    checkbox\n" +
                "    choice-list\n" +
                "    collection\n" +
                "    email-address\n" +
                "    expression\n" +
                "    non-null\n" +
                "    text-length\n" +
                "    text-mask\n" +
                "  valueSeparator: ','\n"
        );
    }

    @Test
    public void testSpreadsheetMetadataWithDefaultsPrintTree() {
        this.treePrintAndCheck(
            SPREADSHEET_METADATA,
            "SpreadsheetMetadataNonEmpty\n" +
                "  auditInfo: \n" +
                "    AuditInfo\n" +
                "      created\n" +
                "        user123@example.com 1999-12-31T12:58:59\n" +
                "      modified\n" +
                "        user123@example.com 1999-12-31T12:58:59\n" +
                "  autoHideScrollbars: false\n" +
                "  cellCharacterWidth: 1\n" +
                "  color1: black\n" +
                "  color2: white\n" +
                "  colorBlack: 1\n" +
                "  colorWhite: 2\n" +
                "  comparators: \n" +
                "    background-color\n" +
                "    border-bottom-color\n" +
                "    border-color\n" +
                "    border-left-color\n" +
                "    border-right-color\n" +
                "    border-top-color\n" +
                "    color\n" +
                "    currency\n" +
                "    custom-list\n" +
                "    custom-list-case-insensitive\n" +
                "    date\n" +
                "    date-time\n" +
                "    day-of-month\n" +
                "    day-of-week\n" +
                "    error\n" +
                "    formatter\n" +
                "    hour-of-am-pm\n" +
                "    hour-of-day\n" +
                "    locale\n" +
                "    minute-of-hour\n" +
                "    month-of-year\n" +
                "    nano-of-second\n" +
                "    number\n" +
                "    outline-color\n" +
                "    parser\n" +
                "    seconds-of-minute\n" +
                "    text\n" +
                "    text-case-insensitive\n" +
                "    text-decoration-color\n" +
                "    text-with-numbers\n" +
                "    text-with-numbers-case-insensitive\n" +
                "    time\n" +
                "    validator\n" +
                "    value-type\n" +
                "    year\n" +
                "  converters: \n" +
                "    binary\n" +
                "    binary-to-text\n" +
                "    boolean\n" +
                "    boolean-to-text\n" +
                "    collection\n" +
                "    collection-to\n" +
                "    collection-to-list\n" +
                "    color\n" +
                "    color-to-number\n" +
                "    csv\n" +
                "    currency\n" +
                "    currency-code-to-currency\n" +
                "    currency-value-to\n" +
                "    currency-value-to-number\n" +
                "    date-time\n" +
                "    date-time-symbols\n" +
                "    decimal-number-symbols\n" +
                "    environment\n" +
                "    environment-to-binary\n" +
                "    environment-to-text\n" +
                "    error-throwing\n" +
                "    error-to-error\n" +
                "    error-to-number\n" +
                "    expression\n" +
                "    form-and-validation\n" +
                "    format-pattern-to-string\n" +
                "    json\n" +
                "    json-to\n" +
                "    locale\n" +
                "    locale-to-text\n" +
                "    logging\n" +
                "    net\n" +
                "    never\n" +
                "    null-to-number\n" +
                "    number\n" +
                "    number-to-color\n" +
                "    number-to-currency-value\n" +
                "    number-to-number\n" +
                "    number-to-text\n" +
                "    optional-to\n" +
                "    plugins\n" +
                "    properties\n" +
                "    properties-to-date-time-symbols\n" +
                "    properties-to-decimal-number-symbols\n" +
                "    properties-to-spreadsheet-metadata\n" +
                "    properties-to-text-style\n" +
                "    simple\n" +
                "    spreadsheet-cell-set\n" +
                "    spreadsheet-id-to-spreadsheet-metadata\n" +
                "    spreadsheet-metadata\n" +
                "    spreadsheet-selection\n" +
                "    spreadsheet-selection-to-spreadsheet-selection\n" +
                "    spreadsheet-selection-to-text\n" +
                "    storage\n" +
                "    storage-binary-to-storage-value-binary\n" +
                "    storage-binary-to-storage-value-csv\n" +
                "    storage-binary-to-storage-value-environment\n" +
                "    storage-binary-to-storage-value-expression\n" +
                "    storage-binary-to-storage-value-json\n" +
                "    storage-binary-to-storage-value-properties\n" +
                "    storage-binary-to-storage-value-tsv\n" +
                "    storage-binary-to-storage-value-txt\n" +
                "    storage-value-info-list-to-text\n" +
                "    storage-value-to-storage-binary-binary\n" +
                "    storage-value-to-storage-binary-csv\n" +
                "    storage-value-to-storage-binary-environment\n" +
                "    storage-value-to-storage-binary-expression\n" +
                "    storage-value-to-storage-binary-json\n" +
                "    storage-value-to-storage-binary-properties\n" +
                "    storage-value-to-storage-binary-tsv\n" +
                "    storage-value-to-storage-binary-txt\n" +
                "    style\n" +
                "    system\n" +
                "    template\n" +
                "    text\n" +
                "    text-node\n" +
                "    text-to-binary\n" +
                "    text-to-boolean-list\n" +
                "    text-to-border\n" +
                "    text-to-charset\n" +
                "    text-to-color\n" +
                "    text-to-converter-selector\n" +
                "    text-to-csv-string-list\n" +
                "    text-to-csv-string-set\n" +
                "    text-to-currency\n" +
                "    text-to-currency-code\n" +
                "    text-to-currency-exchange-rater-selector\n" +
                "    text-to-currency-value\n" +
                "    text-to-date-list\n" +
                "    text-to-date-time-list\n" +
                "    text-to-email-address\n" +
                "    text-to-environment\n" +
                "    text-to-environment-value-name\n" +
                "    text-to-error\n" +
                "    text-to-expression\n" +
                "    text-to-expression-function-selector\n" +
                "    text-to-flag\n" +
                "    text-to-form-name\n" +
                "    text-to-has-host-address\n" +
                "    text-to-host-address\n" +
                "    text-to-indentation\n" +
                "    text-to-json\n" +
                "    text-to-json-pointer\n" +
                "    text-to-json-selector\n" +
                "    text-to-line-ending\n" +
                "    text-to-locale\n" +
                "    text-to-locale-language-tag\n" +
                "    text-to-logging-level\n" +
                "    text-to-margin\n" +
                "    text-to-media-type\n" +
                "    text-to-number-list\n" +
                "    text-to-object\n" +
                "    text-to-padding\n" +
                "    text-to-path\n" +
                "    text-to-properties\n" +
                "    text-to-spreadsheet-comparator-selector\n" +
                "    text-to-spreadsheet-formatter-selector\n" +
                "    text-to-spreadsheet-id\n" +
                "    text-to-spreadsheet-metadata\n" +
                "    text-to-spreadsheet-metadata-color\n" +
                "    text-to-spreadsheet-metadata-property-name\n" +
                "    text-to-spreadsheet-name\n" +
                "    text-to-spreadsheet-parser-selector\n" +
                "    text-to-spreadsheet-selection\n" +
                "    text-to-spreadsheet-text\n" +
                "    text-to-storage-path\n" +
                "    text-to-string-list\n" +
                "    text-to-template-value-name\n" +
                "    text-to-text\n" +
                "    text-to-text-node\n" +
                "    text-to-text-style\n" +
                "    text-to-text-style-property-name\n" +
                "    text-to-time-list\n" +
                "    text-to-tsv-string-list\n" +
                "    text-to-tsv-string-set\n" +
                "    text-to-url\n" +
                "    text-to-url-fragment\n" +
                "    text-to-url-query-string\n" +
                "    text-to-validation-error\n" +
                "    text-to-validator-selector\n" +
                "    text-to-value-type\n" +
                "    text-to-web-color-name\n" +
                "    text-to-zone-offset\n" +
                "    to-binary\n" +
                "    to-boolean\n" +
                "    to-color\n" +
                "    to-color-properties\n" +
                "    to-csv-string-list\n" +
                "    to-currency\n" +
                "    to-currency-code\n" +
                "    to-date-time-symbols\n" +
                "    to-decimal-number-symbols\n" +
                "    to-environment\n" +
                "    to-formatter-selector\n" +
                "    to-has-host-address\n" +
                "    to-host-address\n" +
                "    to-json-node\n" +
                "    to-locale\n" +
                "    to-locale-language-tag\n" +
                "    to-multi-line-text\n" +
                "    to-number\n" +
                "    to-parser-selector\n" +
                "    to-properties\n" +
                "    to-spreadsheet-selection\n" +
                "    to-string\n" +
                "    to-style\n" +
                "    to-styleable\n" +
                "    to-text-node\n" +
                "    to-tsv-string-list\n" +
                "    to-validation-checkbox\n" +
                "    to-validation-choice\n" +
                "    to-validation-choice-list\n" +
                "    to-validation-error-list\n" +
                "    to-validator-selector\n" +
                "    to-value\n" +
                "    to-value-type\n" +
                "    tsv\n" +
                "    url\n" +
                "    url-to-hyperlink\n" +
                "    url-to-image\n" +
                "    value\n" +
                "  currency: AUD (java.util.Currency)\n" +
                "  dateFormatter: \n" +
                "    date\n" +
                "      \"yyyy/mm/dd\"\n" +
                "  dateParser: \n" +
                "    date\n" +
                "      \"yyyy/mm/dd\"\n" +
                "  dateTimeFormatter: \n" +
                "    date-time\n" +
                "      \"yyyy/mm/dd hh:mm\"\n" +
                "  dateTimeOffset: -25569L\n" +
                "  dateTimeParser: \n" +
                "    date-time\n" +
                "      \"yyyy/mm/dd hh:mm\"\n" +
                "  dateTimeSymbols: \n" +
                "    DateTimeSymbols\n" +
                "      ampms\n" +
                "        am\n" +
                "        pm\n" +
                "      monthNames\n" +
                "        January\n" +
                "        February\n" +
                "        March\n" +
                "        April\n" +
                "        May\n" +
                "        June\n" +
                "        July\n" +
                "        August\n" +
                "        September\n" +
                "        October\n" +
                "        November\n" +
                "        December\n" +
                "      monthNameAbbreviations\n" +
                "        Jan.\n" +
                "        Feb.\n" +
                "        Mar.\n" +
                "        Apr.\n" +
                "        May\n" +
                "        Jun.\n" +
                "        Jul.\n" +
                "        Aug.\n" +
                "        Sep.\n" +
                "        Oct.\n" +
                "        Nov.\n" +
                "        Dec.\n" +
                "      weekDayNames\n" +
                "        Sunday\n" +
                "        Monday\n" +
                "        Tuesday\n" +
                "        Wednesday\n" +
                "        Thursday\n" +
                "        Friday\n" +
                "        Saturday\n" +
                "      weekDayNameAbbreviations\n" +
                "        Sun.\n" +
                "        Mon.\n" +
                "        Tue.\n" +
                "        Wed.\n" +
                "        Thu.\n" +
                "        Fri.\n" +
                "        Sat.\n" +
                "  decimalNumberDigitCount: 8\n" +
                "  decimalNumberSymbols: \n" +
                "    DecimalNumberSymbols\n" +
                "      negativeSign\n" +
                "        '-'\n" +
                "      positiveSign\n" +
                "        '+'\n" +
                "      zeroDigit\n" +
                "        '0'\n" +
                "      currencySymbol\n" +
                "        \"$\"\n" +
                "      decimalSeparator\n" +
                "        '.'\n" +
                "      exponentSymbol\n" +
                "        \"e\"\n" +
                "      groupSeparator\n" +
                "        ','\n" +
                "      infinitySymbol\n" +
                "        \"∞\"\n" +
                "      monetaryDecimalSeparator\n" +
                "        '.'\n" +
                "      nanSymbol\n" +
                "        \"NaN\"\n" +
                "      percentSymbol\n" +
                "        '%'\n" +
                "      permillSymbol\n" +
                "        '‰'\n" +
                "  defaultFormHandler: \n" +
                "    basic\n" +
                "  defaultYear: 2000\n" +
                "  errorFormatter: \n" +
                "    badge-error\n" +
                "      \"text @\"\n" +
                "  exporters: \n" +
                "    collection\n" +
                "    empty\n" +
                "    json\n" +
                "  expressionNumberKind: BIG_DECIMAL\n" +
                "  formatters: \n" +
                "    accounting\n" +
                "    automatic\n" +
                "    badge-error\n" +
                "    collection\n" +
                "    currency\n" +
                "    date\n" +
                "    date-time\n" +
                "    default-text\n" +
                "    expression\n" +
                "    full-date\n" +
                "    full-date-time\n" +
                "    full-time\n" +
                "    general\n" +
                "    hyperlinking\n" +
                "    long-date\n" +
                "    long-date-time\n" +
                "    long-time\n" +
                "    medium-date\n" +
                "    medium-date-time\n" +
                "    medium-time\n" +
                "    number\n" +
                "    percent\n" +
                "    scientific\n" +
                "    short-date\n" +
                "    short-date-time\n" +
                "    short-time\n" +
                "    text\n" +
                "    time\n" +
                "  formattingConverter: \n" +
                "    collection\n" +
                "      \"(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, json, currency, logging, plugins, properties, spreadsheet-metadata, storage, style, text-node, template, net, optional-to, collection-to)\"\n" +
                "  formattingCurrencyExchangeRater: \n" +
                "    storage-properties\n" +
                "      \"(\\\"/samples/CurrencyExchangeRates.properties\\\")\"\n" +
                "  formattingFunctions: \n" +
                "  formHandlers: \n" +
                "  formulaConverter: \n" +
                "    collection\n" +
                "      \"(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, json, currency, logging, plugins, properties, spreadsheet-metadata, storage, style, text-node, template, net, optional-to, collection-to)\"\n" +
                "  formulaCurrencyExchangeRater: \n" +
                "    storage-properties\n" +
                "      \"(\\\"/samples/CurrencyExchangeRates.properties\\\")\"\n" +
                "  formulaFunctions: \n" +
                "  functions: \n" +
                "  importers: \n" +
                "    collection\n" +
                "    empty\n" +
                "    json\n" +
                "  locale: en_AU (java.util.Locale)\n" +
                "  numberFormatter: \n" +
                "    number\n" +
                "      \"0.#;0.#;0\"\n" +
                "  numberParser: \n" +
                "    number\n" +
                "      \"0.#;0.#;0\"\n" +
                "  parsers: \n" +
                "    date\n" +
                "    date-time\n" +
                "    full-date\n" +
                "    full-date-time\n" +
                "    full-time\n" +
                "    general\n" +
                "    long-date\n" +
                "    long-date-time\n" +
                "    long-time\n" +
                "    medium-date\n" +
                "    medium-date-time\n" +
                "    medium-time\n" +
                "    number\n" +
                "    short-date\n" +
                "    short-date-time\n" +
                "    short-time\n" +
                "    time\n" +
                "    whole-number\n" +
                "  plugins: \n" +
                "  precision: 7\n" +
                "  queryConverter: \n" +
                "    collection\n" +
                "      \"(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, properties, spreadsheet-metadata, style, text-node, template, net, optional-to, collection-to)\"\n" +
                "  queryFunctions: \n" +
                "  roundingMode: HALF_UP\n" +
                "  scriptingConverter: \n" +
                "    collection\n" +
                "      \"(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, json, currency, logging, plugins, properties, spreadsheet-metadata, storage, style, text-node, template, net, optional-to, collection-to)\"\n" +
                "  scriptingCurrencyExchangeRater: \n" +
                "    storage-properties\n" +
                "      \"(\\\"/samples/CurrencyExchangeRates.properties\\\")\"\n" +
                "  scriptingFunctions: \n" +
                "  showFormulaEditor: true\n" +
                "  showFormulas: false\n" +
                "  showGridLines: true\n" +
                "  showHeadings: true\n" +
                "  sortComparators: \n" +
                "    [background-color, border-bottom-color, border-color, border-left-color, border-right-color, border-top-color, color, currency, custom-list, custom-list-case-insensitive, date, datetime, day-of-month, day-of-year, formatter, hour-of-ampm, hour-of-day, locale, minute-of-hour, month-of-year, nano-of-second, number, outline-color, parser, seconds-of-minute, text, text-case-insensitive, time, validator, value-type, year]\n" +
                "  sortConverter: \n" +
                "    collection\n" +
                "      \"(null-to-number, simple, text, boolean, number, date-time, locale, value, optional-to, collection-to)\"\n" +
                "  style: \n" +
                "    TextStyle\n" +
                "      height=50px\n" +
                "      width=100px\n" +
                "  textFormatter: \n" +
                "    text\n" +
                "      \"@\"\n" +
                "  timeFormatter: \n" +
                "    time\n" +
                "      \"hh:mm:ss\"\n" +
                "  timeParser: \n" +
                "    time\n" +
                "      \"hh:mm:ss\"\n" +
                "  twoDigitYear: 50\n" +
                "  validationConverter: \n" +
                "    collection\n" +
                "      \"(null-to-number, simple, text, boolean, number, date-time, environment, value, error-throwing, expression, form-and-validation, locale, logging, plugins, properties, template, json, optional-to, collection-to)\"\n" +
                "  validationCurrencyExchangeRater: \n" +
                "    storage-properties\n" +
                "      \"(\\\"/samples/CurrencyExchangeRates.properties\\\")\"\n" +
                "  validationFunctions: \n" +
                "  validationValidators: \n" +
                "    absolute-url\n" +
                "    checkbox\n" +
                "    choice-list\n" +
                "    collection\n" +
                "    email-address\n" +
                "    expression\n" +
                "    non-null\n" +
                "    text-length\n" +
                "    text-mask\n" +
                "  validators: \n" +
                "    absolute-url\n" +
                "    checkbox\n" +
                "    choice-list\n" +
                "    collection\n" +
                "    email-address\n" +
                "    expression\n" +
                "    non-null\n" +
                "    text-length\n" +
                "    text-mask\n" +
                "  valueSeparator: ','\n"
        );
    }
}
