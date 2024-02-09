# ATM Machine Simulation
###
Simulation of an ATM interface that allows you to do the following:
* Login: provide unique account number and corresponding pin.
* Signup: provide your contact number, first name and surname.
* Balance: check balance on account.
* Deposit: deposit money into account.
* Withdraw: withdraw money from account.
* Transfer: transfer money into provided destination account.

### Running Program:
* Client: client/src/main/java/tebogo/mkhize/projects/ATMClient.java
* Server: server/src/main/java/tebogo/mkhize/projects/ATMServer.java

### Tools, Technologies & Database:
* Tools & Technologies Used: Java, Socket Programming, maven, database (sqlite), Java Database Connectivity (JDBC), Intellij, etc.
* Comm. Protocol:
  * Requests: {account: ..., request: ..., arguments: ...}
  * responses: {account: ..., outcome: ..., message: ..., data: ...}
  
####
* Database Design:
  * Database populated with below default users and data for testing purposes. Feel free to add own users and use ATM simulation as you please.
###

| account (primary key) | firstName    | lastName     | contact      | pin          | balance      |
|:----------------------|:-------------|:-------------|:-------------|:-------------|:-------------|
| 302746                | john         | doe          | 0824921157   | 2205         | xxxxx        |
| 935426                | jane         | doe          | 0832929213   | 3409         | xxxxx        |
| ............          | ............ | ............ | ............ | ............ | ............ |