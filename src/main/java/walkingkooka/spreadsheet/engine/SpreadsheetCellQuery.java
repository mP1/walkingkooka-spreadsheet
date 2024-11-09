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
import walkingkooka.Cast;
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.UrlQueryString;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReferencePath;
import walkingkooka.text.CaseKind;
import walkingkooka.text.CharSequences;
import walkingkooka.text.HasText;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;

/**
 * Captures the parameter values of a cell find.
 */
public final class SpreadsheetCellQuery implements HasUrlFragment,
        CanBeEmpty,
        HasText {

    /**
     * Parses the given text into a {@link SpreadsheetCellQuery}.
     * <br>
     * Note the query is not verified to be a valid expression syntically in any form.
     */
    public static SpreadsheetCellQuery parse(final String text) {
        Objects.requireNonNull(text, "text");

        return extract(
                Cast.to(
                        UrlQueryString.parse(text)
                                .parameters()
                )
        );
    }

    /**
     * Reads or extracts a {@link SpreadsheetCellQuery} from the parameters probably a {@link UrlQueryString}.
     */
    public static SpreadsheetCellQuery extract(final Map<HttpRequestAttribute<?>, ?> parameters) {
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
                        QUERY.firstParameterValue(parameters)
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

    public static SpreadsheetCellQuery empty() {
        return EMPTY;
    }

    private final static SpreadsheetCellQuery EMPTY = new SpreadsheetCellQuery(
            Optional.empty(), // path
            OptionalInt.empty(), // offset
            OptionalInt.empty(), // max
            Optional.empty(), // valueType
            Optional.empty() // query
    );

    // VisibleForTesting
    SpreadsheetCellQuery(final Optional<SpreadsheetCellRangeReferencePath> path,
                         final OptionalInt offset,
                         final OptionalInt max,
                         final Optional<String> valueType,
                         final Optional<String> query) {
        this.path = path;
        this.offset = offset;
        this.max = max;
        this.valueType = valueType;
        this.query = query;
    }

    public Optional<SpreadsheetCellRangeReferencePath> path() {
        return this.path;
    }

    public SpreadsheetCellQuery setPath(final Optional<SpreadsheetCellRangeReferencePath> path) {
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

    public SpreadsheetCellQuery setOffset(final OptionalInt offset) {
        Objects.requireNonNull(offset, "offset");

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

    public SpreadsheetCellQuery setMax(final OptionalInt max) {
        Objects.requireNonNull(max, "max");

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

    public SpreadsheetCellQuery setValueType(final Optional<String> valueType) {
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

    public Optional<String> query() {
        return this.query;
    }

    public SpreadsheetCellQuery setQuery(final Optional<String> query) {
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

    private final Optional<String> query;

    private SpreadsheetCellQuery replace(final Optional<SpreadsheetCellRangeReferencePath> path,
                                         final OptionalInt offset,
                                         final OptionalInt max,
                                         final Optional<String> valueType,
                                         final Optional<String> query) {
        return path.isPresent() ||
                offset.isPresent() ||
                max.isPresent() ||
                valueType.isPresent() ||
                query.isPresent() ?
                new SpreadsheetCellQuery(
                        path,
                        offset,
                        max,
                        valueType,
                        query
                ) : EMPTY;
    }

    // expression.......................................................................................................

    public Optional<Expression> queryToExpression(final SpreadsheetEngineContext context) {
        return this.query()
                .flatMap(q -> parseQueryParameter(
                                q,
                                (t) -> context.toExpression(
                                        context.parseFormula(
                                                TextCursors.charSequence(q)
                                        )
                                ),
                                QUERY
                        )
                );
    }

    private static <T> T parseQueryParameter(final String text,
                                             final Function<String, T> parser,
                                             final UrlParameterName queryParameter) {
        try {
            return parser.apply(text);
        } catch (final IllegalArgumentException cause) {
            throw invalidQueryParameter(
                    text,
                    queryParameter,
                    cause
            );
        }
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
            urlFragment = urlFragment.appendSlashThen(PATH_URL_FRAGMENT)
                    .appendSlashThen(
                            UrlFragment.with(
                                    path.get()
                                            .toString()
                            )
                    );
        }

        final OptionalInt offset = this.offset;
        if (offset.isPresent()) {
            urlFragment = urlFragment.appendSlashThen(OFFSET_URL_FRAGMENT)
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
            urlFragment = urlFragment.appendSlashThen(MAX_URL_FRAGMENT)
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
            urlFragment = urlFragment.appendSlashThen(VALUE_TYPE_URL_FRAGMENT)
                    .appendSlashThen(
                            UrlFragment.with(
                                    valueType.get()
                            )
                    );
        }

        final Optional<String> query = this.query;
        if (query.isPresent()) {
            urlFragment = urlFragment.appendSlashThen(QUERY_URL_FRAGMENT)
                    .appendSlashThen(
                            UrlFragment.with(
                                    query.get()
                            )
                    );
        }

        return urlFragment;
    }

    private final static UrlFragment PATH_URL_FRAGMENT = UrlFragment.parse("path");

    private final static UrlFragment OFFSET_URL_FRAGMENT = UrlFragment.parse("offset");

    private final static UrlFragment MAX_URL_FRAGMENT = UrlFragment.parse("max");

    private final static UrlFragment VALUE_TYPE_URL_FRAGMENT = UrlFragment.parse("value-type");

    private final static UrlFragment QUERY_URL_FRAGMENT = UrlFragment.parse("query");
    
    // UrlQueryString...................................................................................................

    public static final UrlParameterName CELL_RANGE_PATH = UrlParameterName.with("cell-range-path");

    public static final UrlParameterName MAX = UrlParameterName.with("max");

    public static final UrlParameterName OFFSET = UrlParameterName.with("offset");

    public static final UrlParameterName QUERY = UrlParameterName.with("query");

    public static final UrlParameterName VALUE_TYPE = UrlParameterName.with("value-type");
    
    public UrlQueryString toUrlQueryString() {
        UrlQueryString result = UrlQueryString.EMPTY;

        final Optional<SpreadsheetCellRangeReferencePath> path = this.path();
        final OptionalInt offset = this.offset();
        final OptionalInt max = this.max();
        final Optional<String> valueType = this.valueType();
        final Optional<String> query = this.query();

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
        return this.toUrlQueryString().toString();
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
                other instanceof SpreadsheetCellQuery && this.equals0((SpreadsheetCellQuery) other);
    }

    private boolean equals0(final SpreadsheetCellQuery other) {
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
                JsonNodeContext.computeTypeName(SpreadsheetCellQuery.class),
                SpreadsheetCellQuery::unmarshall,
                SpreadsheetCellQuery::marshall,
                SpreadsheetCellQuery.class
        );
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshall(this.text());
    }

    static SpreadsheetCellQuery unmarshall(final JsonNode node,
                                           final JsonNodeUnmarshallContext context) {
        return parse(node.stringOrFail());
    }
}
