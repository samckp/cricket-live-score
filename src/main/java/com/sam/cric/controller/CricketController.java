package com.sam.cric.controller;

import com.sam.cric.entities.Match;
import com.sam.cric.service.CricketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cricket")
@CrossOrigin("*")
public class CricketController {

    private final Logger LOGGER = LoggerFactory.getLogger(CricketController.class);

    private CricketService cricketService;

    public CricketController(CricketService cricketService) {
        this.cricketService = cricketService;
    }

    @GetMapping("/live")
    public ResponseEntity<?> getLiveMatchScore() throws InterruptedException{

        LOGGER.info("Getting live score !!");
        return new ResponseEntity<>(this.cricketService.getLiveMatchScores(),
                HttpStatus.OK);
    }

    @GetMapping("/points-table")
    public ResponseEntity<?>  getCWC2023PointTable() {

        return new ResponseEntity<>(this.cricketService.getCWC2023PointTable(),
                HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Match>> getAllMatches(){

        return new ResponseEntity<>(this.cricketService.getAllMatches(),
                HttpStatus.OK);
    }
}
