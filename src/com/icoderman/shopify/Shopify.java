package com.icoderman.shopify;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.client.utils.URIBuilder;

/**
 * Main interface for Woocommerce REST API
 */
public interface Shopify {

    /**
     * Creates WooCommerce entity
     *
     * @param endpointBase API endpoint base @see EndpointBaseType
     * @param object       Map with entity properties and values
     * @return Map with created entity
     */
    Map<?, ?> create(String endpointBase, Map<String, Object> object);

    /**
     * Retrieves on WooCommerce entity
     *
     * @param endpointBase API endpoint base @see EndpointBaseType
     * @param id           id of WooCommerce entity
     * @return Retrieved WooCommerce entity
     */
    Map<?, ?> get(String endpointBase, String id);

    /**
     * Retrieves all WooCommerce entities with request parameters
     *
     * @param endpointBase API endpoint base @see EndpointBaseType
     * @param params additional request params
     * @return List of retrieved entities
     */
    LinkedHashMap<?, ?> getAll(String endpointBase, Map<String, String> params);

    /**
     * Retrieves all WooCommerce entities
     *
     * @param endpointBase API endpoint base @see EndpointBaseType
     * @return List of retrieved entities
     */
    default LinkedHashMap<?, ?> getAll(String endpointBase) {
        return getAll(endpointBase, Collections.emptyMap());
    }

    LinkedHashMap<?, ?> getAll(URIBuilder builder);
    
    /**
     * Updates WooCommerce entity
     *
     * @param endpointBase API endpoint base @see EndpointBaseType
     * @param id           id of the entity to update
     * @param object       Map with updated properties
     * @return updated WooCommerce entity
     */
    Map<?, ?> update(String endpointBase, int id, Map<String, Object> object);

    /**
     * Deletes WooCommerce entity
     *
     * @param endpointBase API endpoint base @see EndpointBaseType
     * @param id           id of the entity to update
     * @return deleted WooCommerce entity
     */
    Map<?, ?> delete(String endpointBase, int id);

    /**
     * Makes batch operations on WooCommerce entities
     *
     * @param endpointBase API endpoint base @see EndpointBaseType
     * @param object       Map with lists of entities
     * @return response Map with WooCommerce entities implicated
     */
    Map<?, ?> batch(String endpointBase, Map<String, Object> object);

}
