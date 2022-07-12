Feature: Test operations invoked by GET method
  RPCServer should retrieve GET calls parameters from request path

  Scenario: Get call with no args
    When GET request is made for "/calc/debug"
    Then response status code is 204

  Scenario: Get call with integer types
    When GET request is made for "/calc/sum/1230/4560"
    Then response status code is 200
    And response has numeric value 5790

  Scenario: Get call with short types
    When GET request is made for "/calc/cut/100/200"
    Then response status code is 200
    And response has numeric value 150

  Scenario: Get call with long types
    When GET request is made for "/calc/enlarge/100/200"
    Then response status code is 200
    And response has numeric value 600

  Scenario: Get call with float types
    When GET request is made for "/calc/sub/12.5/20.7"
    Then response status code is 200
    And response has numeric value -8.200001

  Scenario: Get call with double types
    When GET request is made for "/calc/twice/8.988465674311579E307"
    Then response status code is 200
    And response has numeric value 1.7976931348623157E308

  Scenario: Get call with boolean type receive true return false
    When GET request is made for "/calc/inversor/true"
    Then response status code is 200
    And response has boolean value "false"

  Scenario: Get call with boolean type receive false return true
    When GET request is made for "/calc/inversor/false"
    Then response status code is 200
    And response has boolean value "true"

  Scenario: Get call with character types
    When GET request is made for "/calc/bigger/k/j"
    Then response status code is 200
    And response has text value "k"

  Scenario: Get call with byte types
    When GET request is made for "/calc/next/100"
    Then response status code is 200
    And response has numeric value 101

  Scenario: Get call with String type
    When GET request is made for "/calc/reverse/abcdefg"
    Then response status code is 200
    And response has text value "gfedcba"

  Scenario: Get call with Date type
    When GET request is made for "/calc/next-day/2021-02-15T11:40:15.123-0300"
    Then response status code is 200
    And response has date value "2021-02-16T11:40:15.123-0300"
