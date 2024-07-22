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

package walkingkooka.spreadsheet.parser;

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.UrlPath;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenKind;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternKind;
import walkingkooka.text.cursor.parser.Parser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A {link SpreadsheetParserProvider} that supports creating {@link Parser} for each of the
 * available {@link SpreadsheetParsePattern}.
 */
final class SpreadsheetParsePatternSpreadsheetParserProvider implements SpreadsheetParserProvider {

    static SpreadsheetParsePatternSpreadsheetParserProvider with(final SpreadsheetFormatterProvider spreadsheetFormatterProvider) {
        return new SpreadsheetParsePatternSpreadsheetParserProvider(
                Objects.requireNonNull(spreadsheetFormatterProvider, "spreadsheetFormatterProvider")
        );
    }

    private SpreadsheetParsePatternSpreadsheetParserProvider(final SpreadsheetFormatterProvider spreadsheetFormatterProvider) {
        super();
        this.spreadsheetFormatterProvider = spreadsheetFormatterProvider;
    }

    @Override
    public SpreadsheetParser spreadsheetParser(final SpreadsheetParserSelector selector) {
        Objects.requireNonNull(selector, "selector");

        return selector.spreadsheetParsePattern()
                .map(SpreadsheetParsePattern::parser)
                .orElseThrow(() -> new IllegalArgumentException("Unknown parser " + selector.name()));
    }

