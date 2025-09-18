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
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelectorToken;
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
    default void testFormatWithNullValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createFormatter()
                .format(
                    null,
                    this.createContext()
                )
        );
    }

    @Test
    default void testFormatWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createFormatter()
                .format(
                    Optional.of(
                        this.value()
                    ),
                    null
                )
        );
    }

    // helper...........................................................................................................

    F createFormatter();

    Object value();

    SpreadsheetFormatterContext createContext();

    // format...........................................................................................................

    default void formatAndCheck(final Object value,
                                final String text) {
        this.formatAndCheck(
            Optional.of(value),
            text
        );
    }

    default void formatAndCheck(final Optional<Object> value,
                                final String text) {
        this.formatAndCheck(
            value,
            SpreadsheetText.with(text)
        );
    }

    default void formatAndCheck(final Object value,
                                final SpreadsheetText text) {
        this.formatAndCheck(
            Optional.of(value),
            text
        );
    }

    default void formatAndCheck(final Optional<Object> value,
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
            Optional.of(value),
            text
        );
    }

    default void formatAndCheck(final Optional<Object> value,
                                final TextNode text) {
        this.formatAndCheck(
            value,
            Optional.of(text)
        );
    }

    default void formatAndCheck(final Object value,
                                final Optional<TextNode> text) {
        this.formatAndCheck(
            Optional.of(value),
            text
        );
    }

    default void formatAndCheck(final Optional<Object> value,
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
            Optional.of(value),
            text
        );
    }

    default void formatAndCheck(final SpreadsheetFormatter formatter,
                                final Optional<Object> value,
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
            Optional.of(value),
            text
        );
    }

    default void formatAndCheck(final SpreadsheetFormatter formatter,
                                final Optional<Object> value,
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
            Optional.of(value),
            text
        );
    }

    default void formatAndCheck(final SpreadsheetFormatter formatter,
                                final Optional<Object> value,
                                final TextNode text) {
        this.formatAndCheck(
            formatter,
            value,
            Optional.of(text)
        );
    }

    default void formatAndCheck(final SpreadsheetFormatter formatter,
                                final Optional<Object> value,
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
            Optional.of(value)
        );
    }

    default void formatAndCheck(final Optional<Object> value) {
        this.formatAndCheck(
            value,
            this.createContext()
        );
    }

    default void formatAndCheck(final Object value,
                                final SpreadsheetFormatterContext context) {
        this.formatAndCheck(
            Optional.of(value),
            context
        );
    }

    default void formatAndCheck(final Optional<Object> value,
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
            Optional.of(value)
        );
    }

    default void formatAndCheck(final SpreadsheetFormatter formatter,
                                final Optional<Object> value) {
        this.formatAndCheck(
            formatter,
            value,
            this.createContext()
        );
    }

    // tokensAndCheck...........................................................................................

    @Test
    default void testTokenWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createFormatter().tokens(null)
        );
    }

    default void tokensAndCheck() {
        this.tokensAndCheck(
            this.createContext()
        );
    }

    default void tokensAndCheck(final SpreadsheetFormatterContext context,
                                final SpreadsheetFormatterSelectorToken... expected) {
        this.tokensAndCheck(
            context,
            Lists.of(expected)
        );
    }

    default void tokensAndCheck(final SpreadsheetFormatterContext context,
                                final List<SpreadsheetFormatterSelectorToken> expected) {
        this.tokensAndCheck(
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
