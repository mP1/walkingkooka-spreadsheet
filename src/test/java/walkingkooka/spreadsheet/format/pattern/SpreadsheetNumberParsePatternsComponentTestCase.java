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

import walkingkooka.collect.list.Lists;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.TextCursors;

import java.math.BigDecimal;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;

walkingkooka.reflect.*;

public abstract class SpreadsheetNumberParsePatternsComponentTestCase<C extends SpreadsheetNumberParsePatternsComponent> extends SpreadsheetNumberParsePatternsTestCase<C>
        implements ToStringTesting<C> {

    final static BigDecimal VALUE_WITHOUT = null;
    final static Boolean NEXT_CALLED = true;
    final static Boolean NEXT_SKIPPED = false;

    SpreadsheetNumberParsePatternsComponentTestCase() {
        super();
    }

    abstract C createComponent();

    final SpreadsheetNumberParsePatternsContext createContext() {
        return this.createContext(Lists.of(SpreadsheetNumberParsePatternsComponent.textLiteral("@")).iterator());
    }


    final SpreadsheetNumberParsePatternsContext createContext(final Iterator<SpreadsheetNumberParsePatternsComponent> nextComponent) {
        return SpreadsheetNumberParsePatternsContext.with(nextComponent, this.decimalNumberContext());
    }

    final void parseFails(final String text) {
        this.parseFails(text, text);
    }

    final void parseFails(final String text,
                          final String textAfter) {
        this.parseAndCheck(text,
                textAfter,
                NEXT_SKIPPED);
    }

    final void parseAndCheck(final String text,
                             final String textAfter,
                             final boolean next) {
        this.parseAndCheck(text,
                textAfter,
                VALUE_WITHOUT,
                next);
    }

    final void parseAndCheck(final String text,
                             final String textAfter,
                             final BigDecimal value,
                             final boolean next) {
        this.parseAndCheck(this.createComponent(),
                text,
                this.createContext(),
                textAfter,
                value,
                next);
    }

    final void parseAndCheck(final String text,
                             final SpreadsheetNumberParsePatternsContext context,
                             final String textAfter,
                             final BigDecimal value,
                             final boolean next) {
        this.parseAndCheck(this.createComponent(),
                text,
                context,
                textAfter,
                value,
                next);
    }

    final void parseAndCheck(final SpreadsheetNumberParsePatternsComponent component,
                             final String text,
                             final SpreadsheetNumberParsePatternsContext context,
                             final String textAfter,
                             final BigDecimal value,
                             final boolean next) {
        final TextCursor cursor = TextCursors.charSequence(text);

        component.parse(cursor, context);

        final TextCursorSavePoint save = cursor.save();
        cursor.end();

        assertEquals(textAfter,
                save.textBetween(),
                () -> " text left after parsing text " + CharSequences.quoteAndEscape(text));

        if (null != value) {
            assertEquals(value.stripTrailingZeros(),
                    context.computeValue().stripTrailingZeros(),
                    () -> " computeValue after parsing text " + CharSequences.quoteAndEscape(text));
        }
        assertEquals(next,
                !context.next.hasNext(),
                () -> " next component called after parsing text " + CharSequences.quoteAndEscape(text));
    }

    final void checkMode(final SpreadsheetNumberParsePatternsContext context,
                         final SpreadsheetNumberParsePatternsMode mode) {
        assertEquals(mode, context.mode, "mode");
    }

    // TypeNameTesting..................................................................................................

    @Override
    public final String typeNamePrefix() {
        return SpreadsheetNumberParsePatternsComponent.class.getSimpleName();
    }
}
