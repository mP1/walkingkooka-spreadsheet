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

import walkingkooka.InvalidTextLengthException;
import walkingkooka.compare.Comparators;
import walkingkooka.naming.Name;
import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.spreadsheet.SpreadsheetViewportRectangle;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A label or {@link Name} is a name to a cell reference, range and so on.
 * <pre>
 * A Defined Name must begin with a letter or an underscore ( _ ) and consist of only letters, numbers, or underscores.
 * Spaces are not permitted in a Defined Name. Moreover, a Defined Name may not be the same as a valid cell reference.
 * For example, the name AB11 is invalid because AB11 is a valid cell reference. Names are not case sensitive.
 * </pre>
 */
@SuppressWarnings("lgtm[java/inconsistent-equals-and-hashcode]")
final public class SpreadsheetLabelName extends SpreadsheetExpressionReference
        implements Comparable<SpreadsheetLabelName>,
        Name {

    private final static CharPredicate LETTER = CharPredicates.letter();

    // https://contexturesblog.com/archives/2017/12/07/what-are-the-rules-for-excel-names/
    //
    // The first character of a name must be one of the following characters:
    // letter
    // underscore (_)
    // backslash (\).
    private final static CharPredicate INITIAL = LETTER.or(
            CharPredicates.any("\\_")
    );

    // Remaining characters in the name can be
    // letters
    // numbers
    // periods
    // underscore characters
    // The following are not allowed:
    // Space characters are not allowed as part of a name.
    // Names can’t look like cell addresses, such as A$35 or R2D2
    // C, c, R, r — can’t be used as names — Excel uses them as selection shortcuts
    private final static CharPredicate PART = LETTER.or(
            CharPredicates.range('0', '9') // numbers
    ).or(
            CharPredicates.any("._")
    );

    /**
     * The maximum valid length for a label name.
     */
    public final static int MAX_LENGTH = 255;

    static boolean isLabelText0(final String text) {
        return CharPredicates.isInitialAndPart(
                text,
                INITIAL,
                PART
        ) &&
                !SpreadsheetSelection.isCellText(text);
    }

    /**
     * Factory that creates a {@link SpreadsheetLabelName}
     */
    static SpreadsheetLabelName with(final String name) {
        CharPredicates.failIfNullOrEmptyOrInitialAndPartFalse(name, "name", INITIAL, PART);

        if (name.length() >= MAX_LENGTH) {
            throw new InvalidTextLengthException("Label", name, 0, MAX_LENGTH);
        }

        if (isCellText(name)) {
            throw new IllegalArgumentException("Label cannot be a valid cell reference=" + CharSequences.quote(name));
        }

        return new SpreadsheetLabelName(name);
    }

    /**
     * Private constructor
     */
    private SpreadsheetLabelName(final String name) {
        super();
        this.name = name;
    }

    @Override
    public String value() {
        return this.name;
    }

    private final String name;

    /**
     * Creates a {@link SpreadsheetLabelMapping} using this label and the given {@link SpreadsheetExpressionReference}.
     */
    public SpreadsheetLabelMapping mapping(final SpreadsheetExpressionReference reference) {
        return SpreadsheetLabelMapping.with(this, reference);
    }

    public String hateosLinkId() {
        return this.name;
    }

    @Override
    public boolean isAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isFirst() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isLast() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException();
    }

    // SpreadsheetExpressionReference...................................................................................

    @Override
    public SpreadsheetCellReference toCell() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetColumnReference toColumn() {
        throw new UnsupportedOperationException(this.toString());
    }

    @Override
    public SpreadsheetColumnReferenceRange toColumnRange() {
        throw new UnsupportedOperationException(this.toString());
    }

    @Override
    public SpreadsheetRowReference toRow() {
        throw new UnsupportedOperationException(this.toString());
    }

    @Override
    public SpreadsheetRowReferenceRange toRowRange() {
        throw new UnsupportedOperationException(this.toString());
    }

    @Override
    public SpreadsheetSelection simplify() {
        return this;
    }

    @Override
    Set<SpreadsheetViewportSelectionAnchor> anchors() {
        return ANCHORS;
    }

    private final Set<SpreadsheetViewportSelectionAnchor> ANCHORS = EnumSet.of(SpreadsheetViewportSelectionAnchor.NONE);

    // SpreadsheetViewportRectangle.....................................................................................

    /**
     * Creates a {@link SpreadsheetViewportRectangle} using this as the top/left.
     */
    public SpreadsheetViewportRectangle viewportRectangle(final double width,
                                                          final double height) {
        return SpreadsheetViewportRectangle.with(this, width, height);
    }

    // SpreadsheetSelectionVisitor......................................................................................

    @Override
    void accept(final SpreadsheetSelectionVisitor visitor) {
        visitor.visit(this);
    }

    // testXXX..........................................................................................................

    /**
     * Always throws {@link UnsupportedOperationException}.
     */
    @Override
    boolean testCell0(final SpreadsheetCellReference reference) {
        throw new UnsupportedOperationException();
    }

    @Override
    boolean testCellRange0(final SpreadsheetCellRange range) {
        throw new UnsupportedOperationException();
    }

    @Override
    boolean testColumn0(final SpreadsheetColumnReference column) {
        throw new UnsupportedOperationException();
    }

    @Override
    boolean testRow0(final SpreadsheetRowReference row) {
        throw new UnsupportedOperationException();
    }

    // SpreadsheetViewportSelectionNavigation...........................................................................

    @Override
    public SpreadsheetViewportSelectionAnchor defaultAnchor() {
        return SpreadsheetViewportSelectionAnchor.NONE; // should never happen
    }

    @Override
    public boolean isHidden(final Predicate<SpreadsheetColumnReference> hiddenColumnTester,
                            final Predicate<SpreadsheetRowReference> hiddenRowTester) {
        throw new UnsupportedOperationException();
    }

    @Override
    Optional<SpreadsheetSelection> leftColumn(final SpreadsheetViewportSelectionAnchor anchor,
                                              final SpreadsheetViewportSelectionNavigationContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    Optional<SpreadsheetSelection> leftPixels(final SpreadsheetViewportSelectionAnchor anchor,
                                              final int count,
                                              final SpreadsheetViewportSelectionNavigationContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    Optional<SpreadsheetSelection> upRow(final SpreadsheetViewportSelectionAnchor anchor,
                                         final SpreadsheetViewportSelectionNavigationContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    Optional<SpreadsheetSelection> upPixels(final SpreadsheetViewportSelectionAnchor anchor,
                                            final int count,
                                            final SpreadsheetViewportSelectionNavigationContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    Optional<SpreadsheetSelection> rightColumn(final SpreadsheetViewportSelectionAnchor anchor,
                                               final SpreadsheetViewportSelectionNavigationContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    Optional<SpreadsheetSelection> rightPixels(final SpreadsheetViewportSelectionAnchor anchor,
                                               final int count,
                                               final SpreadsheetViewportSelectionNavigationContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    Optional<SpreadsheetSelection> downRow(final SpreadsheetViewportSelectionAnchor anchor,
                                           final SpreadsheetViewportSelectionNavigationContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    Optional<SpreadsheetSelection> downPixels(final SpreadsheetViewportSelectionAnchor anchor,
                                              final int count,
                                              final SpreadsheetViewportSelectionNavigationContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    Optional<SpreadsheetViewport> extendLeftColumn(final SpreadsheetViewportSelectionAnchor anchor,
                                                   final SpreadsheetViewportSelectionNavigationContext contexte) {
        throw new UnsupportedOperationException();
    }

    @Override
    Optional<SpreadsheetViewport> extendLeftPixels(final SpreadsheetViewportSelectionAnchor anchor,
                                                   final int count,
                                                   final SpreadsheetViewportSelectionNavigationContext contexte) {
        throw new UnsupportedOperationException();
    }

    @Override
    Optional<SpreadsheetViewport> extendUpRow(final SpreadsheetViewportSelectionAnchor anchor,
                                              final SpreadsheetViewportSelectionNavigationContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    Optional<SpreadsheetViewport> extendUpPixels(final SpreadsheetViewportSelectionAnchor anchor,
                                                 final int count,
                                                 final SpreadsheetViewportSelectionNavigationContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    Optional<SpreadsheetViewport> extendRightColumn(final SpreadsheetViewportSelectionAnchor anchor,
                                                    final SpreadsheetViewportSelectionNavigationContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    Optional<SpreadsheetViewport> extendRightPixels(final SpreadsheetViewportSelectionAnchor anchor,
                                                    final int count,
                                                    final SpreadsheetViewportSelectionNavigationContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    Optional<SpreadsheetViewport> extendDownRow(final SpreadsheetViewportSelectionAnchor anchor,
                                                final SpreadsheetViewportSelectionNavigationContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    Optional<SpreadsheetViewport> extendDownPixels(final SpreadsheetViewportSelectionAnchor anchor,
                                                   final int count,
                                                   final SpreadsheetViewportSelectionNavigationContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    Optional<SpreadsheetSelection> extendRange(final Optional<? extends SpreadsheetSelection> other,
                                               final SpreadsheetViewportSelectionAnchor anchor) {
        throw new UnsupportedOperationException();
    }

    // focused...........................................................................................................

    @Override
    public SpreadsheetLabelName focused(final SpreadsheetViewportSelectionAnchor anchor) {
        Objects.requireNonNull(anchor, "anchor");
        throw new UnsupportedOperationException();
    }

    // Comparable........................................................................................................

    @Override
    public int compareTo(final SpreadsheetLabelName other) {
        return CASE_SENSITIVITY.comparator().compare(this.name, other.name);
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return CASE_SENSITIVITY.hash(this.name);
    }

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetLabelName;
    }

    @Override
    boolean equals0(final Object other,
                    final boolean includeKind) {
        return this.equals1(
                (SpreadsheetLabelName) other
        );
    }

    private boolean equals1(final SpreadsheetLabelName other) {
        return this.compareTo(other) == Comparators.EQUAL;
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Always returns this.
     */
    @Override
    public SpreadsheetLabelName toRelative() {
        return this;
    }

    // HasCaseSensitivity................................................................................................

    @Override
    public CaseSensitivity caseSensitivity() {
        return CASE_SENSITIVITY;
    }

    public final static CaseSensitivity CASE_SENSITIVITY = CaseSensitivity.INSENSITIVE;
}
