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
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;

import java.util.Optional;
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
        final Optional<SpreadsheetViewportSelectionAnchor> anchor = selection.defaultAnchor();
        this.checkNotEquals(null, anchor, "anchor");
    }

    @Test
    final public void testDefaultAnchorThenSetAnchor() {
        final S selection = this.createSelection();
        final Optional<SpreadsheetViewportSelectionAnchor> anchor = selection.defaultAnchor();
        this.checkNotEquals(null, anchor, "anchor");

        final SpreadsheetViewportSelection viewportSelection = selection.setAnchor(anchor);
        this.checkEquals(anchor, viewportSelection.anchor(), "anchor");
        this.checkEquals(selection, viewportSelection.selection(), "selection");
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
        return Predicates.never();
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
