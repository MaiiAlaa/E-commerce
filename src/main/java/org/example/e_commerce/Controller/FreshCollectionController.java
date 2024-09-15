package org.example.e_commerce.Controller;

import org.example.e_commerce.Entity.FreshCollection;
import org.example.e_commerce.Service.FreshCollectionService;
import org.example.e_commerce.dto.dtoRequest.FreshCollectionRequestDTO;
import org.example.e_commerce.dto.dtoResponse.FreshCollectionResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/collections")
public class FreshCollectionController {

    @Autowired
    private FreshCollectionService freshCollectionService;

    // Create or update a collection
    @PostMapping("/create")
    public ResponseEntity<FreshCollection> createOrUpdateCollection(@RequestBody FreshCollectionRequestDTO freshCollectionRequestDTO) {
        FreshCollection savedCollection = freshCollectionService.saveOrUpdateCollection(freshCollectionRequestDTO);
        return ResponseEntity.ok(savedCollection);
    }

    // Retrieve all collections
    @GetMapping
    public ResponseEntity<FreshCollectionResponseDTO> getAllCollections() {
        FreshCollectionResponseDTO response = freshCollectionService.getAllCollections();
        return ResponseEntity.ok(response);
    }

    // Retrieve a collection by ID
    @GetMapping("/{id}")
    public ResponseEntity<FreshCollection> getCollectionById(@PathVariable Long id) {
        Optional<FreshCollection> collection = freshCollectionService.getCollectionById(id);
        return collection.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete a collection by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCollection(@PathVariable Long id) {
        freshCollectionService.deleteCollection(id);
        return ResponseEntity.noContent().build();
    }
}