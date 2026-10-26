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

package walkingkooka.spreadsheet.environment;

import walkingkooka.Cast;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.color.Color;
import walkingkooka.color.IndexedColor;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.spreadsheet.color.SpreadsheetColors;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataLoader;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.storage.SpreadsheetStorageContext;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StorageEnvironmentContext;
import walkingkooka.storage.StorageEnvironmentContextDelegator;
import walkingkooka.terminal.TerminalContext;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;

/**
 * A {@link SpreadsheetEnvironmentContext} that wraps a {@link StorageEnvironmentContext}, with guards to stop attempts to
 * set/remove {@link #SERVER_URL}.
 * <br>
 * Note if the given {@link StorageEnvironmentContext} is a {@link SpreadsheetEnvironmentContext} it is not wrapped and returned.
 */
final class SpreadsheetEnvironmentContextBasic implements SpreadsheetEnvironmentContext,
    StorageEnvironmentContextDelegator,
    TreePrintable {

    static SpreadsheetEnvironmentContextBasic with(final SpreadsheetMetadataLoader spreadsheetMetadataLoader,
                                                   final Storage<SpreadsheetStorageContext> storage,
                                                   final StorageEnvironmentContext storageEnvironmentContext) {
        Objects.requireNonNull(spreadsheetMetadataLoader, "spreadsheetMetadataLoader");
        Objects.requireNonNull(storage, "storage");
        Objects.requireNonNull(storageEnvironmentContext, "storageEnvironmentContext");

        SpreadsheetEnvironmentContextBasic spreadsheetEnvironmentContextBasic = null;

        if (storageEnvironmentContext instanceof SpreadsheetEnvironmentContextBasic) {
            spreadsheetEnvironmentContextBasic = (SpreadsheetEnvironmentContextBasic) storageEnvironmentContext;
            if (false == (spreadsheetEnvironmentContextBasic.spreadsheetMetadataLoader == spreadsheetMetadataLoader && spreadsheetEnvironmentContextBasic.storage == storage)) {
                spreadsheetEnvironmentContextBasic = null;
            }
        }

        return null != spreadsheetEnvironmentContextBasic ?
            spreadsheetEnvironmentContextBasic :
            new SpreadsheetEnvironmentContextBasic(
                spreadsheetMetadataLoader,
                storage,
                storageEnvironmentContext
            );
    }

    private SpreadsheetEnvironmentContextBasic(final SpreadsheetMetadataLoader spreadsheetMetadataLoader,
                                               final Storage<SpreadsheetStorageContext> storage,
                                               final StorageEnvironmentContext context) {
        super();

        this.spreadsheetMetadataLoader = spreadsheetMetadataLoader;
        this.storage = storage;
        this.context = context;
    }

    // SpreadsheetEnvironmentContext....................................................................................

    @Override
    public AbsoluteUrl serverUrl() {
        return this.environmentValueOrFail(SERVER_URL);
    }

    @Override
    public Optional<SpreadsheetId> spreadsheetId() {
        return this.environmentValue(SPREADSHEET_ID);
    }

    @Override
    public void setSpreadsheetId(final Optional<SpreadsheetId> spreadsheetId) {
        this.context.setOrRemoveEnvironmentValue(
            SPREADSHEET_ID,
            spreadsheetId
        );
    }

    @Override
    public Optional<SpreadsheetMetadata> spreadsheetMetadata() {
        return this.spreadsheetId()
            .flatMap(this.spreadsheetMetadataLoader::loadMetadata);
    }

    @Override
    public void setSpreadsheetMetadata(final Optional<SpreadsheetMetadata> spreadsheetMetadata) {
        Objects.requireNonNull(spreadsheetMetadata, "spreadsheetMetadata");

        throw SPREADSHEET_METADATA.readOnlyEnvironmentValueException();
    }

    @Override
    public Storage<SpreadsheetStorageContext> storage() {
        return this.storage;
    }

    private final Storage<SpreadsheetStorageContext> storage;

    // CanParseEnvironmentValueName.....................................................................................

    @Override
    public EnvironmentValueName<?> parseEnvironmentValueName(final String name) {
        Objects.requireNonNull(name, "name");

        // lazy load ENVIRONMENT_VALUE_NAMES
        if (null == STRING_TO_ENVIRONMENT_VALUE_NAMES) {
            final Map<String, EnvironmentValueName<?>> stringEnvironmentValueNames = Maps.sorted(EnvironmentValueName.CASE_SENSITIVITY.comparator());

            for (int i = SpreadsheetColors.MIN; i <= SpreadsheetColors.MAX; i++) {
                // populate color999 constants
                final SpreadsheetMetadataPropertyName<Color> color = SpreadsheetMetadataPropertyName.numberedColor(
                    IndexedColor.indexed(i)
                );
                stringEnvironmentValueNames.put(
                    color.value(),
                    color.toEnvironmentValueName()
                );
            }

            stringEnvironmentValueNames.put(
                SERVER_URL.value(),
                SERVER_URL
            );

            stringEnvironmentValueNames.put(
                SPREADSHEET_ID.value(),
                SPREADSHEET_ID
            );

            stringEnvironmentValueNames.put(
                SPREADSHEET_METADATA.value(),
                SPREADSHEET_METADATA
            );

            for (final SpreadsheetMetadataPropertyName<?> propertyName : SpreadsheetMetadataPropertyName.ALL) {
                final String propertyNameString = SPREADSHEET_METADATA_PREFIX.concat(
                    propertyName.value()
                );

                stringEnvironmentValueNames.put(
                    propertyNameString,
                    EnvironmentValueName.with(
                        propertyNameString,
                        propertyName.type()
                    )
                );
            }

            stringEnvironmentValueNames.put(
                TerminalContext.TERMINAL_ID.value(),
                TerminalContext.TERMINAL_ID
            );

            STRING_TO_ENVIRONMENT_VALUE_NAMES = stringEnvironmentValueNames;
        }

        // required because wrapped StorageEnvironmentContext may be missing terminalId
        EnvironmentValueName<?> environmentValueName = STRING_TO_ENVIRONMENT_VALUE_NAMES.get(name);
        if (null == environmentValueName) {
            if (EnvironmentValueName.CASE_SENSITIVITY.startsWith(
                name,
                COLOR_PREFIX
            )) {

                environmentValueName = EnvironmentValueName.with(
                    name,
                    Color.class
                );
            } else {
                environmentValueName = this.context.parseEnvironmentValueName(name);
            }
        }
        return environmentValueName;
    }

    private static Map<String, EnvironmentValueName<?>> STRING_TO_ENVIRONMENT_VALUE_NAMES;

    // EnvironmentContextDelegator......................................................................................

    @Override
    public SpreadsheetEnvironmentContext cloneEnvironment() {
        return this.setEnvironmentContext(
            this.context.cloneEnvironment()
        );
    }

    @Override
    public SpreadsheetEnvironmentContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        SpreadsheetEnvironmentContext spreadsheetEnvironmentContext;

        if (environmentContext instanceof SpreadsheetEnvironmentContext) {
            spreadsheetEnvironmentContext = (SpreadsheetEnvironmentContext) environmentContext;
        } else {
            final StorageEnvironmentContext before = this.context;
            final StorageEnvironmentContext after = before.setEnvironmentContext(environmentContext);

            spreadsheetEnvironmentContext = before == after ?
                this :
                SpreadsheetEnvironmentContextBasic.with(
                    this.spreadsheetMetadataLoader,
                    this.storage,
                    after
                );
        }

        return spreadsheetEnvironmentContext;
    }

    /**
     * If given a {@link #SPREADSHEET_METADATA}, the {@link SpreadsheetMetadataLoader} will be given the current {@link #SPREADSHEET_ID},
     * other {@link EnvironmentValueName} will be forwarded to the {@link #context}.
     */
    @Override
    public <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
        Objects.requireNonNull(name, "name");

        Optional<T> environmentValue;

        if (SPREADSHEET_METADATA.equals(name)) {
            environmentValue = Cast.to(
                this.loadSpreadsheetMetadata()
            );
        } else {
            final String environmentValueName = name.value();
            if (environmentValueName.startsWith(SPREADSHEET_METADATA_PREFIX)) {
                final SpreadsheetMetadataPropertyName<?> propertyName = SpreadsheetMetadataPropertyName.tryWith(
                    environmentValueName.substring(
                        SPREADSHEET_METADATA_PREFIX.length()
                    )
                ).orElse(null);

                if (null != propertyName) {
                    // TODO what should happen if SpreadsheetMetadataPropertyName does not match EnvironmentValueName ?
                    environmentValue = Cast.to(
                        this.loadSpreadsheetMetadata()
                            .flatMap(
                                (SpreadsheetMetadata spreadsheetMetadata) -> spreadsheetMetadata.get(propertyName)
                            )
                    );
                } else {
                    environmentValue = Optional.empty();
                }
            } else {
                environmentValue = this.context.environmentValue(name);
            }
        }

        return environmentValue;
    }

    private Optional<SpreadsheetMetadata> loadSpreadsheetMetadata() {
        return this.spreadsheetId()
            .flatMap(this.spreadsheetMetadataLoader::loadMetadata);
    }

    /**
     * Used to load the {@link walkingkooka.spreadsheet.meta.SpreadsheetMetadata} using the current {@link SpreadsheetId}.
     */
    private final SpreadsheetMetadataLoader spreadsheetMetadataLoader;

    @Override
    public <T> void setEnvironmentValue(final EnvironmentValueName<T> name,
                                        final T value) {
        modificationGuard(name);

        this.context.setEnvironmentValue(
            name,
            value
        );
    }

    @Override
    public void removeEnvironmentValue(final EnvironmentValueName<?> name) {
        modificationGuard(name);

        this.context.removeEnvironmentValue(name);
    }

    private static void modificationGuard(final EnvironmentValueName<?> name) {
        if (SERVER_URL.equals(name) || SPREADSHEET_METADATA.equals(name) || name.startsWith(SPREADSHEET_METADATA_PREFIX)) {
            throw name.readOnlyEnvironmentValueException();
        }
    }

    /**
     * Combines the names from the wrapped {@link StorageEnvironmentContext} and adds {@link #SPREADSHEET_ID} and
     * {@link #SPREADSHEET_METADATA}. The names are not cached because the wrapped environment may change at any moment.
     */
    @Override
    public Set<EnvironmentValueName<?>> environmentValueNames() {
        final SortedSet<EnvironmentValueName<?>> names = SortedSets.tree();

        names.addAll(this.storageEnvironmentContext()
            .environmentValueNames());
        names.add(SPREADSHEET_ID);
        names.add(SPREADSHEET_METADATA);

        return SortedSets.immutable(names);
    }

    @Override
    public StorageEnvironmentContext storageEnvironmentContext() {
        return this.context;
    }

    private final StorageEnvironmentContext context;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.spreadsheetMetadataLoader,
            this.storage,
            this.context
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof SpreadsheetEnvironmentContextBasic &&
                this.equals0((SpreadsheetEnvironmentContextBasic) other));
    }

    private boolean equals0(final SpreadsheetEnvironmentContextBasic other) {
        return
            this.spreadsheetMetadataLoader.equals(other.spreadsheetMetadataLoader) &&
            this.storage.equals(other.storage) &&
            this.context.equals(other.context);
    }

    @Override
    public String toString() {
        return this.context.toString();
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());
        printer.indent();
        {
            printer.println("environment");
            printer.indent();
            {
                TreePrintable.printTreeOrToString(
                    this.context,
                    printer
                );
            }
            printer.outdent();

            printer.println("spreadsheetMetadataLoader");
            printer.indent();
            {
                TreePrintable.printTreeOrToString(
                    this.spreadsheetMetadataLoader,
                    printer
                );
            }
            printer.outdent();

            printer.println("storage");
            printer.indent();
            {
                TreePrintable.printTreeOrToString(
                    this.storage,
                    printer
                );
            }
            printer.outdent();
        }
        printer.outdent();
    }
}
