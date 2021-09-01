Raidiam Java Dev Oauth Tech Task
================================

This is a simple Spring Boot application which implements a simple account API which we have secured with a stripped down
version of Oauth2. The protected resources are accessed by the authorisation code flow. For simplicity, we have hidden
the complexities of calling the OAuth provider behind a simple Java interface called SecurityFacade. This hides all the details and
simply returns an access token as if the user had been through a successful auth code flow.

Your task is to make the failing tests pass by correctly calling the protected resources. 
You may modify the tests as you see fit to accomplish this, as long as the assertions themselves do not change.

There are three account holders: Tom, Dick and Harry. These can be looked up using the CustomerService bean.

There are 3 oauth clients configured for you in the system, each with different scopes. Use whichever ones you feel appropriate. 
The SecurityFacadeTest shows how the security facade can be used. 

| Client ID        | Client secret      | Scopes  |
| ------------- |:-------------:| -----:|
| client1      | gp243hg32408gh304eg      | accounts |
| client2      | e5uw3q0gherthrgef42g     |   accounts, transactions |
| client3      | f2oighw40g823ioug34      |    transactions |

