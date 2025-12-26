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
import walkingkooka.HasId;
import walkingkooka.collect.list.ImmutableList;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.color.Color;
import walkingkooka.convert.provider.ConverterAliasSet;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.environment.AuditInfo;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.locale.LocaleContext;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.naming.Name;
import walkingkooka.naming.ValueName;
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.net.UrlPathName;
import walkingkooka.plugin.PluginNameSet;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorAliasSet;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorNameList;
import walkingkooka.spreadsheet.engine.SpreadsheetCellQuery;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterAliasSet;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.pattern.HasSpreadsheetPatternKind;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternKind;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterAliasSet;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterAliasSet;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserAliasSet;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;
import walkingkooka.spreadsheet.store.SpreadsheetCellStoreAction;
import walkingkooka.spreadsheet.viewport.AnchoredSpreadsheetSelection;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionAliasSet;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.validation.form.provider.FormHandlerAliasSet;
import walkingkooka.validation.form.provider.FormHandlerSelector;
import walkingkooka.validation.provider.ValidatorAliasSet;

import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

/**
 * The {@link Name} of metadata property.
 */
public abstract class SpreadsheetMetadataPropertyName<T> implements Name,
    Comparable<SpreadsheetMetadataPropertyName<?>>,
    HasSpreadsheetPatternKind,
    HasUrlFragment,
    HasId<String>,
    ValueName<T> {

    // constants

    private static final CaseSensitivity CASE_SENSITIVITY = CaseSensitivity.SENSITIVE;

    /**
     * A read only cache of already prepared {@link SpreadsheetMetadataPropertyName names}..
     */
    static final Map<String, SpreadsheetMetadataPropertyName<?>> CONSTANTS = Maps.sorted(SpreadsheetMetadataPropertyName.CASE_SENSITIVITY.comparator());

    /**
     * A read only cache of already prepared {@link SpreadsheetMetadataPropertyName names}..
     */
    private static final Map<String, SpreadsheetMetadataPropertyName<?>> ENVIRONMENT_VALUE_NAME_CONSTANTS = Maps.sorted();

    /**
     * Registers a new {@link SpreadsheetMetadataPropertyName}.
     */
    private static <T> SpreadsheetMetadataPropertyName<T> registerConstant(final SpreadsheetMetadataPropertyName<T> constant) {
        SpreadsheetMetadataPropertyName.CONSTANTS.put(
            constant.name,
            constant
        );
        SpreadsheetMetadataPropertyName.ENVIRONMENT_VALUE_NAME_CONSTANTS.put(
            constant.name.toLowerCase(),
            constant
        );
        return constant;
    }

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>auditInfo</code>
     */
    public static final SpreadsheetMetadataPropertyName<AuditInfo> AUDIT_INFO = registerConstant(SpreadsheetMetadataPropertyNameAuditInfo.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>autoHideScrollbars</code>
     */
    public static final SpreadsheetMetadataPropertyName<Boolean> AUTO_HIDE_SCROLLBARS = registerConstant(SpreadsheetMetadataPropertyNameBooleanAutoHideScrollbars.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>cellCharacterWidth</code>
     */
    public static final SpreadsheetMetadataPropertyName<Integer> CELL_CHARACTER_WIDTH = registerConstant(SpreadsheetMetadataPropertyNameIntegerCellCharacterWidth.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>clipboardExporter</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetExporterAliasSet> CLIPBOARD_EXPORTER = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetExporterAliasSetClipboard.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>clipboardImporter</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetImporterAliasSet> CLIPBOARD_IMPORTER = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetImporterAliasSetClipboard.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>comparators</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetComparatorAliasSet> COMPARATORS = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetComparatorAliasSetComparators.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>converters</code>
     */
    public static final SpreadsheetMetadataPropertyName<ConverterAliasSet> CONVERTERS = registerConstant(SpreadsheetMetadataPropertyNameConverterAliasSetConverters.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>dateFormatter</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetFormatterSelector> DATE_FORMATTER = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorDate.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>dateParser</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetParserSelector> DATE_PARSER = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetParserSelectorDate.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>dateTimeFormatter</code>.
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetFormatterSelector> DATE_TIME_FORMATTER = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorDateTime.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>dateTimeOffset</code>
     */
    public static final SpreadsheetMetadataPropertyName<Long> DATE_TIME_OFFSET = registerConstant(SpreadsheetMetadataPropertyNameDateTimeOffset.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>dateTimeParser</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetParserSelector> DATE_TIME_PARSER = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetParserSelectorDateTime.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>decimalNumberSymbols</code>
     */
    public static final SpreadsheetMetadataPropertyName<DateTimeSymbols> DATE_TIME_SYMBOLS = registerConstant(SpreadsheetMetadataPropertyNameDateTimeSymbols.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>decimalNumberSymbols</code>
     */
    public static final SpreadsheetMetadataPropertyName<DecimalNumberSymbols> DECIMAL_NUMBER_SYMBOLS = registerConstant(SpreadsheetMetadataPropertyNameDecimalNumberSymbols.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>defaultFormHandler</code>..
     */
    public static final SpreadsheetMetadataPropertyName<FormHandlerSelector> DEFAULT_FORM_HANDLER = registerConstant(SpreadsheetMetadataPropertyNameFormHandlerSelectorDefault.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>defaultYear</code>
     */
    public static final SpreadsheetMetadataPropertyName<Integer> DEFAULT_YEAR = registerConstant(SpreadsheetMetadataPropertyNameIntegerDefaultYear.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>decimalNumberDigitCount</code>
     */
    public static final SpreadsheetMetadataPropertyName<Integer> DECIMAL_NUMBER_DIGIT_COUNT = registerConstant(SpreadsheetMetadataPropertyNameIntegerDecimalNumberDigitCount.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>errorFormatter</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetFormatterSelector> ERROR_FORMATTER = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorError.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>exporters</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetExporterAliasSet> EXPORTERS = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetExporterAliasSetExporters.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>expressionNumberKind</code>
     */
    public static final SpreadsheetMetadataPropertyName<ExpressionNumberKind> EXPRESSION_NUMBER_KIND = registerConstant(SpreadsheetMetadataPropertyNameExpressionNumberKind.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>findConverter</code>.
     */
    public static final SpreadsheetMetadataPropertyName<ConverterSelector> FIND_CONVERTER = registerConstant(SpreadsheetMetadataPropertyNameConverterSelectorFind.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>findFunctions</code>.
     */
    public static final SpreadsheetMetadataPropertyName<ExpressionFunctionAliasSet> FIND_FUNCTIONS = registerConstant(SpreadsheetMetadataPropertyNameExpressionFunctionAliasSetFind.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>findHighlighting</code>
     */
    public static final SpreadsheetMetadataPropertyName<Boolean> FIND_HIGHLIGHTING = registerConstant(SpreadsheetMetadataPropertyNameBooleanFindHighlighting.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>findQuery</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetCellQuery> FIND_QUERY = registerConstant(SpreadsheetMetadataPropertyNameFindQuery.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>formatters</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetFormatterAliasSet> FORMATTERS = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetFormatterAliasSetFormatters.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>formattingConverter</code>.
     */
    public static final SpreadsheetMetadataPropertyName<ConverterSelector> FORMATTING_CONVERTER = registerConstant(SpreadsheetMetadataPropertyNameConverterSelectorFormatting.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>formattingFunctions</code>.
     * an expression while formatting.
     */
    public static final SpreadsheetMetadataPropertyName<ExpressionFunctionAliasSet> FORMATTING_FUNCTIONS = registerConstant(SpreadsheetMetadataPropertyNameExpressionFunctionAliasSetFormatting.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>formHandlers</code>
     */
    public static final SpreadsheetMetadataPropertyName<FormHandlerAliasSet> FORM_HANDLERS = registerConstant(SpreadsheetMetadataPropertyNameFormHandlerAliasSetFormHandlers.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>formulaConverter</code>.
     */
    public static final SpreadsheetMetadataPropertyName<ConverterSelector> FORMULA_CONVERTER = registerConstant(SpreadsheetMetadataPropertyNameConverterSelectorFormula.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>formulaFunctions</code>.
     */
    public static final SpreadsheetMetadataPropertyName<ExpressionFunctionAliasSet> FORMULA_FUNCTIONS = registerConstant(SpreadsheetMetadataPropertyNameExpressionFunctionAliasSetFormula.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>frozenColumns</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetColumnRangeReference> FROZEN_COLUMNS = registerConstant(SpreadsheetMetadataPropertyNameFrozenColumns.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>frozenRows</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetRowRangeReference> FROZEN_ROWS = registerConstant(SpreadsheetMetadataPropertyNameFrozenRows.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>functions</code>
     */
    public static final SpreadsheetMetadataPropertyName<ExpressionFunctionAliasSet> FUNCTIONS = registerConstant(SpreadsheetMetadataPropertyNameExpressionFunctionAliasSetFunctions.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>hideZeroValues</code>
     */
    public static final SpreadsheetMetadataPropertyName<Boolean> HIDE_ZERO_VALUES = registerConstant(SpreadsheetMetadataPropertyNameBooleanHideZeroValues.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>importers</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetImporterAliasSet> IMPORTERS = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetImporterAliasSetImporters.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>locale</code>
     */
    public static final SpreadsheetMetadataPropertyName<Locale> LOCALE = registerConstant(SpreadsheetMetadataPropertyNameLocale.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>numberFormatter</code>.
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetFormatterSelector> NUMBER_FORMATTER = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorNumber.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>numberParser</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetParserSelector> NUMBER_PARSER = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetParserSelectorNumber.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>parsers</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetParserAliasSet> PARSERS = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetParserAliasSetParsers.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>plugins</code>
     */
    public static final SpreadsheetMetadataPropertyName<PluginNameSet> PLUGINS = registerConstant(SpreadsheetMetadataPropertyNamePluginNameSet.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>precision</code>
     */
    public static final SpreadsheetMetadataPropertyName<Integer> PRECISION = registerConstant(SpreadsheetMetadataPropertyNameIntegerPrecision.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>roundingMode</code>
     */
    public static final SpreadsheetMetadataPropertyName<RoundingMode> ROUNDING_MODE = registerConstant(SpreadsheetMetadataPropertyNameRoundingMode.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>scriptingConverter</code>.
     */
    public static final SpreadsheetMetadataPropertyName<ConverterSelector> SCRIPTING_CONVERTER = registerConstant(SpreadsheetMetadataPropertyNameConverterSelectorScripting.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>scriptingFunctions</code>.
     */
    public static final SpreadsheetMetadataPropertyName<ExpressionFunctionAliasSet> SCRIPTING_FUNCTIONS = registerConstant(SpreadsheetMetadataPropertyNameExpressionFunctionAliasSetScripting.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>showFormulaEditor</code>
     */
    public static final SpreadsheetMetadataPropertyName<Boolean> SHOW_FORMULA_EDITOR = registerConstant(SpreadsheetMetadataPropertyNameBooleanShowFormulaEditor.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>showFormulas</code>
     */
    public static final SpreadsheetMetadataPropertyName<Boolean> SHOW_FORMULAS = registerConstant(SpreadsheetMetadataPropertyNameBooleanShowFormulas.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>showGridLines</code>
     */
    public static final SpreadsheetMetadataPropertyName<Boolean> SHOW_GRID_LINES = registerConstant(SpreadsheetMetadataPropertyNameBooleanShowGridLines.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>showHeadings</code>
     */
    public static final SpreadsheetMetadataPropertyName<Boolean> SHOW_HEADINGS = registerConstant(SpreadsheetMetadataPropertyNameBooleanShowHeadings.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the current <code>sortComparators</code>.
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetComparatorNameList> SORT_COMPARATORS = registerConstant(SpreadsheetMetadataPropertyNameSortComparators.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>{@link ConverterSelector}</code>.
     */
    public static final SpreadsheetMetadataPropertyName<ConverterSelector> SORT_CONVERTER = registerConstant(SpreadsheetMetadataPropertyNameConverterSelectorSort.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>spreadsheetId</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetId> SPREADSHEET_ID = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetId.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>spreadsheetName</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetName> SPREADSHEET_NAME = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetName.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>style</code>
     */
    public static final SpreadsheetMetadataPropertyName<TextStyle> STYLE = registerConstant(SpreadsheetMetadataPropertyNameStyle.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>textFormatter</code>.
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetFormatterSelector> TEXT_FORMATTER = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorText.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>timeFormatter</code>.
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetFormatterSelector> TIME_FORMATTER = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorTime.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>timeParser</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetParserSelector> TIME_PARSER = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetParserSelectorTime.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>twoDigitYear</code>
     */
    public static final SpreadsheetMetadataPropertyName<Integer> TWO_DIGIT_YEAR = registerConstant(SpreadsheetMetadataPropertyNameIntegerTwoDigitYear.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>validationConverter</code>.
     */
    public static final SpreadsheetMetadataPropertyName<ConverterSelector> VALIDATION_CONVERTER = registerConstant(SpreadsheetMetadataPropertyNameConverterSelectorValidation.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>validationFunctions</code>.
     */
    public static final SpreadsheetMetadataPropertyName<ExpressionFunctionAliasSet> VALIDATION_FUNCTIONS = registerConstant(SpreadsheetMetadataPropertyNameExpressionFunctionAliasSetValidation.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>validationValidators</code>.
     */
    public static final SpreadsheetMetadataPropertyName<ValidatorAliasSet> VALIDATION_VALIDATORS = registerConstant(SpreadsheetMetadataPropertyNameValidatorAliasSetValidationValidators.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>validators</code>.
     */
    public static final SpreadsheetMetadataPropertyName<ValidatorAliasSet> VALIDATORS = registerConstant(SpreadsheetMetadataPropertyNameValidatorAliasSetValidators.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>valueSeparator</code>.
     */
    public static final SpreadsheetMetadataPropertyName<Character> VALUE_SEPARATOR = registerConstant(SpreadsheetMetadataPropertyNameCharacterValueSeparator.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the current <code>viewportHome</code>.
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetCellReference> VIEWPORT_HOME = registerConstant(SpreadsheetMetadataPropertyNameViewportHome.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the current <code>viewportSelection</code>.
     */
    public static final SpreadsheetMetadataPropertyName<AnchoredSpreadsheetSelection> VIEWPORT_SELECTION = registerConstant(SpreadsheetMetadataPropertyNameViewportSelection.instance());

    /**
     * A read only view of all names, except for the {@link SpreadsheetMetadataPropertyName#namedColor(SpreadsheetColorName)} and {@link SpreadsheetMetadataPropertyName#numberedColor(int)}.
     */
    public final static Set<SpreadsheetMetadataPropertyName<?>> ALL = Sets.readOnly(
        new TreeSet<>(
            CONSTANTS.values()
        )
    );

    /**
     * Getter that returns all {@link SpreadsheetMetadataPropertyName} that return {@link SpreadsheetFormatterSelector}.
     */
    public static List<SpreadsheetMetadataPropertyName<SpreadsheetFormatterSelector>> formatters() {
        return CONSTANTS.values()
            .stream()
            .filter(SpreadsheetMetadataPropertyName::isSpreadsheetFormatterSelector)
            .map(p -> (SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelector)p)
            .collect(ImmutableList.collector());
    }

    /**
     * Getter that returns all {@link SpreadsheetMetadataPropertyName} that return {@link SpreadsheetParserSelector}.
     */
    public static List<SpreadsheetMetadataPropertyName<SpreadsheetParserSelector>> parsers() {
        return CONSTANTS.values()
            .stream()
            .filter(SpreadsheetMetadataPropertyName::isSpreadsheetParserSelector)
            .map(p -> (SpreadsheetMetadataPropertyNameSpreadsheetParserSelector)p)
            .collect(ImmutableList.collector());
    }

    /**
     * Tries to locate a {@link SpreadsheetMetadataPropertyName} with the given name returning empty if unknown.
     */
    public static Optional<SpreadsheetMetadataPropertyName<?>> tryWith(final String name) {
        CharSequences.failIfNullOrEmpty(name, "name");

        SpreadsheetMetadataPropertyName<?> propertyName = CONSTANTS.get(name);
        if (null == propertyName) {
            if (name.startsWith(COLOR_PREFIX) || name.length() == COLOR_PREFIX.length()) {
                final String after = name.substring(COLOR_PREFIX.length());

                // name dash color is a numbered color, named dash letter is a named color
                try {
                    if (Character.isLetter(after.charAt(0))) {
                        propertyName = namedColor(SpreadsheetColorName.with(after));
                    } else {
                        propertyName = numberedColor(Integer.parseInt(after));
                    }
                } catch (final RuntimeException cause) {
                    propertyName = null;
                }
            }
        }
        return Optional.ofNullable(propertyName);
    }

    /**
     * Factory that assumes a valid {@link SpreadsheetMetadataPropertyName} or fails.
     */
    public static SpreadsheetMetadataPropertyName<?> with(final String name) {
        CharSequences.failIfNullOrEmpty(name, "name");

        SpreadsheetMetadataPropertyName<?> propertyName = CONSTANTS.get(name);
        if (null == propertyName) {
            if (false == name.startsWith(COLOR_PREFIX) || name.length() == COLOR_PREFIX.length()) {
                throw new IllegalArgumentException("Unknown metadata property name " + CharSequences.quoteAndEscape(name));
            }

            final String after = name.substring(COLOR_PREFIX.length());

            // name dash color is a numbered color, named dash letter is a named color
            try {
                if (Character.isLetter(after.charAt(0))) {
                    propertyName = namedColor(SpreadsheetColorName.with(after));
                } else {
                    propertyName = numberedColor(Integer.parseInt(after));
                }
            } catch (final RuntimeException cause) {
                throw new IllegalArgumentException("Invalid metadata property name " + CharSequences.quoteAndEscape(name), cause);
            }
        }
        return propertyName;
    }

    static final String COLOR_PREFIX = "color";

    /**
     * Retrieves a {@link SpreadsheetMetadataPropertyName} for a {@link SpreadsheetColorName named}.
     */
    public static SpreadsheetMetadataPropertyName<Integer> namedColor(final SpreadsheetColorName name) {
        return SpreadsheetMetadataPropertyNameIntegerNamedColor.withColorName(name);
    }

    /**
     * Retrieves a {@link SpreadsheetMetadataPropertyName} for a numbered {@link Color}.
     */
    public static SpreadsheetMetadataPropertyName<Color> numberedColor(final int number) {
        return SpreadsheetMetadataPropertyNameNumberedColor.withNumber(number);
    }

    /**
     * Calls to this constructor will compute the {@link #name} parse the {@link Class#getSimpleName}
     */
    SpreadsheetMetadataPropertyName() {
        this(null);
    }

    /**
     * Package private constructor use factory.
     */
    SpreadsheetMetadataPropertyName(final String name) {
        super();

        String finalName = name;
        if (null == name) {
            finalName = this.getClass()
                .getSimpleName()
                .substring(SpreadsheetMetadataPropertyName.class.getSimpleName().length())
                .replace("Spreadsheet", "");
            finalName = finalName.substring(0, 1)
                .toLowerCase() +
                finalName.substring(1);
        }
        this.name = finalName;

        this.jsonPropertyName = JsonPropertyName.with(finalName);

        this.patchRemove = JsonNode.object()
            .setNull(
                this.jsonPropertyName
            );

        this.urlFragment = UrlFragment.parse(finalName);
        this.urlPathName = UrlPathName.with(finalName);
    }

    @Override
    public final String value() {
        return this.name;
    }

    final String name;

    final JsonPropertyName jsonPropertyName;

    /**
     * Validates the value, returning the value that will be saved.
     */
    @SuppressWarnings("UnusedReturnValue")
    public final T checkValue(final Object value) {
        if (null == value) {
            throw new SpreadsheetMetadataPropertyValueException("Missing value", this, value);
        }

        return this.checkValueNonNull(value);
    }

    abstract T checkValueNonNull(final Object value);

    /**
     * Checks the type of the given value and throws a {@link SpreadsheetMetadataPropertyValueException} if this test fails.
     */
    final T checkValueType(final Object value,
                           final Predicate<Object> typeChecker) {
        if (!typeChecker.test(value)) {
            throw this.spreadsheetMetadataPropertyValueException(value);
        }
        return Cast.to(value);
    }

    /**
     * Creates a {@link SpreadsheetMetadataPropertyValueException} used to report an invalid value.
     */
    final SpreadsheetMetadataPropertyValueException spreadsheetMetadataPropertyValueException(final Object value) {
        // Metadata hide-zero-values=123, Expected XYZ
        return new SpreadsheetMetadataPropertyValueException(
            "Expected " +
                this.expected(),
            this,
            value
        );
    }

    /**
     * Returns the value type of this property.
     */
    @Override
    public abstract Class<T> type();

    /**
     * Provides the specific text about an invalid value for {@link #spreadsheetMetadataPropertyValueException(Object)}.
     */
    abstract String expected();

    /**
     * Defaults must not include a {@link SpreadsheetId}, {@link AuditInfo}
     */
    final boolean isNotDefaultProperty() {
        return this instanceof SpreadsheetMetadataPropertyNameAuditInfo ||
            this instanceof SpreadsheetMetadataPropertyNameSpreadsheetId;
    }

    // loadFromLocale...................................................................................................

    /**
     * Some properties support providing a value for the given Locale for the parent {@link SpreadsheetMetadata} to be updated.
     */
    abstract Optional<T> extractLocaleAwareValue(final LocaleContext context);

    // SpreadsheetMetadataVisitor.......................................................................................

    /**
     * Dispatches to the appropriate {@link SpreadsheetMetadataVisitor} visit method.
     */
    abstract void accept(final T value, final SpreadsheetMetadataVisitor visitor);

    // HasUrlFragment...................................................................................................

    @Override
    public final UrlFragment urlFragment() {
        return this.urlFragment;
    }

    private final UrlFragment urlFragment;

    // parseUrlFragmentSaveValue........................................................................................

    /**
     * This parse method is called with the encoded text from a {@link UrlFragment} representing a save operation of this
     * property. Not all properties support this operation, and will throw a {@link UnsupportedOperationException}.
     */
    public final T parseUrlFragmentSaveValue(final String value) {
        Objects.requireNonNull(value, value);

        return this.checkValue(
            this.parseUrlFragmentSaveValueNonNull(value)
        );
    }

    abstract T parseUrlFragmentSaveValueNonNull(final String value);

    /**
     * This common method should be called by subclasses to indicate {@link #parseUrlFragmentSaveValue(String)} is not supported.
     */
    final T failParseUrlFragmentSaveValueUnsupported() {
        throw new UnsupportedOperationException("UrlFragment save value not supported for " + CharSequences.quoteAndEscape(this.value()));
    }

    // SpreadsheetCellStore.............................................................................................

    /**
     * Returns the appropriate {@link SpreadsheetCellStoreAction} for changes to this {@link SpreadsheetMetadataPropertyName}.
     */
    public final SpreadsheetCellStoreAction spreadsheetCellStoreAction() {
        final SpreadsheetCellStoreAction action;

        switch (this.value()) {
            // id
            case "spreadsheetId":
            case "spreadsheetName":
                action = SpreadsheetCellStoreAction.NONE;
                break;

            // authorship & timestamp
            case "auditInfo":
                action = SpreadsheetCellStoreAction.NONE;
                break;
            // viewport
            case "frozenColumns":
            case "frozenRows":
            case "selection":
            case "viewportCell":
                action = SpreadsheetCellStoreAction.NONE;
                break;
            // number parsing characters.
            case "decimalNumberSymbols":
            case "valueSeparator":
                action = SpreadsheetCellStoreAction.PARSE_FORMULA;
                break;
            // parse-patterns
            case "dateParser":
            case "dateTimeParser":
            case "numberParser":
            case "timeParser":
                action = SpreadsheetCellStoreAction.PARSE_FORMULA;
                break;
            default:
                // all other properties require a full evaluate and format of all cells.
                action = SpreadsheetCellStoreAction.EVALUATE_AND_FORMAT;
                break;
        }

        return action;
    }

    // HasSpreadsheetPatternKind........................................................................................

    /**
     * The corresponding {@link SpreadsheetPatternKind} for this property. Only <code>formatter</code> and
     * <code>parser</code> properties will return a {@link SpreadsheetPatternKind}.
     */
    // time -> TIME_PARSER
    @Override
    public final Optional<SpreadsheetPatternKind> patternKind() {
        SpreadsheetPatternKind kind;
        if (this instanceof SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelector) {
            final SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelector formatter = (SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelector) this;
            kind = formatter.spreadsheetPatternKind;

        } else {
            if (this instanceof SpreadsheetMetadataPropertyNameSpreadsheetParserSelector) {
                final SpreadsheetMetadataPropertyNameSpreadsheetParserSelector parser = (SpreadsheetMetadataPropertyNameSpreadsheetParserSelector) this;
                kind = parser.spreadsheetPatternKind;
            } else {
                kind = null;
            }
        }

        return Optional.ofNullable(kind);
    }

    // SpreadsheetMetadataPropertyNameConverterSelector.................................................................

    /**
     * Returns the {@link SpreadsheetMetadataPropertyName} that matches this {@link SpreadsheetMetadataPropertyNameExpressionFunctionAliasSet}.
     */
    public SpreadsheetMetadataPropertyName<ConverterSelector> toConverterSelector() {
        SpreadsheetMetadataPropertyName<ConverterSelector> converterSelector;

        if (this instanceof SpreadsheetMetadataPropertyNameConverterSelector) {
            converterSelector = Cast.to(this);
        } else {

            final Class<?> type = this.type();
            if (ExpressionFunctionAliasSet.class != type) {
                // Property dateTimeSymbols: invalid type DateTimeSymbols expected ExpressionFunctionAliasSet
                throw new IllegalStateException("Property " + this + ": invalid type " + type.getSimpleName() + " expected " + ExpressionFunctionAliasSet.class.getSimpleName());
            }

            // findFunctions -> findConverter
            final String propertyName = this.name.replace(
                "Functions",
                "Converter"
            );

            converterSelector = Cast.to(
                CONSTANTS.get(propertyName)
            );

            if (null == converterSelector) {
                throw new IllegalArgumentException("Missing " + propertyName + " with type " + ConverterAliasSet.class.getSimpleName());
            }
        }

        return converterSelector;
    }

    /**
     * Returns the matching {@link SpreadsheetMetadataPropertyName} given an {@link EnvironmentValueName}.
     */
    public static <T> SpreadsheetMetadataPropertyName<T> fromEnvironmentValueName(final EnvironmentValueName<T> name) {
        Objects.requireNonNull(name, "name");

        final String nameString = name.value();
        final SpreadsheetMetadataPropertyName<?> spreadsheetMetadataPropertyName = ENVIRONMENT_VALUE_NAME_CONSTANTS.get(nameString.toLowerCase());
        if(null == spreadsheetMetadataPropertyName) {
            throw new IllegalArgumentException("Unknown metadata property name " + CharSequences.quoteAndEscape(nameString));
        }

        return Cast.to(spreadsheetMetadataPropertyName);
    }

    // toEnvironmentValueName...........................................................................................

    /**
     * Getter that returns this {@link SpreadsheetMetadataPropertyName} as a {@link EnvironmentValueName}.
     */
    public EnvironmentValueName<T> toEnvironmentValueName() {
        if (null == this.environmentValueName) {
            this.environmentValueName = EnvironmentValueName.with(
                this.name,
                this.type()
            );
        }
        return this.environmentValueName;
    }

    private EnvironmentValueName<T> environmentValueName;

    // isXXX............................................................................................................

    /**
     * Returns true if this property contains a {@link ConverterSelector}.
     */
    public final boolean isConverterSelector() {
        return this instanceof SpreadsheetMetadataPropertyNameConverterSelector;
    }

    /**
     * Returns true if this property contains a {@link SpreadsheetFormatterSelector}.
     */
    public final boolean isSpreadsheetFormatterSelector() {
        return this instanceof SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelector;
    }

    /**
     * Returns true if this property contains a {@link SpreadsheetParserSelector}.
     */
    public final boolean isSpreadsheetParserSelector() {
        return this instanceof SpreadsheetMetadataPropertyNameSpreadsheetParserSelector;
    }

    // toUrlPathName....................................................................................................

    public UrlPathName toUrlPathName() {
        return this.urlPathName;
    }

    private final UrlPathName urlPathName;

    // Object...........................................................................................................

    @Override
    public final int hashCode() {
        return this.caseSensitivity().hash(this.name);
    }

    @Override
    public final boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetMetadataPropertyName &&
                this.equals0((SpreadsheetMetadataPropertyName<?>) other);
    }

    private boolean equals0(final SpreadsheetMetadataPropertyName<?> other) {
        return this.caseSensitivity().equals(this.name, other.name);
    }

    @Override
    public final String toString() {
        return this.value();
    }

    // HasCaseSensitivity...............................................................................................

    /**
     * Used during hashing and equality checks.
     */
    @Override
    public final CaseSensitivity caseSensitivity() {
        return CASE_SENSITIVITY;
    }

    // Comparable.......................................................................................................

    @Override
    public final int compareTo(final SpreadsheetMetadataPropertyName<?> other) {
        return this.caseSensitivity()
            .comparator()
            .compare(
                this.compareToValue(),
                other.compareToValue()
            );
    }

    private String compareToValue() {
        String value = this.value();

        if (this instanceof SpreadsheetMetadataPropertyNameSpreadsheetId) {
            value = ""; // make ids sort first
        } else {
            if (this instanceof SpreadsheetMetadataPropertyNameNumberedColor) {
                final SpreadsheetMetadataPropertyNameNumberedColor numberedColor = (SpreadsheetMetadataPropertyNameNumberedColor) this;
                value = numberedColor.compareToValue;
            }
        }

        return value;
    }

    // JsonNode.........................................................................................................

    /**
     * Creates a {@link JsonNode} which may be used to patch a {@link SpreadsheetMetadata}.
     */
    public final JsonNode patch(final T value) {
        return null == value ?
            this.patchRemove :
            SpreadsheetMetadata.EMPTY.set(this, value)
                .marshall(JsonNodeMarshallContexts.basic());
    }

    /**
     * Cached {@link JsonNode}
     */
    private final JsonNode patchRemove;

    /**
     * Factory that retrieves a {@link SpreadsheetMetadataPropertyName} parse a {@link JsonNode#name()}.
     */
    static SpreadsheetMetadataPropertyName<?> unmarshallName(final JsonNode node) {
        return with(node.name().value());
    }

    // HasId............................................................................................................

    @Override
    public final String id() {
        return this.value();
    }
}
