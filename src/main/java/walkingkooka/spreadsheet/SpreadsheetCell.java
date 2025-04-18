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
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.CanReplaceReferences;
import walkingkooka.spreadsheet.reference.HasSpreadsheetReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallException;
import walkingkooka.tree.json.patch.Patchable;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.validation.provider.ValidatorSelector;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
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
        TreePrintable,
        UsesToStringBuilder {

    /**
     * A {@link Comparator} that only uses the {@link SpreadsheetCell#REFERENCE_COMPARATOR}.
     */
    public static Comparator<SpreadsheetCell> REFERENCE_COMPARATOR = Comparator.comparing(SpreadsheetCell::reference);

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
                            final Optional<SpreadsheetFormatterSelector> formatter,
                            final Optional<SpreadsheetParserSelector> parser,
                            final TextStyle style,
                            final Optional<TextNode> formattedValue,
                            final Optional<ValidatorSelector> validator) {
        super();

        this.reference = reference;
        this.formula = formula;
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
                        this.formatter,
                        this.parser,
                        this.style,
                        NO_FORMATTED_VALUE_CELL,
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
                        this.formatter,
                        this.parser,
                        this.style,
                        NO_FORMATTED_VALUE_CELL,
                        this.validator
                );
    }

    /**
     * The formula that appears in this cell.
     */
    private final SpreadsheetFormula formula;

    /**
     * If a formula has a Collection value, return the {@link SpreadsheetFormula#expressionValue()} with {@link SpreadsheetErrorKind#VALUE}.
     * A cell range resolves to a {@link List}, thus a cell = a range, will show an #VALUE!.
     */
    private static SpreadsheetFormula checkFormula(final SpreadsheetFormula formula) {
        Objects.requireNonNull(formula, "formula");

        // if value is a List formula should have an ERROR of #VALUE!
        return formula.setExpressionValue(
                formula.expressionValue()
                        .map(v -> v instanceof Collection ? SpreadsheetErrorKind.VALUE : v)
        );
        // TODO https://github.com/mP1/walkingkooka-spreadsheet/issues/2205
    }

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
                        Objects.requireNonNull(formatter, "formatter"),
                        this.parser,
                        this.style,
                        NO_FORMATTED_VALUE_CELL,
                        this.validator
                );
    }

    /**
     * Used to format the output of the cell's formula.
     */
    private final Optional<SpreadsheetFormatterSelector> formatter;

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
                this.formatter,
                parser,
                this.style,
                NO_FORMATTED_VALUE_CELL,
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
                        this.formatter,
                        this.parser,
                        Objects.requireNonNull(style, "style"),
                        NO_FORMATTED_VALUE_CELL,
                        this.validator
                );
    }

    /**
     * The cell style that is used to format the output of the formula.
     */
    private final TextStyle style;

    // formatted .......................................................................................................

    public Optional<TextNode> formattedValue() {
        return this.formattedValue;
    }

    public SpreadsheetCell setFormattedValue(final Optional<TextNode> formattedValue) {
        Objects.requireNonNull(formattedValue, "formattedValue");

        final Optional<TextNode> formatted2 = formattedValue.map(TextNode::root);
        return this.formattedValue.equals(formatted2) ?
                this :
                this.replace(
                        this.reference,
                        this.formula,
                        this.formatter,
                        this.parser,
                        this.style,
                        formatted2,
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
                        this.formatter,
                        this.parser,
                        this.style,
                        this.formattedValue,
                        Objects.requireNonNull(validator, "validator")
                );
    }

    /**
     * An optional validator this cell.
     */
    private final Optional<ValidatorSelector> validator;

    // replace..........................................................................................................

    /**
     * Replacing any of the properties other than formatted will clear formatted
     */
    private SpreadsheetCell replace(final SpreadsheetCellReference reference,
                                    final SpreadsheetFormula formula,
                                    final Optional<SpreadsheetFormatterSelector> formatter,
                                    final Optional<SpreadsheetParserSelector> parser,
                                    final TextStyle style,
                                    final Optional<TextNode> formatted,
                                    final Optional<ValidatorSelector> validator) {
        return new SpreadsheetCell(
                reference,
                formula,
                formatter,
                parser,
                style,
                formatted,
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
                case FORMATTER_PROPERTY_STRING:
                    patched = patched.setFormatter(
                            Optional.ofNullable(
                                    context.unmarshall(
                                            propertyAndValue,
                                            SpreadsheetFormatterSelector.class
                                    )
                            )
                    );
                    break;
                case PARSER_PROPERTY_STRING:
                    patched = patched.setParser(
                            Optional.ofNullable(
                                    context.unmarshall(
                                            propertyAndValue,
                                            SpreadsheetParserSelector.class
                                    )
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
                            Optional.ofNullable(
                                    context.unmarshall(
                                            propertyAndValue,
                                            ValidatorSelector.class
                                    )
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
        checkContext(context);

        return this.makePatch(
                FORMULA_PROPERTY,
                context.marshall(this.formula)
        );
    }

    /**
     * Creates a {@link JsonNode} patch that may be used by {@link #patch(JsonNode, JsonNodeUnmarshallContext)} to patch
     * a formatter.
     */
    public JsonNode formatterPatch(final JsonNodeMarshallContext context) {
        checkContext(context);

        return this.makePatch(
                FORMATTER_PROPERTY,
                context.marshall(
                        this.formatter.orElse(null)
                )
        );
    }

    /**
     * Creates a {@link JsonNode} patch that may be used by {@link #patch(JsonNode, JsonNodeUnmarshallContext)} to patch
     * a parse-pattern.
     */
    public JsonNode parserPatch(final JsonNodeMarshallContext context) {
        checkContext(context);

        return this.makePatch(
                PARSER_PROPERTY,
                context.marshall(
                        this.parser.orElse(null)
                )
        );
    }

    /**
     * Creates a {@link JsonNode} patch that may be used by {@link #patch(JsonNode, JsonNodeUnmarshallContext)} to
     * patch a style
     */
    public JsonNode stylePatch(final JsonNodeMarshallContext context) {
        checkContext(context);

        return this.makePatch(
                STYLE_PROPERTY,
                context.marshall(this.style)
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

    private static JsonNodeMarshallContext checkContext(final JsonNodeMarshallContext context) {
        return Objects.requireNonNull(context, "context");
    }

    // TreePrintable.....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println("Cell " + this.reference());
        printer.indent();
        {
            this.formula.printTree(printer);

            {
                final Optional<SpreadsheetFormatterSelector> formatter = this.formatter();
                if (formatter.isPresent()) {
                    printer.println("formatter:");
                    printer.indent();
                    {
                        formatter.get()
                                .printTree(printer);
                    }
                    printer.outdent();
                }
            }

            {
                final Optional<SpreadsheetParserSelector> parser = this.parser();
                if (parser.isPresent()) {
                    printer.println("parser:");
                    printer.indent();
                    {
                        parser.get().printTree(printer);
                    }
                    printer.outdent();
                }
            }

            this.style.printTree(printer);

            {
                final Optional<TextNode> formatted = this.formattedValue();
                if (formatted.isPresent()) {
                    printer.println("formattedValue:");
                    printer.indent();
                    {
                        formatted.get().printTree(printer);
                    }
                    printer.outdent();
                }
            }

            {
                final Optional<ValidatorSelector> validator = this.validator();
                if (validator.isPresent()) {
                    printer.println("validator:");
                    printer.indent();
                    {
                        validator.get()
                                .printTree(printer);
                    }
                    printer.outdent();
                }
            }
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
        SpreadsheetFormatterSelector formatter = null;
        TextStyle style = TextStyle.EMPTY;
        SpreadsheetParserSelector parser = null;
        TextNode formatted = null;
        ValidatorSelector validator = null;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();
            switch (name.value()) {
                case FORMULA_PROPERTY_STRING:
                    formula = context.unmarshall(child, SpreadsheetFormula.class);
                    break;
                case FORMATTER_PROPERTY_STRING:
                    formatter = context.unmarshall(
                            child,
                            SpreadsheetFormatterSelector.class
                    );
                    break;
                case PARSER_PROPERTY_STRING:
                    parser = context.unmarshall(
                            child,
                            SpreadsheetParserSelector.class
                    );
                    break;
                case STYLE_PROPERTY_STRING:
                    style = context.unmarshall(child, TextStyle.class);
                    break;
                case FORMATTED_VALUE_PROPERTY_STRING:
                    formatted = context.unmarshallWithType(child);
                    break;
                case VALIDATOR_PROPERTY_STRING:
                    validator = context.unmarshallWithType(child);
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
                    break;
            }
        }

        return new SpreadsheetCell(
                reference,
                formula,
                Optional.ofNullable(formatter),
                Optional.ofNullable(parser),
                style,
                Optional.ofNullable(formatted),
                Optional.ofNullable(validator)
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

        if (this.formatter.isPresent()) {
            object = object.set(
                    FORMATTER_PROPERTY,
                    context.marshall(
                            this.formatter.get()
                    )
            );
        }

        final Optional<SpreadsheetParserSelector> parser = this.parser;
        if (parser.isPresent()) {
            object = object.set(
                    PARSER_PROPERTY,
                    context.marshall(
                            parser.get()
                    )
            );
        }

        if (this.style.isNotEmpty()) {
            object = object.set(STYLE_PROPERTY, context.marshall(this.style));
        }

        if (this.formattedValue.isPresent()) {
            object = object.set(FORMATTED_VALUE_PROPERTY, context.marshallWithType(this.formattedValue.get()));
        }

        if (this.validator.isPresent()) {
            object = object.set(
                    VALIDATOR_PROPERTY,
                    context.marshallWithType(
                            this.validator.get()
                    )
            );
        }

        return object;
    }

    private final static String REFERENCE_PROPERTY_STRING = "reference";

    private final static String FORMULA_PROPERTY_STRING = "formula";

    private final static String FORMATTER_PROPERTY_STRING = "formatter";

    private final static String PARSER_PROPERTY_STRING = "parser";

    private final static String STYLE_PROPERTY_STRING = "style";

    private final static String FORMATTED_VALUE_PROPERTY_STRING = "formatted-value";

    private final static String VALIDATOR_PROPERTY_STRING = "validator";

    final static JsonPropertyName REFERENCE_PROPERTY = JsonPropertyName.with(REFERENCE_PROPERTY_STRING);

    final static JsonPropertyName FORMULA_PROPERTY = JsonPropertyName.with(FORMULA_PROPERTY_STRING);

    final static JsonPropertyName FORMATTER_PROPERTY = JsonPropertyName.with(FORMATTER_PROPERTY_STRING);

    final static JsonPropertyName PARSER_PROPERTY = JsonPropertyName.with(PARSER_PROPERTY_STRING);

    final static JsonPropertyName STYLE_PROPERTY = JsonPropertyName.with(STYLE_PROPERTY_STRING);

    final static JsonPropertyName FORMATTED_VALUE_PROPERTY = JsonPropertyName.with(FORMATTED_VALUE_PROPERTY_STRING);

    final static JsonPropertyName VALIDATOR_PROPERTY = JsonPropertyName.with(VALIDATOR_PROPERTY_STRING);

    static {
        SpreadsheetCell.NO_FORMATTED_VALUE_CELL.hashCode();
        SpreadsheetFormula.EMPTY.hashCode();
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
                .value(this.style)
                .enable(ToStringBuilderOption.QUOTE)
                .value(this.parser.map(Object::toString).orElse(""))
                .value(this.formatter.map(Object::toString).orElse(""))
                .value(this.validator.map(Object::toString).orElse(""))
                .disable(ToStringBuilderOption.QUOTE)
                .value(this.formattedValue);
    }
}
