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

import walkingkooka.convert.BinaryNumberConverterFunctions;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextTesting;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataLoader;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.tree.expression.convert.ExpressionNumberBinaryNumberConverterFunctions;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;

import java.math.MathContext;
import java.util.Objects;
import java.util.Optional;

public final class SpreadsheetConverterNumberToTextSpreadsheetConverterContextTest implements SpreadsheetConverterContextTesting2<SpreadsheetConverterNumberToTextSpreadsheetConverterContext>,
    DecimalNumberContextDelegator,
    SpreadsheetEnvironmentContextTesting {

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
    public void testParseStoragePathWithNullFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testDateTimeSymbolsForLocaleWithNullFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testDecimalNumberSymbolsForLocaleWithNullFails() {
        throw new UnsupportedOperationException();
    }

    private final static SpreadsheetId SPREADSHEET_ID = SpreadsheetId.with(1);

    private final static SpreadsheetMetadata SPREADSHEET_METADATA = SpreadsheetMetadata.EMPTY.set(
        SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
        SPREADSHEET_ID
    );

    private final static SpreadsheetMetadataLoader SPREADSHEET_METADATA_LOADER = new SpreadsheetMetadataLoader() {
        @Override
        public Optional<SpreadsheetMetadata> loadMetadata(final SpreadsheetId spreadsheetId) {
            Objects.requireNonNull(spreadsheetId, "spreadsheetId");

            return Optional.ofNullable(
                SPREADSHEET_ID.equals(spreadsheetId) ?
                    SPREADSHEET_METADATA :
                    null
            );
        }
    };

    @Override
    public SpreadsheetConverterNumberToTextSpreadsheetConverterContext createContext() {
        return SpreadsheetConverterNumberToTextSpreadsheetConverterContext.with(
            SpreadsheetConverterContexts.basic(
                HAS_USER_DIRECTORIES,
                SpreadsheetConverterContexts.NO_METADATA,
                SpreadsheetConverterContexts.NO_VALIDATION_REFERENCE,
                Converters.fake(),
                MEDIA_TYPE_DETECTOR,
                BinaryNumberConverterFunctions.multiply(), // multiplier
                SpreadsheetLabelNameResolvers.empty(),
                SPREADSHEET_METADATA_LOADER,
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
                            DATE_TIME_CONTEXT,
                            DECIMAL_NUMBER_CONTEXT
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
