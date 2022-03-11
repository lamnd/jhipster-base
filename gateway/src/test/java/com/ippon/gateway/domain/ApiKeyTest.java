package com.ippon.gateway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ippon.gateway.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ApiKeyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ApiKey.class);
        ApiKey apiKey1 = new ApiKey();
        apiKey1.setId(1L);
        ApiKey apiKey2 = new ApiKey();
        apiKey2.setId(apiKey1.getId());
        assertThat(apiKey1).isEqualTo(apiKey2);
        apiKey2.setId(2L);
        assertThat(apiKey1).isNotEqualTo(apiKey2);
        apiKey1.setId(null);
        assertThat(apiKey1).isNotEqualTo(apiKey2);
    }
}
