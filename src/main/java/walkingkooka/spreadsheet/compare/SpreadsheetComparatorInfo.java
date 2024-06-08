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

package walkingkooka.spreadsheet.compare;

import walkingkooka.Cast;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.plugin.PluginInfoLike;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;

/**
 * Provides a few bits of info describing a {@link SpreadsheetComparator}. The {@link AbsoluteUrl} must be a unique identifier,
 * with the {@link SpreadsheetComparatorName} being a shorter human friendly reference.
 */
public final class SpreadsheetComparatorInfo implements PluginInfoLike<SpreadsheetComparatorInfo, SpreadsheetComparatorName> {

    public static SpreadsheetComparatorInfo parse(final String text) {
        return PluginInfoLike.parse(
                text,
                SpreadsheetComparatorName::with,
                SpreadsheetComparatorInfo::with
        );
    }

    public static SpreadsheetComparatorInfo with(final AbsoluteUrl url,
                                                 final SpreadsheetComparatorName name) {
        return new SpreadsheetComparatorInfo(
                Objects.requireNonNull(url, "url"),
                Objects.requireNonNull(name, "name")
        );
    }

    private SpreadsheetComparatorInfo(final AbsoluteUrl url,
                                      final SpreadsheetComparatorName name) {
        this.url = url;
        this.name = name;
    }

    @Override
    public AbsoluteUrl url() {
        return this.url;
    }

    private final AbsoluteUrl url;

    // HasName..........................................................................................................

    @Override
    public SpreadsheetComparatorName name() {
        return this.name;
    }

    private final SpreadsheetComparatorName name;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.url,
                this.name
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetComparatorInfo &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetComparatorInfo other) {
        return this.url.equals(other.url) &&
                this.name.equals(other.name);
    }

    @Override
    public String toString() {
        return PluginInfoLike.toString(this);
    }

    // Json.............................................................................................................

    static SpreadsheetComparatorInfo unmarshall(final JsonNode node,
                                                final JsonNodeUnmarshallContext context) {
        return PluginInfoLike.unmarshall(
                node,
                context,
                SpreadsheetComparatorName.class,
                SpreadsheetComparatorInfo::with
        );
    }

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetComparatorInfo.class),
                SpreadsheetComparatorInfo::unmarshall,
                SpreadsheetComparatorInfo::marshall,
                SpreadsheetComparatorInfo.class
        );
    }
}
