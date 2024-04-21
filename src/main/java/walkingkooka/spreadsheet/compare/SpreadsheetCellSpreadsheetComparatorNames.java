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

package walkingkooka.spreadsheet.compare;

import walkingkooka.InvalidCharacterException;
import walkingkooka.NeverError;
import walkingkooka.ToStringBuilder;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A selection of {@link SpreadsheetComparatorName names} for a given {@link SpreadsheetColumnOrRowReference}.
 */
public final class SpreadsheetCellSpreadsheetComparatorNames {

    private final static char COLUMN_ROW_ASSIGNMENT = '=';

    private final static char NAME_UP_DOWN_SEPARATOR = ' ';

    private final static char COMPARATOR_SEPARATOR = ',';

    final static char COLUMN_ROW_SEPARATOR = ';';

    /**
     * Parses the text into a single {@link SpreadsheetCellSpreadsheetComparatorNames}.
     */
    public static SpreadsheetCellSpreadsheetComparatorNames parse(final String text) {
        final SpreadsheetCellSpreadsheetComparatorNames[] names = new SpreadsheetCellSpreadsheetComparatorNames[1];
        tryParse(
                text,
                (n) -> names[0] = n,
                false // supportColumnRowSeparator
        );
        return names[0];
    }

    /**
     * Parses the text into a {@link List} of {@link SpreadsheetCellSpreadsheetComparatorNames}.
     */
    public static List<SpreadsheetCellSpreadsheetComparatorNames> parseList(final String text) {
        final List<SpreadsheetCellSpreadsheetComparatorNames> names = Lists.array();
        tryParse(
                text,
                names::add,
                true // supportColumnRowSeparator
        );
        return list(names);
    }

