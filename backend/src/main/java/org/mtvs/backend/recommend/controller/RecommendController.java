//package org.mtvs.backend.recommend.controller;
//
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//
//@RestController
//@RequestMapping("api/recommend")
//public class RecommendController {
//
//    // 사용자의 피부 타입과 피부 고민 맞춤 제품 추천
//    @getMapping("/diagnose")
//    public ResponseEntity<String> diagnose(@RequestBody RequestDTO requestDTO) {
//        User user = userRepository.findById(requestDTO.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
//
//        String skinType = user.getSkinType();
//        List<String> concerns = user.getConcerns();
//
//        String prompt = String.format("%s 피부, %s 고민, 한국 사용자 대상에 맞는 제품을 3개 추천, JSON 형식으로 반환", skinType, concerns);
//
//
//        return ResponseEntity.ok();
//    }
//}