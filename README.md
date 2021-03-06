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

---

### 파일 이어쓰기

```java
File outFile = new File("C:\\Users\\Hee\\eclipse-workspace\\fileuploder", "번역.txt");
PrintWriter pw = null;

try {
  pw = new PrintWriter(new FileWriter(outFile,true));
  pw.println(translatedText);
} catch (FileNotFoundException e) {        
  e.printStackTrace();
} catch (IOException e) {          
  e.printStackTrace();
} finally{
  if(pw != null) pw.close();
}
```

### JSON 파싱하기

**이게 가장 어려웠고, 고쳐야할 부분이 있다 !  하지만, 일단은 완성했다 ! 나중에 고치도록 하겠다.**

일단 연습코드를 보고 JSON을 분석하는 방법을 이해하자 !

- json 파일

```json
{
   "pageInfo": {
         "pageName": "abc",
         "pagePic": "http://example.com/content.jpg"
    }
    "posts": [
         {
              "post_id": "123456789012_123456789012",
              "actor_id": "1234567890",
              "picOfPersonWhoPosted": "http://example.com/photo.jpg",
              "nameOfPersonWhoPosted": "Jane Doe",
              "message": "Sounds cool. Can't wait to see it!",
              "likesCount": "2",
              "comments": [],
              "timeOfPost": "1234567890"
         }
    ]
}
```

- Java로 json 파싱하여 pageName , pagePic , post_id 값 얻기

```java
// 연습코드
// 다운로드 가능한 jar : http://mvnrepository.com/artifact/org.json/json

import org.json.*;

JSONObject obj = new JSONObject(" .... ");
String pageName = obj.getJSONObject("pageInfo").getString("pageName");

JSONArray arr = obj.getJSONArray("posts");
for (int i = 0; i < arr.length(); i++)
{
    String post_id = arr.getJSONObject(i).getString("post_id");
    ......
}
```

#### 파파고 응답 예제 Json

```json
< HTTP/1.1 200 OK
< Server: nginx
< Date: Wed, 28 Sep 2016 06:48:40 GMT
< Content-Type: application/json;charset=utf-8
< Content-Length: 136
< Connection: keep-alive
< Keep-Alive: timeout=5
< Vary: Accept-Encoding
< X-QUOTA: 10
<
* Connection #0 to host openapi.naver.com left intact
{"message":
    {"@type":"response",
        "@service":"naverservice.labs.api",
        "@version":"1.0.0",
        "result":
            {"translatedText":"So glad to see you."}
    }

```

우선, Java에서 JSON 파싱하기가 생각보다 어려웠다. 왜냐하면 파파고의 응답 값을 JSON array에 넣으려고 했더니, Json이 array가 없었다. 그래서 당황스러웠다 !! 파파고의 응답 값은 hash로만 이루어져있었다. 

그래서 파싱한 후 hash의 key값을 이용해 value 값을 뽑아냈다. 하지만 이 부분에서 고쳐야 할 부분이 있다.

JSONObject 을 두 번 사용하여 원하는 key값을 뽑아냈다. 결국 하드코딩... 이 부분을 고치기 위해 좀 더 생각해봐야겠다. 무튼 두 번 사용하여 원하는 value값을 뽑아냈고 번역된 문장만 파일에 저장하도록 만들었다 !

---

### 코드 수정

자바 Swing을 이용하여 GUI 화면을 만들고 파일 읽기, 번역된 내용 출력, 저장 등 기능을 추가하자 !

우선 예제 코드로 연습하자 !

