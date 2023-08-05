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

package walkingkooka.spreadsheet.format.pattern;

import walkingkooka.NeverError;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.spreadsheet.SpreadsheetUrlFragments;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenKind;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.text.CaseKind;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The different types of {@link SpreadsheetPattern}.
 */
public enum SpreadsheetPatternKind implements HasUrlFragment {
    DATE_FORMAT_PATTERN(
            SpreadsheetPattern::parseDateFormatPattern,
            SpreadsheetPattern::dateFormatPattern,
            SpreadsheetFormatParserTokenKind::isDateFormat
    ),

    DATE_PARSE_PATTERN(
            SpreadsheetPattern::parseDateParsePattern,
            SpreadsheetPattern::dateParsePattern,
            SpreadsheetFormatParserTokenKind::isDateParse
    ),

    DATE_TIME_FORMAT_PATTERN(
            SpreadsheetPattern::parseDateTimeFormatPattern,
            SpreadsheetPattern::dateTimeFormatPattern,
            SpreadsheetFormatParserTokenKind::isDateTimeFormat
    ),

    DATE_TIME_PARSE_PATTERN(
            SpreadsheetPattern::parseDateTimeParsePattern,
            SpreadsheetPattern::dateTimeParsePattern,
            SpreadsheetFormatParserTokenKind::isDateTimeParse
    ),

    NUMBER_FORMAT_PATTERN(
            SpreadsheetPattern::parseNumberFormatPattern,
            SpreadsheetPattern::numberFormatPattern,
            SpreadsheetFormatParserTokenKind::isNumberFormat
    ),

    NUMBER_PARSE_PATTERN(
            SpreadsheetPattern::parseNumberParsePattern,
            SpreadsheetPattern::numberParsePattern,
            SpreadsheetFormatParserTokenKind::isNumberParse
    ),

    TEXT_FORMAT_PATTERN(
            SpreadsheetPattern::parseTextFormatPattern,
            SpreadsheetPattern::textFormatPattern,
            SpreadsheetFormatParserTokenKind::isTextFormat
    ),

    TIME_FORMAT_PATTERN(
            SpreadsheetPattern::parseTimeFormatPattern,
            SpreadsheetPattern::timeFormatPattern,
            SpreadsheetFormatParserTokenKind::isTimeFormat
    ),

    TIME_PARSE_PATTERN(
            SpreadsheetPattern::parseTimeParsePattern,
            SpreadsheetPattern::timeParsePattern,
            SpreadsheetFormatParserTokenKind::isTimeParse
    );

    SpreadsheetPatternKind(final Function<String, SpreadsheetPattern> parser,
                           final Function<ParserToken, SpreadsheetPattern> tokenToPattern,
                           final Predicate<SpreadsheetFormatParserTokenKind> formatParserTokenKind) {
        this.parser = parser;
        this.tokenToPattern = tokenToPattern;
        this.spreadsheetFormatParserTokenKinds = Sets.of(
                Arrays.stream(SpreadsheetFormatParserTokenKind.values())
                        .filter(formatParserTokenKind)
                        .toArray(SpreadsheetFormatParserTokenKind[]::new)
        );

        final String nameLowerKebab = CaseKind.SNAKE.change(
                this.name(),
                CaseKind.KEBAB
        );
        this.typeName = "spreadsheet-" + nameLowerKebab;

        this.urlFragment =
                SpreadsheetUrlFragments.PATTERN
                        .append(UrlFragment.SLASH)
                        .append(
                                UrlFragment.with(
                                        CharSequences.subSequence(
                                                nameLowerKebab,
                                                0,
                                                -"_PATTERN".length()
                                        ).toString()
                                )
                        );
    }

    /**
     * Parses the given {@link String pattern} into a {@link SpreadsheetPattern} that matches this enum.
     */
    public SpreadsheetPattern parse(final String pattern) {
        return this.parser.apply(pattern);
    }

    private final Function<String, SpreadsheetPattern> parser;

    /**
     * Accepts a {@link ParserToken} and returns the wrapping {@link SpreadsheetPattern}.
     */
    public SpreadsheetPattern pattern(final ParserToken token) {
        return this.tokenToPattern.apply(token);
    }

    private final Function<ParserToken, SpreadsheetPattern> tokenToPattern;

    public Set<SpreadsheetFormatParserTokenKind> spreadsheetFormatParserTokenKinds() {
        return this.spreadsheetFormatParserTokenKinds;
    }

