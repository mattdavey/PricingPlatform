Feature: Buy side user connecting to send an RFQ

  Background: From Pricing Engine to buy side client message passing

  Scenario: As a buy side user I want to login to a MBP and send an RFQ to get a prices
    Given the following actors exist and are connected as follows
    | Actor                    | Name         | Connection             |
    | MarketDataService        | Bloomberg    |                        |
    | MarketDataService        | Reuters      |                        |
    | BankCentricPricingEngine | Dresdner     | Bloomberg              |
    | BankCentricPricingEngine | MerrillLynch | Bloomberg, Reuters     |
    | MultiBankPlatform        | FXall        | Dresdner, MerrillLynch |
    | Customer                 | HedgeFund    | FXall                  |
    When the actors perform the following actions
    | Actor        | Message | MessageId |
    | HedgeFund    | rfq     |  1        |
    Then the following results should occur
    | Actor        | Message | MessageCount |
    | HedgeFund    | Quote   | 2            |
