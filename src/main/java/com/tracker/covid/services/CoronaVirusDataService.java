package com.tracker.covid.services;

import com.tracker.covid.models.ConfirmedData;
import com.tracker.covid.models.FatalitiesData;
import com.tracker.covid.models.RecoveredData;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoronaVirusDataService {

    private final List<ConfirmedData> allConfirmed = new ArrayList<>();
    private final List<FatalitiesData> allDeaths = new ArrayList<>();
    private final List<RecoveredData> allRecovered = new ArrayList<>();

    public List<ConfirmedData> getConfirmedData() {

        return allConfirmed;
    }

    public List<FatalitiesData> getFatalitiesData() {

        return allDeaths;
    }

    public List<RecoveredData> getRecoveredData() {

        return allRecovered;
    }

    /**
     * This method will make http request on scheduled basis for getting the data for confirmed cases,
     * deaths and recovered cases every 24 hours.
     * @see <a href="https://github.com/CSSEGISandData/COVID-19/tree/master/csse_covid_19_data/csse_covid_19_time_series">CSSEGISandData</a>
     * @throws IOException
     * @throws InterruptedException
     */
    @PostConstruct
    @Scheduled(cron = "0 0 */24 * * *")
    public void getCoronaVirusData() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();

        // Getting confirmed cases
        String CONFIRM_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
        HttpRequest requestconfirmedData = HttpRequest.newBuilder().uri(URI.create(CONFIRM_DATA_URL)).build();

        HttpResponse<String> httpResponseConfirmedData = client.send(requestconfirmedData, HttpResponse.BodyHandlers.ofString());

        // Saving the confirmed data in db
        StringReader readerConfirmedData = new StringReader(httpResponseConfirmedData.body());

        Iterable<CSVRecord> confirmedRecords = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(readerConfirmedData);
        for (CSVRecord record : confirmedRecords) {
            ConfirmedData confirmedData = new ConfirmedData();
            confirmedData.setState(record.get("Province/State"));
            confirmedData.setCountry(record.get("Country/Region"));
            int latestConfirmedCases = Integer.parseInt(record.get(record.size()-1));
            int prevDayDiff = Integer.parseInt(record.get(record.size()-2));
            confirmedData.setLatestConfirmData(latestConfirmedCases);
            confirmedData.setDiffFromPrevDay(latestConfirmedCases - prevDayDiff);
            confirmedData.setPercentDiff(new DecimalFormat("00.00").format(latestConfirmedCases == prevDayDiff ? 0.00 : (((latestConfirmedCases - prevDayDiff) * 100.00) / latestConfirmedCases)));
            allConfirmed.add(confirmedData);
        }

        // Getting fatalities data
        String DEATH_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv";
        HttpRequest requestFatalitiesData = HttpRequest.newBuilder().uri(URI.create(DEATH_DATA_URL)).build();

        HttpResponse<String> httpResponseFatalitiesData = client.send(requestFatalitiesData, HttpResponse.BodyHandlers.ofString());

        // Saving the fatalities data in db
        StringReader readerFatalitiesData = new StringReader(httpResponseFatalitiesData.body());

        Iterable<CSVRecord> fatalitiesRecords = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(readerFatalitiesData);
        for (CSVRecord record : fatalitiesRecords) {
            FatalitiesData fatalitiesData = new FatalitiesData();
            fatalitiesData.setState(record.get("Province/State"));
            fatalitiesData.setCountry(record.get("Country/Region"));
            int latestConfirmedCases = Integer.parseInt(record.get(record.size()-1));
            int prevDayDiff = Integer.parseInt(record.get(record.size()-2));
            fatalitiesData.setLatestFatalities(latestConfirmedCases);
            fatalitiesData.setDiffFromPrevDay(latestConfirmedCases - prevDayDiff);
            fatalitiesData.setPercentDiff(new DecimalFormat("00.00").format(latestConfirmedCases == prevDayDiff ? 0.00 : (((latestConfirmedCases - prevDayDiff) * 100.00) / latestConfirmedCases)));
            allDeaths.add(fatalitiesData);
        }

        // Getting recovered data
        String RECOVERED_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv";
        HttpRequest requestRecoveredData = HttpRequest.newBuilder().uri(URI.create(RECOVERED_DATA_URL)).build();

        HttpResponse<String> httpResponseRecoveredData = client.send(requestRecoveredData, HttpResponse.BodyHandlers.ofString());

        // Saving the recovered data in db
        StringReader readerRecoveredData = new StringReader(httpResponseRecoveredData.body());

        Iterable<CSVRecord> recoveredRecords = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(readerRecoveredData);
        for (CSVRecord record : recoveredRecords) {
            RecoveredData recoveredData = new RecoveredData();
            recoveredData.setState(record.get("Province/State"));
            recoveredData.setCountry(record.get("Country/Region"));
            int latestConfirmedCases = Integer.parseInt(record.get(record.size()-1));
            int prevDayDiff = Integer.parseInt(record.get(record.size()-2));
            recoveredData.setLatestRecoveredData(latestConfirmedCases);
            recoveredData.setDiffFromPrevDay(latestConfirmedCases - prevDayDiff);
            recoveredData.setPercentDiff(new DecimalFormat("00.00").format(latestConfirmedCases == prevDayDiff ? 0.00 : (((latestConfirmedCases - prevDayDiff) * 100.00) / latestConfirmedCases)));
            allRecovered.add(recoveredData);
        }
    }
}
