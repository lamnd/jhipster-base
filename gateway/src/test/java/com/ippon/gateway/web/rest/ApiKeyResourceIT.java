package com.ippon.gateway.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.ippon.gateway.IntegrationTest;
import com.ippon.gateway.domain.ApiKey;
import com.ippon.gateway.repository.ApiKeyRepository;
import com.ippon.gateway.service.EntityManager;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link ApiKeyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class ApiKeyResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_CLIENT_ID = "AAAAAAAAAA";
    private static final String UPDATED_CLIENT_ID = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/api-keys";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ApiKey apiKey;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ApiKey createEntity(EntityManager em) {
        ApiKey apiKey = new ApiKey().description(DEFAULT_DESCRIPTION).clientId(DEFAULT_CLIENT_ID);
        return apiKey;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ApiKey createUpdatedEntity(EntityManager em) {
        ApiKey apiKey = new ApiKey().description(UPDATED_DESCRIPTION).clientId(UPDATED_CLIENT_ID);
        return apiKey;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ApiKey.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        apiKey = createEntity(em);
    }

    @Test
    void createApiKey() throws Exception {
        int databaseSizeBeforeCreate = apiKeyRepository.findAll().collectList().block().size();
        // Create the ApiKey
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(apiKey))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ApiKey in the database
        List<ApiKey> apiKeyList = apiKeyRepository.findAll().collectList().block();
        assertThat(apiKeyList).hasSize(databaseSizeBeforeCreate + 1);
        ApiKey testApiKey = apiKeyList.get(apiKeyList.size() - 1);
        assertThat(testApiKey.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testApiKey.getClientId()).isEqualTo(DEFAULT_CLIENT_ID);
    }

    @Test
    void createApiKeyWithExistingId() throws Exception {
        // Create the ApiKey with an existing ID
        apiKey.setId(1L);

        int databaseSizeBeforeCreate = apiKeyRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(apiKey))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ApiKey in the database
        List<ApiKey> apiKeyList = apiKeyRepository.findAll().collectList().block();
        assertThat(apiKeyList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkClientIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = apiKeyRepository.findAll().collectList().block().size();
        // set the field null
        apiKey.setClientId(null);

        // Create the ApiKey, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(apiKey))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ApiKey> apiKeyList = apiKeyRepository.findAll().collectList().block();
        assertThat(apiKeyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllApiKeysAsStream() {
        // Initialize the database
        apiKeyRepository.save(apiKey).block();

        List<ApiKey> apiKeyList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ApiKey.class)
            .getResponseBody()
            .filter(apiKey::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(apiKeyList).isNotNull();
        assertThat(apiKeyList).hasSize(1);
        ApiKey testApiKey = apiKeyList.get(0);
        assertThat(testApiKey.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testApiKey.getClientId()).isEqualTo(DEFAULT_CLIENT_ID);
    }

    @Test
    void getAllApiKeys() {
        // Initialize the database
        apiKeyRepository.save(apiKey).block();

        // Get all the apiKeyList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(apiKey.getId().intValue()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].clientId")
            .value(hasItem(DEFAULT_CLIENT_ID));
    }

    @Test
    void getApiKey() {
        // Initialize the database
        apiKeyRepository.save(apiKey).block();

        // Get the apiKey
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, apiKey.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(apiKey.getId().intValue()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.clientId")
            .value(is(DEFAULT_CLIENT_ID));
    }

    @Test
    void getNonExistingApiKey() {
        // Get the apiKey
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewApiKey() throws Exception {
        // Initialize the database
        apiKeyRepository.save(apiKey).block();

        int databaseSizeBeforeUpdate = apiKeyRepository.findAll().collectList().block().size();

        // Update the apiKey
        ApiKey updatedApiKey = apiKeyRepository.findById(apiKey.getId()).block();
        updatedApiKey.description(UPDATED_DESCRIPTION).clientId(UPDATED_CLIENT_ID);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedApiKey.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedApiKey))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ApiKey in the database
        List<ApiKey> apiKeyList = apiKeyRepository.findAll().collectList().block();
        assertThat(apiKeyList).hasSize(databaseSizeBeforeUpdate);
        ApiKey testApiKey = apiKeyList.get(apiKeyList.size() - 1);
        assertThat(testApiKey.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testApiKey.getClientId()).isEqualTo(UPDATED_CLIENT_ID);
    }

    @Test
    void putNonExistingApiKey() throws Exception {
        int databaseSizeBeforeUpdate = apiKeyRepository.findAll().collectList().block().size();
        apiKey.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, apiKey.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(apiKey))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ApiKey in the database
        List<ApiKey> apiKeyList = apiKeyRepository.findAll().collectList().block();
        assertThat(apiKeyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchApiKey() throws Exception {
        int databaseSizeBeforeUpdate = apiKeyRepository.findAll().collectList().block().size();
        apiKey.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(apiKey))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ApiKey in the database
        List<ApiKey> apiKeyList = apiKeyRepository.findAll().collectList().block();
        assertThat(apiKeyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamApiKey() throws Exception {
        int databaseSizeBeforeUpdate = apiKeyRepository.findAll().collectList().block().size();
        apiKey.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(apiKey))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ApiKey in the database
        List<ApiKey> apiKeyList = apiKeyRepository.findAll().collectList().block();
        assertThat(apiKeyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateApiKeyWithPatch() throws Exception {
        // Initialize the database
        apiKeyRepository.save(apiKey).block();

        int databaseSizeBeforeUpdate = apiKeyRepository.findAll().collectList().block().size();

        // Update the apiKey using partial update
        ApiKey partialUpdatedApiKey = new ApiKey();
        partialUpdatedApiKey.setId(apiKey.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedApiKey.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedApiKey))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ApiKey in the database
        List<ApiKey> apiKeyList = apiKeyRepository.findAll().collectList().block();
        assertThat(apiKeyList).hasSize(databaseSizeBeforeUpdate);
        ApiKey testApiKey = apiKeyList.get(apiKeyList.size() - 1);
        assertThat(testApiKey.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testApiKey.getClientId()).isEqualTo(DEFAULT_CLIENT_ID);
    }

    @Test
    void fullUpdateApiKeyWithPatch() throws Exception {
        // Initialize the database
        apiKeyRepository.save(apiKey).block();

        int databaseSizeBeforeUpdate = apiKeyRepository.findAll().collectList().block().size();

        // Update the apiKey using partial update
        ApiKey partialUpdatedApiKey = new ApiKey();
        partialUpdatedApiKey.setId(apiKey.getId());

        partialUpdatedApiKey.description(UPDATED_DESCRIPTION).clientId(UPDATED_CLIENT_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedApiKey.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedApiKey))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ApiKey in the database
        List<ApiKey> apiKeyList = apiKeyRepository.findAll().collectList().block();
        assertThat(apiKeyList).hasSize(databaseSizeBeforeUpdate);
        ApiKey testApiKey = apiKeyList.get(apiKeyList.size() - 1);
        assertThat(testApiKey.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testApiKey.getClientId()).isEqualTo(UPDATED_CLIENT_ID);
    }

    @Test
    void patchNonExistingApiKey() throws Exception {
        int databaseSizeBeforeUpdate = apiKeyRepository.findAll().collectList().block().size();
        apiKey.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, apiKey.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(apiKey))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ApiKey in the database
        List<ApiKey> apiKeyList = apiKeyRepository.findAll().collectList().block();
        assertThat(apiKeyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchApiKey() throws Exception {
        int databaseSizeBeforeUpdate = apiKeyRepository.findAll().collectList().block().size();
        apiKey.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(apiKey))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ApiKey in the database
        List<ApiKey> apiKeyList = apiKeyRepository.findAll().collectList().block();
        assertThat(apiKeyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamApiKey() throws Exception {
        int databaseSizeBeforeUpdate = apiKeyRepository.findAll().collectList().block().size();
        apiKey.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(apiKey))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ApiKey in the database
        List<ApiKey> apiKeyList = apiKeyRepository.findAll().collectList().block();
        assertThat(apiKeyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteApiKey() {
        // Initialize the database
        apiKeyRepository.save(apiKey).block();

        int databaseSizeBeforeDelete = apiKeyRepository.findAll().collectList().block().size();

        // Delete the apiKey
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, apiKey.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ApiKey> apiKeyList = apiKeyRepository.findAll().collectList().block();
        assertThat(apiKeyList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
