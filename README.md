Slick 3, Scala 2.11, Play 2.4 Sample

API: Single endpoint accepting GET, PUT, POST, DELETE.

GET /api/names - return all "id"-"name" pairs from table ENTITY

PUT /api/names/:id/:name - update Entity with "id" to specified "name" - return number of updates

POST /api/names/:name - insert new Entity with "name" - return new ID

DELETE /api/names/:id - delete Entity with "id"

Tested with Scala 2.10.6
