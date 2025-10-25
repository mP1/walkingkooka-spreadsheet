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

package walkingkooka.spreadsheet.format.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.Url;
import walkingkooka.plugin.FakeProviderContext;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.convert.SpreadsheetConverters;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.spreadsheet.format.parser.TextSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoaders;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviders;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.text.TextNode;
import walkingkooka.validation.form.FormHandlerContexts;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;
import java.util.Optional;

public final class SpreadsheetFormattersSpreadsheetFormatterProviderTest implements SpreadsheetFormatterProviderTesting<SpreadsheetFormattersSpreadsheetFormatterProvider>,
    ToStringTesting<SpreadsheetFormattersSpreadsheetFormatterProvider>,
    SpreadsheetMetadataTesting {

    private final static ProviderContext PROVIDER_CONTEXT = new FakeProviderContext() {
        @Override
        public <T> T convertOrFail(final Object value,
                                   final Class<T> type) {
            if (value instanceof String && type == Expression.class) {
                return SpreadsheetConverters.textToExpression()
                    .convertOrFail(
                        value,
                        type,
                        SpreadsheetMetadataTesting.SPREADSHEET_FORMATTER_CONTEXT
                    );
            }
            if (value instanceof Double && type == Integer.class) {
                return type.cast(
                    ((Double)value).intValue()
                );
            }

            throw this.convertThrowable(
                "Only support converting String to Expression but got " + value.getClass().getSimpleName() + " " + type.getSimpleName(),
                value,
                type
            );
        }

        @Override
        public Locale locale() {
            return SpreadsheetMetadataTesting.LOCALE;
        }

        @Override
        public <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
            return SpreadsheetMetadataTesting.PROVIDER_CONTEXT.environmentValue(name);
        }

        @Override
        public LocalDateTime now() {
            return NOW.now();
        }
    };

    private final static SpreadsheetFormatterProviderSamplesContext SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT = SpreadsheetFormatterProviderSamplesContexts.basic(
        METADATA_EN_AU.spreadsheetFormatterContext(
            SpreadsheetMetadata.NO_CELL,
            (Optional<Object> value) -> SpreadsheetExpressionEvaluationContexts.basic(
                SpreadsheetMetadata.NO_CELL,
                SpreadsheetExpressionReferenceLoaders.fake(),
                Url.parseAbsolute("https://example.com"),
                METADATA_EN_AU,
                SpreadsheetStoreRepositories.fake(),
                SPREADSHEET_FORMULA_CONVERTER_CONTEXT,
                ENVIRONMENT_CONTEXT,
                (Optional<SpreadsheetCell> cell) -> {
                    throw new UnsupportedOperationException();
                }, // spreadsheetFormatterContextFactory
                FormHandlerContexts.fake(),
                TERMINAL_CONTEXT,
                ExpressionFunctionProviders.fake(),
                PROVIDER_CONTEXT
            ),
            SPREADSHEET_LABEL_NAME_RESOLVER,
            LOCALE_CONTEXT,
            SPREADSHEET_PROVIDER,
            PROVIDER_CONTEXT
        ),
        PROVIDER_CONTEXT
    );

    // SpreadsheetFormatterName.........................................................................................

    // date.............................................................................................................

    @Test
    public void testSpreadsheetFormatterNameWithDate() {
        this.spreadsheetFormatterAndCheck(
            SpreadsheetFormatterName.with("date"),
            Lists.of("dd/mm/yy"),
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseDateFormatPattern("dd/mm/yy").formatter()
        );
    }

    // date-time........................................................................................................

    @Test
    public void testSpreadsheetFormatterNameWithDateTime() {
        this.spreadsheetFormatterAndCheck(
            SpreadsheetFormatterName.with("date-time"),
            Lists.of(
                "dd/mm/yyyy hh:mm:ss"
            ),
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseDateTimeFormatPattern("dd/mm/yyyy hh:mm:ss").formatter()
        );
    }

    // expression.......................................................................................................

    @Test
    public void testSpreadsheetFormatterNameWithExpressionWithStringLiteralExpression() {
        this.spreadsheetFormatterAndCheck(
            "expression \"Hello\"",
            PROVIDER_CONTEXT,
            SpreadsheetFormatters.expression(
                Expression.value("Hello")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterNameWithExpressionWithAddition() {
        this.spreadsheetFormatterAndCheck(
            "expression 1+2",
            PROVIDER_CONTEXT,
            SpreadsheetFormatters.expression(
                Expression.add(
                    Expression.value(
                        EXPRESSION_NUMBER_KIND.one()
                    ),
                    Expression.value(
                        EXPRESSION_NUMBER_KIND.create(2)
                    )
                )
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterNameWithGeneral() {
        this.spreadsheetFormatterAndCheck(
            SpreadsheetFormatterName.with("general"),
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetFormatters.general()
        );
    }

    @Test
    public void testSpreadsheetFormatterNameWithLongDate() {
        this.spreadsheetFormatterAndCheck(
            SpreadsheetFormatterName.LONG_DATE,
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseDateParsePattern("d mmmm yyyy")
                .formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterNameWithLongDateTime() {
        this.spreadsheetFormatterAndCheck(
            SpreadsheetFormatterName.LONG_DATE_TIME,
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseDateTimeParsePattern("d mmmm yyyy \\a\\t h:mm:ss AM/PM")
                .formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterNameWithMediumDate() {
        this.spreadsheetFormatterAndCheck(
            SpreadsheetFormatterName.MEDIUM_DATE,
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseDateParsePattern("d mmm yyyy")
                .formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterNameWithMediumDateTime() {
        this.spreadsheetFormatterAndCheck(
            SpreadsheetFormatterName.MEDIUM_DATE_TIME,
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseDateTimeParsePattern("d mmm yyyy, h:mm:ss AM/PM")
                .formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterNameWithMediumTime() {
        this.spreadsheetFormatterAndCheck(
            SpreadsheetFormatterName.MEDIUM_TIME,
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseTimeParsePattern("h:mm:ss AM/PM")
                .formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterNameWithNumber() {
        this.spreadsheetFormatterAndCheck(
            SpreadsheetFormatterName.with("number"),
            Lists.of("$0.00"),
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseNumberFormatPattern("$0.00").formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterNameWithShortDate() {
        this.spreadsheetFormatterAndCheck(
            SpreadsheetFormatterName.SHORT_DATE,
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseDateParsePattern("d/m/yy")
                .formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterNameWithShortDateTime() {
        this.spreadsheetFormatterAndCheck(
            SpreadsheetFormatterName.SHORT_DATE_TIME,
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseDateTimeParsePattern("d/m/yy, h:mm AM/PM")
                .formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterNameWithShortTime() {
        this.spreadsheetFormatterAndCheck(
            SpreadsheetFormatterName.SHORT_TIME,
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseTimeParsePattern("h:mm AM/PM")
                .formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterNameWithText() {
        this.spreadsheetFormatterAndCheck(
            SpreadsheetFormatterName.with("text"),
            Lists.of(
                "@@\"Hello\""
            ),
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseTextFormatPattern("@@\"Hello\"").formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterNameWithTime() {
        this.spreadsheetFormatterAndCheck(
            SpreadsheetFormatterName.with("time"),
            Lists.of("hh:mm:ss"),
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseTimeFormatPattern("hh:mm:ss").formatter()
        );
    }

    // SpreadsheetFormatterSelector.....................................................................................

    // automatic........................................................................................................

    @Test
    public void testSpreadsheetFormatterSelectorWithAutomaticSixParameters() {
        this.spreadsheetFormatterAndCheck(
            "automatic (date(\"dd/mm/yy\"), date-time(\"dd/mm/yy hh:mm\"), text(\"\\\"Error\\\" @\"), number(\"0.00\"), text(\"@@\"), time(\"hh:mm\"))",
            PROVIDER_CONTEXT,
            SpreadsheetFormatters.automatic(
                SpreadsheetPattern.parseDateFormatPattern("dd/mm/yy").formatter(),
                SpreadsheetPattern.parseDateTimeFormatPattern("dd/mm/yy hh:mm").formatter(),
                SpreadsheetPattern.parseTextFormatPattern("\"Error\" @").formatter(),
                SpreadsheetPattern.parseNumberFormatPattern("0.00").formatter(),
                SpreadsheetPattern.parseTextFormatPattern("@@").formatter(),
                SpreadsheetPattern.parseTimeFormatPattern("hh:mm").formatter()
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWithAutomaticZeroParameters() {
        this.spreadsheetFormatterAndCheck(
            "automatic",
            PROVIDER_CONTEXT,
            SpreadsheetFormatters.automatic(
                METADATA_EN_AU.getOrFail(SpreadsheetMetadataPropertyName.DATE_FORMATTER)
                    .spreadsheetFormatPattern()
                    .get()
                    .formatter(),
                METADATA_EN_AU.getOrFail(SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER)
                    .spreadsheetFormatPattern()
                    .get()
                    .formatter(),
                SpreadsheetFormattersSpreadsheetFormatterProvider.INSTANCE.spreadsheetFormatter(
                    METADATA_EN_AU.getOrFail(SpreadsheetMetadataPropertyName.ERROR_FORMATTER),
                    PROVIDER_CONTEXT
                ),
//
//                METADATA_EN_AU.getOrFail(SpreadsheetMetadataPropertyName.ERROR_FORMATTER)
//                    .spreadsheetFormatPattern()
//                    .get()
//                    .formatter(),
//                SpreadsheetFormatters.badgeError(
//                    SpreadsheetFormatters.p
//                ),
                METADATA_EN_AU.getOrFail(SpreadsheetMetadataPropertyName.NUMBER_FORMATTER)
                    .spreadsheetFormatPattern()
                    .get()
                    .formatter(),
                METADATA_EN_AU.getOrFail(SpreadsheetMetadataPropertyName.TEXT_FORMATTER)
                    .spreadsheetFormatPattern()
                    .get()
                    .formatter(),
                METADATA_EN_AU.getOrFail(SpreadsheetMetadataPropertyName.TIME_FORMATTER)
                    .spreadsheetFormatPattern()
                    .get()
                    .formatter()
            )
        );
    }

    // badge............................................................................................................

    @Test
    public void testSpreadsheetFormatterSelectorWithBadgeError() {
        this.spreadsheetFormatterAndCheck(
            "badge-error text \"Hello \"@@",
            PROVIDER_CONTEXT,
            SpreadsheetFormatters.badgeError(
                SpreadsheetPattern.parseTextFormatPattern("\"Hello \"@@")
                    .formatter()
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWithBadgeError2() {
        this.spreadsheetFormatterAndCheck(
            "badge-error(text \"Hello \"@@)",
            PROVIDER_CONTEXT,
            SpreadsheetFormatters.badgeError(
                SpreadsheetPattern.parseTextFormatPattern("\"Hello \"@@")
                    .formatter()
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWithBadgeError3() {
        this.spreadsheetFormatterAndCheck(
            "badge-error text(\"Hello \"@@)",
            PROVIDER_CONTEXT,
            SpreadsheetFormatters.badgeError(
                SpreadsheetFormatters.text(
                    SpreadsheetPattern.parseTextFormatPattern("(\"Hello \"@@)").value()
                        .cast(TextSpreadsheetFormatParserToken.class)
                )
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWithBadgeError4() {
        this.spreadsheetFormatterAndCheck(
            "badge-error (text(\"Hello \"@@))",
            PROVIDER_CONTEXT,
            SpreadsheetFormatters.badgeError(
                SpreadsheetFormatters.text(
                    SpreadsheetPattern.parseTextFormatPattern("(\"Hello \"@@)").value()
                        .cast(TextSpreadsheetFormatParserToken.class)
                )
            )
        );
    }

    // collection.......................................................................................................

    @Test
    public void testSpreadsheetFormatterSelectorWithCollection() {
        this.spreadsheetFormatterAndCheck(
            "collection (date(\"dd/mm/yy\"), date-time(\"dd/mm/yy hh:mm\"), number(\"0.00\"), text(\"@@\"), time(\"hh:mm\"))",
            PROVIDER_CONTEXT,
            SpreadsheetFormatters.collection(
                Lists.of(
                    SpreadsheetPattern.parseDateFormatPattern("dd/mm/yy").formatter(),
                    SpreadsheetPattern.parseDateTimeFormatPattern("dd/mm/yy hh:mm").formatter(),
                    SpreadsheetPattern.parseNumberFormatPattern("0.00").formatter(),
                    SpreadsheetPattern.parseTextFormatPattern("@@").formatter(),
                    SpreadsheetPattern.parseTimeFormatPattern("hh:mm").formatter()
                )
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWithCurrency() {
        this.spreadsheetFormatterAndCheck(
            "currency",
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseNumberFormatPattern("$0.00")
                .formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWithCurrencyWithThree() {
        this.spreadsheetFormatterAndCheck(
            "currency(3)",
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseNumberFormatPattern("$0.000")
                .formatter()
        );
    }

    // date.............................................................................................................

    @Test
    public void testSpreadsheetFormatterSelectorWithDate() {
        this.spreadsheetFormatterAndCheck(
            "date dd/mm/yy",
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseDateFormatPattern("dd/mm/yy").formatter()
        );
    }

    // date-time........................................................................................................

    @Test
    public void testSpreadsheetFormatterSelectorWithDateTime() {
        this.spreadsheetFormatterAndCheck(
            "date-time dd/mm/yyyy hh:mm:ss",
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseDateTimeFormatPattern("dd/mm/yyyy hh:mm:ss").formatter()
        );
    }

    // default-text.....................................................................................................

    @Test
    public void testSpreadsheetFormatterSelectorWithDefaultText() {
        this.spreadsheetFormatterAndCheck(
            "default-text",
            PROVIDER_CONTEXT,
            SpreadsheetFormatters.defaultText()
        );
    }

    // expression.......................................................................................................

    @Test
    public void testSpreadsheetFormatterSelectorWithExpressionWithStringLiteralExpression() {
        this.spreadsheetFormatterAndCheck(
            "expression \"Hello\"",
            PROVIDER_CONTEXT,
            SpreadsheetFormatters.expression(
                Expression.value("Hello")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWithExpressionWithAdditionExpression() {
        this.spreadsheetFormatterAndCheck(
            "expression 1+2",
            PROVIDER_CONTEXT,
            SpreadsheetFormatters.expression(
                Expression.add(
                    Expression.value(
                        EXPRESSION_NUMBER_KIND.one()
                    ),
                    Expression.value(
                        EXPRESSION_NUMBER_KIND.create(2)
                    )
                )
            )
        );
    }

    // full-date........................................................................................................

    @Test
    public void testSpreadsheetFormatterSelectorWithFullDate() {
        this.spreadsheetFormatterAndCheck(
            SpreadsheetFormatterName.FULL_DATE.text(),
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseDateParsePattern("dddd, d mmmm yyyy")
                .formatter()
        );
    }
    
    // general..........................................................................................................

    @Test
    public void testSpreadsheetFormatterSelectorWithGeneral() {
        this.spreadsheetFormatterAndCheck(
            "general",
            PROVIDER_CONTEXT,
            SpreadsheetFormatters.general()
        );
    }

    // long-date........................................................................................................

    @Test
    public void testSpreadsheetFormatterSelectorWithLongDate() {
        this.spreadsheetFormatterAndCheck(
            "long-date",
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseDateParsePattern("d mmmm yyyy")
                .formatter()
        );
    }

    // medium-date......................................................................................................

    @Test
    public void testSpreadsheetFormatterSelectorWithMediumDate() {
        this.spreadsheetFormatterAndCheck(
            "medium-date",
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseDateParsePattern("d mmm yyyy")
                .formatter()
        );
    }

    // medium-date-time.................................................................................................

    @Test
    public void testSpreadsheetFormatterSelectorWithMediumDateTime() {
        this.spreadsheetFormatterAndCheck(
            "medium-date-time",
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseDateTimeParsePattern("d mmm yyyy, h:mm:ss AM/PM")
                .formatter()
        );
    }

    // medium-time......................................................................................................

    @Test
    public void testSpreadsheetFormatterSelectorWithMediumTime() {
        this.spreadsheetFormatterAndCheck(
            "medium-time",
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseTimeParsePattern("h:mm:ss AM/PM")
                .formatter()
        );
    }

    // number...........................................................................................................

    @Test
    public void testSpreadsheetFormatterSelectorWithNumber() {
        this.spreadsheetFormatterAndCheck(
            "number $0.00",
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseNumberFormatPattern("$0.00").formatter()
        );
    }

    // short-date.......................................................................................................

    @Test
    public void testSpreadsheetFormatterSelectorWithShortDate() {
        this.spreadsheetFormatterAndCheck(
            "short-date",
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseDateParsePattern("d/m/yy")
                .formatter()
        );
    }

    // spreadsheet-pattern-collection...................................................................................

    @Test
    public void testSpreadsheetFormatterSelectorWithSpreadsheetPatternCollection() {
        this.spreadsheetFormatterAndCheck(
            "spreadsheet-pattern-collection (date(\"dd/mm/yy\"), date-time(\"dd/mm/yy hh:mm\"), number(\"0.00\"), text(\"@@\"), time(\"hh:mm\"))",
            PROVIDER_CONTEXT,
            SpreadsheetFormatters.spreadsheetPatternCollection(
                Lists.of(
                    SpreadsheetPattern.parseDateFormatPattern("dd/mm/yy").formatter(),
                    SpreadsheetPattern.parseDateTimeFormatPattern("dd/mm/yy hh:mm").formatter(),
                    SpreadsheetPattern.parseNumberFormatPattern("0.00").formatter(),
                    SpreadsheetPattern.parseTextFormatPattern("@@").formatter(),
                    SpreadsheetPattern.parseTimeFormatPattern("hh:mm").formatter()
                )
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWithText() {
        this.spreadsheetFormatterAndCheck(
            "text @@\"Hello\"",
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseTextFormatPattern("@@\"Hello\"").formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWithTime() {
        this.spreadsheetFormatterAndCheck(
            "time hh:mm:ss",
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseTimeFormatPattern("hh:mm:ss").formatter()
        );
    }

    // testSpreadsheetFormatterNextToken................................................................................

    // automatic........................................................................................................

    @Test
    public void testSpreadsheetFormatterNextTokenWithAutomatic() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterName.AUTOMATIC.setValueText("")
        );
    }

    // badge-error......................................................................................................

    @Test
    public void testSpreadsheetFormatterNextTokenWithBadgeError() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterName.BADGE_ERROR.setValueText("")
        );
    }

    // collection.........................................................................................................

    @Test
    public void testSpreadsheetFormatterNextTokenWithCollection() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterName.COLLECTION.setValueText("")
        );
    }

    // currency.........................................................................................................

    @Test
    public void testSpreadsheetFormatterNextTokenWithCurrency() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterName.CURRENCY.setValueText("")
        );
    }

    // date.............................................................................................................

    @Test
    public void testSpreadsheetFormatterNextTokenWithDateEmpty() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterSelector.parse("date"),
            SpreadsheetFormatterSelectorToken.with(
                "",
                "",
                Lists.of(
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "d",
                        "d"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "dd",
                        "dd"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "ddd",
                        "ddd"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "dddd",
                        "dddd"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "m",
                        "m"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mm",
                        "mm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mmm",
                        "mmm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mmmm",
                        "mmmm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mmmmm",
                        "mmmmm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "yy",
                        "yy"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "yyyy",
                        "yyyy"
                    )
                )
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterNextTokenWithDateNotEmpty() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterSelector.parse("date yyyy"),
            SpreadsheetFormatterSelectorToken.with(
                "",
                "",
                Lists.of(
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "d",
                        "d"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "dd",
                        "dd"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "ddd",
                        "ddd"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "dddd",
                        "dddd"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "m",
                        "m"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mm",
                        "mm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mmm",
                        "mmm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mmmm",
                        "mmmm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mmmmm",
                        "mmmmm"
                    )
                )
            )
        );
    }

    // date-time........................................................................................................

    @Test
    public void testSpreadsheetFormatterNextTokenWithDateTimeEmpty() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterSelector.parse("date-time"),
            SpreadsheetFormatterSelectorToken.with(
                "",
                "",
                Lists.of(
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        ".",
                        "."
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "0",
                        "0"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "A/P",
                        "A/P"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "AM/PM",
                        "AM/PM"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "a/p",
                        "a/p"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "am/pm",
                        "am/pm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "d",
                        "d"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "dd",
                        "dd"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "ddd",
                        "ddd"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "dddd",
                        "dddd"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "h",
                        "h"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "hh",
                        "hh"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "m",
                        "m"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mm",
                        "mm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mmm",
                        "mmm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mmmm",
                        "mmmm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mmmmm",
                        "mmmmm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "s",
                        "s"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "ss",
                        "ss"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "yy",
                        "yy"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "yyyy",
                        "yyyy"
                    )
                )
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterNextTokenWithDateTimeNotEmpty() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterSelector.parse("date-time yyyy"),
            SpreadsheetFormatterSelectorToken.with(
                "",
                "",
                Lists.of(
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        ".",
                        "."
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "0",
                        "0"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "A/P",
                        "A/P"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "AM/PM",
                        "AM/PM"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "a/p",
                        "a/p"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "am/pm",
                        "am/pm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "d",
                        "d"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "dd",
                        "dd"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "ddd",
                        "ddd"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "dddd",
                        "dddd"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "h",
                        "h"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "hh",
                        "hh"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "m",
                        "m"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mm",
                        "mm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mmm",
                        "mmm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mmmm",
                        "mmmm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mmmmm",
                        "mmmmm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "s",
                        "s"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "ss",
                        "ss"
                    )
                )
            )
        );
    }

    // default-text.....................................................................................................

    @Test
    public void testSpreadsheetFormatterNextTokenWithDefaultText() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterName.DEFAULT_TEXT.setValueText("")
        );
    }

    // full-date........................................................................................................

    @Test
    public void testSpreadsheetFormatterNextTokenWithFullDate() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterName.LONG_DATE.setValueText("")
        );
    }

    // general..........................................................................................................

    @Test
    public void testSpreadsheetFormatterNextTokenWithGeneral() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterName.GENERAL.setValueText("")
        );
    }

    // long-date........................................................................................................

    @Test
    public void testSpreadsheetFormatterNextTokenWithLongDate() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterName.LONG_DATE.setValueText("")
        );
    }

    // medium-date......................................................................................................

    @Test
    public void testSpreadsheetFormatterNextTokenWithMediumDate() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterSelector.parse("medium-date")
        );
    }

    // medium-date-time.................................................................................................

    @Test
    public void testSpreadsheetFormatterNextTokenWithMediumDateTime() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterName.MEDIUM_DATE_TIME.setValueText("")
        );
    }

    // medium-time......................................................................................................

    @Test
    public void testSpreadsheetFormatterNextTokenWithMediumTime() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterSelector.parse("medium-time")
        );
    }

    // number...........................................................................................................

    @Test
    public void testSpreadsheetFormatterNextTokenWithNumberEmpty() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterSelector.parse("number"),
            SpreadsheetFormatterSelectorToken.with(
                "",
                "",
                Lists.of(
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "#",
                        "#"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "$",
                        "$"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "%",
                        "%"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        ",",
                        ","
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        ".",
                        "."
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "/",
                        "/"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "0",
                        "0"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "?",
                        "?"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "E",
                        "E"
                    )
                )
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterNextTokenWithNumberNotEmpty() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterSelector.parse("number $0.00"),
            SpreadsheetFormatterSelectorToken.with(
                "",
                "",
                Lists.of(
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "#",
                        "#"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "$",
                        "$"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "%",
                        "%"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        ",",
                        ","
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        ".",
                        "."
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "/",
                        "/"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "?",
                        "?"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "E",
                        "E"
                    )
                )
            )
        );
    }

    // percent..........................................................................................................

    @Test
    public void testSpreadsheetFormatterNextTokenWithPercent() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterName.PERCENT.setValueText("")
        );
    }

    // scientific.......................................................................................................

    @Test
    public void testSpreadsheetFormatterNextTokenWithScientific() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterName.SCIENTIFIC.setValueText("")
        );
    }

    // short-date......................................................................................................

    @Test
    public void testSpreadsheetFormatterNextTokenWithShortDate() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterSelector.parse("short-date")
        );
    }

    // short-date-time..................................................................................................

    @Test
    public void testSpreadsheetFormatterNextTokenWithShortDateTime() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterName.SHORT_DATE_TIME.setValueText("")
        );
    }

    // spreadsheet-pattern-collection...................................................................................

    @Test
    public void testSpreadsheetFormatterSelectorWithNextTokenSpreadsheetPatternCollection() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterSelector.parse(
                "spreadsheet-pattern-collection (date(\"dd/mm/yy\"), date-time(\"dd/mm/yy hh:mm\"), number(\"0.00\"), text(\"@@\"), time(\"hh:mm\"))"
            )
        );
    }

    // text.............................................................................................................

    @Test
    public void testSpreadsheetFormatterNextTokenWithTextEmpty() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterSelector.parse("text"),
            SpreadsheetFormatterSelectorToken.with(
                "",
                "",
                Lists.of(
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "* ",
                        "* "
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "@",
                        "@"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "_ ",
                        "_ "
                    )
                )
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterNextTokenWithTextNotEmpty() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterSelector.parse("text @"),
            SpreadsheetFormatterSelectorToken.with(
                "",
                "",
                Lists.of(
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "* ",
                        "* "
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "_ ",
                        "_ "
                    )
                )
            )
        );
    }

    // time.............................................................................................................

    @Test
    public void testSpreadsheetFormatterNextTokenWithTimeEmpty() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterSelector.parse("time"),
            SpreadsheetFormatterSelectorToken.with(
                "",
                "",
                Lists.of(
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        ".",
                        "."
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "0",
                        "0"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "A/P",
                        "A/P"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "AM/PM",
                        "AM/PM"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "a/p",
                        "a/p"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "am/pm",
                        "am/pm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "h",
                        "h"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "hh",
                        "hh"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "m",
                        "m"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mm",
                        "mm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "s",
                        "s"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "ss",
                        "ss"
                    )
                )
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterNextTokenWithTimeNotEmpty() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterSelector.parse("time hh:mm"),
            SpreadsheetFormatterSelectorToken.with(
                "",
                "",
                Lists.of(
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        ".",
                        "."
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "0",
                        "0"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "A/P",
                        "A/P"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "AM/PM",
                        "AM/PM"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "a/p",
                        "a/p"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "am/pm",
                        "am/pm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "h",
                        "h"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "hh",
                        "hh"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "s",
                        "s"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "ss",
                        "ss"
                    )
                )
            )
        );
    }

    // spreadsheetFormatterSamples......................................................................................

    // automatic........................................................................................................

    @Test
    public void testSpreadsheetFormatterSamplesWithAutomatic() {
        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.AUTOMATIC,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            SpreadsheetFormatterProviderSamplesContexts.fake()
        );
    }

    // collection........................................................................................................

    @Test
    public void testSpreadsheetFormatterSamplesWithCollection() {
        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.COLLECTION,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            SpreadsheetFormatterProviderSamplesContexts.fake()
        );
    }

    // currency.........................................................................................................

    // Currency
    //  currency
    //  Text "$123.50"
    //
    // Currency
    //  currency
    //  Text "$-123.50"
    //
    // Currency
    //  currency
    //  Text "$0.00"
    @Test
    public void testSpreadsheetFormatterSamplesWithCurrency() {
        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.CURRENCY,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "Currency",
                SpreadsheetFormatterName.CURRENCY.setValueText(""),
                TextNode.text("$123.50")
            ),
            SpreadsheetFormatterSample.with(
                "Currency",
                SpreadsheetFormatterName.CURRENCY.setValueText(""),
                TextNode.text("$-123.50")
            ),
            SpreadsheetFormatterSample.with(
                "Currency",
                SpreadsheetFormatterName.CURRENCY.setValueText(""),
                TextNode.text("$0.00")
            )
        );
    }

    // date.............................................................................................................

    // Short
    //  date
    //    "d/m/yy"
    //  Text "31/12/99"
    //
    //Medium
    //  date
    //    "d mmm yyyy"
    //  Text "31 Dec. 1999, 12:58:00 PM"
    //
    //Long
    //  date
    //    "d mmmm yyyy"
    //  Text "31 December 1999"
    //
    //Full
    //  date
    //    "dddd, d mmmm yyyy"
    //  Text "Friday, 31 December 1999"
    @Test
    public void testSpreadsheetFormatterSamplesWithDateWithoutCellSkipSamples() {
        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.DATE,
            SpreadsheetFormatterProvider.SKIP_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "Short",
                SpreadsheetFormatterName.DATE.setValueText("d/m/yy"),
                TextNode.text("31/12/99")
            ),
            SpreadsheetFormatterSample.with(
                "Medium",
                SpreadsheetFormatterName.DATE.setValueText("d mmm yyyy"),
                TextNode.text("31 Dec. 1999")
            ),
            SpreadsheetFormatterSample.with(
                "Long",
                SpreadsheetFormatterName.DATE.setValueText("d mmmm yyyy"),
                TextNode.text("31 December 1999")
            ),
            SpreadsheetFormatterSample.with(
                "Full",
                SpreadsheetFormatterName.DATE.setValueText("dddd, d mmmm yyyy"),
                TextNode.text("Friday, 31 December 1999")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithDateWithoutCellIncludeSamples() {
        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.DATE,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "Short",
                SpreadsheetFormatterName.DATE.setValueText("d/m/yy"),
                TextNode.text("31/12/99")
            ),
            SpreadsheetFormatterSample.with(
                "Medium",
                SpreadsheetFormatterName.DATE.setValueText("d mmm yyyy"),
                TextNode.text("31 Dec. 1999")
            ),
            SpreadsheetFormatterSample.with(
                "Long",
                SpreadsheetFormatterName.DATE.setValueText("d mmmm yyyy"),
                TextNode.text("31 December 1999")
            ),
            SpreadsheetFormatterSample.with(
                "Full",
                SpreadsheetFormatterName.DATE.setValueText("dddd, d mmmm yyyy"),
                TextNode.text("Friday, 31 December 1999")
            ),
            SpreadsheetFormatterSample.with(
                "Sample",
                SpreadsheetFormatterName.DATE.setValueText(""),
                SpreadsheetFormattersSpreadsheetFormatterProvider.sampleError("Empty \"text\"")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithDateWithCellValueSkipSamples() {
        final LocalDate date = LocalDate.of(
            2000,
            1,
            2
        );
        this.checkNotEquals(
            date,
            NOW.now(),
            "date must be different to now"
        );

        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.DATE.setValueText(""),
            SpreadsheetFormatterProvider.SKIP_SAMPLES,
            context(
                Optional.of(date)
            ),
            SpreadsheetFormatterSample.with(
                "Short",
                SpreadsheetFormatterName.DATE.setValueText("d/m/yy"),
                TextNode.text("2/1/00")
            ),
            SpreadsheetFormatterSample.with(
                "Medium",
                SpreadsheetFormatterName.DATE.setValueText("d mmm yyyy"),
                TextNode.text("2 Jan. 2000")
            ),
            SpreadsheetFormatterSample.with(
                "Long",
                SpreadsheetFormatterName.DATE.setValueText("d mmmm yyyy"),
                TextNode.text("2 January 2000")
            ),
            SpreadsheetFormatterSample.with(
                "Full",
                SpreadsheetFormatterName.DATE.setValueText("dddd, d mmmm yyyy"),
                TextNode.text("Sunday, 2 January 2000")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithDateWithCellValueIncludeSamples() {
        final LocalDate date = LocalDate.of(
            2000,
            1,
            2
        );
        this.checkNotEquals(
            date,
            NOW.now(),
            "date must be different to now"
        );

        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.DATE.setValueText(""),
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(date)
            ),
            SpreadsheetFormatterSample.with(
                "Short",
                SpreadsheetFormatterName.DATE.setValueText("d/m/yy"),
                TextNode.text("2/1/00")
            ),
            SpreadsheetFormatterSample.with(
                "Medium",
                SpreadsheetFormatterName.DATE.setValueText("d mmm yyyy"),
                TextNode.text("2 Jan. 2000")
            ),
            SpreadsheetFormatterSample.with(
                "Long",
                SpreadsheetFormatterName.DATE.setValueText("d mmmm yyyy"),
                TextNode.text("2 January 2000")
            ),
            SpreadsheetFormatterSample.with(
                "Full",
                SpreadsheetFormatterName.DATE.setValueText("dddd, d mmmm yyyy"),
                TextNode.text("Sunday, 2 January 2000")
            ),
            SpreadsheetFormatterSample.with(
                "Sample",
                SpreadsheetFormatterName.DATE.setValueText(""),
                SpreadsheetFormattersSpreadsheetFormatterProvider.sampleError("Empty \"text\"")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithDateNotEmptyWithCellValueDate() {
        final LocalDate date = LocalDate.of(
            2000,
            1,
            2
        );
        this.checkNotEquals(
            date,
            NOW.now(),
            "date must be different to now"
        );

        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.DATE.setValueText("\"Hello\" yyyy/mm/ddd");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(date)
            ),
            SpreadsheetFormatterSample.with(
                "Short",
                SpreadsheetFormatterName.DATE.setValueText("d/m/yy"),
                TextNode.text("2/1/00")
            ),
            SpreadsheetFormatterSample.with(
                "Medium",
                SpreadsheetFormatterName.DATE.setValueText("d mmm yyyy"),
                TextNode.text("2 Jan. 2000")
            ),
            SpreadsheetFormatterSample.with(
                "Long",
                SpreadsheetFormatterName.DATE.setValueText("d mmmm yyyy"),
                TextNode.text("2 January 2000")
            ),
            SpreadsheetFormatterSample.with(
                "Full",
                SpreadsheetFormatterName.DATE.setValueText("dddd, d mmmm yyyy"),
                TextNode.text("Sunday, 2 January 2000")
            ),
            SpreadsheetFormatterSample.with(
                "Sample",
                selector,
                TextNode.text("Hello 2000/01/Sun.")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithDateNotEmptyWithCellValueSpreadsheetErrorIgnored() {
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.DATE.setValueText("\"Hello\" yyyy/mm/ddd");

        // NOW should be used in samples
        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(
                    SpreadsheetErrorKind.VALUE.setMessage("Should not appear in formatted samples")
                )
            ),
            SpreadsheetFormatterSample.with(
                "Short",
                SpreadsheetFormatterName.DATE.setValueText("d/m/yy"),
                TextNode.text("31/12/99")
            ),
            SpreadsheetFormatterSample.with(
                "Medium",
                SpreadsheetFormatterName.DATE.setValueText("d mmm yyyy"),
                TextNode.text("31 Dec. 1999")
            ),
            SpreadsheetFormatterSample.with(
                "Long",
                SpreadsheetFormatterName.DATE.setValueText("d mmmm yyyy"),
                TextNode.text("31 December 1999")
            ),
            SpreadsheetFormatterSample.with(
                "Full",
                SpreadsheetFormatterName.DATE.setValueText("dddd, d mmmm yyyy"),
                TextNode.text("Friday, 31 December 1999")
            ),
            SpreadsheetFormatterSample.with(
                "Sample",
                selector,
                TextNode.text("Hello 1999/12/Fri.")
            )
        );
    }

    // date-time........................................................................................................

    // Short
    //  date-time
    //    "d/m/yy, h:mm AM/PM"
    //  Text "31/12/99, 12:58 PM"
    //
    //Medium
    //  date-time
    //    "d mmm yyyy, h:mm:ss AM/PM"
    //  Text "31 Dec. 1999, 12:58:00 PM"
    //
    //Long
    //  date-time
    //    "d mmmm yyyy \\a\\t h:mm:ss AM/PM"
    //  Text "31 December 1999 at 12:58:00 PM"
    //
    //Full
    //  date-time
    //    "dddd, d mmmm yyyy \\a\\t h:mm:ss AM/PM"
    //  Text "Friday, 31 December 1999 at 12:58:00 PM"
    @Test
    public void testSpreadsheetFormatterSamplesWithDateTimeSkipSamples() {
        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.DATE_TIME.setValueText(""),
            SpreadsheetFormatterProvider.SKIP_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "Short",
                SpreadsheetFormatterName.DATE_TIME.setValueText("d/m/yy, h:mm AM/PM"),
                TextNode.text("31/12/99, 12:58 PM")
            ),
            SpreadsheetFormatterSample.with(
                "Medium",
                SpreadsheetFormatterName.DATE_TIME.setValueText("d mmm yyyy, h:mm:ss AM/PM"),
                TextNode.text("31 Dec. 1999, 12:58:00 PM")
            ),
            SpreadsheetFormatterSample.with(
                "Long",
                SpreadsheetFormatterName.DATE_TIME.setValueText("d mmmm yyyy \\a\\t h:mm:ss AM/PM"),
                TextNode.text("31 December 1999 at 12:58:00 PM")
            ),
            SpreadsheetFormatterSample.with(
                "Full",
                SpreadsheetFormatterName.DATE_TIME.setValueText("dddd, d mmmm yyyy \\a\\t h:mm:ss AM/PM"),
                TextNode.text("Friday, 31 December 1999 at 12:58:00 PM")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithDateTimeIncludeSamples() {
        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.DATE_TIME.setValueText(""),
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "Short",
                SpreadsheetFormatterName.DATE_TIME.setValueText("d/m/yy, h:mm AM/PM"),
                TextNode.text("31/12/99, 12:58 PM")
            ),
            SpreadsheetFormatterSample.with(
                "Medium",
                SpreadsheetFormatterName.DATE_TIME.setValueText("d mmm yyyy, h:mm:ss AM/PM"),
                TextNode.text("31 Dec. 1999, 12:58:00 PM")
            ),
            SpreadsheetFormatterSample.with(
                "Long",
                SpreadsheetFormatterName.DATE_TIME.setValueText("d mmmm yyyy \\a\\t h:mm:ss AM/PM"),
                TextNode.text("31 December 1999 at 12:58:00 PM")
            ),
            SpreadsheetFormatterSample.with(
                "Full",
                SpreadsheetFormatterName.DATE_TIME.setValueText("dddd, d mmmm yyyy \\a\\t h:mm:ss AM/PM"),
                TextNode.text("Friday, 31 December 1999 at 12:58:00 PM")
            ),
            SpreadsheetFormatterSample.with(
                "Sample",
                SpreadsheetFormatterName.DATE_TIME.setValueText(""),
                SpreadsheetFormattersSpreadsheetFormatterProvider.sampleError("Empty \"text\"")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithDateTimeWithCellValueDateTimeSkipSamples() {
        final LocalDateTime value = LocalDateTime.of(
            2000,
            1,
            2,
            18,
            30
        );
        this.checkNotEquals(
            value,
            NOW,
            "value must be different to now"
        );

        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.DATE_TIME,
            SpreadsheetFormatterProvider.SKIP_SAMPLES,
            context(
                Optional.of(value)
            ),
            SpreadsheetFormatterSample.with(
                "Short",
                SpreadsheetFormatterName.DATE_TIME.setValueText("d/m/yy, h:mm AM/PM"),
                TextNode.text("2/1/00, 6:30 PM")
            ),
            SpreadsheetFormatterSample.with(
                "Medium",
                SpreadsheetFormatterName.DATE_TIME.setValueText("d mmm yyyy, h:mm:ss AM/PM"),
                TextNode.text("2 Jan. 2000, 6:30:00 PM")
            ),
            SpreadsheetFormatterSample.with(
                "Long",
                SpreadsheetFormatterName.DATE_TIME.setValueText("d mmmm yyyy \\a\\t h:mm:ss AM/PM"),
                TextNode.text("2 January 2000 at 6:30:00 PM")
            ),
            SpreadsheetFormatterSample.with(
                "Full",
                SpreadsheetFormatterName.DATE_TIME.setValueText("dddd, d mmmm yyyy \\a\\t h:mm:ss AM/PM"),
                TextNode.text("Sunday, 2 January 2000 at 6:30:00 PM")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithDateTimeNotEmptyWithCellValueDateTimeIncludeSamples() {
        final LocalDateTime value = LocalDateTime.of(
            2000,
            1,
            2,
            18,
            30
        );
        this.checkNotEquals(
            value,
            NOW,
            "value must be different to now"
        );

        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.DATE_TIME.setValueText("\"Hello\" yyyy/mm/dd hh:mm");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(value)
            ),
            SpreadsheetFormatterSample.with(
                "Short",
                SpreadsheetFormatterName.DATE_TIME.setValueText("d/m/yy, h:mm AM/PM"),
                TextNode.text("2/1/00, 6:30 PM")
            ),
            SpreadsheetFormatterSample.with(
                "Medium",
                SpreadsheetFormatterName.DATE_TIME.setValueText("d mmm yyyy, h:mm:ss AM/PM"),
                TextNode.text("2 Jan. 2000, 6:30:00 PM")
            ),
            SpreadsheetFormatterSample.with(
                "Long",
                SpreadsheetFormatterName.DATE_TIME.setValueText("d mmmm yyyy \\a\\t h:mm:ss AM/PM"),
                TextNode.text("2 January 2000 at 6:30:00 PM")
            ),
            SpreadsheetFormatterSample.with(
                "Full",
                SpreadsheetFormatterName.DATE_TIME.setValueText("dddd, d mmmm yyyy \\a\\t h:mm:ss AM/PM"),
                TextNode.text("Sunday, 2 January 2000 at 6:30:00 PM")
            ),
            SpreadsheetFormatterSample.with(
                "Sample",
                selector,
                TextNode.text("Hello 2000/01/02 18:30")
            )
        );
    }

    // default-text.....................................................................................................

    @Test
    public void testSpreadsheetFormatterSamplesWithDefaultText() {
        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.DEFAULT_TEXT,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "Default",
                SpreadsheetFormatterName.TEXT.setValueText("@"),
                TextNode.text("Hello 123")
            ),
            SpreadsheetFormatterSample.with(
                "Sample",
                SpreadsheetFormatterName.DEFAULT_TEXT.setValueText(""),
                TextNode.text("Hello World 123")
            )
        );
    }

    // expression.......................................................................................................

    @Test
    public void testSpreadsheetFormatterSamplesWithExpressionMissingExpressionString() {
        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.EXPRESSION,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithExpressionIncludingExpressionStringSkipSamples() {
        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.EXPRESSION.setValueText("1+2"),
            SpreadsheetFormatterProvider.SKIP_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithExpressionIncludingExpressionStringIncludeSamples() {
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.EXPRESSION.setValueText("1+2");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "Sample",
                selector,
                TextNode.text("3")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithExpressionIncludingExpressionStringIncludeSamples2() {
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.EXPRESSION.setValueText("123");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "Sample",
                selector,
                TextNode.text("123")
            )
        );
    }

    // full-date........................................................................................................

    // Full
    //  date
    //    "dddd, d mmmm yyyy"
    //  Text "Friday, 31 December 1999"
    @Test
    public void testSpreadsheetFormatterSamplesWithFullDateWithoutCellSkipSamples() {
        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.FULL_DATE,
            SpreadsheetFormatterProvider.SKIP_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "Full Date",
                SpreadsheetFormatterSelector.parse("full-date"),
                TextNode.text("Friday, 31 December 1999")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithFullDateWithoutCellIncludeSamples() {
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.FULL_DATE.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.FULL_DATE,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "Full Date",
                selector,
                TextNode.text("Friday, 31 December 1999")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithFullDateWithCellValueSkipSamples() {
        final LocalDate date = LocalDate.of(
            2000,
            1,
            2
        );
        this.checkNotEquals(
            date,
            NOW.now(),
            "date must be different to now"
        );

        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.FULL_DATE.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.SKIP_SAMPLES,
            context(
                Optional.of(date)
            ),
            SpreadsheetFormatterSample.with(
                "Full Date",
                selector,
                TextNode.text("Sunday, 2 January 2000")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithFullDateWithCellValueIncludeSamples() {
        final LocalDate date = LocalDate.of(
            2000,
            1,
            2
        );
        this.checkNotEquals(
            date,
            NOW.now(),
            "date must be different to now"
        );

        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.FULL_DATE.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(date)
            ),
            SpreadsheetFormatterSample.with(
                "Full Date",
                selector,
                TextNode.text("Sunday, 2 January 2000")
            ),
            SpreadsheetFormatterSample.with(
                "A1",
                selector,
                TextNode.text("Sunday, 2 January 2000")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithFullDateNotEmptyWithCellValueDate() {
        final LocalDate date = LocalDate.of(
            2000,
            1,
            2
        );
        this.checkNotEquals(
            date,
            NOW.now(),
            "date must be different to now"
        );

        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.FULL_DATE.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(date)
            ),
            SpreadsheetFormatterSample.with(
                "Full Date",
                selector,
                TextNode.text("Sunday, 2 January 2000")
            ),
            SpreadsheetFormatterSample.with(
                "A1",
                selector,
                TextNode.text("Sunday, 2 January 2000")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithFullDateNotEmptyWithCellValueSpreadsheetErrorIgnored() {
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.FULL_DATE.setValueText("");

        // NOW should be used in samples
        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(
                    SpreadsheetErrorKind.VALUE.setMessage("Should not appear in formatted samples")
                )
            ),
            SpreadsheetFormatterSample.with(
                "Full Date",
                selector,
                TextNode.text("Friday, 31 December 1999")
            )
        );
    }

    // full-date-time...................................................................................................

    @Test
    public void testSpreadsheetFormatterSamplesWithFullDateTimeWithoutCellSkipSamples() {
        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.FULL_DATE_TIME,
            SpreadsheetFormatterProvider.SKIP_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "Full Date Time",
                SpreadsheetFormatterSelector.parse("full-date-time"),
                TextNode.text("Friday, 31 December 1999 at 12:58:00 PM")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithFullDateTimeWithoutCellIncludeSamples() {
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.FULL_DATE_TIME.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.FULL_DATE_TIME,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "Full Date Time",
                selector,
                TextNode.text("Friday, 31 December 1999 at 12:58:00 PM")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithFullDateTimeWithCellValueSkipSamples() {
        final LocalDateTime dateTime = LocalDateTime.of(
            2000,
            1,
            2,
            12,
            58,
            59
        );
        this.checkNotEquals(
            dateTime,
            NOW.now(),
            "date time must be different to now"
        );

        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.FULL_DATE_TIME.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.SKIP_SAMPLES,
            context(
                Optional.of(dateTime)
            ),
            SpreadsheetFormatterSample.with(
                "Full Date Time",
                selector,
                TextNode.text("Sunday, 2 January 2000 at 12:58:59 PM")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithFullDateTimeWithCellValueIncludeSamples() {
        final LocalDateTime dateTime = LocalDateTime.of(
            2000,
            1,
            2,
            12,
            58,
            59
        );
        this.checkNotEquals(
            dateTime,
            NOW.now(),
            "date time must be different to now"
        );

        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.FULL_DATE_TIME.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(dateTime)
            ),
            SpreadsheetFormatterSample.with(
                "Full Date Time",
                selector,
                TextNode.text("Sunday, 2 January 2000 at 12:58:59 PM")
            ),
            SpreadsheetFormatterSample.with(
                "A1",
                selector,
                TextNode.text("Sunday, 2 January 2000 at 12:58:59 PM")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithFullDateTimeNotEmptyWithCellValueDate() {
        final LocalDateTime dateTime = LocalDateTime.of(
            2000,
            1,
            2,
            12,
            58,
            59
        );
        this.checkNotEquals(
            dateTime,
            NOW.now(),
            "date time must be different to now"
        );

        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.FULL_DATE_TIME.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(dateTime)
            ),
            SpreadsheetFormatterSample.with(
                "Full Date Time",
                selector,
                TextNode.text("Sunday, 2 January 2000 at 12:58:59 PM")
            ),
            SpreadsheetFormatterSample.with(
                "A1",
                selector,
                TextNode.text("Sunday, 2 January 2000 at 12:58:59 PM")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithFullDateTimeNotEmptyWithCellValueSpreadsheetErrorIgnored() {
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.FULL_DATE_TIME.setValueText("");

        // NOW should be used in samples
        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(
                    SpreadsheetErrorKind.VALUE.setMessage("Should not appear in formatted samples")
                )
            ),
            SpreadsheetFormatterSample.with(
                "Full Date Time",
                selector,
                TextNode.text("Friday, 31 December 1999 at 12:58:00 PM")
            )
        );
    }
    
    // general..........................................................................................................

    // General
    //  general
    //  Text "123.5"
    //
    //General
    //  general
    //  Text "-123.5"
    //
    //General
    //  general
    //  Text "0."
    @Test
    public void testSpreadsheetFormatterSamplesWithGeneral() {
        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.GENERAL,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "General",
                SpreadsheetFormatterName.GENERAL.setValueText(""),
                TextNode.text("123.5")
            ),
            SpreadsheetFormatterSample.with(
                "General",
                SpreadsheetFormatterName.GENERAL.setValueText(""),
                TextNode.text("-123.5")
            ),
            SpreadsheetFormatterSample.with(
                "General",
                SpreadsheetFormatterName.GENERAL.setValueText(""),
                TextNode.text("0")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithGeneralAndCellWithValueSkipSamples() {
        final Object value = 999;

        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.GENERAL,
            SpreadsheetFormatterProvider.SKIP_SAMPLES,
            context(
                Optional.of(value)
            ),
            SpreadsheetFormatterSample.with(
                "General",
                SpreadsheetFormatterName.GENERAL.setValueText(""),
                TextNode.text("123.5")
            ),
            SpreadsheetFormatterSample.with(
                "General",
                SpreadsheetFormatterName.GENERAL.setValueText(""),
                TextNode.text("-123.5")
            ),
            SpreadsheetFormatterSample.with(
                "General",
                SpreadsheetFormatterName.GENERAL.setValueText(""),
                TextNode.text("0")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithGeneralAndCellWithValueIncludeSamples() {
        final Object value = 999;

        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.GENERAL,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(value)
            ),
            SpreadsheetFormatterSample.with(
                "General",
                SpreadsheetFormatterName.GENERAL.setValueText(""),
                TextNode.text("123.5")
            ),
            SpreadsheetFormatterSample.with(
                "General",
                SpreadsheetFormatterName.GENERAL.setValueText(""),
                TextNode.text("-123.5")
            ),
            SpreadsheetFormatterSample.with(
                "General",
                SpreadsheetFormatterName.GENERAL.setValueText(""),
                TextNode.text("0")
            ),
            SpreadsheetFormatterSample.with(
                "A1",
                SpreadsheetFormatterName.GENERAL.setValueText(""),
                TextNode.text("999")
            )
        );
    }

    // long-date........................................................................................................

    // Long
    //  date
    //    "d mmmm yyyy"
    //  Text "31 December 1999"
    @Test
    public void testSpreadsheetFormatterSamplesWithLongDateWithoutCellSkipSamples() {
        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.LONG_DATE,
            SpreadsheetFormatterProvider.SKIP_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "Long Date",
                SpreadsheetFormatterSelector.parse("long-date"),
                TextNode.text("31 December 1999")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithLongDateWithoutCellIncludeSamples() {
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.LONG_DATE.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.LONG_DATE,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "Long Date",
                selector,
                TextNode.text("31 December 1999")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithLongDateWithCellValueSkipSamples() {
        final LocalDate date = LocalDate.of(
            2000,
            1,
            2
        );
        this.checkNotEquals(
            date,
            NOW.now(),
            "date must be different to now"
        );

        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.LONG_DATE.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.SKIP_SAMPLES,
            context(
                Optional.of(date)
            ),
            SpreadsheetFormatterSample.with(
                "Long Date",
                selector,
                TextNode.text("2 January 2000")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithLongDateWithCellValueIncludeSamples() {
        final LocalDate date = LocalDate.of(
            2000,
            1,
            2
        );
        this.checkNotEquals(
            date,
            NOW.now(),
            "date must be different to now"
        );

        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.LONG_DATE.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(date)
            ),
            SpreadsheetFormatterSample.with(
                "Long Date",
                selector,
                TextNode.text("2 January 2000")
            ),
            SpreadsheetFormatterSample.with(
                "A1",
                selector,
                TextNode.text("2 January 2000")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithLongDateNotEmptyWithCellValueDate() {
        final LocalDate date = LocalDate.of(
            2000,
            1,
            2
        );
        this.checkNotEquals(
            date,
            NOW.now(),
            "date must be different to now"
        );

        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.LONG_DATE.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(date)
            ),
            SpreadsheetFormatterSample.with(
                "Long Date",
                selector,
                TextNode.text("2 January 2000")
            ),
            SpreadsheetFormatterSample.with(
                "A1",
                selector,
                TextNode.text("2 January 2000")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithLongDateNotEmptyWithCellValueSpreadsheetErrorIgnored() {
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.LONG_DATE.setValueText("");

        // NOW should be used in samples
        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(
                    SpreadsheetErrorKind.VALUE.setMessage("Should not appear in formatted samples")
                )
            ),
            SpreadsheetFormatterSample.with(
                "Long Date",
                selector,
                TextNode.text("31 December 1999")
            )
        );
    }

    // long-date-time...................................................................................................

    @Test
    public void testSpreadsheetFormatterSamplesWithLongDateTimeWithoutCellSkipSamples() {
        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.LONG_DATE_TIME,
            SpreadsheetFormatterProvider.SKIP_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "Long Date Time",
                SpreadsheetFormatterSelector.parse("long-date-time"),
                TextNode.text("31 December 1999 at 12:58:00 PM")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithLongDateTimeWithoutCellIncludeSamples() {
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.LONG_DATE_TIME.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.LONG_DATE_TIME,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "Long Date Time",
                selector,
                TextNode.text("31 December 1999 at 12:58:00 PM")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithLongDateTimeWithCellValueSkipSamples() {
        final LocalDateTime dateTime = LocalDateTime.of(
            2000,
            1,
            2,
            12,
            58,
            59
        );
        this.checkNotEquals(
            dateTime,
            NOW.now(),
            "dateTime must be different to now"
        );

        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.LONG_DATE_TIME.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.SKIP_SAMPLES,
            context(
                Optional.of(dateTime)
            ),
            SpreadsheetFormatterSample.with(
                "Long Date Time",
                selector,
                TextNode.text("2 January 2000 at 12:58:59 PM")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithLongDateTimeWithCellValueIncludeSamples() {
        final LocalDateTime dateTime = LocalDateTime.of(
            2000,
            1,
            2,
            12,
            58,
            59
        );
        this.checkNotEquals(
            dateTime,
            NOW.now(),
            "dateTime must be different to now"
        );

        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.LONG_DATE_TIME.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(dateTime)
            ),
            SpreadsheetFormatterSample.with(
                "Long Date Time",
                selector,
                TextNode.text("2 January 2000 at 12:58:59 PM")
            ),
            SpreadsheetFormatterSample.with(
                "A1",
                selector,
                TextNode.text("2 January 2000 at 12:58:59 PM")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithLongDateTimeNotEmptyWithCellValueDateTime() {
        final LocalDateTime dateTime = LocalDateTime.of(
            2000,
            1,
            2,
            12,
            58,
            59
        );
        this.checkNotEquals(
            dateTime,
            NOW.now(),
            "dateTime must be different to now"
        );

        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.LONG_DATE_TIME.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(dateTime)
            ),
            SpreadsheetFormatterSample.with(
                "Long Date Time",
                selector,
                TextNode.text("2 January 2000 at 12:58:59 PM")
            ),
            SpreadsheetFormatterSample.with(
                "A1",
                selector,
                TextNode.text("2 January 2000 at 12:58:59 PM")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithLongDateTimeNotEmptyWithCellValueSpreadsheetErrorIgnored() {
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.LONG_DATE_TIME.setValueText("");

        // NOW should be used in samples
        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(
                    SpreadsheetErrorKind.VALUE.setMessage("Should not appear in formatted samples")
                )
            ),
            SpreadsheetFormatterSample.with(
                "Long Date Time",
                selector,
                TextNode.text("31 December 1999 at 12:58:00 PM")
            )
        );
    }
    
    // medium-date......................................................................................................

    // Medium
    //  date
    //    "d mmm yyyy"
    //  Text "31 Dec. 1999"
    @Test
    public void testSpreadsheetFormatterSamplesWithMediumDateWithoutCellSkipSamples() {
        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.MEDIUM_DATE,
            SpreadsheetFormatterProvider.SKIP_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "Medium Date",
                SpreadsheetFormatterSelector.parse("medium-date"),
                TextNode.text("31 Dec. 1999")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithMediumDateWithoutCellIncludeSamples() {
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.MEDIUM_DATE.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.MEDIUM_DATE,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "Medium Date",
                selector,
                TextNode.text("31 Dec. 1999")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithMediumDateWithCellValueSkipSamples() {
        final LocalDate date = LocalDate.of(
            2000,
            1,
            2
        );
        this.checkNotEquals(
            date,
            NOW.now(),
            "date must be different to now"
        );

        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.MEDIUM_DATE.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.SKIP_SAMPLES,
            context(
                Optional.of(date)
            ),
            SpreadsheetFormatterSample.with(
                "Medium Date",
                selector,
                TextNode.text("2 Jan. 2000")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithMediumDateWithCellValueIncludeSamples() {
        final LocalDate date = LocalDate.of(
            2000,
            1,
            2
        );
        this.checkNotEquals(
            date,
            NOW.now(),
            "date must be different to now"
        );

        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.MEDIUM_DATE.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(date)
            ),
            SpreadsheetFormatterSample.with(
                "Medium Date",
                selector,
                TextNode.text("2 Jan. 2000")
            ),
            SpreadsheetFormatterSample.with(
                "A1",
                selector,
                TextNode.text("2 Jan. 2000")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithMediumDateNotEmptyWithCellValueDate() {
        final LocalDate date = LocalDate.of(
            2000,
            1,
            2
        );
        this.checkNotEquals(
            date,
            NOW.now(),
            "date must be different to now"
        );

        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.MEDIUM_DATE.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(date)
            ),
            SpreadsheetFormatterSample.with(
                "Medium Date",
                selector,
                TextNode.text("2 Jan. 2000")
            ),
            SpreadsheetFormatterSample.with(
                "A1",
                selector,
                TextNode.text("2 Jan. 2000")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithMediumDateNotEmptyWithCellValueSpreadsheetErrorIgnored() {
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.MEDIUM_DATE.setValueText("");

        // NOW should be used in samples
        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(
                    SpreadsheetErrorKind.VALUE.setMessage("Should not appear in formatted samples")
                )
            ),
            SpreadsheetFormatterSample.with(
                "Medium Date",
                selector,
                TextNode.text("31 Dec. 1999")
            )
        );
    }

    // medium-date-time..................................................................................................
    
    @Test
    public void testSpreadsheetFormatterSamplesWithMediumDateTimeWithoutCellSkipSamples() {
        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.MEDIUM_DATE_TIME,
            SpreadsheetFormatterProvider.SKIP_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "Medium Date Time",
                SpreadsheetFormatterSelector.parse("medium-date-time"),
                TextNode.text("31 Dec. 1999, 12:58:00 PM")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithMediumDateTimeWithoutCellIncludeSamples() {
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.MEDIUM_DATE_TIME.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.MEDIUM_DATE_TIME,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "Medium Date Time",
                selector,
                TextNode.text("31 Dec. 1999, 12:58:00 PM")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithMediumDateTimeWithCellValueSkipSamples() {
        final LocalDateTime dateTime = LocalDateTime.of(
            2000,
            1,
            2,
            12,
            58,
            59
        );
        this.checkNotEquals(
            dateTime,
            NOW.now(),
            "date-time must be different to now"
        );

        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.MEDIUM_DATE_TIME.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.SKIP_SAMPLES,
            context(
                Optional.of(dateTime)
            ),
            SpreadsheetFormatterSample.with(
                "Medium Date Time",
                selector,
                TextNode.text("2 Jan. 2000, 12:58:59 PM")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithMediumDateTimeWithCellValueIncludeSamples() {
        final LocalDateTime dateTime = LocalDateTime.of(
            2000,
            1,
            2,
            12,
            58,
            59
        );
        this.checkNotEquals(
            dateTime,
            NOW.now(),
            "date-time must be different to now"
        );

        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.MEDIUM_DATE_TIME.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(dateTime)
            ),
            SpreadsheetFormatterSample.with(
                "Medium Date Time",
                selector,
                TextNode.text("2 Jan. 2000, 12:58:59 PM")
            ),
            SpreadsheetFormatterSample.with(
                "A1",
                selector,
                TextNode.text("2 Jan. 2000, 12:58:59 PM")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithMediumDateTimeNotEmptyWithCellValueDateTime() {
        final LocalDateTime dateTime = LocalDateTime.of(
            2000,
            1,
            2,
            12,
            58,
            59
        );
        this.checkNotEquals(
            dateTime,
            NOW.now(),
            "date-time must be different to now"
        );

        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.MEDIUM_DATE_TIME.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(dateTime)
            ),
            SpreadsheetFormatterSample.with(
                "Medium Date Time",
                selector,
                TextNode.text("2 Jan. 2000, 12:58:59 PM")
            ),
            SpreadsheetFormatterSample.with(
                "A1",
                selector,
                TextNode.text("2 Jan. 2000, 12:58:59 PM")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithMediumDateTimeNotEmptyWithCellValueSpreadsheetErrorIgnored() {
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.MEDIUM_DATE_TIME.setValueText("");

        // NOW should be used in samples
        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(
                    SpreadsheetErrorKind.VALUE.setMessage("Should not appear in formatted samples")
                )
            ),
            SpreadsheetFormatterSample.with(
                "Medium Date Time",
                selector,
                TextNode.text("31 Dec. 1999, 12:58:00 PM")
            )
        );
    }

    // medium-time......................................................................................................

    @Test
    public void testSpreadsheetFormatterSamplesWithMediumTimeWithoutCellSkipSamples() {
        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.MEDIUM_TIME,
            SpreadsheetFormatterProvider.SKIP_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "Medium Time",
                SpreadsheetFormatterSelector.parse("medium-time"),
                TextNode.text("12:58:00 PM")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithMediumTimeWithoutCellIncludeSamples() {
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.MEDIUM_TIME.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.MEDIUM_TIME,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "Medium Time",
                selector,
                TextNode.text("12:58:00 PM")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithMediumTimeWithCellValueSkipSamples() {
        final LocalTime time = LocalTime.of(
            12,
            58,
            59
        );
        this.checkNotEquals(
            time,
            NOW.now(),
            "time must be different to now"
        );

        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.MEDIUM_TIME.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.SKIP_SAMPLES,
            context(
                Optional.of(time)
            ),
            SpreadsheetFormatterSample.with(
                "Medium Time",
                selector,
                TextNode.text("12:58:59 PM")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithMediumTimeWithCellValueIncludeSamples() {
        final LocalTime time = LocalTime.of(
            12,
            58,
            59
        );
        this.checkNotEquals(
            time,
            NOW.now(),
            "time must be different to now"
        );

        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.MEDIUM_TIME.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(time)
            ),
            SpreadsheetFormatterSample.with(
                "Medium Time",
                selector,
                TextNode.text("12:58:59 PM")
            ),
            SpreadsheetFormatterSample.with(
                "A1",
                selector,
                TextNode.text("12:58:59 PM")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithMediumTimeNotEmptyWithCellValueTime() {
        final LocalTime time = LocalTime.of(
            12,
            58,
            59
        );
        this.checkNotEquals(
            time,
            NOW.now(),
            "time must be different to now"
        );

        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.MEDIUM_TIME.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(time)
            ),
            SpreadsheetFormatterSample.with(
                "Medium Time",
                selector,
                TextNode.text("12:58:59 PM")
            ),
            SpreadsheetFormatterSample.with(
                "A1",
                selector,
                TextNode.text("12:58:59 PM")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithMediumTimeNotEmptyWithCellValueSpreadsheetErrorIgnored() {
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.MEDIUM_TIME.setValueText("");

        // NOW should be used in samples
        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(
                    SpreadsheetErrorKind.VALUE.setMessage("Should not appear in formatted samples")
                )
            ),
            SpreadsheetFormatterSample.with(
                "Medium Time",
                selector,
                TextNode.text("12:58:00 PM")
            )
        );
    }
    
    // number...........................................................................................................

    // Number
    //  number
    //    "#,##0.###"
    //  Text "123.5"
    //
    //Number
    //  number
    //    "#,##0.###"
    //  Text "-123.5"
    //
    //Number
    //  number
    //    "#,##0.###"
    //  Text "0."
    //
    //Integer
    //  number
    //    "#,##0"
    //  Text "124"
    //
    //Integer
    //  number
    //    "#,##0"
    //  Text "-124"
    //
    //Integer
    //  number
    //    "#,##0"
    //  Text "0"
    //
    //Percent
    //  number
    //    "#,##0%"
    //  Text "12,350%"
    //
    //Percent
    //  number
    //    "#,##0%"
    //  Text "-12,350%"
    //
    //Percent
    //  number
    //    "#,##0%"
    //  Text "0%"
    //
    //Currency
    //  number
    //    "$#,##0.00"
    //  Text "$123.50"
    //
    //Currency
    //  number
    //    "$#,##0.00"
    //  Text "$-123.50"
    //
    //Currency
    //  number
    //    "$#,##0.00"
    //  Text "$0.00"
    @Test
    public void testSpreadsheetFormatterSamplesWithNumber() {
        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.NUMBER,
            SpreadsheetFormatterProvider.SKIP_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "Number",
                SpreadsheetFormatterName.NUMBER.setValueText("#,##0.###"),
                TextNode.text("123.5")
            ),
            SpreadsheetFormatterSample.with(
                "Number",
                SpreadsheetFormatterName.NUMBER.setValueText("#,##0.###"),
                TextNode.text("-123.5")
            ),
            SpreadsheetFormatterSample.with(
                "Number",
                SpreadsheetFormatterName.NUMBER.setValueText("#,##0.###"),
                TextNode.text("0.")
            ),
            SpreadsheetFormatterSample.with(
                "Integer",
                SpreadsheetFormatterName.NUMBER.setValueText("#,##0"),
                TextNode.text("124")
            ),
            SpreadsheetFormatterSample.with(
                "Integer",
                SpreadsheetFormatterName.NUMBER.setValueText("#,##0"),
                TextNode.text("-124")
            ),
            SpreadsheetFormatterSample.with(
                "Integer",
                SpreadsheetFormatterName.NUMBER.setValueText("#,##0"),
                TextNode.text("0")
            ),
            SpreadsheetFormatterSample.with(
                "Percent",
                SpreadsheetFormatterName.NUMBER.setValueText("#,##0%"),
                TextNode.text("12,350%")
            ),
            SpreadsheetFormatterSample.with(
                "Percent",
                SpreadsheetFormatterName.NUMBER.setValueText("#,##0%"),
                TextNode.text("-12,350%")
            ),
            SpreadsheetFormatterSample.with(
                "Percent",
                SpreadsheetFormatterName.NUMBER.setValueText("#,##0%"),
                TextNode.text("0%")
            ),
            SpreadsheetFormatterSample.with(
                "Currency",
                SpreadsheetFormatterName.NUMBER.setValueText("$#,##0.00"),
                TextNode.text("$123.50")
            ),
            SpreadsheetFormatterSample.with(
                "Currency",
                SpreadsheetFormatterName.NUMBER.setValueText("$#,##0.00"),
                TextNode.text("$-123.50")
            ),
            SpreadsheetFormatterSample.with(
                "Currency",
                SpreadsheetFormatterName.NUMBER.setValueText("$#,##0.00"),
                TextNode.text("$0.00")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithNumberNotEmptySkipSamples() {
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.NUMBER.setValueText("\"Hello\" $000.000");

        final Number value = 1234.56;

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.SKIP_SAMPLES,
            context(
                Optional.of(value)
            ),
            SpreadsheetFormatterSample.with(
                "Number",
                SpreadsheetFormatterName.NUMBER.setValueText("#,##0.###"),
                TextNode.text("123.5")
            ),
            SpreadsheetFormatterSample.with(
                "Number",
                SpreadsheetFormatterName.NUMBER.setValueText("#,##0.###"),
                TextNode.text("-123.5")
            ),
            SpreadsheetFormatterSample.with(
                "Number",
                SpreadsheetFormatterName.NUMBER.setValueText("#,##0.###"),
                TextNode.text("0.")
            ),
            SpreadsheetFormatterSample.with(
                "Integer",
                SpreadsheetFormatterName.NUMBER.setValueText("#,##0"),
                TextNode.text("124")
            ),
            SpreadsheetFormatterSample.with(
                "Integer",
                SpreadsheetFormatterName.NUMBER.setValueText("#,##0"),
                TextNode.text("-124")
            ),
            SpreadsheetFormatterSample.with(
                "Integer",
                SpreadsheetFormatterName.NUMBER.setValueText("#,##0"),
                TextNode.text("0")
            ),
            SpreadsheetFormatterSample.with(
                "Percent",
                SpreadsheetFormatterName.NUMBER.setValueText("#,##0%"),
                TextNode.text("12,350%")
            ),
            SpreadsheetFormatterSample.with(
                "Percent",
                SpreadsheetFormatterName.NUMBER.setValueText("#,##0%"),
                TextNode.text("-12,350%")
            ),
            SpreadsheetFormatterSample.with(
                "Percent",
                SpreadsheetFormatterName.NUMBER.setValueText("#,##0%"),
                TextNode.text("0%")
            ),
            SpreadsheetFormatterSample.with(
                "Currency",
                SpreadsheetFormatterName.NUMBER.setValueText("$#,##0.00"),
                TextNode.text("$123.50")
            ),
            SpreadsheetFormatterSample.with(
                "Currency",
                SpreadsheetFormatterName.NUMBER.setValueText("$#,##0.00"),
                TextNode.text("$-123.50")
            ),
            SpreadsheetFormatterSample.with(
                "Currency",
                SpreadsheetFormatterName.NUMBER.setValueText("$#,##0.00"),
                TextNode.text("$0.00")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithNumberNotEmptyIncludeSamples() {
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.NUMBER.setValueText("\"Hello\" $000.000");

        final Number value = 1234.56;

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(value)
            ),
            SpreadsheetFormatterSample.with(
                "Number",
                SpreadsheetFormatterName.NUMBER.setValueText("#,##0.###"),
                TextNode.text("123.5")
            ),
            SpreadsheetFormatterSample.with(
                "Number",
                SpreadsheetFormatterName.NUMBER.setValueText("#,##0.###"),
                TextNode.text("-123.5")
            ),
            SpreadsheetFormatterSample.with(
                "Number",
                SpreadsheetFormatterName.NUMBER.setValueText("#,##0.###"),
                TextNode.text("0.")
            ),
            SpreadsheetFormatterSample.with(
                "Integer",
                SpreadsheetFormatterName.NUMBER.setValueText("#,##0"),
                TextNode.text("124")
            ),
            SpreadsheetFormatterSample.with(
                "Integer",
                SpreadsheetFormatterName.NUMBER.setValueText("#,##0"),
                TextNode.text("-124")
            ),
            SpreadsheetFormatterSample.with(
                "Integer",
                SpreadsheetFormatterName.NUMBER.setValueText("#,##0"),
                TextNode.text("0")
            ),
            SpreadsheetFormatterSample.with(
                "Percent",
                SpreadsheetFormatterName.NUMBER.setValueText("#,##0%"),
                TextNode.text("12,350%")
            ),
            SpreadsheetFormatterSample.with(
                "Percent",
                SpreadsheetFormatterName.NUMBER.setValueText("#,##0%"),
                TextNode.text("-12,350%")
            ),
            SpreadsheetFormatterSample.with(
                "Percent",
                SpreadsheetFormatterName.NUMBER.setValueText("#,##0%"),
                TextNode.text("0%")
            ),
            SpreadsheetFormatterSample.with(
                "Currency",
                SpreadsheetFormatterName.NUMBER.setValueText("$#,##0.00"),
                TextNode.text("$123.50")
            ),
            SpreadsheetFormatterSample.with(
                "Currency",
                SpreadsheetFormatterName.NUMBER.setValueText("$#,##0.00"),
                TextNode.text("$-123.50")
            ),
            SpreadsheetFormatterSample.with(
                "Currency",
                SpreadsheetFormatterName.NUMBER.setValueText("$#,##0.00"),
                TextNode.text("$0.00")
            ),
            SpreadsheetFormatterSample.with(
                "Sample",
                selector,
                TextNode.text("Hello $1234.560")
            )
        );
    }

    // percent..........................................................................................................

    @Test
    public void testSpreadsheetFormatterSamplesWithPercent() {
        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.PERCENT,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "Percent",
                SpreadsheetFormatterName.PERCENT.setValueText(""),
                TextNode.text("12350.00%")
            ),
            SpreadsheetFormatterSample.with(
                "Percent",
                SpreadsheetFormatterName.PERCENT.setValueText(""),
                TextNode.text("-12350.00%")
            ),
            SpreadsheetFormatterSample.with(
                "Percent",
                SpreadsheetFormatterName.PERCENT.setValueText(""),
                TextNode.text("0.00%")
            )
        );
    }

    // scientific.......................................................................................................
    
    @Test
    public void testSpreadsheetFormatterSamplesWithScientific() {
        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.SCIENTIFIC,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "Scientific",
                SpreadsheetFormatterName.SCIENTIFIC.setValueText(""),
                TextNode.text("1.24e02")
            ),
            SpreadsheetFormatterSample.with(
                "Scientific",
                SpreadsheetFormatterName.SCIENTIFIC.setValueText(""),
                TextNode.text("-1.24e02")
            ),
            SpreadsheetFormatterSample.with(
                "Scientific",
                SpreadsheetFormatterName.SCIENTIFIC.setValueText(""),
                TextNode.text("0.00e00")
            )
        );
    }

    // short-date.......................................................................................................

    // Short
    //  date
    //    "d/m/yy"
    //  Text "31/12/99"
    //
    @Test
    public void testSpreadsheetFormatterSamplesWithShortDateWithoutCellSkipSamples() {
        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.SHORT_DATE,
            SpreadsheetFormatterProvider.SKIP_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "Short Date",
                SpreadsheetFormatterSelector.parse("short-date"),
                TextNode.text("31/12/99")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithShortDateWithoutCellIncludeSamples() {
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.SHORT_DATE.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.SHORT_DATE,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "Short Date",
                selector,
                TextNode.text("31/12/99")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithShortDateWithCellValueSkipSamples() {
        final LocalDate date = LocalDate.of(
            2000,
            1,
            2
        );
        this.checkNotEquals(
            date,
            NOW.now(),
            "date must be different to now"
        );

        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.SHORT_DATE.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.SKIP_SAMPLES,
            context(
                Optional.of(date)
            ),
            SpreadsheetFormatterSample.with(
                "Short Date",
                selector,
                TextNode.text("2/1/00")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithShortDateWithCellValueIncludeSamples() {
        final LocalDate date = LocalDate.of(
            2000,
            1,
            2
        );
        this.checkNotEquals(
            date,
            NOW.now(),
            "date must be different to now"
        );

        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.SHORT_DATE.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(date)
            ),
            SpreadsheetFormatterSample.with(
                "Short Date",
                selector,
                TextNode.text("2/1/00")
            ),
            SpreadsheetFormatterSample.with(
                "A1",
                selector,
                TextNode.text("2/1/00")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithShortDateNotEmptyWithCellValueDate() {
        final LocalDate date = LocalDate.of(
            2000,
            1,
            2
        );
        this.checkNotEquals(
            date,
            NOW.now(),
            "date must be different to now"
        );

        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.SHORT_DATE.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(date)
            ),
            SpreadsheetFormatterSample.with(
                "Short Date",
                selector,
                TextNode.text("2/1/00")
            ),
            SpreadsheetFormatterSample.with(
                "A1",
                selector,
                TextNode.text("2/1/00")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithShortDateNotEmptyWithCellValueSpreadsheetErrorIgnored() {
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.SHORT_DATE.setValueText("");

        // NOW should be used in samples
        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(
                    SpreadsheetErrorKind.VALUE.setMessage("Should not appear in formatted samples")
                )
            ),
            SpreadsheetFormatterSample.with(
                "Short Date",
                selector,
                TextNode.text("31/12/99")
            )
        );
    }

    // short-date-time..................................................................................................
    
    @Test
    public void testSpreadsheetFormatterSamplesWithShortDateTimeWithoutCellSkipSamples() {
        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.SHORT_DATE_TIME,
            SpreadsheetFormatterProvider.SKIP_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "Short Date Time",
                SpreadsheetFormatterSelector.parse("short-date-time"),
                TextNode.text("31/12/99, 12:58 PM")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithShortDateTimeWithoutCellIncludeSamples() {
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.SHORT_DATE_TIME.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.SHORT_DATE_TIME,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "Short Date Time",
                selector,
                TextNode.text("31/12/99, 12:58 PM")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithShortDateTimeWithCellValueSkipSamples() {
        final LocalDate date = LocalDate.of(
            2000,
            1,
            2
        );
        this.checkNotEquals(
            date,
            NOW.now(),
            "date must be different to now"
        );

        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.SHORT_DATE_TIME.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.SKIP_SAMPLES,
            context(
                Optional.of(date)
            ),
            SpreadsheetFormatterSample.with(
                "Short Date Time",
                selector,
                TextNode.text("2/1/00, 12:00 AM")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithShortDateTimeWithCellValueIncludeSamples() {
        final LocalDate date = LocalDate.of(
            2000,
            1,
            2
        );
        this.checkNotEquals(
            date,
            NOW.now(),
            "date must be different to now"
        );

        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.SHORT_DATE_TIME.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(date)
            ),
            SpreadsheetFormatterSample.with(
                "Short Date Time",
                selector,
                TextNode.text("2/1/00, 12:00 AM")
            ),
            SpreadsheetFormatterSample.with(
                "A1",
                selector,
                TextNode.text("2/1/00, 12:00 AM")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithShortDateTimeNotEmptyWithCellValueDate() {
        final LocalDate date = LocalDate.of(
            2000,
            1,
            2
        );
        this.checkNotEquals(
            date,
            NOW.now(),
            "date must be different to now"
        );

        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.SHORT_DATE_TIME.setValueText("");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(date)
            ),
            SpreadsheetFormatterSample.with(
                "Short Date Time",
                selector,
                TextNode.text("2/1/00, 12:00 AM")
            ),
            SpreadsheetFormatterSample.with(
                "A1",
                selector,
                TextNode.text("2/1/00, 12:00 AM")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithShortDateTimeNotEmptyWithCellValueSpreadsheetErrorIgnored() {
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.SHORT_DATE_TIME.setValueText("");

        // NOW should be used in samples
        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(
                    SpreadsheetErrorKind.VALUE.setMessage("Should not appear in formatted samples")
                )
            ),
            SpreadsheetFormatterSample.with(
                "Short Date Time",
                selector,
                TextNode.text("31/12/99, 12:58 PM")
            )
        );
    }

    // text.............................................................................................................

    @Test
    public void testSpreadsheetFormatterSamplesWithText() {
        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.TEXT,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "Default",
                SpreadsheetFormatterName.TEXT.setValueText("@"),
                TextNode.text("Hello 123")
            ),
            SpreadsheetFormatterSample.with(
                "Sample",
                SpreadsheetFormatterName.TEXT.setValueText(""),
                SpreadsheetFormattersSpreadsheetFormatterProvider.sampleError("Empty \"text\"")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithTextWithCellValueSkipSamples() {
        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.TEXT,
            SpreadsheetFormatterProvider.SKIP_SAMPLES,
            context(
                Optional.of("Cell Value 123")
            ),
            SpreadsheetFormatterSample.with(
                "Default",
                SpreadsheetFormatterName.TEXT.setValueText("@"),
                TextNode.text("Hello 123")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithTextWithCellValueIncludeSamples() {
        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.TEXT,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of("Cell Value 123")
            ),
            SpreadsheetFormatterSample.with(
                "Default",
                SpreadsheetFormatterName.TEXT.setValueText("@"),
                TextNode.text("Hello 123")
            ),
            SpreadsheetFormatterSample.with(
                "A1",
                SpreadsheetFormatterName.TEXT.setValueText("@"),
                TextNode.text("Cell Value 123")
            ),
            SpreadsheetFormatterSample.with(
                "Sample",
                SpreadsheetFormatterName.TEXT.setValueText(""),
                SpreadsheetFormattersSpreadsheetFormatterProvider.sampleError("Empty \"text\"")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithTextNotEmptyWithCellValueSkipSamples() {
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.TEXT.setValueText("\"Hello\" @@");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.SKIP_SAMPLES,
            context(
                Optional.of("Cell Value 123")
            ),
            SpreadsheetFormatterSample.with(
                "Default",
                SpreadsheetFormatterName.TEXT.setValueText("@"),
                TextNode.text("Hello 123")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithTextNotEmptyWithCellValueIncludeSamples() {
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.TEXT.setValueText("\"Hello\" @@");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of("Cell Value 123")
            ),
            SpreadsheetFormatterSample.with(
                "Default",
                SpreadsheetFormatterName.TEXT.setValueText("@"),
                TextNode.text("Hello 123")
            ),
            SpreadsheetFormatterSample.with(
                "A1",
                SpreadsheetFormatterName.TEXT.setValueText("@"),
                TextNode.text("Cell Value 123")
            ),
            SpreadsheetFormatterSample.with(
                "Sample",
                selector,
                TextNode.text("Hello Cell Value 123Cell Value 123")
            )
        );
    }

    // time.............................................................................................................

    // Short
    //  time
    //    "h:mm AM/PM"
    //  Text "12:58 PM"
    //
    //Long
    //  time
    //    "h:mm:ss AM/PM"
    //  Text "12:58:00 PM"
    @Test
    public void testSpreadsheetFormatterSamplesWithTime() {
        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.TIME,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SpreadsheetFormatterSample.with(
                "Short",
                SpreadsheetFormatterName.TIME.setValueText("h:mm AM/PM"),
                TextNode.text("12:58 PM")
            ),
            SpreadsheetFormatterSample.with(
                "Long",
                SpreadsheetFormatterName.TIME.setValueText("h:mm:ss AM/PM"),
                TextNode.text("12:58:00 PM")
            ),
            SpreadsheetFormatterSample.with(
                "Sample",
                SpreadsheetFormatterName.TIME.setValueText(""),
                SpreadsheetFormattersSpreadsheetFormatterProvider.sampleError("Empty \"text\"")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithTimeWithCellValueIncludeSamples() {
        final LocalTime value = LocalTime.of(
            15,
            28,
            29
        );
        this.checkNotEquals(
            value,
            NOW.now().toLocalTime(),
            "value must be different to now"
        );

        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.TIME,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(value)
            ),
            SpreadsheetFormatterSample.with(
                "Short",
                SpreadsheetFormatterName.TIME.setValueText("h:mm AM/PM"),
                TextNode.text("3:28 PM")
            ),
            SpreadsheetFormatterSample.with(
                "Long",
                SpreadsheetFormatterName.TIME.setValueText("h:mm:ss AM/PM"),
                TextNode.text("3:28:29 PM")
            ),
            SpreadsheetFormatterSample.with(
                "Sample",
                SpreadsheetFormatterName.TIME.setValueText(""),
                SpreadsheetFormattersSpreadsheetFormatterProvider.sampleError("Empty \"text\"")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithTimeNotEmptyWithCellValueSkipSamples() {
        final LocalTime value = LocalTime.of(
            15,
            28,
            29
        );
        this.checkNotEquals(
            value,
            NOW.now().toLocalTime(),
            "value must be different to now"
        );

        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.TIME.setValueText("\"Hello\" hh:mm");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.SKIP_SAMPLES,
            context(
                Optional.of(value)
            ),
            SpreadsheetFormatterSample.with(
                "Short",
                SpreadsheetFormatterName.TIME.setValueText("h:mm AM/PM"),
                TextNode.text("3:28 PM")
            ),
            SpreadsheetFormatterSample.with(
                "Long",
                SpreadsheetFormatterName.TIME.setValueText("h:mm:ss AM/PM"),
                TextNode.text("3:28:29 PM")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithTimeNotEmptyWithCellValueIncludeSamples() {
        final LocalTime value = LocalTime.of(
            15,
            28,
            29
        );
        this.checkNotEquals(
            value,
            NOW.now().toLocalTime(),
            "value must be different to now"
        );

        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.TIME.setValueText("\"Hello\" hh:mm");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            context(
                Optional.of(value)
            ),
            SpreadsheetFormatterSample.with(
                "Short",
                SpreadsheetFormatterName.TIME.setValueText("h:mm AM/PM"),
                TextNode.text("3:28 PM")
            ),
            SpreadsheetFormatterSample.with(
                "Long",
                SpreadsheetFormatterName.TIME.setValueText("h:mm:ss AM/PM"),
                TextNode.text("3:28:29 PM")
            ),
            SpreadsheetFormatterSample.with(
                "Sample",
                selector,
                TextNode.text("Hello 15:28")
            )
        );
    }

    @Override
    public SpreadsheetFormattersSpreadsheetFormatterProvider createSpreadsheetFormatterProvider() {
        return SpreadsheetFormattersSpreadsheetFormatterProvider.INSTANCE;
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createSpreadsheetFormatterProvider(),
            "SpreadsheetFormattersSpreadsheetFormatterProvider"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintable() {
        this.treePrintAndCheck(
            this.createSpreadsheetFormatterProvider()
                .spreadsheetFormatterInfos(),
            "SpreadsheetFormatterInfoSet\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/automatic automatic\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/badge-error badge-error\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/collection collection\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/currency currency\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/date date\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/date-time date-time\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/default-text default-text\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/expression expression\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/full-date full-date\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/full-date-time full-date-time\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/general general\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/long-date long-date\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/long-date-time long-date-time\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/medium-date medium-date\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/medium-date-time medium-date-time\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/medium-time medium-time\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/number number\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/percent percent\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/scientific scientific\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/short-date short-date\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/short-date-time short-date-time\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/short-time short-time\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/spreadsheet-pattern-collection spreadsheet-pattern-collection\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/text text\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/time time\n"
        );
    }

    // json.............................................................................................................

    @Test
    public void testMarshall() {
        this.checkEquals(
            JsonNode.parse(
                    "[\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/automatic automatic\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/badge-error badge-error\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/collection collection\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/currency currency\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/date date\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/date-time date-time\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/default-text default-text\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/expression expression\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/full-date full-date\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/full-date-time full-date-time\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/general general\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/long-date long-date\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/long-date-time long-date-time\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/medium-date medium-date\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/medium-date-time medium-date-time\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/medium-time medium-time\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/number number\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/percent percent\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/scientific scientific\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/short-date short-date\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/short-date-time short-date-time\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/short-time short-time\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/spreadsheet-pattern-collection spreadsheet-pattern-collection\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/text text\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/time time\"\n" +
                    "]"
            ),
            JsonNodeMarshallContexts.basic()
                .marshall(
                    this.createSpreadsheetFormatterProvider()
                        .spreadsheetFormatterInfos()
                )
        );
    }

    private static SpreadsheetFormatterProviderSamplesContext context(final Optional<Object> value) {
        return METADATA_EN_AU.spreadsheetFormatterProviderSamplesContext(
            Optional.of(
                SpreadsheetSelection.A1.setFormula(
                    SpreadsheetFormula.EMPTY.setValue(value)
                )
            ),
            FORMATTER_CONTEXT_SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT_BI_FUNCTION,
            SPREADSHEET_LABEL_NAME_RESOLVER,
            LOCALE_CONTEXT,
            SPREADSHEET_PROVIDER,
            PROVIDER_CONTEXT
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetFormattersSpreadsheetFormatterProvider> type() {
        return SpreadsheetFormattersSpreadsheetFormatterProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
