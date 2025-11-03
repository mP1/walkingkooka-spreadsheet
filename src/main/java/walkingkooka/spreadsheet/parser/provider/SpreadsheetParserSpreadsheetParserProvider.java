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

package walkingkooka.spreadsheet.parser.provider;

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.UrlPath;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenKind;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternKind;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterName;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.text.cursor.parser.Parser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A {link SpreadsheetParserProvider} that supports creating {@link Parser} for each of the
 * available {@link SpreadsheetParsePattern}.
 */
final class SpreadsheetParserSpreadsheetParserProvider implements SpreadsheetParserProvider {

    static SpreadsheetParserSpreadsheetParserProvider with(final SpreadsheetFormatterProvider spreadsheetFormatterProvider) {
        return new SpreadsheetParserSpreadsheetParserProvider(
            Objects.requireNonNull(spreadsheetFormatterProvider, "spreadsheetFormatterProvider")
        );
    }

    private SpreadsheetParserSpreadsheetParserProvider(final SpreadsheetFormatterProvider spreadsheetFormatterProvider) {
        super();
        this.spreadsheetFormatterProvider = spreadsheetFormatterProvider;
    }

    @Override
    public SpreadsheetParser spreadsheetParser(final SpreadsheetParserSelector selector,
                                               final ProviderContext context) {
        Objects.requireNonNull(selector, "selector");
        Objects.requireNonNull(context, "context");

        final SpreadsheetParser parser;
        final SpreadsheetParserName name = selector.name();

        switch (name.value()) {
            case SpreadsheetParserName.GENERAL_STRING:
            case SpreadsheetParserName.WHOLE_NUMBER_STRING:
                parser = selector.evaluateValueText(
                    this,
                    context
                );
                break;
            default:
                parser = selector.spreadsheetParsePattern()
                    .map(SpreadsheetParsePattern::parser)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown parser " + name));
                break;
        }

        return parser;
    }

    @Override
    public SpreadsheetParser spreadsheetParser(final SpreadsheetParserName name,
                                               final List<?> values,
                                               final ProviderContext context) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(values, "values");
        Objects.requireNonNull(context, "context");

        final SpreadsheetParser parser;

        switch (name.value()) {
            case SpreadsheetParserName.GENERAL_STRING:
                parser = SpreadsheetParsers.general();
                break;
            case SpreadsheetParserName.WHOLE_NUMBER_STRING:
                parser = SpreadsheetParsers.wholeNumber();
                break;
            default:
                final SpreadsheetPatternKind kind = name.patternKind;
                if (null == kind) {
                    throw new IllegalArgumentException("Unknown parser " + name);
                }

                final List<?> copy = Lists.immutable(values);
                final int count = copy.size();
                switch (count) {
                    case 1:
                        final SpreadsheetParsePattern pattern = (SpreadsheetParsePattern) kind.parse(
                            (String) copy.get(0)
                        );
                        parser = pattern.parser();
                        break;
                    default:
                        throw new IllegalArgumentException("Expected 0 values got " + count);
                }
                break;
        }

