package file_translation;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import org.json.JSONException;
import org.json.JSONObject;  
  
public class Note extends JFrame  
{  
 // 변수 선언  
 JTextArea transText; 
 JTextArea text;  
 Container pane;  
 JMenuBar nb = new JMenuBar();  
 JMenu file, help;  
 JMenuItem transI,openI,saveI,closeI;  
 JFileChooser open = new JFileChooser();//파일 및 디렉토리 선택 컴포넌트 선언  
   
   
 public Note()  
 {  
  super("번역가즈아"); // 부모클래스 생성자 호출  
  pane=getContentPane(); //JFrame 디자인을 위한 컨텐츠 영역 선언  
  pane.setLayout(new BorderLayout()); //JFrame 정렬  
  setJMenuBar(nb); // 메뉴바 붙임  
   
  // 메뉴 및 메뉴 아이템 생성  
  file = new JMenu("파일(F)");  
    
  //키보드 연상기호 설정  
  file.setMnemonic('F');   
    
  //파일 메뉴 내용 생성   
  openI = new JMenuItem("열기");
  transI = new JMenuItem("번역하기"); 
  saveI = new JMenuItem("저장");  
  closeI = new JMenuItem("닫기");  
  
  // 메뉴 단축키를 위한 셋팅    
    openI.setAccelerator(KeyStroke.getKeyStroke('O',Event.CTRL_MASK)); // Ctrl + O
    transI.setAccelerator(KeyStroke.getKeyStroke('T',Event.CTRL_MASK));  // Ctrl + T
    saveI.setAccelerator(KeyStroke.getKeyStroke('S',Event.CTRL_MASK)); // Ctrl + S  
    closeI.setAccelerator(KeyStroke.getKeyStroke('Q',Event.CTRL_MASK)); // Ctrl + Q   
  
  // 메뉴에 내용 붙임  
  file.add(openI);
  file.add(transI);  
  file.add(saveI);  
  file.add(closeI);  
    
  // 메뉴 완성  
  nb.add(file);  
   
//   메뉴에서 새파일 클릭했을때 이벤트 처리  
  transI.addActionListener(new ActionListener()  
  {  
   public void actionPerformed(ActionEvent e)  
   {  
	   String clientId = "YOUR_CLIENT_SECRET";
	   String clientSecret = "YOUR_CLIENT_SECRET";
	   
	   File inFile = open.getSelectedFile();
	   BufferedReader tr = null;

	    try {
	        tr = new BufferedReader(new FileReader(inFile));
	  
	        String line;
        	String translationText = "";
        	
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
	            translationText += translatedText + "\n";
	        }
            text.setText(translationText);
	    } catch (FileNotFoundException e1) {
	        e1.printStackTrace();
	    } catch (IOException e1) {
	        e1.printStackTrace();
	    } catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally {
	        if(tr != null) try {tr.close(); } catch (IOException e1) {}
	    }  
   }  
  });  
   
  // 메뉴에서 열기 클릭했을때 이벤트 처리  
  openI.addActionListener(new ActionListener()  
  {  
   public void actionPerformed(ActionEvent e)  
   {  
    int re = open.showOpenDialog(Note.this); //파일열기 다이얼로그창을 띄운다  
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
    int re = open.showSaveDialog(Note.this);  
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
  Note note = new Note(); // 객체생성  
  note.setSize(800,500); // 사이즈 지정  
  note.setVisible(true); // 화면에 보이게 함  
 }  
}  
