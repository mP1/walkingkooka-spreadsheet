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

package walkingkooka.spreadsheet.compare.provider;

import walkingkooka.InvalidCharacterException;
import walkingkooka.NeverError;
import walkingkooka.ToStringBuilder;
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.CharSequences;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.HasText;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * A selection of {@link SpreadsheetComparatorName names} for a given {@link SpreadsheetSelection}.
 */
public final class SpreadsheetColumnOrRowSpreadsheetComparatorNames implements HasText {

    final static char COLUMN_ROW_AND_COMPARATOR_NAME_SEPARATOR_CHAR = '=';

    public final static CharacterConstant COLUMN_ROW_AND_COMPARATOR_NAME_SEPARATOR = CharacterConstant.with(COLUMN_ROW_AND_COMPARATOR_NAME_SEPARATOR_CHAR);

    final static char COMPARATOR_NAME_SEPARATOR_CHAR = ',';

    public final static CharacterConstant COMPARATOR_NAME_SEPARATOR = CharacterConstant.with(',');

    final static char COLUMN_ROW_COMPARATOR_NAMES_SEPARATOR_CHAR = ';';

    public final static CharacterConstant COLUMN_ROW_COMPARATOR_NAMES_SEPARATOR = CharacterConstant.with(COLUMN_ROW_COMPARATOR_NAMES_SEPARATOR_CHAR);

    /**
     * Tries to extract the {@link SpreadsheetSelection} from the text form of a {@link SpreadsheetColumnOrRowSpreadsheetComparatorNames}.
     * Useful when parsing text from a user which may be incomplete or syntactically wrong.
     */
    public static Optional<SpreadsheetColumnOrRowReferenceOrRange> tryParseColumnOrRow(final String text) {
        Objects.requireNonNull(text, "text");

        final int assignment = text.indexOf(COLUMN_ROW_AND_COMPARATOR_NAME_SEPARATOR.character());

        SpreadsheetColumnOrRowReferenceOrRange columnOrRow;

        try {
            columnOrRow = SpreadsheetSelection.parseColumnOrRow(
                -1 != assignment ?
                    text.substring(0, assignment) :
                    text

            );
        } catch (final RuntimeException failed) {
            columnOrRow = null;
        }

        return Optional.ofNullable(columnOrRow);
    }

    /**
     * Tries to parse the {@link SpreadsheetColumnOrRowSpreadsheetComparatorNames} text returning just its
     * {@link SpreadsheetComparatorName}.
     * <br>
     * This is mostly a helper for building the SORT UI, where it would be better to have all {@link SpreadsheetComparatorName}
     * with remove links even if there are duplicate columns/rows.
     */
    public static List<SpreadsheetComparatorName> tryParseSpreadsheetComparatorNames(final String text) {
        List<SpreadsheetComparatorName> names;

        try {
            names = parse(text)
                .comparatorNames();
        } catch (final NullPointerException rethrow) {
            throw rethrow;
        } catch (final RuntimeException ignore) {
            names = Lists.empty();
        }

        return names;
    }

    /**
     * Parses the text into a single {@link SpreadsheetColumnOrRowSpreadsheetComparatorNames}.
     * <pre>
     * A=day-of-month,month-of-year
     * </pre>
     */
    public static SpreadsheetColumnOrRowSpreadsheetComparatorNames parse(final String text) {
        final SpreadsheetColumnOrRowSpreadsheetComparatorNames[] names = new SpreadsheetColumnOrRowSpreadsheetComparatorNames[1];
        tryParse(
            text,
            (n) -> names[0] = n,
            false // supportColumnRowSeparator
        );
        return names[0];
    }

    /**
     * Parses the text into a {@link List} of {@link SpreadsheetColumnOrRowSpreadsheetComparatorNames}.
     * <pre>
     * A=day-of-month,month-of-year;B=number
     * </pre>
     * </pre>
     */
    public static List<SpreadsheetColumnOrRowSpreadsheetComparatorNames> parseList(final String text) {
        final List<SpreadsheetColumnOrRowSpreadsheetComparatorNames> names = Lists.array();
        tryParse(
            text,
            names::add,
            true // supportColumnRowSeparator
        );
        return SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.with(names);
    }