    @Override
    public SpreadsheetParser spreadsheetParser(final SpreadsheetParserName name,
                                               final List<?> values) {
        Objects.requireNonNull(name, "name");

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
                return pattern.parser();
            default:
                throw new IllegalArgumentException("Expected 0 values got " + count);
        }
    }

    @Override
    public Optional<SpreadsheetParserSelectorTextComponent> spreadsheetParserNextTextComponent(final SpreadsheetParserSelector selector) {
        Objects.requireNonNull(selector, "selector");

        SpreadsheetParserSelectorTextComponent next;

        final SpreadsheetParserName name = selector.name();
        switch(name.value()) {
            case SpreadsheetParserName.DATE_PARSER_PATTERN_STRING:
                next = spreadsheetParserNextTextComponent(
                        selector,
                        SpreadsheetFormatParserTokenKind::isDate
                );
                break;
            case SpreadsheetParserName.DATE_TIME_PARSER_PATTERN_STRING:
                next = spreadsheetParserNextTextComponent(
                        selector,
                        SpreadsheetFormatParserTokenKind::isDateTime
                );
                break;
            case SpreadsheetParserName.NUMBER_PARSER_PATTERN_STRING:
                next = spreadsheetParserNextTextComponent(
                        selector,
                        SpreadsheetFormatParserTokenKind::isNumber
                );
                break;
            case SpreadsheetParserName.TIME_PARSER_PATTERN_STRING:
                next = spreadsheetParserNextTextComponent(
                        selector,
                        SpreadsheetFormatParserTokenKind::isTime
                );
                break;
            default:
                throw new IllegalArgumentException("Unknown parser " + name);
        }

        return Optional.ofNullable(next);
    }

    private SpreadsheetParserSelectorTextComponent spreadsheetParserNextTextComponent(final SpreadsheetParserSelector selector,
                                                                                      final Predicate<SpreadsheetFormatParserTokenKind> filter) {
        SpreadsheetParserSelectorTextComponent next;

        final String text = selector.text()
                .trim();
        final SpreadsheetPatternKind kind = selector.name()
                .patternKind;
        if (text.isEmpty()) {
            next = SpreadsheetParserSelectorTextComponent.with(
                    "", // label
                    "", // text
                    Arrays.stream(SpreadsheetFormatParserTokenKind.values())
                            .filter(filter)
                            .flatMap(k -> k.alternatives().stream())
                            .distinct()
                            .sorted()
                            .map(t -> SpreadsheetParserSelectorTextComponentAlternative.with(t, t))
                            .collect(Collectors.toList())
            );
        } else {
            final SpreadsheetParsePattern pattern = (SpreadsheetParsePattern)kind.parse(text);
            next = SpreadsheetFormatParserTokenKind.last(
                            pattern.value()
                    ).map(k -> toSpreadsheetParserSelectorTextComponent(kind, k))
                    .orElse(null);
        }

        return next;
    }

    private static SpreadsheetParserSelectorTextComponent toSpreadsheetParserSelectorTextComponent(final SpreadsheetPatternKind kind,
                                                                                                   final SpreadsheetFormatParserTokenKind spreadsheetFormatParserTokenKind) {
        return SpreadsheetParserSelectorTextComponent.with(
                "", // label
                "", // text
                kind.spreadsheetFormatParserTokenKinds()
                        .stream()
                        .filter(k -> false == k.isNextTextComponentIgnored())
                        .filter(k -> null == k || false == spreadsheetFormatParserTokenKind.isDuplicate(k))
                        .flatMap(k -> k.alternatives().stream())
                        .distinct()
                        .sorted()
                        .map(t -> SpreadsheetParserSelectorTextComponentAlternative.with(t, t))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Optional<SpreadsheetFormatterSelector> spreadsheetFormatterSelector(final SpreadsheetParserSelector selector) {
        Objects.requireNonNull(selector, "selector");

        final Optional<SpreadsheetFormatterSelector> spreadsheetFormatterSelector;

        final SpreadsheetParserName name = selector.name();
        switch (name.value()) {
            case SpreadsheetParserName.DATE_PARSER_PATTERN_STRING:
                spreadsheetFormatterSelector = this.spreadsheetFormatterSelector(
                        SpreadsheetFormatterName.DATE_FORMAT_PATTERN,
                        selector.text()
                );
                break;
            case SpreadsheetParserName.DATE_TIME_PARSER_PATTERN_STRING:
                spreadsheetFormatterSelector = this.spreadsheetFormatterSelector(
                        SpreadsheetFormatterName.DATE_TIME_FORMAT_PATTERN,
                        selector.text()
                );
                break;
            case SpreadsheetParserName.NUMBER_PARSER_PATTERN_STRING:
                spreadsheetFormatterSelector = this.spreadsheetFormatterSelector(
                        SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN,
                        selector.text()
                );
                break;
            case SpreadsheetParserName.TIME_PARSER_PATTERN_STRING:
                spreadsheetFormatterSelector = this.spreadsheetFormatterSelector(
                        SpreadsheetFormatterName.TIME_FORMAT_PATTERN,
                        selector.text()
                );
                break;
            default:
                spreadsheetFormatterSelector = Optional.empty();
                break;
        }

        return spreadsheetFormatterSelector;
    }

    private Optional<SpreadsheetFormatterSelector> spreadsheetFormatterSelector(final SpreadsheetFormatterName name,
                                                                                final String text) {
        return SpreadsheetFormatterProviders.spreadsheetFormatPattern()
                .spreadsheetFormatterInfos()
                .stream()
                .filter(i -> i.name().equals(name))
                .map(i -> this.SpreadsheetFormatterSelector(i.url(), text))
                .filter(s -> null != s)
                .findFirst();
    }

    private SpreadsheetFormatterSelector SpreadsheetFormatterSelector(final AbsoluteUrl url,
                                                                      final String text) {
        return this.spreadsheetFormatterProvider.spreadsheetFormatterInfos()
                .stream()
                .filter(sfi -> sfi.url().equals(url))
                .map(sfi -> sfi.name().setText(text))
                .findFirst()
                .orElse(null);
    }

    private final SpreadsheetFormatterProvider spreadsheetFormatterProvider;

    @Override
    public Set<SpreadsheetParserInfo> spreadsheetParserInfos() {
        return INFOS;
    }

    private final static Set<SpreadsheetParserInfo> INFOS = Sets.of(
            spreadsheetParserInfo(SpreadsheetParserName.DATE_PARSER_PATTERN),
            spreadsheetParserInfo(SpreadsheetParserName.DATE_TIME_PARSER_PATTERN),
            spreadsheetParserInfo(SpreadsheetParserName.NUMBER_PARSER_PATTERN),
            spreadsheetParserInfo(SpreadsheetParserName.TIME_PARSER_PATTERN)
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
        return "SpreadsheetPattern.parser";
    }
}
