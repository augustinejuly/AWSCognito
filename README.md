# AWSCognito
Example on AWS Cognito

This project has two examples namely for Cognito User Pool and for Cognito Identity Pool

Prerequisites for example 1: <br/>
1. Create an AWS account and get the access and secret key credentials
2. In Cognito user pool dashboard, create an user pool
3. In the attributes section, create the following custom attributes which are of the type String.
   role, region, customer, facility and ipaddress
4. In the Apps section, register an App with the following options
    (a) Enable the option "Generate Client Secret"
    (b) Enable the option "Enable sign-in API for server-based authentication (ADMIN_NO_SRP_AUTH)"
    (c) Disable or Have the option "Only allow Custom Authentication (CUSTOM_AUTH_FLOW_ONLY)" unchecked
5. Now create the app
6. Once the app is created, open the file config.properties in the example here and update the following values
    (a) access_key= your access key
    (b) secret_key= your secret key
    (c) userpool_id= user pool id you just created
    (d) app_name= App name you just registered
    (e) app_client_id=Client Id of the App you just registered 
    (f) app_client_secret=Client Secret of the App you just registered
7. Update the following key with values which are under user data section commentemail=
    (a)userId= username of the user to be registered
    (b)password=password of the user to be registered
    (c)confirm_password=password for forgot password flow
    (d)change_password=password for change password flow
  
8. Java Class to be updated: com/demo/CognitoUserPoolDemo.java
9. Open the java class com/demo/CognitoUserPoolDemo.java and update the region with your appropriate region
10. Execute the java class by uncommenting the required methods within the main method for the appropriate flows.
    SignUp - singUp
    Verifying Email - verifyEmail
    Login - login
    SignOut - singOut
    Update and Read User attributes - updateUserAttributes, getUsers
    Forgot Password - forgotPassword
    Confirm Password - confirmForgotPassword
    Change Password - changePassword
    Renew Access Token - renewAccessTokenUsingRefreshToken
    Delete User -deleteUser