    /**
     * The main parser that supports parsing one or many {@link SpreadsheetColumnOrRowSpreadsheetComparatorNames}.
     */
    private static void tryParse(final String text,
                                 final Consumer<SpreadsheetColumnOrRowSpreadsheetComparatorNames> columnOrRowNameAndDirection,
                                 final boolean supportColumnRowSeparator) {
        CharSequences.failIfNullOrEmpty(text, "text");

        final int MODE_COLUMN_OR_ROW_START = 0;
        final int MODE_COLUMN_OR_ROW = MODE_COLUMN_OR_ROW_START + 1;

        final int MODE_NAME_START = MODE_COLUMN_OR_ROW + 1;
        final int MODE_NAME = MODE_NAME_START + 1;

        final int length = text.length();

        int mode = MODE_COLUMN_OR_ROW_START;
        int tokenStart = 0;
        SpreadsheetColumnOrRowReferenceOrRange columnOrRow = null;
        List<SpreadsheetComparatorName> comparatorNames = null;

        for (int i = 0; i < length; i++) {
            final char c = text.charAt(i);

            switch (mode) {
                case MODE_COLUMN_OR_ROW_START:
                    if (COLUMN_ROW_AND_COMPARATOR_NAME_SEPARATOR.character() == c) {
                        throw new InvalidCharacterException(
                            text,
                            i
                        );
                    }
                    tokenStart = i;
                    columnOrRow = null;
                    comparatorNames = null;
                    mode = MODE_COLUMN_OR_ROW;
                    break;
                case MODE_COLUMN_OR_ROW:
                    if (COLUMN_ROW_AND_COMPARATOR_NAME_SEPARATOR.character() == c) {
                        // parse column OR row
                        try {
                            columnOrRow = SpreadsheetSelection.parseColumnOrRow(
                                text.substring(
                                    tokenStart,
                                    i
                                )
                            );
                        } catch (final InvalidCharacterException invalid) {
                            throw invalid.setTextAndPosition(
                                text,
                                tokenStart + invalid.position()
                            );
                        }

                        comparatorNames = Lists.array();
                        mode = MODE_NAME_START;
                        break;
                    }
                    break;
                case MODE_NAME_START:
                    if (false == SpreadsheetComparatorName.isChar(i, c)) {
                        throw new InvalidCharacterException(
                            text,
                            i
                        );
                    }
                    tokenStart = i;
                    mode = MODE_NAME;
                    break;
                case MODE_NAME:
                    switch (c) {
                        case COMPARATOR_NAME_SEPARATOR_CHAR:
                            comparatorNames.add(
                                SpreadsheetComparatorName.with(
                                    text.substring(
                                        tokenStart,
                                        i
                                    )
                                )
                            );
                            mode = MODE_NAME_START;
                            break;
                        case COLUMN_ROW_COMPARATOR_NAMES_SEPARATOR_CHAR:
                            if (false == supportColumnRowSeparator) {
                                throw new InvalidCharacterException(
                                    text,
                                    i
                                );
                            }
                            comparatorNames.add(
                                SpreadsheetComparatorName.with(
                                    text.substring(
                                        tokenStart,
                                        i
                                    )
                                )
                            );
                            columnOrRowNameAndDirection.accept(
                                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                                    columnOrRow,
                                    comparatorNames
                                )
                            );
                            columnOrRow = null;
                            mode = MODE_COLUMN_OR_ROW_START;
                            break;
                        case '-':
                            // continue parsing name
                            break;
                        default:
                            // continue parsing name
                            if (false == SpreadsheetComparatorName.isChar(i, c)) {
                                throw new InvalidCharacterException(
                                    text,
                                    i
                                );
                            }
                            break;
                    }
                    break;
                default:
                    throw new NeverError("Unknown mode=" + mode);
            }
        }

