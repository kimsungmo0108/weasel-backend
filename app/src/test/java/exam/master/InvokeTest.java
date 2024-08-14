package exam.master;

import org.json.JSONObject;
import org.json.JSONPointer;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class InvokeTest {

    public static String invokeModelwithMultipartFile(MultipartFile file, String prompt) {
        try {
            // MultipartFile을 바이트 배열로 변환
            byte[] imageBytes = file.getBytes();

            // 이미지 파일을 Base64로 인코딩
            String encodedImage = Base64.getEncoder().encodeToString(imageBytes);

            // Bedrock Runtime client 생성
            var client = BedrockRuntimeClient.builder()
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .region(Region.US_EAST_1)
                    .build();

            // model ID, e.g., claude-3-5-sonnet 클로우드 소넷
            var modelId = "anthropic.claude-3-5-sonnet-20240620-v1:0";

            // 요청 템플릿
            var nativeRequestTemplate = """
                {
                    "anthropic_version": "bedrock-2023-05-31",
                    "max_tokens": 1024,
                    "temperature": 0.5,
                    "messages": [{
                        "role": "user",
                        "content": [
                            {
                                "type": "image",
                                "source": {
                                    "type": "base64",
                                    "media_type": "image/png",
                                    "data": "{{image}}"
                                }
                            },
                            {
                                "type": "text",
                                "text": "{{prompt}}"
                            }
                        ]
                    }]
                }""";

            // 템플릿에 prompt 및 이미지 삽입
            String nativeRequest = nativeRequestTemplate
                    .replace("{{image}}", encodedImage)
                    .replace("{{prompt}}", prompt);

            // 요청 전송
            var response = client.invokeModel(request -> request
                    .body(SdkBytes.fromUtf8String(nativeRequest))
                    .modelId(modelId)
            );

            System.out.println("response: " + response);

            // 응답 바이트 배열로 수신 후 UTF-8로 디코딩
            byte[] responseBytes = response.body().asByteArray();
            String responseBody = new String(responseBytes, StandardCharsets.UTF_8);

            System.out.println("Raw Response: " + responseBody);

            // JSON 파싱
            var jsonResponse = new JSONObject(responseBody);

            // 응답 텍스트 추출
            var text = new JSONPointer("/content/0/text").queryFrom(jsonResponse).toString();
            System.out.println("Extracted Text: " + text);

            return text;

        } catch (SdkClientException e) {
            System.err.printf("ERROR: Can't invoke the model. Reason: %s", e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
  public static void main(String[] args) {

      try {

          InputStream inputStream = InvokeTest.class.getClassLoader().getResourceAsStream("problem 1.png");
          if (inputStream == null) {
              throw new RuntimeException("File not found on classpath");
          }
          byte[] imageBytes = inputStream.readAllBytes();
          // MockMultipartFile 생성 (Spring의 MockMultipartFile 사용)
          MultipartFile mockFile = new MockMultipartFile("file", "problem 1.png", "image/png", imageBytes);

          // prompt 정의
          String prompt = "이미지 문제의 해설을 만들어줘";

          // invokeModelwithMultipartFile 메서드 호출 및 결과 출력
          String result = invokeModelwithMultipartFile(mockFile, prompt);
          System.out.println("Model Invocation Result: " + result);

      } catch (IOException e) {
          System.err.println("ERROR: Unable to read the test image file.");
          e.printStackTrace();
      }
  }
}
