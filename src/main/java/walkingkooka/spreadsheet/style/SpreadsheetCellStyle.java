package walkingkooka.spreadsheet.style;

import walkingkooka.Cast;
import walkingkooka.build.tostring.ToStringBuilder;
import walkingkooka.build.tostring.ToStringBuilderOption;
import walkingkooka.build.tostring.UsesToStringBuilder;
import walkingkooka.test.HashCodeEqualsDefined;

import java.util.Objects;

/**
 * A container holding various text-related style attributes.
 */
public final class SpreadsheetCellStyle implements HashCodeEqualsDefined, UsesToStringBuilder {

    /**
     * A {@link SpreadsheetCellStyle} without any properties.
     */
    public final static SpreadsheetCellStyle EMPTY = new SpreadsheetCellStyle(SpreadsheetTextStyle.EMPTY);

    /**
     * Factory that creates a {@link SpreadsheetCellStyle}
     */
    public static SpreadsheetCellStyle with(final SpreadsheetTextStyle text) {
        checkText(text);

        return new SpreadsheetCellStyle(text);
    }

    /**
     * Private ctor use static factory or constant.
     */
    private SpreadsheetCellStyle(final SpreadsheetTextStyle text) {
        super();
        this.text = text;
    }

    public SpreadsheetTextStyle text() {
        return this.text;
    }

    public SpreadsheetCellStyle setText(final SpreadsheetTextStyle text) {
        checkText(text);

        return this.text.equals(text) ?
                this :
                this.replace(text);
    }

    private final SpreadsheetTextStyle text;

    private static void checkText(final SpreadsheetTextStyle text) {
        Objects.requireNonNull(text, "text");
    }

    /**
     * Factory that unconditionally creates a {@link SpreadsheetCellStyle}.
     */
    private SpreadsheetCellStyle replace(final SpreadsheetTextStyle text) {
        return new SpreadsheetCellStyle(text);
    }

    /**
     * Performs a cascading merge of all properties, if a property is absent in this then the property from other is used.
     */
    public SpreadsheetCellStyle merge(final SpreadsheetCellStyle other) {
        Objects.requireNonNull(other, "other");

        final SpreadsheetCellStyle merged = new SpreadsheetCellStyle(this.text.merge(other.text));

        return this.equals0(merged) ?
                this :
                other.equals0(merged) ?
                        other :
                        merged;
    }

    // HashCodeEqualsDefined.........................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.text);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetCellStyle &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetCellStyle other) {
        return this.text.equals(other.text);
    }

    @Override
    public String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    @Override
    public void buildToString(final ToStringBuilder builder) {
        builder.disable(ToStringBuilderOption.QUOTE);
        builder.separator(" ");
        builder.value(this.text);
    }
}
