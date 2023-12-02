package com.f4pl0.pinnacle.portfolioservice.controller;

import com.f4pl0.pinnacle.portfolioservice.H2DatabaseTestConfig;
import com.f4pl0.pinnacle.portfolioservice.dto.stock.AddStockAssetDto;
import com.f4pl0.pinnacle.portfolioservice.dto.stock.StockAssetResponseDto;
import com.f4pl0.pinnacle.portfolioservice.dto.stock.UpdateStockAssetDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:19092", "port=19092"})
@ContextConfiguration(classes = {H2DatabaseTestConfig.class, PortfolioControllerTestConfig.class})
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

        String json = objectMapper.writeValueAsString(addStockAssetDto);

       MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/portfolio/asset/stock")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(json)
                       .header("Authorization", "Bearer " + userToken))
               .andReturn();

       MockHttpServletResponse response = result.getResponse();
       String contentAsString = response.getContentAsString();
       StockAssetResponseDto returnedAsset = objectMapper.readValue(contentAsString, StockAssetResponseDto.class);

       assertEquals(HttpStatus.CREATED.value(), response.getStatus());
       assertEquals(addStockAssetDto.getSymbol(), returnedAsset.getSymbol());
       assertEquals(addStockAssetDto.getQuantity(), returnedAsset.getQuantity());
       assertEquals(addStockAssetDto.getPrice().doubleValue(), returnedAsset.getPrice().doubleValue());
    }

    @Test
    public void testAddPortfolioAssetInvalidSymbol() throws Exception {
        AddStockAssetDto addStockAssetDto = new AddStockAssetDto();
        addStockAssetDto.setSymbol("INVALID");
        addStockAssetDto.setQuantity(10);
        addStockAssetDto.setPrice(BigDecimal.valueOf(100.0));

        String json = objectMapper.writeValueAsString(addStockAssetDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/portfolio/asset/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddPortfolioAssetNegativeQuantity() throws Exception {
        AddStockAssetDto addStockAssetDto = new AddStockAssetDto();
        addStockAssetDto.setSymbol("AAPL");
        addStockAssetDto.setQuantity(-10);
        addStockAssetDto.setPrice(BigDecimal.valueOf(100.0));

        String json = objectMapper.writeValueAsString(addStockAssetDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/portfolio/asset/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddPortfolioAssetZeroPrice() throws Exception {
        AddStockAssetDto addStockAssetDto = new AddStockAssetDto();
        addStockAssetDto.setSymbol("AAPL");
        addStockAssetDto.setQuantity(10);
        addStockAssetDto.setPrice(BigDecimal.valueOf(0.0));

        String json = objectMapper.writeValueAsString(addStockAssetDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/portfolio/asset/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddPortfolioAssetEmptySymbol() throws Exception {
        AddStockAssetDto addStockAssetDto = new AddStockAssetDto();
        addStockAssetDto.setSymbol("");
        addStockAssetDto.setQuantity(10);
        addStockAssetDto.setPrice(BigDecimal.valueOf(100.0));

        String json = objectMapper.writeValueAsString(addStockAssetDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/portfolio/asset/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddPortfolioAssetNullPrice() throws Exception {
        AddStockAssetDto addStockAssetDto = new AddStockAssetDto();
        addStockAssetDto.setSymbol("AAPL");
        addStockAssetDto.setQuantity(10);
        addStockAssetDto.setPrice(null);

        String json = objectMapper.writeValueAsString(addStockAssetDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/portfolio/asset/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllStockAssetsTest() throws Exception {
        AddStockAssetDto asset1 = new AddStockAssetDto();
        asset1.setSymbol("AAPL");
        asset1.setQuantity(10);
        asset1.setPrice(BigDecimal.valueOf(100.0));

        AddStockAssetDto asset2 = new AddStockAssetDto();
        asset2.setSymbol("GOOG");
        asset2.setQuantity(5);
        asset2.setPrice(BigDecimal.valueOf(200.0));

        // Add the assets
        mockMvc.perform(MockMvcRequestBuilders.post("/api/portfolio/asset/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(asset1))
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/portfolio/asset/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(asset2))
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isCreated());

        // Retrieve and validate the assets
        mockMvc.perform(MockMvcRequestBuilders.get("/api/portfolio/asset/stock")
                        .header("Authorization", "Bearer " + userToken))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].symbol", is("AAPL")))
                .andExpect(jsonPath("$[0].quantity", is(10)))
                .andExpect(jsonPath("$[0].price", is(100.0)))
                .andExpect(jsonPath("$[1].symbol", is("GOOG")))
                .andExpect(jsonPath("$[1].quantity", is(5)))
                .andExpect(jsonPath("$[1].price", is(200.0)));
    }

    @Test
    public void testGetNonExistingPortfolioAsset() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/portfolio/asset/stock/{assetId}", UUID.randomUUID())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }

   @Test
    public void testCreateAndUpdatePortfolioAsset() throws Exception {
        // Create a new stock asset
        AddStockAssetDto addStockAssetDto = new AddStockAssetDto();
        addStockAssetDto.setSymbol("AAPL");
        addStockAssetDto.setQuantity(10);
        addStockAssetDto.setPrice(BigDecimal.valueOf(100.0));

        String jsonAdd = objectMapper.writeValueAsString(addStockAssetDto);

        MvcResult resultAdd = mockMvc.perform(MockMvcRequestBuilders.post("/api/portfolio/asset/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAdd)
                        .header("Authorization", "Bearer " + userToken))
                .andReturn();

        MockHttpServletResponse responseAdd = resultAdd.getResponse();
        String contentAsStringAdd = responseAdd.getContentAsString();
        StockAssetResponseDto returnedAssetAdd = objectMapper.readValue(contentAsStringAdd, StockAssetResponseDto.class);

        assertEquals(HttpStatus.CREATED.value(), responseAdd.getStatus());
        assertEquals(addStockAssetDto.getSymbol(), returnedAssetAdd.getSymbol());
        assertEquals(addStockAssetDto.getQuantity(), returnedAssetAdd.getQuantity());
        assertEquals(addStockAssetDto.getPrice().doubleValue(), returnedAssetAdd.getPrice().doubleValue(), 0.01);

        // Update the created stock asset
        UpdateStockAssetDto updateStockAssetDto = new UpdateStockAssetDto();
        updateStockAssetDto.setQuantity(15);
        updateStockAssetDto.setPrice(BigDecimal.valueOf(150.0));

        String jsonUpdate = objectMapper.writeValueAsString(updateStockAssetDto);

        MvcResult resultUpdate = mockMvc.perform(MockMvcRequestBuilders.put("/api/portfolio/asset/stock/{assetId}", returnedAssetAdd.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUpdate)
                        .header("Authorization", "Bearer " + userToken))
                .andReturn();

        MockHttpServletResponse responseUpdate = resultUpdate.getResponse();
        String contentAsStringUpdate = responseUpdate.getContentAsString();
        StockAssetResponseDto returnedAssetUpdate = objectMapper.readValue(contentAsStringUpdate, StockAssetResponseDto.class);

        assertEquals(HttpStatus.OK.value(), responseUpdate.getStatus());
        assertEquals(updateStockAssetDto.getQuantity(), returnedAssetUpdate.getQuantity());
        assertEquals(updateStockAssetDto.getPrice().doubleValue(), returnedAssetUpdate.getPrice().doubleValue(), 0.01);
    }

    @Test
    public void testUpdateNonExistingPortfolioAsset() throws Exception {
        UpdateStockAssetDto updateStockAssetDto = new UpdateStockAssetDto();
        updateStockAssetDto.setQuantity(15);
        updateStockAssetDto.setPrice(BigDecimal.valueOf(150.0));

        String jsonUpdate = objectMapper.writeValueAsString(updateStockAssetDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/portfolio/asset/stock/{assetId}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUpdate)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdatePortfolioAssetNegativeQuantity() throws Exception {
        // Create a new stock asset
        AddStockAssetDto addStockAssetDto = new AddStockAssetDto();
        addStockAssetDto.setSymbol("AAPL");
        addStockAssetDto.setQuantity(10);
        addStockAssetDto.setPrice(BigDecimal.valueOf(100.0));

        String jsonAdd = objectMapper.writeValueAsString(addStockAssetDto);

        MvcResult resultAdd = mockMvc.perform(MockMvcRequestBuilders.post("/api/portfolio/asset/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAdd)
                        .header("Authorization", "Bearer " + userToken))
                .andReturn();

        MockHttpServletResponse responseAdd = resultAdd.getResponse();
        String contentAsStringAdd = responseAdd.getContentAsString();
        StockAssetResponseDto returnedAssetAdd = objectMapper.readValue(contentAsStringAdd, StockAssetResponseDto.class);

        // Update the created stock asset with negative quantity
        UpdateStockAssetDto updateStockAssetDto = new UpdateStockAssetDto();
        updateStockAssetDto.setQuantity(-15);
        updateStockAssetDto.setPrice(BigDecimal.valueOf(150.0));

        String jsonUpdate = objectMapper.writeValueAsString(updateStockAssetDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/portfolio/asset/stock/{assetId}", returnedAssetAdd.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUpdate)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdatePortfolioAssetZeroPrice() throws Exception {
        // Create a new stock asset
        AddStockAssetDto addStockAssetDto = new AddStockAssetDto();
        addStockAssetDto.setSymbol("AAPL");
        addStockAssetDto.setQuantity(10);
        addStockAssetDto.setPrice(BigDecimal.valueOf(100.0));

        String jsonAdd = objectMapper.writeValueAsString(addStockAssetDto);

        MvcResult resultAdd = mockMvc.perform(MockMvcRequestBuilders.post("/api/portfolio/asset/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAdd)
                        .header("Authorization", "Bearer " + userToken))
                .andReturn();

        MockHttpServletResponse responseAdd = resultAdd.getResponse();
        String contentAsStringAdd = responseAdd.getContentAsString();
        StockAssetResponseDto returnedAssetAdd = objectMapper.readValue(contentAsStringAdd, StockAssetResponseDto.class);

        // Update the created stock asset with zero price
        UpdateStockAssetDto updateStockAssetDto = new UpdateStockAssetDto();
        updateStockAssetDto.setQuantity(15);
        updateStockAssetDto.setPrice(BigDecimal.valueOf(0.0));

        String jsonUpdate = objectMapper.writeValueAsString(updateStockAssetDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/portfolio/asset/stock/{assetId}", returnedAssetAdd.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUpdate)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdatePortfolioAssetNullPrice() throws Exception {
        // Create a new stock asset
        AddStockAssetDto addStockAssetDto = new AddStockAssetDto();
        addStockAssetDto.setSymbol("AAPL");
        addStockAssetDto.setQuantity(10);
        addStockAssetDto.setPrice(BigDecimal.valueOf(100.0));

        String jsonAdd = objectMapper.writeValueAsString(addStockAssetDto);

        MvcResult resultAdd = mockMvc.perform(MockMvcRequestBuilders.post("/api/portfolio/asset/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAdd)
                        .header("Authorization", "Bearer " + userToken))
                .andReturn();

        MockHttpServletResponse responseAdd = resultAdd.getResponse();
        String contentAsStringAdd = responseAdd.getContentAsString();
        StockAssetResponseDto returnedAssetAdd = objectMapper.readValue(contentAsStringAdd, StockAssetResponseDto.class);

        // Update the created stock asset with null price
        UpdateStockAssetDto updateStockAssetDto = new UpdateStockAssetDto();
        updateStockAssetDto.setQuantity(15);
        updateStockAssetDto.setPrice(null);

        String jsonUpdate = objectMapper.writeValueAsString(updateStockAssetDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/portfolio/asset/stock/{assetId}", returnedAssetAdd.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUpdate)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeletePortfolioAsset() throws Exception {
        // Create a new stock asset
        AddStockAssetDto addStockAssetDto = new AddStockAssetDto();
        addStockAssetDto.setSymbol("AAPL");
        addStockAssetDto.setQuantity(10);
        addStockAssetDto.setPrice(BigDecimal.valueOf(100.0));

        String jsonAdd = objectMapper.writeValueAsString(addStockAssetDto);

        MvcResult resultAdd = mockMvc.perform(MockMvcRequestBuilders.post("/api/portfolio/asset/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAdd)
                        .header("Authorization", "Bearer " + userToken))
                .andReturn();

        MockHttpServletResponse responseAdd = resultAdd.getResponse();
        String contentAsStringAdd = responseAdd.getContentAsString();
        StockAssetResponseDto returnedAssetAdd = objectMapper.readValue(contentAsStringAdd, StockAssetResponseDto.class);

        assertEquals(HttpStatus.CREATED.value(), responseAdd.getStatus());
        assertEquals(addStockAssetDto.getSymbol(), returnedAssetAdd.getSymbol());
        assertEquals(addStockAssetDto.getQuantity(), returnedAssetAdd.getQuantity());
        assertEquals(addStockAssetDto.getPrice().doubleValue(), returnedAssetAdd.getPrice().doubleValue(), 0.01);

        // Delete the created stock asset
        MvcResult resultDelete = mockMvc.perform(MockMvcRequestBuilders.delete("/api/portfolio/asset/stock/{assetId}", returnedAssetAdd.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), resultDelete.getResponse().getStatus());

        // Try to get the deleted stock asset
        MvcResult resultGet = mockMvc.perform(MockMvcRequestBuilders.get("/api/portfolio/asset/stock/{assetId}", returnedAssetAdd.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andReturn();

        assertEquals(HttpStatus.NOT_FOUND.value(), resultGet.getResponse().getStatus());
    }

    @Test
    public void testDeleteNonExistingPortfolioAsset() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/portfolio/asset/stock/{assetId}", UUID.randomUUID())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }
}