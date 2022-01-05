package functions;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;

import pathfinder.CampusMap;
import pathfinder.ModelAPI;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Optional;

public class Path implements HttpFunction {

  private static ModelAPI model = new CampusMap("campus_buildings.csv", "campus_paths.csv");
  private static Gson gson = new Gson();

  @Override
  public void service(HttpRequest request, HttpResponse response)
      throws IOException {

    // Allow requests from other domains.
    response.appendHeader("Access-Control-Allow-Origin", "*");
    if ("OPTIONS".equals(request.getMethod())) {
      response.appendHeader("Access-Control-Allow-Methods", "GET");
      response.appendHeader("Access-Control-Allow-Headers", "Content-Type");
      response.appendHeader("Access-Control-Max-Age", "3600");
      response.setStatusCode(HttpURLConnection.HTTP_NO_CONTENT);
      return;
    }

    // Make sure they passed start and end buildings that actually exist.
    Optional<String> start = request.getFirstQueryParameter("start");
    Optional<String> end = request.getFirstQueryParameter("end");
    if (!start.isPresent() || !end.isPresent()) {
      response.setStatusCode(400, "Bad Request: Missing start or end.");
      return;
    }
    if (!model.shortNameExists(start.get()) ||
        !model.shortNameExists(end.get())) {
      response.setStatusCode(400, "Bad Request: Nonexistent Building");
      return;
    }

    // Handle the request by returning the shortest path (in JSON format).
    BufferedWriter writer = response.getWriter();
    writer.write(gson.toJson(model.findShortestPath(start.get(), end.get())));
  }

}
