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

import org.junit.jupiter.api.Test;
import walkingkooka.color.Color;
import walkingkooka.convert.Converters;
import walkingkooka.convert.provider.ConverterAliasSet;
import walkingkooka.convert.provider.ConverterProviders;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.environment.AuditInfo;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.PluginNameSet;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorAliasSet;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorNameList;
import walkingkooka.spreadsheet.engine.SpreadsheetCellQuery;
import walkingkooka.spreadsheet.export.SpreadsheetExporterAliasSet;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterAliasSet;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateTimeParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.importer.SpreadsheetImporterAliasSet;
import walkingkooka.spreadsheet.parser.SpreadsheetParserAliasSet;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewport;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionAliasSet;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.validation.form.provider.FormHandlerAliasSet;
import walkingkooka.validation.form.provider.FormHandlerSelector;
import walkingkooka.validation.provider.ValidatorAliasSet;
import walkingkooka.visit.Visiting;

import java.math.RoundingMode;
import java.text.DateFormatSymbols;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class SpreadsheetMetadataVisitorTest implements SpreadsheetMetadataVisitorTesting<SpreadsheetMetadataVisitor> {

    @Override
    public void testCheckToStringOverridden() {
    }

    @Override
    public void testClassVisibility() {
    }

    @Override
    public void testStartVisitMethodsSingleParameter() {
    }

    @Override
    public void testEndVisitMethodsSingleParameter() {
    }

    @Test
    public void testVisitSpreadsheetMetadataSkip() {
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY;

        new FakeSpreadsheetMetadataVisitor() {
            @Override
            protected Visiting startVisit(final SpreadsheetMetadata t) {
                assertSame(metadata, t);
                return Visiting.SKIP;
            }

            @Override
            protected void endVisit(final SpreadsheetMetadata t) {
                assertSame(metadata, t);
            }
        }.accept(metadata);
    }

    @Test
    public void testVisitSpreadsheetMetadataPropertyNameSkip() {
        final SpreadsheetMetadataPropertyName<AuditInfo> propertyName = SpreadsheetMetadataPropertyName.AUDIT_INFO;
        final AuditInfo value = AuditInfo.with(
            EmailAddress.parse("created@example.com"),
            LocalDateTime.MIN,
            EmailAddress.parse("modified@example.com"),
            LocalDateTime.MAX
        );
        final SpreadsheetMetadata metadata = metadata(propertyName, value);

        new FakeSpreadsheetMetadataVisitor() {
            @Override
            protected Visiting startVisit(final SpreadsheetMetadata t) {
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final SpreadsheetMetadata t) {
            }

            @Override
            protected Visiting startVisit(final SpreadsheetMetadataPropertyName<?> p, final Object v) {
                assertSame(propertyName, p, "propertyName");
                assertSame(value, v, "value");
                return Visiting.SKIP;
            }

            @Override
            protected void endVisit(final SpreadsheetMetadataPropertyName<?> p, final Object v) {
                assertSame(propertyName, p, "propertyName");
                assertSame(value, v, "value");
            }
        }.accept(metadata);
    }

    @Test
    public void testVisitAuditInfo() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitAuditInfo(final AuditInfo a) {
                this.visited = a;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.AUDIT_INFO,
            AuditInfo.with(
                EmailAddress.parse("created@example.com"),
                LocalDateTime.MIN,
                EmailAddress.parse("modified@example.com"),
                LocalDateTime.MAX
            )
        );
    }

    @Test
    public void testVisitAutoHideScrollbars() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitAutoHideScrollbars(final boolean b) {
                this.visited = b;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.AUTO_HIDE_SCROLLBARS,
            true
        );
    }

    @Test
    public void testVisitCellCharacterWidth() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitCellCharacterWidth(final int i) {
                this.visited = i;
            }
        }.accept(SpreadsheetMetadataPropertyName.CELL_CHARACTER_WIDTH, 0);
    }

    @Test
    public void testVisitClipboardExporter() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitClipboardExporter(final SpreadsheetExporterAliasSet a) {
                this.visited = a;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.CLIPBOARD_EXPORTER,
            SpreadsheetExporterAliasSet.parse("json")
        );
    }

    @Test
    public void testVisitComparators() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitComparators(final SpreadsheetComparatorAliasSet c) {
                this.visited = c;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.COMPARATORS,
            SpreadsheetComparatorAliasSet.parse("day, month, year")
        );
    }

    @Test
    public void testVisitConverters() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitConverters(final ConverterAliasSet c) {
                this.visited = c;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.CONVERTERS,
            ConverterProviders.converters()
                .converterInfos()
                .aliasSet()
        );
    }

    @Test
    public void testVisitDateFormatter() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitDateFormatter(final SpreadsheetFormatterSelector s) {
                this.visited = s;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.DATE_FORMATTER,
            SpreadsheetPattern.parseDateFormatPattern("DD/MM/YYYY")
                .spreadsheetFormatterSelector()
        );
    }

    @Test
    public void testVisitDateParser() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitDateParser(final SpreadsheetParserSelector p) {
                this.visited = p;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.DATE_PARSER,
            SpreadsheetDateTimeParsePattern.parseDateParsePattern("DD/MM/YYYY")
                .spreadsheetParserSelector()
        );
    }

    @Test
    public void testVisitDateTimeFormatter() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitDateTimeFormatter(final SpreadsheetFormatterSelector s) {
                this.visited = s;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER,
            SpreadsheetPattern.parseDateTimeFormatPattern("DD/MM/YYYY hh:mm").spreadsheetFormatterSelector()
        );
    }

    @Test
    public void testVisitDateTimeOffset() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitDateTimeOffset(final long offset) {
                this.visited = offset;
            }
        }.accept(SpreadsheetMetadataPropertyName.DATE_TIME_OFFSET, Converters.EXCEL_1900_DATE_SYSTEM_OFFSET);
    }

    @Test
    public void testVisitDateTimeParser() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitDateTimeParser(final SpreadsheetParserSelector s) {
                this.visited = s;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.DATE_TIME_PARSER,
            SpreadsheetDateTimeParsePattern.parseDateTimeParsePattern("DD/MM/YYYY HH:MM:SS;DDMMYYYY HHMMSS")
                .spreadsheetParserSelector()
        );
    }

    @Test
    public void testVisitDateTimeSymbols() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitDateTimeSymbols(final DateTimeSymbols s) {
                this.visited = s;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.DATE_TIME_SYMBOLS,
            DateTimeSymbols.fromDateFormatSymbols(
                new DateFormatSymbols(Locale.ENGLISH)
            ));
    }

    @Test
    public void testVisitDecimalNumberSymbols() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitDecimalNumberSymbols(final DecimalNumberSymbols s) {
                this.visited = s;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_SYMBOLS,
            DecimalNumberSymbols.fromDecimalFormatSymbols(
                '+',
                new DecimalFormatSymbols(Locale.ENGLISH)
            ));
    }

    @Test
    public void testVisitDefaultFormHandler() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitDefaultFormHandler(final FormHandlerSelector s) {
                this.visited = s;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.DEFAULT_FORM_HANDLER,
            FormHandlerSelector.parse("hello-form-handler")
        );
    }

    @Test
    public void testVisitDefaultYear() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitDefaultYear(final int i) {
                this.visited = i;
            }
        }.accept(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, 1901);
    }

    @Test
    public void testVisitFindConverter() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitFindConverter(final ConverterSelector s) {
                this.visited = s;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.FIND_CONVERTER,
            ConverterSelector.parse("general")
        );
    }

    @Test
    public void testVisitFindFunctions() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitFindFunctions(final ExpressionFunctionAliasSet a) {
                this.visited = a;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.FIND_FUNCTIONS,
            SpreadsheetExpressionFunctions.parseAliasSet("abs")
        );
    }

    @Test
    public void testVisitFindHighlighting() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitFindHighlighting(final boolean b) {
                this.visited = b;
            }
        }.accept(SpreadsheetMetadataPropertyName.FIND_HIGHLIGHTING, true);
    }

    @Test
    public void testVisitFindQuery() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitFindQuery(final SpreadsheetCellQuery q) {
                this.visited = q;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.FIND_QUERY,
            SpreadsheetCellQuery.parse("1+2")
        );
    }

    @Test
    public void testVisitFormatterFunctions() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitFormattingFunctions(final ExpressionFunctionAliasSet s) {
                this.visited = s;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.FORMATTING_FUNCTIONS,
            SpreadsheetExpressionFunctions.parseAliasSet("hello")
        );
    }

    @Test
    public void testVisitFormattingConverter() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitFormattingConverter(final ConverterSelector s) {
                this.visited = s;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.FORMATTING_CONVERTER,
            ConverterSelector.parse("general")
        );
    }

    @Test
    public void testvisitFormattingFunctions() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitFormattingFunctions(final ExpressionFunctionAliasSet s) {
                this.visited = s;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.FORMATTING_FUNCTIONS,
            SpreadsheetExpressionFunctions.parseAliasSet("hello")
        );
    }

    @Test
    public void testVisitFormHandlers() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitFormHandlers(final FormHandlerAliasSet a) {
                this.visited = a;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.FORM_HANDLERS,
            FormHandlerAliasSet.parse("hello")
        );
    }

    @Test
    public void testVisitFormulaConverter() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitFormulaConverter(final ConverterSelector s) {
                this.visited = s;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.FORMULA_CONVERTER,
            ConverterSelector.parse("general")
        );
    }

    @Test
    public void testVisitFormulaFunctions() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitFormulaFunctions(final ExpressionFunctionAliasSet a) {
                this.visited = a;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.FORMULA_FUNCTIONS,
            SpreadsheetExpressionFunctions.parseAliasSet("abs")
        );
    }

    @Test
    public void testVisitFrozenColumns() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitFrozenColumns(final SpreadsheetColumnRangeReference r) {
                this.visited = r;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.FROZEN_COLUMNS,
            SpreadsheetSelection.parseColumnRange("A:B")
        );
    }

    @Test
    public void testVisitFrozenRows() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitFrozenRows(final SpreadsheetRowRangeReference r) {
                this.visited = r;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.FROZEN_ROWS,
            SpreadsheetSelection.parseRowRange("1:2")
        );
    }

    @Test
    public void testVisitFunctions() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitFunctions(final ExpressionFunctionAliasSet a) {
                this.visited = a;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.FUNCTIONS,
            SpreadsheetExpressionFunctions.parseAliasSet("abs")
        );
    }

    @Test
    public void testVisitGeneralNumberFormatDigitCount() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitGeneralNumberFormatDigitCount(final int i) {
                this.visited = i;
            }
        }.accept(SpreadsheetMetadataPropertyName.GENERAL_NUMBER_FORMAT_DIGIT_COUNT, 123);
    }

    @Test
    public void testVisitLocale() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitLocale(final Locale l) {
                this.visited = l;
            }
        }.accept(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH);
    }

    @Test
    public void testVisitNamedColor() {
        final SpreadsheetColorName name = SpreadsheetColorName.with("shiny");
        final int colorNumber = 23;

        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitNamedColor(final SpreadsheetColorName n,
                                           final int c) {
                checkEquals(name, n, "name");
                this.visited = c;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.namedColor(name),
            colorNumber
        );
    }

    @Test
    public void testVisitNumberedColor() {
        final int number = 7;

        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitNumberedColor(final int n, final Color c) {
                checkEquals(number, n, "number");
                this.visited = c;
            }
        }.accept(SpreadsheetMetadataPropertyName.numberedColor(number), this.color());
    }

    @Test
    public void testVisitNumberFormatter() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitNumberFormatter(final SpreadsheetFormatterSelector s) {
                this.visited = s;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.NUMBER_FORMATTER,
            SpreadsheetPattern.parseNumberFormatPattern("#0.0").spreadsheetFormatterSelector()
        );
    }

    @Test
    public void testVisitNumberParser() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitNumberParser(final SpreadsheetParserSelector s) {
                this.visited = s;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.NUMBER_PARSER,
            SpreadsheetPattern.parseNumberParsePattern("#0.0;#0.00")
                .spreadsheetParserSelector()
        );
    }

    @Test
    public void testVisitPlugins() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitPlugins(final PluginNameSet n) {
                this.visited = n;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.PLUGINS,
            PluginNameSet.parse("TestPlugin111, TestPlugin222")
        );
    }

    @Test
    public void testVisitPrecision() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitPrecision(final int i) {
                this.visited = i;
            }
        }.accept(SpreadsheetMetadataPropertyName.PRECISION, 123);
    }

    @Test
    public void testVisitRoundingMode() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitRoundingMode(final RoundingMode r) {
                this.visited = r;
            }
        }.accept(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.HALF_UP);
    }

    @Test
    public void testVisitScriptingConverter() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitScriptingConverter(final ConverterSelector s) {
                this.visited = s;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.SCRIPTING_CONVERTER,
            ConverterSelector.parse("hello")
        );
    }

    @Test
    public void testVisitScriptingFunctions() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitScriptingFunctions(final ExpressionFunctionAliasSet a) {
                this.visited = a;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.SCRIPTING_FUNCTIONS,
            SpreadsheetExpressionFunctions.parseAliasSet("hello")
        );
    }

    @Test
    public void testVisitShowFormulaEditor() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitShowFormulaEditor(final boolean s) {
                this.visited = s;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.SHOW_FORMULA_EDITOR,
            true
        );
    }

    @Test
    public void testVisitShowFormulas() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitShowFormulas(final boolean s) {
                this.visited = s;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.SHOW_FORMULAS,
            true
        );
    }

    @Test
    public void testVisitShowGridLines() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitShowGridLines(final boolean s) {
                this.visited = s;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.SHOW_GRID_LINES,
            true
        );
    }

    @Test
    public void testVisitShowHeadings() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitShowHeadings(final boolean s) {
                this.visited = s;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.SHOW_HEADINGS,
            true
        );
    }

    @Test
    public void testVisitSortComparatorNames() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitSortComparators(final SpreadsheetComparatorNameList comparatorNames) {
                this.visited = comparatorNames;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.SORT_COMPARATORS,
            SpreadsheetComparatorNameList.parse("day-of-month")
        );
    }

    @Test
    public void testVisitSortConverter() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitSortConverter(final ConverterSelector s) {
                this.visited = s;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.SORT_CONVERTER,
            ConverterSelector.parse("hello")
        );
    }

    @Test
    public void testVisitSpreadsheetComparators() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitComparators(final SpreadsheetComparatorAliasSet s) {
                this.visited = s;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.COMPARATORS,
            SpreadsheetComparatorAliasSet.EMPTY
        );
    }

    @Test
    public void testVisitSpreadsheetExporters() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitExporters(final SpreadsheetExporterAliasSet s) {
                this.visited = s;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.EXPORTERS,
            SpreadsheetExporterAliasSet.EMPTY
        );
    }

    @Test
    public void testVisitSpreadsheetFormatters() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitFormatters(final SpreadsheetFormatterAliasSet s) {
                this.visited = s;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.FORMATTERS,
            SpreadsheetFormatterAliasSet.EMPTY
        );
    }

    @Test
    public void testVisitSpreadsheetId() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitSpreadsheetId(final SpreadsheetId i) {
                this.visited = i;
            }
        }.accept(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(123));
    }

    @Test
    public void testVisitSpreadsheetImporters() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitImporters(final SpreadsheetImporterAliasSet s) {
                this.visited = s;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.IMPORTERS,
            SpreadsheetImporterAliasSet.EMPTY
        );
    }

    @Test
    public void testVisitSpreadsheetParsers() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitParsers(final SpreadsheetParserAliasSet s) {
                this.visited = s;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.PARSERS,
            SpreadsheetParserAliasSet.EMPTY
        );
    }

    @Test
    public void testVisitStyle() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitStyle(final TextStyle style) {
                this.visited = style;
            }
        }.accept(SpreadsheetMetadataPropertyName.STYLE, SpreadsheetMetadata.NON_LOCALE_DEFAULTS.getOrFail(SpreadsheetMetadataPropertyName.STYLE));
    }

    @Test
    public void testVisitTextFormatter() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitTextFormatter(final SpreadsheetFormatterSelector s) {
                this.visited = s;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.TEXT_FORMATTER,
            SpreadsheetPattern.parseTextFormatPattern("@").spreadsheetFormatterSelector()
        );
    }

    @Test
    public void testVisitTimeFormatter() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitTimeFormatter(final SpreadsheetFormatterSelector s) {
                this.visited = s;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.TIME_FORMATTER,
            SpreadsheetPattern.parseTimeFormatPattern("hh:mm").spreadsheetFormatterSelector()
        );
    }

    @Test
    public void testVisitTimeParser() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitTimeParser(final SpreadsheetParserSelector s) {
                this.visited = s;
            }
        }.accept(SpreadsheetMetadataPropertyName.TIME_PARSER, SpreadsheetPattern.parseTimeParsePattern("hh:mm;hh:mm:ss;hh:mm:ss.000").spreadsheetParserSelector());
    }

    @Test
    public void testVisitTwoDigitYear() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitTwoDigitYear(final int i) {
                this.visited = i;
            }
        }.accept(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR, 32);
    }

    @Test
    public void testVisitValidationConverter() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitValidationConverter(final ConverterSelector s) {
                this.visited = s;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.VALIDATION_CONVERTER,
            ConverterSelector.parse("hello-converter")
        );
    }

    @Test
    public void testVisitValidationFunctions() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitValidationFunctions(final ExpressionFunctionAliasSet a) {
                this.visited = a;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.VALIDATION_FUNCTIONS,
            SpreadsheetExpressionFunctions.parseAliasSet("requiredFormFields")
        );
    }

    @Test
    public void testVisitValidationValidators() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitValidationValidators(final ValidatorAliasSet a) {
                this.visited = a;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.VALIDATION_VALIDATORS,
            ValidatorAliasSet.parse("first-validator, second-validator")
        );
    }

    @Test
    public void testVisitValidators() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitValidators(final ValidatorAliasSet a) {
                this.visited = a;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.VALIDATORS,
            ValidatorAliasSet.parse("nonNull")
        );
    }

    @Test
    public void testVisitValueSeparator() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitValueSeparator(final char c) {
                this.visited = c;
            }
        }.accept(SpreadsheetMetadataPropertyName.VALUE_SEPARATOR, '.');
    }

    @Test
    public void testVisitViewport() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitViewport(final SpreadsheetViewport selection) {
                this.visited = selection;
            }
        }.accept(
            SpreadsheetMetadataPropertyName.VIEWPORT,
            SpreadsheetSelection.parseCell("A2")
                .viewportRectangle(100, 50)
                .viewport()
        );
    }

    private static <T> SpreadsheetMetadata metadata(final SpreadsheetMetadataPropertyName<T> propertyName, final T value) {
        return SpreadsheetMetadata.EMPTY.set(propertyName, value);
    }

    @Override
    public SpreadsheetMetadataVisitor createVisitor() {
        return new TestSpreadsheetMetadataVisitor();
    }

    class TestSpreadsheetMetadataVisitor extends FakeSpreadsheetMetadataVisitor {

        <T> void accept(final SpreadsheetMetadataPropertyName<T> propertyName, final T value) {

            final SpreadsheetMetadata metadata = metadata(propertyName, value);
            this.accept(metadata);
            checkEquals(value, this.visited);

            new SpreadsheetMetadataVisitor() {
            }.accept(metadata);
        }

        Object visited;

        @Override
        protected Visiting startVisit(final SpreadsheetMetadata metadata) {
            return Visiting.CONTINUE;
        }

        @Override
        protected void endVisit(final SpreadsheetMetadata metadata) {
        }

        @Override
        protected Visiting startVisit(final SpreadsheetMetadataPropertyName<?> property, final Object value) {
            return Visiting.CONTINUE;
        }

        @Override
        protected void endVisit(final SpreadsheetMetadataPropertyName<?> property, final Object value) {
        }
    }

    private Color color() {
        return Color.parse("#123abc");
    }

    private LocalDateTime dateTime() {
        return LocalDateTime.of(2000, 1, 31, 12, 58, 59);
    }

    private EmailAddress emailAddress() {
        return EmailAddress.parse("user@example.com");
    }

    @Override
    public Class<SpreadsheetMetadataVisitor> type() {
        return SpreadsheetMetadataVisitor.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public String typeNamePrefix() {
        return "";
    }
}
