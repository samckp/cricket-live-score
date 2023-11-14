package com.sam.cric.service;

import com.sam.cric.entities.Match;

import java.util.List;

public interface CricketService {

        List<Match> getLiveMatchScores();
        List<List<String>> getCWC2023PointTable();

        List<Match> getAllMatches();
}
