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

package walkingkooka.spreadsheet.format.pattern;

import walkingkooka.ToStringTesting;
import walkingkooka.collect.iterator.Iterators;
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserTesting;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class SpreadsheetNumberParsePatternsComponentTestCase<C extends SpreadsheetNumberParsePatternsComponent> extends SpreadsheetNumberParsePatternsTestCase<C>
        implements ParserTesting,
        ToStringTesting<C> {

    SpreadsheetNumberParsePatternsComponentTestCase() {
        super();
    }

    abstract C createComponent();

    final SpreadsheetNumberParsePatternsRequest createRequest(final boolean next) {
        return this.createRequest(
                next ?
                        Iterators.empty() :
                        this.next()
        );
    }

    final SpreadsheetNumberParsePatternsRequest createRequest(final Iterator<SpreadsheetNumberParsePatternsComponent> nextComponent) {
        return SpreadsheetNumberParsePatternsRequest.with(
                nextComponent,
                SpreadsheetNumberParsePatternsMode.VALUE,
                this.decimalNumberContext()
        );
    }

    private Iterator<SpreadsheetNumberParsePatternsComponent> next() {
        return Iterators.one(
                new SpreadsheetNumberParsePatternsComponent() {

                    @Override
                    boolean isExpressionCompatible() {
                        return true;
                    }

                    @Override
                    SpreadsheetNumberParsePatternsComponent lastDigit(final SpreadsheetNumberParsePatternsComponentDigitMode mode) {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    boolean parse(final TextCursor cursor,
                                  final SpreadsheetNumberParsePatternsRequest request) {
                        return true;
                    }

                    @Override
                    public String toString() {
                        return this.getClass().getSimpleName();
                    }
                }
        );
    }

    final void parseFails(final String text) {
        final TextCursor cursor = TextCursors.charSequence(text);

        final SpreadsheetNumberParsePatternsRequest request = this.createRequest(NEXT_SKIPPED);
        assertEquals(
                false,
                this.createComponent()
                        .parse(cursor, request),
                () -> "parse of " + CharSequences.quoteAndEscape(text) + " should have returned false"
        );

        final TextCursorSavePoint save = cursor.save();
        cursor.end();

        assertEquals(text,
                save.textBetween(),
                () -> " text left after parsing text " + CharSequences.quoteAndEscape(text));

        assertEquals(
                Lists.empty(),
                request.tokens,
                () -> "tokens\nrequest: " + request
        );
    }

    final void parseAndCheck2(final String text,
                              final String textAfter,
                              final boolean next,
                              final SpreadsheetParserToken... tokens) {
        this.parseAndCheck2(
                text,
                textAfter,
                this.createRequest(next),
                next,
                tokens
        );
    }

    final void parseAndCheck2(final String text,
                              final String textAfter,
                              final SpreadsheetNumberParsePatternsRequest request,
                              final boolean next,
                              final SpreadsheetParserToken... tokens) {
        this.parseAndCheck2(
                this.createComponent(),
                text,
                textAfter,
                request,
                next,
                tokens
        );
    }

    final void parseAndCheck2(final String text,
                              final String textAfter,
                              final SpreadsheetNumberParsePatternsComponentDigitMode mode,
                              final boolean next,
                              final SpreadsheetParserToken... tokens) {
        final SpreadsheetNumberParsePatternsRequest request = this.createRequest(next);
        this.parseAndCheck2(
                this.createComponent(),
                text,
                textAfter,
                request,
                next,
                tokens
        );
        assertEquals(
                mode,
                request.digitMode,
                () -> "request: " + request
        );
    }

    final void parseAndCheck2(final C component,
                              final String text,
                              final String textAfter,
                              final SpreadsheetNumberParsePatternsRequest request,
                              final boolean hasNext,
                              final SpreadsheetParserToken... tokens) {
        final TextCursor cursor = TextCursors.charSequence(text + textAfter);

        assertEquals(
                true, // !hasNext
                component.parse(cursor, request),
                () -> "parse " + CharSequences.quoteAndEscape(text) + " should have matched"
        );

        final TextCursorSavePoint save = cursor.save();
        cursor.end();

        assertEquals(
                textAfter,
                save.textBetween(),
                () -> " text left after parsing text " + CharSequences.quoteAndEscape(text)
        );

        request.addNumberIfNecessary();

        checkEquals(
                Lists.of(tokens),
                request.tokens,
                () -> "tokens\nrequest: " + request
        );

        if (NEXT_CALLED == hasNext) {
            assertEquals(
                    hasNext, // if empty means nothing got consumed and next shouldnt be executed
                    request.next.hasNext(),
                    () -> " next component called after parsing text " + CharSequences.quoteAndEscape(text)
            );
        }
    }

    // TypeNameTesting..................................................................................................

    @Override
    public final String typeNamePrefix() {
        return SpreadsheetNumberParsePatternsComponent.class.getSimpleName();
    }
}
