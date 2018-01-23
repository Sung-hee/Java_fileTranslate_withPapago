package file_translation;

//네이버 Papago NMT API 예제
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.*;

public class app {

public static void main(String[] args) throws JSONException {
	   String clientId = "애플리케이션 클라이언트 아이디값";
	   String clientSecret = "애플리케이션 클라이언트 시크릿값";
   
   File inFile = new File("C:\\Users\\Hee\\eclipse-workspace\\fileuploder", "test.txt");
   File outFile = new File("C:\\Users\\Hee\\eclipse-workspace\\fileuploder", "번역.txt");
  
   BufferedReader tr = null;
   PrintWriter pw = null;
	  
    try {
        tr = new BufferedReader(new FileReader(inFile));
        pw = new PrintWriter(new FileWriter(outFile,true));
  
        String line;
        
        while ((line = tr.readLine()) != null) {
      	 String text = line;
       	 String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
            // post request
            String postParams = "source=en&target=ko&text=" + text;
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();		
            // 파파고에게 요청을 보내고 응답을 받게되면
            // 응답받은 결과값의 형태는 JSON 이다. 
            // 그래서, 우리는 JSON파싱한 후 hash의 Key, value 값을 통해 번역된 결과만 파일에 저장하도록 하자 !
            
            // 응답받은 결과값 JSON형태의 response을 파싱하자 !
            JSONObject obj = new JSONObject(response.toString());
            // 이후 hash의 key, value 값을 통해 필요한 값들만 저장하자. 
            String jsonResult = obj.getJSONObject("message").getString("result");
            JSONObject obj2 = new JSONObject(jsonResult);
            // 마지막으로 key값 translatedText를 이용해 번역된 결과값만 저장하자. 
            String translatedText = obj2.getString("translatedText");
            System.out.println(translatedText);
            pw.println(translatedText);
//            pw.println(response.toString());
//            System.out.println(response.toString());
        }
        pw.close();
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }finally {
        if(tr != null) try {tr.close(); } catch (IOException e) {}
    }
}
}


