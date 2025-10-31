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
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.spreadsheet.SpreadsheetStrings;
import walkingkooka.spreadsheet.formula.parser.LabelSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.viewport.AnchoredSpreadsheetSelection;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportAnchor;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportNavigationContext;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A label or {@link Name} is a name to a cell reference, range and so on.
 * <pre>
 * A Defined Name must begin with a letter or a underscore ( _ ) and consist of only letters, numbers, or underscores.
 * Spaces are not permitted in a Defined Name. Moreover, a Defined Name may not be the same as a valid cell reference.
 * For example, the name AB11 is invalid because AB11 is a valid cell reference. Names are not case sensitive.
 * </pre>
 */
@SuppressWarnings("lgtm[java/inconsistent-equals-and-hashcode]")
final public class SpreadsheetLabelName extends SpreadsheetExpressionReference
    implements Comparable<SpreadsheetLabelName>,
    Name,
    HateosResource<String> {
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
            false == CASE_SENSITIVITY.equals(SpreadsheetStrings.BOOLEAN_TRUE, text) &&
            false == CASE_SENSITIVITY.equals(SpreadsheetStrings.BOOLEAN_FALSE, text) &&
            false == SpreadsheetSelection.isCellText(text);
    }

    /**
     * Factory that creates a {@link SpreadsheetLabelName}
     */
    static SpreadsheetLabelName with(final String name) {
        CharPredicates.failIfNullOrEmptyOrInitialAndPartFalse(
            name,
            "Label",
            INITIAL,
            PART
        );

        if (CASE_SENSITIVITY.equals("true", name) || CASE_SENSITIVITY.equals("false", name)) {
            throw new IllegalArgumentException("Invalid label with " + CharSequences.quoteAndEscape(name));
        }

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

    // Name.............................................................................................................

    @Override
    public String value() {
        return this.name;
    }

    private final String name;

    /**
     * Creates a {@link SpreadsheetLabelMapping} using this label and the given {@link SpreadsheetExpressionReference}.
     */
    public SpreadsheetLabelMapping setLabelMappingReference(final SpreadsheetExpressionReference reference) {
        return SpreadsheetLabelMapping.with(
            this,
            reference
        );
    }

    // HateosResource...................................................................................................

    @Override
    public String hateosLinkId() {
        return this.name;
    }

    @Override
    public Optional<String> id() {
        return Optional.of(
            this.hateosLinkId()
        );
    }

    // SpreadsheetSelection............................................................................................

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
    public SpreadsheetColumnRangeReference toColumnRange() {
        throw new UnsupportedOperationException(this.toString());
    }

    @Override
    public SpreadsheetRowReference toRow() {
        throw new UnsupportedOperationException(this.toString());
    }

    @Override
    public SpreadsheetRowRangeReference toRowRange() {
        throw new UnsupportedOperationException(this.toString());
    }

    @Override
    public SpreadsheetSelection toScalar() {
        return this;
    }

    // toRange..........................................................................................................

    @Override
    public SpreadsheetSelection toRange() {
        throw new UnsupportedOperationException();
    }

    // anchors..........................................................................................................

    @Override
    public Set<SpreadsheetViewportAnchor> anchors() {
        return NONE_ANCHORS;
    }

    // SpreadsheetSelectionVisitor......................................................................................

    @Override
    void accept(final SpreadsheetSelectionVisitor visitor) {
        visitor.visit(this);
    }

    // add column/row...................................................................................................

    @Override
    public SpreadsheetLabelName add(final int column, final int row) {
        throw new UnsupportedOperationException(this.toString());
    }

    @Override
    public SpreadsheetLabelName addSaturated(final int column,
                                             final int row) {
        throw new UnsupportedOperationException(this.toString());
    }

    @Override
    public SpreadsheetLabelName addIfRelative(final int column,
                                              final int row) {
        return this;
    }

    // replaceReferencesMapper..........................................................................................

    @Override
    Optional<Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>>> replaceReferencesMapper0(final SpreadsheetSelection movedTo) {
        throw new UnsupportedOperationException();
    }

    // testXXX..........................................................................................................

    /**
     * Always throws {@link UnsupportedOperationException}.
     */
    @Override
    boolean testCellNonNull(final SpreadsheetCellReference reference) {
        throw new UnsupportedOperationException();
    }

    @Override
    boolean testCellRangeNonNull(final SpreadsheetCellRangeReference range) {
        throw new UnsupportedOperationException();
    }

    @Override
    boolean testColumnNonNull(final SpreadsheetColumnReference column) {
        throw new UnsupportedOperationException();
    }

    @Override
    boolean testRowNonNull(final SpreadsheetRowReference row) {
        throw new UnsupportedOperationException();
    }

    // SpreadsheetViewportNavigation....................................................................................

    @Override
    public SpreadsheetViewportAnchor defaultAnchor() {
        return SpreadsheetViewportAnchor.NONE; // should never happen
    }

    @Override
    public boolean isHidden(final Predicate<SpreadsheetColumnReference> hiddenColumnTester,
                            final Predicate<SpreadsheetRowReference> hiddenRowTester) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetSelection> moveLeftColumn(final SpreadsheetViewportAnchor anchor,
                                                         final SpreadsheetViewportNavigationContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetSelection> moveLeftPixels(final SpreadsheetViewportAnchor anchor,
                                                         final int count,
                                                         final SpreadsheetViewportNavigationContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetSelection> upRow(final SpreadsheetViewportAnchor anchor,
                                                final SpreadsheetViewportNavigationContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetSelection> upPixels(final SpreadsheetViewportAnchor anchor,
                                                   final int count,
                                                   final SpreadsheetViewportNavigationContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetSelection> rightColumn(final SpreadsheetViewportAnchor anchor,
                                                      final SpreadsheetViewportNavigationContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetSelection> rightPixels(final SpreadsheetViewportAnchor anchor,
                                                      final int count,
                                                      final SpreadsheetViewportNavigationContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetSelection> downRow(final SpreadsheetViewportAnchor anchor,
                                                  final SpreadsheetViewportNavigationContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetSelection> downPixels(final SpreadsheetViewportAnchor anchor,
                                                     final int count,
                                                     final SpreadsheetViewportNavigationContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendLeftColumn(final SpreadsheetViewportAnchor anchor,
                                                                   final SpreadsheetViewportNavigationContext contexte) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendLeftPixels(final SpreadsheetViewportAnchor anchor,
                                                                   final int count,
                                                                   final SpreadsheetViewportNavigationContext contexte) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendUpRow(final SpreadsheetViewportAnchor anchor,
                                                              final SpreadsheetViewportNavigationContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendUpPixels(final SpreadsheetViewportAnchor anchor,
                                                                 final int count,
                                                                 final SpreadsheetViewportNavigationContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendRightColumn(final SpreadsheetViewportAnchor anchor,
                                                                    final SpreadsheetViewportNavigationContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendRightPixels(final SpreadsheetViewportAnchor anchor,
                                                                    final int count,
                                                                    final SpreadsheetViewportNavigationContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendDownRow(final SpreadsheetViewportAnchor anchor,
                                                                final SpreadsheetViewportNavigationContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendDownPixels(final SpreadsheetViewportAnchor anchor,
                                                                   final int count,
                                                                   final SpreadsheetViewportNavigationContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    Optional<SpreadsheetSelection> extendRange(final Optional<? extends SpreadsheetSelection> other,
                                               final SpreadsheetViewportAnchor anchor) {
        throw new UnsupportedOperationException();
    }

    // focused...........................................................................................................

    @Override
    public SpreadsheetLabelName focused(final SpreadsheetViewportAnchor anchor) {
        Objects.requireNonNull(anchor, "anchor");
        throw new UnsupportedOperationException();
    }

    // HasParserToken...................................................................................................

    @Override
    public LabelSpreadsheetFormulaParserToken toParserToken() {
        return SpreadsheetFormulaParserToken.label(
            this,
            this.text()
        );
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
    boolean equalsNotSameAndNotNull(final Object other,
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

    // SpreadsheetSelectionIgnoresReferenceKindComparator...............................................................

    @Override
    int spreadsheetSelectionIgnoresReferenceKindComparatorPriority() {
        return SpreadsheetSelectionIgnoresReferenceKindComparator.LABEL;
    }
}
