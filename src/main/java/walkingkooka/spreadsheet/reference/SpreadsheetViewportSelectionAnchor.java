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

package walkingkooka.spreadsheet.reference;

import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.text.CaseKind;
import walkingkooka.text.CharSequences;

/**
 * Each of the {@link walkingkooka.spreadsheet.reference.SpreadsheetSelection} require an anchor to create a {@link SpreadsheetViewportSelection}.
 * Not all combinations are valid for each of range.
 */
public enum SpreadsheetViewportSelectionAnchor implements HasUrlFragment {
    NONE,
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT,
    TOP,
    BOTTOM,
    LEFT,
    RIGHT;

    SpreadsheetViewportSelectionAnchor() {
        this.kebabText = CaseKind.kebabEnumName(this);
    }

    public String kebabText() {
        return this.kebabText;
    }

    private final String kebabText;

    SpreadsheetViewportSelectionAnchor setLeft() {
        return this.replace(
                RIGHT,
                LEFT
        );
    }

    SpreadsheetViewportSelectionAnchor setTop() {
        return this.replace(
                BOTTOM,
                TOP
        );
    }

    SpreadsheetViewportSelectionAnchor setRight() {
        return this.replace(
                LEFT,
                RIGHT
        );
    }

    SpreadsheetViewportSelectionAnchor setBottom() {
        return this.replace(
                TOP,
                BOTTOM
        );
    }

    private SpreadsheetViewportSelectionAnchor replace(final SpreadsheetViewportSelectionAnchor find,
                                                       final SpreadsheetViewportSelectionAnchor replace) {
        return valueOf(
                this.name()
                        .replace(
                                find.name(), replace.name()
                        )
        );
    }

    /**
     * Uses this anchor to select the cell-range that will be moved.
     */
    final SpreadsheetCellReference cell(final SpreadsheetCellRange range) {
        this.failIfNone();

        return this.column(
                        range.columnRange()
                )
                .setRow(
                        this.row(
                                range.rowRange()
                        )
                );
    }

    /**
     * Uses this anchor to select the {@link SpreadsheetCellRange} that will remain fixed.
     */
    final SpreadsheetCellReference fixedCell(final SpreadsheetCellRange range) {
        final SpreadsheetColumnReference column = fixedColumn(range.columnRange());
        final SpreadsheetRowReference row = fixedRow(range.rowRange());

        return column.setRow(row);
    }

    /**
     * Uses this anchor to select the {@link SpreadsheetColumnReferenceRange} that will be moved.
     */
    final SpreadsheetColumnReference column(final SpreadsheetColumnReferenceRange range) {
        this.failIfNone();

        return this.isLeft() ?
                range.end() :
                range.begin();
    }

    /**
     * Uses this anchor to select the {@link SpreadsheetColumnReferenceRange} that will remain fixed.
     */
    final SpreadsheetColumnReference fixedColumn(final SpreadsheetColumnReferenceRange range) {
        return other(
                range,
                this.column(range)
        );
    }

    /**
     * Uses this anchor to select the {@link SpreadsheetRowReferenceRange} that will be moved.
     */
    final SpreadsheetRowReference row(final SpreadsheetRowReferenceRange range) {
        this.failIfNone();

        return this.isTop() ?
                range.end() :
                range.begin();
    }

    /**
     * Uses this anchor to select the {@link SpreadsheetRowReferenceRange} that will remain fixed.
     */
    final SpreadsheetRowReference fixedRow(final SpreadsheetRowReferenceRange range) {
        return other(
                range,
                this.row(range)
        );
    }

    private boolean isLeft() {
        return this == LEFT || this == TOP_LEFT || this == BOTTOM_LEFT;
    }

    private boolean isTop() {
        return this == TOP || this == TOP_LEFT || this == TOP_RIGHT;
    }

    private void failIfNone() {
        if (this == NONE) {
            throw new IllegalArgumentException("Invalid operation for " + this);
        }
    }

    @Override
    public UrlFragment urlFragment() {
        return UrlFragment.with(
                CaseKind.SNAKE.change(
                        this.name(),
                        CaseKind.KEBAB
                )
        );
    }

    /**
     * Helper that returns the other / opposite range given the inputs.
     */
    private static <RR extends SpreadsheetColumnOrRowReference & Comparable<RR>> RR other(final SpreadsheetColumnOrRowReferenceRange<RR> range,
                                                                                          final RR bound) {
        final RR begin = range.begin();
        return begin != bound ?
                begin :
                range.end();
    }

    public final static SpreadsheetViewportSelectionAnchor CELL = NONE;
    public final static SpreadsheetViewportSelectionAnchor COLUMN = NONE;
    public final static SpreadsheetViewportSelectionAnchor ROW = NONE;

    public final static SpreadsheetViewportSelectionAnchor COLUMN_RANGE = RIGHT;
    public final static SpreadsheetViewportSelectionAnchor ROW_RANGE = BOTTOM;

    public final static SpreadsheetViewportSelectionAnchor CELL_RANGE = valueOf(ROW_RANGE.name() + "_" + COLUMN_RANGE.name());

    /**
     * Accepts text that has a more pretty form of any {@link SpreadsheetViewportSelectionAnchor enum value}.
     * The text is identical to the enum name but in lower case and underscore replaced with dash.
     * <br>
     * {@link #TOP_LEFT} = <pre>top-left</pre>.
     */
    public static SpreadsheetViewportSelectionAnchor from(final String text) {
        CharSequences.failIfNullOrEmpty(text, "anchor");

        for (final SpreadsheetViewportSelectionAnchor navigation : values()) {
            if (navigation.kebabText.equals(text)) {
                return navigation;
            }
        }

        throw new IllegalArgumentException("Invalid text=" + CharSequences.quoteAndEscape(text));
    }
}
