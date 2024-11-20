### Log masking and encryption at rest in Spring Boot
Enroute to creating a GDPR complaint application, our first pit stop will be creating a simple Spring Boot application that would encrypt PII fields and mask the PII fields during logging.

**What we will be doing...**
We will develop a basic application for creating and retrieving user records, focusing on secure handling of Personally Identifiable Information (PII). Each user entity will contain specific fields classified as PII, such as email addresses, which will be stored in an encrypted format for data protection. Additionally, the application will include certain sensitive fields that, while stored in plain text, will appear masked in logs or HTTP GET responses to ensure data privacy. We will utilize Spring's Aspect-Oriented Programming (AOP) features to implement these masking and encryption functionalities effectively.

Let's get started...

**Step 1 - Create a Spring Boot Application:**
Create a simple Spring boot application using spring.io or your IDE. You'll need to add the following dependencies to your application:
* Web
* Spring Data JPA
* PostgreSQL Driver

**Step 2 - Create Annotations:** 
* PiiField - All the fields annotated with this annotation will mark these fields as PII fields and will be encrypted when saved in the Database.
```Java
@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.FIELD)  
public @interface PiiField {
}
```
* *MaskedField* - All the fields annotated with this annotation will mark fields to be sent back in a masked format in response, but the data will be saved in plaintext format in the database.
```Java
@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.FIELD)  
public @interface MaskedField {  
}
```
* *Encrypt* - All the methods marked with this annotation will go through the encryption process
```Java
@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.METHOD)  
public @interface Encrypt {  
}
```
* *Logged* - The controller methods marked with this annotation will log the parameters with which the call was performed. This will be used in tracing.
```Java
@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.METHOD)  
public @interface Logged {  
}
```
* *MaskedResponse*- Controllers marked with this annotation will mask the fields annotated with @MaskedField
```Java
@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.METHOD)  
public @interface MaskedResponse {  
}
```


**Step 3 - Add Controllers, Services, Repositories and Entities to save and retrieve User data:**
Create the Application User entity as follows:
```Java
@Entity  
public class AppUser {
    @Id  
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String instagramHandle;
    
    // Getter/Setters etc are ommited
}
```

Create the DTO and add relevant annotations to the fields:
```Java
public class UserDto implements Serializable {  
    long id;  
    String name;  
    @PiiField  
    String email;  
    @MaskedField  
    String instagramHandle;
    
    //Getter/Setter/ToString etc are ommited
}
```

Create a simple JPA repository to work with Application User data:
```Java
@Repository  
public interface UserRepo extends JpaRepository<AppUser, Long> {  
}
```

Create the User service and Add @Encrypt to the saveUser function (since we want to encrypt the data before saving.):
```Java
@Service  
public class UserService {  
  
    private final UserRepo userRepo;  
  
    public UserService(UserRepo userRepo) {  
        this.userRepo = userRepo;  
    }  
    
    public UserDto saveUser(UserDto userDto) {  
        AppUser appUser = new AppUser();  
        // Create App User from UserDto
        AppUser savedUser = userRepo.save(appUser);  
        userDto.setId(savedUser.getId());  
        return userDto;  
    }  
  
    public UserDto getUserById(long id) {  
        if(userRepo.findById(id).isPresent()){  
            AppUser appUser = userRepo.findById(id).get();  
            // Create User Dto from App User
            return userDto;
        }  
        return null;  
    }  
}
```

**Step 4 - Implementing Logging and mask PIIs in logs:**
There are certain requests that we may need to log for compliance purpose. For this, we are using an annotation @Logged. Adding this would log the request parameters. Let's create an aspect that would be used to handle logging.

```Java
@Aspect  
@Component  
public class LoggingAspect {  
  
    Logger logger = LoggerFactory.getLogger(LoggingAspect.class);  
  
    @Pointcut("@annotation(com.roy.gdprspring.annotations.Logged)")  
    public void logged() {}  
  
    @Before("logged()")  
    public void loggedPointcut(JoinPoint joinPoint) {  
        Arrays.stream(joinPoint.getArgs()).forEach(x->{  
            logger.info("{}",x);  // You can replace logging with whatever you'd like
        });  
    }  
}
```

**Log Masking:** For log masking, we can custom-format the logs by extending the Logback's ClassicConverter. The following piece of code will replace the PIIs with a bunch of \*s

