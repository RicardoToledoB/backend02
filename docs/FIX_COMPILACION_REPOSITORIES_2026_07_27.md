# Fix compilación repositorios - 2026-07-27

Error detectado:

- `cannot find symbol: method findActiveTrueOrderBySortOrderAscNameAsc()` en `CitationTypeRepository`
- `cannot find symbol: method findActiveTrueOrderByNameAsc()` en `BiopsychosocialCommitmentLevelRepository`

Causa:

`DemandService.getCatalogs()` llama métodos derivados de Spring Data JPA que deben estar declarados explícitamente en los repositorios.

Archivos corregidos:

- `src/main/java/com/cosam/project01/demand/repository/CitationTypeRepository.java`
- `src/main/java/com/cosam/project01/demand/repository/BiopsychosocialCommitmentLevelRepository.java`

Métodos agregados:

```java
List<CitationTypeEntity> findByActiveTrueOrderBySortOrderAscNameAsc();
List<BiopsychosocialCommitmentLevelEntity> findByActiveTrueOrderByNameAsc();
```

Luego compilar:

```bash
./mvnw clean package -DskipTests
sudo systemctl restart gestiondemanda-api
```
