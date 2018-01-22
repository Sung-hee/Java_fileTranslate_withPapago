## 자바로 만드는 번역을 부탁해 !

#### 이번에는 파일을 번역하는 프로그램을 만들어 보겠습니다 !

이걸 시작하게 된 계기는 ! 지인에게 파일을 통째로 번역해주는 프로그램을 만들어 달라고 요청이 들어왔습니다. ! 

그래서 일단은 txt 파일을 읽고 새로운 txt파일에 번역 된 글을 저장하는 방식으로 만들어 보겠습니다 !

---

#### 파일 읽기, 이어쓰기, API 사용을 통해 구현하겠습니다. 우선 사용 방법에 대한 예제들을 확인하겠습니다.

### 1. 파일 읽기

```java
File inFile = new File("C:\\Users\\Hee\\eclipse-workspace\\fileuploder", "test.txt");
BufferedReader tr = null;

try {
  br = new BufferedReader(new FileReader(inFile));
  String line;
  while ((line = br.readLine()) != null) {
    System.out.println(line);
  }
} catch (FileNotFoundException e) {
  e.printStackTrace();
} catch (IOException e) {
  e.printStackTrace();
}finally {
  if(br != null) try {br.close(); } catch (IOException e) {}
}
```

### 2. 파파고 API 사용법

https://developers.naver.com/docs/labs/translator/ 를 참고하자 !

```java
// 네이버 기계번역 API 예제
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
public class APIExamTranslate {

    public static void main(String[] args) {
        String clientId = "YOUR_CLIENT_ID";//애플리케이션 클라이언트 아이디값";
        String clientSecret = "YOUR_CLIENT_SECRET";//애플리케이션 클라이언트 시크릿값";
        try {
            String text = URLEncoder.encode("만나서 반갑습니다.", "UTF-8");
            String apiURL = "https://openapi.naver.com/v1/language/translate";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
            // post request
            String postParams = "source=ko&target=en&text=" + text;
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
            System.out.println(response.toString());
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
```

### 파일 쓰기

```java
File outFile = new File("C:\\Users\\Public", "번역.txt");

BufferedWriter bw = null;
try {
  bw = new BufferedWriter(new FileWriter(outFile));
  bw.write("테스트 합니다.");
  bw.flush();
} catch (IOException e) {
  e.printStackTrace();
}finally {
  if(bw != null) try {bw.close(); } catch (IOException e) {}
}
```

하지만 이렇게하면 덮어쓰기가 되어서 저장파일에는 마지막 한 줄만 번역이 되어있다 ! 

이부분은 다음에 고치도록 하겠습니다 !