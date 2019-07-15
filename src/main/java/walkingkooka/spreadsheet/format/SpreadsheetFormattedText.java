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

package walkingkooka.spreadsheet.format;

import walkingkooka.Cast;
import walkingkooka.ToStringBuilder;
import walkingkooka.ToStringBuilderOption;
import walkingkooka.UsesToStringBuilder;
import walkingkooka.collect.map.Maps;
import walkingkooka.color.ColorHslOrHsv;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.text.HasText;
import walkingkooka.tree.text.HasTextNode;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

import java.util.Objects;
import java.util.Optional;

/**
 * Holds the {@link ColorHslOrHsv} and text that results from formatting a value.
 */
public final class SpreadsheetFormattedText implements HasText,
        HasTextNode,
        HashCodeEqualsDefined,
        UsesToStringBuilder {

    /**
     * Constant that holds an empty color.
     */
    public final static Optional<ColorHslOrHsv> WITHOUT_COLOR = Optional.empty();

    /**
     * Creates a {@link SpreadsheetFormattedText}
     */
    public static SpreadsheetFormattedText with(final Optional<ColorHslOrHsv> color, final String text) {
        checkColor(color);
        checkText(text);

        return new SpreadsheetFormattedText(color, text);
    }

    private static void checkColor(final Optional<ColorHslOrHsv> color) {
        Objects.requireNonNull(color, "color");
    }

    private static void checkText(final String text) {
        Objects.requireNonNull(text, "text");
    }

    /**
     * Private ctor use factory.
     */
    private SpreadsheetFormattedText(final Optional<ColorHslOrHsv> color, final String text) {
        this.color = color;
        this.text = text;
    }

    public Optional<ColorHslOrHsv> color() {
        return this.color;
    }

    public SpreadsheetFormattedText setColor(final Optional<ColorHslOrHsv> color) {
        checkColor(color);

        return this.color.equals(color) ?
                this :
                this.replace(color, this.text);
    }

    private final Optional<ColorHslOrHsv> color;

    @Override
    public String text() {
        return this.text;
    }

    public SpreadsheetFormattedText setText(final String text) {
        checkText(text);

        return this.text.equals(text) ?
                this :
                this.replace(this.color, text);
    }

    private final String text;

    private SpreadsheetFormattedText replace(final Optional<ColorHslOrHsv> color, final String text) {
        return new SpreadsheetFormattedText(color, text);
    }

    // HasTextNode......................................................................................................

    @Override
    public TextNode toTextNode() {
        return this.color.map(c -> TextStyle.with(Maps.of(TextStylePropertyName.TEXT_COLOR, c)))
                .orElse(TextStyle.EMPTY)
                .replace(TextNode.text(this.text));
    }

    // Object ..........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.color, this.text);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetFormattedText &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetFormattedText other) {
        return this.color.equals(other.color) &&
                this.text.equals(other.text);
    }

    @Override
    public String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    @Override
    public void buildToString(ToStringBuilder builder) {
        builder.separator(" ");
        builder.value(this.color);

        builder.enable(ToStringBuilderOption.QUOTE);
        builder.value(this.text);
    }
}
