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
import walkingkooka.Either;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.MathContext;

public final class SpreadsheetConverterNumberToTextTest extends SpreadsheetConverterTestCase<SpreadsheetConverterNumberToText> {

    private final static ExpressionNumberKind KIND = ExpressionNumberKind.BIG_DECIMAL;

    @Test
    public void testConvertNumberWholeNumberToText() {
        this.convertAndCheck(
            KIND.create(123),
            "123"
        );
    }

    @Test
    public void testConvertNumberWithDecimalPlacesToText() {
        this.convertAndCheck(
            KIND.create(45.75),
            "45.75"
        );
    }

    @Override
    public SpreadsheetConverterNumberToText createConverter() {
        return SpreadsheetConverterNumberToText.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return new FakeSpreadsheetConverterContext() {
            @Override
            public ExpressionNumberKind expressionNumberKind() {
                return KIND;
            }

            @Override
            public MathContext mathContext() {
                return MathContext.DECIMAL32;
            }

            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> type) {
                return type.isInstance(value);
            }

            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                return this.successfulConversion(
                    target.cast(value),
                    target
                );
            }

            @Override
            public SpreadsheetMetadata spreadsheetMetadata() {
                return SpreadsheetMetadata.EMPTY.set(
                    SpreadsheetMetadataPropertyName.GENERAL_NUMBER_FORMAT_DIGIT_COUNT,
                    DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT
                );
            }

            @Override
            public char decimalSeparator() {
                return '.';
            }

            @Override
            public char zeroDigit() {
                return '0';
            }
        };
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterNumberToText> type() {
        return SpreadsheetConverterNumberToText.class;
    }
}
