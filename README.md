# AWSCognito
Example on AWS Cognito

This project has two examples namely for <b>Cognito User Pool</b> and for <b>Cognito Identity Pool</b>

<b>Prerequisites for example 1:</b> <br/>
1. Create an AWS account and get the access and secret key credentials <br/>
2. In Cognito user pool dashboard, create an user pool <br/>
3. In the attributes section, create the following custom attributes which are of the type String. <br/>
   role, region, customer, facility and ipaddress <br/>
4. In the Apps section, register an App with the following options <br/>
    (a) Enable the option "Generate Client Secret" <br/>
    (b) Enable the option "Enable sign-in API for server-based authentication (ADMIN_NO_SRP_AUTH)" <br/>
    (c) Disable or Have the option "Only allow Custom Authentication (CUSTOM_AUTH_FLOW_ONLY)" unchecked <br/>
5. Now create the app <br/>
6. Once the app is created, open the file <b>config.properties</b> in the example here and update the following values <br/>
    (a) access_key= your access key <br/>
    (b) secret_key= your secret key <br/>
    (c) userpool_id= user pool id you just created <br/>
    (d) app_name= App name you just registered <br/>
    (e) app_client_id=Client Id of the App you just registered  <br/>
    (f) app_client_secret=Client Secret of the App you just registered <br/>
7. Update the following key with values which are under the section user data  <br/>
    (a)userId= username of the user to be registered <br/>
    (b)password=password of the user to be registered <br/>
    (c)confirm_password=password for forgot password flow <br/>
    (d)change_password=password for change password flow <br/>
  
8. Java Class to be updated: com/demo/CognitoUserPoolDemo.java <br/>
9. Open the java class <b>com/demo/CognitoUserPoolDemo.java</b> and update the region with your appropriate region <br/>
10. Execute the java class by uncommenting the required methods within the main method for the appropriate flows. <br/>
    SignUp - singUp <br/>
    Verifying Email - verifyEmail <br/>
    Login - login (once it is executed, copy the id_token, access_token and refresh_token from the console and paster them into the config.properties) <br/>
    SignOut - singOut <br/>
    Update and Read User attributes - updateUserAttributes, getUsers <br/>
    Forgot Password - forgotPassword <br/>
    Confirm Password - confirmForgotPassword <br/>
    Change Password - changePassword <br/>
    Renew Access Token - renewAccessTokenUsingRefreshToken <br/>
    Delete User -deleteUser <br/>
 
 <b>Prerequisites for example 2:</b> <br/>
 1. 
