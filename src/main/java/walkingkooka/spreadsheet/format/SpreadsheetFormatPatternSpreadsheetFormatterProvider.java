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
import walkingkooka.net.UrlPath;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenKind;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternKind;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A {link SpreadsheetFormatterProvider} that supports creating {@link SpreadsheetFormatter} for each of the
 * available {@link walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern}.
 */
final class SpreadsheetFormatPatternSpreadsheetFormatterProvider implements SpreadsheetFormatterProvider {

    final static SpreadsheetFormatPatternSpreadsheetFormatterProvider INSTANCE = new SpreadsheetFormatPatternSpreadsheetFormatterProvider();

    private SpreadsheetFormatPatternSpreadsheetFormatterProvider() {
        super();
    }

    @Override
    public SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterSelector selector) {
        Objects.requireNonNull(selector, "selector");

        final SpreadsheetFormatter formatter;

        final SpreadsheetFormatterName name = selector.name();
        switch (name.value()) {
            case SpreadsheetFormatterName.AUTOMATIC_STRING:
            case SpreadsheetFormatterName.COLLECTION_STRING:
            case SpreadsheetFormatterName.GENERAL_STRING:
                formatter = selector.evaluateText(this);
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
                                                     final List<?> values) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(values, "values");

        final List<?> copy = Lists.immutable(values);
        final int count = copy.size();

        final SpreadsheetFormatter formatter;

        switch (name.value()) {
            case SpreadsheetFormatterName.AUTOMATIC_STRING:
                switch (count) {
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
                        throw new IllegalArgumentException("Expected 5 value(s) got " + count);
                }
                break;
            case SpreadsheetFormatterName.COLLECTION_STRING:
                switch (count) {
                    case 5:
                        formatter = SpreadsheetFormatters.collection(
                                values.stream()
                                        .map(c -> (SpreadsheetFormatter)c)
                                        .collect(Collectors.toList())
                        );
                        break;
                    default:
                        throw new IllegalArgumentException("Expected 5 value(s) got " + count);
                }
                break;
            case SpreadsheetFormatterName.GENERAL_STRING:
                if (0 != count) {
                    throw new IllegalArgumentException("Expected 0 value(s) got " + count);
                }
                formatter = SpreadsheetFormatters.general();
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

    @Override
    public Optional<SpreadsheetFormatterSelectorTextComponent> spreadsheetFormatterNextTextComponent(final SpreadsheetFormatterSelector selector) {
        Objects.requireNonNull(selector, "selector");

        SpreadsheetFormatterSelectorTextComponent next;

        final SpreadsheetFormatterName name = selector.name();
        switch(name.value()) {
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
            case SpreadsheetFormatterName.GENERAL_STRING:
                next = null;
                break;
            case SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN_STRING:
                next = formatPatternNextTextComponent(
                        selector,
                        SpreadsheetFormatParserTokenKind::isNumber
                );
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

    private SpreadsheetFormatterSelectorTextComponent formatPatternNextTextComponent(final SpreadsheetFormatterSelector selector,
                                                                                     final Predicate<SpreadsheetFormatParserTokenKind> filter) {
        SpreadsheetFormatterSelectorTextComponent next;

        final String text = selector.text()
                .trim();
        final SpreadsheetPatternKind kind = selector.name()
                .patternKind;
        if (text.isEmpty()) {
            next = SpreadsheetFormatterSelectorTextComponent.with(
                    "", // label
                    "", // text
                    Arrays.stream(SpreadsheetFormatParserTokenKind.values())
                            .filter(filter)
                            .flatMap(k -> k.alternatives().stream())
                            .distinct()
                            .sorted()
                            .map(t -> SpreadsheetFormatterSelectorTextComponentAlternative.with(t, t))
                            .collect(Collectors.toList())
            );
        } else {
            final SpreadsheetFormatPattern formatPattern = kind.parse(text)
                    .toFormat();
            next = SpreadsheetFormatterSelectorTextComponent.nextTextComponent(
                    formatPattern.value(),
                    kind
            );
        }

        return next;
    }

    @Override
    public Set<SpreadsheetFormatterInfo> spreadsheetFormatterInfos() {
        return INFOS;
    }

    private final static Set<SpreadsheetFormatterInfo> INFOS = Sets.of(
            spreadsheetFormatterInfo(SpreadsheetFormatterName.AUTOMATIC),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.COLLECTION),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.DATE_FORMAT_PATTERN),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.DATE_TIME_FORMAT_PATTERN),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.GENERAL),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.TEXT_FORMAT_PATTERN),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.TIME_FORMAT_PATTERN)
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
