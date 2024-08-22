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

package walkingkooka.spreadsheet.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.convert.provider.ConverterProviders;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProviders;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviders;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetProviderTest implements SpreadsheetProviderTesting<BasicSpreadsheetProvider>,
        HashCodeEqualsDefinedTesting2<BasicSpreadsheetProvider>,
        ToStringTesting<BasicSpreadsheetProvider>,
        SpreadsheetMetadataTesting {

    @Override
    public void testExpressionFunctionWithNullNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testExpressionFunctionWithNullContextFails() {
        throw new UnsupportedOperationException();
    }

    // with.............................................................................................................

    @Test
    public void testWithNullConverterProviderFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetProvider.with(
                        null,
                        EXPRESSION_FUNCTION_PROVIDER,
                        SPREADSHEET_COMPARATOR_PROVIDER,
                        SPREADSHEET_FORMATTER_PROVIDER,
                        SPREADSHEET_PARSER_PROVIDER
                )
        );
    }

    @Test
    public void testWithNullExpressionFunctionProviderFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetProvider.with(
                        CONVERTER_PROVIDER,
                        null,
                        SPREADSHEET_COMPARATOR_PROVIDER,
                        SPREADSHEET_FORMATTER_PROVIDER,
                        SPREADSHEET_PARSER_PROVIDER
                )
        );
    }

    @Test
    public void testWithNullSpreadsheetComparatorProviderFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetProvider.with(
                        CONVERTER_PROVIDER,
                        EXPRESSION_FUNCTION_PROVIDER,
                        null,
                        SPREADSHEET_FORMATTER_PROVIDER,
                        SPREADSHEET_PARSER_PROVIDER
                )
        );
    }

    @Test
    public void testWithNullSpreadsheetFormatterProviderFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetProvider.with(
                        CONVERTER_PROVIDER,
                        EXPRESSION_FUNCTION_PROVIDER,
                        SPREADSHEET_COMPARATOR_PROVIDER,
                        null,
                        SPREADSHEET_PARSER_PROVIDER
                )
        );
    }

    @Test
    public void testWithNullSpreadsheetParserProviderFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetProvider.with(
                        CONVERTER_PROVIDER,
                        EXPRESSION_FUNCTION_PROVIDER,
                        SPREADSHEET_COMPARATOR_PROVIDER,
                        SPREADSHEET_FORMATTER_PROVIDER,
                        null
                )
        );
    }

    @Override
    public BasicSpreadsheetProvider createSpreadsheetProvider() {
        return BasicSpreadsheetProvider.with(
                CONVERTER_PROVIDER,
                EXPRESSION_FUNCTION_PROVIDER,
                SPREADSHEET_COMPARATOR_PROVIDER,
                SPREADSHEET_FORMATTER_PROVIDER,
                SPREADSHEET_PARSER_PROVIDER
        );
    }

    // hashCode/equals...................................................................................................
    @Test
    public void testEqualsDifferentConverterProvider() {
        this.checkNotEquals(
                BasicSpreadsheetProvider.with(
                        ConverterProviders.fake(),
                        EXPRESSION_FUNCTION_PROVIDER,
                        SPREADSHEET_COMPARATOR_PROVIDER,
                        SPREADSHEET_FORMATTER_PROVIDER,
                        SPREADSHEET_PARSER_PROVIDER
                )
        );
    }

    @Test
    public void testEqualsDifferentExpressionFunctionProvider() {
        this.checkNotEquals(
                BasicSpreadsheetProvider.with(
                        CONVERTER_PROVIDER,
                        ExpressionFunctionProviders.fake(),
                        SPREADSHEET_COMPARATOR_PROVIDER,
                        SPREADSHEET_FORMATTER_PROVIDER,
                        SPREADSHEET_PARSER_PROVIDER
                )
        );
    }

    @Test
    public void testEqualsDifferentSpreadsheetComparatorProvider() {
        this.checkNotEquals(
                BasicSpreadsheetProvider.with(
                        CONVERTER_PROVIDER,
                        EXPRESSION_FUNCTION_PROVIDER,
                        SpreadsheetComparatorProviders.fake(),
                        SPREADSHEET_FORMATTER_PROVIDER,
                        SPREADSHEET_PARSER_PROVIDER
                )
        );
    }

    @Test
    public void testEqualsDifferentSpreadsheetFormatterProvider() {
        this.checkNotEquals(
                BasicSpreadsheetProvider.with(
                        CONVERTER_PROVIDER,
                        EXPRESSION_FUNCTION_PROVIDER,
                        SPREADSHEET_COMPARATOR_PROVIDER,
                        SpreadsheetFormatterProviders.fake(),
                        SPREADSHEET_PARSER_PROVIDER
                )
        );
    }

    @Test
    public void testEqualsDifferentSpreadsheetParserProvider() {
        this.checkNotEquals(
                BasicSpreadsheetProvider.with(
                        CONVERTER_PROVIDER,
                        EXPRESSION_FUNCTION_PROVIDER,
                        SPREADSHEET_COMPARATOR_PROVIDER,
                        SPREADSHEET_FORMATTER_PROVIDER,
                        SpreadsheetParserProviders.fake()
                )
        );
    }

    @Override
    public BasicSpreadsheetProvider createObject() {
        return this.createSpreadsheetProvider();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createSpreadsheetProvider(),
                CONVERTER_PROVIDER + " " +
                        EXPRESSION_FUNCTION_PROVIDER + " " +
                        SPREADSHEET_COMPARATOR_PROVIDER + " " +
                        SPREADSHEET_FORMATTER_PROVIDER + " " +
                        SPREADSHEET_PARSER_PROVIDER
        );
    }

    // Class............................................................................................................

    @Override
    public Class<BasicSpreadsheetProvider> type() {
        return BasicSpreadsheetProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
