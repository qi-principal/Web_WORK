package com.adserver.service;

import com.adserver.model.Ad;
import com.adserver.repository.AdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AdServiceTest {

    @Mock
    private AdRepository adRepository;

    @InjectMocks
    private AdService adService;

    private Ad testAd;

    @BeforeEach
    void setUp() {
        testAd = new Ad();
        testAd.setAdId(1L);
        testAd.setAdName("Test Ad");
        testAd.setAdDescribe("Test Description");
        testAd.setAdUrl("http://test.com");
        testAd.setPicUrl("http://test.com/image.jpg");
    }

    @Test
    void createAd_Success() {
        when(adRepository.save(any(Ad.class))).thenReturn(testAd);

        Ad result = adService.createAd(testAd);

        assertNotNull(result);
        assertEquals("Test Ad", result.getAdName());
        verify(adRepository).save(testAd);
    }

    @Test
    void getAd_Success() {
        when(adRepository.findById(1L)).thenReturn(Optional.of(testAd));

        Ad result = adService.getAd(1L);

        assertNotNull(result);
        assertEquals(1L, result.getAdId());
        assertEquals("Test Ad", result.getAdName());
    }

    @Test
    void getAd_NotFound() {
        when(adRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            adService.getAd(1L)
        );
    }

    @Test
    void deleteAd_Success() {
        doNothing().when(adRepository).deleteById(1L);

        adService.deleteAd(1L);

        verify(adRepository).deleteById(1L);
    }
} 