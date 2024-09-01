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

import walkingkooka.Cast;
import walkingkooka.convert.provider.ConverterProvider;
import walkingkooka.convert.provider.ConverterProviderDelegator;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorProvider;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorProviderDelegator;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviderDelegator;
import walkingkooka.spreadsheet.importer.SpreadsheetImporterProvider;
import walkingkooka.spreadsheet.importer.SpreadsheetImporterProviderDelegator;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProvider;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProviderDelegator;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviderDelegator;

import java.util.Objects;

/**
 * A {@link SpreadsheetProvider} that delegates all methods to the provided {@link walkingkooka.plugin.Provider}.
 */
final class BasicSpreadsheetProvider implements SpreadsheetProvider,
        ConverterProviderDelegator,
        ExpressionFunctionProviderDelegator,
        SpreadsheetComparatorProviderDelegator,
        SpreadsheetFormatterProviderDelegator,
        SpreadsheetImporterProviderDelegator,
        SpreadsheetParserProviderDelegator {

    static BasicSpreadsheetProvider with(final ConverterProvider converterProvider,
                                         final ExpressionFunctionProvider expressionFunctionProvider,
                                         final SpreadsheetComparatorProvider spreadsheetComparatorProvider,
                                         final SpreadsheetFormatterProvider spreadsheetFormatterProvider,
                                         final SpreadsheetImporterProvider spreadsheetImporterProvider,
                                         final SpreadsheetParserProvider spreadsheetParserProvider) {
        return new BasicSpreadsheetProvider(
                Objects.requireNonNull(converterProvider, "converterProvider"),
                Objects.requireNonNull(expressionFunctionProvider, "expressionFunctionProvider"),
                Objects.requireNonNull(spreadsheetComparatorProvider, "spreadsheetComparatorProvider"),
                Objects.requireNonNull(spreadsheetFormatterProvider, "spreadsheetFormatterProvider"),
                Objects.requireNonNull(spreadsheetImporterProvider, "spreadsheetImporterProvider"),
                Objects.requireNonNull(spreadsheetParserProvider, "spreadsheetParserProvider")
        );
    }

    private BasicSpreadsheetProvider(final ConverterProvider converterProvider,
                                     final ExpressionFunctionProvider expressionFunctionProvider,
                                     final SpreadsheetComparatorProvider spreadsheetComparatorProvider,
                                     final SpreadsheetFormatterProvider spreadsheetFormatterProvider,
                                     final SpreadsheetImporterProvider spreadsheetImporterProvider,
                                     final SpreadsheetParserProvider spreadsheetParserProvider) {
        this.converterProvider = converterProvider;
        this.expressionFunctionProvider = expressionFunctionProvider;
        this.spreadsheetComparatorProvider = spreadsheetComparatorProvider;
        this.spreadsheetFormatterProvider = spreadsheetFormatterProvider;
        this.spreadsheetImporterProvider = spreadsheetImporterProvider;
        this.spreadsheetParserProvider = spreadsheetParserProvider;
    }

    @Override
    public ConverterProvider converterProvider() {
        return this.converterProvider;
    }

    private final ConverterProvider converterProvider;

    @Override
    public ExpressionFunctionProvider expressionFunctionProvider() {
        return expressionFunctionProvider;
    }

    private final ExpressionFunctionProvider expressionFunctionProvider;

    @Override
    public SpreadsheetComparatorProvider spreadsheetComparatorProvider() {
        return this.spreadsheetComparatorProvider;
    }

    private final SpreadsheetComparatorProvider spreadsheetComparatorProvider;

    @Override
    public SpreadsheetFormatterProvider spreadsheetFormatterProvider() {
        return this.spreadsheetFormatterProvider;
    }

    private final SpreadsheetFormatterProvider spreadsheetFormatterProvider;

    @Override
    public SpreadsheetImporterProvider spreadsheetImporterProvider() {
        return this.spreadsheetImporterProvider;
    }

    private final SpreadsheetImporterProvider spreadsheetImporterProvider;

    @Override
    public SpreadsheetParserProvider spreadsheetParserProvider() {
        return this.spreadsheetParserProvider;
    }

    private final SpreadsheetParserProvider spreadsheetParserProvider;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.converterProvider,
                this.expressionFunctionProvider,
                this.spreadsheetComparatorProvider,
                this.spreadsheetFormatterProvider,
                this.spreadsheetParserProvider
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof BasicSpreadsheetProvider &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final BasicSpreadsheetProvider other) {
        return this.converterProvider.equals(other.converterProvider) &&
                this.expressionFunctionProvider.equals(other.expressionFunctionProvider) &&
                this.spreadsheetComparatorProvider.equals(other.spreadsheetComparatorProvider) &&
                this.spreadsheetFormatterProvider.equals(other.spreadsheetFormatterProvider) &&
                this.spreadsheetImporterProvider.equals(other.spreadsheetImporterProvider) &&
                this.spreadsheetParserProvider.equals(other.spreadsheetParserProvider);
    }

    @Override
    public String toString() {
        return this.converterProvider +
                " " +
                this.expressionFunctionProvider +
                " " +
                this.spreadsheetComparatorProvider +
                " " +
                this.spreadsheetFormatterProvider +
                " " +
                this.spreadsheetImporterProvider +
                " " +
                this.spreadsheetParserProvider;
    }
}
