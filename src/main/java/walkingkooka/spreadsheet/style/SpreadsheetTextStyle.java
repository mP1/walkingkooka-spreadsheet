package walkingkooka.spreadsheet.style;

import walkingkooka.Cast;
import walkingkooka.build.tostring.ToStringBuilder;
import walkingkooka.build.tostring.ToStringBuilderOption;
import walkingkooka.build.tostring.UsesToStringBuilder;
import walkingkooka.color.Color;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeException;
import walkingkooka.tree.json.JsonNodeName;
import walkingkooka.tree.json.JsonObjectNode;

import java.util.Objects;
import java.util.Optional;

/**
 * A container holding various text-related style attributes.
 */
public final class SpreadsheetTextStyle implements HashCodeEqualsDefined,
        HasJsonNode,
        UsesToStringBuilder {

    public final static Optional<FontFamilyName> NO_FONT_FAMILY = Optional.empty();
    public final static Optional<FontSize> NO_FONT_SIZE = Optional.empty();
    public final static Optional<Color> NO_COLOR = Optional.empty();
    public final static Optional<Color> NO_BACKGROUND_COLOR = Optional.empty();

    public final static Optional<Boolean> NO_BOLD = Optional.empty();
    public final static Optional<Boolean> NO_ITALICS = Optional.empty();
    public final static Optional<Boolean> NO_UNDERLINE = Optional.empty();
    public final static Optional<Boolean> NO_STRIKETHRU = Optional.empty();

    public final static Optional<Boolean> BOLD = Optional.of(Boolean.TRUE);
    public final static Optional<Boolean> ITALICS = Optional.of(Boolean.TRUE);
    public final static Optional<Boolean> UNDERLINE = Optional.of(Boolean.TRUE);
    public final static Optional<Boolean> STRIKETHRU = Optional.of(Boolean.TRUE);
    /**
     * A {@link SpreadsheetTextStyle} without any properties.
     */
    public final static SpreadsheetTextStyle EMPTY = new SpreadsheetTextStyle(NO_FONT_FAMILY,
            NO_FONT_SIZE,
            NO_COLOR,
            NO_BACKGROUND_COLOR,
            NO_BOLD,
            NO_ITALICS,
            NO_UNDERLINE,
            NO_STRIKETHRU);

    /**
     * Factory that creates a {@link SpreadsheetTextStyle}
     */
    public static SpreadsheetTextStyle with(final Optional<FontFamilyName> fontFamily,
                                            final Optional<FontSize> fontSize,
                                            final Optional<Color> color,
                                            final Optional<Color> backgroundColor,
                                            final Optional<Boolean> bold,
                                            final Optional<Boolean> italics,
                                            final Optional<Boolean> underline,
                                            final Optional<Boolean> strikethru) {
        checkFontFamily(fontFamily);
        checkFontSize(fontSize);
        checkColor(color);
        checkBackgroundColor(backgroundColor);
        checkBold(bold);
        checkItalics(italics);
        checkUnderline(underline);
        checkStrikethru(strikethru);

        return new SpreadsheetTextStyle(fontFamily, fontSize, color, backgroundColor, bold, italics, underline, strikethru);
    }

    /**
     * Private ctor use static factory or constant.
     */
    private SpreadsheetTextStyle(final Optional<FontFamilyName> fontFamily,
                                 final Optional<FontSize> fontSize,
                                 final Optional<Color> color,
                                 final Optional<Color> backgroundColor,
                                 final Optional<Boolean> bold,
                                 final Optional<Boolean> italics,
                                 final Optional<Boolean> underline,
                                 final Optional<Boolean> strikethru) {
        this.fontFamily = fontFamily;
        this.fontSize = fontSize;
        this.color = color;
        this.backgroundColor = backgroundColor;
        this.bold = bold;
        this.italics = italics;
        this.underline = underline;
        this.strikethru = strikethru;
    }

    public Optional<FontFamilyName> fontFamily() {
        return this.fontFamily;
    }

    public SpreadsheetTextStyle setFontFamily(final Optional<FontFamilyName> fontFamily) {
        checkFontFamily(fontFamily);

        return this.fontFamily.equals(fontFamily) ?
                this :
                this.replace(fontFamily, this.fontSize, this.color, this.backgroundColor, this.bold, this.italics, this.underline, this.strikethru);
    }

    private final Optional<FontFamilyName> fontFamily;

    private static void checkFontFamily(final Optional<FontFamilyName> fontFamily) {
        Objects.requireNonNull(fontFamily, "fontFamily");
    }

    public Optional<FontSize> fontSize() {
        return this.fontSize;
    }

    public SpreadsheetTextStyle setFontSize(final Optional<FontSize> fontSize) {
        checkFontSize(fontSize);

        return this.fontSize.equals(fontSize) ?
                this :
                this.replace(this.fontFamily, fontSize, this.color, this.backgroundColor, this.bold, this.italics, this.underline, this.strikethru);
    }

    private final Optional<FontSize> fontSize;

    private static void checkFontSize(final Optional<FontSize> fontSize) {
        Objects.requireNonNull(fontSize, "fontSize");
    }

    public Optional<Color> color() {
        return this.color;
    }

    public SpreadsheetTextStyle setColor(final Optional<Color> color) {
        checkColor(color);

        return this.color.equals(color) ?
                this :
                this.replace(this.fontFamily, this.fontSize, color, this.backgroundColor, this.bold, this.italics, this.underline, this.strikethru);
    }

    private final Optional<Color> color;

    private static void checkColor(final Optional<Color> color) {
        Objects.requireNonNull(color, "color");
    }

    public Optional<Color> backgroundColor() {
        return this.backgroundColor;
    }

    public SpreadsheetTextStyle setBackgroundColor(final Optional<Color> backgroundColor) {
        checkBackgroundColor(backgroundColor);

        return this.backgroundColor.equals(backgroundColor) ?
                this :
                this.replace(this.fontFamily, this.fontSize, this.color, backgroundColor, this.bold, this.italics, this.underline, this.strikethru);
    }

    private final Optional<Color> backgroundColor;

    private static void checkBackgroundColor(final Optional<Color> backgroundColor) {
        Objects.requireNonNull(backgroundColor, "backgroundColor");
    }

    public Optional<Boolean> bold() {
        return this.bold;
    }

    public SpreadsheetTextStyle setBold(final Optional<Boolean> bold) {
        checkBold(bold);

        return this.bold.equals(bold) ?
                this :
                this.replace(this.fontFamily, this.fontSize, this.color, this.backgroundColor, bold, this.italics, this.underline, this.strikethru);
    }

    private final Optional<Boolean> bold;

    private static void checkBold(final Optional<Boolean> bold) {
        Objects.requireNonNull(bold, "bold");
    }

    public Optional<Boolean> italics() {
        return this.italics;
    }

    public SpreadsheetTextStyle setItalics(final Optional<Boolean> italics) {
        checkItalics(italics);

        return this.italics.equals(italics) ?
                this :
                this.replace(this.fontFamily, this.fontSize, this.color, this.backgroundColor, this.bold, italics, this.underline, this.strikethru);
    }

    private final Optional<Boolean> italics;

    private static void checkItalics(final Optional<Boolean> italics) {
        Objects.requireNonNull(italics, "italics");
    }

    public Optional<Boolean> underline() {
        return this.underline;
    }

    public SpreadsheetTextStyle setUnderline(final Optional<Boolean> underline) {
        checkUnderline(underline);

        return this.underline.equals(underline) ?
                this :
                this.replace(this.fontFamily, this.fontSize, this.color, this.backgroundColor, this.bold, this.italics, underline, this.strikethru);
    }

    private final Optional<Boolean> underline;

    private static void checkUnderline(final Optional<Boolean> underline) {
        Objects.requireNonNull(underline, "underline");
    }

    public Optional<Boolean> strikethru() {
        return this.strikethru;
    }

    public SpreadsheetTextStyle setStrikethru(final Optional<Boolean> strikethru) {
        checkStrikethru(strikethru);

        return this.strikethru.equals(strikethru) ?
                this :
                this.replace(this.fontFamily, this.fontSize, this.color, this.backgroundColor, this.bold, this.italics, this.underline, strikethru);
    }

    private final Optional<Boolean> strikethru;

    private static void checkStrikethru(final Optional<Boolean> strikethru) {
        Objects.requireNonNull(strikethru, "strikethru");
    }

    /**
     * Factory that unconditionally creates a {@link SpreadsheetTextStyle}.
     */
    private SpreadsheetTextStyle replace(final Optional<FontFamilyName> fontFamily,
                                         final Optional<FontSize> fontSize,
                                         final Optional<Color> color,
                                         final Optional<Color> backgroundColor,
                                         final Optional<Boolean> bold,
                                         final Optional<Boolean> italics,
                                         final Optional<Boolean> underline,
                                         final Optional<Boolean> strikethru) {
        return new SpreadsheetTextStyle(fontFamily,
                fontSize,
                color,
                backgroundColor, bold, italics, underline, strikethru);
    }

    /**
     * Performs a cascading merge of all properties, if a property is absent in this then the property from other is used.
     */
    public SpreadsheetTextStyle merge(final SpreadsheetTextStyle other) {
        Objects.requireNonNull(other, "other");

        final SpreadsheetTextStyle merged = new SpreadsheetTextStyle(merge(this.fontFamily, other.fontFamily),
                merge(this.fontSize, other.fontSize),
                merge(this.color, other.color),
                merge(this.backgroundColor, other.backgroundColor),
                merge(this.bold, other.bold),
                merge(this.italics, other.italics),
                merge(this.underline, other.underline),
                merge(this.strikethru, other.strikethru));

        return this.equals0(merged) ?
                this :
                other.equals0(merged) ?
                        other :
                        merged;
    }

    private static <T> Optional<T> merge(final Optional<T> first, final Optional<T> second) {
        return first.isPresent() ?
                first :
                second;
    }

    /**
     * Returns true if ALL properties are empty/ absent.
     */
    public boolean isEmpty() {
        return !this.fontFamily.isPresent() &&
                !this.fontSize.isPresent() &&
                !this.color.isPresent() &&
                !this.backgroundColor.isPresent() &&
                !this.bold.isPresent() &&
                !this.italics.isPresent() &&
                !this.underline.isPresent() &&
                !this.strikethru.isPresent();
    }

    // HasJsonNode.........................................................................................

    /**
     * Factory that creates a {@link SpreadsheetTextStyle} from a {@link JsonNode}.
     */
    public static SpreadsheetTextStyle fromJsonNode(final JsonNode node) {
        Objects.requireNonNull(node, "node");

        FontFamilyName fontFamilyName = null;
        FontSize fontSize = null;
        Color color = null;
        Color backgroundColor = null;
        Boolean bold = null;
        Boolean italics = null;
        Boolean underline = null;
        Boolean strikethru = null;

        try {
            for (JsonNode child : node.objectOrFail().children()) {
                final JsonNodeName name = child.name();
                switch (name.value()) {
                    case FONT_FAMILY_PROPERTY_STRING:
                        fontFamilyName = FontFamilyName.fromJsonNode(child);
                        break;
                    case FONT_SIZE_PROPERTY_STRING:
                        fontSize = FontSize.fromJsonNode(child);
                        break;
                    case COLOR_PROPERTY_STRING:
                        color = Color.fromJsonNode(child);
                        break;
                    case BACKGROUND_COLOR_PROPERTY_STRING:
                        backgroundColor = Color.fromJsonNode(child);
                        break;
                    case BOLD_PROPERTY_STRING:
                        bold = booleanFromJsonNode(child);
                        break;
                    case ITALICS_PROPERTY_STRING:
                        italics = booleanFromJsonNode(child);
                        break;
                    case UNDERLINE_PROPERTY_STRING:
                        underline = booleanFromJsonNode(child);
                        break;
                    case STRIKETHRU_PROPERTY_STRING:
                        strikethru = booleanFromJsonNode(child);
                        break;
                    default:
                        HasJsonNode.unknownPropertyPresent(name, node);
                }
            }
        } catch (final JsonNodeException cause) {
            throw new IllegalArgumentException(cause.getMessage(), cause);
        }

        return with(
                Optional.ofNullable(fontFamilyName),
                Optional.ofNullable(fontSize),
                Optional.ofNullable(color),
                Optional.ofNullable(backgroundColor),
                Optional.ofNullable(bold),
                Optional.ofNullable(italics),
                Optional.ofNullable(underline),
                Optional.ofNullable(strikethru));
    }

    /**
     * Helper that fetches a property and complains if it is present but not a {@link Boolean}.
     */
    private static Boolean booleanFromJsonNode(final JsonNode value) {
        try {
            return value.booleanValueOrFail();
        } catch (final JsonNodeException cause) {
            throw new IllegalArgumentException(value.name() + " is not a boolean=" + value);
        }
    }

    // toJsonNode.................................................................................................

    /**
     * Creates the {@link JsonNode} equivalent of this object instance.
     */
    @Override
    public JsonNode toJsonNode() {
        JsonObjectNode object = JsonObjectNode.object();

        object = add0(FONT_FAMILY_PROPERTY, this.fontFamily, object);
        object = add0(FONT_SIZE_PROPERTY, this.fontSize, object);
        object = add0(COLOR_PROPERTY, this.color, object);
        object = add0(BACKGROUND_COLOR_PROPERTY, this.backgroundColor, object);

        object = add1(BOLD_PROPERTY, this.bold, object);
        object = add1(ITALICS_PROPERTY, this.italics, object);
        object = add1(UNDERLINE_PROPERTY, this.underline, object);
        object = add1(STRIKETHRU_PROPERTY, this.strikethru, object);

        return object;
    }

    private static JsonObjectNode add0(final JsonNodeName property,
                                       final Optional<? extends HasJsonNode> value,
                                       final JsonObjectNode object) {
        return value.isPresent() ?
                object.set(property, value.get().toJsonNode()) :
                object;
    }

    private static JsonObjectNode add1(final JsonNodeName property,
                                       final Optional<Boolean> value,
                                       final JsonObjectNode object) {
        return value.isPresent() ?
                object.set(property, JsonNode.booleanNode(value.get())) :
                object;
    }

    // @VisibleForTesting

    private final static String FONT_FAMILY_PROPERTY_STRING = "font-family";
    private final static String FONT_SIZE_PROPERTY_STRING = "font-size";
    private final static String COLOR_PROPERTY_STRING = "color";
    private final static String BACKGROUND_COLOR_PROPERTY_STRING = "background-color";
    private final static String BOLD_PROPERTY_STRING = "bold";
    private final static String ITALICS_PROPERTY_STRING = "italics";
    private final static String UNDERLINE_PROPERTY_STRING = "underline";
    private final static String STRIKETHRU_PROPERTY_STRING = "strikethru";
    
    final static JsonNodeName FONT_FAMILY_PROPERTY = JsonNodeName.with(FONT_FAMILY_PROPERTY_STRING);
    final static JsonNodeName FONT_SIZE_PROPERTY = JsonNodeName.with(FONT_SIZE_PROPERTY_STRING);
    final static JsonNodeName COLOR_PROPERTY = JsonNodeName.with(COLOR_PROPERTY_STRING);
    final static JsonNodeName BACKGROUND_COLOR_PROPERTY = JsonNodeName.with(BACKGROUND_COLOR_PROPERTY_STRING);
    final static JsonNodeName BOLD_PROPERTY = JsonNodeName.with(BOLD_PROPERTY_STRING);
    final static JsonNodeName ITALICS_PROPERTY = JsonNodeName.with(ITALICS_PROPERTY_STRING);
    final static JsonNodeName UNDERLINE_PROPERTY = JsonNodeName.with(UNDERLINE_PROPERTY_STRING);
    final static JsonNodeName STRIKETHRU_PROPERTY = JsonNodeName.with(STRIKETHRU_PROPERTY_STRING);

    // HashCodeEqualsDefined.........................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.fontFamily, this.fontSize, this.color, this.backgroundColor, this.bold, this.italics, this.underline, this.strikethru);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetTextStyle &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetTextStyle other) {
        return this.fontFamily.equals(other.fontFamily) &&
                this.fontSize.equals(other.fontSize) &&
                this.color.equals(other.color) &&
                this.backgroundColor.equals(other.backgroundColor) &&
                this.bold.equals(other.bold) &&
                this.italics.equals(other.italics) &&
                this.underline.equals(other.underline) &&
                this.strikethru.equals(other.strikethru);
    }

    @Override
    public String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    @Override
    public void buildToString(final ToStringBuilder builder) {
        builder.disable(ToStringBuilderOption.QUOTE);
        builder.separator(" ");
        builder.value(this.fontFamily);
        builder.value(this.fontSize);

        builder.value(this.color);
        builder.value(this.backgroundColor);

        add(this.bold, "bold", builder);
        add(this.italics, "italics", builder);
        add(this.underline, "underline", builder);
        add(this.strikethru, "strikethru", builder);
    }

    private static void add(final Optional<Boolean> value, final String label, final ToStringBuilder builder) {
        if (value.isPresent() && value.get()) {
            builder.value(label);
        }
    }
}
