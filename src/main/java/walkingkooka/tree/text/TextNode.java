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

package walkingkooka.tree.text;

import walkingkooka.Cast;
import walkingkooka.ToStringBuilder;
import walkingkooka.ToStringBuilderOption;
import walkingkooka.UsesToStringBuilder;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.Url;
import walkingkooka.text.HasText;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.Node;
import walkingkooka.tree.TraversableHasTextOffset;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.select.NodeSelector;
import walkingkooka.tree.select.parser.ExpressionNodeSelectorParserToken;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Base class that may be used to represent rich text, some nodes with styling textStyle and others with plain text.
 */
public abstract class TextNode implements Node<TextNode, TextNodeName, TextStylePropertyName<?>, Object>,
    HasText,
    HasTextNode,
    Styleable,
    HasHtml,
    TreePrintable,
    TraversableHasTextOffset<TextNode>,
    UsesToStringBuilder {

    /**
     * Returns if the given {@link Class} is a {@link TextNode} or subclass.
     */
    public static boolean isTextNodeClass(final Class<?> clazz) {
        return TextNode.class == clazz ||
          Image.class == clazz ||
          Text.class == clazz ||
          TextPlaceholderNode.class == clazz ||
          Badge.class == clazz ||
          Hyperlink.class == clazz ||
          TextStyleNameNode.class == clazz ||
          TextStyleNode.class == clazz;
    }

    /**
     * No children constant
     */
    public final static List<TextNode> NO_CHILDREN = Lists.empty();

    /**
     * No or empty attributes
     */
    public final static Map<TextStylePropertyName<?>, Object> NO_ATTRIBUTES = Maps.empty();

    /**
     * Constant that represents no parent.
     */
    // must appear before EMPTY_TEXT otherwise EMPTY_TEXT.parent will be null (BAD)
    private final static Optional<TextNode> NO_PARENT = Optional.empty();

    /**
     * Constant that holds a {@link Text} with no text.
     */
    public final static Text EMPTY_TEXT = Text.emptyText();

    // public factory methods..........................................................................................

    /**
     * {@see Hyperlink}
     */
    public static Badge badge(final String badgeText) {
        return Badge.with(badgeText);
    }

    /**
     * {@see Hyperlink}
     */
    public static Hyperlink hyperlink(final Url url) {
        return Hyperlink.with(url);
    }

    /**
     * {@see Image}
     */
    public static Image image(final Url url) {
        return Image.with(url);
    }

    /**
     * {@see TextPlaceholderNode}
     */
    public static TextPlaceholderNode placeholder(final TextPlaceholderName placeholder) {
        return TextPlaceholderNode.with(placeholder);
    }

    /**
     * {@see TextStyleNode}
     */
    public static TextNode style(final List<TextNode> children) {
        return TextStyleNode.with(children, TextStyleNode.NO_ATTRIBUTES_MAP);
    }

    /**
     * {@see TextStyleNameNode}
     */
    public static TextStyleNameNode styleName(final TextStyleName styleName) {
        return TextStyleNameNode.with(styleName);
    }

    /**
     * {@see Text}
     */
    public static Text text(final String value) {
        return Text.with(value);
    }

    /**
     * Package private ctor to limit sub classing.
     */
    TextNode(final int index) {
        this.parent = NO_PARENT;
        this.index = index;
    }

    /**
     * Sets or replace the current {@link String text}.
     */
    public abstract TextNode setText(final String text);

    /**
     * Helper used by most sub-classes to replace any children with a new {@link TextNode}.
     * If the text is empty the children are cleared.
     */
    final <T extends TextNode> T replaceChildrenWithText(final String text) {
        return (T) this.setChildren(
            text.isEmpty() ?
                NO_CHILDREN :
                Lists.of(
                    TextNode.text(text)
                )
        );
    }

    // parent .........................................................................................................

    @Override
    public final Optional<TextNode> parent() {
        return this.parent;
    }

    /**
     * This setter is used to recreate the entire graph including parents of parents receiving new children.
     * It is only ever called by a parent node and is used to adopt new children.
     */
    final TextNode setParent(final Optional<TextNode> parent, final int index) {
        if (this.isBadge() && parent.isPresent()) {
            ///throw new IllegalArgumentException("Badges cannot have a parent");
            System.out.println("Badges cannot have a parent");
        }

        final TextNode copy = this.replace(index);
        copy.parent = parent;
        return copy;
    }

    private Optional<TextNode> parent;

    /**
     * Sub classes should call this and cast.
     */
    final TextNode removeParent0() {
        return this.isRoot() ?
            this :
            this.replace(NO_INDEX);
    }

    /**
     * Sub classes must create a new copy of the parent and replace the identified child using its index or similar,
     * and also sets its parent after creation, returning the equivalent child at the same index.
     */
    abstract TextNode setChild(final TextNode newChild, final int index);

    /**
     * Only ever called after during the completion of a setChildren, basically used to recreate the parent graph
     * containing this child.
     */
    final TextNode replaceChild(final Optional<TextNode> previousParent,
                                final int childIndex) {
        return previousParent.isPresent() ?
            previousParent.get()
                .setChild(this, childIndex)
                .children()
                .get(childIndex) :
            this;
    }

    // index........................................................................................................

    @Override
    public final int index() {
        return this.index;
    }

    final int index;

    /**
     * Replaces the index, retaining other textStyle.
     */
    abstract TextNode replace(final int index);

    // setAttributes....................................................................................................

    /**
     * If a {@link TextStyleNode} the attributes are replaced, for other {@link TextNode} types, when the
     * attributes are non empty this node is wrapped in a {@link TextStyleNode}.
     */
    @Override
    public final TextNode setAttributes(final Map<TextStylePropertyName<?>, Object> attributes) {
        final TextNodeMap textStyleMap = TextNodeMap.with(attributes);
        return textStyleMap.isEmpty() ?
            this.setAttributesEmptyTextStyleMap() :
            this.setAttributesNonEmptyTextStyleMap(textStyleMap);
    }

    /**
     * Factory called when no attributes.
     */
    abstract TextNode setAttributesEmptyTextStyleMap();

    /**
     * Factory that accepts a non empty {@link TextStyleNode} either wrapping or replacing (for {@link TextStyleNode}.
     */
    abstract TextNode setAttributesNonEmptyTextStyleMap(final TextNodeMap textStyleMap);

    /**
     * Would be setter that adds the given {@link TextStyle}.
     */
    public final TextNode setTextStyle(final TextStyle textStyle) {
        return this.setAttributes(
            Objects.requireNonNull(textStyle, "textStyle")
                .textStyleMap()
        );
    }

    /**
     * Getter that returns a {@link TextStyle} view over attributes.
     */
    @Override
    public abstract TextStyle textStyle();

    // Styleable........................................................................................................

    @Override
    public final TextNode merge(final TextStyle textStyle) {
        Objects.requireNonNull(textStyle, "textStyle");

        return this.setTextStyle(
            this.textStyle()
                .merge(textStyle)
        );
    }

    @Override
    public <T> TextNode set(final TextStylePropertyName<T> propertyName,
                            final T propertyValue) {
        return this.setTextStyle(
            this.textStyle()
                .set(
                    propertyName,
                    propertyValue
                )
        );
    }

    @Override
    public <T> TextNode setOrRemove(final TextStylePropertyName<T> propertyName,
                                    final T propertyValue) {
        return this.setTextStyle(
            this.textStyle()
                .setOrRemove(
                    propertyName,
                    propertyValue
                )
        );
    }

    @Override
    public TextNode remove(final TextStylePropertyName<?> propertyName) {
        return this.setTextStyle(
            this.textStyle()
                .remove(propertyName)
        );
    }

    // is...............................................................................................................

    /**
     * Only {@link Badge} returns true
     */
    public final boolean isBadge() {
        return this instanceof Badge;
    }

    /**
     * Only {@link Hyperlink} returns true
     */
    public final boolean isHyperlink() {
        return this instanceof Hyperlink;
    }

    /**
     * Only {@link TextPlaceholderNode} returns true
     */
    public final boolean isPlaceholder() {
        return this instanceof TextPlaceholderNode;
    }

    /**
     * Only {@link TextStyleNode} returns true
     */
    public final boolean isStyle() {
        return this instanceof TextStyleNode;
    }

    /**
     * Only {@link TextStyleNameNode} returns true
     */
    public final boolean isStyleName() {
        return this instanceof TextStyleNameNode;
    }

    /**
     * Only {@link Text} returns true
     */
    public final boolean isText() {
        return this instanceof Text;
    }

    // toHtml...........................................................................................................

    /**
     * Returns the HTML equivalent of this {@link TextNode}.
     */
    @Override
    public abstract String toHtml();

    /**
     * Internal method appends the HTML for this {@link TextNode}.
     */
    abstract boolean buildHtml(final boolean shouldIndent,
                               final IndentingPrinter html);

    // helper............................................................................................................

    /**
     * Helperful to assist casting, typically widening a {@link TextNode} to a sub class.
     */
    final <T extends TextNode> T cast() {
        return Cast.to(this);
    }

    // Object ..........................................................................................................

    @Override
    public abstract int hashCode();

    @Override
    public final boolean equals(final Object other) {
        return this == other ||
            null != other && this.getClass() == other.getClass() &&
                this.equals0(Cast.to(other));
    }

    abstract boolean equals0(final TextNode other);

    // TextNodeVisitor..................................................................................................

    abstract void accept(final TextNodeVisitor visitor);

    // JsonNodeMarshallContext.................................................................................................

    abstract JsonNode marshall(final JsonNodeMarshallContext context);

    // Object ..........................................................................................................

    @Override
    public final String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    // UsesToStringBuilder..............................................................................................

    @Override
    public final void buildToString(final ToStringBuilder b) {
        b.defaults();
        b.enable(ToStringBuilderOption.ESCAPE);
        b.disable(ToStringBuilderOption.SKIP_IF_DEFAULT_VALUE);
        b.labelSeparator(": ");
        b.valueSeparator(", ");
        b.separator("");

        this.buildToString0(b);
    }

    abstract void buildToString0(final ToStringBuilder b);

    // NodeSelector ....................................................................................................

    /**
     * {@see NodeSelector#absolute}
     */
    public static NodeSelector<TextNode, TextNodeName, TextStylePropertyName<?>, Object> absoluteNodeSelector() {
        return NodeSelector.absolute();
    }

    /**
     * {@see NodeSelector#relative}
     */
    public static NodeSelector<TextNode, TextNodeName, TextStylePropertyName<?>, Object> relativeNodeSelector() {
        return NodeSelector.relative();
    }

    /**
     * Creates a {@link NodeSelector} for {@link TextNode} from a {@link ExpressionNodeSelectorParserToken}.
     */
    public static NodeSelector<TextNode, TextNodeName, TextStylePropertyName<?>, Object> nodeSelectorExpressionParserToken(final ExpressionNodeSelectorParserToken token,
                                                                                                                           final Predicate<ExpressionFunctionName> functions) {
        return NodeSelector.parserToken(
            token,
            n -> TextNodeName.with(n.value()),
            functions,
            TextNode.class
        );
    }

    // JsonNode.........................................................................................................

    static {
        JsonNodeContext.register(
            "badge",
            Badge::unmarshallBadge,
            Badge::marshall,
            Badge.class
        );

        JsonNodeContext.register("hyperlink",
            Hyperlink::unmarshallHyperLink,
            Hyperlink::marshall,
            Hyperlink.class);

        JsonNodeContext.register("image",
            Image::unmarshallImage,
            Image::marshall,
            Image.class);

        JsonNodeContext.register("text",
            Text::unmarshallText,
            Text::marshall,
            Text.class);

        JsonNodeContext.register("text-placeholder",
            TextPlaceholderNode::unmarshallTextPlaceholderNode,
            TextPlaceholderNode::marshall,
            TextPlaceholderNode.class);

        JsonNodeContext.register("text-style-node",
            TextStyleNode::unmarshallTextStyleNode,
            TextStyleNode::marshall,
            TextStyleNode.class);

        JsonNodeContext.register("text-styleName",
            TextStyleNameNode::unmarshallTextStyleNameNode,
            TextStyleNameNode::marshall,
            TextStyleNameNode.class);
    }

    // HasTextNode......................................................................................................

    @Override
    public final TextNode toTextNode() {
        return this;
    }
}
