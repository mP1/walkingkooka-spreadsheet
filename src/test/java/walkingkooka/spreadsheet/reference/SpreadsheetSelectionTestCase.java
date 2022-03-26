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

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.predicate.PredicateTesting2;
import walkingkooka.predicate.Predicates;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.IsMethodTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStores;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStores;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class SpreadsheetSelectionTestCase<S extends SpreadsheetSelection> implements ClassTesting2<S>,
        HashCodeEqualsDefinedTesting2<S>,
        JsonNodeMarshallingTesting<S>,
        IsMethodTesting<S>,
        ParseStringTesting<S>,
        PredicateTesting2<S, SpreadsheetCellReference>,
        ToStringTesting<S>,
        TreePrintableTesting {

    SpreadsheetSelectionTestCase() {
        super();
    }

    final void testCellRangeAndCheck(final S selection,
                                     final SpreadsheetCellRange range,
                                     final boolean expected) {
        this.checkEquals(
                expected,
                selection.testCellRange(range),
                () -> selection + " testCellRange " + range
        );
    }

    // toRelative.......................................................................................................

    final void toRelativeAndCheck(final S selection) {
        this.toRelativeAndCheck(selection, selection);
    }

    final void toRelativeAndCheck(final S selection,
                                  final S expected) {
        if (expected.equals(selection)) {
            assertSame(
                    expected,
                    selection.toRelative(),
                    () -> selection.toString()
            );
        } else {
            this.checkEquals(
                    expected,
                    selection.toRelative(),
                    () -> selection.toString()
            );
        }
    }

    // defaultAnchor....................................................................................................

    @Test
    final public void testDefaultAnchor() {
        final S selection = this.createSelection();
        final SpreadsheetViewportSelectionAnchor anchor = selection.defaultAnchor();
        this.checkNotEquals(null, anchor, "anchor");
    }

    @Test
    final public void testDefaultAnchorThenSetAnchor() {
        final S selection = this.createSelection();
        final SpreadsheetViewportSelectionAnchor anchor = selection.defaultAnchor();
        this.checkNotEquals(null, anchor, "anchor");

        final SpreadsheetViewportSelection viewportSelection = selection.setAnchor(anchor);
        this.checkEquals(anchor, viewportSelection.anchor(), "anchor");
        this.checkEquals(selection, viewportSelection.selection(), "selection");
    }

    // isHidden.........................................................................................................

    final void isHiddenAndCheck(final String selection,
                                final Predicate<SpreadsheetColumnReference> hiddenColumnTester,
                                final Predicate<SpreadsheetRowReference> hiddenRowTester,
                                final boolean expected) {
        this.isHiddenAndCheck(
                this.parseString(selection),
                hiddenColumnTester,
                hiddenRowTester,
                expected
        );
    }

    final void isHiddenAndCheck(final S selection,
                                final Predicate<SpreadsheetColumnReference> hiddenColumnTester,
                                final Predicate<SpreadsheetRowReference> hiddenRowTester,
                                final boolean expected) {
        this.checkEquals(
                expected,
                selection.isHidden(hiddenColumnTester, hiddenRowTester),
                () -> "isHidden " + selection
        );
    }

    // left.............................................................................................................

    final void leftAndCheck(final String selection) {
        this.leftAndCheck(
                selection,
                selection
        );
    }

    final void leftAndCheck(final String selection,
                            final String expected) {
        final S parsed = this.parseString(selection);
        this.leftAndCheck(
                parsed,
                parsed.defaultAnchor(),
                this.parseString(expected)
        );
    }

    final void leftAndCheck(final String selection,
                            final SpreadsheetViewportSelectionAnchor anchor) {
        this.leftAndCheck(
                selection,
                anchor,
                selection
        );
    }

    final void leftAndCheck(final String text,
                            final SpreadsheetViewportSelectionAnchor anchor,
                            final String expected) {
        this.leftAndCheck(
                this.parseString(text),
                anchor,
                this.parseString(expected)
        );
    }

    final void leftAndCheck(final S selection,
                            final SpreadsheetSelection expected) {
        this.leftAndCheck(
                selection,
                selection.defaultAnchor(),
                expected
        );
    }

    final void leftAndCheck(final S selection,
                            final SpreadsheetViewportSelectionAnchor anchor,
                            final SpreadsheetSelection expected) {
        this.leftAndCheck(
                selection,
                anchor,
                columnStore(),
                expected
        );
    }

    final void leftAndCheck(final S selection,
                            final SpreadsheetViewportSelectionAnchor anchor,
                            final SpreadsheetColumnStore columnStore,
                            final SpreadsheetSelection expected) {
        this.checkEquals(
                expected.simplify(),
                selection.left(anchor, columnStore, SpreadsheetRowStores.fake()),
                () -> selection + " anchor=" + anchor + " navigate left"
        );
    }

    // up.............................................................................................................

    final void upAndCheck(final String selection) {
        this.upAndCheck(
                selection,
                selection
        );
    }

    final void upAndCheck(final String selection,
                          final String expected) {
        final S parsed = this.parseString(selection);
        this.upAndCheck(
                parsed,
                parsed.defaultAnchor(),
                this.parseString(expected)
        );
    }

    final void upAndCheck(final String selection,
                          final SpreadsheetViewportSelectionAnchor anchor) {
        this.upAndCheck(
                selection,
                anchor,
                selection
        );
    }

    final void upAndCheck(final String selection,
                          final SpreadsheetViewportSelectionAnchor anchor,
                          final String expected) {
        this.upAndCheck(
                this.parseString(selection),
                anchor,
                this.parseString(expected)
        );
    }

    final void upAndCheck(final S selection,
                          final SpreadsheetSelection expected) {
        this.upAndCheck(
                selection,
                selection.defaultAnchor(),
                expected
        );
    }

    final void upAndCheck(final S selection,
                          final SpreadsheetViewportSelectionAnchor anchor,
                          final SpreadsheetSelection expected) {
        this.upAndCheck(
                selection,
                anchor,
                rowStore(),
                expected
        );
    }

    final void upAndCheck(final S selection,
                          final SpreadsheetViewportSelectionAnchor anchor,
                          final SpreadsheetRowStore rowStore,
                          final SpreadsheetSelection expected) {
        this.checkEquals(
                expected.simplify(),
                selection.up(anchor, SpreadsheetColumnStores.fake(), rowStore),
                () -> selection + " anchor=" + anchor + " navigate up"
        );
    }

    // right.............................................................................................................

    final void rightAndCheck(final String selection) {
        this.rightAndCheck(
                selection,
                selection
        );
    }

    final void rightAndCheck(final S selection) {
        this.rightAndCheck(
                selection,
                selection
        );
    }

    final void rightAndCheck(final String selection,
                             final String expected) {
        final S parsed = this.parseString(selection);

        this.rightAndCheck(
                parsed,
                parsed.defaultAnchor(),
                this.parseString(expected)
        );
    }

    final void rightAndCheck(final String selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final String expected) {
        this.rightAndCheck(
                selection,
                anchor,
                this.parseString(expected)
        );
    }

    final void rightAndCheck(final S selection,
                             final SpreadsheetSelection expected) {
        this.rightAndCheck(
                selection,
                selection.defaultAnchor(),
                expected
        );
    }

    final void rightAndCheck(final String selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final SpreadsheetSelection expected) {
        this.rightAndCheck(
                this.parseString(selection),
                anchor,
                expected
        );
    }

    final void rightAndCheck(final S selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final SpreadsheetSelection expected) {
        this.rightAndCheck(
                selection,
                anchor,
                columnStore(),
                expected
        );
    }

    final void rightAndCheck(final S selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final SpreadsheetColumnStore columnStore,
                             final SpreadsheetSelection expected) {
        this.checkEquals(
                expected.simplify(),
                selection.right(anchor, columnStore, SpreadsheetRowStores.fake()),
                () -> selection + " anchor=" + anchor + " navigate right"
        );
    }

    // down.............................................................................................................

    final void downAndCheck(final String selection) {
        this.downAndCheck(
                selection,
                selection
        );
    }

    final void downAndCheck(final S selection) {
        this.downAndCheck(
                selection,
                selection
        );
    }

    final void downAndCheck(final String selection,
                            final String expected) {
        final S parsed = this.parseString(selection);

        this.downAndCheck(
                parsed,
                parsed.defaultAnchor(),
                this.parseString(expected)
        );
    }

    final void downAndCheck(final String selection,
                            final SpreadsheetViewportSelectionAnchor anchor) {
        this.downAndCheck(
                selection,
                anchor,
                selection
        );
    }

    final void downAndCheck(final String selection,
                            final SpreadsheetViewportSelectionAnchor anchor,
                            final String expected) {
        this.downAndCheck(
                this.parseString(selection),
                anchor,
                this.parseString(expected)
        );
    }

    final void downAndCheck(final S selection,
                            final SpreadsheetSelection expected) {
        this.downAndCheck(
                selection,
                selection.defaultAnchor(),
                expected
        );
    }

    final void downAndCheck(final String selection,
                            final SpreadsheetViewportSelectionAnchor anchor,
                            final SpreadsheetSelection expected) {
        this.downAndCheck(
                this.parseString(selection),
                anchor,
                expected
        );
    }

    final void downAndCheck(final S selection,
                            final SpreadsheetViewportSelectionAnchor anchor,
                            final SpreadsheetSelection expected) {
        this.downAndCheck(
                selection,
                anchor,
                rowStore(),
                expected
        );
    }

    final void downAndCheck(final S selection,
                            final SpreadsheetViewportSelectionAnchor anchor,
                            final SpreadsheetRowStore rowStore,
                            final SpreadsheetSelection expected) {
        this.checkEquals(
                expected.simplify(),
                selection.down(anchor, SpreadsheetColumnStores.fake(), rowStore),
                () -> selection + " anchor=" + anchor + " navigate down"
        );
    }

    private SpreadsheetColumnStore columnStore() {
        return SpreadsheetColumnStores.treeMap();
    }

    private SpreadsheetRowStore rowStore() {
        return SpreadsheetRowStores.treeMap();
    }

    // extendRange......................................................................................................

    final void extendRangeAndCheck(final String selection) {
        this.extendRangeAndCheck(
                selection,
                selection
        );
    }


    final void extendRangeAndCheck(final String selection,
                                   final String moved) {
        this.extendRangeAndCheck(
                selection,
                moved,
                SpreadsheetViewportSelectionAnchor.NONE
        );
    }

    final void extendRangeAndCheck(final String selection,
                                   final String moved,
                                   final SpreadsheetViewportSelectionAnchor anchor) {
        final S parsed = this.parseString(selection);

        this.extendRangeAndCheck(
                parsed,
                this.parseString(moved).simplify(),
                anchor,
                parsed
        );
    }

    final void extendRangeAndCheck(final String selection,
                                   final String moved,
                                   final String expected) {
        this.extendRangeAndCheck(
                selection,
                moved,
                SpreadsheetViewportSelectionAnchor.NONE,
                expected
        );
    }

    final void extendRangeAndCheck(final String selection,
                                   final String moved,
                                   final SpreadsheetViewportSelectionAnchor anchor,
                                   final String expected) {
        this.extendRangeAndCheck(
                this.parseString(selection),
                this.parseString(moved).simplify(),
                anchor,
                this.parseRange(expected)
        );
    }

    final void extendRangeAndCheck(final S selection,
                                   final SpreadsheetSelection moved,
                                   final SpreadsheetViewportSelectionAnchor anchor,
                                   final SpreadsheetSelection expected) {
        this.checkEquals(
                true,
                moved instanceof SpreadsheetCellReference || moved instanceof SpreadsheetColumnReference || moved instanceof SpreadsheetRowReference,
                () -> moved + " must be either cell/column/row"
        );
        this.checkEquals(
                expected.simplify(),
                selection.extendRange(moved, anchor),
                () -> selection + " extendRange " + moved + " " + anchor
        );
    }

    abstract SpreadsheetSelection parseRange(final String range);

    // extendLeft.......................................................................................................

    final void extendLeftAndCheck(final String selection) {
        this.extendLeftAndCheck(
                this.parseString(selection)
        );
    }

    final void extendLeftAndCheck(final String selection,
                                  final SpreadsheetViewportSelectionAnchor anchor) {
        this.extendLeftAndCheck(
                this.parseString(selection),
                anchor
        );
    }

    final void extendLeftAndCheck(final String selection,
                                  final SpreadsheetViewportSelectionAnchor anchor,
                                  final String expected) {
        final SpreadsheetSelection parsed = this.parseRange(expected)
                .simplify();

        this.extendLeftAndCheck(
                this.parseString(selection),
                anchor,
                parsed.setAnchor(parsed.defaultAnchor())
        );
    }

    final void extendLeftAndCheck(final S selection) {
        this.extendLeftAndCheck(
                selection,
                selection.defaultAnchor()
        );
    }

    final void extendLeftAndCheck(final S selection,
                                  final SpreadsheetViewportSelectionAnchor anchor) {
        this.extendLeftAndCheck(
                selection,
                anchor,
                selection.setAnchor(anchor)
        );
    }

    final void extendLeftAndCheck(final String selection,
                                  final String expectedSelection,
                                  final SpreadsheetViewportSelectionAnchor expectedAnchor) {
        final S parsed = this.parseString(selection);

        this.extendLeftAndCheck(
                parsed,
                parsed.defaultAnchor(),
                this.parseRange(expectedSelection).setAnchor(expectedAnchor)
        );
    }

    final void extendLeftAndCheck(final String text,
                                  final SpreadsheetViewportSelectionAnchor anchor,
                                  final String expected,
                                  final SpreadsheetViewportSelectionAnchor expectedAnchor) {
        this.extendLeftAndCheck(
                this.parseString(text),
                anchor,
                this.parseRange(expected)
                        .simplify()
                        .setAnchor(expectedAnchor)
        );
    }

    final void extendLeftAndCheck(final S selection,
                                  final SpreadsheetViewportSelectionAnchor anchor,
                                  final SpreadsheetViewportSelection expected) {
        this.extendLeftAndCheck(
                selection,
                anchor,
                columnStore(),
                expected
        );
    }

    final void extendLeftAndCheck(final S selection,
                                  final SpreadsheetViewportSelectionAnchor anchor,
                                  final SpreadsheetColumnStore columnStore,
                                  final SpreadsheetViewportSelection expected) {
        this.checkEquals(
                expected,
                selection.extendLeft(anchor, columnStore, SpreadsheetRowStores.fake()),
                () -> selection + " anchor=" + anchor + " navigate extendLeft"
        );
    }

    // extendUp.......................................................................................................

    final void extendUpAndCheck(final String selection) {
        this.extendUpAndCheck(
                this.parseString(selection)
        );
    }

    final void extendUpAndCheck(final String selection,
                                final SpreadsheetViewportSelectionAnchor anchor) {
        this.extendUpAndCheck(
                this.parseString(selection),
                anchor
        );
    }

    final void extendUpAndCheck(final String selection,
                                final SpreadsheetViewportSelectionAnchor anchor,
                                final String expected) {
        final SpreadsheetSelection parsed = this.parseRange(expected)
                .simplify();

        this.extendUpAndCheck(
                this.parseString(selection),
                anchor,
                parsed.setAnchor(parsed.defaultAnchor())
        );
    }

    final void extendUpAndCheck(final S selection) {
        this.extendUpAndCheck(
                selection,
                selection.defaultAnchor()
        );
    }

    final void extendUpAndCheck(final S selection,
                                final SpreadsheetViewportSelectionAnchor anchor) {
        this.extendUpAndCheck(
                selection,
                anchor,
                selection.setAnchor(anchor)
        );
    }

    final void extendUpAndCheck(final String selection,
                                final String expectedSelection,
                                final SpreadsheetViewportSelectionAnchor expectedAnchor) {
        final S parsed = this.parseString(selection);

        this.extendUpAndCheck(
                parsed,
                parsed.defaultAnchor(),
                this.parseRange(expectedSelection).setAnchor(expectedAnchor)
        );
    }

    final void extendUpAndCheck(final String text,
                                final SpreadsheetViewportSelectionAnchor anchor,
                                final String expected,
                                final SpreadsheetViewportSelectionAnchor expectedAnchor) {
        this.extendUpAndCheck(
                this.parseString(text),
                anchor,
                this.parseRange(expected)
                        .simplify()
                        .setAnchor(expectedAnchor)
        );
    }

    final void extendUpAndCheck(final S selection,
                                final SpreadsheetViewportSelectionAnchor anchor,
                                final SpreadsheetViewportSelection expected) {
        this.extendUpAndCheck(
                selection,
                anchor,
                rowStore(),
                expected
        );
    }

    final void extendUpAndCheck(final S selection,
                                final SpreadsheetViewportSelectionAnchor anchor,
                                final SpreadsheetRowStore rowStore,
                                final SpreadsheetViewportSelection expected) {
        this.checkEquals(
                expected,
                selection.extendUp(anchor, SpreadsheetColumnStores.fake(), rowStore),
                () -> selection + " anchor=" + anchor + " navigate extendUp"
        );
    }

    // extendRight.......................................................................................................

    final void extendRightAndCheck(final String selection) {
        this.extendRightAndCheck(
                this.parseString(selection)
        );
    }

    final void extendRightAndCheck(final String selection,
                                   final SpreadsheetViewportSelectionAnchor anchor) {
        this.extendRightAndCheck(
                this.parseString(selection),
                anchor
        );
    }

    final void extendRightAndCheck(final String selection,
                                   final SpreadsheetViewportSelectionAnchor anchor,
                                   final String expected) {
        final SpreadsheetSelection parsed = this.parseRange(expected)
                .simplify();

        this.extendRightAndCheck(
                this.parseString(selection),
                anchor,
                parsed.setAnchor(parsed.defaultAnchor())
        );
    }

    final void extendRightAndCheck(final S selection) {
        this.extendRightAndCheck(
                selection,
                selection.defaultAnchor()
        );
    }

    final void extendRightAndCheck(final S selection,
                                   final SpreadsheetViewportSelectionAnchor anchor) {
        this.extendRightAndCheck(
                selection,
                anchor,
                selection.setAnchor(anchor)
        );
    }

    final void extendRightAndCheck(final String selection,
                                   final String expectedSelection,
                                   final SpreadsheetViewportSelectionAnchor expectedAnchor) {
        final S parsed = this.parseString(selection);

        this.extendRightAndCheck(
                parsed,
                parsed.defaultAnchor(),
                this.parseRange(expectedSelection).setAnchor(expectedAnchor)
        );
    }

    final void extendRightAndCheck(final String text,
                                   final SpreadsheetViewportSelectionAnchor anchor,
                                   final String expected,
                                   final SpreadsheetViewportSelectionAnchor expectedAnchor) {
        this.extendRightAndCheck(
                this.parseString(text),
                anchor,
                this.parseRange(expected)
                        .simplify()
                        .setAnchor(expectedAnchor)
        );
    }

    final void extendRightAndCheck(final S selection,
                                   final SpreadsheetViewportSelectionAnchor anchor,
                                   final SpreadsheetViewportSelection expected) {
        this.extendRightAndCheck(
                selection,
                anchor,
                columnStore(),
                expected
        );
    }

    final void extendRightAndCheck(final S selection,
                                   final SpreadsheetViewportSelectionAnchor anchor,
                                   final SpreadsheetColumnStore columnStore,
                                   final SpreadsheetViewportSelection expected) {
        this.checkEquals(
                expected,
                selection.extendRight(anchor, columnStore, SpreadsheetRowStores.fake()),
                () -> selection + " anchor=" + anchor + " navigate extendRight"
        );
    }

    // extendDown.......................................................................................................

    final void extendDownAndCheck(final String selection) {
        this.extendDownAndCheck(
                this.parseString(selection)
        );
    }

    final void extendDownAndCheck(final String selection,
                                  final SpreadsheetViewportSelectionAnchor anchor) {
        this.extendDownAndCheck(
                this.parseString(selection),
                anchor
        );
    }

    final void extendDownAndCheck(final String selection,
                                  final SpreadsheetViewportSelectionAnchor anchor,
                                  final String expected) {
        final SpreadsheetSelection parsed = this.parseRange(expected)
                .simplify();

        this.extendDownAndCheck(
                this.parseString(selection),
                anchor,
                parsed.setAnchor(parsed.defaultAnchor())
        );
    }

    final void extendDownAndCheck(final S selection) {
        this.extendDownAndCheck(
                selection,
                selection.defaultAnchor()
        );
    }

    final void extendDownAndCheck(final S selection,
                                  final SpreadsheetViewportSelectionAnchor anchor) {
        this.extendDownAndCheck(
                selection,
                anchor,
                selection.setAnchor(anchor)
        );
    }

    final void extendDownAndCheck(final String selection,
                                  final String expectedSelection,
                                  final SpreadsheetViewportSelectionAnchor expectedAnchor) {
        final S parsed = this.parseString(selection);

        this.extendDownAndCheck(
                parsed,
                parsed.defaultAnchor(),
                this.parseRange(expectedSelection).setAnchor(expectedAnchor)
        );
    }

    final void extendDownAndCheck(final String text,
                                  final SpreadsheetViewportSelectionAnchor anchor,
                                  final String expected,
                                  final SpreadsheetViewportSelectionAnchor expectedAnchor) {
        this.extendDownAndCheck(
                this.parseString(text),
                anchor,
                this.parseRange(expected)
                        .simplify()
                        .setAnchor(expectedAnchor)
        );
    }

    final void extendDownAndCheck(final S selection,
                                  final SpreadsheetViewportSelectionAnchor anchor,
                                  final SpreadsheetViewportSelection expected) {
        this.extendDownAndCheck(
                selection,
                anchor,
                rowStore(),
                expected
        );
    }

    final void extendDownAndCheck(final S selection,
                                  final SpreadsheetViewportSelectionAnchor anchor,
                                  final SpreadsheetRowStore rowStore,
                                  final SpreadsheetViewportSelection expected) {
        this.checkEquals(
                expected,
                selection.extendDown(anchor, SpreadsheetColumnStores.fake(), rowStore),
                () -> selection + " anchor=" + anchor + " navigate extendDown"
        );
    }

    // simplify.........................................................................................................

    final void simplifyAndCheck(final String selection) {
        this.simplifyAndCheck(
                this.parseString(selection)
        );
    }

    final void simplifyAndCheck(final S selection) {
        this.simplifyAndCheck(
                selection,
                selection
        );
    }

    final void simplifyAndCheck(final String selection,
                                final SpreadsheetSelection expected) {
        this.simplifyAndCheck(
                this.parseString(selection),
                expected
        );
    }

    final void simplifyAndCheck(final S selection,
                                final SpreadsheetSelection expected) {
        this.checkEquals(
                expected,
                selection.simplify(),
                () -> "simplify " + selection
        );
    }

    // equalsIgnoreReferenceKind........................................................................................

    @Test
    public void testEqualsIgnoreReferenceKindSame() {
        final S selection = this.createSelection();

        this.equalsIgnoreReferenceKindAndCheck(
                selection,
                selection,
                true
        );
    }

    final void equalsIgnoreReferenceKindAndCheck(final String left,
                                                 final String right,
                                                 final boolean expected) {
        this.equalsIgnoreReferenceKindAndCheck(
                this.parseString(left),
                this.parseString(right),
                expected
        );
    }

    final void equalsIgnoreReferenceKindAndCheck(final S left,
                                                 final S right,
                                                 final boolean expected) {
        this.checkEquals(
                expected,
                left.equalsIgnoreReferenceKind(right),
                () -> left + " equalsIgnoreReferenceKind " + right
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // Json..............................................................................................................

    @Test
    public final void testJsonNodeMarshall() {
        final S selection = this.createSelection();
        this.marshallAndCheck(selection, JsonNode.string(selection.toString()));
    }

    abstract S createSelection();

    // HashCodeEqualsDefinedTesting.....................................................................................

    @Override
    public final S createObject() {
        return this.createSelection();
    }

    // IsMethodTesting...................................................................................................

    @Override
    public final S createIsMethodObject() {
        return this.createSelection();
    }

    @Override
    public final String isMethodTypeNamePrefix() {
        return "Spreadsheet";
    }

    @Override
    public final String isMethodTypeNameSuffix() {
        return "";//ExpressionReference.class.getSimpleName();
    }

    @Override
    public final Predicate<String> isMethodIgnoreMethodFilter() {
        return Predicates.is("isHidden");
    }

    // JsonNodeTesting..................................................................................................

    @Override
    public final S createJsonNodeMarshallingValue() {
        return this.createSelection();
    }

    // ParsingTesting...................................................................................................

    @Override
    public final Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> expected) {
        assertTrue(
                IllegalArgumentException.class.isAssignableFrom(expected),
                expected.getName() + " not a sub class of " + IllegalArgumentException.class
        );
        return expected;
    }

    @Override
    public final RuntimeException parseStringFailedExpected(final RuntimeException expected) {
        assertTrue(
                expected instanceof IllegalArgumentException,
                expected.getClass().getName() + "=" + expected + " not a sub class of " + IllegalArgumentException.class
        );
        return expected;
    }

    // PredicateTesting.................................................................................................

    @Override
    public final S createPredicate() {
        return this.createSelection();
    }

    public final void testTypeNaming() {
        // nop
    }

    @Override
    public final String typeNamePrefix() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final String typeNameSuffix() {
        throw new UnsupportedOperationException();
    }
}
