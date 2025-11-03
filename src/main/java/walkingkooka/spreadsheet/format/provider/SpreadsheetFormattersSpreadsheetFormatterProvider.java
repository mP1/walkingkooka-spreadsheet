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

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.color.WebColorName;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.UrlPath;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenKind;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternKind;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStylePropertyName;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A {link SpreadsheetFormatterProvider} that supports creating {@link SpreadsheetFormatter} for each of the
 * available {@link walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern}.
 */
final class SpreadsheetFormattersSpreadsheetFormatterProvider implements SpreadsheetFormatterProvider {

    /**
     * Singleton
     */
    final static SpreadsheetFormattersSpreadsheetFormatterProvider INSTANCE = new SpreadsheetFormattersSpreadsheetFormatterProvider();

    final static String SAMPLE_LABEL = "Sample";

    private SpreadsheetFormattersSpreadsheetFormatterProvider() {
        super();
    }

    @Override
    public SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterSelector selector,
                                                     final ProviderContext context) {
        Objects.requireNonNull(selector, "selector");
        Objects.requireNonNull(context, "context");

        final SpreadsheetFormatter formatter;

        final SpreadsheetFormatterName name = selector.name();
        switch (name.value()) {
            case SpreadsheetFormatterName.AUTOMATIC_STRING:
            case SpreadsheetFormatterName.COLLECTION_STRING:
            case SpreadsheetFormatterName.CURRENCY_STRING:
            case SpreadsheetFormatterName.DEFAULT_TEXT_STRING:
            case SpreadsheetFormatterName.FULL_DATE_STRING:
            case SpreadsheetFormatterName.FULL_DATE_TIME_STRING:
            case SpreadsheetFormatterName.FULL_TIME_STRING:
            case SpreadsheetFormatterName.GENERAL_STRING:
            case SpreadsheetFormatterName.LONG_DATE_STRING:
            case SpreadsheetFormatterName.LONG_DATE_TIME_STRING:
            case SpreadsheetFormatterName.LONG_TIME_STRING:
            case SpreadsheetFormatterName.MEDIUM_DATE_STRING:
            case SpreadsheetFormatterName.MEDIUM_DATE_TIME_STRING:
            case SpreadsheetFormatterName.MEDIUM_TIME_STRING:
            case SpreadsheetFormatterName.PERCENT_STRING:
            case SpreadsheetFormatterName.SCIENTIFIC_STRING:
            case SpreadsheetFormatterName.SHORT_DATE_STRING:
            case SpreadsheetFormatterName.SHORT_DATE_TIME_STRING:
            case SpreadsheetFormatterName.SHORT_TIME_STRING:
                formatter = selector.evaluateValueText(
                    this,
                    context
                );
                break;
            case SpreadsheetFormatterName.BADGE_ERROR_STRING:
                final String badgeErrorText = selector.valueText();

                formatter = SpreadsheetFormatters.badgeError(
                    spreadsheetFormatter(
                        SpreadsheetFormatterSelector.parse(
                            badgeErrorText.startsWith("(") && badgeErrorText.endsWith(")") ?
                                badgeErrorText.substring(
                                    1,
                                    badgeErrorText.length() - 1
                                ) :
                                badgeErrorText
                        ),
                        context
                    )
                );
                break;
            case SpreadsheetFormatterName.EXPRESSION_STRING:
                formatter = expressionFormatter(
                    selector.valueText(),
                    context
                );
                break;
            default:
                formatter = selector.spreadsheetFormatPattern()
                    .map(SpreadsheetPattern::formatter)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown formatter " + name));
                break;
        }

        return formatter;
    }

    @Override
    public SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterName name,
                                                     final List<?> values,
                                                     final ProviderContext context) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(values, "values");
        Objects.requireNonNull(context, "context");

        final List<?> copy = Lists.immutable(values);
        final int count = copy.size();

        final SpreadsheetFormatter formatter;

        switch (name.value()) {
            case SpreadsheetFormatterName.AUTOMATIC_STRING:
                switch (count) {
                    case 0:
                        formatter = SpreadsheetFormatters.automatic(
                            this.spreadsheetFormatter(
                                DATE_FORMATTER,
                                context
                            ),
                            this.spreadsheetFormatter(
                                DATE_TIME_FORMATTER,
                                context
                            ),
                            this.spreadsheetFormatter(
                                ERROR_FORMATTER,
                                context
                            ),
                            this.spreadsheetFormatter(
                                NUMBER_FORMATTER,
                                context
                            ),
                            this.spreadsheetFormatter(
                                TEXT_FORMATTER,
                                context
                            ),
                            this.spreadsheetFormatter(
                                TIME_FORMATTER,
                                context
                            )
                        );
                        break;
                    case 6:
                        formatter = SpreadsheetFormatters.automatic(
                            (SpreadsheetFormatter) copy.get(0), // date
                            (SpreadsheetFormatter) copy.get(1), // date-time
                            (SpreadsheetFormatter) copy.get(2), // error
                            (SpreadsheetFormatter) copy.get(3), // number
                            (SpreadsheetFormatter) copy.get(4), // text
                            (SpreadsheetFormatter) copy.get(5) // time
                        );
                        break;
                    default:
                        throw new IllegalArgumentException("Expected 0 or 6 value(s) got " + count);
                }
                break;
            case SpreadsheetFormatterName.BADGE_ERROR_STRING:
                parameterCountCheck(1, count);

                formatter = SpreadsheetFormatters.badgeError(
                    (SpreadsheetFormatter) copy.get(0)
                );
                break;
            case SpreadsheetFormatterName.COLLECTION_STRING:
                formatter = SpreadsheetFormatters.collection(
                    values.stream()
                        .map(c -> (SpreadsheetFormatter) c)
                        .collect(Collectors.toList())
                );
                break;
            case SpreadsheetFormatterName.CURRENCY_STRING:
                formatter = currency(
                    values,
                    context
                );
                break;
            case SpreadsheetFormatterName.DEFAULT_TEXT_STRING:
                parameterCountCheck(count);

                formatter = SpreadsheetFormatters.defaultText();
                break;
            case SpreadsheetFormatterName.EXPRESSION_STRING:
                parameterCountCheck(1, count);

                formatter = expressionFormatter(
                    copy.get(0),
                    context
                );
                break;
            case SpreadsheetFormatterName.FULL_DATE_STRING:
                parameterCountCheck(count);

                formatter = fullDate(
                    context
                );
                break;
            case SpreadsheetFormatterName.FULL_DATE_TIME_STRING:
                parameterCountCheck(count);

                formatter = fullDateTime(
                    context
                );
                break;
            case SpreadsheetFormatterName.FULL_TIME_STRING:
                parameterCountCheck(count);

                formatter = fullTime(
                    context
                );
                break;
            case SpreadsheetFormatterName.GENERAL_STRING:
                parameterCountCheck(count);

                formatter = SpreadsheetFormatters.general();
                break;
            case SpreadsheetFormatterName.LONG_DATE_STRING:
                parameterCountCheck(count);

                formatter = longDate(
                    context
                );
                break;
            case SpreadsheetFormatterName.LONG_DATE_TIME_STRING:
                parameterCountCheck(count);

                formatter = longDateTime(
                    context
                );
                break;
            case SpreadsheetFormatterName.LONG_TIME_STRING:
                parameterCountCheck(count);

                formatter = longTime(context);
                break;
            case SpreadsheetFormatterName.MEDIUM_DATE_STRING:
                parameterCountCheck(count);

                formatter = mediumDate(
                    context
                );
                break;
            case SpreadsheetFormatterName.MEDIUM_DATE_TIME_STRING:
                parameterCountCheck(count);

                formatter = mediumDateTime(
                    context
                );
                break;
            case SpreadsheetFormatterName.MEDIUM_TIME_STRING:
                parameterCountCheck(count);

                formatter = mediumTime(
                    context
                );
                break;
            case SpreadsheetFormatterName.PERCENT_STRING:
                formatter = percent(
                    values,
                    context
                );
                break;
            case SpreadsheetFormatterName.SCIENTIFIC_STRING:
                formatter = scientific(
                    values,
                    context
                );
                break;
            case SpreadsheetFormatterName.SHORT_DATE_STRING:
                parameterCountCheck(count);

                formatter = shortDate(
                    context
                );
                break;
            case SpreadsheetFormatterName.SHORT_DATE_TIME_STRING:
                parameterCountCheck(count);

                formatter = shortDateTime(context);
                break;
            case SpreadsheetFormatterName.SHORT_TIME_STRING:
                parameterCountCheck(count);

                formatter = shortTime(context);
                break;
            default:
                final SpreadsheetPatternKind kind = name.patternKind;
                if (null == kind) {
                    throw new IllegalArgumentException("Unknown formatter " + name);
                }
                parameterCountCheck(
                    count,
                    1
                );
                formatter = kind.parse(
                    (String) copy.get(0)
                ).formatter();
        }

        return formatter;
    }

    private static void parameterCountCheck(final int count) {
        parameterCountCheck(
            count,
            0
        );
    }

    private static void parameterCountCheck(final int count,
                                            final int expected) {
        if (expected != count) {
            throw new IllegalArgumentException("Expected " + expected + " value(s) got " + count);
        }
    }

    private static SpreadsheetFormatter currency(final List<?> values,
                                                 final ProviderContext context) {
        final int decimalPlaces = values.size() == 0 ?
            2 :
            context.convertOrFail(
                values.get(0),
                Integer.class
            );

        final String pattern = "$0." +
            decimalPlaces(decimalPlaces);

        return SpreadsheetPattern.parseNumberFormatPattern(
            pattern +
                ";" +
                "[RED]" + pattern
        ).formatter();
    }

    private static SpreadsheetFormatter date(final int dateFormat,
                                             final ProviderContext context) {
        return SpreadsheetPattern.dateParsePattern(
            (SimpleDateFormat) DateFormat.getDateInstance(
                dateFormat,
                context.locale()
            )
        ).formatter();
    }

    private static SpreadsheetFormatter dateTime(final int dateFormat,
                                                 final ProviderContext context) {
        return SpreadsheetPattern.dateTimeParsePattern(
            (SimpleDateFormat) DateFormat.getDateTimeInstance(
                dateFormat,
                dateFormat,
                context.locale()
            )
        ).formatter();
    }

    private static SpreadsheetFormatter fullDate(final ProviderContext context) {
        return date(
            DateFormat.FULL,
            context
        );
    }

    private static SpreadsheetFormatter fullDateTime(final ProviderContext context) {
        return dateTime(
            DateFormat.FULL,
            context
        );
    }

    private static SpreadsheetFormatter fullTime(final ProviderContext context) {
        return time(
            DateFormat.FULL,
            context
        );
    }
    
    private static SpreadsheetFormatter longDate(final ProviderContext context) {
        return date(
            DateFormat.LONG,
            context
        );
    }

    private static SpreadsheetFormatter longDateTime(final ProviderContext context) {
        return dateTime(
            DateFormat.LONG,
            context
        );
    }

    private static SpreadsheetFormatter longTime(final ProviderContext context) {
        return time(
            DateFormat.LONG,
            context
        );
    }

    private static SpreadsheetFormatter mediumDate(final ProviderContext context) {
        return date(
            DateFormat.MEDIUM,
            context
        );
    }

    private static SpreadsheetFormatter mediumDateTime(final ProviderContext context) {
        return dateTime(
            DateFormat.MEDIUM,
            context
        );
    }

    private static SpreadsheetFormatter mediumTime(final ProviderContext context) {
        return time(
            DateFormat.MEDIUM,
            context
        );
    }

    private static SpreadsheetFormatter percent(final List<?> values,
                                                final ProviderContext context) {
        final int decimalPlaces = values.size() == 0 ?
            2 :
            context.convertOrFail(
                values.get(0),
                Integer.class
            );

        return SpreadsheetPattern.parseNumberFormatPattern(
            "0." +
                decimalPlaces(decimalPlaces) +
                "%"
        ).formatter();
    }

    private static SpreadsheetFormatter scientific(final List<?> values,
                                                   final ProviderContext context) {
        final int decimalPlaces = values.size() == 0 ?
            2 :
            context.convertOrFail(
                values.get(0),
                Integer.class
            );

        // "0.00E+00"
        return SpreadsheetPattern.parseNumberFormatPattern(
            "0." +
                decimalPlaces(decimalPlaces) +
                "E+00"
        ).formatter();
    }

    private static SpreadsheetFormatter shortDate(final ProviderContext context) {
        return date(
            DateFormat.SHORT,
            context
        );
    }

    private static SpreadsheetFormatter shortDateTime(final ProviderContext context) {
        return dateTime(
            DateFormat.SHORT,
            context
        );
    }

    private static SpreadsheetFormatter shortTime(final ProviderContext context) {
        return time(
            DateFormat.SHORT,
            context
        );
    }

    private static SpreadsheetFormatter time(final int dateFormat,
                                             final ProviderContext context) {
        return SpreadsheetPattern.timeParsePattern(
            (SimpleDateFormat) DateFormat.getTimeInstance(
                dateFormat,
                context.locale()
            )
        ).formatter();
    }

    private static CharSequence decimalPlaces(final int decimalPlaces) {
        return CharSequences.repeating(
            '0',
            decimalPlaces
        );
    }

    private SpreadsheetFormatter spreadsheetFormatter(final EnvironmentValueName<SpreadsheetFormatterSelector> value,
                                                      final ProviderContext context) {
        return this.spreadsheetFormatter(
            context.environmentValueOrFail(value),
            context
        );
    }

    private static SpreadsheetFormatter expressionFormatter(final Object expression,
                                                            final ProviderContext context) {
        return SpreadsheetFormatters.expression(
            context.convertOrFail(
                expression,
                Expression.class
            )
        );
    }

    private final static EnvironmentValueName<SpreadsheetFormatterSelector> DATE_FORMATTER = environmentValueName(SpreadsheetMetadataPropertyName.DATE_FORMATTER);

    private final static EnvironmentValueName<SpreadsheetFormatterSelector> DATE_TIME_FORMATTER = environmentValueName(SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER);

    private final static EnvironmentValueName<SpreadsheetFormatterSelector> ERROR_FORMATTER = environmentValueName(SpreadsheetMetadataPropertyName.ERROR_FORMATTER);

    private final static EnvironmentValueName<SpreadsheetFormatterSelector> NUMBER_FORMATTER = environmentValueName(SpreadsheetMetadataPropertyName.NUMBER_FORMATTER);

    private final static EnvironmentValueName<SpreadsheetFormatterSelector> TEXT_FORMATTER = environmentValueName(SpreadsheetMetadataPropertyName.TEXT_FORMATTER);

    private final static EnvironmentValueName<SpreadsheetFormatterSelector> TIME_FORMATTER = environmentValueName(SpreadsheetMetadataPropertyName.TIME_FORMATTER);

    private static EnvironmentValueName<SpreadsheetFormatterSelector> environmentValueName(final SpreadsheetMetadataPropertyName<SpreadsheetFormatterSelector> formatter) {
        return EnvironmentValueName.with(
            formatter.text()
        );
    }

    @Override
    public Optional<SpreadsheetFormatterSelectorToken> spreadsheetFormatterNextToken(final SpreadsheetFormatterSelector selector) {
        Objects.requireNonNull(selector, "selector");

        SpreadsheetFormatterSelectorToken next;

        final SpreadsheetFormatterName name = selector.name();
        switch (name.value()) {
            case SpreadsheetFormatterName.AUTOMATIC_STRING:
                next = null;
                break;
            case SpreadsheetFormatterName.BADGE_ERROR_STRING:
                next = null;
                break;
            case SpreadsheetFormatterName.COLLECTION_STRING:
                next = null;
                break;
            case SpreadsheetFormatterName.CURRENCY_STRING:
                next = null;
                break;
            case SpreadsheetFormatterName.DATE_STRING:
                next = formatPatternNextTextComponent(
                    selector,
                    SpreadsheetFormatParserTokenKind::isDate
                );
                break;
            case SpreadsheetFormatterName.DATE_TIME_STRING:
                next = formatPatternNextTextComponent(
                    selector,
                    SpreadsheetFormatParserTokenKind::isDateTime
                );
                break;
            case SpreadsheetFormatterName.DEFAULT_TEXT_STRING:
                next = null;
                break;
            case SpreadsheetFormatterName.EXPRESSION_STRING:
                next = null;
                break;
            case SpreadsheetFormatterName.FULL_DATE_STRING:
                next = null;
                break;
            case SpreadsheetFormatterName.FULL_DATE_TIME_STRING:
                next = null;
                break;
            case SpreadsheetFormatterName.FULL_TIME_STRING:
                next = null;
                break;
            case SpreadsheetFormatterName.GENERAL_STRING:
                next = null;
                break;
            case SpreadsheetFormatterName.LONG_DATE_STRING:
                next = null;
                break;
            case SpreadsheetFormatterName.LONG_DATE_TIME_STRING:
                next = null;
                break;
            case SpreadsheetFormatterName.LONG_TIME_STRING:
                next = null;
                break;
            case SpreadsheetFormatterName.MEDIUM_DATE_STRING:
                next = null;
                break;
            case SpreadsheetFormatterName.MEDIUM_DATE_TIME_STRING:
                next = null;
                break;
            case SpreadsheetFormatterName.MEDIUM_TIME_STRING:
                next = null;
                break;
            case SpreadsheetFormatterName.NUMBER_STRING:
                next = formatPatternNextTextComponent(
                    selector,
                    SpreadsheetFormatParserTokenKind::isNumber
                );
                break;
            case SpreadsheetFormatterName.PERCENT_STRING:
                next = null;
                break;
            case SpreadsheetFormatterName.SCIENTIFIC_STRING:
                next = null;
                break;
            case SpreadsheetFormatterName.SHORT_DATE_STRING:
                next = null;
                break;
            case SpreadsheetFormatterName.SHORT_DATE_TIME_STRING:
                next = null;
                break;
            case SpreadsheetFormatterName.SHORT_TIME_STRING:
                next = null;
                break;
            case SpreadsheetFormatterName.TEXT_STRING:
                next = formatPatternNextTextComponent(
                    selector,
                    SpreadsheetFormatParserTokenKind::isText
                );
                break;
            case SpreadsheetFormatterName.TIME_STRING:
                next = formatPatternNextTextComponent(
                    selector,
                    SpreadsheetFormatParserTokenKind::isTime
                );
                break;
            default:
                throw new IllegalArgumentException("Unknown selector " + name);
        }

        return Optional.ofNullable(next);
    }

    private SpreadsheetFormatterSelectorToken formatPatternNextTextComponent(final SpreadsheetFormatterSelector selector,
                                                                             final Predicate<SpreadsheetFormatParserTokenKind> filter) {
        SpreadsheetFormatterSelectorToken next;

        final String text = selector.valueText()
            .trim();
        final SpreadsheetPatternKind kind = selector.name()
            .patternKind;
        if (text.isEmpty()) {
            next = SpreadsheetFormatterSelectorToken.with(
                "", // label
                "", // text
                Arrays.stream(SpreadsheetFormatParserTokenKind.values())
                    .filter(filter)
                    .flatMap(k -> k.alternatives().stream())
                    .distinct()
                    .sorted()
                    .map(t -> SpreadsheetFormatterSelectorTokenAlternative.with(t, t))
                    .collect(Collectors.toList())
            );
        } else {
            final SpreadsheetFormatPattern formatPattern = kind.parse(text)
                .toFormat();
            next = SpreadsheetFormatParserTokenKind.last(
                    formatPattern.value()
                ).map(k -> toSpreadsheetFormatterSelectorTextComponent(kind, k))
                .orElse(null);
        }

        return next;
    }

    private static SpreadsheetFormatterSelectorToken toSpreadsheetFormatterSelectorTextComponent(final SpreadsheetPatternKind kind,
                                                                                                 final SpreadsheetFormatParserTokenKind spreadsheetFormatParserTokenKind) {
        return SpreadsheetFormatterSelectorToken.with(
            "", // label
            "", // text
            kind.spreadsheetFormatParserTokenKinds()
                .stream()
                .filter(k -> false == k.isNextTokenIgnored())
                .filter(k -> null == k || false == spreadsheetFormatParserTokenKind.isDuplicate(k))
                .flatMap(k -> k.alternatives().stream())
                .distinct()
                .sorted()
                .map(t -> SpreadsheetFormatterSelectorTokenAlternative.with(t, t))
                .collect(Collectors.toList())
        );
    }

    // spreadsheetFormatterSamples......................................................................................

    @Override
    public List<SpreadsheetFormatterSample> spreadsheetFormatterSamples(final SpreadsheetFormatterSelector selector,
                                                                        final boolean includeSamples,
                                                                        final SpreadsheetFormatterProviderSamplesContext context) {
        Objects.requireNonNull(selector, "selector");
        Objects.requireNonNull(context, "context");

        final List<SpreadsheetFormatterSample> samples = Lists.array();

        final SpreadsheetFormatterName name = selector.name();
        switch (name.value()) {
            case SpreadsheetFormatterName.AUTOMATIC_STRING:
            case SpreadsheetFormatterName.BADGE_ERROR_STRING:
            case SpreadsheetFormatterName.COLLECTION_STRING:
                break;
            case SpreadsheetFormatterName.CURRENCY_STRING: {
                samples.add(
                    currencySample(
                        123.5,
                        context
                    )
                );
                samples.add(
                    currencySample(
                        -123.5,
                        context
                    )
                );
                samples.add(
                    currencySample(
                        0,
                        context
                    )
                );
                if (includeSamples) {
                    final Object value = cellValueOr(
                        context,
                        () -> null
                    );
                    if (null != value) {
                        samples.add(
                            currencySample(
                                context.cell()
                                    .get()
                                    .reference()
                                    .text(),
                                value,
                                context
                            )
                        );
                    }
                }
                break;
            }
            case SpreadsheetFormatterName.DATE_STRING:
                samples.add(
                    this.dateSpreadsheetFormatterSample(
                        "Short",
                        DateFormat.SHORT,
                        context
                    )
                );
                samples.add(
                    this.dateSpreadsheetFormatterSample(
                        "Medium",
                        DateFormat.MEDIUM,
                        context
                    )
                );
                samples.add(
                    this.dateSpreadsheetFormatterSample(
                        "Long",
                        DateFormat.LONG,
                        context
                    )
                );
                samples.add(
                    this.dateSpreadsheetFormatterSample(
                        "Full",
                        DateFormat.FULL,
                        context
                    )
                );
                if (includeSamples) {
                    samples.add(
                        this.sample(
                            SAMPLE_LABEL,
                            selector,
                            this.dateValue(context),
                            context
                        )
                    );
                }
                break;
            case SpreadsheetFormatterName.DATE_TIME_STRING:
                samples.add(
                    this.dateTimeSpreadsheetFormatterSample(
                        "Short",
                        DateFormat.SHORT,
                        context
                    )
                );
                samples.add(
                    this.dateTimeSpreadsheetFormatterSample(
                        "Medium",
                        DateFormat.MEDIUM,
                        context
                    )
                );
                samples.add(
                    this.dateTimeSpreadsheetFormatterSample(
                        "Long",
                        DateFormat.LONG,
                        context
                    )
                );
                samples.add(
                    this.dateTimeSpreadsheetFormatterSample(
                        "Full",
                        DateFormat.FULL,
                        context
                    )
                );
                if (includeSamples) {
                    samples.add(
                        this.sample(
                            SAMPLE_LABEL,
                            selector,
                            this.dateTimeValue(context),
                            context
                        )
                    );
                }
                break;
            case SpreadsheetFormatterName.DEFAULT_TEXT_STRING:
                samples.add(
                    SpreadsheetFormatterSample.with(
                        "Default",
                        SpreadsheetFormatterSelector.DEFAULT_TEXT_FORMAT,
                        TextNode.text("Hello 123")
                    )
                );
                if (includeSamples) {
                    samples.add(
                        this.sample(
                            SAMPLE_LABEL,
                            selector,
                            this.textValue(context),
                            context
                        )
                    );
                }
                break;
            case SpreadsheetFormatterName.EXPRESSION_STRING: {
                if (includeSamples && false == selector.valueText().isEmpty()) {
                    samples.add(
                        this.sample(
                            SAMPLE_LABEL,
                            selector,
                            cellValueOr(
                                context,
                                () -> null
                            ),
                            context
                        )
                    );
                }
                break;
            }
            case SpreadsheetFormatterName.FULL_DATE_STRING:
                samples.add(
                    fullDateSample(
                        this.dateValue(context),
                        context
                    )
                );
                if (includeSamples) {
                    final Object value = cellValueOr(
                        context,
                        () -> null
                    );
                    if (null != value) {
                        samples.add(
                            fullDateSample(
                                context.cell()
                                    .get()
                                    .reference()
                                    .text(),
                                value,
                                context
                            )
                        );
                    }
                }
                break;
            case SpreadsheetFormatterName.FULL_DATE_TIME_STRING:
                samples.add(
                    fullDateTimeSample(
                        this.dateTimeValue(context),
                        context
                    )
                );
                if (includeSamples) {
                    final Object value = cellValueOr(
                        context,
                        () -> null
                    );
                    if (null != value) {
                        samples.add(
                            fullDateTimeSample(
                                context.cell()
                                    .get()
                                    .reference()
                                    .text(),
                                value,
                                context
                            )
                        );
                    }
                }
                break;
            case SpreadsheetFormatterName.FULL_TIME_STRING:
                samples.add(
                    fullTimeSample(
                        this.dateValue(context),
                        context
                    )
                );
                if (includeSamples) {
                    final Object value = cellValueOr(
                        context,
                        () -> null
                    );
                    if (null != value) {
                        samples.add(
                            fullTimeSample(
                                context.cell()
                                    .get()
                                    .reference()
                                    .text(),
                                value,
                                context
                            )
                        );
                    }
                }
                break;
            case SpreadsheetFormatterName.GENERAL_STRING: {
                samples.add(
                    generalSample(
                        123.5,
                        context
                    )
                );
                samples.add(
                    generalSample(
                        -123.5,
                        context
                    )
                );
                samples.add(
                    generalSample(
                        0,
                        context
                    )
                );
                if (includeSamples) {
                    final Object value = cellValueOr(
                        context,
                        () -> null
                    );
                    if (null != value) {
                        samples.add(
                            generalSample(
                                context.cell()
                                    .get()
                                    .reference()
                                    .text(),
                                value,
                                context
                            )
                        );
                    }
                }
                break;
            }
            case SpreadsheetFormatterName.LONG_DATE_STRING:
                samples.add(
                    longDateSample(
                        this.dateValue(context),
                        context
                    )
                );
                if (includeSamples) {
                    final Object value = cellValueOr(
                        context,
                        () -> null
                    );
                    if (null != value) {
                        samples.add(
                            longDateSample(
                                context.cell()
                                    .get()
                                    .reference()
                                    .text(),
                                value,
                                context
                            )
                        );
                    }
                }
                break;
            case SpreadsheetFormatterName.LONG_DATE_TIME_STRING:
                samples.add(
                    longDateTimeSample(
                        this.dateTimeValue(context),
                        context
                    )
                );
                if (includeSamples) {
                    final Object value = cellValueOr(
                        context,
                        () -> null
                    );
                    if (null != value) {
                        samples.add(
                            longDateTimeSample(
                                context.cell()
                                    .get()
                                    .reference()
                                    .text(),
                                value,
                                context
                            )
                        );
                    }
                }
                break;
            case SpreadsheetFormatterName.LONG_TIME_STRING:
                samples.add(
                    longTimeSample(
                        this.timeValue(context),
                        context
                    )
                );
                if (includeSamples) {
                    final Object value = cellValueOr(
                        context,
                        () -> null
                    );
                    if (null != value) {
                        samples.add(
                            longTimeSample(
                                context.cell()
                                    .get()
                                    .reference()
                                    .text(),
                                value,
                                context
                            )
                        );
                    }
                }
                break;
            case SpreadsheetFormatterName.MEDIUM_DATE_STRING:
                samples.add(
                    mediumDateSample(
                        this.dateValue(context),
                        context
                    )
                );
                if (includeSamples) {
                    final Object value = cellValueOr(
                        context,
                        () -> null
                    );
                    if (null != value) {
                        samples.add(
                            mediumDateSample(
                                context.cell()
                                    .get()
                                    .reference()
                                    .text(),
                                value,
                                context
                            )
                        );
                    }
                }
                break;
            case SpreadsheetFormatterName.MEDIUM_DATE_TIME_STRING:
                samples.add(
                    mediumDateTimeSample(
                        this.dateTimeValue(context),
                        context
                    )
                );
                if (includeSamples) {
                    final Object value = cellValueOr(
                        context,
                        () -> null
                    );
                    if (null != value) {
                        samples.add(
                            mediumDateTimeSample(
                                context.cell()
                                    .get()
                                    .reference()
                                    .text(),
                                value,
                                context
                            )
                        );
                    }
                }
                break;
            case SpreadsheetFormatterName.MEDIUM_TIME_STRING:
                samples.add(
                    mediumTimeSample(
                        this.dateTimeValue(context),
                        context
                    )
                );
                if (includeSamples) {
                    final Object value = cellValueOr(
                        context,
                        () -> null
                    );
                    if (null != value) {
                        samples.add(
                            mediumTimeSample(
                                context.cell()
                                    .get()
                                    .reference()
                                    .text(),
                                value,
                                context
                            )
                        );
                    }
                }
                break;
            case SpreadsheetFormatterName.NUMBER_STRING:
                samples.add(
                    this.numberSpreadsheetFormatterSample(
                        "Number",
                        DecimalFormat::getInstance,
                        123.5,
                        context
                    )
                );
                samples.add(
                    this.numberSpreadsheetFormatterSample(
                        "Number",
                        DecimalFormat::getInstance,
                        -123.5,
                        context
                    )
                );
                samples.add(
                    this.numberSpreadsheetFormatterSample(
                        "Number",
                        DecimalFormat::getInstance,
                        0,
                        context
                    )
                );
                samples.add(
                    this.numberSpreadsheetFormatterSample(
                        "Integer",
                        DecimalFormat::getIntegerInstance,
                        123.5,
                        context
                    )
                );
                samples.add(
                    this.numberSpreadsheetFormatterSample(
                        "Integer",
                        DecimalFormat::getIntegerInstance,
                        -123.5,
                        context
                    )
                );
                samples.add(
                    this.numberSpreadsheetFormatterSample(
                        "Integer",
                        DecimalFormat::getIntegerInstance,
                        0,
                        context
                    )
                );
                samples.add(
                    this.numberSpreadsheetFormatterSample(
                        "Percent",
                        DecimalFormat::getPercentInstance,
                        123.5,
                        context
                    )
                );
                samples.add(
                    this.numberSpreadsheetFormatterSample(
                        "Percent",
                        DecimalFormat::getPercentInstance,
                        -123.5,
                        context
                    )
                );
                samples.add(
                    this.numberSpreadsheetFormatterSample(
                        "Percent",
                        DecimalFormat::getPercentInstance,
                        0,
                        context
                    )
                );
                samples.add(
                    this.numberSpreadsheetFormatterSample(
                        "Currency",
                        DecimalFormat::getCurrencyInstance,
                        123.5,
                        context
                    )
                );
                samples.add(
                    this.numberSpreadsheetFormatterSample(
                        "Currency",
                        DecimalFormat::getCurrencyInstance,
                        -123.5,
                        context
                    )
                );
                samples.add(
                    this.numberSpreadsheetFormatterSample(
                        "Currency",
                        DecimalFormat::getCurrencyInstance,
                        0,
                        context
                    )
                );
                if (includeSamples) {
                    samples.add(
                        this.sample(
                            SAMPLE_LABEL,
                            selector,
                            this.numberValue(context),
                            context
                        )
                    );
                }
                break;
            case SpreadsheetFormatterName.PERCENT_STRING: {
                samples.add(
                    percentSample(
                        123.5,
                        context
                    )
                );
                samples.add(
                    percentSample(
                        -123.5,
                        context
                    )
                );
                samples.add(
                    percentSample(
                        0,
                        context
                    )
                );
                if (includeSamples) {
                    final Object value = cellValueOr(
                        context,
                        () -> null
                    );
                    if (null != value) {
                        samples.add(
                            percentSample(
                                context.cell()
                                    .get()
                                    .reference()
                                    .text(),
                                value,
                                context
                            )
                        );
                    }
                }
                break;
            }
            case SpreadsheetFormatterName.SCIENTIFIC_STRING: {
                samples.add(
                    scientificSample(
                        123.5,
                        context
                    )
                );
                samples.add(
                    scientificSample(
                        -123.5,
                        context
                    )
                );
                samples.add(
                    scientificSample(
                        0,
                        context
                    )
                );
                if (includeSamples) {
                    final Object value = cellValueOr(
                        context,
                        () -> null
                    );
                    if (null != value) {
                        samples.add(
                            scientificSample(
                                context.cell()
                                    .get()
                                    .reference()
                                    .text(),
                                value,
                                context
                            )
                        );
                    }
                }
                break;
            }
            case SpreadsheetFormatterName.SHORT_DATE_STRING:
                samples.add(
                    shortDateSample(
                        this.dateValue(context),
                        context
                    )
                );
                if (includeSamples) {
                    final Object value = cellValueOr(
                        context,
                        () -> null
                    );
                    if (null != value) {
                        samples.add(
                            shortDateSample(
                                context.cell()
                                    .get()
                                    .reference()
                                    .text(),
                                value,
                                context
                            )
                        );
                    }
                }
                break;
            case SpreadsheetFormatterName.SHORT_DATE_TIME_STRING:
                samples.add(
                    shortDateTimeSample(
                        this.dateTimeValue(context),
                        context
                    )
                );
                if (includeSamples) {
                    final Object value = cellValueOr(
                        context,
                        () -> null
                    );
                    if (null != value) {
                        samples.add(
                            shortDateTimeSample(
                                context.cell()
                                    .get()
                                    .reference()
                                    .text(),
                                value,
                                context
                            )
                        );
                    }
                }
                break;
            case SpreadsheetFormatterName.SHORT_TIME_STRING:
                samples.add(
                    shortTimeSample(
                        this.timeValue(context),
                        context
                    )
                );
                if (includeSamples) {
                    final Object value = cellValueOr(
                        context,
                        () -> null
                    );
                    if (null != value) {
                        samples.add(
                            shortTimeSample(
                                context.cell()
                                    .get()
                                    .reference()
                                    .text(),
                                value,
                                context
                            )
                        );
                    }
                }
                break;
            case SpreadsheetFormatterName.TEXT_STRING: {
                final Object value = cellValueOr(
                    context,
                    () -> null
                );

                samples.add(
                    SpreadsheetFormatterSample.with(
                        "Default",
                        SpreadsheetFormatterSelector.DEFAULT_TEXT_FORMAT,
                        TextNode.text("Hello 123")
                    )
                );

                if (includeSamples) {
                    if (null != value) {
                        samples.add(
                            sample(
                                context.cell()
                                    .get()
                                    .reference()
                                    .text(),
                                SpreadsheetFormatterSelector.DEFAULT_TEXT_FORMAT,
                                value,
                                context
                            )
                        );
                    }
                    samples.add(
                        this.sample(
                            SAMPLE_LABEL,
                            selector,
                            this.textValue(context),
                            context
                        )
                    );
                }
                break;
            }
            case SpreadsheetFormatterName.TIME_STRING:
                samples.add(
                    this.timeSpreadsheetFormatterSample(
                        "Short",
                        DateFormat.SHORT,
                        context
                    )
                );
                samples.add(
                    this.timeSpreadsheetFormatterSample(
                        "Long",
                        DateFormat.LONG,
                        context
                    )
                );
                if (includeSamples) {
                    samples.add(
                        this.sample(
                            SAMPLE_LABEL,
                            selector,
                            this.timeValue(context),
                            context
                        )
                    );
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown formatter " + name);
        }

        return Lists.immutable(samples);
    }

    private SpreadsheetFormatterSample currencySample(final Object value,
                                                      final SpreadsheetFormatterProviderSamplesContext context) {
        return currencySample(
            "Currency",
            value,
            context
        );
    }

    private SpreadsheetFormatterSample currencySample(final String label,
                                                      final Object value,
                                                      final SpreadsheetFormatterProviderSamplesContext context) {
        return this.sample(
            label,
            SpreadsheetFormatterName.CURRENCY.setValueText(""),
            value,
            context
        );
    }

    private SpreadsheetFormatterSample dateSpreadsheetFormatterSample(final String label,
                                                                      final int dateFormatStyle,
                                                                      final SpreadsheetFormatterProviderSamplesContext context) {
        return this.sample(
            label,
            SpreadsheetPattern.dateParsePattern(
                (SimpleDateFormat) DateFormat.getDateInstance(
                    dateFormatStyle,
                    context.locale()
                )
            ),
            dateValue(context),
            context
        );
    }

    private Object dateValue(final SpreadsheetFormatterProviderSamplesContext context) {
        return cellValueOr(
            context,
            () -> context.now()
                .toLocalDate()
        );
    }

    private SpreadsheetFormatterSample dateTimeSpreadsheetFormatterSample(final String label,
                                                                          final int dateFormatStyle,
                                                                          final SpreadsheetFormatterProviderSamplesContext context) {
        return this.sample(
            label,
            SpreadsheetPattern.dateTimeParsePattern(
                (SimpleDateFormat) DateFormat.getDateTimeInstance(
                    dateFormatStyle,
                    dateFormatStyle,
                    context.locale()
                )
            ),
            dateTimeValue(context),
            context
        );
    }

    private Object dateTimeValue(final SpreadsheetFormatterProviderSamplesContext context) {
        return cellValueOr(
            context,
            context::now
        );
    }

    private SpreadsheetFormatterSample fullDateSample(final Object value,
                                                      final SpreadsheetFormatterProviderSamplesContext context) {
        return fullDateSample(
            "Full Date",
            value,
            context
        );
    }

    private SpreadsheetFormatterSample fullDateSample(final String label,
                                                      final Object value,
                                                      final SpreadsheetFormatterProviderSamplesContext context) {
        return this.sample(
            label,
            SpreadsheetFormatterName.FULL_DATE.setValueText(""),
            value,
            context
        );
    }

    private SpreadsheetFormatterSample fullDateTimeSample(final Object value,
                                                          final SpreadsheetFormatterProviderSamplesContext context) {
        return fullDateTimeSample(
            "Full Date Time",
            value,
            context
        );
    }

    private SpreadsheetFormatterSample fullDateTimeSample(final String label,
                                                          final Object value,
                                                          final SpreadsheetFormatterProviderSamplesContext context) {
        return this.sample(
            label,
            SpreadsheetFormatterName.FULL_DATE_TIME.setValueText(""),
            value,
            context
        );
    }

    private SpreadsheetFormatterSample fullTimeSample(final Object value,
                                                      final SpreadsheetFormatterProviderSamplesContext context) {
        return fullTimeSample(
            "Full Time",
            value,
            context
        );
    }

    private SpreadsheetFormatterSample fullTimeSample(final String label,
                                                      final Object value,
                                                      final SpreadsheetFormatterProviderSamplesContext context) {
        return this.sample(
            label,
            SpreadsheetFormatterName.FULL_TIME.setValueText(""),
            value,
            context
        );
    }

    private SpreadsheetFormatterSample generalSample(final Object value,
                                                     final SpreadsheetFormatterProviderSamplesContext context) {
        return generalSample(
            "General",
            value,
            context
        );
    }

    private SpreadsheetFormatterSample generalSample(final String label,
                                                     final Object value,
                                                     final SpreadsheetFormatterProviderSamplesContext context) {
        return this.sample(
            label,
            SpreadsheetFormatterName.GENERAL.setValueText(""),
            value,
            context
        );
    }

    private SpreadsheetFormatterSample longDateSample(final Object value,
                                                      final SpreadsheetFormatterProviderSamplesContext context) {
        return longDateSample(
            "Long Date",
            value,
            context
        );
    }

    private SpreadsheetFormatterSample longDateSample(final String label,
                                                      final Object value,
                                                      final SpreadsheetFormatterProviderSamplesContext context) {
        return this.sample(
            label,
            SpreadsheetFormatterName.LONG_DATE.setValueText(""),
            value,
            context
        );
    }

    private SpreadsheetFormatterSample longDateTimeSample(final Object value,
                                                          final SpreadsheetFormatterProviderSamplesContext context) {
        return longDateTimeSample(
            "Long Date Time",
            value,
            context
        );
    }

    private SpreadsheetFormatterSample longDateTimeSample(final String label,
                                                          final Object value,
                                                          final SpreadsheetFormatterProviderSamplesContext context) {
        return this.sample(
            label,
            SpreadsheetFormatterName.LONG_DATE_TIME.setValueText(""),
            value,
            context
        );
    }

    private SpreadsheetFormatterSample longTimeSample(final Object value,
                                                      final SpreadsheetFormatterProviderSamplesContext context) {
        return longTimeSample(
            "Long Time",
            value,
            context
        );
    }

    private SpreadsheetFormatterSample longTimeSample(final String label,
                                                      final Object value,
                                                      final SpreadsheetFormatterProviderSamplesContext context) {
        return this.sample(
            label,
            SpreadsheetFormatterName.LONG_TIME.setValueText(""),
            value,
            context
        );
    }

    private SpreadsheetFormatterSample mediumDateSample(final Object value,
                                                        final SpreadsheetFormatterProviderSamplesContext context) {
        return mediumDateSample(
            "Medium Date",
            value,
            context
        );
    }

    private SpreadsheetFormatterSample mediumDateSample(final String label,
                                                        final Object value,
                                                        final SpreadsheetFormatterProviderSamplesContext context) {
        return this.sample(
            label,
            SpreadsheetFormatterName.MEDIUM_DATE.setValueText(""),
            value,
            context
        );
    }

    private SpreadsheetFormatterSample mediumDateTimeSample(final Object value,
                                                            final SpreadsheetFormatterProviderSamplesContext context) {
        return mediumDateTimeSample(
            "Medium Date Time",
            value,
            context
        );
    }

    private SpreadsheetFormatterSample mediumDateTimeSample(final String label,
                                                            final Object value,
                                                            final SpreadsheetFormatterProviderSamplesContext context) {
        return this.sample(
            label,
            SpreadsheetFormatterName.MEDIUM_DATE_TIME.setValueText(""),
            value,
            context
        );
    }

    private SpreadsheetFormatterSample mediumTimeSample(final Object value,
                                                        final SpreadsheetFormatterProviderSamplesContext context) {
        return mediumTimeSample(
            "Medium Time",
            value,
            context
        );
    }

    private SpreadsheetFormatterSample mediumTimeSample(final String label,
                                                        final Object value,
                                                        final SpreadsheetFormatterProviderSamplesContext context) {
        return this.sample(
            label,
            SpreadsheetFormatterName.MEDIUM_TIME.setValueText(""),
            value,
            context
        );
    }

    private SpreadsheetFormatterSample numberSpreadsheetFormatterSample(final String label,
                                                                        final Function<Locale, NumberFormat> decimalFormat,
                                                                        final Number value,
                                                                        final SpreadsheetFormatterProviderSamplesContext context) {
        return this.sample(
            label,
            SpreadsheetPattern.decimalFormat(
                (DecimalFormat) decimalFormat.apply(
                    context.locale()
                )
            ),
            value,
            context
        );
    }

    private Object numberValue(final SpreadsheetFormatterProviderSamplesContext context) {
        return cellValueOr(
            context,
            () -> 1234.50
        );
    }

    private SpreadsheetFormatterSample percentSample(final Object value,
                                                     final SpreadsheetFormatterProviderSamplesContext context) {
        return percentSample(
            "Percent",
            value,
            context
        );
    }

    private SpreadsheetFormatterSample percentSample(final String label,
                                                     final Object value,
                                                     final SpreadsheetFormatterProviderSamplesContext context) {
        return this.sample(
            label,
            SpreadsheetFormatterName.PERCENT.setValueText(""),
            value,
            context
        );
    }

    private SpreadsheetFormatterSample scientificSample(final Object value,
                                                        final SpreadsheetFormatterProviderSamplesContext context) {
        return scientificSample(
            "Scientific",
            value,
            context
        );
    }

    private SpreadsheetFormatterSample scientificSample(final String label,
                                                        final Object value,
                                                        final SpreadsheetFormatterProviderSamplesContext context) {
        return this.sample(
            label,
            SpreadsheetFormatterName.SCIENTIFIC.setValueText(""),
            value,
            context
        );
    }

    private SpreadsheetFormatterSample shortDateSample(final Object value,
                                                       final SpreadsheetFormatterProviderSamplesContext context) {
        return shortDateSample(
            "Short Date",
            value,
            context
        );
    }

    private SpreadsheetFormatterSample shortDateSample(final String label,
                                                       final Object value,
                                                       final SpreadsheetFormatterProviderSamplesContext context) {
        return this.sample(
            label,
            SpreadsheetFormatterName.SHORT_DATE.setValueText(""),
            value,
            context
        );
    }

    private SpreadsheetFormatterSample shortDateTimeSample(final Object value,
                                                           final SpreadsheetFormatterProviderSamplesContext context) {
        return shortDateTimeSample(
            "Short Date Time",
            value,
            context
        );
    }

    private SpreadsheetFormatterSample shortDateTimeSample(final String label,
                                                           final Object value,
                                                           final SpreadsheetFormatterProviderSamplesContext context) {
        return this.sample(
            label,
            SpreadsheetFormatterName.SHORT_DATE_TIME.setValueText(""),
            value,
            context
        );
    }

    private SpreadsheetFormatterSample shortTimeSample(final Object value,
                                                       final SpreadsheetFormatterProviderSamplesContext context) {
        return shortTimeSample(
            "Short Time",
            value,
            context
        );
    }

    private SpreadsheetFormatterSample shortTimeSample(final String label,
                                                       final Object value,
                                                       final SpreadsheetFormatterProviderSamplesContext context) {
        return this.sample(
            label,
            SpreadsheetFormatterName.SHORT_TIME.setValueText(""),
            value,
            context
        );
    }

    private SpreadsheetFormatterSample timeSpreadsheetFormatterSample(final String label,
                                                                      final int dateFormatStyle,
                                                                      final SpreadsheetFormatterProviderSamplesContext context) {
        return this.sample(
            label,
            SpreadsheetPattern.timeParsePattern(
                (SimpleDateFormat) DateFormat.getTimeInstance(
                    dateFormatStyle,
                    context.locale()
                )
            ),
            timeValue(context),
            context
        );
    }

    private Object textValue(final SpreadsheetFormatterProviderSamplesContext context) {
        return cellValueOr(
            context,
            () -> "Hello World 123"
        );
    }

    private Object timeValue(final SpreadsheetFormatterProviderSamplesContext context) {
        return cellValueOr(
            context,
            context.now()::toLocalTime
        );
    }

    private static Object cellValueOr(final SpreadsheetFormatterContext context,
                                      final Supplier<Object> defaultValue) {
        Object value = context.cell()
            .flatMap(c -> c.formula().value())
            .orElse(null);

        if (null == value || value instanceof SpreadsheetError) {
            value = defaultValue.get();
        }

        return value;
    }

    private SpreadsheetFormatterSample sample(final String label,
                                              final SpreadsheetParsePattern pattern,
                                              final Object value,
                                              final SpreadsheetFormatterProviderSamplesContext context) {
        return this.sample(
            label,
            pattern.toFormat()
                .spreadsheetFormatterSelector(),
            value,
            context
        );
    }

    private SpreadsheetFormatterSample sample(final String label,
                                              final SpreadsheetFormatterSelector selector,
                                              final Object value,
                                              final SpreadsheetFormatterProviderSamplesContext context) {
        TextNode formatted;

        // if fetching the formatter or formatting the value fails capture the exception
        try {
            final SpreadsheetFormatter formatter = this.spreadsheetFormatter(
                selector,
                context.providerContext()
            );

            formatted = formatter.formatOrEmptyText(
                Optional.ofNullable(value),
                context
            );
        } catch (final RuntimeException fail) {
            formatted = sampleError(fail);
        }

        return SpreadsheetFormatterSample.with(
            label,
            selector,
            formatted
        );
    }

    static TextNode sampleError(final RuntimeException cause) {
        final String message = cause.getMessage();

        return sampleError(
            CharSequences.isNullOrEmpty(message) ?
                cause.getClass().getSimpleName() :
                message
        );
    }

    static TextNode sampleError(final String message) {
        return TextNode.style(
            Lists.of(
                RED_ERROR,
                TextNode.text(
                    " " + message
                )
            )
        );
    }

    private final static TextNode RED_ERROR = TextNode.text("ERROR")
        .set(
            TextStylePropertyName.COLOR,
            WebColorName.RED.color()
        );

    // spreadsheetFormatterInfos........................................................................................

    @Override
    public SpreadsheetFormatterInfoSet spreadsheetFormatterInfos() {
        return INFOS;
    }

    private final static SpreadsheetFormatterInfoSet INFOS = SpreadsheetFormatterInfoSet.with(
        Sets.of(
            spreadsheetFormatterInfo(SpreadsheetFormatterName.AUTOMATIC),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.BADGE_ERROR),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.COLLECTION),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.CURRENCY),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.DATE),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.DATE_TIME),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.DEFAULT_TEXT),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.EXPRESSION),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.FULL_DATE),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.FULL_DATE_TIME),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.FULL_TIME),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.GENERAL),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.LONG_DATE),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.LONG_DATE_TIME),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.LONG_TIME),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.MEDIUM_DATE),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.MEDIUM_DATE_TIME),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.MEDIUM_TIME),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.NUMBER),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.PERCENT),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.SCIENTIFIC),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.SHORT_DATE),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.SHORT_DATE_TIME),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.SHORT_TIME),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.TEXT),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.TIME)
        )
    );

    private static SpreadsheetFormatterInfo spreadsheetFormatterInfo(final SpreadsheetFormatterName name) {
        return SpreadsheetFormatterInfo.with(
            SpreadsheetFormatterProviders.BASE_URL.appendPath(UrlPath.parse(name.value())),
            name
        );
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
