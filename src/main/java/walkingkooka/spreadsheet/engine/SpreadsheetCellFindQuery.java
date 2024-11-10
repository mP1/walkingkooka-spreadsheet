/*
 * Copyright 2023 Miroslav Pokorny (github.com/mP1)
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

package walkingkooka.spreadsheet.engine;

import walkingkooka.CanBeEmpty;
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.UrlQueryString;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.spreadsheet.meta.SpreadsheetCellQuery;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReferencePath;
import walkingkooka.text.CaseKind;
import walkingkooka.text.CharSequences;
import walkingkooka.text.HasText;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserContexts;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.cursor.parser.StringParserToken;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * Captures the parameter values of a cell find.
 */
public final class SpreadsheetCellFindQuery implements HasUrlFragment,
        CanBeEmpty,
        HasText {

    /**
     * Parses the given text into a {@link SpreadsheetCellFindQuery}.
     */
    public static SpreadsheetCellFindQuery parse(final String text) {
        final TextCursor cursor = TextCursors.charSequence(text);

        SpreadsheetCellFindQuery query = SpreadsheetCellFindQuery.empty();

        String component = parseComponentOrNull(cursor);
        if (null != component) {
            if ("path".equals(component)) {
                query = query.setPath(
                        parseComponent(cursor)
                                .map(SpreadsheetCellRangeReferencePath::fromKebabCase)
                );

                component = parseComponentOrNull(cursor);
            }
            if ("offset".equals(component)) {
                query = query.setOffset(
                        parseInt(
                                cursor,
                                "offset"
                        )
                );
                component = parseComponentOrNull(cursor);
            }
            if ("max".equals(component)) {
                query = query.setMax(
                        parseInt(
                                cursor,
                                "max"
                        )
                );
                component = parseComponentOrNull(cursor);
            }
            if ("value-type".equals(component)) {
                query = query.setValueType(
                        parseComponent(cursor)
                );
                component = parseComponentOrNull(cursor);
            }
            if ("query".equals(component)) {
                cursor.next();

                final TextCursorSavePoint save = cursor.save();
                cursor.end();

                final String queryText = save.textBetween()
                        .toString();

                query = query.setQuery(
                        queryText.isEmpty() ?
                                Optional.empty() :
                                Optional.of(
                                        SpreadsheetCellQuery.parse(queryText)
                                )
                );
            }
        }

        if(false == cursor.isEmpty()) {
            throw new IllegalArgumentException("Invalid query got " + CharSequences.quoteAndEscape(text));
        }

        return query;
    }

    private static OptionalInt parseInt(final TextCursor cursor,
                                        final String label) {
        return parseComponent(cursor)
                .map(
                        (final String t) -> {
                            try {
                                return OptionalInt.of(
                                        Integer.parseInt(t)
                                );
                            } catch (final NumberFormatException cause) {
                                throw new IllegalArgumentException("Invalid " + label + " got " + CharSequences.quoteAndEscape(t));
                            }
                        }
                ).orElseGet(
                        OptionalInt::empty
                );
    }

    private static String parseComponentOrNull(final TextCursor cursor) {
        return parseComponent(cursor)
                .orElse(null);
    }

    /**
     * Consumes a path component within a {@link TextCursor}.
     */
    static Optional<String> parseComponent(final TextCursor cursor) {
        return COMPONENT.parse(cursor, CONTEXT)
                .map(p -> p.cast(StringParserToken.class)
                        .value()
                        .substring(1)
                );
    }

    private final static int MAX_LENGTH = 8192;

    /**
     * A {@link Parser} that consumes a path component within an {@link UrlFragment}.
     */
    private final static Parser<ParserContext> COMPONENT = Parsers.stringInitialAndPartCharPredicate(
            CharPredicates.is('/'),
            CharPredicates.not(
                    CharPredicates.is('/')
            ),
            1,
            MAX_LENGTH
    );

    private final static ParserContext CONTEXT = ParserContexts.fake();

    /**
     * Reads or extracts a {@link SpreadsheetCellFindQuery} from the parameters probably a {@link UrlQueryString}.
     */
    public static SpreadsheetCellFindQuery extract(final Map<HttpRequestAttribute<?>, ?> parameters) {
        Objects.requireNonNull(parameters, "parameters");

        return empty()
                .setPath(
                        CELL_RANGE_PATH.firstParameterValue(parameters)
                                .map(SpreadsheetCellRangeReferencePath::fromKebabCase)
                ).setMax(
                        parseIntegerQueryParameter(
                                parameters,
                                MAX
                        )
                ).setOffset(
                        parseIntegerQueryParameter(
                                parameters,
                                OFFSET
                        )
                ).setValueType(
                        VALUE_TYPE.firstParameterValue(parameters)
                ).setQuery(
                        SpreadsheetCellQuery.extract(parameters)
                );
    }

    private static OptionalInt parseIntegerQueryParameter(final Map<HttpRequestAttribute<?>, ?> parameters,
                                                          final UrlParameterName parameterName) {
        return parameterName.firstParameterValue(parameters)
                .map(t -> parseInt(t, parameterName))
                .orElse(OptionalInt.empty());
    }

    private static OptionalInt parseInt(final String text,
                                        final UrlParameterName parameterName) {
        try {
            final int value = Integer.parseInt(text);
            if (value < 0) {
                throw new IllegalArgumentException("Invalid " + parameterName + " " + value + " < 0");
            }
            return OptionalInt.of(value);
        } catch (final NumberFormatException cause) {
            throw invalidQueryParameter(
                    text,
                    parameterName,
                    cause
            );
        }
    }

    private static IllegalArgumentException invalidQueryParameter(final String text,
                                                                  final UrlParameterName parameter,
                                                                  final Throwable cause) {
        return new IllegalArgumentException(
                invalidQueryParameterMessage(
                        text,
                        parameter
                ),
                cause
        );
    }

    private static String invalidQueryParameterMessage(final String text,
                                                       final UrlParameterName parameter) {
        return "Invalid " + parameter + "=" + CharSequences.quoteAndEscape(text);
    }

    public static SpreadsheetCellFindQuery empty() {
        return EMPTY;
    }

    private final static SpreadsheetCellFindQuery EMPTY = new SpreadsheetCellFindQuery(
            Optional.empty(), // path
            OptionalInt.empty(), // offset
            OptionalInt.empty(), // max
            Optional.empty(), // valueType
            Optional.empty() // query
    );

    // VisibleForTesting
    SpreadsheetCellFindQuery(final Optional<SpreadsheetCellRangeReferencePath> path,
                             final OptionalInt offset,
                             final OptionalInt max,
                             final Optional<String> valueType,
                             final Optional<SpreadsheetCellQuery> query) {
        this.path = path;
        this.offset = offset;
        this.max = max;
        this.valueType = valueType;
        this.query = query;
    }

    public Optional<SpreadsheetCellRangeReferencePath> path() {
        return this.path;
    }

    public SpreadsheetCellFindQuery setPath(final Optional<SpreadsheetCellRangeReferencePath> path) {
        Objects.requireNonNull(path, "path");

        return this.path.equals(path) ?
                this :
                this.replace(
                        path,
                        this.offset,
                        this.max,
                        this.valueType,
                        this.query
                );
    }

    private final Optional<SpreadsheetCellRangeReferencePath> path;

    public OptionalInt offset() {
        return this.offset;
    }

    public SpreadsheetCellFindQuery setOffset(final OptionalInt offset) {
        Objects.requireNonNull(offset, "offset");

        if(offset.isPresent()) {
            final int value = offset.getAsInt();
            if(value < 0) {
                throw new IllegalArgumentException("Invalid offset " + value + " < 0");
            }
        }

        return this.offset.equals(offset) ?
                this :
                this.replace(
                        this.path,
                        offset,
                        this.max,
                        this.valueType,
                        this.query
                );
    }

    private final OptionalInt offset;

    public OptionalInt max() {
        return this.max;
    }

    public SpreadsheetCellFindQuery setMax(final OptionalInt max) {
        Objects.requireNonNull(max, "max");

        if (max.isPresent()) {
            final int value = max.getAsInt();
            if (value < 0) {
                throw new IllegalArgumentException("Invalid max " + value + " < 0");
            }
        }

        return this.max.equals(max) ?
                this :
                this.replace(
                        this.path,
                        this.offset,
                        max,
                        this.valueType,
                        this.query
                );
    }

    private final OptionalInt max;

    public Optional<String> valueType() {
        return this.valueType;
    }

    public SpreadsheetCellFindQuery setValueType(final Optional<String> valueType) {
        Objects.requireNonNull(valueType, "valueType");

        return this.valueType.equals(valueType) ?
                this :
                this.replace(
                        this.path,
                        this.offset,
                        this.max,
                        valueType,
                        this.query
                );
    }

    private final Optional<String> valueType;

    public Optional<SpreadsheetCellQuery> query() {
        return this.query;
    }

    public SpreadsheetCellFindQuery setQuery(final Optional<SpreadsheetCellQuery> query) {
        Objects.requireNonNull(query, "query");

        return this.query.equals(query) ?
                this :
                this.replace(
                        this.path,
                        this.offset,
                        this.max,
                        this.valueType,
                        query
                );
    }

    private final Optional<SpreadsheetCellQuery> query;

    private SpreadsheetCellFindQuery replace(final Optional<SpreadsheetCellRangeReferencePath> path,
                                             final OptionalInt offset,
                                             final OptionalInt max,
                                             final Optional<String> valueType,
                                             final Optional<SpreadsheetCellQuery> query) {
        return path.isPresent() ||
                offset.isPresent() ||
                max.isPresent() ||
                valueType.isPresent() ||
                query.isPresent() ?
                new SpreadsheetCellFindQuery(
                        path,
                        offset,
                        max,
                        valueType,
                        query
                ) : EMPTY;
    }

    // CanBeEmpty.......................................................................................................

    /**
     * Returns true if all properties are empty.
     */
    @Override
    public boolean isEmpty() {
        return this == EMPTY;
    }

    // HasUrlFragment...................................................................................................

    @Override
    public UrlFragment urlFragment() {
        UrlFragment urlFragment = UrlFragment.EMPTY;

        final Optional<SpreadsheetCellRangeReferencePath> path = this.path;
        if (path.isPresent()) {
            urlFragment = urlFragment.append(PATH_URL_FRAGMENT)
                    .appendSlashThen(
                            UrlFragment.with(
                                    path.get()
                                            .toString()
                                            .toLowerCase()
                            )
                    );
        }

        final OptionalInt offset = this.offset;
        if (offset.isPresent()) {
            urlFragment = urlFragment.append(OFFSET_URL_FRAGMENT)
                    .appendSlashThen(
                            UrlFragment.with(
                                    String.valueOf(
                                            offset.getAsInt()
                                    )
                            )
                    );
        }

        final OptionalInt max = this.max;
        if (max.isPresent()) {
            urlFragment = urlFragment.append(MAX_URL_FRAGMENT)
                    .appendSlashThen(
                            UrlFragment.with(
                                    String.valueOf(
                                            max.getAsInt()
                                    )
                            )
                    );
        }

        final Optional<String> valueType = this.valueType;
        if (valueType.isPresent()) {
            urlFragment = urlFragment.append(VALUE_TYPE_URL_FRAGMENT)
                    .appendSlashThen(
                            UrlFragment.with(
                                    valueType.get()
                            )
                    );
        }

        final Optional<SpreadsheetCellQuery> query = this.query;
        if (query.isPresent()) {
            urlFragment = urlFragment.append(QUERY_URL_FRAGMENT)
                    .appendSlashThen(
                            query.get()
                                    .urlFragment()
                    );
        }

        return urlFragment;
    }

    private final static UrlFragment PATH_URL_FRAGMENT = UrlFragment.parse("/path");

    private final static UrlFragment OFFSET_URL_FRAGMENT = UrlFragment.parse("/offset");

    private final static UrlFragment MAX_URL_FRAGMENT = UrlFragment.parse("/max");

    private final static UrlFragment VALUE_TYPE_URL_FRAGMENT = UrlFragment.parse("/value-type");

    private final static UrlFragment QUERY_URL_FRAGMENT = UrlFragment.parse("/query");
    
    // UrlQueryString...................................................................................................

    public static final UrlParameterName CELL_RANGE_PATH = UrlParameterName.with("cell-range-path");

    public static final UrlParameterName MAX = UrlParameterName.with("max");

    public static final UrlParameterName OFFSET = UrlParameterName.with("offset");

    public static final UrlParameterName QUERY = SpreadsheetCellQuery.QUERY;

    public static final UrlParameterName VALUE_TYPE = UrlParameterName.with("value-type");
    
    public UrlQueryString toUrlQueryString() {
        UrlQueryString result = UrlQueryString.EMPTY;

        final Optional<SpreadsheetCellRangeReferencePath> path = this.path();
        final OptionalInt offset = this.offset();
        final OptionalInt max = this.max();
        final Optional<String> valueType = this.valueType();
        final Optional<SpreadsheetCellQuery> query = this.query();

        if (path.isPresent()) {
            result = result.addParameter(
                    CELL_RANGE_PATH,
                    CaseKind.kebabEnumName(
                            path.get()
                    )
            );
        }
        if (max.isPresent()) {
            result = result.addParameter(
                    MAX,
                    String.valueOf(
                            max.getAsInt()
                    )
            );
        }
        if (offset.isPresent()) {
            result = result.addParameter(
                    OFFSET,
                    String.valueOf(
                            offset.getAsInt()
                    )
            );
        }
        if (query.isPresent()) {
            result = result.addParameter(
                    QUERY,
                    query.get()
                            .text()
            );
        }
        if (valueType.isPresent()) {
            result = result.addParameter(
                    VALUE_TYPE,
                    valueType.get()
            );
        }

        return result;
    }

    // HasText..........................................................................................................

    @Override
    public String text() {
        return this.urlFragment()
                .toString();
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.path,
                this.offset,
                this.max,
                this.valueType,
                this.query
        );
    }

    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetCellFindQuery && this.equals0((SpreadsheetCellFindQuery) other);
    }

    private boolean equals0(final SpreadsheetCellFindQuery other) {
        return this.path.equals(other.path) &&
                this.offset.equals(other.offset) &&
                this.max.equals(other.max) &&
                this.valueType.equals(other.valueType) &&
                this.query.equals(other.query);
    }

    @Override
    public String toString() {
        return this.urlFragment()
                .toString();
    }
    
    // json.............................................................................................................

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetCellFindQuery.class),
                SpreadsheetCellFindQuery::unmarshall,
                SpreadsheetCellFindQuery::marshall,
                SpreadsheetCellFindQuery.class
        );
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshall(this.text());
    }

    static SpreadsheetCellFindQuery unmarshall(final JsonNode node,
                                               final JsonNodeUnmarshallContext context) {
        return parse(node.stringOrFail());
    }
}
