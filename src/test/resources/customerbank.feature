Feature: Buy side user connecting to send an RFQ

  Background: From Pricing Engine to buy side client message passing

  Scenario: As a buy side user I want to login and send an RFQ
    Given the following users
    | Role       | Company    |
    | Customer1  | HedgeFund1 |
    | Customer2  | Bank1      |
    And the following system are available
    | System     |
    | 360T       |
    And the users connect to the systems
    When users submit messages as follows
    | Role         | Message | Id |
    | Customer1    | StartRFQ|  1 |
