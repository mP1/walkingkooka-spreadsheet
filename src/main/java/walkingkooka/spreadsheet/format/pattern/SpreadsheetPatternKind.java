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
import walkingkooka.collect.set.Sets;
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.spreadsheet.SpreadsheetUrlFragments;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.spreadsheet.format.SpreadsheetPatternSpreadsheetFormatter;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenKind;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.text.CaseKind;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.parser.ParserToken;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The different types of {@link SpreadsheetPattern}. While this kind is named after a {@link SpreadsheetPattern} it is also used to
 * identify {@link SpreadsheetFormatter} or {@link walkingkooka.text.cursor.parser.Parser} for each of the spreadsheet value types.
 */
public enum SpreadsheetPatternKind implements HasUrlFragment {
    DATE_FORMAT_PATTERN(
        SpreadsheetPattern::parseDateFormatPattern,
        SpreadsheetPattern::dateFormatPattern,
        SpreadsheetFormatParserTokenKind::isDateFormat,
        "date"
    ),

    DATE_PARSE_PATTERN(
        SpreadsheetPattern::parseDateParsePattern,
        SpreadsheetPattern::dateParsePattern,
        SpreadsheetFormatParserTokenKind::isDateParse,
        "date"
    ),

    DATE_TIME_FORMAT_PATTERN(
        SpreadsheetPattern::parseDateTimeFormatPattern,
        SpreadsheetPattern::dateTimeFormatPattern,
        SpreadsheetFormatParserTokenKind::isDateTimeFormat,
        "date-time"
    ),

    DATE_TIME_PARSE_PATTERN(
        SpreadsheetPattern::parseDateTimeParsePattern,
        SpreadsheetPattern::dateTimeParsePattern,
        SpreadsheetFormatParserTokenKind::isDateTimeParse,
        "date-time"
    ),

    NUMBER_FORMAT_PATTERN(
        SpreadsheetPattern::parseNumberFormatPattern,
        SpreadsheetPattern::numberFormatPattern,
        SpreadsheetFormatParserTokenKind::isNumberFormat,
        "number"
    ),

    NUMBER_PARSE_PATTERN(
        SpreadsheetPattern::parseNumberParsePattern,
        SpreadsheetPattern::numberParsePattern,
        SpreadsheetFormatParserTokenKind::isNumberParse,
        "number"
    ),

    TEXT_FORMAT_PATTERN(
        SpreadsheetPattern::parseTextFormatPattern,
        SpreadsheetPattern::textFormatPattern,
        SpreadsheetFormatParserTokenKind::isTextFormat,
        "text"
    ),

    TIME_FORMAT_PATTERN(
        SpreadsheetPattern::parseTimeFormatPattern,
        SpreadsheetPattern::timeFormatPattern,
        SpreadsheetFormatParserTokenKind::isTimeFormat,
        "time"
    ),

    TIME_PARSE_PATTERN(
        SpreadsheetPattern::parseTimeParsePattern,
        SpreadsheetPattern::timeParsePattern,
        SpreadsheetFormatParserTokenKind::isTimeParse,
        "time"
    );

