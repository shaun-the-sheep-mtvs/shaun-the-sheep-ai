package org.mtvs.backend.deeprecommend.controller;

import lombok.extern.slf4j.Slf4j;
import org.mtvs.backend.auth.model.User;
import org.mtvs.backend.deeprecommend.service.DeepRecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class DeepRecommendController {

    @Autowired
    private DeepRecommendService recommendService;
    @PostMapping("/recommend")
    public String ask(@RequestBody User user){
        return recommendService.askOpenAI(user.getId());
    }

}
