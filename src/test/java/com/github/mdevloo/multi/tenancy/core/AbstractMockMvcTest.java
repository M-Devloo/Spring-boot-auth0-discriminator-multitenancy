package com.github.mdevloo.multi.tenancy.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mdevloo.multi.tenancy.AbstractIntegrationTest;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.function.Function;

import static org.springframework.security.oauth2.jwt.JwtClaimNames.SUB;

@AutoConfigureMockMvc
@TestExecutionListeners(
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS,
    listeners = WithSecurityContextTestExecutionListener.class)
@Getter(value = AccessLevel.PROTECTED)
public class AbstractMockMvcTest extends AbstractIntegrationTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired private MockMvc mockMvc;
  @Autowired private ResourceLoader resourceLoader;

  @MockBean
  @SuppressWarnings("unused")
  private JwtDecoder jwtDecoder;

  protected final Jwt getJwtToken() {
    return this.getJwtToken("auth0|current");
  }

  protected final Jwt getJwtToken(final String sub) {
    return Jwt.withTokenValue("token")
        .header("alg", "none")
        .claim(SUB, sub)
        .claim("scope", "read")
        .build();
  }

  protected final String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected void assertResponse(final String responseContent, final String filePath) {
    this.assertResponse(responseContent, filePath, (a -> a), null);
  }

  protected void assertResponse(
      final String responseContent, final String filePath, final Collection<String> ignoreFields) {
    this.assertResponse(responseContent, filePath, (a -> a), ignoreFields);
  }

  private void assertResponse(
      final String responseContent,
      final String filePath,
      final Function<String, String> assertionTransformer,
      final Collection<String> ignoreFields) {
    try {
      final JsonNode jsonNode = this.objectMapper.readValue(responseContent, JsonNode.class);

      Assertions.assertThat(
              this.objectMapper.writeValueAsString(
                  this.objectMapper.readValue(
                      assertionTransformer.apply(this.readFile(filePath)), JsonNode.class)))
          .isEqualTo(this.objectMapper.writeValueAsString(this.getNode(jsonNode, ignoreFields)));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Object getNode(final JsonNode node, Collection<String> ignoreFields) {
    if (node instanceof ObjectNode) {
      final ObjectNode objectNode = (ObjectNode) node;
      if (CollectionUtils.isNotEmpty(ignoreFields)) {
        objectNode.remove(ignoreFields);
      }
      return objectNode;
    } else {
      return node;
    }
  }

  private String readFile(final String path) {
    try (final InputStream inputStream =
        this.resourceLoader.getResource("classpath:" + path).getInputStream()) {
      return IOUtils.toString(new InputStreamReader(inputStream));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
