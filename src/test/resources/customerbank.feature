Feature: Hedge fund accessing an MBP

  Background: From Pricing Engine to buy side client message passing

@focus
  Scenario: As a buy side user I want to send an RFQ to get a prices
    Given the following actors exist and are connected as follows
    | Actor                    | Name         | Connection             |
    | MarketDataService        | Bloomberg    |                        |
    | MarketDataService        | Reuters      |                        |
    | BankCentricPricingEngine | Dresdner     | Bloomberg              |
    | BankCentricPricingEngine | MerrillLynch | Bloomberg, Reuters     |
    | MultiBankPlatform        | FXall        | Dresdner, MerrillLynch |
    | Customer                 | HedgeFund    | FXall                  |
    When the actors perform the following actions
    | Actor      | Message  | Parameter |
    | Bloomberg  | generate |  34       |
    | Reuters    | generate |  35       |
    | HedgeFund  | rfq      |           |
    Then the following results should occur
    | Actor        | Message | MessageCount | Payload   |
    | HedgeFund    | Quote   | 2            |           |
    | HedgeFund    | Price   | 2            | 34.5,35.5 |
