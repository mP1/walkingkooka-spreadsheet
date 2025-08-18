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

package walkingkooka.spreadsheet.viewport;

import walkingkooka.NeverError;
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.CaseKind;
import walkingkooka.text.CharSequences;

import java.util.Objects;

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
        final String kebabText = CaseKind.SNAKE.change(
            this.name(),
            CaseKind.KEBAB
        );
        this.kebabText = kebabText;
        this.urlFragment = kebabText.equals("none") ?
            UrlFragment.EMPTY :
            UrlFragment.with(kebabText);
    }

    public String kebabText() {
        return this.kebabText;
    }

    private final String kebabText;

    public SpreadsheetViewportAnchor setLeft() {
        return this.replace(
            RIGHT,
            LEFT
        );
    }

    public SpreadsheetViewportAnchor setTop() {
        return this.replace(
            BOTTOM,
            TOP
        );
    }

    public SpreadsheetViewportAnchor setRight() {
        return this.replace(
            LEFT,
            RIGHT
        );
    }

    public SpreadsheetViewportAnchor setBottom() {
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
     * Returns the opposite anchor.
     */
    public SpreadsheetViewportAnchor opposite() {
        final SpreadsheetViewportAnchor opposite;

        switch (this) {
            case NONE:
                opposite = NONE;
                break;
            case TOP:
                opposite = BOTTOM;
                break;
            case TOP_LEFT:
                opposite = BOTTOM_RIGHT;
                break;
            case TOP_RIGHT:
                opposite = BOTTOM_LEFT;
                break;
            case BOTTOM:
                opposite = TOP;
                break;
            case BOTTOM_LEFT:
                opposite = TOP_RIGHT;
                break;
            case BOTTOM_RIGHT:
                opposite = TOP_LEFT;
                break;
            case LEFT:
                opposite = RIGHT;
                break;
            case RIGHT:
                opposite = LEFT;
                break;
            default:
                opposite = NeverError.unhandledEnum(this, values());
                break;
        }

        return opposite;
    }

    /**
     * Returns the {@link SpreadsheetSelection} for the given {@link SpreadsheetViewportAnchor}
     */
    public final SpreadsheetSelection selection(final SpreadsheetSelection selection) {
        Objects.requireNonNull(selection, "selection");

        if (selection.isLabelName()) {
            throw new IllegalArgumentException("Label not supported: " + selection);
        }

        return this == NONE ?
            selection :
            selection.isCellRange() ?
                this.cell(selection.toCellRange()) :
                selection.isColumnRange() ?
                    this.column(selection.toColumnRange()) :
                    selection.isRowRange() ?
                        this.row(selection.toRowRange()) :
                        this.selectionFail(selection);
    }

    private SpreadsheetSelection selectionFail(final SpreadsheetSelection selection) {
        throw new UnsupportedOperationException(selection.toString());
    }

    /**
     * Uses this anchor to select the {@link SpreadsheetCellReference} from the given {@link SpreadsheetCellRangeReference}..
     */
    public final SpreadsheetCellReference cell(final SpreadsheetCellRangeReference range) {
        Objects.requireNonNull(range, "range");

        final SpreadsheetColumnReference column = this.column(range.columnRange());
        final SpreadsheetRowReference row = this.row(range.rowRange());

        return column.setRow(row);
    }

    /**
     * Uses this anchor to select the {@link SpreadsheetColumnReference} that will remain fixed.
     */
    public final SpreadsheetColumnReference column(final SpreadsheetColumnRangeReference range) {
        Objects.requireNonNull(range, "range");

        this.failIfNone();

        return this.isLeft() ?
            range.begin() :
            range.end();
    }

    /**
     * Uses this anchor to select the {@link SpreadsheetRowReference}
     */
    public final SpreadsheetRowReference row(final SpreadsheetRowRangeReference range) {
        Objects.requireNonNull(range, "range");

        this.failIfNone();

        return this.isTop() ?
            range.begin() :
            range.end();
    }

    private boolean isLeft() {
        return this == LEFT || this == TOP_LEFT || this == BOTTOM_LEFT;
    }

    private boolean isRight() {
        return this == RIGHT || this == TOP_RIGHT || this == BOTTOM_RIGHT;
    }

    private boolean isTop() {
        return this == TOP || this == TOP_LEFT || this == TOP_RIGHT;
    }

    private boolean isBottom() {
        return this == BOTTOM || this == BOTTOM_LEFT || this == BOTTOM_RIGHT;
    }

    private void failIfNone() {
        if (this == NONE) {
            throw new IllegalArgumentException("Invalid operation for " + this);
        }
    }

    @Override
    public UrlFragment urlFragment() {
        return this.urlFragment;
    }

    private final UrlFragment urlFragment;

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

    /**
     * Given this anchor returns a column range compatible anchor. This is useful for converting a cell-range and its
     * anchor to a column-range. Note NONE will return NONE, this allows code to work converting cell/cell-range to column/column-range.
     */
    public final SpreadsheetViewportAnchor toColumnOrColumnRangeAnchor() {
        final SpreadsheetViewportAnchor column;

        if (this == NONE) {
            column = NONE;
        } else {
            if (this.isLeft()) {
                column = LEFT;
            } else {
                if (this.isRight()) {
                    column = RIGHT;
                } else {
                    throw new IllegalArgumentException("Cannot convert " + this + " to a column range compatible anchor");
                }
            }
        }

        return column;
    }

    /**
     * Given this anchor returns a row range compatible anchor. This is useful for converting a cell-range and its
     * anchor to a row-range. Note NONE will return NONE, this allows code to work converting cell/cell-range to row/row-range.
     */
    public final SpreadsheetViewportAnchor toRowOrRowRangeAnchor() {
        final SpreadsheetViewportAnchor row;

        if (this == NONE) {
            row = NONE;
        } else {
            if (this.isTop()) {
                row = TOP;
            } else {
                if (this.isBottom()) {
                    row = BOTTOM;
                } else {
                    throw new IllegalArgumentException("Cannot convert " + this + " to a row range compatible anchor");
                }
            }
        }

        return row;
    }
}
