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

import walkingkooka.Cast;
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
import walkingkooka.visit.Visitor;

import java.math.RoundingMode;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * A {@link Visitor} which dispatches each {@link SpreadsheetMetadataPropertyName} to a visit method which accepts the accompanying
 * value. Note it does not visit default properties only those immediately set upon the given {@link SpreadsheetMetadata}.
 */
public abstract class SpreadsheetMetadataVisitor extends Visitor<SpreadsheetMetadata> {

    protected SpreadsheetMetadataVisitor() {
    }

    // Visitor..........................................................................................................

    @Override
    public final void accept(final SpreadsheetMetadata metadata) {
        Objects.requireNonNull(metadata, "metadata");

        if (Visiting.CONTINUE == this.startVisit(metadata)) {
            metadata.accept(this);
        }
        this.endVisit(metadata);
    }

    // SpreadsheetMetadata........................................................................................................

    protected Visiting startVisit(final SpreadsheetMetadata metadata) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetMetadata metadata) {
        // nop
    }

    // entries..........................................................................................................

    final void acceptPropertyAndValue(final Entry<SpreadsheetMetadataPropertyName<?>, Object> entry) {
        final SpreadsheetMetadataPropertyName<?> propertyName = entry.getKey();
        final Object value = entry.getValue();

        if (Visiting.CONTINUE == this.startVisit(propertyName, value)) {
            propertyName.accept(Cast.to(value), this);
        }
        this.endVisit(propertyName, value);
    }

    protected Visiting startVisit(final SpreadsheetMetadataPropertyName<?> property, final Object value) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetMetadataPropertyName<?> property, final Object value) {
    }

    // properties..........................................................................................................

    protected void visitAuditInfo(final AuditInfo auditInfo) {
        // nop
    }

    protected void visitCellCharacterWidth(final int value) {
        // nop
    }

    protected void visitClipboardExporter(final SpreadsheetExporterAliasSet aliases) {
        // nop
    }

    protected void visitClipboardImporter(final SpreadsheetImporterAliasSet aliases) {
        // nop
    }

    protected void visitComparators(final SpreadsheetComparatorAliasSet aliases) {
        // nop
    }

    protected void visitConverters(final ConverterAliasSet aliases) {
        // nop
    }

    protected void visitDateFormatter(final SpreadsheetFormatterSelector selector) {
        // nop
    }

    protected void visitDateParser(final SpreadsheetParserSelector selector) {
        // nop
    }

    protected void visitDateTimeOffset(final long offset) {
        // nop
    }

    protected void visitDateTimeParser(final SpreadsheetParserSelector selector) {
        // nop
    }

    protected void visitDateTimeFormatter(final SpreadsheetFormatterSelector selector) {
        // nop
    }

    protected void visitDateTimeSymbols(final DateTimeSymbols symbols) {
        // nop
    }

    protected void visitDecimalNumberSymbols(final DecimalNumberSymbols symbols) {
        // nop
    }

    protected void visitDefaultFormHandler(final FormHandlerSelector selector) {
        // nop
    }

    protected void visitDefaultYear(final int defaultYear) {
        // nop
    }

    protected void visitExporters(final SpreadsheetExporterAliasSet aliases) {
        // nop
    }

    protected void visitExpressionNumberKind(final ExpressionNumberKind expressionNumberKind) {
        // nop
    }

    protected void visitFindConverter(final ConverterSelector selector) {
        // nop
    }

    protected void visitFindFunctions(final ExpressionFunctionAliasSet aliases) {
        // nop
    }

    protected void visitFindHighlighting(final boolean value) {
        // nop
    }

    protected void visitFindQuery(final SpreadsheetCellQuery query) {
        // nop
    }

    protected void visitFormatters(final SpreadsheetFormatterAliasSet aliases) {
        // nop
    }

    protected void visitFormattingConverter(final ConverterSelector selector) {
        // nop
    }

    protected void visitFormattingFunctions(final ExpressionFunctionAliasSet functions) {
        // nop
    }

    protected void visitFormHandlers(final FormHandlerAliasSet aliases) {
        // nop
    }

    protected void visitFormulaConverter(final ConverterSelector selector) {
        // nop
    }

    protected void visitFormulaFunctions(final ExpressionFunctionAliasSet aliases) {
        // nop
    }

    protected void visitFrozenColumns(final SpreadsheetColumnRangeReference range) {
        // nop
    }

    protected void visitFrozenRows(final SpreadsheetRowRangeReference range) {
        // nop
    }

    protected void visitFunctions(final ExpressionFunctionAliasSet aliases) {
        // nop
    }

    protected void visitGeneralNumberFormatDigitCount(final int generalFormatDigitCount) {
        // nop
    }

    protected void visitGridLines(final boolean show) {
        // nop
    }

    protected void visitHeadings(final boolean show) {
        // nop
    }

    protected void visitHideZeroValues(final boolean value) {
        // nop
    }

    protected void visitImporters(final SpreadsheetImporterAliasSet aliases) {
        // nop
    }

    protected void visitLocale(final Locale locale) {
        // nop
    }

    protected void visitNamedColor(final SpreadsheetColorName name,
                                   final int colorNumber) {
        // nop
    }

    protected void visitNumberedColor(final int number, final Color color) {
        // nop
    }

    protected void visitNumberFormatter(final SpreadsheetFormatterSelector selector) {
        // nop
    }

    protected void visitNumberParser(final SpreadsheetParserSelector selector) {
        // nop
    }

    protected void visitParsers(final SpreadsheetParserAliasSet aliases) {
        // nop
    }

    protected void visitPlugins(final PluginNameSet names) {
        // nop
    }

    protected void visitPrecision(final int precision) {
        // nop
    }

    protected void visitRoundingMode(final RoundingMode roundingMode) {
        // nop
    }

    protected void visitScriptingConverter(final ConverterSelector selector) {
        // nop
    }

    protected void visitScriptingFunctions(final ExpressionFunctionAliasSet aliases) {
        // nop
    }

    protected void visitSortComparators(final SpreadsheetComparatorNameList comparatorNames) {
        // nop
    }

    protected void visitSortConverter(final ConverterSelector selector) {
        // nop
    }

    protected void visitSpreadsheetId(final SpreadsheetId id) {
        // nop
    }

    protected void visitSpreadsheetName(final SpreadsheetName name) {
        // nop
    }

    protected void visitStyle(final TextStyle style) {
        // nop
    }

    protected void visitTextFormatter(final SpreadsheetFormatterSelector selector) {
        // nop
    }

    protected void visitTimeFormatter(final SpreadsheetFormatterSelector selector) {
        // nop
    }

    protected void visitTimeParser(final SpreadsheetParserSelector selector) {
        // nop
    }

    protected void visitTwoDigitYear(final int value) {
        // nop
    }

    protected void visitValidators(final ValidatorAliasSet aliases) {
        // nop
    }

    protected void visitValidationConverter(final ConverterSelector selector) {
        // nop
    }

    protected void visitvalidationFunctions(final ExpressionFunctionAliasSet aliases) {
        // nop
    }

    protected void visitvalidationValidators(final ValidatorAliasSet aliases) {
        // nop
    }

    protected void visitValueSeparator(final char valueSeparator) {
        // nop
    }

    protected void visitViewport(final SpreadsheetViewport viewport) {
        // nop
    }
}
