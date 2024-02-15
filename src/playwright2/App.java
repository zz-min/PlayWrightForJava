package playwright2;

import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

import com.microsoft.playwright.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class App {

	public static void main(String[] args) {
		Browser browser = null;
		
		try (Playwright playwright = Playwright.create()) {
			// 1.Browser 객체 생성 
			// (1) Headless true (default) 
            browser = playwright.chromium().launch(); 
			
			// (2) Headless false 
            // Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
			
            // 2. Page 객체 생성
            // (1) Browser객체에서 직접 Page 생성 (default) 
            // Page page = browser.newPage();
            
            // (2) BrowserContext 객체에서 Page 생성 
            // ** 메모리 내 격리된 브라우저 프로필인 BrowserContext가 존재.  
            // ** 서로 간섭하지 않도록 각 테스트마다 새 BrowserContext생성하는 것이 좋음.
            BrowserContext context = browser.newContext();
            Page page = context.newPage();
            
            // 3. Page 객체 내 기능들 
            // (1) 페이지 이동 
//            page.navigate("https://search.shopping.naver.com/search/category/100000580"); // 가전>TV
            page.navigate("https://search.shopping.naver.com/search/category/100000582"); // 가전>냉장고
//            page.navigate("https://search.shopping.naver.com/search/category/100000584"); // 가전>세탁기/건조기
            
            // 브랜드 지정 (삼성) 
            ElementHandle samsungElement = page.waitForSelector("//span[text()='삼성']"); // XPath로 경로 지정 
            samsungElement.click();
            
            // (2) 페이지 타이틀 가져오기
//            String title = page.title();
//            System.out.println("* title : " + page.title());            
            
            // (3) 페이지 스크린샷 이미지 저장하기 
//            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("samsung.png")));

            // (4) assertThat : 해당페이지가 필요한 조건이 충족될 때까지 기다리는 오버로드 (검사가 자동으로 재시도)
            assertThat(page).hasTitle(Pattern.compile("네이버 쇼핑"));
            
            // (5) 페이지별 리스트
            page.evaluate("window.scrollBy(0, 2000)");//스크롤 2000px 아래로 이동
            
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("samsung1.png")));

            List<ElementHandle> products = page.querySelectorAll(".product_item__MDtDF");
            System.out.println("products count : "+products.size());
            
            for(ElementHandle e:products) {
    	        String str = e.innerText();
    	        String[] lines = str.split("\n");
    	        if(lines.length>3) {
    	        	// 정상     	        	
    	        	System.out.println("name : "+lines[1]+" / price : "+lines[2]);
    	        }else {
    	        	//비정상 
    	        	System.out.println("ERROR : "+str);
    	        }
            }
            
            browser.close();
        }catch (PlaywrightException e) {
            e.printStackTrace();
        }
	}

}
