Feature: Test operations invoked by POST method
  RPCServer should retrieve POST calls parameters from request body

  Scenario: Post call with no args
    When POST request is made to procedure "/blog/create" with payload
      | text      | Lorem ipsum dolor sit amet, consectetur adipiscing elit |
      | author    | John Doe                                                |
      | stars     | five                                                    |
      | createdAt | 2022-07-12T08:20:00.235-0300                            |
    Then response status code is 204
    And GET request for "/blog/read" should return 1 elements with payload
      | text      | Lorem ipsum dolor sit amet, consectetur adipiscing elit |
      | author    | John Doe                                                |
      | stars     | five                                                    |
      | createdAt | 2022-07-12T08:20:00.235-0300                            |
