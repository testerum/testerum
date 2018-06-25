# Steps variables

Source:
* from headers/body
* JSON path / XML path / regex

Destination:
* variable name

step-def: When I extract '<<extractionExpression: String>>' from response (header <<headerName: String>>|body) into variable '<<variableName: String>>'

extractionExpression:
   - ``json-path=$.data.'auth-token'``
   - ``xml-path=//auth-token[text()]``
   
   
Step-def: When I define variable <<variableName: String>> extracted from <<source: String>> with expression <<expression: String>>
source: response.headers['Content-Type']
expression: jsonPath(".user.name")



body:
[
{
  user:Ionut,
  address: {
     city: 'Amsterdam Rai'
  }
}
]
Step-def: When I define variable 'ionut' extracted from 'json_path(response.body, '$.[0]')'
Step-def: When I define variable 'ionut_upper' extracted from 'upperCase(ionut)'
Step-def: When I define variable 'ionut_address' extracted from 'regex(ionut, '$.address')


Step-def: When I define variable 'ionut_city' with value 'json(response.body)[0].address.city'
Step-def: When I define variable 'rai' with value 'regex('xx', ionut_city)'
