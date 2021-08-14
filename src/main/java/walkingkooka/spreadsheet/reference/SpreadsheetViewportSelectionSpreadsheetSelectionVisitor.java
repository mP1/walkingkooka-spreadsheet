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

import java.util.EnumSet;
import java.util.stream.Collectors;

/**
 * A {@link SpreadsheetSelectionVisitor} used by {@link SpreadsheetViewportSelection} to verify a selection and anchor combination.
 */
final class SpreadsheetViewportSelectionSpreadsheetSelectionVisitor extends SpreadsheetSelectionVisitor {

    static void checkAnchor(final SpreadsheetSelection selection, final SpreadsheetViewportSelectionAnchor anchor) {
        new SpreadsheetViewportSelectionSpreadsheetSelectionVisitor(anchor)
                .accept(selection);
    }

    // visible for teesting
    SpreadsheetViewportSelectionSpreadsheetSelectionVisitor(final SpreadsheetViewportSelectionAnchor anchor) {
        this.anchor = anchor;
    }

    @Override
    protected void visit(final SpreadsheetCellRange range) {
        this.checkAnchor(
                range,
                CELL
        );
    }

    private final static EnumSet<SpreadsheetViewportSelectionAnchor> CELL = EnumSet.of(
            SpreadsheetViewportSelectionAnchor.LEFT,
            SpreadsheetViewportSelectionAnchor.RIGHT,
            SpreadsheetViewportSelectionAnchor.TOP,
            SpreadsheetViewportSelectionAnchor.TOP_LEFT,
            SpreadsheetViewportSelectionAnchor.TOP_RIGHT,
            SpreadsheetViewportSelectionAnchor.BOTTOM,
            SpreadsheetViewportSelectionAnchor.BOTTOM_LEFT,
            SpreadsheetViewportSelectionAnchor.BOTTOM_RIGHT
    );

    @Override
    protected void visit(final SpreadsheetCellReference reference) {
        this.checkWithoutAnchor(reference);
    }

    @Override
    protected void visit(final SpreadsheetColumnReference reference) {
        this.checkWithoutAnchor(reference);
    }

    @Override
    protected void visit(final SpreadsheetColumnReferenceRange range) {
        this.checkAnchor(
                range,
                COLUMN
        );
    }

    private final static EnumSet<SpreadsheetViewportSelectionAnchor> COLUMN = EnumSet.of(
            SpreadsheetViewportSelectionAnchor.LEFT,
            SpreadsheetViewportSelectionAnchor.RIGHT
    );

    @Override
    protected void visit(final SpreadsheetLabelName label) {
        throw new IllegalArgumentException("Labels cannot be a viewport range=" + label);
    }

    @Override
    protected void visit(final SpreadsheetRowReference reference) {
        this.checkWithoutAnchor(reference);
    }

    @Override
    protected void visit(final SpreadsheetRowReferenceRange range) {
        this.checkAnchor(
                range,
                ROW
        );
    }

    private final static EnumSet<SpreadsheetViewportSelectionAnchor> ROW = EnumSet.of(
            SpreadsheetViewportSelectionAnchor.TOP,
            SpreadsheetViewportSelectionAnchor.BOTTOM
    );

    private void checkWithoutAnchor(final SpreadsheetSelection selection) {
        final SpreadsheetViewportSelectionAnchor anchor = this.anchor;
        if (null != anchor) {
            throw new IllegalArgumentException(selection + " must not have an anchor got " + anchor);
        }
    }

    private void checkAnchor(final SpreadsheetSelection selection,
                             final EnumSet<SpreadsheetViewportSelectionAnchor> anyOf) {
        final SpreadsheetViewportSelectionAnchor anchor = this.anchor;
        if (null == anchor) {
            throw new IllegalArgumentException(selection + " missing anchor");
        }

        if (!anyOf.contains(anchor)) {
            throw new IllegalArgumentException(selection + " contains invalid anchor " + anchor +
                    ", valid anchors: " +
                    anyOf.stream().map(Object::toString).collect(Collectors.joining(", ")));
        }
    }

    private final SpreadsheetViewportSelectionAnchor anchor;

    @Override
    public String toString() {
        return String.valueOf(this.anchor);
    }
}
