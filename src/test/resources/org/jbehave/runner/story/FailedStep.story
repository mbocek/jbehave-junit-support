Scenario: Failed step
When Sign up with audit
Then Failed step

When Auditing user
Then User with name Tester is properly signed in
