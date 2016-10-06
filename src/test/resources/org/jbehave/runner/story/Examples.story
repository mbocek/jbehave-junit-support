Meta:

Narrative:
As a user
I want to perform a login
So that I can achieve a business goal

Scenario: login to system
Given login with data:
|username  |password   |
|JohnDow   |Passw0rd   |
When I submit login data on <url>
Then user should be logged in <status>

Examples:
|url                       |status       |
|http://examples.com/login |OK           |
|http://examples.com/logout|NOK          |
