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

package walkingkooka.spreadsheet.meta.store;

import walkingkooka.datetime.HasNow;
import walkingkooka.environment.AuditInfo;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.store.Store;
import walkingkooka.store.Stores;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A {@link SpreadsheetMetadataStore} that uses a {@link Map}.
 */
final class TreeMapSpreadsheetMetadataStore implements SpreadsheetMetadataStore {

    /**
     * Factory that creates a new {@link TreeMapSpreadsheetMetadataStore}
     */
    static TreeMapSpreadsheetMetadataStore with(final SpreadsheetMetadata createTemplate,
                                                final HasNow now) {
        Objects.requireNonNull(createTemplate, "createTemplate");

        if (false == createTemplate.get(SpreadsheetMetadataPropertyName.LOCALE).isPresent()) {
            throw new IllegalArgumentException("Metadata missing: " + SpreadsheetMetadataPropertyName.LOCALE);
        }

        return new TreeMapSpreadsheetMetadataStore(
            createTemplate,
            Objects.requireNonNull(now, "now")
        );
    }

    /**
     * Private ctor.
     */
    private TreeMapSpreadsheetMetadataStore(final SpreadsheetMetadata createTemplate,
                                            final HasNow now) {
        super();

        this.store = Stores.treeMap(Comparator.naturalOrder(), TreeMapSpreadsheetMetadataStore::idSetter);
        this.createTemplate = createTemplate;
        this.now = now;

    }

    private static SpreadsheetMetadata idSetter(final SpreadsheetId id,
                                                final SpreadsheetMetadata spreadsheetMetadata) {
        return spreadsheetMetadata.set(SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
            SpreadsheetId.with(null == id ? 1 : id.value() + 1));
    }

    @Override
    public SpreadsheetMetadata create(final EmailAddress creator,
                                      final Optional<Locale> locale) {
        Objects.requireNonNull(creator, "creator");
        Objects.requireNonNull(locale, "locale");

        final LocalDateTime timestamp = this.now.now();

        SpreadsheetMetadata unsaved = this.createTemplate.set(
            SpreadsheetMetadataPropertyName.AUDIT_INFO,
            AuditInfo.with(
                creator,
                timestamp,
                creator,
                timestamp
            )
        );
        if (locale.isPresent()) {
            unsaved = unsaved.set(
                SpreadsheetMetadataPropertyName.LOCALE,
                locale.orElse(null)
            );
        }

        // assumes that the template has a default Locale in "defaults"
        return this.save(unsaved);
    }

    private final SpreadsheetMetadata createTemplate;

    @Override
    public Optional<SpreadsheetMetadata> load(final SpreadsheetId id) {
        return this.store.load(id);
    }

    @Override
    public SpreadsheetMetadata save(final SpreadsheetMetadata spreadsheetMetadata) {
        Objects.requireNonNull(spreadsheetMetadata, "spreadsheetMetadata");

        final Set<SpreadsheetMetadataPropertyName<?>> missing = spreadsheetMetadata.missingRequiredProperties();
        if (false == missing.isEmpty()) {
            throw new IllegalArgumentException(
                "Metadata missing required properties: " +
                    missing.stream()
                        .map(SpreadsheetMetadataPropertyName::toString)
                        .collect(Collectors.joining(", "))
            );
        }

        return this.store.save(spreadsheetMetadata);
    }

    @Override
    public Runnable addSaveWatcher(final Consumer<SpreadsheetMetadata> saved) {
        return this.store.addSaveWatcher(saved);
    }

    @Override
    public void delete(final SpreadsheetId id) {
        this.store.delete(id);
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<SpreadsheetId> deleted) {
        return this.store.addDeleteWatcher(deleted);
    }

    @Override
    public int count() {
        return this.store.count();
    }

    @Override
    public Set<SpreadsheetId> ids(final int offset,
                                  final int count) {
        return this.store.ids(
            offset,
            count
        );
    }

    @Override
    public List<SpreadsheetMetadata> values(final int offset,
                                            final int count) {
        return this.store.values(
            offset,
            count
        );
    }

    @Override
    public List<SpreadsheetMetadata> between(final SpreadsheetId from,
                                             final SpreadsheetId to) {
        return this.store.between(
            from,
            to
        );
    }

    private final Store<SpreadsheetId, SpreadsheetMetadata> store;

    /**
     * Used to provide the current time to timestamp operations.
     */
    private final HasNow now;

    @Override
    public String toString() {
        return this.store.toString();
    }
}
