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
import walkingkooka.UsesToStringBuilder;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
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

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A spreadsheet cell including its formula, and other attributes such as format, text properties(styling) and more.
 */
public final class SpreadsheetCell implements CanBeEmpty,
        CanReplaceReferences<SpreadsheetCell>,
        Comparable<SpreadsheetCell>,
        HasSpreadsheetReference<SpreadsheetCellReference>,
        HateosResource<SpreadsheetCellReference>,
        Patchable<SpreadsheetCell>,
        TreePrintable,
        UsesToStringBuilder {

    /**
     * Holds an absent {@link SpreadsheetFormatterSelector}.
     */
    public final static Optional<SpreadsheetFormatterSelector> NO_FORMATTER = Optional.empty();

    /**
     * Holds an absent {@link TextNode}.
     */
    public final static Optional<TextNode> NO_FORMATTED_VALUE_CELL = Optional.empty();

    /**
     * Holds an absent {@link SpreadsheetParsePattern}.
     */
    public final static Optional<SpreadsheetParsePattern> NO_PARSE_PATTERN = Optional.empty();

    /**
     * An empty {@link TextStyle}.
     */
    public final static TextStyle NO_STYLE = TextStyle.EMPTY;

    /**
     * Factory that creates a new {@link SpreadsheetCell}
     */
    public static SpreadsheetCell with(final SpreadsheetCellReference reference,
                                       final SpreadsheetFormula formula) {
        checkReference(reference);

        return new SpreadsheetCell(
                reference,
                checkFormula(formula),
                NO_STYLE,
                NO_PARSE_PATTERN,
                NO_FORMATTER,
                NO_FORMATTED_VALUE_CELL
        );
    }

    private static void checkReference(final SpreadsheetCellReference reference) {
        Objects.requireNonNull(reference, "reference");
    }

    /**
     * If a formula has a Collection value, return the value with {@link SpreadsheetErrorKind#VALUE}.
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

    private static void checkTextStyle(final TextStyle style) {
        Objects.requireNonNull(style, "style");
    }

    /**
     * Private ctor
     */
    private SpreadsheetCell(final SpreadsheetCellReference reference,
                            final SpreadsheetFormula formula,
                            final TextStyle style,
                            final Optional<SpreadsheetParsePattern> parsePattern,
                            final Optional<SpreadsheetFormatterSelector> formatter,
                            final Optional<TextNode> formattedValue) {
        super();

        this.reference = reference.toRelative();
        this.formula = formula;
        this.style = style;
        this.parsePattern = parsePattern;
        this.formatter = formatter;
        this.formattedValue = formattedValue;
    }

    // HasId .......................................................................................

    public Optional<SpreadsheetCellReference> id() {
        return Optional.of(this.reference());
    }

    @Override
    public String hateosLinkId() {
        return this.reference().hateosLinkId();
    }

    // reference .............................................................................................

    @Override
    public SpreadsheetCellReference reference() {
        return this.reference;
    }

    public SpreadsheetCell setReference(final SpreadsheetCellReference reference) {
        checkReference(reference);

        return this.reference.equals(reference) ?
                this :
                this.replace(reference, this.formula, this.style, this.parsePattern, this.formatter, NO_FORMATTED_VALUE_CELL);
    }

    /**
     * The reference that identifies this cell.
     */
    private final SpreadsheetCellReference reference;

    // formula .............................................................................................

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
                this.replace(this.reference, formula, this.style, this.parsePattern, this.formatter, NO_FORMATTED_VALUE_CELL);
    }

    /**
     * The formula that appears in this cell.
     */
    private final SpreadsheetFormula formula;

    // style .............................................................................................

    public TextStyle style() {
        return this.style;
    }

    public SpreadsheetCell setStyle(final TextStyle style) {
        checkTextStyle(style);

        return this.style.equals(style) ?
                this :
                this.replace(this.reference, this.formula, style, this.parsePattern, this.formatter, NO_FORMATTED_VALUE_CELL);
    }

    /**
     * The cell style that is used to format the output of the formula.
     */
    private final TextStyle style;

    // parsePattern..... .............................................................................................

    public Optional<SpreadsheetParsePattern> parsePattern() {
        return this.parsePattern;
    }

    /**
     * Returns a {@link SpreadsheetCell} with the given {@link SpreadsheetParsePattern}. If the formula has a token or
     * expression they will be cleared.
     */
    public SpreadsheetCell setParsePattern(final Optional<SpreadsheetParsePattern> parsePattern) {
        Objects.requireNonNull(parsePattern, "parsePattern");

        return this.parsePattern.equals(parsePattern) ?
                this :
                this.setParsePattern0(parsePattern);
    }

    private SpreadsheetCell setParsePattern0(final Optional<SpreadsheetParsePattern> parsePattern) {
        final SpreadsheetFormula formula = this.formula;

        return this.replace(
                this.reference,
                formula.setToken(SpreadsheetFormula.NO_TOKEN)
                        .setText(formula.text()),
                this.style,
                parsePattern,
                this.formatter,
                NO_FORMATTED_VALUE_CELL
        );
    }

    /**
     * When present used to parse non expressions into a value.
     */
    private final Optional<SpreadsheetParsePattern> parsePattern;

    // formatter..... .............................................................................................

    public Optional<SpreadsheetFormatterSelector> formatter() {
        return this.formatter;
    }

    public SpreadsheetCell setFormatter(final Optional<SpreadsheetFormatterSelector> formatter) {
        Objects.requireNonNull(formatter, "formatter");

        return this.formatter.equals(formatter) ?
                this :
                this.replace(this.reference, this.formula, this.style, this.parsePattern, formatter, NO_FORMATTED_VALUE_CELL);
    }

    /**
     * Used to format the output of the cell's formula.
     */
    private final Optional<SpreadsheetFormatterSelector> formatter;

    // formatted .............................................................................................

    public Optional<TextNode> formattedValue() {
        return this.formattedValue;
    }

    public SpreadsheetCell setFormattedValue(final Optional<TextNode> formattedValue) {
        Objects.requireNonNull(formattedValue, "formattedValue");

        final Optional<TextNode> formatted2 = formattedValue.map(TextNode::root);
        return this.formattedValue.equals(formatted2) ?
                this :
                this.replace(this.reference, this.formula, this.style, this.parsePattern, this.formatter, formatted2);
    }

    /**
     * A cached form of the cell output formatted and formula executed.
     */
    private final Optional<TextNode> formattedValue;

    // replace..........................................................................................................

    /**
     * Replacing any of the properties other than formatted will clear formatted
     */
    private SpreadsheetCell replace(final SpreadsheetCellReference reference,
                                    final SpreadsheetFormula formula,
                                    final TextStyle style,
                                    final Optional<SpreadsheetParsePattern> parsePattern,
                                    final Optional<SpreadsheetFormatterSelector> formatter,
                                    final Optional<TextNode> formatted) {
        return new SpreadsheetCell(
                reference,
                formula,
                style,
                parsePattern,
                formatter,
                formatted
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
                false == this.parsePattern.isPresent() &&
                this.style.isEmpty();
    }

    // Comparable.......................................................................................................

    @Override
    public int compareTo(final SpreadsheetCell other) {
        return this.reference().compareTo(other.reference());
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
        ).setFormula(this.formula().replaceReferences(mapper));
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
                case PARSE_PATTERN_PROPERTY_STRING:
                    patched = patched.setParsePattern(
                            Optional.ofNullable(
                                    context.unmarshallWithType(propertyAndValue)
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
    public JsonNode parsePatternPatch(final JsonNodeMarshallContext context) {
        checkContext(context);

        return this.makePatch(
                PARSE_PATTERN_PROPERTY,
                context.marshallWithType(
                        this.parsePattern.orElse(null)
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
                final Optional<SpreadsheetParsePattern> parsePattern = this.parsePattern();
                if (parsePattern.isPresent()) {
                    printer.println("parsePattern:");
                    printer.indent();
                    {
                        parsePattern.get().printTree(printer);
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

            cell = unmarshall0(
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

    private static SpreadsheetCell unmarshall0(final SpreadsheetCellReference reference,
                                               final JsonNode node,
                                               final JsonNodeUnmarshallContext context) {
        SpreadsheetFormula formula = SpreadsheetFormula.EMPTY;
        TextStyle style = TextStyle.EMPTY;
        SpreadsheetParsePattern parsePattern = null;
        SpreadsheetFormatterSelector formatter = null;
        TextNode formatted = null;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();
            switch (name.value()) {
                case FORMULA_PROPERTY_STRING:
                    formula = context.unmarshall(child, SpreadsheetFormula.class);
                    break;
                case STYLE_PROPERTY_STRING:
                    style = context.unmarshall(child, TextStyle.class);
                    break;
                case PARSE_PATTERN_PROPERTY_STRING:
                    parsePattern = context.unmarshallWithType(child);
                    break;
                case FORMATTER_PROPERTY_STRING:
                    formatter = context.unmarshall(
                            child,
                            SpreadsheetFormatterSelector.class
                    );
                    break;
                case FORMATTED_VALUE_PROPERTY_STRING:
                    formatted = context.unmarshallWithType(child);
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
                    break;
            }
        }

        return new SpreadsheetCell(
                reference,
                formula,
                style,
                Optional.ofNullable(parsePattern),
                Optional.ofNullable(formatter),
                Optional.ofNullable(formatted)
        );
    }

    /**
     * <pre>
     * {
     *   "A1": {
     *     "formula": {
     *       "text": "=1+2"
     *     },
     *     "formatter": "text-format @",
     *     "parse-pattern": {
     *       "type": "spreadsheet-number-parse-pattern",
     *       "value": "$0.00"
     *     },
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
                        marshall0(context)
                );
    }

    private JsonPropertyName referenceToJsonPropertyName() {
        return JsonPropertyName.with(
                this.reference.toString()
        );
    }

    private JsonNode marshall0(final JsonNodeMarshallContext context) {
        JsonObject object = JsonNode.object()
                .set(FORMULA_PROPERTY, context.marshall(this.formula));

        if (false == this.style.isEmpty()) {
            object = object.set(STYLE_PROPERTY, context.marshall(this.style));
        }

        if (this.parsePattern.isPresent()) {
            object = object.set(
                    PARSE_PATTERN_PROPERTY,
                    context.marshallWithType(
                            this.parsePattern.get()
                    )
            );
        }

        if (this.formatter.isPresent()) {
            object = object.set(
                    FORMATTER_PROPERTY,
                    context.marshall(
                            this.formatter.get()
                    )
            );
        }
        if (this.formattedValue.isPresent()) {
            object = object.set(FORMATTED_VALUE_PROPERTY, context.marshallWithType(this.formattedValue.get()));
        }

        return object;
    }

    private final static String REFERENCE_PROPERTY_STRING = "reference";
    private final static String FORMULA_PROPERTY_STRING = "formula";
    private final static String STYLE_PROPERTY_STRING = "style";
    private final static String PARSE_PATTERN_PROPERTY_STRING = "parse-pattern";
    private final static String FORMATTER_PROPERTY_STRING = "formatter";
    private final static String FORMATTED_VALUE_PROPERTY_STRING = "formatted-value";

    final static JsonPropertyName REFERENCE_PROPERTY = JsonPropertyName.with(REFERENCE_PROPERTY_STRING);
    final static JsonPropertyName FORMULA_PROPERTY = JsonPropertyName.with(FORMULA_PROPERTY_STRING);
    final static JsonPropertyName STYLE_PROPERTY = JsonPropertyName.with(STYLE_PROPERTY_STRING);
    final static JsonPropertyName PARSE_PATTERN_PROPERTY = JsonPropertyName.with(PARSE_PATTERN_PROPERTY_STRING);
    final static JsonPropertyName FORMATTER_PROPERTY = JsonPropertyName.with(FORMATTER_PROPERTY_STRING);
    final static JsonPropertyName FORMATTED_VALUE_PROPERTY = JsonPropertyName.with(FORMATTED_VALUE_PROPERTY_STRING);

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
                this.parsePattern,
                this.formatter,
                this.formattedValue
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
                this.parsePattern.equals(other.parsePattern) &&
                this.formatter.equals(other.formatter) &&
                this.formattedValue.equals(other.formattedValue);
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
                .value(this.parsePattern)
                .value(this.formatter)
                .value(this.formattedValue);
    }
}
