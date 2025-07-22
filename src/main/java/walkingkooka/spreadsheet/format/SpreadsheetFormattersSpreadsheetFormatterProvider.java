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

package walkingkooka.spreadsheet.format;

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.UrlPath;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenKind;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternKind;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.text.TextNode;

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
            case SpreadsheetFormatterName.DEFAULT_TEXT_STRING:
            case SpreadsheetFormatterName.EXPRESSION_STRING:
            case SpreadsheetFormatterName.GENERAL_STRING:
            case SpreadsheetFormatterName.SPREADSHEET_PATTERN_COLLECTION_STRING:
                formatter = selector.evaluateValueText(
                    this,
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
                    case 5:
                        formatter = SpreadsheetFormatters.automatic(
                            (SpreadsheetFormatter) copy.get(0), // date
                            (SpreadsheetFormatter) copy.get(1), // date-time
                            (SpreadsheetFormatter) copy.get(2), // number
                            (SpreadsheetFormatter) copy.get(3), // text
                            (SpreadsheetFormatter) copy.get(4) // time
                        );
                        break;
                    default:
                        throw new IllegalArgumentException("Expected 0 or 5 value(s) got " + count);
                }
                break;
            case SpreadsheetFormatterName.COLLECTION_STRING:
                formatter = SpreadsheetFormatters.collection(
                    values.stream()
                        .map(c -> (SpreadsheetFormatter) c)
                        .collect(Collectors.toList())
                );
                break;
            case SpreadsheetFormatterName.DEFAULT_TEXT_STRING:
                if (0 != count) {
                    throw new IllegalArgumentException("Expected 0 value(s) got " + count);
                }
                formatter = SpreadsheetFormatters.defaultText();
                break;
            case SpreadsheetFormatterName.EXPRESSION_STRING:
                if (1 != count) {
                    throw new IllegalArgumentException("Expected 1 value(s) got " + count);
                }
                formatter = SpreadsheetFormatters.expression(
                    context.convertOrFail(
                        copy.get(0),
                        Expression.class
                    )
                );
                break;
            case SpreadsheetFormatterName.GENERAL_STRING:
                if (0 != count) {
                    throw new IllegalArgumentException("Expected 0 value(s) got " + count);
                }
                formatter = SpreadsheetFormatters.general();
                break;
            case SpreadsheetFormatterName.SPREADSHEET_PATTERN_COLLECTION_STRING:
                formatter = SpreadsheetFormatters.spreadsheetPatternCollection(
                    values.stream()
                        .map(c -> (SpreadsheetPatternSpreadsheetFormatter) c)
                        .collect(Collectors.toList())
                );
                break;
            default:
                final SpreadsheetPatternKind kind = name.patternKind;
                if (null == kind) {
                    throw new IllegalArgumentException("Unknown formatter " + name);
                }
                if (1 != count) {
                    throw new IllegalArgumentException("Expected 1 value(s) got " + count);
                }
                formatter = kind.parse(
                    (String) copy.get(0)
                ).formatter();
        }

        return formatter;
    }

    private SpreadsheetFormatter spreadsheetFormatter(final EnvironmentValueName<SpreadsheetFormatterSelector> value,
                                                      final ProviderContext context) {
        return this.spreadsheetFormatter(
            context.environmentValueOrFail(value),
            context
        );
    }

    private final static EnvironmentValueName<SpreadsheetFormatterSelector> DATE_FORMATTER = environmentValueName(SpreadsheetMetadataPropertyName.DATE_FORMATTER);

    private final static EnvironmentValueName<SpreadsheetFormatterSelector> DATE_TIME_FORMATTER = environmentValueName(SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER);

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
            case SpreadsheetFormatterName.COLLECTION_STRING:
                next = null;
                break;
            case SpreadsheetFormatterName.DATE_FORMAT_PATTERN_STRING:
                next = formatPatternNextTextComponent(
                    selector,
                    SpreadsheetFormatParserTokenKind::isDate
                );
                break;
            case SpreadsheetFormatterName.DATE_TIME_FORMAT_PATTERN_STRING:
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
            case SpreadsheetFormatterName.GENERAL_STRING:
                next = null;
                break;
            case SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN_STRING:
                next = formatPatternNextTextComponent(
                    selector,
                    SpreadsheetFormatParserTokenKind::isNumber
                );
                break;
            case SpreadsheetFormatterName.SPREADSHEET_PATTERN_COLLECTION_STRING:
                next = null;
                break;
            case SpreadsheetFormatterName.TEXT_FORMAT_PATTERN_STRING:
                next = formatPatternNextTextComponent(
                    selector,
                    SpreadsheetFormatParserTokenKind::isText
                );
                break;
            case SpreadsheetFormatterName.TIME_FORMAT_PATTERN_STRING:
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
    public List<SpreadsheetFormatterSample> spreadsheetFormatterSamples(final SpreadsheetFormatterName name,
                                                                        final SpreadsheetFormatterProviderSamplesContext context) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(context, "context");

        final List<SpreadsheetFormatterSample> samples;

        switch (name.value()) {
            case SpreadsheetFormatterName.AUTOMATIC_STRING:
            case SpreadsheetFormatterName.COLLECTION_STRING:
                samples = SpreadsheetFormatterProvider.NO_SPREADSHEET_FORMATTER_SAMPLES;
                break;
            case SpreadsheetFormatterName.DATE_FORMAT_PATTERN_STRING:
                samples = Lists.of(
                    this.dateSpreadsheetFormatterSample(
                        "Short",
                        DateFormat.SHORT,
                        context
                    ),
                    this.dateSpreadsheetFormatterSample(
                        "Medium",
                        DateFormat.MEDIUM,
                        context
                    ),
                    this.dateSpreadsheetFormatterSample(
                        "Long",
                        DateFormat.LONG,
                        context
                    ),
                    this.dateSpreadsheetFormatterSample(
                        "Full",
                        DateFormat.FULL,
                        context
                    )
                );
                break;
            case SpreadsheetFormatterName.DATE_TIME_FORMAT_PATTERN_STRING:
                samples = Lists.of(
                    this.dateTimeSpreadsheetFormatterSample(
                        "Short",
                        DateFormat.SHORT,
                        context
                    ),
                    this.dateTimeSpreadsheetFormatterSample(
                        "Medium",
                        DateFormat.MEDIUM,
                        context
                    ),
                    this.dateTimeSpreadsheetFormatterSample(
                        "Long",
                        DateFormat.LONG,
                        context
                    ),
                    this.dateTimeSpreadsheetFormatterSample(
                        "Full",
                        DateFormat.FULL,
                        context
                    )
                );
                break;
            case SpreadsheetFormatterName.DEFAULT_TEXT_STRING:
                samples = Lists.of(
                    SpreadsheetFormatterSample.with(
                        "Default",
                        SpreadsheetFormatterSelector.DEFAULT_TEXT_FORMAT,
                        TextNode.text("Hello 123")
                    )
                );
                break;
            case SpreadsheetFormatterName.EXPRESSION_STRING:
                samples = NO_SPREADSHEET_FORMATTER_SAMPLES;
                break;
            case SpreadsheetFormatterName.GENERAL_STRING:
                final Object value = cellValueOr(
                    context,
                    () -> null
                );

                samples = listFilterNulls(
                    generalSample(
                        123.5,
                        context
                    ),
                    generalSample(
                        -123.5,
                        context
                    ),
                    generalSample(
                        0,
                        context
                    ),
                    null != value ?
                        generalSample(
                            context.cell()
                                .get()
                                .reference()
                                .text(),
                            value,
                            context
                        ) :
                        null
                );
                break;
            case SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN_STRING:
                samples = Lists.of(
                    this.numberSpreadsheetFormatterSample(
                        "Number",
                        DecimalFormat::getInstance,
                        123.5,
                        context
                    ),
                    this.numberSpreadsheetFormatterSample(
                        "Number",
                        DecimalFormat::getInstance,
                        -123.5,
                        context
                    ),
                    this.numberSpreadsheetFormatterSample(
                        "Number",
                        DecimalFormat::getInstance,
                        0,
                        context
                    ),
                    this.numberSpreadsheetFormatterSample(
                        "Integer",
                        DecimalFormat::getIntegerInstance,
                        123.5,
                        context
                    ),
                    this.numberSpreadsheetFormatterSample(
                        "Integer",
                        DecimalFormat::getIntegerInstance,
                        -123.5,
                        context
                    ),
                    this.numberSpreadsheetFormatterSample(
                        "Integer",
                        DecimalFormat::getIntegerInstance,
                        0,
                        context
                    ),
                    this.numberSpreadsheetFormatterSample(
                        "Percent",
                        DecimalFormat::getPercentInstance,
                        123.5,
                        context
                    ),
                    this.numberSpreadsheetFormatterSample(
                        "Percent",
                        DecimalFormat::getPercentInstance,
                        -123.5,
                        context
                    ),
                    this.numberSpreadsheetFormatterSample(
                        "Percent",
                        DecimalFormat::getPercentInstance,
                        0,
                        context
                    ),
                    this.numberSpreadsheetFormatterSample(
                        "Currency",
                        DecimalFormat::getCurrencyInstance,
                        123.5,
                        context
                    ),
                    this.numberSpreadsheetFormatterSample(
                        "Currency",
                        DecimalFormat::getCurrencyInstance,
                        -123.5,
                        context
                    ),
                    this.numberSpreadsheetFormatterSample(
                        "Currency",
                        DecimalFormat::getCurrencyInstance,
                        0,
                        context
                    )
                );
                break;
            case SpreadsheetFormatterName.TEXT_FORMAT_PATTERN_STRING:
                samples = Lists.of(
                    SpreadsheetFormatterSample.with(
                        "Default",
                        SpreadsheetFormatterSelector.DEFAULT_TEXT_FORMAT,
                        TextNode.text("Hello 123")
                    )
                );
                break;
            case SpreadsheetFormatterName.TIME_FORMAT_PATTERN_STRING:
                samples = Lists.of(
                    this.timeSpreadsheetFormatterSample(
                        "Short",
                        DateFormat.SHORT,
                        context
                    ),
                    this.timeSpreadsheetFormatterSample(
                        "Long",
                        DateFormat.LONG,
                        context
                    )
                );
                break;
            default:
                throw new IllegalArgumentException("Unknown formatter " + name);
        }


        return samples;
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
            cellValueOr(
                context,
                () -> context.now()
                    .toLocalDate()
            ),
            context
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
            cellValueOr(
                context,
                context::now
            ),
            context
        );
    }

    private SpreadsheetFormatterSample generalSample(final Object value,
                                                     final SpreadsheetFormatterContext context) {
        return generalSample(
            "General",
            value,
            context
        );
    }

    private SpreadsheetFormatterSample generalSample(final String label,
                                                     final Object value,
                                                     final SpreadsheetFormatterContext context) {
        return SpreadsheetFormatterSample.with(
            label,
            SpreadsheetFormatterName.GENERAL.setValueText(""),
            context.formatValueOrEmptyText(
                Optional.ofNullable(value)
            )
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
            cellValueOr(
                context,
                context.now()::toLocalTime
            ),
            context
        );
    }

    private static Object cellValueOr(final SpreadsheetFormatterContext context,
                                      final Supplier<Object> defaultValue) {
        Object value =context.cell()
            .flatMap(c -> c.formula().value())
            .orElse(null);

        if(null == value) {
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
        final SpreadsheetFormatter formatter = this.spreadsheetFormatter(
            selector,
            context
        );

        return SpreadsheetFormatterSample.with(
            label,
            selector,
            formatter.formatOrEmptyText(
                Optional.of(value),
                context
            )
        );
    }

    private List<SpreadsheetFormatterSample> listFilterNulls(final SpreadsheetFormatterSample ... samples) {
        return Arrays.stream(samples)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    // spreadsheetFormatterInfos........................................................................................

    @Override
    public SpreadsheetFormatterInfoSet spreadsheetFormatterInfos() {
        return INFOS;
    }

    private final static SpreadsheetFormatterInfoSet INFOS = SpreadsheetFormatterInfoSet.with(
        Sets.of(
            spreadsheetFormatterInfo(SpreadsheetFormatterName.AUTOMATIC),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.COLLECTION),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.DATE_FORMAT_PATTERN),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.DATE_TIME_FORMAT_PATTERN),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.DEFAULT_TEXT),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.EXPRESSION),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.GENERAL),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.SPREADSHEET_PATTERN_COLLECTION),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.TEXT_FORMAT_PATTERN),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.TIME_FORMAT_PATTERN)
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
        return "SpreadsheetFormatPattern.spreadsheetFormatter";
    }
}
