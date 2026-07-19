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

import org.junit.jupiter.api.Test;
import walkingkooka.convert.BinaryNumberConverterFunctions;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataLoaders;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.tree.expression.convert.ExpressionNumberBinaryNumberConverterFunctions;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;

import java.math.MathContext;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetConverterContextCycleTest implements SpreadsheetConverterContextTesting<SpreadsheetConverterContextCycle>,

    DecimalNumberContextDelegator {

    private final static Object VALUE = "Hello";
    private final static Class<?> TYPE = Object.class;
    private final static SpreadsheetConverterContext CONTEXT = SpreadsheetConverterContexts.fake();

    // with.............................................................................................................

    @Test
    public void testWithNullTypeFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetConverterContextCycle.with(
                VALUE,
                null,
                CONTEXT
            )
        );
    }

    @Test
    public void testWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetConverterContextCycle.with(
                VALUE,
                TYPE,
                null
            )
        );
    }

    @Test
    public void testWithNullValue() {
        SpreadsheetConverterContextCycle.with(
            null,
            TYPE,
            CONTEXT
        );
    }
    
    @Override
    public void testSetObjectPostProcessor() {
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
    public void testSetPreProcessorSame() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetConverterContextCycle createContext() {
        return SpreadsheetConverterContextCycle.with(
            VALUE,
            TYPE,
            SpreadsheetConverterContextBasic.with(
                HAS_USER_DIRECTORIES,
                SpreadsheetConverterContexts.NO_METADATA,
                Optional.empty(), // validationReference
                Converters.fake(),
                MEDIA_TYPE_DETECTOR,
                BinaryNumberConverterFunctions.multiply(), // multiplier
                SpreadsheetLabelNameResolvers.empty(),
                SpreadsheetMetadataLoaders.empty(),
                JsonNodeConverterContexts.basic(
                    ExpressionNumberConverterContexts.basic(
                        Converters.fake(),
                        ExpressionNumberBinaryNumberConverterFunctions.multiply(), // multiplier
                        ConverterContexts.basic(
                            false, // canNumbersHaveGroupSeparator
                            Converters.JAVA_EPOCH_OFFSET, // dateOffset
                            ',', // valueSeparator
                            Converters.fake(),
                            BinaryNumberConverterFunctions.fake(), // multiplier
                            BINARY_TEXT_CONTEXT,
                            CURRENCY_LOCALE_CONTEXT,
                            DateTimeContexts.basic(
                                LOCALE_CONTEXT.dateTimeSymbolsForLocale(LOCALE)
                                    .get(),
                                LOCALE_CONTEXT.locale(),
                                DEFAULT_YEAR,
                                TWO_DIGIT_YEAR,
                                HAS_NOW
                            ),
                            this.decimalNumberContext()
                        ),
                        EXPRESSION_NUMBER_KIND
                    ),
                    JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT
                ),
                LOCALE_CONTEXT
            )
        );
    }

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return DecimalNumberContexts.american(MATH_CONTEXT);
    }

    @Override
    public int decimalNumberDigitCount() {
        return this.decimalNumberContext()
            .decimalNumberDigitCount();
    }

    @Override
    public MathContext mathContext() {
        return MATH_CONTEXT;
    }

    @Override
    public Class<SpreadsheetConverterContextCycle> type() {
        return SpreadsheetConverterContextCycle.class;
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
}