    /**
     * The main parser that supports parsing one or many {@link SpreadsheetCellSpreadsheetComparatorNames}.
     */
    private static void tryParse(final String text,
                                 final Consumer<SpreadsheetCellSpreadsheetComparatorNames> columnOrRowNameAndDirection,
                                 final boolean supportColumnRowSeparator) {
        CharSequences.failIfNullOrEmpty(text, "text");

        final int modeColumnOrRowStart = 0;
        final int modeColumnOrRow = modeColumnOrRowStart + 1;

        final int modeNameStart = modeColumnOrRow + 1;
        final int modeName = modeNameStart + 1;

        final int modeUpOrDownStart = modeName + 1;
        final int modeUpOrDown = modeUpOrDownStart + 1;

        final int length = text.length();

        final Set<SpreadsheetColumnOrRowReference> duplicates = Sets.sorted();
        int mode = 0;
        int tokenStart = 0;
        Function<String, SpreadsheetColumnOrRowReference> columnOrRowParser = SpreadsheetSelection::parseColumnOrRow;
        SpreadsheetColumnOrRowReference columnOrRow = null;
        SpreadsheetComparatorName comparatorName = null;
        List<SpreadsheetComparatorNameAndDirection> comparatorNameAndDirections = null;

        for (int i = 0; i < length; i++) {
            final char c = text.charAt(i);

            switch (mode) {
                case modeColumnOrRowStart:
                    if (COLUMN_ROW_ASSIGNMENT == c) {
                        throw new InvalidCharacterException(
                                text,
                                i
                        );
                    }
                    tokenStart = i;
                    columnOrRow = null;
                    comparatorNameAndDirections = null;
                    mode = modeColumnOrRow;
                    break;
                case modeColumnOrRow:
                    if (COLUMN_ROW_ASSIGNMENT == c) {
                        // parse column OR row
                        try {
                            columnOrRow = columnOrRowParser.apply(
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

                        if (false == duplicates.add(columnOrRow)) {
                            throw new IllegalArgumentException(
                                    "Duplicate " +
                                            columnOrRow.cellColumnOrRowText() +
                                            " " +
                                            columnOrRow
                            );
                        }

                        comparatorNameAndDirections = Lists.array();
                        columnOrRowParser = columnOrRow.columnOrRowReferenceKind()
                                ::parse;
                        mode = modeNameStart;
                        break;
                    }
                    break;
                case modeNameStart:
                    if (false == SpreadsheetComparatorName.PART.test(c)) {
                        throw new InvalidCharacterException(
                                text,
                                i
                        );
                    }
                    tokenStart = i;
                    comparatorName = null;
                    mode = modeName;
                    break;
                case modeName:
                    switch (c) {
                        case NAME_UP_DOWN_SEPARATOR:
                            comparatorName = SpreadsheetComparatorName.with(
                                    text.substring(
                                            tokenStart,
                                            i
                                    )
                            );
                            mode = modeUpOrDownStart;
                            break;
                        case COMPARATOR_SEPARATOR:
                            comparatorNameAndDirections.add(
                                    SpreadsheetComparatorName.with(
                                            text.substring(
                                                    tokenStart,
                                                    i
                                            )
                                    ).setDirection(SpreadsheetComparatorDirection.DEFAULT)
                            );
                            mode = modeNameStart;
                            break;
                        case COLUMN_ROW_SEPARATOR:
                            if (false == supportColumnRowSeparator) {
                                throw new InvalidCharacterException(
                                        text,
                                        i
                                );
                            }
                            comparatorNameAndDirections.add(
                                    SpreadsheetComparatorName.with(
                                            text.substring(
                                                    tokenStart,
                                                    i
                                            )
                                    ).setDirection(SpreadsheetComparatorDirection.DEFAULT)
                            );
                            columnOrRowNameAndDirection.accept(
                                    SpreadsheetCellSpreadsheetComparatorNames.with(
                                            columnOrRow,
                                            comparatorNameAndDirections
                                    )
                            );
                            columnOrRow = null;
                            comparatorName = null;
                            mode = modeColumnOrRowStart;
                            break;
                        case '-':
                            // continue parsing name
                            break;
                        default:
                            // continue parsing name
                            if (false == SpreadsheetComparatorName.PART.test(c)) {
                                throw new InvalidCharacterException(
                                        text,
                                        i
                                );
                            }
                            break;
                    }
                    break;
                case modeUpOrDownStart:
                    if (c > 'Z' || false == Character.isLetter(c)) {
                        throw new InvalidCharacterException(
                                text,
                                i
                        );
                    }
                    tokenStart = i;
                    mode = modeUpOrDown;
                    break;
                case modeUpOrDown:
                    switch (c) {
                        case COMPARATOR_SEPARATOR:
                            comparatorNameAndDirections.add(
                                    upOrDown(
                                            tokenStart,
                                            i,
                                            text,
                                            comparatorName
                                    )
                            );
                            mode = modeNameStart;
                            break;
                        case COLUMN_ROW_SEPARATOR:
                            if (false == supportColumnRowSeparator) {
                                throw new InvalidCharacterException(
                                        text,
                                        i
                                );
                            }
                            comparatorNameAndDirections.add(
                                    upOrDown(
                                            tokenStart,
                                            i,
                                            text,
                                            comparatorName
                                    )
                            );
                            columnOrRowNameAndDirection.accept(
                                    SpreadsheetCellSpreadsheetComparatorNames.with(
                                            columnOrRow,
                                            comparatorNameAndDirections
                                    )
                            );
                            mode = modeColumnOrRowStart;
                            break;
                        default:
                            if (false == SpreadsheetComparatorName.INITIAL.test(c)) {
                                throw new InvalidCharacterException(
                                        text,
                                        i
                                );
                            }
                            // continue gathering UP or DOWN text
                            break;
                    }
                    break;
                default:
                    throw new NeverError("Unknown mode=" + mode);
            }
        }

        switch (mode) {
            case modeName:
                comparatorNameAndDirections.add(
                        SpreadsheetComparatorName.with(
                                text.substring(
                                        tokenStart,
                                        length
                                )
                        ).setDirection(SpreadsheetComparatorDirection.DEFAULT)
                );
                columnOrRowNameAndDirection.accept(
                        SpreadsheetCellSpreadsheetComparatorNames.with(
                                columnOrRow,
                                comparatorNameAndDirections
                        )
                );
                break;
            case modeUpOrDown:
                comparatorNameAndDirections.add(
                        upOrDown(
                                tokenStart,
                                length,
                                text,
                                comparatorName
                        )
                );
                columnOrRowNameAndDirection.accept(
                        SpreadsheetCellSpreadsheetComparatorNames.with(
                                columnOrRow,
                                comparatorNameAndDirections
                        )
                );
                break;
            case modeColumnOrRow:
                throw new IllegalArgumentException("Expected column/row");
            case modeNameStart:
                throw new IllegalArgumentException("Missing comparator name");
            case modeUpOrDownStart:
                throw new IllegalArgumentException("Missing " + SpreadsheetComparatorDirection.UP + "/" + SpreadsheetComparatorDirection.DOWN);
            default:
                break;
        }
    }

    private static SpreadsheetComparatorNameAndDirection upOrDown(final int start,
                                                                  final int end,
                                                                  final String text,
                                                                  final SpreadsheetComparatorName name) {
        final String upOrDown = text.substring(
                start,
                end
        );

        final SpreadsheetComparatorDirection direction;
        try {
            direction = SpreadsheetComparatorDirection.valueOf(upOrDown);
        } catch (final IllegalArgumentException invalid) {
            throw new IllegalArgumentException(
                    "Missing " +
                            SpreadsheetComparatorDirection.UP +
                            "/" +
                            SpreadsheetComparatorDirection.DOWN +
                            " at " +
                            start
            );
        }

        return name.setDirection(direction);
    }

    /**
     * Returns an immutable list with the given {@link SpreadsheetCellSpreadsheetComparatorNames comparator names}.
     */
    public static List<SpreadsheetCellSpreadsheetComparatorNames> list(final List<SpreadsheetCellSpreadsheetComparatorNames> comparatorNames) {
        return SpreadsheetCellSpreadsheetComparatorNamesList.with(comparatorNames);
    }

    public static String listToString(final List<SpreadsheetCellSpreadsheetComparatorNames> comparatorNames) {
        return SpreadsheetCellSpreadsheetComparatorNamesList.with(comparatorNames)
                .parseString();
    }

    /**
     * Factory that creates a new {@link SpreadsheetCellSpreadsheetComparatorNames}.
     */
    public static SpreadsheetCellSpreadsheetComparatorNames with(final SpreadsheetColumnOrRowReference columnOrRow,
                                                                 final List<SpreadsheetComparatorNameAndDirection> comparatorNameAndDirections) {

        return new SpreadsheetCellSpreadsheetComparatorNames(
                Objects.requireNonNull(columnOrRow, "columnOrRows"),
                Lists.immutable(
                        Objects.requireNonNull(comparatorNameAndDirections, "comparatorNameAndDirections")
                )
        );
    }

    private SpreadsheetCellSpreadsheetComparatorNames(final SpreadsheetColumnOrRowReference columnOrRow,
                                                      final List<SpreadsheetComparatorNameAndDirection> comparatorNameAndDirections) {
        if (comparatorNameAndDirections.isEmpty()) {
            throw new IllegalArgumentException("Expected at least 1 comparator got none");
        }

        this.columnOrRow = columnOrRow;
        this.comparatorNameAndDirections = comparatorNameAndDirections;
    }

    public SpreadsheetColumnOrRowReference columnOrRow() {
        return this.columnOrRow;
    }

    private final SpreadsheetColumnOrRowReference columnOrRow;

    public List<SpreadsheetComparatorNameAndDirection> comparatorNameAndDirections() {
        return this.comparatorNameAndDirections;
    }

    private final List<SpreadsheetComparatorNameAndDirection> comparatorNameAndDirections;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.columnOrRow,
                this.comparatorNameAndDirections
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetCellSpreadsheetComparatorNames && this.equals0((SpreadsheetCellSpreadsheetComparatorNames) other);
    }

    private boolean equals0(final SpreadsheetCellSpreadsheetComparatorNames other) {
        return this.columnOrRow.equals(other.columnOrRow) &&
                this.comparatorNameAndDirections.equals(other.comparatorNameAndDirections);
    }

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .value(this.columnOrRow)
                .separator(String.valueOf(COLUMN_ROW_ASSIGNMENT))
                .valueSeparator(String.valueOf(COMPARATOR_SEPARATOR))
                .value(this.comparatorNameAndDirections)
                .build();
    }

    // Json.............................................................................................................

    static SpreadsheetCellSpreadsheetComparatorNames unmarshall(final JsonNode node,
                                                                final JsonNodeUnmarshallContext context) {
        return parse(node.stringOrFail());
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(this.toString());
    }

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetCellSpreadsheetComparatorNames.class),
                SpreadsheetCellSpreadsheetComparatorNames::unmarshall,
                SpreadsheetCellSpreadsheetComparatorNames::marshall,
                SpreadsheetCellSpreadsheetComparatorNames.class
        );
    }
}
