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

package walkingkooka.spreadsheet.format.parser;

import walkingkooka.InvalidCharacterException;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.Parser;

import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * A {@link SpreadsheetFormatParserContext} without any functionality.
 */
final class SpreadsheetFormatParserContextBasic implements SpreadsheetFormatParserContext,
    DecimalNumberContextDelegator {

    static SpreadsheetFormatParserContextBasic with(final BiFunction<Parser<?>, TextCursor, InvalidCharacterException> invalidCharacterExceptionFactory) {
        return new SpreadsheetFormatParserContextBasic(
            Objects.requireNonNull(invalidCharacterExceptionFactory, "invalidCharacterExceptionFactory")
        );
    }

    private SpreadsheetFormatParserContextBasic(final BiFunction<Parser<?>, TextCursor, InvalidCharacterException> invalidCharacterExceptionFactory) {
        super();
        this.invalidCharacterExceptionFactory = invalidCharacterExceptionFactory;
    }

    @Override
    public List<String> ampms() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int defaultYear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canNumbersHaveGroupSeparator() {
        return false;
    }

    @Override
    public InvalidCharacterException invalidCharacterException(final Parser<?> parser,
                                                               final TextCursor cursor) {
        return this.invalidCharacterExceptionFactory.apply(
            parser,
            cursor
        );
    }

    private final BiFunction<Parser<?>, TextCursor, InvalidCharacterException> invalidCharacterExceptionFactory;

    @Override
    public char valueSeparator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Locale locale() {
        return DECIMAL_NUMBER_CONTEXT.locale();
    }

    @Override
    public MathContext mathContext() {
        return DECIMAL_NUMBER_CONTEXT.mathContext();
    }

    @Override
    public List<String> monthNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> monthNameAbbreviations() {
        throw new UnsupportedOperationException();
    }

    @Override
    public LocalDateTime now() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int twoDigitYear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> weekDayNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> weekDayNameAbbreviations() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DateTimeSymbols dateTimeSymbols() {
        throw new UnsupportedOperationException();
    }

    // DecimalNumberContextDelegator....................................................................................

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return DECIMAL_NUMBER_CONTEXT;
    }

    private final static DecimalNumberContext DECIMAL_NUMBER_CONTEXT = DecimalNumberContexts.american(MathContext.UNLIMITED);

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.invalidCharacterExceptionFactory + " " + DECIMAL_NUMBER_CONTEXT;
    }
}
