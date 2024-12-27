package com.adserver.controller;

import com.adserver.AdServerApplication;
import com.adserver.BaseTest;
import com.adserver.config.TestConfig;
import com.adserver.model.Ad;
import com.adserver.service.AdService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdController.class)
@Import({TestConfig.class})
@WithMockUser
@ContextConfiguration(classes = {AdServerApplication.class, TestConfig.class})
public class AdControllerTest extends BaseTest {

    @MockBean
    private AdService adService;

    private Ad testAd;

    @BeforeEach
    void initTest() {
        testAd = createTestAd();
    }

    @Test
    void createAd_Success() throws Exception {
        // Arrange
        when(adService.createAd(any(Ad.class))).thenReturn(testAd);

        // Act
        ResultActions result = mockMvc.perform(post("/api/ads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(testAd)));

        // Assert
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adId").value(testAd.getAdId()))
                .andExpect(jsonPath("$.adName").value(testAd.getAdName()))
                .andExpect(jsonPath("$.adDescribe").value(testAd.getAdDescribe()))
                .andExpect(jsonPath("$.adUrl").value(testAd.getAdUrl()))
                .andExpect(jsonPath("$.picUrl").value(testAd.getPicUrl()));

        verify(adService).createAd(any(Ad.class));
    }

    @Test
    void getAd_Success() throws Exception {
        // Arrange
        when(adService.getAd(1L)).thenReturn(testAd);

        // Act & Assert
        mockMvc.perform(get("/api/ads/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adId").value(testAd.getAdId()))
                .andExpect(jsonPath("$.adName").value(testAd.getAdName()));

        verify(adService).getAd(1L);
    }

    @Test
    void getAd_NotFound() throws Exception {
        // Arrange
        when(adService.getAd(1L)).thenThrow(new RuntimeException("Ad not found"));

        // Act & Assert
        mockMvc.perform(get("/api/ads/1"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(adService).getAd(1L);
    }

    @Test
    void deleteAd_Success() throws Exception {
        // Arrange
        doNothing().when(adService).deleteAd(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/ads/1"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(adService).deleteAd(1L);
    }

    @Test
    void deleteAd_NotFound() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Ad not found")).when(adService).deleteAd(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/ads/1"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(adService).deleteAd(1L);
    }

    private Ad createTestAd() {
        Ad ad = new Ad();
        ad.setAdId(1L);
        ad.setAdName("Test Ad");
        ad.setAdDescribe("Test Description");
        ad.setAdUrl("http://test.com");
        ad.setPicUrl("http://test.com/image.jpg");
        return ad;
    }
} 