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

package walkingkooka.spreadsheet;

import walkingkooka.Cast;
import walkingkooka.Value;
import walkingkooka.compare.LowerOrUpper;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.net.http.server.hateos.HasHateosLinkId;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserException;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;

import java.math.MathContext;
import java.util.Objects;
import java.util.function.IntFunction;

/**
 * Captures the common features shared by a row or column.
 */
abstract class SpreadsheetColumnOrRowReference<R extends SpreadsheetColumnOrRowReference<R>> implements Value<Integer>,
        Comparable<R>,
        LowerOrUpper<R>,
        HashCodeEqualsDefined,
        HasHateosLinkId,
        HasJsonNode {

    /**
     * Parsers the text expecting a valid {@link SpreadsheetRowReference} or fails.
     */
    static <T extends SpreadsheetParserToken> T parse0(final String text,
                                                       final Parser<ParserContext> parser,
                                                       final Class<T> type) {
        try {
            return type.cast(parser.parse(TextCursors.charSequence(text),
                    SpreadsheetParserContexts.basic(DecimalNumberContexts.american(MathContext.DECIMAL32)))
                    .get());
        } catch (final ParserException cause) {
            throw new IllegalArgumentException(cause.getMessage(), cause);
        }
    }

    final static int CACHE_SIZE = 100;

    /**
     * Fills an array with what will become a cache of {@link SpreadsheetColumnOrRowReference}.
     */
    static <R extends SpreadsheetColumnOrRowReference<R>> R[] fillCache(final IntFunction<R> reference, final R[] array) {
        for (int i = 0; i < CACHE_SIZE; i++) {
            array[i] = reference.apply(i);
        }

        return array;
    }

    final static void checkReferenceKind(final SpreadsheetReferenceKind referenceKind) {
        Objects.requireNonNull(referenceKind, "referenceKind");
    }

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetColumnOrRowReference(final int value, final SpreadsheetReferenceKind referenceKind) {
        this.value = value;
        this.referenceKind = referenceKind;
    }

    /**
     * Adds a delta to the value and returns an instance with the result.
     */
    abstract SpreadsheetColumnOrRowReference add(final int value);

    final SpreadsheetColumnOrRowReference add0(final int value) {
        return 0 == value ?
                this :
                this.setValue(this.value + value);
    }

    abstract SpreadsheetColumnOrRowReference setValue(final int value);

    @Override
    public final Integer value() {
        return this.value;
    }

    final int value;

    public final SpreadsheetReferenceKind referenceKind() {
        return this.referenceKind;
    }

    abstract R setReferenceKind(final SpreadsheetReferenceKind referenceKind);

    final R setReferenceKind0(final SpreadsheetReferenceKind referenceKind) {
        checkReferenceKind(referenceKind);

        return this.referenceKind == referenceKind ?
                Cast.to(this) :
                this.replaceReferenceKind(referenceKind);
    }

    private final SpreadsheetReferenceKind referenceKind;

    /**
     * Unconditionally creates a new {@link SpreadsheetColumnOrRowReference} with the given {@link SpreadsheetReferenceKind}.
     */
    abstract R replaceReferenceKind(final SpreadsheetReferenceKind referenceKind);

    // HashCodeEqualsDefined............................................................................................

    @Override
    public final int hashCode() {
        return Objects.hash(this.value, this.referenceKind);
    }

    public final boolean equals(final Object other) {
        return this == other ||
                this.canBeEqual(other) &&
                        this.equals0(Cast.to(other));
    }

    abstract boolean canBeEqual(Object other);

    private boolean equals0(final SpreadsheetColumnOrRowReference other) {
        return this.value == other.value &&
                this.referenceKind == other.referenceKind;
    }

    @Override
    abstract public String toString();

    static void checkOther(final SpreadsheetColumnOrRowReference other) {
        Objects.requireNonNull(other, "other");
    }

    // HasJsonNode............................................................................................

    @Override
    public final JsonNode toJsonNode() {
        return JsonNode.string(this.toString());
    }
}