        switch (mode) {
            case MODE_NAME:
                comparatorNames.add(
                    SpreadsheetComparatorName.with(
                        text.substring(
                            tokenStart,
                            length
                        )
                    )
                );
                columnOrRowNameAndDirection.accept(
                    SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                        columnOrRow,
                        comparatorNames
                    )
                );
                break;
            case MODE_COLUMN_OR_ROW_START:
                break;
            case MODE_COLUMN_OR_ROW:
                if (length != tokenStart) {
                    // could be a column/row missing '=' or could be an invalid character within a column/row
                    try {
                        SpreadsheetSelection.parseColumnOrRow(
                            text.substring(
                                tokenStart,
                                length
                            )
                        );
                    } catch (final InvalidCharacterException invalid) {
                        throw invalid.setTextAndPosition(
                            text,
                            tokenStart + invalid.position()
                        );
                    }
                }
                throw new IllegalArgumentException("Missing " + CharSequences.quoteIfChars(COLUMN_ROW_AND_COMPARATOR_NAME_SEPARATOR_CHAR));
            case MODE_NAME_START:
                throw new IllegalArgumentException("Missing comparator name");
            default:
                break;
        }
    }

    /**
     * Factory that creates a new {@link SpreadsheetColumnOrRowSpreadsheetComparatorNames}.
     */
    public static SpreadsheetColumnOrRowSpreadsheetComparatorNames with(final SpreadsheetColumnOrRowReferenceOrRange columnOrRow,
                                                                        final List<SpreadsheetComparatorName> comparatorNames) {

        return new SpreadsheetColumnOrRowSpreadsheetComparatorNames(
            Objects.requireNonNull(columnOrRow, "columnOrRow"),
            checkComparatorNameAndDirections(comparatorNames)
        );
    }

    private SpreadsheetColumnOrRowSpreadsheetComparatorNames(final SpreadsheetColumnOrRowReferenceOrRange columnOrRow,
                                                             final List<SpreadsheetComparatorName> comparatorNames) {


        this.columnOrRow = columnOrRow;
        this.comparatorNames = comparatorNames;
    }

    public SpreadsheetColumnOrRowReferenceOrRange columnOrRow() {
        return this.columnOrRow;
    }

    /**
     * Would be setter that returns a {@link SpreadsheetColumnOrRowSpreadsheetComparatorNames} with the given {@link SpreadsheetSelection} creating a new instance if necessary.
     */
    public SpreadsheetColumnOrRowSpreadsheetComparatorNames setColumnOrRow(final SpreadsheetColumnOrRowReferenceOrRange columnOrRow) {
        Objects.requireNonNull(columnOrRow, "columnOrRows");

        return this.columnOrRow.equals(columnOrRow) ?
            this :
            new SpreadsheetColumnOrRowSpreadsheetComparatorNames(
                columnOrRow,
                this.comparatorNames
            );
    }

    private final SpreadsheetColumnOrRowReferenceOrRange columnOrRow;

    // comparatorNames......................................................................................

    public List<SpreadsheetComparatorName> comparatorNames() {
        return this.comparatorNames;
    }

    /**
     * Would be setter that returns a {@link SpreadsheetColumnOrRowSpreadsheetComparatorNames} with the given {@link SpreadsheetComparatorName} creating a new instance if necessary.
     */
    public SpreadsheetColumnOrRowSpreadsheetComparatorNames setComparatorNameAndDirections(final List<SpreadsheetComparatorName> comparatorNames) {
        final List<SpreadsheetComparatorName> copy = checkComparatorNameAndDirections(comparatorNames);

        return this.comparatorNames.equals(copy) ?
            this :
            new SpreadsheetColumnOrRowSpreadsheetComparatorNames(
                this.columnOrRow,
                copy
            );
    }

    private final List<SpreadsheetComparatorName> comparatorNames;

    private static List<SpreadsheetComparatorName> checkComparatorNameAndDirections(final List<SpreadsheetComparatorName> comparatorNames) {
        List<SpreadsheetComparatorName> copy = Lists.immutable(
            Objects.requireNonNull(comparatorNames, "comparatorNames")
        );
        if (comparatorNames.isEmpty()) {
            throw new IllegalArgumentException("Empty comparators");
        }
        return copy;
    }

    // SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.............................................................

    /**
     * Creates a {@link SpreadsheetColumnOrRowSpreadsheetComparatorNamesList} with this.
     */
    public SpreadsheetColumnOrRowSpreadsheetComparatorNamesList list() {
        return SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.with(
            Lists.of(
                this
            )
        );
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.columnOrRow,
            this.comparatorNames
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetColumnOrRowSpreadsheetComparatorNames && this.equals0((SpreadsheetColumnOrRowSpreadsheetComparatorNames) other);
    }

    private boolean equals0(final SpreadsheetColumnOrRowSpreadsheetComparatorNames other) {
        return this.columnOrRow.equals(other.columnOrRow) &&
            this.comparatorNames.equals(other.comparatorNames);
    }

    @Override
    public String toString() {
        return ToStringBuilder.empty()
            .labelSeparator(COLUMN_ROW_AND_COMPARATOR_NAME_SEPARATOR.string())
            .valueSeparator(COMPARATOR_NAME_SEPARATOR.string())
            .label(this.columnOrRow.text())
            .value(this.comparatorNames)
            .build();
    }

    // HasText..........................................................................................................

    @Override
    public String text() {
        return this.toString();
    }

    // Json.............................................................................................................

    static SpreadsheetColumnOrRowSpreadsheetComparatorNames unmarshall(final JsonNode node,
                                                                       final JsonNodeUnmarshallContext context) {
        return parse(node.stringOrFail());
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(this.toString());
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetColumnOrRowSpreadsheetComparatorNames.class),
            SpreadsheetColumnOrRowSpreadsheetComparatorNames::unmarshall,
            SpreadsheetColumnOrRowSpreadsheetComparatorNames::marshall,
            SpreadsheetColumnOrRowSpreadsheetComparatorNames.class
        );
    }
}
