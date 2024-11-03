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
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReferencePath;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * Captures the parameter values of a cell find.
 */
public final class SpreadsheetCellFind implements HasUrlFragment,
        CanBeEmpty {

    public static SpreadsheetCellFind empty() {
        return EMPTY;
    }

    private final static SpreadsheetCellFind EMPTY = new SpreadsheetCellFind(
            Optional.empty(), // path
            OptionalInt.empty(), // offset
            OptionalInt.empty(), // max
            Optional.empty(), // valueType
            Optional.empty() // query
    );

    // VisibleForTesting
    SpreadsheetCellFind(final Optional<SpreadsheetCellRangeReferencePath> path,
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

    public SpreadsheetCellFind setPath(final Optional<SpreadsheetCellRangeReferencePath> path) {
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

    public SpreadsheetCellFind setOffset(final OptionalInt offset) {
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

    public SpreadsheetCellFind setMax(final OptionalInt max) {
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

    public SpreadsheetCellFind setValueType(final Optional<String> valueType) {
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

    public SpreadsheetCellFind setQuery(final Optional<String> query) {
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

    private SpreadsheetCellFind replace(final Optional<SpreadsheetCellRangeReferencePath> path,
                                        final OptionalInt offset,
                                        final OptionalInt max,
                                        final Optional<String> valueType,
                                        final Optional<String> query) {
        return path.isPresent() ||
                offset.isPresent() ||
                max.isPresent() ||
                valueType.isPresent() ||
                query.isPresent() ?
                new SpreadsheetCellFind(
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
            urlFragment = urlFragment.appendSlashThen(PATH)
                    .appendSlashThen(
                            UrlFragment.with(
                                    path.get()
                                            .toString()
                            )
                    );
        }

        final OptionalInt offset = this.offset;
        if (offset.isPresent()) {
            urlFragment = urlFragment.appendSlashThen(OFFSET)
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
            urlFragment = urlFragment.appendSlashThen(MAX)
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
            urlFragment = urlFragment.appendSlashThen(VALUE_TYPE)
                    .appendSlashThen(
                            UrlFragment.with(
                                    valueType.get()
                            )
                    );
        }

        final Optional<String> query = this.query;
        if (query.isPresent()) {
            urlFragment = urlFragment.appendSlashThen(QUERY)
                    .appendSlashThen(
                            UrlFragment.with(
                                    query.get()
                            )
                    );
        }

        return urlFragment;
    }

    private final static UrlFragment PATH = UrlFragment.parse("path");

    private final static UrlFragment OFFSET = UrlFragment.parse("offset");


    private final static UrlFragment MAX = UrlFragment.parse("max");


    private final static UrlFragment VALUE_TYPE = UrlFragment.parse("value-type");

    private final static UrlFragment QUERY = UrlFragment.parse("query");

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
                other instanceof SpreadsheetCellFind && this.equals0((SpreadsheetCellFind) other);
    }

    private boolean equals0(final SpreadsheetCellFind other) {
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
}