```Java
public class LogMaskingConverter extends ClassicConverter {  
  
    private static final String PII_MASK = "*********";  
    private static final Logger log = LoggerFactory.getLogger(LogMaskingConverter.class);  
    private final AnnotationUtil annotationUtil = new AnnotationUtil();  
  
    @Override  
    public String convert(ILoggingEvent event) {  
        String message = event.getFormattedMessage();  
        // Check if the message contains an object and mask the annotated fields  
        if (event.getArgumentArray() != null && event.getArgumentArray().length > 0) {  
            Object[] arguments = event.getArgumentArray();  
            Object[] arguments_copy = DeepCopyUtil.deepCopy(event.getArgumentArray());  
            if(arguments_copy != null){  
                for (Object arg : arguments_copy) {  
                    maskFields(arg);  
                }  
                return Arrays.asList(arguments_copy).toString();  
            }  
            return Arrays.asList(arguments).toString();  
        }  
        return message;  
    }  
  
    private void maskFields(Object obj) {  
        if (obj == null) {  
            return;  
        }  
  
        Class<?> objClass = obj.getClass();  
        Field[] fields = objClass.getDeclaredFields();  
        for (Field field : fields) {  
            if (annotationUtil.isAnnotationPresent(field, PiiField.class)|| annotationUtil.isAnnotationPresent(field, MaskedField.class)) {  
                field.setAccessible(true);  // Make private fields accessible  
                try {  
                    // Mask the field if it's a string (or adjust for other types)  
                    if (field.get(obj) instanceof String) {  
                        field.set(obj, PII_MASK);  
                    }  
                } catch (IllegalAccessException e) {  
                    log.error(e.getMessage());  
                }  
            }  
        }  
    }  
}
```

The utility classes DeepCopyUtil and AnnotationUtil can be found in the repository. I haven't put them here for the brevity.

**Step 5 - Encrypt the data:**
We want to encrypt the data while saving the data in the Database tables. For that, we will create the EncryptionAspect.

```Java
@Aspect  
@Component  
public class EncryptionAspect {  
    @Pointcut("@annotation(com.roy.gdprspring.annotations.Encrypt)")  
    public void encryptPointCut() {}  
  
    @Around("encryptPointCut()")  
    public Object encryptAround(ProceedingJoinPoint joinPoint) throws Throwable {  
        Object[] args = joinPoint.getArgs();  
        Object[] mod_args = Arrays.stream(args).peek(s -> {  
            Class<?> clazz = s.getClass();  
            Field[] fields = clazz.getDeclaredFields();  
            for (Field field : fields) {  
                if(field.isAnnotationPresent(PiiField.class)){  
                    field.setAccessible(true);  
                    try {  
                        field.set(s,field.get(s)+"_ENCRYPTED");  // Add your own implementation
                    } catch (IllegalAccessException e) {  
                        throw new RuntimeException(e);  
                    }  
                }  
            }  
        }).toArray();  
        return joinPoint.proceed(mod_args);  
    }  
}
```

Add @Encrypt to the saveUser function in UserService.
```Java
@Service  
public class UserService {  
    
    // Additional code goes here...
    
    @Encrypt  // Add this annotation for encryption
    public UserDto saveUser(UserDto userDto) {  
        AppUser appUser = new AppUser();  
        appUser.setName(userDto.getName());  
        appUser.setEmail(userDto.getEmail());  
        appUser.setInstagramHandle(userDto.getInstagramHandle());  
        AppUser savedUser = userRepo.save(appUser);  
        userDto.setId(savedUser.getId());  
        return userDto;  
    }  
  
    // Additional code goes here  
}
```

Currently, the data is being saved with the text **\_ENCRYPTED** added at the end of the text. In the next blog we will implement an actual encryption using AWS KMS.

**Step 6 - Implement Masked response:**
For masking the data in Response, we can create a ResponseBodyAdvice.

```Java
@ControllerAdvice  
public class MaskedResponseAdvice implements ResponseBodyAdvice<Object> {  
    @Override  
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {  
        return returnType.hasMethodAnnotation(MaskedResponse.class);  
    }  
  
    @Override  
    public Object beforeBodyWrite(Object body, MethodParameter returnType,  
                                  MediaType selectedContentType,  
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,  
                                  ServerHttpRequest request, ServerHttpResponse response) {  
        if(body==null){  
            return null;  
        }  
  
        maskFields(body);  
        return body;  
    }  
  
    private void maskFields(Object body) {  
        Field[] fields = body.getClass().getDeclaredFields();  
        for (Field field : fields) {  
            if (field.isAnnotationPresent(MaskedField.class)) {  
                field.setAccessible(true);  
                try {  
                    Object value = field.get(body);  
                    if (value != null) {  
                        field.set(body, maskValue(value.toString()));  
                    }  
                } catch (IllegalAccessException e) {  
                    throw new RuntimeException(e);  
                }  
            }  
        }  
    }  
  
    private String maskValue(String value) {  
        if (value.length() <= 4) {  
            return "****";  
        }  
        return "****" + value.substring(value.length() - 4);  
    }  
}
```

### Disclaimer

The code provided here is a sample code, enough to give you an idea about what can be done. This code is not production ready and there is no optimization. But this will give you a base to start with.

