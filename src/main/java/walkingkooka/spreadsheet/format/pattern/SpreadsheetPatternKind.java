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

import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.spreadsheet.SpreadsheetUrlFragments;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

/**
 * The different types of {@link SpreadsheetPattern}.
 */
public enum SpreadsheetPatternKind implements HasUrlFragment {
    DATE_FORMAT_PATTERN(SpreadsheetPattern::parseDateFormatPattern),

    DATE_PARSE_PATTERN(SpreadsheetPattern::parseDateParsePattern),

    DATE_TIME_FORMAT_PATTERN(SpreadsheetPattern::parseDateTimeFormatPattern),

    DATE_TIME_PARSE_PATTERN(SpreadsheetPattern::parseDateTimeParsePattern),

    NUMBER_FORMAT_PATTERN(SpreadsheetPattern::parseNumberFormatPattern),

    NUMBER_PARSE_PATTERN(SpreadsheetPattern::parseNumberParsePattern),

    TEXT_FORMAT_PATTERN(SpreadsheetPattern::parseTextFormatPattern),

    TIME_FORMAT_PATTERN(SpreadsheetPattern::parseTimeFormatPattern),

    TIME_PARSE_PATTERN(SpreadsheetPattern::parseTimeParsePattern);

    SpreadsheetPatternKind(final Function<String, SpreadsheetPattern> parser) {
        final String name = this.name()
                .toLowerCase();

        this.typeName =
                "spreadsheet-" +
                        name.replace('_', '-');

        this.urlFragment =
                SpreadsheetUrlFragments.PATTERN
                        .append(UrlFragment.SLASH)
                        .append(
                                UrlFragment.with(
                                        CharSequences.subSequence(
                                                        typeName,
                                                        "spreadsheet-".length(),
                                                        -"_PATTERN".length()
                                                ).toString()
                                                .replace('_', '-')
                                )
                        );

        this.parser = parser;
    }

    /**
     * Parses the given {@link String pattern} into a {@link SpreadsheetPattern} that matches this enum.
     */
    public SpreadsheetPattern parse(final String pattern) {
        return this.parser.apply(pattern);
    }

    private final Function<String, SpreadsheetPattern> parser;

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
     * Returns true if this {@link SpreadsheetPatternKind} is sub-class of {@link SpreadsheetFormatPattern}.
     */
    public boolean isFormatPattern() {
        return this.name().contains("FORMAT");
    }

    /**
     * Checks and throws a {@link IllegalArgumentException} if the {@link SpreadsheetPattern#kind()} is different to this.
     */
    public void check(final SpreadsheetPattern pattern) {
        Objects.requireNonNull(pattern, "pattern");
        final SpreadsheetPatternKind kind = pattern.kind();
        if (this != kind) {
            throw new IllegalArgumentException("Pattern " + pattern + " is not a " + kind + ".");
        }
    }

    /**
     * Factory that creates a {@link JsonNode} patch for the given {@link SpreadsheetPattern}.
     */
    public JsonNode patternPatch(final SpreadsheetPattern pattern,
                                 final JsonNodeMarshallContext context) {
        this.check(pattern);
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
