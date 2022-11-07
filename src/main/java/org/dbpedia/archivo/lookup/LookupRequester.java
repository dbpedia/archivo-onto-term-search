package org.dbpedia.archivo.lookup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class LookupRequester {

    private final String lookup_endpoint;

    public LookupRequester(@Value("${ocs.lookup.uri}") String lookupBase) {
        this.lookup_endpoint = lookupBase + "?query=%s";
    }

    public List<LookupObject> getResult(String query) throws Exception {

        Gson gson = new Gson();


        String encoded_query = URLEncoder.encode(query, StandardCharsets.UTF_8);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest req = HttpRequest.newBuilder().uri(new URI(String.format(this.lookup_endpoint, encoded_query))).build();

        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());

        Map<String, List<LookupObject>> result_map = gson.fromJson(response.body(), new TypeToken<Map<String, List<LookupObject>>>() {}.getType());

        return result_map.get("docs");
    }
}


