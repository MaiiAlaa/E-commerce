package org.example.e_commerce.dto.dtoResponse;

import java.util.List;

public class FreshCollectionResponseDTO {

    private int statusCode;
    private String message;
    private List<FreshCollectionDTO> freshCollections;

    // Getters and Setters

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<FreshCollectionDTO> getFreshCollections() {
        return freshCollections;
    }

    public void setFreshCollections(List<FreshCollectionDTO> freshCollections) {
        this.freshCollections = freshCollections;
    }

    // Nested DTO for the individual collection data
    public static class FreshCollectionDTO {
        private Long collectionId;
        private String name;
        private String image;
        private String description;

        // Getters and Setters
        public Long getCollectionId() {
            return collectionId;
        }

        public void setCollectionId(Long collectionId) {
            this.collectionId = collectionId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}