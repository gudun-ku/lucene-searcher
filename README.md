# Full-Text Search Service example

A small in-memory full-text search service built with **Java 21**, **Spring Boot 3**, and **Apache Lucene**. Supports
fuzzy matching and multiple languages.

---

## Features

- **Fuzzy Search**: Approximate matching for typos and partial keywords.
- **Multilanguage Support**: Loads phrases from JSON files (e.g., English and Russian).
- **REST API**: Simple endpoint for searching phrases.
- **In-Memory Index**: Uses Apache Lucene for fast queries.

---

## API Endpoint

### Search for Phrases

- **URL**: `/search`
- **Method**: `GET`
- **Query Parameters**:
    - `query`: Search query (min 3 characters).
    - `lang`: Language code (default: `en`).

#### Example Requests:

1. English:
   ```
    GET /search?query=delivry&lang=en
   ```

   **Response**:
   ```json
   ["DevOps practices improve software delivery"]
   ```

2. Russian:
3.
```
GET /search?query=доставка&lang=ru
```

**Response**:
```json
["DevOps практики улучшают доставку программного обеспечения"]
```
---

## Error Handling

- **Query Too Short**:
    - **Status Code**: `400 Bad Request`
    - **Response**: `"Query must be at least 3 characters long."`

- **Unsupported Language**:
    - **Status Code**: `400 Bad Request`
    - **Response**: `"Unsupported language: <lang>"`

- **Internal Server Error**:
    - **Status Code**: `500 Internal Server Error`
    - **Response**: `"An internal server error occurred."`

---

## JSON Files for Phrases

Phrases are loaded from JSON files in `src/main/resources`.

#### English (`phrases_en.json`):

```json
[
  {
    "phrase": "DevOps practices improve software delivery",
    "keywords": [
      "devops",
      "practices",
      "software",
      "delivery"
    ]
  }
]
```

#### Russian (`phrases_ru.json`):

```json
[
  {
    "phrase": "DevOps практики улучшают доставку программного обеспечения",
    "keywords": [
      "devops",
      "практики",
      "доставка",
      "по"
    ]
  }
]
```

---

## How to Run

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-repo/full-text-search-service.git
   cd full-text-search-service
   ```

2. **Build and Run**:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

3. **Test the API**:
   Use [Postman](https://www.postman.com/) or `curl` to send requests to `/search`.

---

## Dependencies

- **Spring Boot 3**: REST API.
- **Apache Lucene**: Full-text search.
- **JUnit 5**: Unit testing.
- **Mockito**: Mocking in tests.

---

## License

MIT License. See [LICENSE](LICENSE).

---
