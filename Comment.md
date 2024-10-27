## 더미(Mock)로 테스트 대체   

> ### 더미로 대체하여 테스트를 하는 경우   
> `Mock` 객체를 사용하는 경우는 대체로 외부 시스템과의 상호작용을 피하고 로직 하나에 집중하기 위하여 사용이 된다.   
> 
> 이메일이나, 푸시알람, 서드파티 API 호출 등의 외부 리소스에 의존하는 기능을 테스트 할 때, 실제 시스템에 영향을 주지 않고 로직을 검증하기 위해서 `Mock`을 사용한다.   
> 
> 

```java
    @Test
    void userCreateDto_유저_생성() {
//        given
    UserCreateDto dto = UserCreateDto.builder()
            .email("ljy5314@naver.com")
            .address("Andong")
            .nickname("Lee")
            .build();

//        when
    UserEntity result = userService.create(dto);

//        then
    assertThat(result.getId()).isNotNull();
    assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
//        assertThat(result.getCertificationCode()).isEqualTo();
//    해당 테스트 코드에서 `CertificationCode`값은 UUID 랜덤 값이기 때문에 지금은 테스트를 진행하지 않는다.
}
```

일단, 해당 테스트를 진행을 결과 실패하게 된다. 그 이유는 

```java
    @Transactional
    public UserEntity create(UserCreateDto userCreateDto) {
//  ...
        sendCertificationEmail(userCreateDto.getEmail(), certificationUrl);
    }

    private void sendCertificationEmail(String email, String certificationUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Please certify your email address");
        message.setText("Please click the following link to certify your email address: " + certificationUrl);
        mailSender.send(message);
    }
```
해당 부분 때문에 실패라는 결과를 얻는다. 이를 해결하기 위해서 `JavaMailSender`를 더미로 대체하여 테스트를 진행한다. 

```java
    BDDMockito.doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));
```
해당 코드를 작성하여 `SimpleMailMessage.class`의 `send`가 호출이 되어도 동작하지 말라는 설정을 해준다.

<details>
<summary> `BDDMockito.doNothing()`의 역할</summary>
<div markdown="1">

`JavaMailSender`의 `send` 메서드를 호출할 때 아무 작업도 하지 않도록 설정합니다.   
즉, 이 코드는 `send` 메서드가 호출되더라도 실제로는 아무런 행동을 하지 않고 넘어가도록 하는 것이다. 
이는 테스트 중에 메일 전송 기능이 호출되더라도 테스트 환경에 영향을 미치지 않도록 보장한다. 
이 코드를 통해 메일 전송 로직이 호출되는지를 확인하고 싶지 않은 경우, 또는 메일 전송의 부작용을 방지하고 싶을 때 유용합니다.

#### 1. BDDMockito.doNothing()

   •	기능: `doNothing()` 메서드는 특정 메서드 호출이 발생했을 때 아무 작업도 수행하지 않도록 설정
   •	목적: 이 설정은 주로 테스트 중에 메서드의 부작용을 피하고, 테스트의 결과에 영향을 주지 않도록 하기 위해 사용

#### 2. .when(javaMailSender)

   •	기능: `when(...)` 메서드는 `Mockito`에서 특정 행동을 정의할 때 사용하는 메서드다.
   이 메서드 뒤에 오는 인자는 모의 객체의 메서드를 호출할 때 어떤 행동을 할지를 지정한다
   •	목적: 여기서 `javaMailSender`는 실제 `JavaMailSender` 객체가 아니라 모의(가짜) 객체이다. 
   `when` 다음에 오는 메서드는 이 모의 객체에서 어떤 메서드가 호출될지를 나타낸다.

#### 3. .send(any(SimpleMailMessage.class))

   •	기능: `send(...)`는 `JavaMailSender` 인터페이스의 메서드로,
   이메일을 전송하는 데 사용된다. `any(SimpleMailMessage.class)`는 `Mockito`의 매처(`matcher`)로,
   `SimpleMailMessage` 타입의 객체가 어떤 것이든 상관없이 이 메서드가 호출될 수 있음을 나타낸다.
   •	목적: 이메일을 보내는 메서드인 `send`가 호출될 때,
   전달된 인자가 어떤 것이든 간에 상관하지 않고 모의 객체에서 아무런 행동을 하지 않도록 설정한다.


</div>
</details>

```java
    @MockBean
    private JavaMailSender javaMailSender;
```
`JavaMailSender`라는 `Bean`객체를  `Mock`으로 선언된 객체로 덮어쓴다. 

