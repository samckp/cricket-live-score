package com.sam.cric.service.impl;

import com.sam.cric.constant.IConstants;
import com.sam.cric.entities.Match;
import com.sam.cric.repositories.MatchRepo;
import com.sam.cric.service.CricketService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class CricketServiceImpl implements CricketService {

    private final Logger LOGGER = LoggerFactory.getLogger(CricketServiceImpl.class);
    private MatchRepo matchRepo;

    @Value("${tableUrl}")
    private String tableUrl;

    @Value("${liveScoreUrl}")
    private String liveScoreUrl;


    public CricketServiceImpl(MatchRepo matchRepo) {
        this.matchRepo = matchRepo;
    }
    @Override
    public List<Match> getLiveMatchScores() {

        List<Match> matches = new ArrayList<>();

        try {
            Document document = Jsoup.connect(liveScoreUrl).get();
            Elements liveScoreElements = document.select("div.cb-mtch-lst.cb-tms-itm");

            for(Element match : liveScoreElements){

                HashMap<String, String> liveMatchInfo = new LinkedHashMap<>();
                String teamsHeading = match.select("h3.cb-lv-scr-mtch-hdr").select("a").text();
                String matchNumberVenue = match.select("span").text();
                Elements matchBatTeamInfo = match.select("div.cb-hmscg-bat-txt");
                String battingTeam = matchBatTeamInfo.select("div.cb-hmscg-tm-nm").text();
                String score = matchBatTeamInfo.select("div.cb-hmscg-tm-nm+div").text();
                Elements bowlTeamInfo = match.select("div.cb-hmscg-bwl-txt");
                String bowlTeam = bowlTeamInfo.select("div.cb-hmscg-tm-nm").text();
                String bowlTeamScore = bowlTeamInfo.select("div.cb-hmscg-tm-nm+div").text();
                String textLive = match.select("div.cb-text-live").text();
                String textComplete = match.select("div.cb-text-complete").text();
                //getting match link
                String matchLink = match.select("a.cb-lv-scrs-well.cb-lv-scrs-well-live").attr("href").toString();

                Match match1 = new Match();
                match1.setTeamHeading(teamsHeading);
                match1.setMatchNumberVenue(matchNumberVenue);
                match1.setBattingTeam(battingTeam);
                match1.setBattingTeamScore(score);
                match1.setBowlTeam(bowlTeam);
                match1.setBowlTeamScore(bowlTeamScore);
                match1.setLiveText(textLive);
                match1.setMatchLink(matchLink);
                match1.setTextComplete(textComplete);
                match1.setMatchStatus();

                matches.add(match1);

//              update the match in database
                updateMatch(match1);
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }

        return matches;
    }

    private void updateMatch(Match match1) {

        Match match = this.matchRepo.findByTeamHeading(match1.getTeamHeading()).orElse(null);
        if (match == null) {
            this.matchRepo.save(match1);
        } else {

            match1.setMatchId(match.getMatchId());
            this.matchRepo.save(match1);
        }

    }


    @Override
    public List<List<String>> getCWC2023PointTable() {

        List<List<String>> pointsTable = new ArrayList<>();

        try{

            Document document = Jsoup.connect(tableUrl).get();
            Elements table = document.select("table.cb-srs-pnts");
            Elements tableHeads = table.select("thead>tr>*");
            List<String> headers = new ArrayList<>();
            tableHeads.forEach(element -> {
                headers.add(element.text());
            });
            pointsTable.add(headers);

            Elements bodyTrs = table.select("tbody>*");

            bodyTrs.forEach(tr -> {
                List<String> points = new ArrayList<>();
                if (tr.hasAttr("class")) {
                    Elements tds = tr.select("td");
                    String team = tds.get(0).select("div.cb-col-84").text();
                    points.add(team);
                    tds.forEach(td -> {
                        if (!td.hasClass("cb-srs-pnts-name")) {
                            points.add(td.text());
                        }
                    });

                    LOGGER.info(points.toString());
                    pointsTable.add(points);
                }
            });


        } catch (Exception e) {
           LOGGER.error(e.getMessage());
        }

        return pointsTable;
    }

    @Override
    public List<Match> getAllMatches() {
        return this.matchRepo.findAll();
    }
}
