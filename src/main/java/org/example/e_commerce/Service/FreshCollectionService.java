package org.example.e_commerce.Service;

import org.example.e_commerce.Entity.FreshCollection;
import org.example.e_commerce.Repository.FreshCollectionRepository;
import org.example.e_commerce.dto.dtoRequest.FreshCollectionRequestDTO;
import org.example.e_commerce.dto.dtoResponse.FreshCollectionResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FreshCollectionService {

    @Autowired
    private FreshCollectionRepository freshCollectionRepository;

    // Create or update a collection
    public FreshCollection saveOrUpdateCollection(FreshCollectionRequestDTO freshCollectionRequestDTO) {
        FreshCollection collection = new FreshCollection();
        collection.setName(freshCollectionRequestDTO.getName());
        collection.setDescription(freshCollectionRequestDTO.getDescription());
        return freshCollectionRepository.save(collection);
    }


    public FreshCollectionResponseDTO getAllCollections() {
        List<FreshCollection> collections = freshCollectionRepository.findAll();

        // Mapping entities to DTO
        List<FreshCollectionResponseDTO.FreshCollectionDTO> collectionDTOs = collections.stream()
                .map(collection -> {
                    FreshCollectionResponseDTO.FreshCollectionDTO dto = new FreshCollectionResponseDTO.FreshCollectionDTO();
                    dto.setCollectionId(collection.getCollection_id());
                    dto.setName(collection.getName());
                    dto.setImage(collection.getImage());
                    dto.setDescription(collection.getDescription());
                    return dto;
                })
                .collect(Collectors.toList());

        // Creating the response DTO
        FreshCollectionResponseDTO responseDTO = new FreshCollectionResponseDTO();
        responseDTO.setStatusCode(0);
        responseDTO.setMessage("Collections retrieved successfully");
        responseDTO.setFreshCollections(collectionDTOs);

        return responseDTO;
    }

    // Retrieve a collection by ID
    public Optional<FreshCollection> getCollectionById(Long id) {
        return freshCollectionRepository.findById(id);
    }

    // Delete a collection by ID
    public void deleteCollection(Long id) {
        freshCollectionRepository.deleteById(id);
    }
}