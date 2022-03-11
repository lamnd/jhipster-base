package com.ippon.gateway.web.rest;

import com.ippon.gateway.domain.ApiKey;
import com.ippon.gateway.repository.ApiKeyRepository;
import com.ippon.gateway.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.ippon.gateway.domain.ApiKey}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ApiKeyResource {

    private final Logger log = LoggerFactory.getLogger(ApiKeyResource.class);

    private static final String ENTITY_NAME = "apiKey";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyResource(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    /**
     * {@code POST  /api-keys} : Create a new apiKey.
     *
     * @param apiKey the apiKey to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new apiKey, or with status {@code 400 (Bad Request)} if the apiKey has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/api-keys")
    public Mono<ResponseEntity<ApiKey>> createApiKey(@Valid @RequestBody ApiKey apiKey) throws URISyntaxException {
        log.debug("REST request to save ApiKey : {}", apiKey);
        if (apiKey.getId() != null) {
            throw new BadRequestAlertException("A new apiKey cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return apiKeyRepository
            .save(apiKey)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/api-keys/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /api-keys/:id} : Updates an existing apiKey.
     *
     * @param id the id of the apiKey to save.
     * @param apiKey the apiKey to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated apiKey,
     * or with status {@code 400 (Bad Request)} if the apiKey is not valid,
     * or with status {@code 500 (Internal Server Error)} if the apiKey couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/api-keys/{id}")
    public Mono<ResponseEntity<ApiKey>> updateApiKey(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ApiKey apiKey
    ) throws URISyntaxException {
        log.debug("REST request to update ApiKey : {}, {}", id, apiKey);
        if (apiKey.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, apiKey.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return apiKeyRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return apiKeyRepository
                        .save(apiKey)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            result ->
                                ResponseEntity
                                    .ok()
                                    .headers(
                                        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString())
                                    )
                                    .body(result)
                        );
                }
            );
    }

    /**
     * {@code PATCH  /api-keys/:id} : Partial updates given fields of an existing apiKey, field will ignore if it is null
     *
     * @param id the id of the apiKey to save.
     * @param apiKey the apiKey to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated apiKey,
     * or with status {@code 400 (Bad Request)} if the apiKey is not valid,
     * or with status {@code 404 (Not Found)} if the apiKey is not found,
     * or with status {@code 500 (Internal Server Error)} if the apiKey couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/api-keys/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<ApiKey>> partialUpdateApiKey(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ApiKey apiKey
    ) throws URISyntaxException {
        log.debug("REST request to partial update ApiKey partially : {}, {}", id, apiKey);
        if (apiKey.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, apiKey.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return apiKeyRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<ApiKey> result = apiKeyRepository
                        .findById(apiKey.getId())
                        .map(
                            existingApiKey -> {
                                if (apiKey.getDescription() != null) {
                                    existingApiKey.setDescription(apiKey.getDescription());
                                }
                                if (apiKey.getClientId() != null) {
                                    existingApiKey.setClientId(apiKey.getClientId());
                                }

                                return existingApiKey;
                            }
                        )
                        .flatMap(apiKeyRepository::save);

                    return result
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            res ->
                                ResponseEntity
                                    .ok()
                                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                                    .body(res)
                        );
                }
            );
    }

    /**
     * {@code GET  /api-keys} : get all the apiKeys.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of apiKeys in body.
     */
    @GetMapping("/api-keys")
    public Mono<List<ApiKey>> getAllApiKeys() {
        log.debug("REST request to get all ApiKeys");
        return apiKeyRepository.findAll().collectList();
    }

    /**
     * {@code GET  /api-keys} : get all the apiKeys as a stream.
     * @return the {@link Flux} of apiKeys.
     */
    @GetMapping(value = "/api-keys", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ApiKey> getAllApiKeysAsStream() {
        log.debug("REST request to get all ApiKeys as a stream");
        return apiKeyRepository.findAll();
    }

    /**
     * {@code GET  /api-keys/:id} : get the "id" apiKey.
     *
     * @param id the id of the apiKey to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the apiKey, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/api-keys/{id}")
    public Mono<ResponseEntity<ApiKey>> getApiKey(@PathVariable Long id) {
        log.debug("REST request to get ApiKey : {}", id);
        Mono<ApiKey> apiKey = apiKeyRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(apiKey);
    }

    /**
     * {@code DELETE  /api-keys/:id} : delete the "id" apiKey.
     *
     * @param id the id of the apiKey to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/api-keys/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteApiKey(@PathVariable Long id) {
        log.debug("REST request to delete ApiKey : {}", id);
        return apiKeyRepository
            .deleteById(id)
            .map(
                result ->
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
            );
    }
}
