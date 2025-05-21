package org.mtvs.backend.deeprecommend.controller;

import org.mtvs.backend.deeprecommend.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;
    @GetMapping("/recommend")
    public String ask(@RequestBody RequestJsonArrayRoutineDTO requestJsonArrayRoutineDTO){
        return recommendService.askOpenAI(requestJsonArrayRoutineDTO);
    }

}
