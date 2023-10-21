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
 * Each of the {@link walkingkooka.spreadsheet.reference.SpreadsheetSelection} require an anchor to create a {@link SpreadsheetViewport}.
 * Not all combinations are valid for each of range.
 */
public enum SpreadsheetViewportAnchor implements HasUrlFragment {
    NONE,
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT,
    TOP,
    BOTTOM,
    LEFT,
    RIGHT;

    SpreadsheetViewportAnchor() {
        this.kebabText = CaseKind.kebabEnumName(this);
    }

    public String kebabText() {
        return this.kebabText;
    }

    private final String kebabText;

    SpreadsheetViewportAnchor setLeft() {
        return this.replace(
                RIGHT,
                LEFT
        );
    }

    SpreadsheetViewportAnchor setTop() {
        return this.replace(
                BOTTOM,
                TOP
        );
    }

    SpreadsheetViewportAnchor setRight() {
        return this.replace(
                LEFT,
                RIGHT
        );
    }

    SpreadsheetViewportAnchor setBottom() {
        return this.replace(
                TOP,
                BOTTOM
        );
    }

    private SpreadsheetViewportAnchor replace(final SpreadsheetViewportAnchor find,
                                              final SpreadsheetViewportAnchor replace) {
        return valueOf(
                this.name()
                        .replace(
                                find.name(), replace.name()
                        )
        );
    }

    /**
     * Returns the {@link SpreadsheetSelection} opposite the given {@link SpreadsheetViewportAnchor}
     */
    final SpreadsheetSelection selection(final SpreadsheetSelection selection) {
        if (selection.isLabelName()) {
            throw new IllegalArgumentException("Label not supported: " + selection);
        }

        return this == NONE ?
                selection :
                selection.isCellRange() ?
                        this.cell(selection.toCellRange()) :
                        selection.isColumnReferenceRange() ?
                                this.column(selection.toColumnRange()) :
                                selection.isRowReferenceRange() ?
                                        this.row(selection.toRowRange()) :
                                        this.selectionFail(selection);
    }

    private SpreadsheetSelection selectionFail(final SpreadsheetSelection selection) {
        throw new UnsupportedOperationException(selection.toString());
    }

    /**
     * Uses this anchor to select the {@link SpreadsheetCellReference} that will be moved.
     */
    final SpreadsheetCellReference cell(final SpreadsheetCellRange range) {
        // this.failIfNone(); unnecessary #column will fail if NONE.

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
     * Uses this anchor to select the {@link SpreadsheetCellReference} that will remain fixed.
     */
    final SpreadsheetCellReference fixedCell(final SpreadsheetCellRange range) {
        final SpreadsheetColumnReference column = fixedColumn(range.columnRange());
        final SpreadsheetRowReference row = fixedRow(range.rowRange());

        return column.setRow(row);
    }

    /**
     * Uses this anchor to select the {@link SpreadsheetColumnReference} that will be moved.
     */
    final SpreadsheetColumnReference column(final SpreadsheetColumnReferenceRange range) {
        this.failIfNone();

        return this.isLeft() ?
                range.end() :
                range.begin();
    }

    /**
     * Uses this anchor to select the {@link SpreadsheetColumnReference} that will remain fixed.
     */
    final SpreadsheetColumnReference fixedColumn(final SpreadsheetColumnReferenceRange range) {
        return other(
                range,
                this.column(range)
        );
    }

    /**
     * Uses this anchor to select the {@link SpreadsheetRowReference} that will be moved.
     */
    final SpreadsheetRowReference row(final SpreadsheetRowReferenceRange range) {
        this.failIfNone();

        return this.isTop() ?
                range.end() :
                range.begin();
    }

    /**
     * Uses this anchor to select the {@link SpreadsheetRowReference} that will remain fixed.
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

    public final static SpreadsheetViewportAnchor CELL = NONE;
    public final static SpreadsheetViewportAnchor COLUMN = NONE;
    public final static SpreadsheetViewportAnchor ROW = NONE;

    public final static SpreadsheetViewportAnchor COLUMN_RANGE = RIGHT;
    public final static SpreadsheetViewportAnchor ROW_RANGE = BOTTOM;

    public final static SpreadsheetViewportAnchor CELL_RANGE = valueOf(ROW_RANGE.name() + "_" + COLUMN_RANGE.name());

    /**
     * Accepts text that has a more pretty form of any {@link SpreadsheetViewportAnchor enum value}.
     * The text is identical to the enum name but in lower case and underscore replaced with dash.
     * <br>
     * {@link #TOP_LEFT} = <pre>top-left</pre>.
     */
    public static SpreadsheetViewportAnchor parse(final String text) {
        CharSequences.failIfNullOrEmpty(text, "text");

        for (final SpreadsheetViewportAnchor navigation : values()) {
            if (navigation.kebabText.equals(text)) {
                return navigation;
            }
        }

        throw new IllegalArgumentException("Invalid text=" + CharSequences.quoteAndEscape(text));
    }
}