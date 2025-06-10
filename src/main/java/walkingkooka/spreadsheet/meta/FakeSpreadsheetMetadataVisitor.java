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

package walkingkooka.spreadsheet.meta;

import walkingkooka.color.Color;
import walkingkooka.convert.provider.ConverterAliasSet;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.environment.AuditInfo;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.plugin.PluginNameSet;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetName;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorAliasSet;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorNameList;
import walkingkooka.spreadsheet.engine.SpreadsheetCellQuery;
import walkingkooka.spreadsheet.export.SpreadsheetExporterAliasSet;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterAliasSet;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.importer.SpreadsheetImporterAliasSet;
import walkingkooka.spreadsheet.parser.SpreadsheetParserAliasSet;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetViewport;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionAliasSet;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.validation.form.provider.FormHandlerAliasSet;
import walkingkooka.validation.form.provider.FormHandlerSelector;
import walkingkooka.validation.provider.ValidatorAliasSet;
import walkingkooka.visit.Visiting;

import java.math.RoundingMode;
import java.util.Locale;

public class FakeSpreadsheetMetadataVisitor extends SpreadsheetMetadataVisitor {

    protected FakeSpreadsheetMetadataVisitor() {
        super();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetMetadata metadata) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final SpreadsheetMetadata metadata) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetMetadataPropertyName<?> property, final Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final SpreadsheetMetadataPropertyName<?> property, final Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitAuditInfo(final AuditInfo auditInfo) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitCellCharacterWidth(final int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitClipboardExporter(final SpreadsheetExporterAliasSet aliases) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitClipboardImporter(final SpreadsheetImporterAliasSet aliases) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitComparators(final SpreadsheetComparatorAliasSet aliases) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitConverters(final ConverterAliasSet aliases) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitDateFormatter(final SpreadsheetFormatterSelector selector) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitDateParser(final SpreadsheetParserSelector selector) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitDateTimeFormatter(final SpreadsheetFormatterSelector selector) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitDateTimeOffset(final long offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitDateTimeParser(final SpreadsheetParserSelector selector) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitDateTimeSymbols(final DateTimeSymbols symbols) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitDecimalNumberSymbols(final DecimalNumberSymbols symbols) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitDefaultYear(final int defaultYear) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitExporters(final SpreadsheetExporterAliasSet aliases) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitExpressionNumberKind(final ExpressionNumberKind expressionNumberKind) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitFindConverter(final ConverterSelector selector) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitFindFunctions(final ExpressionFunctionAliasSet aliases) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitFindQuery(final SpreadsheetCellQuery query) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitFindHighlighting(final boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitFormatterFunctions(final ExpressionFunctionAliasSet functions) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitFormattingConverter(final ConverterSelector selector) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitFormattingFunctions(final ExpressionFunctionAliasSet functions) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitFormHandlers(final FormHandlerAliasSet aliases) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitFormulaConverter(final ConverterSelector selector) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitFormulaFunctions(final ExpressionFunctionAliasSet aliases) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitFrozenColumns(final SpreadsheetColumnRangeReference range) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitFrozenRows(final SpreadsheetRowRangeReference range) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitFunctions(final ExpressionFunctionAliasSet aliases) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitGeneralNumberFormatDigitCount(final int generalFormatDigitCount) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitHideZeroValues(final boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitImporters(final SpreadsheetImporterAliasSet aliases) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitLocale(final Locale locale) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitNamedColor(final SpreadsheetColorName name,
                                   final int colorNumber) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitNumberedColor(final int number, final Color color) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitNumberFormatter(final SpreadsheetFormatterSelector selector) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitNumberParser(final SpreadsheetParserSelector selector) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitParsers(final SpreadsheetParserAliasSet aliases) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitPlugins(final PluginNameSet names) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitPrecision(final int precision) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitRoundingMode(final RoundingMode roundingMode) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitSortComparators(final SpreadsheetComparatorNameList comparatorNames) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitSortConverter(final ConverterSelector selector) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitSpreadsheetId(final SpreadsheetId id) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitSpreadsheetName(final SpreadsheetName name) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitStyle(final TextStyle style) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitTextFormatter(final SpreadsheetFormatterSelector selector) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitTimeFormatter(final SpreadsheetFormatterSelector selector) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitTimeParser(final SpreadsheetParserSelector selector) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitTwoDigitYear(final int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitValidatorConverter(final ConverterSelector selector) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitValidatorFormHandler(final FormHandlerSelector selector) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitValidatorFunctions(final ExpressionFunctionAliasSet aliases) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitValidatorValidators(final ValidatorAliasSet aliases) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitValidators(final ValidatorAliasSet aliases) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitValueSeparator(final char valueSeparator) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitViewport(final SpreadsheetViewport viewport) {
        throw new UnsupportedOperationException();
    }
}