    SpreadsheetPatternKind(final Function<String, SpreadsheetPattern> parser,
                           final Function<ParserToken, SpreadsheetPattern> tokenToPattern,
                           final Predicate<SpreadsheetFormatParserTokenKind> formatParserTokenKind,
                           final String urlFragmentSuffix) {
        this.parser = parser;
        this.tokenToPattern = tokenToPattern;
        this.spreadsheetFormatParserTokenKinds = Sets.of(
            Arrays.stream(SpreadsheetFormatParserTokenKind.values())
                .filter(formatParserTokenKind)
                .toArray(SpreadsheetFormatParserTokenKind[]::new)
        );


        this.typeName = "spreadsheet-" + CaseKind.SNAKE.change(
            this.name(),
            CaseKind.KEBAB
        );

        // eg time, text
        this.urlFragment =
            (
                this.isFormatPattern() ?
                    SpreadsheetUrlFragments.FORMATTER :
                    SpreadsheetUrlFragments.PARSER
            )
                .append(UrlFragment.SLASH)
                .append(
                    UrlFragment.with(urlFragmentSuffix)
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
    public SpreadsheetPatternSpreadsheetFormatter formatter(final Locale locale) {
        Objects.requireNonNull(locale, "locale");

        final SpreadsheetPatternSpreadsheetFormatter formatter;

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
                formatter = SpreadsheetFormatters.defaultText();
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
        SpreadsheetMetadataPropertyName<?> name;

        // must be a switch and not a field to avoid weird cycles between SpreadsheetMetadataPropertyName and this enum.
        switch (this) {
            case DATE_FORMAT_PATTERN:
                name = SpreadsheetMetadataPropertyName.DATE_FORMATTER;
                break;
            case DATE_PARSE_PATTERN:
                name = SpreadsheetMetadataPropertyName.DATE_PARSER;
                break;
            case DATE_TIME_FORMAT_PATTERN:
                name = SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER;
                break;
            case DATE_TIME_PARSE_PATTERN:
                name = SpreadsheetMetadataPropertyName.DATE_TIME_PARSER;
                break;
            case NUMBER_FORMAT_PATTERN:
                name = SpreadsheetMetadataPropertyName.NUMBER_FORMATTER;
                break;
            case NUMBER_PARSE_PATTERN:
                name = SpreadsheetMetadataPropertyName.NUMBER_PARSER;
                break;
            case TEXT_FORMAT_PATTERN:
                name = SpreadsheetMetadataPropertyName.TEXT_FORMATTER;
                break;
            case TIME_FORMAT_PATTERN:
                name = SpreadsheetMetadataPropertyName.TIME_FORMATTER;
                break;
            case TIME_PARSE_PATTERN:
                name = SpreadsheetMetadataPropertyName.TIME_PARSER;
                break;
            default:
                NeverError.unhandledCase(this, values());
                name = null;
                break;
        }

        return name;
    }

    /**
     * Returns the equivalent {@link SpreadsheetPatternKind}. If this a {@link #isFormatPattern()} it will return itself.
     */
    public SpreadsheetPatternKind toFormat() {
        final SpreadsheetPatternKind kind;

        switch (this) {
            case DATE_FORMAT_PATTERN:
            case DATE_PARSE_PATTERN:
                kind = DATE_FORMAT_PATTERN;
                break;
            case DATE_TIME_FORMAT_PATTERN:
            case DATE_TIME_PARSE_PATTERN:
                kind = DATE_TIME_FORMAT_PATTERN;
                break;
            case NUMBER_FORMAT_PATTERN:
            case NUMBER_PARSE_PATTERN:
                kind = NUMBER_FORMAT_PATTERN;
                break;
            case TEXT_FORMAT_PATTERN:
                kind = this;
                break;
            case TIME_FORMAT_PATTERN:
            case TIME_PARSE_PATTERN:
                kind = TIME_FORMAT_PATTERN;
                break;
            default:
                kind = NeverError.unhandledCase(
                    this,
                    values()
                );
                break;
        }

        return kind;
    }

    /**
     * Returns the equivalent {@link SpreadsheetPatternKind}. If this a {@link #isFormatPattern()} it will return itself.
     * Note unlike {@link #toFormat()} this returns an {@link Optional} because there is no parse equivalent of {@link #TEXT_FORMAT_PATTERN}.
     */
    public Optional<SpreadsheetPatternKind> toParse() {
        final SpreadsheetPatternKind kind;

        switch (this) {
            case DATE_FORMAT_PATTERN:
            case DATE_PARSE_PATTERN:
                kind = DATE_PARSE_PATTERN;
                break;
            case DATE_TIME_FORMAT_PATTERN:
            case DATE_TIME_PARSE_PATTERN:
                kind = DATE_TIME_PARSE_PATTERN;
                break;
            case NUMBER_FORMAT_PATTERN:
            case NUMBER_PARSE_PATTERN:
                kind = NUMBER_PARSE_PATTERN;
                break;
            case TEXT_FORMAT_PATTERN:
                kind = null;
                break;
            case TIME_FORMAT_PATTERN:
            case TIME_PARSE_PATTERN:
                kind = TIME_PARSE_PATTERN;
                break;
            default:
                kind = NeverError.unhandledCase(
                    this,
                    values()
                );
                break;
        }

        return Optional.ofNullable(kind);
    }

    @Override
    public UrlFragment urlFragment() {
        return this.urlFragment;
    }

    private final UrlFragment urlFragment;

    /**
     * Returns true if this {@link SpreadsheetPatternKind} identifies a pattern that is a subclass of {@link SpreadsheetFormatPattern}.
     */
    public boolean isFormatPattern() {
        return this.name().contains("FORMAT");
    }

    /**
     * Returns true if this {@link SpreadsheetPatternKind} identifies a pattern that is a subclass of {@link SpreadsheetParsePattern}.
     */
    public boolean isParsePattern() {
        return false == this.isFormatPattern();
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
