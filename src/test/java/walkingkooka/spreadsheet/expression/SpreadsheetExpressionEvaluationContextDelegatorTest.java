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

package walkingkooka.spreadsheet.expression;

import walkingkooka.net.Url;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;

import java.math.MathContext;
import java.util.Objects;
import java.util.Optional;

public final class SpreadsheetExpressionEvaluationContextDelegatorTest implements SpreadsheetExpressionEvaluationContextTesting<SpreadsheetExpressionEvaluationContextDelegatorTest.TestSpreadsheetExpressionEvaluationContextDelegator>,
        SpreadsheetMetadataTesting {

    @Override
    public void testEvaluateExpressionUnknownFunctionNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TestSpreadsheetExpressionEvaluationContextDelegator createContext() {
        return new TestSpreadsheetExpressionEvaluationContextDelegator();
    }

    @Override
    public String currencySymbol() {
        return new TestSpreadsheetExpressionEvaluationContextDelegator()
                .currencySymbol();
    }

    @Override
    public char decimalSeparator() {
        return new TestSpreadsheetExpressionEvaluationContextDelegator()
                .decimalSeparator();
    }

    @Override
    public String exponentSymbol() {
        return new TestSpreadsheetExpressionEvaluationContextDelegator()
                .exponentSymbol();
    }

    @Override
    public char groupSeparator() {
        return new TestSpreadsheetExpressionEvaluationContextDelegator()
                .groupSeparator();
    }

    @Override
    public MathContext mathContext() {
        return new TestSpreadsheetExpressionEvaluationContextDelegator()
                .mathContext();
    }

    @Override
    public char negativeSign() {
        return new TestSpreadsheetExpressionEvaluationContextDelegator()
                .negativeSign();
    }

    @Override
    public char percentageSymbol() {
        return new TestSpreadsheetExpressionEvaluationContextDelegator()
                .percentageSymbol();
    }

    @Override
    public char positiveSign() {
        return new TestSpreadsheetExpressionEvaluationContextDelegator()
                .positiveSign();
    }

    // class............................................................................................................

    @Override
    public Class<TestSpreadsheetExpressionEvaluationContextDelegator> type() {
        return TestSpreadsheetExpressionEvaluationContextDelegator.class;
    }

    final static class TestSpreadsheetExpressionEvaluationContextDelegator implements SpreadsheetExpressionEvaluationContextDelegator {
        @Override
        public SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext() {
            return SpreadsheetExpressionEvaluationContexts.basic(
                    Optional.empty(), // cell
                    SpreadsheetCellStores.treeMap(),
                    Url.parseAbsolute("https://example.com"),
                    (rr) -> {
                        Objects.requireNonNull(rr, "rr");
                        throw new UnsupportedOperationException();
                    },
                    METADATA_EN_AU,
                    SPREADSHEET_FORMULA_CONVERTER_CONTEXT,
                    EXPRESSION_FUNCTION_PROVIDER,
                    PROVIDER_CONTEXT
            );
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
}
