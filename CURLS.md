# Lista de CURLs — Smart Book Finder

## 1. Buscar libros

```bash
# Búsqueda por título
curl -X GET "http://localhost:8080/api/books/search?title=Dune"

# Búsqueda por título y autor
curl -X GET "http://localhost:8080/api/books/search?title=Dune&author=Herbert"

# Búsqueda con todos los parámetros
curl -X GET "http://localhost:8080/api/books/search?title=Dune&author=Herbert&language=EN&publishedAfter=1960"

# Búsqueda solo por autor
curl -X GET "http://localhost:8080/api/books/search?author=Asimov"
```

## 2. Guardar libro favorito

```bash
curl -X POST "http://localhost:8080/api/favorites" \
  -H "Content-Type: application/json" \
  -d '{
    "bookKey": "/works/OL123W",
    "title": "Dune",
    "author": "Frank Herbert",
    "publicationYear": 1965,
    "editionsCount": 100,
    "coverImageUrl": "https://covers.openlibrary.org/b/id/12345-M.jpg"
  }'
```

## 3. Listar favoritos

```bash
curl -X GET "http://localhost:8080/api/favorites"
```

## 4. Eliminar favorito

```bash
curl -X DELETE "http://localhost:8080/api/favorites/1"
```

## 5. Historial de búsquedas

```bash
# Paginado
curl -X GET "http://localhost:8080/api/history?page=0&size=10"

# Sin paginación (usa valores por defecto)
curl -X GET "http://localhost:8080/api/history"
```
