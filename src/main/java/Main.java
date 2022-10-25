
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final long CLIENT_TIMEOUT = 30L;
    private static final String CSV_SEPARATOR = ",";

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter first username");
        String userName = scanner.nextLine();
        System.out.println("Enter second username");
        String userName2 = scanner.nextLine();
        List<String> list = Arrays.asList(userName, userName2);
        List<TwitterUser> users = new ArrayList<>();
        for (String un : list) {
            String token = "Bearer AAAAAAAAAAAAAAAAAAAAANRILgAAAAAAnNwIzUejRCOuH5E6I8xnZz4puTs%3D1Zv7ttfk8LF81IUq16cHjhLTvJu4FA33AGWWjCpTnA";
            String guestToken = "1584852932415131649";
            String url = "https://twitter.com/i/api/graphql/HThKoC4xtXHcuMIok4O0HA/UserByScreenName?variables=%7B%22screen_name%22%3A%22" + un + "%22%2C%22withSafetyModeUserFields%22%3Atrue%2C%22withSuperFollowsUserFields%22%3Atrue%7D&features=%7B%22verified_phone_label_enabled%22%3Afalse%2C%22responsive_web_graphql_timeline_navigation_enabled%22%3Atrue%7D";

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(CLIENT_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(CLIENT_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(CLIENT_TIMEOUT, TimeUnit.SECONDS)
                    .build();

            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .addHeader("authorization", token)
                    .addHeader("x-guest-token", guestToken)
                    .get();

            try {
                Response response = client.newCall(requestBuilder.build()).execute();
                JsonObject data = JsonParser.parseString(Objects.requireNonNull(response.body()).string()).getAsJsonObject().get("data").getAsJsonObject();
                JsonObject user = data.get("user").getAsJsonObject();
                JsonObject result = user.get("result").getAsJsonObject();
                TwitterUser twitterUser = new TwitterUser();
                twitterUser.setCreatedDate(result.get("legacy").getAsJsonObject().get("created_at").getAsString());
                twitterUser.setUserName(un);
                twitterUser.setFollowersCount(result.get("legacy").getAsJsonObject().get("followers_count").getAsInt());
                users.add(twitterUser);
            }
            catch (Exception e){
                System.out.println("User with " + un + " doesn't exists");
            }

        }


        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("users.csv"), "UTF-8"));
        for (TwitterUser user : users)
        {
            StringBuffer oneLine = new StringBuffer();
            oneLine.append(user.getUserName());
            oneLine.append(CSV_SEPARATOR);
            oneLine.append(user.getCreatedDate());
            oneLine.append(CSV_SEPARATOR);
            oneLine.append(user.getFollowersCount());
            bw.write(oneLine.toString());
            bw.newLine();
        }
        bw.flush();
        bw.close();
        System.out.println("Successfully finished");
    }

}

class TwitterUser {
    private String userName;
    private String createdDate;
    private Integer followersCount;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(Integer followersCount) {
        this.followersCount = followersCount;
    }
}