```java
import java.awt.*;  
import javax.swing.*;  
import javax.swing.filechooser.*;  
import javax.swing.text.StyledEditorKit;  
import java.awt.event.*;  
import java.io.*;  
  
public class Jfilechooser extends JFrame  
{  
 // 변수 선언  
 JTextArea text;  
 Container pane;  
 JMenuBar nb = new JMenuBar();  
 JMenu file, help;  
 JMenuItem newI,openI,saveI,closeI,infoI,helpI;  
 JFileChooser open = new JFileChooser();//파일 및 디렉토리 선택 컴포넌트 선언  
   
   
 public Jfilechooser()  
 {  
  super("MemoNote"); // 부모클래스 생성자 호출  
  pane=getContentPane(); //JFrame 디자인을 위한 컨텐츠 영역 선언  
  pane.setLayout(new BorderLayout()); //JFrame 정렬  
  setJMenuBar(nb); // 메뉴바 붙임  
   
  // 메뉴 및 메뉴 아이템 생성  
  file = new JMenu("파일(F)");  
  help = new JMenu("정보(I)");  
    
  //키보드 연상기호 설정  
  file.setMnemonic('F');  
  help.setMnemonic('I');  
    
  //파일 메뉴 내용 생성  
  newI = new JMenuItem("새파일");  
  openI = new JMenuItem("열기");  
  saveI = new JMenuItem("저장");  
  closeI = new JMenuItem("닫기");  
  
  // 메뉴 단축키를 위한 셋팅  
    newI.setAccelerator(KeyStroke.getKeyStroke('N',Event.CTRL_MASK));  // Ctrl + N  
    openI.setAccelerator(KeyStroke.getKeyStroke('O',Event.CTRL_MASK)); // Ctrl + O  
    saveI.setAccelerator(KeyStroke.getKeyStroke('S',Event.CTRL_MASK)); // Ctrl + S  
    closeI.setAccelerator(KeyStroke.getKeyStroke('Q',Event.CTRL_MASK)); // Ctrl + Q  
  
  //정보 메뉴 생성  
  infoI = new JMenuItem("정보");  
  infoI.setAccelerator(KeyStroke.getKeyStroke('R',Event.CTRL_MASK)); // Ctrl + R  
  
  // 메뉴에 내용 붙임  
  file.add(newI);  
  file.add(openI);  
  file.add(saveI);  
  file.add(closeI);  
  help.add(infoI);  
    
  // 메뉴 완성  
  nb.add(file);  
  nb.add(help);  
   
  // 메뉴에서 새파일 클릭했을때 이벤트 처리  
  newI.addActionListener(new ActionListener()  
  {  
   public void actionPerformed(ActionEvent e)  
   {  
    text.setText(""); // text내용을 모두 지운다  
   }  
  });  
   
  // 메뉴에서 열기 클릭했을때 이벤트 처리  
  openI.addActionListener(new ActionListener()  
  {  
   public void actionPerformed(ActionEvent e)  
   {  
    int re = open.showOpenDialog(Jfilechooser.this); //파일열기 다이얼로그창을 띄운다  
    if (re==JFileChooser.APPROVE_OPTION)     //리턴 상태 확인  
    {  
     String fN;  
     String dir;  
     String str;  
      
     File file_open = open.getSelectedFile(); // 선택한 파일명을 가져온다  
     
     FileInputStream fis;   //파일 시스템의 파일 입력 바이트 취급 스트림 선언  
     ByteArrayOutputStream bo;  //데이터 바이트 배열에 기입해지는 출력 스트림 선언  
      try  
      {  
       fis = new FileInputStream(file_open); // FileInputStream객체를 생성  
       bo = new ByteArrayOutputStream();     // ByteArrayOutputStream객체를 생성  
       int i = 0;  
       while ((i = fis.read()) != -1) // 파일이 끝날때까지 읽어드림  
       {  
        bo.write(i);                  //len 바이트를 바이트 배열 출력 Stream에 기입  
       }  
        text.setText(bo.toString()); // 화면에 뿌려준다  
       fis.close();                 // FileInputStream을 닫는다.                  
       bo.close();                  // ByteArrayOutputStreamm을 닫는다.  
      }  
      catch(FileNotFoundException fe)  
      {}  
      catch(IOException ie)  
      {}  
    }  
   }  
  });  
   
  //메뉴에서 저장 클릭했을때 이벤트 처리  
  saveI.addActionListener(new ActionListener()  
  {  
   public void actionPerformed(ActionEvent e)  
   {  
    int re = open.showSaveDialog(Jfilechooser.this);  
    if (re==JFileChooser.APPROVE_OPTION) // 파일저장 다이얼로그를 띄운다  
    {  
     File file_open = open.getSelectedFile(); // 저장할 파일명을 가져온다  
   
     try  
     {  
     PrintWriter pw   = new PrintWriter(new BufferedWriter(new FileWriter(file_open))); // PrintWriter객체를 생성해서  
     pw.write(text.getText()); // 화면의 내용을 파일에 쓴다  
     pw.close();  
     }  
       
     catch(FileNotFoundException ie2)  
        {}  
     catch(IOException ie)  
        {}  
    }  
   }  
  });  
   
  //메뉴에서 닫기를 클릭했을때 이벤트 처리  
  closeI.addActionListener(new ActionListener()  
  {  
   public void actionPerformed(ActionEvent e)  
   {  
    dispose(); // 창을 닫고   
    System.exit(0); // 종료한다  
   }  
  });  
   
  // 메뉴에서 정보를 클릭했을때 이벤트 처리  
  infoI.addActionListener(new ActionListener()  
  {  
   public void actionPerformed(ActionEvent e)  
   {  
 // 정보를 보여주는 다이얼로그를 띄운다  
   JOptionPane.showMessageDialog(pane , "MemoNote 2009/12 Kim & Park");  
   }  
  });   
  text = new JTextArea();  
   
  // 화면에 보여질 text들의 상태에 따른 색상 지정  
  text.setCaretColor(Color.black);   
  text.setSelectedTextColor(Color.white);  
  text.setSelectionColor(Color.blue);  
  text.setBackground(Color.white);  
   
  pane.add(new JScrollPane(text));  
  
 }  
   
 public static void main(String[] args)  
 {  
  Jfilechooser note = new Jfilechooser(); // 객체생성  
  note.setSize(400,300); // 사이즈 지정  
  note.setVisible(true); // 화면에 보이게 함  
 }  
}  
```

이 예제코드로 swing에 익숙해 진 후 프로젝트 코드에 적용하자 ! 

나는 '번역'이라는 파일 메뉴를 만들었고 이벤트가 발생 시 번역 과정을 수행하도록 수정하였음 !

수정된 코드는 파일에서 확인합시다 !

---

2018-02-01 - 텍스트 파일 번역은 끝이 났지만 다른 문서 파일 파싱 및 인코딩 해결 못함.