# BioThenticate Spring Boot Plugin
A Spring boot plugin that integrates with BioThenticate for MFA authentication. This plugin contains a custom 
implementation of Spring boot's `AuthenticationManager` which will pull all users from BioThenticate and proceed 
to handle authentication for all users. When a user supplies their credentials, the plugin will first send them to 
BioThenticate for authentication then trigger a MFA request to BioThenticate for that user. Only after the user has passed both
factors will they be let in the application. 

## Quick Start

### Dependencies
#### Maven
--- TODO add maven dep code ---

#### Gradle
--- TODO add gradle dep code ---

### Main Class
```java
@EnableBioThenticate
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
```

### Web Security
```java
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired private IdentityService identityService;
    @Autowired private BioThenticateClient bioThenticateClient;

    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return new BioThenticateAuthenticationManager(this.bioThenticateClient, this.identityService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/admin").hasAuthority("ADMIN")
                .antMatchers("/user").hasAnyAuthority("ADMIN", "USER")
                .anyRequest().authenticated()
                .and().httpBasic();
    }

}
```

### IdentityService
```java
@Service
public class StandardIdentityService implements IdentityService {

    private AccountRepository accountRepository;

    @Autowired
    public StandardIdentityService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void save(Identity identity) {
        Account account = new Account();
        account.setId(identity.getId());
        account.setEmail(identity.getEmail());
        account.setFirstName(identity.getFirstName());
        account.setLastName(identity.getLastName());
        account.setAccountRole(identity.getAccountRole());
        account.setInactive(identity.isInactive());
        this.accountRepository.save(account);
    }
}
```

### Account & AccountRepository
```java
public interface AccountRepository extends JpaRepository<Account, UUID> { }
```

```java
@Data
@Entity
@Table(name = "accounts")
public class Account implements Identity {

    @Id
    private UUID id;

    private String email;

    private String firstName;

    private String lastName;

    @Enumerated(value = EnumType.STRING)
    private AccountRole accountRole;

    private boolean inactive;

}
```

## Build
For the most part you will not need to build this from the source as you can get it from gradle, maven or elsewhere. 
But it is supported if needed.

1. Clone the repository ---TODO insert url here---
2. Navigate to the root directory and run `gradlew clean build`
3. Once completed the .jar will be located at `build/libs/`

## License
--- TODO insert license details here ----

## Authors
The [SOFTwarfare](https://softwarfare.com) Dev Team
