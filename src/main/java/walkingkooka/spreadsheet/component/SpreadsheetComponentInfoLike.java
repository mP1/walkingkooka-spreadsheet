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

package walkingkooka.spreadsheet.component;

import walkingkooka.naming.HasName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.HasAbsoluteUrl;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Captures the common members for a SpreadsheetComponent INFO.
 */
public interface SpreadsheetComponentInfoLike<I extends SpreadsheetComponentInfoLike<I, N>, N extends SpreadsheetComponentNameLike<N>> extends
        HasName<N>,
        HasAbsoluteUrl,
        Comparable<I>,
        HateosResource<N> {

    // Comparable.......................................................................................................

    @Override
    default int compareTo(final I other) {
        return this.name().compareTo(other.name());
    }

    // HateosResource...................................................................................................

    @Override
    default String hateosLinkId() {
        return this.name().value();
    }

    @Override
    default Optional<N> id() {
        return Optional.of(
                this.name()
        );
    }

    // json.............................................................................................................

    /**
     * Marshalls this {@link SpreadsheetComponentInfoLike} into a {@link JsonNode}.
     */
    default JsonNode marshall(final JsonNodeMarshallContext context) {
        Objects.requireNonNull(context, "context");

        return JsonNode.object()
                .set(
                        SpreadsheetComponentInfoLikeJsonConstants.URL_PROPERTY,
                        context.marshall(this.url())
                ).set(
                        SpreadsheetComponentInfoLikeJsonConstants.NAME_PROPERTY,
                        context.marshall(this.name())
                );
    }

    static <I extends SpreadsheetComponentInfoLike<I, N>, N extends SpreadsheetComponentNameLike<N>> I unmarshall(final JsonNode node,
                                                                                                                  final JsonNodeUnmarshallContext context,
                                                                                                                  final Class<N> nameType,
                                                                                                                  final BiFunction<AbsoluteUrl, N, I> factory) {
        Objects.requireNonNull(node, "node");
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(nameType, "nameType");
        Objects.requireNonNull(factory, "factory");

        AbsoluteUrl url = null;
        N name = null;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName jsonPropertyName = child.name();

            switch (jsonPropertyName.value()) {
                case SpreadsheetComponentInfoLikeJsonConstants.URL_PROPERTY_STRING:
                    url = context.unmarshall(
                            child,
                            AbsoluteUrl.class
                    );
                    break;
                case SpreadsheetComponentInfoLikeJsonConstants.NAME_PROPERTY_STRING:
                    name = context.unmarshall(
                            child,
                            nameType
                    );
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(
                            jsonPropertyName,
                            node
                    );
                    break;
            }
        }

        return factory.apply(
                url,
                name
        );
    }
}
