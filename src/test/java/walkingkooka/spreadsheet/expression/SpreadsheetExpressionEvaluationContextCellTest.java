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

import walkingkooka.Cast;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.spreadsheet.SpreadsheetContextSupplier;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextFactory;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContexts;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.terminal.TerminalContextTesting;

import java.math.MathContext;
import java.math.RoundingMode;

public final class SpreadsheetExpressionEvaluationContextCellTest implements SpreadsheetExpressionEvaluationContextTesting2<SpreadsheetExpressionEvaluationContextCell>,
    DecimalNumberContextDelegator,
    SpreadsheetMetadataTesting,
    TerminalContextTesting {

    private final static SpreadsheetContextSupplier SPREADSHEET_CONTEXT_SUPPLIER = (id) -> {
        throw new UnsupportedOperationException();
    };

    private final static SpreadsheetMetadataContext SPREADSHEET_METADATA_CONTEXT = SpreadsheetMetadataContexts.basic(
        SPREADSHEET_METADATA_CREATOR,
        SpreadsheetMetadataStores.readOnly(
            SpreadsheetMetadataStores.treeMap()
        )
    );

    static {
        SpreadsheetEnvironmentContext context = SpreadsheetMetadataTesting.SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

        for (final EnvironmentValueName<?> name : SpreadsheetEnvironmentContextFactory.ENVIRONMENT_VALUE_NAMES) {
            if (name.equals(SpreadsheetEnvironmentContextFactory.CONVERTER)) {
                continue;
            }

            context.setEnvironmentValue(
                name,
                Cast.to(
                    METADATA_EN_AU.getOrFail(
                        SpreadsheetMetadataPropertyName.fromEnvironmentValueName(name)
                    )
                )
            );
        }

        context.setEnvironmentValue(
            SpreadsheetEnvironmentContextFactory.CONVERTER,
            METADATA_EN_AU.getOrFail(
                SpreadsheetMetadataPropertyName.VALIDATION_CONVERTER
            )
        );
        context.setEnvironmentValue(
            SpreadsheetEnvironmentContextFactory.DECIMAL_NUMBER_DIGIT_COUNT,
            DECIMAL_NUMBER_DIGIT_COUNT
        );

        context.setEnvironmentValue(
            SpreadsheetEnvironmentContext.CURRENT_WORKING_DIRECTORY,
            CURRENT_WORKING_DIRECTORY
        );

        context.setSpreadsheetId(OPTIONAL_SPREADSHEET_ID);

        SPREADSHEET_ENVIRONMENT_CONTEXT = context;
    }

    private final static SpreadsheetEnvironmentContext SPREADSHEET_ENVIRONMENT_CONTEXT;

    @Override
    public void testSetCellWithSame() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testInputNotNull() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testOutputNotNull() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testErrorNotNull() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEnvironmentContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEvaluateExpressionUnknownFunctionNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetSpreadsheetMetadataWithDifferentIdFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetExpressionEvaluationContextCell createContext() {
        return (SpreadsheetExpressionEvaluationContextCell)
            SpreadsheetExpressionEvaluationContextCell.with(
                OPTIONAL_SPREADSHEET_CELL,
                SpreadsheetExpressionEvaluationContexts.spreadsheetEnvironmentContext(
                    MEDIA_TYPE_DETECTOR,
                    MULTIPLIER,
                    SPREADSHEET_CONTEXT_SUPPLIER,
                    CURRENCY_LOCALE_CONTEXT,
                    SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                    SPREADSHEET_METADATA_CONTEXT,
                    TERMINAL_CONTEXT,
                    SPREADSHEET_PROVIDER,
                    PROVIDER_CONTEXT
                )
            );
    }

    // DecimalNumberContextDelegator....................................................................................

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return DECIMAL_NUMBER_CONTEXT;
    }


    @Override
    public int decimalNumberDigitCount() {
        return 8;
    }

    @Override
    public MathContext mathContext() {
        return new MathContext(
            7,
            RoundingMode.HALF_UP
        );
    }

    // Class............................................................................................................

    @Override
    public Class<SpreadsheetExpressionEvaluationContextCell> type() {
        return SpreadsheetExpressionEvaluationContextCell.class;
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
}