<details>
<summary>왜 `MockBean`을 사용하는 이유</summary>
<div markdown="1">

```java
    @MockBean
    private JavaMailSender javaMailSender;
```

`@MockBean` 을 사용하면 테스트 환경에서 실제 `JavaMailSender` `bean` 대신에 모의(mock) 객체를 주입한다. 이를 통하여 실제 이메일 전송 기능을 사용하지 않고도 테스트가 동작할 수 있다.     
`Mock` 객체를 사용함으로써 테스트의 속도를 높이고, 불필요한 외부 의존성을 줄이며, 예외 상황(예: 이메일 전송 실패)에 대한 테스트를 쉽게 구현할 수 있다.    

결과적으로, 테스트는 코드의 로직을 검증하는 데 집중할 수 있습니다.

</div>
</details>

----

## Controller Test

컨트롤러 테스트를 징행하는데 있어 가장 간단한 `HealthCheckContorller`먼저 테스트 코드를 작성했다.

```java
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class HealthCheckControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthCheck_응답_결과_200() throws Exception {
         mockMvc.perform(get("/health_check.html"))
                 .andExpect(status().isOk());
    }
}
```

테스트 코드에 대하여 처음 학습하는 나에게 있어 천천히 하나씩 살펴 보겠다.   

### 클래스 선언부 어노테이션 


<details>
<summary>@AutoConfigureMockMvc</summary>
<div markdown="1">

해당 어노테이션은 `Spring MVC`의 `MockMvc`를 자동으로 설정해 준다.     
`MockMvc`는 실제 웹 서버를 실행하지 않고도 `HTTP` 요청을 보내고 응답을 검증할 수 있는 기능을 제공한다. 
이를 통해 컨트롤러에 대한 테스트를 더욱 쉽게 수행할 수 있다.    

`MockMvc`는 웹 애플리케이션의 컨트롤러를 테스트하기 위한 강력한 도구로, 실제 요청과 응답의 흐름을 모방할 수 있다.

</div>
</details>

<details>
<summary>@AutoConfigureTestDatabase</summary>
<div markdown="1">

`@AutoConfigureTestDatabase` 어노테이션은 테스트 환경에서 사용할 데이터베이스를 자동으로 설정해 준다.   
기본적으로 `H2` 데이터베이스와 같은 인메모리 데이터베이스를 사용하여 테스트를 수행할 수 있게 해준다. 
이를 통해 테스트 환경에서 실제 데이터베이스와 분리된 상태에서 테스트를 진행할 수 있으며, 데이터베이스의 상태가 테스트에 영향을 미치지 않도록 할 수 있다.

</div>
</details>

이제 테스트 코드에 대해서 확인 하자.    

```java
    mockMvc.perform(get("/health_check.html"))
           .andExpect(status().isOk());
```

`mockMvc.perform(get("/health_check.html"))`    
이 코드는 `MockMvc`를 사용하여 `HTTP` `GET` 요청을 `/health_check.html` 경로로 수행하는 것이다.   
`get()` 메소드는 요청할 URL을 지정하고, 이 요청을 수행하여 결과를 `MockMvc`가 처리할 수 있도록 한다.    


`.andExpect(status().isOk())`   
이 부분은 요청의 결과에 대한 기대값을 설정한다. `status().isOk()`는 `HTTP` 응답 상태 코드가 `200(OK)`임을 기대하는 것으로, 즉 요청이 성공적으로 처리되었다는 것을 의미한다.      


<details>
<summary>다양한 사용 방법</summary>
<div markdown="1">
간단하게 `MockMvc`의 다른 사용 방법에 대해서 몇 가지 알아 보겠다.

  * 응답 본문 확인   
    특정 문자열이나 JSON 구조가 응답 본문에 포함되어 있는지 확인할 수 있다.
   ```java
        mockMvc.perform(get("/health_check.html"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("expected content")));
   ```
  
  * HTTP POST 요청    
    `POST` 요청을 보낼 때는 `post()` 메소드를 사용하고, 요청 본문에 데이터를 포함할 수 있다.   
   ```java
      mockMvc.perform(post("/api/resource")
         .contentType(MediaType.APPLICATION_JSON)
         .content("{\"key\":\"value\"}"))
        .andExpect(status().isCreated());
   ```

  * 응답 헤더 확인    
    응답의 특정 헤더 값을 확인할 수도 있다.
    ```java
      mockMvc.perform(get("/health_check.html"))
        .andExpect(status().isOk())
        .andExpect(header().string("Content-Type", "text/html;charset=UTF-8"));
    ```

</div>
</details>