    private final Set<SpreadsheetFormatParserTokenKind> spreadsheetFormatParserTokenKinds;

    /**
     * Returns a {@link SpreadsheetFormatter} that uses the default pattern for the given {@link Locale}.
     */
    public SpreadsheetFormatter formatter(final Locale locale) {
        Objects.requireNonNull(locale, "locale");

        final SpreadsheetFormatter formatter;

        switch (this) {
            case DATE_FORMAT_PATTERN:
            case DATE_PARSE_PATTERN:
                formatter = SpreadsheetPattern.dateFormatPatternLocale(locale)
                        .formatter();
                break;
            case DATE_TIME_FORMAT_PATTERN:
            case DATE_TIME_PARSE_PATTERN:
                formatter = SpreadsheetPattern.dateTimeFormatPatternLocale(locale)
                        .formatter();
                break;
            case NUMBER_FORMAT_PATTERN:
            case NUMBER_PARSE_PATTERN:
                formatter = SpreadsheetPattern.decimalFormat(
                                (DecimalFormat)
                                        DecimalFormat.getInstance(locale)
                        ).toFormat()
                        .formatter();
                break;
            case TEXT_FORMAT_PATTERN:
                formatter = SpreadsheetFormatters.text(
                        SpreadsheetFormatParserToken.text(
                                Lists.of(
                                        SpreadsheetFormatParserToken.textPlaceholder(
                                                "@",
                                                "@"
                                        )
                                ),
                                "@"
                        )
                );
                break;
            case TIME_FORMAT_PATTERN:
            case TIME_PARSE_PATTERN:
                formatter = SpreadsheetPattern.timeParsePatternLocale(locale)
                        .toFormat()
                        .formatter();
                break;
            default:
                NeverError.unhandledCase(this, values());
                formatter = null;
                break;
        }

        return formatter;
    }

    /**
     * This is the corresponding type name that appears in JSON for each pattern.
     */
    public String typeName() {
        return this.typeName;
    }

    private final String typeName;

    /**
     * Returns the {@link SpreadsheetMetadataPropertyName} for this {@link SpreadsheetPatternKind}.
     */
    public SpreadsheetMetadataPropertyName<?> spreadsheetMetadataPropertyName() {
        return SpreadsheetMetadataPropertyName.with(
                this.typeName()
                        .substring("spreadsheet-".length())
        );
    }

    @Override
    public UrlFragment urlFragment() {
        return this.urlFragment;
    }

    private final UrlFragment urlFragment;

    /**
     * Returns true if this {@link SpreadsheetPatternKind} identifies a pattern that is a sub-class of {@link SpreadsheetFormatPattern}.
     */
    public boolean isFormatPattern() {
        return this.name().contains("FORMAT");
    }

    /**
     * Returns true if this {@link SpreadsheetPatternKind} identifies a pattern that is a sub-class of {@link SpreadsheetParsePattern}.
     */
    public boolean isParsePattern() {
        return false == this.isFormatPattern();
    }

    /**
     * Checks and throws a {@link IllegalArgumentException} if the {@link SpreadsheetPattern#kind()} is different to this.
     * A null {@link SpreadsheetPattern} parameter will never fail.
     */
    public void checkSameOrFail(final SpreadsheetPattern pattern) {
        if (null != pattern) {
            final SpreadsheetPatternKind kind = pattern.kind();
            if (this != kind) {
                throw new IllegalArgumentException("Pattern " + pattern + " is not a " + kind + ".");
            }
        }
    }

    /**
     * Factory that creates a {@link JsonNode} patch for the given {@link SpreadsheetPattern}.
     */
    public JsonNode patternPatch(final SpreadsheetPattern pattern,
                                 final JsonNodeMarshallContext context) {
        this.checkSameOrFail(pattern);
        return this.isFormatPattern() ?
                SpreadsheetDelta.formatPatternPatch(
                        (SpreadsheetFormatPattern) pattern,
                        context
                ) :
                SpreadsheetDelta.parsePatternPatch(
                        (SpreadsheetParsePattern) pattern,
                        context
                );
    }

    /**
     * Tries to find the matching {@link SpreadsheetPatternKind} given its {@link SpreadsheetPatternKind#typeName()}
     */
    public static SpreadsheetPatternKind fromTypeName(final String typeName) {
        Objects.requireNonNull(typeName, "typeName");

        return Arrays.stream(values())
                .filter(e -> e.typeName().equals(typeName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown typeName " + CharSequences.quoteAndEscape(typeName)));

    }
}