        return parser;
    }

    @Override
    public Optional<SpreadsheetParserSelectorToken> spreadsheetParserNextToken(final SpreadsheetParserSelector selector) {
        Objects.requireNonNull(selector, "selector");

        SpreadsheetParserSelectorToken next;

        final SpreadsheetParserName name = selector.name();
        switch (name.value()) {
            case SpreadsheetParserName.DATE_STRING:
                next = spreadsheetParserNextToken(
                    selector,
                    SpreadsheetFormatParserTokenKind::isDate
                );
                break;
            case SpreadsheetParserName.DATE_TIME_STRING:
                next = spreadsheetParserNextToken(
                    selector,
                    SpreadsheetFormatParserTokenKind::isDateTime
                );
                break;
            case SpreadsheetParserName.GENERAL_STRING:
                next = null;
                break;
            case SpreadsheetParserName.NUMBER_PARSER_PATTERN_STRING:
                next = spreadsheetParserNextToken(
                    selector,
                    SpreadsheetFormatParserTokenKind::isNumber
                );
                break;
            case SpreadsheetParserName.TIME_STRING:
                next = spreadsheetParserNextToken(
                    selector,
                    SpreadsheetFormatParserTokenKind::isTime
                );
                break;
            case SpreadsheetParserName.WHOLE_NUMBER_STRING:
                next = null;
                break;
            default:
                throw new IllegalArgumentException("Unknown parser " + name);
        }

        return Optional.ofNullable(next);
    }

    private SpreadsheetParserSelectorToken spreadsheetParserNextToken(final SpreadsheetParserSelector selector,
                                                                      final Predicate<SpreadsheetFormatParserTokenKind> filter) {
        SpreadsheetParserSelectorToken next;

        final String text = selector.valueText()
            .trim();
        final SpreadsheetPatternKind kind = selector.name()
            .patternKind;
        if (text.isEmpty()) {
            next = SpreadsheetParserSelectorToken.with(
                "", // label
                "", // text
                Arrays.stream(SpreadsheetFormatParserTokenKind.values())
                    .filter(filter)
                    .flatMap(k -> k.alternatives().stream())
                    .distinct()
                    .sorted()
                    .map(t -> SpreadsheetParserSelectorTokenAlternative.with(t, t))
                    .collect(Collectors.toList())
            );
        } else {
            final SpreadsheetParsePattern pattern = (SpreadsheetParsePattern) kind.parse(text);
            next = SpreadsheetFormatParserTokenKind.last(
                    pattern.value()
                ).map(k -> toSpreadsheetParserSelectorToken(kind, k))
                .orElse(null);
        }

        return next;
    }

    private static SpreadsheetParserSelectorToken toSpreadsheetParserSelectorToken(final SpreadsheetPatternKind kind,
                                                                                   final SpreadsheetFormatParserTokenKind spreadsheetFormatParserTokenKind) {
        return SpreadsheetParserSelectorToken.with(
            "", // label
            "", // text
            kind.spreadsheetFormatParserTokenKinds()
                .stream()
                .filter(k -> false == k.isNextTokenIgnored())
                .filter(k -> null == k || false == spreadsheetFormatParserTokenKind.isDuplicate(k))
                .flatMap(k -> k.alternatives().stream())
                .distinct()
                .sorted()
                .map(t -> SpreadsheetParserSelectorTokenAlternative.with(t, t))
                .collect(Collectors.toList())
        );
    }

    @Override
    public Optional<SpreadsheetFormatterSelector> spreadsheetFormatterSelector(final SpreadsheetParserSelector selector) {
        Objects.requireNonNull(selector, "selector");

        final Optional<SpreadsheetFormatterSelector> spreadsheetFormatterSelector;

        final SpreadsheetParserName name = selector.name();
        switch (name.value()) {
            case SpreadsheetParserName.DATE_STRING:
                spreadsheetFormatterSelector = this.spreadsheetFormatterSelector(
                    SpreadsheetFormatterName.DATE,
                    selector.valueText()
                );
                break;
            case SpreadsheetParserName.DATE_TIME_STRING:
                spreadsheetFormatterSelector = this.spreadsheetFormatterSelector(
                    SpreadsheetFormatterName.DATE_TIME,
                    selector.valueText()
                );
                break;
            case SpreadsheetParserName.GENERAL_STRING:
                spreadsheetFormatterSelector = this.spreadsheetFormatterSelector(
                    SpreadsheetFormatterName.GENERAL,
                    selector.valueText()
                );
                break;
            case SpreadsheetParserName.NUMBER_PARSER_PATTERN_STRING:
                spreadsheetFormatterSelector = this.spreadsheetFormatterSelector(
                    SpreadsheetFormatterName.NUMBER,
                    selector.valueText()
                );
                break;
            case SpreadsheetParserName.TIME_STRING:
                spreadsheetFormatterSelector = this.spreadsheetFormatterSelector(
                    SpreadsheetFormatterName.TIME,
                    selector.valueText()
                );
                break;
            case SpreadsheetParserName.WHOLE_NUMBER_STRING:
                spreadsheetFormatterSelector = Optional.empty();
                break;
            default:
                spreadsheetFormatterSelector = Optional.empty();
                break;
        }

        return spreadsheetFormatterSelector;
    }

    private Optional<SpreadsheetFormatterSelector> spreadsheetFormatterSelector(final SpreadsheetFormatterName name,
                                                                                final String text) {
        return FORMAT_PATTERN_SPREADSHEET_FORMATTER_PROVIDER.spreadsheetFormatterInfos()
            .stream()
            .filter(i -> i.name().equals(name))
            .map(i -> this.SpreadsheetFormatterSelector(i.url(), text))
            .filter(Objects::nonNull)
            .findFirst();
    }

    private final static SpreadsheetFormatterProvider FORMAT_PATTERN_SPREADSHEET_FORMATTER_PROVIDER = SpreadsheetFormatterProviders.spreadsheetFormatters();

    private SpreadsheetFormatterSelector SpreadsheetFormatterSelector(final AbsoluteUrl url,
                                                                      final String text) {
        return this.spreadsheetFormatterProvider.spreadsheetFormatterInfos()
            .stream()
            .filter(sfi -> sfi.url().equals(url))
            .map(sfi -> sfi.name().setValueText(text))
            .findFirst()
            .orElse(null);
    }

    private final SpreadsheetFormatterProvider spreadsheetFormatterProvider;

    @Override
    public SpreadsheetParserInfoSet spreadsheetParserInfos() {
        return INFOS;
    }

    private final static SpreadsheetParserInfoSet INFOS = SpreadsheetParserInfoSet.with(
        Sets.of(
            spreadsheetParserInfo(SpreadsheetParserName.DATE),
            spreadsheetParserInfo(SpreadsheetParserName.DATE_TIME),
            spreadsheetParserInfo(SpreadsheetParserName.GENERAL),
            spreadsheetParserInfo(SpreadsheetParserName.NUMBER),
            spreadsheetParserInfo(SpreadsheetParserName.TIME),
            spreadsheetParserInfo(SpreadsheetParserName.WHOLE_NUMBER)
        )
    );

    private static SpreadsheetParserInfo spreadsheetParserInfo(final SpreadsheetParserName name) {
        return SpreadsheetParserInfo.with(
            SpreadsheetParserProviders.BASE_URL.appendPath(UrlPath.parse(name.value())),
            name
        );
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
