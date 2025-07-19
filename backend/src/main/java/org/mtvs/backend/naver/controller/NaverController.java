package org.mtvs.backend.naver.controller;

import lombok.RequiredArgsConstructor;
import org.mtvs.backend.naver.image.api.NaverApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/naver")
@RequiredArgsConstructor
public class NaverController {
    
    private final NaverApiService naverApiService;
    
    /**
     * Endpoint to process all products without valid image URLs
     * This can be called manually or scheduled to run periodically
     */
    @PostMapping("/process-missing-images")
    public ResponseEntity<?> processMissingImages() {
        return naverApiService.processProductsWithoutImages();
    }
    
    /**
     * Get count of products without valid images
     */
    @GetMapping("/missing-images/count")
    public ResponseEntity<?> getMissingImagesCount() {
        // This would require a new method in NaverApiService to just count
        return ResponseEntity.ok("Use POST /process-missing-images to process and get count");
    }
}