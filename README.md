This is the *Anti-Fraud System* project I made myself.


<p>This project demonstrates (in a simplified form) the principles of anti-fraud systems in the financial sector. For this project, we will work on a system with an expanded role model, a set of REST endpoints responsible for interacting with users, and an internal transaction validation logic based on a set of heuristic rules.</p><br/><br/>Learn more at <a href="https://hyperskill.org/projects/232?utm_source=ide&utm_medium=ide&utm_campaign=ide&utm_content=project-card">https://hyperskill.org/projects/232</a>

Here's the link to the project: https://hyperskill.org/projects/232

Check out my profile: https://hyperskill.org/profile/445879089

# Description of main app features

## UserController:
* 1. Register User: Accepts a POST request to "/api/auth/user" with user details and registers a new user in the system. Returns the registration status.
* 2. Get Users: Accepts a GET request to "/api/auth/list" and retrieves a list of all users in the system.
* 3. Delete User: Accepts a DELETE request to "/api/auth/user/{username}" and deletes the user with the specified username from the system.
* 4. Update User Role: Accepts a PUT request to "/api/auth/role" with user details and updates the role of the user in the system.
* 5. Set User Account Status: Accepts a PUT request to "/api/auth/access" with user details and sets the account status (locked/unlocked) of the user in the system.

## TransactionController:
* 1. Submit Transaction: Accepts a POST request to **"/api/antifraud/transaction"** with transaction details and processes the transaction. Returns the result of the transaction.
* 2. Get Transaction History: Accepts a GET request to **"/api/antifraud/history"** and retrieves the transaction history.
* 3. Get Transaction History by Card Number: Accepts a GET request to **"/api/antifraud/history/{number}"** with the card number and retrieves the transaction history for the specified card number.
* 4. Add Transaction Feedback: Accepts a PUT request to **"/api/antifraud/transaction"** with transaction feedback details and adds the feedback to the transaction.
* 5. Save Suspicious IP Address: Accepts a POST request to **"/api/antifraud/suspicious-ip"** with the IP address details and saves the suspicious IP address.
* 6. Delete Suspicious IP Address: Accepts a DELETE request to **"/api/antifraud/suspicious-ip/{ip}"** and deletes the suspicious IP address with the specified IP.
* 7. Get Suspicious IP Addresses: Accepts a GET request to **"/api/antifraud/suspicious-ip"** and retrieves the list of suspicious IP addresses.
* 8. Save Stolen Card: Accepts a POST request to **"/api/antifraud/stolencard"** with stolen card details and saves the stolen card information.
* 9. Delete Stolen Card: Accepts a DELETE request to **"/api/antifraud/stolencard/{number}"** and deletes the stolen card with the specified card number.
* 10. Get Stolen Cards: Accepts a GET request to **"/api/antifraud/stolencard"** and retrieves the list of stolen cards.
* 11. Get Maximum Value Transaction by Card Number: Accepts a GET request to **"/api/antifraud/transaction/max_value/{cardNumber}"** with the card number and retrieves the maximum value transaction for the specified card number.
      
# Schema of BD
![Schema_OF_BD](https://github.com/AlexKlinkov/AntiFraudSystem/blob/master/schema_of_BD.jpg)

# Postman collection of queries for testing app
1. Open Postman (desktop version) or download it before ![postman](https://www.postman.com/downloads)
2. Import the file with queries ![postman_collection_with_test_queries]()
3. Run the query from the first to the last sequently (Data Base doesn't drop after reboot app, so that's mean that repeat query is send can invoke exception, for example to avoid of it you can delete data base before repeat launch app)

# Stack of technology:
* Java: Java 17
* Gradle: Gradle version 7.x.x
* Spring Boot: Spring Boot version 2.6.3
* Spring Dependency Management: Spring Dependency Management version 1.0.11.RELEASE
* H2 Database: H2 version 1.4.200
* Hibernate Validator: Hibernate Validator version 6.2.0.Final
