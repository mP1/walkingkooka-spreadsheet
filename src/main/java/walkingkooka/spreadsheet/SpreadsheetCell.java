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

package walkingkooka.spreadsheet;

import walkingkooka.CanBeEmpty;
import walkingkooka.Cast;
import walkingkooka.ToStringBuilder;
import walkingkooka.ToStringBuilderOption;
import walkingkooka.UsesToStringBuilder;
import walkingkooka.collect.list.CsvStringList;
import walkingkooka.collect.list.Lists;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.datetime.HasOptionalDateTimeSymbols;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.math.HasOptionalDecimalNumberSymbols;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.format.HasOptionalSpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.CanReplaceReferences;
import walkingkooka.spreadsheet.reference.HasSpreadsheetReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.HasText;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.Printer;
import walkingkooka.text.printer.Printers;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallException;
import walkingkooka.tree.json.patch.Patchable;
import walkingkooka.tree.text.HasTextNode;
import walkingkooka.tree.text.HasTextStyle;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.util.HasOptionalLocale;
import walkingkooka.validation.ValidationValueTypeName;
import walkingkooka.validation.provider.HasOptionalValidatorSelector;
import walkingkooka.validation.provider.ValidatorSelector;

import java.math.MathContext;
import java.text.DateFormatSymbols;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A spreadsheet cell including its formula, and other attributes such as format, text properties(styling) and more.
 */
