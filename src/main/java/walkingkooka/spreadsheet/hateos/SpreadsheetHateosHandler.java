package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.net.http.server.hateos.HateosHandlerContext;
import walkingkooka.net.http.server.hateos.HateosResourceName;
import walkingkooka.tree.Node;

import java.util.Objects;

/**
 * Base class for all handlers.
 */
abstract class SpreadsheetHateosHandler<K extends Comparable<K>, V, N extends Node<N, ?, ?, ?>> {

    static <K extends Comparable<K>, V, N extends Node<N, ?, ?, ?>> void check(final HateosContentType<N, V> contentType) {
        Objects.requireNonNull(contentType, "contentType");
    }

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetHateosHandler(final HateosContentType<N, V> contentType) {
        super();
        this.contentType = contentType;
    }

    /**
     * Converts the value into a {@link Node}.
     */
    final N toNode(final V value) {
        return this.contentType.toNode(value);
    }

    /**
     * Converts the value into a {@link Node}, and then adds links.
     */
    final N addLinks(final K id, final V value, final HateosHandlerContext<N> context) {
        return context.addLinks(this.resourceName(), id, this.toNode(value));
    }

    final HateosContentType<N, V> contentType;

    abstract HateosResourceName resourceName();
}
