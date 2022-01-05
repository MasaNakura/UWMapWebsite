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

public class Buildings implements HttpFunction {

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


    // Handle the request by returning the building names as a map.
    BufferedWriter writer = response.getWriter();
    writer.write(gson.toJson(model.buildingNames()));
  }

}
