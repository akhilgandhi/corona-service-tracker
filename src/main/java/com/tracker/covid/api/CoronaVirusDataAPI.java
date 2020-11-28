package com.tracker.covid.api;

import com.tracker.covid.models.*;
import com.tracker.covid.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
public class CoronaVirusDataAPI {

    @Autowired
    private CoronaVirusDataService coronaVirusDataService;

    public CoronaVirusDataAPI(CoronaVirusDataService coronaVirusDataService) {
        this.coronaVirusDataService = coronaVirusDataService;
    }

    @GetMapping(value = "/")
    public HomePageData getHomePageData() {

        HomePageData homePageData = new HomePageData();

        homePageData.setTotalConfirmedCases(coronaVirusDataService.getConfirmedData().stream().mapToInt(ConfirmedData::getLatestConfirmData).sum());
        homePageData.setTotalFatalities(coronaVirusDataService.getFatalitiesData().stream().mapToInt(FatalitiesData::getLatestFatalities).sum());
        homePageData.setTotalRecovered(coronaVirusDataService.getRecoveredData().stream().mapToInt(RecoveredData::getLatestRecoveredData).sum());
        homePageData.setTotalActiveCases(homePageData.getTotalConfirmedCases() - homePageData.getTotalRecovered());

        homePageData.setCountryWithHighestConfirmedCases(coronaVirusDataService.getConfirmedData().stream().collect(Collectors.groupingBy(ConfirmedData::getCountry, Collectors.summingInt(ConfirmedData::getLatestConfirmData))).entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get().getKey());
        homePageData.setCountryWithHighestFatalities(coronaVirusDataService.getFatalitiesData().stream().collect(Collectors.groupingBy(FatalitiesData::getCountry, Collectors.summingInt(FatalitiesData::getLatestFatalities))).entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get().getKey());
        homePageData.setCountryWithHighestRecoveredCases(coronaVirusDataService.getRecoveredData().stream().collect(Collectors.groupingBy(RecoveredData::getCountry, Collectors.summingInt(RecoveredData::getLatestRecoveredData))).entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get().getKey());

        homePageData.setHighestConfirmedCasesByCountry(coronaVirusDataService.getConfirmedData().stream().collect(Collectors.groupingBy(ConfirmedData::getCountry, Collectors.summingInt(ConfirmedData::getLatestConfirmData))).entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get().getValue());
        homePageData.setHighestFatalitiesByCountry(coronaVirusDataService.getFatalitiesData().stream().collect(Collectors.groupingBy(FatalitiesData::getCountry, Collectors.summingInt(FatalitiesData::getLatestFatalities))).entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get().getValue());
        homePageData.setHighestRecoveredCasesByCountry(coronaVirusDataService.getRecoveredData().stream().collect(Collectors.groupingBy(RecoveredData::getCountry, Collectors.summingInt(RecoveredData::getLatestRecoveredData))).entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get().getValue());

        homePageData.setTotalConfirmCasesIndia(coronaVirusDataService.getConfirmedData().stream().filter((name) -> (name.getCountry().equals("India"))).map(ConfirmedData::getLatestConfirmData).findAny().orElse(null));
        homePageData.setTotalFatalitiesIndia(coronaVirusDataService.getFatalitiesData().stream().filter((name) -> (name.getCountry().equals("India"))).map(FatalitiesData::getLatestFatalities).findAny().orElse(null));
        homePageData.setTotalRecoveredCasesIndia(coronaVirusDataService.getRecoveredData().stream().filter((name) -> (name.getCountry().equals("India"))).map(RecoveredData::getLatestRecoveredData).findAny().orElse(null));

        return homePageData;
    }

    @GetMapping(value = "/discovered")
    public List<ConfirmedData> listConfirmData() {
        return coronaVirusDataService.getConfirmedData();
    }

    @GetMapping(value = "/fatalities")
    public List<FatalitiesData> listFatalitiesData() {
        return coronaVirusDataService.getFatalitiesData();
    }

    @GetMapping(value = "/cured")
    public List<RecoveredData> listCuredData() {
        return coronaVirusDataService.getRecoveredData();
    }
}
