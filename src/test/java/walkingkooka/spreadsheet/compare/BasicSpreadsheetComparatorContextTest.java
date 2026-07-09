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

package walkingkooka.spreadsheet.compare;

import org.junit.jupiter.api.Test;
import walkingkooka.convert.BinaryNumberConverterFunctions;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.locale.LocaleContextTesting;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContexts;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataLoader;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.storage.HasUserDirectorieses;
import walkingkooka.tree.expression.convert.ExpressionNumberBinaryNumberConverterFunctions;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;

import java.math.MathContext;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetComparatorContextTest implements SpreadsheetComparatorContextTesting<BasicSpreadsheetComparatorContext>,
    DecimalNumberContextDelegator,
    LocaleContextTesting {

    private final static SpreadsheetId SPREADSHEET_ID = SpreadsheetId.with(1);

    private final static SpreadsheetMetadata SPREADSHEET_METADATA = SpreadsheetMetadata.EMPTY.set(
        SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
        SPREADSHEET_ID
    );

    private final static SpreadsheetMetadataLoader SPREADSHEET_METADATA_LOADER = new SpreadsheetMetadataLoader() {
        @Override
        public Optional<SpreadsheetMetadata> loadMetadata(final SpreadsheetId id) {
            Objects.requireNonNull(id, "id");

            return Optional.ofNullable(
                SPREADSHEET_ID.equals(id) ?
                    SPREADSHEET_METADATA :
                    null
            );
        }
    };

    private final static BiFunction<Object, Object, SpreadsheetExpressionEvaluationContext> SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT_FACTORY = (final Object left,
                                                                                                                                                 final Object right) -> {
        throw new UnsupportedOperationException();
    };

    private final static SpreadsheetConverterContext CONTEXT = SpreadsheetConverterContexts.basic(
        HasUserDirectorieses.fake(),
        SpreadsheetConverterContexts.NO_METADATA,
        SpreadsheetConverterContexts.NO_VALIDATION_REFERENCE,
        Converters.objectToString(),
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
                    Converters.objectToString(),
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
    );

    @Test
    public void testWithNullSpreadsheetExpressionEvaluationContextFactoryFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetComparatorContext.with(
                null,
                CONTEXT
            )
        );
    }

    @Test
    public void testWithNullConverterContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetComparatorContext.with(
                SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT_FACTORY,
                null
            )
        );
    }

    @Test
    public void testConvert() {
        this.convertAndCheck(
            this.createContext(),
            LocalDate.of(1999, 12, 31),
            String.class,
            "1999-12-31"
        );
    }

    @Test
    public void testLoadMetadata() {
        this.loadMetadataAndCheck(
            this.createContext(),
            SPREADSHEET_ID,
            SPREADSHEET_METADATA
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            BasicSpreadsheetComparatorContext.with(
                SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT_FACTORY,
                CONTEXT
            ),
            CONTEXT.toString()
        );
    }

    @Override
    public BasicSpreadsheetComparatorContext createContext() {
        return BasicSpreadsheetComparatorContext.with(
            SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT_FACTORY,
            CONTEXT
        );
    }

    @Override
    public int decimalNumberDigitCount() {
        return CONTEXT.decimalNumberDigitCount();
    }

    @Override
    public MathContext mathContext() {
        return CONTEXT.mathContext();
    }

    // DecimalNumberContextDelegator....................................................................................

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return CONTEXT;
    }

    // class............................................................................................................

    @Override
    public Class<BasicSpreadsheetComparatorContext> type() {
        return BasicSpreadsheetComparatorContext.class;
    }
}
