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

import walkingkooka.Cast;
import walkingkooka.InvalidCharacterException;
import walkingkooka.NeverError;
import walkingkooka.ToStringBuilder;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.compare.Comparators;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.CharSequences;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * For a single column or row holds a list of {@link SpreadsheetComparator}. Note that values that cannot be converted ond empty cells
 * will be appended to the sorted values.
 */
public final class SpreadsheetCellSpreadsheetComparators {

    private final static char COLUMN_ROW_ASSIGNMENT = '=';

    private final static char NAME_UP_DOWN_SEPARATOR = ' ';

    private final static char COMPARATOR_SEPARATOR = ',';

    private final static char COLUMN_ROW_SEPARATOR = ';';

    public static List<SpreadsheetCellSpreadsheetComparators> parse(final String text,
                                                                    final Function<String, SpreadsheetComparator<?>> nameToComparator) {
        CharSequences.failIfNullOrEmpty(text, "text");
        Objects.requireNonNull(nameToComparator, "nameToComparator");

        final int modeColumnOrRowStart = 0;
        final int modeColumnOrRow = modeColumnOrRowStart + 1;

        final int modeNameStart = modeColumnOrRow + 1;
        final int modeName = modeNameStart + 1;

        final int modeUpOrDownStart = modeName + 1;
        final int modeUpOrDown = modeUpOrDownStart + 1;

        final int length = text.length();

        final Set<SpreadsheetColumnOrRowReference> duplicates = Sets.sorted();
        final List<SpreadsheetCellSpreadsheetComparators> columnOrRowComparators = Lists.array();
        int mode = 0;
        int tokenStart = 0;
        Function<String, SpreadsheetColumnOrRowReference> columnOrRowParser = SpreadsheetSelection::parseColumnOrRow;
        SpreadsheetColumnOrRowReference columnOrRow = null;
        SpreadsheetComparator<?> comparator = null;
        List<SpreadsheetComparator<?>> comparators = null;

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
                    comparators = null;
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

                        comparators = Lists.array();
                        columnOrRowParser = columnOrRow.columnOrRowReferenceKind()
                                ::parse;
                        mode = modeNameStart;
                        break;
                    }
                    break;
                case modeNameStart:
                    if (c > 'z' || false == Character.isLetter(c)) {
                        throw new InvalidCharacterException(
                                text,
                                i
                        );
                    }
                    tokenStart = i;
                    comparator = null;
                    mode = modeName;
                    break;
                case modeName:
                    switch (c) {
                        case NAME_UP_DOWN_SEPARATOR:
                            comparator = nameToComparator.apply(
                                    text.substring(
                                            tokenStart,
                                            i
                                    )
                            );
                            mode = modeUpOrDownStart;
                            break;
                        case COMPARATOR_SEPARATOR:
                            comparators.add(
                                    nameToComparator.apply(
                                            text.substring(
                                                    tokenStart,
                                                    i
                                            )
                                    )
                            );
                            mode = modeNameStart;
                            break;
                        case COLUMN_ROW_SEPARATOR:
                            comparators.add(
                                    nameToComparator.apply(
                                            text.substring(
                                                    tokenStart,
                                                    i
                                            )
                                    )
                            );
                            columnOrRowComparators.add(
                                    SpreadsheetCellSpreadsheetComparators.with(
                                            columnOrRow,
                                            comparators
                                    )
                            );
                            columnOrRow = null;
                            comparator = null;
                            mode = modeColumnOrRowStart;
                            break;
                        case '-':
                            // continue parsing name
                            break;
                        default:
                            // continue parsing name
                            if (c > 'z' || false == Character.isLetterOrDigit(c)) {
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
                            comparators.add(
                                    upOrDown(
                                            tokenStart,
                                            i,
                                            text,
                                            comparator
                                    )
                            );
                            mode = modeNameStart;
                            break;
                        case COLUMN_ROW_SEPARATOR:
                            comparators.add(
                                    upOrDown(
                                            tokenStart,
                                            i,
                                            text,
                                            comparator
                                    )
                            );
                            columnOrRowComparators.add(
                                    SpreadsheetCellSpreadsheetComparators.with(
                                            columnOrRow,
                                            comparators
                                    )
                            );
                            mode = modeColumnOrRowStart;
                            break;
                        default:
                            if (c < 'A' || c > 'Z') {
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
                comparators.add(
                        nameToComparator.apply(
                                text.substring(
                                        tokenStart,
                                        length
                                )
                        )
                );
                columnOrRowComparators.add(
                        SpreadsheetCellSpreadsheetComparators.with(
                                columnOrRow,
                                comparators
                        )
                );
                break;
            case modeUpOrDown:
                comparators.add(
                        upOrDown(
                                tokenStart,
                                length,
                                text,
                                comparator
                        )
                );
                columnOrRowComparators.add(
                        SpreadsheetCellSpreadsheetComparators.with(
                                columnOrRow,
                                comparators
                        )
                );
                break;
            case modeColumnOrRow:
                throw new IllegalArgumentException("Expected column/row");
            case modeNameStart:
                throw new IllegalArgumentException("Missing comparator name");
            case modeUpOrDownStart:
                throw new IllegalArgumentException("Missing " + SpreadsheetComparators.UP + "/" + SpreadsheetComparators.DOWN);
            default:
                break;
        }

        return Lists.immutable(columnOrRowComparators);
    }

    private static SpreadsheetComparator<?> upOrDown(final int start,
                                                     final int end,
                                                     final String text,
                                                     final SpreadsheetComparator<?> comparator) {
        final SpreadsheetComparator<?> result;

        final String upOrDown = text.substring(
                start,
                end
        );
        switch (upOrDown) {
            case SpreadsheetComparators.UP:
                result = comparator;
                break;
            case SpreadsheetComparators.DOWN:
                result = SpreadsheetComparators.reverse(
                        comparator
                );
                break;
            default:
                throw new IllegalArgumentException(
                        "Missing " +
                                SpreadsheetComparators.UP +
                                "/" +
                                SpreadsheetComparators.DOWN +
                                " at " +
                                start
                );
        }

        return result;
    }

    public static SpreadsheetCellSpreadsheetComparators with(final SpreadsheetColumnOrRowReference columnOrRow,
                                                             final List<SpreadsheetComparator<?>> comparators) {

        return new SpreadsheetCellSpreadsheetComparators(
                Objects.requireNonNull(columnOrRow, "columnOrRows"),
                Lists.immutable(
                        Objects.requireNonNull(comparators, "comparators")
                )
        );
    }

    private SpreadsheetCellSpreadsheetComparators(final SpreadsheetColumnOrRowReference columnOrRow,
                                                  final List<SpreadsheetComparator<?>> comparators) {
        if (comparators.isEmpty()) {
            throw new IllegalArgumentException("Expected at least 1 comparator got none");
        }

        this.columnOrRow = columnOrRow;
        this.comparators = comparators;
    }

    int compare(final SpreadsheetCell left,
                final SpreadsheetCell right,
                final SpreadsheetComparatorContext context) {
        int result = Comparators.EQUAL;

        final Object leftValue = valueOf(left);
        final Object rightValue = valueOf(right);

        // try one by one, until a non equal match.
        for (final SpreadsheetComparator<?> comparator : this.comparators) {
            final Class<?> type = comparator.type();

            Object convertedLeftValue = null;
            if (null != leftValue) {
                convertedLeftValue = context.convert(
                        leftValue,
                        type
                ).orElseLeft(null);
            }

            Object convertedRightValue = null;
            if (null != rightValue) {
                convertedRightValue = context.convert(
                        rightValue,
                        type
                ).orElseLeft(null);
            }

            final boolean missingLeft = null == convertedLeftValue;
            final boolean missingRight = null == convertedRightValue;
            if (missingLeft || missingRight) {
                result = missingLeft && missingRight ?
                        Comparators.EQUAL :
                        missingLeft ?
                                        Comparators.MORE :
                                Comparators.LESS; // missing | nulls etc come AFTER
            } else {
                result = comparator.compare(
                        Cast.to(convertedLeftValue),
                        Cast.to(convertedRightValue)
                );
            }

            if (Comparators.EQUAL != result) {
                break;
            }
        }

        return result;
    }

    private static Object valueOf(final SpreadsheetCell cell) {
        return null == cell ?
                null :
                cell.formula().
                        value()
                        .orElse(null);
    }

    public SpreadsheetColumnOrRowReference columnOrRow() {
        return this.columnOrRow;
    }

    private final SpreadsheetColumnOrRowReference columnOrRow;

    public List<SpreadsheetComparator<?>> comparators() {
        return this.comparators;
    }

    private final List<SpreadsheetComparator<?>> comparators;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.columnOrRow,
                this.comparators
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetCellSpreadsheetComparators && this.equals0((SpreadsheetCellSpreadsheetComparators) other);
    }

    private boolean equals0(final SpreadsheetCellSpreadsheetComparators other) {
        return this.columnOrRow.equals(other.columnOrRow) &&
                this.comparators.equals(other.comparators);
    }

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .value(this.columnOrRow)
                .value(this.comparators)
                .build();
    }
}
