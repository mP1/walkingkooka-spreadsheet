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

package walkingkooka.spreadsheet;

import walkingkooka.Value;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.reference.CanReplaceReferences;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A container for cells within a {@link SpreadsheetCellRangeReference}.
 */
public final class SpreadsheetCellRange implements Value<Map<SpreadsheetCellReference, ?>>,
        TreePrintable {

    public static SpreadsheetCellRange with(final SpreadsheetCellRangeReference range,
                                            final Map<SpreadsheetCellReference, ?> value) {
        checkRange(range);
        Objects.requireNonNull(value, "value");

        final Map<SpreadsheetCellReference, ?> copy = Maps.immutable(value);

        checkValues(range, copy);

        return new SpreadsheetCellRange(
                range,
                copy
        );
    }

    private SpreadsheetCellRange(final SpreadsheetCellRangeReference range,
                                 final Map<SpreadsheetCellReference, ?> value) {
        this.range = range;
        this.value = value;
    }

    public SpreadsheetCellRangeReference range() {
        return this.range;
    }

    public SpreadsheetCellRange setRange(final SpreadsheetCellRangeReference range) {
        checkRange(range);

        return this.range.equals(range) ?
                this :
                this.setRange0(range);
    }

    private SpreadsheetCellRange setRange0(final SpreadsheetCellRangeReference range) {
        final Map<SpreadsheetCellReference, ?> value = this.value;

        if (false == range.containsAll(this.range)) {
            checkValues(
                    range,
                    value
            );
        }

        return new SpreadsheetCellRange(
                range,
                value
        );
    }

    private final SpreadsheetCellRangeReference range;

    private static SpreadsheetCellRangeReference checkRange(final SpreadsheetCellRangeReference range) {
        return Objects.requireNonNull(range, "range");
    }

    @Override
    public Map<SpreadsheetCellReference, ?> value() {
        return this.value;
    }

    public SpreadsheetCellRange setValue(final Map<SpreadsheetCellReference, ?> value) {
        checkRange(range);

        final Map<SpreadsheetCellReference, ?> copy = Maps.immutable(value);
        checkValues(this.range, copy);

        return this.value.equals(copy) ?
                this :
                new SpreadsheetCellRange(
                        range,
                        value
                );
    }

    private final Map<SpreadsheetCellReference, ?> value;

    private static void checkValues(final SpreadsheetCellRangeReference range,
                                    final Map<SpreadsheetCellReference, ?> value) {
        final Set<SpreadsheetCellReference> outOfBounds = value.keySet()
                .stream()
                .filter(c -> false == range.testCell(c))
                .collect(Collectors.toCollection(Sets::sorted));

        if (false == outOfBounds.isEmpty()) {
            throw new IllegalArgumentException(
                    "Found " +
                            outOfBounds.size() +
                            " cells out of range " +
                            range +
                            " got " +
                            outOfBounds.stream()
                                    .map(Object::toString)
                                    .collect(Collectors.joining(", "))
            );
        }
    }

    // move.............................................................................................................

    /**
     * Moves all the values from this range to the new given {@link SpreadsheetCellRangeReference}.
     * This will be used by a PASTE from the clipboard to a new range. The target could be smaller and the extra
     * cells will be clipped and lost.
     */
    public SpreadsheetCellRange move(final SpreadsheetCellRangeReference range) {
        checkRange(range);

        return this.range.equalsIgnoreReferenceKind(range) ?
                this.setRange(range) :
                move0(range);
    }

    private SpreadsheetCellRange move0(final SpreadsheetCellRangeReference to) {
        final SpreadsheetCellRangeReference from = this.range;

        final SpreadsheetCellReference fromBegin = from.begin();
        final SpreadsheetCellReference toBegin = to.begin();

        final Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>> mapper = SpreadsheetCellRangeMoveMapper.with(
                toBegin.column()
                        .value() -
                        fromBegin.column()
                                .value(),
                toBegin.row()
                        .value() -
                        fromBegin.row()
                                .value()
        );

        final Map<SpreadsheetCellReference, Object> cellToValue = Maps.sorted();

        for (final Entry<SpreadsheetCellReference, ?> cellAndValue : this.value.entrySet()) {
            final Optional<SpreadsheetCellReference> maybeCell = mapper.apply(cellAndValue.getKey());

            // destination could be out of bounds, ignore those.
            if (maybeCell.isPresent()) {
                final SpreadsheetCellReference cell = maybeCell.get();

                // destination range could be smaller, ignore values outside
                if (to.testCell(cell)) {
                    Object value = cellAndValue.getValue();
                    if (value instanceof CanReplaceReferences) {
                        final CanReplaceReferences<?> can = (CanReplaceReferences<?>) value;
                        value = can.replaceReferences(mapper);
                    }

                    cellToValue.put(
                            cell,
                            value
                    );
                }
            }
        }

        return new SpreadsheetCellRange(
                to,
                cellToValue
        );
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.range.toStringMaybeStar());

        printer.indent();
        {
            for (final Entry<SpreadsheetCellReference, ?> cellAndValue : this.value.entrySet()) {
                printer.println(
                        cellAndValue.getKey()
                                .toString()
                );

                printer.indent();
                {
                    TreePrintable.printTreeOrToString(
                            cellAndValue.getValue(),
                            printer
                    );
                    printer.lineStart();
                }
                printer.outdent();
            }
        }
        printer.outdent();
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.range,
                this.value
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetCellRange && this.equals0((SpreadsheetCellRange) other);
    }

    private boolean equals0(final SpreadsheetCellRange other) {
        return this.range.equals(other.range) &&
                this.value.equals(other.value);
    }

    @Override
    public String toString() {
        return this.range + " " + this.value;
    }
}
