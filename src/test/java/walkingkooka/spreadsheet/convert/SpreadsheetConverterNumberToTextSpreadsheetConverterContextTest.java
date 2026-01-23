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

package walkingkooka.spreadsheet.convert;

import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.Locale;

public final class SpreadsheetConverterNumberToTextSpreadsheetConverterContextTest implements SpreadsheetConverterContextTesting<SpreadsheetConverterNumberToTextSpreadsheetConverterContext>,
    DecimalNumberContextDelegator {

    @Override
    public void testAmpms() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testAmpmNegativeFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testAmpmInvalidFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testMonthNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testMonthNames2() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testMonthNameNegativeFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testMonthNameInvalidFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testMonthNameAbbreviations() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testMonthNamesAbbreviation2() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testMonthNameAbbrevationNegativeFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testMonthNameAbbreviationInvalidFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testTwoDigitYear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testWeekDayNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testWeekDayNames2() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testWeekDayNameNegativeFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testWeekDayNameInvalidFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testWeekDayNameAbbreviations() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testWeekDayNameAbbreviations2() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testWeekDayNameAbbrevationNegativeFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testWeekDayNameAbbreviationInvalidFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetObjectPostProcessor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetObjectPostProcessorNullFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetObjectPostProcessorSame() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void testSetPreProcessor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetPreProcessorNullFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetPreProcessorSame() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetConverterNumberToTextSpreadsheetConverterContext createContext() {
        final Locale locale = Locale.ENGLISH;
        final LocaleContext localeContext = LocaleContexts.jre(locale);
        final ExpressionNumberKind expressionNumberKind = ExpressionNumberKind.BIG_DECIMAL;

        return SpreadsheetConverterNumberToTextSpreadsheetConverterContext.with(
            SpreadsheetConverterContexts.basic(
                SpreadsheetConverterContexts.NO_METADATA,
                SpreadsheetConverterContexts.NO_VALIDATION_REFERENCE,
                Converters.fake(),
                SpreadsheetLabelNameResolvers.empty(),
                JsonNodeConverterContexts.basic(
                    ExpressionNumberConverterContexts.basic(
                        Converters.fake(),
                        ConverterContexts.basic(
                            false, // canNumbersHaveGroupSeparator
                            Converters.JAVA_EPOCH_OFFSET, // dateOffset
                            INDENTATION,
                            LineEnding.NL,
                            ',', // valueSeparator
                            Converters.fake(),
                            DateTimeContexts.basic(
                                localeContext.dateTimeSymbolsForLocale(locale)
                                    .get(),
                                locale,
                                1900,
                                20,
                                LocalDateTime::now
                            ),
                            DECIMAL_NUMBER_CONTEXT
                        ),
                        expressionNumberKind
                    ),
                    JsonNodeMarshallUnmarshallContexts.basic(
                        JsonNodeMarshallContexts.basic(),
                        JsonNodeUnmarshallContexts.basic(
                            expressionNumberKind,
                            DECIMAL_NUMBER_CONTEXT.mathContext()
                        )
                    )
                ),
                localeContext
            )
        );
    }

    private final static DecimalNumberContext DECIMAL_NUMBER_CONTEXT = DecimalNumberContexts.american(MathContext.DECIMAL32);

    @Override
    public int decimalNumberDigitCount() {
        return DECIMAL_NUMBER_CONTEXT.decimalNumberDigitCount();
    }

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return DECIMAL_NUMBER_CONTEXT;
    }

    @Override
    public MathContext mathContext() {
        return DECIMAL_NUMBER_CONTEXT.mathContext();
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterNumberToTextSpreadsheetConverterContext> type() {
        return SpreadsheetConverterNumberToTextSpreadsheetConverterContext.class;
    }
}
