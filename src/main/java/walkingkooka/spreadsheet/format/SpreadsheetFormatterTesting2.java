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

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.TypeNameTesting;
import walkingkooka.tree.text.TextNode;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Mixin interface with default methods to assist testing of an {@link SpreadsheetFormatter}.
 */
public interface SpreadsheetFormatterTesting2<F extends SpreadsheetFormatter>
        extends SpreadsheetFormatterTesting,
        ToStringTesting<F>,
        TypeNameTesting<F> {

    // format...........................................................................................................

    @Test
    default void testFormatNullValueFails() {
        assertThrows(NullPointerException.class, () -> this.createFormatter().format(null, this.createContext()));
    }

    @Test
    default void testFormatNullContextFails() {
        assertThrows(NullPointerException.class, () -> this.createFormatter().format(this.value(), null));
    }

    // helper...........................................................................................................

    F createFormatter();

    Object value();

    SpreadsheetFormatterContext createContext();

    // format...........................................................................................................

    default void formatAndCheck(final Object value,
                                final String text) {
        this.formatAndCheck(
                value,
                SpreadsheetText.with(text)
        );
    }

    default void formatAndCheck(final Object value,
                                final SpreadsheetText text) {
        this.formatAndCheck(
                this.createFormatter(),
                value,
                text.toTextNode()
        );
    }

    default void formatAndCheck(final Object value,
                                final TextNode text) {
        this.formatAndCheck(
                value,
                Optional.of(text)
        );
    }

    default void formatAndCheck(final Object value,
                                final Optional<TextNode> text) {
        this.formatAndCheck(
                this.createFormatter(),
                value,
                text
        );
    }

    default void formatAndCheck(final SpreadsheetFormatter formatter,
                                final Object value,
                                final String text) {
        this.formatAndCheck(
                formatter,
                value,
                SpreadsheetText.with(text)
        );
    }

    default void formatAndCheck(final SpreadsheetFormatter formatter,
                                final Object value,
                                final SpreadsheetText text) {
        this.formatAndCheck(
                formatter,
                value,
                text.toTextNode()
        );
    }

    default void formatAndCheck(final SpreadsheetFormatter formatter,
                                final Object value,
                                final TextNode text) {
        this.formatAndCheck(
                formatter,
                value,
                Optional.of(text)
        );
    }

    default void formatAndCheck(final SpreadsheetFormatter formatter,
                                final Object value,
                                final Optional<TextNode> text) {
        this.formatAndCheck(
                formatter,
                value,
                this.createContext(),
                text
        );
    }

    // format fail and check

    default void formatAndCheck(final Object value) {
        this.formatAndCheck(
                value,
                this.createContext()
        );
    }

    default void formatAndCheck(final Object value,
                                final SpreadsheetFormatterContext context) {
        this.formatAndCheck(
                this.createFormatter(),
                value,
                context
        );
    }

    default void formatAndCheck(final SpreadsheetFormatter formatter,
                                final Object value) {
        this.formatAndCheck(
                formatter,
                value,
                this.createContext()
        );
    }

    // textComponentsAndCheck...........................................................................................

    default void textComponentsAndCheck(final SpreadsheetFormatterContext context) {
        this.textComponentsAndCheck(
                context,
                Optional.empty()
        );
    }

    default void textComponentsAndCheck(final SpreadsheetFormatterContext context,
                                        final SpreadsheetFormatterSelectorTextComponent... expected) {
        this.textComponentsAndCheck(
                context,
                Lists.of(expected)
        );
    }

    default void textComponentsAndCheck(final SpreadsheetFormatterContext context,
                                        final List<SpreadsheetFormatterSelectorTextComponent> expected) {
        this.textComponentsAndCheck(
                context,
                Optional.of(expected)
        );
    }

    default void textComponentsAndCheck(final SpreadsheetFormatterContext context,
                                        final Optional<List<SpreadsheetFormatterSelectorTextComponent>> expected) {
        this.textComponentsAndCheck(
                this.createFormatter(),
                context,
                expected
        );
    }

    // TypeNameTesting .................................................................................................

    @Override
    default String typeNamePrefix() {
        return "";
    }

    @Override
    default String typeNameSuffix() {
        return SpreadsheetFormatter.class.getSimpleName();
    }
}
