package com.f4pl0.pinnacle.portfolioservice.controller;

import com.f4pl0.pinnacle.portfolioservice.H2DatabaseTestConfig;
import com.f4pl0.pinnacle.portfolioservice.dto.AddStockAssetDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Date;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:19092", "port=19092"})
@ContextConfiguration(classes = {H2DatabaseTestConfig.class})
public class PortfolioControllerTest {

    private static WireMockServer wireMockServer;
    private static final RSAKey rsaJWK = generateJWK();
    private static final String userToken;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    static {
        try {
            userToken = generateToken(rsaJWK.toPrivateKey());
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    public static void setup() {
        RSAKey rsaPublicJWK = rsaJWK.toPublicJWK();

        // WireMock for Oauth2 endpoints
        wireMockServer = new WireMockServer(9090);
        wireMockServer.start();
        configureFor("localhost", wireMockServer.port());

        // Stub for /.well-known/jwks.json endpoint
        stubFor(get(urlEqualTo("/.well-known/jwks.json"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"keys\":[" + rsaPublicJWK.toJSONString() + "]}")));

        // Stub for /.well-known/openid-configuration endpoint
        stubFor(get(urlEqualTo("/.well-known/openid-configuration"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{"
                                + "\"issuer\":\"http://localhost:9090\","
                                + "\"authorization_endpoint\":\"http://localhost:9090/oauth2/authorize\","
                                + "\"token_endpoint\":\"http://localhost:9090/oauth2/token\","
                                + "\"userinfo_endpoint\":\"http://localhost:9090/userinfo\","
                                + "\"jwks_uri\":\"http://localhost:9090/.well-known/jwks.json\""
                                + "}")));
    }

    @AfterAll
    public static void teardown() {
        wireMockServer.stop();
    }

    @SneakyThrows
    private static RSAKey generateJWK() {
        return new RSAKeyGenerator(2048)
                .keyID("123456789")
                .generate();
    }

    @SneakyThrows
    private static String generateToken(PrivateKey privateKey) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 3600000); // 1 hour

        JWSSigner signer = new RSASSASigner(privateKey);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject("mock@user.com")
                .audience("pinnacle-oidc-client")
                .claim("scope", Arrays.asList("openid", "profile"))
                .issuer("http://localhost:9090")
                .issueTime(now)
                .expirationTime(expiryDate)
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("123456789").build(),
                claimsSet);

        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    @Test
    public void testAddPortfolioAsset() throws Exception {
        AddStockAssetDto addStockAssetDto = new AddStockAssetDto();
        addStockAssetDto.setSymbol("AAPL");
        addStockAssetDto.setQuantity(10);
        addStockAssetDto.setPrice(BigDecimal.valueOf(100.0));

        mockMvc.perform(post("/api/portfolio/asset/stock")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addStockAssetDto)))
                .andExpect(status().isCreated());
    }

    // TODO: Add tests for updating, and checking the values of the portfolio.
}