public final class SpreadsheetCell implements CanBeEmpty,
    CanReplaceReferences<SpreadsheetCell>,
    HasSpreadsheetReference<SpreadsheetCellReference>,
    HateosResource<SpreadsheetCellReference>,
    Patchable<SpreadsheetCell>,
    HasOptionalDateTimeSymbols,
    HasOptionalDecimalNumberSymbols,
    HasOptionalLocale,
    HasOptionalSpreadsheetFormatterSelector,
    HasOptionalValidatorSelector,
    HasTextNode,
    HasTextStyle,
    HasText,
    TreePrintable,
    UsesToStringBuilder {

    /**
     * A {@link Comparator} that only uses the {@link SpreadsheetCell#REFERENCE_COMPARATOR}.
     */
    public static final Comparator<SpreadsheetCell> REFERENCE_COMPARATOR = Comparator.comparing(SpreadsheetCell::reference);

    /**
     * Holds an absent {@link DateTimeSymbols}
     */
    public final static Optional<DateTimeSymbols> NO_DATETIME_SYMBOLS = Optional.empty();

    /**
     * Holds an absent {@link DateTimeSymbols}
     */
    public final static Optional<DecimalNumberSymbols> NO_DECIMAL_NUMBER_SYMBOLS = Optional.empty();

    /**
     * Holds an absent {@link SpreadsheetFormatterSelector}.
     */
    public final static Optional<SpreadsheetFormatterSelector> NO_FORMATTER = Optional.empty();

    /**
     * Holds an absent {@link TextNode}.
     */
    public final static Optional<TextNode> NO_FORMATTED_VALUE_CELL = Optional.empty();

    /**
     * Holds an absent {@link SpreadsheetParserSelector}.
     */
    public final static Optional<SpreadsheetParserSelector> NO_PARSER = Optional.empty();

    /**
     * No {@link Locale}.
     */
    public final static Optional<Locale> NO_LOCALE = Optional.empty();

    /**
     * An empty {@link TextStyle}.
     */
    public final static TextStyle NO_STYLE = TextStyle.EMPTY;

    /**
     * No validator has been defined for this cell.
     */
    public final static Optional<ValidatorSelector> NO_VALIDATOR = Optional.empty();

    /**
     * Factory that creates a new {@link SpreadsheetCell}
     */
    public static SpreadsheetCell with(final SpreadsheetCellReference reference,
                                       final SpreadsheetFormula formula) {
        return new SpreadsheetCell(
            checkReference(reference),
            checkFormula(formula),
            NO_DATETIME_SYMBOLS,
            NO_DECIMAL_NUMBER_SYMBOLS,
            NO_LOCALE,
            NO_FORMATTER,
            NO_PARSER,
            NO_STYLE,
            NO_FORMATTED_VALUE_CELL,
            NO_VALIDATOR
        );
    }

    /**
     * Private ctor
     */
    private SpreadsheetCell(final SpreadsheetCellReference reference,
                            final SpreadsheetFormula formula,
                            final Optional<DateTimeSymbols> dateTimeSymbols,
                            final Optional<DecimalNumberSymbols> decimalNumberSymbols,
                            final Optional<Locale> locale,
                            final Optional<SpreadsheetFormatterSelector> formatter,
                            final Optional<SpreadsheetParserSelector> parser,
                            final TextStyle style,
                            final Optional<TextNode> formattedValue,
                            final Optional<ValidatorSelector> validator) {
        super();

        this.reference = reference;
        this.formula = formula;
        this.dateTimeSymbols = dateTimeSymbols;
        this.decimalNumberSymbols = decimalNumberSymbols;
        this.locale = locale;
        this.formatter = formatter;
        this.parser = parser;
        this.style = style;
        this.formattedValue = formattedValue;
        this.validator = validator;
    }

    // HasId ...........................................................................................................

    @Override
    public Optional<SpreadsheetCellReference> id() {
        return Optional.of(this.reference());
    }

    @Override
    public String hateosLinkId() {
        return this.reference().hateosLinkId();
    }

    // HasSpreadsheetReference..........................................................................................

    @Override
    public SpreadsheetCellReference reference() {
        return this.reference;
    }

    public SpreadsheetCell setReference(final SpreadsheetCellReference reference) {
        return this.reference.equals(reference) ?
            this :
            this.replace(
                checkReference(reference),
                this.formula,
                this.dateTimeSymbols,
                this.decimalNumberSymbols,
                this.locale,
                this.formatter,
                this.parser,
                this.style,
                this.validator
            );
    }

    private static SpreadsheetCellReference checkReference(final SpreadsheetCellReference reference) {
        return Objects.requireNonNull(reference, "reference")
            .toRelative();
    }

    /**
     * The reference that identifies this cell.
     */
    private final SpreadsheetCellReference reference;

    // formula .........................................................................................................

    public SpreadsheetFormula formula() {
        return this.formula;
    }

    public SpreadsheetCell setFormula(final SpreadsheetFormula formula) {
        return this.setFormula0(
            checkFormula(formula)
        );
    }

    private SpreadsheetCell setFormula0(final SpreadsheetFormula formula) {
        return this.formula.equals(formula) ?
            this :
            this.replace(
                this.reference,
                formula,
                this.dateTimeSymbols,
                this.decimalNumberSymbols,
                this.locale,
                this.formatter,
                this.parser,
                this.style,
                this.validator
            );
    }

    /**
     * The formula that appears in this cell.
     */
    private final SpreadsheetFormula formula;

    /**
     * If a formula has a Collection value, return the {@link SpreadsheetFormula#value()} with {@link SpreadsheetErrorKind#VALUE}.
     * A cell range resolves to a {@link List}, thus a cell = a range, will show an #VALUE!.
     */
    private static SpreadsheetFormula checkFormula(final SpreadsheetFormula formula) {
        Objects.requireNonNull(formula, "formula");

        // if value is a List formula should have an ERROR of #VALUE!
        return formula.setValue(
            formula.value()
                .map(v -> v instanceof Collection ? SpreadsheetErrorKind.VALUE : v)
        );
        // TODO https://github.com/mP1/walkingkooka-spreadsheet/issues/2205
    }

    // dateTimeSymbols..................................................................................................

    @Override
    public Optional<DateTimeSymbols> dateTimeSymbols() {
        return this.dateTimeSymbols;
    }

    /**
     * Returns a {@link SpreadsheetCell} with the given {@link DateTimeSymbols}. If the formula has a token or
     * expression they will be cleared.
     */
    public SpreadsheetCell setDateTimeSymbols(final Optional<DateTimeSymbols> dateTimeSymbols) {
        return this.dateTimeSymbols.equals(dateTimeSymbols) ?
            this :
            this.replaceDateTimeSymbols(
                Objects.requireNonNull(dateTimeSymbols, "dateTimeSymbols")
            );
    }

    private SpreadsheetCell replaceDateTimeSymbols(final Optional<DateTimeSymbols> dateTimeSymbols) {
        return this.replace(
            this.reference,
            this.formula,
            dateTimeSymbols,
            this.decimalNumberSymbols,
            this.locale,
            this.formatter,
            this.parser,
            this.style,
            this.validator
        );
    }

    /**
     * An optional {@link DateTimeSymbols} which will override the default {@link DateTimeSymbols}.
     */
    private final Optional<DateTimeSymbols> dateTimeSymbols;

    // decimalNumberSymbols..................................................................................................

    @Override
    public Optional<DecimalNumberSymbols> decimalNumberSymbols() {
        return this.decimalNumberSymbols;
    }

    /**
     * Returns a {@link SpreadsheetCell} with the given {@link DecimalNumberSymbols}. If the formula has a token or
     * expression they will be cleared.
     */
    public SpreadsheetCell setDecimalNumberSymbols(final Optional<DecimalNumberSymbols> decimalNumberSymbols) {
        return this.decimalNumberSymbols.equals(decimalNumberSymbols) ?
            this :
            this.replaceDecimalNumberSymbols(
                Objects.requireNonNull(decimalNumberSymbols, "decimalNumberSymbols")
            );
    }

    private SpreadsheetCell replaceDecimalNumberSymbols(final Optional<DecimalNumberSymbols> decimalNumberSymbols) {
        return this.replace(
            this.reference,
            this.formula,
            this.dateTimeSymbols,
            decimalNumberSymbols,
            this.locale,
            this.formatter,
            this.parser,
            this.style,
            this.validator
        );
    }

    /**
     * An optional {@link DecimalNumberSymbols} which will override the default {@link DecimalNumberSymbols}.
     */
    private final Optional<DecimalNumberSymbols> decimalNumberSymbols;

    // locale...........................................................................................................

    @Override
    public Optional<Locale> locale() {
        return this.locale;
    }

    public SpreadsheetCell setLocale(final Optional<Locale> locale) {
        return this.locale.equals(locale) ?
            this :
            this.replace(
                this.reference,
                this.formula,
                this.dateTimeSymbols,
                this.decimalNumberSymbols,
                Objects.requireNonNull(locale, "locale"),
                this.formatter,
                this.parser,
                this.style,
                this.validator
            );
    }

    private final Optional<Locale> locale;

    // formatter........................................................................................................

    public Optional<SpreadsheetFormatterSelector> formatter() {
        return this.formatter;
    }

    public SpreadsheetCell setFormatter(final Optional<SpreadsheetFormatterSelector> formatter) {
        return this.formatter.equals(formatter) ?
            this :
            this.replace(
                this.reference,
                this.formula,
                this.dateTimeSymbols,
                this.decimalNumberSymbols,
                this.locale,
                Objects.requireNonNull(formatter, "formatter"),
                this.parser,
                this.style,
                this.validator
            );
    }

    /**
     * Used to format the output of the cell's formula.
     */
    private final Optional<SpreadsheetFormatterSelector> formatter;

    // HasOptionalSpreadsheetFormatterSelector..........................................................................

    @Override
    public Optional<SpreadsheetFormatterSelector> formatterSelector() {
        return this.formatter();
    }

    // parser...........................................................................................................

    public Optional<SpreadsheetParserSelector> parser() {
        return this.parser;
    }

    /**
     * Returns a {@link SpreadsheetCell} with the given {@link SpreadsheetParserSelector}. If the formula has a token or
     * expression they will be cleared.
     */
    public SpreadsheetCell setParser(final Optional<SpreadsheetParserSelector> parser) {
        return this.parser.equals(parser) ?
            this :
            this.replaceParser(
                Objects.requireNonNull(parser, "parser")
            );
    }

    private SpreadsheetCell replaceParser(final Optional<SpreadsheetParserSelector> parser) {
        final SpreadsheetFormula formula = this.formula;

        return this.replace(
            this.reference,
            formula.setToken(SpreadsheetFormula.NO_TOKEN)
                .setText(formula.text()),
            this.dateTimeSymbols,
            this.decimalNumberSymbols,
            this.locale,
            this.formatter,
            parser,
            this.style,
            this.validator
        );
    }

    /**
     * A {@link SpreadsheetParserSelector} which will be used to parse and validate a formula
     */
    private final Optional<SpreadsheetParserSelector> parser;

    // style ...........................................................................................................

    public TextStyle style() {
        return this.style;
    }

    public SpreadsheetCell setStyle(final TextStyle style) {
        return this.style.equals(style) ?
            this :
            this.replace(
                this.reference,
                this.formula,
                this.dateTimeSymbols,
                this.decimalNumberSymbols,
                this.locale,
                this.formatter,
                this.parser,
                Objects.requireNonNull(style, "style"),
                this.validator
            );
    }

    /**
     * The cell style that is used to format the output of the formula.
     */
    private final TextStyle style;

    // HasTextStyle.....................................................................................................

    @Override
    public TextStyle textStyle() {
        return this.style;
    }

    // formatted .......................................................................................................

    public Optional<TextNode> formattedValue() {
        return this.formattedValue;
    }

    public SpreadsheetCell setFormattedValue(final Optional<TextNode> formattedValue) {
        Objects.requireNonNull(formattedValue, "formattedValue");

        final Optional<TextNode> formattedValueRoot = formattedValue.map(TextNode::root);
        return this.formattedValue.equals(formattedValueRoot) ?
            this :
            new SpreadsheetCell(
                this.reference,
                this.formula,
                this.dateTimeSymbols,
                this.decimalNumberSymbols,
                this.locale,
                this.formatter,
                this.parser,
                this.style,
                formattedValueRoot,
                this.validator
            );
    }

    /**
     * A cached form of the cell output formatted and formula executed.
     */
    private final Optional<TextNode> formattedValue;

    // validator........................................................................................................

    public Optional<ValidatorSelector> validator() {
        return this.validator;
    }

    public SpreadsheetCell setValidator(final Optional<ValidatorSelector> validator) {
        return this.validator.equals(validator) ?
            this :
            this.replace(
                reference,
                this.formula,
                this.dateTimeSymbols,
                this.decimalNumberSymbols,
                this.locale,
                this.formatter,
                this.parser,
                this.style,
                Objects.requireNonNull(validator, "validator")
            );
    }

    /**
     * An optional validator this cell.
     */
    private final Optional<ValidatorSelector> validator;

    // HasOptionalValidatorSelector.....................................................................................

    @Override
    public Optional<ValidatorSelector> validatorSelector() {
        return this.validator;
    }

    // replace..........................................................................................................

    /**
     * Replacing any of the properties other than formatted will clear formatted
     */
    private SpreadsheetCell replace(final SpreadsheetCellReference reference,
                                    final SpreadsheetFormula formula,
                                    final Optional<DateTimeSymbols> dateTimeSymbols,
                                    final Optional<DecimalNumberSymbols> decimalNumberSymbols,
                                    final Optional<Locale> locale,
                                    final Optional<SpreadsheetFormatterSelector> formatter,
                                    final Optional<SpreadsheetParserSelector> parser,
                                    final TextStyle style,
                                    final Optional<ValidatorSelector> validator) {
        return new SpreadsheetCell(
            reference,
            formula,
            dateTimeSymbols,
            decimalNumberSymbols,
            locale,
            formatter,
            parser,
            style,
            NO_FORMATTED_VALUE_CELL,
            validator
        );
    }

    // CanBeEmpty ......................................................................................................

    /**
     * A {@link SpreadsheetCell} is empty if it has no formula, no format or parse patterns and no style.
     */
    @Override
    public boolean isEmpty() {
        return this.formula.isEmpty() &&
            false == this.dateTimeSymbols.isPresent() &&
            false == this.decimalNumberSymbols.isPresent() &&
            false == this.locale.isPresent() &&
            false == this.formatter.isPresent() &&
            false == this.parser.isPresent() &&
            this.style.isEmpty() &&
            false == this.validator.isPresent();
    }

    // replaceReferences................................................................................................

    /**
     * Accepts a mapper which may be used to update the {@link #reference()} and {@link SpreadsheetFormula}.
     */
    @Override
    public SpreadsheetCell replaceReferences(final Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>> mapper) {
        Objects.requireNonNull(mapper, "mapper");

        final SpreadsheetCellReference cell = this.reference();
        return this.setReference(
            mapper.apply(this.reference())
                .orElseThrow(
                    () -> new IllegalArgumentException("Mapper returned nothing for " + cell)
                )
        ).setFormula(
            this.formula()
                .replaceReferences(mapper)
        );
    }

    // Patchable.......................................................................................................

    /**
     * Patches the given {@link SpreadsheetCell}. The cell and formatted properties cannot be updated via a patch.
     */
    @Override
    public SpreadsheetCell patch(final JsonNode json,
                                 final JsonNodeUnmarshallContext context) {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(context, "context");

        SpreadsheetCell patched = this;

        for (final JsonNode propertyAndValue : json.objectOrFail().children()) {
            final JsonPropertyName propertyName = propertyAndValue.name();
            switch (propertyName.value()) {
                case FORMULA_PROPERTY_STRING:
                    patched = patched.setFormula(
                        patched.formula()
                            .patch(
                                propertyAndValue,
                                context
                            )
                    );
                    break;
                case DATE_TIME_SYMBOLS_PROPERTY_STRING:
                    patched = patched.setDateTimeSymbols(
                        context.unmarshallOptional(
                            propertyAndValue,
                            DateTimeSymbols.class
                        )
                    );
                    break;
                case DECIMAL_NUMBER_SYMBOLS_PROPERTY_STRING:
                    patched = patched.setDecimalNumberSymbols(
                        context.unmarshallOptional(
                            propertyAndValue,
                            DecimalNumberSymbols.class
                        )
                    );
                    break;
                case LOCALE_PROPERTY_STRING:
                    patched = patched.setLocale(
                        context.unmarshallOptional(
                            propertyAndValue,
                            Locale.class
                        )
                    );
                    break;
                case FORMATTER_PROPERTY_STRING:
                    patched = patched.setFormatter(
                        context.unmarshallOptional(
                            propertyAndValue,
                            SpreadsheetFormatterSelector.class
                        )
                    );
                    break;
                case PARSER_PROPERTY_STRING:
                    patched = patched.setParser(
                        context.unmarshallOptional(
                            propertyAndValue,
                            SpreadsheetParserSelector.class
                        )
                    );
                    break;
                case STYLE_PROPERTY_STRING:
                    patched = patched.setStyle(
                        patched.style()
                            .patch(
                                propertyAndValue,
                                context
                            )
                    );
                    break;
                case VALIDATOR_PROPERTY_STRING:
                    patched = patched.setValidator(
                        context.unmarshallOptional(
                            propertyAndValue,
                            ValidatorSelector.class
                        )
                    );
                    break;
                case REFERENCE_PROPERTY_STRING:
                case FORMATTED_VALUE_PROPERTY_STRING:
                    Patchable.invalidPropertyPresent(propertyName, propertyAndValue);
                    break;
                default:
                    Patchable.unknownPropertyPresent(propertyName, propertyAndValue);
                    break;
            }
        }

        return patched;
    }

    /**
     * Creates a {@link JsonNode} patch that may be used by {@link #patch(JsonNode, JsonNodeUnmarshallContext)}.
     */
    public JsonNode formulaPatch(final JsonNodeMarshallContext context) {
        Objects.requireNonNull(context, "context");

        return this.makePatch(
            FORMULA_PROPERTY,
            context.marshall(this.formula)
        );
    }

    /**
     * Creates a {@link JsonNode} patch that may be used by {@link #patch(JsonNode, JsonNodeUnmarshallContext)} to patch
     * a {@link DateTimeSymbols}.
     */
    public JsonNode dateTimeSymbolsPatch(final JsonNodeMarshallContext context) {
        Objects.requireNonNull(context, "context");

        return this.makePatch(
            DATE_TIME_SYMBOLS_PROPERTY,
            context.marshallOptional(this.dateTimeSymbols)
        );
    }

    /**
     * Creates a {@link JsonNode} patch that may be used by {@link #patch(JsonNode, JsonNodeUnmarshallContext)} to patch
     * a {@link DecimalNumberSymbols}.
     */
    public JsonNode decimalNumberSymbolsPatch(final JsonNodeMarshallContext context) {
        Objects.requireNonNull(context, "context");

        return this.makePatch(
            DECIMAL_NUMBER_SYMBOLS_PROPERTY,
            context.marshallOptional(this.decimalNumberSymbols)
        );
    }

    /**
     * Creates a {@link JsonNode} patch that may be used by {@link #patch(JsonNode, JsonNodeUnmarshallContext)} to patch
     * a {@link Locale}.
     */
    public JsonNode localePatch(final JsonNodeMarshallContext context) {
        Objects.requireNonNull(context, "context");

        return this.makePatch(
            LOCALE_PROPERTY,
            context.marshallOptional(this.locale)
        );
    }

    /**
     * Creates a {@link JsonNode} patch that may be used by {@link #patch(JsonNode, JsonNodeUnmarshallContext)} to patch
     * a formatter.
     */
    public JsonNode formatterPatch(final JsonNodeMarshallContext context) {
        Objects.requireNonNull(context, "context");

        return this.makePatch(
            FORMATTER_PROPERTY,
            context.marshallOptional(this.formatter)
        );
    }

    /**
     * Creates a {@link JsonNode} patch that may be used by {@link #patch(JsonNode, JsonNodeUnmarshallContext)} to patch
     * a parse-pattern.
     */
    public JsonNode parserPatch(final JsonNodeMarshallContext context) {
        Objects.requireNonNull(context, "context");

        return this.makePatch(
            PARSER_PROPERTY,
            context.marshallOptional(this.parser)
        );
    }

    /**
     * Creates a {@link JsonNode} patch that may be used by {@link #patch(JsonNode, JsonNodeUnmarshallContext)} to
     * patch a style
     */
    public JsonNode stylePatch(final JsonNodeMarshallContext context) {
        Objects.requireNonNull(context, "context");

        return this.makePatch(
            STYLE_PROPERTY,
            context.marshall(this.style)
        );
    }

    /**
     * Creates a {@link JsonNode} patch that may be used by {@link #patch(JsonNode, JsonNodeUnmarshallContext)} to patch
     * a {@link ValidatorSelector}.
     */
    public JsonNode validatorPatch(final JsonNodeMarshallContext context) {
        Objects.requireNonNull(context, "context");

        return this.makePatch(
            VALIDATOR_PROPERTY,
            context.marshallOptional(this.validator)
        );
    }

    private JsonNode makePatch(final JsonPropertyName propertyName,
                               final JsonNode value) {
        return JsonNode.object()
            .set(
                propertyName,
                value
            ).setName(
                JsonPropertyName.with(
                    this.reference()
                        .toString()
                )
            );
    }

    // HasText..........................................................................................................

    /**
     * Returns a CSV record with ALL cell properties. Empty or missing properties will have empty tokens.
     * Most properties are converted to text (they implement {@link HasText#text()}, except for
     * {@link SpreadsheetFormula#valueType()} and {@link SpreadsheetCell#formattedValue()}.
     * <br>
     * This text will be used by various save history tokens to serialize a complete cell.
     */
    @Override
    public String text() {
        final SpreadsheetFormula formula = this.formula;

        return CsvStringList.EMPTY.setElements(
            Lists.of(
                this.reference().text(), // cell reference
                formula.text(),
                toText(formula.valueType()),
                toJsonText(formula.value()),
                toText(this.dateTimeSymbols),
                toText(this.decimalNumberSymbols),
                toJsonText(this.locale),
                toText(this.formatter),
                toText(this.parser),
                this.style.text(),
                toJsonText(this.formattedValue),
                toText(this.validator)
            )
        ).text();
    }

    private static String toJsonText(final Optional<?> value) {
        String json = "";

        if (value.isPresent()) {
            final JsonNode jsonNode = MARSHALL_CONTEXT.marshallWithType(value.get());

            final StringBuilder builder = new StringBuilder();
            final Printer printer = Printers.stringBuilder(
                builder,
                LineEnding.NONE
            );

            jsonNode.printJson(
                printer.indenting(Indentation.EMPTY)
            );

            json = builder.toString();
        }

        return json;
    }

    /**
     * The {@link SpreadsheetFormula#value()} and {@link #formattedValue()} will both be marshalled to JSON
     * before being included in the CSV {@link #text()}.
     */
    private final static JsonNodeMarshallContext MARSHALL_CONTEXT = JsonNodeMarshallContexts.basic();

    /**
     * Converts the {@link Optional} holding {@link HasText} into a token or returns an empty string.
     */
    private static String toText(final Optional<? extends HasText> hasText) {
        return hasText.map(HasText::text)
            .orElse("");
    }

    // parse............................................................................................................

    /**
     * Parses the CSV text back into a {@link SpreadsheetCell}. This is the inverse of {@link #text()}.
     */
    public static SpreadsheetCell parse(final String csv) {
        Objects.requireNonNull(csv, "csv");

        final CsvStringList list = CsvStringList.parse(csv);

        final int count = list.size();
        if (12 != count) {
            throw new IllegalArgumentException("Expected 12 tokens but got " + count);
        }

        SpreadsheetCell cell = SpreadsheetCell.with(
            SpreadsheetSelection.parseCell(
                list.get(0)
            ),
            SpreadsheetFormula.EMPTY.setText(
                list.get(1)
            )
        );

        cell = cell.setFormula(
            cell.formula()
                .setValueType(
                    parseCellComponent(
                        list.get(2),
                        ValidationValueTypeName::with
                    )
                ).setValue(
                    unmarshallCellComponentWithType(
                        list.get(3)
                    )
                )
        ).setDateTimeSymbols(
            parseCellComponent(
                list.get(4),
                DateTimeSymbols::parse
            )
        ).setDecimalNumberSymbols(
            parseCellComponent(
                list.get(5),
                DecimalNumberSymbols::parse
            )
        ).setLocale(
            parseCellComponent(
                list.get(6),
                Locale::forLanguageTag
            )
        ).setFormatter(
            parseCellComponent(
                list.get(7),
                SpreadsheetFormatterSelector::parse
            )
        ).setParser(
            parseCellComponent(
                list.get(8),
                SpreadsheetParserSelector::parse
            )
        ).setStyle(
            TextStyle.parse(
                list.get(9)
            )
        );

        final Optional<TextNode> formattedValue = unmarshallCellComponentWithType(
            list.get(10)
        );

        // must call setFormattedValue after setValidator because later clears former
        return cell.setValidator(
            parseCellComponent(
                list.get(11),
                ValidatorSelector::parse
            )
        ).setFormattedValue(formattedValue);
    }

    private static <TT> Optional<TT> parseCellComponent(final String text,
                                                        final Function<String, TT> componentParser) {
        return Optional.ofNullable(
            text.isEmpty() ?
                null :
                componentParser.apply(text)
        );
    }

    private static <TT> Optional<TT> unmarshallCellComponentWithType(final String text) {
        return Optional.ofNullable(
            text.isEmpty() ?
                null :
                UNMARSHALL_CONTEXT.unmarshallWithType(
                    JsonNode.parse(text)
                )
        );
    }

    private final static JsonNodeUnmarshallContext UNMARSHALL_CONTEXT = JsonNodeUnmarshallContexts.basic(
        ExpressionNumberKind.BIG_DECIMAL,
        MathContext.UNLIMITED
    );

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println("Cell " + this.reference());
        printer.indent();
        {
            this.formula.printTree(printer);

            this.printTreeLabel(
                "dateTimeSymbols",
                this.dateTimeSymbols,
                printer
            );

            this.printTreeLabel(
                "decimalNumberSymbols",
                this.decimalNumberSymbols,
                printer
            );

            this.printTreeLabel(
                "locale",
                this.locale,
                printer
            );

            this.printTreeLabel(
                "formatter",
                this.formatter,
                printer
            );

            this.printTreeLabel(
                "parser",
                this.parser,
                printer
            );

            final TextStyle style = this.style;
            if (style.isNotEmpty()) {
                this.printTreeLabel(
                    "style",
                    style,
                    printer
                );
            }

            this.printTreeLabel(
                "formattedValue",
                this.formattedValue,
                printer
            );

            this.printTreeLabel(
                "validator",
                this.validator,
                printer
            );
        }
        printer.outdent();
    }

    private void printTreeLabel(final String label,
                                final Optional<?> value,
                                final IndentingPrinter printer) {
        if (value.isPresent()) {
            this.printTreeLabel(
                label,
                value.get(),
                printer
            );
        }
    }

    private void printTreeLabel(final String label,
                                final Object value,
                                final IndentingPrinter printer) {
        printer.print(label);
        printer.println(":");

        printer.indent();
        {
            TreePrintable.printTreeOrToString(
                value,
                printer
            );
        }
        printer.outdent();
    }

    // JsonNodeContext...................................................................................................

    /**
     * Factory that creates a {@link SpreadsheetCell} parse a {@link JsonNode}.
     */
    static SpreadsheetCell unmarshall(final JsonNode node,
                                      final JsonNodeUnmarshallContext context) {
        Objects.requireNonNull(node, "node");

        SpreadsheetCell cell = null;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();
            if (null != cell) {
                JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
            }

            cell = unmarshallProperties(
                SpreadsheetSelection.parseCell(name.value()),
                child,
                context
            );
        }

        if (null == cell) {
            throw new JsonNodeUnmarshallException("Missing cell reference", node);
        }

        return cell;
    }

    private static SpreadsheetCell unmarshallProperties(final SpreadsheetCellReference reference,
                                                        final JsonNode node,
                                                        final JsonNodeUnmarshallContext context) {
        SpreadsheetFormula formula = SpreadsheetFormula.EMPTY;
        Optional<DateTimeSymbols> dateTimeSymbols = NO_DATETIME_SYMBOLS;
        Optional<DecimalNumberSymbols> decimalNumberSymbols = NO_DECIMAL_NUMBER_SYMBOLS;
        Optional<Locale> locale = NO_LOCALE;
        Optional<SpreadsheetFormatterSelector> formatter = NO_FORMATTER;
        Optional<SpreadsheetParserSelector> parser = NO_PARSER;
        TextStyle style = TextStyle.EMPTY;
        Optional<TextNode> formatted = NO_FORMATTED_VALUE_CELL;
        Optional<ValidatorSelector> validator = NO_VALIDATOR;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();
            switch (name.value()) {
                case FORMULA_PROPERTY_STRING:
                    formula = context.unmarshall(child, SpreadsheetFormula.class);
                    break;
                case DATE_TIME_SYMBOLS_PROPERTY_STRING:
                    dateTimeSymbols = context.unmarshallOptional(
                        child,
                        DateTimeSymbols.class
                    );
                    break;
                case DECIMAL_NUMBER_SYMBOLS_PROPERTY_STRING:
                    decimalNumberSymbols = context.unmarshallOptional(
                        child,
                        DecimalNumberSymbols.class
                    );
                    break;
                case LOCALE_PROPERTY_STRING:
                    locale = context.unmarshallOptional(
                        child,
                        Locale.class
                    );
                    break;
                case FORMATTER_PROPERTY_STRING:
                    formatter = context.unmarshallOptional(
                        child,
                        SpreadsheetFormatterSelector.class
                    );
                    break;
                case PARSER_PROPERTY_STRING:
                    parser = context.unmarshallOptional(
                        child,
                        SpreadsheetParserSelector.class
                    );
                    break;
                case STYLE_PROPERTY_STRING:
                    style = context.unmarshall(
                        child,
                        TextStyle.class
                    );
                    break;
                case FORMATTED_VALUE_PROPERTY_STRING:
                    formatted = context.unmarshallOptionalWithType(child);
                    break;
                case VALIDATOR_PROPERTY_STRING:
                    validator = context.unmarshallOptional(
                        child,
                        ValidatorSelector.class
                    );
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(
                        name,
                        node
                    );
                    break;
            }
        }

        return new SpreadsheetCell(
            reference,
            formula,
            dateTimeSymbols,
            decimalNumberSymbols,
            locale,
            formatter,
            parser,
            style,
            formatted,
            validator
        );
    }

    /**
     * <pre>
     * {
     *   "A1": {
     *     "formula": {
     *       "text": "=1+2"
     *     },
     *     "formatter": "text-format-pattern @",
     *     "parser": "number-parse-pattern $0.00",
     *     "style": {
     *       "font-style": "ITALIC"
     *     }
     *   }
     * }
     * </pre>
     */
    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.object()
            .set(
                this.referenceToJsonPropertyName(),
                marshallProperties(context)
            );
    }

    private JsonPropertyName referenceToJsonPropertyName() {
        return JsonPropertyName.with(
            this.reference.toString()
        );
    }

    private JsonNode marshallProperties(final JsonNodeMarshallContext context) {
        JsonObject object = JsonNode.object()
            .set(FORMULA_PROPERTY, context.marshall(this.formula));

        if (this.dateTimeSymbols.isPresent()) {
            object = object.set(
                DATE_TIME_SYMBOLS_PROPERTY,
                context.marshallOptional(this.dateTimeSymbols)
            );
        }

        if (this.decimalNumberSymbols.isPresent()) {
            object = object.set(
                DECIMAL_NUMBER_SYMBOLS_PROPERTY,
                context.marshallOptional(this.decimalNumberSymbols)
            );
        }

        if (this.locale.isPresent()) {
            object = object.set(
                LOCALE_PROPERTY,
                context.marshallOptional(this.locale)
            );
        }

        if (this.formatter.isPresent()) {
            object = object.set(
                FORMATTER_PROPERTY,
                context.marshallOptional(this.formatter)
            );
        }

        final Optional<SpreadsheetParserSelector> parser = this.parser;
        if (parser.isPresent()) {
            object = object.set(
                PARSER_PROPERTY,
                context.marshallOptional(parser)
            );
        }

        if (this.style.isNotEmpty()) {
            object = object.set(STYLE_PROPERTY, context.marshall(this.style));
        }

        if (this.formattedValue.isPresent()) {
            object = object.set(
                FORMATTED_VALUE_PROPERTY,
                context.marshallOptionalWithType(this.formattedValue)
            );
        }

        if (this.validator.isPresent()) {
            object = object.set(
                VALIDATOR_PROPERTY,
                context.marshallOptional(this.validator)
            );
        }

        return object;
    }

    private final static String REFERENCE_PROPERTY_STRING = "reference";

    private final static String FORMULA_PROPERTY_STRING = "formula";

    private final static String DATE_TIME_SYMBOLS_PROPERTY_STRING = "dateTimeSymbols";

    private final static String DECIMAL_NUMBER_SYMBOLS_PROPERTY_STRING = "decimalNumberSymbols";

    private final static String LOCALE_PROPERTY_STRING = "locale";

    private final static String FORMATTER_PROPERTY_STRING = "formatter";

    private final static String PARSER_PROPERTY_STRING = "parser";

    private final static String STYLE_PROPERTY_STRING = "style";

    private final static String FORMATTED_VALUE_PROPERTY_STRING = "formattedValue";

    private final static String VALIDATOR_PROPERTY_STRING = "validator";

    final static JsonPropertyName REFERENCE_PROPERTY = JsonPropertyName.with(REFERENCE_PROPERTY_STRING);

    final static JsonPropertyName DATE_TIME_SYMBOLS_PROPERTY = JsonPropertyName.with(DATE_TIME_SYMBOLS_PROPERTY_STRING);

    final static JsonPropertyName DECIMAL_NUMBER_SYMBOLS_PROPERTY = JsonPropertyName.with(DECIMAL_NUMBER_SYMBOLS_PROPERTY_STRING);

    final static JsonPropertyName LOCALE_PROPERTY = JsonPropertyName.with(LOCALE_PROPERTY_STRING);

    final static JsonPropertyName FORMULA_PROPERTY = JsonPropertyName.with(FORMULA_PROPERTY_STRING);

    final static JsonPropertyName FORMATTER_PROPERTY = JsonPropertyName.with(FORMATTER_PROPERTY_STRING);

    final static JsonPropertyName PARSER_PROPERTY = JsonPropertyName.with(PARSER_PROPERTY_STRING);

    final static JsonPropertyName STYLE_PROPERTY = JsonPropertyName.with(STYLE_PROPERTY_STRING);

    final static JsonPropertyName FORMATTED_VALUE_PROPERTY = JsonPropertyName.with(FORMATTED_VALUE_PROPERTY_STRING);

    final static JsonPropertyName VALIDATOR_PROPERTY = JsonPropertyName.with(VALIDATOR_PROPERTY_STRING);

    static {
        SpreadsheetCell.NO_FORMATTED_VALUE_CELL.hashCode();
        SpreadsheetFormula.EMPTY.hashCode();

        final Locale locale = Locale.getDefault();

        DateTimeSymbols.fromDateFormatSymbols(
            new DateFormatSymbols(locale)
        );
        DecimalNumberSymbols.fromDecimalFormatSymbols(
            '+',
            new DecimalFormatSymbols(locale)
        );
        TextNode.NO_ATTRIBUTES.isEmpty();
        SpreadsheetPattern.DEFAULT_TEXT_FORMAT_PATTERN.toString();

        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetCell.class),
            SpreadsheetCell::unmarshall,
            SpreadsheetCell::marshall,
            SpreadsheetCell.class
        );
    }

    // HashCodeEqualsDefined..........................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.reference,
            this.formula,
            this.dateTimeSymbols,
            this.decimalNumberSymbols,
            this.locale,
            this.style,
            this.parser,
            this.formatter,
            this.formattedValue,
            this.validator
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetCell &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetCell other) {
        return this.reference.equals(other.reference()) &&
            this.formula.equals(other.formula()) &&
            this.dateTimeSymbols.equals(other.dateTimeSymbols) &&
            this.decimalNumberSymbols.equals(other.decimalNumberSymbols) &&
            this.locale.equals(other.locale) &&
            this.style.equals(other.style) &&
            this.parser.equals(other.parser) &&
            this.formatter.equals(other.formatter) &&
            this.formattedValue.equals(other.formattedValue) &&
            this.validator.equals(other.validator);
    }

    @Override
    public String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    @Override
    public void buildToString(final ToStringBuilder builder) {
        builder.value(this.reference)
            .value(this.formula)
            .value(this.dateTimeSymbols)
            .value(this.decimalNumberSymbols)
            .value(this.locale)
            .value(this.style)
            .enable(ToStringBuilderOption.QUOTE)
            .value(this.parser.map(Object::toString).orElse(""))
            .value(this.formatter.map(Object::toString).orElse(""))
            .value(this.validator.map(Object::toString).orElse(""))
            .disable(ToStringBuilderOption.QUOTE)
            .value(this.formattedValue);
    }

    // HasTextNode......................................................................................................

    @Override
    public TextNode toTextNode() {
        return this.formattedValue()
            .orElse(null);
    }
}
