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

package walkingkooka.spreadsheet.engine;

import walkingkooka.CanBeEmpty;
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.UrlQueryString;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReferencePath;
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
import walkingkooka.validation.ValidationValueTypeName;

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
            if (PATH_STRING.equals(component)) {
                query = query.setPath(
                    parseComponent(cursor)
                        .map(SpreadsheetCellRangeReferencePath::parse)
                );

                component = parseComponentOrNull(cursor);
            }
            if (OFFSET_STRING.equals(component)) {
                query = query.setOffset(
                    parseInt(
                        cursor,
                        OFFSET_STRING
                    )
                );
                component = parseComponentOrNull(cursor);
            }
            if (COUNT_STRING.equals(component)) {
                query = query.setCount(
                    parseInt(
                        cursor,
                        COUNT_STRING
                    )
                );
                component = parseComponentOrNull(cursor);
            }
            if (VALUE_TYPE_STRING.equals(component)) {
                query = query.setValueType(
                    parseComponent(cursor)
                        .map(ValidationValueTypeName::with)
                );
                component = parseComponentOrNull(cursor);
            }
            if (QUERY_STRING.equals(component)) {
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

        if (false == cursor.isEmpty()) {
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

    private final static int COUNT_LENGTH = 8192;

    /**
     * A {@link Parser} that consumes a path component within an {@link UrlFragment}.
     */
    private final static Parser<ParserContext> COMPONENT = Parsers.initialAndPartCharPredicateString(
        CharPredicates.is('/'),
        CharPredicates.not(
            CharPredicates.is('/')
        ),
        1,
        COUNT_LENGTH
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
                    .map(SpreadsheetCellRangeReferencePath::parse)
            ).setCount(
                parseIntegerQueryParameter(
                    parameters,
                    COUNT
                )
            ).setOffset(
                parseIntegerQueryParameter(
                    parameters,
                    OFFSET
                )
            ).setValueType(
                VALUE_TYPE.firstParameterValue(parameters)
                    .map(ValidationValueTypeName::with)
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
        OptionalInt.empty(), // count
        Optional.empty(), // valueType
        Optional.empty() // query
    );

    // VisibleForTesting
    SpreadsheetCellFindQuery(final Optional<SpreadsheetCellRangeReferencePath> path,
                             final OptionalInt offset,
                             final OptionalInt count,
                             final Optional<ValidationValueTypeName> valueType,
                             final Optional<SpreadsheetCellQuery> query) {
        this.path = path;
        this.offset = offset;
        this.count = count;
        this.valueType = valueType;
        this.query = query;
    }

    public Optional<SpreadsheetCellRangeReferencePath> path() {
        return this.path;
    }

    public SpreadsheetCellFindQuery setPath(final Optional<SpreadsheetCellRangeReferencePath> path) {
        Objects.requireNonNull(path, PATH_STRING);

        return this.path.equals(path) ?
            this :
            this.replace(
                path,
                this.offset,
                this.count,
                this.valueType,
                this.query
            );
    }

    private final Optional<SpreadsheetCellRangeReferencePath> path;

    public OptionalInt offset() {
        return this.offset;
    }

    public SpreadsheetCellFindQuery setOffset(final OptionalInt offset) {
        Objects.requireNonNull(offset, OFFSET_STRING);

        if (offset.isPresent()) {
            final int value = offset.getAsInt();
            if (value < 0) {
                throw new IllegalArgumentException("Invalid " + OFFSET_STRING + " " + value + " < 0");
            }
        }

        return this.offset.equals(offset) ?
            this :
            this.replace(
                this.path,
                offset,
                this.count,
                this.valueType,
                this.query
            );
    }

    private final OptionalInt offset;

    public OptionalInt count() {
        return this.count;
    }

    public SpreadsheetCellFindQuery setCount(final OptionalInt count) {
        Objects.requireNonNull(count, COUNT_STRING);

        if (count.isPresent()) {
            final int value = count.getAsInt();
            if (value < 0) {
                throw new IllegalArgumentException("Invalid " + COUNT_STRING + " " + value + " < 0");
            }
        }

        return this.count.equals(count) ?
            this :
            this.replace(
                this.path,
                this.offset,
                count,
                this.valueType,
                this.query
            );
    }

    private final OptionalInt count;

    public Optional<ValidationValueTypeName> valueType() {
        return this.valueType;
    }

    public SpreadsheetCellFindQuery setValueType(final Optional<ValidationValueTypeName> valueType) {
        Objects.requireNonNull(valueType, VALUE_TYPE_STRING);

        return this.valueType.equals(valueType) ?
            this :
            this.replace(
                this.path,
                this.offset,
                this.count,
                valueType,
                this.query
            );
    }

    private final Optional<ValidationValueTypeName> valueType;

    public Optional<SpreadsheetCellQuery> query() {
        return this.query;
    }

    public SpreadsheetCellFindQuery setQuery(final Optional<SpreadsheetCellQuery> query) {
        Objects.requireNonNull(query, QUERY_STRING);

        return this.query.equals(query) ?
            this :
            this.replace(
                this.path,
                this.offset,
                this.count,
                this.valueType,
                query
            );
    }

    private final Optional<SpreadsheetCellQuery> query;

    private SpreadsheetCellFindQuery replace(final Optional<SpreadsheetCellRangeReferencePath> path,
                                             final OptionalInt offset,
                                             final OptionalInt count,
                                             final Optional<ValidationValueTypeName> valueType,
                                             final Optional<SpreadsheetCellQuery> query) {
        return path.isPresent() ||
            offset.isPresent() ||
            count.isPresent() ||
            valueType.isPresent() ||
            query.isPresent() ?
            new SpreadsheetCellFindQuery(
                path,
                offset,
                count,
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

        final OptionalInt count = this.count;
        if (count.isPresent()) {
            urlFragment = urlFragment.append(COUNT_URL_FRAGMENT)
                .appendSlashThen(
                    UrlFragment.with(
                        String.valueOf(
                            count.getAsInt()
                        )
                    )
                );
        }

        final Optional<ValidationValueTypeName> valueType = this.valueType;
        if (valueType.isPresent()) {
            urlFragment = urlFragment.append(VALUE_TYPE_URL_FRAGMENT)
                .appendSlashThen(
                    UrlFragment.with(
                        valueType.get()
                            .value()
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

    private final static String PATH_STRING = "path";

    private final static UrlFragment PATH_URL_FRAGMENT = UrlFragment.parse("/" + PATH_STRING);

    private final static String OFFSET_STRING = "offset";

    private final static UrlFragment OFFSET_URL_FRAGMENT = UrlFragment.parse("/" + OFFSET_STRING);

    private final static String COUNT_STRING = "count";

    private final static UrlFragment COUNT_URL_FRAGMENT = UrlFragment.parse("/" + COUNT_STRING);

    private final static String VALUE_TYPE_STRING = "value-type";

    private final static UrlFragment VALUE_TYPE_URL_FRAGMENT = UrlFragment.parse("/" + VALUE_TYPE_STRING);

    private final static String QUERY_STRING = "query";

    private final static UrlFragment QUERY_URL_FRAGMENT = UrlFragment.parse("/" + QUERY_STRING);


    // UrlQueryString...................................................................................................

    public static final UrlParameterName CELL_RANGE_PATH = UrlParameterName.with("cell-range-path");

    public static final UrlParameterName COUNT = UrlParameterName.with(COUNT_STRING);

    public static final UrlParameterName OFFSET = UrlParameterName.with(OFFSET_STRING);

    public static final UrlParameterName QUERY = SpreadsheetCellQuery.QUERY;

    public static final UrlParameterName VALUE_TYPE = UrlParameterName.with(VALUE_TYPE_STRING);

    public UrlQueryString toUrlQueryString() {
        UrlQueryString result = UrlQueryString.EMPTY;

        final Optional<SpreadsheetCellRangeReferencePath> path = this.path();
        final OptionalInt offset = this.offset();
        final OptionalInt count = this.count();
        final Optional<ValidationValueTypeName> valueType = this.valueType();
        final Optional<SpreadsheetCellQuery> query = this.query();

        if (path.isPresent()) {
            result = result.addParameter(
                CELL_RANGE_PATH,
                path.get()
                    .toString()
            );
        }
        if (count.isPresent()) {
            result = result.addParameter(
                COUNT,
                String.valueOf(
                    count.getAsInt()
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
                    .value()
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
            this.count,
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
            this.count.equals(other.count) &&
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
